package nik.heatsupply.login;

import java.io.PrintWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletMessage {
	private static final Logger LOG = LoggerFactory.getLogger(ServletMessage.class);
	public static final int SUCCESS = 0;
	public static final int TRY_AGAIN = 1;
	public static final int ERROR_PASSWORD = 2;
	public static final int WRONG_SERIAL_NUMBER = 3;
	public static final int LOGIN_BAD = 4;
	public static final int LOGIN_SESSION_TIMEOUT = 5;

	public static void send(HttpServletResponse response, int messageId, String par) {
		JsonObjectBuilder jsn = Json.createObjectBuilder();
		try(PrintWriter out = response.getWriter();) {
			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			switch(messageId) {
				case TRY_AGAIN: jsn.add("message", "keyTryAgain"); break;
				case ERROR_PASSWORD: jsn.add("message", "keyWrongPassword"); break;
				case SUCCESS: jsn.add("message", "success"); break;
				case WRONG_SERIAL_NUMBER: jsn.add("message", "Wrong serial number"); break;
				case LOGIN_BAD: jsn.add("message", "keyBadAuthentication"); break;
				case LOGIN_SESSION_TIMEOUT: jsn.add("message", String.format("Wait %s secconds, please...", par)); break;
			}
			jsn.add("messageId", messageId);

			out.println(jsn.build().toString());
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public static void send(HttpServletResponse response, int messageId) {
		send(response, messageId, "");
	}
}