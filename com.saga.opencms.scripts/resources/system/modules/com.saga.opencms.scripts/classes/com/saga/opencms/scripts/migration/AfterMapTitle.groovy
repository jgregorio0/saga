package com.saga.sagasuite.scriptgroovy.migration
import com.saga.sagasuite.scriptgroovy.util.SgCnt
import com.saga.sagasuite.scriptgroovy.util.SgLog
import com.saga.sagasuite.scripts.SgReportManager
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsProperty
import org.opencms.file.CmsResource
import org.opencms.file.types.I_CmsResourceType
import org.opencms.json.JSONObject
import org.opencms.main.OpenCms

public class AfterMapTitle {

	CmsObject cmso;
	String idProceso;
	JSONObject jsonCnf;
	def resMods = [:];

	String jsonPath;
//	boolean modResources;
	String siteRoot;
	SgLog log;
	def errors;
	def warns;
	def infos;

	String sourceFolder;
	String targetFolder;
	String sourceType;
	String targetType;
	String targetPattern;
	boolean isSameResource;
	def mappingsLoc;
	def propsAdd = [:];
	def propsRm = [];
	def propsCp = [];
	def propsMap = [:];
	def jExBefore;
	def jExAfter;

	public void init(def cmso, def idProceso, def jsonCnf, def resMods) {
		this.cmso = cmso;
		this.idProceso = idProceso;
		this.jsonCnf = jsonCnf;
		this.resMods = resMods;

		this.errors = [:]
		this.warns = [:]
		this.infos = [:]

		/******* PROCESO ********/
		log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());

		execute();
	}

	public void execute() {
		try {
			log.init()

			// Obtenemos los datos del fichero json de configuracion
			loadConfig()
			log.print().init()

			// Obtenemos los recursos modificados
			def resources = resMods.keySet();

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
					warns.put(res, "WARN: ${e.getMessage()} -- Cause: ${e.getCause()}")
				} catch (Exception e) {
					e.printStackTrace()
					log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
					errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
				}
			}
		} catch (Exception e) {
			e.printStackTrace()
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
		}
	}

	def loadConfig(){
		// Obtenemos el json que relaciona el nombre del fichero antiguo con el id nuevo
//		SgCnt jsonCnt = new SgCnt(cmso, jsonPath)
		JSONObject json = jsonCnf;

		// Obtenemos los datos del json de configuracion
		sourceFolder = (String)getJSON(json, "sourceFolder");
		targetFolder = (String)getJSON(json, "targetFolder");
		sourceType = (String)getJSON(json, "sourceType");
		targetType = (String)getJSON(json, "targetType");
		targetPattern = (String)getJSON(json, "targetPattern");

		jsonCnf = json;
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

	def validResource(CmsResource res) {
		I_CmsResourceType xmlPageType =
				OpenCms.getResourceManager().getResourceType("xmlpage");
		I_CmsResourceType resType =
				OpenCms.getResourceManager().getResourceType(res.typeId)
		boolean isXmlPage = resType.isIdentical(xmlPageType)
		if (!isXmlPage) {
			throw new WarnException("No es xmlpage");
		}
		return true;
	}
}