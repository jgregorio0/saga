package com.saga.sagasuite.scriptgroovy.migration

import com.saga.sagasuite.scriptgroovy.util.*
import com.saga.sagasuite.scripts.SgReportManager
import org.apache.commons.lang3.StringEscapeUtils
import org.opencms.file.CmsObject

public class MappingResources {

    CmsObject cmso;
    String idProceso;
    String jsonPath;
//	boolean modResources;
    String siteRoot;
    SgLog log;

    class ScriptReport {
        def infos = [:];
        def warns = [:];
        def errors = [:];
    }
    ScriptReport report;

    String contentStr;
    def json;

    public void init(def cmso, def idProceso, def mapFile, def onlyCheck) {
        this.report = new ScriptReport();

        this.cmso = cmso;
        this.idProceso = idProceso;
        this.jsonPath = mapFile;

        siteRoot = cmso.getRequestContext().getSiteRoot()

        /******* PROCESO ********/
        log = new SgLog(
                SgReportManager.getInstance(cmso),
                idProceso,
                cmso.getRequestContext().getCurrentUser().getName());
        // Ejecuta script
        execute()
    }

    public void execute() {
        try {

            log.init()

            // Obtenemos el json que relaciona el nombre del fichero antiguo con el id nuevo
            SgCnt cnt = new SgCnt(cmso, jsonPath)
            log.add("Content: " + StringEscapeUtils.escapeXml(cnt.strContent)).print()

            // From xml content to JSON
            String jsonStr = SgMapping.toJson(cnt.strContent);
            log.add("JSON: $jsonStr").print();

            // From json to CmsResource
            SgMapping mapping = new SgMapping(cmso);
            String newResRootPath = "/sites/default/test.xml";
            mapping.mapResource(newResRootPath, "seo_file", jsonStr);
            log.add("mapeado recurso: $newResRootPath").print();
        } catch (Exception e) {
            log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
            report.errors.put("MappingResources", "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
        }
    }
}