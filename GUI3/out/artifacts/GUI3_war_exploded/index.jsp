<%--
  Created by IntelliJ IDEA.
  User: moham
  Date: 24/4/2018
  Time: 12:04 AM
  To change this template use File | Settings | File Templates.
--%>
<%@page import="java.sql.*"%>
<%-- <% Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); %> --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">

<head>

  <meta charset="UTF-8">
  <meta name="robots" content="index, follow">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

  <script src="APT.js"></script>

  <link rel="stylesheet" href="apt.css">
  <link rel="icon" href="p4.jpg">

  <title>Harry Potato</title>

</head>

<body>

<div id="main">

  <center>

    <p id="SEname">Harry Potato</p>

    <form method="GET" action="QueryX" name="Query" onsubmit="checkform(this) ">

        <input type="hidden" name="formx" value="form1">
        <input type = "hidden" name = "page" value = "1" />
        <input type="text" name="SQuery" id="SQuery" size=100><br>
        <input type="submit" name="Lumos" id="Lumos" value="Lumos">
        <input type="submit" name="Felix Felicis" id="Felix Felicis" value="Felix Felicis">
      <%-- //open first link from the search query results--%>

    </form>
  </center>

</div>

</body>

</html>