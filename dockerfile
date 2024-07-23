FROM ubuntu:18.04

# Install necessary tools
RUN apt-get update && apt-get install -y wget unzip ca-certificates

# Install OpenJDK 8
RUN apt-get update && apt-get install -y openjdk-8-jdk

# Set up environment variables for Java
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Install Maven 3.9.4 (latest compatible with Java 8)
RUN wget https://www.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.zip -O /tmp/apache-maven-3.9.4-bin.zip && \
    unzip /tmp/apache-maven-3.9.4-bin.zip -d /usr/local && \
    rm /tmp/apache-maven-3.9.4-bin.zip

# Set environment variables for Maven
ENV MAVEN_HOME=/usr/local/apache-maven-3.9.4
ENV PATH=$MAVEN_HOME/bin:$PATH

# Set working directory
WORKDIR /app

# Copy your project files
COPY . .

# Default command
CMD ["/bin/bash"]
