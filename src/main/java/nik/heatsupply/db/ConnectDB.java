package nik.heatsupply.db;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.db.jdbc.BatisJDBC;
import nik.heatsupply.db.jdbc.IBatisJDBC;
import nik.heatsupply.db.jdbc.PostgresDB;
import nik.heatsupply.db.jdbc.mappers.IMapper;
import nik.heatsupply.socket.Server;
import nik.heatsupply.socket.model.Meter;
import nik.heatsupply.socket.model.MeterUser;
import nik.heatsupply.socket.model.UserWeb;

@SuppressWarnings("unchecked")
public class ConnectDB {
	private static final String DATASOURCE_NAME = "heatSupplyDS";
	private static DataSource dsLocal;
	private static Context context = null;
	
	public ConnectDB() {
		System.out.println("create ConnectDB " + this.toString());
	}
	
	public static boolean updateUser(int idUser, String password, String name, String middlename,
			String surname, String phone, String email, int languageid, String[] owners) {
		Encryptor encr = new Encryptor();
		while(password.length() < 12) password += " ";
		String passwordE = encr.encrypt(password);
		
		List<MeterUser> oldOwners = ConnectDB.getMetersList(idUser);
		String owneraccount = oldOwners.get(0).getIdAccount();
		int type_device_id = oldOwners.get(0).getIdTypeDevice();
		IBatisJDBC[] iCollection = new IBatisJDBC[owners.length + oldOwners.size() + 1];
		iCollection[0] =  s -> s.getMapper(IMapper.class)
				.updateUser(idUser, passwordE, name, middlename, surname, phone, email, languageid);
		
		int counter = 1;
		String[] oldIdMeters = new String[oldOwners.size()];
		for(int i = 0; i< oldOwners.size(); i ++) {
			MeterUser old = oldOwners.get(i);
			oldIdMeters[i] = old.getIdmeter() + "";
			if(!isArrayContains(owners, old.getIdmeter() + "")) {
				iCollection[counter++] = s -> s.getMapper(IMapper.class).deleteMeterUser(idUser, old.getIdmeter());
			}
		}
		for(String owner : owners) {
			if(!isArrayContains(oldIdMeters, owner)) {
				int idMeter = Integer.parseInt(owner);
				iCollection[counter++] = s -> s.getMapper(IMapper.class).addMeterUser(idUser, idMeter, 0, type_device_id, owneraccount);
			}
		}
		
		return new BatisJDBC(iCollection).runCollection();
	}
	
	private static boolean isArrayContains(String[] arr, String targetValue) {
		for(String s: arr){
			if(s.equals(targetValue))
				return true;
		}
		return false;
	}
	
	public static UserWeb getUser(int idUser) {
		return (UserWeb) new BatisJDBC(s -> s.getMapper(IMapper.class).getUser(idUser)).get();
	}
	
	public static UserWeb getUser(String login) {
		return (UserWeb) new BatisJDBC(s -> s.getMapper(IMapper.class).getUserByLogin(login)).get();
	}
	
	public static List<Meter> getOwnerList(String owneraccount) {
		return (List<Meter>) new BatisJDBC(s -> s.getMapper(IMapper.class).getOwnerList(owneraccount)).get();
	}
	
	public static Integer getMaxUserId() {
		return (Integer) new BatisJDBC(s -> s.getMapper(IMapper.class).getMaxUserId()).get();
	}
	
	public static List<MeterUser> getMetersList(int idUser) {
		return (List<MeterUser>) new BatisJDBC(s -> s.getMapper(IMapper.class).getMetersList(idUser)).get();
	}
	
	public static boolean addUser(int id, String login, String password, String name, String middlename,
			String surname, String phone, String email, int languageid, String[] owners, String owneraccount, String lastcash) {
		Encryptor encr = new Encryptor();
		while(password.length() < 12) password += " ";
		String passwordE = encr.encrypt(password);

		IBatisJDBC[] iCollection = new IBatisJDBC[owners.length + 1];
		iCollection[0] =  s -> s.getMapper(IMapper.class)
						.addUser(id, login, passwordE, name, middlename, surname, phone, email, languageid);
		
		for (int i = 0; i < owners.length; i++) {
			String owner = owners[i];
			int idMeter = Integer.parseInt(owner);
			double lastCashSum = lastcash.length() == 0 ? 0.0 : Double.parseDouble(lastcash);
			int type_device_id = 1;
			iCollection[i + 1] = s -> s.getMapper(IMapper.class)
					.addMeterUser(id, idMeter, lastCashSum, type_device_id, owneraccount);
		}
		return new BatisJDBC(iCollection).runCollection();
	}
	
	public static boolean deleteUser(int idUser) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).deleteUser(idUser)).run();
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
				System.out.println(e1.getMessage());
			}
		}
		return dsLocal;
	}
	
	private static PostgresDB postgressDB;
	private static boolean isFirstStart = true;
	public static PostgresDB getPostgressDB() {
		if (postgressDB == null) {
			synchronized (ConnectDB.class) {
				if (!isFirstStart) Server.clearUsers();
				postgressDB = new PostgresDB();
				isFirstStart = false;
				System.out.println("New connection");
			}
		}
		return postgressDB;
	}
	
	public static void setPostgressDB(PostgresDB value) {
		postgressDB = value;
	}
}