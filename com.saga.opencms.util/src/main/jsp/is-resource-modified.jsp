<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencmshispano.module.resources.manager.ResourceManager" %>
<%@ page import="java.util.HashMap" %>
<%@page buffer="none" session="true" trimDirectiveWhitespaces="true"%>
<%!
    /**
     * Valida que el recurso ha sido modificado si:
     * 1- No existia (fecha de ultima modificacion previa es nula)
     * y ha sido creado (fecha de ultima modificacion posterior no es nula)
     * 2- Ha sido editado (fecha de ultima modificacion previa es menor que la posterior)
     *
     * @param prev
     * @param post
     * @return
     */
    private boolean isCreatedOrModified(Long prev, Long post) {
        if (prev == null) {
            // No existia el recurso y ha sido creado posteriormente
            return post != null;
        } else {
            // Si existia previamente comprobamos que ha sido editado
            return post != null && prev < post;
        }
    }

    /**
     * Devuelve fecha de ultima modificacion. En caso de que no exista recurso devuelve <code>null</code>
     * @param cmso
     * @param resourcePath
     * @return
     */
    private Long getDateLastModified(CmsObject cmso, String resourcePath) {
        Long dateLastModified = null;
        try {
            CmsResource resource = cmso.readResource(resourcePath, CmsResourceFilter.ALL);
            dateLastModified = resource.getDateLastModified();
        } catch (Exception e) {
//			LOG.debug("No se ha podido obtener dateLastModified ya que no existe el recurso " + resourcePath);
        }
        return dateLastModified;
    }
%>

<%
    String resPath = "/.content/sgfreehtml/sgfreehtml-00009.xml";

    String content = "<Title><![CDATA[Encabezado productos - inoxfil]]></Title>\n" +
            "    <VerTituloRecurso>false</VerTituloRecurso>\n" +
            "    <Content name=\"Content0\">\n" +
            "      <links/>\n" +
            "      <content><![CDATA[<header>\n" +
            "<h2 class=\"title\">Inoxfil</h2>\n" +
            "</header>]]></content>\n" +
            "    </Content>\n" +
            "  </SGFreehtml>\n" +
            "  <SGFreehtml language=\"en\">\n" +
            "    <Title><![CDATA[Encabezado productos - inoxfil]]></Title>\n" +
            "    <VerTituloRecurso>false</VerTituloRecurso>\n" +
            "    <Content name=\"Content0\">\n" +
            "      <links/>\n" +
            "      <content><![CDATA[<header>\n" +
            "<h2 class=\"title\">Inoxfil</h2>\n" +
            "</header>]]></content>\n" +
            "    </Content>";

    HashMap<String, Object> data = new HashMap<String, Object>();
    data.put("Title", "Encabezado productos - inoxfil");
    data.put("Content", "<header><h2 class=\"title\">Inoxfil</h2></header>");

    CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
//   TODO ResourceManager rm = new ResourceManager(cmso);

    Long lastModifiedPrev = getDateLastModified(cmso, resPath);
//   TODO CmsResource resource = rm.saveCmsResource(data, resPath, "sgfreehtml", true);
    Long lastModifiedPost = getDateLastModified(cmso, resPath);

    boolean isCreatedOrModified = isCreatedOrModified(lastModifiedPrev, lastModifiedPost);
%>
<div>
    <p>Resource: <%=resPath%></p>
    <p>lastModifiedPrev: <%=lastModifiedPrev%></p>
    <p>lastModifiedPost: <%=lastModifiedPost%></p>
    <p>isCreatedOrModified: <%=isCreatedOrModified%></p>
</div>