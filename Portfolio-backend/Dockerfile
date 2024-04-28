# java base image
FROM eclipse-temurin:17-jre-alpine AS builder

# create directory for the application
RUN mkdir -p /app

# set working directory
WORKDIR /app

# copy application code
COPY --from=builder ./*.jar target/*.jar /app/

# expose the spring boot application port
EXPOSE 8080

# define a second stage for the final image
FROM eclipse-temurin:17-jre-alpine

# copy the application har directly
COPY --from=builder /app/*.jar /app/

# entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app/*.jar"]