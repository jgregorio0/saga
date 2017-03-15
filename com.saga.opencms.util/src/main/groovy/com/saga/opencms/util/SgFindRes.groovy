package com.saga.opencms.util

import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.main.OpenCms
import org.opencms.relations.CmsRelationFilter

/**
 * Find resources
 * JSON:
 * {
 *     source: {
 *         path: "/sites/default/" // Path from start searching. If it is a folder it looks for many resources. If path is resource search only for resource.
 *         filter: {
 *             type: "containerpage" // Resource type
 *         }
 *         recursive: true // Find recursive from folder path (If path is resource it would never be recursive)
 *     }
 * }
 */
public class SgFindRes {
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
		CmsResourceFilter resFilter = getPreConfigFilter(filter?.id)

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

	private CmsResourceFilter getPreConfigFilter(String id) {
		switch (id){
			case "ALL":
				return CmsResourceFilter.ALL;
			case "ALL_MODIFIED":
				return CmsResourceFilter.ALL_MODIFIED;
			case "DEFAULT":
				return CmsResourceFilter.DEFAULT;
			case "DEFAULT_FILES":
				return CmsResourceFilter.DEFAULT_FILES;
			case "DEFAULT_FOLDERS":
				return CmsResourceFilter.DEFAULT_FOLDERS;
			case "DEFAULT_ONLY_VISIBLE":
				return CmsResourceFilter.DEFAULT_ONLY_VISIBLE;
			case "IGNORE_EXPIRATION":
				return CmsResourceFilter.IGNORE_EXPIRATION;
			case "ONLY_VISIBLE":
				return CmsResourceFilter.ONLY_VISIBLE;
			case "ONLY_VISIBLE_NO_DELETED":
				return CmsResourceFilter.ONLY_VISIBLE_NO_DELETED;
			default:
				return CmsResourceFilter.ALL;
		}
	}
	public static final CmsResourceFilter ONLY_VISIBLE_NO_DELETED = ONLY_VISIBLE.addExcludeState(
			CmsResource.STATE_DELETED);
}