# Spring XML to Annotation Converter - Usage Guide

## 🚀 Quick Start

### Prerequisites
- Java 11 or newer
- Spring project with XML configuration files
- Backup of your project (recommended)

### Basic Usage

```bash
# Convert a project with default settings
java -jar xml-to-annotation-converter.jar -p /path/to/your/spring/project

# Convert with verbose output
java -jar xml-to-annotation-converter.jar -p /path/to/your/spring/project -V

# Dry run (preview changes without modifying files)
java -jar xml-to-annotation-converter.jar -p /path/to/your/spring/project -d
```

## 📁 Project Structure Examples

### Example 1: Standard Maven Project
```
my-spring-app/
├── src/main/java/
│   ├── com/example/
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   └── UserServiceImpl.java
│   │   ├── dao/
│   │   │   ├── UserDao.java
│   │   │   └── UserDaoImpl.java
│   │   └── controller/
│   │       └── UserController.java
│   └── com/example/
├── src/main/resources/
│   ├── applicationContext.xml
│   ├── spring-security.xml
│   └── database-config.xml
└── pom.xml
```

### Example 2: Gradle Project
```
my-spring-app/
├── src/main/java/
│   └── com/example/
├── src/main/resources/
│   ├── applicationContext.xml
│   └── spring-config.xml
├── build.gradle
└── settings.gradle
```

## 🔧 Command Line Options

| Option | Description | Example |
|--------|-------------|---------|
| `-p, --projectDir` | Project directory to convert | `-p /home/user/my-spring-app` |
| `-b, --backupDir` | Backup directory | `-b /home/user/backups` |
| `-c, --config` | Configuration file | `-c config.yaml` |
| `-d, --dryRun` | Preview changes only | `-d` |
| `-n, --noBackup` | Skip backups | `-n` |
| `-r, --report` | Custom report path | `-r conversion_report.md` |
| `-e, --exclude` | Exclude pattern | `-e ".*test.*"` |
| `-i, --include` | Include pattern | `-i ".*service.*"` |
| `-V, --verbose` | Verbose output | `-V` |
| `-h, --help` | Show help | `-h` |

## 📋 Step-by-Step Conversion Process

### Step 1: Prepare Your Project
```bash
# 1. Create a backup
cp -r my-spring-app my-spring-app-backup

# 2. Ensure your project compiles
cd my-spring-app
mvn clean compile
```

### Step 2: Run the Converter
```bash
# Basic conversion
java -jar xml-to-annotation-converter.jar -p /path/to/my-spring-app

# With backups and verbose output
java -jar xml-to-annotation-converter.jar \
  -p /path/to/my-spring-app \
  -b /path/to/backups \
  -V
```

### Step 3: Review the Results
```bash
# Check the generated report
cat conversion_report.md

# Review modified files
git diff  # if using git
```

### Step 4: Test Your Application
```bash
# Compile and test
mvn clean compile test

# Run the application
mvn spring-boot:run
```

## 🎯 Real-World Examples

### Example 1: Convert a Legacy Spring MVC Application

**Before Conversion:**
```xml
<!-- applicationContext.xml -->
<bean id="userService" class="com.example.service.UserServiceImpl">
    <property name="userDao" ref="userDao"/>
    <property name="emailService" ref="emailService"/>
    <property name="maxUsers" value="1000"/>
</bean>

<bean id="userDao" class="com.example.dao.UserDaoImpl">
    <property name="dataSource" ref="dataSource"/>
</bean>
```

**After Conversion:**
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
    
    // ... rest of implementation
}

// UserDaoImpl.java
@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private DataSource dataSource;
    
    // ... rest of implementation
}
```

### Example 2: Convert Spring Security Configuration

**Before Conversion:**
```xml
<!-- spring-security.xml -->
<beans:bean id="userDetailsService" 
    class="com.example.security.CustomUserDetailsService">
    <beans:property name="userDao" ref="userDao"/>
</beans:bean>

<authentication-manager>
    <authentication-provider user-service-ref="userDetailsService">
        <password-encoder ref="passwordEncoder"/>
    </authentication-provider>
</authentication-manager>
```

**After Conversion:**
```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }
}

// CustomUserDetailsService.java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserDao userDao;
    
    // ... implementation
}
```

## ⚙️ Configuration Examples

### YAML Configuration
```yaml
# config.yaml
projectDirectory: /path/to/my-spring-app
backupDirectory: /path/to/backups
createBackups: true
dryRun: false
verbose: true
removeEmptyXmlFiles: true
addTODOsForAmbiguousCases: true
reportFormat: markdown
reportOutputPath: ./conversion_report.md

# Custom mappings
customMappings:
  "myCustomBean": "@MyCustomAnnotation"
  "legacyService": "@Service"

# Include/exclude patterns
includePatterns:
  - ".*service.*"
  - ".*dao.*"
excludePatterns:
  - ".*test.*"
  - ".*config.*"
```

### Properties Configuration
```properties
# config.properties
projectDirectory=/path/to/my-spring-app
backupDirectory=/path/to/backups
createBackups=true
dryRun=false
verbose=true
removeEmptyXmlFiles=true
addTODOsForAmbiguousCases=true
reportFormat=markdown
reportOutputPath=./conversion_report.md
```

## 🔍 Advanced Usage Scenarios

### Scenario 1: Large Enterprise Application
```bash
# Convert only specific modules
java -jar xml-to-annotation-converter.jar \
  -p /path/to/enterprise-app \
  -i ".*user-management.*" \
  -e ".*test.*" \
  -b /path/to/backups \
  -V

# Convert with custom configuration
java -jar xml-to-annotation-converter.jar \
  -p /path/to/enterprise-app \
  -c enterprise-config.yaml \
  -d
```

### Scenario 2: Microservices Migration
```bash
# Convert each microservice separately
for service in user-service order-service payment-service; do
    echo "Converting $service..."
    java -jar xml-to-annotation-converter.jar \
      -p /path/to/$service \
      -b /path/to/backups/$service \
      -r conversion_report_$service.md
done
```

### Scenario 3: Gradual Migration
```bash
# Step 1: Dry run to see what will be converted
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -d \
  -V

# Step 2: Convert only service layer
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -i ".*service.*" \
  -b /path/to/backups

# Step 3: Convert DAO layer
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -i ".*dao.*" \
  -b /path/to/backups
```

## 🛡️ Safety Features

### Automatic Backups
```bash
# Backups are created automatically
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -b /path/to/backups

# Backup files are timestamped
# Original: UserServiceImpl.java
# Backup:  UserServiceImpl.java.2024-01-15-14-30-25.backup
```

### Dry Run Mode
```bash
# Preview changes without modifying files
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -d \
  -V
```

### Rollback
```bash
# If something goes wrong, restore from backup
cp /path/to/backups/UserServiceImpl.java.2024-01-15-14-30-25.backup \
   /path/to/project/src/main/java/com/example/service/UserServiceImpl.java
```

## 📊 Understanding the Output

### Console Output
```
=== Spring XML to Annotation Converter ===
Project Directory: /path/to/my-spring-app
Backup Directory: /path/to/backups
Dry Run: false
Verbose: true

=== Processing Files ===
Found 5 XML files to process
Processing: applicationContext.xml
  - Found 3 beans
  - Converting: userService, userDao, emailService
  - Modified: UserServiceImpl.java, UserDaoImpl.java
Processing: spring-security.xml
  - Found 2 beans
  - Converting: userDetailsService, passwordEncoder

=== Conversion Summary ===
XML Files Processed: 5
Java Files Modified: 8
Beans Converted: 12
Properties Converted: 25
Constructor Args Converted: 3
TODOs Generated: 2
Errors: 0
Duration: 45 seconds
Success: Yes

✅ Conversion completed successfully
```

### Report File (conversion_report.md)
```markdown
# Spring XML to Annotation Conversion Report

## Summary
- **Project**: my-spring-app
- **Date**: 2024-01-15 14:30:25
- **Duration**: 45 seconds
- **Success**: Yes

## Statistics
- XML Files Processed: 5
- Java Files Modified: 8
- Beans Converted: 12
- Properties Converted: 25
- Constructor Args Converted: 3
- TODOs Generated: 2
- Errors: 0

## Conversion Results
### applicationContext.xml
- ✅ userService → @Service annotation added
- ✅ userDao → @Repository annotation added
- ✅ emailService → @Service annotation added

## TODO Items
1. **Manual Review Required**: Complex bean definition in security-config.xml
2. **Ambiguous Mapping**: Multiple UserService implementations found

## File Backups
- UserServiceImpl.java → UserServiceImpl.java.2024-01-15-14-30-25.backup
- UserDaoImpl.java → UserDaoImpl.java.2024-01-15-14-30-25.backup
```

## 🚨 Troubleshooting

### Common Issues

**Issue**: "No Spring XML files found"
```bash
# Solution: Check if XML files are in the right location
find /path/to/project -name "*.xml" | grep -i spring
```

**Issue**: "Class not found" errors
```bash
# Solution: Ensure project compiles before conversion
mvn clean compile
```

**Issue**: "Permission denied" errors
```bash
# Solution: Check file permissions
chmod -R 644 /path/to/project/src/main/java/
```

**Issue**: "Backup failed" errors
```bash
# Solution: Ensure backup directory is writable
mkdir -p /path/to/backups
chmod 755 /path/to/backups
```

### Getting Help
```bash
# Show help
java -jar xml-to-annotation-converter.jar -h

# Show version
java -jar xml-to-annotation-converter.jar -v

# Run with debug output
java -jar xml-to-annotation-converter.jar -p /path/to/project -V
```

## 🎯 Best Practices

### Before Conversion
1. **Backup your project** (the tool does this automatically, but extra safety never hurts)
2. **Ensure your project compiles** and tests pass
3. **Review your XML configuration** to understand the current structure
4. **Run a dry run** to see what will be converted

### During Conversion
1. **Use verbose mode** (`-V`) to see detailed output
2. **Start with a small subset** if you have a large project
3. **Monitor the console output** for any warnings or errors

### After Conversion
1. **Review the conversion report** for any TODO items
2. **Compile and test** your application
3. **Manually review** complex conversions
4. **Update your build configuration** if needed (e.g., remove XML config references)

### For Large Projects
1. **Convert incrementally** by module or layer
2. **Use include/exclude patterns** to focus on specific areas
3. **Run tests after each conversion** to catch issues early
4. **Keep backups** for each conversion step

## 🔄 Migration Strategies

### Strategy 1: Big Bang
```bash
# Convert everything at once
java -jar xml-to-annotation-converter.jar -p /path/to/project -V
```

### Strategy 2: Incremental by Layer
```bash
# Convert service layer first
java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*service.*"

# Then DAO layer
java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*dao.*"

# Finally controllers
java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*controller.*"
```

### Strategy 3: Incremental by Module
```bash
# Convert user management module
java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*user.*"

# Convert order management module
java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*order.*"
```

This comprehensive guide should help you successfully convert any Spring project from XML to annotation-based configuration! 