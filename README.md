# SQL Gateway Application

A Java Web Application demonstrating SQL execution and user registration with database integration.

## Features

### 1. SQL Gateway
- Execute SQL statements (SELECT, INSERT, UPDATE, DELETE) via web interface
- Display query results in formatted HTML tables
- Show affected row counts for DML operations
- Error handling for invalid SQL

### 2. Email List Registration
- User registration form with validation
- Duplicate email detection
- Database persistence using PreparedStatement
- Connection pooling for performance

## Project Structure

```
SQLGatewayApp/
├── src/main/
│   ├── java/murach/
│   │   ├── sql/
│   │   │   ├── SqlGatewayServlet.java
│   │   │   └── SQLUtil.java
│   │   ├── email/
│   │   │   └── EmailListServlet.java
│   │   ├── business/
│   │   │   └── User.java
│   │   └── data/
│   │       ├── ConnectionPool.java
│   │       ├── DBUtil.java
│   │       └── UserDB.java
│   └── webapp/
│       ├── WEB-INF/web.xml
│       ├── META-INF/context.xml
│       ├── styles/main.css
│       ├── sqlGateway.jsp
│       ├── index.jsp
│       └── thanks.jsp
├── pom.xml
└── database.sql
```

## Setup Instructions

### 1. Database Setup
```bash
# Run the PostgreSQL script
psql -U postgres -f database_postgresql.sql
```

### 2. Configure Database Connection
Edit `src/main/webapp/META-INF/context.xml`:
- Update `username` and `password` to match your PostgreSQL credentials
- Default is `postgres`/`postgres`

### 3. Open in NetBeans
1. File → Open Project
2. Navigate to `SQLGatewayApp` folder
3. Click "Open Project"

### 4. Run Application
1. Right-click project → Run
2. Access URLs:
   - Email List: `http://localhost:8080/SQLGatewayApp/`
   - SQL Gateway: `http://localhost:8080/SQLGatewayApp/sqlGateway`

## Technologies Used

- Java 11
- Jakarta EE (Servlets, JSP)
- JSTL
- PostgreSQL
- Maven
- JDBC Connection Pooling

## Sample SQL Queries

```sql
-- View all users (Note: Use quotes for table name "user")
SELECT * FROM "user"

-- Add new user
INSERT INTO "user" (email, firstname, lastname) 
VALUES ('test@example.com', 'Test', 'User')

-- Update user
UPDATE "user" SET firstname = 'Updated' WHERE email = 'test@example.com'

-- Delete user
DELETE FROM "user" WHERE email = 'test@example.com'
```

## Security Notes

- Uses PreparedStatement to prevent SQL Injection
- Connection pooling for resource management
- Proper error handling and resource cleanup
