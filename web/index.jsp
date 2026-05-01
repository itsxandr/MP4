<%-- 
    Document   : index
    Created on : 02 27, 26, 12:19:03 PM
    Author     : georg
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
String header = application.getInitParameter("header");
String footer = application.getInitParameter("footer");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <header>
        <h2><%= header %></h2>
    </header>

    <main>
        <div class="card-container">
            <h3>Account Login</h3>
            
            <% 
                String captchaError = (String) request.getAttribute("captchaError");
                if (captchaError != null) { 
            %>
                <p style="color: red; font-weight: bold; font-size: 0.9em; margin-bottom: 10px;"><%= captchaError %></p>
            <% } %>

            <form action="LoginServlet" method="post" style="box-shadow: none; background: none; padding: 0;">
                
                <input type="text" name="email" placeholder="Email Address" required 
                       value="<%= request.getAttribute("retainedEmail") != null ? request.getAttribute("retainedEmail") : "" %>"><br>
                
                <input type="password" name="password" placeholder="Password" required><br>
                
                <div style="margin: 10px 0;">
                    <img id="captchaImage" src="<%= request.getContextPath() %>/CaptchaServlet" alt="Captcha Image" style="border-radius: 5px; border: 1px solid #f0cadd; margin-bottom: 5px; vertical-align: middle;">
                    
                    <a href="#" onclick="document.getElementById('captchaImage').src = '<%= request.getContextPath() %>/CaptchaServlet?' + new Date().getTime(); return false;" style="font-size: 0.9em; margin-left: 10px; color: #b05c82; text-decoration: none;">[refresh]</a>
                    <br><br>
                    
                    <input type="text" name="user_captcha" placeholder="Enter Captcha above" required>
                </div>
                
                <input type="submit" value="Login">
            </form>
        </div>
    </main>

    <footer>
        <p><%= footer %></p>
    </footer>

</body>
</html>


