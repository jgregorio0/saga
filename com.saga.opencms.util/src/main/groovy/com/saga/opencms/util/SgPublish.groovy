package com.saga.sagasuite.scriptgroovy.util
import org.opencms.db.CmsPublishList
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.main.OpenCms

class SgPublish {

    List<CmsResource> pubList
    CmsObject cmso;
    public SgPublish(CmsObject cmso){
        this.cmso = cmso;
    }

    /**
     * Publish list of resources
     * @param pubList
     * @return
     */
    CmsPublishList publish(List<CmsResource> pubList){
        CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, true, true)
        OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        return toPublish
    }

    /**
     * Publish list of resources
     * @return
     */
    CmsPublishList publish(){
        CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, true, true)
        OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        return toPublish
    }

    /**
     * Add resource to publish list
     * @param resource
     */
    boolean add(CmsResource resource){
        pubList.add(resource)
    }

    /**
     * Publish one resource
     * @param cmso
     * @param path
     */
    void publish(String path){
        OpenCms.getPublishManager().publishResource(cmso, path);
    }

    /**
     * Check if resource state is published
     * @param path
     * @return
     */
    boolean isPublished(CmsResource resource) {
        return resource.getState().isUnchanged()
    }
}
