<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="javax.annotation.Nullable" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="org.opencms.configuration.CmsParameterConfiguration" %>
<%@ page import="org.opencms.db.CmsDbPool" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@page buffer="none" session="true" trimDirectiveWhitespaces="true" %>

<%!
    private final Log LOG = CmsLog.getLog(this.getClass());

    /**
     * Ensure close all
     * @param con
     * @param statement
     * @param res
     */
    public void closeAll(@Nullable Connection con, @Nullable Statement statement, @Nullable ResultSet res) {
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
    public void reconnect(String ocmsPropsFilePath, String poolName) {
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
    public Connection tryConnection(String poolName){
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
    public Connection tryConnection(String poolName, String fileForReconnect){
        Connection con = tryConnection(poolName);
        if (con == null) {
            reconnect(fileForReconnect, poolName);
            con = tryConnection(poolName, fileForReconnect);
        }
        return con;
    }

    /**
     * Check if connection is available
     */
    public boolean checkConnection(String poolName){
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
%>

<%
    String pool = "aldipim";
    boolean aldipim = checkConnection(pool);
    LOG.debug("Connected to adlipim -- " + aldipim);
    if (!aldipim) {
        String webInfPath = OpenCms.getSystemInfo().getWebInfRfsPath();
        String folder = "config";
        String file = "opencms.properties";
        String propertiesFilePath = webInfPath + "/" + folder + "/" + file;
        LOG.debug("Try to reconect using property file " + propertiesFilePath);
        reconnect(propertiesFilePath, pool);
        aldipim = checkConnection(pool);
        LOG.debug("Reconnected to adlipim -- " + aldipim);
    }
%>