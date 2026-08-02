# Stage 1: Build the Angular application
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build --configuration=production

# Stage 2: Serve the application with Nginx
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the build output
COPY --from=build /app/dist/pharmacy-project /tmp/app-dist
RUN rm -rf /usr/share/nginx/html/* && \
    if [ -d "/tmp/app-dist/browser" ]; then \
        cp -r /tmp/app-dist/browser/* /usr/share/nginx/html/; \
    else \
        cp -r /tmp/app-dist/* /usr/share/nginx/html/; \
    fi && \
    rm -rf /tmp/app-dist

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
