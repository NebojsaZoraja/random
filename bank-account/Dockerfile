FROM openjdk:17
EXPOSE 8200
ADD target/*.jar bank-account.jar
ENTRYPOINT ["java","-jar","/bank-account.jar"]