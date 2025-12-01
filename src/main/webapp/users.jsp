<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - SQL Gateway App</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
    <style>
        /* Additional styles specific to user management */
        .page-header {
            background-color: var(--bg-primary);
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: var(--shadow-md);
        }
        
        .page-header h1 {
            margin-bottom: 0.5rem;
        }
        
        .page-header p {
            margin-bottom: 0;
        }
        
        .action-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }
        
        .btn {
            padding: 0.625rem 1.25rem;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.875rem;
            font-weight: 500;
            transition: all 0.2s ease;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-back {
            background-color: var(--bg-tertiary);
            color: var(--text-secondary);
            border: 1px solid var(--border-color);
        }
        
        .btn-back:hover {
            background-color: var(--bg-secondary);
            color: var(--text-primary);
        }
        
        .btn-delete {
            background-color: var(--error);
            color: white;
            padding: 0.5rem 1rem;
            font-size: 0.875rem;
        }
        
        .btn-delete:hover {
            background-color: #dc2626;
            transform: translateY(-1px);
            box-shadow: var(--shadow-md);
        }
        
        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
            background-color: var(--bg-primary);
            border: 1px solid var(--border-color);
            border-radius: 8px;
            box-shadow: var(--shadow-sm);
        }
        
        .empty-state-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
            opacity: 0.3;
        }
        
        .empty-state h2 {
            color: var(--text-secondary);
            font-weight: 600;
            margin-bottom: 0.5rem;
        }
        
        .empty-state p {
            color: var(--text-muted);
        }
        
        .user-count {
            color: var(--text-muted);
            font-size: 0.875rem;
        }
        
        /* Table action buttons */
        table form {
            padding: 0;
            margin: 0;
            border: none;
            box-shadow: none;
            background: transparent;
        }
        
        table button {
            width: auto;
        }
    </style>
</head>
<body>

<div class="nav">
    <a href="emailList">Email List</a>
    <a href="userManagement">User Management</a>
    <a href="sqlGateway">SQL Gateway</a>
</div>

<div class="container">
    <div class="page-header">
        <h1>👥 User Management</h1>
        <p>Manage your email list subscribers</p>
    </div>
    
    <c:if test="${not empty message}">
        <div class="message ${messageType}">
            ${message}
        </div>
    </c:if>
    
    <c:if test="${not empty users}">
        <div style="text-align: right; margin-bottom: 1rem;">
            <span class="user-count">Total Users: ${users.size()}</span>
        </div>
    </c:if>
    
    <c:choose>
        <c:when test="${not empty users}">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email</th>
                        <th style="width: 120px;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.userId}</td>
                            <td>${user.firstName}</td>
                            <td>${user.lastName}</td>
                            <td>${user.email}</td>
                            <td>
                                <form action="userManagement" method="post" style="display: inline;" 
                                      onsubmit="return confirm('Are you sure you want to delete ${user.firstName} ${user.lastName}?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="userId" value="${user.userId}">
                                    <button type="submit" class="btn btn-delete">🗑️ Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <h2>No users found</h2>
                <p>There are no users in the database yet.</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
