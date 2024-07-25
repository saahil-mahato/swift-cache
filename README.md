# Cache System library

## Setting up Docker and testing

1. Run the command: `sudo docker build -t swiftcache .`
2. Activate the cli with: `sudo docker run -it -v "$(pwd)":/app swiftcache /bin/bash`
3. Run `mvn clean install` to install dependencies and run the test.
