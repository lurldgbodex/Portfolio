Task Manager API

The Task Manager API is a RESTful web service that provides endpoints for managing tasks and user authentication. It allows users to create, retrieve, update, and delete tasks, as well as register and log in to user accounts.
Features

    User registration: Users can create new accounts by providing their name, email, and password.
    User login: Existing users can log in with their email and password to obtain an access token for authentication.
    Create task: Authenticated users can create new tasks by providing a title and description.
    Get all tasks: Authenticated users can retrieve a list of all tasks associated with their account.
    Update task: Authenticated users can update the title or description of their own tasks.
    Delete task: Authenticated users can delete their own tasks.

Technologies Used

The Task Manager API is built using the following technologies:

    Node.js: A JavaScript runtime for executing server-side JavaScript code.
    Express.js: A web application framework for building APIs and handling HTTP requests.
    MongoDB: A NoSQL database for storing task and user data.
    Mongoose: An Object Data Modeling (ODM) library for MongoDB, used to define data models and interact with the database.
    JSON Web Tokens (JWT): A method for securely transmitting information as JSON objects, used for user authentication and authorization.

Getting Started

To set up and run the Task Manager API locally, follow these steps:

    Clone the repository: git clone <repository-url>
    Install dependencies: npm install
    Set up the environment variables:
        Create a .env file in the project root directory.
        Define the following environment variables in the .env file:
            DB_HOST: The connection host. The default is set to 'localhost'.
            DB_PORT: The connection port. The default is set to '27017'.
            DB_NAME: The database name. The default is set as 'task_manager'.
            JWT_SECRET: A secret key for signing and verifying JWT tokens.
    Start the server: npm start
    The API will be accessible at http://localhost:3000.

API Endpoints
User Registration

    POST /api/users/register
        Request body: { "name": "John Doe", "email": "john@example.com", "password": "password" }
        Creates a new user account with the provided name, email, and password.

User Login

    POST /api/users/login
        Request body: { "email": "john@example.com", "password": "password" }
        Returns an access token if the email and password are valid, which can be used for authentication.

Task Management

    POST /api/tasks
        Request body: { "title": "Task Title", "description": "Task Description" }
        Creates a new task with the provided title and description. Requires authentication.

    GET /api/tasks
        Retrieves a list of all tasks associated with the authenticated user. Requires authentication.

    PATCH /api/tasks/:taskId
        Request body: { "title": "New Task Title" } or { "description": "New Task Description" } or { "status": "IN_PROGRESS" }
        Updates the title or description or status of the specified task. Requires authentication and ownership of the task.
        NOTE:
        Status field is defined as an enum with the values:
            "TODO", "IN_PROGRESS", "DONE". TODO is the default value

    DELETE /api/tasks/:taskId
        Deletes the specified task. Requires authentication and ownership of the task.

Error Handling

The API includes centralized error handling to provide consistent and meaningful error responses. If an error occurs during a request, the API will return an appropriate HTTP status code and an error message in the response body.
Authentication

The Task Manager API uses JSON Web Tokens (JWT) for user authentication. Upon successful login, an access token is generated and returned to the client. The client should include this access token in the Authorization header of subsequent requests to access protected endpoints.
Conclusion

The Task Manager API provides a simple and secure way to manage tasks and user accounts. By following the documentation and utilizing the provided endpoints, developers can build task management applications that integrate seamlessly with the API.
