#!/bin/bash

JAR_NAME=app.jar

if ! type docker > /dev/null
then
  echo "docker does not exist"
  echo "Start installing docker"
  sudo apt-get update
  sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
  sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"
  sudo apt update
  apt-cache policy docker-ce
  sudo apt install -y docker-ce
  sudo chmod 666 /var/run/docker.sock
fi

CURRENT_PID=$(pgrep -f $JAR_NAME)

echo "Current PID: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동중인 어플리케이션이 없으므로 종료하지 않습니다."
else
    if docker container inspect meshroom-backend-container > /dev/null 2>&1; then
        echo "Stopping and removing existing docker container..."
        docker container stop meshroom-backend-container
        sleep 10
    fi
fi

docker container rm meshroom-backend-container

if docker images | grep -q "bjho606/meshroom-backend-image"; then
  echo "Removing existing docker image..."
  docker rmi bjho606/meshroom-backend-image:latest
fi

echo "Pulling latest docker image..."
docker pull bjho606/meshroom-backend-image:latest

echo "Running new docker container..."
docker run -d -p 8080:8080 --name meshroom-backend-container -v /home/ubuntu/logs:/home/ubuntu/logs bjho606/meshroom-backend-image:latest

echo "Deployment completed successfully."
