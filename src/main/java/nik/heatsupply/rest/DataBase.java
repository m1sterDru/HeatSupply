package nik.heatsupply.rest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.mail.MessagingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.dao.DataBaseImpl;
import nik.heatsupply.mail.MailSender;
import nik.heatsupply.socket.model.UserWeb;

/**
 * @author pavel.naduda
 * 1. filesindir - Return all files in lang directory - http://localhost:8080/HeatSupply/dataServer/db/filesInDir
 * 2. recover - Send leter to email - http://localhost:8080/HeatSupply/dataServer/db/recover?params=yourEmailAddress
 */
@Path("/db")
public class DataBase {
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{comand}")
	@GET
	public String getDataById(@PathParam("comand") String comand, 
			@QueryParam("params") String params) throws ParseException {

		String ret = "";
		JsonObjectBuilder j = Json.createObjectBuilder();
		switch (comand.toLowerCase()) {
		case "filesindir":
			JsonArrayBuilder jsn = Json.createArrayBuilder();
			File folder = getFileFromURL("/lang");
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				jsn.add(Json.createObjectBuilder().add("name", file.getName()));
			}
			ret = jsn.build().toString();
			break;
		case "recover":
			try {
				String userData = params;
				String login = userData.contains("@") ? null : userData;
				String mailAddress = userData.contains("@") ? userData : "";
				
				DataBaseImpl dbImpl = new DataBaseImpl();
				UserWeb user = login != null ? dbImpl.getUserByLogin(login) : dbImpl.getUserByEmail(mailAddress);
				if(user != null) {
					mailAddress = user.getEmail();
					MailSender ms = new MailSender();
					Encryptor encr = new Encryptor();
					String emailBody = "<strong>HeatSupply Authentication</strong><hr>" +
							"Your " + (login != null ? "password is <strong>\"" + encr.decrypt(user.getPassword()).trim() : 
														"login is <strong>\"" + user.getLogin()) + "\"</strong><br>" +

							"<br><br><hr><font size=\"0.8em\"><strong>Regards, Pavlo Naduda<br></strong>" +
							"phone: 050 66 22 55 6<br>" +
							"e-mail: naduda.pr@gmail.com (pr@ukreni.com.ua)</font>";
	
					ms.generateAndSendEmail(mailAddress, "HeatSupply Authentication", emailBody, null);
					j.add("result", "success");
				} else j.add("result", "");
			} catch (MessagingException e) {
				j.add("result", "");
			}
			ret = j.build().toString();
			break;
		default: ret = "Get: > Comand <" + comand + "> not found"; break;
		}
		return ret;
	}
	
	private File getFileFromURL(String path) {
		URL url = this.getClass().getClassLoader().getResource(path);
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		}
		return file;
	}
}