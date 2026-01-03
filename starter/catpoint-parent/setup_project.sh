#!/bin/bash

# Move Image Service files
cp src/main/java/com/udacity/catpoint/service/FakeImageService.java Image-Service/src/main/java/com/udacity/catpoint/image/
cp src/main/java/com/udacity/catpoint/service/AwsImageService.java Image-Service/src/main/java/com/udacity/catpoint/image/

# Move Security Service files  
mkdir -p Security-Service/src/main/java/com/udacity/catpoint/security/application
mkdir -p Security-Service/src/main/java/com/udacity/catpoint/security/data
mkdir -p Security-Service/src/main/java/com/udacity/catpoint/security/service

cp -r src/main/java/com/udacity/catpoint/application/* Security-Service/src/main/java/com/udacity/catpoint/security/application/
cp -r src/main/java/com/udacity/catpoint/data/* Security-Service/src/main/java/com/udacity/catpoint/security/data/
cp src/main/java/com/udacity/catpoint/service/SecurityService.java Security-Service/src/main/java/com/udacity/catpoint/security/service/
cp src/main/java/com/udacity/catpoint/service/StyleService.java Security-Service/src/main/java/com/udacity/catpoint/security/service/

# Copy resources
mkdir -p Security-Service/src/main/resources
cp -r src/main/resources/* Security-Service/src/main/resources/ 2>/dev/null || true

echo "Files moved successfully!"
