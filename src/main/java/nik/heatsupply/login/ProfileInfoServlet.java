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
import javax.servlet.http.HttpSession;

import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.model.Meter;
import nik.heatsupply.socket.model.MeterUser;
import nik.heatsupply.socket.model.UserWeb;

@WebServlet("/ProfileInfoServlet")
public class ProfileInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try (PrintWriter out = response.getWriter();) {
			JsonObjectBuilder jsn = Json.createObjectBuilder();
			HttpSession session = request.getSession(false);

			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			if(session != null && Boolean.parseBoolean(session.getAttribute("login").toString())) {
				String userId = session.getAttribute("userId").toString();
				UserWeb u = ConnectDB.getUser(Integer.parseInt(userId));
				if(u != null) {
					jsn.add("name", notNull(u.getName()))
					 .add("middleName", notNull(u.getMiddlename()))
					 .add("surName", notNull(u.getSurname()))
					 .add("email", notNull(u.getEmail()))
					 .add("phone", notNull(u.getPhone()));
					
					List<MeterUser> actOwners = ConnectDB.getMetersList(u.getId());
					String owneraccount = actOwners.get(0).getIdAccount();
					List<Meter> owners = ConnectDB.getOwnerList(owneraccount);
					JsonArrayBuilder jArray = Json.createArrayBuilder();
					owners.forEach(m -> {
						jArray.add(m.getId() + "_" + m.getSerialnumber() + "_" + m.getOwnername());
					});
					jsn.add("owners", jArray);
					
					JsonArrayBuilder jArray2 = Json.createArrayBuilder();
					actOwners.forEach(m -> {
						jArray2.add(m.getIdmeter());
					});
					jsn.add("actowners", jArray2);
				}
			} else {
				jsn.add("loginBad", "");
			}
			out.println(jsn.build().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String notNull(String s) {
		if(s == null) return ""; else return s;
	}
}
