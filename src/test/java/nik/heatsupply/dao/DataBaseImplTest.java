package nik.heatsupply.dao;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.tomcat.dbcp.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.tomcat.dbcp.dbcp2.datasources.SharedPoolDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import nik.heatsupply.socket.model.MeterUser;
import nik.heatsupply.socket.model.UserWeb;

public class DataBaseImplTest {
	private static DataBaseImpl dbImpl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {		
		Path p = Paths.get(DataBaseImpl.class.getClassLoader().getResource(".").toURI()).getParent().getParent();
		File context = new File(p + "/src/main/webapp/META-INF/context.xml");
		Properties props = getSourceProperties(context, "heatSupplyDS");
		
		SharedPoolDataSource tds = null;
		try {
			DriverAdapterCPDS cpds = new DriverAdapterCPDS();
			cpds.setDriver(props.getProperty("driverClassName"));

			cpds.setUrl(props.getProperty("url"));
			cpds.setUser(props.getProperty("username"));
			cpds.setPassword(props.getProperty("password"));

			tds = new SharedPoolDataSource();
			tds.setConnectionPoolDataSource(cpds);
			tds.setMaxTotal(Integer.parseInt(props.getProperty("maxActive")));
			tds.setDefaultMaxWaitMillis(Integer.parseInt(props.getProperty("maxWait")));
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
		int idUser = dbImpl.getMaxUserId();

		String result = dbImpl.addUser("TestUser", "123", "+380951389592", "123@yandex.ru", DataBaseSuper.LANGUAGE_EN, "064248", "70320", "");
		assertEquals(result, DataBaseSuper.ADD_USER_SUCCESS);
		assertTrue(dbImpl.deleteUser(idUser));
		result = dbImpl.addUser("TestUser", "123", "+380951389592", "123@yandex.ru", DataBaseSuper.LANGUAGE_EN, "064248", "70320", "");
		assertEquals(result, DataBaseSuper.ADD_USER_SUCCESS);
		result = dbImpl.addUser("TestUser", "123", "+380951389592", "123@yandex.ru", DataBaseSuper.LANGUAGE_EN, "064248", "70320", "");
		assertEquals(result, "123@yandex.ru");
		
		boolean boolResult = dbImpl.updateUser(idUser, "123", "+380951389592", "dima@yandex.ru", DataBaseSuper.LANGUAGE_EN);
		assertTrue(boolResult);
		
		UserWeb user = dbImpl.getUser(idUser);
		assertEquals("dima@yandex.ru", user.getEmail());
		user = dbImpl.getUserByEmail(user.getEmail());
		assertEquals("dima@yandex.ru", user.getEmail());
		
		MeterUser meterUser = dbImpl.getMetersList(idUser).get(0);
		boolResult = dbImpl.addMeterUser(idUser, 56308, meterUser.getLastcash(), meterUser.getIdTypeDevice(), meterUser.getIdAccount());
		assertTrue(boolResult);
		
		assertTrue(dbImpl.getMetersList(idUser).size() == 2);
		assertTrue(dbImpl.removeUserMeter(idUser, 56308));
		assertTrue(dbImpl.getMeterById(70320).getOwneraccount().equals("064248"));
		assertTrue(dbImpl.getOwnerList("064248").size() == 1);

		assertTrue(dbImpl.deleteUserFromDB(idUser));
		
		System.out.println("\n\n\n-------------------------------------------------------");
		System.out.println("||\t1. DataBase connection is SUCCESS");
		System.out.println("||\t2. Method addUser is SUCCESS ");
		System.out.println("||\t3. Method deleteUser is SUCCESS ");
		System.out.println("||\t4. Method updateUser is SUCCESS ");
		System.out.println("||\t5. Method getUser & getUserByEmail is SUCCESS ");
		System.out.println("||\t6. Method getUserMeter is SUCCESS ");
		System.out.println("||\t7. Method getMeterList is SUCCESS ");
		System.out.println("||\t8. Method removeUserMeter is SUCCESS ");
		System.out.println("||\t9. Method getMeterById is SUCCESS ");
		System.out.println("||\t10. Method getOwnerList is SUCCESS ");
		System.out.println("||\t11. Method deleteUserFromDB is SUCCESS "); 
		System.out.println("||");
		System.out.println("||\tDataBaseImpl Done");
		System.out.println("-------------------------------------------------------\n\n\n");
	}

	private static Properties getSourceProperties(File file, String sourceName) {
		Properties props = new Properties();
		
		boolean isNotCloseTag = false;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if(line.indexOf(sourceName) > 0) {
					isNotCloseTag = true;
					line = line.substring(line.indexOf(sourceName) + sourceName.length() + 2);
				}
				if(isNotCloseTag) {
					if(line.indexOf("/>") > 0) {
						isNotCloseTag = false;
						line = line.substring(0, line.length() - 2);
					}
					String[] arr = line.trim().split(" ");
					for (String prop : arr) {
						String key = prop.substring(0, prop.indexOf("="));
						String value = prop.substring(prop.indexOf("=") + 2, prop.length() - 1);
						props.put(key, value);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
}
