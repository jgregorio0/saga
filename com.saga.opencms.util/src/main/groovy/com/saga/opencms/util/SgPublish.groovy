package com.saga.opencms.util
import org.opencms.db.CmsPublishList
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.main.OpenCms

/**
 * Created by jgregorio on 15/11/2016.
 */

public class SgPublish {

    List<CmsResource> pubList;
    CmsObject cmso;

    public SgPublish(CmsObject cmso){
        this.cmso = cmso;
    }

    /**
     * Publish list of resources
     * Resource list must not contain null objects
     * @param pubList
     * @return
     */
    public CmsPublishList publish(List<CmsResource> pubList){
        CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, true, true)
        OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        return toPublish
    }

    /**
     * Publish list of resources
     * @return
     */
    public CmsPublishList publish(){
        CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, true, true);
        OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        return toPublish
    }

    /**
     * Publish one resource
     * @param cmso
     * @param path
     */
    public void publish(String path){
        OpenCms.getPublishManager().publishResource(cmso, path);
    }

    /**
     * Publish one resource
     * @param path
     */
    public void publish(String path, boolean sibilings, boolean subResources) throws Exception {
        CmsResource resource = cmso.readResource(path);
        if (!isPublished(resource)) {
            List<CmsResource> pubList = new ArrayList<CmsResource>();
            pubList.add(resource);
            CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, sibilings, subResources);
            OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        }
    }

    /**
     * Add resource to publish list
     * @param resource
     */
    public boolean add(CmsResource resource){
        pubList.add(resource)
    }

    /**
     * Check if resource state is published
     * @param path
     * @return
     */
    public static boolean isPublished(CmsResource resource) {
        return resource.getState().isUnchanged()
    }

    /**
     * Wait until finish publishing
     * @param ms max waiting time
     * @return
     */
    public static def waitFinish(long ms){
        OpenCms.getPublishManager().waitWhileRunning(ms)
        return this;
    }
}