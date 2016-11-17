package com.saga.opencms.util;

import org.opencms.file.CmsObject;
import org.opencms.flex.CmsFlexController;
import org.opencms.main.OpenCms;
import org.opencms.staticexport.CmsLinkManager;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jgregorio on 08/05/2015.
 */
public class CmsLinkUtil {

    private HttpServletRequest request;

    public CmsLinkUtil(HttpServletRequest request) {
        this.request = request;
    }

    public String getLink (String target) {
        CmsFlexController controller = CmsFlexController.getController(request);
        // be sure the link is absolute
        String uri = CmsLinkManager.getAbsoluteUri(target, controller.getCurrentRequest().getElementUri());
        CmsObject cms = controller.getCmsObject();

        // generate the link
        return OpenCms.getLinkManager().substituteLinkForUnknownTarget(cms, uri);
    }

}
