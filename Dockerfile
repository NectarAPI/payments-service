FROM amazoncorretto:17-alpine-jdk
MAINTAINER dev@nectar.software
WORKDIR /etc/payments-service
ARG JAR_FILE=build/libs/payments-service-3.0.0-alpha.jar
COPY ${JAR_FILE} payments-service.jar
ENTRYPOINT ["java","-jar","payments-service.jar"]
