package nik.heatsupply.dao;

import static org.junit.Assert.*;

import org.apache.tomcat.dbcp.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.tomcat.dbcp.dbcp2.datasources.SharedPoolDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import nik.heatsupply.socket.model.UserWeb;

public class DataBaseImplTest {
	private static DataBaseImpl dbImpl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SharedPoolDataSource tds = null;
		
		try {
			DriverAdapterCPDS cpds = new DriverAdapterCPDS();
			cpds.setDriver("org.postgresql.Driver");
			String dbConnect = String.format("jdbc:postgresql://%s:%s/%s", "10.2.6.144", "5432", "nik_dneprobl_pro(2014.06.26)");

			cpds.setUrl(dbConnect);
			cpds.setUser("postgres");
			cpds.setPassword("askue");

			tds = new SharedPoolDataSource();
			tds.setConnectionPoolDataSource(cpds);
			tds.setMaxTotal(20);
			tds.setDefaultMaxWaitMillis(50);
			tds.setValidationQuery("select 1");
			tds.setDefaultTestOnBorrow(true);
			tds.setDefaultTestOnReturn(true);
			tds.setDefaultTestWhileIdle(true);
		} catch(Exception e) {
			System.err.println("\n\n\t Error in create datasource");
		}
		dbImpl = new DataBaseImpl(tds);
	}

	@Test
	public void test() {
		if (dbImpl != null) assertTrue(dbImpl.getDataSource() != null);
		
		int iduser = dbImpl.getMaxUserId();
		
		
		
		String result = dbImpl.addUser("TestUser", "123", "+380951389592", "123@yandex.ru", DataBaseSuper.LANGUAGE_EN, "064248", "70320", "");
		assertEquals(result, DataBaseSuper.ADD_USER_SUCCESS);
		assertTrue(dbImpl.deleteUser(iduser));
		result = dbImpl.addUser("TestUser", "123", "+380951389592", "123@yandex.ru", DataBaseSuper.LANGUAGE_EN, "064248", "70320", "");
		assertEquals(result, DataBaseSuper.ADD_USER_SUCCESS);
		result = dbImpl.addUser("TestUser", "123", "+380951389592", "123@yandex.ru", DataBaseSuper.LANGUAGE_EN, "064248", "70320", "");
		assertEquals(result, "123@yandex.ru");
		
		boolean boolResult = dbImpl.updateUser( iduser, "123", "+380951389592", "dima@yandex.ru", DataBaseSuper.LANGUAGE_EN);
		assertTrue(boolResult);
		
		UserWeb user = dbImpl.getUser(iduser);
		assertEquals("dima@yandex.ru", user.getEmail());
				
		boolResult = dbImpl.addUserMeter(iduser, 56308);
		assertTrue(boolResult);
		
		assertTrue(dbImpl.getMetersList(iduser).size() == 2);
		assertTrue(dbImpl.removeUserMeter(iduser, 56308));
		assertTrue(dbImpl.getMeterById(70320).getOwneraccount().equals("064248"));
		assertTrue(dbImpl.getOwnerList("064248").size() == 1);
		
		
		
		
		assertTrue(dbImpl.deleteUserFromDB(iduser));
		
		System.out.println("\n\n\n-------------------------------------------------------");
		System.out.println("||\t1. DataBase connection is SUCCESS");
		System.out.println("||\t2. Method addUser is SUCCESS ");
		System.out.println("||\t3. Method deleteUser is SUCCESS ");
		System.out.println("||\t4. Method updateUser is SUCCESS ");
		System.out.println("||\t5. Method getUser is SUCCESS ");
		System.out.println("||\t6. Method getUserMeter is SUCCESS ");
		System.out.println("||\t7. Method getMeterList is SUCCESS ");
		System.out.println("||\t8. Method removeUserMeter is SUCCESS ");
		System.out.println("||\t9. Method getMeterById is SUCCESS ");
		System.out.println("||\t10. Method getOwnerList is SUCCESS ");
		System.out.println("||\t11. Method deleteUserFromDB is SUCCESS "); //Test done!!!
		System.out.println("||");
		System.out.println("||\tDataBaseImpl Done");
		System.out.println("-------------------------------------------------------\n\n\n");
	}

}
