package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.socket.Server;

@WebServlet("/StartServlet")
public class StartServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(StartServlet.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try(PrintWriter out = response.getWriter();) {
			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");
	
			JsonObjectBuilder jsn = Json.createObjectBuilder();
			HttpSession session = request.getSession(false);
	
			if(Server.isValid(session)) {
				String user = session.getAttribute("user").toString();
				String userId = session.getAttribute("userId").toString();
				jsn.add("isLogin", "true")
				   .add("user", user)
				   .add("userId", userId);
			} else {
				jsn.add("isLogin", "false");
			}
			out.println(jsn.build().toString());
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
	}
}