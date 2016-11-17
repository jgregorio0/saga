package com.saga.opencms.util;

import org.opencms.db.CmsPublishList;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgregorio on 15/11/2016.
 */

public class SgPublish {

    private List<CmsResource> pubList;
    private CmsObject cmso;
    public SgPublish(CmsObject cmso){
        this.cmso = cmso;
    }

    /**
     * Publish list of resources.
     * Resource list must not contain null object
     * @param pubList
     * @return
     */
    public CmsPublishList publish(List<CmsResource> pubList) throws CmsException {
        CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, true, true);
        OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        return toPublish;
    }

    /**
     * Publish list of resources
     * @return
     */
    public CmsPublishList publish() throws CmsException {
        CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, true, true);
        OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        return toPublish;
    }

    /**
     * Add resource to publish list
     * @param resource
     */
    boolean add(CmsResource resource){
        return pubList.add(resource);
    }

    /**
     * Publish one resource
     * @param path
     */
    public void publish(String path) throws Exception {
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
     * Check if resource state is published
     * @param resource
     * @return
     */
    public boolean isPublished(CmsResource resource) {
        return resource.getState().isUnchanged();
    }
}