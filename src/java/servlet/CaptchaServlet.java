package servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CaptchaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Get desired length from web.xml (default to 6 if not found)
        int length = 6;
        String lengthParam = getServletContext().getInitParameter("captchaLength");
        if (lengthParam != null) {
            length = Integer.parseInt(lengthParam);
        }

        // 2. Generate the Random String (following sir decs sample codes)
        String chrs = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder captchaStr = new StringBuilder();
        Random rand = new Random();
        while (length-- > 0) {
            int index = rand.nextInt(62);
            captchaStr.append(chrs.charAt(index));
        }

        // 3. Save the Captcha string in the session to check later during login
        HttpSession session = request.getSession();
        session.setAttribute("captchaText", captchaStr.toString());

        // 4. Create an image to display the Captcha visually
        int width = 120;
        int height = 40;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Draw background and text
        g2d.setColor(new Color(253, 246, 248)); // Sakura theme light pink background
        g2d.fillRect(0, 0, width, height);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(176, 92, 130)); // Sakura dark pink text
        g2d.drawString(captchaStr.toString(), 15, 28);
        g2d.dispose();

        // 5. Send the image to the JSP
        response.setContentType("image/png");
        ImageIO.write(bufferedImage, "png", response.getOutputStream());
    }
}
