FROM maven:3.9.9-amazoncorretto-21-alpine as builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM quay.io/wildfly/wildfly

COPY --from=builder /app/target/dispatcher-app.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080
