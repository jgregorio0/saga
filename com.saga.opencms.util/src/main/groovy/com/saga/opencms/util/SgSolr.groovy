package com.saga.opencms.util
import com.fasterxml.jackson.core.JsonProcessingException
import groovy.json.JsonBuilder
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.opencms.file.CmsObject
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.search.CmsSearchException
import org.opencms.search.CmsSearchResource
import org.opencms.search.solr.CmsSolrQuery
import org.opencms.search.solr.CmsSolrResultList
import org.opencms.util.CmsRequestUtil

public class SgSolr {

	private static final Log LOG = CmsLog.getLog(SgSolr.class);

	/**
	 * Constantes para la busqueda de Solr
	 */
	public static final String P_PARENT_FOLDER = "parentFolders";
	public static final String P_RESOURCE_TYPES = "resourceTypes";
	public static final String P_CUSTOM_FILTER = "customFilter";
	public static final String P_LOCALE = "localeFilter";
	public static final String P_FACET_FIELD = "facetField";
	public static final String P_FACET_MINCOUNT = "facetMinCount";
	public final static String P_ROWS = "maxResults";
	public final static String PROP_SOLR_INDEX = "search.index";
	public final static String SOLR_INDEX_ONLINE = "Solr Online";
	public final static String SOLR_INDEX_OFFLINE = "Solr Offline";
	public final static String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public final static String SYS_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

	/**
	 * Constantes para la query
	 */
	private final String QUERY_KEY_ID = "fq=id:";
	private final String QUERY_KEY_PARENT_FOLDER = "fq=parent-folders:";
	private final String QUERY_KEY_TYPE = "fq=type:";
	private final String QUERY_KEY_CATEGORY = "fq=category:";
	private final String QUERY_KEY_LOCALE = "fq=con_locales:";
	private final String QUERY_KEY_ROWS = "rows=";
	private final String QUERY_KEY_START = "start=";
	private final String QUERY_KEY_FACET = "facet=true";
	private final String QUERY_KEY_FACET_FIELD = "facet.field=";
	private final String QUERY_KEY_FACET_MIN_COUNT = "facet.mincount=";
	private final String QUERY_SEP = "&";
	private final String QUERY_OR = "OR";
	private final String QUERY_AND = "AND";
	private final String QUERY_OPEN_P = "(";
	private final String QUERY_CLOSE_P = ")";

	private CmsObject cmso;
	private String solrIndex;

	public SgSolr(CmsObject cmso) {
		this(cmso, null);
	}

	public SgSolr(CmsObject cmso, String solrIndex) {
		this.cmso = cmso;
		initSolrIndex(solrIndex);
	}

	private void initSolrIndex(String customSolrIndex) {
		// 1- Custom
		if (StringUtils.isNotBlank(customSolrIndex)){
			this.solrIndex = customSolrIndex;
		} else {

			// 2- Property search.index
			try {
				this.solrIndex = cmso.readPropertyObject(
						cmso.getRequestContext().getUri(),
						PROP_SOLR_INDEX, true)
						.getValue();
			} catch (Exception e) {}

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
	 * Devuelve un json generado a partir de los resultados encontrados en Solr
	 * que contiene los campos indicados.
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
	 * @param json
	 * @return
	 * @throws CmsSearchException
	 * @throws JsonProcessingException
	 */
	public String search(String query, List<String> fields)
			throws CmsSearchException, JsonProcessingException {
		CmsSolrResultList results = search(query);
		return getListMapResults(results, fields);
	}

	public List getListMapResults(CmsSolrResultList results, List fields){
		List<Map<String, String>> contents = new ArrayList<Map<String, String>>();
		for (int iRes = 0; iRes < results.size(); iRes++) {
			Map<String, String> contenido = new HashMap<String, String>();
			CmsSearchResource result = results.get(iRes);
			for (int iField = 0; iField < fields.size(); iField++) {
				String field = fields.get(iField);
				contenido.put(field, getSolrField(result, field));
			}
			contents.add(contenido);
		}
		return contents;
	}

	public String getJsonArrayResults(CmsSolrResultList results, List fields)
			throws JsonProcessingException {
		def list = getListMapResults(results, fields);
		println new JsonBuilder(list).toString();
	}

	/**
	 * Find solr field. If it does not exist return empty String.
	 * @param result
	 * @param field
	 * @return
	 */
	public static String getSolrField(CmsSearchResource result, String field) {
		String res = result.getField(field);
		res = res == null ? "" : res;
		return res;
	}
}