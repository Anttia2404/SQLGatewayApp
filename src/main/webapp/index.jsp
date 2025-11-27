<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email List - SQL Gateway App</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>

<div class="nav">
    <a href="emailList">Email List</a>
    <a href="sqlGateway">SQL Gateway</a>
</div>

<div class="container">
    <h1>Join Our Email List</h1>
    <p>Enter your information below to subscribe to our mailing list.</p>
    
    <c:if test="${not empty message}">
        <div class="message error">
            ${message}
        </div>
    </c:if>

    <form action="emailList" method="post">
        <input type="hidden" name="action" value="add">
        
        <label>Email Address</label>
        <input type="email" name="email" value="${user.email}" required placeholder="your.email@example.com">
        
        <label>First Name</label>
        <input type="text" name="firstName" value="${user.firstName}" required placeholder="John">
        
        <label>Last Name</label>
        <input type="text" name="lastName" value="${user.lastName}" required placeholder="Doe">
        
        <input type="submit" value="Subscribe">
    </form>
</div>

</body>
</html>
