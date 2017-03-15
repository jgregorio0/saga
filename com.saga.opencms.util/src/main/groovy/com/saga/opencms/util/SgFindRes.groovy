package com.saga.opencms.util
import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.main.OpenCms
/**
 * Find resources
 * JSON:
 * {
 *     source: {
 *         path: "/sites/default/" // Obligatorio. Path from start searching. If it is a folder it looks for many resources. If path is resource search only for resource.
 *         filter: {
 *             type: "containerpage" // Opcional. Resource type
 *             impl: "ALL" // Opcional.
 *         }
 *         recursive: true // Find recursive from folder path (If path is resource it would never be recursive)
 *     }
 * }
 */
public class SgFindRes {

	public static enum Impls {ALL, ALL_MODIFIED, DEFAULT, DEFAULT_FILES,
		DEFAULT_FOLDERS, DEFAULT_ONLY_VISIBLE,
		IGNORE_EXPIRATION, ONLY_VISIBLE, ONLY_VISIBLE_NO_DELETED};

	CmsObject cmso;
	String jsonStr;
	def json;


	public SgFindRes(CmsObject cmso, String jsonStr){
		this.cmso = cmso;
		this.jsonStr = jsonStr;
		this.json = new JsonSlurper().parseText(jsonStr);
	}

	/**
	 * Find resources for the given config
	 * @return
     */
	public List<CmsResource> find() {
		// Validate json
		List<CmsResource> resources = [];
		if (validate()) {

			// load filter
			CmsResourceFilter filter = loadFilter()

			// Check if serching in depth
			boolean recursive = false;
			if (json.source.recursive) {
				recursive = true;
			}

			// Find resources
			resources = findResources(json.source.path, filter, recursive)
		}
		return resources;
	}

	/**
	 * Find resources from path allowed by filter
	 * @param path If it is a folder it will search for many resources. If it is a resource it search only for one resource.
	 * @param filter
     * @return
     */
	List<CmsResource> findResources(String path, CmsResourceFilter filter, boolean inDepth) {
		def resources = [];
		if (CmsResource.isFolder(path)) {
			resources = cmso.readResources(path, filter, inDepth);
		} else {
			resources.add(cmso.readResource(path), filter);
		}

		return resources;
	}

	/**
	 * Load filter from json config
	 * @return
     */
	CmsResourceFilter loadFilter() {
		def filter = json.source.filter
		CmsResourceFilter resFilter = getPreConfigFilter(filter?.impl)

		// Resource type
		if (filter?.type) {
			def type = OpenCms.getResourceManager().getResourceType(filter.type)
			resFilter.addRequireType(type);
		}

		//TODO añadir otros filtros
	}

	boolean validate() {
		def source = json.source;
		if (StringUtils.isBlank(source)
				|| StringUtils.isBlank(source.path)) {
			throw new Exception("ERROR source and source.path are required");
		}
		return true;
	}

	private CmsResourceFilter getPreConfigFilter(String impl) {
		switch (impl){
			case Impls.ALL:
				return CmsResourceFilter.ALL;
			case Impls.ALL_MODIFIED:
				return CmsResourceFilter.ALL_MODIFIED;
			case Impls.DEFAULT:
				return CmsResourceFilter.DEFAULT;
			case Impls.DEFAULT_FILES:
				return CmsResourceFilter.DEFAULT_FILES;
			case Impls.DEFAULT_FOLDERS:
				return CmsResourceFilter.DEFAULT_FOLDERS;
			case Impls.DEFAULT_ONLY_VISIBLE:
				return CmsResourceFilter.DEFAULT_ONLY_VISIBLE;
			case Impls.IGNORE_EXPIRATION:
				return CmsResourceFilter.IGNORE_EXPIRATION;
			case Impls.ONLY_VISIBLE:
				return CmsResourceFilter.ONLY_VISIBLE;
			case Impls.ONLY_VISIBLE_NO_DELETED:
				return CmsResourceFilter.ONLY_VISIBLE_NO_DELETED;
			default:
				return CmsResourceFilter.ALL;
		}
	}
}