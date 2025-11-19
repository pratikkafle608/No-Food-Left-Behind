import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Restaurant")
public class Restaurant extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials db url in ec2, db user and password
    private static final String DB_URL = "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(getStyledHeader("Access Denied"));
        out.println("<div class='error'>");
        out.println("<h2>Access Denied</h2>");
        out.println("<p>Please use the restaurant registration form to submit your information.</p>");
        out.println("</div>");
        out.println("<a href='Signup.html'><button>Go to Registration Form</button></a>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Retrieve information from parameters from the form from UI called signup.html
        String imageSource = request.getParameter("imageSource");        
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String addressState = request.getParameter("addressState");
        String addressStreet = request.getParameter("addressStreet");
        String addressZIP = request.getParameter("addressZIP");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
  


        // Print received parameters for the users to see illustrate the backend
        System.out.println("=== REGISTRATION ATTEMPT ===");
        
        System.out.println("Image: " + imageSource);
        System.out.println("Username: " + userName);
        System.out.println("Password: " + password);
        System.out.println("State: " + addressState);
        System.out.println("Street: " + addressStreet);
        System.out.println("ZIP: " + addressZIP);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);

        // Enhanced validation based on table constraints
        if (!validateInput(userName, password, addressState, addressZIP, email, phone, response)) {
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            out.println(getStyledHeader("Registration Status"));

            // Load MySQL JDBC driver 
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");

            // Establish connection to verify the connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established");

            // SQL insert query that matches exact table structure and its attributes
            String sql = "INSERT INTO Restaurant (User_name, Password, Address_State, Address_Street_Line, Address_ZIP, Email_address, Phone, image_source) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            // Set parameters according to table structure for username, passwrd and address street, state and zip-code
            stmt.setString(1, userName.trim()); // User_name varchar(20) NOT NULL
            stmt.setString(2, password.trim()); // Password varchar(20) NOT NULL
            stmt.setString(3, addressState.trim().toUpperCase()); // Address_State varchar(2) NOT NULL
            // Address_Street_Line char(25) NULL - handle null and trim to 25 chars
            if (addressStreet != null && !addressStreet.trim().isEmpty()) {
                String street = addressStreet.trim();
                if (street.length() > 25) {
                    street = street.substring(0, 25); // Trim to 25 characters for char(25)
                }
                stmt.setString(4, street);
            } else {
                stmt.setNull(4, java.sql.Types.CHAR);
            }
            stmt.setString(5, addressZIP.trim()); // Address_ZIP varchar(10) NOT NULL
            stmt.setString(6, email.trim()); // Email_address varchar(25) NOT NULL
            stmt.setString(7, phone.trim()); // Phone varchar(12) NOT NULL
            
            if (imageSource != null && !imageSource.trim().isEmpty()) {
                stmt.setString(8, imageSource.trim());
            } else {
                stmt.setNull(8, java.sql.Types.VARCHAR);
            }

            System.out.println("Executing SQL: " + sql);
            System.out.println("With parameters: [" + userName + ", " + password + ", " + addressState + ", " + addressStreet + ", " + addressZIP + ", " + email + ", " + phone + "]");

            // Execute insert
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                // Success - get the auto-generated Restaurant_ID
                try {
                    var keys = stmt.getGeneratedKeys();
                    if (keys != null && keys.next()) {
                        int restaurantId = keys.getInt(1);
                        System.out.println("Auto-generated Restaurant_ID: " + restaurantId);
                    }
                } catch (SQLException e) {
                    System.out.println("Could not retrieve auto-generated ID: " + e.getMessage());
                }

                // Success message
                out.println("<div class='success'>");
                out.println("<h2>Registration Successful! ✅</h2>");
                out.println("<p>Your restaurant has been successfully registered in our database.</p>");
                out.println("<div style='background: #f0f9f0; padding: 15px; border-radius: 5px; margin: 15px 0; text-align: left; display: inline-block;'>");
                out.println("<h3>Registered Details:</h3>");
                out.println("<table style='margin: 0 auto;'>");
                out.println("<tr><td><strong>Username:</strong></td><td>" + userName + "</td></tr>");
                out.println("<tr><td><strong>State:</strong></td><td>" + addressState.toUpperCase() + "</td></tr>");
                out.println("<tr><td><strong>ZIP Code:</strong></td><td>" + addressZIP + "</td></tr>");
                if (addressStreet != null && !addressStreet.trim().isEmpty()) {
                    out.println("<tr><td><strong>Street:</strong></td><td>" + addressStreet + "</td></tr>");
                }
                out.println("<tr><td><strong>Email:</strong></td><td>" + email + "</td></tr>");
                out.println("<tr><td><strong>Phone:</strong></td><td>" + phone + "</td></tr>");
                
                if (imageSource != null && !imageSource.trim().isEmpty()) {
                    out.println("<tr><td><strong>Image:</strong></td><td><img src='" + imageSource + "' alt='Restaurant Image' style='max-width: 100px; max-height: 100px; border-radius: 5px;'></td></tr>");
                }
                
                out.println("</table>");
                out.println("</div>");
                out.println("</div>");
                
                out.println("<div style='margin-top: 20px;'>");
                out.println("<a href='SignupSearch.html'><button>View All Restaurants</button></a>");
                out.println("<a href='Signup.html'><button>Register Another Restaurant</button></a>");
                out.println("<a href='MainApp.html'><button>Return to Home</button></a>");
                out.println("</div>");

                System.out.println("=== REGISTRATION SUCCESS ===");

            } else {
                showError(out, "Registration Failed", "No rows were inserted. Please try again.");
                System.out.println("=== REGISTRATION FAILED - No rows affected ===");
            }

        } catch (ClassNotFoundException e) {
            showError(out, "Database Error", "MySQL JDBC Driver not found. Please check server configuration.");
            System.out.println("=== DRIVER ERROR: " + e.getMessage() + " ===");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("=== SQL ERROR: " + e.getMessage() + " ===");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error
                showError(out, "Registration Failed", "Username or email already exists. Please choose different credentials.");
            } else if (e.getErrorCode() == 0) { // Connection error
                showError(out, "Database Connection Error", "Cannot connect to database. Please try again later.");
            } else {
                showError(out, "Database Error", "Error: " + e.getMessage() + " (Code: " + e.getErrorCode() + ")");
            }
            e.printStackTrace();
        } catch (Exception e) {
            showError(out, "Unexpected Error", "An unexpected error occurred: " + e.getMessage());
            System.out.println("=== UNEXPECTED ERROR: " + e.getMessage() + " ===");
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
            out.println("</body></html>");
        }
    }

    private boolean validateInput(String userName, String password, String addressState, 
                                 String addressZIP, String email, String phone, 
                                 HttpServletResponse response) throws IOException {
        
        if (userName == null || userName.trim().isEmpty()) {
            sendValidationError(response, "User name is required.");
            return false;
        }
        if (userName.trim().length() > 20) {
            sendValidationError(response, "User name must be 20 characters or less.");
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            sendValidationError(response, "Password is required.");
            return false;
        }
        if (password.trim().length() > 20) {
            sendValidationError(response, "Password must be 20 characters or less.");
            return false;
        }
        
        if (addressState == null || addressState.trim().isEmpty()) {
            sendValidationError(response, "State is required.");
            return false;
        }
        if (addressState.trim().length() != 2) {
            sendValidationError(response, "State must be exactly 2 characters (e.g., TX).");
            return false;
        }
        
        if (addressZIP == null || addressZIP.trim().isEmpty()) {
            sendValidationError(response, "ZIP code is required.");
            return false;
        }
        if (addressZIP.trim().length() > 10) {
            sendValidationError(response, "ZIP code must be 10 characters or less.");
            return false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            sendValidationError(response, "Email address is required.");
            return false;
        }
        if (email.trim().length() > 25) {
            sendValidationError(response, "Email address must be 25 characters or less.");
            return false;
        }
        if (!email.contains("@")) {
            sendValidationError(response, "Please enter a valid email address.");
            return false;
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            sendValidationError(response, "Phone number is required.");
            return false;
        }
        if (phone.trim().length() > 12) {
            sendValidationError(response, "Phone number must be 12 characters or less.");
            return false;
        }
        
        return true;
    }

    private void sendValidationError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(getStyledHeader("Validation Error"));
        out.println("<div class='error'>");
        out.println("<h2>Invalid Input</h2>");
        out.println("<p>" + message + "</p>");
        out.println("</div>");
        out.println("<a href='Signup.html'><button>Go Back to Form</button></a>");
        out.println("</body></html>");
    }

    private void showError(PrintWriter out, String title, String message) {
        out.println("<div class='error'>");
        out.println("<h2>" + title + "</h2>");
        out.println("<p>" + message + "</p>");
        out.println("</div>");
        out.println("<div style='margin-top: 20px;'>");
        out.println("<a href='Signup.html'><button>Try Again</button></a>");
        out.println("<a href='MainApp.html'><button>Return to Home</button></a>");
        out.println("</div>");
    }

    private String getStyledHeader(String title) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<title>" + title + "</title>" +
               "<style>" +
               "body { font-family: Georgia, serif; background-color: #f9f7f1; color: #1f3d2c; text-align: center; padding: 50px; }" +
               "nav { background-color: #1f3d2c; color: white; padding: 1rem; margin-bottom: 30px; }" +
               ".nav-links { display: flex; gap: 1.5rem; list-style: none; justify-content: center; padding: 0; }" +
               ".nav-links a { color: white; text-decoration: none; }" +
               ".success { background-color: #d4edda; color: #155724; padding: 20px; border-radius: 10px; display: inline-block; margin: 20px; border: 1px solid #c3e6cb; }" +
               ".error { background-color: #f8d7da; color: #721c24; padding: 20px; border-radius: 10px; display: inline-block; margin: 20px; border: 1px solid #f5c6cb; }" +
               "button { background-color: #1f3d2c; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; margin: 10px; }" +
               "button:hover { background-color: #305a43; }" +
               "table { border-collapse: collapse; }" +
               "td { padding: 5px 10px; text-align: left; }" +
               "tr td:first-child { font-weight: bold; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<nav>" +
               "<div class='logo' style='font-size: 1.5rem; font-weight: bold; margin-bottom: 10px;'>No Food Left Behind</div>" +
               "<ul class='nav-links'>" +
               "<li><a href='MainApp.html'>Home</a></li>" +
               "<li><a href='Signup.html'>Register Restaurant</a></li>" +
               "<li><a href='RestaurantView'>View Restaurants</a></li>" +
               "</ul>" +
               "</nav>";
    }
}