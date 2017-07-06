<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Tag que resuelve el link basandose en cms:link incluyendo opciones adicionales" %>

<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.flex.CmsFlexController" %>
<%@ tag import="org.opencms.main.CmsException" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="org.opencms.main.OpenCms" %>
<%@ tag import="org.opencms.staticexport.CmsLinkManager" %>
<%@ tag import="org.opencms.util.CmsStringUtil" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@attribute name="target" required="true" rtexprvalue="true" type="java.lang.String"
             description="Ruta absoluta al recurso" %>
<%@attribute name="baseUri" required="false" rtexprvalue="true" type="java.lang.String"
             description="Uri desde que se genera el link. Si no se indica se obtiene del contexto" %>
<%@attribute name="baseSite" required="false" rtexprvalue="true" type="java.lang.String"
             description="Site desde que se genera el link. Si no se indica se obtiene del contexto" %>
<%@attribute name="secure" required="false" rtexprvalue="true" type="java.lang.Boolean"
             description="Site desde que se genera el link. Si no se indica se obtiene del contexto" %>
<%@attribute name="relativeScheme" required="false" rtexprvalue="true" type="java.lang.Boolean"
             description="Fuerza que el path empiece por doble barra //" %>
<%!
  private final Log LOG = CmsLog.getLog(this.getClass());

  private String relativizeSchema(Boolean relScheme, String link) {
    String linkRel = link;
    if (relScheme != null && relScheme) {
      // Si contiene esquema (http, https, etc)
      if(OpenCms.getLinkManager().hasScheme(linkRel)){
        linkRel = linkRel.substring(linkRel.indexOf(":") + 1);
      }

      if (!linkRel.startsWith("//")) {
        if (!linkRel.startsWith("/")){
          linkRel = "/" + linkRel;
        }
        linkRel = "/" + linkRel;
      }
    }
    return linkRel;
  }

  private CmsObject customizeCmsObject(CmsObject cms, String baseUri, String baseSite) {
    CmsObject cmso = cms;
    boolean isCustomUri = CmsStringUtil.isNotEmptyOrWhitespaceOnly(baseUri);
    boolean isCustomSite = CmsStringUtil.isNotEmptyOrWhitespaceOnly(baseSite);
    if (isCustomSite || isCustomUri) {
      try {
        cmso = OpenCms.initCmsObject(cmso);
        if (isCustomSite) {
          cmso.getRequestContext().setSiteRoot(baseSite);
        }
        if (isCustomUri) {
          cmso.getRequestContext().setUri(baseUri);
        }
      } catch (CmsException e) {
        LOG.error("customizing cms objecto using baseUri: " + baseUri + " and baseSite: " + baseSite, e);
      }
    }
    return cmso;
  }

  public String link(ServletRequest req, String target, String baseUri, String baseSite, Boolean secure, Boolean relScheme) {

    CmsFlexController controller = CmsFlexController.getController(req);

    // be sure the link is absolute
    String uri = CmsLinkManager.getAbsoluteUri(target, controller.getCurrentRequest().getElementUri());
    CmsObject cms = controller.getCmsObject();

    // Customize cms object
    cms = customizeCmsObject(cms, baseUri, baseSite);

    // Choose secure (http or https)
    secure = secure != null ? secure : false;

    // Get link
    String link = OpenCms.getLinkManager().substituteLinkForUnknownTarget(cms, uri, secure);

    // Protocolo relativo
    link = relativizeSchema(relScheme, link);

    return link;
  }
%>
<c:out value="<%=link(request, target, baseUri, baseSite, secure, relativeScheme)%>"/>