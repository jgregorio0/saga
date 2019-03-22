package com.saga.opencms.util

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.opencms.file.CmsObject
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.search.CmsSearchException
import org.opencms.search.solr.CmsSolrIndex
import org.opencms.search.solr.CmsSolrQuery
import org.opencms.search.solr.CmsSolrResultList
import org.opencms.util.CmsRequestUtil

import java.text.DateFormat
import java.text.SimpleDateFormat

public class SgSolr {

    private static final Log LOG = CmsLog.getLog(SgSolr.class);

    /**
     * Constantes para la busqueda de Solr
     */
    public static final String P_PARENT_FOLDERS = "parentFolders";
    public static final String P_RESOURCE_TYPES = "resourceTypes";
    public static final String P_CUSTOM_FILTER = "customFilter";
    public static final String P_LOCALE = "localeFilter";
    public static final String P_FACET_FIELD = "facetField";
    public static final String P_FACET_MINCOUNT = "facetMinCount";
    public static final String P_ROWS = "maxResults";
    public static final String PROP_SOLR_INDEX = "search.index";
    public static final String SOLR_INDEX_ONLINE = CmsSolrIndex.DEFAULT_INDEX_NAME_ONLINE;
    public static final String SOLR_INDEX_OFFLINE = CmsSolrIndex.DEFAULT_INDEX_NAME_OFFLINE;
    public static final String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String SOLR_DATE_TIMEZONE = "UTC";
    public static final String SYS_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

    /**
     * Constantes para la query
     */
    public static final String Q_ID = "fq=id:";
    public static final String Q_PARENT_FOLDERS = "fq=parent-folders:";
    public static final String Q_TYPE = "fq=type:";
    public static final String Q_CATEGORY = "fq=category:";
    public static final String Q_LOCALE = "fq=con_locales:";
    public static final String Q_ROWS = "rows=";
    public static final String Q_START = "start=";
    public static final String Q_FACET = "facet=true";
    public static final String Q_FACET_LIMIT = "facet.limit=";
    public static final String Q_FACET_FIELD = "facet.field=";
    public static final String Q_FACET_MIN_COUNT = "facet.mincount=";
    public static final String Q_SEP = "&";
    public static final String Q_OR = "OR";
    public static final String Q_AND = "AND";
    public static final String Q_OPEN_P = "(";
    public static final String Q_CLOSE_P = ")";

    /** Default Fields */
    public static final String PATH = "path";
    public static final String ID = "id";
    public static final String LINK = "link";

    private CmsObject cmso;
    private String solrIndex;

    public SgSolr(CmsObject cmso) {
        this(cmso, null);
    }

    public SgSolr(CmsObject cmso, String solrIndex) {
        this(cmso, solrIndex, null, null, null)
    }

    public SgSolr(CmsObject cmso, String index, String locale, String uri, String site) {
        this.cmso = customCmsObject(cmso, locale, uri, site);
        initSolrIndex(solrIndex);
    }

    /**
     * Customize a new initialized copy of CmsObject
     * @param baseCms
     * @param uri
     * @param site
     * @return
     * @throws org.opencms.main.CmsException
     */
    public static CmsObject customCmsObject(
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

    private void initSolrIndex(String customSolrIndex) {
        // 1- Custom
        if (StringUtils.isNotBlank(customSolrIndex)) {
            this.solrIndex = customSolrIndex;
        } else {

            // 2- Property search.index
            try {
                this.solrIndex = cmso.readPropertyObject(
                        cmso.getRequestContext().getUri(),
                        PROP_SOLR_INDEX, true)
                        .getValue();
            } catch (Exception e) {
            }

            if (this.solrIndex == null) {

                // 3- Online/Offline project
                this.solrIndex = SOLR_INDEX_OFFLINE;
                if (cmso.getRequestContext().getCurrentProject().isOnlineProject()) {
                    this.solrIndex = SOLR_INDEX_ONLINE;
                }
            }
        }
    }

    /**
     * Devuelve la lista de resultados encontrados por la busqueda Solr
     * dada la query como parametro.
     * Por defecto OpenCms añade los parametros:
     * default
     * fq=expired:[NOW TO *]
     *            &con_locales:es
     *            &parent-folders:"/sites/chefcaprabo/"
     *            &released:[* TO NOW]
     * q=*:*
     * fl=*,score
     * qt=edismax
     * rows=10
     * start=0
     *
     * @param query
     */
    public CmsSolrResultList search(String query) throws CmsSearchException {
        Map<String, String[]> parameters = CmsRequestUtil.createParameterMap(query);
        CmsSolrQuery solrQuery = new CmsSolrQuery(cmso, parameters);
        return OpenCms.getSearchManager().getIndexSolr(
                solrIndex).search(cmso, solrQuery, true);
    }

    /**
     * Create solr query Object using string query.
     * It can be customize after create, for example: solrQuery.removeExpiration()
     * @param query
     * @return
     */
    public CmsSolrQuery solrQuery(String query) {
        Map<String, String[]> fq = CmsRequestUtil.createParameterMap(query);
        CmsSolrQuery solrQuery = new CmsSolrQuery(cmso, fq);
        return solrQuery;
    }

    /**
     * Search without default parameters
     * @param query
     * @return
     * @throws CmsSearchException
     */
    public CmsSolrResultList search(CmsSolrQuery solrQuery) throws CmsSearchException {
        return OpenCms.getSearchManager().getIndexSolr(
                solrIndex).search(cmso, solrQuery, true);
    }

    /**
     * Return date format for solr
     * @return
     */
    public static SimpleDateFormat solrFormat() {
        DateFormat dfSolr = new SimpleDateFormat(SOLR_DATE_FORMAT);
        dfSolr.setTimeZone(TimeZone.getTimeZone(SOLR_DATE_TIMEZONE));
        return dfSolr;
    }
}