package com.springconverter.java;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.type.Type;
import com.springconverter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Analyzes and modifies Java source files to add Spring annotations.
 */
public class JavaSourceModifier {
    private static final Logger logger = LoggerFactory.getLogger(JavaSourceModifier.class);

    /**
     * Modifies a Java source file to add Spring annotations based on bean definitions.
     */
    public ConversionResult modifyJavaFile(String filePath, SpringBean bean) {
        logger.info("Modifying Java file: {} for bean: {}", filePath, bean.getId());
        
        ConversionResult result = new ConversionResult(filePath, bean.getId(), 
                ConversionResult.ConversionType.BEAN_TO_COMPONENT);
        
        try {
            // Parse the Java file
            CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
            
            // Find the class that matches the bean
            Optional<ClassOrInterfaceDeclaration> classOpt = findClassByName(cu, bean.getClassName());
            
            if (classOpt.isPresent()) {
                ClassOrInterfaceDeclaration classDecl = classOpt.get();
                
                // Add class-level annotations
                addClassAnnotations(classDecl, bean, result);
                
                // Add field-level annotations for properties
                addFieldAnnotations(classDecl, bean, result);
                
                // Add constructor annotations
                addConstructorAnnotations(classDecl, bean, result);
                
                // Write the modified file
                if (!result.isFailed()) {
                    writeModifiedFile(cu, filePath);
                    result.setStatus(ConversionResult.ConversionStatus.SUCCESS);
                    result.setTargetFile(filePath);
                }
                
            } else {
                result.setStatus(ConversionResult.ConversionStatus.FAILED);
                result.setErrorMessage("Class not found: " + bean.getClassName());
            }
            
        } catch (FileNotFoundException e) {
            result.setStatus(ConversionResult.ConversionStatus.FAILED);
            result.setErrorMessage("File not found: " + filePath);
        } catch (Exception e) {
            result.setStatus(ConversionResult.ConversionStatus.FAILED);
            result.setErrorMessage("Error modifying file: " + e.getMessage());
            logger.error("Error modifying Java file: {}", filePath, e);
        }
        
        return result;
    }

    private Optional<ClassOrInterfaceDeclaration> findClassByName(CompilationUnit cu, String className) {
        if (className == null || className.isEmpty()) {
            return Optional.empty();
        }
        
        // Extract simple class name from full class name
        final String simpleClassName = className.contains(".") ? 
                className.substring(className.lastIndexOf('.') + 1) : className;
        
        return cu.findFirst(ClassOrInterfaceDeclaration.class, 
                classDecl -> classDecl.getNameAsString().equals(simpleClassName));
    }

    private void addClassAnnotations(ClassOrInterfaceDeclaration classDecl, SpringBean bean, ConversionResult result) {
        // Determine the appropriate annotation based on class name or bean attributes
        String annotation = determineClassAnnotation(bean);
        
        if (annotation != null) {
            // Check if annotation already exists
            boolean annotationExists = classDecl.getAnnotations().stream()
                    .anyMatch(ann -> ann.getNameAsString().equals(annotation.substring(1)));
            
            if (!annotationExists) {
                AnnotationExpr annotationExpr = new MarkerAnnotationExpr(annotation);
                classDecl.addAnnotation(annotationExpr);
                result.addAnnotation(annotation);
                result.addModification("Added " + annotation + " to class");
                logger.debug("Added {} annotation to class {}", annotation, classDecl.getNameAsString());
            }
        }
        
        // Add @Primary if specified
        if (bean.isPrimary()) {
            addAnnotationIfNotExists(classDecl, "@Primary", result);
        }
        
        // Add @Lazy if specified
        if (bean.isLazyInit()) {
            addAnnotationIfNotExists(classDecl, "@Lazy", result);
        }
        
        // Add @Scope if not singleton
        if (!"singleton".equals(bean.getScope())) {
            SingleMemberAnnotationExpr scopeAnnotation = new SingleMemberAnnotationExpr();
            scopeAnnotation.setName("Scope");
            scopeAnnotation.setMemberValue(new NameExpr("\"" + bean.getScope() + "\""));
            classDecl.addAnnotation(scopeAnnotation);
            result.addAnnotation("@Scope(\"" + bean.getScope() + "\")");
            result.addModification("Added @Scope annotation");
        }
    }

    private String determineClassAnnotation(SpringBean bean) {
        String className = bean.getClassName();
        if (className == null) {
            return "@Component";
        }
        
        final String simpleClassName = className.contains(".") ? 
                className.substring(className.lastIndexOf('.') + 1) : className;
        
        // Determine annotation based on class name patterns
        String lowerClassName = simpleClassName.toLowerCase();
        if (lowerClassName.contains("service")) {
            return "@Service";
        } else if (lowerClassName.contains("repository") || lowerClassName.contains("dao")) {
            return "@Repository";
        } else if (lowerClassName.contains("controller")) {
            return "@Controller";
        } else if (lowerClassName.contains("config") || lowerClassName.contains("configuration")) {
            return "@Configuration";
        } else {
            return "@Component";
        }
    }

    private void addFieldAnnotations(ClassOrInterfaceDeclaration classDecl, SpringBean bean, ConversionResult result) {
        for (Property property : bean.getProperties()) {
            if (property.isReference()) {
                // Find the field with the property name
                Optional<FieldDeclaration> fieldOpt = classDecl.getFields().stream()
                        .filter(field -> field.getVariable(0).getNameAsString().equals(property.getName()))
                        .findFirst();
                
                if (fieldOpt.isPresent()) {
                    FieldDeclaration field = fieldOpt.get();
                    addAnnotationIfNotExists(field, "@Autowired", result);
                    
                    // Add @Qualifier if needed
                    if (property.getRef() != null && !property.getRef().isEmpty()) {
                        addQualifierAnnotation(field, property.getRef(), result);
                    }
                } else {
                    // Field doesn't exist, add TODO
                    result.addModification("TODO: Add field '" + property.getName() + "' with @Autowired annotation");
                }
            } else if (property.isValue()) {
                // Handle @Value annotations
                Optional<FieldDeclaration> fieldOpt = classDecl.getFields().stream()
                        .filter(field -> field.getVariable(0).getNameAsString().equals(property.getName()))
                        .findFirst();
                
                if (fieldOpt.isPresent()) {
                    FieldDeclaration field = fieldOpt.get();
                    addValueAnnotation(field, property.getValue(), result);
                }
            }
        }
    }

    private void addConstructorAnnotations(ClassOrInterfaceDeclaration classDecl, SpringBean bean, ConversionResult result) {
        if (!bean.getConstructorArgs().isEmpty()) {
            // Find constructor with matching number of parameters
            Optional<MethodDeclaration> constructorOpt = classDecl.getMethods().stream()
                    .filter(method -> method.getNameAsString().equals(classDecl.getNameAsString()) && 
                            method.getParameters().size() == bean.getConstructorArgs().size())
                    .findFirst();
            
            if (constructorOpt.isPresent()) {
                MethodDeclaration constructor = constructorOpt.get();
                addAnnotationIfNotExists(constructor, "@Autowired", result);
                
                // Add @Qualifier annotations to parameters if needed
                for (int i = 0; i < constructor.getParameters().size() && i < bean.getConstructorArgs().size(); i++) {
                    Parameter param = constructor.getParameter(i);
                    ConstructorArg arg = bean.getConstructorArgs().get(i);
                    
                    if (arg.isReference() && arg.getRef() != null) {
                        addQualifierAnnotation(param, arg.getRef(), result);
                    }
                }
            }
        }
    }

    private void addAnnotationIfNotExists(ClassOrInterfaceDeclaration classDecl, String annotation, ConversionResult result) {
        boolean exists = classDecl.getAnnotations().stream()
                .anyMatch(ann -> ann.getNameAsString().equals(annotation.substring(1)));
        
        if (!exists) {
            AnnotationExpr annotationExpr = new MarkerAnnotationExpr(annotation);
            classDecl.addAnnotation(annotationExpr);
            result.addAnnotation(annotation);
            result.addModification("Added " + annotation + " annotation");
        }
    }

    private void addAnnotationIfNotExists(FieldDeclaration field, String annotation, ConversionResult result) {
        boolean exists = field.getAnnotations().stream()
                .anyMatch(ann -> ann.getNameAsString().equals(annotation.substring(1)));
        
        if (!exists) {
            AnnotationExpr annotationExpr = new MarkerAnnotationExpr(annotation);
            field.addAnnotation(annotationExpr);
            result.addAnnotation(annotation);
            result.addModification("Added " + annotation + " to field " + field.getVariable(0).getNameAsString());
        }
    }

    private void addAnnotationIfNotExists(MethodDeclaration method, String annotation, ConversionResult result) {
        boolean exists = method.getAnnotations().stream()
                .anyMatch(ann -> ann.getNameAsString().equals(annotation.substring(1)));
        
        if (!exists) {
            AnnotationExpr annotationExpr = new MarkerAnnotationExpr(annotation);
            method.addAnnotation(annotationExpr);
            result.addAnnotation(annotation);
            result.addModification("Added " + annotation + " to constructor");
        }
    }

    private void addQualifierAnnotation(FieldDeclaration field, String qualifier, ConversionResult result) {
        SingleMemberAnnotationExpr qualifierAnnotation = new SingleMemberAnnotationExpr();
        qualifierAnnotation.setName("Qualifier");
        qualifierAnnotation.setMemberValue(new NameExpr("\"" + qualifier + "\""));
        field.addAnnotation(qualifierAnnotation);
        result.addAnnotation("@Qualifier(\"" + qualifier + "\")");
        result.addModification("Added @Qualifier annotation");
    }

    private void addQualifierAnnotation(Parameter parameter, String qualifier, ConversionResult result) {
        SingleMemberAnnotationExpr qualifierAnnotation = new SingleMemberAnnotationExpr();
        qualifierAnnotation.setName("Qualifier");
        qualifierAnnotation.setMemberValue(new NameExpr("\"" + qualifier + "\""));
        parameter.addAnnotation(qualifierAnnotation);
        result.addAnnotation("@Qualifier(\"" + qualifier + "\")");
        result.addModification("Added @Qualifier annotation to parameter");
    }

    private void addValueAnnotation(FieldDeclaration field, String value, ConversionResult result) {
        SingleMemberAnnotationExpr valueAnnotation = new SingleMemberAnnotationExpr();
        valueAnnotation.setName("Value");
        valueAnnotation.setMemberValue(new NameExpr("\"" + value + "\""));
        field.addAnnotation(valueAnnotation);
        result.addAnnotation("@Value(\"" + value + "\")");
        result.addModification("Added @Value annotation");
    }

    private void writeModifiedFile(CompilationUnit cu, String filePath) throws IOException {
        String modifiedContent = cu.toString();
        Files.write(Path.of(filePath), modifiedContent.getBytes());
        logger.debug("Successfully wrote modified file: {}", filePath);
    }

    /**
     * Finds Java source files that correspond to a given class name.
     */
    public List<String> findJavaFilesForClass(String projectDirectory, String className) {
        List<String> javaFiles = new ArrayList<>();
        
        if (className == null || className.isEmpty()) {
            return javaFiles;
        }
        
        final String simpleClassName = className.contains(".") ? 
                className.substring(className.lastIndexOf('.') + 1) : className;
        
        try {
            Files.walk(Path.of(projectDirectory))
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> path.getFileName().toString().equals(simpleClassName + ".java"))
                    .forEach(path -> javaFiles.add(path.toString()));
        } catch (IOException e) {
            logger.error("Error searching for Java files", e);
        }
        
        return javaFiles;
    }
} 