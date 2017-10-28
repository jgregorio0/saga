<%@ tag import="com.saga.opencms.util.SgSolrJson" %>
<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.json.JSONObject" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="java.util.HashMap" %>
<%@ tag import="java.util.Map" %>
<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Genera un tag img" %>

<%@attribute name="solrquery" type="java.lang.String" required="true" rtexprvalue="true"
             description="Query para ejecutar búsqueda en solr" %>
<%@attribute name="fields" type="java.lang.String" required="true" rtexprvalue="true"
             description="Campos a obtener de los resultados de solr" %>
<%@attribute name="index" type="java.lang.String" required="false" rtexprvalue="true"
             description="Índice Solr donde realizar la búsqueda" %>
<%@attribute name="locale" type="java.lang.String" required="false" rtexprvalue="true"
             description="Forzar locale de la petición. En caso de que venga de una petición AJAX es necesario forzar el locale ya que pierde el contexto." %>
<%@attribute name="uri" type="java.lang.String" required="false" rtexprvalue="true"
             description="Forzar uri de la petición. En caso de que venga de una petición AJAX es necesario forzar el URI ya que pierde el contexto." %>
<%@attribute name="site" type="java.lang.String" required="false" rtexprvalue="true"
             description="Forzar site de la petición. En caso de que venga de una petición AJAX es necesario forzar el site ya que pierde el contexto." %>

<%--
- solrquery (Obligatorio): 'fq=type:("noticia")&fq=parent-folders:("/sites/default/")'
- fields (Obligatorio): 'path,id,link,Title_es'
- index (Opcional): 'Solr_Offline'
- locale (Opcional): 'es'
- uri (Opcional): '/blog'
- site (Opcional): '/sites/default'

*If field is multivalued add [m] at the end of the field id to return array value
*If field is date type add [d] at the end of the field id to return long value
--%>

<%!
    final Log LOG = CmsLog.getLog(this.getClass());
%>
<%

    //default
    // fq=expired:[NOW TO *]
    //      &con_locales:es
    //      &parent-folders:"/sites/chefcaprabo/"
    //      &released:[* TO NOW]
    // q=*:*
    // fl=*,score
    // qt=edismax
    // rows=10
    // start=0

    // Parametros
    JSONObject json = new JSONObject();
    try {
        // Context
        Map<String, String> ctxt = new HashMap<>();
        ctxt.put(SgSolrJson.LOCALE, locale);
        ctxt.put(SgSolrJson.URI, uri);
        ctxt.put(SgSolrJson.SITE, site);
        ctxt.put(SgSolrJson.INDEX, index);

        SgSolrJson solr = new SgSolrJson(request, ctxt);
        json = solr.searchSolrFields(solrquery, fields);
    } catch (Exception e) {
        LOG.error("solr tag", e);
        json = SgSolrJson.errorJResponse(e);
    } finally {
        out.print(json.toString());
    }
%>