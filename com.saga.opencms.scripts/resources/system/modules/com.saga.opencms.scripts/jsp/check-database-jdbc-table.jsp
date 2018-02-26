<%@ page import="java.sql.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>

<%@page buffer="none" session="true" trimDirectiveWhitespaces="true" %>

<div>

    <%
        String QUERY_ASIGNATURAS_EQ_TIT_DIFF_CENTRO =
                "select DISTINCT p.*, a.NOMBRE_AREA, a.ID_DEPARTAMENTO"
                        + " from PROFESOR p"
                        + " INNER JOIN AREA a on a.ID_AREA = p.ID_AREA"
                        + " INNER JOIN ASIGN_PROFE asig on asig.ID_PROFESOR = p.ID_PROFESOR"
//                        + " where p.ID_PROFESOR=?";
                        + " GROUP BY p.ID_PROFESOR";

        // TODO modify conection data
        String url = "jdbc:oracle:thin:@{URL}:{PORT}/{SCHEMA}";
        String user = "USER";
        String pass = "PASS";
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(url, user, pass);
            out.println("<p>Establecida conexion</p>");
            stmt = con.createStatement();
            rs = stmt.executeQuery(QUERY_ASIGNATURAS_EQ_TIT_DIFF_CENTRO);

//            METADATA
            HashMap<String, Integer> colNameType = new HashMap<String, Integer>();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                int columnType = rsmd.getColumnType(i);
                colNameType.put(columnName, columnType);
            }

//            TABLE HEADER
            out.println("<table style=\"width:100%\">");
            out.println("<tr>");
            out.println("<th>Num</th>");
            Iterator<String> it = colNameType.keySet().iterator();
            while (it.hasNext()) {
                String colName = it.next();
                Integer colType = colNameType.get(colName);
//        out.println("<p>" + colName + ": " + colValue + "</p>");
                out.println("<th>" + colName + "(" + colType + ")" + "</th>");
            }

            out.println("</tr>");

//            TABLE BODY
            int count = 0;
            while (rs.next()) {
                out.println("<tr>");
                count++;
                out.println("<td>" + count + "</td>");
                it = colNameType.keySet().iterator();
                while (it.hasNext()) {
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