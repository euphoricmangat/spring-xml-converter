package com.springconverter.report;

import com.springconverter.config.ConverterConfig;
import com.springconverter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Generates comprehensive reports of the conversion process.
 */
public class ReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    /**
     * Generates a markdown report of the conversion process.
     */
    public void generateReport(ConversionReport report, ConverterConfig config) throws IOException {
        String reportPath = config.getReportOutputPath();
        if (reportPath == null || reportPath.isEmpty()) {
            reportPath = config.getProjectDirectory() + "/conversion_report.md";
        }

        String reportContent = buildReportContent(report, config);
        Files.write(Path.of(reportPath), reportContent.getBytes());
        
        logger.info("Generated conversion report: {}", reportPath);
    }

    private String buildReportContent(ConversionReport report, ConverterConfig config) {
        StringBuilder content = new StringBuilder();
        
        // Header
        content.append("# Spring XML to Annotation Conversion Report\n\n");
        content.append("**Generated:** ").append(report.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        content.append("**Project Directory:** ").append(report.getProjectDirectory()).append("\n");
        if (report.getBackupDirectory() != null) {
            content.append("**Backup Directory:** ").append(report.getBackupDirectory()).append("\n");
        }
        content.append("**Duration:** ").append(report.getDurationInSeconds()).append(" seconds\n\n");
        
        // Summary
        content.append("## Summary\n\n");
        content.append("| Metric | Count |\n");
        content.append("|--------|-------|\n");
        content.append("| XML Files Processed | ").append(report.getTotalXmlFilesProcessed()).append(" |\n");
        content.append("| Java Files Modified | ").append(report.getTotalJavaFilesModified()).append(" |\n");
        content.append("| Beans Converted | ").append(report.getTotalBeansConverted()).append(" |\n");
        content.append("| Properties Converted | ").append(report.getTotalPropertiesConverted()).append(" |\n");
        content.append("| Constructor Args Converted | ").append(report.getTotalConstructorArgsConverted()).append(" |\n");
        content.append("| TODOs Generated | ").append(report.getTotalTODOsGenerated()).append(" |\n");
        content.append("| Errors | ").append(report.getTotalErrors()).append(" |\n");
        content.append("| Success | ").append(report.isSuccessful() ? "✅ Yes" : "❌ No").append(" |\n\n");
        
        // Configuration
        content.append("## Configuration\n\n");
        content.append("| Setting | Value |\n");
        content.append("|---------|-------|\n");
        content.append("| Create Backups | ").append(config.isCreateBackups() ? "Yes" : "No").append(" |\n");
        content.append("| Dry Run | ").append(config.isDryRun() ? "Yes" : "No").append(" |\n");
        content.append("| Remove Empty XML Files | ").append(config.isRemoveEmptyXmlFiles() ? "Yes" : "No").append(" |\n");
        content.append("| Add TODOs for Ambiguous Cases | ").append(config.isAddTODOsForAmbiguousCases() ? "Yes" : "No").append(" |\n\n");
        
        // Conversion Results
        if (!report.getConversionResults().isEmpty()) {
            content.append("## Conversion Results\n\n");
            content.append("| File | Bean ID | Status | Type | Annotations Added |\n");
            content.append("|------|---------|--------|------|-------------------|\n");
            
            for (ConversionResult result : report.getConversionResults()) {
                String statusIcon = getStatusIcon(result.getStatus());
                String annotations = String.join(", ", result.getAnnotationsAdded());
                content.append("| ").append(result.getSourceFile()).append(" | ")
                       .append(result.getBeanId()).append(" | ")
                       .append(statusIcon).append(" ").append(result.getStatus()).append(" | ")
                       .append(result.getType()).append(" | ")
                       .append(annotations.isEmpty() ? "-" : annotations).append(" |\n");
            }
            content.append("\n");
        }
        
        // TODO Items
        if (!report.getTodoItems().isEmpty()) {
            content.append("## TODO Items\n\n");
            content.append("The following items require manual intervention:\n\n");
            
            for (TodoItem todo : report.getTodoItems()) {
                content.append("### ").append(todo.getId()).append("\n\n");
                content.append("- **Description:** ").append(todo.getDescription()).append("\n");
                content.append("- **File:** ").append(todo.getFilePath()).append("\n");
                if (todo.getLineNumber() > 0) {
                    content.append("- **Line:** ").append(todo.getLineNumber()).append("\n");
                }
                content.append("- **Category:** ").append(todo.getCategory()).append("\n");
                content.append("- **Priority:** ").append(todo.getPriority()).append("\n");
                if (todo.getSuggestedAction() != null) {
                    content.append("- **Suggested Action:** ").append(todo.getSuggestedAction()).append("\n");
                }
                if (todo.getRelatedBeanId() != null) {
                    content.append("- **Related Bean:** ").append(todo.getRelatedBeanId()).append("\n");
                }
                content.append("\n");
            }
        }
        
        // Errors
        if (!report.getErrors().isEmpty()) {
            content.append("## Errors\n\n");
            content.append("The following errors occurred during conversion:\n\n");
            
            for (ConversionError error : report.getErrors()) {
                content.append("### ").append(error.getId()).append("\n\n");
                content.append("- **Message:** ").append(error.getMessage()).append("\n");
                content.append("- **File:** ").append(error.getFilePath()).append("\n");
                if (error.getLineNumber() > 0) {
                    content.append("- **Line:** ").append(error.getLineNumber()).append("\n");
                }
                content.append("- **Type:** ").append(error.getType()).append("\n");
                content.append("- **Severity:** ").append(error.getSeverity()).append("\n");
                if (error.getRelatedBeanId() != null) {
                    content.append("- **Related Bean:** ").append(error.getRelatedBeanId()).append("\n");
                }
                content.append("\n");
            }
        }
        
        // File Backups
        if (!report.getFileBackups().isEmpty()) {
            content.append("## File Backups\n\n");
            content.append("The following files were backed up before modification:\n\n");
            content.append("| Original File | Backup File |\n");
            content.append("|---------------|-------------|\n");
            
            for (Map.Entry<String, String> backup : report.getFileBackups().entrySet()) {
                content.append("| ").append(backup.getKey()).append(" | ").append(backup.getValue()).append(" |\n");
            }
            content.append("\n");
        }
        
        // Recommendations
        content.append("## Recommendations\n\n");
        
        if (report.getTotalErrors() > 0) {
            content.append("⚠️ **Review Required:** ").append(report.getTotalErrors())
                   .append(" errors occurred during conversion. Please review the error section above.\n\n");
        }
        
        if (report.getTotalTODOsGenerated() > 0) {
            content.append("📝 **Manual Intervention Required:** ").append(report.getTotalTODOsGenerated())
                   .append(" TODO items were generated. Please review and address each item.\n\n");
        }
        
        if (report.getTotalBeansConverted() > 0) {
            content.append("✅ **Conversion Successful:** ").append(report.getTotalBeansConverted())
                   .append(" beans were successfully converted to annotations.\n\n");
        }
        
        content.append("### Next Steps\n\n");
        content.append("1. **Review the converted code** to ensure all annotations are correctly applied\n");
        content.append("2. **Address TODO items** listed in the report\n");
        content.append("3. **Test the application** to verify functionality is preserved\n");
        content.append("4. **Remove commented XML** once testing is complete\n");
        content.append("5. **Update build configuration** if needed (e.g., component scanning)\n\n");
        
        // Footer
        content.append("---\n");
        content.append("*Report generated by Spring XML to Annotation Converter*\n");
        
        return content.toString();
    }

    private String getStatusIcon(ConversionResult.ConversionStatus status) {
        switch (status) {
            case SUCCESS:
                return "✅";
            case FAILED:
                return "❌";
            case SKIPPED:
                return "⏭️";
            case PARTIAL:
                return "⚠️";
            default:
                return "❓";
        }
    }

    /**
     * Generates a summary report for console output.
     */
    public void printSummary(ConversionReport report) {
        logger.info("=== Conversion Summary ===");
        logger.info("XML Files Processed: {}", report.getTotalXmlFilesProcessed());
        logger.info("Java Files Modified: {}", report.getTotalJavaFilesModified());
        logger.info("Beans Converted: {}", report.getTotalBeansConverted());
        logger.info("Properties Converted: {}", report.getTotalPropertiesConverted());
        logger.info("Constructor Args Converted: {}", report.getTotalConstructorArgsConverted());
        logger.info("TODOs Generated: {}", report.getTotalTODOsGenerated());
        logger.info("Errors: {}", report.getTotalErrors());
        logger.info("Duration: {} seconds", report.getDurationInSeconds());
        logger.info("Success: {}", report.isSuccessful() ? "Yes" : "No");
        
        if (report.getTotalErrors() > 0) {
            logger.warn("⚠️  {} errors occurred during conversion", report.getTotalErrors());
        }
        
        if (report.getTotalTODOsGenerated() > 0) {
            logger.info("📝 {} TODO items require manual intervention", report.getTotalTODOsGenerated());
        }
        
        if (report.isSuccessful()) {
            logger.info("✅ Conversion completed successfully");
        } else {
            logger.error("❌ Conversion completed with errors");
        }
    }
} 