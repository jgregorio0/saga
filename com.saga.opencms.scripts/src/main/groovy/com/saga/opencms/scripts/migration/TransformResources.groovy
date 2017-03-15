package com.saga.sagasuite.scriptgroovy

import com.saga.sagasuite.scriptgroovy.util.*
import com.saga.sagasuite.scripts.SgReportManager
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.i18n.CmsEncoder
import org.opencms.json.JSONArray
import org.opencms.json.JSONObject
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.page.CmsXmlPageFactory

public class TransformResources {

	static String AUX_FOLDER = "newTransformResources/"
	static String S_EQ_T_PATTERN = "SOURCE_EQ_TARGET"
	static String CP_ALL_PROPS = "ALL"

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

	def modRes;
	def mapPaths;
	def mapUUIDs;
	JSONObject jsonCnf;
	String sourceFolder;
	String sourceType;
	String sourcePath;
	String targetFolder;
	String targetType;
	String targetPattern;
	boolean isSourceEqTarget;
	boolean isMapping;
	boolean isProperties;
	boolean isScripts;
	def mapping;
	def propsAdd = [:];
	def propsRm = [];
	def propsCp = [];
	def propsMap = [:];
	def jExBefore;
	def jExAfter;

	public void init(def cmso, def idProceso, def mapFile, def onlyCheck) {
		this.report = new ScriptReport();

		this.cmso = cmso;
		this.idProceso = idProceso;
		this.jsonPath = mapFile;
//		this.modResources = !onlyCheck;
		this.mapPaths = [:]
		this.mapUUIDs = [:]
		this.modRes = [];

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

			// Ejecutamos scripts before
			executeBefore()

			// Ejecutamos transformar recursos
			executeTransform();

			// Ejecutamos scripts before
			executeAfter()
		} catch (Exception e) {
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
			report.errors.put("TransformResources", "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
		}
	}

	def loadConfig(){
		log.init()

		// Obtenemos el json que relaciona el nombre del fichero antiguo con el id nuevo
		SgCnt cnt = new SgCnt(cmso, jsonPath)
		jsonCnf = new JSONObject(cnt.strContent);
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

		// Check if it is source eq target case
		isSourceEqTarget = isSourceEqTarget(targetFolder, sourceFolder, targetPattern)

		// Mappings
		mapping = [:];
		JSONObject jLocales = (JSONObject)getJSON(jsonCnf, "mapping")
		isMapping = jLocales != null;
		if (isMapping) {
			jLocales.keys().each {
				def mappings = [:];
				JSONObject jMappings = (JSONObject)getJSON(jLocales, it)
				jMappings.keys().each {
					mappings.put(it, getJSON(jMappings,it))
				}
				mapping.put(it, mappings)
			}
		}

		// Properties
		JSONObject jProperties = (JSONObject)getJSON(jsonCnf, "properties")
		isProperties = jProperties != null;
		if (isProperties) {

			// Copy properties
			JSONArray jArrayCp = (JSONArray)getJSON(jProperties, "cp");
			if (jArrayCp != null && jArrayCp.length() > 0) {
				propsCp = jArrayCp.m_myArrayList;
			}

			// Add properties
			JSONObject jAdds = (JSONObject)getJSON(jProperties, "add")
			if (jAdds != null && jAdds.length() > 0) {
				propsAdd = jAdds.m_map;
			}

			// Remove properties
			JSONArray jArrayRm = (JSONArray)getJSON(jProperties, "rm")
			if (jArrayRm != null && jArrayRm.length() > 0) {
				propsRm = jArrayRm.m_myArrayList;
			}

			// Mapping properties
			JSONObject jMaps = (JSONObject)getJSON(jProperties, "map")
			if (jMaps != null && jMaps.length() > 0) {
				propsMap = jMaps.m_map;
			}
		}

		// Scripts
		JSONObject jScripts = (JSONObject)getJSON(jsonCnf, "scripts");
		isScripts = jScripts != null
		if (isScripts) {
			jExBefore = jScripts == null ? null : (JSONObject)getJSON(jScripts, "executeBefore");
			jExAfter = jScripts == null ? null : (JSONObject)getJSON(jScripts, "executeAfter");
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
		log.add("mappings: Opcional. Mapeo del contenido del recurso.")
		log.add("$mapping")
		log.add("properties.cp: Opcional. Propiedades a copiar.")
		log.add("$propsCp")
		log.add("properties.map: Opcional. Propiedades a mapear con otro nombre.")
		log.add("$propsMap")
		log.add("properties.add: Opcional. Propiedades a añadir.")
		log.add("$propsAdd")
		log.add("properties.rm: Opcional. Propiedades a eliminar.")
		log.add("$propsRm")
		log.add("scripts.before: Opcional. Script a ejecutar antes de transformar los recursos.")
		log.add("$jExBefore")
		log.add("scripts.after: Opcional. Script a ejectutar despues de transformar los recursos.")
		log.add("$jExAfter")
				.print()
	}

	public void executeBefore() {
		if (jExBefore){
			String path = (String)getJSON(jExBefore, "path")
			String method = (String)getJSON(jExBefore, "method")
			JSONArray jArgs = (JSONArray)getJSON(jExBefore, "args")
			def args = [];
			jArgs.m_myArrayList.each {
				args.add(this.getProperty(it))
			}

			new SgScript().invokeScript(cmso, path, method, args.toArray())
		}
	}

	def executeAfter() {
		if (jExAfter){
			String path = (String)getJSON(jExAfter, "path")
			String method = (String)getJSON(jExAfter, "method")
			JSONArray jArgs = (JSONArray)getJSON(jExAfter, "args")
			def args = [];
			jArgs.m_myArrayList.each {
				args.add(this.getProperty(it))
			}

			new SgScript().invokeScript(cmso, path, method, args.toArray())
		}
	}

	public void executeTransform() {
		log.init();

		// Inicializamos la carpeta auxiliar
		if (isSourceEqTarget) {
			targetFolder = sourceFolder + AUX_FOLDER
			cleanAuxFolder(targetFolder)
		}

		// Obtenemos los recursos a modificar
		def resources = findResources();

		//Cargamos el porcentaje
		int total = resources.size()
		log.percentage(0, total)
				.add("Numero de elementos a tratar: $total")
				.print()

		resources.eachWithIndex{ res, i ->
			try {
				String sourcePath = res.getRootPath()
				log.loop(i+1, total)
						.add("(${i+1}/$total) -> $sourcePath");

				// Si es un recurso valido
				if (validResource(res)) {

					// Creamos el nuevo recurso
					//TODO que pasa si ya existe?? sobrescribir
					String newResPath = createResource(res)
					log.add("creado nuevo recurso: $newResPath");

					// Mapeo de contenido
					executeMapping(res, newResPath);

					// Mapeo de propiedades
					executeProperties(res.getRootPath(), newResPath);

					// Relacionamos el antiguo con el nuevo
					addMappingRelation(res.rootPath, newResPath);

					// Mostramos info
					log.add("Transformación completa del recurso ${res.getRootPath()} a $newResPath")
							.print();
					report.infos.put(res,"Transformación completa del recurso ${res.getRootPath()} a $newResPath")

					// Solo para patron EQUALS: Procedemos a copiar el recurso auxiliar en el original
					if (isSourceEqTarget) {
						executeSourceEqTarget(res, newResPath)
						report.infos.put(res,"Transformación completa del recurso $sourcePath sobre sí mismo")
					}
					modRes.add(res)
				}

				log.print()
			} catch (WarnException e) {
				e.printStackTrace()
				log.warn("WARN: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
				report.warns.put(res, "WARN: ${e.getMessage()} -- Cause: ${e.getCause()}")
			} catch (Exception e) {
				e.printStackTrace()
				log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
				report.errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
			}
		}
	}

	/**
	 * Find source resources
	 * @return
	 */
	List<CmsResource> findResources() {
		//TODO añadir mas opciones CmsResourceFilter
//        CmsResourceFilter filter = CmsResourceFilter.ALL.requireType(
//                OpenCms.getResourceManager().getResourceType(type));
		SgCms.findResources(cmso, sourceFolder, sourceType);
	}

	/**
	 * Mapping content
	 * @param res
	 * @param newResPath
	 * @return
	 */
	def executeMapping(def res, String newResPath) {
		// Si hay configuracion de mapeo de campos
		if (isMapping){

			// Obtenemos el recurso antiguo
			def oldXml = getOldXmlRes(res)
			//TODO check if old contiene xml que se pueda leer
			log.add("Obtenemos contenido xml antiguo")

			// Inicializamos el contenido nuevo
			SgCnt newCnt = new SgCnt(cmso, newResPath);
			if (newCnt.xmlContent == null) {
				throw new Exception("Recurso $newResPath no tiene contenido xml")
			}

			// Para cada locale
			mapping.each { localeStr, mappings ->
				log.add("Para locale $localeStr")

				try {
					Locale locale = new Locale(localeStr);

					// Validamos que haya contenido para el locale dado
					if (validateOldXmlLocale(res, oldXml, locale)) {
						log.add("Existe contenido $localeStr en el recurso antiguo")

						// Iniciamos el locale
						newCnt.setLocale(locale);
						newCnt.initLocale(locale)

						// Para cada campo mapeado
						mappings.each { element, mapping ->

							// Obtenemos el valor y lo guardamos
							String valueStr = oldXml.getStringValue(cmso, element, locale)
							newCnt.setStringValue(mapping, valueStr);
							log.add("Copiamos el contenido $localeStr / $element -> $mapping")
						}
					}
				} catch (WarnException e) {
					e.printStackTrace()
					log.warn("WARN: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
					report.warns.put(res, "WARN: ${e.getMessage()} -- Cause: ${e.getCause()}")
				} catch (Exception e) {
					e.printStackTrace()
					log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
					report.errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
				}

			}

			// Guardamos los cambios en el recurso
			newCnt.saveXml();
			log.add("Modificado contenido de $newResPath");
		}
	}

	def executeProperties(String sourcePath, String targetPath) {
		if (isProperties) {
			// Copiamos mapeamos agregamos y eliminamos propiedades
			cpProps(sourcePath, targetPath)
			mapProps(sourcePath, targetPath)
			addProps(targetPath)
			rmProps(targetPath)
		}
	}

	def executeSourceEqTarget(def res, String auxPath) {
		String oriPath = res.getRootPath()
		log.add("EQUALS: Procedemos a copiar el recurso auxiliar  $auxPath >> en el original $oriPath");

		try {

			// Cambiamos tipo y modificamos contenido
			SgCms sgCms = new SgCms(cmso);
			sgCms.changeType(oriPath, targetType)
			log.add("Cambiamos el tipo del recurso original $oriPath a $targetType")

			SgCnt oriCnt = new SgCnt(cmso, oriPath)
			SgCnt auxCnt = new SgCnt(cmso, auxPath)
			oriCnt.saveStr(auxCnt.strContent)
			log.add("Copiamos el contenido")

			// Copiamos las propiedades del auxiliar y eliminamos las propiedades en origen
			new SgProperties(cmso).copyAllProperties(auxPath, oriPath, false);
			log.add("Copiamos las propiedades")
			rmProps(oriPath);

			// TODO eliminar auxiliares
		} catch (Exception e) {
			e.printStackTrace()
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
			report.errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
		}
	}

	/**
	 * Create migration target resource
	 * @param res
	 * @return
	 */
	String createResource(CmsResource res) {
		SgCms sgCms = new SgCms(cmso);
		String newResPath = getNewResPath(sgCms, res, sourceFolder, targetFolder, targetPattern)
		sgCms.createResource(newResPath, targetType)
		return newResPath;
	}

	def addProps(String targetPath) {
		// Agregamos propiedades
		if (propsAdd.size() > 0) {
			log.add("Agregamos las propiedades $propsAdd")

			SgProperties sgProps = new SgProperties(cmso)
			sgProps.addProperties(targetPath, propsAdd)
		}
		return this;
	}

	def mapProps(String sourcePath, String targetPath) {
		// Mapeamos las propiedades
		if (propsMap.size() > 0){
			log.add("Mapeamos las propiedades $propsMap")

			SgProperties sgProps = new SgProperties(cmso)
			sgProps.mapProperties(sourcePath, targetPath, propsMap, false)
		}
		return this;
	}

	def rmProps(String targetPath) {
		// Eliminamos propiedades
		if (propsRm.size() > 0) {
			log.add("Eliminamos las propiedades $propsRm")

			SgProperties sgProps = new SgProperties(cmso)
			sgProps.rmProperties(targetPath, propsRm);
		}
		return this;
	}

	def cpProps(String sourcePath, String targetPath) {
		// Si contiene la clave ALL copiamos todas las propiedades. Sino copiamos solo el listado.
		if (propsCp.size() > 0){
			log.add("Copiamos las propiedades $propsCp")

			// Copiamos propiedades
			SgProperties sgProps = new SgProperties(cmso)
			if (propsCp.contains(CP_ALL_PROPS)) {
				sgProps.copyAllProperties(sourcePath, targetPath, false)
			} else {
				sgProps.copyProperties(sourcePath, targetPath, propsCp, false)
			}
		}
		return this;
	}


	def obtainResources(CmsObject cmso, String path) {
		def cntpages = [];
		if (CmsResource.isFolder(path)) {
			CmsResourceFilter filter = CmsResourceFilter.ALL.addRequireFile();
			cntpages = cmso.readResources(path, filter, true);
		} else {
			cntpages.add(cmso.readResource(path));
		}

		return cntpages
	}

	def validResource(CmsResource res) {
		I_CmsResourceType xmlPageType =
				OpenCms.getResourceManager().getResourceType(sourceType);
		I_CmsResourceType resType =
				OpenCms.getResourceManager().getResourceType(res.typeId)
		boolean isXmlPage = resType.isIdentical(xmlPageType)
		if (!isXmlPage) {
			throw new WarnException("El recurso ${res.getRootPath()} no es de tipo $sourceType");
		}
		return true;
	}

	/**
	 * Add relation path and uuid
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	def addMappingRelation(String oldPath, String newPath){
		try {
			mapPaths.put(oldPath, newPath)
			String oldUuid = cmso.readResource(oldPath).getStructureId().toString()
			String newUuid = cmso.readResource(newPath).getStructureId().toString()
			mapUUIDs.put(newUuid, oldUuid)

			//
			SgProperties sgProps = new SgProperties(cmso)
			def props = ["migration.sourcePath": oldPath,
						 "migration.targetPath": newPath,
						 "migration.sourceUUID": oldUuid,
						 "migration.targetUUID": newUuid]
			sgProps.addProperties(newPath, props).save(oldPath)
		} catch (Exception e){
			log.error("Error mapeando relacion entre el recurso viejo $oldPath y el nuevo $newPath")
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}")
			log.print()
		}
	}

	def getNewResPath(def sgCms, def res, def sourceFolder, def targetFolder, def targetPattern){
		if (targetPattern.equals(S_EQ_T_PATTERN)) {
			return CmsStringUtil.joinPaths(targetFolder, res.getName())
		} else {
			return sgCms.nextResoucePath(targetFolder, targetPattern)
		}
	}

	def getOldXmlRes(def res){
		// TODO incluri xmlContent -- CmsXmlContentFactory.unmarshal(allContent, CmsEncoder.ENCODING_UTF_8, resolver);
		// TODO incluir sourceType xmlpage
		CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(null);
		CmsFile f = cmso.readFile(res.getRootPath(), CmsResourceFilter.ALL);
		String allContent = new String(f.getContents(), CmsEncoder.ENCODING_UTF_8);

//		CmsXmlContentFactory.unmarshal(allContent, CmsEncoder.ENCODING_UTF_8, resolver);
		return CmsXmlPageFactory.unmarshal(allContent, CmsEncoder.ENCODING_UTF_8, resolver);
	}

	boolean isSourceEqTarget(def targetFolder, def sourceFolder, def targetPattern){
		return targetFolder.equals(sourceFolder) && targetPattern.equals(S_EQ_T_PATTERN);
	}

	def cleanAuxFolder(String folder){
		if (cmso.existsResource(folder)) {
			SgCms.delete(cmso, folder)
		}
		new SgCms(cmso).createFolder(folder)
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


	public class WarnException extends Exception {
		public WarnException() {
			super();
		}
		public WarnException(String s) {
			super(s);
		}
	}

	boolean validateOldXmlLocale(def res, def xml, Locale locale){
		if (!xml.hasLocale(locale)) {
			throw new WarnException("El recurso $res no tiene locale ${locale.getLanguage()}")
		}
		return true;
	}

	public Object getJSON(JSONObject json, String key){
		Object o = null;
		try {
			 o = json.get(key)
		} catch (Exception e){
			log.add("No existe valor '$key'")
		}
		return o;
	}
}