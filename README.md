mWallet
A secure and modern digital wallet REST API built with Spring Boot that enables users to create accounts, verify their email, manage wallets, deposit funds, transfer money, and track transactions.
The project follows production-ready practices including JWT authentication, role-based authorization, optimistic locking, Flyway database migrations, idempotent transaction processing, rate limiting, and email notifications.


## Features

## Authentication & User Management

* User registration
* Email verification
* Resend verification email
* JWT authentication
* Refresh Tokens
* BCrypt password hashing
* Role-based authorization (USER & ADMIN)
* Secure login

---

### Wallet Management

* Automatic wallet creation after email verification
* Unique wallet account number generation
* Wallet balance retrieval
* NGN wallet support

---

### Deposits

* Atomicc deposit operation
* Automatic balance update
* Deposit transaction history
* Deposit email notifications
* Logging
* Idempotent deposits to prevent duplicate processing

---

### Transfers

* Wallet-to-wallet transfers 
* Atomic debit and credit operations
* Optimistic locking to prevent concurrent balance modification
* Insufficient balance validation
* Duplicate transfer protection using Idempotency Keys
* Loging
* Debit email alerts
* Credit email alerts

---

### Transactions

* Retrieve all wallet transactions
* Retrieve a specific transaction
* Retrieve wallet deposits
* Retrieve wallet
* Logging
* Transaction references for every operation

---

### Admin Features

* View user wallet
* View user transactions
* View user deposits
* View user transfers

---

### Security

* Spring Security
* JWT Authentication
* Role-based access control
* Password encryption using BCrypt
* Endpoint authorization
* Global exception handling
* Request validation

---

### Rate Limiting

Implemented using **Bucket4j**.

Protected endpoints include:

* Registration
* Login
* Email Verification
* Resend Verification
* Deposit
* Transfer
* Wallet deposits
* Wallet transfers
* Wallet retrieval
* Wallet transaction retrieval

Rate limiting is performed per authenticated user or client IP where applicable.

---

### Email Notifications

Powered by **Brevo SMTP/API**

The application automatically sends:

* Email verification
* Wallet creation details
* Deposit alerts
* Debit alerts
* Credit alerts

---

### Reliability Features

* Flyway database migrations
* Optimistic locking
* Idempotent transaction processing
* Duplicate request prevention
* Transaction reference generation
* Database uniqueness constraints

---

## Tech Stack

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* PostgreSQL
* Flyway
* JWT
* Bucket4j
* Brevo Email API
* Maven
* Hibernate
* Lombok

---

## Project Structure

```text
src
├── authentication
├── config
├── deposit
├── email
├── enumTpes
├── exception
├── idempotency
├── rateLimit
├── refreshToken
├── interceptor
├── roles
├── transaction
├── transfer
├── userAccount
├── userEvent
├── util
└── wallet
```

---

### Configure Environment Variables

```
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

JWT_SECRET

BREVO_API_KEY
BREVO_SENDER_EMAIL
BREVO_SENDER_NAME
```

### Run Flyway Migrations

Migrations execute automatically on application startup.

---

### Start the application

```bash
mvn spring-boot:run
```

---

## API Documentation

Scalar UI is available at

```
https://registry.scalar.com/@default-team-w73gl/apis/openapi-definition@latest
```

---

## Deployment

The application is configured for deployment on Railway.

Required environment variables:

* DATABASE_URL
* DATABASE_USERNAME
* DATABASW_PASSWORD
* JWT_SECRET
* BREVO_API_KEY
* BREVO_SENDER_EMAIL
* BREVO_SENDER_NAME

---

## Future Improvements

* Multi-currency wallets
* Scheduled transfers
* Transaction reversal workflow
* Account freeze/unfreeze
* Beneficiary management
* Redis caching
* Docker support
* Monitoring with Prometheus & Grafana

---

## License

This project is .

---

## Author

**Olarewaju Oluwatomi**
