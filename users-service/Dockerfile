FROM openjdk:17
COPY target/*.jar users-service.jar
EXPOSE 8770
ENTRYPOINT ["java","-jar","/users-service.jar"]