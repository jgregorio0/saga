package com.saga.opencms.util;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsResource;
import org.opencms.jsp.CmsJspNavElement;
import org.opencms.main.CmsLog;

/**
 * Created by jgregorio on 09/09/2016.
 * Ref: http://www.baeldung.com/httpclient-post-http-request
 *
 * Utility class for request using HTTP GET and POST methods
 */
public class SgNav {
	private static final Log LOG = CmsLog.getLog(SgNav.class);

	public String resourceNameLvl(CmsJspNavElement elem){
		String resNameLvl = null;
		int elemLvl = elem.getNavTreeLevel();
		String resName = elem.getResourceName();
		String[] resNameLevels = resName.split("/");
		for (int i = 1; i < elemLvl + 2; i++) {
			if (resNameLvl == null) {
				resNameLvl = "/";
			}
			resNameLvl += resNameLevels[i] + "/";
		}
		return resNameLvl;
	}

	public boolean isActiveSection(CmsJspNavElement elem, String uri){
		String resNameLvl = resourceNameLvl(elem);
		String parentFolderLvl = CmsResource.getParentFolder(resNameLvl);
		boolean isActiveSection = uri.startsWith(parentFolderLvl);
		return isActiveSection;
	}

	public boolean isParentItem(CmsJspNavElement elem, int startLvl){
		boolean isParentItem = elem.getNavTreeLevel() == startLvl;
		return isParentItem;
	}

	public boolean isMarkItem(CmsJspNavElement elem, String uri){
		String resPath = elem.getResourceName();
		if (elem.isNavigationLevel()) {
			resPath = elem.getParentFolderName();
		}
		boolean isMarkItem = uri.startsWith(resPath);
		return isMarkItem;
	}
}