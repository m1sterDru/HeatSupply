package nik.heatsupply.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.dao.mappers.IMapper;
import nik.heatsupply.socket.Server;
import nik.heatsupply.socket.model.Meter;
import nik.heatsupply.socket.model.MeterUser;
import nik.heatsupply.socket.model.UserWeb;

@SuppressWarnings("unchecked")
public class DataBaseImpl extends DataBaseSuper implements IDataBase {
	private static final Logger LOG = LoggerFactory.getLogger(DataBaseImpl.class);
	
	public DataBaseImpl() {
		super();
		LOG.info("Create DataBaseImpl " + this.toString());
	}

	@Override
	public Integer getMaxUserId() {
		return (Integer) new BatisImpl(s -> s.getMapper(IMapper.class).getMaxUserId()).get();
	}
	
	@Override
	public String addUser(String login, String password, String phone, String email, 
			int languageid, String owneraccount, String meterId, String lastcash) {

		String name = "";
		String middlename = "";
		String surname = "";

		UserWeb user = getUserByLogin(login);
		if(user != null) {
			if(user.isActive()) {
				return user.getEmail();
			} else {
				if(Server.dbImpl.activateUser(login)) {
					return ADD_USER_SUCCESS;
				} else {
					return ADD_USER_TRY_AGAIN;
				}
			}
		} else {
			Encryptor encr = new Encryptor();
			while(password.length() < 12) password += " ";
			String passwordE = encr.encrypt(password);
	
			int id = Server.dbImpl.getMaxUserId();
			IBatis[] iCollection = new IBatis[2];
			iCollection[0] =  s -> s.getMapper(IMapper.class)
							.addUser(id, login, passwordE, name, middlename, surname, phone, email, languageid);
	
			int idMeter = Integer.parseInt(meterId);
			double lastCashSum = lastcash.length() == 0 ? 0.0 : Double.parseDouble(lastcash);
			iCollection[1] = s -> s.getMapper(IMapper.class)
					.addMeterUser(id, idMeter, lastCashSum, 0, owneraccount);
	
			return new BatisImpl(iCollection).runCollection() ? "0" : "1";
		}
	}
	
	@Override
	public boolean deleteUser(int idUser) {
		return new BatisImpl(s -> s.getMapper(IMapper.class).deleteUser(idUser)).run();
	}
	
	@Override
	public boolean activateUser(String login) {
		return new BatisImpl(s -> s.getMapper(IMapper.class).activateUser(login)).run();
	}
	
	@Override
	public boolean updateUser(int idUser, String password, String phone, String email, int languageid) {
		Encryptor encr = new Encryptor();
		while(password.length() < 12) password += " ";
		String passwordE = encr.encrypt(password);

		IBatis updateUser =  s -> s.getMapper(IMapper.class)
				.updateUser(idUser, passwordE, phone, email, languageid);
		
		return new BatisImpl(updateUser).run();
	}
	
	@Override
	public UserWeb getUser(int idUser) {
		return (UserWeb) new BatisImpl(s -> s.getMapper(IMapper.class).getUser(idUser)).get();
	}
	
	@Override
	public UserWeb getUserByLogin(String login) {
		return (UserWeb) new BatisImpl(s -> s.getMapper(IMapper.class).getUserByLogin(login)).get();
	}
	
	@Override
	public boolean addMeterUser(int iduser, int idmeter, double lastcash, int type_device_id, String idaccount) {
		return new BatisImpl(s -> s.getMapper(IMapper.class)
				.addMeterUser(iduser, idmeter, lastcash, type_device_id, idaccount)).run();
	}
	
	@Override
	public boolean addUserMeter(int idUser, int idMeter) {
		List<MeterUser> oldOwners = getMetersList(idUser);
		String owneraccount = oldOwners.get(0).getIdAccount();
		int type_device_id = oldOwners.get(0).getIdTypeDevice();
		
		IBatis addUser = s -> s.getMapper(IMapper.class).addMeterUser(idUser, idMeter, 0, type_device_id, owneraccount);
		return new BatisImpl(addUser).run();
	}
	
	@Override
	public boolean removeUserMeter(int idUser, int idMeter) {
		IBatis deleteMeterUser = s -> s.getMapper(IMapper.class).deleteMeterUser(idUser, idMeter);
		return new BatisImpl(deleteMeterUser).run();
	}
	
	@Override
	public List<MeterUser> getMetersList(int idUser) {
		return (List<MeterUser>) new BatisImpl(s -> s.getMapper(IMapper.class).getMetersList(idUser)).get();
	}
	
	@Override
	public List<Meter> getOwnerList(String owneraccount) {
		return (List<Meter>) new BatisImpl(s -> s.getMapper(IMapper.class).getOwnerList(owneraccount)).get();
	}
	
	@Override
	public Meter getMeterById(int idMeter) {
		return (Meter) new BatisImpl(s -> s.getMapper(IMapper.class).getMeterById(idMeter)).get();
	}
}