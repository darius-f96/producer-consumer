FROM maven:3.9.9-amazoncorretto-21-alpine as builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM tomcat:jdk21-openjdk
COPY --from=builder /app/target/producer-app.war /usr/local/tomcat/webapps/
EXPOSE 8080
