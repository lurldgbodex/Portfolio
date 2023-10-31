# API Endpoint Documentation

This documentation provides information about the available endpoints in the API, their purpose, usage, and request/response details.

## Authentication

To access certain endpoints, authentication is required. Use the `X-Token` header with a valid user token.

## Endpoints

### GET /status

**Description:** Get the status of the database.

**Request:** None

**Response:**

- `redis` (boolean): Redis database status.
- `db` (boolean): MongoDB database status.

### GET /stats

**Description:** Get statistics counts of user and file collections in the MongoDB database.

**Request:** None

**Response:**

- `users` (number): The number of users in the MongoDB database.
- `files` (number): The number of files in the MongoDB database.

### POST /users

**Description:** Create a new user with an email and password.

**Request:**

- `email` (string, required): The user's email.
- `password` (string, required): The user's password.

**Response:**

- `id` (string): The ID of the created user.
- `email` (string): The email of the created user.

### GET /users/me

**Description:** Get details of the currently authenticated user.

**Request:** None

**Response:**

- `id` (string): The ID of the user.
- `email` (string): The email of the user.

### GET /connect

**Description:** Sign in a user by generating an authentication token.

**Request Headers:**

- `Authorization` (string, required): Base64-encoded email and password.

**Response:**

- `token` (string): The authentication token (valid for 24 hours).

### GET /disconnect

**Description:** Sign out a user based on their token.

**Request Headers:**

- `X-Token` (string, required): User authentication token.

**Response:**

- No content. Status code 204 indicates success.

### POST /files

**Description:** Upload a file (e.g., image) and optionally generate thumbnails for images.

**Request:**

- `name` (string, required): The name of the file.
- `type` (string, required): The type of the file (folder, file, image).
- `parentId` (string, optional): The ID of the parent folder (default: 0 for the root).
- `isPublic` (boolean, optional): Specify if the file is public (default: false).
- `data` (string, optional): Base64-encoded file content for type=file or type=image.

**Response:**

- `id` (string): The ID of the uploaded file.
- `name` (string): The name of the file.
- `type` (string): The type of the file.
- `parentId` (string): The ID of the parent folder.
- `isPublic` (boolean): Indicates if the file is public.
- `localPath` (string): The path to the local file.

### GET /files/:id

**Description:** Get details of a file based on its ID.

**Request Parameters:**

- `id` (string, required): The ID of the file.

**Request Headers:**

- `X-Token` (string, required): User authentication token.

**Response:**

- Detailed information about the file.

### GET /files

**Description:** Get files based on a parent ID.

**Query Parameters:**

- `parentId` (string, optional): The ID of the parent folder (default: 0).
- `page` (number, optional): The page number for paginated results (default: 0).

**Request Headers:**

- `X-Token` (string, required): User authentication token.

**Response:**

- A list of files belonging to the specified parent folder.

### PUT /files/:id/publish

**Description:** Make a file public by updating its `isPublic` property.

**Request Parameters:**

- `id` (string, required): The ID of the file to publish.

**Request Headers:**

- `X-Token` (string, required): User authentication token.

**Response:**

- Updated information about the file.

### PUT /files/:id/unpublish

**Description:** Unpublish a file by updating its `isPublic` property.

**Request Parameters:**

- `id` (string, required): The ID of the file to unpublish.

**Request Headers:**

- `X-Token` (string, required): User authentication token.

**Response:**

- Updated information about the file.

### GET /files/:id/data

**Description:** Get the content of a file or thumbnails based on the file ID and size.

**Request Parameters:**

- `id` (string, required): The ID of the file.

**Query Parameters:**

- `size` (string, optional): Size of the image (500, 250, 100).

**Request Headers:**

- `X-Token` (string, required): User authentication token.

**Response:**

- The file content or thumbnail, depending on the size.

## Error Handling

- If an error occurs, the API will return an error response with an appropriate HTTP status code and an error message in JSON format.

## Status Codes

- `200`: Success.
- `201`: Resource created.
- `400`: Bad request or invalid input.
- `401`: Unauthorized.
- `404`: Resource not found.
- `500`: Internal server error.

---
