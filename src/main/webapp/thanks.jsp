<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thank You - SQL Gateway App</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>

<div class="nav">
    <a href="emailList">Email List</a>
    <a href="sqlGateway">SQL Gateway</a>
</div>

<div class="container">
    <h1>Thank You</h1>

    <div class="message success">
        <p>You have successfully joined our email list.</p>
    </div>

    <div style="background-color: var(--bg-tertiary); padding: 1.5rem; border-radius: 6px; margin: 1.5rem 0;">
        <p style="margin-bottom: 0.75rem;"><strong>Email:</strong> ${user.email}</p>
        <p style="margin-bottom: 0.75rem;"><strong>First Name:</strong> ${user.firstName}</p>
        <p style="margin-bottom: 0;"><strong>Last Name:</strong> ${user.lastName}</p>
    </div>

    <p>To add another email address, click the button below.</p>

    <form action="emailList" method="get">
        <input type="hidden" name="action" value="join">
        <input type="submit" value="Add Another">
    </form>
</div>

</body>
</html>
