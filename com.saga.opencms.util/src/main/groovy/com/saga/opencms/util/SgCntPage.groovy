package com.saga.opencms.util

import org.apache.commons.logging.Log
import org.opencms.ade.configuration.CmsADEConfigData
import org.opencms.ade.containerpage.shared.CmsFormatterConfig
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.util.CmsUUID
import org.opencms.xml.containerpage.*

class SgCntPage {
    private static final Log LOG = CmsLog.getLog(SgCntPage.class);

    static final String typeName = "containerpage";
    static final I_CmsResourceType type =
            OpenCms.getResourceManager().getResourceType(typeName);

    CmsObject cmso;
    String path;
    public SgCntPage(CmsObject cmso, String path){
        this.cmso = cmso;
        this.path = path;
    }

    /**
     * Check if resource is containerpage
     * @param path
     * @return
     */
    public boolean isContainerPage(){
        boolean isConPag = false;

        //Comprobamos si existe el path
        if(cmso.existsResource(this.path)){

            //Si existe, comprobamos si el tipo es containerpage
            try{
                CmsResource r = cmso.readResource(this.path);
                I_CmsResourceType rType = OpenCms.getResourceManager().getResourceType(r)
                if(rType.isIdentical(type)){
                    isConPag = true;
                }
            }catch(CmsException ex){
                ex.printStackTrace()
            }
        }

        return isConPag;
    }

    /**
     * Obtain resource added into containerpage by container name ending
     * @param container Container name ending
     * @param elemPos Element position (the first is 0)
     * @return
     */
    public List<CmsResource> readResources(String container){
        List<CmsResource> resources = [];
        try {
            List<CmsUUID> uuids = readResourcesId(container);
            uuids.each {
                try {
                    resources.add(cmso.readResource(it))
                } catch (Exception e){
                    e.printStackTrace()
                    LOG.error("ERROR: reading resource $it from containerpage $path", e)
                }
            }
        } catch (Exception e){
            e.printStackTrace()
            LOG.error("ERROR: reading resources from containerpage $path", e)
        }

        return resources
    }

    /**
     * Obtain resource added into containerpage by container name ending
     * @param container Container name ending
     * @param elemPos Element position (the first is 0)
     * @return
     */
    public CmsResource readResource(
            String container, int elemPos){
        CmsResource resource = null;
        try {
            CmsUUID uuid = readResourceId(container, elemPos);
            resource = cmso.readResource(uuid)
        } catch (Exception e){
            e.printStackTrace()
        }

        return resource
    }

        /**
     * Obtain resource UUID added into containerpage by container name ending
     * @param container Container name ending
     * @param elemPos Element position (the first is 0)
     * @return StructureId
     */
    public CmsUUID readResourceId(
            String container, int elemPos){
        CmsUUID res = null;

        // Leemos el containerpage actual y obtenemos el bean
        CmsXmlContainerPage xmlCnt = CmsXmlContainerPageFactory.unmarshal(
                cmso, cmso.readFile(this.path));
        CmsContainerPageBean cPageBean = xmlCnt.getContainerPage(cmso);

        if (cPageBean != null) {
            CmsContainerBean cBean = findContainer(cPageBean, container)

            // Buscamos el contenedor en la posicion
            if (cBean != null) {
                CmsContainerElementBean element =
                        cPageBean.getContainers()
                        .get(cBean.getName())
                        .getElements()[elemPos]

                // Obtenemos el id del elemento
                if (element != null) {
                    res = element.getId();
                }
            }



//            containers.each { name, cmsContainer ->
//                if(cmsContainer.getName().indexOf(container) > -1){
//                    cmsContainer.getElements().eachWithIndex { element, i ->
//                        if (elemPos == i) {
//                            res = element.getId()
//                        }
//                    }
//                }
//            }
        }

        return res;
    }

    /**
     * Obtain resource UUID added into containerpage by container name ending
     * @param container Container name ending
     * @param elemPos Element position (the first is 0)
     * @return StructureId
     */
    public List<CmsUUID> readResourcesId(String container){
        List<CmsUUID> list = [];

        // Leemos el containerpage actual y obtenemos el bean
        CmsXmlContainerPage xmlCnt = CmsXmlContainerPageFactory.unmarshal(
                cmso, cmso.readFile(this.path));
        CmsContainerPageBean cPageBean = xmlCnt.getContainerPage(cmso);

        if (cPageBean != null) {
            CmsContainerBean cBean = findContainer(cPageBean, container)

            // Buscamos el contenedor en la posicion
            if (cBean != null) {
                List<CmsContainerElementBean> elements =
                        cPageBean.getContainers()
                        .get(cBean.getName())
                        .getElements()

                // Obtenemos los id del elemento
                elements.each{
                    list.add(it.getId())
                }
            }
        }

        return list;
    }

    /**
     * Adding element to containerpage.
     * Containerpage must not be expired.
     * @param container
     * @param resPath
     * @param create
     * @return
     */
    public boolean addResourceToContainerPage(
            String container, String resPath, boolean create){
        addResourceToContainerPage(container, resPath, 0, create);
    }

    /**
     * Adding element to containerpage at specified position
     * If the number of elements is not big enough the element is added at last.
     * Containerpage must not be expired.
     * @param container
     * @param resPath
     * @param create
     * @param elemPos
     * @return
     */
    public boolean addResourceToContainerPage(
            String container, String resPath, int elemPos, boolean create) {
        boolean added = false;

        // Leemos el containerpage actual obtenemos el bean
        CmsXmlContainerPage xmlContPag = CmsXmlContainerPageFactory.unmarshal(
                cmso, cmso.readFile(this.path));
        CmsContainerPageBean cPageBean = xmlContPag.getContainerPage(cmso);

        // Obtenemos el contenedor por el nombre
        if (cPageBean != null) {
            CmsContainerBean cmsCont = findContainer(cPageBean, container)

            // Obtenemos el formatter
            if (cmsCont != null) {
                CmsResource resource = cmso.readResource(
                        resPath, CmsResourceFilter.IGNORE_EXPIRATION);
                I_CmsFormatterBean formatter = findFormatter(cmsCont, resource)

                // Generamos el nuevo elemento
                if (formatter != null) {

                    CmsContainerElementBean newElement =
                            generateElement(resource, formatter, cmsCont, create)

                    // El contenedor lo dejamos tal cual
                    // y lo que hacemos es añadir un nuevo elemento al final
                    List<CmsContainerElementBean> newElements = new ArrayList<CmsContainerElementBean>();
                    newElements += cmsCont.getElements();

                    //Incluimos el nuevo
                    if (elemPos < newElements.size()) {
                        newElements.add(elemPos, newElement);
                    } else {
                        newElements.add(newElement);
                    }

                    //Generamos el nuevo containerBean
                    CmsContainerBean newContainerBean = new CmsContainerBean(
                            cmsCont.getName(),
                            cmsCont.getType(),
                            cmsCont.getParentInstanceId(),
                            cmsCont.getMaxElements(),
                            newElements);

                    // Generamos la nueva lista de contenedores
                    List<CmsContainerBean> newContainers = new ArrayList<CmsContainerBean>();
                    newContainers.add(newContainerBean);

                    // Generamos el nuevo container bean
                    CmsContainerPageBean newContBean = new CmsContainerPageBean(newContainers);

                    // Guardamos los nuevos contenedores en el containerpage
                    xmlContPag.save(cmso, newContBean);

                    added = true;
                }
            }
        }

        return added;
    }

    /**
     * Generate element for containerpage
     * @param resource
     * @param formatter
     * @param container
     * @return
     */
    CmsContainerElementBean generateElement(
            CmsResource resource, I_CmsFormatterBean formatter,
            CmsContainerBean container, boolean create) {

        // Generamos los settings
        Map<String, String> settings = new HashMap<String, String>();
        settings.put(CmsFormatterConfig.getSettingsKeyForContainer(container.getName()), formatter.getId());

        //Generamos el objeto
        CmsContainerElementBean elem = new CmsContainerElementBean(
                resource.getStructureId(),
                formatter.getJspStructureId(),
                settings,
                create);

        //Debemos inicializarlo para que se genere correctamente
        elem.initResource(cmso);

        return elem
    }

    /**
     * Find container by name.
     * @param cPageBean
     * @param name
     * @return
     */
    CmsContainerBean findContainer(CmsContainerPageBean cPageBean, String name) {
        CmsContainerBean container = null;

        Map<String, CmsContainerBean> containers = cPageBean.getContainers();

        String contName = containers.keySet().find {
            it.indexOf(name) > -1
        }

        // Incluimos el elemento en el contenedor
        if (contName != null) {
            container = containers.get(contName);
        }

        return container
    }
/**
     * Find formatter for resource
     * @param container
     * @param resource
     * @return
     */
    public I_CmsFormatterBean findFormatter(CmsContainerBean container, CmsResource resource) {
        I_CmsFormatterBean formatter = null;

        // Find container type and width
        String widthStr = container.getWidth();
        int containerWidth = -1;
        if(widthStr!=null) {
            containerWidth = Integer.parseInt(container.getWidth());
        }
        String containerType = container.getType();

        // Find formatter for container parametters
        CmsADEConfigData subSitemapConfig = OpenCms.getADEManager().lookupConfiguration(cmso, this.path);
        CmsFormatterConfiguration formatters = subSitemapConfig.getFormatters(cmso, resource);

        formatter = formatters.getDefaultFormatter(containerType, containerWidth, true);
        return formatter
    }

    /**
     * Remove containerpage element from container name and position.
     * @param containerPagePath Path relativo del containerpage
     * @param xmlContainerName Nombre del contenedor en el que se encuentra
     * el recurso. Basta con que coincida parte de ese nombre.
     * @param elemPos Posicion del elemento que leemos en caso de que haya
     * mas de uno en el contenedor (siendo 0 el primer elemento)
     * @return Devuelve el StructureId del recurso
     */
     boolean removeElement(String containerName, int elemPos) {
         boolean rm = false;

         // Leemos el containerpage actual obtenemos el bean para trabajar con el
         CmsXmlContainerPage xmlCnt = CmsXmlContainerPageFactory.unmarshal(cmso, cmso.readFile(path));
         CmsContainerPageBean containerpage = xmlCnt.getContainerPage(cmso);

         // Obtenemos el contenedor por el nombre
         if (containerpage != null) {
             CmsContainerBean container = findContainer(containerpage, containerName)

             // Generamos una nueva lista de elementos sin el que deseamos eliminar
             if (container != null) {
                 List<CmsContainerElementBean> newElements = new ArrayList<CmsContainerElementBean>();
                 newElements += container.getElements();

                 //Eliminamos el nuevo
                 newElements.remove(elemPos);

                //Generamos el nuevo containerBean
                CmsContainerBean newContainer = new CmsContainerBean(
                    container.getName(),
                    container.getType(),
                    container.getParentInstanceId(),
                    container.getMaxElements(),
                    newElements);

                // Generamos la nueva lista de contenedores
                List<CmsContainerBean> newContainers = new ArrayList<CmsContainerBean>();
                newContainers.add(newContainer);

                // Generamos el nuevo container bean
                CmsContainerPageBean newContainerpage = new CmsContainerPageBean(newContainers);

                // Guardamos los nuevos contenedores en el containerpage
                xmlCnt.save(cmso, newContainerpage);

                rm = true;
             }
         }

         return rm;
     }

    /**
     * Returns containerpage path index.html ending
     * @param newpath
     * @return
     */
    public static String generatePath(String newpath) {
        String folder = CmsResource.getFolderPath(newpath)
        return folder + "index.html"
    }


    /**
     * Obtain resource added into containerpage by container name ending
     * and resource type
     * @param containerPage
     * @param container
     * @param typeName
     * @param pos
     * @return
     */
    public static CmsResource readResource(
            CmsObject cmso, String containerpage, String container, int pos, String typeName) {
        I_CmsResourceType type =
                OpenCms.getResourceManager().getResourceType(typeName)
        CmsResource resource = new SgCntPage(cmso, containerpage)
                .readResource(container, pos)
        if (resource != null) {
            I_CmsResourceType resType =
                    OpenCms.getResourceManager().getResourceType(resource)
            if (resType.isIdentical(type)){
                return resource
            }
        }
        return null
    }
}
