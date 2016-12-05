package com.saga.opencms.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchellbosecke.pebble.error.PebbleException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.search.CmsSearchException;
import org.opencms.search.CmsSearchResource;
import org.opencms.search.solr.CmsSolrQuery;
import org.opencms.search.solr.CmsSolrResultList;
import org.opencms.util.CmsRequestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jgregorio on 17/04/2015.
 */
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
	public String search(Json json)
			throws CmsSearchException, JsonProcessingException {
		CmsSolrResultList results = search(json.getQuery());
		return json.getJsonResults(results);
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

	public static class Json {

		private String query;
		private ArrayList<String> fields;
		private String template;

		public Json() {
		}

		/**
		 * Create Json for solr searching. Parameters must be format as examples.
		 * @param query String query = "fq=parent-folders:(\\\"/sites/default/\\\")
		 * 		&fq=type:(\\\"function\\\")
		 * 		&sort=created desc
		 * 		&rows=10
		 * 		&start=0";
		 * @param fields String fields = "[\"Title_prop_s\", \"filename\", \"path\"]";
		 * @param template String template = "/system/modules/com.saga.opencms.solrlist/elements/solr-list-template.jsp";
         * @throws IOException
         */
		public Json(String query, ArrayList<String> fields, String template) throws IOException {
			setQuery(query);
			setFields(fields);
			setTemplate(template);
		}

		/**
		 * Create Json for solr searching. Parameters must be format as examples.
		 * String query = "fq=parent-folders:(\\\"/sites/default/\\\")
		 * 		&fq=type:(\\\"function\\\")
		 * 		&sort=created desc
		 * 		&rows=10
		 * 		&start=0";
		 * String fields = "[\"Title_prop_s\", \"filename\", \"path\"]";
		 * String template = "/system/modules/com.saga.opencms.solrlist/elements/solr-list-template.jsp";

		 * @param jsonStr String jsonStr = "{\"query\": \"" + query + "\"
		 *                , \"fields\": " + fields
		 *                , \"template\": \"" + template + "\"}";
		 * @throws IOException
         */
		public Json(String jsonStr)
				throws IOException {
			HashMap<String,Object> json =
					new ObjectMapper().readValue(jsonStr, HashMap.class);
			setQuery((String) json.get("query"));
			setFields((ArrayList<String>) json.get("fields"));
			setTemplate((String) json.get("template"));
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public ArrayList<String> getFields() {
			return fields;
		}

		public void setFields(ArrayList fields) {
			this.fields = fields;
		}

		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		public String toString(){
			return "SgSolr.Json{" +
					"\"query\": \"" + getQuery() + "\"" +
					", \"fields\": " + getFields() +
					", \"template\": \"" + getTemplate() + "\"" +
					"}";
		}

		public String toJson(){
			return "{" +
					"\"query\": \"" + getQuery() + "\"" +
					", \"fields\": " + getFields() +
					", \"template\": \"" + getTemplate() + "\"" +
					"}";
		}

		public String getJsonResults(CmsSolrResultList results)
				throws JsonProcessingException {
			List<Map<String, String>> contents = new ArrayList<Map<String, String>>();
			for (int iRes = 0; iRes < results.size(); iRes++) {
				Map<String, String> contenido = new HashMap<String, String>();
				CmsSearchResource result = results.get(iRes);
				for (int iField = 0; iField < getFields().size(); iField++) {
					String field = getFields().get(iField);
					contenido.put(field, getSolrField(result, field));

				}
				contents.add(contenido);
			}
			return new ObjectMapper().writeValueAsString(contents);
		}

//		public String getTemplateResults(String jsonResults)
//				throws IOException, PebbleException {
//			HashMap<String,Object> ctx =
//					new ObjectMapper().readValue(jsonResults, HashMap.class);
//			return new SgPebble().process(getTemplate(), ctx);
//		}
//
//		public String getTemplateResults(CmsObject cmso, String jsonResults)
//				throws IOException, PebbleException {
//			HashMap<String,Object> ctx = new HashMap<String, Object>();
//			ArrayList results = new ObjectMapper().readValue(jsonResults, ArrayList.class);
//			ctx.put("results", results);
//			return new SgPebble().process(cmso, getTemplate(), ctx);
//		}
	}
}