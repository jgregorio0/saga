package com.saga.opencms.util

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.opencms.file.CmsObject
import org.opencms.flex.CmsFlexController
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.staticexport.CmsLinkManager

import javax.annotation.Nullable
import javax.servlet.ServletRequest

public class SgLink {

	private static final Log LOG = CmsLog.getLog(this.getClass());

	private static String relativizeSchema(Boolean relScheme, String link) {
		String linkRel = link;
		if (relScheme != null && relScheme) {
			// Si contiene esquema (http, https, etc)
			if(OpenCms.getLinkManager().hasScheme(linkRel)){
				linkRel = linkRel.substring(linkRel.indexOf(":") + 1);
			}

			if (!linkRel.startsWith("//")) {
				if (!linkRel.startsWith("/")){
					linkRel = "/" + linkRel;
				}
				linkRel = "/" + linkRel;
			}
		}
		return linkRel;
	}

	private static CmsObject customizeCmsObject(CmsObject cms, String baseUri, String baseSite) {
		CmsObject cmso = cms;
		boolean isCustomUri = StringUtils.isNotBlank(baseUri);
		boolean isCustomSite = StringUtils.isNotBlank(baseSite);
		if (isCustomSite || isCustomUri) {
			try {
				cmso = OpenCms.initCmsObject(cmso);
				if (isCustomSite) {
					cmso.getRequestContext().setSiteRoot(baseSite);
				}
				if (isCustomUri) {
					cmso.getRequestContext().setUri(baseUri);
				}
			} catch (CmsException e) {
				LOG.error("customizing cms objecto using baseUri: " + baseUri + " and baseSite: " + baseSite, e);
			}
		}
		return cmso;
	}

	/**
	 *
	 * @param req
	 * @param target
	 * @param baseUri
	 * @param baseSite
	 * @param secure
	 * @param relScheme
	 * @return
	 */

	public static String link(ServletRequest req, String target, @Nullable String baseUri, @Nullable String baseSite, @Nullable Boolean secure, @Nullable Boolean relScheme) {

		CmsFlexController controller = CmsFlexController.getController(req);

		// be sure the link is absolute
		String uri = CmsLinkManager.getAbsoluteUri(target, controller.getCurrentRequest().getElementUri());
		CmsObject cms = controller.getCmsObject();

		// Customize cms object
		cms = customizeCmsObject(cms, baseUri, baseSite);

		// Choose secure (http or https)
		secure = secure != null ? secure : false;

		// Get link
		String link = OpenCms.getLinkManager().substituteLinkForUnknownTarget(cms, uri, secure);

		// Protocolo relativo
		link = relativizeSchema(relScheme, link);

		return link;
	}
}