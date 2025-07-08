package com.springconverter.parser;

import com.springconverter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Spring XML configuration files.
 */
public class XmlParser {
    private static final Logger logger = LoggerFactory.getLogger(XmlParser.class);

    /**
     * Parses a Spring XML configuration file and extracts all bean definitions.
     */
    public List<SpringBean> parseXmlFile(String filePath) throws XmlParsingException {
        logger.info("Parsing XML file: {}", filePath);
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            
            List<SpringBean> beans = new ArrayList<>();
            
            // Parse beans
            NodeList beanNodes = document.getElementsByTagName("bean");
            for (int i = 0; i < beanNodes.getLength(); i++) {
                Element beanElement = (Element) beanNodes.item(i);
                SpringBean bean = parseBeanElement(beanElement, filePath);
                beans.add(bean);
            }
            
            // Parse context:component-scan
            NodeList componentScanNodes = document.getElementsByTagName("context:component-scan");
            for (int i = 0; i < componentScanNodes.getLength(); i++) {
                Element componentScanElement = (Element) componentScanNodes.item(i);
                SpringBean componentScanBean = parseComponentScanElement(componentScanElement, filePath);
                beans.add(componentScanBean);
            }
            
            // Parse import elements
            NodeList importNodes = document.getElementsByTagName("import");
            for (int i = 0; i < importNodes.getLength(); i++) {
                Element importElement = (Element) importNodes.item(i);
                SpringBean importBean = parseImportElement(importElement, filePath);
                beans.add(importBean);
            }
            
            logger.info("Parsed {} beans from {}", beans.size(), filePath);
            return beans;
            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new XmlParsingException("Failed to parse XML file: " + filePath, e);
        }
    }

    private SpringBean parseBeanElement(Element beanElement, String sourceFile) {
        String id = beanElement.getAttribute("id");
        String className = beanElement.getAttribute("class");
        String scope = beanElement.getAttribute("scope");
        String lazyInit = beanElement.getAttribute("lazy-init");
        String primary = beanElement.getAttribute("primary");
        String initMethod = beanElement.getAttribute("init-method");
        String destroyMethod = beanElement.getAttribute("destroy-method");
        
        SpringBean bean = new SpringBean(id, className);
        bean.setSourceFile(sourceFile);
        bean.setScope(scope.isEmpty() ? "singleton" : scope);
        bean.setLazyInit("true".equalsIgnoreCase(lazyInit));
        bean.setPrimary("true".equalsIgnoreCase(primary));
        bean.setInitMethod(initMethod);
        bean.setDestroyMethod(destroyMethod);
        
        // Parse properties
        NodeList propertyNodes = beanElement.getElementsByTagName("property");
        for (int i = 0; i < propertyNodes.getLength(); i++) {
            Element propertyElement = (Element) propertyNodes.item(i);
            Property property = parsePropertyElement(propertyElement);
            bean.addProperty(property);
        }
        
        // Parse constructor arguments
        NodeList constructorArgNodes = beanElement.getElementsByTagName("constructor-arg");
        for (int i = 0; i < constructorArgNodes.getLength(); i++) {
            Element constructorArgElement = (Element) constructorArgNodes.item(i);
            ConstructorArg constructorArg = parseConstructorArgElement(constructorArgElement);
            bean.addConstructorArg(constructorArg);
        }
        
        // Store all attributes
        NamedNodeMap attributes = beanElement.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            bean.addAttribute(attr.getNodeName(), attr.getNodeValue());
        }
        
        return bean;
    }

    private Property parsePropertyElement(Element propertyElement) {
        String name = propertyElement.getAttribute("name");
        String value = propertyElement.getAttribute("value");
        String ref = propertyElement.getAttribute("ref");
        String type = propertyElement.getAttribute("type");
        
        Property property = new Property(name);
        property.setType(type);
        
        if (!value.isEmpty()) {
            property.setValue(value);
        } else if (!ref.isEmpty()) {
            property.setRef(ref);
        } else {
            // Check for nested elements
            NodeList children = propertyElement.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) child;
                    String tagName = childElement.getTagName();
                    
                    if ("list".equals(tagName)) {
                        property.setList(true);
                        // Parse list items
                        NodeList listItems = childElement.getElementsByTagName("value");
                        StringBuilder listValue = new StringBuilder();
                        for (int j = 0; j < listItems.getLength(); j++) {
                            if (j > 0) listValue.append(",");
                            listValue.append(listItems.item(j).getTextContent());
                        }
                        property.setValue(listValue.toString());
                    } else if ("map".equals(tagName)) {
                        property.setMap(true);
                        // TODO: Parse map entries
                    } else if ("set".equals(tagName)) {
                        property.setSet(true);
                        // TODO: Parse set items
                    } else if ("value".equals(tagName)) {
                        property.setValue(childElement.getTextContent());
                    } else if ("ref".equals(tagName)) {
                        property.setRef(childElement.getAttribute("bean"));
                    }
                }
            }
        }
        
        return property;
    }

    private ConstructorArg parseConstructorArgElement(Element constructorArgElement) {
        String value = constructorArgElement.getAttribute("value");
        String ref = constructorArgElement.getAttribute("ref");
        String type = constructorArgElement.getAttribute("type");
        String index = constructorArgElement.getAttribute("index");
        String name = constructorArgElement.getAttribute("name");
        
        ConstructorArg constructorArg = new ConstructorArg();
        constructorArg.setType(type);
        constructorArg.setName(name);
        
        if (!index.isEmpty()) {
            try {
                constructorArg.setIndex(Integer.parseInt(index));
            } catch (NumberFormatException e) {
                logger.warn("Invalid index value: {}", index);
            }
        }
        
        if (!value.isEmpty()) {
            constructorArg.setValue(value);
        } else if (!ref.isEmpty()) {
            constructorArg.setRef(ref);
        } else {
            // Check for nested elements
            NodeList children = constructorArgElement.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) child;
                    String tagName = childElement.getTagName();
                    
                    if ("value".equals(tagName)) {
                        constructorArg.setValue(childElement.getTextContent());
                    } else if ("ref".equals(tagName)) {
                        constructorArg.setRef(childElement.getAttribute("bean"));
                    }
                }
            }
        }
        
        return constructorArg;
    }

    private SpringBean parseComponentScanElement(Element componentScanElement, String sourceFile) {
        String basePackage = componentScanElement.getAttribute("base-package");
        
        SpringBean bean = new SpringBean("componentScan", "org.springframework.context.annotation.ComponentScan");
        bean.setSourceFile(sourceFile);
        
        Property basePackageProperty = new Property("basePackage", basePackage);
        bean.addProperty(basePackageProperty);
        
        return bean;
    }

    private SpringBean parseImportElement(Element importElement, String sourceFile) {
        String resource = importElement.getAttribute("resource");
        
        SpringBean bean = new SpringBean("import", "org.springframework.context.annotation.Import");
        bean.setSourceFile(sourceFile);
        
        Property resourceProperty = new Property("resource", resource);
        bean.addProperty(resourceProperty);
        
        return bean;
    }

    /**
     * Custom exception for XML parsing errors.
     */
    public static class XmlParsingException extends Exception {
        public XmlParsingException(String message) {
            super(message);
        }
        
        public XmlParsingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 