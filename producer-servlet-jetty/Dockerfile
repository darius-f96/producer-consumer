FROM maven:3.9.9-amazoncorretto-21-alpine as builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM jetty:9.4.56-jdk21-amazoncorretto

WORKDIR /var/lib/jetty
#RUN java -jar /usr/local/jetty/start.jar --add-to-start=pid,state
COPY --from=builder /app/target/producer-app.war /var/lib/jetty/webapps/ROOT.war
EXPOSE 8084
