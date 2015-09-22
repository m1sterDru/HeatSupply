package nik.heatsupply.socket.messages;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.model.Meter;
import nik.heatsupply.socket.model.MeterUser;
import nik.heatsupply.socket.model.UserWeb;

public class ProfileMessages {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileMessages.class);
	protected static final int SUCCESS = 0;
	protected static final int OWNER_NOT_EXIST = 1;
	protected static final int METER_NOT_EXIST = 2;
	protected static final int TRY_AGAIN = 3;

	public static void deleteOwner(CommandMessage cm, Session session) {
		CommandMessage retMessage = new CommandMessage("deleteOwner");
		retMessage.setParameters("success", "false");
		try {
			int idMeter = Integer.parseInt(cm.getParameters().get("idMeter"));
			int idUser = Integer.parseInt(cm.getParameters().get("userId"));
			ConnectDB.removeUserMeter(idUser, idMeter);

			retMessage.setParameters("success", "true");
			retMessage.setParameters("idUser", idUser + "");
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		try {
			session.getBasicRemote().sendObject(retMessage);
		} catch (IOException | EncodeException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public static void removeProfile(CommandMessage cm, Session session, HttpSession httpSession) {
		int idUser = Integer.parseInt(cm.getParameters().get("userId"));
		if(ConnectDB.deleteUser(idUser)) {
			sendMessage(session, SUCCESS, "removeProfile");
			httpSession.invalidate();
		} else {
			sendMessage(session, TRY_AGAIN, "removeProfile");
		}
	}
	
	public static void updateProfile(CommandMessage cm, Session session, HttpSession httpSession) {
		int idUser = Integer.parseInt(cm.getParameters().get("userId"));
		if(ConnectDB.deleteUser(idUser)) {
			sendMessage(session, SUCCESS, "updateProfile");
			httpSession.invalidate();
		} else {
			sendMessage(session, TRY_AGAIN, "updateProfile");
		}
	}
	
	public static void getProfileInfo(CommandMessage cm, Session session) {
		int idUser = Integer.parseInt(cm.getParameters().get("userId"));
		UserWeb u = ConnectDB.getUser(idUser);
		if(u != null) {
			CommandMessage message = new CommandMessage("profileInfo");
//			message.setParameters("name", notNull(u.getName()));
//			message.setParameters("middleName", notNull(u.getMiddlename()));
//			message.setParameters("surName", notNull(u.getSurname()));
			message.setParameters("email", notNull(u.getEmail()));
			message.setParameters("phone", notNull(u.getPhone()));
			
			try {
				session.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
	
	public static void getOwnerList(CommandMessage cm, Session session) {
		int idUser = Integer.parseInt(cm.getParameters().get("userId"));
		String selector = cm.getParameters().get("selector");
		UserWeb u = ConnectDB.getUser(idUser);
		if(u != null) {
			CommandMessage message = new CommandMessage("ownerList");
			message.setParameters("selector", selector);
			List<MeterUser> actOwners = ConnectDB.getMetersList(u.getId());
			actOwners.forEach(m -> {
				try {
					Meter met = ConnectDB.getMeterById(m.getIdmeter());
					message.setParameters(m.getIdmeter() + "", notNull(met.getSerialnumber()) + ";" + 
							met.getOwneraccount() + ";" + met.getOwnername().replace(";", ""));
				} catch (Exception e) {
					LOG.error(ExceptionUtils.getStackTrace(e));
				}
			});
			try {
				session.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
	
	private static String notNull(String s) {
		if(s == null) return ""; else return s;
	}
	
	public static void addOwner(CommandMessage cm, Session session) {
		String owneraccount = cm.getParameters().get("account");
		String meterNumber = cm.getParameters().get("number");
		String userId = cm.getParameters().get("userId");
		String type_accountS = cm.getParameters().get("typeAccount");
		int type_account = type_accountS == null ? 0 : Integer.parseInt(type_accountS);

		new AddOwner(owneraccount, meterNumber, new IAddOwner() {
			@Override
			public void onSuccess(Meter meter) {
				int idUser = Integer.parseInt(userId);
				if(ConnectDB.addMeterUser(idUser, meter.getId(), 0, type_account, owneraccount)) {
					sendMessage(session, SUCCESS, "addOwner", "idUser_" + userId);
				} else {
					sendMessage(session, TRY_AGAIN, "addOwner");
				}
			}
			
			@Override
			public void onOwnerNotExist() {
				sendMessage(session, OWNER_NOT_EXIST, "addOwner");
			}
			
			@Override
			public void onMeterNotExist() {
				sendMessage(session, METER_NOT_EXIST, "addOwner");
			}
		}).run();
	}
	
	private static void sendMessage(Session session, int messageID, String command) {
		sendMessage(session, messageID, command, null);
	}

	private static void sendMessage(Session session, int messageID, String command, String par) {
		String text = "";
		String success = "false";
		switch(messageID) {
			case OWNER_NOT_EXIST: text = "keyOwnerAccountError"; break;
			case METER_NOT_EXIST: text = "keyMeterNotExist"; break;
			case TRY_AGAIN: text = "Something wrong. Try again."; break;
			case SUCCESS: success = "true"; break;
		}
		CommandMessage retMessage = new CommandMessage(command);
		retMessage.setParameters("success", success);
		retMessage.setParameters("message", text);
		if(par != null) retMessage.setParameters(par.substring(0, par.indexOf("_")), par.substring(par.indexOf("_") + 1));
		try {
			session.getBasicRemote().sendObject(retMessage);
		} catch (IOException | EncodeException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public interface IAddOwner {
		public void onOwnerNotExist();
		public void onMeterNotExist();
		public void onSuccess(Meter meter);
	}
	
	public static class AddOwner {
		private IAddOwner owner;
		private String owneraccount;
		private String meterNumber;
		
		public AddOwner(String owneraccount, String meterNumber, IAddOwner owner) {
			this.owneraccount = owneraccount;
			this.meterNumber = meterNumber;
			this.owner = owner;
		}
		
		public void run() {
			List<Meter> owners = ConnectDB.getOwnerList(owneraccount);

			if(owners.size() == 0) {
				owner.onOwnerNotExist();
			} else {
				Optional<Meter> meter = owners.stream().filter(f -> f.getSerialnumber().equals(meterNumber)).findFirst();
				if(meter.isPresent()) {
					owner.onSuccess(meter.get());
				} else {
					owner.onMeterNotExist();
				}
			}
		}
	}
}