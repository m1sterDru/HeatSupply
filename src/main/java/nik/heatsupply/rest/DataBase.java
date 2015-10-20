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

import nik.heatsupply.mail.MailSender;

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
		case "currentuser":
			String idUser = params;
			j.add("user", "admin_" + idUser)
			 .add("password", "password")
			 .add("email", "qqq@gmail.com");
			ret = j.build().toString();
			break;
		case "recover":
			String mailAddress = params;
			MailSender ms = new MailSender();
			String emailBody = "<strong>mail.properties</strong><hr>" +
								"mail.smtp.port=587<br>" +
								"mail.smtp.auth=true<br>" +
								"mail.smtp.starttls.enable=true<br>" +
								"mail.address=n*****r@gmail.com<br>" +
								"mail.password=b************k" + 
								"<br><br><hr><font size=\"0.8em\"><strong>Regards, Pavlo Naduda<br></strong>" +
								"phone: 050 66 22 55 6<br>" +
								"e-mail: naduda.pr@gmail.com (pr@ukreni.com.ua)</font>";
			try {
				ms.generateAndSendEmail(mailAddress, "Properties", emailBody, null);
				j.add("mail", mailAddress);
			} catch (MessagingException e) {
				j.add("mail", "");
			}
			j.add("mail", mailAddress);
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
