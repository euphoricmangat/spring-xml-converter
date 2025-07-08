package com.springconverter.engine;

import com.springconverter.config.ConverterConfig;
import com.springconverter.file.FileManager;
import com.springconverter.java.JavaSourceModifier;
import com.springconverter.model.*;
import com.springconverter.parser.XmlParser;
import com.springconverter.parser.XmlParser.XmlParsingException;
import com.springconverter.report.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main engine that orchestrates the XML to annotation conversion process.
 */
public class ConversionEngine {
    private static final Logger logger = LoggerFactory.getLogger(ConversionEngine.class);
    
    private final ConverterConfig config;
    private final ConversionReport report;
    private final XmlParser xmlParser;
    private final JavaSourceModifier javaModifier;
    private final FileManager fileManager;
    private final ReportGenerator reportGenerator;

    public ConversionEngine(ConverterConfig config) {
        this.config = config;
        this.report = new ConversionReport();
        this.xmlParser = new XmlParser();
        this.javaModifier = new JavaSourceModifier();
        this.fileManager = new FileManager(config, report);
        this.reportGenerator = new ReportGenerator();
        
        // Initialize report with configuration
        this.report.setProjectDirectory(config.getProjectDirectory());
        this.report.setBackupDirectory(config.getBackupDirectory());
        this.report.addConfiguration("createBackups", config.isCreateBackups());
        this.report.addConfiguration("dryRun", config.isDryRun());
        this.report.addConfiguration("verbose", config.isVerbose());
    }

    /**
     * Executes the complete conversion process.
     */
    public ConversionReport execute() {
        logger.info("Starting Spring XML to Annotation conversion");
        logger.info("Project Directory: {}", config.getProjectDirectory());
        logger.info("Configuration: {}", config);
        
        try {
            // Validate inputs
            if (!validateInputs()) {
                logger.error("Input validation failed");
                return report;
            }
            
            // Find XML files
            List<String> xmlFiles = fileManager.findXmlFiles(config.getProjectDirectory());
            report.setTotalXmlFilesProcessed(xmlFiles.size());
            
            if (xmlFiles.isEmpty()) {
                logger.warn("No Spring XML files found in project directory");
                return report;
            }
            
            // Process each XML file
            for (String xmlFile : xmlFiles) {
                processXmlFile(xmlFile);
            }
            
            // Generate final report
            report.finalize();
            reportGenerator.generateReport(report, config);
            reportGenerator.printSummary(report);
            
            logger.info("Conversion process completed");
            
        } catch (Exception e) {
            logger.error("Fatal error during conversion process", e);
            report.addError(new ConversionError("Fatal error: " + e.getMessage(), 
                    config.getProjectDirectory(), ConversionError.ErrorType.CONFIGURATION_ERROR));
            report.finalize();
        }
        
        return report;
    }

    private boolean validateInputs() {
        // Validate project directory
        if (!fileManager.validateProjectDirectory(config.getProjectDirectory())) {
            return false;
        }
        
        // Validate backup directory
        if (!fileManager.validateBackupDirectory(config.getBackupDirectory())) {
            return false;
        }
        
        // Ensure report directory exists
        try {
            fileManager.ensureReportDirectoryExists();
        } catch (IOException e) {
            logger.error("Failed to create report directory", e);
            return false;
        }
        
        return true;
    }

    private void processXmlFile(String xmlFilePath) {
        logger.info("Processing XML file: {}", xmlFilePath);
        
        try {
            // Parse XML file
            List<SpringBean> beans = xmlParser.parseXmlFile(xmlFilePath);
            
            if (beans.isEmpty()) {
                logger.info("No beans found in XML file: {}", xmlFilePath);
                return;
            }
            
            // Track converted beans for this file
            List<String> convertedBeanIds = new ArrayList<>();
            
            // Process each bean
            for (SpringBean bean : beans) {
                ConversionResult result = processBean(bean);
                report.addConversionResult(result);
                
                if (result.isSuccessful()) {
                    convertedBeanIds.add(bean.getId());
                    report.setTotalBeansConverted(report.getTotalBeansConverted() + 1);
                    report.setTotalPropertiesConverted(report.getTotalPropertiesConverted() + bean.getProperties().size());
                    report.setTotalConstructorArgsConverted(report.getTotalConstructorArgsConverted() + bean.getConstructorArgs().size());
                } else {
                    logger.warn("Failed to convert bean: {} - {}", bean.getId(), result.getErrorMessage());
                }
            }
            
            // Update XML file to comment out converted beans
            if (!convertedBeanIds.isEmpty()) {
                fileManager.updateXmlFile(xmlFilePath, convertedBeanIds);
            }
            
            // Remove empty XML file if configured
            fileManager.removeEmptyXmlFile(xmlFilePath);
            
        } catch (XmlParsingException e) {
            logger.error("Failed to parse XML file: {}", xmlFilePath, e);
            report.addError(new ConversionError("XML parsing error: " + e.getMessage(), 
                    xmlFilePath, ConversionError.ErrorType.XML_PARSING_ERROR));
        } catch (IOException e) {
            logger.error("Failed to process XML file: {}", xmlFilePath, e);
            report.addError(new ConversionError("File processing error: " + e.getMessage(), 
                    xmlFilePath, ConversionError.ErrorType.WRITE_ERROR));
        }
    }

    private ConversionResult processBean(SpringBean bean) {
        logger.debug("Processing bean: {} ({})", bean.getId(), bean.getClassName());
        
        ConversionResult result = new ConversionResult(bean.getSourceFile(), bean.getId(), 
                ConversionResult.ConversionType.BEAN_TO_COMPONENT);
        
        try {
            // Find corresponding Java file
            List<String> javaFiles = javaModifier.findJavaFilesForClass(config.getProjectDirectory(), bean.getClassName());
            
            if (javaFiles.isEmpty()) {
                // No Java file found - create TODO
                TodoItem todo = new TodoItem(
                    "No Java file found for bean: " + bean.getId() + " (class: " + bean.getClassName() + ")",
                    bean.getSourceFile(),
                    TodoItem.TodoCategory.AMBIGUOUS_BEAN_MAPPING
                );
                todo.setPriority(TodoItem.TodoPriority.HIGH);
                todo.setRelatedBeanId(bean.getId());
                todo.setRelatedClassName(bean.getClassName());
                todo.setSuggestedAction("Create or locate the Java class: " + bean.getClassName());
                report.addTodoItem(todo);
                
                result.setStatus(ConversionResult.ConversionStatus.SKIPPED);
                result.setErrorMessage("No Java file found for class: " + bean.getClassName());
                return result;
            }
            
            if (javaFiles.size() > 1) {
                // Multiple Java files found - create TODO
                TodoItem todo = new TodoItem(
                    "Multiple Java files found for bean: " + bean.getId() + " (class: " + bean.getClassName() + ")",
                    bean.getSourceFile(),
                    TodoItem.TodoCategory.AMBIGUOUS_BEAN_MAPPING
                );
                todo.setPriority(TodoItem.TodoPriority.HIGH);
                todo.setRelatedBeanId(bean.getId());
                todo.setRelatedClassName(bean.getClassName());
                todo.setSuggestedAction("Manually specify which Java file to use: " + String.join(", ", javaFiles));
                report.addTodoItem(todo);
                
                result.setStatus(ConversionResult.ConversionStatus.SKIPPED);
                result.setErrorMessage("Multiple Java files found for class: " + bean.getClassName());
                return result;
            }
            
            // Modify Java file
            String javaFile = javaFiles.get(0);
            ConversionResult javaResult = javaModifier.modifyJavaFile(javaFile, bean);
            
            if (javaResult.isSuccessful()) {
                report.setTotalJavaFilesModified(report.getTotalJavaFilesModified() + 1);
                result.setStatus(ConversionResult.ConversionStatus.SUCCESS);
                result.setTargetFile(javaFile);
                result.setAnnotationsAdded(javaResult.getAnnotationsAdded());
                result.setModifications(javaResult.getModifications());
            } else {
                result.setStatus(ConversionResult.ConversionStatus.FAILED);
                result.setErrorMessage(javaResult.getErrorMessage());
                
                // Add TODO for failed conversion
                TodoItem todo = new TodoItem(
                    "Failed to convert bean: " + bean.getId() + " - " + javaResult.getErrorMessage(),
                    javaFile,
                    TodoItem.TodoCategory.MANUAL_REVIEW_REQUIRED
                );
                todo.setPriority(TodoItem.TodoPriority.HIGH);
                todo.setRelatedBeanId(bean.getId());
                todo.setRelatedClassName(bean.getClassName());
                todo.setSuggestedAction("Manually add Spring annotations to: " + javaFile);
                report.addTodoItem(todo);
            }
            
        } catch (Exception e) {
            logger.error("Error processing bean: {}", bean.getId(), e);
            result.setStatus(ConversionResult.ConversionStatus.FAILED);
            result.setErrorMessage("Unexpected error: " + e.getMessage());
            
            report.addError(new ConversionError("Bean processing error: " + e.getMessage(), 
                    bean.getSourceFile(), ConversionError.ErrorType.INVALID_BEAN_DEFINITION));
        }
        
        return result;
    }

    /**
     * Gets the conversion report.
     */
    public ConversionReport getReport() {
        return report;
    }

    /**
     * Checks if the conversion was successful.
     */
    public boolean isSuccessful() {
        return report.isSuccessful();
    }
} 