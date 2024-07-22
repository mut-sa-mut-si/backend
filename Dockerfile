FROM openjdk:17-jdk

WORKDIR /app

COPY ./build/libs/grwm-server-0.0.1.jar /app/grwm-server-0.0.1.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/grwm-server-0.0.1.jar"]
