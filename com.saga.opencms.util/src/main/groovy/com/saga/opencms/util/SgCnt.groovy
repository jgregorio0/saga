package com.saga.opencms.util
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.utils.URIBuilder
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.CmsResourceTypeXmlContent
import org.opencms.file.types.I_CmsResourceType
import org.opencms.i18n.CmsEncoder
import org.opencms.loader.CmsLoaderException
import org.opencms.main.CmsException
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil
import org.opencms.xml.CmsXmlContentDefinition
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.CmsXmlException
import org.opencms.xml.CmsXmlUtils
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.opencms.xml.types.I_CmsXmlContentValue
import org.xml.sax.SAXException

public class SgCnt {

	/** XSD */
	public static final String EXT_XSD = ".xsd"
	public static final String SCHEMA_DEF = "opencms:/"

	CmsObject cmso;
	String path;
	Locale locale;
	CmsFile file;
	String strContent;
	CmsXmlContent xmlContent;

	public SgCnt(){
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resourcePath
	 */
	public SgCnt(CmsObject cmso, String resourcePath)
			throws CmsException, UnsupportedEncodingException {
		init(cmso, resourcePath, cmso.getRequestContext().getLocale());
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resource
	 */
	public SgCnt(CmsObject cmso, CmsResource resource)
			throws CmsException, UnsupportedEncodingException {
		init(cmso, cmso.getSitePath(resource), cmso.getRequestContext().getLocale());
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resourcePath
	 * @param locale
	 */
	public SgCnt(CmsObject cmso, String resourcePath, Locale locale)
			throws CmsException, UnsupportedEncodingException {
		init(cmso, resourcePath, locale);
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resourcePath
	 * @param localeStr
	 */
	public SgCnt(CmsObject cmso, String resourcePath, String localeStr)
			throws CmsException, UnsupportedEncodingException {
		Locale locale = new Locale(localeStr);
		init(cmso, resourcePath, locale);
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resource
	 * @param localeStr
	 */
	public SgCnt(CmsObject cmso, CmsResource resource, String localeStr)
			throws CmsException, UnsupportedEncodingException {
		String resourcePath = cmso.getSitePath(resource);
		Locale locale = new Locale(localeStr);
		init(cmso, resourcePath, locale);
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resource
	 * @param locale
	 */
	public SgCnt(CmsObject cmso, CmsResource resource, Locale locale)
			throws CmsException, UnsupportedEncodingException {
		String resourcePath = cmso.getSitePath(resource);
		init(cmso, resourcePath, locale);
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects throw exception but strContent is readable.
	 * @param cmso
	 * @param path
	 * @param locale
	 */
	private void init(CmsObject cmso, String path, Locale locale)
			throws CmsException, UnsupportedEncodingException {
		this.cmso = cmso;
		this.path = path;
		this.locale = locale;

		//Leemos el recurso
		file = cmso.readFile(path, CmsResourceFilter.ALL);
		strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
		try {
			unmarshall(file);
		} catch (Exception e) {
			xmlContent = null;
		}
	}

	CmsObject getCmso() {
		return cmso
	}

	String getPath() {
		return this.path
	}

	Locale getLocale() {
		return locale
	}

	CmsFile getFile() {
		return file
	}

	String getStrContent() {
		return strContent
	}

	CmsXmlContent getXmlContent() {
		return xmlContent
	}

	void setPath(String path) {
		this.path = path
	}

	void setLocale(Locale locale) {
		this.locale = locale
	}

	void setLocale(String locale) {
		this.locale = new Locale(locale)
	}

	void setFile(CmsFile file) {
		this.file = file
	}

	void setStrContent(String strContent) {
		this.strContent = strContent
	}

	void setXmlContent(CmsXmlContent xmlContent) {
		this.xmlContent = xmlContent
	}
/**
 * Update control code content
 * @param strContent
 * @return
 * @throws UnsupportedEncodingException
 * @throws CmsException
 */
	public SgCnt update(String strContent)
			throws UnsupportedEncodingException, CmsException {
		this.strContent = strContent;
		file.setContents(strContent.getBytes(CmsEncoder.ENCODING_UTF_8));
		if (xmlContent != null) {
			unmarshall(file);
			repair();
		}
		return this;
	}

	/**
	 * Unmarshall xml content from file
	 * @param file
	 * @return
	 * @throws CmsXmlException
	 */
	public SgCnt unmarshall(CmsFile file) throws CmsXmlException {
		xmlContent = CmsXmlContentFactory.unmarshal(cmso, file);
		return this;
	}

	/**
	 * Create locale for xml content
	 * @param locale
	 * @return
	 */
	public SgCnt initLocale(Locale locale) {
		if (!xmlContent.hasLocale(locale)){
			xmlContent.addLocale(cmso, locale);
		}
		return this;
	}

	/**
	 * Create locale content node
	 * @param locale
	 * @return
	 */
	public SgCnt initLocale(String locale) {
		return initLocale(new Locale(locale));
	}

	/**
	 * Clone xml locale resource content
	 * @param resource
	 * @param srcLoc
	 * @param dstLoc
	 * @param overwrite
	 */
	public boolean cloneXmlLocale(Locale srcLoc, Locale dstLoc, boolean overwrite)
			throws IllegalArgumentException, CmsXmlException {
		boolean copied = false;

		//Validamos primero que sea un XmlContent
		if(!isXmlContentType()){
			throw new IllegalArgumentException(
					"resource $path is not xml content");
		}

		//Obtenemos el XmlContent
		if (!hasLocale(srcLoc)) {
			throw new IllegalArgumentException(
					"resource $path has not locale $srcLoc");
		}

		//Si ya existe el locale lo borramos o no hacemos nada
		if (hasLocale(dstLoc)) {
			if (overwrite) {
				xmlContent.removeLocale(dstLoc);
			} else {
				return false;
			}
		}

		// Copiamos el locale
		xmlContent.copyLocale(srcLoc, dstLoc);
		return copied;
	}

	/**
	 * Get content definition for resource type
	 * by: rtinoco
	 * @param type
	 * @return
	 */
	public static CmsXmlContentDefinition contentDefinition(CmsObject cmso, String type)
			throws CmsLoaderException, CmsXmlException, SAXException, IOException {
		I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(type);
		String schemaUri = "opencms:/${resType.configuration.schema}";
		return CmsXmlContentDefinition.unmarshal(schemaUri, new CmsXmlEntityResolver(cmso));
	}

	/**
	 * Create document for resource definition and locale
	 * by: rtinoco
	 * @param cmso
	 * @param locale
	 * @param contentDefinition
	 * @return
	 */
	public static CmsXmlContent createDocument(CmsObject cmso, Locale locale, CmsXmlContentDefinition contentDefinition){
		return CmsXmlContentFactory.createDocument(
				cmso, locale, "UTF-8", contentDefinition);
	}


	/**
	 * Check if resource is xml content
	 * @return
	 */
	public boolean isXmlContentType(){
		CmsResource resource = cmso.readResource(this.path);
		return CmsResourceTypeXmlContent.isXmlContent(resource);
	}

	/**
	 * Check if resource is xml content
	 * @return
	 */
	public static boolean isXmlContentType(CmsObject cmso, String path){
		CmsResource resource = cmso.readResource(path);
		return CmsResourceTypeXmlContent.isXmlContent(resource);
	}

	/**
	 * Check if content has locale
	 * @param locale
	 * @return
	 */
	public boolean hasLocale(String locale){
		return xmlContent.hasLocale(new Locale(locale));
	}

	/**
	 * Provide value for content path.
	 * If it exists return value. If it does not exist create empty value for path.
	 * @param path
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue procureValue(String path, int pos)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		if (xmlContent.hasValue(path, locale, pos)) {
			return xmlContent.getValue(path, locale, pos);
		} else {

			// Si no existe aseguramos que el padre exista
			if (CmsXmlUtils.isDeepXpath(path)) {
				String parentPath = CmsXmlUtils.createXpath(CmsXmlUtils.removeLastXpathElement(path), 1);
				procureValue(parentPath, obtainNodeIndex(parentPath));
			}

			// Comprobamos que al crear el padre no se haya creado automaticamente el hijo
			if (xmlContent.hasValue(path, locale, pos)) {
				return xmlContent.getValue(path, locale, pos);
			} else {
				return xmlContent.addValue(cmso, path, locale, pos);
			}
		}
	}


	/**
	 * Provide value for content path.
	 * If it exists return value. If it does not exist create empty value for path.
	 * @param path
	 */
	public I_CmsXmlContentValue procureValue(String path)
			throws Exception {
		return procureValue(path, 0);
	}

	/**
	 * Remove value. Value must exists.
	 * @param path
	 * @return
	 */
	public SgCnt rmValue(String path){
		return rmValue(path, 0);
	}

	/**
	 * Remove value. Value must exists.
	 * @param path
	 * @param i
	 * @return
	 */
	public SgCnt rmValue(String path, int i){
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		xmlContent.removeValue(path, locale, i);
		return this;
	}

	/**
	 * Get index for given path
	 * @param path
	 * @return
	 */
	private int obtainNodeIndex(String path) {
		int index = CmsXmlUtils.getXpathIndexInt(path);
		if (index > 0) {
			index = index - 1;
		}
		return index;
	}


	/**
	 * Set value for element path.
	 * If index is specified all previous index values must exists.
	 * @param path ex. Title, Content/Title, Content[2]/Title
	 * @param value
	 * @return
	 */
	public SgCnt setStringValue(String path, String value)
			throws Exception {
		if (path.contains("[")) {
			setStringValueIdx(path, value)
		} else {
			setStringValue(path, value, 0);
		}
		return this;
	}

	/**
	 * Set value for element path to the given index.
	 * All previous index values must exists.
	 * @param path
	 * @param value
	 * @param pos
	 * @return
	 */
	public SgCnt setStringValue(String path, String value, int pos)
			throws Exception {
		I_CmsXmlContentValue content = procureValue(path, pos);
		content.setStringValue(cmso, value);
		return this;
	}

	/**
	 * Create a new element at last position and set value for element path.
	 * @param path
	 * @param value
	 * @return
	 */
	public SgCnt appendStringValue(String path, String value)
			throws Exception {
		setStringValue(path, value, count(path));
		return this;
	}

	/**
	 * Append category at last position.
	 * Categories are appended separated by comma in control code content.
	 * Categories
	 * @param path
	 * @param value
	 * @return
	 */
	public SgCnt appendCategoryStringValue(String path, String value)
			throws Exception {
		String newVal = getStringValue(path) + "," + value;
		setStringValue(path, newVal, 0);
		return this;
	}

	/**
	 * Set value for element path.
	 * If the element does not exist, procure new element with empty value.
	 * @param pathWithIdx Content[2]/Title[1]
	 * @param value
	 * @return
	 */
	public SgCnt setStringValueIdx(String pathWithIdx, String value)
			throws Exception {
		I_CmsXmlContentValue content = procureValueIdx(pathWithIdx);
		content.setStringValue(cmso, value);
		return this;
	}

	/**
	 * Filling the content path with empty values
	 * @param path
	 * @param limit
	 */
	public void fillWithEmptyValues(String path, int limit)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content does not exist");
		}
		for (int i = 0; i < limit; i++) {
			if (!xmlContent.hasValue(path, locale, i)){
				xmlContent.addValue(cmso, path, locale, i);
			}
		}
	}

	/**
	 * Saving resource changes done in string content.
	 * Unmarshall and repair updating xml content internally.
	 */
	public SgCnt saveStr()
			throws CmsException, UnsupportedEncodingException {
		SgCms.lock(cmso, this.path);
		update(strContent);
		cmso.writeFile(file);
		SgCms.unlock(cmso, this.path);
		return this;
	}

	/**
	 * Saving file changes from using given string content.
	 * Unmarshall and repair updating xml content internally.
	 */
	public SgCnt saveStr(String contents)
			throws CmsException, UnsupportedEncodingException {
		SgCms.lock(cmso, this.path);
		update(contents);
		cmso.writeFile(file);
		SgCms.unlock(cmso, this.path);
		return this;
	}

	/**
	 * Saving file changes done in Xml content.
	 * String content is not updated.
	 */
	public SgCnt saveXml()
			throws CmsException {
		SgCms.lock(cmso, this.path);
		file.setContents(xmlContent.marshal());
		repair()
		cmso.writeFile(file);
		SgCms.unlock(cmso, this.path);
		return this;
	}

	/**
	 * Repair Xml content.
	 * Resource must be locked and unlocked manually.
	 * @return
	 * @throws CmsException
	 */
	public SgCnt repair()
			throws CmsException {
		xmlContent.setAutoCorrectionEnabled(true);
		xmlContent.correctXmlStructure(cmso);
		xmlContent.getHandler().prepareForWrite(cmso, xmlContent, file);
		return this;
	}

	/**
	 * Return String value for given path element
	 * @param pathg
	 * @return
	 */
	public String getStringValue(String path)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		return xmlContent.getStringValue(cmso, path, locale);
	}

	/**
	 * Returns String value
	 * @param basePath Parent path
	 * @param tag Tag element
	 * @param pos 0 based position
	 * @return
	 * @throws Exception
	 */
	public String getStringValue(String basePath, String tag, Integer pos)
			throws Exception {
		String path = basePath + "/" + tag + "[" + (pos+1) + "]";
		return getStringValue(path);
	}

	/**
	 * String value for element defined by path without indexes and list of indexes.
	 * For example, "Gallery/Image" and indexes [0, 1] define "Gallery[1]/Image[2]".
	 * @param pathNoIdx Element path without index "Gallery/Image"
	 * @param idxs List of indexes [0, 1]
	 * @return
	 */
	public String getStringValueWithIndex(String pathNoIdx, List<Integer> idxs)
			throws Exception {
		String pathWithIndex = generatePathWithIndex(pathNoIdx, idxs);
		return getStringValue(pathWithIndex);
	}

	/**
	 * Returns String value for given path when content is Html type.
	 * @param path
	 * @return
	 */
	public String getHtmlStringValue(String path)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		String value = null;
		if (xmlContent.hasValue(path, locale)){
			value = xmlContent.getValue(path, locale).getStringValue(cmso);
		}
		return value;
	}

	/**
	 * Check if the content has value for element
	 * @param element
	 * @return
	 */
	public boolean hasValue(String element)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource ${this.path} ".toString());
		}
		return xmlContent.hasValue(element, locale);
	}

	/**
	 * Check if the content has value for element in the position
	 * @param element
	 * @param pos
	 * @return
	 */
	public boolean hasValue(String element, int pos)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource ${this.path} ".toString());
		}
		return xmlContent.hasValue(element, locale, pos);
	}

	/**
	 * Add value to Xml content
	 * @param path
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue addValue(String path, int pos) {
		return xmlContent.addValue(cmso, path, locale, pos);
	}

	/**
	 * Add value to Xml content
	 * @param path
	 * @return
	 */
	public I_CmsXmlContentValue addValue(String path) {
		return xmlContent.addValue(cmso, path, locale, count(path));
	}

	/**
	 * Add value to Xml content
	 * @param path
	 * @param value
	 * @return
	 */
	public SgCnt addValue(String path, String value) throws Exception {
		I_CmsXmlContentValue xmlValue = addValue(path);
		setStringValue(path, value, xmlValue.getIndex());
		return this;
	}

	/**
	 * Check if the content has value for given path and position
	 * @param path
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue getValue(String path, int pos)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		return xmlContent.getValue(path, locale, pos);
	}

	/**
	 * Check if the content has value for given path and position
	 * @param path
	 * @return
	 */
	public I_CmsXmlContentValue getValue(String path)
			throws Exception {
		return getValue(path, 0);
	}

	/**
	 * Return list of contents into resource defined by element
	 * @param path
	 * @return
	 */
	public List<I_CmsXmlContentValue> getXmlContentValues(String path){
		return xmlContent.getValues(path, locale);
	}

	/**
	 * Check if exists xmlPath content
	 * @param xmlPath
	 * @param pos
	 * @return
	 */
	public boolean contains(String xmlPath, int pos) {
		return xmlContent.getValue(xmlPath, locale, pos) != null;
	}

	/**
	 * Check if exists xmlPath content
	 * @param xmlPath
	 * @return
	 */
	public boolean contains(String xmlPath) {
		return contains(xmlPath, 0);
	}

	/**
	 * Count how many elements for given path
	 * @param xmlPath
	 * @return
	 */
	public int count(String xmlPath) {
		return xmlContent.getIndexCount(xmlPath, locale);
	}

	/**
	 * Return map composed by resource identification (structureId@rootPath) and one level content (XmlPath: Value):
	 * <structureId@rootPath: <path1: value1, path2: value2, ...>>
	 * by: rtinoco
	 * @return
	 */
	public Map<String, Map<String, String>> export()
			throws CmsException {
		String cmsVfsFileType = "OpenCmsVfsFile";
		Map<String, Map<String, String>> exp =
				new LinkedHashMap<String, Map<String, String>>();
		Map<String, String> content = new LinkedHashMap<String, String>();

		final List<String> names = elemNames();
		Collections.sort(names);
		final List<String> elemPaths = new ArrayList<String>();
		for (int i = 0; i < names.size(); i++) {
			if (i > 0 && names.get(i).startsWith(names.get(i - 1))) {
				elemPaths.remove(elemPaths.size() - 1);
			}
			elemPaths.add(names.get(i));
		}

		for (String elemPath : elemPaths) {
			final I_CmsXmlContentValue value = xmlContent.getValue(elemPath, locale);
			if (value.isSimpleType()) {
				if (value.getTypeName().equals(cmsVfsFileType)) {
					String onlineLink = OpenCms.getLinkManager().getOnlineLink(cmso, value.getStringValue(cmso));
					content.put(elemPath, onlineLink);
				} else {
					content.put(elemPath, value.getStringValue(cmso));
				}
			}
		}

		CmsResource resource = cmso.readResource(this.path);
		exp.put(resource.getStructureId().getStringValue() + "@" + resource.getRootPath(), content);
		return exp;
	}

	/**
	 * Return list of elements names
	 * @return
	 */
	public List<String> elemNames(){
		return xmlContent.getNames(locale);
	}

	/**
	 * Return last path value and ensure parent element and brother elements exist
	 * @param pathWithIndex Content[2]/Image[4]/Link
	 * @return
	 */
	public I_CmsXmlContentValue procureValueIdx(String pathWithIndex)
			throws Exception {
		return procureValueIdx(new Path(pathWithIndex));
	}

	/**
	 * Return last path value and ensure parent elements and brother elements exist
	 * @param path
	 * @return
	 */
	public I_CmsXmlContentValue procureValueIdx(Path path)
			throws Exception {
		I_CmsXmlContentValue value = null;
		for (int elemPos = 0; elemPos < path.elems.size(); elemPos++) {
			for (int idx = 1; idx <= path.elems.get(elemPos).idx; idx++) {
				value = procureValueIdx(path.elems.get(elemPos), idx - 1);
			}
		}
		return value;
	}

	/**
	 * Procure value for element path and position.
	 *
	 * @param elem Absolute element path
	 * @param pos Element Index less 1
	 * @return
	 */
	public I_CmsXmlContentValue procureValueIdx(Elem elem, int pos)
			throws Exception {
		String elemPath = elem.absElemPathWitoutLastIdx();
		if (hasValue(elemPath, pos)) {
			return getValue(elemPath, pos);
		} else {
			return addValue(elemPath, pos);
		}
	}

	/**
	 * Generate path for mapping content
	 * @param cleanPath
	 * @param tarPattIdxs
	 * @param srcPattIdxs
	 * @param srcIdxValues
	 * @return
	 */
	String generatePathWithIndex(String cleanPath, List<Integer> tarPattIdxs, List<Integer> srcPattIdxs, List<Integer> srcIdxValues) {
		Path path = new Path(cleanPath);
		for (int i = 0; i < srcPattIdxs.size(); i++) {
			path.elems.get(tarPattIdxs.get(i)).idx = srcIdxValues.get(srcPattIdxs.get(i));
		}
		return path.toString();
	}

	/**
	 * Generate path for mapping content
	 * @param cleanPath
	 * @param srcIdxValues
	 * @return
	 */
	String generatePathWithIndex(String cleanPath, List<Integer> srcIdxValues) {
		Path path = new Path(cleanPath);
		for (int i = 0; i < srcIdxValues.size(); i++) {
			path.elems.get(i).idx = srcIdxValues.get(i);
		}
		return path.toString();
	}

	/**
	 * Path y Elem classes emulate elems path with index. For example:
	 * Path: Content[1]/Text[2]
	 * Elem: Content[1]
	 */
	public class Path{
		public String absPath;
		public List<Elem> elems;
		Path(String path){
			absPath = path;
			elems = new ArrayList<Elem>();
			String[] elemsPathParts = path.split("/");

			for (int i = 0; i < elemsPathParts.length; i++) {
				String parent = null;
				if (i > 0) {
					parent = elemsPathParts[0];
					for (int j = 1; j < i; j++) {
						parent = parent + "/" + elemsPathParts[j];
					}
				}

				String elemPath = elemsPathParts[i];
				String idx = String.valueOf(1);
				if (elemPath.contains("[")){
					int idxBegin = elemPath.indexOf("[");
					idx = elemPath.substring(idxBegin + 1, elemPath.length() - 1);
					elemPath = elemPath.substring(0, idxBegin);
				}

				elems.add(new Elem(idx, elemPath, parent));
			}
		}

		/**
		 * Return a list of indexes for path. For example:
		 * Content[2]/Image[10]/Image[1]
		 * returns [2, 10, 1]
		 * @param pathWithIdx
		 * @return
		 */
		public List indexes(String pathWithIdx){
			List<Integer> idxs = new ArrayList<Integer>();
			String[] parts = pathWithIdx.split("/");
			for (String part : parts) {
				idxs.add(CmsXmlUtils.getXpathIndexInt(part));
			}
			return idxs;
		}

		public List indexes(){
			List idxs = new ArrayList<Integer>();
			for (int i = 0; i < elems.size(); i++) {
				idxs.add(elems.get(i).idx);
			}
			return idxs;
		}

		public String toString(){
			String res = elems.get(0).toString();
			for (int i = 1; i < elems.size(); i++) {
				res = res + "/" + elems.get(i).toString();
			}
			return res;
		}
	}
	public class Elem{
		public int idx; // XPath index. For example, Gallery[1] idx is 1. Must not be 0.
		public String elemPath;
		public String parent;

		public Elem(Integer idx, String path, String parent){
			this.idx = idx;
			this.elemPath = path;
			this.parent = parent;
		}

		public Elem(String idx, String path, String parent){
			this.idx = Integer.valueOf(idx);
			this.elemPath = path;
			this.parent = parent;
		}

		public Elem(String idx, String path){
			this(idx, path, null);
		}

		/**
		 * Return absolute element path
		 * @return
		 */
		public String absElemPath(){
			if (StringUtils.isNotEmpty(parent)) {
				return parent + "/" + elemPath + "[" + idx + "]";
			} else {
				return elemPath + "[" + idx + "]";
			}
		}

		/**
		 * Return absolute element path without last index
		 * @return
		 */
		public String absElemPathWitoutLastIdx(){
			if (StringUtils.isNotEmpty(parent)) {
				return parent + "/" + elemPath;
			} else {
				return elemPath;
			}
		}

		public String toString(){
			return elemPath + "[" + idx + "]";
		}
	}

	/**
	 * Read type resource
	 * @param content
	 * @return
	 */
	public static String readSchemaPath(String content){
		String path = null;
		int iStart = content.indexOf(SCHEMA_DEF) + "opencms:/".length();
		int iEnd = content.indexOf("\">", iStart);
		if (iStart > -1 && iEnd > -1 && iEnd > iStart) {
			path = content.substring(iStart, iEnd);
		}
		return path;
	}

	/**
	 * Read type resource
	 * @param content
	 * @return
	 */
	public static String readType(String content){
		String type = null;
		String schema = readSchemaPath(content)
		if (schema) {
			int lastSlash = schema.lastIndexOf('/') + 1;
			int ext = schema.indexOf(EXT_XSD, lastSlash);
			if (lastSlash > -1 && ext > -1 && ext > lastSlash) {
				type = schema.substring(lastSlash, ext);
			}
		}
		return type;
	}

	/**
	 * Add value to content node
	 * @param cmso
	 * @param locale
	 * @param content
	 * @param idxPaths
	 * @param value
	 */
	public static void addValueToContent(CmsObject cmso, Locale locale, CmsXmlContent content, List<String> idxPaths, String value) {
		I_CmsXmlContentValue node = createNode(cmso, locale, content, idxPaths);
		node.setStringValue(cmso, value);
	}

	/**
	 * Add default index [1] for each path element
	 * @param path
	 * @return
	 */
	public static List<String> addDefaultIdxPaths(String path){
		List<String> idxPaths = [];
		List<String> paths = Arrays.asList(path.split("/"))
		for (int i = 0; i < paths.size(); i++) {
			String idxPath = paths.get(i);
			if (!isIdxPath(idxPath)) {
				idxPath =+ "[1]"
			}
			idxPaths.add(idxPath)
		}
		return idxPaths;
	}

	/**
	 * Create new content node
	 *
	 * @param cmso
	 * @param locale
	 * @param content Xml resource content
	 * @param idxPaths List of paths that represent node including index (Content[1]/Text[2])
	 * @return Node given by joining param paths
	 * @throws Exception
	 */
	public static I_CmsXmlContentValue createNode(CmsObject cmso, Locale locale, CmsXmlContent content, List<String> idxPaths)
			throws Exception {
		I_CmsXmlContentValue node;

		// Add content nodes for whole path
		String currPath = "";
		for (int i = 0; i < idxPaths.size(); i++) {
			String targetPath = idxPaths.get(i);
			if (i == 0) {
				currPath = targetPath;
			} else {
				currPath = currPath + "/" + targetPath;
			}

			String cleanPath = cleanLastIdxPath(currPath);
			int idxPath = getIdxPath(currPath) - 1;
			if (!content.hasValue(cleanPath, locale, idxPath)) {
				content.addValue(cmso, cleanPath, locale, idxPath);
			}

			if (i == idxPaths.size() - 1) {
				node = content.getValue(cleanPath, locale, idxPath);
			}
		}
		return node;
	}

	/**
	 * Check if path contains index
	 * @param path
	 * @return
	 */
	public static boolean isIdxPath(String path){
		return path.contains("[");
	}

	/**
	 * Return index for path (Content[1] -> 1)
	 * @param idxPath
	 * @return
	 */
	public static String cleanLastIdxPath(String idxPath){
		int lastBracket = idxPath.lastIndexOf("[");
		return idxPath.substring(0, lastBracket);
	}

	/**
	 * Clean indexes from paths
	 * @param idxPath
	 * @return
	 */
	public static List<String> cleanAllIdxPath(String idxPath){
		List<String> paths = [];
		List<String> idxPathsList = Arrays.asList(idxPath.split("/"))
		for (int i = 0; i < idxPathsList.size(); i++) {
			String path = idxPathsList.get(i);
			if (isIdxPath(idxPath)) {
				path = cleanLastIdxPath(path)
			}
			paths.add(path)
		}
		return paths;
	}

	public static int getIdxPath(String idxPath){
		int lastBracket = idxPath.lastIndexOf("[");
		return Integer.valueOf(idxPath.substring(lastBracket + 1, lastBracket + 2));
	}

	/**
	 * Upload file from URL
	 *
	 * @param CmsObject cmso
	 * @param cfg Config galleries
	 * @param url Origin URL file
	 * @return
	 */
	CmsResource createFile(CmsObject cmso, String url, String imageGallery, String downloadGallery) {
		// Obtenemos la información del fichero que almacenar
		Map fileInfo = getFileInfo(url, imageGallery, downloadGallery);

		// Cuando se importan ficheros, se almacena en la propiedad 'migration.uri' la ruta original del fichero.
		// Ya que todas las imágenes van a la misma carpeta durante la importación, y para evitar que ficheros con el
		// mismo nombre en el origen, aunque alojados en carpetas distintas, se traten como si fueran ficheros igulaes,
		// comprobamos que si existe en la galería donde se importan los ficheros un fichero que procede de la misma ruta
		// origen (lo hacemos comprobando la propiedad 'migration.uri')
		CmsResource res = findByProperty([imageGallery, downloadGallery], "migration.uri", url)

		// si no existe el fichero lo creamos
		if (!res) {
			res = uploadFile(cmso, url, fileInfo.path, fileInfo.resourceType);
		}

		return res;
	}



	/**
	 * Get info map (resourceType and path) from file
	 *
	 * @param url Origin URL file
	 * @param imageGallery
	 * @param downloadGallery
	 * @return
	 */
	public static Map getFileInfo(String url, String imageGallery, String downloadGallery) {
		def info = [:];
		String filePath = new URIBuilder(url).path;
		String fileName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());

		String ext = FilenameUtils.getExtension(fileName).toLowerCase();
		if (SgCms.EXT_IMAGE.contains(ext)) {
			info.resourceType = OpenCms.resourceManager.getResourceType(SgCms.IMAGE_TYPE);
			info.path = CmsStringUtil.addLeadingAndTrailingSlash(imageGallery) + fileName;
		} else if (SgCms.EXT_VIDEO.contains(ext)){
			info.resourceType = OpenCms.resourceManager.getResourceType(SgCms.PLAIN_TYPE);
			info.path = CmsStringUtil.addLeadingAndTrailingSlash(downloadGallery) + fileName;
		} else {
			info.resourceType = OpenCms.resourceManager.getResourceType(SgCms.BINARY_TYPE);
			info.path = CmsStringUtil.addLeadingAndTrailingSlash(downloadGallery) + fileName;
		}
		info;
	}


}