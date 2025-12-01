FROM eclipse-temurin:25

LABEL project="Archer's Log Book"
LABEL description="This is a spring-boot based REST API that helps to build a back-end for [Archer's Log Book](https://roundkick.nz/) project"

ARG SERVER_PORT
ARG DB_URL
ARG DB_USER
ARG DB_PWD

ENV SERVER_PORT=${SERVER_PORT:-7878}
ENV SERVER_SSL_KEYSTORE=${SERVER_SSL_KEYSTORE:-classpath:archery-logbook-keystore.jks}
ENV SERVER_SSL_KEYSTORE_PASSWORD=${SERVER_SSL_KEYSTORE_PASSWORD}
ENV SERVER_SSL_KEY_ALIAS=${SERVER_SSL_KEY_ALIAS}
ENV SERVER_SSL_KEY_PASSWORD=${SERVER_SSL_KEY_PASSWORD}
ENV DB_URL=${DB_URL:-jdbc:mariadb://host.docker.internal:3306/archery}
ENV DB_USER=${DB_USER:-robin}
ENV DB_PWD=${DB_PWD:-hood}

COPY *.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]




