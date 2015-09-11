package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.model.UserWeb;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final int SUCCESS = 0;
	private final int TRY_AGAIN = 1;
	private final int ERROR_PASSWORD = 2;
	private final int WRONG_SERIAL_NUMBER = 3;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		HttpSession session = request.getSession(false);
		if(session == null) {
			response.sendRedirect("index.html");
			return;
		}
		
		Iterator<String> iter = request.getParameterMap().keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			System.out.println(key + " = " + request.getParameter(key));
		}

		String idUserS = request.getParameter("idUser");
		String password = request.getParameter("password");
		String password1 = request.getParameter("password1");
		String name = request.getParameter("name");
		String middleName = request.getParameter("middleName");
		String surName = request.getParameter("surName");
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		String languageId = request.getParameter("languageId");
		
		int idUser = Integer.parseInt(idUserS);

		Encryptor encr = new Encryptor();
		UserWeb u = ConnectDB.getUser(idUser);
		int idLang = 3;
		switch(languageId){
			case "en": idLang = 1; break;
			case "ru": idLang = 2; break;
			case "uk": idLang = 3; break;
		}
		if(encr.decrypt(u.getPassword()).trim().equals(password)) {
			if(ConnectDB.updateUser(idUser, password1.length() == 0 ? password : password1, 
					name, middleName, surName, phone, email, idLang)) {
				sendMessage(response, SUCCESS);
			} else {
				sendMessage(response, TRY_AGAIN);
			}
		} else {
			sendMessage(response, ERROR_PASSWORD);
		}
	}
	
	private void sendMessage(HttpServletResponse response, int messageId) {
		JsonObjectBuilder jsn = Json.createObjectBuilder();
		try(PrintWriter out = response.getWriter();) {
			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			switch(messageId) {
				case TRY_AGAIN: jsn.add("message", "Error. Try again."); break;
				case ERROR_PASSWORD: jsn.add("message", "Password is wrong!"); break;
				case SUCCESS: jsn.add("message", "success"); break;
				case WRONG_SERIAL_NUMBER: jsn.add("message", "Wrong serial number"); break;
			}
			jsn.add("messageId", messageId);

			out.println(jsn.build().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
