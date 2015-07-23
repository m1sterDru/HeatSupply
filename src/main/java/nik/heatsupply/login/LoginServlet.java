package nik.heatsupply.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int FIVE_MINUTES = 5 * 60;
	private boolean isChecked = false;
	
	public LoginServlet() {
		System.out.println("=============== LoginServlet initialized ===============");
	}
	 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("user");
		String pwd = request.getParameter("pwd");

		isChecked = user.equals("admin") && pwd.equals("qwe");
		String userId = isChecked ? "12" : "0";
		if(isChecked){
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			session.setAttribute("userId", userId);
			session.setMaxInactiveInterval(FIVE_MINUTES);
			Cookie userName = new Cookie("user", user);
			userName.setMaxAge(FIVE_MINUTES);
			response.addCookie(userName);

			response.sendRedirect("main.html");
		} else {
			response.sendRedirect("login.html");
		}
		System.out.println(user + " === " + pwd);
	}
}