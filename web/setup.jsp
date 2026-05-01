<%@page import="dao.UserDAO, util.SecurityUtil"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    // Fetching configuration from web.xml as per FR-11 
    String dbURL = application.getInitParameter("dbURL");
    String dbUser = application.getInitParameter("dbUser");
    String dbPass = application.getInitParameter("dbPass");
    String encKey = application.getInitParameter("encryptionKey");
    String cipherAlgo = application.getInitParameter("cipherAlgorithm");

    try {
        String defaultEmail = "admin@system.com";
        String defaultPassword = "admin";
        
        // Encrypting the password before storage as per FR-04 
        String encryptedPassword = SecurityUtil.encrypt(defaultPassword, encKey, cipherAlgo);

        UserDAO dao = new UserDAO(dbURL, dbUser, dbPass);
        
        // Ensures a clean start for the admin account
        dao.deleteUser(defaultEmail); 
        
        // Adding the record with the 'admin' role as per FR-15 [cite: 99]
        dao.addUser(defaultEmail, encryptedPassword, "admin");

        out.println("<h2 style='color: green;'>Success!</h2>");
        out.println("<p>The encrypted Admin user has been created.</p>");
        out.println("<p><b>Email:</b> " + defaultEmail + "</p>");
        out.println("<p><b>Password:</b> " + defaultPassword + "</p>");
        out.println("<br><p style='color: red;'><b>REMINDER:</b> Delete this file after use!</p>");
        out.println("<br><a href='index.jsp'>Return to Login Page</a>");

    } catch (Exception e) {
        out.println("<h2 style='color: red;'>Setup Error</h2>");
        out.println("<p>" + e.getMessage() + "</p>");
        e.printStackTrace();
    }
%>