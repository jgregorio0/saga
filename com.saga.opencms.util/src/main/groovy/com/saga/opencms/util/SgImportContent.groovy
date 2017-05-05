package com.saga.opencms.util

import com.saga.sagasuite.scripts.SgReportManager
import groovy.json.JsonSlurper
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpStatus
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.LaxRedirectStrategy
import org.apache.http.util.EntityUtils
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsProperty
import org.opencms.file.CmsResource
import org.opencms.file.types.I_CmsResourceType
import org.opencms.lock.CmsLock
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil
import org.opencms.xml.CmsXmlContentDefinition
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.CmsXmlUtils
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.opencms.xml.types.I_CmsXmlContentValue

class SgImportContent {

	def UTF8 = 'UTF-8'
	def EXT_IMAGE = ["jpeg", "jpg", "png", "gif", "tif", "tiff"];
	def TYPE_VFS_FILE = "OpenCmsVfsFile";
	def TYPE_CATEGORY = "OpenCmsCategory";
	def TYPE_IMAGE = "image";
	def TYPE_BINARY = "binary";
	def TYPE_FOLDER = "folder";
	def TYPE_IMAGE_GALLERY = "imagegallery";
	def TYPE_DOWNLOAD_GALLERY = "downloadgallery";
	def TYPE_LINK_GALLERY = "linkgallery";

	CmsObject cmso;
	String idProceso;
	String jsonPath;
	Locale locale;
	String siteRoot;
	SgLog log;

	/** report */
	class ScriptReport {
		def infos = [:];
		def warns = [:];
		def errors = [:];
	}
	ScriptReport report;

	/** Parametros de la importacion*/
	JsonSlurper jsonSlurper;
	List mappingConfig;
	GroovyClassLoader groovyClassLoader;
	I_CmsResourceType resourceType;
	CmsXmlContentDefinition contentDefinition;
	SecuencialNamePattern seqNames;

	public void init(def cmso, def idProceso, def jsonPath) {
		this.report = new ScriptReport();

		this.cmso = cmso;
		this.idProceso = idProceso;
		this.jsonPath = jsonPath;
		this.locale = cmso.getRequestContext().getLocale();

		this.siteRoot = cmso.getRequestContext().getSiteRoot()

		/******* PROCESO ********/
		this.log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());

		// Ejecuta script
		executeImport();

		// Report final
		showReport();
	}

	/**
	 * Ejecuta la importación de recursos
	 *
	 * @param CmsObject cmso
	 * @param configPath Ruta del fichero json con la configuración de la importación
	 */
	def executeImport() {
		try {
			loadConfig();
			if (validateConfig()) {
				importConfigs();
			}
		} catch (Exception e) {
			log.error("execute import", e).print();
			report.errors.put("execute import", e.getMessage());
		}
	}

	/**
	 * Load config file and groovy class loader
	 * @return
	 */
	def loadConfig(){
		// Nombre del usuario que ejecuta la tarea
		jsonSlurper = new JsonSlurper();

		def contents = cmso.readFile(jsonPath).getContents();

		// Obtenemos la configuración del mapeo. En un fichero de configuración se
		// pueden definir distintos mapeos
		mappingConfig = jsonSlurper.parseText(new String(contents, "UTF-8"));
		ClassLoader parentClassLoader = getClass().getClassLoader();
		groovyClassLoader = new GroovyClassLoader(parentClassLoader);
	}

	/**
	 * Validate configuration file
	 * @return
	 */
	boolean validateConfig(){
		if (!(mappingConfig != null && mappingConfig.size() > 0)){
			throw new Exception("$jsonPath must contains array of configurations");
		}
		return true;
	}

	def importConfigs(){
		int total = mappingConfig.size();
		for (int i = 0; i < total; i++) {
			try {
				Map cfg = mappingConfig.get(i);

				log.init().add("${i+1}/$total - Executing import");
				importConfig(cfg);
				log.print();
			} catch (Exception e) {
				log.error("import config $i", e).print();
				report.errors.put("import config $i", e.getMessage());
			}
		}
	}

	def importConfig(Map cfg) {
		// solicitamos los recursos en el origen
		String resText = getResources(cfg);

		// si la respuesta es correcta
		if (validateResText(resText)) {
			Map resources = jsonSlurper.parseText(resText);


			// Mapa con todos los recursos a mapear
			log.add("total resources to import ${resources.size()}");

			// Tipo de recurso a crear
			resourceType = OpenCms.resourceManager.getResourceType(cfg.targetType);
			contentDefinition = getContentDefinition(cmso, resourceType);
			log.add("Resource type ${cfg.targetType}");

			// Creamos las distintas carpetas definidas en la configuración
			createFolder(cmso, cfg.targetFolder, TYPE_FOLDER);
			createFolder(cmso, cfg.imageGallery, TYPE_IMAGE_GALLERY);
			createFolder(cmso, cfg.downloadGallery, TYPE_DOWNLOAD_GALLERY);
			createFolder(cmso, cfg.linkGallery, TYPE_LINK_GALLERY);
			log.add("Created folders ${cfg.targetFolder}, ${cfg.imageGallery}, ${cfg.downloadGallery}, ${cfg.linkGallery}");

			// Instancia de la utilidad para crear los nombres de los nuevos recursos
			seqNames = new SecuencialNamePattern(cfg.namePattern);
//			int index = 1;
//			double total = resources.keySet().size() as Double;
//			double rate = 0d;
//			double count = 0d;

			importResources(cfg, resources);
			log.print();
		}

	}

	/**
	 * Validate response status is OK
	 * @param response
	 * @return
	 */
	private boolean validateResponse(CloseableHttpResponse response) {
		int sCode = response.statusLine.statusCode;
		if (sCode != HttpStatus.SC_OK){
			throw new Exception("Response status code ($sCode) is not OK (200)");
		}
		return true;
	}

	/**
	 * Validate response text
	 * @param resText
	 * @return
	 */
	boolean validateResText(def resText){
		if (StringUtils.isBlank(resText)) {
			throw new Exception("Response must not be blank");
		}
		return true;
	}

	def importResources(def cfg, Map resources){
		log.print();
		int totalRes = resources.size();
		resources.eachWithIndex { sourceResource, resourceValues, i ->
			try {
				log.percentage(i, totalRes).add("IMPORTANDO $sourceResource");
				importResource(cfg, sourceResource, resourceValues, i);
				log.print();
			} catch (Exception e) {
				log.error("importResources $i", e).print();
				report.errors.put("importResources $i", e.getMessage());
			}
		}
	}

	def importResource(def cfg, def sourceResource, def resourceValues, def index){
		List resourceProperties = extractMigrationProperties(sourceResource);
		log.add("Resource $resourceProperties");

		// Creamos el nuevo documento
		final CmsXmlContent content = CmsXmlContentFactory.createDocument(cmso, locale, UTF8, contentDefinition);

		// Iteramos sobre los campos del recurso que vamos a importar
		int totalValues = resourceValues.size();
		resourceValues.eachWithIndex {resourcePath, pathValue, iValues ->
			log.add("${iValues+1}/$totalValues - $resourcePath");

			// Path del recurso origen sin los índices
			String sourceSimplePath = CmsXmlUtils.removeXpath(resourcePath);

			// Obtenemos de la configuración si se ha definido un script para mapear un valor (ruta) en concreto
			String dynamicNodeMapping = cfg.dynamicNodeMappings.get(sourceSimplePath);

			if (dynamicNodeMapping) {
				log.add("Ejecutamos dinamiNodeMapping $dynamicNodeMapping");
				invokeScript(cmso, groovyClassLoader, dynamicNodeMapping, 'mapValue', [cmso, cfg, content, sourceSimplePath, resourceValues] as Object[]);
			} else {

				// obtenemos la configuracion de la ruta origen
				String sourceConfigPath = cfg.fieldMapping.keySet().find {
					sourceSimplePath.equals(getSimplePath(it))
				};

				// obtenemos la configuración de la ruta destino definida para la ruta origen
				String targetConfigPath = sourceConfigPath == null ? null : cfg.fieldMapping.get(sourceConfigPath);

				log.add("$sourceConfigPath >> $targetConfigPath");
				if (targetConfigPath) {
					String targetNaturalPath = getSimplePath(targetConfigPath);

					// Generamos los fragmentos de la ruta destino
					String[] targetPathParts = targetNaturalPath.split("/");

					// Obtenemos los índices de la ruta origen donde se produce repetición
					List sourceRepetitionIndexes = getIterationIndexes(sourceConfigPath);

					// Obtenemos los índices de la ruta destino donde se produce repetición
					List targetRepetitionIndexes = getIterationIndexes(targetConfigPath);

					// Si el patrón de repetición difiere del origen al destino no es posible realizar el mapeo
					if (sourceRepetitionIndexes.size() != targetRepetitionIndexes.size()) {
						throw new Exception('NO COINCIDEN LOS PATRONES DE REPETICIÓN');
					}

					// obtenemos los índices de la ruta origen. Si por ejemplo la ruta es Content[2]/Image[10]/Image[1]
					// la lista obtenida sería [2, 10, 1]
					List currentValueIndexes = resourcePath.split('/').collect { CmsXmlUtils.getXpathIndexInt(it) };

					// Creamos el nodo destino
					I_CmsXmlContentValue targetNode = createTargetNode(cmso, locale, targetPathParts,
							targetRepetitionIndexes, currentValueIndexes, content, null, 0);
					log.add("Create nodo $targetNaturalPath");

					// Asignamos el valor al nodo creado
					setNodeValue(groovyClassLoader, cmso, content, cfg, targetNode, sourceSimplePath, pathValue);
					log.add("Asignamos valor al nodo $targetNaturalPath << '$pathValue'");

					// Asignamos los valores por defecto a nodos
					createDefaultValues(cmso, locale, content, targetNode, cfg);
				}
			}
		}

		// Mapeos estáticos
		createStaticValues(cmso, locale, content, cfg);

		// Aplicamos post procesamiento al contenido creado
		applyResourceProcessor(groovyClassLoader, cmso, locale, content, cfg, sourceResource, resourceValues);

		// Almacenamos el recurso
		String resPath = cfg.targetFolder + seqNames.secuence(index + 1);
		saveResource(cmso, content, resourceType, resPath, resourceProperties);

		log.add("Migrado recurso a $resPath << ${StringEscapeUtils.escapeHtml4(content.toString())}");
		report.infos.put(resPath, "Migrado");
	}

/**
 * Ejecuta los scripts configurados para procesar el contenido una vez creado pero antes de ser almacenado en el VFS
 *
 * @param loader GroovyClassLoader
 * @param CmsObject cmso
 * @param locale Locale
 * @param content Contenido
 * @param cfg Configuración del recurso
 * @param sourceResource Ruta del recurso origen
 * @param resourceValues Valores del recurso origen
 * @return
 */
	def applyResourceProcessor(GroovyClassLoader loader, CmsObject cmso, Locale locale, CmsXmlContent content, Map cfg,
							   String sourceResource, Map resourceValues) {
		def arguments = [cmso, locale, content, cfg, sourceResource, resourceValues];
		cfg.dynamicResourceProcessors.each { it ->
			invokeScript(cmso, loader, it, 'processResource', arguments);
		}

	}

/**
 *
 * Invoca el método de un script groovy
 *
 * @param CmsObject cmso
 * @param loader ClassLoader para cargar el script
 * @param path Ruta del VFS donde se encuentra el script
 * @param method Método que llamar
 * @param args Argumentos del método
 * @return
 */
	def invokeScript(CmsObject cmso, GroovyClassLoader loader, String path, String method, Object[] args) {
		// Obtenmos el contenido del script
		byte[] scriptContent = cmso.readFile(cmso.readResource(path)).contents;
		Class aClass = loader.parseClass(new String(scriptContent, "UTF-8"));
		GroovyObject groovyObject = (GroovyObject) aClass.newInstance();
		groovyObject.invokeMethod(method, args);
	}

/**
 * Asigna el valor de un campo del contenido estructurado
 *
 *
 * @param CmsObject cmso
 * @param cfg Mapa con la configuración de la importación
 * @param node Nodo del contenido estructurado al que asignar el valor
 * @param sourcePath Ruta del nodo correspondiente en el recurso origen
 * @param value Valor que asignar
 * @return
 */
	def setNodeValue(GroovyClassLoader classLoader, CmsObject cmso, CmsXmlContent content, Map cfg, I_CmsXmlContentValue node, String sourcePath, String value) {

		// Obtenemos el script de transformación del valor si lo hubiera
		String transformerScript = cfg.valueTransformers.get(CmsXmlUtils.removeXpath(node.path));

		// Si existe el script lo ejecutamos
		if (transformerScript) {
			invokeScript(cmso, classLoader, transformerScript, 'transformValue', [cmso, cfg, content, node, sourcePath, value] as Object[]);
		} else {
			// si no existe el script ejecutamos la asignación de valor por defecto
			switch (node.typeName) {
			// Si asignamos el valor de tipo fichero descargamos el fichero del origen,
			// lo almacenamos en la galería configurada y asignamo el valor
				case TYPE_VFS_FILE:
					CmsResource resource = createFile(cmso, cfg, value);
					node.setStringValue(cmso, resource?.rootPath);
					break;
			// Si se trata de una categoría, creamos las categoría correspondientes en el destino
			// y asignamos el valor
				case TYPE_CATEGORY:
					def categories = StringUtils.split(value, ';,') as ArrayList;
					createFolder(cmso, cfg.categoryFolder, TYPE_FOLDER);
					def paths = [];
					categories.sort().each {
						String path = (cfg.categoryFolder + '/' + it).replaceAll("/+", "/");
						createFolder(cmso, path, 'folder');
						paths << path;
					}
					node.setStringValue(cmso, paths.join(','));
					break;
			// En cualquier otro caso asignamos el valor de forma directa
				default:
					if (cfg.valueMappings.get(sourcePath)) {
						String mappedValue = cfg.valueMappings.get(sourcePath).get(value);
						node.setStringValue(cmso, StringUtils.defaultString(mappedValue));
					} else {
						node.setStringValue(cmso, value);
					}

			}
		}

	}

/**
 *
 * Guarda el nuevo recurso creado
 *
 * @param CmsObject cmso
 * @param content Contenido del recurso
 * @param name Nombre del nuevo recurso
 * @param properties Propiedades que asignar al nuevo recurso
 * @return
 */
	def saveResource(CmsObject cmso, CmsXmlContent content, I_CmsResourceType resourceType, String name, List properties) {
		if (!cmso.existsResource(name)) {
			CmsResource created = cmso.createResource(name, resourceType, content.marshal(), properties);
			CmsFile createdFile = cmso.readFile(created);
			CmsXmlContent createdContent = CmsXmlContentFactory.unmarshal(cmso, createdFile);
			createdContent.handler.prepareForWrite(cmso, createdContent, createdFile);
			cmso.writeFile(createdFile);
			cmso.unlockResource(created);
		}
	}

/**
 * Obtiene y almacena un fichero asociado al recurso
 *
 * @param CmsObject cmso
 * @param cfg Configuración del mapeo de recursos
 * @param uri URI de la que obtener el fichero en el sitio origen
 * @return
 */
	CmsResource createFile(CmsObject cmso, Map cfg, String uri) {
		// Obtenemos la información del fichero que almacenar
		Map fileInfo = getFileInfo(cfg, uri);

		// Cuando se importan ficheros, se almacena en la propiedad 'migration.uri' la ruta original del fichero.
		// Ya que todas las imágenes van a la misma carpeta durante la importación, y para evitar que ficheros con el
		// mismo nombre en el origen, aunque alojados en carpetas distintas, se traten como si fueran ficheros igulaes,
		// comprobamos que si existe en la galería donde se importan los ficheros un fichero que procede de la misma ruta
		// origen (lo hacemos comprobando la propiedad 'migration.uri')
		CmsResource existent = null;
		try {
			if (fileInfo.resourceType.typeName.equals("image")) {
				List<CmsResource> l = cmso.readResourcesWithProperty(cfg.imageGallery, "migration.uri", uri);
				existent = l.empty ? null : l.first();
			} else {
				List<CmsResource> l = cmso.readResourcesWithProperty(cfg.downloadGallery, "migration.uri", uri);
				existent = l.empty ? null : l.first();
			}

		} catch (e){}

		// Si ya hemos importado el fichero lo devolvemos
		if (existent) {
			return existent;
		}
		// si no existe el fichero lo creamos
		if (!cmso.existsResource(fileInfo.path)) {
			byte[] file = getFile(uri);
			if (file.length > 0) {
				CmsResource res = cmso.createResource(fileInfo.path, fileInfo.resourceType, file, [new CmsProperty("migration.uri", uri, uri)]);
				cmso.unlockResource(res);
				return res;
			} else {
				return null;
			}
			// si existe un fichero con el mismo nombre, pero que procede de una URL distinta, añadimos el tiempo actual en
			// milisegundos para evitar nombres duplicados
		} else {
			byte[] f = getFile(uri);
			if (f.length > 0) {
				String name = FilenameUtils.removeExtension(fileInfo.path) + '_' + System.currentTimeMillis() + '.' + FilenameUtils.getExtension(fileInfo.path).toLowerCase();
				CmsResource res = cmso.createResource(name, fileInfo.resourceType, f, [new CmsProperty("migration.uri", uri, uri)]);
				cmso.unlockResource(res);
				return res;
			} else {
				return null;
			}



		}
	}

/**
 * Genera un mapa con la información del fichero que se va a almacenar
 *
 * @param config Configuración del mapeo de recursos
 * @param uri URI del fichero origen
 * @return Mapa con el tipo de recurso y la ruta y nombre con el que almacenarlo
 */
	Map getFileInfo(Map config, String uri) {
		def info = [:];
		String filePath = new URIBuilder(uri).path;
		String fileName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());

		if (EXT_IMAGE.contains(FilenameUtils.getExtension(fileName).toLowerCase())) {
			info.resourceType = OpenCms.resourceManager.getResourceType(TYPE_IMAGE);
			info.path = CmsStringUtil.addLeadingAndTrailingSlash(config.imageGallery) + fileName;
		} else {
			info.resourceType = OpenCms.resourceManager.getResourceType(TYPE_BINARY);
			info.path = CmsStringUtil.addLeadingAndTrailingSlash(config.downloadGallery) + fileName;
		}
		info;
	}

/**
 * Asigna los valores configurado como estáticos en el recurso destino
 *
 * @param CmsObject cmso
 * @param locale locale
 * @param content Contenido
 * @param cfg Configuración del mapeo
 * @return
 */
	def createStaticValues(CmsObject cmso, Locale locale, CmsXmlContent content, Map cfg) {
		cfg.staticValues.each { k, v ->
			I_CmsXmlContentValue node = createNode(cmso, locale, content, k.split("/"), null, 0);
			node.setStringValue(cmso, v);
		}
	}

/**
 *  Asigna los valores por defecto configurados en el mapeo. El mapeo permite asignar valores por
 *  defecto a nodos compuestos en el recurso destino que no tienen una equivalencia en el origen.
 *  Un ejemplo de ello sería:
 *  - Queremos mapear el nodo origen Content/ImageMain/File al nodo destino Content/Gallery/Image/Image
 *  - El tipo Content/Gallery/Image es un tipo complejo que tiene los campos Image, Position, Width, Height
 *  - Pero en el origen no existen los campos Position, Width y Height, aún así queremos que se asignen valores
 *  a estos campos
 *  - En la configuración del mapeo podemos definir valores por defecto para estos campos en el recurso destino
 *  de la siguiente forma:
 *
 *  {
 *     ...
 *     defaultValues : {
 *         "Content/Gallery/Image/Position" : "top",
 *         "Content/Gallery/Image/Width" : "100%",
 *         "Content/Gallery/Image/Height" : "200",
 *     }
 *     ...
 *  }
 *
 *  De esta forma, cada vez que se cree un nodo Content/Gallery/Image/Image se va a tomar su nodo padre
 *  (Content/Gallery/Image) y se va a comprobar si esta ruta comienza por alguna de las definidads en 'defaultValues',
 *  si es así, se crea el nodo correspondiente y se asigna el valor
 *
 * @param CmsObject cmso
 * @param locale Locale
 * @param content CmsXmlContent del recurso que se está creando
 * @param targetNode Nodo al que se le está asignando el valor
 * @param cfg Configuración del mapeo
 * @return
 */
	def createDefaultValues(CmsObject cmso, Locale locale, CmsXmlContent content, I_CmsXmlContentValue targetNode, Map cfg) {
		if (CmsXmlUtils.isDeepXpath(targetNode.path)) {
			final String parentPath = CmsXmlUtils.removeLastXpathElement(CmsXmlUtils.removeXpath(targetNode.path));
			Set defVals = cfg.defaultValues.keySet().findAll {
				parentPath.startsWith(CmsXmlUtils.removeLastXpathElement(it));
			}

			defVals.each {
				try {
					String defaultValue = cfg.defaultValues.get(it);
					def pathLength = CmsXmlUtils.removeLastXpathElement(it).split("/").length;
					def lastPath = CmsXmlUtils.getLastXpathElement(it);
					def nodePathFragments = CmsXmlUtils.removeLastXpathElement(targetNode.path).split("/") as List;
					def defValuePath = nodePathFragments.subList(0, pathLength).join("/") + "/" + lastPath;

					if (content.hasValue(defValuePath, locale, 0)) {
						if (StringUtils.isEmpty(content.getValue(defValuePath, locale, 0).getStringValue(cmso))) {
							def value = content.getValue(defValuePath, locale, 0);
							value.setStringValue(cmso, defaultValue);
						}
					} else {
						def value = content.addValue(cmso, defValuePath, locale, 0);
						value.setStringValue(cmso, defaultValue);
					}

				} catch (e) {

				}
			}
		}
	}

/**
 *
 * Genera el nodo correspondiente a la configuración del mapeo en el recurso destino
 *
 * @param CmsObject cmso
 * @param locale Locale
 * @param targetPaths Ruta destino dividida en sus distintas partes como un array
 * @param targetRepetitionIndexes Posiciones en la ruta destino donde se repiten nodos
 * @param currentValueIndexes Índices del valor actual
 * @param content Contenido que creamos
 * @param parentNode Nodo padre del nodo actual que se procesa
 * @param index Índice del nodo que debemos crear
 * @return El nuevo nodo creado
 * @throws Exception
 */
	I_CmsXmlContentValue createTargetNode(CmsObject cmso, Locale locale, String[] targetPaths,
										  List targetRepetitionIndexes, List currentValueIndexes,
										  CmsXmlContent content, String parentNode, int index) throws Exception {
		I_CmsXmlContentValue contentValue;

		int currentNodeIndex = getCurrentNodeIndex(targetRepetitionIndexes, currentValueIndexes, index);
		final String currentNode = parentNode != null ? parentNode + "/" + targetPaths[index] : targetPaths[index];

		if (!content.hasValue(currentNode, locale, currentNodeIndex)) {
			/* Esto es necesario porque se pueden dar situaciones en las que se reciba un
            * path con un índice y no se hayan creado los índices previos (vamos a crear el path
            * Content[1]/Image[10]/Image pero no están creados Content[1]/Image[9]/Image, ....). De esta forma
            * aseguramos que todos los índices previos al que se reciben se crean antes */
			for (int i = 0; i <= currentNodeIndex ; i++) {
				if (!content.hasValue(currentNode, locale, i)) {
					content.addValue(cmso, currentNode, locale, i);
				}
			}
			contentValue = content.getValue(currentNode, locale, currentNodeIndex);
		} else {
			contentValue = content.getValue(currentNode, locale, currentNodeIndex);
		}
		// si hemos llegado al nodo final asignamos el valor
		if (index != targetPaths.length - 1) {
			String newParentNode = currentNode + "[" + (currentNodeIndex + 1) + "]";
			contentValue = createTargetNode(cmso, locale, targetPaths, targetRepetitionIndexes, currentValueIndexes, content, newParentNode, index + 1);
		}
		contentValue;
	}

/**
 * Permite generar un nuevo nodo. Esta función es genérica para crear un nodo dada su ruta y no tiene en cuenta
 * configuraciones de mapeo. Es una función recursiva
 *
 * @param CmsObject cmso
 * @param locale Locale
 * @param content Contenido donde creamos el nodo
 * @param targetPaths Ruta del nodo que hay que crear dividida en sus distintas partes como una array
 * @param parentNode Nodo padre del nodo que se va a crear.
 * @param index Índice del nodo que se va a crear
 * @return Nuevo nodo creado
 * @throws Exception
 */
	I_CmsXmlContentValue createNode(CmsObject cmso, Locale locale, CmsXmlContent content, String[] targetPaths, String parentNode, int index) throws Exception {
		I_CmsXmlContentValue contentValue;
		String currentNode = parentNode != null ? parentNode + "/" + targetPaths[index] : targetPaths[index];
		if (!content.hasValue(currentNode, locale, 0)) {
			contentValue = content.addValue(cmso, currentNode, locale, 0);
		} else {
			contentValue = content.getValue(currentNode, locale, 0);
		}
		if (index != targetPaths.length - 1) {
			String newParentNode = currentNode + "[1]";
			contentValue = createNode(cmso, locale, content, targetPaths, newParentNode, index + 1);
		}
		contentValue;
	}

/**
 * Dados los índices en la ruta destino donde se deben repetir valor, los índices del valor original
 * actual y el índice del nodo origen actual a crear obtenemos el índice del nodo destino que estamos
 * creando
 *
 *
 * @param targetRepetitionIndexes
 * @param currentValueIndexes
 * @param i
 * @return
 */
	int getCurrentNodeIndex(List targetRepetitionIndexes, List currentValueIndexes, int i) {
		if (targetRepetitionIndexes.contains(i)) {
			currentValueIndexes.get(targetRepetitionIndexes.indexOf(i)) - 1;
		} else {
			0;
		}
	}

/**
 * Obtien la lista con los índices de iteración según la configuración del mapeo. Ejemplo:
 *
 * Si en la configuración hemos definido una ruta de mapeo tal que así: Content* /ImageMain /Image*
 * Este método devolverá la lista [0, 2], indicando que en esta ruta se repiten valores en la posición 0
 * y 2 de la ruta. Cada posición (fragmento) de la ruta viene demarcada por el carácter '/'
 *
 * @param path
 * @return
 */
	List getIterationIndexes(String path) {
		def splitter = path.split('/') as ArrayList;
		splitter.collect({if (it.endsWith('*')) splitter.indexOf(it)}).inject([], {r, v -> if (v != null) r << v; r});
	}

/**
 * Elimina de una ruta definida en el mapeo el carácter *
 *
 * @param path
 * @return
 */
	String getSimplePath(String path) {
		path.replaceAll("\\*", "");
	}

/**
 * Extrae de la clave del mapa con los recursos importados el uuid y la ruta del recurso en el sitio
 * origen para ser asignados como propiedades al recurso destino que se genera
 *
 * @param res Clave del recurso del que extraer las propiedades
 * @return
 */
	def extractMigrationProperties(String res) {
		String uuid = res.substring(0, res.indexOf('@'));
		String path = res.substring(res.indexOf('@') + 1, res.length());
		[new CmsProperty("migration.path", path, path), new CmsProperty("migration.uuid", uuid, uuid)];
	}

/**
 * Obtiene la definición del contenido destino al que se va a mapear
 *
 * @param CmsObject cmso
 * @param contentType Tipo de contenido
 * @return
 */
	CmsXmlContentDefinition getContentDefinition(CmsObject cmso, I_CmsResourceType resourceType) {
		def schemaUri = "opencms:/${resourceType.configuration.schema}";
		CmsXmlContentDefinition.unmarshal(schemaUri, new CmsXmlEntityResolver(cmso));
	}

/**
 * Genera una carpeta
 *
 * @param CmsObject cmso
 * @param path Ruta de la carpeta
 * @param type Tipo de la carpeta
 * @return
 */
	def createFolder(CmsObject cmso, String path, String type) {
		if (path) {
			if (!cmso.existsResource(path)) {
				cmso.createResource(path, OpenCms.resourceManager.getResourceType(type));
				cmso.unlockResource(path);
			} else {
				tryUnlockInherited(cmso, path);
			}
		}
	}

/**
 * Trata de desbloquear un recurso. Si el bloque es heredado, navega por las carpetas padres
 * desbloqueándolas todas
 *
 * @param cmso
 * @param resource
 * @return
 */
	def tryUnlockInherited(CmsObject cmso, String resource) {
		CmsResource res = cmso.readResource(resource);
		CmsLock lock = cmso.getLock(res);
		if (lock.inherited) {
			tryUnlockInherited(cmso, cmso.readParentFolder(res.structureId).rootPath);
		} else if (!lock.unlocked) {
			if (!lock.isOwnedBy(cmso.requestContext.currentUser)) {
				cmso.changeLock(res);
			}
			cmso.unlockResource(res);
		}

	}

	/**
	 * Reliza la petición HTTP y obtiene los recursos del origen
	 * @param config
	 * @return
	 */
	String getResources(Map config) {
		String resText = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClientBuilder.create().build()
			URIBuilder uriBuilder = new URIBuilder(config.exportUri).addParameter("folder", config.sourceFolder)
					.addParameter("type", config.sourceType)
			if (StringUtils.isNotEmpty(config.maxResults)) {
				uriBuilder.addParameter("max", config.maxResults.toString());
			}
			if (StringUtils.isNotEmpty(config.since)) {
				uriBuilder.addParameter("since", config.since.toString());
			}
			URI uri = uriBuilder.build();
			final HttpGet httpGet = new HttpGet(uri);
			log.add("GET resources from ${uri.toString()}");

			CloseableHttpResponse response = httpClient.execute(httpGet);

			if (validateResponse(response)) {
				resText = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			log.error("GET resources ${config.exportUri}", e).print();
			report.errors.put("GET resources ${config.exportUri}", e.getMessage());
		} finally{
			try {
				httpClient.close();
			} catch (Exception e) {
				log.error("GET Respnse ${config.exportUri} close client", e).print();
				report.errors.put("GET Respnse ${config.exportUri} close client", e.getMessage());
			}
		}
		return resText;
	}

/**
 * Clase de utilidad para crear los nombres de los recursos nuevos que se importan
 */
	class SecuencialNamePattern {
		// Patrón del nombre
		String strPattern;
		// Pad en el patrón de la secuencia númerica
		int pad;

		public SecuencialNamePattern(String namePattern) {
			pad = namePattern.substring(namePattern.indexOf('{') + 1, namePattern.indexOf('}')).toInteger();
			strPattern = namePattern.substring(0, namePattern.indexOf('{')) + '%s' + namePattern.substring(namePattern.indexOf('}') + 1, namePattern.length())
		}
		// Genera el nombre del fichero según el número de la secuencia
		def secuence(Integer seq) {
			String.format(strPattern, StringUtils.leftPad(seq.toString(), pad, '0'));
		}
	}

/**
 * Descarga un fichero según su URL
 *
 * @param url URL del fichero
 * @return array de bytes con el contenido
 */
	byte[] getFile(String url) {
		CloseableHttpClient httpclient = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build();
		CloseableHttpResponse response = httpclient.execute(new HttpGet(new URIBuilder(url).build()))
		byte[] f = EntityUtils.toByteArray(response.getEntity());
		httpclient.close();
		return f;

	}

	/**
	 * Print report
	 * @return
	 */
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