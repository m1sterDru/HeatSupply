package nik.heatsupply.db;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.db.jdbc.BatisJDBC;
import nik.heatsupply.db.jdbc.IBatisJDBC;
import nik.heatsupply.db.jdbc.PostgresDB;
import nik.heatsupply.db.jdbc.mappers.IMapper;
import nik.heatsupply.socket.model.Meter;
import nik.heatsupply.socket.model.MeterUser;
import nik.heatsupply.socket.model.UserWeb;

@SuppressWarnings("unchecked")
public class ConnectDB {
	private static final Logger LOG = LoggerFactory.getLogger(ConnectDB.class);
	private static final String DATASOURCE_NAME = "heatSupplyDS";
	private static DataSource dsLocal;
	private static PostgresDB postgressDB;
	private static Context context = null;
	
	public ConnectDB() {
		LOG.info("Create ConnectDB " + this.toString());
	}
	
	public static boolean updateUser(int idUser, String password, String phone, String email, int languageid) {
		Encryptor encr = new Encryptor();
		while(password.length() < 12) password += " ";
		String passwordE = encr.encrypt(password);

		IBatisJDBC updateUser =  s -> s.getMapper(IMapper.class)
				.updateUser(idUser, passwordE, phone, email, languageid);
		
		return new BatisJDBC(updateUser).run();
	}
	
	public static boolean addUserMeter(int idUser, int idMeter) {
		List<MeterUser> oldOwners = ConnectDB.getMetersList(idUser);
		String owneraccount = oldOwners.get(0).getIdAccount();
		int type_device_id = oldOwners.get(0).getIdTypeDevice();
		
		IBatisJDBC addUser = s -> s.getMapper(IMapper.class).addMeterUser(idUser, idMeter, 0, type_device_id, owneraccount);
		return new BatisJDBC(addUser).run();
	}
	
	public static boolean removeUserMeter(int idUser, int idMeter) {
		IBatisJDBC deleteMeterUser = s -> s.getMapper(IMapper.class).deleteMeterUser(idUser, idMeter);
		return new BatisJDBC(deleteMeterUser).run();
	}
	
	public static UserWeb getUser(int idUser) {
		return (UserWeb) new BatisJDBC(s -> s.getMapper(IMapper.class).getUser(idUser)).get();
	}
	
	public static UserWeb getUserByLogin(String login) {
		return (UserWeb) new BatisJDBC(s -> s.getMapper(IMapper.class).getUserByLogin(login)).get();
	}
	
	public static UserWeb getUser(String login) {
		return (UserWeb) new BatisJDBC(s -> s.getMapper(IMapper.class).getUserByLogin(login)).get();
	}
	
	public static List<Meter> getOwnerList(String owneraccount) {
		return (List<Meter>) new BatisJDBC(s -> s.getMapper(IMapper.class).getOwnerList(owneraccount)).get();
	}
	
	public static Meter getMeterById(int idMeter) {
		return (Meter) new BatisJDBC(s -> s.getMapper(IMapper.class).getMeterById(idMeter)).get();
	}
	
	public static Integer getMaxUserId() {
		return (Integer) new BatisJDBC(s -> s.getMapper(IMapper.class).getMaxUserId()).get();
	}
	
	public static List<MeterUser> getMetersList(int idUser) {
		return (List<MeterUser>) new BatisJDBC(s -> s.getMapper(IMapper.class).getMetersList(idUser)).get();
	}
	
	public static String addUser(String login, String password, String phone, String email, 
			int languageid, String owneraccount, String meterId, String lastcash) {

		String name = "";
		String middlename = "";
		String surname = "";

		UserWeb user = ConnectDB.getUserByLogin(login);
		if(user != null) {
			if(user.isActive()) {
				System.out.println("====mail " + user.getLogin() + " = " + user.isActive());
				return user.getEmail();
			} else {
				System.out.println("====2222");
				return "2";
			}
		} else {
			System.out.println("====3333");
			return "3";
		}
//		Encryptor encr = new Encryptor();
//		while(password.length() < 12) password += " ";
//		String passwordE = encr.encrypt(password);
//
//		int id = ConnectDB.getMaxUserId();
//		IBatisJDBC[] iCollection = new IBatisJDBC[2];
//		iCollection[0] =  s -> s.getMapper(IMapper.class)
//						.addUser(id, login, passwordE, name, middlename, surname, phone, email, languageid);
//
//		int idMeter = Integer.parseInt(meterId);
//		double lastCashSum = lastcash.length() == 0 ? 0.0 : Double.parseDouble(lastcash);
//		iCollection[1] = s -> s.getMapper(IMapper.class)
//				.addMeterUser(id, idMeter, lastCashSum, 0, owneraccount);
//
//		return new BatisJDBC(iCollection).runCollection() ? "1" : "0";
	}
	
	public static boolean deleteUser(int idUser) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).deleteUser(idUser)).run();
	}
	
	public static boolean activateUser(String login) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).activateUser(login)).run();
	}
	
	public static boolean addMeterUser(int iduser, int idmeter, double lastcash, int type_device_id, String idaccount) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class)
				.addMeterUser(iduser, idmeter, lastcash, type_device_id, idaccount)).run();
	}

	public static DataSource getDataSource() {
		if (dsLocal != null) return dsLocal;
		try {
			if (context == null) {
				context = new InitialContext();
			}
			dsLocal = (DataSource) context.lookup(DATASOURCE_NAME);
		} catch (Exception e) {
			try {
				dsLocal = (DataSource) context.lookup("java:comp/env/" + DATASOURCE_NAME);
			} catch (Exception e1) {
				LOG.error(ExceptionUtils.getStackTrace(e1));
			}
		}
		return dsLocal;
	}
	
	public static PostgresDB getPostgressDB() {
		if (postgressDB == null) {
			synchronized (ConnectDB.class) {
				postgressDB = new PostgresDB();
				LOG.info("New connection");
			}
		}
		return postgressDB;
	}
	
	public static void setPostgressDB(PostgresDB value) {
		postgressDB = value;
	}
}