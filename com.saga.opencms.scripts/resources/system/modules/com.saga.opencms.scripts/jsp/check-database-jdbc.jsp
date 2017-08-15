<%@ page import="java.sql.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>

<%@page buffer="none" session="true" trimDirectiveWhitespaces="true" %>

<div>

<%--MYSQL--%>
<%--<%
  String url =
  String user =
  String pass =
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
//        jdbc.servername=
//        jdbc.port=
//        jdbc.username=
//        jdbc.password=
//        jdbc.servicename=
//        jdbc.processEscapes=


  String QUERY = "select * from PROFESOR p";

  String url =
  String user =
  String pass =
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