<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.util.CmsUUID" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="caprabo" tagdir="/WEB-INF/tags/saga/caprabochef" %>

<%!
    /**
     * Execute favourite tag for resource:
     * structureId: structure id of resource
     * filePath (/.content/...): file path of resource
     * detailPath (/es/recetas/recetas/Vasitos-tricolor-con-queso-de-cabra): detail page of resource
     * IMPORTANTE: detailPath no puede terminar en "/" si no es una carpeta
     */
    private final Log LOG = CmsLog.getLog(this.getClass());
%>

<%
    CmsResource resource = null;
    String structureId = request.getParameter("structureId");
    String filePath = request.getParameter("filePath");
    String detailPath = request.getParameter("detailPath");

//    String site = request.getParameter("site");
//    String uri = request.getParameter("uri");

    CmsJspStandardContextBean cms = CmsJspStandardContextBean.getInstance(request);
    try {
//        CmsObject cmso = customizeCmsObject(cms.getVfs().getCmsObject(), uri, site);
        CmsObject cmso = cms.getVfs().getCmsObject();
        if (!StringUtils.isBlank(structureId)) {
            LOG.debug("for structureId: " + structureId);
            resource = cmso.readResource(CmsUUID.valueOf(structureId));
        } else if (!StringUtils.isBlank(filePath)) {
            LOG.debug("for filePath: " + filePath);
            resource = cmso.readResource(filePath);
        } else if (!StringUtils.isBlank(detailPath)) {
            LOG.debug("for detailPath: " + detailPath);
            String detailName = CmsResource.getName(detailPath);
            CmsUUID strIdUUID = cmso.readIdForUrlName(detailName);
            resource = cmso.readResource(strIdUUID);
        }
    } catch (Exception e) {
        LOG.error("Obteniendo recurso con id: " + structureId
                + " | filepath: " + filePath
                + " | detailPath: " + detailPath
                , e);
    } finally {
        LOG.error("resource: " + resource);
        pageContext.setAttribute("resource", resource);
    }
%>
<c:set var="idx">${param.idx}</c:set>
<c:set var="structureId">${param.structureId}</c:set>
<c:set var="filePath">${param.filePath}</c:set>
<c:set var="detailPath">${param.detailPath}</c:set>

<%--Obtenemos recurso y favoritos--%>
<c:set var="res" value=""/>
<c:set var="fav" value=""/>
<c:if test="${not empty resource}">
    <c:set var="res" value="${resource}"/>
    <c:set var="stdFav">hola</c:set>
</c:if>
<%
    JSONObject json = new JSONObject();
    try {
        json.put("idx", "" + pageContext.getAttribute("idx"));
        json.put("structureId", "" + pageContext.getAttribute("structureId"));
        json.put("filePath", "" + pageContext.getAttribute("filePath"));
        json.put("detailPath", "" + pageContext.getAttribute("detailPath"));
        json.put("res", "" + pageContext.getAttribute("res"));
        json.put("fav", "" + pageContext.getAttribute("fav"));
    } catch (Exception e) {
        LOG.error("error creando json", e);
        json.put("error" , e.getMessage());
    } finally {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }
%>