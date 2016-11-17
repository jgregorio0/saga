package com.saga.opencms.util;


import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.relations.CmsCategory;
import org.opencms.relations.CmsCategoryService;
import org.opencms.util.CmsStringUtil;

import java.util.List;

public class SgCategory {

    private CmsObject cmso;
    private CmsCategoryService catSer;

    public SgCategory(CmsObject cmso){
        this.cmso = cmso;
        catSer = CmsCategoryService.getInstance();
    }

    /**
     * Create relation between category and resource.
     * Do not add category to resource xml content.
     * @param resRelPath
     * @param catRootPath
     * @return
     */
    public SgCategory addCategory(String resRelPath, String catRootPath) throws CmsException {
        CmsCategory cat = readCategory(catRootPath);
        addCategory(resRelPath, cat);
        return this;
    }

    /**
     * Create relation between category and resource.
     * Do not add category to resource xml content.
     * @param resRelPath
     * @param category
     * @return
     */
    public SgCategory addCategory(String resRelPath, CmsCategory category) throws CmsException {
        SgCms.lock(cmso, resRelPath);
        catSer.addResourceToCategory(cmso, resRelPath, category);
        SgCms.unlock(cmso, resRelPath);
        return this;
    }

    /**
     * Create relation between category and resource.
     * Do not add category to resource xml content.
     * @param resource
     * @param category
     * @return
     */
    public void addCategory(CmsResource resource, CmsResource category, String catXmlPath) throws Exception {
        CmsCategory cmsCategory  = catSer.getCategory(cmso, category);
        String resPath = cmso.getSitePath(resource);

        // Si no la contiene
        if (!contains(resPath, cmsCategory)) {

            SgCnt cnt = new SgCnt(cmso, resource);
            if (cnt.contains(catXmlPath)) {
                String valueStr = cnt.getStringValue(catXmlPath) + "," + category.getRootPath();
                cnt.setStringValue(catXmlPath, valueStr);
            } else {
                cnt.setStringValue(catXmlPath, category.getRootPath());
            }
        }
    }

    /**
     * Find CmsCategory object
     * @param repository
     * @param catPath
     * @return
     */
    public CmsCategory readCategory(String repository, String catPath) throws CmsException {
        String absPath = CmsStringUtil.joinPaths(repository, catPath);
        return catSer.getCategory(cmso, absPath);
    }

    /**
     * Find CmsCategory object
     * @param catRootPath
     * @return
     */
    public CmsCategory readCategory(String catRootPath) throws CmsException {
        return catSer.getCategory(cmso, catRootPath);
    }

    /**
     * Find all resources for category
     * @param catRelPath
     * @param repository
     * @param filter
     * @return
     */
    public List<CmsResource> readCategoryResources(String catRelPath, String repository, CmsResourceFilter filter) throws CmsException {
        return catSer.readCategoryResources(
                cmso, catRelPath, false, repository, filter);
    }

    /**
     * Return first resource found for category
     * @param catRelPath
     * @param repository
     * @param filter
     * @return
     */
    public CmsResource readFirstCategoryResource(String catRelPath, String repository, CmsResourceFilter filter) throws CmsException {
        CmsResource found = null;
        List<CmsResource> resources = readCategoryResources(catRelPath, repository, filter);
        if (resources.size() > 0) {
            found = resources.get(0);
        }
        return found;
    }

    /**
     * Find all categories which a resource belongs
     */
    public List<CmsCategory> readResourceCategories(CmsResource resource) throws CmsException {
        return catSer.readResourceCategories(cmso, resource);
    }

    /**
     * Check if resource contains category
     * @param resPath
     * @param category
     * @return
     */
    public boolean contains(String resPath, CmsCategory category) throws CmsException {
        boolean contains = false;
        if (catSer.readResourceCategories(
                cmso, cmso.readResource(resPath, CmsResourceFilter.IGNORE_EXPIRATION))
                    .contains(category)) {
            contains = true;
        }
        return contains;
    }

    /**
     * Repair relations for resource
     * @param resource
     * @return
     */
    public SgCategory repairRelations(CmsResource resource) throws CmsException {
        catSer.repairRelations(cmso, resource);
        return this;
    }


}