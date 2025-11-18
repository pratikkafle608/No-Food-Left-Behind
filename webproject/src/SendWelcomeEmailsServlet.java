import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SendWelcomeEmails")
public class SendWelcomeEmailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<html><body>");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Connect to DB using credentials from EmailUtility
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(EmailUtility.DB_URL, EmailUtility.DB_USER, EmailUtility.DB_PASSWORD);

            String query = "SELECT Subscriber_ID, User_email FROM Subscriber";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            // Loop through all emails
            int successCount = 0;
            int failCount = 0;
            while (rs.next()) {
                String email = rs.getString("User_email");
                int id = rs.getInt("Subscriber_ID");

                try {
                    // Send welcome email using EmailUtility
                    EmailUtility.sendEmail(
                        email,
                        "Welcome to No Food Left Behind!",
                        "Dear Subscriber,\n\nWelcome to the No Food Left Behind community! We're excited to have you join us in our mission to reduce food waste. You'll receive updates on available food donations, tips, and special offers.\n\nStay connected and make a difference!\n\nBest regards,\nThe No Food Left Behind Team"
                    );
                    successCount++;
                    response.getWriter().println("<p>✅ Email sent to: " + email + " (ID: " + id + ")</p>");
                } catch (Exception e) {
                    failCount++;
                    response.getWriter().println("<p>❌ Failed to send to: " + email + " (ID: " + id + ") - " + e.getMessage() + "</p>");
                    e.printStackTrace();
                }
            }

            response.getWriter().println("<h3>Summary: " + successCount + " emails sent successfully, " + failCount + " failed.</h3>");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h3>❌ Error: " + e.getMessage() + "</h3>");
        } finally {
            // Clean up JDBC resources
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
            response.getWriter().println("</body></html>");
        }
    }
}