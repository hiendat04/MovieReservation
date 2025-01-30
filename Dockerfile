FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} movie-reservation.jar

ENTRYPOINT ["java", "-jar", "movie-reservation.jar"]

EXPOSE 8080