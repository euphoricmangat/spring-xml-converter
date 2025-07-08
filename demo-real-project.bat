@echo off
echo ========================================
echo Spring XML to Annotation Converter - Real Project Demo
echo ========================================
echo.

REM Check if JAR exists
if not exist "target\xml-to-annotation-converter.jar" (
    echo ERROR: JAR file not found!
    echo Please run build-simple.bat first to build the project.
    pause
    exit /b 1
)

echo This demo shows how to use the converter on a real Spring project.
echo.

echo ========================================
echo Demo 1: Basic Usage
echo ========================================
echo.

echo Command: java -jar xml-to-annotation-converter.jar -p /path/to/project
echo.
echo This will:
echo - Find all Spring XML files in the project
echo - Convert bean definitions to annotations
echo - Create automatic backups
echo - Generate a detailed report
echo.

echo ========================================
echo Demo 2: Safe Conversion with Dry Run
echo ========================================
echo.

echo Command: java -jar xml-to-annotation-converter.jar -p /path/to/project -d -V
echo.
echo This will:
echo - Preview all changes without modifying files
echo - Show detailed output of what would be converted
echo - Generate a report of planned changes
echo - Allow you to review before proceeding
echo.

echo ========================================
echo Demo 3: Selective Conversion
echo ========================================
echo.

echo Command: java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*service.*" -b /path/to/backups
echo.
echo This will:
echo - Convert only service layer classes
echo - Create backups in specified directory
echo - Leave other layers unchanged
echo.

echo ========================================
echo Demo 4: Enterprise Project with Config
echo ========================================
echo.

echo Step 1: Create configuration file
echo.
echo config.yaml:
echo projectDirectory: /path/to/enterprise-app
echo backupDirectory: /path/to/backups
echo createBackups: true
echo dryRun: false
echo verbose: true
echo includePatterns:
echo   - ".*user-management.*"
echo excludePatterns:
echo   - ".*test.*"
echo   - ".*config.*"
echo.

echo Step 2: Run conversion
echo Command: java -jar xml-to-annotation-converter.jar -c config.yaml
echo.

echo ========================================
echo Demo 5: Step-by-Step Migration
echo ========================================
echo.

echo For large projects, convert incrementally:
echo.
echo Step 1: Service Layer
echo java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*service.*" -b /path/to/backups
echo mvn clean compile test
echo.
echo Step 2: DAO Layer
echo java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*dao.*" -b /path/to/backups
echo mvn clean compile test
echo.
echo Step 3: Controller Layer
echo java -jar xml-to-annotation-converter.jar -p /path/to/project -i ".*controller.*" -b /path/to/backups
echo mvn clean compile test
echo.

echo ========================================
echo Demo 6: Troubleshooting
echo ========================================
echo.

echo If you encounter issues:
echo.
echo 1. Check if XML files exist:
echo    find /path/to/project -name "*.xml" ^| grep -i spring
echo.
echo 2. Ensure project compiles:
echo    cd /path/to/project ^&^& mvn clean compile
echo.
echo 3. Run with verbose output:
echo    java -jar xml-to-annotation-converter.jar -p /path/to/project -V
echo.
echo 4. Check file permissions:
echo    chmod -R 644 /path/to/project/src/main/java/
echo.

echo ========================================
echo Demo 7: Understanding Output
echo ========================================
echo.

echo The tool generates:
echo.
echo 1. Console Output:
echo    - Processing progress
echo    - Statistics (files processed, beans converted)
echo    - Success/failure status
echo.
echo 2. Report File (conversion_report.md):
echo    - Detailed summary
echo    - List of all changes
echo    - TODO items for manual review
echo    - Backup file locations
echo.
echo 3. Backup Files:
echo    - Timestamped backups of modified files
echo    - Original files preserved
echo.

echo ========================================
echo Demo 8: Rollback Process
echo ========================================
echo.

echo If something goes wrong:
echo.
echo 1. Restore from tool's backup:
echo    cp /path/to/backups/UserServiceImpl.java.2024-01-15-14-30-25.backup ^
echo       /path/to/project/src/main/java/com/example/service/UserServiceImpl.java
echo.
echo 2. Or restore entire project:
echo    cp -r my-project-backup/* my-project/
echo.

echo ========================================
echo Demo 9: Best Practices
echo ========================================
echo.

echo Before conversion:
echo 1. Backup your project manually
echo 2. Ensure it compiles: mvn clean compile
echo 3. Run tests: mvn test
echo 4. Do a dry run: -d -V
echo.
echo During conversion:
echo 1. Use verbose mode: -V
echo 2. Create backups: -b /path/to/backups
echo 3. Monitor output for warnings
echo.
echo After conversion:
echo 1. Review the report: cat conversion_report.md
echo 2. Compile and test: mvn clean compile test
echo 3. Test your application
echo 4. Review TODO items in the report
echo.

echo ========================================
echo Demo 10: Real-World Example
echo ========================================
echo.

echo Let's say you have a Spring MVC application:
echo.
echo Before conversion:
echo - applicationContext.xml with 20+ bean definitions
echo - spring-security.xml with security configuration
echo - database-config.xml with data source configuration
echo.
echo After conversion:
echo - All beans converted to @Service, @Repository, @Controller
echo - Properties converted to @Autowired and @Value
echo - Security configuration converted to @Configuration class
echo - Database configuration converted to @Configuration class
echo - XML files commented out or removed
echo.

echo ========================================
echo Demo Complete!
echo ========================================
echo.
echo To try the converter on your project:
echo 1. Run: example-usage.bat
echo 2. Or use the commands shown above
echo 3. Start with a dry run: -d -V
echo 4. Review the results before proceeding
echo.
pause 