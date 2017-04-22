package com.saga.opencms.util

import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsPropertyDefinition
import org.opencms.file.CmsResource
import org.opencms.flex.CmsFlexController
import org.opencms.loader.I_CmsResourceLoader
import org.opencms.main.CmsException
import org.opencms.main.OpenCms

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public class SgJspInclude {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Locale locale;
	private CmsFlexController controller;
	private CmsObject cmso;


	public CmsJspIncludeUtil(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		controller = CmsFlexController.getController(request);
		cmso = controller.getCmsObject();
		locale = cmso.getRequestContext().getLocale();
	}

	/**
	 * Incluye la lista de parametros recibida en el request, solo en caso de
	 * que no esten definidos ya, y llama a la JSP que se indica.
	 * Devuelve la respuesta de la JSP a la que se ha llamado.
	 * @param jspFileName
	 * @param params
	 * @return
	 * @throws CmsException
	 * @throws ServletException
	 * @throws java.io.IOException
	 */
	public String obtainJspResponse(String jspFileName, List<String[]> params)
			throws CmsException, ServletException, IOException {
		if (!(jspFileName != null && jspFileName.length() > 0)) {
			throw new IllegalArgumentException("ERROR JspIncludeUtil.obtainJspResponse:" +
					" parametro jspFileName incorrecto");
		}

		// Incluimos los parametros en el request
		if (params != null && params.size() > 0) {
			for (String[] param : params) {

				if (param.length == 2
						&& !existsAttribute(param[0])) {
					request.setAttribute(param[0], param[1]);
				}
			}
		}

		CmsResource cmsResource = cmso.readResource(jspFileName);

		// get the loader for the requested file
		I_CmsResourceLoader loader =
				OpenCms.getResourceManager().getLoader(cmsResource);
		String element = null;
		CmsFile file = cmso.readFile(jspFileName);

		// get the bytes from the loader and convert them to a String
		byte[] result = loader.dump(
				cmso,
				file,
				element,
				locale,
				request,
				response);

		// use the encoding from the property or the system default if not available
		String encoding = cmso.readPropertyObject(
				file, CmsPropertyDefinition.PROPERTY_CONTENT_ENCODING, true)
				.getValue(OpenCms.getSystemInfo().getDefaultEncoding());

		// If the included target issued a redirect null will be returned from loader
		if (result == null) {
			result = new byte[0];
		}

		return new String(result, encoding);
	}

	private boolean existsAttribute(String key) {
		return request.getAttribute(key) != null;
	}
}