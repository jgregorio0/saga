<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.io.BufferedInputStream" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileInputStream" %>

<%
    String filename = "/com.saga.caprabo.frontend_1.1.zip";
    String folder = "packages/modules";
    String webInfPath = OpenCms.getSystemInfo().getWebInfRfsPath();
    String filepath = webInfPath + folder + filename;
    response.setContentType("application/zip");
    response.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");

    byte[] buf = new byte[1024];
    File file = new File(filepath);
    long length = file.length();
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
    ServletOutputStream os = response.getOutputStream();
    response.setContentLength((int) length);
    while ((in != null) && ((length = in.read(buf)) != -1)) {
        os.write(buf, 0, (int) length);
    }
    in.close();
    os.close();
%>