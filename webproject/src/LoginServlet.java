

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		String username = request.getParameter("username");
		
		String password = request.getParameter("password");
		
		if("Neff".equals(username) && "Password".equals(password)) {
			response.sendRedirect("Welcome.html");
		}
		else {
			response.sendRedirect("text/html");
			response.getWriter().println("<h3> invalid login, try again.</h3>");
			response.getWriter().println("<ahref = 'login.html'> Back to Login</a>");
		}
	}

}
