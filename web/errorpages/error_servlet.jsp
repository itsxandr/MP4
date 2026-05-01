<%-- 
    Document   : error_servlet
    Created on : 02 28, 26, 4:07:45 PM
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
    <title>System Error</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <header>
        <h2><%= headerText %></h2>
    </header>

    <main>
        <div class="error-container">
            <h2>System Error</h2>
            <p>The server was unable to complete your request due to the following system error:</p>
            
            <div style="color: #b05c82; padding: 15px; border: 1px solid #f0cadd; background-color: #fdf6f8; border-radius: 8px; text-align: left; margin-bottom: 20px;">
                <strong>Error Details:</strong><br><br>
                <% 
                    // The servlet container passes the error message via this attribute
                    String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
                    
                    // Fallback: If the attribute is null, try grabbing the message directly from the exception object
                    if (errorMessage == null && exception != null) {
                        errorMessage = exception.getMessage();
                    }
                    
                    // Print the message, or a default if both are empty
                    out.print(errorMessage != null ? errorMessage : "Unknown system error occurred.");
                %>
            </div>

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