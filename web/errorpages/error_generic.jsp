    <%-- 
    Document   : error_generic
    Created on : 02 28, 26, 4:07:17 PM
    Author     : georg
--%>
<%@ page isErrorPage="true" contentType="text/html; charset=UTF-8" %>
<%
// Fetch global header and footer variables
String headerText = application.getInitParameter("header");
String footerText = application.getInitParameter("footer");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Unexpected Error</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <header>
        <h2><%= headerText %></h2>
    </header>

    <main>
        <div class="error-container">
            <h2>Whoops!</h2>
            <p style="font-size: 1.05em;">We encountered an unexpected database or connection error. Please try again later.</p>
            
            <br>
            <a href="<%= request.getContextPath() %>/index.jsp">
                <button type="button">Return to Login</button>
            </a>
        </div>
    </main>

    <footer>
        <p><%= footerText %></p>
    </footer>

</body>
</html>