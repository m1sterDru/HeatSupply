package nik.heatsupply.dao.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import nik.heatsupply.socket.model.Meter;
import nik.heatsupply.socket.model.MeterUser;
import nik.heatsupply.socket.model.UserWeb;

public interface IMapper {
	@Update("update users_web set " +
			"password = #{password}, email = #{email}, phone = #{phone}, languageid = #{languageid} " +
			"where id = #{id}")
	Integer updateUser(@Param("id") int idUser, @Param("password")String password,
			@Param("phone")String phone, @Param("email")String email, @Param("languageid")int languageid);
	
	@Delete("delete from meter_user where iduser = #{iduser} and idmeter = #{idmeter}")
	Integer deleteMeterUser(@Param("iduser")int idUser, @Param("idmeter")int idmeter);
	
	@Select("select * from users_web where id = #{id}")
	UserWeb getUser(@Param("id")int idUser);
	
	@Select("select * from users_web where login = #{login}")
	UserWeb getUserByLogin(@Param("login")String login);
	
	@Select("select * from users_web where email = #{email}")
	UserWeb getUserByEmail(@Param("email")String email);
	
	@Select("select coalesce(max(id), 0) + 1 from users_web")
	Integer getMaxUserId();
	
	@Select("select * from meter_user where iduser = #{iduser}")
	List<MeterUser> getMetersList(@Param("iduser")int iduser);
	
	@Select("select * from public.meter where owneraccount = #{owneraccount}")
	List<Meter> getOwnerList(@Param("owneraccount")String owneraccount);
	
	@Select("select * from public.meter where id = #{id}")
	Meter getMeterById(@Param("id")int idMeter);
	
	@Insert("insert into users_web " +
			"(id, login, password, name, middlename, surname, phone, email, languageid, active) values " +
			"(#{id}, #{login}, #{password}, #{name}, #{middlename},"
			+ "#{surname}, #{phone}, #{email}, #{languageid}, true)")
	Integer addUser(@Param("id")int id, @Param("login")String login, @Param("password")String password, 
			@Param("name")String name, @Param("middlename")String middlename, @Param("surname")String surname,
			@Param("phone")String phone, @Param("email")String email, @Param("languageid")int languageid);
		
	@Update("update users_web set active = false where id = #{id}")
	Integer deleteUser(@Param("id")int id);
	
	@Delete("delete from users_web where id = #{id}")
	Integer deleteUserFromDB(@Param("id")int id);
	
	@Update("update users_web set active = true where login = #{login}")
	Integer activateUser(@Param("login")String login);
	
	@Insert("insert into meter_user (iduser, idmeter, lastcash, type_device_id, idaccount) values " +
			"(#{iduser}, #{idmeter}, #{lastcash}, #{type_device_id}, #{idaccount})")
	Integer addMeterUser(@Param("iduser")int iduser, @Param("idmeter")int idmeter, 
			@Param("lastcash")double lastcash, @Param("type_device_id")int type_device_id, @Param("idaccount")String idaccount);
}