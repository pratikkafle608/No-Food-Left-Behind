import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/AddFood")
public class AddFood extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.getWriter().println("<html><body><h2>Please use the Add Food form to submit data.</h2><a href='AddFood.html'>Go back to form</a></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract form parameters
        String restaurantIdStr = request.getParameter("restaurantId");
        String foodName = request.getParameter("foodName");
        String discount = request.getParameter("discount");
        String pickupTime = request.getParameter("pickTime");

        // Debug output
        System.out.println("=== ADD FOOD REQUEST ===");
        System.out.println("Restaurant ID: " + restaurantIdStr);
        System.out.println("Food Name: " + foodName);
        System.out.println("Discount: " + discount);
        System.out.println("Pickup Time: " + pickupTime);

        Integer restaurantId = null;
        if (restaurantIdStr != null && !restaurantIdStr.trim().isEmpty()) {
            try {
                restaurantId = Integer.parseInt(restaurantIdStr);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Restaurant ID");
                return;
            }
        }

        // Insert into database
        String sql = "INSERT INTO Food (Restaurant_ID, Food_Name, Discount, Pickup_Time) VALUES (?, ?, ?, ?)";
        
        try {
            // FIX: Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                System.out.println("Database connection established");
                
                if (restaurantId != null) {
                    stmt.setInt(1, restaurantId);
                } else {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                }
                stmt.setString(2, foodName);
                stmt.setString(3, discount);
                if (pickupTime != null && !pickupTime.trim().isEmpty()) {
                    stmt.setString(4, pickupTime);
                } else {
                    stmt.setNull(4, java.sql.Types.CHAR);
                }
                
                int rowsAffected = stmt.executeUpdate();
                System.out.println("Rows affected: " + rowsAffected);
                
                response.sendRedirect("AddFood.html?message=Food%20added%20successfully");
                
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding food: " + e.getMessage());
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database driver not available");
        }
    }
}