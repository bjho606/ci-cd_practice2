FROM openjdk:17-jdk-slim

RUN    apt-get -y update

# run application
WORKDIR /home/ubuntu
COPY /build/libs/Backend-0.0.1-SNAPSHOT.jar app.jar
COPY /.env .env

# set timezon
ENV TZ=Asia/Seoul

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]