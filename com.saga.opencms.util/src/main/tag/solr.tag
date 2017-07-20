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
<%@ tag import="org.apache.solr.common.util.DateUtil" %>
<%@ tag import="java.text.ParseException" %>
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
    public static final String BRACKET = "[";
    public static final String TYPE_DATE = "d";
    public static final String TYPE_MULTIVALUED = "m";
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
     * @return
     */
    public List<Map<String, Object>> getMapResults(CmsSolrResultList results) throws JSONException {
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        for (int iRes = 0; iRes < results.size(); iRes++) {
            Map<String, Object> contenido = new HashMap<String, Object>();
            CmsSearchResource result = results.get(iRes);
            Map<String, Object> resContent = getDefaultMapCmsSearchResource(result);
        }
        return contents;
    }

    /**
     * Return default attributes for each solr result
     * @param solrResource
     * @return
     */
    private Map<String, Object> getDefaultMapCmsSearchResource(CmsSearchResource solrResource) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("path", solrResource.getRootPath());
        result.put("structureId", solrResource.getStructureId());
        result.put("resourceId", solrResource.getResourceId());
        result.put("typeId", solrResource.getTypeId());
        result.put("isFolder", solrResource.isFolder());
        result.put("flags", solrResource.getFlags());
        result.put("project", solrResource.getProjectLastModified());
        result.put("state", solrResource.getState());
        result.put("dateCreated", solrResource.getDateCreated());
        result.put("userCreated", solrResource.getUserCreated());
        result.put("dateLastModified", solrResource.getDateLastModified());
        result.put("userLastModified", solrResource.getUserLastModified());
        result.put("dateReleased", solrResource.getDateReleased());
        result.put("dateExpired", solrResource.getDateExpired());
        result.put("dateContent", solrResource.getDateContent());
        result.put("dateContent", solrResource.getDateContent());
        result.put("size", solrResource.getLength());
        result.put("sibilingCount", solrResource.getSiblingCount());
        result.put("version", solrResource.getVersion());

        return result;
    }

    /**
     * Find simple value solr field. If it does not exist return empty String.
     * @param result
     * @param solrField
     * @return
     */
    public String getSimpleSolrField(CmsSearchResource result, SolrField solrField) {
        String res = "";

        if (solrField.isDateType()){
            // DATE
            res = getDateSolrField(result, solrField.getFieldName()).toString();
        } else if (solrField.isLink()) {
            // LINK
            res = cmsLink(result.getRootPath());
        } else {
            // OTHER
            res = result.getField(solrField.getFieldName());
        }

        res = res == null ? "" : res;
        return res;
    }

    /**
     * Find multi valued solr field. If it does not exist return empty String.
     * @param result
     * @param solrField
     * @return
     */
    public Object getMultiSolrField(CmsSearchResource result, SolrField solrField) {
        Object res = "";
        try {
            List<String> multivaluedField = result.getMultivaluedField(solrField.getFieldName());

            // If type date
            if (solrField.isDateType()) {
                multivaluedField = multiDateStringToLong(multivaluedField);
            }

            // JSON Array
            res = new JSONArray(multivaluedField);
            LOG.debug("parsed multi value " + res + " for field " + solrField);
        } catch (Exception e) {
            LOG.error("parsing multi valued field " + solrField, e);
        }

        return res;
    }


    /**
     * Parse format date string to long
     * @param multivaluedField
     * @return
     */
    private List<String> multiDateStringToLong(List<String> multivaluedField) {
        ArrayList<String> results = new ArrayList<String>();
        for (int i = 0; i < multivaluedField.size(); i++) {
            String dateStr = multivaluedField.get(i);
            try {
                Date date = DateUtil.parseDate(dateStr);
                results.add(String.valueOf(date.getTime()));
            } catch (ParseException e) {
                LOG.error("Error parsing date " + dateStr);
            }
        }
        return results;
    }

    /**
     * Find date type value solr field. If it does not exist return empty String.
     * @param result
     * @param fieldName
     * @return
     */
    public Object getDateSolrField(CmsSearchResource result, String fieldName) {
        Object res = "";
        try {
            res = result.getDateField(fieldName).getTime();
            LOG.debug("parsed date type value " + res + " for field " + fieldName);
        } catch (Exception e) {
            LOG.error("parsing date type value field " + fieldName, e);
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
        SolrField solrField = new SolrField(field);
        if (solrField.isMultivaluedType()){
            // MULTIVALUED
            contenido.put(solrField.getFieldName(), getMultiSolrField(result, solrField));
        } else {
            // SIMPLE
            contenido.put(solrField.getFieldName(), getSimpleSolrField(result, solrField));
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

    /**
     * Create class SolrField to manage multivalued and date types
     */
    class SolrField {

        private String field;
        private String fieldName;
        private boolean isMultivaluedType;
        private boolean isDateType;
        private boolean isLink;
        private boolean isBracketsAnnoted;

        public SolrField(String field) {
            this.field = field;

            this.fieldName = field;
            this.isMultivaluedType = false;
            this.isDateType = false;
            this.isLink = false;
            this.isBracketsAnnoted = hasBrackets(field);

            if (isBracketsAnnoted) {
                initSolrField(field);
            }
        }

        private void initSolrField(String field) {
            this.fieldName = removeBrackets(field);
            this.isMultivaluedType = isMultiValued(field);
            this.isDateType = isDateValued(field);
            this.isLink = isLinkField(field);
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getFieldName() {
            return fieldName;
        }

        public boolean isMultivaluedType() {
            return isMultivaluedType;
        }

        public boolean isDateType() {
            return isDateType;
        }

        public boolean isLink() {
            return isLink;
        }

        /**
         * Check if it field has brackets annotation
         * @param field
         * @return
         */
        private boolean hasBrackets(String field) {
            return field.indexOf(BRACKET) > 0;
        }

        /**
         * Check if field is multivalued type
         * @param field
         * @return
         */
        private boolean isMultiValued(String field) {
            boolean isMultiVal = false;
            int iBracket = field.indexOf(BRACKET);
            if (field.indexOf(BRACKET) > 0){
                String bracketCnt = field.substring(iBracket);
                isMultiVal = bracketCnt.contains(TYPE_MULTIVALUED);
            }
            return isMultiVal;
        }

        /**
         * Check if field is data type
         * @param field
         * @return
         */
        private boolean isDateValued(String field) {
            boolean isDateVal = false;
            int iBracket = field.indexOf(BRACKET);
            if (field.indexOf(BRACKET) > 0){
                String bracketCnt = field.substring(iBracket);
                isDateVal = bracketCnt.contains(TYPE_DATE);
            }
            return isDateVal;
        }

        /**
         * Check if field is data type
         * @param field
         * @return
         */
        private boolean isLinkField(String field) {
            return field.equals("link");
        }

        /**
         * Remove brackets and return field name
         * @param field
         * @return
         */
        private String removeBrackets(String field) {
            int iBrackets = field.indexOf(BRACKET);
            if (iBrackets > 0) {
                return field.substring(0, iBrackets);
            } else {
                return null;
            }
        }

        @Override
        public String toString() {
            return "SolrField{" +
                    "field='" + field + '\'' +
                    ", fieldName='" + fieldName + '\'' +
                    ", isMultivaluedType=" + isMultivaluedType +
                    ", isDateType=" + isDateType +
                    ", isLink=" + isLink +
                    ", isBracketsAnnoted=" + isBracketsAnnoted +
                    '}';
        }
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