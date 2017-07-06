<%@ page import="java.sql.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>

<%@page buffer="none" session="true" trimDirectiveWhitespaces="true" %>

<div>

<%--MYSQL--%>
<%--<%
  String url = "jdbc:mysql://wordpress-rds.cq90jkt2ggmk.eu-west-1.rds.amazonaws.com:3306/pim_db";
  String user = "pim_ro_user";
  String pass = "23nfaHA/-m";
  Connection con = null;
  Statement stmt = null;
  ResultSet rs = null;
  try {
    con = DriverManager.getConnection(url, user, pass);
    out.println("Establecida conexion");
    stmt = con.createStatement();
    rs = stmt.executeQuery("SELECT * FROM pim_db.view_stores;");
    while (rs.next()) {
      final String f1 = rs.getString(1);
      out.println(f1);
    }
  } catch (Exception e) {
    out.println("ERROR");
    out.println(e.getMessage());
  } finally {
    if (con != null) {
      con.close();
    }
    if (stmt != null) {
      stmt.close();
    }
    if (rs != null) {
      rs.close();
    }
  }
%>--%>

<%--ORACLE--%>
<%
  //        jdbc.drivertype=thin
//        jdbc.servername=rivendel.upo.es
//        jdbc.port=1521
//        jdbc.username=upo_data
//        jdbc.password=upo_data00
//        jdbc.servicename=epcd
//        jdbc.processEscapes=false

    /*String QUERY = "select DISTINCT p.*, a.NOMBRE_AREA, a.ID_DEPARTAMENTO" +
            " from PROFESOR p" +
            " INNER JOIN AREA a on a.ID_AREA = p.ID_AREA" +
            " where  a.ID_DEPARTAMENTO='z121'";*/

  String QUERY = "select * from PROFESOR p";

  String url = "jdbc:oracle:thin:@rivendel.upo.es:1521/epcd";
  String user = "upo_data";
  String pass = "upo_data00";
  Connection con = null;
  Statement stmt = null;
  ResultSet rs = null;
  try {
    con = DriverManager.getConnection(url, user, pass);
    out.println("<p>Establecida conexion</p>");
    stmt = con.createStatement();
    rs = stmt.executeQuery(QUERY);

    // Column names
    out.println("<h1>Cabecera</h1>");
    HashMap<String, Integer> colNameType = new HashMap<String, Integer>();
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    for (int i = 1; i <= columnCount; i++) {
      String columnName = rsmd.getColumnName(i);
      int columnType = rsmd.getColumnType(i);
      colNameType.put(columnName, columnType);
      out.println("<p>columnName: " + columnName + "(" + columnType + ")</p>");
    }

    // Dynamic Content
    out.println("<h1>Contenido</h1>");
    while (rs.next()) {
      Iterator<String> it = colNameType.keySet().iterator();
      while (it.hasNext()){
        String colName = it.next();
        String colValue = rs.getString(colName);
        out.println("<p>" + colName + ": " + colValue + "</p>");
      }
//                final String idProfesor = rs.getString(1);
//                String idProfesor = rs.getString("ID_PROFESOR");
//                out.println("<p>idProfesor: " + idProfesor + "</p>");
    }
  } catch (Exception e) {
    out.println("ERROR");
    out.println(e.getMessage());
  } finally {
    if (con != null) {
      con.close();
    }
    if (stmt != null) {
      stmt.close();
    }
    if (rs != null) {
      rs.close();
    }
  }
%>
</div>