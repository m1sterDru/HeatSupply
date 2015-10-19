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

import nik.heatsupply.socket.Server;
import nik.heatsupply.socket.messages.ProfileMessages;
import nik.heatsupply.socket.messages.ProfileMessages.AddOwner;
import nik.heatsupply.socket.messages.ProfileMessages.IAddOwner;
import nik.heatsupply.socket.model.Meter;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(RegisterServlet.class);
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try {
			String owneraccount = request.getParameter("owneraccount");
			String meterNumber = request.getParameter("meterNumber");
			String password = request.getParameter("password");
			String login = request.getParameter("login");
			String phone = request.getParameter("phone");
			String email = request.getParameter("email");
			String languageId = request.getParameter("languageId");

			new AddOwner(owneraccount, meterNumber, new IAddOwner() {
				@Override
				public void onOwnerNotExist() {
					sendMessage(response, ProfileMessages.OWNER_NOT_EXIST);
				}

				@Override
				public void onMeterNotExist() {
					sendMessage(response, ProfileMessages.METER_NOT_EXIST);
				}

				@Override
				public void onSuccess(Meter meter) {
					int idLang = 3;
					switch(languageId){
						case "en": idLang = 1; break;
						case "ru": idLang = 2; break;
						case "uk": idLang = 3; break;
					}
					
					String result = Server.dbImpl.addUser(login, password, phone, email, idLang, 
							owneraccount, meter.getId() + "", "");
					switch(result) {
						case "0": sendMessage(response, ProfileMessages.SUCCESS); break;
						case "1": sendMessage(response, ProfileMessages.TRY_AGAIN); break;
						default:
							JsonArrayBuilder jArray = Json.createArrayBuilder();
							String emailEnd = result.substring(result.indexOf("@"));
							result = result.substring(0, result.indexOf("@"));
							jArray.add(result.substring(0, 1) + "***" + result.substring(result.length() - 1, result.length()) + emailEnd);
							sendMessage(response, ProfileMessages.USER_EXIST, jArray); 
							break;
					}
				}
			}).run();
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
				case ProfileMessages.SUCCESS: jsn.add("message", "success"); break;
				case ProfileMessages.OWNER_NOT_EXIST: jsn.add("message", "keyOwnerAccountError"); break;
				case ProfileMessages.TRY_AGAIN: jsn.add("message", "keyTryAgain"); break;
				case ProfileMessages.METER_NOT_EXIST: jsn.add("message", "keyMeterNotExist"); break;
				case ProfileMessages.USER_EXIST: jsn.add("message", "kUserExist"); break;
			}
			jsn.add("messageId", messageId);
			if(jArray != null) jsn.add("array", jArray);

			out.println(jsn.build().toString());
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
	}
}