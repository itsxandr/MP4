<%-- 
    Document   : noLoginCredentials
    Created on : 02 27, 26, 12:21:17 PM
    Author     : georg
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
// Fetch global header and footer variables
String headerText = application.getInitParameter("header");
String footerText = application.getInitParameter("footer");
%>
<!DOCTYPE html>
<html>
<head>
    <title>No Login Credentials</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <header>
        <h2><%= headerText %></h2>
    </header>

    <main>
        <div class="error-container">
            <h2>Error</h2>
            <p>Username and Password cannot both be empty.</p>
            
            <br>
            <a href="<%= request.getContextPath() %>/index.jsp">
                <button type="button">Back to Login</button>
            </a>
        </div>
    </main>

    <footer>
        <p><%= footerText %></p>
    </footer>

</body>
</html>