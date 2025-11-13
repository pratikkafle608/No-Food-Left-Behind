import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/RestaurantView")
public class RestaurantView extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            out.println(getPageHeader("All Registered Restaurants"));

            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // SQL query to get all restaurants
            String sql = "SELECT Restaurant_ID, User_name, Address_State, Address_Street_Line, Address_ZIP, Email_address, Phone FROM Restaurant ORDER BY Restaurant_ID DESC";
            stmt = conn.prepareStatement(sql);
            
            // Execute query
            rs = stmt.executeQuery();
            
            // Process results and build HTML table
            List<Restaurant> restaurants = new ArrayList<>();
            int count = 0;
            
            out.println("<div class='container'>");
            out.println("<h2>All Registered Restaurants</h2>");
            
            // Start table
            out.println("<table class='restaurant-table'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>Username</th>");
            out.println("<th>State</th>");
            out.println("<th>Street Address</th>");
            out.println("<th>ZIP Code</th>");
            out.println("<th>Email</th>");
            out.println("<th>Phone</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            
            while (rs.next()) {
                count++;
                out.println("<tr>");
                out.println("<td>" + rs.getInt("Restaurant_ID") + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("User_name")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("Address_State")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("Address_Street_Line") != null ? rs.getString("Address_Street_Line") : "Not provided") + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("Address_ZIP")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("Email_address")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("Phone")) + "</td>");
                out.println("</tr>");
            }
            
            out.println("</tbody>");
            out.println("</table>");
            
            if (count == 0) {
                out.println("<div class='no-data'>");
                out.println("<h3>No Restaurants Found</h3>");
                out.println("<p>No restaurants have been registered yet.</p>");
                out.println("</div>");
            } else {
                out.println("<div class='summary'>");
                out.println("<p><strong>Total Restaurants Registered: " + count + "</strong></p>");
                out.println("</div>");
            }
            
            out.println("<div class='actions'>");
            out.println("<a href='Signup.html'><button>Register New Restaurant</button></a>");
            out.println("<a href='MainApp.html'><button>Return to Home</button></a>");
            out.println("<button onclick='window.print()'>Print List</button>");
            out.println("</div>");
            
            out.println("</div>"); // Close container

        } catch (ClassNotFoundException e) {
            showError(out, "Database Driver Error", "MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            showError(out, "Database Error", "Unable to connect to database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError(out, "Unexpected Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.println("</body></html>");
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to GET method
        doGet(request, response);
    }
    
    private String getPageHeader(String title) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<title>" + title + "</title>" +
               "<style>" +
               "body { font-family: Georgia, serif; background-color: #f9f7f1; color: #1f3d2c; margin: 0; padding: 0; }" +
               "nav { background-color: #1f3d2c; color: white; display: flex; justify-content: space-between; align-items: center; padding: 0.8rem 2rem; }" +
               ".nav-links { display: flex; gap: 1.5rem; list-style: none; margin: 0; padding: 0; }" +
               ".nav-links a { color: white; text-decoration: none; font-size: 1rem; transition: color 0.3s; }" +
               ".nav-links a:hover { color: #c7e0d0; }" +
               ".container { padding: 30px; max-width: 1200px; margin: 0 auto; }" +
               ".restaurant-table { width: 100%; border-collapse: collapse; margin: 30px 0; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1); border-radius: 10px; overflow: hidden; }" +
               ".restaurant-table th { background-color: #1f3d2c; color: white; padding: 15px; text-align: left; font-weight: bold; }" +
               ".restaurant-table td { padding: 12px 15px; border-bottom: 1px solid #e0e0e0; text-align: left; }" +
               ".restaurant-table tr:hover { background-color: #f5f5f5; }" +
               ".restaurant-table tr:last-child td { border-bottom: none; }" +
               ".no-data { padding: 40px; color: #666; font-style: italic; text-align: center; }" +
               ".summary { text-align: center; margin: 20px 0; padding: 15px; background-color: #e8f5e8; border-radius: 5px; }" +
               ".actions { text-align: center; margin: 30px 0; }" +
               "button { background-color: #1f3d2c; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; margin: 5px; }" +
               "button:hover { background-color: #305a43; }" +
               ".error { background-color: #f8d7da; color: #721c24; padding: 20px; border-radius: 10px; margin: 20px; text-align: center; }" +
               "@media (max-width: 768px) { " +
               "  nav { flex-direction: column; gap: 0.8rem; }" +
               "  .restaurant-table { font-size: 14px; }" +
               "  .restaurant-table th, .restaurant-table td { padding: 8px 10px; }" +
               "}" +
               "@media print { " +
               "  nav, .actions { display: none; }" +
               "  body { background-color: white; }" +
               "  .restaurant-table { box-shadow: none; }" +
               "}" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<nav>" +
               "<div class='logo' style='font-size: 1.5rem; font-weight: bold;'>No Food Left Behind</div>" +
               "<ul class='nav-links'>" +
               "<li><a href='MainApp.html'>Home</a></li>" +
               "<li><a href='Signup.html'>Register Restaurant</a></li>" +
               "<li><a href='RestaurantView'>View Restaurants</a></li>" +
               "<li><a href='AddFood.html'>Add Food</a></li>" +
               "<li><a href='BrowseFood.html'>Browse Food</a></li>" +
               "</ul>" +
               "</nav>";
    }
    
    private void showError(PrintWriter out, String title, String message) {
        out.println("<div class='container'>");
        out.println("<div class='error'>");
        out.println("<h2>" + title + "</h2>");
        out.println("<p>" + message + "</p>");
        out.println("</div>");
        out.println("<div class='actions'>");
        out.println("<a href='RestaurantView'><button>Try Again</button></a>");
        out.println("<a href='MainApp.html'><button>Return to Home</button></a>");
        out.println("</div>");
        out.println("</div>");
    }
    
    // Helper method to escape HTML for security
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
    
    // Restaurant data class (inner class)
    public static class Restaurant {
        private int restaurantId;
        private String userName;
        private String addressState;
        private String addressStreet;
        private String addressZIP;
        private String email;
        private String phone;
        
        // Getters and setters
        public int getRestaurantId() { return restaurantId; }
        public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getAddressState() { return addressState; }
        public void setAddressState(String addressState) { this.addressState = addressState; }
        
        public String getAddressStreet() { return addressStreet; }
        public void setAddressStreet(String addressStreet) { this.addressStreet = addressStreet; }
        
        public String getAddressZIP() { return addressZIP; }
        public void setAddressZIP(String addressZIP) { this.addressZIP = addressZIP; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}