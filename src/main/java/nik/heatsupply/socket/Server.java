package nik.heatsupply.socket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.dao.DataBaseImpl;
import nik.heatsupply.socket.messages.CommandMessage;
import nik.heatsupply.socket.messages.Message;
import nik.heatsupply.socket.messages.ProfileMessages;
import nik.heatsupply.socket.messages.ReportMessages;
import nik.heatsupply.socket.messages.coders.MessageDecoder;
import nik.heatsupply.socket.messages.coders.MessageEncoder;
import nik.heatsupply.socket.model.UserWeb;

@ServerEndpoint(value = "/socketServer", configurator = GetHttpSessionConfigurator.class,
encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class Server {
	private static final Logger LOG = LoggerFactory.getLogger(Server.class);
	private static final Map<HttpSession, Boolean> sessions = Collections.synchronizedMap(new HashMap<>());
	private static EndpointConfig endpointConfig;
	public static DataBaseImpl dbImpl = new DataBaseImpl();

	@OnOpen
	public void handlerOpen(Session session, EndpointConfig config) throws IOException, EncodeException {
		endpointConfig = config;
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		if(!isValid(httpSession)) {
			LOG.info("Not valid session. ==> Close");
			session.close();
			return;
		} else {
			session.setMaxIdleTimeout(1000 * httpSession.getMaxInactiveInterval());
			LOG.info("Socket connected - " + session.getId());
			
			new AutoCloseSession(session, httpSession).start();
		}
	}

	@OnMessage
	public void handlerMessage(final Session session, Message message) throws IOException, EncodeException {
		HttpSession httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());
		if(!isValid(httpSession)) {
			LOG.info("Not valid session");
			return;
		}
		if (message.getType().equals(CommandMessage.class.getSimpleName())) {
			CommandMessage cm = (CommandMessage) message;
			switch (cm.getCommand().toLowerCase()) {
			case "user":
				cm.setParameters("login", httpSession.getAttribute("user").toString());
				int idUser = Integer.parseInt(httpSession.getAttribute("userId").toString());
				UserWeb user = dbImpl.getUser(idUser);
				String lang = "en";
				switch(user.getLanguageid()){
					case 1: lang = "en"; break;
					case 2: lang = "ru"; break;
					case 3: lang = "uk"; break;
				}
				cm.setParameters("lang", lang);
				session.getBasicRemote().sendObject(cm);
				break;
			case "getreport":
				ReportMessages.getReport(cm, session, ReportMessages.HTML_FORMAT);
				break;
			case "savereport":
				ReportMessages.getReport(cm, session, ReportMessages.NOT_HTML_FORMAT);
				break;
			case "deleteowner":
				ProfileMessages.deleteOwner(cm, session);
				break;
			case "addowner":
				ProfileMessages.addOwner(cm, session);
				break;
			case "profileinfo":
				ProfileMessages.getProfileInfo(cm, session);
				break;
			case "ownerlist":
				ProfileMessages.getOwnerList(cm, session);
				break;
			case "removeprofile":
				ProfileMessages.removeProfile(cm, session, httpSession);
				break;
			case "updateprofile":
				ProfileMessages.updateProfile(cm, session, httpSession);
				break;
			default:
				break;
			}
		}
	}

	@OnClose
	public void handlerClose(Session session, CloseReason closeReason) {
		LOG.info("Socket disconnected - " + session.getId() + ".\n\t Reason = " + closeReason.getReasonPhrase());
//		HttpSession httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());
//		sessions.remove(httpSession);
	}
	
	@OnError
	public void handlerError(Session session, Throwable thr) {
		LOG.error("Error - " + ExceptionUtils.getFullStackTrace(thr));
//		thr.printStackTrace();
	}

	public static Map<HttpSession, Boolean> getSessions() {
		return sessions;
	}
	
	public static boolean isValid(HttpSession session) {
		return sessions.get(session) == null ? false : sessions.get(session);
	}

	private class AutoCloseSession extends Thread {
		private HttpSession httpSession;
		private Session session;

		public AutoCloseSession(Session session, HttpSession httpSession) {
			this.setName("AutoClose_Session");
			this.session = session;
			this.httpSession = httpSession;
			try {
				sessions.keySet().forEach(s -> {
					try {
						if(s.getMaxInactiveInterval() - (System.currentTimeMillis() - s.getLastAccessedTime())/1000 < 3) {
							sessions.remove(s);
							LOG.info("Session removed - " + s.getId());
						}
					} catch (Exception e) {
						sessions.remove(s);
						LOG.info("Session removed - " + s.getId());
					}
				});
			} catch (Exception e) {
				LOG.error("Error in AutoCloseSession");
			}
		}

		@Override
		public void run() {
			try {
				boolean isWait = true;
				while(isWait && httpSession != null && !httpSession.isNew() && session.isOpen()) {
					long sessionTime = System.currentTimeMillis() - httpSession.getLastAccessedTime();
					isWait = sessionTime < (httpSession.getMaxInactiveInterval() - 1) * 1000;
					Thread.sleep(1000);
				}
				if(session.isOpen()) {
					session.close(new CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "HttpSession is closed"));
					sessions.remove(httpSession);
					LOG.info("Sessions count = " + sessions.size());
				}
			} catch (InterruptedException | IOException e) {
				LOG.error(e.getMessage());
			}
		}
	}
}