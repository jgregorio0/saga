package com.saga.opencms.util
import org.opencms.db.CmsPublishList
import org.opencms.file.CmsObject
import org.opencms.file.CmsProject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.publish.CmsPublishManager
import org.opencms.report.CmsLogReport
import org.opencms.report.I_CmsReport
/**
 * Created by jgregorio on 15/11/2016.
 */

public class SgPublish {

    private static final LOG = CmsLog.getLog(SgPublish);

    List<CmsResource> pubList;
    CmsObject cmso;

    public SgPublish(CmsObject cmso){
        this.cmso = cmso;
        pubList = [];
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
     * Publish list of resources.
     * Resource list must not contain null object
     * @param rootPaths
     * @return
     */
    public CmsPublishList publishPaths(Collection<String> rootPaths) throws CmsException {
        List<CmsResource> resources = new ArrayList<CmsResource>();
        Iterator<String> it = rootPaths.iterator();
        while (it.hasNext()){
            String rootPath = it.next();
            String sitePath = SgCms.sitePath(cmso, rootPath);
            resources.add(cmso.readResource(sitePath, CmsResourceFilter.ALL));
        }
        return publish(resources);
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
     * Publish with class generated report //TODO test if report show publishing resources
     * @param cmso
     * @param clazz
     * @param resourcename
     * @param sibilings
     * @throws CmsException
     */
    public void publish(CmsObject cmso, Class clazz, String resourcename, boolean sibilings) throws CmsException {
        I_CmsReport report = new CmsLogReport(
                cmso.getRequestContext().getLocale(),
                clazz);
        CmsPublishManager publishManager = OpenCms.getPublishManager();
        CmsResource resource = cmso.readResource(resourcename, CmsResourceFilter.ALL);
        publishManager.publishProject(cmso, report, resource, sibilings);
//        publishManager.waitWhileRunning();
    }

    /**
     * Add resource to publish list
     * @param resource
     */
    public boolean add(String path){
        boolean added = false;

        try {
            CmsResource resource = cmso.readResource(path);
            added = add(resource);
        } catch (Exception e) {
            LOG.error("Cannot find resource $path for publishing", e);
        }

        return added;
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

    /**
     * Based on CmsDeleteExpiredResourcesJob.
     * Initialize publishing by creating new project
     * @param cmso
     * @return
     */
    public static I_CmsReport initPublishing(CmsObject cmso){
        CmsProject project = cmso.createTempfileProject();
        cmso.getRequestContext().setCurrentProject(project);
        I_CmsReport report = new CmsLogReport(
            cmso.getRequestContext().getLocale(),
            this.class);
        return report;
    }

    /**
     * Based on CmsDeleteExpiredResourcesJob.
     * Execute publishing for resources modified on project
     * @param cmso
     * @param report
     */
    public static void exePublishing(CmsObject cmso, I_CmsReport report){
        CmsPublishManager publishManager = OpenCms.getPublishManager();
        publishManager.publishProject(cmso, report);
        // this is to not scramble the logging output:
        publishManager.waitWhileRunning();
    }
}