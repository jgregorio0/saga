package com.saga.opencms.util

import java.util.logging.Logger

public class SgClone {

	static String createMode = "CREATE_MODE"
	static String copyMode = "COPY_MODE"

	I_CmsResourceType containerpageType = SgCntPage.type
	I_CmsResourceType subsitemapType = OpenCms.getResourceManager().getResourceType("subsitemap");
	I_CmsResourceType htmlRedirect = OpenCms.getResourceManager().getResourceType("htmlredirect")
	I_CmsResourceType contentType = OpenCms.getResourceManager().getResourceType("content_folder")
	I_CmsResourceType sitemapconfigType = OpenCms.getResourceManager().getResourceType("sitemap_config")
	I_CmsResourceType inhGroupType = OpenCms.getResourceManager().getResourceType("inheritance_group")
	I_CmsResourceType inhConfigType = OpenCms.getResourceManager().getResourceType("inheritance_config")

	protected static final Logger LOG = Logger.getLogger(SgClone.class);

	CmsObject cmso;
	SgResource r;
	SgFolder f;
	SgPublish pub;

	public SgClone(CmsObject cmso){
		this.cmso = cmso
		r = new SgResource(cmso)
		f = new SgFolder(cmso)
	}
	/**
	 * Clone subsitemap by creating clonated resource propiedades
	 * @param params
	 * @return
	 */
	def cloneSubsitemap(String pathFrom, String pathTo) {
		Properties p = new SgProperties(cmso, pathTo)

		// Creamos el subsitemap
		// Si existe y es una carpeta le cambiamos el tipo
		if (cmso.existsResource(pathTo)) {

			// Si es folder cambiamos el tipo
			if (r.isType(pathTo, f.folderType)){
				r.changeType(pathTo, subsitemapType)
				p.copyProperties(pathFrom, false)
			} else if (r.isType(pathTo, subsitemapType)){
				p.copyProperties(pathFrom, false)
			} else {
				throw new IllegalArgumentException("ERROR: resource $pathTo already exists")
			}
		} else {

			// Creamos el subsitemap
			cloneResource(pathFrom, pathTo, subsitemapType, createMode)
		}

		// Creamos la carpeta .content sin que genere el .config por defecto
		String contentFrom =  f.joinPath(pathFrom, ".content", false);
		String contentTo = f.joinPath(pathTo, ".content", false);
		try {
			create(contentTo, folderType)
			changeType(contentTo, contentType)
			copyProperties(contentFrom, contentTo)
		} catch (Exception e) {
			e.printStackTrace()
			LOG.error(e)
		}

		// Creamos el fichero .config
		String configFrom =  f.joinPath(pathFrom, ".content/.config", false);
		String configTo =  f.joinPath(pathTo, ".content/.config", false);
		try {
			cloneResource(configFrom, configTo, sitemapconfigType, copyMode)
		} catch (Exception e) {
			e.printStackTrace()
			LOG.error(e)
		}
	}

	/**
	 * Clone resource by copying or creating and copying properties
	 * @param pathFrom
	 * @param pathTo
	 * @param type
	 * @param mode
	 * @return
	 */
	def cloneResource(String pathFrom, String pathTo, I_CmsResourceType type, String mode) {
		if (mode.equals(createMode)) {
			r.create(pathTo, type)
			copyProperties(pathFrom, pathTo)
		} else if (mode.equals(copyMode)) {
			r.copy(pathFrom, pathTo)
		}
	}

	/**
	 * Clone containerpage
	 * @param cntPageFrom
	 * @param cntPageTo
	 * @return
	 */
	def cloneCntPage(String cntPageFrom, String cntPageTo, List<String> cntPageContainers) {
		// Clonamos el containerpage
		clone(cntPageFrom, cntPageTo, containerpageType, copyMode)

		// Clonamos los contenidos
		CmsCntPageUtil cntPageUtil = new CmsCntPageUtil(cmso, cntPageTo)
		cntPageContainers.each {
			List<CmsResource> resources = cntPageUtil.readResources(it)
			resources.each {

				// Clonamos cada recurso contenido en el containerpage
//				cloneXmlLocale(it, new Locale(localeFrom), new Locale(localeTo), owXml)

				// Publicamos el recurso
				if (pub.isPublished(it)) {
					pub.addResource(it)
				}
			}
		}

	}

	/**
	 * Clone xml locale resource content
	 * @param resource
	 * @param srcLoc
	 * @param dstLoc
	 * @param overwrite
	 */
	def cloneXmlLocale(CmsResource resource, Locale srcLoc, Locale dstLoc, boolean overwrite) {
		try{
			//Validamos primero que sea un XmlContent
			// y que no sea uno de los tipos que no necesitan clonar el locale
			String path = cmso.getSitePath(resource)

			if(!CmsResourceTypeXmlContent.isXmlContent(resource)){
				throw new MigrationException("El recurso $path no es contenido xml" +
						" por lo que no podemos clonar su contenido")
			}

			if (r.isType(path, sitemapconfigType) &&
					r.isType(path, inhGroupType) &&
					r.isType(path, inhConfigType)){
				throw new IllegalArgumentException("ERROR: Resource $path can not be type" +
						" ${sitemapconfigType.getTypeName()}" +
						" or ${inhGroupType.getTypeName()}" +
						" or ${inhConfigType.getTypeName()} ")
			}

			//Obtenemos el XmlContent
			SgXml xmlRes = new SgXml(cmso, path, srcLoc)
			if (!xmlRes.xmlContent.hasLocale(srcLoc)) {
				throw new IllegalArgumentException(
						"ERROR: Resource $path has not locale $srcLoc")
			}

			//Si ya existe el locale lo borramos o no hacemos nada
			if (xmlRes.xmlContent.hasLocale(dstLoc)) {

				if (overwrite) {
					xmlRes.xmlContent.removeLocale(dstLoc)
				} else {
					return null
				}
			}

			// Copiamos el locale
			xmlRes.xmlContent.copyLocale(srcLoc, dstLoc)
			xmlRes.saveXml()

		} catch (Exception e) {
			e.printStackTrace()
			LOG.error(e);
		}
	}



	/**
	 * Copy properties from one resource to another
	 * @param pathFrom
	 * @param pathTo
	 * @return
	 */
	def copyProperties(String pathFrom, String pathTo) {
		new CmsPropertiesUtil(cmso, pathTo)
				.copyProperties(pathFrom, false)
	}
}