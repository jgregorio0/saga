package com.saga.opencms.synchronization

import com.saga.opencms.util.SgCms
import com.saga.opencms.util.SgPublish
import com.saga.opencms.util.SgRes
import org.apache.commons.logging.Log
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.jsp.CmsJspActionElement
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.report.I_CmsReport

/**
 * Created by jgregorio on 02/03/2018.
 */
class SgResourceService {
    private static final Log LOG = CmsLog.getLog(SgResourceService.class);

    private CmsObject cmsObject;
    private CmsJspActionElement cms;
    private SgResManager rm;
    private SgCms sgCms;
    private SgRes sgRes;
    private SgPublish sgPub;

    public SgResourceService(CmsJspActionElement cms, CmsObject cmsObject) {
        this(cmsObject);
        this.cms = cms;
    }

    public SgResourceService(CmsObject cmsObject) {
        this.cmsObject = cmsObject;
        rm = new SgResManager(cmsObject);
        sgCms = new SgCms(cmsObject);
        sgPub = new SgPublish(cmsObject);
        sgRes = new SgRes(cmsObject, CmsResourceFilter.ALL, SgRes.Mode.MODE_UPDATE_NON_EMPTY);
    }

    public SgResourceService(CmsObject cmsObject, CmsResourceFilter filter) {
        this(cmsObject);
        sgRes = new SgRes(cmsObject, filter, SgRes.Mode.MODE_UPDATE_NON_EMPTY);
    }

    public List<CmsResource> updateResources(List<SgResource> recursos, boolean publish){
        List<CmsResource> modified = new ArrayList<CmsResource>();

        //Recorremos la lista entera y vamos creando cada uno de los recursos
        LOG.debug("updating " + recursos.size() + " resources");
        for (SgResource recurso : recursos) {

            //Actualizamos cada uno de los recursos
            LOG.debug("updating resource: " + recurso);
            CmsResource res = updateResource(recurso);
            boolean isModified = res != null;
            LOG.debug("modified >> " + isModified);
            if (isModified) {
                modified.add(res);
            }
        }

        if(publish) {
            LOG.debug("publishing " + modified.size() + " resources");
            try {
                sgPub.publish(modified);
            } catch (CmsException e) {
                LOG.error("ERROR publishing resources", e);
            }
        }

        return modified;
    }

    public CmsResource updateResource(SgResource resource)  {

        try {
            //Generamos el data con el mapeo de la informacion
            HashMap data = getDataByResource(resource.getFields());
            //Obtenemos el id del recurso
            String resourceTypeName = OpenCms.getSgResManager()
                    .getResourceType(resource.getResourceType()).getTypeName();

            // Aseguramos la existencia del recurso y de la carpeta padre.
            // En caso de crear la carpeta padre publicamos.
            sgCms.ensureResource(resource.getPath(), resourceTypeName, SgCms.CreateParents.PARENT_FOLDER);
            String parentFolder = CmsResource.getParentFolder(resource.getPath());
            sgPub.publish(parentFolder, false, false);

            // Actualizamos el contenido del recurso en todos los locales disponibles
            CmsResource cmsResource = sgRes.save(data, resource.getPath(), resourceTypeName, false, OpenCms.getLocaleManager().getAvailableLocales());

            return cmsResource;
        }catch(Exception ex){
            LOG.error("Error actualizando recurso " + resource, ex);
        }
        return null;
    }

    private HashMap<?,?> getDataByResource(List<SgField> fields){
        HashMap<String, Object> data = new HashMap<String, Object>();

        //Recorremos todos los campos añadiendo el campo
        for (SgField field : fields) {
            if(SgField.FIELD_TYPE_SIMPLE.equals(field.getType()))
                data.put(field.getName(), field.getValue());
            else if(SgField.FIELD_TYPE_NESTED.equals(field.getType()))
                data.put(field.getName(), getDataByResource(field.getFields()));
            else if(SgField.FIELD_TYPE_MULTIPLE_SIMPLE.equals(field.getType())) {
                List<String> fieldsAux = new ArrayList<String>();
                for (SgField f : field.getFields()) {
                    fieldsAux.add(f.getValue());
                }
                data.put(field.getName(), fieldsAux);
            }
            else if(SgField.FIELD_TYPE_MULTIPLE_NESTED.equals(field.getType())) {
                List<HashMap> fieldsAux = new ArrayList<HashMap>();
                for (SgField f : field.getFields()) {
                    fieldsAux.add(getDataByResource(f.getFields()));
                }
                data.put(field.getName(), fieldsAux);
            }else if(SgField.FIELD_TYPE_MULTIPLE_CHOICE.equals(field.getType())) {
                List<HashMap> fieldsAux = new ArrayList<HashMap>();
                for (SgField f : field.getFields()) {
                    fieldsAux.add(getDataByResource(f.getFields()));
                }
                data.put(field.getName(), fieldsAux);
            }
        }
        return data;
    }

    private void publishListResource(List<CmsResource> resources) {
        try {
            OpenCms.getPublishManager().publishProject(this.cmsObject, (I_CmsReport)null, OpenCms.getPublishManager().getPublishList(this.cmsObject, resources, true, true));
        } catch (CmsException var3) {
            var3.printStackTrace();
        }

    }
}
