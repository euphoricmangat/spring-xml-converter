@echo off
echo ========================================
echo GitHub Upload Script for Spring XML Converter
echo ========================================
echo.

REM Check if Git is installed
git --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Git is not installed or not in PATH
    echo.
    echo Please install Git from: https://git-scm.com/download/win
    echo After installation, restart your terminal and run this script again.
    echo.
    pause
    exit /b 1
)

echo Git is installed. Proceeding with GitHub upload...
echo.

REM Check if repository is already initialized
if exist ".git" (
    echo Git repository already exists.
    echo.
    echo Choose an option:
    echo 1. Add all changes and commit
    echo 2. Create new repository (will remove existing .git)
    echo 3. Exit
    echo.
    set /p choice="Enter your choice (1-3): "
    
    if "%choice%"=="1" goto :add_and_commit
    if "%choice%"=="2" (
        echo Removing existing .git directory...
        rmdir /s /q .git
        goto :init_repo
    )
    if "%choice%"=="3" exit /b 0
    echo Invalid choice. Exiting.
    exit /b 1
)

:init_repo
echo Initializing Git repository...
git init

echo.
echo ========================================
echo Repository Setup
echo ========================================
echo.
echo Please provide the following information:
echo.

set /p repo_name="Repository name (e.g., spring-xml-converter): "
set /p repo_description="Repository description: "
set /p github_username="Your GitHub username: "

echo.
echo ========================================
echo Adding files to Git
echo ========================================
echo.

echo Adding all files...
git add .

echo.
echo ========================================
echo Creating initial commit
echo ========================================
echo.

git commit -m "Initial commit: Spring XML to Annotation Converter

A comprehensive Java tool that automatically converts Spring XML-based configuration to annotation-based configuration.

Features:
- Automated conversion of XML bean definitions to annotations
- Safe file operations with automatic backups
- Comprehensive reporting and TODO tracking
- Support for complex Spring configurations
- Production-ready with extensive error handling

This tool helps developers migrate legacy Spring applications from XML configuration to modern annotation-based configuration while preserving all functionality."

echo.
echo ========================================
echo Setting up remote repository
echo ========================================
echo.

echo Please create a new repository on GitHub:
echo 1. Go to https://github.com/new
echo 2. Repository name: %repo_name%
echo 3. Description: %repo_description%
echo 4. Make it Public or Private (your choice)
echo 5. DO NOT initialize with README, .gitignore, or license (we already have these)
echo 6. Click 'Create repository'
echo.
echo After creating the repository, GitHub will show you commands.
echo Copy the HTTPS URL of your new repository.
echo.

set /p remote_url="Enter the GitHub repository URL (e.g., https://github.com/username/repo-name.git): "

echo.
echo Adding remote repository...
git remote add origin %remote_url%

echo.
echo ========================================
echo Pushing to GitHub
echo ========================================
echo.

echo Pushing to GitHub...
git branch -M main
git push -u origin main

echo.
echo ========================================
echo Success!
echo ========================================
echo.
echo Your code has been successfully uploaded to GitHub!
echo Repository URL: %remote_url%
echo.
echo Next steps:
echo 1. Visit your repository on GitHub
echo 2. Add a detailed description
echo 3. Set up GitHub Pages if desired
echo 4. Create releases for version tags
echo.
pause

goto :eof

:add_and_commit
echo.
echo ========================================
echo Adding and committing changes
echo ========================================
echo.

echo Adding all changes...
git add .

set /p commit_message="Enter commit message (or press Enter for default): "
if "%commit_message%"=="" set commit_message="Update Spring XML to Annotation Converter"

git commit -m "%commit_message%"

echo.
echo ========================================
echo Pushing changes
echo ========================================
echo.

echo Pushing to GitHub...
git push

echo.
echo ========================================
echo Success!
echo ========================================
echo.
echo Your changes have been successfully pushed to GitHub!
echo.
pause 