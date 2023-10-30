# Files Manager - README

This project is a summary of ALX back-end trimester: authentication, NodeJS, MongoDB, Redis, pagination and background processing.

The objective is to build a simple platform to upload and view files:

- User authentication via a token
- List all files
- Upload a new file
- Change permission of a file
- View a file
- Generate thumbnails for images

## Learning Objectives

- how to create an API with Express
- how to authenticate a user
- how to store data in MongoDB
- how to store temporary data in Redis
- how to setup and use a background worker

## Getting Started

Follow these steps to set up, run and use the API:

### Clone the repository

clone the respository to your local machine using Git:

```bash
git clone https://github.com/lurldgbodex/Portfolio.git
```

### install Dependencies

Navigate to the project directory and install the required Node.js dependencies

```bash
cd Files_manager
npm install
```

### configure the database and server port

create a .env file in the project root directory and define the connection strings with your values e.g

```bash
DB_HOST="localhost"
DB_PORT=27017
DB_DATABASE="files_manager"
PORT=5000
```

### Start the API

Start the API server by running the following command:

```bash
npm run start-server
```

The API should now be running and accessible at the base URL https://localhost:5000/status.

## API EndPoints

The files_manager API provides the following endpoints

### GET Database status

- Endpoint: `/status`
- HTTP Method: GET
- Description: Returns information about whether mongodb and redis are alive
- Response: JSON Object containing database status

### Get database stats

- Endpoint: `/stats`
- HTTP Method: GET
- Description: get the number of users and files in database
- Response: JSON Object of no of users and no of files

### Create a new user

- Endpoint: `/users`
- HTTP Method: POST
- Description: creates a new user record
- Response: JSON Object of the newly created user

### connect to the API

- Endpoint: `/connect`
- HTTP Method: GET
- Description: sign-in user by generating a new authentication token
- Response: JSON Object of the newly created token

### Disconnect

- Endpoint: `/disconnect`
- HTTP Method: GET
- Description: sign-out the user based on token

### Get User

- Endpont: `/users/me`
- HTTP Method: GET
- Description: retrieve the user information based on token
- Response: JSON Object of user details

### Create a new file

- Endpoint: `/files`
- HTTP Method: POST
- Description: create a new file in DB and disk
- Response: JSON Object of new file

### Get file document based on Id

- Endpoint: `/files/:id`
- HTTP Method: GET
- Description: Retrieve file document based on the id
- Response: JSON Object of file

### Get file based on parentId with pagination

- Endpoint :`/files?parentId=${parentid}?page=${page}`
- HTTP Method: GET
- Description: retrieve all users file documents for a specific parentId with pagination
- Response: JSON Object of files documents of a parentId

### Publish a file

- Endpoint: `/files/:id/publish`
- HTTP Method: PUT
- Description: set the isPublic to true on file document
- Response: JSON Object of file

### UnPublish a file

- Endpoint: `/files/:id/unpublish`
- HTTP Method: PUT
- Description: set the isPublic to false on file document
- Response: JSON Object of file

### Get the file data

- Endpoint: `/files/:id/data`
- HTTP Method: GET
- Description: Get the content of the file document based on the id
- Response: content of file

## Testing

- The endpoints was extensively tested to ensure proper functioning.
- after setup, you can run unit test with the command `npm test tests/${testFolder}` from the root directory
