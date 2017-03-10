package com.saga.sagasuite.scriptgroovy.util

import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.lock.CmsLock
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil

class SgCms {

    static String folderType = "folder";
    static String folderImageType = "imagegallery";
    static String folderDownloadType = "downloadgallery";
    static String folderLinkType = "linkgallery";

    CmsObject cmso;

    public SgCms(CmsObject cmso){
        this.cmso = cmso
    }

    /**
     * Genera una recurso del tipo dado
     *
     * @param path Ruta del nuevo recurso
     * @param type Tipo del recurso
     * @return
     */
    def createResource(String path, String type) {
        if (path) {
            if (!cmso.existsResource(path)) {
                cmso.createResource(path, OpenCms.resourceManager.getResourceType(type));
            }
            unlock(path);
        }
        return this;
    }

    /**
     * Genera una carpeta
     *
     * @param path Ruta de la carpeta
     * @return
     */
    def createFolder(String path) {
        createResource(path, folderType)
        return this;
    }

    /**
     * Genera una galeria de imagenes
     *
     * @param path Ruta de la galeria de imagenes
     * @return
     */
    def createImageGallery(String path) {
        createResource(path, folderImageType)
        return this;
    }

    /**
     * Genera una galeria de descargas
     *
     * @param path Ruta de la galeria de descargas
     * @return
     */
    def createDownloadGallery(String path) {
        createResource(path, folderDownloadType)
        return this;
    }

    /**
     * Genera una galeria de enlaces
     *
     * @param path Ruta de la galeria de enlaces
     * @return
     */
    def createLinkGallery(String path) {
        createResource(path, folderLinkType)
        return this;
    }

    /**
     * Delete resource (including siblings)
     * @param path
     * @return
     */
    def delete(String path) {
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
    }

    /**
     * Dada una carpeta contenedora y un patron formado por el nombre y un índice,
     * genera la ruta en la que se debería crear el siguiente recurso.
     * @param folder "/system/modules/com.saga.sagasuite.scriptgroovy/test/blog/"
     * @param pattern "blog-%(number:5).xml"
     * @return
     */
    String nextResoucePath(String folder, String pattern){
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
}