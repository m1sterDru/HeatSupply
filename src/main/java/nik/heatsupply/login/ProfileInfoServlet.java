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

@WebServlet("/ProfileInfoServlet")
public class ProfileInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try (PrintWriter out = response.getWriter();) {
			JsonObjectBuilder jsn = Json.createObjectBuilder();
			HttpSession session = request.getSession(false);

			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			if(session != null && Boolean.parseBoolean(session.getAttribute("login").toString())) {
				String userId = session.getAttribute("userId").toString();
				jsn.add("user", "admin_" + userId)
				 .add("password", "password")
				 .add("email", "qqq@gmail.com");
			} else {
				jsn.add("loginBad", "");
			}
			out.println(jsn.build().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
