<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.fins.gt.server.GridServerHandler" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.safehaus.uuid.UUIDGenerator" %>
<%@ page import="org.safehaus.uuid.UUID" %>
<%@ page import="org.opencms.security.CmsPasswordEncryptionException" %>
<%@ page import="org.opencms.util.CmsUUID" %>
<%@ page import="org.opencms.security.I_CmsPrincipal" %>
<%@ page import="com.fins.org.json.JSONObject" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>


<%!
	public static String queryList =
					"SELECT USER_ID," +
							" USER_NAME," +
							" USER_FIRSTNAME," +
							" USER_LASTNAME," +
							" USER_EMAIL," +
							" USER_DATECREATED" +
					" FROM opencms952.CMS_USERS;";

	public static String queryInsert =
					"INSERT INTO opencms952.CMS_USERS" +
							" (USER_ID," +
							" USER_NAME," +
							" USER_PASSWORD," +
							" USER_FIRSTNAME," +
							" USER_LASTNAME," +
							" USER_EMAIL," +
							" USER_LASTLOGIN," +
							" USER_FLAGS," +
							" USER_OU," +
							" USER_DATECREATED)" +
							" VALUES" +
							"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

	public static String queryUpdate = 
					"UPDATE opencms952.CMS_USERS" +
							" SET USER_NAME = ?," +
							" USER_FIRSTNAME = ?," +
							" USER_LASTNAME = ?," +
							" USER_EMAIL = ?," +
							" WHERE USER_ID = ?;";

	public static String queryDelete =
					"DELETE FROM opencms952.CMS_USERS" +
							" WHERE USER_ID = ?;";

	final Log LOG = CmsLog.getLog(this.getClass());
	private String pool;
	private CmsObject cmso;

	Connection getConnection(){
		Connection conn = null;
		String poolName = (pool != null && pool.length() > 0) ? pool : "default";
		try {
			conn = OpenCms.getSqlManager().getConnection(poolName);
		} catch (Exception e) {
			LOG.error("ERROR obteniendo conexion", e);
		}
		return conn;
	}

	void close(ResultSet rs, Statement stmt, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				LOG.error("ERROR cerrando resultados", e);
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				LOG.error("ERROR cerrando estado", e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				LOG.error("ERROR cerrando conexion", e);
			}
		}

	}

	List list(){
	   Connection conn = getConnection();
	   if(conn==null)
	 		return new ArrayList();

	   Statement stmt = null;
	   ResultSet rs = null;
	   List list = new ArrayList();

	   try{
		   stmt = conn.createStatement();
		   rs = stmt.executeQuery(queryList);
		   while(rs.next()){
				Map map = new HashMap();
				map.put("USER_ID",rs.getString("USER_ID"));
				map.put("USER_NAME",rs.getString("USER_NAME"));
			    map.put("USER_FIRSTNAME",rs.getString("USER_FIRSTNAME"));
			    map.put("USER_LASTNAME",rs.getString("USER_LASTNAME"));
				map.put("USER_EMAIL",rs.getString("USER_EMAIL"));
				map.put("USER_DATECREATED",new Long(rs.getLong("USER_DATECREATED")));
				list.add(map);
		   }
	   }catch(Exception e){
		   LOG.error("ERROR obteniendo lista", e);
	   } finally {
		   close(rs, stmt, conn);
	   }
	   return list;
	}

	int[] insert(List updatedList){
		int[] opresults=null;
		Connection conn=null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(queryInsert);
			for (int i=0;i<updatedList.size();i++){
				Map record= (Map)updatedList.get(i);
				String id = UUIDGenerator.getInstance().generateRandomBasedUUID().toString();
				pstmt.setString(1, id);
				pstmt.setString(2, String.valueOf(record.get("USER_NAME")));
				pstmt.setString(3, OpenCms.getPasswordHandler().digest("123456"));
				pstmt.setString(3, String.valueOf(record.get("USER_FIRSTNAME")));
				pstmt.setString(4, String.valueOf(record.get("USER_LASTNAME")));
				pstmt.setString(5, String.valueOf(record.get("USER_EMAIL")));
				pstmt.setLong(6, 0L);
				pstmt.setInt(7, I_CmsPrincipal.FLAG_ENABLED);
				pstmt.setString(8, "/");
				pstmt.setLong(9, 0L);
				pstmt.addBatch();
			}
			opresults = pstmt.executeBatch();
		} catch (SQLException e) {
			LOG.error("ERROR insertando registros", e);
			opresults=null;
		} catch (CmsPasswordEncryptionException e) {
			LOG.error("ERROR insertando registros - generando password", e);
			opresults=null;
		} finally{
			close(null, pstmt, conn);
		}
		return opresults;
	}

	int[] update(List updatedList){
		int[] opresults=null;
		Connection conn=null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(queryUpdate);
			for (int i=0;i<updatedList.size();i++){
				Map record= (Map)updatedList.get(i);
				pstmt.setString(1,String.valueOf(record.get("USER_NAME")));
				pstmt.setString(2,String.valueOf(record.get("USER_FIRSTNAME")));
				pstmt.setString(3,String.valueOf(record.get("USER_LASTNAME")));
				pstmt.setString(4,String.valueOf(record.get("USER_EMAIL")));
				pstmt.setString(4,String.valueOf(record.get("USER_ID")));
				pstmt.addBatch();
			}
			opresults = pstmt.executeBatch();
		} catch (SQLException e) {
			LOG.error("ERROR actualizando registros", e);
			opresults=null;
		}finally{
			close(null, pstmt, conn);
		}
		return opresults;
	}

	int[] delete(List updatedList){
		int[] opresults=null;
		Connection conn=null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(queryDelete);
			for (int i=0;i<updatedList.size();i++){
				Map record= (Map)updatedList.get(i);
				pstmt.setString(1,String.valueOf(record.get("USER_ID")));
				pstmt.addBatch();
			}
			opresults = pstmt.executeBatch();
		} catch (SQLException e) {
			LOG.error("ERROR borrando registros", e);
			opresults=null;
		}finally{
			close(null, pstmt, conn);
		}
		return opresults;
	}

	boolean saveOrders(List insertedRecords , List updatedList, List deletedRecords){
		//you can control transaction, commit, rollback here
		int[] insertCodes = insert(insertedRecords);
		int[] updateCodes = update(updatedList);
		int[] deleteCodes = delete(deletedRecords);
		boolean success=insertCodes!=null && updateCodes!=null && deleteCodes!=null;
		return success;
	}

%>
<%
	// GridServerHandçler is server side wrapper, you can get all the info posted to server in your Java way instead of JavaScript
	GridServerHandler gridServerHandler=new GridServerHandler(request,response);

	String gt_json = request.getParameter("_gt_json");
	JSONObject json = new JSONObject(gt_json);
	pool = (String) json.getJSONObject("parameters").get("pool");
	String operation = request.getParameter("actionMethod");
	if("save".equals(operation)){

//		boolean success=true;
//
//		//to get the appended records here. Every record is in a map
//		List insertedRecords = gridServerHandler.getInsertedRecords();
//		//to get the updated records here. Every record is in a map
//		List updatedList = gridServerHandler.getUpdatedRecords();
//		//to get the deleted records here. Every record is in a map
//		List deletedRecords = gridServerHandler.getDeletedRecords();
//
//
//		// if you are using beanm, you could get records with : xxx.getXXXXXXRecords(Class beanClass);
//		//example - List updateList = gridServerHandler.getUpdatedRecords(OrderVO.class);
//
//		//to do update delete insert on real database
//		success = saveOrders(insertedRecords , updatedList,  deletedRecords );
//
//
//		//set result
//		gridServerHandler.setSuccess(success);
//
//		//if failure, you could send some message to client
//    //gridServerHandler.setSuccess(false);
//    //gridServerHandler.setException("... exception info ...");
//
//		//to print out JSON string to client
//		out.print(gridServerHandler.getSaveResponseText());

	}else { //client is retrieving data
		List list = list();
		
    //get how many records we are sending
		int totalRowNum= list.size();
		gridServerHandler.setTotalRowNum(totalRowNum);
		
		//if you would like paginal output on server side, you may interested in the following 4 methods
		// gridServerHandler.getStartRowNum() first record no of current page
		// gridServerHandler.getEndRowNum() last record no of current page
		// gridServerHandler.getPageSize() how many records per page holds
		// gridServerHandler.getTotalRowNum() how many records in total
		
		
		// we take map as this sample, you need to use gridServerHelp.setData(list,BeanClass.class); to deal with bean
		gridServerHandler.setData(list);
	  // gridServerHandler.setException("your exception message");
		
		//print out JSON string to client
		out.print(gridServerHandler.getLoadResponseText());
		
		//you could get the posted data by calling gridServerHandler.getLoadResponseText() and obtain more flexibility, such as chaning contentType or encoding of response.
	}
%>