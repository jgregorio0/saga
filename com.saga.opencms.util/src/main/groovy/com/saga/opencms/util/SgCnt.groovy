package com.saga.opencms.util

import org.apache.commons.lang3.StringEscapeUtils
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.i18n.CmsEncoder
import org.opencms.main.OpenCms
import org.opencms.xml.CmsXmlContentDefinition
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.CmsXmlUtils
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.opencms.xml.types.I_CmsXmlContentValue

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
	 * @param locale
	 */
	public SgCnt(CmsObject cmso, String resourcePath, Locale locale){
		init(cmso, resourcePath, locale)
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resourcePath
	 * @param locale
	 */
	public SgCnt(CmsObject cmso, String resourcePath, String localeStr){
		Locale locale = new Locale(localeStr)
		init(cmso, resourcePath, locale)
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resourcePath
	 * @param localeStr
	 */
	public SgCnt(CmsObject cmso, CmsResource resource, String localeStr){
		String resourcePath = cmso.getSitePath(resource)
		Locale locale = new Locale(localeStr)
		init(cmso, resourcePath, locale)
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but strContent is still readable.
	 * @param cmso
	 * @param resourcePath
	 * @param localeStr
	 */
	public SgCnt(CmsObject cmso, CmsResource resource, Locale locale){
		String resourcePath = cmso.getSitePath(resource)
		init(cmso, resourcePath, locale)
	}

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects throw exception but strContent is readable.
	 * @param cmso
	 * @param path
	 * @param locale
	 */
	private void init(CmsObject cmso, String path, Locale locale){
		this.cmso = cmso;
		this.path = path;
		this.locale = locale;

		//Leemos el recurso
		file = cmso.readFile(path, CmsResourceFilter.ALL);
		strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
		resolver = new CmsXmlEntityResolver(cmso);
		try {
			unmarshall(file)
		} catch (Exception e) {
//			throw new Exception("unmarshalled failed for resource $path".toString())
			xmlContent = null;
		}
	}

	def update(String strContent) {
		this.strContent = strContent
		file.setContents(strContent.getBytes(CmsEncoder.ENCODING_UTF_8))
		if (xmlContent != null) {
			unmarshall(file)
			repair()
		}
		this
	}

	public def unmarshall(CmsFile file) {
		xmlContent = CmsXmlContentFactory.unmarshal(cmso, file)
		this
	}

	public def unmarshall() {
		xmlContent = CmsXmlContentFactory
				.unmarshal(cmso, strContent, CmsEncoder.ENCODING_UTF_8, resolver)
		this
	}

	public def marshall() {
		strContent = new String (xmlContent.marshal(), CmsEncoder.ENCODING_UTF_8)
		this
	}

	/**
	 * Obtiene la definición del contenido destino al que se va a mapear
	 *  by: rtinoco
	 * @param cmsObject CmsObject
	 * @param contentType Tipo de contenido
	 * @return
	 */
	CmsXmlContentDefinition contentDefinition(String type) {
		I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(type)
		def schemaUri = "opencms:/${resType.configuration.schema}";
		return CmsXmlContentDefinition.unmarshal(schemaUri, new CmsXmlEntityResolver(cmso));
	}

	/**
	 * Asegura un valor en el path indicado, devolviendo uno existente o creandolo
	 * @param path
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue procureValue(String path, int pos){
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString())
		}
		if (xmlContent.hasValue(path, locale, pos)) {
			xmlContent.getValue(path, locale, pos)
		} else {

			// Si no existe aseguramos que el padre exista
			if (CmsXmlUtils.isDeepXpath(path)) {
				String parentPath = CmsXmlUtils.createXpath(CmsXmlUtils.removeLastXpathElement(path), 1);
				procureValue(parentPath, obtainNodeIndex(parentPath))
			}

			// Comprobamos que al crear el padre no se haya creado automaticamente el hijo
			if (xmlContent.hasValue(path, locale, pos)) {
				xmlContent.getValue(path, locale, pos)
			} else {
				xmlContent.addValue(cmso, path, locale, pos);
			}
		}
	}

	private int obtainNodeIndex(String path) {
		int index = CmsXmlUtils.getXpathIndexInt(path)
		if (index > 0) {
			index = index - 1;
		}
	}
/**
 * Asegura un valor en el path indicado, devolviendo uno existente o creandolo
 * @param path
 */
	public I_CmsXmlContentValue procureValue(String path){
		procureValue(path, 0)
	}

	/**
	 * Set value for element path.
	 * @param path
	 * @param value
	 * @return
	 */
	public def setStringValue(String path, String value){
		setStringValue(path, value, 0)
	}

	/**
	 * Set value for element path. If the element does not exist, procure new element and set value.
	 * @param path
	 * @param value
	 * @param pos
	 * @return
	 */
	public def setStringValue(String path, String value, int pos){
		I_CmsXmlContentValue content = procureValue(path, pos)
		content.setStringValue(cmso, value)
		return this
	}

	/**
	 * Create a new element at last position and set value for element path.
	 * @param path
	 * @param value
	 * @param pos
	 * @return
	 */
	public def appendStringValue(String path, String value){
		setStringValue(path, value, count(path))
		return this
	}

	/**
	 * Append category at last position.
	 * @param path
	 * @param value
	 * @return
	 */
	public def appendCategoryStringValue(String path, String value){
		String newVal = getStringValue(path) + "," + value;
		setStringValue(path, newVal, 0);
		return this
	}

	/**
	 * Set value for element path. If the element does not exist, procure new element and set value.
	 * @param path
	 * @param value
	 * @param pos
	 * @return
	 */
	public def setStringValueIdx(String pathWithIdx, String value){
		I_CmsXmlContentValue content = procureValueIdx(pathWithIdx)
		content.setStringValue(cmso, value)
		return this
	}

	/**
	 * Filling the content with empty tags
	 * @param tag
	 * @param limit
	 */
	public void fillWithEmptyValues(String tag, int limit){
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString())
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
	public def saveStr(){
		cmso.lockResource(path)
		update(strContent)
		cmso.writeFile(file)
		cmso.unlockResource(path)
		this
	}

	/**
	 * Saving file changes from String content.
	 * For updating xml content execute unmarshall.
	 */
	public def saveStr(String contents){
		cmso.lockResource(path)
		update(contents)
		cmso.writeFile(file)
		cmso.unlockResource(path)
		this
	}

	/**
	 * Saving file from Xml content changes.
	 * For updating string content execute marshall.
	 */
	public def saveXml(){
		cmso.lockResource(path)
		file.setContents(xmlContent.marshal())
		cmso.writeFile(file)
		cmso.unlockResource(path)
		this
	}

	public def repair(){
		xmlContent.setAutoCorrectionEnabled(true);

		// now correct the XML
		xmlContent.correctXmlStructure(cmso);

		// Prepare for write
		xmlContent.getHandler().prepareForWrite(cmso, xmlContent, file);

		this
	}

	/**
	 * Returns String value from OpenCmsString element
	 * @param tag
	 * @return
	 */
	public String getStringValue(String tag) {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString())
		}
		xmlContent.getStringValue(cmso, tag, locale)
	}

	/**
	 * Returns String value from OpenCmsHtml element
	 * @param tag
	 * @return
	 */
	public String getHtmlStringValue(String element) {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString())
		}
		String value = null;
		if (xmlContent.hasValue(element, locale)){
			value = xmlContent.getValue(element, locale).getStringValue(cmso)
		}
		return value
	}

	/**
	 * Check if the content has value for element
	 * @param tag
	 * @return
	 */
	public boolean hasValue(String element) {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString())
		}
		xmlContent.hasValue(element, locale)
	}

	/**
	 * Check if the content has value for element in the position
	 * @param tag
	 * @param pos
	 * @return
	 */
	public boolean hasValue(String element, int pos) {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString())
		}
		xmlContent.hasValue(element, locale, pos)
	}

	/**
	 * Add value to content definition
	 * @param element
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue addValue(String element, int pos) {
		xmlContent.addValue(cmso, element, locale, pos);
	}

	/**
	 * Check if the content has value for element in the position
	 * @param element
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue getValue(String element, int pos) {
		if (xmlContent == null) {
			throw new Exception("xml content not exists for resource $path".toString())
		}
		return xmlContent.getValue(element, locale, pos)
	}

	/**
	 * Check if the content has value for element in the position
	 * @param element
	 * @param pos
	 * @return
	 */
	public I_CmsXmlContentValue getValue(String element) {
		return getValue(element, locale, 0)
	}



	/**
	 * Check if exists xmlPath content
	 * @param xmlPath
	 * @param pos
	 * @return
	 */
	public boolean contains(String xmlPath, int pos) {
		xmlContent.getValue(xmlPath, locale, pos) != null;
	}

	/**
	 * Check if exists xmlPath content
	 * @param xmlPath
	 * @return
	 */
	public boolean contains(String xmlPath) {
		contains(xmlPath, 0)
	}

	/**
	 * Check if exists xmlPath content
	 * @param xmlPath
	 * @return
	 */
	public int count(String xmlPath) {
		xmlContent.getIndexCount(xmlPath, locale)
	}

	/**
	 * Return map composed by resource identification and content:
	 * <structureId@rootPath: <path1: value1, path2: value2, ...>>
	 * by: rtinoco
	 * @return
	 */
	public Map<String, Map<String, String>> export(){
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
					String onlineLink = OpenCms.getLinkManager().getOnlineLink(cmso, value.getStringValue(cmso))
					content.put(elemPath, onlineLink);
				} else {
					content.put(elemPath, value.getStringValue(cmso));
				}
			}
		}

		CmsResource resource = cmso.readResource(path)
		exp.put(resource.getStructureId().getStringValue() + "@" + resource.getRootPath(), content);
		return exp;
	}

	/**
	 * Return list of elements names
	 * @return
	 */
	public List<String> elemNames(){
		return xmlContent.getNames(locale)
	}




	/**
	 * Return escape HTML block
	 * @param s
	 * @return
	 */
	public static String escapeHTML(String s) {
		return StringEscapeUtils.escapeHtml4(s)
	}

	/**
	 * Return last path value and ensure parent path and brother elements exist
	 * @param pathWithIndex Content[2]/Image[4]/Link
	 * @return
	 */
	public I_CmsXmlContentValue procureValueIdx(String pathWithIndex) {
		return procureValueIdx(new Path(pathWithIndex));
	}

	/**
	 * Return last element value. Ensure parent elements and brother elements exist
	 * @param path
	 * @return
	 */
	public I_CmsXmlContentValue procureValueIdx(Path path) {
		I_CmsXmlContentValue value;
		for (int elemPos = 0; elemPos < path.elems.size(); elemPos++) {
			for (int idx = 1; idx <= path.elems[elemPos].idx; idx++) {
				value = procureValueIdx(path.elems[elemPos], idx - 1);
			}
		}
		return value;
	}

	/**
	 * Procure value for element path and position.
	 *
	 * @param elemPath Absolute element path
	 * @param pos Element Index less 1
	 * @return
	 */
	public I_CmsXmlContentValue procureValueIdx(Elem elem, int pos) {
		String elemPath = elem.absElemPathWitoutLastIdx()
		if (hasValue(elemPath, pos)) {
			return getValue(elemPath, pos)
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
	String generatePathWithIndex(String cleanPath, List tarPattIdxs, List srcPattIdxs, List srcIdxValues) {
		Path path = new Path(cleanPath)
		for (int i = 0; i < srcPattIdxs.size(); i++) {
			path.elems[tarPattIdxs[i]].idx = srcIdxValues[srcPattIdxs[i]];;
		}
		return path.toString();
	}

	/**
	 * Generate path for mapping content
	 * @param cleanPath
	 * @param srcIdxValues
	 * @return
	 */
	String generatePathWithIndex(String cleanPath, List srcIdxValues) {
		Path path = new Path(cleanPath)
		for (int i = 0; i < srcIdxValues.size(); i++) {
			path.elems[i].idx = srcIdxValues[i];
		}
		return path.toString();
	}

	/**
	 * Path y Elem classes emulate elems path with index. For example:
	 * Path: Content[1]/Text[2]
	 * Elem: Content[1]
	 */
	class Path{
		String absPath;
		List<Elem> elems;
		Path(String path){
			absPath = path;
			elems = [];
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
				String idx = 1.toString();
				if (elemPath.contains("[")){
					int idxBegin = elemPath.indexOf("[")
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
		List indexes(String pathWithIdx){
			return pathWithIdx.split('/').collect {
				CmsXmlUtils.getXpathIndexInt(it) };
		}

		List indexes(){
			List idxs = []
			for (int i = 0; i < elems.size(); i++) {
				idxs.add(elems[i].idx)
			}
			return idxs;
		}

		String toString(){
			String res = elems[0].toString();
			for (int i = 1; i < elems.size(); i++) {
				res = res + "/" + elems[i].toString()
			}
			return res;
		}
	}
	class Elem{
		int idx;
		String elemPath;
		String parent;

		Elem(Integer idx, String path, String parent){
			this.idx = idx;
			this.elemPath = path;
			this.parent = parent;
		}

		Elem(String idx, String path, String parent){
			this.idx = idx.toInteger();
			this.elemPath = path;
			this.parent = parent;
		}

		/**
		 * Return absolute element path
		 * @return
		 */
		String absElemPath(){
			if (parent) {
				return "$parent/$elemPath[$idx]"
			} else {
				return "$elemPath[$idx]"
			}
		}

		/**
		 * Return absolute element path without last index
		 * @return
		 */
		String absElemPathWitoutLastIdx(){
			if (parent) {
				return "$parent/$elemPath"
			} else {
				return "$elemPath"
			}
		}

		String toString(){
			return "$elemPath[$idx]"
		}
	}
}