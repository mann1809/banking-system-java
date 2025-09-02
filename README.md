# Banking System Application

A Java-based console banking application with role-based access control for administrators and customers.

## Features

### Admin Features
- Create new customer accounts
- View transaction history for any user
- Check account balance for any user
- Approve cheque book requests

### Customer Features
- Check account balance
- Transfer funds to other users
- View personal transaction history
- Request cheque books

## Database Requirements

- PostgreSQL database
- Database name: `bank`

## How to Run

1. Ensure PostgreSQL is running on localhost:5432
2. Create a database named `bank`
3. Compile the Java files
4. Run the Main class

## Login System

The application supports two user roles:
- **Admin**: Full access to manage customers and view all transactions
- **User**: Limited access to personal banking operations

## Technologies Used

- Java
- PostgreSQL
- JDBC for database connectivity