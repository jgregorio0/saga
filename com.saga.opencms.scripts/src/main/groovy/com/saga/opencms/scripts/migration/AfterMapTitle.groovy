package com.saga.sagasuite.scriptgroovy.migration

import com.saga.sagasuite.scriptgroovy.util.SgCnt
import com.saga.sagasuite.scriptgroovy.util.SgLog
import com.saga.sagasuite.scripts.SgReportManager
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsProperty
import org.opencms.file.CmsResource
import org.opencms.json.JSONObject

public class AfterMapTitle {

	def report;
	CmsObject cmso;
	String idProceso;
	JSONObject jsonCnf;
	def resMods = [:];

	String jsonPath;
//	boolean modResources;
	String siteRoot;
	SgLog log;

	String sourcePath;
	String sourceFolder;
	String sourceType;
	String targetFolder;
	String targetType;
	String targetPattern;

	public void init(def cmso, def idProceso, def jsonCnf, def resMods, def report) {
		this.report = report;
		this.cmso = cmso;
		this.idProceso = idProceso;
		this.jsonCnf = jsonCnf;
		this.resMods = resMods;

		/******* PROCESO ********/
		log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());
		log.init().add("*** AfterMapTitle BEGIN ***")
		execute();
		log.init().add("*** AfterMapTitle END ***")
	}

	public void execute() {
		try {

			// Obtenemos los datos del fichero json de configuracion
			loadConfig()

			// Obtenemos los recursos modificados
			def resources = resMods;

			//Cargamos el porcentaje
			int total = resources.size()
			log.percentage(0, total)
					.add("Numero de elementos a tratar: $total")
					.print()

			resources.eachWithIndex { res, i ->
				try {
					log.loop(i+1, total)
							.add("(${i+1}/$total) -> ${res.getRootPath()}");

					CmsProperty titleSource = cmso.readPropertyObject(res, "title.source", false);
					if (titleSource == null || titleSource.isNullProperty()) {
						throw new Exception("No existe propiedad 'title.source'")
					}

					String propValue = titleSource.getValue();
					if (StringUtils.isBlank(propValue)) {
						throw new Exception("El valor de la propiedad 'title.source' es vacío")
					}

					SgCnt cnt = new SgCnt(cmso, res);
					cnt.setStringValue("Title", propValue)
							.saveXml();
					log.add("Modificado el recurso ${res.getRootPath()} con el título $propValue")
					log.print()
				} catch (WarnException e) {
					e.printStackTrace()
					log.warn("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
					report.warns.put(res, "WARN: ${e.getMessage()} -- Cause: ${e.getCause()}")
				} catch (Exception e) {
					e.printStackTrace()
					log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
					report.errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
				}
			}
		} catch (Exception e) {
			e.printStackTrace()
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
			report.errors.put("AfterMapTitle", "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
		}
	}

	def loadConfig(){
		log.init()

		// Obtenemos el json que relaciona el nombre del fichero antiguo con el id nuevo
		log.add("json de configuracion ${jsonCnf.toString()}")

		// Source
		JSONObject jSource = (JSONObject)getJSON(jsonCnf, "source");
		sourcePath = (String)getJSON(jSource, "path");
		sourceType = (String)getJSON(jSource, "type");
		if (StringUtils.isBlank(sourcePath)
				|| StringUtils.isBlank(sourceType)) {
			throw new Exception("source.path y source.type no pueden ser nulos")
		}
		sourceFolder = CmsResource.getFolderPath(sourcePath);

		// Target
		JSONObject jTarget = (JSONObject)getJSON(jsonCnf, "target");
		targetFolder = (String)getJSON(jTarget, "folder");
		targetType = (String)getJSON(jTarget, "type");
		targetPattern = (String)getJSON(jTarget, "pattern");
		if (StringUtils.isBlank(targetFolder)
				|| StringUtils.isBlank(targetType)
				|| StringUtils.isBlank(targetPattern)) {
			throw new Exception("target.path y target.type no pueden ser nulos")
		}


		log.add("Obtenemos datos:")
		log.add("sourcePath: Obligatorio. Recurso a transformar o carpeta contenedora de los recursos a transformar")
		log.add("$sourcePath")
		log.add("sourceFolder: Calculado. Carpeta que contiene el/los recurso/s a transformar")
		log.add("$sourceFolder")
		log.add("sourceType: Obligatorio. Tipo de recurso a transformar.")
		log.add("$sourceType")
		log.add("targetFolder: Obligatorio. Carpeta destino donde se generan los recursos transformados.")
		log.add("$targetFolder")
		log.add("targetType: Obligatorio. Tipo de los recursos transformados.")
		log.add("$targetType")
		log.add("targetPattern: Obligatorio. Patrón de generación de recursos.")
		log.add("$targetPattern")
				.print()
	}

	public Object getJSON(JSONObject json, String key){
		Object o = null;
		try {
			o = json.get(key)
		} catch (Exception e){
			log.warn("No existe valor $key en el json de configuracion")
		}
		return o;
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