package nik.heatsupply.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
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
	private static HttpSession httpSession;
	private static int userId;
	private static final Map<Session, String> users = Collections.synchronizedMap(new HashMap<>());

	public Server() {
		System.out.println("====================< new Server >====================");
	}

	public static void sendObject(Session session, Object message){
		try {
			updateSessionMaxIdle(session);
			session.getBasicRemote().sendObject(message);
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
	}

	public static void updateSessionMaxIdle(Session session){
		long t = System.currentTimeMillis() - httpSession.getLastAccessedTime();
		if (t < httpSession.getMaxInactiveInterval() * 1000) {
			session.setMaxIdleTimeout(1000 * httpSession.getMaxInactiveInterval() - t);
		} else {
			try {
				if(httpSession != null) {
					httpSession.invalidate();
					System.out.println("invalidate ===========");
				} else {
					System.out.println("NULL");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(t + " == " + session.getMaxIdleTimeout() / 1000);
	}

	private void sender(){
		for (int i = 0; i < 20; i++) {
			Iterator<Session> iter = users.keySet().iterator();
			while (iter.hasNext()) {
				Session s = (Session) iter.next();
				try {
					CommandMessage cm = new CommandMessage("user " + i);
					cm.setParameters("value", "test");
					sendObject(s, cm);
				} catch (Exception e) {
					users.remove(s);
					System.out.println("remove");
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
	public void handlerOpen(Session session, EndpointConfig config) throws UnsupportedEncodingException, IOException, EncodeException {
		httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		System.out.println(httpSession.getAttribute("user").toString());
		session.setMaxIdleTimeout(1000 * httpSession.getMaxInactiveInterval());
		userId = Integer.parseInt(httpSession.getAttribute("userId").toString());
		users.put(session, userId + "");
		System.out.println("Socket connected at " + new Date(System.currentTimeMillis()));
		if(httpSession != null){
			CommandMessage cm = new CommandMessage("user");
			cm.setParameters("value", httpSession.getAttribute("user").toString());
			sendObject(session, cm);
			sender();
		}
	}

	@OnMessage
	public void handlerMessage(final Session session, Message message) throws IOException, EncodeException {
		updateSessionMaxIdle(session);
		System.out.println(message);
	}

	@OnClose
	public void handlerClose(Session session) {
		long id = System.currentTimeMillis();
		System.out.println("Socket disconnected at " + new Date(id));
	}

	public static void clearUsers() {
		Iterator<Session> iter = users.keySet().iterator();
		while (iter.hasNext()) {
			Session session = (Session) iter.next();
			users.remove(session);
		}
	}

	public static Map<Session, String> getUsers() {
		return users;
	}

	public static int getUserId() {
		return userId;
	}
}