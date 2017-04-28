package com.saga.opencms.util

import org.apache.commons.logging.Log
import org.opencms.main.CmsLog

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

public class SgJdbc {

	private static final Log LOG = CmsLog.getLog(SgJdbc.class);

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
}