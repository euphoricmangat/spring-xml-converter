@echo off
echo ========================================
echo Testing Project Compilation
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
echo Testing compilation without dependencies
echo ========================================
echo.

REM Try to compile just the main classes to check syntax
echo Testing syntax compilation...
javac -cp . -d target\test-classes -sourcepath src\main\java src\main\java\com\springconverter\parser\XmlParser.java

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed! There are syntax errors.
    echo Please fix the compilation errors before proceeding.
    pause
    exit /b 1
) else (
    echo.
    echo SUCCESS: Basic syntax compilation passed!
    echo The project should compile successfully with dependencies.
)

echo.
echo ========================================
echo Compilation test completed!
echo ========================================
echo.
echo Next steps:
echo 1. Run build-simple.bat to build with dependencies
echo 2. Run test-build.bat to test the complete build
echo 3. Run upload-to-github.bat to upload to GitHub
echo.
pause 