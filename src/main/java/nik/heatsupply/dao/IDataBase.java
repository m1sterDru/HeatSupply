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
	boolean deleteUserFromDB(int idUser);
	UserWeb getUser(int idUser);

	boolean removeUserMeter(int idUser, int idMeter);
	List<MeterUser> getMetersList(int idUser);
	
	Meter getMeterById(int idMeter);
	List<Meter> getOwnerList(String owneraccount);
}