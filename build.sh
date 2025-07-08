#!/bin/bash

echo "Building Spring XML to Annotation Converter..."

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# Create target directory
mkdir -p target/classes

# Create lib directory for dependencies
mkdir -p lib

echo ""
echo "========================================"
echo "NOTE: This build script requires manual download of dependencies"
echo "========================================"
echo ""
echo "Please download the following JAR files to the 'lib' directory:"
echo ""
echo "Required dependencies:"
echo "- javaparser-core-3.25.5.jar"
echo "- slf4j-api-2.0.9.jar"
echo "- logback-classic-1.4.11.jar"
echo "- logback-core-1.4.11.jar"
echo "- snakeyaml-2.0.jar"
echo "- commons-cli-1.5.0.jar"
echo ""
echo "You can download these from Maven Central:"
echo "https://repo1.maven.org/maven2/"
echo ""
echo "After downloading the dependencies, run this script again."
echo ""

# Check if dependencies exist
if [ ! -f "lib/javaparser-core-3.25.5.jar" ]; then
    echo "Error: Dependencies not found. Please download them first."
    exit 1
fi

echo "Compiling source files..."

# Build classpath
CLASSPATH="lib/*"

# Compile Java files
javac -cp "$CLASSPATH" -d target/classes \
    src/main/java/com/springconverter/*.java \
    src/main/java/com/springconverter/model/*.java \
    src/main/java/com/springconverter/config/*.java \
    src/main/java/com/springconverter/parser/*.java \
    src/main/java/com/springconverter/java/*.java \
    src/main/java/com/springconverter/file/*.java \
    src/main/java/com/springconverter/engine/*.java \
    src/main/java/com/springconverter/report/*.java

if [ $? -ne 0 ]; then
    echo "Error: Compilation failed"
    exit 1
fi

echo "Copying resources..."
cp -r src/main/resources/* target/classes/ 2>/dev/null || true

echo "Creating JAR file..."

# Create manifest file
cat > target/manifest.txt << EOF
Main-Class: com.springconverter.Main
Class-Path: .
EOF

# Add classpath entries for dependencies
for jar in lib/*.jar; do
    echo "Class-Path: lib/$(basename "$jar")" >> target/manifest.txt
done

# Create JAR
jar cfm target/xml-to-annotation-converter-1.0.0.jar target/manifest.txt -C target/classes .

echo ""
echo "========================================"
echo "Build completed successfully!"
echo "========================================"
echo ""
echo "Executable JAR: target/xml-to-annotation-converter-1.0.0.jar"
echo ""
echo "Usage:"
echo "java -jar target/xml-to-annotation-converter-1.0.0.jar -p /path/to/project"
echo ""
echo "For help:"
echo "java -jar target/xml-to-annotation-converter-1.0.0.jar -h"
echo "" 