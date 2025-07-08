@echo off
echo ========================================
echo Spring XML to Annotation Converter Demo
echo ========================================
echo.

REM Check if the tool JAR exists
if not exist "target\xml-to-annotation-converter-1.0.0.jar" (
    echo Error: Tool JAR not found. Please build the project first.
    echo Run: build.bat
    exit /b 1
)

echo This demo will convert the sample project from XML to annotation configuration.
echo.
echo Sample project structure:
echo - sample-project/src/main/resources/applicationContext.xml (Spring XML config)
echo - sample-project/src/main/java/com/example/service/UserServiceImpl.java
echo - sample-project/src/main/java/com/example/dao/UserDaoImpl.java
echo - sample-project/src/main/java/com/example/service/EmailServiceImpl.java
echo.

echo Step 1: Running dry-run to preview changes...
echo.
java -jar target\xml-to-annotation-converter-1.0.0.jar -p sample-project -d -V

echo.
echo Step 2: Running actual conversion...
echo.
java -jar target\xml-to-annotation-converter-1.0.0.jar -p sample-project -V

echo.
echo ========================================
echo Demo completed!
echo ========================================
echo.
echo Check the following files for changes:
echo - sample-project/src/main/resources/applicationContext.xml (commented out)
echo - sample-project/src/main/java/com/example/service/UserServiceImpl.java (annotations added)
echo - sample-project/src/main/java/com/example/dao/UserDaoImpl.java (annotations added)
echo - sample-project/src/main/java/com/example/service/EmailServiceImpl.java (annotations added)
echo - sample-project/conversion_report.md (detailed report)
echo.
echo The conversion report contains detailed information about all changes made.
echo. 