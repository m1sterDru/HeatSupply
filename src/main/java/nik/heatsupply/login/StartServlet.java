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

@WebServlet(value="/StartServlet", urlPatterns="/StartServlet")
public class StartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setHeader("Cache-control", "no-cache, no-store");

		JsonObjectBuilder jsn = Json.createObjectBuilder();
		System.out.println("StartServlet");
		HttpSession session = request.getSession(false);
		if(session != null) {
			String user = session.getAttribute("user").toString();
			System.out.println("============ " + user);
			jsn.add("isLogin", "true");
			jsn.add("user", user);
		} else {
			System.out.println("Null");
			jsn.add("isLogin", "false");
		}
		out.println(jsn.build().toString());
		out.close();
	}
}