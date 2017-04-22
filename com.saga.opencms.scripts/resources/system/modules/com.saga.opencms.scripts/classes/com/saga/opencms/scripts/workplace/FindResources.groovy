package com.saga.opencms.scripts.workplace

import com.saga.opencms.scripts.SgReportManager
import com.saga.opencms.util.SgCnt
import com.saga.opencms.util.SgFindRes
import com.saga.opencms.util.SgLog
import groovy.json.JsonSlurper
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource

public class FindResources {

	CmsObject cmso;
	String idProceso;
	String jsonPath;
	String siteRoot;
	SgLog log;

	String jsonStr;
	def json;

	class ScriptReport {
		def infos = [:];
		def warns = [:];
		def errors = [:];
	}
	ScriptReport report;

	public void init(def cmso, def idProceso, def jsonPath) {
		this.report = new ScriptReport();

		this.cmso = cmso;
		this.idProceso = idProceso;
		this.jsonPath = jsonPath;

		siteRoot = cmso.getRequestContext().getSiteRoot()

		/******* PROCESO ********/
		log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());
		// Ejecuta script
		execute()

		// Report final
		showReport();
	}

	public void execute() {
		try {

			// Obtenemos los datos del fichero json de configuracion
			loadConfig()

			SgFindRes sgFind = new SgFindRes(cmso, jsonStr);
			List<CmsResource> resources = sgFind.find();

			log.init()
			resources.each{
				log.add(it);
				report.infos.put(it.getRootPath(), "Encontrado recurso")
			}
			log.print();

		} catch (Exception e) {
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
			report.errors.put("TransformResources", "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
		}
	}

	def loadConfig(){
		log.init()

		// Obtenemos el json que relaciona el nombre del fichero antiguo con el id nuevo
		SgCnt cnt = new SgCnt(cmso, jsonPath)
		jsonStr = cnt.strContent;
		json = new JsonSlurper().parseText(jsonStr);
		log.add("Json de configuracion: $json")
				.print()
	}

	def showReport(){
		log.init().add("***REPORT FINAL***")
		int infosTotal = report.infos.size();
		log.add("MODIFICADOS ($infosTotal)")
		report.infos.eachWithIndex{ key, value, idx ->
			log.add("(${idx + 1}/$infosTotal - $key - $value)")
		}
		log.print()

		int warnsTotal = report.warns.size();
		log.init().warn("AVISOS ($warnsTotal)")
		report.warns.eachWithIndex{ key, value, idx ->
			log.warn("(${idx + 1}/$warnsTotal - $key - $value)")
		}
		log.print()

		int errorsTotal = report.errors.size();
		log.init().error("ERRORES ($errorsTotal)")
		report.errors.eachWithIndex{ key, value, idx ->
			log.error("(${idx + 1}/$errorsTotal - $key - $value)")
		}
		log.print()
	}
}
