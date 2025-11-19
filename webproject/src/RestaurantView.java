import java.io.*;
import java.sql.*;
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
            out.println(getPageHeader("Our Partner Restaurants"));

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT Restaurant_ID, User_name, Address_State, Address_Street_Line, Address_ZIP, Email_address, Phone, image_source FROM Restaurant ORDER BY Restaurant_ID DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            int count = 0;

            out.println("<div class='container'>");
            out.println("<h2>All Registered Restaurants</h2>");

            // CARD CONTAINER
            out.println("<div class='card-container'>");

            while (rs.next()) {
                count++;

                out.println("<div class='card'>");
                
                String img = rs.getString("image_source");
                
                // Debug output
                out.println("<!-- DEBUG: Image path = " + (img != null ? img : "NULL") + " -->");
                System.out.println("Image path from DB for Restaurant " + rs.getInt("Restaurant_ID") + ": " + img);
                
                if (img != null && !img.trim().isEmpty()) {
                    out.println("<img src='" + escapeHtml(img) + "' alt='Restaurant Image' "
                            + "style='width:100%; height:180px; object-fit:cover; border-radius:8px; margin-bottom:10px;' "
                            + "onerror=\"this.style.display='none'; this.nextElementSibling.style.display='flex';\">");
                    
                    // Fallback div
                    out.println("<div style='width:100%; height:180px; background:#ddd; border-radius:8px; "
                            + "margin-bottom:10px; display:none; align-items:center; justify-content:center; color:#555;'>"
                            + "Image Not Available</div>");
                } else {
                    out.println("<div style='width:100%; height:180px; background:#ddd; border-radius:8px; "
                            + "margin-bottom:10px; display:flex; align-items:center; justify-content:center; color:#555;'>"
                            + "No Image</div>");
                }
                

                out.println("<h3>Restaurant ID: " + rs.getInt("Restaurant_ID") + "</h3>");

                out.println("<p><strong>Username:</strong> "
                        + escapeHtml(rs.getString("User_name")) + "</p>");

                out.println("<p><strong>State:</strong> "
                        + escapeHtml(rs.getString("Address_State")) + "</p>");

                out.println("<p><strong>Street Address:</strong> "
                        + escapeHtml(rs.getString("Address_Street_Line") != null
                        ? rs.getString("Address_Street_Line")
                        : "Not Provided") + "</p>");

                out.println("<p><strong>ZIP Code:</strong> "
                        + escapeHtml(rs.getString("Address_ZIP")) + "</p>");

                out.println("<p><strong>Email:</strong> "
                        + escapeHtml(rs.getString("Email_address")) + "</p>");

                out.println("<p><strong>Phone:</strong> "
                        + escapeHtml(rs.getString("Phone")) + "</p>");

                out.println("</div>"); // close card
            }

            out.println("</div>"); // close card-container

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
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}

            out.println("</body></html>");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
                ".card-container { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; margin-top: 30px; }" +
                ".card { background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); border-left: 6px solid #1f3d2c; }" +
                ".card h3 { margin-top: 0; }" +
                ".card p { margin: 6px 0; }" +
                ".no-data { text-align: center; padding: 40px; color: #666; font-style: italic; }" +
                ".summary { text-align: center; margin: 20px 0; padding: 15px; background-color: #e8f5e8; border-radius: 5px; }" +
                ".actions { text-align: center; margin: 30px 0; }" +
                "button { background-color: #1f3d2c; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; margin: 5px; }" +
                "button:hover { background-color: #305a43; }" +

                "@media print { nav, .actions { display: none; } body { background-color: white; } }" +

                "</style>" +
                "</head>" +
                "<body>" +

                "<nav>" +
                "<div class='logo' style='display: flex; align-items: center; gap: 10px;'>" +
                "<img src='image_source' alt='Logo' style='height:40px; border-radius:6px;'>" +
                "<span style='font-size: 1.5rem; font-weight: bold;'>No Food Left Behind</span>" +
                "</div>" +

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
        out.println("</div>");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
