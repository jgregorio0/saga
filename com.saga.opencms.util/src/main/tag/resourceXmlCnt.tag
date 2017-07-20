<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Genera CmsXmlContent del recurso" %>

<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.file.CmsFile" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.file.CmsResource" %>
<%@ tag import="org.opencms.file.CmsResourceFilter" %>
<%@ tag import="org.opencms.flex.CmsFlexController" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="org.opencms.util.CmsUUID" %>
<%@ tag import="org.opencms.xml.content.CmsXmlContent" %>
<%@ tag import="org.opencms.xml.content.CmsXmlContentFactory" %>

<%@ attribute name="id" type="java.lang.String" required="true" rtexprvalue="true"
             description="Identificador (path o structureId)" %>

<%--
- id (Obligatorio): '/.content/sgcontentsections/texto-0001.xml' o 'e63cd59d-3bc2-11e7-aeff-00304884abe0'
--%>

<%!
    Log LOG = CmsLog.getLog("_TAG_RESOURCE_XML_CNT");

    public static final String XML_RESOURCE = "_xmlResource";
%>
<%
    CmsXmlContent xmlResource = null;
    try {
        CmsObject cmso = CmsFlexController.getCmsObject(request);

        // get resource
        CmsResource resource = null;
        if (CmsUUID.isValidUUID(id)) {
            resource = cmso.readResource(CmsUUID.valueOf(id));
        } else {
            resource = cmso.readResource(id, CmsResourceFilter.ALL);
        }

        // check resource exists
        if (resource == null) {
            throw new Exception("Resource is not found");
        }
        CmsFile file = cmso.readFile(resource);
        xmlResource = CmsXmlContentFactory.unmarshal(cmso, file);
    } catch (Exception e) {
        LOG.error(e);
    } finally {
        request.setAttribute(XML_RESOURCE, xmlResource);
    }
%>