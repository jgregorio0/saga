package com.saga.opencms.util;

import org.apache.commons.logging.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jgregorio on 17/04/2015.
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
    private Map<String, List<String>> queryParams;
    private String customSolrIndex;
    private String customRows;
    private CmsObject cmso;

    public CmsSolrUtil(CmsObject cmso) {
        setCmso(cmso);
        queryParams = new HashMap<String, List<String>>();
    }

    /**
     * Limpiamos los parametros para realizar una nueva busqueda
     */
    public void clear(){
        queryParams.clear();
    }

    /**
     * Devuelve la lista de resultados encontrados por la busqueda Solr
     * dada la query como parametro
     * @param query
     */
    public CmsSolrResultList search(String query) throws CmsSearchException {
        CmsSolrResultList results = null;

        String solrIndex = null;
        if (customSolrIndex != null && customSolrIndex.length() > 0) {
            solrIndex = customSolrIndex;
        } else {
//            solrIndex = getDefaultSolrIndex();
        }

        if (cmso != null) {
            Map<String, String[]> parameters = CmsRequestUtil.createParameterMap(query);
            CmsSolrQuery solrQuery = new CmsSolrQuery(cmso, parameters);
            results = OpenCms.getSearchManager().getIndexSolr(
                    solrIndex).search(cmso, solrQuery, true);
        }
        return results;
    }

    /**
     * Segun la configuracion por defecto si el containerpage contiene algun valor
     * en la propiedad search.index, dicho valor sera el indice de busqueda.
     * En caso de que no contenga valor si estamos en el projecto online busca
     * en "Solr Online" si estamos en el proyecto offline busca en "Solr Offline"
     * @return
     */
//    private String getDefaultSolrIndex() {
//        String uri = cmso.getRequestContext().getUri();
//        try {
//            CmsProperty propIndex = cmso.readPropertyObject(uri, PROP_SOLR_INDEX, true);
//        } catch (CmsException e) {
////            LOG.error();//TODO: mostrar error de busqueda propiedad
//        }
//        if (cmso.getRequestContext().getCurrentProject().isOnlineProject()) {
//
//        }
//
//    }

    // TODO Crear simpleSearch con busqueda por defecto de 10 resultados

    /**
     * Creamos la query para realizar la busqueda segun los
     * atributos de los que disponemos
     * @return
     */
    public String createQuery (){
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, List<String>> paramEntry : queryParams.entrySet()) {
            List<String> paramList = paramEntry.getValue();
            if (paramList != null && !paramList.isEmpty()){
                sb.append(paramEntry.getKey());
                for (int i = 0; i < paramList.size(); i++) {
                    String param = paramList.get(i);
                    if (i == 0){
                        sb.append(QUERY_OPEN_P);
                        sb.append(param);
                    } else {
                        sb.append(" " + QUERY_OR + " " + param);
                    }
                    if (i == paramList.size() - 1) {
                        sb.append(QUERY_CLOSE_P);
                    }
                }
            }
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
     * @param type
     * @param param
     */
    public void addSolrQueryParameter(String type, String param){
        if (queryParams.get(type) != null) {
            queryParams.get(type).add(param);
        } else {
            ArrayList<String> strings = new ArrayList<String>();
            strings.add(param);
            queryParams.put(type,strings);
        }
    }

    /**
     * Incluye varios parametros de busqueda del mismo tipo
     * @param type
     * @param params
     */
    public void addSolrQueryParameter(String type, String[] params){
        for (String param : params) {
            addSolrQueryParameter(type, param);
        }
    }

    /**
     * Incluye varios parametros de busqueda del tipo correspondiente
     * a la posicion del array
     * @param types
     * @param params
     */
    public void addSolrQueryParameter(String[] types, String[] params){
        if (types.length != params.length){
            throw new IllegalArgumentException("ERROR types and params length must be equal");
        }
        for (int i = 0; i < types.length; i++) {
            addSolrQueryParameter(types[i], params[i]);
        }
    }

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
}
