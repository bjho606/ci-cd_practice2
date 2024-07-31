FROM node:20-alpine

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

RUN npm install -g http-server

EXPOSE 80

# http-server를 사용하여 빌드된 애플리케이션 실행
CMD [ "http-server", "dist", "-p", "80" ]