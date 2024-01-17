FROM adoptopenjdk/openjdk13:alpine-jre
MAINTAINER reagan@nectar.software
WORKDIR /etc/payments-service
ARG JAR_FILE=build/libs/payments-service-1.11.0-alpha.jar
COPY ${JAR_FILE} payments-service.jar
ENTRYPOINT ["java","-jar","payments-service.jar"]
