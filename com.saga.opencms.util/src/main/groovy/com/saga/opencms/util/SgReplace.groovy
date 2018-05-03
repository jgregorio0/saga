package com.saga.opencms.util

import org.opencms.file.CmsObject
import org.opencms.file.CmsResource

import java.util.regex.Pattern

public class SgReplace {

	static def uuidBlock = "<uuid>.*</uuid>"
	static def uuidTags = "<uuid>|</uuid>"
	static def targetBlock = "<target>.*</target>"
	static def targetTags = "<target>|</target>"


	CmsObject cmso;
	String content;

	/**
	 * Initialize content.
	 * The resource must be locked by user before change content.
	 * Plain objects have no xmlContent but content is still readable.
	 * @param cmso
	 * @param resourcePath
	 * @param locale
	 */
	public SgReplace(CmsObject cmso, String content){
		this.cmso = cmso
		this.content = content
	}

	/**
	 * Replace all matches origin by replacement regex or literal string
	 * @param origin
	 * @param replacement
	 * @param quote If the keys will be quoted to avoid regex expresions
	 * @return
	 */
	public def replaceAll(String origin, String replacement, boolean quote) {
		if (quote) {
			replaceAll(Pattern.quote(origin), replacement)
		} else {
			replaceAll(origin, replacement)
		}
		this
	}

	/**
	 * Replace first match origin by replacement regex or literal string
	 * @param origin
	 * @param replacement
	 * @param quote If the keys will be quoted to avoid regex expresions
	 * @return
	 */
	public def replaceFirst(String origin, String replacement, boolean quote) {
		if (quote) {
			replaceFirst(Pattern.quote(origin), replacement)
		} else {
			replaceFirst(origin, replacement)
		}
		this
	}

	/**
	 * Replace all matches origin by replacement string
	 * @param origin
	 * @param replacement
	 * @return
	 */
	public def replaceAll(String origin, String replacement) {
		content = content.replaceAll(origin, replacement)
		this
	}

	/**
	 * Replace first match origin by replacement string
	 * @param origin
	 * @param replacement
	 * @return
	 */
	public def replaceFirst(String origin, String replacement) {
		content = content.replaceFirst(origin, replacement)
		this
	}

	/**
	 * Replace first match origin by replacement string, begining from locale
	 * @param origin
	 * @param replacement
	 * @param locale
	 * @return
	 */
	public def replaceFirst(String origin, String replacement, String locale) {
		String locContent = findLocale(locale)
//		int iLoc = content.indexOf(generateLocaleBlock(locale))
		if (locContent != null && !locContent.isEmpty()) {

			String oldContent = locContent
			String newContent = oldContent.replaceFirst(origin, replacement)
			replaceFirst(oldContent, newContent, true)
		}
		this
	}

	/**
	 * Replace first match origin by replacement string, begining from locale
	 * @param origin
	 * @param replacement
	 * @param quote
	 * @param locale
	 * @return
	 */
	public def replaceFirst(String origin, String replacement, boolean quote, String locale) {
		if (quote) {
			origin = Pattern.quote(origin)
		}
		replaceFirst(origin, replacement, locale)
	}


/**
 * Replace each key for value in all matches content string
 * @param map Contains keys to findAndReplace for values
 * @return
 */
	public def replaceAll(Map map) {
		map.each{
			replaceAll(it.key, it.value)
		}
		this
	}

	/**
	 * Replace each key for value in all matches content string
	 * @param map Contains keys to findAndReplace for values
	 * @param quote If the keys will be quoted to avoid regex expresions
	 * @return
	 */
	public def replaceAll(Map map, boolean quote) {
		map.each{
			replaceAll(it.key, it.value, quote)
		}
		this
	}

	/**
	 * Replace each key for value in only first match content string
	 * @param map Contains keys to findAndReplace for values
	 * @param quote If the keys will be quoted to avoid regex expresions
	 * @return
	 */
	public def replaceFirst(Map map, boolean quote) {
		map.each{
			replaceFirst(it.key, it.value, quote)
		}
		this
	}

	/**
	 * Replace each key for value in only first match content string
	 * @param map Contains keys to findAndReplace for values
	 * @param quote If the keys will be quoted to avoid regex expresions
	 * @return
	 */
	public def replaceFirst(Map map, boolean quote, String locale) {
		map.each{
			replaceFirst(it.key, it.value, quote, locale)
		}
		this
	}

	/**
	 * Find blocks that match with eachMatchpattern and
	 * extract origin string by removing cleanPattern.
	 * The result origin Strings are replaced by destination.
	 * If pattern returns list of strings, only the fist element would be treated.
	 * @param eachMatchPattern
	 * @param cleanPattern
	 * @param origin
	 * @param destination
	 * @param quote If the origin contains regex symbols must be quoted to avoid regex substitutions
	 * @return
	 */
	public def findReplaces(
			String eachMatchPattern, String cleanPattern,
			String origin, String destination, boolean quote) {

		// Obtain strings to substitute
		def subs = [:]
		content.eachMatch(eachMatchPattern, {
			String oldBlock = null;
			String oldStr = null;

			// Difference between String and List case
			if (String.isInstance(it)){
				oldBlock = it
				oldStr = cleanPattern != null ? it.replaceAll(cleanPattern, "") : it
			} else {
				oldBlock = it[0]
				oldStr = cleanPattern != null ? it[0].replaceAll(cleanPattern, "") : it[0]
			}

			// Get new String for findAndReplace
			// Origin can be regex or literal
			if (quote) {
				origin = Pattern.quote(origin)
			}
			String newStr = oldStr != null ?
					oldStr.replaceAll(origin, destination)
					: null

			if (newStr != null && !oldStr.equals(newStr)) {

				// Using quoted blocks to avoid nested string substitutions
				String newBlock = oldBlock.replaceAll(oldStr, newStr)
				subs.put(oldBlock, newBlock)
			}
		})

		// Replace all strings obtained
//		if (subs.size() > 0) {
//			replaceAll(subs, true)
//		}

		return subs
	}

	public def findReplaces(String origin, String destination, boolean quote) {

		// Obtain strings to substitute
		def subs = [:]

		String originSub = origin;
		if (quote) {
			originSub = Pattern.quote(origin)
		}
		content.eachMatch(origin, {
			String oldStr = null;

			// Difference between String and List case
			if (String.isInstance(it)){
				oldStr = it
			} else {
				oldStr = it[0]
			}

			String newStr = oldStr != null ?
					oldStr.replaceAll(originSub, destination)
					: null

			if (newStr != null && !oldStr.equals(newStr)) {

				// Using quoted blocks to avoid nested string substitutions
				subs.put(oldStr, newStr)
			}
		})

		return subs
	}

	/**
	 * Find blocks that match with eachMatchpattern and removes cleanPattern.
	 * Each result is takes as key and replaced by its value matching the map parametter.
	 * If blocks return list of list, only the fist element would be treated.
	 * @param eachMatchPattern
	 * @param cleanPattern
	 * @param quote If the keys will be quoted to avoid regex expresions
	 * @return
	 */
	public def findReplaces(
			String eachMatchPattern, String cleanPattern,
			Map origin2Destination, boolean quote) {

		// Obtain strings to substitute
		def subs = [:]
		content.eachMatch(eachMatchPattern, {
			String oldBlock = null;
			String oldStr = null;

			// Difference between String and List case
			if (String.isInstance(it)){
				oldBlock = it
				oldStr = cleanPattern != null ? it.replaceAll(cleanPattern, "") : it
			} else {
				oldBlock = it[0]
				oldStr = cleanPattern != null ? it[0].replaceAll(cleanPattern, "") : it[0]
			}

			// Get new String for findAndReplace
			String newStr = origin2Destination.get(oldStr)
			if (newStr != null && quote) {
				newStr = Pattern.quote(newStr)
			}

			if (newStr != null && !oldStr.equals(newStr)) {

				// Using blocks to avoid nested string substitutions
				String newBlock = oldBlock.replaceAll(oldStr, newStr)
				subs.put(oldBlock, newBlock)
			}
		})

		// Replace all strings obtained
//		if (subs.size() > 0) {
//			replaceAll(subs)
//		}

		subs
	}

	/**
	 * Find blocks that match with eachMatchpattern and removes cleanPattern.
	 * If blocks return list of list, only the fist element would be treated.
	 * @param eachMatchPattern
	 * @param cleanPattern
	 * @return
	 */
	public def findAll(String eachMatchPattern, String cleanPattern) {

		// Obtain strings to substitute
		def list = []
		content.eachMatch(eachMatchPattern, {
			String oldBlock = null;
			String oldStr = null;

			// Difference between String and List case
			if (String.isInstance(it)){
				oldBlock = it
				oldStr = cleanPattern != null ? it.replaceAll(cleanPattern, "") : it
			} else {
				oldBlock = it[0]
				oldStr = cleanPattern != null ? it[0].replaceAll(cleanPattern, "") : it[0]
			}

			list.add(oldStr)
		})

		list
	}

	/**
	 * Find <target> and <uuid> blocks for old path and return map to findAndReplace.
	 * @param pathOld
	 * @param pathNew
	 * @return
	 */
	public def findTarget(String pathOld, String pathNew){
		def subs = [:]

		// Iniciamos el bloque buscado
		String wantedBlock = generateTargetBlock(pathOld)

		String uuidNew = cmso.readResource(relativize(pathNew))
				.getResourceId().toString();
		List<String> blocksOld = content.findAll(wantedBlock);
		for (String blockOld : blocksOld){
//			String blockOld = content.find(wantedBlock);
			if (blockOld != null && blockOld.length() > 0) {
				String targetBlockNew = null;
				String uuidBlockNew = null;

				// Agregamos a las sustituciones el path antiguo y nuevo
				String targetBlockOld = blockOld.find(targetBlock)
				if (targetBlockOld != null && targetBlockOld.length() > 0) {
					targetBlockNew = targetBlockOld.replaceAll(pathOld, pathNew)
//				subs.put(targetBlockOld, targetBlockNew)
//				blockNew.replaceFirst(Pattern.quote(targetBlockOld), targetBlockNew)
				}

				// Agregamos a las sustituciones el uuid antiguo y nuevo
				String uuidBlockOld = blockOld.find(uuidBlock)
				if (uuidBlockOld != null && uuidBlockOld.length() > 0) {
					String uuidOld = uuidBlockOld.replaceAll(uuidTags, "")
					uuidBlockNew = uuidBlockOld.replaceAll(uuidOld, uuidNew)
//				subs.put(uuidBlockOld, uuidBlockNew)
//				blockNew.replaceFirst(Pattern.quote(uuidBlockOld), uuidBlockNew)
				}

				// Si hemos encontrado sustituciones
				if (targetBlockNew != null) {
					String blockNew = blockOld.replaceFirst(Pattern.quote(targetBlockOld), targetBlockNew)
					if (uuidBlockNew != null) {
						blockNew = blockNew.replaceFirst(Pattern.quote(uuidBlockOld), uuidBlockNew)
					}
					subs.put(blockOld, blockNew)
				}
			}
		}


//		replaceAll(subs, true)

		return subs
	}

	/**
	 * Find all uuids for targets
	 * @param pathOld
	 * @param pathNew
	 * @return
	 */
	public def findTargetUuids(){
		def subs = [:]

		List<String> blocksOld = findAll(uuidBlock, null);
		blocksOld.each {
			if (it != null && it.length() > 0) {
				subs.put(it, "")
			}
		}

		return subs
	}

	/**
	 * Removes all uuids for all targets
	 * @return
	 */
	public def removeTargetUuids(){
		def subs = findTargetUuids()
		replaceAll(subs, true);
	}


/**
 * Devuelve el path sin el site
 * @param path
 * @return
 */
	private String relativize(String path) {
		String siteRoot = cmso.getRequestContext().getSiteRoot()
		String relName = path;
		if (relName.startsWith(siteRoot)) {
			relName = relName.substring(siteRoot.length());
		}
		relName
	}

	public static Map replace(CmsObject cmso, CmsResource r, String source, String replacement, boolean quoted){
		// Es necesario eliminar los uuids para que no repare los enlaces al final del proceso
		SgCnt xml = new SgCnt(cmso, r);
		SgReplace replace = SgReplace(cmso, xml.strContent);
		replace.removeTargetUuids();

		// Obtenemos y reemplazamos la cadena dada
		def subs = replace.findReplaces(source, replacement, quoted);
		replace.replaceAll(subs, true);

		// Guardamos el contenido
		xml.saveStr(replace.content);
		return subs;
	}

	/**
	 * Generate a block with target and uuid
	 * @param path
	 * @return
	 */
	public static String generateTargetBlock(String path) {
		return "<target>.*" +
				path.replace("-", "\\-") +
				".*</target>\\s*.*</uuid>";
	}

	/**
	 * Find initial tag for locale
	 * @param locale
	 * @return
	 */
	public String findInitLocaleTag(String locale) {
		return content.find("<\\w* language=\"$locale\">")
	}

	/**
	 * Find ending tag for locale
	 * @param locale
	 * @return
	 */
	public String findEndLocaleTag(String locale) {
		String initTag = findInitLocaleTag(locale)
		if (initTag != null) {
			int end = initTag.indexOf(" language")
			return "</" + initTag.substring(1, end) + ">"
		}
		return null
	}

	/**
	 * Find locale string content
	 * @param locale
	 * @return
	 */
	public String findLocale(String locale) {
		String init = findInitLocaleTag(locale)
		String end = findEndLocaleTag(locale)
		if (init != null && init.length() > 0 &&
				end != null && end.length() > 0) {
			int iInit = content.indexOf(init)
			int iEnd = content.indexOf(end, iInit) + end.length()
			if (iInit > -1 && iEnd > -1) {
				return content.substring(iInit, iEnd)
			}
		}
		return null
	}


	public static List<String> findAllHref(String content){
		def xml = new SgSlurper(content).slurpTagSoup();

		List<String> hrefs = xml.depthFirst().inject([]) { list, node ->
			if (node.@href != ""){
				list.add(node.@href.text())
			}
			list
		}

		return hrefs;
	}

	public static List<String> findAllSrc(String content){
		def xml = new SgSlurper(content).slurpTagSoup();

		List<String> srcs = xml.depthFirst().inject([]) { list, node ->
			if (node.@src != ""){
				list.add(node.@src.text())
			}
			list
		}

		return srcs;
	}
}