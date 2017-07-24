package com.saga.opencms.util
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.solr.common.util.DateUtil
import org.opencms.file.CmsObject
import org.opencms.flex.CmsFlexController
import org.opencms.json.JSONArray
import org.opencms.json.JSONException
import org.opencms.json.JSONObject
import org.opencms.jsp.util.CmsJspStandardContextBean
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.search.CmsSearchException
import org.opencms.search.CmsSearchResource
import org.opencms.search.solr.CmsSolrIndex
import org.opencms.search.solr.CmsSolrQuery
import org.opencms.search.solr.CmsSolrResultList
import org.opencms.staticexport.CmsLinkManager
import org.opencms.util.CmsRequestUtil

import javax.servlet.http.HttpServletRequest
import java.text.ParseException

public class SgSolrJson {

	private static final Log LOG = CmsLog.getLog(SgSolrJson.class);

	public static final String BRACKET = "[";
	public static final String TYPE_DATE = "d";
	public static final String TYPE_MULTIVALUED = "m";
	public static final String INDEX = "index"
	public static final String LOCALE = "locale"
	public static final String URI = "uri"
	public static final String SITE = "site"

	private CmsObject cmso;
	private HttpServletRequest req;

	private String solrquery;
	private String fields;
	private String index;

	public SgSolrJson(HttpServletRequest request, Map<String, String> ctx){
		req = request;
		cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject()

		String locale = ctx.get(LOCALE);
		String uri = ctx.get(URI);
		String site = ctx.get(SITE);

		cmso = initCmsObject(cmso, locale, uri, site)
		LOG.debug("init cmso: " +
				"locale: " + cmso.getRequestContext().getLocale() +
				" | uri: " + cmso.getRequestContext().getUri() +
				" | site: " + cmso.getRequestContext().getSiteRoot());

		// Init index
		index = initSolrIndex(ctx.get(INDEX));
		LOG.debug("index: " + index);
	}

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
				solrIndex = CmsSolrIndex.DEFAULT_INDEX_NAME_ONLINE;
			} else {
				solrIndex = CmsSolrIndex.DEFAULT_INDEX_NAME_OFFLINE;
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
	public CmsSolrResultList search(String query, String solrIndex)
			throws CmsSearchException {
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
	public JSONArray getJsonResults(CmsSolrResultList results, String[] fields)
			throws JSONException {
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
	public List<Map<String, Object>> getMapResults(CmsSolrResultList results)
			throws JSONException {
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

	/**
	 * Return Json String
	 * @param solrquery
	 * @param fields
	 * @param index
	 * @param locale
	 * @param uri
	 * @param site
	 * @return
	 */
	public String searchSolrFieldsStr(String solrquery, String fields) {
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
		JSONObject json;
		try {

			// Search
			LOG.debug("For query: " + solrquery);
			CmsSolrResultList results = search(solrquery, index);
			LOG.debug("Get " + results.size() + " results");

			// Fields
			LOG.debug("For fields: " + fields);
			String[] fieldsArray = fields.split(",");
			JSONArray jResults = getJsonResults(results, fieldsArray);

			json = com.saga.upo.util.SgJson.successJResponse(results.getNumFound(), jResults);
		} catch (Exception e) {
			LOG.error("SgSolrJson", e);
			json = com.saga.upo.util.SgJson.errorJResponse(e);
		}
		return json.toString();
	}

	/**
	 * Return Json String
	 * @param solrquery
	 * @param fields
	 * @param index
	 * @param locale
	 * @param uri
	 * @param site
	 * @return
	 */
	public JSONObject searchSolrFields(String solrquery, String fields) {
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
		JSONObject json;
		try {

			// Search
			LOG.debug("For query: " + solrquery);
			CmsSolrResultList results = search(solrquery, index);
			LOG.debug("Get " + results.size() + " results");

			// Fields
			LOG.debug("For fields: " + fields);
			String[] fieldsArray = fields.split(",");
			JSONArray jResults = getJsonResults(results, fieldsArray);

			json = com.saga.upo.util.SgJson.successJResponse(results.getNumFound(), jResults);
		} catch (Exception e) {
			LOG.error("SgSolrJson", e);
			json = com.saga.upo.util.SgJson.errorJResponse(e);
		}
		return json;
	}
}