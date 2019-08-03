# Jenkins draft script

# initial image
# FROM maven:3.6.1-jdk-8 as maven

# copy the project files
# COPY ./pom.xml ./pom.xml

# offline copy
# RUN mvn dependency:go-offline -B

# COPY ./src ./src

# build for release
# RUN mvn clean package -DskipTests

# final base image
FROM adoptopenjdk/openjdk8:jdk8u212-b04-slim

WORKDIR /build

COPY ./target/redis-idempotency-app-1.0-SNAPSHOT.jar ./app.jar

CMD ["java", "-jar", "./app.jar"]
