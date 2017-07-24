<%@ page import="java.io.InputStream" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.chemistry.opencmis.commons.impl.json.JSONObject" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.util.CmsRequestUtil" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="java.util.List" %>
<%!
    Log LOG = CmsLog.getLog(this.getClass());
%>
<%
    JSONObject jRes = new JSONObject();
    try {
//        Part file = request.getPart("file");
        // Subimos los ficheros a la carpeta temporal (WEB-INF/packages/)
        List<FileItem> fileItems = CmsRequestUtil.readMultipartFileItems(request);
        FileItem file = fileItems.get(0);

        String filename = file.getName();
        LOG.debug("Filename: " + filename);

        long size = file.getSize();
        LOG.debug("Size: " + size);

        String contentType = file.getContentType();
        LOG.debug("ContentType: " + contentType);

        // JSON Response
        jRes.put("error", false);
        jRes.put("filename", filename);
        jRes.put("size", size);
        jRes.put("contentType", contentType);
//        InputStream filecontent = file.getInputStream();
        // ... Do your file saving job here.


//        response.setContentType("text/plain");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write("File " + filename + " successfully uploaded");
    } catch (Exception e) {
        LOG.error(e);
        jRes.put("error", true);
        jRes.put("errorMsg", e.getMessage());
        jRes.put("errorStacktrace", CmsException.getStackTraceAsString(e));
    } finally {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jRes.toString());
    }

%>