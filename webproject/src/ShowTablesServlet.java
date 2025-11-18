import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ShowTables")
public class ShowTablesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = 
        "jdbc:mysql://ec2-3-141-40-94.us-east-2.compute.amazonaws.com:3306/no_food_left_behind";
    private static final String DB_USER = "remoteSql";
    private static final String DB_PASSWORD = "remotePassword1";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Database Tables</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; background: #f7f7f7; padding: 20px; }");
        out.println("h1 { color: #13572b; }");
        out.println("table { border-collapse: collapse; width: 300px; background: white; }");
        out.println("td, th { border: 1px solid #ccc; padding: 10px; }");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<h1>Tables in Database: no_food_left_behind</h1>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            DatabaseMetaData meta = conn.getMetaData();

            ResultSet rs = meta.getTables("no_food_left_behind", null, "%", new String[]{"TABLE"});

            out.println("<table>");
            out.println("<tr><th>Table Name</th></tr>");

            boolean hasTable = false;
            while (rs.next()) {
                hasTable = true;
                String tableName = rs.getString("TABLE_NAME");
                out.println("<tr><td>" + tableName + "</td></tr>");
            }

            if (!hasTable) {
                out.println("<tr><td>No tables found.</td></tr>");
            }

            out.println("</table>");

            rs.close();
            conn.close();

        } catch (SQLException | ClassNotFoundException e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        out.println("</body></html>");
    }
}
