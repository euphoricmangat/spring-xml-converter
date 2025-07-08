package com.springconverter;

import com.springconverter.config.ConverterConfig;
import com.springconverter.engine.ConversionEngine;
import com.springconverter.model.ConversionReport;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Main application class for Spring XML to Annotation Converter.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    private static final String APP_NAME = "Spring XML to Annotation Converter";
    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        try {
            Main app = new Main();
            app.run(args);
        } catch (Exception e) {
            logger.error("Application failed with error", e);
            System.exit(1);
        }
    }

    public void run(String[] args) {
        // Parse command line arguments
        CommandLine cmd = parseCommandLine(args);
        if (cmd == null) {
            System.exit(1);
        }

        // Load configuration
        ConverterConfig config = loadConfiguration(cmd);
        if (config == null) {
            System.exit(1);
        }

        // Validate configuration
        if (!validateConfiguration(config)) {
            System.exit(1);
        }

        // Execute conversion
        ConversionEngine engine = new ConversionEngine(config);
        ConversionReport report = engine.execute();

        // Exit with appropriate code
        System.exit(report.isSuccessful() ? 0 : 1);
    }

    private CommandLine parseCommandLine(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("help")) {
                printHelp(formatter, options);
                return null;
            }
            
            if (cmd.hasOption("version")) {
                printVersion();
                return null;
            }
            
            return cmd;
            
        } catch (ParseException e) {
            logger.error("Failed to parse command line arguments: {}", e.getMessage());
            printHelp(formatter, options);
            return null;
        }
    }

    private Options createOptions() {
        Options options = new Options();
        
        options.addOption("h", "help", false, "Show this help message");
        options.addOption("v", "version", false, "Show version information");
        options.addOption("p", "projectDir", true, "Project directory to convert (required)");
        options.addOption("b", "backupDir", true, "Backup directory (optional)");
        options.addOption("c", "config", true, "Configuration file (YAML/JSON)");
        options.addOption("d", "dryRun", false, "Dry run mode (no files modified)");
        options.addOption("n", "noBackup", false, "Disable automatic backups");
        options.addOption("r", "report", true, "Report output path");
        options.addOption("e", "exclude", true, "Exclude pattern (regex)");
        options.addOption("i", "include", true, "Include pattern (regex)");
        options.addOption("V", "verbose", false, "Verbose output");
        
        return options;
    }

    private void printHelp(HelpFormatter formatter, Options options) {
        System.out.println(APP_NAME + " v" + VERSION);
        System.out.println();
        System.out.println("Converts Spring XML configuration to annotation-based configuration.");
        System.out.println();
        formatter.printHelp("java -jar xml-to-annotation-converter.jar", options);
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar xml-to-annotation-converter.jar -p /path/to/project");
        System.out.println("  java -jar xml-to-annotation-converter.jar -p /path/to/project -b /path/to/backup -d");
        System.out.println("  java -jar xml-to-annotation-converter.jar -p /path/to/project -c config.yaml");
    }

    private void printVersion() {
        System.out.println(APP_NAME + " v" + VERSION);
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
    }

    private ConverterConfig loadConfiguration(CommandLine cmd) {
        ConverterConfig config = new ConverterConfig();
        
        // Required: project directory
        if (!cmd.hasOption("projectDir")) {
            logger.error("Project directory is required. Use -p or --projectDir option.");
            return null;
        }
        config.setProjectDirectory(cmd.getOptionValue("projectDir"));
        
        // Optional: backup directory
        if (cmd.hasOption("backupDir")) {
            config.setBackupDirectory(cmd.getOptionValue("backupDir"));
        }
        
        // Optional: configuration file
        if (cmd.hasOption("config")) {
            if (!loadConfigFile(config, cmd.getOptionValue("config"))) {
                return null;
            }
        }
        
        // Optional: dry run
        if (cmd.hasOption("dryRun")) {
            config.setDryRun(true);
        }
        
        // Optional: disable backups
        if (cmd.hasOption("noBackup")) {
            config.setCreateBackups(false);
        }
        
        // Optional: report path
        if (cmd.hasOption("report")) {
            config.setReportOutputPath(cmd.getOptionValue("report"));
        }
        
        // Optional: exclude patterns
        if (cmd.hasOption("exclude")) {
            String[] excludes = cmd.getOptionValues("exclude");
            for (String exclude : excludes) {
                config.addExcludePattern(exclude);
            }
        }
        
        // Optional: include patterns
        if (cmd.hasOption("include")) {
            String[] includes = cmd.getOptionValues("include");
            for (String include : includes) {
                config.addIncludePattern(include);
            }
        }
        
        // Optional: verbose
        if (cmd.hasOption("verbose")) {
            config.setVerbose(true);
        }
        
        return config;
    }

    private boolean loadConfigFile(ConverterConfig config, String configPath) {
        try {
            Path path = Path.of(configPath);
            if (!Files.exists(path)) {
                logger.error("Configuration file not found: {}", configPath);
                return false;
            }
            
            String extension = getFileExtension(configPath);
            if ("yaml".equalsIgnoreCase(extension) || "yml".equalsIgnoreCase(extension)) {
                return loadYamlConfig(config, path);
            } else if ("json".equalsIgnoreCase(extension)) {
                return loadJsonConfig(config, path);
            } else if ("properties".equalsIgnoreCase(extension)) {
                return loadPropertiesConfig(config, path);
            } else {
                logger.error("Unsupported configuration file format: {}", extension);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Failed to load configuration file: {}", configPath, e);
            return false;
        }
    }

    private boolean loadYamlConfig(ConverterConfig config, Path path) {
        try {
            // Simple YAML parsing - could be enhanced with proper YAML library
            String content = Files.readString(path);
            String[] lines = content.split("\n");
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    switch (key) {
                        case "backupDirectory":
                            config.setBackupDirectory(value);
                            break;
                        case "dryRun":
                            config.setDryRun(Boolean.parseBoolean(value));
                            break;
                        case "createBackups":
                            config.setCreateBackups(Boolean.parseBoolean(value));
                            break;
                        case "verbose":
                            config.setVerbose(Boolean.parseBoolean(value));
                            break;
                        case "removeEmptyXmlFiles":
                            config.setRemoveEmptyXmlFiles(Boolean.parseBoolean(value));
                            break;
                        case "addTODOsForAmbiguousCases":
                            config.setAddTODOsForAmbiguousCases(Boolean.parseBoolean(value));
                            break;
                        case "reportFormat":
                            config.setReportFormat(value);
                            break;
                        case "reportOutputPath":
                            config.setReportOutputPath(value);
                            break;
                    }
                }
            }
            
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to read YAML configuration file", e);
            return false;
        }
    }

    private boolean loadJsonConfig(ConverterConfig config, Path path) {
        // TODO: Implement JSON configuration loading
        logger.warn("JSON configuration loading not yet implemented");
        return false;
    }

    private boolean loadPropertiesConfig(ConverterConfig config, Path path) {
        try {
            Properties props = new Properties();
            props.load(Files.newInputStream(path));
            
            if (props.containsKey("backupDirectory")) {
                config.setBackupDirectory(props.getProperty("backupDirectory"));
            }
            if (props.containsKey("dryRun")) {
                config.setDryRun(Boolean.parseBoolean(props.getProperty("dryRun")));
            }
            if (props.containsKey("createBackups")) {
                config.setCreateBackups(Boolean.parseBoolean(props.getProperty("createBackups")));
            }
            if (props.containsKey("verbose")) {
                config.setVerbose(Boolean.parseBoolean(props.getProperty("verbose")));
            }
            if (props.containsKey("removeEmptyXmlFiles")) {
                config.setRemoveEmptyXmlFiles(Boolean.parseBoolean(props.getProperty("removeEmptyXmlFiles")));
            }
            if (props.containsKey("addTODOsForAmbiguousCases")) {
                config.setAddTODOsForAmbiguousCases(Boolean.parseBoolean(props.getProperty("addTODOsForAmbiguousCases")));
            }
            if (props.containsKey("reportFormat")) {
                config.setReportFormat(props.getProperty("reportFormat"));
            }
            if (props.containsKey("reportOutputPath")) {
                config.setReportOutputPath(props.getProperty("reportOutputPath"));
            }
            
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to read properties configuration file", e);
            return false;
        }
    }

    private String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot > 0) {
            return filePath.substring(lastDot + 1);
        }
        return "";
    }

    private boolean validateConfiguration(ConverterConfig config) {
        // Validate project directory
        File projectDir = new File(config.getProjectDirectory());
        if (!projectDir.exists()) {
            logger.error("Project directory does not exist: {}", config.getProjectDirectory());
            return false;
        }
        
        if (!projectDir.isDirectory()) {
            logger.error("Project path is not a directory: {}", config.getProjectDirectory());
            return false;
        }
        
        if (!projectDir.canRead()) {
            logger.error("Project directory is not readable: {}", config.getProjectDirectory());
            return false;
        }
        
        // Validate backup directory if specified
        if (config.getBackupDirectory() != null && !config.getBackupDirectory().isEmpty()) {
            File backupDir = new File(config.getBackupDirectory());
            if (backupDir.exists() && !backupDir.isDirectory()) {
                logger.error("Backup path exists but is not a directory: {}", config.getBackupDirectory());
                return false;
            }
            
            if (!backupDir.exists()) {
                try {
                    backupDir.mkdirs();
                } catch (Exception e) {
                    logger.error("Failed to create backup directory: {}", config.getBackupDirectory(), e);
                    return false;
                }
            }
            
            if (!backupDir.canWrite()) {
                logger.error("Backup directory is not writable: {}", config.getBackupDirectory());
                return false;
            }
        }
        
        logger.info("Configuration validation passed");
        return true;
    }
} 