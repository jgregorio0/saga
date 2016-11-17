<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<div>
    Redirect to (cms:link) <br/>

    <%
        // Se tiene que cumplir que el enlace sea absoluto y pertenezca a otro root site
        // Con los datos
        String absTarget = "/sites/acerinox/.content/tsacxproducto/tsacxproducto-00009.html";
        String siteRoot = OpenCms.getSiteManager().getSiteRoot(absTarget);
        CmsFlexController controller = CmsFlexController.getController(request);

        // be sure the link is absolute
        CmsObject cmso = controller.getCmsObject();

        // Obtenemos la raiz actual
        String originalSiteRoot = cmso.getRequestContext().getSiteRoot();
        String originalUri = cmso.getRequestContext().getUri();
        String link = "NADA";
        try {
            cmso.getRequestContext().setSiteRoot(siteRoot);
            cmso.getRequestContext().setUri(siteRoot);

            // generate the link
            link = OpenCms.getLinkManager().substituteLinkForUnknownTarget(cmso, absTarget);
        } catch (Exception e){}
        finally {
            cmso.getRequestContext().setSiteRoot(originalSiteRoot);
            cmso.getRequestContext().setUri(originalUri);
        }
    %>
    <h4>Target <%=absTarget%></h4>
    <p>siteRoot: <%=siteRoot%></p>
    <p>link: <%=link%></p>
</div>