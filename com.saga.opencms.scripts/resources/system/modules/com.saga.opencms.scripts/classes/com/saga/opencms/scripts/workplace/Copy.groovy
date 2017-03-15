package com.saga.opencms.scripts.workplace

import com.saga.opencms.scripts.SgReportManager
import com.saga.opencms.util.SgCms
import com.saga.opencms.util.SgLog
import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsResourceFilter
import org.opencms.main.OpenCms

public class Copy {

	CmsObject cmso;
	String idProceso;
	String jsonStr;
	String siteRoot;
	SgLog log;
	def errors;
	def warns;
	def infos;
	def mods;
	def json;

	public void init(def cmso, def idProceso, def jsonStr) {
		this.cmso = cmso;
		this.idProceso = idProceso;
		this.jsonStr = jsonStr;
		this.errors = [:]
		this.warns = [:]
		this.infos = [:]

		siteRoot = cmso.getRequestContext().getSiteRoot()

		/******* PROCESO ********/
		log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());
		execute()
	}

	public void execute() {
		try {
			// Obtenemos los datos del fichero json de configuracion
			loadConfig()

			// Validamos configuracion
			if (validate()) {

				def resources = findResources();
			}

			// Report final
			showReport();
		} catch (WarnException e) {
			e.printStackTrace()
			log.warn("WARN: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
			warns.put(this.class, "WARN: ${e.getMessage()} -- Cause: ${e.getCause()}")
		} catch (Exception e) {
			e.printStackTrace()
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
			errors.put(this.class, "ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}")
		}
	}

	def findResources() {
		CmsResourceFilter resFilter = CmsResourceFilter.ALL;

		// Si existe configuracion para el filtro
		def filter = json.source.filter;
		if (filter) {
			log.add("Configuracion del filtro de recursos")

			// tipo de recurso
			if (filter.type) {
				def type = OpenCms.getResourceManager().getResourceType(filter.type)
				resFilter.addRequireType(type);
				log.add("Tipo de recurso: ${filter.type}")
			}

			resFilter.requireTimerange()
		}
		SgCms.findResources(cmso, )
	}

	boolean validate() {
		if (StringUtils.isBlank(json.source)
				|| StringUtils.isBlank(json.source.path)) {
			throw new Exception("source y source.path no pueden ser nulos")
		}
		return true;
	}

	def showReport() {
		log.init().add("***REPORT FINAL***")
		int infosTotal = infos.size();
		log.add("MODIFICADOS ($infosTotal)")
		infos.eachWithIndex { key, value, idx ->
			log.add("(${idx + 1}/$infosTotal - $key - $value)")
		}
		log.print()

		int warnsTotal = warns.size();
		log.init().warn("AVISOS ($warnsTotal)")
		warns.eachWithIndex { key, value, idx ->
			log.warn("(${idx + 1}/$warnsTotal - $key - $value)")
		}
		log.print()

		int errorsTotal = errors.size();
		log.init().error("ERRORES ($errorsTotal)")
		errors.eachWithIndex { key, value, idx ->
			log.error("(${idx + 1}/$warnsTotal - $key - $value)")
		}
		log.print()
	}

	def loadConfig() {
		log.init()
				.add("json de configuracion $jsonStr")

		// Inicializa json de entrada
		json = new JsonSlurper().parseText(jsonStr);
	}

	public class WarnException extends Exception {
		public WarnException() {
			super();
		}
		public WarnException(String s) {
			super(s);
		}
	}
}