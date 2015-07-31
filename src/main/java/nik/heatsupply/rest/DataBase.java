package nik.heatsupply.rest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/db")
public class DataBase {
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{comand}")
	@GET
	public String getDataById(@PathParam("comand") String comand, 
			@QueryParam("params") String params) throws ParseException {

		String ret = "";
		switch (comand.toLowerCase()) {
		case "filesindir":
			String path = "webapp";
			System.out.println(path);
			JsonArrayBuilder jsn = Json.createArrayBuilder();
//			File folder = new File(path);
//			System.out.println(folder.list());
//			for(String s : folder.list()){
//				System.out.println(s);
//			}
			File folder = getFileFromURL("/lang");
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				System.out.println(file.getName());
			}
//			File[] listOfFiles = folder.listFiles();
//				for (int i = 0; i < listOfFiles.length; i++) {
//					if (listOfFiles[i].isFile()) {
//						System.out.println("File " + listOfFiles[i].getName());
//						JsonObjectBuilder j = Json.createObjectBuilder();
//						j.add("name", listOfFiles[i].getName());
//						jsn.add(j);
//					} else if (listOfFiles[i].isDirectory()) {
//						System.out.println("Directory " + listOfFiles[i].getName());
//					}
//				}
			ret = jsn.build().toString();
			break;
		case "currentuser":
			JsonObjectBuilder j = Json.createObjectBuilder();
			String idUser = params;
			j.add("user", "admin_" + idUser)
			 .add("password", "password")
			 .add("email", "qqq@gmail.com");
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
