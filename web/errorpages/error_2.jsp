<%-- 
    Document   : error_2
    Created on : 02 27, 26, 12:20:39 PM
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
    <title>Error 2</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <header>
        <h2><%= headerText %></h2>
    </header>

    <main>
        <div class="error-container">
            <h2>Error 2</h2>
            <p>Correct username but incorrect password.</p>
            
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
