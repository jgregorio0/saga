<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.opencms.db.CmsPublishList" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.util.List" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.report.I_CmsReport" %>
<%@ page import="org.opencms.file.CmsProject" %>
<%@ page import="org.opencms.report.CmsLogReport" %>
<%@ page import="org.opencms.publish.CmsPublishManager" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>

<%@page buffer="none" session="true" trimDirectiveWhitespaces="true" %>
<%!
    /**
     * Publish list of resources
     * Resource list must not contain null objects
     * @param pubList
     * @return
     */
    public CmsPublishList publish(CmsObject cmso, List<CmsResource> pubList) throws CmsException {
        CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, true, true);
        OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        return toPublish;
    }

    /**
     * Publish one resource
     * @param cmso
     * @param path
     */
    public void publish(CmsObject cmso, String path) throws Exception {
        OpenCms.getPublishManager().publishResource(cmso, path);
    }

    /**
     * Based on CmsDeleteExpiredResourcesJob.
     * Initialize publishing by creating new project
     * @param cmso
     * @return
     */
    public I_CmsReport initPublishing(CmsObject cmso) throws CmsException {
        CmsProject project = cmso.createTempfileProject();
        cmso.getRequestContext().setCurrentProject(project);
        I_CmsReport report = new CmsLogReport(
                cmso.getRequestContext().getLocale(),
                this.getClass());
        return report;
    }

    /**
     * Based on CmsDeleteExpiredResourcesJob.
     * Execute publishing for resources modified on project
     * @param cmso
     * @param report
     */
    public void exePublishing(CmsObject cmso, I_CmsReport report) throws CmsException {
        CmsPublishManager publishManager = OpenCms.getPublishManager();
        publishManager.publishProject(cmso, report);
        // this is to not scramble the logging output:
        publishManager.waitWhileRunning();
    }

    /**
     * Based on CmsDeleteExpiredResourcesJob.
     * Execute publishing for resources modified on project
     * @param cmso
     * @param report
     */
    public void publish(CmsObject cmso, Class clazz, String resourcename) throws CmsException {
        I_CmsReport report = new CmsLogReport(
                cmso.getRequestContext().getLocale(),
                clazz);
        CmsPublishManager publishManager = OpenCms.getPublishManager();
        CmsResource resource = cmso.readResource(resourcename, CmsResourceFilter.ALL);
        publishManager.publishProject(cmso, report, resource, true);
        publishManager.waitWhileRunning();
    }
%>

<%
    String error = null;
    try {
        CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
//        publish(cmso, "/shared/.content/sgtechnicaldocumentation");
//        I_CmsReport report = initPublishing(cmso);
//        EXECUTE CODE

        publish(cmso, this.getClass(), "/shared/.content/sgtechnicaldocumentation");
    } catch (Exception e) {
        error = CmsException.getStackTraceAsString(e);
    }
%>
<c:choose>
    <c:when test="<%=error != null%>">
        <h1>ERROR</h1>
        <p><%=error%></p>
    </c:when>
    <c:otherwise>
        <h1>PUBLICADO!!!</h1>
    </c:otherwise>
</c:choose>

