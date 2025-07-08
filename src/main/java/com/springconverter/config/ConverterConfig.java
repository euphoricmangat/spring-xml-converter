package com.springconverter.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration settings for the XML to annotation converter.
 */
public class ConverterConfig {
    private String projectDirectory;
    private String backupDirectory;
    private boolean createBackups = true;
    private boolean dryRun = false;
    private boolean verbose = false;
    private List<String> excludePatterns;
    private List<String> includePatterns;
    private Map<String, String> customMappings;
    private Map<String, String> annotationMappings;
    private boolean removeEmptyXmlFiles = true;
    private boolean addTODOsForAmbiguousCases = true;
    private String reportFormat = "markdown";
    private String reportOutputPath;

    public ConverterConfig() {
        this.excludePatterns = new ArrayList<>();
        this.includePatterns = new ArrayList<>();
        this.customMappings = new HashMap<>();
        this.annotationMappings = new HashMap<>();
        initializeDefaultMappings();
    }

    private void initializeDefaultMappings() {
        // Default annotation mappings
        annotationMappings.put("service", "@Service");
        annotationMappings.put("repository", "@Repository");
        annotationMappings.put("controller", "@Controller");
        annotationMappings.put("component", "@Component");
        annotationMappings.put("configuration", "@Configuration");
        annotationMappings.put("autowired", "@Autowired");
        annotationMappings.put("qualifier", "@Qualifier");
        annotationMappings.put("value", "@Value");
        annotationMappings.put("primary", "@Primary");
        annotationMappings.put("lazy", "@Lazy");
        annotationMappings.put("scope", "@Scope");
        annotationMappings.put("dependsOn", "@DependsOn");
        annotationMappings.put("import", "@Import");
        annotationMappings.put("componentScan", "@ComponentScan");
        annotationMappings.put("propertySource", "@PropertySource");
        annotationMappings.put("enableAspectJAutoProxy", "@EnableAspectJAutoProxy");
        annotationMappings.put("enableTransactionManagement", "@EnableTransactionManagement");
        annotationMappings.put("enableCaching", "@EnableCaching");
        annotationMappings.put("enableScheduling", "@EnableScheduling");
        annotationMappings.put("enableAsync", "@EnableAsync");
    }

    // Getters and Setters
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

    public boolean isCreateBackups() {
        return createBackups;
    }

    public void setCreateBackups(boolean createBackups) {
        this.createBackups = createBackups;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public void addExcludePattern(String pattern) {
        this.excludePatterns.add(pattern);
    }

    public List<String> getIncludePatterns() {
        return includePatterns;
    }

    public void setIncludePatterns(List<String> includePatterns) {
        this.includePatterns = includePatterns;
    }

    public void addIncludePattern(String pattern) {
        this.includePatterns.add(pattern);
    }

    public Map<String, String> getCustomMappings() {
        return customMappings;
    }

    public void setCustomMappings(Map<String, String> customMappings) {
        this.customMappings = customMappings;
    }

    public void addCustomMapping(String xmlElement, String annotation) {
        this.customMappings.put(xmlElement, annotation);
    }

    public Map<String, String> getAnnotationMappings() {
        return annotationMappings;
    }

    public void setAnnotationMappings(Map<String, String> annotationMappings) {
        this.annotationMappings = annotationMappings;
    }

    public boolean isRemoveEmptyXmlFiles() {
        return removeEmptyXmlFiles;
    }

    public void setRemoveEmptyXmlFiles(boolean removeEmptyXmlFiles) {
        this.removeEmptyXmlFiles = removeEmptyXmlFiles;
    }

    public boolean isAddTODOsForAmbiguousCases() {
        return addTODOsForAmbiguousCases;
    }

    public void setAddTODOsForAmbiguousCases(boolean addTODOsForAmbiguousCases) {
        this.addTODOsForAmbiguousCases = addTODOsForAmbiguousCases;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public String getReportOutputPath() {
        return reportOutputPath;
    }

    public void setReportOutputPath(String reportOutputPath) {
        this.reportOutputPath = reportOutputPath;
    }

    public String getAnnotationFor(String key) {
        return annotationMappings.get(key.toLowerCase());
    }

    public boolean shouldExcludeFile(String filePath) {
        for (String pattern : excludePatterns) {
            if (filePath.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldIncludeFile(String filePath) {
        if (includePatterns.isEmpty()) {
            return true;
        }
        for (String pattern : includePatterns) {
            if (filePath.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ConverterConfig{" +
                "projectDirectory='" + projectDirectory + '\'' +
                ", backupDirectory='" + backupDirectory + '\'' +
                ", createBackups=" + createBackups +
                ", dryRun=" + dryRun +
                ", verbose=" + verbose +
                ", excludePatterns=" + excludePatterns.size() +
                ", includePatterns=" + includePatterns.size() +
                ", customMappings=" + customMappings.size() +
                '}';
    }
} 