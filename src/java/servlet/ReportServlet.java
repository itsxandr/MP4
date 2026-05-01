package servlet;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import dao.UserDAO;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;
import util.SecurityUtil;

public class ReportServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReportServlet.class.getName());

    private String dbDriver;
    private String dbURL;
    private String dbUser;
    private String dbPass;

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
        } catch (ClassNotFoundException e) {
            throw new ServletException("DB Driver not found.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null || session.getAttribute("role") == null) {
            response.sendRedirect(request.getContextPath() + "/errorpages/error_session.jsp");
            return;
        }

        String username = String.valueOf(session.getAttribute("username"));
        String role = String.valueOf(session.getAttribute("role"));

        UserDAO dao = new UserDAO(dbURL, dbUser, dbPass);
        String timestampDisplay = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date());
        String timestampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        try {
            if ("admin".equalsIgnoreCase(role)) {
                String filename = "Admin_Report_" + timestampFile + ".pdf";
                writeAdminReport(response, dao, timestampDisplay, filename);
                LOGGER.info("Admin report generated for user: " + username);
            } else if ("guest".equalsIgnoreCase(role)) {
                String filename = "Guest_Report_" + username + "_" + timestampFile + ".pdf";
                writeGuestReport(request, response, dao, username, timestampDisplay, filename);
                LOGGER.info("Guest report generated for user: " + username);
            } else {
                LOGGER.warning("Unauthorized report role access attempt: " + role + " by " + username);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized role.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate report for user: " + username, e);
            throw new ServletException("Unable to generate report.", e);
        }
    }

    private void writeAdminReport(HttpServletResponse response, UserDAO dao, String timestampDisplay, String filename)
            throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        Document document = new Document(PageSize.LETTER, 40, 40, 55, 55);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new FooterEvent());
        document.open();

        addTitle(document, "ADMIN REPORT");
        addTimestamp(document, timestampDisplay);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(8f);
        table.setWidths(new float[]{3f, 2f});

        addHeaderCell(table, "Username");
        addHeaderCell(table, "Role");

        List<User> users = dao.getAllUsersSorted();
        for (User user : users) {
            table.addCell(new PdfPCell(new Phrase(user.getEmail())));
            table.addCell(new PdfPCell(new Phrase(user.getRole())));
        }

        document.add(table);
        document.close();
    }

    private void writeGuestReport(HttpServletRequest request, HttpServletResponse response, UserDAO dao,
            String username, String timestampDisplay, String filename) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        Rectangle guestSize = new Rectangle(420, 595);
        Document document = new Document(guestSize, 30, 30, 55, 55);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new FooterEvent());
        document.open();

        addTitle(document, "GUEST REPORT");
        addTimestamp(document, timestampDisplay);

        User currentUser = dao.getUserByEmail(username);
        if (currentUser == null) {
            throw new ServletException("User record not found for report generation.");
        }

        String encKey = request.getServletContext().getInitParameter("encryptionKey");
        String cipherAlgo = request.getServletContext().getInitParameter("cipherAlgorithm");
        String decryptedPassword = SecurityUtil.decrypt(currentUser.getPassword(), encKey, cipherAlgo);

        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL);
        Paragraph userLine = new Paragraph("Username: " + currentUser.getEmail(), bodyFont);
        userLine.setSpacingBefore(12f);
        document.add(userLine);

        Paragraph passwordLine = new Paragraph("Decrypted Password: " + decryptedPassword, bodyFont);
        passwordLine.setSpacingBefore(10f);
        document.add(passwordLine);

        document.close();
    }

    private void addTitle(Document document, String title) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLDITALIC);
        Paragraph titleParagraph = new Paragraph(title, titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingAfter(10f);
        document.add(titleParagraph);
    }

    private void addTimestamp(Document document, String timestampDisplay) throws DocumentException {
        Font generatedFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Paragraph generatedLine = new Paragraph("Generated on: " + timestampDisplay, generatedFont);
        generatedLine.setSpacingAfter(8f);
        document.add(generatedLine);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private static class FooterEvent extends PdfPageEventHelper {

        private PdfTemplate totalPages;
        private BaseFont baseFont;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPages = writer.getDirectContent().createTemplate(30, 16);
            try {
                baseFont = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            String owner = "Owner: KLINE & MOGRO";
            String pageText = "Page " + writer.getPageNumber() + " of ";

            float y = document.bottom() - 20;
            float leftX = document.left();
            float rightTextWidth = baseFont.getWidthPoint(pageText, 11);
            float rightX = document.right() - rightTextWidth - 20;

            canvas.beginText();
            canvas.setFontAndSize(baseFont, 11);
            canvas.showTextAligned(Element.ALIGN_LEFT, owner, leftX, y, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, pageText, rightX, y, 0);
            canvas.endText();

            canvas.addTemplate(totalPages, rightX + rightTextWidth, y);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            totalPages.beginText();
            totalPages.setFontAndSize(baseFont, 11);
            totalPages.showText(String.valueOf(writer.getPageNumber() - 1));
            totalPages.endText();
        }
    }
}
