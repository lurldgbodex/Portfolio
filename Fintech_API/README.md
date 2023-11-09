# Financial Services API

## Overview

This is a RESTful API for a financial services application built using Python and Flask. The application provides various features for users, including creating accounts, applying for loans, sending and receiving money. It also maintains user account records, loan histories, and transaction logs.

## Features

- **User Account Management**: Users can create accounts, update their profiles, and delete their accounts. User profiles include information such as username, email, first name, last name, phone number, address, and balance.

- **Loan Management**: Users can apply for loans, and administrators can approve and disburse loans. The system tracks loan status and repayment details.

- **Money Transfer**: Users can send money to other users. Transactions are recorded, and balances are updated accordingly.

## Getting Started

### Prerequisites

- Python 3
- Flask
- Flask-RESTful
- Flask-JWT-Extended
- SQLAlchemy
- MySQL
- Docker

### Installation

1. Clone the repository: `git clone <repository-url>`
2. Install the required packages: `pip install -r requirements.txt`

### Configuration

- Configure your database connection in the `config.py` file.
- Set up your environment variables for security (e.g., secret keys and database variables).

### Running the Application

- Start the application with `python app.py`.

### Docker Container

- You can run the application in a Docker container. Use the provided `Dockerfile` and `docker-compose.yml` to set up your environment.

## API Endpoints

- User Account Management:

  - POST `/users/register`: Create a new account.
  - POST `/users/login`: Authenticate and receive an access token.
  - GET `/users/profile`: Retrieve user profile.
  - PUT `/users/profile`: Update user profile.
  - DELETE `/users/profile`: Delete user account.
  - GET `/users/profile/balance`: Check balance

- Loan Management:

  - POST `/loans/apply`: Apply for a loan.
  - GET `/loans/applications/pending`: list of loans not approved or disbursed
  - PUT `/loans/approve/<loan_id>`: Approve a loan.
  - PUT `/loans/disburse/<loan_id>`: Disburse an approved loan.
  - POST `/loans/repay/<loan_id>`: Repay a loan.
  - GET `/loans/history`: View loan and repayment history.

- Money Transfer:

  - POST `/transactions/send`: Send money to another user.
  - GET `/transactions/history`: View transaction history.

## Error Handling

The API provides detailed error messages and status codes for various scenarios, ensuring a smooth user experience.

## Security

The application uses Flask-JWT-Extended for user authentication and authorization. Sensitive data is protected and should be stored securely. Make sure to follow best security practices.
