# Travel Booking App
## Overview
This RESTful API is built using Javascript NestJs, and it serves as the backend for managing and travel-booking-app.

The application uses nestJs Guard for securing endpoints and providing authorization. Inorder to access an endpoint, a user must have the necessary authorization.

The api uses a microservice architecture where each service runs independently and communicates with other services using either Request - Response or Event pattern depending on communication needs. The application provides various features for user management, airline and flight managment.

## Table of contents
1. [Getting Started](#getting-started)
2. [Features](#features)
3. [Folder Structure](#folder-structure)

## Getting Started
1. Clone the repository:
    ```bash
   $ git clone <repository_url>
   $ cd travel-booking-app-backend
    ```
2. Build and run the application using docker
    ```bash
   $ docker compose up
    ```
3. Access the API at [http:localhost:3000](http://localhost:8080)

## Features
- **User Service**: Users can create account, authenticate and get their account details. Users are created and assign roles which determines the resources and action they can perform on the application.
- **Airline Service**: Airline service allows the creation of airline and its management, including routes and flights for the airline depending on user authorization.
- **Flight Service**: Flight service allows users to schedule and book a flight depending on user authorization.
- **Api-Gateway**: Central entry point to the application and routes request to different services.
- **Containerization**: The application is contained using docker. The compose.yml file at the root of the application is used to build each service and start it in a container. You can start the application by using the command `docker compose up`

## Folder Structure
The project uses a microservice architecture, each feature is organized into services
- **User Service**: contain code for user service.
- **Airline Service**: contain code for airline service
- **Flight Service**: contain code for flight-service.
- **Api-Gateway**: contains code for api-gateway service