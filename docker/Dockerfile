FROM eclipse-temurin:17

#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
COPY tomcat-ssl-keystore.jks tomcat-ssl-keystore.jks
COPY *.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]




