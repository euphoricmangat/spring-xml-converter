@echo off
echo Building Spring XML to Annotation Converter...

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    exit /b 1
)

REM Create target directory
if not exist "target" mkdir target
if not exist "target\classes" mkdir target\classes

REM Create lib directory for dependencies
if not exist "lib" mkdir lib

echo.
echo ========================================
echo NOTE: This build script requires manual download of dependencies
echo ========================================
echo.
echo Please download the following JAR files to the 'lib' directory:
echo.
echo Required dependencies:
echo - javaparser-core-3.25.5.jar
echo - slf4j-api-2.0.9.jar
echo - logback-classic-1.4.11.jar
echo - logback-core-1.4.11.jar
echo - snakeyaml-2.0.jar
echo - commons-cli-1.5.0.jar
echo.
echo You can download these from Maven Central:
echo https://repo1.maven.org/maven2/
echo.
echo After downloading the dependencies, run this script again.
echo.

REM Check if dependencies exist
if not exist "lib\javaparser-core-3.25.5.jar" (
    echo Error: Dependencies not found. Please download them first.
    exit /b 1
)

echo Compiling source files...

REM Compile Java files
javac -cp "lib\*" -d target\classes src\main\java\com\springconverter\*.java src\main\java\com\springconverter\model\*.java src\main\java\com\springconverter\config\*.java src\main\java\com\springconverter\parser\*.java src\main\java\com\springconverter\java\*.java src\main\java\com\springconverter\file\*.java src\main\java\com\springconverter\engine\*.java src\main\java\com\springconverter\report\*.java

if errorlevel 1 (
    echo Error: Compilation failed
    exit /b 1
)

echo Copying resources...
copy src\main\resources\*.* target\classes\ >nul 2>&1

echo Creating JAR file...

REM Create manifest file
echo Main-Class: com.springconverter.Main > target\manifest.txt
echo Class-Path: . >> target\manifest.txt
for %%f in (lib\*.jar) do echo Class-Path: lib\%%f >> target\manifest.txt

REM Create JAR
jar cfm target\xml-to-annotation-converter-1.0.0.jar target\manifest.txt -C target\classes .

echo.
echo ========================================
echo Build completed successfully!
echo ========================================
echo.
echo Executable JAR: target\xml-to-annotation-converter-1.0.0.jar
echo.
echo Usage:
echo java -jar target\xml-to-annotation-converter-1.0.0.jar -p /path/to/project
echo.
echo For help:
echo java -jar target\xml-to-annotation-converter-1.0.0.jar -h
echo. 