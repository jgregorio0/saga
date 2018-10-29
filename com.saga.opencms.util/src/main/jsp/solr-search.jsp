<%@ page import="com.saga.cedinox.zonaprivada.util.SolrUtil" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ page import="org.opencms.search.CmsSearchResource" %>
<%@ page import="org.opencms.search.I_CmsSearchDocument" %>
<%@ page import="org.opencms.search.solr.CmsSolrQuery" %>
<%@ page import="org.opencms.util.CmsPair" %>
<%@ page import="org.opencms.util.CmsRequestUtil" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.search.solr.CmsSolrIndex" %>
<%@ page import="java.util.*" %>
<%@ page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<div>
    SOLR Search <br/>

    <%! public static final String PROP_ENCUESTA_NOTIFICADA = "sagasuite.encuesta.notificada";
        public static final String XML_FECHA_ENCUESTA_ON = "xmlfechaencuestaon";
        public static final String PROP_SOLR_INDEX = "search.index";
    %><%
    CmsObject cmso = CmsFlexController.getCmsObject(request);
        /*SolrUtil solr = new SolrUtil(cmso);

        solr.setCustomSolrIndex("Solr Offline");
        solr.setCurrentSite(cmso.getRequestContext().getSiteRoot());
        solr.setParentFolders(new String[]{"/.galleries/documentos/"});
//        String q = solr.createQuery();
        String q = "rows=1000"
                + "&start=0"
                + "&fq=type:cedseminario"
                + "&fq=con_locales:es"
                + "&fq=parent-folders:(\"/sites/cedinox/.content/cedseminario/\")"
                + "&fq=FechaEncuestaOn:[2018-07-05T11:42:00Z TO NOW]";*/


    CmsSolrQuery solrQuery = new CmsSolrQuery();

    // Sin límite de resultados
    solrQuery.setRows(1000);

    // Seminarios con la encuesta activa
    solrQuery.addFilterQuery(
            "type",
            Arrays.asList(new String[]{"sgblog"}),
            true,
            true);

    // Seminarios con la encuesta activa
    solrQuery.addFilterQuery(
            "parent-folders",
            Arrays.asList(new String[]{"/sites/cedinox/es/area-de-edicion/test-chino/"}),
            true,
            true);

    String q = solrQuery.toString();
%>
    <h2>QUERY</h2>

    <p><%=q%>
    </p>

    <h2>RESULTS</h2>
    <ol>

    <%
        // 1- Property search.index
        String solrIndex = null;
        try {
            solrIndex = cmso.readPropertyObject(
                    cmso.getRequestContext().getUri(),
                    PROP_SOLR_INDEX, true)
                    .getValue();
        } catch (Exception e) {
        }

        if (solrIndex == null) {

            // 2- Online/Offline project
            solrIndex = CmsSolrIndex.DEFAULT_INDEX_NAME_OFFLINE;
            if (cmso.getRequestContext().getCurrentProject().isOnlineProject()) {
                solrIndex = CmsSolrIndex.DEFAULT_INDEX_NAME_ONLINE;
            }
        }

        CmsSolrResultList results = OpenCms.getSearchManager().getIndexSolr(
                solrIndex).search(cmso, solrQuery, true);
        for (CmsSearchResource resource : results) {
            String resStr = resource.toString();
    %>
    <li><%=resStr%></li>

    <h4>Fields</h4>
    <ol>
        <%
            I_CmsSearchDocument document = resource.getDocument();
            List<String> fieldNames = document.getFieldNames();
            for (String fieldName : fieldNames) {
                String fieldValue = resource.getField(fieldName);
        %>

        <li>
            <dl>
                <dd><%=fieldName%>
                </dd>
                <dt><%=fieldValue%>
                </dt>
            </dl>
        </li>
        <%
            }
        %>
    </ol>

    <%
        }
    %>
    </ol>
</div>