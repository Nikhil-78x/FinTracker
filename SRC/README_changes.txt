
FinTrack - Complete package (frontend fixed + backend + SQL)

Contents:
- backend/        (Java backend project generated earlier)
- frontend_fixed/ (Your frontend files, patched: added name attributes, form actions, method=POST, submit buttons)
- sql/schema.sql  (SQL to create database, user, tables)

Important:
- Backend expects MySQL on localhost and a DB user 'fintrack_user' with password 'your_password_here'.
  Edit src/backend/DBConnection.java and sql/schema.sql to set a secure password before running.
- To run backend:
  javac -cp .:mysql-connector-java-8.0.33.jar src/backend/*.java
  java -cp .:mysql-connector-java-8.0.33.jar src.backend.MainServer
- The frontend forms now POST to http://localhost:8000 endpoints:
  /signup, /login, /addTransaction, /setBudget, /profile, /getTransactions, /dashboard

If you want, I can also:
- Replace the simple token/login with a session implementation,
- Add profile and budget endpoints to the backend,
- Switch password hashing to bcrypt.

