<%-- 
    Document   : guest
    Created on : 02 27, 26, 12:19:30 PM
    Author     : georg
--%>

<%@page import="dao.UserDAO,model.User"%>
<%
// 1. Prevent caching first
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires", 0);

// 2. Session and Role Verification
if (session.getAttribute("username") == null) {
    response.sendRedirect(request.getContextPath() + "/errorpages/error_session.jsp");
    return;
}

String role = (String) session.getAttribute("role");

if (role == null || !role.equalsIgnoreCase("guest")) {
    response.sendRedirect(request.getContextPath() + "/errorpages/error_session.jsp");
    return;
}
    
// 3. Database Connection
String dbURL = application.getInitParameter("dbURL");
String dbUser = application.getInitParameter("dbUser");
String dbPass = application.getInitParameter("dbPass");

UserDAO dao = new UserDAO(dbURL, dbUser, dbPass);

String email = (String) session.getAttribute("username");
User user = dao.getUserByEmail(email);

// 4. Global Header and Footer 
String headerText = application.getInitParameter("header");
String footerText = application.getInitParameter("footer");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Guest Panel</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <header>
        <h2><%= headerText %></h2>
    </header>

    <main>
        
        <div class="card-container">
            <h3>Guest Panel</h3>
            <p style="font-size: 1.1em;">Welcome, <b><%= email %></b></p>
            <p style="margin-top: 0; color: #d47fa6; font-weight: bold;">(GUEST)</p>
            <br>
            <a href="<%= request.getContextPath() %>/LogoutServlet" style="font-size: 0.9em; color: #a0a0a0;">Logout</a>
        </div>

        <table>
            <tr>
                <th colspan="3" style="text-align: center; font-size: 1.2em; border-bottom: 2px solid #f0cadd;">Your Credentials</th>
            </tr>
            <tr>
                <th>Email</th>
                <th>Password</th>
                <th>Role</th>
            </tr>
            <tr>
                <td><%= user.getEmail() %></td>
                <td><%= user.getPassword() %></td>
                <td><b style="color: #d47fa6;"><%= user.getRole().toUpperCase() %></b></td>
            </tr>
        </table>

    </main>

    <footer>
        <p><%= footerText %></p>
    </footer>

</body>
</html>