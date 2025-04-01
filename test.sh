#!/bin/bash

# Define the output file
output_file="project_content.txt"

# Remove the existing output file if it exists
rm -f $output_file

# Add project structure to the output file
echo "Project Structure:" >> $output_file
tree src/main/java src/test/java src/main/resources src/test/resources . >> $output_file

# Add a separator
echo -e "\n\nCode Files and Resources:\n" >> $output_file

# Add the content of src/main/java Java files
echo -e "\n\nJava Files from src/main/java:\n" >> $output_file
find src/main/java -name "*.java" | while read file; do
    echo -e "\n// File: $file\n" >> $output_file
    cat "$file" >> $output_file
done

# Add the content of src/test/java Java files
echo -e "\n\nJava Files from src/test/java:\n" >> $output_file
find src/test/java -name "*.java" | while read file; do
    echo -e "\n// File: $file\n" >> $output_file
    cat "$file" >> $output_file
done

# Add the content of src/main/resources files, excluding .p12 files and certs folder
echo -e "\n\nResource Files from src/main/resources:\n" >> $output_file
find src/main/resources -type f ! -name "*.p12" ! -path "*/certs/*" | while read file; do
    echo -e "\n// File: $file\n" >> $output_file
    cat "$file" >> $output_file
done

# Add the content of src/test/resources files, excluding .p12 files and certs folder
echo -e "\n\nResource Files from src/test/resources:\n" >> $output_file
find src/test/resources -type f ! -name "*.p12" ! -path "*/certs/*" | while read file; do
    echo -e "\n// File: $file\n" >> $output_file
    cat "$file" >> $output_file
done

# Add the content of pom.xml
echo -e "\n\nPOM File (pom.xml):\n" >> $output_file
if [ -f "pom.xml" ]; then
    echo -e "\n// File: pom.xml\n" >> $output_file
    cat "pom.xml" >> $output_file
else
    echo "POM file not found!" >> $output_file
fi