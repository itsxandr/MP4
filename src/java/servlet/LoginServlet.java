package servlet;

import dao.UserDAO;
import exceptions.AuthenticationException;
import exceptions.NullValueException;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoginServlet extends HttpServlet {

    private String dbDriver, dbURL, dbUser, dbPass;
    
    // 1. Initialize the Logger
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private FileHandler fileHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();

        dbDriver = context.getInitParameter("dbDriver");
        dbURL = context.getInitParameter("dbURL");
        dbUser = context.getInitParameter("dbUser");
        dbPass = context.getInitParameter("dbPass");

        try {
            Class.forName(dbDriver);
            
            // Setup the Log File in the WEB-INF folder
            //String logPath = context.getRealPath("/WEB-INF/logs"); not using currently cause I couldnt find the file location
            String logPath = "C:/Users/georg/OneDrive/Documents/NetBeansProjects/MP3";
            File logDir = new File(logPath);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            fileHandler = new FileHandler(logPath + "/auth.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.info("LoginServlet initialized successfully.");
            
        } catch (ClassNotFoundException e) {
            throw new ServletException("DB Driver not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userCaptcha = request.getParameter("user_captcha");
        
        HttpSession currentSession = request.getSession(false);

        LOGGER.info("Login attempt initiated for email: " + (email != null ? email : "UNKNOWN"));

        try {
           // 2. CAPTCHA VALIDATION & 3-STRIKE LIMIT
            
            Integer captchaAttempts = (currentSession != null) ? (Integer) currentSession.getAttribute("captchaAttempts") : null;
            if (captchaAttempts == null) {
                captchaAttempts = 0;
            }

            // Lockout Check: If they already failed 3 times in the past
            if (captchaAttempts >= 3) {
                LOGGER.warning("Login locked out: Maximum CAPTCHA attempts reached.");
                throw new AuthenticationException("Maximum CAPTCHA attempts (3) exceeded.");
            }

            String sessionCaptcha = (currentSession != null) ? (String) currentSession.getAttribute("captchaText") : null;

            // Validate the Captcha
            if (sessionCaptcha == null || userCaptcha == null || !sessionCaptcha.equals(userCaptcha)) {
                captchaAttempts++;
                if (currentSession != null) {
                    currentSession.setAttribute("captchaAttempts", captchaAttempts);
                }
                
                if (captchaAttempts >= 3) {
                    // Strike 3: Throw the exception and route to the hard error page
                    LOGGER.warning("CAPTCHA validation failed. Maximum attempts (3) reached.");
                    throw new AuthenticationException("Authentication Failed: You have failed the CAPTCHA 3 times.");
                } else {
                    // Strikes 1 & 2: only generate warning
                    LOGGER.warning("CAPTCHA validation failed. Attempt " + captchaAttempts + " of 3.");
                    
                    // Attach an error message to display 
                    request.setAttribute("captchaError", "Invalid CAPTCHA. Attempt " + captchaAttempts + " of 3.");
                    
                    // Retain email throughout login attempts for quality of life
                    request.setAttribute("retainedEmail", email);
                    
                    // Forward them back to the login screen
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                    return; 
                }
            }

            // If CAPTCHA is correct, reset the counter to 0
            if (currentSession != null) {
                currentSession.setAttribute("captchaAttempts", 0);
            }
            LOGGER.info("CAPTCHA validated successfully for email: " + email);

            // 3. Check for empty fields
            if ((email == null || email.isEmpty()) && (password == null || password.isEmpty())) {
                LOGGER.warning("Failed login: Both email and password fields were empty.");
                throw new NullValueException("Both fields empty.");
            }

            // 4. Database Validation & Encryption Check
            UserDAO dao = new UserDAO(dbURL, dbUser, dbPass);
            boolean exists = dao.emailExists(email);

            if (!exists && (password == null || password.isEmpty())) {
                LOGGER.warning("Failed login: Email does not exist (" + email + ")");
                response.sendRedirect(request.getContextPath() + "/errorpages/error_1.jsp");
                return;
            }

            // Get the encryption keys from web.xml
            String encKey = getServletContext().getInitParameter("encryptionKey");
            String cipherAlgo = getServletContext().getInitParameter("cipherAlgorithm");

            if (exists) {
                String storedEncryptedPassword = dao.getEncryptedPassword(email);
                String decryptedPassword = util.SecurityUtil.decrypt(storedEncryptedPassword, encKey, cipherAlgo);

                // Compare the decrypted database value to the users input
                if (!password.equals(decryptedPassword)) {
                    LOGGER.warning("Failed login: Invalid password provided for email (" + email + ")");
                    response.sendRedirect(request.getContextPath() + "/errorpages/error_2.jsp");
                    return;
                }
            }

            if (!exists && password != null && !password.isEmpty()) {
                LOGGER.warning("Failed login: Invalid credentials.");
                throw new AuthenticationException("Invalid credentials.");
            }
            // 5. SUCCESSFUL LOGIN & SESSION FIXATION PROTECTION
            LOGGER.info("Authentication successful for email: " + email);
            String role = dao.getUserRole(email);

            // Destroy the old session entirely to prevent Session attacks
            if (currentSession != null) {
                currentSession.invalidate();
            }
            
            // Create a brand new secure session
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("username", email);
            newSession.setAttribute("role", role);

            LOGGER.info("Secure session created for user: " + email);

            request.getRequestDispatcher("success.jsp").forward(request, response);

        } catch (NullValueException | AuthenticationException e) {
            throw new ServletException(e);
        } catch (Exception e) {
            LOGGER.severe("System Error during login: " + e.getMessage());
            throw new ServletException(e);
        }
    }
}