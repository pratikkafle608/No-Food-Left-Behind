import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SendWelcomeEmailsServlet")
public class SendWelcomeEmailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection info
    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Connect to DB
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "SELECT User_email FROM Subscriber";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            // Loop through all emails
            int count = 0;
            while (rs.next()) {
                String email = rs.getString("User_email");

                try {
                    // Send email
                    EmailUtility.sendEmail(
                        email,
                        "Welcome to the Food Community!",
                        "Welcome to the food community! We’re glad to have you with us. Together we can make a difference by reducing food waste."
                    );
                    count++;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("❌ Failed to send to: " + email);
                }
            }

            response.setContentType("text/html");
            response.getWriter().println("<h3>✅ Successfully sent " + count + " welcome emails!</h3>");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error sending emails: " + e.getMessage());
        } finally {
            // Clean up JDBC resources
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }
}
