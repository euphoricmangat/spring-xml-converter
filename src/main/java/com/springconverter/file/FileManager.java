package com.springconverter.file;

import com.springconverter.config.ConverterConfig;
import com.springconverter.model.ConversionError;
import com.springconverter.model.ConversionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages file operations, backups, and directory traversal for the conversion process.
 */
public class FileManager {
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
    private final ConverterConfig config;
    private final ConversionReport report;

    public FileManager(ConverterConfig config, ConversionReport report) {
        this.config = config;
        this.report = report;
    }

    /**
     * Finds all XML files in the project directory that match Spring configuration patterns.
     */
    public List<String> findXmlFiles(String projectDirectory) {
        List<String> xmlFiles = new ArrayList<>();
        
        try {
            Files.walk(Path.of(projectDirectory))
                    .filter(path -> path.toString().endsWith(".xml"))
                    .filter(path -> isSpringXmlFile(path.toString()))
                    .filter(path -> !config.shouldExcludeFile(path.toString()))
                    .filter(path -> config.shouldIncludeFile(path.toString()))
                    .forEach(path -> xmlFiles.add(path.toString()));
        } catch (IOException e) {
            logger.error("Error searching for XML files in: {}", projectDirectory, e);
            report.addError(new ConversionError("Failed to search for XML files: " + e.getMessage(), 
                    projectDirectory, ConversionError.ErrorType.FILE_NOT_FOUND));
        }
        
        logger.info("Found {} XML files in project directory", xmlFiles.size());
        return xmlFiles;
    }

    /**
     * Determines if a file is a Spring XML configuration file.
     */
    private boolean isSpringXmlFile(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath));
            return content.contains("xmlns:beans") || 
                   content.contains("http://www.springframework.org/schema/beans") ||
                   content.contains("<bean") ||
                   content.contains("context:component-scan") ||
                   content.contains("aop:config") ||
                   content.contains("tx:advice");
        } catch (IOException e) {
            logger.warn("Could not read file to determine if it's a Spring XML: {}", filePath);
            return false;
        }
    }

    /**
     * Creates a backup of a file before modification.
     */
    public String createBackup(String filePath) throws IOException {
        if (!config.isCreateBackups()) {
            return null;
        }

        Path sourcePath = Path.of(filePath);
        String backupDir = config.getBackupDirectory();
        
        if (backupDir == null || backupDir.isEmpty()) {
            backupDir = sourcePath.getParent().toString() + "/backup";
        }
        
        // Create backup directory if it doesn't exist
        Path backupPath = Path.of(backupDir);
        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
        }
        
        // Generate backup filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = sourcePath.getFileName().toString();
        String backupFileName = fileName.replace(".", "_" + timestamp + ".");
        Path backupFilePath = backupPath.resolve(backupFileName);
        
        // Copy file to backup location
        Files.copy(sourcePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
        
        logger.info("Created backup: {} -> {}", filePath, backupFilePath);
        report.addFileBackup(filePath, backupFilePath.toString());
        
        return backupFilePath.toString();
    }

    /**
     * Restores a file from backup.
     */
    public boolean restoreFromBackup(String originalFilePath, String backupFilePath) {
        try {
            Path originalPath = Path.of(originalFilePath);
            Path backupPath = Path.of(backupFilePath);
            
            if (Files.exists(backupPath)) {
                Files.copy(backupPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Restored file from backup: {} -> {}", backupFilePath, originalFilePath);
                return true;
            } else {
                logger.error("Backup file not found: {}", backupFilePath);
                return false;
            }
        } catch (IOException e) {
            logger.error("Failed to restore file from backup: {}", backupFilePath, e);
            return false;
        }
    }

    /**
     * Safely writes content to a file, creating backup if enabled.
     */
    public void writeFile(String filePath, String content) throws IOException {
        String backupPath = null;
        
        try {
            // Create backup if enabled
            if (config.isCreateBackups()) {
                backupPath = createBackup(filePath);
            }
            
            // Write the new content
            Files.write(Path.of(filePath), content.getBytes());
            logger.debug("Successfully wrote file: {}", filePath);
            
        } catch (IOException e) {
            logger.error("Failed to write file: {}", filePath, e);
            
            // Restore from backup if write failed
            if (backupPath != null) {
                restoreFromBackup(filePath, backupPath);
            }
            
            throw e;
        }
    }

    /**
     * Removes or comments out bean definitions from XML files after conversion.
     */
    public void updateXmlFile(String xmlFilePath, List<String> convertedBeanIds) throws IOException {
        if (config.isDryRun()) {
            logger.info("DRY RUN: Would update XML file: {}", xmlFilePath);
            return;
        }
        
        String content = Files.readString(Path.of(xmlFilePath));
        String originalContent = content;
        
        // Comment out converted bean definitions
        for (String beanId : convertedBeanIds) {
            content = commentOutBeanDefinition(content, beanId);
        }
        
        // Write updated content if changed
        if (!content.equals(originalContent)) {
            writeFile(xmlFilePath, content);
            logger.info("Updated XML file: {}", xmlFilePath);
        }
    }

    /**
     * Comments out a bean definition in XML content.
     */
    private String commentOutBeanDefinition(String content, String beanId) {
        // Simple regex to find and comment out bean definitions
        // This is a basic implementation - could be enhanced with proper XML parsing
        String pattern = "(<bean[^>]*id=\"" + beanId + "\"[^>]*>.*?</bean>)";
        String replacement = "<!-- Converted to annotation: $1 -->";
        
        return content.replaceAll(pattern, replacement);
    }

    /**
     * Removes empty XML files if configured to do so.
     */
    public void removeEmptyXmlFile(String xmlFilePath) throws IOException {
        if (!config.isRemoveEmptyXmlFiles()) {
            return;
        }
        
        String content = Files.readString(Path.of(xmlFilePath));
        String trimmedContent = content.trim();
        
        // Check if file is essentially empty (only contains XML declaration and empty root element)
        if (trimmedContent.matches("^<\\?xml[^>]*\\?>\\s*<[^>]*>\\s*</[^>]*>\\s*$")) {
            if (config.isDryRun()) {
                logger.info("DRY RUN: Would remove empty XML file: {}", xmlFilePath);
            } else {
                Files.delete(Path.of(xmlFilePath));
                logger.info("Removed empty XML file: {}", xmlFilePath);
            }
        }
    }

    /**
     * Creates the report directory and ensures it exists.
     */
    public void ensureReportDirectoryExists() throws IOException {
        String reportPath = config.getReportOutputPath();
        if (reportPath != null && !reportPath.isEmpty()) {
            Path reportDir = Path.of(reportPath).getParent();
            if (reportDir != null && !Files.exists(reportDir)) {
                Files.createDirectories(reportDir);
            }
        }
    }

    /**
     * Validates that the project directory exists and is accessible.
     */
    public boolean validateProjectDirectory(String projectDirectory) {
        Path projectPath = Path.of(projectDirectory);
        
        if (!Files.exists(projectPath)) {
            logger.error("Project directory does not exist: {}", projectDirectory);
            report.addError(new ConversionError("Project directory does not exist: " + projectDirectory, 
                    projectDirectory, ConversionError.ErrorType.FILE_NOT_FOUND));
            return false;
        }
        
        if (!Files.isDirectory(projectPath)) {
            logger.error("Project path is not a directory: {}", projectDirectory);
            report.addError(new ConversionError("Project path is not a directory: " + projectDirectory, 
                    projectDirectory, ConversionError.ErrorType.FILE_NOT_FOUND));
            return false;
        }
        
        if (!Files.isReadable(projectPath)) {
            logger.error("Project directory is not readable: {}", projectDirectory);
            report.addError(new ConversionError("Project directory is not readable: " + projectDirectory, 
                    projectDirectory, ConversionError.ErrorType.PERMISSION_DENIED));
            return false;
        }
        
        return true;
    }

    /**
     * Validates that the backup directory can be created and written to.
     */
    public boolean validateBackupDirectory(String backupDirectory) {
        if (backupDirectory == null || backupDirectory.isEmpty()) {
            return true; // Will use default backup location
        }
        
        Path backupPath = Path.of(backupDirectory);
        
        try {
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
            
            if (!Files.isDirectory(backupPath)) {
                logger.error("Backup path is not a directory: {}", backupDirectory);
                return false;
            }
            
            if (!Files.isWritable(backupPath)) {
                logger.error("Backup directory is not writable: {}", backupDirectory);
                return false;
            }
            
            return true;
        } catch (IOException e) {
            logger.error("Failed to create or validate backup directory: {}", backupDirectory, e);
            return false;
        }
    }

    /**
     * Gets the total number of files in the project directory.
     */
    public long getTotalFileCount(String projectDirectory) {
        try {
            return Files.walk(Path.of(projectDirectory))
                    .filter(Files::isRegularFile)
                    .count();
        } catch (IOException e) {
            logger.warn("Could not count files in project directory: {}", projectDirectory);
            return -1;
        }
    }
} 