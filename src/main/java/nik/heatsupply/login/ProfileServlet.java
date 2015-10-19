package nik.heatsupply.login;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.socket.Server;
import nik.heatsupply.socket.model.UserWeb;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		HttpSession session = request.getSession(false);
		if(session == null) {
			response.sendRedirect("index.html");
			return;
		}
		
//		Iterator<String> iter = request.getParameterMap().keySet().iterator();
//		while (iter.hasNext()) {
//			String key = (String) iter.next();
//			System.out.println(key + " = " + request.getParameter(key));
//		}

		String idUserS = request.getParameter("idUser");
		String password = request.getParameter("password");
		String password1 = request.getParameter("password1");
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		String languageId = request.getParameter("languageId");
		
		int idUser = Integer.parseInt(idUserS);

		Encryptor encr = new Encryptor();
		UserWeb u = Server.dbImpl.getUser(idUser);
		int idLang = 3;
		switch(languageId){
			case "en": idLang = 1; break;
			case "ru": idLang = 2; break;
			case "uk": idLang = 3; break;
		}
		if(encr.decrypt(u.getPassword()).trim().equals(password)) {
			if(Server.dbImpl.updateUser(idUser, password1.length() == 0 ? password : password1, 
					phone, email, idLang)) {
				ServletMessage.send(response, ServletMessage.SUCCESS);
			} else {
				ServletMessage.send(response, ServletMessage.TRY_AGAIN);
			}
		} else {
			ServletMessage.send(response, ServletMessage.ERROR_PASSWORD);
		}
	}
}
