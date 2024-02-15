FROM eclipse-temurin:17

LABEL project="Archer's Log Book"
LABEL description="This is a spring-boot based REST API that helps to build a back-end for [Archer's Log Book](https://roundkick.nz/) project"

ARG SERVER_PORT
ARG DB_URL
ARG DB_USER
ARG DB_PWD

ENV SERVER_PORT=${SERVER_PORT:-7878}
ENV DB_URL=${DB_URL:-jdbc:mysql://host.docker.internal:3306/archery}
ENV DB_USER=${DB_USER:-robin}
ENV DB_PWD=${DB_PWD:-hood}

COPY tomcat-ssl-keystore.jks tomcat-ssl-keystore.jks
COPY *.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]




