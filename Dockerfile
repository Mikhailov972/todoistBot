FROM openjdk:21-jdk-slim
COPY build/libs/todoistBot.jar todoistBot.jar
ENTRYPOINT ["java","-jar","/todoistBot.jar"]