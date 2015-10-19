package nik.heatsupply.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.dao.mappers.IMapper;

public class DataBaseSuper {
	private static final Logger LOG = LoggerFactory.getLogger(DataBaseSuper.class);
	private static final String DATASOURCE_NAME = "heatSupplyDS";
	private SqlSessionFactory sqlSessionFactory;
	private static DataSource dsLocal;
	private static Context context = null;
	public static String ADD_USER_SUCCESS = "0";
	public static String ADD_USER_TRY_AGAIN = "1";
	
	public DataBaseSuper() {
		setMappers(getDataSource());
	}
	private void setMappers(DataSource dataSource) {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(IMapper.class);
		//configuration.addMappers("jdbc.mappers");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}
	
	public DataSource getDataSource() {
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
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
}