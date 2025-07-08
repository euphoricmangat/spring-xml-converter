package com.springconverter.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of converting a single Spring bean or configuration element.
 */
public class ConversionResult {
    private String sourceFile;
    private String targetFile;
    private String beanId;
    private String className;
    private ConversionStatus status;
    private LocalDateTime timestamp;
    private List<String> annotationsAdded;
    private List<String> modifications;
    private String errorMessage;
    private ConversionType type;

    public ConversionResult() {
        this.timestamp = LocalDateTime.now();
        this.annotationsAdded = new ArrayList<>();
        this.modifications = new ArrayList<>();
    }

    public ConversionResult(String sourceFile, String beanId, ConversionType type) {
        this();
        this.sourceFile = sourceFile;
        this.beanId = beanId;
        this.type = type;
    }

    // Getters and Setters
    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ConversionStatus getStatus() {
        return status;
    }

    public void setStatus(ConversionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getAnnotationsAdded() {
        return annotationsAdded;
    }

    public void setAnnotationsAdded(List<String> annotationsAdded) {
        this.annotationsAdded = annotationsAdded;
    }

    public void addAnnotation(String annotation) {
        this.annotationsAdded.add(annotation);
    }

    public List<String> getModifications() {
        return modifications;
    }

    public void setModifications(List<String> modifications) {
        this.modifications = modifications;
    }

    public void addModification(String modification) {
        this.modifications.add(modification);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ConversionType getType() {
        return type;
    }

    public void setType(ConversionType type) {
        this.type = type;
    }

    public boolean isSuccessful() {
        return status == ConversionStatus.SUCCESS;
    }

    public boolean isFailed() {
        return status == ConversionStatus.FAILED;
    }

    public boolean isSkipped() {
        return status == ConversionStatus.SKIPPED;
    }

    @Override
    public String toString() {
        return "ConversionResult{" +
                "sourceFile='" + sourceFile + '\'' +
                ", beanId='" + beanId + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}';
    }

    public enum ConversionStatus {
        SUCCESS,
        FAILED,
        SKIPPED,
        PARTIAL
    }

    public enum ConversionType {
        BEAN_TO_COMPONENT,
        PROPERTY_TO_AUTOWIRED,
        CONSTRUCTOR_ARG_TO_AUTOWIRED,
        COMPONENT_SCAN,
        IMPORT_RESOURCE,
        AOP_CONFIG,
        TRANSACTION_CONFIG,
        CUSTOM
    }
} 