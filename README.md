# Spring XML to Annotation Converter

A Java tool that automatically converts Spring XML-based configuration to annotation-based configuration. This tool scans your codebase, identifies Spring XML configuration files, and transforms them to use modern Spring annotations while preserving all functionality.

## Features

- **Automated Conversion**: Converts XML bean definitions to `@Component`, `@Service`, `@Repository`, `@Controller` annotations
- **Dependency Injection**: Transforms `<property>` and `<constructor-arg>` elements to `@Autowired` annotations
- **Safe Operations**: Creates backups before modifying files with rollback capability
- **Comprehensive Reporting**: Generates detailed reports of all changes and TODO items
- **Flexible Configuration**: Supports YAML, JSON, and properties configuration files
- **Dry Run Mode**: Preview changes without modifying files
- **Selective Processing**: Include/exclude patterns for targeted conversion

## Supported XML Elements

| XML Element | Annotation Equivalent |
|-------------|----------------------|
| `<bean>` | `@Component`, `@Service`, `@Repository`, `@Controller` |
| `<property ref="...">` | `@Autowired` |
| `<property value="...">` | `@Value` |
| `<constructor-arg ref="...">` | Constructor `@Autowired` |
| `<context:component-scan>` | `@ComponentScan` |
| `<import resource="...">` | `@Import` |
| `<aop:config>` | `@EnableAspectJAutoProxy` |
| `<tx:advice>` | `@EnableTransactionManagement` |

## Requirements

- Java 11 or newer
- Maven 3.6+ (for building from source)

## Installation

### Option 1: Download Pre-built JAR

1. Download the latest release JAR file from the releases page
2. Place it in your desired directory

### Option 2: Build from Source

```bash
git clone <repository-url>
cd spring-xml-converter
mvn clean package
```

The executable JAR will be created in the `target/` directory.

## Usage

### Basic Usage

```bash
java -jar xml-to-annotation-converter.jar -p /path/to/your/project
```

### Advanced Usage

```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/your/project \
  -b /path/to/backup/directory \
  -c config.yaml \
  -d \
  -V
```

### Command Line Options

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

### Configuration File

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

Then use it:

```bash
java -jar xml-to-annotation-converter.jar -p /path/to/project -c config.yaml
```

## Examples

### Example 1: Simple Conversion

```bash
# Convert a project with default settings
java -jar xml-to-annotation-converter.jar -p /home/user/my-spring-app
```

### Example 2: Safe Conversion with Backups

```bash
# Convert with backups and dry run first
java -jar xml-to-annotation-converter.jar \
  -p /home/user/my-spring-app \
  -b /home/user/backups \
  -d \
  -V
```

### Example 3: Selective Conversion

```bash
# Convert only specific files, excluding tests
java -jar xml-to-annotation-converter.jar \
  -p /home/user/my-spring-app \
  -i ".*applicationContext.*" \
  -e ".*test.*" \
  -e ".*Test.*"
```

## Conversion Process

### Before Conversion

```xml
<!-- applicationContext.xml -->
<bean id="userService" class="com.example.service.UserServiceImpl">
    <property name="userDao" ref="userDao"/>
    <property name="emailService" ref="emailService"/>
</bean>

<bean id="userDao" class="com.example.dao.UserDaoImpl"/>
```

```java
// UserServiceImpl.java
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private EmailService emailService;
    
    // getters and setters...
}
```

### After Conversion

```xml
<!-- applicationContext.xml (commented out) -->
<!-- Converted to annotation: <bean id="userService" class="com.example.service.UserServiceImpl">...</bean> -->
<!-- Converted to annotation: <bean id="userDao" class="com.example.dao.UserDaoImpl"/> -->
```

```java
// UserServiceImpl.java
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private EmailService emailService;
    
    // getters and setters...
}
```

```java
// UserDaoImpl.java
@Repository
public class UserDaoImpl implements UserDao {
    // implementation...
}
```

## Output and Reports

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

## Safety Features

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

## Handling Edge Cases

### Ambiguous Mappings
When the tool encounters ambiguous situations, it creates TODO items:

- Multiple Java files with the same class name
- Missing Java classes for bean definitions
- Complex XML configurations
- Custom XML namespaces

### Manual Intervention Required
Some cases require manual review:

- Complex factory methods
- External bean references
- Custom XML namespaces
- Non-standard wiring patterns

## Best Practices

### Before Conversion
1. **Backup your project** (the tool does this automatically, but extra safety never hurts)
2. **Run in dry-run mode first** to preview changes
3. **Review your XML configuration** for complex patterns
4. **Ensure your project compiles** before conversion

### After Conversion
1. **Review the generated report** for TODO items
2. **Test your application** thoroughly
3. **Address TODO items** manually
4. **Remove commented XML** once testing is complete
5. **Update build configuration** if needed (e.g., component scanning)

### Testing Strategy
1. Run the tool in dry-run mode
2. Review the preview report
3. Run the actual conversion
4. Compile and test the application
5. Address any issues found
6. Remove old XML configuration

## Troubleshooting

### Common Issues

**"No Spring XML files found"**
- Ensure your project contains Spring XML configuration files
- Check that files contain Spring-specific XML elements
- Verify the project directory path is correct

**"Permission denied"**
- Ensure you have read/write permissions for the project directory
- Check backup directory permissions if specified

**"Class not found"**
- The tool creates TODO items for missing Java classes
- Ensure all referenced classes exist in your project
- Check package names and class paths

**"Multiple Java files found"**
- The tool creates TODO items when multiple files match a class name
- Manually specify which file to use or rename conflicting files

### Getting Help

1. **Check the logs**: Look in `logs/converter.log` for detailed information
2. **Review the report**: The generated report contains detailed error information
3. **Use verbose mode**: Add `-V` flag for more detailed output
4. **Run dry-run first**: Always test with `-d` flag before actual conversion

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or feature requests:

1. Check the troubleshooting section above
2. Review existing issues on GitHub
3. Create a new issue with detailed information

---

**Note**: This tool is designed to assist with Spring XML to annotation migration but may not handle all edge cases. Always review the generated code and test thoroughly before deploying to production. 