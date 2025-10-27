import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class UnsubscribeServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
							throws ServletException, IOException {

		String email = request.getParameter("email");


		System.out.println("Unsubscribed: "+ email);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<h3>We're sorry to see you go,"+ email+ "!</h3>");
		out.println("<a href = 'Login.html'> Back to Login</a>");

}
}
