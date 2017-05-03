package com.saga.opencms.util

import org.apache.commons.logging.Log
import org.opencms.configuration.CmsParameterConfiguration
import org.opencms.db.CmsDbPool
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms

import javax.annotation.Nullable
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
	public static void closeAll(@Nullable Connection con, @Nullable Statement statement, @Nullable ResultSet res) {
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
	public static void reconnect(String ocmsPropsFilePath, String poolName) {
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
	public static Connection tryConnection(String poolName){
		Connection con = null;
		try {
			con = OpenCms.getSqlManager().getConnection(poolName);
		} catch (Exception e) {
			LOG.error("connection to " + poolName, e);
		}
		return con;
	}

	/**
	 * Try to get connection from OpenCms pool.
	 * If it is not accessible try to reconnect using file opencms.properties
	 * @param poolName
	 * @param fileForReconnect
	 * @return
	 */
	public static Connection tryConnection(String poolName, String fileForReconnect){
		Connection con = tryConnection(poolName);
		if (con == null) {
			reconnect(fileForReconnect, poolName);
			con = tryConnection(poolName);
		}
		return con;
	}

	/**
	 * Check if connection is available
	 */
	public static boolean checkConnection(String poolName){
		boolean openCon = false;
		Connection con = tryConnection(poolName);
		if (con == null) {
			openCon = false;
		} else {
			openCon = true;
		}
		closeAll(con, null, null);
		return openCon;
	}
}