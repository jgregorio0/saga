package com.saga.opencms.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import groovy.json.JsonBuilder
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.opencms.file.CmsObject
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.search.CmsSearchException
import org.opencms.search.CmsSearchResource
import org.opencms.search.solr.CmsSolrQuery
import org.opencms.search.solr.CmsSolrResultList
import org.opencms.util.CmsRequestUtil

public class SgMustache {

	private CmsObject cmso;

	public MustacheUtil(CmsObject cmso) {
		this.cmso = cmso;
	}

	/**
	 * Obtenemos el contenido segun indica la plantilla Mustache
	 * y el valor de los campos de nuestro contexto (ctx)
	 * @param templatePath
	 * @param ctx
	 * @return
	 * @throws CmsException
	 * @throws java.io.UnsupportedEncodingException
	 */
	public String process(String templatePath, Map<String, Object> ctx)
			throws CmsException, UnsupportedEncodingException {
		// Cargamos los contenidos de la plantilla
		byte[] contents = cmso.readFile(templatePath).getContents();
		String contentStr = new String(contents, "UTF-8");

		// Creamos el objeto Mustache con la plantilla y el contexto
		DefaultMustacheFactory defaultMustacheFactory = new DefaultMustacheFactory();
		StringWriter sw = new StringWriter();
		Mustache d = defaultMustacheFactory.compile(new StringReader(contentStr), "def");
		d.execute(sw, ctx);
		sw.flush();
		return sw.toString();
	}
}