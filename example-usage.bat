@echo off
echo ========================================
echo Spring XML to Annotation Converter - Example Usage
echo ========================================
echo.

REM Check if JAR exists
if not exist "target\xml-to-annotation-converter.jar" (
    echo ERROR: JAR file not found!
    echo Please run build-simple.bat first to build the project.
    pause
    exit /b 1
)

echo This script demonstrates how to use the Spring XML to Annotation Converter
echo on a real Spring project.
echo.

echo ========================================
echo Step 1: Prepare Your Spring Project
echo ========================================
echo.

echo Before running the converter, ensure your Spring project:
echo 1. Compiles successfully
echo 2. Has XML configuration files
echo 3. Has corresponding Java classes
echo.

set /p project_path="Enter the path to your Spring project (e.g., C:\projects\my-spring-app): "

if not exist "%project_path%" (
    echo ERROR: Project directory does not exist: %project_path%
    pause
    exit /b 1
)

echo.
echo ========================================
echo Step 2: Create Backup Directory
echo ========================================
echo.

set backup_dir=%project_path%\backup-%date:~-4,4%-%date:~-10,2%-%date:~-7,2%-%time:~0,2%-%time:~3,2%-%time:~6,2%
set backup_dir=%backup_dir: =0%

echo Creating backup directory: %backup_dir%
if not exist "%backup_dir%" mkdir "%backup_dir%"

echo.
echo ========================================
echo Step 3: Run Dry Run First
echo ========================================
echo.

echo Running dry run to preview changes...
echo.

call run.bat -p "%project_path%" -b "%backup_dir%" -d -V

echo.
echo ========================================
echo Step 4: Review Dry Run Results
echo ========================================
echo.

if exist "conversion_report.md" (
    echo Dry run report generated: conversion_report.md
    echo.
    echo Press any key to view the report...
    pause >nul
    type conversion_report.md
) else (
    echo No conversion report generated. This might mean:
    echo - No Spring XML files were found
    echo - No beans were detected
    echo - The project structure is not supported
)

echo.
echo ========================================
echo Step 5: Run Actual Conversion
echo ========================================
echo.

set /p proceed="Do you want to proceed with the actual conversion? (y/n): "
if /i "%proceed%"=="y" (
    echo.
    echo Running actual conversion...
    echo.
    
    call run.bat -p "%project_path%" -b "%backup_dir%" -V
    
    echo.
    echo ========================================
    echo Step 6: Review Results
    echo ========================================
    echo.
    
    if exist "conversion_report.md" (
        echo Conversion completed! Review the report:
        echo conversion_report.md
        echo.
        echo Backup files created in: %backup_dir%
        echo.
        echo Next steps:
        echo 1. Review the conversion report
        echo 2. Compile your project: mvn clean compile
        echo 3. Run tests: mvn test
        echo 4. Test your application
        echo.
        echo If something goes wrong, restore from backup:
        echo copy "%backup_dir%\*.backup" "%project_path%\src\main\java\"
    ) else (
        echo No conversion report generated.
    )
) else (
    echo Conversion cancelled.
)

echo.
echo ========================================
echo Example Usage Complete
echo ========================================
echo.
pause 