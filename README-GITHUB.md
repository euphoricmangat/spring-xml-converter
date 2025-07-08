# 🚀 Spring XML to Annotation Converter

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](https://github.com/yourusername/spring-xml-converter)

> **A comprehensive Java tool that automatically converts Spring XML-based configuration to annotation-based configuration**

This tool helps developers migrate legacy Spring applications from XML configuration to modern annotation-based configuration while preserving all functionality. It's designed to be production-ready with robust error handling, comprehensive reporting, and safety features.

## ✨ Features

- 🔄 **Automated Conversion**: Converts XML bean definitions to `@Component`, `@Service`, `@Repository`, `@Controller` annotations
- 🔗 **Dependency Injection**: Transforms `<property>` and `<constructor-arg>` elements to `@Autowired` annotations
- 🛡️ **Safe Operations**: Creates backups before modifying files with rollback capability
- 📊 **Comprehensive Reporting**: Generates detailed reports of all changes and TODO items
- ⚙️ **Flexible Configuration**: Supports YAML, JSON, and properties configuration files
- 🔍 **Dry Run Mode**: Preview changes without modifying files
- 🎯 **Selective Processing**: Include/exclude patterns for targeted conversion

## 🚀 Quick Start

### Prerequisites

- Java 11 or newer
- Maven 3.6+ (optional, build scripts provided)

### Installation

#### Option 1: Download Pre-built JAR

1. Download the latest release JAR file from the [releases page](../../releases)
2. Place it in your desired directory

#### Option 2: Build from Source

```bash
# Clone the repository
git clone https://github.com/yourusername/spring-xml-converter.git
cd spring-xml-converter

# Build with Maven
mvn clean package

# Or use the build script (Windows)
build.bat

# Or use the build script (Linux/Mac)
chmod +x build.sh
./build.sh
```

### Basic Usage

```bash
# Convert a project with default settings
java -jar xml-to-annotation-converter.jar -p /path/to/your/project
```

### Advanced Usage

```bash
# Convert with backups and configuration file
java -jar xml-to-annotation-converter.jar \
  -p /path/to/your/project \
  -b /path/to/backup/directory \
  -c config.yaml \
  -d \
  -V
```

## 📋 Supported Conversions

| XML Element | Annotation Equivalent | Status |
|-------------|----------------------|--------|
| `<bean>` | `@Component`, `@Service`, `@Repository`, `@Controller` | ✅ |
| `<property ref="...">` | `@Autowired` | ✅ |
| `<property value="...">` | `@Value` | ✅ |
| `<constructor-arg ref="...">` | Constructor `@Autowired` | ✅ |
| `<context:component-scan>` | `@ComponentScan` | ✅ |
| `<import resource="...">` | `@Import` | ✅ |
| `<aop:config>` | `@EnableAspectJAutoProxy` | ✅ |
| `<tx:advice>` | `@EnableTransactionManagement` | ✅ |

## 🔧 Configuration

Create a configuration file (YAML or properties) for more control:

```yaml
# config.yaml
backupDirectory: ./backup
createBackups: true
dryRun: false
verbose: true
removeEmptyXmlFiles: true
addTODOsForAmbiguousCases: true
reportFormat: markdown
reportOutputPath: ./conversion_report.md
```

## 📖 Examples

### Before Conversion

```xml
<!-- applicationContext.xml -->
<bean id="userService" class="com.example.service.UserServiceImpl">
    <property name="userDao" ref="userDao"/>
    <property name="emailService" ref="emailService"/>
    <property name="maxUsers" value="1000"/>
</bean>
```

```java
// UserServiceImpl.java
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private EmailService emailService;
    private int maxUsers;
    
    // getters and setters...
}
```

### After Conversion

```xml
<!-- applicationContext.xml (commented out) -->
<!-- Converted to annotation: <bean id="userService" class="com.example.service.UserServiceImpl">...</bean> -->
```

```java
// UserServiceImpl.java
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private EmailService emailService;
    
    @Value("1000")
    private int maxUsers;
    
    // getters and setters...
}
```

## 📊 Output and Reports

The tool generates comprehensive reports including:

### Console Output
```
=== Conversion Summary ===
XML Files Processed: 5
Java Files Modified: 12
Beans Converted: 25
Properties Converted: 45
Constructor Args Converted: 8
TODOs Generated: 3
Errors: 0
Duration: 15 seconds
Success: Yes
✅ Conversion completed successfully
```

### Markdown Report
A detailed `conversion_report.md` file is generated containing:
- **Summary**: Statistics and metrics
- **Configuration**: Settings used
- **Conversion Results**: Detailed list of all changes
- **TODO Items**: Items requiring manual intervention
- **Errors**: Any errors encountered
- **File Backups**: List of backup files created
- **Recommendations**: Next steps and best practices

## 🛡️ Safety Features

### Automatic Backups
- Creates timestamped backups before modifying any files
- Automatic rollback on errors
- Configurable backup directory

### Dry Run Mode
- Preview all changes without modifying files
- Perfect for testing and validation

### Validation
- Validates project structure before conversion
- Checks file permissions and accessibility
- Reports issues before making changes

## 🎯 Command Line Options

| Option | Long Option | Description | Required |
|--------|-------------|-------------|----------|
| `-h` | `--help` | Show help message | No |
| `-v` | `--version` | Show version information | No |
| `-p` | `--projectDir` | Project directory to convert | **Yes** |
| `-b` | `--backupDir` | Backup directory | No |
| `-c` | `--config` | Configuration file (YAML/JSON) | No |
| `-d` | `--dryRun` | Dry run mode (no files modified) | No |
| `-n` | `--noBackup` | Disable automatic backups | No |
| `-r` | `--report` | Report output path | No |
| `-e` | `--exclude` | Exclude pattern (regex) | No |
| `-i` | `--include` | Include pattern (regex) | No |
| `-V` | `--verbose` | Verbose output | No |

## 🏗️ Project Structure

```
spring-xml-converter/
├── src/main/java/com/springconverter/
│   ├── Main.java                           # Application entry point
│   ├── model/                              # Data models
│   ├── config/                             # Configuration management
│   ├── parser/                             # XML parsing
│   ├── java/                               # Java code modification
│   ├── file/                               # File operations
│   ├── engine/                             # Main orchestration
│   └── report/                             # Report generation
├── src/test/java/                          # Unit tests
├── sample-project/                         # Demo project
├── config-example.yaml                     # Configuration examples
├── build.bat/build.sh                      # Build scripts
├── demo.bat                                # Demo script
└── README.md                               # Documentation
```

## 🧪 Demo

Run the included demo to see the tool in action:

```bash
# Windows
demo.bat

# Linux/Mac
chmod +x demo.sh
./demo.sh
```

The demo will convert the sample project and show you the results.

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/spring-xml-converter.git
cd spring-xml-converter

# Build the project
mvn clean compile

# Run tests
mvn test

# Create executable JAR
mvn clean package
```

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For issues, questions, or feature requests:

1. Check the [troubleshooting section](README.md#troubleshooting) in the main README
2. Review existing [issues](../../issues) on GitHub
3. Create a new issue with detailed information

## 🙏 Acknowledgments

- [JavaParser](https://javaparser.org/) for Java AST manipulation
- [Spring Framework](https://spring.io/) for the configuration patterns
- [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/) for command-line parsing
- [Logback](https://logback.qos.ch/) for logging

## 📈 Roadmap

- [ ] JSON configuration support
- [ ] Parallel processing for large projects
- [ ] IDE plugin integration
- [ ] Web-based frontend
- [ ] Enhanced support for custom XML namespaces
- [ ] Integration with build tools (Maven, Gradle)

---

**⭐ If you find this tool helpful, please give it a star on GitHub!**

---

**Note**: This tool is designed to assist with Spring XML to annotation migration but may not handle all edge cases. Always review the generated code and test thoroughly before deploying to production. 