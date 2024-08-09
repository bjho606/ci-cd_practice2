# Stage 1: Build the Vue.js app
FROM node:20 AS build-stage
WORKDIR /app
COPY /home/ubuntu/meshroom/S11P12A401/Frontend/lolmushroom/.env /app/.env
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve the app with Nginx
FROM nginx:alpine

# Copy the built app from the build stage
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Copy SSL certificates
COPY /home/ubuntu/meshroom/S11P12A401/Frontend/lolmushroom/ssl /etc/letsencrypt

# Copy Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
EXPOSE 443

CMD ["nginx", "-g", "daemon off;"]
