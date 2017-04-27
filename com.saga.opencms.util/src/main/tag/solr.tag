<%@ tag import="org.apache.commons.lang3.StringUtils" %>
<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.flex.CmsFlexController" %>
<%@ tag import="org.opencms.json.JSONArray" %>
<%@ tag import="org.opencms.json.JSONException" %>
<%@ tag import="org.opencms.main.CmsException" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="org.opencms.main.OpenCms" %>
<%@ tag import="org.opencms.search.CmsSearchException" %>
<%@ tag import="org.opencms.search.CmsSearchResource" %>
<%@ tag import="org.opencms.search.solr.CmsSolrQuery" %>
<%@ tag import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ tag import="org.opencms.staticexport.CmsLinkManager" %>
<%@ tag import="org.opencms.util.CmsRequestUtil" %>
<%@ tag import="java.util.*" %>
<%@ tag import="org.opencms.json.JSONObject" %>
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
--%>

<%!


    final Log LOG = CmsLog.getLog(this.getClass());

    private CmsObject cmso;
    private HttpServletRequest req;

    /**
     * Customize a new initialized copy of CmsObject
     * @param baseCms
     * @param uri
     * @param site
     * @return
     * @throws CmsException
     */
    private CmsObject initCmsObject(
            CmsObject baseCms, String locale, String uri, String site)
            throws CmsException {
        CmsObject cmso = baseCms;
        boolean isLocale = StringUtils.isNotBlank(locale);
        boolean isCustomSite = StringUtils.isNotBlank(site);
        boolean isCustomUri = StringUtils.isNotBlank(uri);
        if (isLocale || isCustomSite || isCustomUri) {
            cmso = OpenCms.initCmsObject(baseCms);
            if (isLocale) {
                cmso.getRequestContext().setLocale(new Locale(locale));
            }
            if (isCustomSite) {
                cmso.getRequestContext().setSiteRoot(site);
            }
            if (isCustomUri) {
                cmso.getRequestContext().setUri(uri);
            }
        }
        return cmso;
    }

    /**
     * Inicializa indice de solr
     * @param customSolrIndex
     */
    private String initSolrIndex(String customSolrIndex) {
        String solrIndex;

        // 1- Custom
        if (StringUtils.isNotBlank(customSolrIndex)){
            solrIndex = customSolrIndex;
        } else {

            // 2- Online/Offline project
            if (cmso.getRequestContext().getCurrentProject().isOnlineProject()) {
                solrIndex = "Solr Online";
            } else {
                solrIndex = "Solr Offline";
            }
        }
        return solrIndex;
    }

    /**
     * Devuelve la lista de resultados encontrados por la busqueda Solr
     * dada la query como parametro.
     * Por defecto OpenCms añade los parametros:
     * default
     * fq=expired:[NOW TO *]
     *            &con_locales:${cms.locale}
     *            &parent-folders:"${cms.site}"
     *            &released:[* TO NOW]
     * q=*:*
     * fl=*,score
     * qt=edismax
     * rows=10
     * start=0
     *
     * @param query
     */
    public CmsSolrResultList search(String query, String solrIndex) throws CmsSearchException {
        Map<String, String[]> parameters = CmsRequestUtil.createParameterMap(query);
        CmsSolrQuery solrQuery = new CmsSolrQuery(cmso, parameters);
        return OpenCms.getSearchManager().getIndexSolr(
                solrIndex).search(cmso, solrQuery, true);
    }

    /**
     *
     * @param results
     * @param fields
     * @return
     */
    public JSONArray getJsonResults(CmsSolrResultList results, String[] fields) throws JSONException {
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        for (int iRes = 0; iRes < results.size(); iRes++) {
            Map<String, Object> contenido = new HashMap<String, Object>();
            CmsSearchResource result = results.get(iRes);
            for (int iField = 0; iField < fields.length; iField++) {
                String field = fields[iField];
                addSolrField(contenido, result, field);
            }

            contents.add(contenido);
        }
        return new JSONArray(contents);
    }

    /**
     * Map results content value
     * @param results
     * @param fields
     * @return
     */
    public List<Map<String, Object>> getMapResults(CmsSolrResultList results, String[] fields) throws JSONException {
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        for (int iRes = 0; iRes < results.size(); iRes++) {
            Map<String, Object> contenido = new HashMap<String, Object>();
            CmsSearchResource result = results.get(iRes);
            for (int iField = 0; iField < fields.length; iField++) {
                String field = fields[iField];
                addSolrField(contenido, result, field);
            }
        }
        return contents;
    }

    /**
     * Find simple value solr field. If it does not exist return empty String.
     * @param result
     * @param fieldName
     * @return
     */
    public String getSimpleSolrField(CmsSearchResource result, String fieldName) {
        String res = "";
        if (fieldName.equals("link")) {
            res = cmsLink(result.getRootPath());
        } else {
            res = result.getField(fieldName);
        }

        res = res == null ? "" : res;
        return res;
    }

    /**
     * Find multi valued solr field. If it does not exist return empty String.
     * @param result
     * @param fieldName
     * @return
     */
    public Object getMultiSolrField(CmsSearchResource result, String fieldName) {
        Object res = "";
        try {
            List<String> multivaluedField = result.getMultivaluedField(fieldName);
            res = new JSONArray(multivaluedField);
            LOG.debug("parsed multi value " + res + " for field " + fieldName);
        } catch (Exception e) {
            LOG.error("parsing multi valued field " + fieldName, e);
        }

        return res;
    }

    /**
     * Check if field is multivalued
     * @param field
     * @return
     */
    private boolean isMultiValued(String field) {
        return field.indexOf("[") > 0;
    }

    /**
     * Remove brackets and return field name
     * @param field
     * @return
     */
    private String removeBrackets(String field) {
        int iBrackets = field.indexOf("[");
        return field.substring(0, iBrackets);
    }

    /**
     * Find simple or multi valued solr field. If it does not exist return empty String.
     * @param result
     * @param field
     * @return
     */
    public Object getSolrField(CmsSearchResource result, String field) {
        Object res = "";
        boolean isMultiValued = field.endsWith("[]");
        if (isMultiValued) {
            res = getMultiSolrField(result, field);
        } else {
            res = getSimpleSolrField(result, field);
        }
        return res;
    }

    /**
     * Add solr field name and value to map (multi and simple value)
     * @param contenido
     * @param result
     * @param field
     */
    private void addSolrField(Map<String, Object> contenido, CmsSearchResource result, String field) {
        if (isMultiValued(field)){
            String multiFieldName = removeBrackets(field);
            contenido.put(multiFieldName, getMultiSolrField(result, multiFieldName));
        } else {
            contenido.put(field, getSimpleSolrField(result, field));
        }
    }

    /**
     * Link to resource
     * @param target
     * @return
     */
    private String cmsLink(String target){
        CmsFlexController controller = CmsFlexController.getController(req);
        // be sure the link is absolute
        String uri = CmsLinkManager.getAbsoluteUri(target, controller.getCurrentRequest().getElementUri());

        // generate the link
        return OpenCms.getLinkManager().substituteLinkForUnknownTarget(cmso, uri);
    }
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
        // Init cmso
        req = request;
        cmso = initCmsObject(
                CmsFlexController.getCmsObject(request), locale, uri, site);
        LOG.debug("init cmso: " +
                "locale: " + cmso.getRequestContext().getLocale() +
                " | uri: " + cmso.getRequestContext().getUri() +
                " | site: " + cmso.getRequestContext().getSiteRoot());

        // Init index
        String solrIndex = initSolrIndex(index);
        LOG.debug("SolrIndex: " + solrIndex);

        // Search
        LOG.debug("For query: " + solrquery);
        CmsSolrResultList results = search(solrquery, solrIndex);
        LOG.debug("Get " + results.size() + " results");

        // Fields
        LOG.debug("For fields: " + fields);
        String[] fieldsArray = fields.split(",");
        JSONArray jResults = getJsonResults(results, fieldsArray);
        json.put("success", true);
        json.put("total", results.getNumFound());
        json.put("resultsSize", jResults.length());
        json.put("results", jResults);
    } catch (Exception e) {
        LOG.error("solr tag", e);
        json.put("success", false);
        json.put("error", e.getMessage());
    } finally {
        out.print(json.toString());
    }
%>