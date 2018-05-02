<%--
  Created by IntelliJ IDEA.
  User: moham
  Date: 24/4/2018
  Time: 12:04 AM
  To change this template use File | Settings | File Templates.
--%>
<%@page import="java.sql.*"%>
<%@page import="java.io.IOException"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.QueryPackage.results" %>

<%-- <% Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); %> --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">

<head>

    <meta charset="UTF-8">
    <meta name="robots" content="index, follow">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

    <script src="APT2.js"></script>

    <link rel="stylesheet" href="apt2.css">
    <link rel="icon" href="p4.jpg">

    <%
        String queryInput = request.getAttribute("queryTest").toString();
    %>
    <title><%= queryInput%> - Harry Potato Search</title>

</head>

<body>

<header>

    <a href="index.jsp" style="text-decoration: none"><p id="SEname">Harry Potato</p></a>

    <form method="GET" action="QueryX" name="form1">

        <input type="text" name="SQuery" id="SQuery" value=<%= queryInput%> size=100>
        <input type="submit" value = "Lumos" name="Lumos" alt="Lumos">
        <input type="hidden" name="formx" value="form1">

    </form>

</header>

        
<div id="main" style="margin: 2vw 40vw 0vw 10vw;">

    <table>
        <% ArrayList<results> info = (ArrayList<results>)request.getAttribute("linkInfo");
            for (int i = 0; i < info.size(); i++) {
                results infos = info.get(i); %>
        <tr>
            <td>
                <h3 class="r" style="margin-bottom: 0px; font-size: 1.17em">
                    <a href=<%out.print(infos.getURL());%>> <%out.print(infos.getTitle());%></a>
                </h3>
                <cite class="citeclass" style="font-size: 14px; color: #006621;"> <%out.print(infos.getURL());%></cite> <br>
                <span class="st" style="margin-bottom: 20px"><%out.print(infos.getText());%></span> <br>
            </td>
        </tr>
        <% } %>
    </table>

    <form method="GET" action="QueryX">
        <input type="hidden" name="formx" value="form2">

        <table border="0" cellpadding="5" cellspacing="5" style="display: inline">
            <tr>
        <% int pageNum = (int)request.getAttribute("noOfPages");
            int currPage = (int)request.getAttribute("currentPage");

            if (currPage == 1){%>
        <%--For displaying Previous link except for the 1st page --%>
            <td style="padding: 30.2px 17.6px 30.2px 17.6px"><input type="hidden" name="previous" src="Previous.png" width="25" height="25"></td>
        <% }
        else {%>
        <td><input type="submit" name="previous" src="Previous.png" width="25" height="25" value="Previous"></td>
        <% }

        for (int i = 1; i <= pageNum; i++){
            if (currPage == i){ %>
                <td><input type="submit" name="page" src="blackdot.png" width="25" height="25" disabled
                            value=<%out.print(i);%>></td>
            <% }
            else {%>
                <td><input type="submit" name="page" src="reddot.png" width="25" height="25"
                           value=<%out.print(i);%>></td>
            <% }
            }

            if (currPage == pageNum){%>
                <td style="padding: 30.2px 17.6px 30.2px 17.6px"><input type="hidden" name="next" src="next.png" width="25" height="25"></td>
            <% }
            else {%>
                <td><input type="submit" name="next" src="next.png" width="25" height="25" value="Next"></td>
            <% } %>
            </tr>
        </table>

    </form>
</div>

</body>

</html>
