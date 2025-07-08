package com.springconverter.parser;

import com.springconverter.model.SpringBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlParserTest {

    private XmlParser parser;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        parser = new XmlParser();
    }

    @Test
    void testParseSimpleBean() throws IOException, XmlParser.XmlParsingException {
        // Create a simple XML file
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.springframework.org/schema/beans
                   http://www.springframework.org/schema/beans/spring-beans.xsd">
                
                <bean id="userService" class="com.example.service.UserServiceImpl"/>
                
            </beans>
            """;
        
        Path xmlFile = tempDir.resolve("test.xml");
        Files.write(xmlFile, xmlContent.getBytes());
        
        List<SpringBean> beans = parser.parseXmlFile(xmlFile.toString());
        
        assertEquals(1, beans.size());
        SpringBean bean = beans.get(0);
        assertEquals("userService", bean.getId());
        assertEquals("com.example.service.UserServiceImpl", bean.getClassName());
        assertEquals("singleton", bean.getScope());
    }

    @Test
    void testParseBeanWithProperties() throws IOException, XmlParser.XmlParsingException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.springframework.org/schema/beans
                   http://www.springframework.org/schema/beans/spring-beans.xsd">
                
                <bean id="userService" class="com.example.service.UserServiceImpl">
                    <property name="userDao" ref="userDao"/>
                    <property name="maxUsers" value="100"/>
                </bean>
                
            </beans>
            """;
        
        Path xmlFile = tempDir.resolve("test.xml");
        Files.write(xmlFile, xmlContent.getBytes());
        
        List<SpringBean> beans = parser.parseXmlFile(xmlFile.toString());
        
        assertEquals(1, beans.size());
        SpringBean bean = beans.get(0);
        assertEquals(2, bean.getProperties().size());
        
        // Check first property (ref)
        assertEquals("userDao", bean.getProperties().get(0).getName());
        assertEquals("userDao", bean.getProperties().get(0).getRef());
        assertTrue(bean.getProperties().get(0).isReference());
        
        // Check second property (value)
        assertEquals("maxUsers", bean.getProperties().get(1).getName());
        assertEquals("100", bean.getProperties().get(1).getValue());
        assertTrue(bean.getProperties().get(1).isValue());
    }

    @Test
    void testParseBeanWithConstructorArgs() throws IOException, XmlParser.XmlParsingException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.springframework.org/schema/beans
                   http://www.springframework.org/schema/beans/spring-beans.xsd">
                
                <bean id="userService" class="com.example.service.UserServiceImpl">
                    <constructor-arg ref="userDao"/>
                    <constructor-arg value="100"/>
                </bean>
                
            </beans>
            """;
        
        Path xmlFile = tempDir.resolve("test.xml");
        Files.write(xmlFile, xmlContent.getBytes());
        
        List<SpringBean> beans = parser.parseXmlFile(xmlFile.toString());
        
        assertEquals(1, beans.size());
        SpringBean bean = beans.get(0);
        assertEquals(2, bean.getConstructorArgs().size());
        
        // Check first constructor arg (ref)
        assertEquals("userDao", bean.getConstructorArgs().get(0).getRef());
        assertTrue(bean.getConstructorArgs().get(0).isReference());
        
        // Check second constructor arg (value)
        assertEquals("100", bean.getConstructorArgs().get(1).getValue());
        assertTrue(bean.getConstructorArgs().get(1).isValue());
    }

    @Test
    void testParseComponentScan() throws IOException, XmlParser.XmlParsingException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                   xmlns:context="http://www.springframework.org/schema/context"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.springframework.org/schema/beans
                   http://www.springframework.org/schema/beans/spring-beans.xsd
                   http://www.springframework.org/schema/context
                   http://www.springframework.org/schema/context/spring-context.xsd">
                
                <context:component-scan base-package="com.example"/>
                
            </beans>
            """;
        
        Path xmlFile = tempDir.resolve("test.xml");
        Files.write(xmlFile, xmlContent.getBytes());
        
        List<SpringBean> beans = parser.parseXmlFile(xmlFile.toString());
        
        assertEquals(1, beans.size());
        SpringBean bean = beans.get(0);
        assertEquals("componentScan", bean.getId());
        assertEquals("org.springframework.context.annotation.ComponentScan", bean.getClassName());
        assertEquals(1, bean.getProperties().size());
        assertEquals("basePackage", bean.getProperties().get(0).getName());
        assertEquals("com.example", bean.getProperties().get(0).getValue());
    }

    @Test
    void testParseImport() throws IOException, XmlParser.XmlParsingException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.springframework.org/schema/beans
                   http://www.springframework.org/schema/beans/spring-beans.xsd">
                
                <import resource="applicationContext-security.xml"/>
                
            </beans>
            """;
        
        Path xmlFile = tempDir.resolve("test.xml");
        Files.write(xmlFile, xmlContent.getBytes());
        
        List<SpringBean> beans = parser.parseXmlFile(xmlFile.toString());
        
        assertEquals(1, beans.size());
        SpringBean bean = beans.get(0);
        assertEquals("import", bean.getId());
        assertEquals("org.springframework.context.annotation.Import", bean.getClassName());
        assertEquals(1, bean.getProperties().size());
        assertEquals("resource", bean.getProperties().get(0).getName());
        assertEquals("applicationContext-security.xml", bean.getProperties().get(0).getValue());
    }

    @Test
    void testParseMultipleBeans() throws IOException, XmlParser.XmlParsingException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.springframework.org/schema/beans
                   http://www.springframework.org/schema/beans/spring-beans.xsd">
                
                <bean id="userService" class="com.example.service.UserServiceImpl"/>
                <bean id="userDao" class="com.example.dao.UserDaoImpl"/>
                <bean id="emailService" class="com.example.service.EmailServiceImpl"/>
                
            </beans>
            """;
        
        Path xmlFile = tempDir.resolve("test.xml");
        Files.write(xmlFile, xmlContent.getBytes());
        
        List<SpringBean> beans = parser.parseXmlFile(xmlFile.toString());
        
        assertEquals(3, beans.size());
        
        // Check all beans are present
        assertTrue(beans.stream().anyMatch(b -> "userService".equals(b.getId())));
        assertTrue(beans.stream().anyMatch(b -> "userDao".equals(b.getId())));
        assertTrue(beans.stream().anyMatch(b -> "emailService".equals(b.getId())));
    }

    @Test
    void testParseBeanWithAttributes() throws IOException, XmlParser.XmlParsingException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.springframework.org/schema/beans
                   http://www.springframework.org/schema/beans/spring-beans.xsd">
                
                <bean id="userService" class="com.example.service.UserServiceImpl" 
                      scope="prototype" lazy-init="true" primary="true"/>
                
            </beans>
            """;
        
        Path xmlFile = tempDir.resolve("test.xml");
        Files.write(xmlFile, xmlContent.getBytes());
        
        List<SpringBean> beans = parser.parseXmlFile(xmlFile.toString());
        
        assertEquals(1, beans.size());
        SpringBean bean = beans.get(0);
        assertEquals("prototype", bean.getScope());
        assertTrue(bean.isLazyInit());
        assertTrue(bean.isPrimary());
    }

    @Test
    void testParseNonSpringXml() throws IOException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <configuration>
                <property name="test" value="value"/>
            </configuration>
            """;
        
        Path xmlFile = tempDir.resolve("test.xml");
        Files.write(xmlFile, xmlContent.getBytes());
        
        List<SpringBean> beans = parser.parseXmlFile(xmlFile.toString());
        
        assertEquals(0, beans.size());
    }

    @Test
    void testParseInvalidXml() {
        String invalidXml = "This is not valid XML";
        
        Path xmlFile = tempDir.resolve("invalid.xml");
        try {
            Files.write(xmlFile, invalidXml.getBytes());
            assertThrows(XmlParser.XmlParsingException.class, () -> {
                parser.parseXmlFile(xmlFile.toString());
            });
        } catch (IOException e) {
            fail("Failed to write test file");
        }
    }
} 