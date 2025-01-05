FROM maven:3.9.9-amazoncorretto-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM jetty

WORKDIR /var/lib/jetty
#RUN java -jar /usr/local/jetty/start.jar --add-to-start=pid,state
COPY --from=builder /app/target/jetty-producer-app.war /var/lib/jetty/webapps/root.war

RUN java -jar "$JETTY_HOME/start.jar" --add-modules=ee10-webapp,ee10-deploy,ee10-jsp,ee10-jstl,ee10-websocket-jetty,ee10-websocket-jakarta

EXPOSE 8080
