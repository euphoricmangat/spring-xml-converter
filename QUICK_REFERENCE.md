# Spring XML to Annotation Converter - Quick Reference

## 🚀 Basic Commands

```bash
# Show help
java -jar xml-to-annotation-converter.jar -h

# Basic conversion
java -jar xml-to-annotation-converter.jar -p /path/to/project

# Dry run (preview only)
java -jar xml-to-annotation-converter.jar -p /path/to/project -d

# Verbose output
java -jar xml-to-annotation-converter.jar -p /path/to/project -V

# With backups
java -jar xml-to-annotation-converter.jar -p /path/to/project -b /path/to/backups
```

## 📋 Common Usage Patterns

### 1. Convert Entire Project
```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/spring-project \
  -b /path/to/backups \
  -V
```

### 2. Preview Changes First
```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/spring-project \
  -d \
  -V
```

### 3. Convert Only Service Layer
```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/spring-project \
  -i ".*service.*" \
  -b /path/to/backups
```

### 4. Convert Excluding Tests
```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/spring-project \
  -e ".*test.*" \
  -b /path/to/backups
```

### 5. Use Custom Configuration
```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/spring-project \
  -c config.yaml \
  -V
```

## 🔧 Configuration Files

### YAML Config (config.yaml)
```yaml
projectDirectory: /path/to/project
backupDirectory: /path/to/backups
createBackups: true
dryRun: false
verbose: true
removeEmptyXmlFiles: true
addTODOsForAmbiguousCases: true
reportFormat: markdown
reportOutputPath: ./conversion_report.md
```

### Properties Config (config.properties)
```properties
projectDirectory=/path/to/project
backupDirectory=/path/to/backups
createBackups=true
dryRun=false
verbose=true
removeEmptyXmlFiles=true
addTODOsForAmbiguousCases=true
reportFormat=markdown
reportOutputPath=./conversion_report.md
```

## 📊 What Gets Converted

| XML Element | Java Annotation | Example |
|-------------|----------------|---------|
| `<bean class="...Service">` | `@Service` | `@Service` |
| `<bean class="...Repository">` | `@Repository` | `@Repository` |
| `<bean class="...Controller">` | `@Controller` | `@Controller` |
| `<property ref="...">` | `@Autowired` | `@Autowired private UserDao userDao;` |
| `<property value="...">` | `@Value` | `@Value("1000") private int maxUsers;` |
| `<constructor-arg ref="...">` | Constructor `@Autowired` | Constructor injection |
| `<context:component-scan>` | `@ComponentScan` | `@ComponentScan("com.example")` |
| `<import resource="...">` | `@Import` | `@Import(OtherConfig.class)` |

## 🛡️ Safety Commands

### Create Manual Backup
```bash
cp -r my-project my-project-backup
```

### Restore from Backup
```bash
# If using tool's backup
cp /path/to/backups/UserServiceImpl.java.2024-01-15-14-30-25.backup \
   /path/to/project/src/main/java/com/example/service/UserServiceImpl.java

# If using manual backup
cp -r my-project-backup/* my-project/
```

### Skip Backups (Not Recommended)
```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -n
```

## 🔍 Troubleshooting Commands

### Check if XML files exist
```bash
find /path/to/project -name "*.xml" | grep -i spring
```

### Check Java compilation
```bash
cd /path/to/project
mvn clean compile
```

### Check file permissions
```bash
ls -la /path/to/project/src/main/java/
chmod -R 644 /path/to/project/src/main/java/
```

### Run with debug output
```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -V
```

## 📈 Migration Strategies

### Big Bang (Small Projects)
```bash
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -V
```

### Incremental by Layer
```bash
# Service layer first
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -i ".*service.*" \
  -b /path/to/backups

# Then DAO layer
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -i ".*dao.*" \
  -b /path/to/backups

# Finally controllers
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -i ".*controller.*" \
  -b /path/to/backups
```

### Incremental by Module
```bash
# User module
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -i ".*user.*" \
  -b /path/to/backups

# Order module
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -i ".*order.*" \
  -b /path/to/backups
```

## 🎯 Best Practices

### Before Conversion
1. **Backup your project**
2. **Ensure it compiles**: `mvn clean compile`
3. **Run tests**: `mvn test`
4. **Do a dry run**: `-d -V`

### During Conversion
1. **Use verbose mode**: `-V`
2. **Create backups**: `-b /path/to/backups`
3. **Monitor output** for warnings

### After Conversion
1. **Review the report**: `cat conversion_report.md`
2. **Compile and test**: `mvn clean compile test`
3. **Test your application**
4. **Review TODO items** in the report

## 📞 Getting Help

```bash
# Show help
java -jar xml-to-annotation-converter.jar -h

# Show version
java -jar xml-to-annotation-converter.jar -v

# Run with maximum verbosity
java -jar xml-to-annotation-converter.jar \
  -p /path/to/project \
  -V \
  -d
```

## 🔄 Common Workflows

### Workflow 1: Safe Conversion
```bash
# 1. Dry run
java -jar xml-to-annotation-converter.jar -p /path/to/project -d -V

# 2. Review report
cat conversion_report.md

# 3. Actual conversion
java -jar xml-to-annotation-converter.jar -p /path/to/project -b /path/to/backups -V

# 4. Test
cd /path/to/project && mvn clean compile test
```

### Workflow 2: Large Project
```bash
# 1. Convert service layer
java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*service.*" -b /path/to/backups

# 2. Test
cd /path/to/project && mvn clean compile test

# 3. Convert DAO layer
java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*dao.*" -b /path/to/backups

# 4. Test again
cd /path/to/project && mvn clean compile test
```

### Workflow 3: Enterprise Project
```bash
# 1. Create configuration
cat > enterprise-config.yaml << EOF
projectDirectory: /path/to/enterprise-app
backupDirectory: /path/to/backups
createBackups: true
dryRun: false
verbose: true
includePatterns:
  - ".*user-management.*"
excludePatterns:
  - ".*test.*"
  - ".*config.*"
EOF

# 2. Run conversion
java -jar xml-to-annotation-converter.jar -c enterprise-config.yaml

# 3. Review and test
cat conversion_report.md
cd /path/to/enterprise-app && mvn clean compile test
``` 