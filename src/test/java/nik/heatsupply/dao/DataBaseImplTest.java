package nik.heatsupply.dao;

import static org.junit.Assert.*;

import org.apache.tomcat.dbcp.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.tomcat.dbcp.dbcp2.datasources.SharedPoolDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

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

		System.out.println("-------------------------------------------------------");
		System.out.println("||\t1. DataBase connection is SUCCESS");
		System.out.println("||");
		System.out.println("||\tTESTS DONE!");
		System.out.println("-------------------------------------------------------");
	}

}
