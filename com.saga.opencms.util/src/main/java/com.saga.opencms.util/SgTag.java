package com.saga.opencms.util;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.flex.CmsFlexController;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import javax.servlet.http.HttpServletRequest;

/**
 *  Based on org.opencmshispano.module.resources.manager developed by sraposo
 */

public class SgTag {
	private static final Log LOG = CmsLog.getLog(SgTag.class);

	public static String resourcePathLookBackTag(HttpServletRequest request, String filename){
		String path = "";
		try {
			final CmsObject cmso = CmsFlexController.getCmsObject(request);
			String uri = cmso.getRequestContext().getUri();
			String folder = CmsResource.getFolderPath(uri);
			LOG.debug("uri: " + uri + " folder: " + folder);
			path = findResourcePath(cmso, folder, filename);

		} catch (Exception e) {
			LOG.error("ERROR resourcePathLookBackTag: ", e);
		}
		return path;
	}

	/**
	 * Return file path if exists into current folder or any parent folder.
	 * Returns null if resource not exists
	 * @param folder
	 * @param filename
	 * @return
	 */
	private static String findResourcePath(CmsObject cmso, String folder, String filename){
		// Limite en caso de que no encuentre el fichero
		if (folder == null) {
			return "";
		}

		// si existe el fichero devolvemos el path formado por la ruta y el nombre
		String filepath = CmsStringUtil.joinPaths(folder, filename);
		if (cmso.existsResource(filepath)){
			LOG.debug("SEARCHING: " + filepath + " -- OK");
			return filepath;
		} else {
			LOG.debug("SEARCHING: " + filepath + " -- NOT FOUND");

			// si no existe buscamos en la carpeta padre
			String parentFolder = CmsResource.getParentFolder(folder);
			return findResourcePath(cmso, parentFolder, filename);
		}
	}
}