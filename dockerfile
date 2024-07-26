FROM ubuntu:22.04

# Install necessary tools
RUN apt-get update && apt-get install -y wget unzip ca-certificates

# Install OpenJDK 8
RUN apt-get update && apt-get install -y openjdk-8-jdk maven

# Set working directory
WORKDIR /app

# Copy your project files
COPY . .

# Default command
CMD ["/bin/bash"]
