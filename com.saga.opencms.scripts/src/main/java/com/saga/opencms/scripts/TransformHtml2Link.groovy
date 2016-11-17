package com.saga.opencms.scripts
import com.saga.sagasuite.scriptgroovy.util.SgLogUtil
import com.saga.sagasuite.scriptgroovy.util.SgReplaceUtil
import com.saga.sagasuite.scriptgroovy.util.SgXmlUtil
import com.saga.sagasuite.scripts.SgReportManager
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil

import java.util.regex.Pattern

class TransformHtml2Link {

	static String defPdf = "/sites/upo-sede-electronica/.galleries/descargas-perfil-de-contratante/descargas-expedientes/no-existe.pdf"

	CmsObject cmso;
	String path;
	boolean onlyCheck;
	String siteRoot;
	SgLogUtil log;

	public void init(def cmso, def idProceso, def path, def onlyCheck) {
		this.cmso = cmso;
		this.path = path;
		this.onlyCheck = onlyCheck;

		siteRoot = cmso.getRequestContext().getSiteRoot()

		/******* PROCESO ********/
		log = new SgLogUtil(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());

		execute()
	}

	public void execute() {
		try {


			String sourceFolder = path;
			String sourceType = "upocicexpediente";
			String downloadFolder = "/sites/upo-sede-electronica/.galleries/descargas-perfil-de-contratante/descargas-expedientes/";
			String pdfPattern = "[\\w/]*.pdf";
			String linkPattern = "[\\w/.:]*.html";
			String linkContratacion = "http://www.upo.es/contratacion";
			String oldSite = "/sites/sede-electronica/";

			// Obtenemos los recursos a modificar
			def resources = obtainResources(cmso, sourceFolder, sourceType)

			//Cargamos el porcentaje
			int total = resources.size()
			log.percentage(0, total)
					.add("Numero de elementos a tratar: $total")
					.print()

			resources.eachWithIndex{ res, i ->
				try {
					SgXmlUtil xml = new SgXmlUtil(cmso, res, "es")
					String id = obtainId(xml.getStringValue("Id"))
					log.init()
							.percentage(i+1, total)
							.add("\n(" + i+1 + "/" + total + ") -> " + res.getRootPath() + " ($id)");

					int count = xml.count("Content")
					for (int j = 0; j < count; j++) {
						log.add("Content ${j+1}/$count")
						String html = xml.getHtmlStringValue("Content[${j+1}]/TextBlock/Text")
						log.add("Text: " + SgReplaceUtil.escapeHTML(html))
						SgReplaceUtil replace = new SgReplaceUtil(cmso, html)
						def pdfs = replace.findAll(oldSite + pdfPattern, "")
						subsPdf(j, pdfs, downloadFolder, id, xml)

						def links = replace.findAll(linkPattern, "")
						links += replace.findAll(linkContratacion, "")
						subsLink(j, links, xml)

						if (!onlyCheck) {
							xml.saveXml()
						}
					}
					log.print()
				} catch (Exception e) {
					e.printStackTrace()
					log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
				}
			}
		} catch (Exception e) {
			e.printStackTrace()
			log.error("ERROR: ${e.getMessage()}").add("Cause: ${e.getCause()}").print();
		}
	}

	def subsLink(int j, List<String> links, SgXmlUtil xml) {
		if (links.size() > 0) {
			log.add("Links: " + links)
			links.eachWithIndex{ link, k ->
				int idx = k + 1;
				if (xml.hasValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Link")) {
					int attCount = xml.count("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Link")
					idx = attCount + idx;
				}
				xml.setStringValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Link[${idx}]/Href", link)
				xml.setStringValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Link[${idx}]/Title", "Enlace")
				xml.setStringValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Link[${idx}]/Icon", "External-link")
				xml.setStringValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Link[${idx}]/Target", "_blank")
				xml.setStringValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Link[${idx}]/Follow", "false")
				log.add("Añadido Link[${idx}]: $link")
			}
		}
	}

	def subsPdf(int j, List<String> pdfs, String downloadFolder, String id, SgXmlUtil xml) {
		if (pdfs.size() > 0) {
			log.add("Pdfs: " + pdfs)
			def newPdfs = []
			pdfs.each{
				String pdfname = it.substring(it.lastIndexOf("/") + 1)
				newPdfs.add(CmsStringUtil.joinPaths(downloadFolder, id, pdfname))
			}
			log.add("NewPdfs: " + newPdfs)
			newPdfs.eachWithIndex{ newpdf, k ->
				int idx = k + 1;
				if (xml.hasValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Attachment")) {
					int attCount = xml.count("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Attachment")
					idx = attCount + idx;
				}
				xml.setStringValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Attachment[${idx}]/ShowFormat", "true")
				if (!cmso.existsResource(newpdf)){
					log.warn("No existe el pdf: $newpdf")
					xml.setStringValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Attachment[${idx}]/File", defPdf)
				} else {
					xml.setStringValue("Content[${j+1}]/TextBlock/Gallery/LinksGallery/Option/Attachment[${idx}]/File", newpdf)
					log.add("Añadido pdf: Attachment[${idx}]/File")
				}
			}
		}
	}

	def obtainResources(CmsObject cmso, String path, String type) {
		def resources = [];
		I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(type)
		if (CmsResource.isFolder(path)) {
			CmsResourceFilter filter = CmsResourceFilter.ALL.addRequireFile();
			filter.addRequireType(resType)
			resources = cmso.readResources(path, filter, true);
		} else {
			CmsResource candidate = cmso.readResource(path)
			if (OpenCms.getResourceManager().getResourceType(candidate)
					.isIdentical(resType)) {
				resources.add(candidate)
			}
		}

		return resources
	}

	String obtainId(String id) {
		String fmtId = id.toUpperCase();

		if (fmtId.contains("EXPTE ")) {
			fmtId = fmtId.replaceFirst(Pattern.quote("EXPTE "), "")
		}
		if (fmtId.contains("EXPTE. ")) {
			fmtId = fmtId.replaceFirst(Pattern.quote("EXPTE. "), "")
		}

//		if (fmtId.contains(".")) {
//			fmtId = fmtId.replaceFirst(Pattern.quote("."), "-")
//		}
		if (fmtId.contains("/")) {
			fmtId = fmtId.replaceFirst("/", "-")
		}
		if (fmtId.contains(" ")) {
			fmtId = fmtId.replaceFirst(" ", "-")
		}
		return fmtId
	}
}