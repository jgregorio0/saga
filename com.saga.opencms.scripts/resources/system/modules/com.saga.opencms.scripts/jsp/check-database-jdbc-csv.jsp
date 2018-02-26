<%@ page import="java.sql.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.opencms.main.CmsException" %>

<%@page buffer="none" session="true" trimDirectiveWhitespaces="true" %>

<%--MYSQL--%>
<%
  String url = "jdbc:mysql://{URL}:{PORT}/{SCHEMA}";
  String user = "pim_ro_user";
  String pass = "23nfaHA/-m";

    String colDel = "{{SEP_COLUMNA}}";
    String rowDel = "{{SEP_FILA}}";

  String QUERY = "SELECT * FROM pim_db.view_products WHERE HOUR(TIMEDIFF(NOW(), date_lastupdate)) <= 26;";

  Connection con = null;
  Statement stmt = null;
  ResultSet rs = null;
  try {
    con = DriverManager.getConnection(url, user, pass);
//    out.println("<p>Establecida conexion</p>");
    stmt = con.createStatement();
    rs = stmt.executeQuery(QUERY);

    // Column names
//    out.println("<h1>Cabecera</h1>");
//    out.println("<table style=\"width:100%\">");
//    out.println("<tr>");
    HashMap<String, Integer> colNameType = new HashMap<String, Integer>();
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    out.print("Num");
    out.print(colDel);
    for (int i = 1; i <= columnCount; i++) {
//      out.println("<th>");
      String columnName = rsmd.getColumnName(i);
      int columnType = rsmd.getColumnType(i);
      colNameType.put(columnName, columnType);
      out.print(columnName + "(" + columnType + ")");
        out.print(colDel);
//      out.println("</th>");
    }
    out.print(rowDel);

    // Dynamic Content
//    out.println("<h1>Contenido</h1>");
    int count = 0;
    while (rs.next()) {
//      out.println("<tr>");
      count++;
      out.print(count);
      out.print(colDel);
      Iterator<String> it = colNameType.keySet().iterator();
      while (it.hasNext()){
        try {
          String colName = it.next();
          String colValue = rs.getString(colName);
          out.print(colValue);
            out.print(colDel);
        } catch (Exception e) {
//          out.print(e.getMessage() + " --- " + CmsException.getStackTraceAsString(e) + ";");
          out.print(e.getMessage());
            out.print(colDel);
        }
      }
        out.print(rowDel);
    }
//    out.println("</table>");
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