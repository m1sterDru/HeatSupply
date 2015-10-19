package nik.heatsupply.dao;

import java.util.List;

import nik.heatsupply.socket.model.Meter;
import nik.heatsupply.socket.model.MeterUser;
import nik.heatsupply.socket.model.UserWeb;

public interface IDataBase {
	Integer getMaxUserId();
	String addUser(String login, String password, String phone, String email, 
			int languageid, String owneraccount, String meterId, String lastcash);
	boolean updateUser(int idUser, String password, String phone, String email, int languageid);
	boolean deleteUser(int idUser);
	boolean activateUser(String login);
	UserWeb getUser(int idUser);
	UserWeb getUserByLogin(String login);
	
	boolean addMeterUser(int iduser, int idmeter, double lastcash, int type_device_id, String idaccount);
	boolean addUserMeter(int idUser, int idMeter);
	boolean removeUserMeter(int idUser, int idMeter);
	List<MeterUser> getMetersList(int idUser);
	
	Meter getMeterById(int idMeter);
	List<Meter> getOwnerList(String owneraccount);
}