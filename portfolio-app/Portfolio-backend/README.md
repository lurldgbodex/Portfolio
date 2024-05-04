# Portfolio Backend API

Welcome to the Portfolio Backend API! This RESTful API is built using Java Spring Boot, and it serves as the backend for managing and retrieving portfolio-related data. The application follows clean architecture principles, allowing for modular development and easy feature integration.

The application uses spring security for securing endpoints and providing authorization. An Admin role is needed to create, update or delete but no authentication is need to read.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Getting Started](#getting-started)
3. [Features](#features)
4. [Folder Structure](#folder-structure)
5. [Docker](#docker)

## Prerequisites
- [Java](https://www.java.com/en/download/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MySQL](https://www.mysql.com/)
- [Docker](https://www.docker.com/)

## Getting Started
1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd Portfolio-backend
   ```

2. Build and run the application:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

3. Access the API at [http://localhost:8080](http://localhost:8080)

## Features
The Portfolio Backend API provides endpoints to manage various aspects of a user's portfolio, including:
- Skills
- Projects
- Certifications
- Education
- Experience
- Languages
- About
- Users

## Folder Structure
The project follows a clean architecture, organized into the following packages:
- **about**: Manages user profile details.
- **certification**: Handles certifications and associated details.
- **education**: Manages educational background information.
- **experience**: Manages work experience details.
- **language**: Deals with language proficiency levels.
- **project**: Handles project details.
- **skill**: Manages skills and associated details.
- **user**: Manages user information.

## Docker
The application and MySQL database are containerized using Docker. You can find the Docker configuration in the `docker-compose.yml` file. To run the application with Docker:
```bash
docker-compose up
```