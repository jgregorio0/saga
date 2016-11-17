package com.saga.opencms.util

import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.relations.CmsCategory
import org.opencms.relations.CmsCategoryService
import org.opencms.util.CmsStringUtil

class SgCategory {

    CmsObject cmso;
    CmsCategoryService catSer;

    public SgCategory(CmsObject cmso){
        this.cmso = cmso;
        catSer = CmsCategoryService.getInstance();
    }

    /**
     * Create relation between category and resource.
     * Do not add category to resource xml content.
     * @param resource
     * @param catRootPath
     * @return
     */
    def addCategory(String resRelPath, String catRootPath){
        CmsCategory cat = readCategory(catRootPath)
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
    def addCategory(String resRelPath, CmsCategory category){
        SgCms.lock(cmso, resRelPath);
        catSer.addResourceToCategory(cmso, resRelPath, category)
        SgCms.unlock(cmso, resRelPath);
        return this;
    }

    /**
     * Find CmsCategory object
     * @param repository
     * @param catPath
     * @return
     */
    CmsCategory readCategory(String repository, String catPath){
        String absPath = CmsStringUtil.joinPaths(repository, catPath)
        return catSer.getCategory(cmso, absPath)
    }

    /**
     * Find CmsCategory object
     * @param catRootPath
     * @return
     */
    def readCategory(String catRootPath){
        return catSer.getCategory(cmso, catRootPath)
    }

    /**
     * Find all resources for category
     * @param catRelPath
     * @param repository
     * @param filter
     * @return
     */
    List<CmsResource> readCategoryResources(String catRelPath, String repository, CmsResourceFilter filter){
        return catSer.readCategoryResources(
                cmso, catRelPath, false, repository, filter)
    }

    /**
     * Return first resource found for category
     * @param catRelPath
     * @param repository
     * @param filter
     * @return
     */
    CmsResource readFirstCategoryResource(String catRelPath, String repository, CmsResourceFilter filter){
        CmsResource found = null;
        def resources = readCategoryResources(catRelPath, repository, filter);
        if (resources.size() > 0) {
            found = resources.get(0);
        }
        return found;
    }

    /**
     * Find all categories which a resource belongs
     */
    List<CmsCategory> readResourceCategories(CmsResource resource){
        return catSer.readResourceCategories(cmso, resource);
    }

    /**
     * Check if resource contains category
     * @param resource
     * @param category
     * @return
     */
    boolean containsCategory(CmsResource resource, CmsCategory category){
        boolean hasCat = false;
        List<CmsCategory> categories = readResourceCategories(resource);
        for (int i = 0; i < categories.size() && !hasCat; i++) {
            CmsCategory cat = categories.get(i);
            if (cat.equals(category)){
                hasCat = true;
            }
        }
        return hasCat;
    }

    /**
     * Repair relations for resource
     * @param resource
     * @return
     */
    def repairRelations(CmsResource resource){
        catSer.repairRelations(cmso, resource);
    }
}