package com.saga.opencms.scripts

import com.saga.sagasuite.scriptgroovy.util.SgCnt
import com.saga.sagasuite.scriptgroovy.util.SgLogUtil
import com.saga.sagasuite.scriptgroovy.util.SgSlurperUtil
import com.saga.sagasuite.scripts.SgReportManager
import groovy.json.internal.LazyMap
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.json.JSONObject
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil

/**
 * Ejecuta la importación de recursos
 *
 * @param cmsObject CmsObject
 * @param configPath Ruta del fichero json con la configuración de la importación
 */

class ExMappingCnt {


    CmsObject cmso;
    String idProceso;
    String cnfFile;
    String site;
    Locale locale;
    SgLogUtil log;

    public def init(def cms, def idProcess, def cnf) {
        this.cmso = cms;
        this.idProceso = idProceso;
        this.cnfFile = cnf;
        this.site = cmso.getRequestContext().getSiteRoot();
        this.locale = cmso.getRequestContext().getLocale();
        this.log = new SgLogUtil(
                SgReportManager.getInstance(cmso),
                idProcess,
                cmso.getRequestContext().getCurrentUser().getName());
        return execute();
    }

    JSONObject execute() {
        try {
            // Obtenemos el fichero de configuracion
            List mappingConfig =
                    (List) new SgSlurperUtil(cmso, cnfFile).slurpObject()
            log.add("Fichero de configuracion: " + mappingConfig.toString())

            int totalCnfs = mappingConfig.size()
            log.add("Para un total de $totalCnfs configuraciones").print()

            // Para cada configuración
            Map<String, Map<String, String>> exp =
                    new LinkedHashMap<String, Map<String, String>>();
            mappingConfig.eachWithIndex { LazyMap cnf, int iCnf ->
                log.init().add("Configuracion ${iCnf + 1}/$totalCnfs")

                // Obtenemos los parametros de busquedas
                final String folder = cleanParam(cnf.sourceFolder);
                final String type = cleanParam(cnf.sourceType);
                final String since = cleanParam(cnf.since);
                final String max = cleanParam(cnf.maxResults);
                log.add("Para los parametros " +
                        "folder: '$folder', type: '$type', since: '$since', max: '$max'")

                // Creamos el filtro y obtenemos los recursos a exportar
                final I_CmsResourceType resourceType =
                        OpenCms.getResourceManager().getResourceType(type);
                folder = CmsStringUtil.isNotEmptyOrWhitespaceOnly(folder) ? folder : "/";
                CmsResourceFilter filter = CmsResourceFilter.DEFAULT_FILES
                        .addRequireType(resourceType.getTypeId())
                        .addExcludeFlags(CmsResource.FLAG_TEMPFILE)
                        .addExcludeTimerange();
                Long startDate = toLong(since);
                if (startDate != null) {
                    filter = filter.addRequireLastModifiedAfter(startDate);
                }
                List<CmsResource> find = cmso.readResources(folder, filter, true);
                List<CmsResource> resources = new ArrayList<CmsResource>();
                Integer maxResults = toInteger(max);
                if (maxResults != null) {
                    resources = maxResults < find.size() ? find.subList(0, maxResults) : find;
                } else {
                    resources = find;
                }

                int total = resources.size();
                log.add("Obtenemos $total recursos").print()

                // Exportamos los recursos

                resources.eachWithIndex { CmsResource resource, int i ->
                    try {
                        log.init().percentage(i, total)
                                .add("${i+1}/$total - Para el recurso ${resource.rootPath}")
                        SgCnt cnt = new SgCnt(cmso, resource, locale);
                        Map<String, Map<String, String>> resExp = cnt.export();
                        (LinkedHashMap<String, Map<String, String>>)exp
                                .putAll((LinkedHashMap<String, Map<String, String>>)resExp);

                        log.add("Exportamos el contenidoj $resExp").print();
                    } catch (Exception e){
                        log.error(e).print();
                    }
                }
            }

            // Creamos el json con los recursos a exportar
            log.init()
            try {
                JSONObject jo = new JSONObject(exp);
                log.add("Exportamos el json: " + jo.toString(1)).print()

                return jo;
            } catch (Exception e) {
                log.error(e).print();
            }
        } catch (Exception e) {
            log.error(e).print();
        }
    }

    String cleanParam(String param) {
        if (param.startsWith("[")) {
            param = param.substring(1)
        }
        if (param.endsWith("]")) {
            param = param.substring(0, param.length() - 1)
        }
        return param
    }

    Integer toInteger(String max) {
        Integer maxResults = null;
        try {
            maxResults = Integer.parseInt(max);
        } catch(Exception e) {}
        return maxResults;
    }

    Long toLong(String since) {
        Long startDate = null;
        try {
            startDate = Long.parseLong(since);
        } catch(Exception e) {}
        return startDate;
    }
}