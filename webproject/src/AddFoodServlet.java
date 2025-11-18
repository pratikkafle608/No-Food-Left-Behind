import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/AddFoodServlet")
public class AddFoodServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println(getPageHeader("Food Items"));

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // FIX: Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT Food_ID, Restaurant_ID, Food_Name, Discount, Pickup_Time FROM Food ORDER BY Food_ID DESC";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            out.println("<div class='container'>");
            out.println("<h2>Available Food Items</h2>");

            if (!rs.isBeforeFirst()) {
                out.println("<p>No food items available.</p>");
            } else {
                out.println("<div class='food-grid'>");
                while (rs.next()) {
                    out.println("<div class='food-box'>");
                    out.println("<div class='field'><span class='field-label'>ID:</span>" + rs.getInt("Food_ID") + "</div>");
                    out.println("<div class='field'><span class='field-label'>Restaurant ID:</span>" + rs.getInt("Restaurant_ID") + "</div>");
                    out.println("<div class='field'><span class='field-label'>Name:</span>" + escapeHtml(rs.getString("Food_Name")) + "</div>");
                    out.println("<div class='field'><span class='field-label'>Discount:</span>" + escapeHtml(rs.getString("Discount")) + "</div>");

                    String pickupTime = rs.getString("Pickup_Time");
                    if (pickupTime != null) {
                        out.println("<div class='field'><span class='field-label'>Pickup Time:</span>" + escapeHtml(pickupTime) + "</div>");
                    }
                    out.println("</div>");
                }
                out.println("</div>");
            }
            out.println("</div>");

        } catch (ClassNotFoundException e) {
            out.println("<div class='container'><p>Driver Error: MySQL Driver not found.</p></div>");
            e.printStackTrace();
        } catch (SQLException e) {
            out.println("<div class='container'><p>Database Error: " + e.getMessage() + "</p></div>");
            e.printStackTrace();
        } catch (Exception e) {
            out.println("<div class='container'><p>Unexpected Error: " + e.getMessage() + "</p></div>");
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }

        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String getPageHeader(String title) {
        return "<!DOCTYPE html>" +
               "<html><head><title>" + title + "</title>" +
               "<style>" +
               "body { font-family: Georgia, serif; background-color:#f9f7f1; color:#1f3d2c; margin:0; padding:0; }" +
               "nav { background-color:#1f3d2c; color:white; display:flex; justify-content:space-between; align-items:center; padding:0.8rem 2rem; }" +
               ".nav-links { display:flex; gap:1.5rem; list-style:none; margin:0; padding:0; }" +
               ".nav-links a { color:white; text-decoration:none; font-size:1rem; }" +
               ".nav-links a:hover { color:#c7e0d0; }" +
               ".container { padding:30px; max-width:1200px; margin:0 auto; }" +
               "h2 { text-align:center; margin-bottom:30px; font-weight:bold; }" +
               ".food-grid { display:grid; grid-template-columns:repeat(3, 1fr); gap:20px; }" +
               ".food-box { background-color:white; border-radius:12px; box-shadow:0 3px 8px rgba(0,0,0,0.1); padding:20px; display:flex; flex-direction:column; gap:6px; transition:box-shadow 0.3s ease; }" +
               ".food-box:hover { box-shadow:0 6px 15px rgba(0,0,0,0.15); }" +
               ".field-label { font-weight:700; margin-right:8px; color:#1f3d2c; min-width:100px; display:inline-block; }" +
               ".field { display:flex; align-items:center; font-size:14.5px; color:#2f4f3f; }" +
               "@media (max-width:900px) { .food-grid { grid-template-columns:repeat(2, 1fr); } }" +
               "@media (max-width:600px) { .food-grid { grid-template-columns:1fr; } }" +
               "</style></head><body>" +
               "<nav>" +
               "<div class='logo' style='display:flex; align-items:center; gap:10px;'>" +
               "<img src='image_source' alt='Logo' style='height:40px; border-radius:6px;'>" +
               "<span style='font-size:1.5rem; font-weight:bold;'>No Food Left Behind</span>" +
               "</div>" +
               "<ul class='nav-links'>" +
               "<li><a href='MainApp.html'>Home</a></li>" +
               "<li><a href='subscribe.html'>subscribe</a></li>" +
               "<li><a href='Signup.html'>Sign Up</a></li>" +
               "<li><a href='SubscriberView'>Subscribers</a></li>" +
               "<li><a href='AddFood.html'>Add Food</a></li>" +
               "<li><a href='AddFoodServlet'>Browse Food</a></li>" +
               "</ul>" +
               "</nav>";
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}