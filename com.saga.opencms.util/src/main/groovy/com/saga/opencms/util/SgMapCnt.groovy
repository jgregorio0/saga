package com.saga.sagasuite.scriptgroovy.migration
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.saga.sagasuite.scriptgroovy.util.SgCms
import com.saga.sagasuite.scriptgroovy.util.SgLog
import com.saga.sagasuite.scripts.report.SgReportManager
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.collections.Predicate
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
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
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil
import org.opencms.xml.CmsXmlContentDefinition
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.CmsXmlUtils
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.opencms.xml.types.I_CmsXmlContentValue

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class SgMapCnt {

	private static Log LOG = CmsLog.getLog(SgMapCnt.class);

	def EXT_IMAGE = ["jpeg", "jpg", "png", "gif", "tif", "tiff"];
	def TYPE_VFS_FILE = "OpenCmsVfsFile";
	def TYPE_VAR_LINK = "OpenCmsVarLink";
	def TYPE_CATEGORY = "OpenCmsCategory";
	def TYPE_IMAGE = "image";
	def TYPE_BINARY = "binary";
	def TYPE_FOLDER = "folder";
	def TYPE_IMAGE_GALLERY = "imagegallery";
	def TYPE_DOWNLOAD_GALLERY = "downloadgallery";

	CmsObject cmso;
	String idProceso;
	String jsonPath;
	// boolean modResources;
	String siteRoot;
	SgLog log;

	/** report */
	class ScriptReport {
		def infos = [:];
		def warns = [:];
		def errors = [:];
	}
	ScriptReport report;

	/** import attributes */
	private SgCms sgCms;
	private CmsObject cmsObject;
	private ObjectMapper mapper;
	private List<MappingConfig> mappingConfigs;
	private MappingConfig mappingConfig;
	private HttpEntity entity;
	private CloseableHttpClient httpClient;
	private CloseableHttpResponse execute;
	private URIBuilder uriBuilder;
	private Locale locale;
	private CmsXmlContentDefinition contentDefinition;
	private I_CmsResourceType resourceType;

	private String oriUuid;
	private String oriPath;

	public void init(def cmso, def idProceso, def jsonPath, def onlyCheck) {
		this.report = new ScriptReport();

		this.cmso = cmso;
		this.idProceso = idProceso;
		this.jsonPath = jsonPath;
//    this.modResources = !onlyCheck;

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
			loadConfig();
			if (validateConfigList()) {
				executeImports();
			}
		} catch (Exception e) {
			log.error(e).print();
			report.errors.put("ImportContent >> execute", "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}");
		}
	}

	def executeImports(){
		int totalImports = mappingConfigs.size()
		for (int i = 0; i < totalImports; i++) {
			log.init().add("Importing ${i+1}/$totalImports")
			try {
				MappingConfig mappingConfig = mappingConfigs.get(i);
				if (validateConfig(mappingConfig)) {
					executeImport(mappingConfig);
				}
			} catch (Exception e) {
				log.error(e).print();
				report.errors.put("executeImport >> $i", "ERROR: ${e.getMessage()} -- Cause: ${e.getCause()}")
			}
		}
	}

	def loadConfig() {
		log.init()

		sgCms = new SgCms(cmso);

		// load mapping config
		final String configuration = jsonPath;
		if (StringUtils.isBlank(configuration)) {
			throw new Exception("Path to json configuration file must not be blank");
		}


		cmsObject = cmso;
		CmsResource resource = cmsObject.readResource(configuration);
		CmsFile cmsFile = cmsObject.readFile(resource);
		String s = new String(cmsFile.getContents(), "UTF-8");
		mapper = new ObjectMapper();
		mappingConfigs = mapper.readValue(s, new TypeReference<List<MappingConfig>>(){});

		log.add("mappings: " + mappingConfigs).print();
	}

	boolean validateConfigList(){
		if (!(mappingConfigs != null && mappingConfigs.size() > 0)){
			throw new Exception("Mapping configs is empty");
		}
		return true;
	}

	boolean validateConfig(def mappingConfig){
		if (StringUtils.isBlank(mappingConfig.getSourceFolder())) {
			throw new Exception("sourceFolder must not be blank")
		}
		if (StringUtils.isBlank(mappingConfig.getSourceType())) {
			throw new Exception("sourceType must not be blank")
		}
		return true;
	}

	def executeImport(def mappingConfig){
		this.mappingConfig = mappingConfig;

		//load source uri
		String uri = loadSourceUri();

		// Read values
		Map<String, Map<String, String>> sourceValues = readSourceValues(uri);
		if (validateSourceValues(sourceValues)) {

			// Creamos la carpeta destino o nos aseguramos que no está bloqueda
			ensureTargetFolder();

			// Cargamos configuracion para crear cada recurso
			loadResContentConfig();

			// Creamos los recursos
			createResources(sourceValues);
		}

		// Close http
		closeHttp();
	}

	def createResources(Map<String, Map<String, String>>  sourceValues){
		int index = 1;
		int total = sourceValues.keySet().size()
		for (String res : sourceValues.keySet()) {
			createResource(sourceValues, res, index, total);
			index++;
		}
	}

	def createResource(def sourceValues, String res, def index, def total){
		try {
			// Id for resource
			oriUuid = res.substring(0, res.indexOf('@'));
			oriPath = res.substring(res.indexOf('@') + 1, res.length());

			log.init().add("$index/$total - for resouce $oriPath");

			// Properties
			final List<CmsProperty> fileProperties = new ArrayList<CmsProperty>();
			fileProperties.add(new CmsProperty("migration.path", oriPath, oriPath));
			fileProperties.add(new CmsProperty("migration.uuid", oriUuid, oriUuid));
			//TODO add navigation props

			// Resource xml path values
			final Map<String, String> values = sourceValues.get(res);

			// Sort xml path
			List<String> keys = new ArrayList<String>(values.keySet());
			Collections.sort(keys);

			// Create content document
			CmsXmlContent content = CmsXmlContentFactory.createDocument(
					cmsObject, locale, "UTF-8", contentDefinition);

			// Iteramos sobre cada campo de un recurso
			createFields(keys, values, content);

			// Mapeos de campos estáticos
			mapStaticFields(content);

			// Load file name
//			final int padSize = loadPadSize();
//			final String pattern = loadPattern();
//			final String fName = String.format(pattern, StringUtils.leftPad("" + index, padSize, '0'));
			final String resPath = sgCms.generateNextResoucePath(
					mappingConfig.getTargetFolder(), mappingConfig.getNamePattern());

			// Add content
			final byte[] marshal = content.marshal();
//			String resPath = mappingConfig.getTargetFolder() + fName;
			CmsResource createdResource = cmsObject.createResource(resPath, resourceType, marshal, fileProperties);
			CmsFile createdFile = cmsObject.readFile(createdResource);
			CmsXmlContent cmsXmlContent = CmsXmlContentFactory.unmarshal(cmsObject, createdFile);
			cmsXmlContent.getHandler().prepareForWrite(cmsObject, cmsXmlContent, createdFile);
			cmsObject.unlockResource(createdResource);

			log.add("created resource $resPath").print();
			report.infos.put("$oriPath >> $resPath", "migrado");
		} catch (Exception e) {
			log.error("createResource $res", e).print();
			report.errors.put("createResource $res", e.getMessage());
		}

	}

	def loadPattern(){
		String namePattern = mappingConfig.getNamePattern();
		String begin = namePattern.substring(0, namePattern.indexOf('#'));
		String end = namePattern.substring(namePattern.lastIndexOf('#') + 1, namePattern.length());
		return begin + "%s" + end;
	}

	def loadPadSize(){
		return StringUtils.countMatches(mappingConfig.getNamePattern(), "#");
	}

	def mapStaticFields(def content){
		if (!mappingConfig.getStaticValues().isEmpty()) {
			for (String key : mappingConfig.getStaticValues().keySet()) {
				final I_CmsXmlContentValue node = createNode(cmsObject, locale, content, key.split("/"), null, 0);
				String value = mappingConfig.getStaticValues().get(key)
				addNodeValue(node, value);
				log.add("mapped static field $oriPath >> $key with value $value")
			}
		}
	}

	def createFields(List<String> keys, Map<String, String> values, CmsXmlContent content){
		for (String val : keys) {
			createField(values, val, content);
		}
	}

	def createField(Map<String, String>  values, String val, CmsXmlContent content){
		try {
			// load node value
			final String nodeValue = values.get(val);

			// load source config path
			final String naturalPath = getNaturalPath(val);
			final String sourceConfigPath = loadSourceConfigPath(naturalPath);

			// load target config path
			final String targetConfigPath = mappingConfig.getFieldMapping().get(sourceConfigPath);

			// validate target config
			if (validateTargetConfigPath(targetConfigPath)) {

				// load Path
				final String targetNaturalPath = getNaturalPath(targetConfigPath);
				final String[] targetPathParts= targetNaturalPath.split("/");

				// load source indexes
				List<Integer> sourceRepetitionIndexes = loadRepetitionIndexes(sourceConfigPath);

				// load target indexes
				List<Integer> targetRepetitionIndexes = loadRepetitionIndexes(targetConfigPath);

				// validate
				if (validateRepetitionIndexes(sourceRepetitionIndexes, targetRepetitionIndexes)){

					// load current value indexes
					List<Integer> currentValueIndexes = loadCurrValueIdxs(val, sourceRepetitionIndexes);

					// create target node
					final I_CmsXmlContentValue targetNode =
							createTargetNode(targetPathParts, targetRepetitionIndexes,
									currentValueIndexes, content, null, 0);

					// create default node values
					List<String> defaultValueKeys = createDefaultValueKeys(targetNaturalPath);

					// Add default keys
					addDefaultValues(defaultValueKeys, targetNode, content);

					// Add node value
					addNodeValueType(targetNode, nodeValue, naturalPath);

					log.add("created field >> $oriPath >> $val");
				}
			}
		} catch (Exception e) {
			log.error("createField >> $oriPath >> $val", e).print();
			report.errors.put("createField >> $oriPath >> $val", e.getMessage());
		}
	}

	def addNodeValueType(I_CmsXmlContentValue targetNode, String nodeValue, String naturalPath){
		if (isFile(targetNode)) {

			// Si es un fichero
			final String vfsFileName = nodeValue.substring(nodeValue.lastIndexOf('/') + 1, nodeValue.length());
			if (isImage(nodeValue)) {

				// Si es una imagen
				addImgValue(targetNode, nodeValue, vfsFileName);
			} else {

				// Si es un documento
				addBinValue(targetNode, nodeValue, vfsFileName);
			}
		}
		else if (isLink(targetNode)) {

			// Si es un link
			addLinkValue(targetNode, nodeValue);
		}
		else if (isCategory(targetNode)) {

			// Si es una categoria
			addCategoriesValue(targetNode, nodeValue);
		}
		else {
			// Valores mapeados
			if (isMappingConfigValue(naturalPath)) {

				// Si ha sido declarado en el fichero de configuracion
				addMappedValue(targetNode, nodeValue, naturalPath);
			} else {
				addNodeValue(targetNode, nodeValue);
			}
		}
	}

	def addNodeValue(I_CmsXmlContentValue targetNode, String nodeValue){
		targetNode.setStringValue(cmsObject, nodeValue);
	}

	def addMappedValue(I_CmsXmlContentValue targetNode, String nodeValue, String naturalPath){
		final String mappedValue = mappingConfig.getValueMappings().get(naturalPath).get(nodeValue);
		addNodeValue(targetNode, StringUtils.defaultString(mappedValue));
	}

	def addLinkValue(I_CmsXmlContentValue targetNode, String nodeValue){
		addNodeValue(targetNode, nodeValue);
	}

	def addImgValue(I_CmsXmlContentValue targetNode, String nodeValue, String vfsFileName){
		CmsResource imageFolder = ensureImageFolder();
		final String imageRootPath = imageFolder.getRootPath() + vfsFileName;
		ensureImage(imageRootPath, nodeValue);
		if (!cmsObject.existsResource(imageRootPath)) {
			throw new Exception("image $imageRootPath do not exists");
		}
		addNodeValue(targetNode, imageRootPath);
	}

	def ensureImage(def imageRootPath, def nodeValue) {
		if (!cmsObject.existsResource(imageRootPath)) {
			final byte[] file = getFileOnline(nodeValue);
			if (file.length > 0) {
				final I_CmsResourceType imageType = OpenCms.getResourceManager().getResourceType(TYPE_IMAGE);
				final CmsResource resource1 = cmsObject.createResource(imageRootPath, imageType, file, null);
				if (cmsObject.getLock(resource1).isOwnedBy(cmsObject.getRequestContext().getCurrentUser())) {
					cmsObject.unlockResource(resource1);
				}
			}
		}
	}

	def ensureDownloadGallery(){
		CmsResource downloadFolder;
		final boolean existsResource = cmsObject.existsResource(mappingConfig.getDownloadGallery());
		if (!existsResource) {
			final I_CmsResourceType downloadgallery = OpenCms.getResourceManager().getResourceType(TYPE_DOWNLOAD_GALLERY);
			downloadFolder = cmsObject.createResource(mappingConfig.getImageGallery(), downloadgallery);
		} else {
			downloadFolder = cmsObject.readFolder(mappingConfig.getImageGallery());
		}
		return downloadFolder;
	}

	def ensureBinary(def downloadRootPath, def nodeValue){
		if (!cmsObject.existsResource(downloadRootPath)) {
			final byte[] file = getFileOnline(nodeValue);
			if (file.length > 0) {
				final I_CmsResourceType binaryType = OpenCms.getResourceManager().getResourceType(TYPE_BINARY);
				final CmsResource resource1 = cmsObject.createResource(downloadRootPath, binaryType, file, null);
				if (cmsObject.getLock(resource1).isOwnedBy(cmsObject.getRequestContext().getCurrentUser())) {
					cmsObject.unlockResource(resource1);
				}
			}
		}
	}

	def ensureCategoriesFolder(String categoriesFolder){
		final I_CmsResourceType folderType = OpenCms.getResourceManager().getResourceType(TYPE_FOLDER);

		if (!cmsObject.existsResource(categoriesFolder)) {
			final CmsResource categoryFolderResource = cmsObject.createResource(categoriesFolder, folderType);
			cmsObject.unlockResource(categoryFolderResource);
		}
	}

	def ensureCategory(String categoryPath) {
		final I_CmsResourceType folderType = OpenCms.getResourceManager().getResourceType(TYPE_FOLDER);
		if (!cmsObject.existsResource(categoryPath)) {
			cmsObject.createResource(categoryPath, folderType);
			cmsObject.unlockResource(categoryPath);
		}
	}

	List<String> ensureCategories(String categoriesFolder, List<String> categories){
		List<String> categoryPaths = new ArrayList<String>(categories.size());
		for (String category : categories) {
			if (category.endsWith("/")) {
				category = category.substring(0, category.length() - 1);
			}
			final String categoryPath = categoriesFolder + "/" + category;
			ensureCategory(categoryPath)
			categoryPaths.add(categoryPath);
		}
		return categoryPaths;
	}

	def addCategoriesValue(I_CmsXmlContentValue targetNode, String nodeValue){
		final String siteRoot = OpenCms.getSiteManager().getSiteForRootPath(mappingConfig.getTargetFolder()).getSiteRoot();
		final String categoriesFolder = siteRoot + "/.categories";
		ensureCategoriesFolder(categoriesFolder);

		final String[] catSplit = nodeValue.split(";");
		final List<String> categories = Arrays.<String>asList(catSplit);
		Collections.sort(categories);
		List<String> categoryPaths = ensureCategories(categoriesFolder, categories);

		addNodeValue(targetNode, StringUtils.join(categoryPaths, ","));
	}

	def addBinValue(I_CmsXmlContentValue targetNode, String nodeValue, String vfsFileName){
		CmsResource downloadFolder = ensureDownloadGallery();

		final String downloadRootPath = downloadFolder.getRootPath() + vfsFileName;
		ensureBinary(downloadRootPath, nodeValue);
		if (!cmsObject.existsResource(downloadRootPath)) {
			throw new Exception("binary $downloadRootPath do not exists");
		}
		addNodeValue(targetNode, downloadRootPath);
	}

	def ensureImageFolder(){
		CmsResource imageFolder;
		final boolean existsResource = cmsObject.existsResource(mappingConfig.getImageGallery());
		if (!existsResource) {
			final I_CmsResourceType imagegallery = OpenCms.getResourceManager().getResourceType(TYPE_IMAGE_GALLERY);
			imageFolder = cmsObject.createResource(mappingConfig.getImageGallery(), imagegallery);
		} else {
			imageFolder = cmsObject.readFolder(mappingConfig.getImageGallery());
		}
		if (cmsObject.getLock(imageFolder).isOwnedBy(cmsObject.getRequestContext().getCurrentUser())) {
			cmsObject.unlockResource(imageFolder);
		}

		return imageFolder;
	}

	def isMappingConfigValue(String naturalPath){
		return mappingConfig.getValueMappings().containsKey(naturalPath);
	}

	def isLink(def targetNode){
		return targetNode.getTypeName().equals(TYPE_VAR_LINK)
	}

	def isImage(def nodeValue){
		return FilenameUtils.isExtension(nodeValue, EXT_IMAGE);
	}

	def isFile(def targetNode){
		return targetNode.getTypeName().equals(TYPE_VFS_FILE);
	}

	def isCategory(I_CmsXmlContentValue targetNode){
		return targetNode.getTypeName().equals(TYPE_CATEGORY);
	}

	def addDefaultValues(def defaultValueKeys, def targetNode, def content){
		if (validateDefaultKeys(defaultValueKeys)) {

			// Add value to each default key
			for (String defaultValueKey : defaultValueKeys) {
				addDefaultValue(defaultValueKey, targetNode, content);
			}
		}
	}

	def addDefaultValue(def defaultValueKey, def targetNode, def content){
		// Add value
		try {
			final String rootPath = CmsXmlUtils.removeLastXpathElement(defaultValueKey);
			final int pathsSize = rootPath.split("/").length;

			final String defaultValue = mappingConfig.getDefaultValues().get(defaultValueKey);
			final String parentPath = StringUtils.join(CmsStringUtil.splitAsList(targetNode.getPath(), "/").subList(0, pathsSize), '/');

			final String childPath = CmsXmlUtils.getLastXpathElement(defaultValueKey);
			if (content.hasValue(parentPath + "/" + childPath, locale, 0)) {
				content.getValue(parentPath + "/" + childPath, locale, 0).setStringValue(cmsObject, defaultValue);
			} else {
				content.addValue(cmsObject, parentPath + "/" + childPath, locale, 0).setStringValue(cmsObject, defaultValue);
			}

		} catch (Exception e) {
			log.error("addDefaultValue >> $oriPath >> $defaultValueKey", e).print();
			report.errors.put("addDefaultValue >> $oriPath >> $defaultValueKey", "adding value to $defaultValueKey")
		}
	}

	def validateDefaultKeys(def defaultValueKeys){
		return !defaultValueKeys.isEmpty()
	}

	def createDefaultValueKeys(def targetNaturalPath){
		return (List<String> ) CollectionUtils.select(mappingConfig.getDefaultValues().keySet(), new Predicate() {
			@Override
			public boolean evaluate(Object o) {
				final String key = (String) o;
				String s1 = "";
				String s2 = "";
				if (targetNaturalPath.lastIndexOf('/') != -1) {
					s1 = targetNaturalPath.substring(0, targetNaturalPath.lastIndexOf('/'));
				} else {
					s1 = targetNaturalPath;
				}

				if (key.lastIndexOf('/') != -1) {
					s2 = key.substring(0, key.lastIndexOf('/'));
				} else {
					s2 = key;
				}

				return s1.startsWith(s2);
			}
		});
	}

	def loadCurrValueIdxs(def val, def sourceRepetitionIndexes){
		List<Integer> currentValueIndexes = new ArrayList<Integer>();

		// load value path parts
		final String[] currentValueParts = val.split("/");
		for (int i = 0; i < currentValueParts.length; i++) {
			if (sourceRepetitionIndexes.contains(i)) {
				currentValueIndexes.add(getIndex(currentValueParts[i]));
			}
		}

		return currentValueIndexes;
	}

	def loadSourceConfigPath(def naturalPath){
		final Set<String> sourceKeys = mappingConfig.getFieldMapping().keySet();
		return (String) CollectionUtils.find(sourceKeys, new Predicate() {
			@Override
			public boolean evaluate(Object o) {
				final String k = (String) o;
				return getNaturalPath(k).equals(naturalPath);
			}
		});
	}

	def validateTargetConfigPath(def targetConfigPath){
		if (StringUtils.isBlank(targetConfigPath)) {
			throw new Exception("Target config path must not be blank");
		}
		return true;
	}

	def validateRepetitionIndexes(def sourceRepetitionIndexes, def targetRepetitionIndexes){
		if (sourceRepetitionIndexes.size() != targetRepetitionIndexes.size()) {
			throw new Exception("REPETITION PATTERN DO NOT MATCH");
		}
		return true;
	}

	def loadRepetitionIndexes(def configPath){
		// load target indexes
		final String[] configPathSplitter = configPath.split("/");
		List<Integer> repetitionIndexes = new ArrayList<Integer>();
		for (int i = 0; i < configPathSplitter.length; i++) {
			if (configPathSplitter[i].endsWith("*")) {
				repetitionIndexes.add(i);
			}
		}
		return repetitionIndexes;
	}

	def loadResContentConfig(){
		locale = cmsObject.getRequestContext().getLocale();
		resourceType = OpenCms.getResourceManager().getResourceType(mappingConfig.getTargetType());
		String schema = resourceType.getConfiguration().get("schema");
		String schemaUri = "opencms:/" + schema;
		contentDefinition = CmsXmlContentDefinition.unmarshal(schemaUri, new CmsXmlEntityResolver(cmsObject));
	}

	def closeHttp(){
		try {
			EntityUtils.consume(entity);
		} catch (Exception e) {
			log.error(e).print();
			report.errors.put("ImportContent >> closeHttp", "closing entity")
		} finally {
			execute.close();
		}
	}

	def ensureTargetFolder() {
		String folderPath = mappingConfig.getTargetFolder();
		sgCms.ensureResource(folderPath, TYPE_FOLDER);
		log.add("creada carpeta $folderPath").print();
		/*final I_CmsResourceType folderType = OpenCms.getResourceManager().getResourceType(TYPE_FOLDER);
		if (!cmsObject.existsResource(mappingConfig.getTargetFolder())) {
			final CmsResource categoryFolderResource = cmsObject.createResource(mappingConfig.getTargetFolder(), folderType);
			cmsObject.unlockResource(categoryFolderResource);
		} else {

			final CmsLock lock = cmsObject.getLock(mappingConfig.getTargetFolder());
			if (lock.isInherited()) {
				throw new Exception("Tartget folder ${mappingConfig.getTargetFolder()} is blocked by heritance" );
			}
			if (!lock.isUnlocked()) {
				if (!lock.isOwnedBy(cmsObject.getRequestContext().getCurrentUser())) {
					cmsObject.changeLock(mappingConfig.getTargetFolder());
				}
				cmsObject.unlockResource(mappingConfig.getTargetFolder());
			}
		}*/
	}

	String loadSourceUri(){
		httpClient = HttpClientBuilder.create().build();
		uriBuilder = new URIBuilder(mappingConfig.getExportUri())
				.addParameter("folder", mappingConfig.getSourceFolder())
				.addParameter("type", mappingConfig.getSourceType());
		if (mappingConfig.getMaxResults() != null) {
			uriBuilder.addParameter("max", mappingConfig.getMaxResults().toString());
		}
		if (mappingConfig.getSince() != null) {
			uriBuilder.addParameter("since", mappingConfig.getSince().toString());
		}

		String sourceUri = uriBuilder.build();
		log.add("source uri: " + sourceUri).print();
		return sourceUri;
	}

	Map<String, Map<String, String>> readSourceValues(String sourceUri){
		def sourceValues = null;
		final HttpGet httpGet = new HttpGet(sourceUri);
		execute = httpClient.execute(httpGet);
		if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			entity = execute.getEntity();
			final String result = EntityUtils.toString(entity, "UTF-8");
			sourceValues = mapper.readValue(result, Map.class);
		}
		log.add("load sourceValues: $sourceValues");
		return sourceValues;
	}

	def validateSourceValues(def sourceValues){
		if (sourceValues == null) {
			throw new Exception("empty source values")
		}
		return true;
	}

	static class MappingConfig {

		Map<String, String> fieldMapping = new LinkedHashMap<String, String>();
		Map<String, String> defaultValues = new LinkedHashMap<String, String>();
		Map<String, String> staticValues = new LinkedHashMap<String, String>();
		Map<String, Map<String, String>> valueMappings = new LinkedHashMap<String, Map<String, String>>();
		Map<String, String> valueTransformers = new LinkedHashMap<String, String>();
		String exportUri;
		String imageGallery;
		String downloadGallery;
		String linkGallery;
		String categoryFolder;
		String targetFolder;
		String targetType;
		String sourceType;
		String sourceFolder;
		String namePattern;
		// Script que realiza el mapeo
		String mapper;
		Integer maxResults;
		Long since;

		public MappingConfig() {
		}

		public Map<String, String> getFieldMapping() {
			return fieldMapping;
		}

		public void setFieldMapping(Map<String, String> fieldMapping) {
			this.fieldMapping = fieldMapping;
		}

		public Map<String, String> getDefaultValues() {
			return defaultValues;
		}

		public void setDefaultValues(Map<String, String> defaultValues) {
			this.defaultValues = defaultValues;
		}

		public Map<String, Map<String, String>> getValueMappings() {
			return valueMappings;
		}

		public void setValueMappings(Map<String, Map<String, String>> valueMappings) {
			this.valueMappings = valueMappings;
		}

		public Map<String, String> getValueTransformers() {
			return valueTransformers;
		}

		public void setValueTransformers(Map<String, String> transformers) {
			this.valueTransformers = transformers;
		}

		public String getExportUri() {
			return exportUri;
		}

		public void setExportUri(String exportUri) {
			this.exportUri = exportUri;
		}

		public String getImageGallery() {
			return imageGallery;
		}

		public void setImageGallery(String imageGallery) {
			this.imageGallery = imageGallery;
		}

		public String getDownloadGallery() {
			return downloadGallery;
		}

		public void setDownloadGallery(String downloadGallery) {
			this.downloadGallery = downloadGallery;
		}

		public String getLinkGallery() {
			return linkGallery;
		}

		public void setLinkGallery(String linkGallery) {
			this.linkGallery = linkGallery;
		}

		public String getCategoryFolder() {
			return categoryFolder
		}

		public void setCategoryFolder(String categoryFolder) {
			this.categoryFolder = categoryFolder
		}

		public String getTargetFolder() {
			return targetFolder;
		}

		public void setTargetFolder(String targetFolder) {
			this.targetFolder = targetFolder;
		}

		public String getTargetType() {
			return targetType;
		}

		public void setTargetType(String targetType) {
			this.targetType = targetType;
		}

		public String getSourceType() {
			return sourceType;
		}

		public void setSourceType(String sourceType) {
			this.sourceType = sourceType;
		}

		public String getSourceFolder() {
			return sourceFolder;
		}

		public void setSourceFolder(String sourceFolder) {
			this.sourceFolder = sourceFolder;
		}

		public String getNamePattern() {
			return namePattern;
		}

		public void setNamePattern(String namePattern) {
			this.namePattern = namePattern;
		}

		public Integer getMaxResults() {
			return maxResults;
		}

		public void setMaxResults(Integer maxResults) {
			this.maxResults = maxResults;
		}

		public Long getSince() {
			return since;
		}

		public void setSince(Long since) {
			this.since = since;
		}

		public String getMapper() {
			return mapper;
		}

		public void setMapper(String mapper) {
			this.mapper = mapper;
		}

		public Map<String, String> getStaticValues() {
			return staticValues;
		}

		public void setStaticValues(Map<String, String> staticValues) {
			this.staticValues = staticValues;
		}

		@Override
		public String toString() {
			return "MappingConfig{" +
					"fieldMapping=" + fieldMapping +
					", defaultValues=" + defaultValues +
					", staticValues=" + staticValues +
					", valueMappings=" + valueMappings +
					", valueTransformers=" + valueTransformers +
					", exportUri='" + exportUri + '\'' +
					", imageGallery='" + imageGallery + '\'' +
					", mapper='" + mapper + '\'' +
					", downloadGallery='" + downloadGallery + '\'' +
					", linkGallery='" + linkGallery + '\'' +
					", categoryFolder='" + categoryFolder + '\'' +
					", targetFolder='" + targetFolder + '\'' +
					", targetType='" + targetType + '\'' +
					", sourceType='" + sourceType + '\'' +
					", sourceFolder='" + sourceFolder + '\'' +
					", namePattern='" + namePattern + '\'' +
					", maxResults=" + maxResults +
					", since=" + since +
					'}';
		}
	}

	public byte[] getFileOnline(String uri) {
		CloseableHttpClient httpclient = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build();
		try {
			final URI build = new URIBuilder(uri).build();
			HttpGet get = new HttpGet(build);
			final byte[] execute = httpclient.execute(get, new ResponseHandler<byte[]>() {
				@Override
				public byte[] handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
					return EntityUtils.toByteArray(httpResponse.getEntity());
				}
			});
			return execute;
		} catch (Exception e) {

		} finally {
			try {
				httpclient.close();
			} catch (Exception e) {

			}
		}
		return new byte[0];
	}

	static class Downloader {

		public File download(URL url, File dstFile) {
			CloseableHttpClient httpclient = HttpClients.custom()
					.setRedirectStrategy(new LaxRedirectStrategy()) // adds HTTP REDIRECT support to GET and POST methods
					.build();
			try {
				HttpGet get = new HttpGet(url.toURI()); // we're using GET but it could be via POST as well
				File downloaded = httpclient.execute(get, new FileDownloadResponseHandler(dstFile));
				return downloaded;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			} finally {
				IOUtils.closeQuietly(httpclient);
			}
		}

		static class FileDownloadResponseHandler implements ResponseHandler<File> {

			private final File target;

			public FileDownloadResponseHandler(File target) {
				this.target = target;
			}

			@Override
			public File handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				InputStream source = response.getEntity().getContent();
				FileUtils.copyInputStreamToFile(source, this.target);
				return this.target;
			}
		}
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

	private String getNaturalPath(String path) {
		return path.replaceAll("\\[\\d+\\]", "").replaceAll("\\*", "");
	}

	private Integer getIndex(String node) {
		return Integer.parseInt(node.substring(node.indexOf('[') + 1, node.indexOf(']')));
	}

	private I_CmsXmlContentValue createTargetNode(String[] targetPaths,
												  List<Integer> targetRepetitionIndexes, List<Integer> currentValueIndexes,
												  CmsXmlContent content, String parentNode, int index) throws Exception {
		I_CmsXmlContentValue contentValue = null;

		final Integer currentNodeIndex = getCurrentNodeIndex(targetRepetitionIndexes, currentValueIndexes, index);
		final String currentNode = parentNode != null ? parentNode + "/" + targetPaths[index] : targetPaths[index];

		if (!content.hasValue(currentNode, locale, currentNodeIndex)) {

			contentValue = content.addValue(cmsObject, currentNode, locale, currentNodeIndex);
		} else {

			contentValue = content.getValue(currentNode, locale, currentNodeIndex);
		}
		// si hemos llegado al nodo final asignamos el valor
		if (index != targetPaths.length - 1) {
			String newParentNode = currentNode + "[" + (currentNodeIndex + 1) + "]";

			contentValue = createTargetNode(targetPaths, targetRepetitionIndexes, currentValueIndexes, content, newParentNode, index + 1);
		}
		return contentValue;
	}

	private Integer getCurrentNodeIndex(List<Integer> targetRepetitionIndexes, List<Integer> currentValueIndexes, int i) {
		if (targetRepetitionIndexes.contains(i)) {
			final int targetRepetitionIndex = targetRepetitionIndexes.indexOf(i);
			return currentValueIndexes.get(targetRepetitionIndex) - 1;
		} else {
			return 0;
		}
	}

	private I_CmsXmlContentValue createNode(CmsObject cmsObject, Locale locale, CmsXmlContent content, String[] targetPaths, String parentNode, int index) throws Exception {
		I_CmsXmlContentValue contentValue;
		final String currentNode = parentNode != null ? parentNode + "/" + targetPaths[index] : targetPaths[index];
		if (!content.hasValue(currentNode, locale, 0)) {
			contentValue = content.addValue(cmsObject, currentNode, locale, 0);
		} else {
			contentValue = content.getValue(currentNode, locale, 0);
		}
		if (index != targetPaths.length - 1) {
			String newParentNode = currentNode + "[1]";
			contentValue = createNode(cmsObject, locale, content, targetPaths, newParentNode, index + 1);
		}
		return contentValue;
	}
}