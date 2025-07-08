package com.springconverter.model;

import java.time.LocalDateTime;

/**
 * Represents an error that occurred during the conversion process.
 */
public class ConversionError {
    private String id;
    private String message;
    private String filePath;
    private int lineNumber;
    private ErrorSeverity severity;
    private ErrorType type;
    private LocalDateTime timestamp;
    private String stackTrace;
    private String relatedBeanId;
    private String relatedClassName;

    public ConversionError() {
        this.timestamp = LocalDateTime.now();
        this.id = generateId();
    }

    public ConversionError(String message, String filePath, ErrorType type) {
        this();
        this.message = message;
        this.filePath = filePath;
        this.type = type;
    }

    private String generateId() {
        return "ERROR_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ErrorSeverity severity) {
        this.severity = severity;
    }

    public ErrorType getType() {
        return type;
    }

    public void setType(ErrorType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getRelatedBeanId() {
        return relatedBeanId;
    }

    public void setRelatedBeanId(String relatedBeanId) {
        this.relatedBeanId = relatedBeanId;
    }

    public String getRelatedClassName() {
        return relatedClassName;
    }

    public void setRelatedClassName(String relatedClassName) {
        this.relatedClassName = relatedClassName;
    }

    @Override
    public String toString() {
        return "ConversionError{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", filePath='" + filePath + '\'' +
                ", type=" + type +
                ", severity=" + severity +
                '}';
    }

    public enum ErrorSeverity {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }

    public enum ErrorType {
        XML_PARSING_ERROR,
        JAVA_PARSING_ERROR,
        FILE_NOT_FOUND,
        PERMISSION_DENIED,
        INVALID_BEAN_DEFINITION,
        AMBIGUOUS_CLASS_MAPPING,
        UNSUPPORTED_XML_ELEMENT,
        CONFIGURATION_ERROR,
        BACKUP_ERROR,
        WRITE_ERROR
    }
} 