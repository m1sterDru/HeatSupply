package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.messages.ProfileMessages.AddOwner;
import nik.heatsupply.socket.messages.ProfileMessages.IAddOwner;
import nik.heatsupply.socket.model.Meter;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(RegisterServlet.class);
	private static final long serialVersionUID = 1L;
	private static final int FIRST_REG_STEP = 1;
	private static final int SECOND_REG_STEP = 2;

	private final int SUCCESS = 0;
	private final int OWNER_NOT_EXIST = 1;
	private final int NOT_REALIZED = 2;
	private final int TRY_AGAIN = 3;
	private final int METER_NOT_EXIST = 4;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try {
			String step = request.getParameter("step");
			String noPay = request.getParameter("noPay");
			String owneraccount = request.getParameter("owneraccount");
			String meterNumber = request.getParameter("meterNumber");
			String meterId = request.getParameter("meterId");
			String password = request.getParameter("password");
			String login = request.getParameter("login");
			String name = request.getParameter("name");
			String middleName = request.getParameter("middleName");
			String surName = request.getParameter("surName");
			String phone = request.getParameter("phone");
			String email = request.getParameter("email");
			String languageId = request.getParameter("languageId");

			if(Integer.parseInt(step) == FIRST_REG_STEP) {
				if(Boolean.parseBoolean(noPay)) {
					new AddOwner(owneraccount, meterNumber, new IAddOwner() {
						@Override
						public void onOwnerNotExist() {
							sendMessage(response, OWNER_NOT_EXIST);
						}

						@Override
						public void onMeterNotExist() {
							sendMessage(response, METER_NOT_EXIST);
						}

						@Override
						public void onSuccess(Meter meter) {
							JsonArrayBuilder jArray = Json.createArrayBuilder();
							jArray.add("id_" + meter.getId());
							jArray.add("name_" + meter.getOwnername());
							sendMessage(response, SUCCESS, jArray);
						}
					}).run();
				} else {
					LOG.info("No realized yet.");
					sendMessage(response, NOT_REALIZED);
				}
			} else if(Integer.parseInt(step) == SECOND_REG_STEP) {
				int idUser = ConnectDB.getMaxUserId();
				int idLang = 3;
				switch(languageId){
					case "en": idLang = 1; break;
					case "ru": idLang = 2; break;
					case "uk": idLang = 3; break;
				}
				
				if(ConnectDB.addUser(idUser, login, password, name, middleName, surName, phone, email,
						idLang, owneraccount, meterId, "")) {
					sendMessage(response, SUCCESS);
				} else {
					sendMessage(response, TRY_AGAIN);
				}
			}
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
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
			case SUCCESS: jsn.add("message", "success"); break;
			case OWNER_NOT_EXIST: jsn.add("message", "Owner account is not exist! (TEST: 3570/0117928"); break;
			case NOT_REALIZED: jsn.add("message", "No realized yet."); break;
			case TRY_AGAIN: jsn.add("message", "Error. Try again."); break;
			case METER_NOT_EXIST: jsn.add("message", "This meter is not exist. Check meter's number please."); break;
			}
			jsn.add("messageId", messageId);
			if(jArray != null) jsn.add("array", jArray);

			out.println(jsn.build().toString());
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
	}
}