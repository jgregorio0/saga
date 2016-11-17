package com.saga.opencms.util;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.types.I_CmsResourceType;
import org.opencms.flex.CmsFlexController;
import org.opencms.lock.CmsLock;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.I_CmsPrincipal;
import org.opencms.staticexport.CmsLinkManager;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.CmsXmlEntityResolver;
import org.opencms.xml.CmsXmlException;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.safehaus.uuid.UUIDGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SgCms {

	static String folderType = "folder";
	static String folderImageType = "imagegallery";
	static String folderDownloadType = "downloadgallery";
	static String folderLinkType = "linkgallery";

	CmsObject cmso;

	public SgCms(CmsObject cmso){
		this.cmso = cmso;
	}

	/**
	 * Genera una recurso del tipo dado
	 *
	 * @param path Ruta del nuevo recurso
	 * @param type Tipo del recurso
	 * @return
	 */
	public SgCms createResource(String path, String type)
			throws CmsException {
		cmso.createResource(path, OpenCms.getResourceManager().getResourceType(type));
		unlock(path);
		return this;
	}

	/**
	 * Asegura la existencia de un recurso del tipo dado y de sus carpetas padres.
	 *
	 * @param path Ruta del nuevo recurso
	 * @param type Tipo del recurso
	 * @return
	 */
	public SgCms ensureResource(String path, String type)
			throws CmsException {
		if (!cmso.existsResource(path)) {
			String parentFolder = CmsResource.getParentFolder(path);
			ensureResource(parentFolder, folderType);
			createResource(path, type);
		}
		return this;
	}

	/**
	 * Genera una carpeta
	 *
	 * @param path Ruta de la carpeta
	 * @return
	 */
	public SgCms createFolder(String path)
			throws CmsException {
		createResource(path, folderType);
		return this;
	}

	/**
	 * Genera una galeria de imagenes
	 *
	 * @param path Ruta de la galeria de imagenes
	 * @return
	 */
	public SgCms createImageGallery(String path)
			throws CmsException {
		createResource(path, folderImageType);
		return this;
	}

	/**
	 * Genera una galeria de descargas
	 *
	 * @param path Ruta de la galeria de descargas
	 * @return
	 */
	public SgCms createDownloadGallery(String path)
			throws CmsException {
		createResource(path, folderDownloadType);
		return this;
	}

	/**
	 * Genera una galeria de enlaces
	 *
	 * @param path Ruta de la galeria de enlaces
	 * @return
	 */
	public SgCms createLinkGallery(String path)
			throws CmsException {
		createResource(path, folderLinkType);
		return this;
	}

	/**
	 * Unlock resource.
	 * If resource is lock by inheritance try to unlock parent folder.
	 * If lock owns to another user steal lock and unlock.
	 * @param path
	 * @return
	 */
	public SgCms unlock(String path)
			throws CmsException {
		CmsLock lock = cmso.getLock(path);
		if (lock.isInherited()) {
			unlock(CmsResource.getParentFolder(path));
		} else if (!lock.isUnlocked()) {
			if (!lock.isOwnedBy(cmso.getRequestContext().getCurrentUser())) {
				cmso.changeLock(path);
			}
			cmso.unlockResource(path);
		}
		return this;
	}

	/**
	 * Unlock resource.
	 * If resource is lock by inheritance try to unlock parent folder.
	 * If lock owns to another user steal lock and unlock.
	 * @param path
	 * @return
	 */
	public static void unlock(CmsObject cmso, String path)
			throws CmsException {
		CmsLock lock = cmso.getLock(path);
		if (lock.isInherited()) {
			unlock(cmso, CmsResource.getParentFolder(path));
		} else if (!lock.isUnlocked()) {
			if (!lock.isOwnedBy(cmso.getRequestContext().getCurrentUser())) {
				cmso.changeLock(path);
			}
			cmso.unlockResource(path);
		}
	}

	/**
	 * Lock resource if it is possible.
	 * If lock owns to another user steal lock.
	 * @param path
	 * @return
	 */
	public SgCms lock(String path)
			throws CmsException {
		CmsLock lock = cmso.getLock(path);
		if (!lock.isUnlocked() &&
				!lock.isOwnedBy(cmso.getRequestContext().getCurrentUser())) {
			cmso.changeLock(path);
		}
		cmso.lockResource(path);
		return this;
	}

	/**
	 * Lock resource if it is possible.
	 * If lock owns to another user steal lock.
	 * @param path
	 * @return
	 */
	public static void lock(CmsObject cmso, String path)
			throws CmsException {
		CmsLock lock = cmso.getLock(path);
		if (!lock.isUnlocked() &&
				!lock.isOwnedBy(cmso.getRequestContext().getCurrentUser())) {
			cmso.changeLock(path);
		}
		cmso.lockResource(path);
	}

	/**
	 * Dada una carpeta contenedora y un patron formado por el nombre y un índice,
	 * genera la ruta en la que se debería crear el siguiente recurso.
	 * @param folder
	 * @param pattern
	 * @return
	 */
	public String nextResoucePath(String folder, String pattern)
			throws CmsException {
		String namePattern = CmsStringUtil.joinPaths(folder, pattern);
		String newResPath = OpenCms.getResourceManager().getNameGenerator()
				.getNewFileName(cmso, namePattern, 5);
		return newResPath;
	}

	/**
	 * Set expire date
	 * @param path
	 * @param dateExp
	 * @return
	 */
	public SgCms setDateExpire(String path, Long dateExp) throws CmsException {
		lock(path);
		cmso.setDateExpired(path, dateExp, true);
		unlock(path);
		return this;
	}

	/**
	 * Check if resource is expired
	 * @param path
	 * @param dateExp
	 * @return
	 */
	public boolean isExpired(String path, Date dateExp)
			throws CmsException {
		return cmso.readResource(path, CmsResourceFilter.ALL).isExpired(dateExp.getTime());
	}

	/**
	 * Change resource type
	 * @param path
	 * @param type
	 * @return
	 */
	public SgCms changeType(String path, I_CmsResourceType type)
			throws CmsException {
		lock(path);
		cmso.chtype(path, type);
		unlock(path);
		return this;
	}

	/**
	 * Check resource type
	 * @param path
	 * @return
	 */
	boolean isType(String path, I_CmsResourceType type) throws CmsException {
		CmsResource resource = cmso.readResource(path, CmsResourceFilter.ALL);
		I_CmsResourceType rType = OpenCms.getResourceManager().getResourceType(resource);
		return rType.isIdentical(type);
	}

	/**
	 * Copy resource
	 * @param pathFrom
	 * @param pathTo
	 * @return
	 */
	public SgCms copyResource(String pathFrom, String pathTo)
			throws CmsException {

		// Comprobamos si ya existe
		if(cmso.existsResource(pathTo)){
			throw new IllegalArgumentException("ERROR: resource $pathTo already exists");
		}

		// Copiamos el recurso
		lock(pathFrom);
		cmso.copyResource(pathFrom, pathTo, CmsResource.CmsResourceCopyMode.valueOf(1));
		unlock(pathFrom);
		unlock(pathTo);

		return this;
	}

	/**
	 * Generamos un nombre válido para un grupo dada su ou y su nombre.
	 * La ou no puede empezar y debe terminar con slash "/".
	 * El nombre no puede empezar ni terminar con slash "/".
	 * @param ou
	 * @param groupname
	 * @return
	 */
	public String generateGroupName(String ou, String groupname){
		String group = ou;
		if (group.startsWith("/")) {
			group = group.substring(1);
		}
		if (!group.endsWith("/")) {
			group = group + "/";
		}
		return group + groupname;
	}

	/**
	 * Crea grupo
	 * @param groupNameWithOU
	 * @param description
	 * @param isEnabled
	 * @param parentGroupName
	 * @return
	 * @throws CmsException
	 */
	public SgCms createGroup(
			String groupNameWithOU, String description,
			boolean isEnabled, String parentGroupName)
			throws CmsException {
		int flag = I_CmsPrincipal.FLAG_DISABLED;
		if (isEnabled) {
			flag = I_CmsPrincipal.FLAG_ENABLED;
		}
		cmso.createGroup(groupNameWithOU, description, flag, parentGroupName);
		return this;
	}

	/**
	 * Create absolute link from relative uri
	 * @param req
	 * @param uri
	 * @return
	 */
	public static String link(HttpServletRequest req, String uri){
		CmsFlexController controller = CmsFlexController.getController(req);
		// be sure the link is absolute
		String target = CmsLinkManager.getAbsoluteUri(uri, controller.getCurrentRequest().getElementUri());
		CmsObject cms = controller.getCmsObject();

		// generate the link
		return OpenCms.getLinkManager().substituteLinkForUnknownTarget(cms, target);
	}

	/**
	 * Generate random uuid
	 * @return
	 */
	public static String uuid(){
		return UUIDGenerator.getInstance().generateRandomBasedUUID().toString();
	}

	/**
	 * Performs a touch operation for a single resource.<p>
	 *
	 * @param resourceName the resource name of the resource to touch
	 * @param recursive the flag if the touch operation is recursive
	 *
	 * @throws CmsException if touching the resource fails
	 */
	protected void touch(
			String resourceName,
			boolean recursive) throws CmsException {

		// lock resource
		lock(resourceName);

		// if file rewrite
		CmsResource sourceRes = cmso.readResource(resourceName, CmsResourceFilter.ALL);
		if (sourceRes.isFile()) {
			rewrite(sourceRes);
		} else if (recursive) {

			// if recursive rewrite all sub files
			Iterator<CmsResource> it = cmso.readResources(resourceName, CmsResourceFilter.ALL, true).iterator();
			while (it.hasNext()) {
				CmsResource subRes = it.next();
				if (subRes.isFile()) {
					rewrite(subRes);
				}
			}
		}
	}

	/**
	 * Rewrites the content of the given file.<p>
	 *
	 * @param resource the resource to rewrite the content for
	 *
	 * @throws CmsException if something goes wrong
	 */
	private void rewrite(CmsResource resource) throws CmsException {

		CmsFile file = cmso.readFile(resource);
		file.setContents(file.getContents());
		cmso.writeFile(file);
	}

	/**
	 * Performs the correction of the XML content resources according to their XML schema definition.<p>
	 *
	 * @param folderPath
	 * @param resourceType
	 * @param isIncludeSubFolders
	 * @param isForce
	 * @throws CmsException if reading the list of resources to repair fails
	 */
	private void repairXmlContents(
			String folderPath, String resourceType, boolean isIncludeSubFolders, boolean isForce)
				throws CmsException {

		// set the resource filter to filter XML contents of the selected type
		CmsResourceFilter filter = 
				CmsResourceFilter.IGNORE_EXPIRATION.addRequireType(
						OpenCms.getResourceManager().getResourceType(resourceType));
		String path = CmsResource.getFolderPath(folderPath);
		// get the list of resources to check
		List<CmsResource> resources = cmso.readResources(path, filter, isIncludeSubFolders);

		// create an entity resolver to use
		CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(cmso);

		// iterate the resources
		Iterator<CmsResource> i = resources.iterator();
		while (i.hasNext()) {

			CmsResource res = i.next();

			// get the file contents
			CmsFile file = cmso.readFile(res);
			// get the XML content
			CmsXmlContent xmlContent = CmsXmlContentFactory.unmarshal(cmso, file);

			// check the XML structure
			boolean fixFile = isForce;
			if (!fixFile) {
				try {
					xmlContent.validateXmlStructure(resolver);
				} catch (CmsXmlException e) {
					// XML structure is not valid, this file has to be fixed
					fixFile = true;
				}
			}
			if (fixFile) {

				// check the lock state of the file to repair
				CmsLock lock = cmso.getLock(res);
				boolean isLocked = false;
				boolean canWrite = false;
				if (lock.isNullLock()) {
					// file is not locked, lock it
					cmso.lockResource(cmso.getSitePath(res));
					isLocked = true;
					canWrite = true;
				} else if (lock.isOwnedBy(cmso.getRequestContext().getCurrentUser())) {
					// file is locked by current user
					canWrite = true;
				}

				if (canWrite) {
					// enable "auto correction mode" - this is required or the XML structure will not be fully corrected
					xmlContent.setAutoCorrectionEnabled(true);
					// now correct the XML
					xmlContent.correctXmlStructure(cmso);
					file.setContents(xmlContent.marshal());
					// write the corrected file
					cmso.writeFile(file);
				}

				if (isLocked) {
					// unlock previously locked resource
					cmso.unlockResource(cmso.getSitePath(res));
				}
			}
		}
	}

}