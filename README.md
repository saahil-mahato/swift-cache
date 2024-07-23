# Cache System library

Requirements:
Java 1.5
Maven 2.2.1

## Note
For testing, we can use Java 1.8 with language level set to "5' in the IDE settings

## Setting up Docker and testing

1. Run the command: `sudo docker build -t swiftcache .`
2. Activate the cli with: `sudo docker run -it -v "$(pwd)":/app swiftcache /bin/bash`
3. Run `mvn clean install` to install dependencies and run the test.