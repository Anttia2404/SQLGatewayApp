<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SQL Gateway - SQL Gateway App</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>

<div class="nav">
    <a href="emailList">Email List</a>
    <a href="userManagement">User Management</a>
    <a href="sqlGateway">SQL Gateway</a>
</div>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${sqlStatement == null}">
    <c:set var="sqlStatement" value='select * from "user"' />
</c:if>

<div class="container">
    <h1>SQL Gateway</h1>
    <p>Enter an SQL statement below and execute it against the database.</p>

    <form action="sqlGateway" method="post">
        <label>SQL Statement</label>
        <textarea name="sqlStatement" rows="10" placeholder="Enter your SQL query here...">${sqlStatement}</textarea>
        <input type="submit" value="Execute Query">
    </form>

    <c:if test="${not empty sqlResult}">
        <div class="result-section">
            <h2>Query Results</h2>
            ${sqlResult}
        </div>
    </c:if>
</div>

</body>
</html>
