<%--
  Created by IntelliJ IDEA.
  User: moham
  Date: 24/4/2018
  Time: 12:04 AM
  To change this template use File | Settings | File Templates.
--%>
<%@page import="java.sql.*"%>
<%@page import="java.io.IOException"%>
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
        String queryInput = request.getParameter("SQuery");
    %>
    <title><%= queryInput%> - Harry Potato Search</title>

</head>

<body>

<header>

    <p id="SEname">Harry Potato</p>

    <form method="GET" action="main.jsp" name="form1">

        <input type="text" name="SQuery" id="SQuery" size=100>
        <input type="image" name="Lumos" alt="Lumos" src="ElderWand.png">

    </form>

</header>

<%!
    public class HPEngine{
        String URL = ""; //URL of database
        String USERNAME = "";
        String PASSWORD = "";

        Connection connection = null;
        PreparedStatement query = null;
        ResultSet resultSet = null;

        protected void doGet(HttpServletRequest request,  HttpServletResponse response)
                throws ServletException, IOException {
            String queryInput;

            queryInput = request.getParameter("SQuery");
        }

        public HPEngine() throws SQLException{

            try{
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                String queryInput = request.getParameter("SQuery");

                if (queryInput.startsWith("\"") && queryInput.endsWith("\""))
                {
                    String searchQ = "SELECT " + queryInput + "FROM "; //Search query
                    query = connection.prepareStatement(searchQ); // actual select search statement needed
                }
                else if (queryInput.isEmpty())
                {
                    //add button code here
                }
                else {
                    String[] words=queryInput.split("\\s");

                    for(String w:words) {

                        String searchQ = "SELECT " + w + "FROM "; //Search query
                        query = connection.prepareStatement(searchQ); // actual select search statement needed
                    }
                }

            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }

        public ResultSet getresult() throws SQLException{

            try{
                resultSet = query.executeQuery();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return resultSet;
        }
    }
%>

<div id="main">

</div>

</body>

</html>