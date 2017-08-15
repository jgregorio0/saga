<%@ page import="java.sql.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>

<%@page buffer="none" session="true" trimDirectiveWhitespaces="true" %>

<div>

  <%--MYSQL--%>
  <%
    String url =
    String user =
    String pass =

    String QUERY = "SELECT * FROM pim_db.view_products WHERE HOUR(TIMEDIFF(NOW(), date_lastupdate)) <= 26;";

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      con = DriverManager.getConnection(url, user, pass);
      out.println("<p>Establecida conexion</p>");
      stmt = con.createStatement();
      rs = stmt.executeQuery(QUERY);

      // Column names
//    out.println("<h1>Cabecera</h1>");
      out.println("<table style=\"width:100%\">");
      out.println("<tr>");
      HashMap<String, Integer> colNameType = new HashMap<String, Integer>();
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnCount = rsmd.getColumnCount();
      out.println("<th>Num</th>");
      for (int i = 1; i <= columnCount; i++) {
        out.println("<th>");
        String columnName = rsmd.getColumnName(i);
        int columnType = rsmd.getColumnType(i);
        colNameType.put(columnName, columnType);
        out.println(columnName + "(" + columnType + ")");
        out.println("</th>");
      }
      out.println("</tr>");

      // Dynamic Content
//    out.println("<h1>Contenido</h1>");
      int count = 0;
      while (rs.next()) {
        out.println("<tr>");
        count++;
        out.println("<td>" + count + "</td>");
        Iterator<String> it = colNameType.keySet().iterator();
        while (it.hasNext()){
          String colName = it.next();
          String colValue = rs.getString(colName);
//        out.println("<p>" + colName + ": " + colValue + "</p>");
          out.println("<td>" + colValue + "</td>");
        }
        out.println("</tr>");
      }
      out.println("</table>");
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