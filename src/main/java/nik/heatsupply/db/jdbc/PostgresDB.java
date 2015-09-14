package nik.heatsupply.db.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.db.jdbc.mappers.IMapper;

public class PostgresDB {
	private static final Logger LOG = LoggerFactory.getLogger(PostgresDB.class);
	private SqlSessionFactory sqlSessionFactory;
	private String connStr;
	
	public PostgresDB () {
		setMappers(ConnectDB.getDataSource());
		
		try {
			Connection conn = ConnectDB.getDataSource().getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			connStr = dbmd.getURL();
			connStr = connStr.substring(connStr.indexOf("://") + 3);
			String ip = connStr.substring(0, connStr.indexOf("/"));
			if(connStr.indexOf("?") > 0) {
				connStr = connStr.substring(connStr.indexOf("/") + 1);
				connStr = ip + "_" + connStr.substring(0, connStr.indexOf("?"));
			} else {
				connStr = connStr.replace("/", "_");
			}
		} catch (SQLException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	private void setMappers(DataSource dataSource) {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(IMapper.class);
		//configuration.addMappers("jdbc.mappers");
		configuration.addMapper(BaseMapper.class);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
	
	public String getConnStr() {
		return connStr;
	}

	//	=============================================================================================
	public static String getQuery(Map<String, Object> params) {
		return params.get("query").toString();
	}

	public interface BaseMapper {
		@SelectProvider(type = PostgresDB.class, method = "getQuery")
		void update(@Param("query") String query);
	}
}