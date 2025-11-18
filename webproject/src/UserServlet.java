import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/SubscriberView")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            out.println(getPageHeader("Subscribed Users"));

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT Subscriber_ID, User_email FROM Subscriber ORDER BY Subscriber_ID DESC";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            int count = 0;

            out.println("<div class='container'>");
            out.println("<h2>All Subscribed Users</h2>");

            // CARD GRID
            out.println("<div class='card-container'>");

            while (rs.next()) {
                count++;

                out.println("<div class='card'>");

                out.println("<h3>Subscriber ID: " + rs.getInt("Subscriber_ID") + "</h3>");

                out.println("<p><strong>Email:</strong> " +
                        escapeHtml(rs.getString("User_email")) + "</p>");

                out.println("</div>"); // close card
            }

            out.println("</div>"); // close card-container

            if (count == 0) {
                out.println("<div class='no-data'>");
                out.println("<h3>No Subscribers Found</h3>");
                out.println("<p>No users have subscribed yet.</p>");
                out.println("</div>");
            } else {
                out.println("<div class='summary'>");
                out.println("<p><strong>Total Subscribers: " + count + "</strong></p>");
                out.println("</div>");
            }

            out.println("<div class='actions'>");
            out.println("<a href='subscribe.html'><button>Subscribe New User</button></a>");
            out.println("<a href='MainApp.html'><button>Return to Home</button></a>");
            out.println("<button onclick='window.print()'>Print List</button>");
            out.println("</div>");

            out.println("</div>"); // close container

        } catch (ClassNotFoundException e) {
            showError(out, "Driver Error", "MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            showError(out, "Database Error", "Error reading subscribers: " + e.getMessage());
        } catch (Exception e) {
            showError(out, "Unexpected Error", e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}

            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ----------------- PAGE HEADER -----------------
    private String getPageHeader(String title) {
        return "<!DOCTYPE html>" +
                "<html><head><title>" + title + "</title>" +
                "<style>" +

                "body { font-family: Georgia, serif; background-color:#f9f7f1; color:#1f3d2c; margin:0; padding:0; }" +

                "nav { background-color:#1f3d2c; color:white; display:flex; justify-content:space-between; align-items:center; padding:0.8rem 2rem; }" +
                ".nav-links { display:flex; gap:1.5rem; list-style:none; margin:0; padding:0; }" +
                ".nav-links a { color:white; text-decoration:none; font-size:1rem; }" +
                ".nav-links a:hover { color:#c7e0d0; }" +

                ".container { padding:30px; max-width:1200px; margin:auto; }" +
                ".card-container { display:grid; grid-template-columns:repeat(auto-fill,minmax(280px,1fr)); gap:20px; margin-top:30px; }" +
                ".card { background:white; padding:20px; border-radius:12px; box-shadow:0 2px 8px rgba(0,0,0,0.15); border-left:6px solid #1f3d2c; }" +
                ".no-data { text-align:center; padding:40px; color:#666; font-style:italic; }" +
                ".summary { text-align:center; background:#e8f5e8; padding:15px; border-radius:6px; margin:20px 0; }" +
                ".actions { text-align:center; margin:30px 0; }" +
                "button { background:#1f3d2c; color:white; padding:10px 20px; border:none; border-radius:5px; cursor:pointer; }" +
                "button:hover { background:#305a43; }" +

                "@media print { nav, .actions { display:none; } body { background:white; } }" +

                "</style></head><body>" +

                "<nav>" +
                "<div class='logo' style='display:flex; align-items:center; gap:10px;'>" +
                "<img src='image_source' alt='Logo' style='height:40px; border-radius:6px;'>" +
                "<span style='font-size:1.5rem; font-weight:bold;'>No Food Left Behind</span>" +
                "</div>" +

                "<ul class='nav-links'>" +
                "<li><a href='MainApp.html'>Home</a></li>" +
                "<li><a href='Signup.html'>Register User</a></li>" +
                "<li><a href='UserView'>View Users</a></li>" +
                "<li><a href='SubscriberView'>View Subscribers</a></li>" +
                "<li><a href='AddFood.html'>Add Food</a></li>" +
                "<li><a href='BrowseFood.html'>Browse Food</a></li>" +
                "</ul>" +
                "</nav>";
    }

    private void showError(PrintWriter out, String title, String msg) {
        out.println("<div class='container'><div class='error'>");
        out.println("<h2>" + title + "</h2>");
        out.println("<p>" + msg + "</p>");
        out.println("</div></div>");
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
