package nik.heatsupply.dao;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.socket.Server;

public class BatisImpl {
	private static final Logger LOG = LoggerFactory.getLogger(BatisImpl.class);
	private static final int MAX_REPET = 5;
	private IBatis iBatis;
	private IBatis[] iCollection;
	private int count;
	private boolean isCommit = true;
	private SqlSession session;
	
	public BatisImpl(IBatis iBatis) {
		this.iBatis = iBatis;
	}
	
	public BatisImpl(IBatis[] iCollection) {
		this.iCollection = iCollection;
	}

	public void setCommit(boolean isCommit) {
		this.isCommit = isCommit;
	}

	public Object get() {
		while (count < MAX_REPET) {
			session = null;
			try {
				if(Server.dbImpl.getDataSource() != null) {
					session = Server.dbImpl.getSqlSessionFactory().openSession(isCommit);
					try {
						return iBatis.getResult(session);
					} catch (Exception e) {
						if(count == MAX_REPET - 1) LOG.error(ExceptionUtils.getStackTrace(e));
					} finally {
						if (session != null) session.close();
					}
				} else {
					Thread.sleep(1000);
					Server.dbImpl = new DataBaseImpl();
				}
			} catch (Exception e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
			}
			count++;
		}
		return null;
	}
	
	public boolean run() {
		while (count < MAX_REPET) {
			session = null;
			try {
				if(Server.dbImpl.getDataSource() != null) {
					session = Server.dbImpl.getSqlSessionFactory().openSession(isCommit);
					iBatis.getResult(session);
					return true;
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				if(count == MAX_REPET - 1) LOG.error(ExceptionUtils.getStackTrace(e));
			} finally {
				if (session != null) session.close();
			}
			count++;
		}
		return false;
	}
	
	public boolean runCollection() {
		while (count < MAX_REPET) {
			session = null;
			try {
				if(Server.dbImpl.getDataSource() != null) {
					session = Server.dbImpl.getSqlSessionFactory().openSession(false);
					for(int i = 0; i < iCollection.length; i++) {
						iBatis = iCollection[i];
						if(iBatis != null) iBatis.getResult(session);
					}
					session.commit();
					return true;
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				if(session != null) session.rollback();
				if(count == MAX_REPET - 1) LOG.error(ExceptionUtils.getStackTrace(e));
			} finally {
				if (session != null) session.close();
			}
			count++;
		}
		return false;
	}
}