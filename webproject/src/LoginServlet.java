import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showError(out, "Invalid Input", "Email and password are required.");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            out.println(getPageHeader("Login Result"));

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Query: Check Email_address and Password fields in Restaurant table
            String sql = "SELECT Restaurant_ID FROM Restaurant WHERE Email_address = ? AND Password = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.trim());
            stmt.setString(2, password.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Successful login: redirect to AddFood.html only if Email_address and Password match
                response.sendRedirect("AddFood.html");
                return; // Exit after redirect
            } else {
                showError(out, "Authentication Failed", "Sorry, failed to authenticate. Please check your email and password.");
            }

        } catch (ClassNotFoundException e) {
            showError(out, "Driver Error", "MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            showError(out, "Database Error", "Error during login: " + e.getMessage());
        } catch (Exception e) {
            showError(out, "Unexpected Error", e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException ignored) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ignored) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException ignored) {
            }

            out.println("</body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to the login page
        response.sendRedirect("Login.html");
    }

    // ----------------- PAGE HEADER -----------------
    private String getPageHeader(String title) {
        return "<!DOCTYPE html>" + "<html><head><title>" + title + "</title>" + "<style>" +

                "body { font-family: Georgia, serif; background-color:#f9f7f1; color:#1f3d2c; margin:0; padding:0; }" +

                "nav { background-color:#1f3d2c; color:white; display:flex; justify-content:space-between; align-items:center; padding:0.8rem 2rem; }"
                + ".nav-links { display:flex; gap:1.5rem; list-style:none; margin:0; padding:0; }"
                + ".nav-links a { color:white; text-decoration:none; font-size:1rem; }"
                + ".nav-links a:hover { color:#c7e0d0; }" +

                ".container { padding:30px; max-width:1200px; margin:auto; text-align:center; }"
                + ".actions { text-align:center; margin:30px 0; }"
                + "button { background:#1f3d2c; color:white; padding:10px 20px; border:none; border-radius:5px; cursor:pointer; }"
                + "button:hover { background:#305a43; }" +

                "</style></head><body>" +

                "<nav>" + "<div class='logo' style='display:flex; align-items:center; gap:10px;'>"
                + "<img src='image_source' alt='Logo' style='height:40px; border-radius:6px;'>"
                + "<span style='font-size:1.5rem; font-weight:bold;'>No Food Left Behind</span>" + "</div>" +

                "<ul class='nav-links'>" + "<li><a href='MainApp.html'>Home</a></li>"
                + "<li><a href='subscribe.html'>subscribe</a></li>"
                + "<li><a href='Signup.html'>Sign Up</a></li>"
                + "<li><a href='SubscriberView'>Subscribers</a></li>" + "<li><a href='AddFood.html'>Add Food</a></li>"
                + "<li><a href='BrowseFood.html'>Browse Food</a></li>" + "</ul>" + "</nav>";
    }

    private void showError(PrintWriter out, String title, String msg) {
        out.println("<div class='container'><div class='error'>");
        out.println("<h2>" + title + "</h2>");
        out.println("<p>" + msg + "</p>");
        out.println("<div class='actions'>");
        out.println("<a href='Login.html'><button>Try Again</button></a>");
        out.println("<a href='MainApp.html'><button>Return to Home</button></a>");
        out.println("</div>");
        out.println("</div></div>");
    }

    private String escapeHtml(String s) {
        if (s == null)
            return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
                "&#39;");
    }
}