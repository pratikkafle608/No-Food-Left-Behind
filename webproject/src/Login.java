import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials — same as Restaurant.java
    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("userName");
        String password = request.getParameter("password");

        if (username == null || password == null ||
            username.isEmpty() || password.isEmpty()) {

            response.sendRedirect("Login.html?error=1");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Query for login
            String sql = "SELECT * FROM Restaurant WHERE User_name=? AND Password=?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());

            rs = stmt.executeQuery();

            if (rs.next()) {
                // Login successful → redirect to AddFood.html
                response.sendRedirect("AddFood.html");
            } else {
                // Login failed → redirect back to login with error
                response.sendRedirect("Login.html?error=1");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect("Login.html?error=1");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignore) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignore) {}
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("Login.html");
    }
}