<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.util.Scanner" %>

<%
    String filename = "opencms-modules.xml";
    String fileFolder = "config/";
    String webInfPath = OpenCms.getSystemInfo().getWebInfRfsPath();

    String filepath = webInfPath + fileFolder + filename;
    response.setContentType("application/zip");
    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

    ServletOutputStream os = response.getOutputStream();
    FileInputStream inputStream = null;
    Scanner sc = null;
    try {
        inputStream = new FileInputStream(filepath);
        sc = new Scanner(inputStream, "UTF-8");
        while (sc.hasNextLine()) {
            os.write(sc.nextLine().getBytes());
        }
        // note that Scanner suppresses exceptions
        if (sc.ioException() != null) {
            throw sc.ioException();
        }
    } finally {
        if (inputStream != null) {
            inputStream.close();
        }
        if (os != null) {
            os.close();
        }
        if (sc != null) {
            sc.close();
        }
    }
%>