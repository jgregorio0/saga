package com.saga.opencms.scripts
import com.saga.sagasuite.scriptgroovy.util.*
import com.saga.sagasuite.scripts.SgReportManager
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpStatus
import org.opencms.file.CmsObject
/**
 * Created by jgregorio on 15/09/2015.
 */
class SitemapLocExists {

	final dateFormat = "yyyy-MM-dd_HH-mm-ss";
	final basePath = "/system/scripts-logs/";
	final fileName = "-sitemap-loc-exists.csv";

	CmsObject cmso;
	String url;
	String idProceso;
	String site;
	Locale locale;
	SgLog log;

	public SitemapLocExists(){
	}

	public void init(def cms, String idProceso, String url) {

		this.cmso = cms;
		this.idProceso = idProceso;
		this.url = url;

		this.site = cmso.getRequestContext().getSiteRoot();
		this.locale = cmso.getRequestContext().getLocale();

		this.log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());

		run()
	}

	def run(){
		try {

			// Inicializamos la respuesta
			StringBuffer res = new StringBuffer();
			res.append("URL;CODE");
			if (validate()) {

				log.add("Obtenemos el contenido de subsitemap");
				SgNet net = new SgNet();
				net.get(url)

				log.add("Respuesta (${net.getResCode()})");
				log.add(StringEscapeUtils.escapeHtml4(net.getResStr()));

				if (net.getResCode() != HttpStatus.SC_OK) {
					throw new Exception("ERROR executing get to $url");
				}

				// Obtenemos los loc
				SgSlurper slurper = new SgSlurper(net.getResStr());
				GPathResult xml = slurper.slurpXml();
				def locs = xml.depthFirst().findAll{
					it.name() == 'loc'
				}

				int total = locs.size();
				for (int i = 0; i < locs.size(); i++) {
					try {
						def loc = locs.get(i);
						int count = i+1;
						log.loop(i, total)
								.add("$count/$total -- Para el recurso $loc");

						String locUrl = loc.toString();
						net.get(locUrl);

						res.append("\n$locUrl;${net.getResCode()}");

						if (net.getResCode() != HttpStatus.SC_OK) {
							throw new Exception("ERROR executing GET for url $locUrl returns error: ${net.getResCode()}");
						}

						log.add("OK");
					} catch (Exception e) {
						log.error(e).print();
					}
				}
			}
			log.print();

			// Creamos recurso con las comprobaciones
			String date = Calendar.getInstance().format(dateFormat);
			String path = basePath + date + fileName;
			new SgCms(cmso).createResource(path, "plain");
			new SgCnt(cmso, path).saveStr(res.toString());

			log.add("Creado fichero con los resultados de la comprobación en $path").print();
		} catch (Exception e) {
			log.error(e).print();
		}
	}

	/**
	 * Comprueba que los parametros sean correctos
	 * @return
	 */
	private boolean validate() {
		boolean validate = true;

		boolean valSitemapPath = !StringUtils.isEmpty(url);
		if (!valSitemapPath) {
			throw new IllegalArgumentException("La ruta '$url' no es válida");
		}
		validate = validate && valSitemapPath;

		return validate;
	}

	public class WarnException extends Exception {
		public WarnException() {
			super();
		}
		public WarnException(String s) {
			super(s);
		}
	}
}