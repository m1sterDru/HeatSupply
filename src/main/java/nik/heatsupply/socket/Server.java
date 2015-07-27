package nik.heatsupply.socket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import nik.heatsupply.socket.messages.CommandMessage;
import nik.heatsupply.socket.messages.Message;
import nik.heatsupply.socket.messages.coders.MessageDecoder;
import nik.heatsupply.socket.messages.coders.MessageEncoder;

@ServerEndpoint(value = "/socketServer", configurator = GetHttpSessionConfigurator.class,
encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class Server {
	private static final Map<Session, String> users = Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, Session> httpSessions = Collections.synchronizedMap(new HashMap<>());

	public Server() {
		System.out.println("====================< new Server >====================");
	}

	public static void sendObject(Session session, HttpSession httpSession, Object message){
		try {
			if(updateSessionMaxIdle(session, httpSession) && session.isOpen())
				session.getBasicRemote().sendObject(message);
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
	}

	private static boolean updateSessionMaxIdle(Session session, HttpSession httpSession){
		long t = System.currentTimeMillis() - httpSession.getLastAccessedTime();
		if (t < (httpSession.getMaxInactiveInterval() - 1) * 1000) {
			session.setMaxIdleTimeout(1000 * httpSession.getMaxInactiveInterval() - t);
			return true;
		} else {
			try {
				if(session.isOpen()) session.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void sender(HttpSession httpSession, Session ss) {
		for (int i = 0; i < 20; i++) {
			if(!ss.isOpen()) break;
			Iterator<Session> iter = users.keySet().iterator();
			while (iter.hasNext()) {
				Session s = (Session) iter.next();
				try {
					CommandMessage cm = new CommandMessage("user " + i);
					cm.setParameters("value", "test");
					sendObject(s, httpSession, cm);
				} catch (Exception e) {
					break;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@OnOpen
	public void handlerOpen(Session session, EndpointConfig config) {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

		Session oldSession = httpSessions.get(httpSession.getId());
		if (oldSession != null) {
			users.remove(oldSession);
			try {
				oldSession.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			httpSessions.remove(httpSession.getId());
		}

		session.setMaxIdleTimeout(1000 * httpSession.getMaxInactiveInterval());
		int userId = Integer.parseInt(httpSession.getAttribute("userId").toString());
		users.put(session, userId + "");
		httpSessions.put(httpSession.getId(), session);
		System.out.println("Socket connected - " + session.getId());
		if(httpSession != null){
			CommandMessage cm = new CommandMessage("user");
			cm.setParameters("value", httpSession.getAttribute("user").toString());
			sendObject(session, httpSession, cm);
			new Thread(() -> {
				sender(httpSession, session);
			}).start();
		}
	}

	@OnMessage
	public void handlerMessage(final Session session, Message message, EndpointConfig config) {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		System.out.println(message);
		updateSessionMaxIdle(session, httpSession);
		if (message.getType().equals(CommandMessage.class.getName())) {
			CommandMessage cm = (CommandMessage) message;
			switch (cm.getCommand().toLowerCase()) {
			case "close":
				System.out.println("CLOSE");
				break;
	
			default:
				break;
			}
		}
	}

	@OnClose
	public void handlerClose(Session session, EndpointConfig config) {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		users.remove(session);
		if (httpSession != null) httpSessions.remove(httpSession.getId());
		System.out.println("Socket disconnected - " + session.getId());
	}

	public static Map<Session, String> getUsers() {
		return users;
	}
}