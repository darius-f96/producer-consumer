FROM maven:3.9.9-amazoncorretto-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM tomcat:jdk21-openjdk
COPY --from=builder /app/target/tomcat-consumer-app.war /usr/local/tomcat/webapps/
EXPOSE 8080
