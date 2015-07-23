package nik.heatsupply.login;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter extends AHttpFilter {
	@Override
	void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String uri = request.getRequestURI();
		if (uri.contains("/dataServer/")) {
			chain.doFilter(request, response);
			return;
		}

		HttpSession session = request.getSession(false);
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with, sid, mycustom, smuser");
		
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.

		if(session == null && (uri.endsWith("main.html") || uri.endsWith("profile.html"))) {
			System.out.println("===============RRRRRRRRRRRRRRRR    " + uri);
			response.sendRedirect("login.html");
		} else {
			chain.doFilter(request, response);
		}
	}
}