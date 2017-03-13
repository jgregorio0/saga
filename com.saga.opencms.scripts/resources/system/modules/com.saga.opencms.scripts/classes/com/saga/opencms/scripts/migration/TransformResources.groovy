package com.saga.sagasuite.scriptgroovy

import com.saga.sagasuite.scriptgroovy.util.*
import com.saga.sagasuite.scripts.SgReportManager
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
	static String EQUALS_TARGET_PATTERN = "EQUALS"
	static String CP_ALL_PROPS = "ALL"

	CmsObject cmso;
	String idProceso;

	String jsonPath;
//	boolean modResources;
	String siteRoot;
	SgLog log;
	def errors;
	def warns;
	def infos;

	def mapPaths;
	def mapUUIDs;
	JSONObject jsonCnf;
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

	public void init(def cmso, def idProceso, def mapFile, def onlyCheck) {
		this.cmso = cmso;
		this.idProceso = idProceso;
		this.jsonPath = mapFile;
//		this.modResources = !onlyCheck;
		this.mapPaths = [:]
		this.mapUUIDs = [:]
		this.errors = [:]
		this.warns = [:]
		this.infos = [:]

		siteRoot = cmso.getRequestContext().getSiteRoot()

		/******* PROCESO ********/
		log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());
//		log.init().add("hola!!").print()
		execute()
	}

	public void execute() {
		try {
			log.init()

			// Obtenemos los datos del fichero json de configuracion
			loadConfig()
			log.print().init()

			// Ejecutamos scripts before
			executeBefore()

			// Obtenemos los recursos a modificar
			def resources = obtainResources(cmso, sourceFolder)

			// Inicializamos la carpeta auxiliar
			if (isSameResource) {
				targetFolder = sourceFolder + AUX_FOLDER
				cleanAuxFolder(targetFolder)
			}

			//Cargamos el porcentaje
			int total = resources.size()
			log.percentage(0, total)
					.add("Numero de elementos a tratar: $total")
					.print()

			resources.eachWithIndex{ res, i ->
				try {
					log.loop(i+1, total)
							.add("(${i+1}/$total) -> ${res.getRootPath()}");

					// Si es un recurso valido
					if (validResource(res)) {

						// Obtenemos el recurso antiguo
						def oldXml = getOldXmlRes(res)
						log.add("Obtenemos contenido xml antiguo")

						// Creamos el nuevo recurso
						String newResPath = createResource(res)
						log.add("creado nuevo recurso: $newResPath");

						// Inicializamos el contenido nuevo
						SgCnt newCnt = new SgCnt(cmso, newResPath);

						// Para cada locale
						mappingsLoc.each { localeStr, mappings ->
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
								log.warn("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
								warns.put(res, "WARN: ${e.getMessage()} -- Cause: ${e.getCause()}")
							} catch (Exception e) {
								e.printStackTrace()
								log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
								errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
							}

						}

						// Guardamos los cambios en el recurso
						newCnt.saveXml();
						log.add("Modificado contenido de $newResPath");

						// Copiamos agregamos y eliminamos propiedades
						cpProps(res.getRootPath(), newResPath)
						mapProps(res.getRootPath(), newResPath)
						addProps(newResPath)
						rmProps(newResPath)
						log.add("Copiamos las propiedades $propsCp")
						log.add("Mapeamos las propiedades $propsMap")
						log.add("Agregamos las propiedades $propsAdd")
						log.add("Eliminamos las propiedades $propsRm")


						// Relacionamos el antiguo con el nuevo
						addMappingRelation(res.rootPath, newResPath);

						// Mostramos info
						infos.put(res,"Transformación completa del recurso ${res.getRootPath()} a $newResPath")
						log.add("Transformación completa del recurso ${res.getRootPath()} a $newResPath");
						log.print();

						// Solo para patron EQUALS: Procedemos a copiar el recurso auxiliar en el original
						if (isSameResource) {
							log.add("EQUALS: Procedemos a copiar el recurso auxiliar  $newResPath en el original ${res.getRootPath()}");

							try {
								// Desde auxPath a res
								String auxPath = newResPath;
								String originPath = res.getRootPath();

								// Cambiamos tipo y modificamos contenido
								SgCms sgCms = new SgCms(cmso);
								sgCms.changeType(originPath, targetType)
								log.add("Cambiamos el tipo del recurso original $originPath a $targetType")

								SgCnt oriCnt = new SgCnt(cmso, originPath)
								SgCnt auxCnt = new SgCnt(cmso, auxPath)
								oriCnt.saveStr(auxCnt.strContent)
								log.add("Copiamos el contenido del recurso auxiliar $auxPath -> al recurso original $originPath")

								// Agregamos y eliminamos propiedades
								cpProps(auxPath, originPath)
								// No mapeamos ya que se mapean propiedades erroneas produce un error
//								addProps(originPath)
								rmProps(originPath)
								log.add("Mapeamos las propiedades $propsMap")
								log.add("Agregamos las propiedades $propsAdd")
								log.add("Eliminamos las propiedades $propsRm")

								log.print();
								infos.put(res,"Transformación completa del recurso $originPath sobre sí mismo")

								// TODO eliminar auxiliares
							} catch (Exception e) {
								e.printStackTrace()
								log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
								errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
							}
						}
					}

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

			// Si queremos transformar el mismo recurso y no generar uno nuevo (targetPattern == EQUALS y sourceFolder == targetFolder)
//			if (isSameResource) {
//
//				log.init()
//						.add("***EQUALS: PROCEDEMOS A COPIAR RECURSOS AUXILIARES EN ANTIGUOS***")
//						.print();
//				total = resources.size()
//				resources.eachWithIndex { res, i ->
//					try {
//						log.loop(i + 1, total)
//								.add("(${i + 1}/$total) -> ${res.getRootPath()}");
//
//						String auxPath = mapPaths.get(res.getRootPath())
//						if (!auxPath) {
//							log.warn("No ha sido transformado")
//						} else {
//
//							// Cambiamos tipo y modificamos contenido
//							SgCms sgCms = new SgCms(cmso);
//							sgCms.changeType(cmso.getSitePath(res), targetType)
//							log.add("Cambiamos el tipo del recurso antiguo ${res.getRootPath()} a $targetType")
//
//							SgCnt oldCnt = new SgCnt(cmso, res)
//							SgCnt newCnt = new SgCnt(cmso, auxPath)
//							oldCnt.saveStr(newCnt.strContent)
//							log.add("Copiamos el contenido del recurso auxiliar $auxPath -> al recurso antiguo ${res.getRootPath()}")
//
//							// Copiamos propiedades
//							mapProps(res.getRootPath(), newResPath)
//							addProps(newResPath)
//							rmProps(newResPath)
//							log.add("Mapeamos las propiedades $propsMap")
//							log.add("Agregamos las propiedades $propsAdd")
//							log.add("Eliminamos las propiedades $propsRm")
//						}
//
//						log.print()
//
//						// TODO eliminar auxiliares
//					} catch (Exception e) {
//						e.printStackTrace()
//						log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
//						errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
//					}
//				}
//			}
//			log.print()

			// Ejecutamos scripts before
			executeAfter()

			// Report final
			showReport();
		} catch (Exception e) {
			log.print();
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
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
			SgProperties sgProps = new SgProperties(cmso)
			sgProps.addProperties(targetPath, propsAdd)
		}
		return this;
	}

	def mapProps(String sourcePath, String targetPath) {
		// Mapeamos las propiedades
		if (propsCp.size() > 0){
			SgProperties sgProps = new SgProperties(cmso)
			sgProps.mapProperties(sourcePath, targetPath, propsMap, false)
		}
		return this;
	}

	def rmProps(String targetPath) {
		// Eliminamos propiedades
		if (propsRm.size() > 0) {
			SgProperties sgProps = new SgProperties(cmso)
			sgProps.rmProperties(targetPath, propsRm);
		}
		return this;
	}

	def cpProps(String sourcePath, String targetPath) {
		// Si contiene la clave ALL copiamos todas las propiedades. Sino copiamos solo el listado.
		if (propsCp.size() > 0){
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

	def executeBefore() {
		//TODO agregar script
//		def scripts = jsonCnf.getJSONObject("scripts");
//		scripts.getJSONArray("before")
	}

	def executeAfter() {
		if (jExAfter){
			String path = (String)getJSON(jExAfter, "path")
//			if (!path.startsWith("/")) {
//				String webInfPath = OpenCms.getSystemInfo().getWebInfRfsPath().replaceAll("\\\\", "/");
//				path = webInfPath + path;
//			}
			String method = (String)getJSON(jExAfter, "method")
			JSONArray jArgs = (JSONArray)getJSON(jExAfter, "args")
			def args = [];
			jArgs.m_myArrayList.each {
				args.add(this.getProperty(it))
			}

			new SgScript().invokeScript(cmso, path, method, args.toArray())
		}
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
				OpenCms.getResourceManager().getResourceType("xmlpage");
		I_CmsResourceType resType =
				OpenCms.getResourceManager().getResourceType(res.typeId)
		boolean isXmlPage = resType.isIdentical(xmlPageType)
		if (!isXmlPage) {
			throw new WarnException("No es xmlpage");
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
		if (targetPattern.equals("EQUALS")) {
			if (sourceFolder.equals(targetFolder)) {
				throw new IllegalArgumentException(
						"La carpeta de origen no puede ser la misma que la de destino" +
								" ($sourceFolder) si se ha seleccionado targetPattern 'EQUALS'")
			}
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

	boolean isSameResource(def targetFolder, def sourceFolder, def targetPattern){
		return targetFolder.equals(sourceFolder) && targetPattern.equals(EQUALS_TARGET_PATTERN);
	}

	def cleanAuxFolder(String folder){
		if (cmso.existsResource(folder)) {
			SgCms.delete(cmso, folder)
		}
		new SgCms(cmso).createFolder(folder)
	}

	def showReport(){
		log.init().add("***REPORT FINAL***")
		int infosTotal = infos.size();
		log.add("MODIFICADOS ($infosTotal)")
		infos.eachWithIndex{ key, value, idx ->
			log.add("(${idx + 1}/$infosTotal - $key - $value)")
		}
		log.print()

		int warnsTotal = warns.size();
		log.init().warn("AVISOS ($warnsTotal)")
		warns.eachWithIndex{ key, value, idx ->
			log.warn("(${idx + 1}/$warnsTotal - $key - $value)")
		}
		log.print()

		int errorsTotal = errors.size();
		log.init().error("ERRORES ($errorsTotal)")
		errors.eachWithIndex{ key, value, idx ->
			log.error("(${idx + 1}/$warnsTotal - $key - $value)")
		}
		log.print()
	}

	def loadConfig(){
		// Obtenemos el json que relaciona el nombre del fichero antiguo con el id nuevo
		SgCnt jsonCnt = new SgCnt(cmso, jsonPath)
		JSONObject json = new JSONObject(jsonCnt.strContent);
		log.add("json de configuracion ${json.toString()}")

		// Obtenemos los datos del json de configuracion
		sourceFolder = (String)getJSON(json, "sourceFolder");
		targetFolder = (String)getJSON(json, "targetFolder");
		sourceType = (String)getJSON(json, "sourceType");
		targetType = (String)getJSON(json, "targetType");
		targetPattern = (String)getJSON(json, "targetPattern");
		isSameResource = isSameResource(targetFolder, sourceFolder, targetPattern)

		mappingsLoc = [:];
		JSONObject jLocales = (JSONObject)getJSON(json, "mapping")
		jLocales?.keys().each {
			def mappings = [:];
			JSONObject jMappings = (JSONObject)getJSON(jLocales, it)
			jMappings.keys().each {
				mappings.put(it, getJSON(jMappings,it))
			}
			mappingsLoc.put(it, mappings)
		}

		// Propiedades
		JSONObject jProperties = (JSONObject)getJSON(json, "properties")

		// Copiamos
		JSONArray jArrayCp = (JSONArray)getJSON(jProperties, "cp");
		propsCp = jArrayCp?.length() > 0 ? jArrayCp.m_myArrayList : [];

		// Agregamos
		JSONObject jAdds = (JSONObject)getJSON(jProperties, "add")
		propsAdd = jAdds?.length() > 0 ? jAdds.m_map : [:];

		// Eliminamos
		JSONArray jArrayRm = (JSONArray)getJSON(jProperties, "rm")
		propsRm = jArrayRm?.length() > 0 ? jArrayRm.m_myArrayList : [];

		// Mapeamos
		JSONObject jMaps = (JSONObject)getJSON(jProperties, "map")
		propsMap = jMaps?.length() > 0 ? jMaps.m_map : [:];

		// Obtenemos los scripts de ejecucion
		JSONObject jScripts = (JSONObject)getJSON(json, "scripts");
		jExBefore = jScripts == null ? null : (JSONObject)getJSON(jScripts, "executeBefore");
		jExAfter = jScripts == null ? null : (JSONObject)getJSON(jScripts, "executeAfter");

		log.add("Obtenemos datos:")
		log.add("sourceFolder: $sourceFolder")
		log.add("targetFolder: $targetFolder")
		log.add("sourceType: $sourceType")
		log.add("targetType: $targetType")
		log.add("targetPattern: $targetPattern")
		log.add("mappings: $mappingsLoc")
		log.add("Copy properties: $propsCp")
		log.add("Map properties: $propsMap")
		log.add("Add properties: $propsAdd")
		log.add("Remove properties: $propsRm")
		log.add("execute Before: $jExBefore")
		log.add("execute After: $jExAfter")

		log.print()

		jsonCnf = json;
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
			log.warn("No existe valor $key en el json de configuracion")
		}
		return o;
	}
}