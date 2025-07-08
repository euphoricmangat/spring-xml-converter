package com.springconverter.model;

import java.time.LocalDateTime;

/**
 * Represents a TODO item that requires manual intervention during conversion.
 */
public class TodoItem {
    private String id;
    private String description;
    private String filePath;
    private int lineNumber;
    private TodoPriority priority;
    private TodoCategory category;
    private LocalDateTime createdAt;
    private String suggestedAction;
    private String relatedBeanId;
    private String relatedClassName;

    public TodoItem() {
        this.createdAt = LocalDateTime.now();
    }

    public TodoItem(String description, String filePath, TodoCategory category) {
        this();
        this.description = description;
        this.filePath = filePath;
        this.category = category;
        this.id = generateId();
    }

    private String generateId() {
        return "TODO_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public TodoPriority getPriority() {
        return priority;
    }

    public void setPriority(TodoPriority priority) {
        this.priority = priority;
    }

    public TodoCategory getCategory() {
        return category;
    }

    public void setCategory(TodoCategory category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
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
        return "TodoItem{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", filePath='" + filePath + '\'' +
                ", category=" + category +
                ", priority=" + priority +
                '}';
    }

    public enum TodoPriority {
        HIGH,
        MEDIUM,
        LOW
    }

    public enum TodoCategory {
        AMBIGUOUS_BEAN_MAPPING,
        CUSTOM_XML_NAMESPACE,
        COMPLEX_FACTORY_METHOD,
        EXTERNAL_BEAN_REFERENCE,
        NON_STANDARD_WIRING,
        UNSUPPORTED_XML_ELEMENT,
        MANUAL_REVIEW_REQUIRED,
        CONFIGURATION_ISSUE
    }
} 