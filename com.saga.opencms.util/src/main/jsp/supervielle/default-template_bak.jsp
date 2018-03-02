<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.i18n.CmsEncoder" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.main.CmsException" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%!

    private static String END_CONTROL_CODE_DEFINITION = "?>";

    public String cleanControlCode(String text) {
        String cleanText = text;
        int pos = cleanText.indexOf(END_CONTROL_CODE_DEFINITION);
        if (pos > 0) {
            cleanText = cleanText.substring(pos + END_CONTROL_CODE_DEFINITION.length());
        }
        return cleanText;
    }
%>

<%--<c:if test="${!cms.isOnlineProject}">--%>
<c:choose>
    <c:when test="${param.edit}">
        <!--=== Placeholder for OpenCms toolbar in edit mode ===-->
        <div id="toolbar-placeholder" style="height: 52px;background: #eee"></div>

        <cms:enable-ade/>
        <cms:container name="container" type="element" width="1200" maxElements="1" editableby="ROLE.DEVELOPER"/>
    </c:when>
    <c:otherwise>
        <%
            String result = "";
            try {
                CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
                String path = "/.content/flexiblecontents/fc_00001.xml";
                CmsFile file = cmso.readFile(path, CmsResourceFilter.ALL);
                String content = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
                String xml = StringEscapeUtils.escapeXml(content);
                /*String cleanXml = cleanControlCode(xml);
                GPathResult gPathResult = new XmlSlurper(false, true, true).parseText(cleanXml);
                result = gPathResult.text();*/
                result = xml;
            } catch (Exception e) {
                result = "ERROR - " + CmsException.getStackTraceAsString(e);
            } finally {
                response.setContentType("text/xml");
                response.getWriter().write(result);
            }
        %>
    </c:otherwise>
</c:choose>
