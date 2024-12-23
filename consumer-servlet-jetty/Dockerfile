FROM maven:3.9.9-amazoncorretto-21-alpine as builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM jetty

WORKDIR /var/lib/jetty
COPY ./target/consumer-app.war /var/lib/jetty/webapps/root.war

RUN java -jar "$JETTY_HOME/start.jar" --add-modules=ee10-webapp,ee10-deploy,ee10-jsp,ee10-jstl,ee10-websocket-jetty,ee10-websocket-jakarta

EXPOSE 8080