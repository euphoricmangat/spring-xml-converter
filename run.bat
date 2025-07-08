@echo off
REM Run script for Spring XML to Annotation Converter

if not exist "target\xml-to-annotation-converter.jar" (
    echo ERROR: JAR file not found!
    echo Please run build-simple.bat first to build the project.
    pause
    exit /b 1
)

if not exist "lib" (
    echo ERROR: lib directory not found!
    echo Please run build-simple.bat first to download dependencies.
    pause
    exit /b 1
)

echo Running Spring XML to Annotation Converter...
echo.

java -cp "target\xml-to-annotation-converter.jar;lib\*" com.springconverter.Main %*

if errorlevel 1 (
    echo.
    echo Application exited with error code: %errorlevel%
    pause
) 