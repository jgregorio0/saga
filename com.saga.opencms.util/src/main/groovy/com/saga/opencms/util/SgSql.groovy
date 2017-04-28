package com.saga.opencms.util

import org.apache.commons.logging.Log
import org.opencms.configuration.CmsParameterConfiguration
import org.opencms.db.CmsDbPool
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

public class SgSql {

	private static final Log LOG = CmsLog.getLog(SgSql.class);

	/**
	 * Constantes para la busqueda de Solr
	 */
	public static final String P_PARENT_FOLDER = "parentFolders";

	/**
	 * Ensure close all
	 * @param con
	 * @param statement
	 * @param res
	 */
	public static void closeAll(Connection con, Statement statement, ResultSet res) {
		if(res != null) {
			try {
				res.close();
			} catch (Exception e) {
				LOG.error("closing resultset");
			}
		}

		if(statement != null) {
			try {
				statement.close();
			} catch (Exception var6) {
				LOG.error("closing statement");
			}
		}

		if(con != null) {
			try {
				if(!con.isClosed()) {
					con.close();
				}
			} catch (Exception var5) {
				LOG.error("closing connection");
			}
		}
	}

	/**
	 * Reconnect OpenCms pool
	 * @param ocmsPropsFilePath
	 * @param poolName
	 */
	private void reconect(String ocmsPropsFilePath, String poolName) {
		Connection con = null;
		try {
			final CmsParameterConfiguration cmsParamsConfig =
					new CmsParameterConfiguration(ocmsPropsFilePath);
			CmsDbPool.createDriverManagerConnectionPool(cmsParamsConfig, poolName);
		} catch (Exception e){
			LOG.error("reconnecting using file " + ocmsPropsFilePath, e);
		}
	}

	/**
	 * Try to get connection from OpenCms pool
	 * @param poolName
	 * @return
	 */
	private Connection tryConnection(String poolName){
		Connection con = null;
		try {
			con = OpenCms.getSqlManager().getConnection(poolName);
		} catch (Exception e) {
			LOG.error("connection to " + poolName, e);
		}
		return con;
	}
}