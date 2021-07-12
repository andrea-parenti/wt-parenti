<%--
  Created by IntelliJ IDEA.
  User: andrea
  Date: 12/07/21
  Time: 12:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Login</title>
</head>
<body>
<h1>Welcome to Exam Management Application</h1>
<form action="CheckLogin" method="POST">
    username: <input type="text" name="username">
    <br>
    password: <input type="password" name="password">
    <br>
    <input type="submit" value="login">
</form>
</body>
</html>
