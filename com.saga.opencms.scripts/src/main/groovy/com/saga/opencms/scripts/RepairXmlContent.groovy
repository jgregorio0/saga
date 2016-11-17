package com.saga.sagasuite.scriptgroovy
import com.saga.sagasuite.scriptgroovy.util.SgCnt
import com.saga.sagasuite.scriptgroovy.util.SgLog
import com.saga.sagasuite.scriptgroovy.util.SgPublish
import com.saga.sagasuite.scripts.SgReportManager
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.CmsResourceTypeXmlContent
import org.opencms.file.types.I_CmsResourceType
import org.opencms.main.OpenCms
/**
 * Created by jgregorio on 15/09/2015.
 */
class RepairXmlContent {


	CmsObject cmso;
	String idProceso;
	String rootPath;
	String type;
	String exCntPage;
	String rewrite;
	String publish;
	def onlyCheck;

	String site;
	Locale locale;
	static I_CmsResourceType cntPageType =
			OpenCms.getResourceManager().getResourceType("containerpage");


	SgLog log;

	public RepairXmlContent(){
	}

	public void init(def cms, def idProceso, def rootPath, def type, def exCntPage, def rewrite, def publish, def onlyCheck) {

		this.cmso = cms;
		this.idProceso = idProceso;
		this.rootPath = rootPath;
		this.type = type;
		this.exCntPage = exCntPage;
		this.rewrite = rewrite;
		this.publish = publish;
		this.onlyCheck = onlyCheck;

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
			if (validate()) {

				log.add("Creamos filtro con todos los recursos, sin tener en cuenta las carpetas")
				CmsResourceFilter filter = CmsResourceFilter.ALL.addRequireFile();

				if (exCntPage) {
					log.add("Excluimos los containerpage")
					filter = filter.addExcludeType(cntPageType);
				}

				if (!StringUtils.isEmpty(type)) {
					log.add("Requerimos el tipo $type");
					filter = filter.addRequireType(
							OpenCms.getResourceManager().getResourceType(type));
				}

				SgPublish pub = new SgPublish(cmso);
				log.print()

				// Para cada recurso, si es xml  y coincide con el tipo especificado lo reparamos
				List<CmsResource> resources = cmso.readResources(rootPath, filter, true);
				int total = resources.size();
				int i = 0;
				for (CmsResource resource : resources) {
					try {
						i++;
						log.init().percentage(i, total)
								.add("$i/$total -- Para el recurso ${resource.getRootPath()}");

						if (!CmsResourceTypeXmlContent.isXmlContent(resource)) {
							throw new Exception("El recurso ${resource.getRootPath()} no es xmlContent");
						}

						if (exCntPage &&
								OpenCms.getResourceManager().getResourceType(resource)
										.isIdentical(cntPageType)) {
							throw new Exception("El recurso $resource es de tipo containerpage");
						}

						if (type &&
								! (OpenCms.getResourceManager().getResourceType(type)
										.isIdentical(
										OpenCms.getResourceManager().getResourceType(resource)))) {
							throw new Exception("El recurso $resource no es de tipo $type");
						}


						pub.add(resource)
						if (!onlyCheck) {
							SgCnt cnt = new SgCnt(cmso, resource, "es");
							cnt.repair();
							log.add("Recurso reparado")

							if (rewrite) {
								cnt.saveXml();
								log.add("Recurso reescrito")
							}
						} else {
							String rw = rewrite != null ? "reescribiendo" : "sin reescribir"
							log.add("Reparamos el recurso $rw su contenido")
						}

						log.print()
					} catch (Exception e) {
						log.error(e).print();
					}
				}

				if (publish) {
					log.add("Publicamos ${pub.resources.size()} ficheros");
					pub.publish();
				}
			}
			log.print()
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

		boolean valRootPath = !StringUtils.isEmpty(rootPath) && cmso.existsResource(rootPath);
		if (!valRootPath) {
			throw new IllegalArgumentException("La ruta '$rootPath' no es válida");
		}
		validate = validate && valRootPath;

		return validate;
	}
}