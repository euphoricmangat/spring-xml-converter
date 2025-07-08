@echo off
echo ========================================
echo Simple Build Script for Spring XML Converter
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 11 or newer from: https://adoptium.net/
    pause
    exit /b 1
)

echo Java is available. Checking version...
java -version

echo.
echo ========================================
echo Creating directories
echo ========================================
echo.

if not exist "lib" mkdir lib
if not exist "target" mkdir target
if not exist "target\classes" mkdir target\classes

echo.
echo ========================================
echo Downloading dependencies
echo ========================================
echo.

REM Download JavaParser
echo Downloading JavaParser...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/github/javaparser/javaparser-core/3.25.8/javaparser-core-3.25.8.jar' -OutFile 'lib/javaparser-core-3.25.8.jar'}"

REM Download SLF4J
echo Downloading SLF4J...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar' -OutFile 'lib/slf4j-api-2.0.9.jar'}"

REM Download Logback
echo Downloading Logback...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.4.11/logback-classic-1.4.11.jar' -OutFile 'lib/logback-classic-1.4.11.jar'}"
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.4.11/logback-core-1.4.11.jar' -OutFile 'lib/logback-core-1.4.11.jar'}"

REM Download SnakeYAML
echo Downloading SnakeYAML...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/yaml/snakeyaml/2.0/snakeyaml-2.0.jar' -OutFile 'lib/snakeyaml-2.0.jar'}"

REM Download Apache Commons CLI
echo Downloading Apache Commons CLI...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/commons-cli/commons-cli/1.5.0/commons-cli-1.5.0.jar' -OutFile 'lib/commons-cli-1.5.0.jar'}"

echo.
echo ========================================
echo Compiling source code
echo ========================================
echo.

REM Create classpath
set CLASSPATH=lib\javaparser-core-3.25.8.jar;lib\slf4j-api-2.0.9.jar;lib\logback-classic-1.4.11.jar;lib\logback-core-1.4.11.jar;lib\snakeyaml-2.0.jar;lib\commons-cli-1.5.0.jar

REM Compile all Java files
echo Compiling Java source files...
javac -cp "%CLASSPATH%" -d target\classes -sourcepath src\main\java src\main\java\com\springconverter\*.java src\main\java\com\springconverter\model\*.java src\main\java\com\springconverter\config\*.java src\main\java\com\springconverter\parser\*.java src\main\java\com\springconverter\java\*.java src\main\java\com\springconverter\file\*.java src\main\java\com\springconverter\report\*.java src\main\java\com\springconverter\engine\*.java

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    echo Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Copying resources
echo ========================================
echo.

REM Copy resources
if exist "src\main\resources" (
    xcopy /E /I /Y src\main\resources target\classes
)

echo.
echo ========================================
echo Creating JAR file
echo ========================================
echo.

REM Create manifest file
echo Main-Class: com.springconverter.Main > manifest.txt
echo Class-Path: javaparser-core-3.25.8.jar slf4j-api-2.0.9.jar logback-classic-1.4.11.jar logback-core-1.4.11.jar snakeyaml-2.0.jar commons-cli-1.5.0.jar >> manifest.txt

REM Create JAR file
jar cfm target\xml-to-annotation-converter.jar manifest.txt -C target\classes .

if errorlevel 1 (
    echo.
    echo ERROR: JAR creation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build completed successfully!
echo ========================================
echo.
echo JAR file created: target\xml-to-annotation-converter.jar
echo.
echo To run the application:
echo java -cp "target\xml-to-annotation-converter.jar;lib\*" com.springconverter.Main -h
echo.
echo Or create a run script:
echo echo java -cp "target\xml-to-annotation-converter.jar;lib\*" com.springconverter.Main %%* > run.bat
echo.
pause 