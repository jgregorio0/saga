package com.saga.opencms.util

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.opencms.file.*
import org.opencms.file.types.I_CmsResourceType
import org.opencms.flex.CmsFlexController
import org.opencms.lock.CmsLock
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.security.CmsOrganizationalUnit
import org.opencms.security.I_CmsPrincipal
import org.opencms.staticexport.CmsLinkManager
import org.opencms.util.CmsStringUtil
import org.opencms.util.CmsUUID
import org.opencms.workplace.CmsWorkplaceAction
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.safehaus.uuid.UUIDGenerator

import javax.servlet.http.HttpServletRequest

class SgCms {

    private static Log LOG = CmsLog.getLog(SgCms);

    /** Default paths */
    public static String CONTENT_PATH = "/.content";
    public static String CONFIG_PATH = "/.config";

    /** Default types */
    public static String FOLDER_TYPE = "folder";
    public static String IMAGE_GALLERY_TYPE = "imagegallery";
    public static String DOWNLOAD_GALLERY_TYPE = "downloadgallery";
    public static String LINK_GALLERY_TYPE = "linkgallery";
    public static String SITEMAP_CONFIG_TYPE = "sitemap_config";
    public static String CONTENT_FOLDER_TYPE = "content_folder";
    public static String SUBSITEMAP_TYPE = "subsitemap";
    public static String INHERITANCE_GROUP_TYPE = "inheritance_group";
    public static String INHERITANCE_CONFIG_TYPE = "inheritance_config";

    /** SPECIAL PARAMETESR */
    public static final String BACKUP = "~";

    CmsObject cmso;

    public SgCms(CmsObject cmso){
        this.cmso = cmso
    }

    /**
     * Create and unlock new resource
     *
     * @param path
     * @param type
     * @return
     * @throws CmsException
     */
    public SgCms createResource(String path, String type)
            throws CmsException {
        cmso.createResource(path, OpenCms.getResourceManager().getResourceType(type));
        unlock(path);
        return this;
    }

    /**
     * Create and Save resource
     * @param cmso
     * @param content
     * @param resourceType
     * @param path
     * @param properties
     * @return
     */
    public static SgCms createResource(CmsObject cmso, CmsXmlContent content, I_CmsResourceType resourceType, String path, List properties) {
        if (!cmso.existsResource(path)) {
            CmsResource created = cmso.createResource(path, resourceType, content.marshal(), properties);
            CmsFile createdFile = cmso.readFile(created);
            CmsXmlContent createdContent = CmsXmlContentFactory.unmarshal(cmso, createdFile);
            createdContent.handler.prepareForWrite(cmso, createdContent, createdFile);
            cmso.writeFile(createdFile);
            cmso.unlockResource(created);
        }
        return this;
    }

    /**
     * Ensure resource and parent folders exist.
     * Force unlock.
     *
     * @param path Resource path to ensure
     * @param type Resource type
     * @return
     */
    public SgCms ensureResource(String path, String type)
            throws CmsException {
        if (StringUtils.isNotBlank(path)) {
            if (!cmso.existsResource(path)) {
                String parentFolder = CmsResource.getParentFolder(path);
                ensureResource(parentFolder, FOLDER_TYPE);
                createResource(path, type);
            } else {
                unlock(path);
            }
        }
        return this;
    }

    /**
     * Ensure resource does not exists. If exists delete.
     * If resource was published before, publish delete action.
     * @param path
     * @return
     */
    public SgCms ensureNotExistsResource(String path){
        if (cmso.existsResource(path)) {
            // Check if resource was publish before move
            CmsResource resource = cmso.readResource(path);
            SgPublish sgPub = new SgPublish(cmso);
            boolean isPublish = sgPub.isPublished(resource);

            // If exists delete
            delete(path);

            // If published before
            if(isPublish){
                sgPub.publish(path);
            }
        }
        return this;
    }

    /**
     * Move resource
     * @param pathFrom
     * @param pathTo
     * @return
     */
    public SgCms move(String pathFrom, String pathTo){
        lock(pathFrom);
        cmso.moveResource(pathFrom, pathTo);
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
        createResource(path, FOLDER_TYPE);
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
        createResource(path, IMAGE_GALLERY_TYPE);
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
        createResource(path, DOWNLOAD_GALLERY_TYPE);
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
        createResource(path, LINK_GALLERY_TYPE);
        return this;
    }

    /**
     * Delete resource (including siblings)
     * @param path
     * @return
     */
    def delete(String path) throws CmsException {
        lock(cmso, path)
        cmso.deleteResource(path, CmsResource.CmsResourceDeleteMode.MODE_DELETE_REMOVE_SIBLINGS)
        return this;
    }

    /**
     * Delete resource (including siblings)
     * @param path
     * @return
     */
    static def delete(CmsObject cmso, String path) {
        lock(cmso, path)
        cmso.deleteResource(path, CmsResource.CmsResourceDeleteMode.MODE_DELETE_REMOVE_SIBLINGS)
        return this;
    }

    /**
     * Unlock resource.
     * If resource is lock by inheritance try to unlock parent folder.
     * If lock owns to another user steal lock and unlock.
     * @param path
     * @return
     */
    def unlock(String path) {
        CmsLock lock = cmso.getLock(path);
        if (lock.inherited) {
            unlock(CmsResource.getParentFolder(path));
        } else if (!lock.unlocked) {
            if (!lock.isOwnedBy(cmso.requestContext.currentUser)) {
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
    def static unlock(CmsObject cmso, String path) {
        CmsLock lock = cmso.getLock(path);
        if (lock.inherited) {
            unlock(cmso, CmsResource.getParentFolder(path));
        } else if (!lock.unlocked) {
            if (!lock.isOwnedBy(cmso.requestContext.currentUser)) {
                cmso.changeLock(path);
            }
            cmso.unlockResource(path);
        }
        return this;
    }

    /**
     * Lock resource if it is possible.
     * If lock owns to another user steal lock.
     * @param path
     * @return
     */
    def lock(String path) {
        CmsLock lock = cmso.getLock(path);
        if (!lock.isUnlocked() &&
                !lock.isOwnedBy(cmso.requestContext.currentUser)) {
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
    def static lock(CmsObject cmso, String path) {
        CmsLock lock = cmso.getLock(path);
        if (!lock.isUnlocked() &&
                !lock.isOwnedBy(cmso.requestContext.currentUser)) {
            cmso.changeLock(path);
        }
        cmso.lockResource(path);
        return this;
    }

    /**
     * Dada una carpeta contenedora y un patron formado por el nombre y un índice,
     * genera la ruta en la que se debería crear el siguiente recurso.
     * @param folder "/system/modules/com.saga.sagasuite.scriptgroovy/test/blog/"
     * @param pattern "blog-%(number:5).xml"
     * @return
     */
    public String generateNextResoucePath(String folder, String pattern){
        String namePattern = CmsStringUtil.joinPaths(folder, pattern)
        String newResPath = OpenCms.getResourceManager().getNameGenerator()
                .getNewFileName(cmso, namePattern, 5)
        return newResPath;
    }

    /**
     * Dada una carpeta contenedora y un patron formado por el nombre y un índice,
     * genera la ruta en la que se debería crear el siguiente recurso.
     * @param folder "/system/modules/com.saga.sagasuite.scriptgroovy/test/blog/"
     * @param pattern "blog-%(number:5).xml"
     * @return
     */
    public static String generateNextResoucePath(CmsObject cmso, String folder, String pattern){
        String namePattern = CmsStringUtil.joinPaths(folder, pattern)
        String newResPath = OpenCms.getResourceManager().getNameGenerator()
                .getNewFileName(cmso, namePattern, 5)
        return newResPath;
    }

    /**
     * Set expire date
     * @param params
     * @return
     */
    def setDateExpire(String path, Date dateExp){
        lock(path)
        cmso.setDateExpired(path, dateExp, true)
        unlock(path)
        return this;
    }

    /**
     * Check if resource is expired
     * @param cmso
     * @param path
     * @param dateExp
     * @return
     */
    boolean isExpired(String path, Date dateExp){
        return cmso.readResource(path, CmsResourceFilter.ALL).isExpired(dateExp)
    }

    /**
     * Check if file is a backup and contains ~
     * @param path
     * @return
     */
    public static boolean isFileBackUp(String path) {
        return path.contains(BACKUP);
    }

    /**
     * Change resource type
     * @param path
     * @param type
     * @return
     */
    def changeType(String path, I_CmsResourceType type) {
        lock(path);
        cmso.chtype(path, type);
        unlock(path);
        return this;
    }

    /**
     * Change resource type
     * @param path
     * @param type
     * @return
     */
    def changeType(String path, String type) {
        I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(type)
        changeType(path, resType);
        return this;
    }

    /**
     * Change resource type
     * @param path
     * @param type
     * @return
     */
    static def changeType(CmsObject cmso, String path, String type) {
        lock(cmso, path)
        I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(type)
        cmso.chtype(path, resType);
        unlock(cmso, path);
        return this;
    }

    /**
     * Check resource type
     * @param path
     * @return
     */
    boolean isType(String path, I_CmsResourceType type) {
        CmsResource resource = cmso.readResource(path, CmsResourceFilter.ALL)
        I_CmsResourceType rType = OpenCms.getResourceManager().getResourceType(resource)
        return rType.isIdentical(type)
    }

    /**
     * Read resource type
     * @param path
     * @return
     */
    public I_CmsResourceType readType(String path){
        CmsResource resource = cmso.readResource(path);
        return OpenCms.getResourceManager().getResourceType(resource.getTypeId());
    }

    /**
     * Check resource type
     * @param path
     * @param type
     * @return
     */
    boolean isType(String path, String type) {
        I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(type)
        return isType(path, resType);
    }

    /**
     * Copy resource
     * @param pathFrom
     * @param pathTo
     * @return
     */
    def copyResource(String pathFrom, String pathTo) {

        // Comprobamos si ya existe
        if(cmso.existsResource(pathTo)){
            throw new IllegalArgumentException("ERROR: resource $pathTo already exists")
        }

        // Copiamos el recurso
        lock(pathFrom)
        cmso.copyResource(pathFrom, pathTo, CmsResource.CmsResourceCopyMode.valueOf(1))
        unlock(pathFrom)
        unlock(pathTo)

        return this
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
        return UUIDGenerator.getInstance().generateRandomBasedUUID();
    }

    /**
     * Performs a touch operation for a single resource.<p>
     *
     * @param resourceName the resource name of the resource to touch
     * @param recursive the flag if the touch operation is recursive
     *
     * @throws CmsException if touching the resource fails
     */
    public def touch(
            String resourceName,
            boolean recursive) throws CmsException {

        // if file rewrite
        CmsResource sourceRes = cmso.readResource(resourceName, CmsResourceFilter.ALL);
        if (sourceRes.isFile()) {
            String path = cmso.getSitePath(sourceRes);
            rewrite(path);
        } else if (recursive) {

            // if recursive rewrite all sub files
            Iterator<CmsResource> it = cmso.readResources(resourceName, CmsResourceFilter.ALL, true).iterator();
            while (it.hasNext()) {
                CmsResource subRes = it.next();
                if (subRes.isFile()) {
                    String path = cmso.getSitePath(subRes);
                    rewrite(path);
                }
            }
        }
        return this;
    }

    /**
     * Rewrites the content of the given file.<p>
     *
     * @param resource the resource to rewrite the content for
     *
     * @throws CmsException if something goes wrong
     */
    public def rewrite(String path) throws CmsException {
    // lock resource
        lock(path);
        CmsFile file = cmso.readFile(path);
        file.setContents(file.getContents());
        cmso.writeFile(file);
        unlock(path)
        return this;
    }

    /**
     * Create user using fqn (ou/username)
     * @param cmso
     * @param fqn
     * @param pass
     * @return
     */
    public static CmsUser createUser(
            CmsObject cmso, String fqn, String pass, boolean add2Users)
            throws CmsException {
        CmsUser user = null;
        user = cmso.createUser(fqn, pass, null, null);
        if (add2Users) {
            String usersGroup =
                    CmsOrganizationalUnit.getParentFqn(fqn)
            + CmsOrganizationalUnit.SEPARATOR
            + "Users";
            add2Group(cmso, fqn, usersGroup);
        }
        return user;
    }

    /**
     * Create user using Admin CmsObject
     * @param fqn
     * @param pass
     * @param add2Users
     * @return
     * @throws CmsException
     */
    public static CmsUser createUser(String fqn, String pass, boolean add2Users)
            throws CmsException {
        CmsUser user = null;
        CmsObject cmsoAdmin = getCmsAdmin();
        createUser(cmsoAdmin, fqn, pass, add2Users);

        return user;
    }

    /**
     * Add group using fqn (ou/username) and group (ou/groupname)
     * @param cmso
     * @param fqn
     * @param group
     * @return
     */
    public static def add2Group(
            CmsObject cmso, String fqn, String group)
            throws CmsException {
        cmso.addUserToGroup(fqn, group);
        return this;
    }

    /**
     * Return CmsObject for Admin
     * @return
     * @throws CmsException
     */
    public static CmsObject getCmsAdmin()
            throws CmsException {
        return CmsWorkplaceAction.getInstance().getCmsAdminObject();
    }

    /**
     * Obtain resource by its detail path
     * @param cmso
     * @param detailPath
     * @return
     * @throws CmsException
     */
    public static CmsResource resourceByDetailPath(
            CmsObject cmso, String detailPath)
            throws CmsException {
        String detailName = CmsResource.getName(detailPath);
        CmsUUID strIdUUID = cmso.readIdForUrlName(detailName);
        return cmso.readResource(strIdUUID);
    }

    /**
     * Customize a new initialized copy of CmsObject
     * @param baseCms
     * @param uri
     * @param site
     * @return
     * @throws CmsException
     */
    public static CmsObject customCmsObject(
            CmsObject baseCms, String locale, String uri, String site)
            throws CmsException {
        CmsObject cmso = baseCms;
        boolean isLocale = StringUtils.isNotBlank(locale);
        boolean isCustomSite = StringUtils.isNotBlank(site);
        boolean isCustomUri = StringUtils.isNotBlank(uri);
        if (isLocale || isCustomSite || isCustomUri) {
            cmso = OpenCms.initCmsObject(baseCms);
            if (isLocale) {
                cmso.getRequestContext().setLocale(new Locale(locale));
            }
            if (isCustomSite) {
                cmso.getRequestContext().setSiteRoot(site);
            }
            if (isCustomUri) {
                cmso.getRequestContext().setUri(uri);
            }
        }
        return cmso;
    }

    /**
     * Find resources of the given type
     * @param path
     * @param filter
     * @return
     */
    def static findResources(CmsObject cmso, String path, String type){
        CmsResourceFilter filter = CmsResourceFilter.ALL.addRequireType(
                OpenCms.getResourceManager().getResourceType(type))
        return findResources(cmso, path, filter);
    }

    /**
     * Find resources allowed by filter
     * @param path
     * @param filter
     * @return
     */
    def static findResources(CmsObject cmso, String path, CmsResourceFilter filter){
        def resources = [];
        if (CmsResource.isFolder(path)) {
            resources = cmso.readResources(path, filter, true);
        } else {
            resources.add(cmso.readResource(path, filter));
        }

        return resources;
    }

    /**
     * Clone resource by copying or creating and copying properties
     * @param pathFrom
     * @param pathTo
     * @param copyMode Copy all resources inside
     * @return
     */
    public def cloneResource(String pathFrom, String pathTo, boolean copyMode) {
        if (!copyMode) {
            // Get resource type
            String type = readType(pathFrom).getTypeName();

            // create resource
            createResource(pathTo, type);

            // copy properties
            new SgProperties(cmso)
                    .copyAllProperties(pathFrom, pathTo, false);
        } else if (copyMode) {
            copyResource(pathFrom, pathTo)
        }
        return this;
    }

    /**
     * Clone subsitemap folder, content folder and config files.
     * No content and containerpages are cloned
     * @param params
     * @return
     */
    def cloneSubsitemapFolder(String pathFrom, String pathTo) {
        // Si existe y es una carpeta le cambiamos el tipo
        if (cmso.existsResource(pathTo)) {
            SgProperties sgProps = new SgProperties(cmso);

            // Si es folder cambiamos el tipo
            if (isType(pathTo, FOLDER_TYPE)){
                changeType(pathTo, SUBSITEMAP_TYPE)
                sgProps.copyAllProperties(pathFrom, pathTo, false);
            } else if (isType(pathTo, SUBSITEMAP_TYPE)){
                sgProps.copyAllProperties(pathFrom, pathTo, false);
            } else {
                throw new IllegalArgumentException(
                        "resource $pathTo already exists" +
                                " and it is not folder or subsitemap");
            }
        } else {

            // Creamos el subsitemap
            cloneResource(pathFrom, pathTo, false);
        }

        // Creamos la carpeta .content sin que genere el .config por defecto
        String contentFrom =  CmsStringUtil.joinPaths(pathFrom, CONFIG_PATH);
        String contentTo = CmsStringUtil.joinPaths(pathTo, CONTENT_PATH);
        try {
            createResource(contentTo, CONTENT_FOLDER_TYPE);
            new SgProperties(cmso)
                    .copyAllProperties(contentFrom, contentTo, false);
        } catch (Exception e) {
            LOG.error(e)
        }

        // Creamos el fichero .config
        String configFrom =  CmsStringUtil.joinPaths(
                pathFrom, CONTENT_PATH, CONFIG_PATH);
        String configTo =  CmsStringUtil.joinPaths(
                pathTo, CONTENT_PATH, CONFIG_PATH);
        try {
            cloneResource(configFrom, configTo, true);
        } catch (Exception e) {
            LOG.error(e)
        }
    }

    /**
     * Return a map with internal resource information
     * @param path
     * @return
     */
    public static Map<String, String> readInternalInfo(CmsObject cmso, String path){
        Map<String, String> infos = [:];

        CmsResource resource = cmso.readResource(path);
        infos.put(dateContent: resource.getDateContent())
        infos.put(dateCreated: resource.getDateCreated())
        infos.put(dateExpired: resource.getDateExpired())
        infos.put(dateLastModified: resource.getDateLastModified())
        infos.put(dateReleased: resource.getDateReleased())
        infos.put(flags: resource.getFlags())
        infos.put(state: resource.getState().getState())
        infos.put(typeId: resource.getTypeId())
        infos.put(userCreated: resource.getUserCreated().getStringValue())
        infos.put(userLastModified: resource.getUserLastModified().getStringValue())
        infos.put(version: resource.getVersion())

        return infos;
    }

    /**
     * Return resource indentified by structureid@rootpath
     * @param resource
     * @return
     */
    public static String identifyResource(CmsResource resource){
        return resource.getStructureId().getStringValue() + "@" + resource.getRootPath();
    }
}