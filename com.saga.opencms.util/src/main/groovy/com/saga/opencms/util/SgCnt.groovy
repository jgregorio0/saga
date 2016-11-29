package com.saga.opencms.util

import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.i18n.CmsEncoder
import org.opencms.loader.CmsLoaderException
import org.opencms.main.CmsException
import org.opencms.main.OpenCms
import org.opencms.xml.CmsXmlContentDefinition
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.CmsXmlException
import org.opencms.xml.CmsXmlUtils
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.opencms.xml.types.I_CmsXmlContentValue
import org.xml.sax.SAXException

public class SgCnt {
	CmsObject cmso;
	String path;
	Locale locale;
	CmsFile file;
	String strContent;
	CmsXmlContent xmlContent;
	CmsXmlEntityResolver resolver;


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
		resolver = new CmsXmlEntityResolver(cmso);
		try {
			unmarshall(file);
		} catch (Exception e) {
//			throw new Exception("unmarshalled failed for resource $path".toString())
			xmlContent = null;
		}
	}

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

	public SgCnt unmarshall(CmsFile file) throws CmsXmlException {
		xmlContent = CmsXmlContentFactory.unmarshal(cmso, file);
		return this;
	}

	public SgCnt unmarshall()
			throws CmsXmlException {
		xmlContent = CmsXmlContentFactory
				.unmarshal(cmso, strContent, CmsEncoder.ENCODING_UTF_8, resolver);
		return this;
	}

	public SgCnt marshall()
			throws CmsXmlException, UnsupportedEncodingException {
		strContent = new String(xmlContent.marshal(), CmsEncoder.ENCODING_UTF_8);
		return this;
	}

	/**
	 * Obtiene la definición del contenido destino al que se va a mapear
	 *  by: rtinoco
	 * @param type
	 * @return
	 */
	CmsXmlContentDefinition contentDefinition(String type)
			throws CmsLoaderException, CmsXmlException, SAXException, IOException {
		I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(type);
		String schemaUri = "opencms:/${resType.configuration.schema}";
		return CmsXmlContentDefinition.unmarshal(schemaUri, new CmsXmlEntityResolver(cmso));
	}

	/**
	 * Asegura un valor en el path indicado, devolviendo uno existente o creandolo
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
				return procureValue(parentPath, obtainNodeIndex(parentPath));
			}

			// Comprobamos que al crear el padre no se haya creado automaticamente el hijo
			if (xmlContent.hasValue(path, locale, pos)) {
				return xmlContent.getValue(path, locale, pos);
			} else {
				return xmlContent.addValue(cmso, path, locale, pos);
			}
		}
	}

	private int obtainNodeIndex(String path) {
		int index = CmsXmlUtils.getXpathIndexInt(path);
		if (index > 0) {
			index = index - 1;
		}
		return index;
	}

	/**
	 * Asegura un valor en el path indicado, devolviendo uno existente o creandolo
	 * @param path
	 */
	public I_CmsXmlContentValue procureValue(String path)
			throws Exception {
		return procureValue(path, 0);
	}

	/**
	 * Set value for element path.
	 * @param path
	 * @param value
	 * @return
	 */
	public SgCnt setStringValue(String path, String value)
			throws Exception {
		return setStringValue(path, value, 0);
	}

	/**
	 * Set value for element path. If the element does not exist, procure new element and set value.
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
	 * Set value for element path. If the element does not exist, procure new element and set value.
	 * @param pathWithIdx
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
	 * Filling the content with empty tags
	 * @param tag
	 * @param limit
	 */
	public void fillWithEmptyValues(String tag, int limit)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		for (int i = 0; i < limit; i++) {
			if (!xmlContent.hasValue(tag, locale, i)){
				xmlContent.addValue(cmso, tag, locale, i);
			}
		}
	}

	/**
	 * Saving file changes from String content.
	 * For updating xml content execute unmarshall.
	 */
	public SgCnt saveStr()
			throws CmsException, UnsupportedEncodingException {
		SgCms.lock(cmso, path);
		update(strContent);
		cmso.writeFile(file);
		SgCms.unlock(cmso, path);
		return this;
	}

	/**
	 * Saving file changes from String content.
	 * For updating xml content execute unmarshall.
	 */
	public SgCnt saveStr(String contents)
			throws CmsException, UnsupportedEncodingException {
		SgCms.lock(cmso, path);
		update(contents);
		cmso.writeFile(file);
		SgCms.unlock(cmso, path);
		return this;
	}

	/**
	 * Saving file from Xml content changes.
	 * For updating string content execute marshall.
	 */
	public SgCnt saveXml()
			throws CmsException {
		SgCms.lock(cmso, path);
		file.setContents(xmlContent.marshal());
		cmso.writeFile(file);
		SgCms.unlock(cmso, path);
		return this;
	}

	public SgCnt repair()
			throws CmsException {
		SgCms.lock(cmso, path);
		xmlContent.setAutoCorrectionEnabled(true);

		// now correct the XML
		xmlContent.correctXmlStructure(cmso);

		// Prepare for write
		xmlContent.getHandler().prepareForWrite(cmso, xmlContent, file);
		SgCms.unlock(cmso, path);

		return this;
	}

	/**
	 * Returns String value from OpenCmsString element
	 * @param tag
	 * @return
	 */
	public String getStringValue(String tag)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		return xmlContent.getStringValue(cmso, tag, locale);
	}

	/**
	 * Returns String value from OpenCmsString element
	 * @param base Parent path
	 * @param tag Tag element
	 * @param pos 0 based position
	 * @return
	 * @throws Exception
	 */
	public String getStringValue(String base, String tag, Integer pos)
			throws Exception {
		String path = base + "/" + tag + "[" + (pos+1) + "]";
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
	 * String value for element defined by path without indexes and list of indexes.
	 * For example, "Gallery/Image" and indexes [0, 1] define "Gallery[1]/Image[2]".
	 * @param pathNoIdx Element path without index "Gallery/Image"
	 * @param idxs List of indexes [0, 1]
	 * @return
	 */
	public String getStringValueWithIndex(String pathNoIdx, Integer[] idxs)
			throws Exception {
		return getStringValueWithIndex(pathNoIdx, Arrays.asList(idxs));
	}

	/**
	 * Returns String value from OpenCmsHtml element
	 * @param element
	 * @return
	 */
	public String getHtmlStringValue(String element)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		String value = null;
		if (xmlContent.hasValue(element, locale)){
			value = xmlContent.getValue(element, locale).getStringValue(cmso);
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
			throw new Exception("xml content not exists for resource $path".toString());
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
			throw new Exception("xml content not exists for resource $path".toString());
		}
		return xmlContent.hasValue(element, locale, pos);
	}

	/**
	 * Add value to content definition
	 * @param element
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue addValue(String element, int pos) {
		return xmlContent.addValue(cmso, element, locale, pos);
	}

	/**
	 * Add value to content definition
	 * @param element
	 * @return
	 */
	public I_CmsXmlContentValue addValue(String element) {
		return xmlContent.addValue(cmso, element, locale, count(element));
	}

	/**
	 * Add value to content definition
	 * @param element
	 * @param value
	 * @return
	 */
	public SgCnt addValue(String element, String value) throws Exception {
		I_CmsXmlContentValue xmlValue = addValue(element);
		setStringValue(element, value, xmlValue.getIndex());
		return this;
	}

	/**
	 * Check if the content has value for element in the position
	 * @param element
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue getValue(String element, int pos)
			throws Exception {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString());
		}
		return xmlContent.getValue(element, locale, pos);
	}

	/**
	 * Check if the content has value for element in the position
	 * @param element
	 * @return
	 */
	public I_CmsXmlContentValue getValue(String element)
			throws Exception {
		return getValue(element, 0);
	}

	/**
	 * Return list of contents into resource defined by element
	 * @param element
	 * @return
	 */
	public List<I_CmsXmlContentValue> getValues(String element){
		return xmlContent.getValues(element, locale);
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
	 * Check if exists xmlPath content
	 * @param xmlPath
	 * @return
	 */
	public int count(String xmlPath) {
		return xmlContent.getIndexCount(xmlPath, locale);
	}

	/**
	 * Return map composed by resource identification and content:
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

		CmsResource resource = cmso.readResource(path);
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
	 * Return escape HTML block
	 * @param s
	 * @return
	 */
	public static String escapeHTML(String s) {
		return StringEscapeUtils.escapeHtml4(s);
	}

	/**
	 * Return last path value and ensure parent path and brother elements exist
	 * @param pathWithIndex Content[2]/Image[4]/Link
	 * @return
	 */
	public I_CmsXmlContentValue procureValueIdx(String pathWithIndex)
			throws Exception {
		return procureValueIdx(new Path(pathWithIndex));
	}

	/**
	 * Return last element value. Ensure parent elements and brother elements exist
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
}