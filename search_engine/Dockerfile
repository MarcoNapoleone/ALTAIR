FROM eclipse-temurin:21-jre-alpine

LABEL author="Giovanni Pio Grieco"

ADD src/main/resources/application.properties application.properties
ADD target/altair-0.0.1.jar altair-0.0.1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "altair-0.0.1.jar"]