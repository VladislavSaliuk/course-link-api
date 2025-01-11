FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} course-link-api-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "course-link-api-0.0.1-SNAPSHOT.jar"]