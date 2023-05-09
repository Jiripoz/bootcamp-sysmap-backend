FROM openjdk:17-alpine

# Install Maven and other required tools
RUN apk add --no-cache curl tar bash
RUN curl -fsSL https://downloads.apache.org/maven/maven-3/3.9.1/binaries/apache-maven-3.9.1-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-3.9.1 /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# Create a directory for the application
RUN mkdir -p /app

# Set the working directory to the application directory
WORKDIR /app

RUN apk add --no-cache mongodb-tools


# Copy the application source code and mvnw script to the container
COPY . /app
COPY mvnw /app/
COPY mvnw.cmd /app
RUN dos2unix mvnw
RUN chmod +x mvnw
RUN chmod +x /app/mvnw
RUN sed -i 's/\r$//' mvnw
# Build the application using Maven
RUN ./mvnw clean install

# Expose the port used by the application
EXPOSE 8080

# Set the entrypoint command for the container
CMD ["./mvnw", "spring-boot:run"]
