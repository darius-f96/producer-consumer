FROM maven:3.9.9-amazoncorretto-21-alpine as builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM jetty:jdk21
COPY ./target/consumer-app.war /var/lib/jetty/webapps/consumer-app.war
EXPOSE 8083
