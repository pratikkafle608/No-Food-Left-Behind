import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SubscribeServlet")
public class Users extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    // Handle GET requests (e.g., direct access to the URL)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect to the subscribe form page or display a message
        response.sendRedirect("subscribe.html"); // Adjust if you want to handle differently
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is required");
            return;
        }

        // Note: Subscriber_ID is auto_increment, so we only insert User_email
        // The subscriberId field in the form might be unnecessary for subscription (insert), but if needed for other logic, it can be retrieved later.

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to insert into Subscriber table
            String sql = "INSERT INTO Subscriber (User_email) VALUES (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.trim());

            // Execute the insert
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Success: redirect or send success message
                response.sendRedirect("subscription_success.html"); // Assuming a success page exists
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to subscribe");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}