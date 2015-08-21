package nik.heatsupply.db.jdbc.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import nik.heatsupply.socket.model.User;

public interface IMapper {
	@Select("select * from public.user where id = #{id}")
	User getUser(@Param("id")int idUser);
	
	@Select("select * from public.user where login = #{login}")
	User getUserByLogin(@Param("login")String login);
	
	@Insert("insert into users " +
			"(idUser, userName, login, password, email, lasttime) values " +
			"((select coalesce(max(iduser), 0) + 1 from users), #{userName}, #{login}, #{password}, #{email}, now())")
	Integer addUser(@Param("userName")String userName, @Param("login")String login, 
			@Param("password")String password, @Param("email") String email);
	
	@Update("update public.user set " +
			"userName = #{userName}, login = #{login}, password = #{password}, email = #{email}, lasttime = now() " +
			"where iduser = #{iduser}")
	Integer updateUser(@Param("iduser") int idUser, @Param("userName")String userName, @Param("login")String login, 
			@Param("password")String password, @Param("email") String email);
}