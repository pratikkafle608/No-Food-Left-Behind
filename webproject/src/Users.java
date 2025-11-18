import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/Users")
public class Users extends HttpServlet {
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

		if (email == null || email.trim().isEmpty()) {
			showError(out, "Invalid Input", "Email address is required.");
			return;
		}

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			out.println(getPageHeader("Subscription Success"));

			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

			String sql = "INSERT INTO Subscriber (User_email) VALUES (?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, email.trim());

			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				out.println("<div class='container'>");
				out.println("<h2>Thank You for Subscribing!</h2>");
				out.println("<p>You have successfully subscribed with the email: <strong>" + escapeHtml(email)
						+ "</strong></p>");
				out.println("<p>You will receive updates and discounts soon.</p>");
				out.println("<div class='actions'>");
				out.println("<a href='MainApp.html'><button>Return to Home</button></a>");
				out.println("</div>");
				out.println("</div>");
			} else {
				showError(out, "Subscription Failed", "Unable to subscribe. Please try again.");
			}

		} catch (ClassNotFoundException e) {
			showError(out, "Driver Error", "MySQL Driver not found: " + e.getMessage());
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) { // Duplicate entry error code for MySQL
				showError(out, "Already Subscribed", "This email is already subscribed.");
			} else {
				showError(out, "Database Error", "Error subscribing: " + e.getMessage());
			}
		} catch (Exception e) {
			showError(out, "Unexpected Error", e.getMessage());
		} finally {
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
		// Redirect GET requests to the subscribe page or handle appropriately
		response.sendRedirect("subscribe.html");
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
		out.println("<a href='subscribe.html'><button>Try Again</button></a>");
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
