# FinTrack - Java + JDBC Backend (Standalone)

## What this delivers
- A lightweight Java backend using the JRE HttpServer (no servlet container required).
- JDBC-based DAOs for users and transactions.
- SQL schema for creating the MySQL database and tables.
- Simple authentication (email + password) using hashed passwords (SHA-256).
- Endpoints:
  - POST  /signup         -> form params: username, email, password
  - POST  /login          -> form params: email, password (returns simple session token)
  - POST  /addTransaction -> form params: amount, description, category, date, recurring (optional)
  - GET   /getTransactions?email=... -> returns transactions for user
  - POST  /setBudget      -> form params: email, month, year, amount
  - GET   /dashboard?email=...
  - GET   /profile?email=...

## Notes / Integration with your frontend
- Your uploaded frontend HTML is at: `/mnt/data/FinTrack.zip`
- Many of the provided HTML inputs do not contain `name` attributes. For the backend to receive form data, either:
  1. Add `name` attributes to the form inputs to match the expected param names (recommended), or
  2. Submit via JavaScript `fetch()` sending JSON (keys must match the expected names below).
- Expected field names:
  - Signup: username, email, password
  - Login: email, password
  - Add Transaction: email, amount, description, category, date (YYYY-MM-DD), recurring (true/false)
  - Set Budget: email, month (1-12), year (YYYY), amount

## Requirements
- Java 11+ (JRE)
- MySQL server (or compatible)
- MySQL JDBC Connector (mysql-connector-java). Put the JAR on classpath when compiling/running.

## Compile & Run (example)
1. Compile:
   ```
   javac -cp .:mysql-connector-java-8.0.33.jar src/backend/*.java
   ```
2. Run:
   ```
   java -cp .:mysql-connector-java-8.0.33.jar src.backend.MainServer
   ```
3. Server will start on port 8000 by default.

## Files included
- src/backend/MainServer.java
- src/backend/DBConnection.java
- src/backend/UserDAO.java
- src/backend/TransactionDAO.java
- src/backend/Utils.java
- sql/schema.sql

