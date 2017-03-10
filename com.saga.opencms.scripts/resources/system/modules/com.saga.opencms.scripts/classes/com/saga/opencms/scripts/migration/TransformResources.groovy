package com.saga.sagasuite.scriptgroovy
import com.saga.sagasuite.scriptgroovy.util.SgCms
import com.saga.sagasuite.scriptgroovy.util.SgCnt
import com.saga.sagasuite.scriptgroovy.util.SgLog
import com.saga.sagasuite.scriptgroovy.util.SgProperties
import com.saga.sagasuite.scripts.SgReportManager
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.i18n.CmsEncoder
import org.opencms.json.JSONObject
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.page.CmsXmlPageFactory

public class TransformResources {

	static String AUX_FOLDER = "newTransformResources/"
	static String EQUALS_TARGET_PATTERN = "EQUALS"

	CmsObject cmso;
	String jsonPath;
//	boolean modResources;
	String siteRoot;
	SgLog log;
	def mapPaths;
	def mapUUIDs;
	def errors;
	def warns;
	def infos;

	JSONObject jsonCnf;
	String sourceFolder;
	String targetFolder;
	String sourceType;
	String targetType;
	String targetPattern;
	boolean isSameResource;
	def mappingsLoc;

	public void init(def cmso, def idProceso, def mapFile, def onlyCheck) {
		this.cmso = cmso;
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
						SgCms sgCms = new SgCms(cmso);
						String newResPath = getNewResPath(sgCms, res, sourceFolder, targetFolder, targetPattern)
						sgCms.createResource(newResPath, targetType)
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
										log.add("Copiamos el contenido $localeStr / $element del recurso antiguo al nuevo")
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

						// Relacionamos el antiguo con el nuevo
						addMappingRelation(res.rootPath, newResPath);

						infos.put(res,"Modificado recurso ${res.getRootPath()}")
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
			if (isSameResource) {

				log.init()
						.add("***EQUALS: PROCEDEMOS A COPIAR RECURSOS AUXILIARES EN ANTIGUOS***")
						.print();
				total = resources.size()
				resources.eachWithIndex { res, i ->
					try {
						log.loop(i + 1, total)
								.add("(${i + 1}/$total) -> ${res.getRootPath()}");

						String auxPath = mapPaths.get(res.getRootPath())
						if (!auxPath) {
							log.warn("No ha sido migrado")
						} else {

							// Cambiamos tipo y modificamos contenido
							SgCms sgCms = new SgCms(cmso);
							sgCms.changeType(cmso.getSitePath(res), targetType)
							log.add("Cambiamos el tipo del recurso antiguo ${res.getRootPath()} a $targetType")

							SgCnt oldCnt = new SgCnt(cmso, res)
							SgCnt newCnt = new SgCnt(cmso, auxPath)
							oldCnt.saveStr(newCnt.strContent)

							log.add("Copiamos el contenido del recurso auxiliar $auxPath -> al recurso antiguo ${res.getRootPath()}")
						}

						log.print()

						// TODO eliminar auxiliares
					} catch (Exception e) {
						e.printStackTrace()
						log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
						errors.put(res, "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
					}
				}
			}
			log.print()

			// Report final
			showReport();
		} catch (Exception e) {
			log.print();
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
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

	def addMappingRelation(String oldPath, String newPath){
		try {
			mapPaths.put(oldPath, newPath)
			String oldUuid = cmso.readResource(oldPath).getStructureId().toString()
			String newUuid = cmso.readResource(newPath).getStructureId().toString()
			mapUUIDs.put(newUuid, oldUuid)

			// TODO add all properties cmso.readAllPropertyDefinitions() sgProps.copyProperties(oldPath)
			SgProperties sgProps = new SgProperties(cmso)
			def props = [oldPath: oldPath, newPath: newPath, newUuid: newUuid, oldUuid: oldUuid]
			sgProps.addProperties(newPath, props)
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

//		log.add("MODIFICADOS ($infosTotal)")
//		infos.eachWithIndex{ key, value, idx ->
//			log.add("($idx/$infosTotal - $key - $value)")
//		}
//		mapPaths.each{ key, value ->
//			boolean isSameResource = isSameResource(json.get("targetFolder"), json.get("sourceFolder"), json.get("targetPattern"))
//			if (isSameResource) {
//				log.add("$key -> (Recurso auxiliar)$value")
//			} else {
//				log.add("$key -> $value")
//			}
//		}
//		log.print();
//		log.init().error("ERRORES (${errors.size()})")
//		errors.eachWithIndex{key, value, idx ->
//			log.add("$idx - $key -> $value")
//		}
//		log.print()
	}

	def loadConfig(){
		// Obtenemos el json que relaciona el nombre del fichero antiguo con el id nuevo
		SgCnt jsonCnt = new SgCnt(cmso, jsonPath)
		JSONObject json = new JSONObject(jsonCnt.strContent);
		log.add("json de configuracion ${json.toString()}")

		// Obtenemos los datos del json de configuracion
		sourceFolder = json.getString("sourceFolder");
		targetFolder = json.getString("targetFolder");
		sourceType = json.getString("sourceType");
		targetType = json.getString("targetType");
		targetPattern = json.getString("targetPattern");
		isSameResource = isSameResource(targetFolder, sourceFolder, targetPattern)
		mappingsLoc = [:];
		JSONObject jLocales = json.getJSONObject("mapping")
		jLocales.keys().each {
			def mappings = [:];
			JSONObject jMappings = jLocales.getJSONObject(it)
			jMappings.keys().each {
				mappings.put(it, jMappings.get(it))
			}
			mappingsLoc.put(it, mappings)
		}
		log.add("Obtenemos datos:")
		log.add("sourceFolder: $sourceFolder")
		log.add("targetFolder: $targetFolder")
		log.add("sourceType: $sourceType")
		log.add("targetType: $targetType")
		log.add("targetPattern: $targetPattern")
		log.add("mappings: $mappingsLoc")

		log.print()

		jsonCnf = json;
	}

	def getJSON(JSONObject json, String field){
		Object o = null;
		try {
			o = json.get(field)
		} catch (Exception e){
			log.error("No existe el campo $field en el json de configuracion")
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

	boolean validateOldXmlLocale(def res, def xml, Locale locale){
		if (!xml.hasLocale(locale)) {
			throw new WarnException("El recurso $res no tiene locale ${locale.getLanguage()}")
		}
		return true;
	}
}