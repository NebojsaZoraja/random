FROM openjdk:17
EXPOSE 8765
ADD target/*.jar api-gateway.jar
ENTRYPOINT ["java","-jar","/api-gateway.jar"]
