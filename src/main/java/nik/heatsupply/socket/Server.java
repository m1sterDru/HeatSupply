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
	private HttpSession httpSession;
	private static int userId;
	private static final Map<Session, String> users = Collections.synchronizedMap(new HashMap<>());
//	private static final Map<Integer, Scheme> schemes = Collections.synchronizedMap(new HashMap<>());
	
	public Server() {
		System.out.println("====================< new Server >====================");
	}
	
	@OnOpen
	public void handlerOpen(Session session, EndpointConfig config) throws UnsupportedEncodingException, IOException, EncodeException {
		httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		if(httpSession != null) 
			System.out.println(httpSession.getAttribute("user").toString());
//			userId = Integer.parseInt(httpSession.getAttribute("userId").toString());
		users.put(session, userId + "");
		System.out.println("Socket connected at " + new Date(System.currentTimeMillis()));
		if(httpSession != null){
			CommandMessage cm = new CommandMessage("user");
			cm.setParameters("value", httpSession.getAttribute("user").toString());
			session.getBasicRemote().sendObject(cm);
		}
	}
	
	@OnMessage
	public void handlerMessage(final Session session, Message message) throws IOException, EncodeException {
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