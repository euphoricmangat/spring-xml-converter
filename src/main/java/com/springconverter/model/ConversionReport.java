package com.springconverter.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive report of the XML to annotation conversion process.
 */
public class ConversionReport {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String projectDirectory;
    private String backupDirectory;
    
    // Statistics
    private int totalXmlFilesProcessed;
    private int totalJavaFilesModified;
    private int totalBeansConverted;
    private int totalPropertiesConverted;
    private int totalConstructorArgsConverted;
    private int totalTODOsGenerated;
    private int totalErrors;
    
    // Detailed tracking
    private List<ConversionResult> conversionResults;
    private List<TodoItem> todoItems;
    private List<ConversionError> errors;
    private Map<String, String> fileBackups;
    
    // Configuration used
    private Map<String, Object> configuration;

    public ConversionReport() {
        this.startTime = LocalDateTime.now();
        this.conversionResults = new ArrayList<>();
        this.todoItems = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.fileBackups = new HashMap<>();
        this.configuration = new HashMap<>();
    }

    // Getters and Setters
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getProjectDirectory() {
        return projectDirectory;
    }

    public void setProjectDirectory(String projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    public String getBackupDirectory() {
        return backupDirectory;
    }

    public void setBackupDirectory(String backupDirectory) {
        this.backupDirectory = backupDirectory;
    }

    public int getTotalXmlFilesProcessed() {
        return totalXmlFilesProcessed;
    }

    public void setTotalXmlFilesProcessed(int totalXmlFilesProcessed) {
        this.totalXmlFilesProcessed = totalXmlFilesProcessed;
    }

    public int getTotalJavaFilesModified() {
        return totalJavaFilesModified;
    }

    public void setTotalJavaFilesModified(int totalJavaFilesModified) {
        this.totalJavaFilesModified = totalJavaFilesModified;
    }

    public int getTotalBeansConverted() {
        return totalBeansConverted;
    }

    public void setTotalBeansConverted(int totalBeansConverted) {
        this.totalBeansConverted = totalBeansConverted;
    }

    public int getTotalPropertiesConverted() {
        return totalPropertiesConverted;
    }

    public void setTotalPropertiesConverted(int totalPropertiesConverted) {
        this.totalPropertiesConverted = totalPropertiesConverted;
    }

    public int getTotalConstructorArgsConverted() {
        return totalConstructorArgsConverted;
    }

    public void setTotalConstructorArgsConverted(int totalConstructorArgsConverted) {
        this.totalConstructorArgsConverted = totalConstructorArgsConverted;
    }

    public int getTotalTODOsGenerated() {
        return totalTODOsGenerated;
    }

    public void setTotalTODOsGenerated(int totalTODOsGenerated) {
        this.totalTODOsGenerated = totalTODOsGenerated;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(int totalErrors) {
        this.totalErrors = totalErrors;
    }

    public List<ConversionResult> getConversionResults() {
        return conversionResults;
    }

    public void setConversionResults(List<ConversionResult> conversionResults) {
        this.conversionResults = conversionResults;
    }

    public void addConversionResult(ConversionResult result) {
        this.conversionResults.add(result);
    }

    public List<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    public void addTodoItem(TodoItem todo) {
        this.todoItems.add(todo);
        this.totalTODOsGenerated++;
    }

    public List<ConversionError> getErrors() {
        return errors;
    }

    public void setErrors(List<ConversionError> errors) {
        this.errors = errors;
    }

    public void addError(ConversionError error) {
        this.errors.add(error);
        this.totalErrors++;
    }

    public Map<String, String> getFileBackups() {
        return fileBackups;
    }

    public void setFileBackups(Map<String, String> fileBackups) {
        this.fileBackups = fileBackups;
    }

    public void addFileBackup(String originalFile, String backupFile) {
        this.fileBackups.put(originalFile, backupFile);
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public void addConfiguration(String key, Object value) {
        this.configuration.put(key, value);
    }

    public long getDurationInSeconds() {
        if (endTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, endTime).getSeconds();
    }

    public boolean isSuccessful() {
        return totalErrors == 0;
    }

    public void finalize() {
        this.endTime = LocalDateTime.now();
    }
} 