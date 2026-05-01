<%-- 
    Document   : success
    Created on : 02 27, 26, 12:19:13 PM
    Author     : georg
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
// 1. Check if the user is actually logged in
if (session == null || session.getAttribute("username") == null) {
    response.sendRedirect(request.getContextPath() + "/errorpages/error_session.jsp");
    return;
}

// 2. get global variables
String headerText = application.getInitParameter("header");
String footerText = application.getInitParameter("footer");

// 3. Get user details
String username = (String) session.getAttribute("username");
String role = (String) session.getAttribute("role");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <header>
        <h2><%= headerText %></h2>
    </header>

    <main>
        <div class="card-container">
            <h3>Login Successful!</h3>
            
            <p style="font-size: 1.1em; margin-bottom: 5px;">Welcome, <b><%= username %></b></p>
            <p style="margin-top: 0; color: #888;">Your role is: <b style="color: #d47fa6;"><%= role.toUpperCase() %></b></p>

            <br>

            <% if (role.equalsIgnoreCase("admin")) { %>
                <a href="<%= request.getContextPath() %>/admin.jsp">
                    <button type="button" style="margin-bottom: 15px;">Go to Admin Panel</button>
                </a>
            <% } else { %>
                <a href="<%= request.getContextPath() %>/guest.jsp">
                    <button type="button" style="margin-bottom: 15px;">Go to Guest Panel</button>
                </a>
            <% } %>

            <br>
            <a href="<%= request.getContextPath() %>/LogoutServlet" style="font-size: 0.9em; color: #a0a0a0;">Logout</a>
        </div>
    </main>

    <footer>
        <p><%= footerText %></p>
    </footer>

</body>
</html>