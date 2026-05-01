package servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogoutServlet extends HttpServlet {

    // 1. Initialize the Logger
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());
    private FileHandler fileHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        try {
            // 2. Point to the exact same auth.log file used by LoginServlet
            String logPath = config.getServletContext().getRealPath("/WEB-INF/logs");
            File logDir = new File(logPath);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // overwrite current log and not create dupe
            fileHandler = new FileHandler(logPath + "/auth.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 3. Check the session without creating a new one
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String user = (String) session.getAttribute("username");
            
            // 4. Log the successful logout event
            LOGGER.info("User logged out successfully: " + (user != null ? user : "Unknown"));
            
            // 5. Invalidate the session
            session.invalidate();
        }

        // Redirect back to the login page
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}