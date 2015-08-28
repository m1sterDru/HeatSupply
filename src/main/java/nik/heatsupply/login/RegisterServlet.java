package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.model.Meter;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final int ERROR_LOGIN = 1;
	private final int ERROR_PASSWORD = 2;
	private final int SUCCESS = 3;
	private final int OWNER_NOT_EXIST = 4;
	private final int SELECT_OWNER = 5;
	private final int NEXT_STEP = 6;
	private final int NOT_REALIZED = 7;
	private final int TRY_AGAIN = 8;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try {
			String step = request.getParameter("step");
			String noPay = request.getParameter("noPay");
			String ownersId = request.getParameter("owners");
			String owneraccount = request.getParameter("owneraccount");
			String lastcash = request.getParameter("lastcash");
			String password = request.getParameter("password");
			String login = request.getParameter("login");
			String name = request.getParameter("name");
			String middleName = request.getParameter("middleName");
			String surName = request.getParameter("surName");
			String phone = request.getParameter("phone");
			String email = request.getParameter("email");
			String languageId = request.getParameter("languageId");

			if(Integer.parseInt(step) == 1) {
				if(Boolean.parseBoolean(noPay)) {
					List<Meter> owners = ConnectDB.getOwnerList(owneraccount);
					if(owners.size() == 0) {
						sendMessage(response, OWNER_NOT_EXIST);
					} else {
						JsonArrayBuilder jArray = Json.createArrayBuilder();
						owners.forEach(m -> {
							jArray.add(m.getId() + "_" + m.getSerialnumber() + "_" + m.getOwnername());
						});
						sendMessage(response, SUCCESS, jArray);
					}
				} else {
					System.out.println("No realized yet.");
					sendMessage(response, NOT_REALIZED);
				}
			} else if(Integer.parseInt(step) == 2) {
				String[] owners = ownersId.split(";");
				
				if(owners[0].length() > 0) {
					sendMessage(response, NEXT_STEP);
				} else {
					sendMessage(response, SELECT_OWNER);
				}
			} else if(Integer.parseInt(step) == 3) {
				String[] owners = ownersId.split(";");

				int idUser = ConnectDB.getMaxUserId();
				int idLang = 3;
				switch(languageId){
					case "en": idLang = 1; break;
					case "ru": idLang = 2; break;
					case "uk": idLang = 3; break;
				}
				
				if(ConnectDB.addUser(idUser, login, password, name, middleName, surName, phone, email,
						idLang, owners, owneraccount, lastcash)) {
					sendMessage(response, SUCCESS);
				} else {
					sendMessage(response, TRY_AGAIN);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("#/");
		}
	}
	
	private void sendMessage(HttpServletResponse response, int messageId) {
		sendMessage(response, messageId, null);
	}
	
	private void sendMessage(HttpServletResponse response, int messageId, JsonArrayBuilder jArray) {
		JsonObjectBuilder jsn = Json.createObjectBuilder();
		try(PrintWriter out = response.getWriter();) {
			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			switch(messageId) {
			case ERROR_LOGIN: jsn.add("message", "Change login please!"); break;
			case ERROR_PASSWORD: jsn.add("message", "Passwords are different!"); break;
			case SUCCESS: jsn.add("message", "success"); break;
			case OWNER_NOT_EXIST: jsn.add("message", "Owner account doesn't exist!"); break;
			case SELECT_OWNER: jsn.add("message", "Select owner please..."); break;
			case NEXT_STEP: jsn.add("message", ""); break;
			case NOT_REALIZED: jsn.add("message", "No realized yet."); break;
			case TRY_AGAIN: jsn.add("message", "Error. Try again."); break;
			}
			jsn.add("messageId", messageId);
			if(jArray != null) jsn.add("array", jArray);

			out.println(jsn.build().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}