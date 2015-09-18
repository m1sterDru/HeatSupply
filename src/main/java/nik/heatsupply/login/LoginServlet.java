package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.Server;
import nik.heatsupply.socket.model.UserWeb;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);
	private static final long serialVersionUID = 1L;
	private static final int SESSION_TIMIOUT = 15 * 60;
	private static final int MAX_LOGIN_TRY = 30;

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
			try(PrintWriter out = response.getWriter();){
				int time = SESSION_TIMIOUT - (int) ((System.currentTimeMillis() - lastTryLogin) / 1000);
//				out.println("<h1 style='color:red;'>Lock</h1>");
//				out.println("<a href='" + url + "/login.html'>Try again after " + time + " s</a>");
				ServletMessage.send(response, ServletMessage.LOGIN_SESSION_TIMEOUT, time + "");
			} catch(Exception e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
			}
			return;
		} else {
			session.setAttribute("lock", "false");
		}

		String user = request.getParameter("user");
		String pwd = request.getParameter("pwd");

		if(isChecked(user, pwd, session)){
//			Cookie userName = new Cookie("user", user);
//			userName.setMaxAge(SESSION_TIMIOUT);
//			response.addCookie(userName);
//			response.sendRedirect("main.html");
			ServletMessage.send(response, ServletMessage.SUCCESS);
		} else {
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
			ServletMessage.send(response, ServletMessage.LOGIN_BAD);
//			response.sendRedirect("#/login");
		}
	}
	
	private boolean isChecked(String login, String password, HttpSession session) {
		UserWeb u = ConnectDB.getUser(login);
		if(u == null) return false;
		Encryptor encr = new Encryptor();
		if(encr.decrypt(u.getPassword()).trim().equals(password)) {
			session.setAttribute("user", login);
			session.setAttribute("userId", u.getId());
			session.setMaxInactiveInterval(SESSION_TIMIOUT);
			Server.getSessions().put(session, true);
			return true;
		}
		return false;
	}
}