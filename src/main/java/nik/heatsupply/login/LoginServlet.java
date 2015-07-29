package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int SESSION_TIMIOUT = 30;
	private static final int MAX_LOGIN_TRY = 3;
	private boolean isChecked = false;
	private int logCounter = 0;
	private long lastTryLogin;
	private boolean isLock;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		isLock = session.getAttribute("lock") != null ? Boolean.parseBoolean(session.getAttribute("lock").toString()) : false;
		lastTryLogin = session.getAttribute("lastTryLogin") != null ? 
				Long.parseLong(session.getAttribute("lastTryLogin").toString()) : System.currentTimeMillis();
		if(isLock && System.currentTimeMillis() - lastTryLogin < SESSION_TIMIOUT * 1000) {
			session.setAttribute("logCounter", 0);
			response.setContentType("text/html");
			
			String url = request.getRequestURL().toString();
			url = url.substring(0, url.lastIndexOf("/"));
			PrintWriter out = response.getWriter();
			int time = SESSION_TIMIOUT - (int) ((System.currentTimeMillis() - lastTryLogin) / 1000);
			out.println("<h1 style='color:red;'>Lock</h1>");
			out.println("<a href='" + url + "/login.html'>Try again after " + time + " s</a>");
			out.close();
			return;
		} else {
			session.setAttribute("lock", "false");
		}

		String user = request.getParameter("user");
		String pwd = request.getParameter("pwd");

		isChecked = user.equals("admin") && pwd.equals("qwe");
		String userId = isChecked ? "12" : "0";

		if(isChecked){
			session.setAttribute("user", user);
			session.setAttribute("userId", userId);
			session.setAttribute("login", "true");
			session.setMaxInactiveInterval(SESSION_TIMIOUT);
//			Cookie userName = new Cookie("user", user);
//			userName.setMaxAge(SESSION_TIMIOUT);
//			response.addCookie(userName);
			response.sendRedirect("main.html");
		} else {
			session.setAttribute("login", "false");
			session.setAttribute("lastTryLogin", System.currentTimeMillis());

			if(session.getAttribute("logCounter") == null || session.isNew()) {
				logCounter = 0;
			} else {
				logCounter = Integer.parseInt(session.getAttribute("logCounter").toString());
			}
			session.setAttribute("logCounter", ++logCounter);
			if(logCounter == MAX_LOGIN_TRY){
				session.setAttribute("lock", "true");
			}
			response.sendRedirect("#/login");
		}
		System.out.println(user + " === " + pwd);
	}
}