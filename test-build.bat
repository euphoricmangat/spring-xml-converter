@echo off
echo ========================================
echo Testing Spring XML Converter Build
echo ========================================
echo.

REM Check if JAR exists
if not exist "target\xml-to-annotation-converter.jar" (
    echo Building project first...
    call build-simple.bat
    if errorlevel 1 (
        echo Build failed!
        pause
        exit /b 1
    )
)

echo.
echo ========================================
echo Testing application
echo ========================================
echo.

echo Running help command...
call run.bat -h

echo.
echo ========================================
echo Testing with sample project
echo ========================================
echo.

if exist "sample-project" (
    echo Running demo conversion...
    call run.bat -p sample-project -d -V
) else (
    echo Sample project not found, skipping demo.
)

echo.
echo ========================================
echo Test completed!
echo ========================================
echo.
pause 