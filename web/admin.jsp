    <%-- 
    Document   : admin
    Created on : 02 27, 26, 12:19:20 PM
    Author     : georg
--%>
<%@page import="dao.UserDAO,model.User,java.util.*"%>
<%
// 1. Prevent Caching First
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires", 0);

// 2. Session and Role Verification
if (session.getAttribute("username") == null) {
    response.sendRedirect(request.getContextPath() + "/errorpages/error_session.jsp");
    return;
}

String role = (String) session.getAttribute("role");
if (role == null || !role.equalsIgnoreCase("admin")) {
    response.sendRedirect(request.getContextPath() + "/errorpages/error_session.jsp");
    return;
}

// 3. Database Connection setup
String dbURL = application.getInitParameter("dbURL");
String dbUser = application.getInitParameter("dbUser");
String dbPass = application.getInitParameter("dbPass");

UserDAO dao = new UserDAO(dbURL, dbUser, dbPass);

// 4. CRUD operations
String action = request.getParameter("action");

if (action != null) {
    try {
        
        String encKey = application.getInitParameter("encryptionKey");
        String cipherAlgo = application.getInitParameter("cipherAlgorithm");

        if (action.equals("add")) {
            String rawPassword = request.getParameter("password");
            String encryptedPassword = util.SecurityUtil.encrypt(rawPassword, encKey, cipherAlgo);
            
            dao.addUser(
                request.getParameter("email"),
                encryptedPassword,
                request.getParameter("role")
            );
        }
        if (action.equals("update")) {
            String rawPassword = request.getParameter("password");
            String encryptedPassword = util.SecurityUtil.encrypt(rawPassword, encKey, cipherAlgo);
            
            dao.updateUser(
                request.getParameter("email"),
                encryptedPassword,
                request.getParameter("role")
            );
        }
        if (action.equals("delete")) {
            String targetEmail = request.getParameter("email");
            String currentAdmin = (String) session.getAttribute("username");
            
            // Prevent Admin from deleting themselves
            if (targetEmail.equalsIgnoreCase(currentAdmin)) {
                out.println("<script>alert('Access Denied: You cannot delete your own admin account.');</script>");
            } else {
                dao.deleteUser(targetEmail);
            }
        }
        // Redirect back to admin.jsp to refresh the user list
        if (!action.equals("delete") || !request.getParameter("email").equalsIgnoreCase((String)session.getAttribute("username"))) {
            response.sendRedirect("admin.jsp");
            return;
        }
    } catch (Exception e) {
        response.sendRedirect(request.getContextPath() + "/errorpages/error_generic.jsp");
        return;
    }
}

// 5. Global Header and Footer 
String headerText = application.getInitParameter("header");
String footerText = application.getInitParameter("footer");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Panel</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <header>
        <h2><%= headerText %></h2>
    </header>

    <main>

        <div class="card-container">
            <h3>Admin Panel</h3>
            <p style="font-size: 1.1em;">Welcome, <b><%= session.getAttribute("username") %></b></p>
            <p style="margin-top: 0; color: #d47fa6; font-weight: bold;">(ADMIN)</p>
            <br>
            <a href="<%= request.getContextPath() %>/LogoutServlet" style="font-size: 0.9em; color: #a0a0a0;">Logout</a>
        </div>

        <table>
            <tr>
                <th colspan="3" style="text-align: center; font-size: 1.2em; border-bottom: 2px solid #f0cadd;">All Users</th>
            </tr>
            <tr>
                <th>Email</th>
                <th>Password</th>
                <th>Role</th>
            </tr>
            <%
            List<User> users = dao.getAllUsersSorted();
            for (User u : users) {
            %>
            <tr>
                <td><%= u.getEmail() %></td>
                <td><%= u.getPassword() %></td>
                <td><b style="color: #d47fa6;"><%= u.getRole().toUpperCase() %></b></td>
            </tr>
            <% } %>
        </table>

        <form method="post" action="admin.jsp">
            <h3>Add User</h3>
            <input type="hidden" name="action" value="add">
            <input type="text" name="email" placeholder="Email Address" required>
            <input type="text" name="password" placeholder="Password" required>
            <input type="text" name="role" placeholder="Role (e.g., admin or guest)" required>
            <input type="submit" value="Add User">
        </form>

        <form method="post" action="admin.jsp">
            <h3>Update User</h3>
            <input type="hidden" name="action" value="update">
            <input type="text" name="email" placeholder="Target Email Address" required>
            <input type="text" name="password" placeholder="New Password" required>
            <input type="text" name="role" placeholder="New Role" required>
            <input type="submit" value="Update User">
        </form>

        <form method="post" action="admin.jsp">
            <h3>Delete User</h3>
            <input type="hidden" name="action" value="delete">
            <input type="text" name="email" placeholder="Email Address to Delete" required>
            <input type="submit" value="Delete User" style="background-color: #d47fa6;">
        </form>

    </main>

    <footer>
        <p><%= footerText %></p>
    </footer>

</body>
</html>