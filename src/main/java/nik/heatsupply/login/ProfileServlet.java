package nik.heatsupply.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("user");
		String email = request.getParameter("email");

		System.out.println("ProfileServlet");
		HttpSession session = request.getSession(false);
		if(session != null) {
			System.out.println("New User = " + user + "; new E-mail = " + email);
			response.sendRedirect("main.html");
		} else {
			response.sendRedirect("index.html");
		}
	}
}
