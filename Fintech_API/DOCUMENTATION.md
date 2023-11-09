# Financial Services API Documentation

## Overview

The Financial Services API provides various features for users to manage their finances, apply for loans, send and receive money. This documentation outlines the available endpoints, their usage, and expected responses.

## Table of Contents

1. [Authentication](#authentication)
2. [User Account Management](#user-account-management)
3. [Loan Management](#loan-management)
4. [Money Transfer](#money-transfer)
5. [Error Handling](#error-handling)

## Authentication

### Login

- **Endpoint**: `/users/login`
- **HTTP Method**: POST
- **Description**: Authenticate and receive an access token.
- **Request Parameters**:
  - `username` (string, required): User's username.
  - `password` (string, required): User's password.
- **Response**:
  - Success (HTTP 200):
    ```json
    {
      "token": "your-access-token"
    }
    ```
  - Failure (HTTP 401):
    ```json
    {
      "message": "Invalid username or password"
    }
    ```

## User Account Management

### Create Account

- **Endpoint**: `/users/register`
- **HTTP Method**: POST
- **Description**: Create a new account.
- **Request Parameters**:
  - `username` (string, required): Desired username.
  - `password` (string, required): Password.
  - _Other optional user profile fields._
- **Response**:
  - Success (HTTP 201):
    ```json
    {
      "success": true,
      "message": "User created successfully"
    }
    ```
  - Failure (HTTP 400):
    ```json
    {
      "error": "User already exists"
    }
    ```

### User Balance

**Endpoint**: `/users/profile/balance`

**HTTP Method**: GET

**Description**: This endpoint allows authorized users to retrieve their account balance. The response includes the current balance of the authenticated user.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:
  ```http
  GET /users/profile/balance
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "balance": 1000.0
  }
  ```

- Failure (HTTP 400):
  - If the user is not found:
  ```json
  {
    "message": "User not found"
  }
  ```

**Permissions**:

- Only authorized users with valid JWT tokens can access this endpoint.

**Notes**:

- Users can retrieve their account balance, which is presented as a numeric value.

### User Profile

**Endpoint**: `/users/profile`

**HTTP Methods**: GET, PUT

**Description**: This endpoint allows authorized users to view and update their user profile information. Users can view their profile details, such as username, email, first name, last name, phone number, address, balance, and date of birth. They can also update some of these profile fields.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

### Retrieve User Profile

### Request

- **HTTP Method**: GET

- **Description**: Retrieves the user's profile information.

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:

  ```http
  GET /users/profile
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "id": 123,
    "username": "john_doe",
    "email": "john.doe@example.com",
    "first_name": "John",
    "last_name": "Doe",
    "phone_number": "123-456-7890",
    "address": "123 Main St, City, Country",
    "balance": 1000.0,
    "date_of_birth": "1990-01-01"
  }
  ```

- Failure (HTTP 400):
  - If the user is not found:
    ```json
    {
      "message": "User not found"
    }
    ```

### Update User Profile

### Request

- **HTTP Method**: PUT

- **Description**: Updates user profile information.

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:

  ```http
  PUT /users/profile
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

  Request Body (JSON):

  ```json
  {
    "email": "new_email@example.com",
    "first_name": "New First Name",
    "last_name": "New Last Name",
    "phone_number": "987-654-3210",
    "address": "456 Updated St, City, Country",
    "date_of_birth": "1995-02-15"
  }
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "message": "User profile updated successfully"
  }
  ```

- Failure (HTTP 400):

  - If the user is not found:
    ```json
    {
      "message": "User not found"
    }
    ```
  - If the request body is empty or missing:
    ```json
    {
      "message": "Please provide fields to update"
    }
    ```

- Failure (HTTP 400):

  - If there are validation errors:
    ```json
    {
      "error": "User profile update failed",
      "message": "Invalid date format"
    }
    ```

- Failure (HTTP 500):
  - If there is a server error:
    ```json
    {
      "error": "User profile update failed",
      "message": "Internal server error"
    }
    ```

**Permissions**:

- Only authorized users with valid JWT tokens can access these endpoints.

**Notes**:

- Users can retrieve and update their user profile information.
- The profile can be updated with a subset of the following fields: email, first name, last name, phone number, address, and date of birth.
- Field validation is performed, ensuring that valid values are provided and that data integrity is maintained.
- Users can update their profile as needed, keeping their information up to date.

### Delete User Account

**Endpoint**: `/users/profile`

**HTTP Method**: DELETE

**Description**: This endpoint allows authenticated users to delete their user accounts. Before an account can be deleted, the user must meet the following criteria:

1. No outstanding loans should be associated with the user's account.
2. The user's account balance should be zero.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**Response Format**:

- JSON

### Request

No request parameters are required as the action is based on the authenticated user's token.

### Response

- Success (HTTP 200):

  ```json
  {
    "message": "User account deleted successfully"
  }
  ```

- Failure (HTTP 400 or 403):

  - If the user has an outstanding loan:

  ```json
  {
    "message": "You have to pay your loan before you can delete your account"
  }
  ```

  - If the user's account balance is greater than zero:

  ```json
  {
    "message": "Please withdraw your balance before closing your account"
  }
  ```

**Permissions**:

- The user must be authenticated with a valid JWT token.
- The user must have no outstanding loans.
- The user's account balance must be zero.

## Loan Management

### Apply for a Loan

**Endpoint**: `/loans/apply`

**HTTP Method**: POST

**Description**: This endpoint allows authenticated users to apply for a loan. To apply for a loan, the user must have a complete user profile, and they should not have any active loans or outstanding repayments. The user can specify the loan amount they want and, optionally, the due date. If the due date is not specified, a default value of 30 days is used.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**Request Body**:

- `amount` (required): The loan amount requested. Must be a positive float.
- `due_date` (optional): The number of days for the loan's due date. If not provided, the default value is 30 days.

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:

  ```http
  POST /loans/apply
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  Content-Type: application/json

  {
    "amount": 1000.0,
    "due_date": 45
  }
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "message": "Loan application submitted successfully"
  }
  ```

- Failure (HTTP 400 or 404):

  - If the user's profile is incomplete:

  ```json
  {
    "message": "Please complete your profile before you apply for a loan"
  }
  ```

  - If the user already has an active loan:

  ```json
  {
    "message": "You already have an active loan."
  }
  ```

  - If the request is missing required parameters or the loan amount is not a positive float:

  ```json
  {
    "error": "Invalid request. Check your parameters."
  }
  ```

  - If the specified due date is invalid:

  ```json
  {
    "error": "Invalid due date. Due date should be a positive integer."
  }
  ```

**Permissions**:

- The user must be authenticated with a valid JWT token.
- The user must have a complete user profile.
- The user should not have an active loan.

**Notes**:

- Loan applications are subject to approval by the admin.
- The user is encouraged to provide the due date to customize the loan term.
- All loan details, including the requested amount, interest, and due date, are managed by the system and cannot be modified by the user.

### Pending Loan Applications

**Endpoint**: `/loans/applications/pending`

**HTTP Method**: GET

**Description**: This endpoint allows authorized users, specifically admins and financial officers, to view a list of pending loan applications that require either approval or disbursement. It provides details about the loan applications, including the loan amount, status, interest, and date of application.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:
  ```http
  GET /loans/applications/pending
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "loans": [
      {
        "loan_id": 1,
        "loan_amount": 1000.0,
        "loan_status": "pending",
        "interest": 50.0,
        "Loan_date": "2023-10-01T14:30:00"
      },
      {
        "loan_id": 2,
        "loan_amount": 750.0,
        "loan_status": "approved",
        "interest": 37.5,
        "Loan_date": "2023-10-05T11:45:00"
      }
      // ... Additional loan records ...
    ]
  }
  ```

- Failure (HTTP 403):

  - If the user is not authorized to access this endpoint:

  ```json
  {
    "message": "Unauthorized to access this endpoint"
  }
  ```

  - If there are no pending loans:

  ```json
  {
    "message": "There is no loan pending for approval or disbursement"
  }
  ```

**Permissions**:

- Only users with the roles of "admin" or "financial_officer" are allowed to access this endpoint.
- Other user roles will receive a "403 Unauthorized" response.

**Notes**:

- The endpoint provides a list of loans with the following details: loan ID, loan amount, loan status, interest (calculated based on interest rate), and the date of application.
- Admins or financial officers can review the list of pending loans and take necessary actions.
- Loans with a status of "pending" require approval, while those with a status of "approved" are pending disbursement.
- The interest is calculated based on the predefined interest rate.
- All loans have a timestamp indicating when the application was made.

### Approve Loan Application

**Endpoint**: `/loans/approve/<int:loan_id>`

**HTTP Method**: PUT

**Description**: This endpoint is used to approve a pending loan application. Only users with the "admin" role are authorized to approve loans. When a loan is approved, its status changes from "pending" to "approved," and the approval is recorded with the user who approved it.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**URL Parameters**:

- `loan_id` (int): The unique identifier of the loan application to be approved.

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:
  ```http
  PUT /loans/approve/123
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "message": "Loan approved successfully"
  }
  ```

- Failure (HTTP 404):

  - If the specified loan does not exist:

  ```json
  {
    "message": "Loan not found"
  }
  ```

  - If the user is not authorized to approve loans (role is not "admin"):

  ```json
  {
    "message": "Unauthorized to approve loans"
  }
  ```

**Permissions**:

- Only users with the "admin" role are allowed to access this endpoint.
- If another user role attempts to use this endpoint, they will receive a "403 Unauthorized" response.

**Notes**:

- This endpoint is designed to be used by administrators for loan approval.
- The user must provide the loan ID in the URL to specify which loan to approve.
- When a loan is successfully approved, the status changes from "pending" to "approved."
- The "approved_by" field of the loan is updated to store the user ID of the administrator who approved the loan.
- A successful response message confirms that the loan has been approved.

### Disburse Loan

**Endpoint**: `/loans/disburse/<int:loan_id>`

**HTTP Method**: PUT

**Description**: This endpoint is used to disburse an approved loan. Only users with the "financial_officer" role are authorized to disburse loans. When a loan is disbursed, its status changes from "approved" to "disbursed," and the disbursement is recorded with the user who disbursed it. The user's balance is increased by the loan amount.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**URL Parameters**:

- `loan_id` (int): The unique identifier of the loan to be disbursed.

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:
  ```http
  PUT /loans/disburse/123
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "message": "Loan disbursed successfully"
  }
  ```

- Failure (HTTP 404):

  - If the specified loan does not exist:

  ```json
  {
    "message": "Loan not found"
  }
  ```

  - If the user is not authorized to disburse loans (role is not "financial_officer"):

  ```json
  {
    "message": "Unauthorized to disburse loans"
  }
  ```

**Permissions**:

- Only users with the "financial_officer" role are allowed to access this endpoint.
- If another user role attempts to use this endpoint, they will receive a "403 Unauthorized" response.

**Notes**:

- This endpoint is designed for financial officers to disburse approved loans.
- The user must provide the loan ID in the URL to specify which loan to disburse.
- When a loan is successfully disbursed, the status changes from "approved" to "disbursed."
- The "disbursed_by" field of the loan is updated to store the user ID of the financial officer who disbursed the loan.
- The user's balance is increased by the loan amount after disbursement.
- A successful response message confirms that the loan has been disbursed.

### Repay Loan

**Endpoint**: `/loans/repay/<int:loan_id>`

**HTTP Method**: POST

**Description**: This endpoint allows users to make loan repayments. Users are required to specify the loan they want to repay by providing the `loan_id`. Users can choose to make either a full repayment or a partial repayment. After each successful repayment, the loan's remaining balance is updated, and a repayment record is created in the system.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**URL Parameters**:

- `loan_id` (int): The unique identifier of the loan to be repaid.

**Request Body**:

- `type` (string, optional): Specifies the type of repayment. Allowed values are `"FULL"` (full repayment) and `"PARTIAL"` (partial repayment). Defaults to `"FULL"` if not provided.

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request (Full Repayment):

  ```http
  POST /loans/repay/123
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

- Example Request (Partial Repayment):

  ```http
  POST /loans/repay/123
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  Content-Type: application/json

  {
    "type": "PARTIAL"
  }
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "message": "Loan repayment successful"
  }
  ```

- Failure (HTTP 400):

  - If the specified loan does not exist:

  ```json
  {
    "message": "Loan not found"
  }
  ```

  - If the user is not authorized to repay this loan (the loan's `user_id` does not match the current user's ID):

  ```json
  {
    "message": "Unauthorized to repay this loan"
  }
  ```

  - If the specified loan is not in the "disbursed" status, indicating that it must be disbursed before repayment:

  ```json
  {
    "message": "Loan must be disbursed to make a repayment"
  }
  ```

  - If an invalid repayment type is provided:

  ```json
  {
    "message": "Invalid repay type entered"
  }
  ```

  - If the user's balance is insufficient to make the repayment:

  ```json
  {
    "message": "Your balance is insufficient to make repayment"
  }
  ```

- Note: When a full repayment is made and the loan's remaining balance becomes zero, the loan's status is updated to "paid."

**Permissions**:

- Only authorized users with valid JWT tokens can access this endpoint.

**Notes**:

- Users can specify the repayment type as "FULL" or "PARTIAL" in the request body.
- When making a partial repayment, the amount is calculated as half of the remaining balance.
- A successful repayment updates the loan's remaining balance, decreases the user's balance, and creates a repayment record.
- If the remaining balance of the loan becomes zero, the loan's status is updated to "paid."

### Loan History

**Endpoint**: `/loans/history`

**HTTP Method**: GET

**Description**: This endpoint allows users to retrieve their loan history, including details of their past loans and repayments. The loan history data provides insights into each loan's status, amount, interest, and associated repayments.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:
  ```http
  GET /loans/history
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "loan_history": [
      {
        "loan_id": 1,
        "loan_amount": 1000.0,
        "loan_status": "paid",
        "interest": 200.0,
        "Loan_date": "2023-10-15 12:30:45",
        "repayment_amount": 1200.0,
        "repayment_date": "2023-11-15 09:45:23"
      },
      {
        "loan_id": 2,
        "loan_amount": 750.0,
        "loan_status": "disbursed",
        "interest": 150.0,
        "Loan_date": "2023-09-20 15:10:33",
        "repayment_amount": 0.0,
        "repayment_date": null
      }
      // Additional loan history entries...
    ]
  }
  ```

- Failure (HTTP 404):
  - If the user is not found (the JWT token is invalid):
  ```json
  {
    "message": "User not found"
  }
  ```

**Permissions**:

- Only authorized users with valid JWT tokens can access this endpoint.

**Notes**:

- The loan history data includes an array of loan history entries.
- Each entry provides details such as `loan_id`, `loan_amount`, `loan_status`, `interest`, `Loan_date`, `repayment_amount`, and `repayment_date`.
- If a loan has not been repaid (repayment amount is 0.00), the `repayment_date` will be `null`.
- The loan status can be one of "pending," "approved," "disbursed," or "paid."

## Money Transfer

### Send Money

**Endpoint**: `/transactions/send`

**HTTP Method**: POST

**Description**: This endpoint allows users to send money to another user. Users can specify the recipient's username, the amount to send, and an optional transaction description.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**Request Body**:

- `amount`: (float) The amount of money to send (required).
- `receiver`: (string) The username of the recipient (required).
- `description`: (string, optional) A description or note for the transaction.

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:

  ```http
  POST /transactions/send
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  Content-Type: application/json

  {
    "amount": 100.00,
    "receiver": "recipient_username",
    "description": "Payment for services"
  }
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "message": "Money sent successfully"
  }
  ```

- Failure (HTTP 400):

  - If the sender's balance is insufficient:

  ```json
  {
    "message": "Your balance is insufficient to make the transaction"
  }
  ```

  - If the recipient user is not found:

  ```json
  {
    "message": "Recipient user not found"
  }
  ```

  - If the transaction fails for any other reason:

  ```json
  {
    "error": "Transaction failed",
    "message": "Detailed error message"
  }
  ```

**Permissions**:

- Only authorized users with valid JWT tokens can access this endpoint.

**Notes**:

- Users must provide the recipient's username, the amount to send, and can optionally include a transaction description.
- The sender's account balance must be sufficient to complete the transaction; otherwise, it will fail.
- A unique transaction ID is generated for each transaction.
- The transaction status is set to "success."
- The sender's balance is updated to reflect the transaction.
- The recipient will receive the funds in their account.

### Transaction History

**Endpoint**: `/transactions/history`

**HTTP Method**: GET

**Description**: This endpoint allows users to retrieve their transaction history. It provides information about all transactions (debits and credits) involving the user's account.

**Request Headers**:

- `Authorization`: Bearer token obtained during user login (`JWT` token).

**Response Format**:

- JSON

### Request

- Authentication is required, and the user's JWT token should be included in the request header.

- Example Request:
  ```http
  GET /transactions/history
  Host: your-api.com
  Authorization: Bearer YOUR_JWT_TOKEN
  ```

### Response

- Success (HTTP 200):

  ```json
  {
    "transactions": [
      {
        "id": "transaction_id_1",
        "sender": "sender_username_1",
        "receiver": "receiver_username_1",
        "amount": 100.0,
        "status": "success",
        "type": "debit",
        "timestamp": "transaction_timestamp_1"
      },
      {
        "id": "transaction_id_2",
        "sender": "sender_username_2",
        "receiver": "receiver_username_2",
        "amount": 75.5,
        "status": "success",
        "type": "credit",
        "timestamp": "transaction_timestamp_2"
      },
      {
        "id": "transaction_id_3",
        "sender": "sender_username_3",
        "receiver": "receiver_username_3",
        "amount": 50.0,
        "status": "success",
        "type": "debit",
        "timestamp": "transaction_timestamp_3"
      }
    ]
  }
  ```

- Failure (HTTP 400):
  - If the user is not found:
  ```json
  {
    "message": "User not found"
  }
  ```

**Permissions**:

- Only authorized users with valid JWT tokens can access this endpoint.

**Notes**:

- Users can retrieve a list of their transaction history, including both debits and credits.
- Each transaction in the response includes the transaction ID, sender, receiver, transaction amount, transaction status, transaction type (debit/credit), and timestamp.
- The "type" field is used to distinguish between debit (outgoing transactions) and credit (incoming transactions).
- The response contains an array of transaction history data.

## Error Handling

The API provides detailed error messages and status codes for various scenarios, ensuring a smooth user experience.
