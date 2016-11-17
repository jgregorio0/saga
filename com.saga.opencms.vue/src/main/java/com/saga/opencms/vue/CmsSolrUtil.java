package com.saga.opencms.vue;

import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.search.CmsSearchException;
import org.opencms.search.CmsSearchResource;
import org.opencms.search.solr.CmsSolrQuery;
import org.opencms.search.solr.CmsSolrResultList;
import org.opencms.util.CmsRequestUtil;

import java.util.*;

/**
 * Created by jgregorio on 17/04/2015.
 *
 * CmsSolrUtil is a tool for using Solr requests on OpenCms.
 *
 * For using:
 * 1- Add parameters
 * 2- Create query
 * 3- Search
 *
 */
public class CmsSolrUtil {

    private static final Log LOG = CmsLog.getLog(CmsSolrUtil.class);

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
    public final static int DEFAULT_ROWS = 1000;

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
    private final String QUERY_OPEN_P = "(";
    private final String QUERY_CLOSE_P = ")";

    /**
     * Parametros de la busqueda
     */
    private List<Param> params;
    private String customSolrIndex;
    private String customRows;
    private CmsObject cmso;

    public CmsSolrUtil(CmsObject cmso) {
        setCmso(cmso);
        params = new ArrayList<Param>();
    }

    /**
     * Limpiamos los parametros para realizar una nueva busqueda
     */
    public void clear(){
        params.clear();
    }

    /**
     * Devuelve la lista de resultados encontrados por la busqueda Solr
     * dada la query como parametro
     *
     * Por defecto se incluyen en la query:
     * q=*:*
     * &fl=*,score
     * &qt=edismax
     * &rows=10
     * &fq=expired:[NOW TO *]
     * &fq=con_locales:{locale}
     * &fq=parent-folders:"{siteRoot}"
     * &fq=released:[* TO NOW]
     *
     * @param query
     */
    public CmsSolrResultList search(String query) throws CmsSearchException {
        CmsSolrResultList results = null;

        String solrIndex = getSolrIndex();

        if (cmso != null) {
            Map<String, String[]> parameters = CmsRequestUtil.createParameterMap(query);
            CmsSolrQuery solrQuery = new CmsSolrQuery(cmso, parameters);
            results = OpenCms.getSearchManager().getIndexSolr(
                    solrIndex).search(cmso, solrQuery, true);
        }
        return results;
    }

    /**
     * Devuelve la lista de resultados encontrados en la busqueda Solr
     * usando los parametros configurados anteriormente.
     *
     * Por defecto se incluye la query:
     * q=*:*
     * &fl=*,score
     * &qt=edismax
     * &rows=10
     * &fq=expired:[NOW TO *]
     * &fq=con_locales:{locale}
     * &fq=parent-folders:"{siteRoot}"
     * &fq=released:[* TO NOW]
     *
     * */
    public CmsSolrResultList search() throws CmsSearchException {
        CmsSolrResultList results = null;
        String solrIndex = getSolrIndex();
        String query = createQuery();
        Map<String, String[]> parameters =
                CmsRequestUtil.createParameterMap(query);
        CmsSolrQuery solrQuery = new CmsSolrQuery(cmso, parameters);
        results = OpenCms.getSearchManager().getIndexSolr(
                solrIndex).search(cmso, solrQuery, true);
        if (results != null){
            System.out.println("results distinto de null");
        } else {
            System.out.println("result null");
        }
        return results;
    }

    /**
     * Trasnform query parameters map <String, List<String>> to <String, String[]>
     * @param queryParams
     * @return
     */
    private Map<String, String[]> queryParametersMapArray(Map<String, List<String>> queryParams) {
        Map<String, String[]> queryArray = new HashMap<String, String[]>();
        Set<String> keys = queryParams.keySet();
        for (String key : keys) {
            List<String> values = queryParams.get(key);
            String[] valuesArr = new String[values.size()];
            valuesArr = values.toArray(valuesArr);
            queryArray.put(key, valuesArr);
        }
        return queryArray;
    }

    /**
     * Return custom solr index or default solr index if custom does not exist.
     * @return
     */
    private String getSolrIndex() {
        if (customSolrIndex != null && customSolrIndex.length() > 0) {
            return customSolrIndex;
        } else {
            return getDefaultSolrIndex();
        }
    }

    /**
     * Segun la configuracion por defecto si el containerpage contiene algun valor
     * en la propiedad search.index, dicho valor sera el indice de busqueda.
     * En caso de que no contenga valor si estamos en el projecto online busca
     * en "Solr Online" si estamos en el proyecto offline busca en "Solr Offline"
     * @return
     */
    private String getDefaultSolrIndex() {
        String solrIndex = null;
        String uri = cmso.getRequestContext().getUri();
        try {
            CmsProperty propIndex = cmso.readPropertyObject(uri, PROP_SOLR_INDEX, true);
            solrIndex = propIndex.getValue();
        } catch (CmsException e) {
            LOG.error("ERROR: Property " + PROP_SOLR_INDEX + " not found", e);
        }

        if (solrIndex == null){
            if (cmso.getRequestContext().getCurrentProject().isOnlineProject()) {
                solrIndex = SOLR_INDEX_ONLINE;
            } else {
                solrIndex = SOLR_INDEX_OFFLINE;
            }
        }

        return solrIndex;
    }

    // TODO Crear simpleSearch con busqueda por defecto de 10 resultados

    /**
     * Creamos la query para realizar la busqueda segun los
     * atributos de los que disponemos
     * @return
     */
    public String createQuery (){
        StringBuffer sb = new StringBuffer();
        for (Param param : params) {
            sb.append(param.toString());
        }
        return sb.toString();
    }

    // TODO tratar casos en los que start y rows no esten definidos

    //TODO probar parametros con comillas y sin comillas

    //TODO incluir el locale en la query por defecto

    /**
     * Creamos un mapa para facilitar la busqueda en la lista de Solr
     * relacionando el structureid con el recurso encontrado.
     * @param solrList
     * @return
     */
    public Map<String, CmsSearchResource> createRelationMap(CmsSolrResultList solrList) {
        Map<String, CmsSearchResource> map = new HashMap<String, CmsSearchResource>();
        for (CmsSearchResource cmsSearchResource : solrList) {
            String idStructure = cmsSearchResource.getStructureId().toString();
            if (idStructure != null) {
                map.put(idStructure, cmsSearchResource);
            }
        }
        return map;
    }

    /**
     * Incluye un parametro de busqueda
     * @param id
     * @param name
     * @param value
     */
    public void addSolrQueryParameter(String id, String name, String value){
        params.add(new Param(id, name, value));
    }



//    /**
//     * Incluye los parametros declarados en el json.
//     * El objeto json debe contener el id, nombre y valor del paramtro. Por ejemplo:
//     * {"fq" : ["parent-folders":"/sites/default/", "type":["alkacon-v8-webformreport","sigmagridtable"]]}
//     * @param json
//     */
//    public void addSolrQueryParameter(JSONObject json){
//        Iterator<String> itIds = json.keys();
//        while (itIds.hasNext()) {
//            String id = itIds.next();
//            JSONArray filters = json.getJSONArray(id);
//
//            Iterator<Object> itNames = filters.iterator();
//            while(itNames.hasNext()){
//                String name = (String)itNames.next();
//            }
//            if (filterValue instanceof String) {
//                new CmsSolrParam()
//                addSolrQueryParameter(filterName, (String)filterValue);
//            } else if (filterValue instanceof JSONArray) {
//                addSolrQueryParameter(filterName, (JSONArray) filterValue);
//            }
//        }
//    }
//
//    /**
//     * Add all parameter values to Solr Query
//     * @param name
//     * @param valueJsonArray
//     */
//    private void addSolrQueryParameter(String name, JSONArray valueJsonArray) {
//        for (int i = 0; i < valueJsonArray.length(); i++) {
//            String filterValue = valueJsonArray.getString(i);
//            addSolrQueryParameter(name, filterValue);
//        }
//    }

    public CmsObject getCmso() {
        return cmso;
    }

    public void setCmso(CmsObject cmso) {
        this.cmso = cmso;
    }

    public String getCustomSolrIndex() {
        return customSolrIndex;
    }

    public void setCustomSolrIndex(String customSolrIndex) {
        this.customSolrIndex = customSolrIndex;
    }

    public String getCustomRows() {
        return customRows;
    }

    public void setCustomRows(String customRows) {
        this.customRows = customRows;
    }

    public static class Param {
        private String solrId;
        private String name;
        private String value;
        private String del;
        private List<String> values;


        public Param(String solrId, String name, String value){
            setSolrId(solrId);
            setName(name);
            setValue(value);
        }

        public Param(String solrId, String name, String del, List<String> values){
            setSolrId(solrId);
            setName(name);
            setDel(del);
            setValues(values);
        }

        public String getSolrId() {
            return solrId;
        }

        public void setSolrId(String solrId) {
            this.solrId = solrId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDel() {
            return del;
        }

        public void setDel(String del) {
            this.del = del;
        }

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        public void addValues(String value) {
            if (getValues() == null) {
                setValues(new ArrayList<String>());
            }
            getValues().add(value);
        }

        public boolean isSimpleType() {
            if (value != null) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isComplexType() {
            if (values != null) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isNameType() {
            if (name != null) {
                return true;
            } else {
                return false;
            }
        }

        public String toString(){
            if (isSimpleType()) {
                StringBuffer sb = new StringBuffer();
                sb.append(getSolrId() + "=");
                if (isNameType()) {
                    sb.append(getName() + ":");
                }
                sb.append(getValue());
                return sb.toString();
            } else if (isComplexType()) {
                StringBuffer sb = new StringBuffer();
                sb.append(getSolrId() + "=");
                if (isNameType()) {
                    sb.append(getName() + ":");
                }
                sb.append("(");
                for (int i = 0; i < getValues().size(); i++) {
                    String value = getValues().get(i);
                    if (i > 0) {
                        sb.append(" " + getDel() + " ");
                    }
                    sb.append("\"").append(value).append("\"");
                }
                sb.append(")");
                return sb.toString();
            }
            return super.toString();
        }
    }
}
