import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class SubscribeServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
							throws ServletException, IOException {
		
		String email = request.getParameter("email");
		
		
		System.out.println("New subscriber: "+ email);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<h3>Thank you for subscribing,"+ email+ "!</h3>");
		out.println("<a href = 'Login.html'> Back to Login</a>");
	}
	

}
