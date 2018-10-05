package com.saga.opencms.util

import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.relations.CmsCategory
import org.opencms.relations.CmsCategoryService
import org.opencms.relations.CmsRelation
import org.opencms.relations.CmsRelationFilter
import org.opencms.util.CmsStringUtil

class SgRelation {

    public static List<CmsResource> findTargetsWhereRootPathStartsWithAndFacultadIsNotSourceFacultad(CmsObject cmso, CmsResource source, String startsWith, String sourceFacultad) throws CmsException {
        List<CmsResource> targetList = new ArrayList<CmsResource>();

        CmsRelationFilter targets = CmsRelationFilter.SOURCES.filterResource(source);
        List<CmsRelation> targetRelations = cmso.readRelations(targets);

        // por cada relacion obtenemos las que apuntan fuera de la facultad
        for (int iRelation = 0; iRelation < targetRelations.size(); iRelation++) {
            CmsRelation targetRel = targetRelations.get(iRelation);
            CmsResource target = targetRel.getTarget(cmso, CmsResourceFilter.ALL);
            String targetRootPath = target.getRootPath();
            String[] targetPaths = targetRootPath.split("/");
            String targetFacultad = targetPaths.length > 3 ?
                    CmsStringUtil.joinPaths(
                            targetPaths[0], targetPaths[1], targetPaths[2], targetPaths[3])
                    : null;
            // Solo incluimos las relaciones dentro de facultades (startsWith /sites/facultades/)
            if (targetRootPath.startsWith(startsWith)) {
                // Solo incluimos las relaciones que enlazan a otras facultades
                if (!sourceFacultad.equals(targetFacultad)) {
                    targetList.add(target);
                }
            }
        }
        return targetList;
    }
}