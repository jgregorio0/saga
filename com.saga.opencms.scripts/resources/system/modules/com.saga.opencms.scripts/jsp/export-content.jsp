<%@ page buffer="none" session="false" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.opencms.file.*" %>
<%@ page import="org.opencms.file.types.CmsResourceTypeXmlContent" %>
<%@ page import="org.opencms.file.types.CmsResourceTypeXmlPage" %>
<%@ page import="org.opencms.file.types.I_CmsResourceType" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.loader.CmsLoaderException" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.relations.CmsRelation" %>
<%@ page import="org.opencms.relations.CmsRelationFilter" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
<%@ page import="org.opencms.xml.content.CmsXmlContent" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ page import="org.opencms.xml.types.I_CmsXmlContentValue" %>
<%@ page import="java.util.*" %>

<%--
  Realiza la exportación a JSON. Los parámetros que recibe son:
  - type: Tipo del recurso que exportar
  - max: Número máximo de recursos que retornar
  - folder: Carpeta desde la que obtener los recursos
  - file: Ruta del fichero concreto a exportar
  - since: Fecha de última modificación expresada en milisegundos a partir de la que obtener los recurso
--%>

<%!
    private boolean validate(CmsObject cmso, String file, String folder, String type) throws Exception {
        boolean valFile = validateFile(cmso, file);
        boolean valFolder = validateFolder(cmso, folder);
        boolean valType = validateType(type);

        // Valida file o folder y type
        if (valFile) {
            return true;
        } else if (valFolder && valType) {
            return true;
        }

        // En caso de no tener parametros necesarios
        throw new Exception("Parámetros no válidos: se debe indicar (file) o (folder y tipo)");
    }

    private boolean validateFile(CmsObject cmso, String file) {
        if (StringUtils.isBlank(file)) {
            return false;
        }
        if (!cmso.existsResource(file)) {
            return false;
        }
        return true;
    }

    private boolean validateType(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        try {
            OpenCms.getResourceManager().getResourceType(type);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean validateFolder(CmsObject cmso, String folder) {
        if (StringUtils.isBlank(folder)) {
            return false;
        }
        if (!cmso.existsResource(folder)) {
            return false;
        }
        return true;
    }

    String identifyResource(String structureId, String path){
        return structureId + "@" + path;
    }

    String identifyResource(CmsResource resource){
        return resource.getStructureId().getStringValue() + "@" + resource.getRootPath();
    }

    String getOnlineLink(CmsObject cmsObject, String resource) {
        try {
            return OpenCms.getLinkManager().getOnlineLink(cmsObject, resource);
        } catch (Exception e) {
            return "";
        }
    }

    private boolean isPointer(CmsResource resource) throws CmsLoaderException {
        return OpenCms.getResourceManager().getResourceType("pointer").getTypeId() == resource.getTypeId();
    }
%>
<%
    try {
        final CmsObject cmsObject = CmsFlexController.getCmsObject(request);
        cmsObject.getRequestContext().setSiteRoot("");
        final CmsProject offline = cmsObject.readProject("Online");
        cmsObject.getRequestContext().setCurrentProject(offline);
        final Locale locale = cmsObject.getRequestContext().getLocale();
        Map<String, Map<String, Map<String, String>>> exp = new LinkedHashMap<String, Map<String, Map<String, String>>>();

        final String type = request.getParameter("type");
        final String max = request.getParameter("max");
        final String since = request.getParameter("since");
        String folder = request.getParameter("folder");
        String filePath = request.getParameter("file");

        // Valida parametros tipo y carpeta obligatorios
        if (validate(cmsObject, filePath, folder, type)) {

            // Parametros de fecha y máximo de recursos
            Long startDate = null;
            Integer maxResults = null;
            try {
                startDate = Long.parseLong(since);
            } catch(Exception e) {}

            try {
                maxResults = Integer.parseInt(max);
            } catch(Exception e) {}

            // Filtro de recursos
            List<CmsResource> resources = new ArrayList<CmsResource>();

            // Si es file
            if (StringUtils.isNotBlank(filePath)) {
                CmsResource resource = cmsObject.readResource(filePath);
                resources.add(resource);
            } else {

                // Si es folder
                final I_CmsResourceType resourceType = OpenCms.getResourceManager().getResourceType(type);
                folder = CmsStringUtil.isNotEmptyOrWhitespaceOnly(folder) ? folder : "/";
                CmsResourceFilter filter = CmsResourceFilter.DEFAULT_FILES.addRequireType(resourceType.getTypeId()).addExcludeFlags(CmsResource.FLAG_TEMPFILE).addExcludeTimerange();
                if (startDate != null) {
                    filter = filter.addRequireLastModifiedAfter(startDate);
                }
                List<CmsResource> find = cmsObject.readResources(folder, filter, true);

                // Max results
                if (maxResults != null) {
                    resources = maxResults < find.size() ? find.subList(0, maxResults) : find;
                } else {
                    resources = find;
                }
            }

            // Generar JSON para cada recurso obtenido
            for (CmsResource resource : resources) {

                // Id del recurso
                Map<String, String> content = new LinkedHashMap<String, String>();
                if (CmsResourceTypeXmlContent.isXmlContent(resource) || CmsResourceTypeXmlPage.isXmlPage(resource)) {
                    final CmsXmlContent unmarshal = CmsXmlContentFactory.unmarshal(cmsObject, cmsObject.readFile(resource));
                    final List<String> names = unmarshal.getNames(locale);
                    Collections.sort(names);
                    final List<String> paths = new ArrayList<String>();
                    for (int i = 0; i < names.size(); i++) {
                        if (i > 0 && names.get(i).startsWith(names.get(i - 1))) {
                            paths.remove(paths.size() - 1);
                        }
                        paths.add(names.get(i));
                    }

                    // Contenido
                    for (String path : paths) {
                        final I_CmsXmlContentValue value = unmarshal.getValue(path, locale);
                        if (value.isSimpleType()) {
                            if (value.getTypeName().equals("OpenCmsVfsFile")) {
                                content.put(path, getOnlineLink(cmsObject, value.getStringValue(cmsObject)));
                            } else {
                                content.put(path, value.getStringValue(cmsObject));
                            }

                        }
                    }
                } else if (isPointer(resource)) {
                    CmsFile link = cmsObject.readFile(cmsObject.getSitePath(resource), CmsResourceFilter.ALL);
                    String linkUrl = new String(link.getContents());
                    content.put("link", linkUrl);
                }


                // Propiedades
                HashMap<String, String> props = new HashMap<String, String>();
                List<CmsProperty> propList = cmsObject.readPropertyObjects(resource, false);
                for (CmsProperty prop : propList){
                    if (!prop.isNullProperty()) {
                        props.put(prop.getName(), prop.getValue());
                    }
                }

                HashMap<String, String> propsHeritance = new HashMap<String, String>();
                List<CmsProperty> propListHeritance = cmsObject.readPropertyObjects(resource, true);
                for (CmsProperty prop : propListHeritance){
                    if (!prop.isNullProperty()) {
                        propsHeritance.put(prop.getName(), prop.getValue());
                    }
                }
                propListHeritance.removeAll(props.entrySet());

                // Información propiea del fichero
                HashMap<String, String> file = new HashMap<String, String>();
                try {
                    file.put("type", OpenCms.getResourceManager().getResourceType(resource).getTypeName());
                    file.put("dateContent", Long.toString(resource.getDateContent()));
                    file.put("dateCreated", Long.toString(resource.getDateCreated()));
                    file.put("dateExpired", Long.toString(resource.getDateExpired()));
                    file.put("dateLastModified", Long.toString(resource.getDateLastModified()));
                    file.put("dateReleased", Long.toString(resource.getDateReleased()));
                    file.put("state", Integer.toString(resource.getState().getState()));
                } catch (Exception e){}

                // Relaciones
                HashMap<String, String> relations = new HashMap<String, String>();
                try{
                    List<CmsRelation> relationsForResource = cmsObject.getRelationsForResource(resource, CmsRelationFilter.ALL);
                    for (int i = 0; i < relationsForResource.size(); i++) {
                        CmsRelation relation = relationsForResource.get(i);
                        String sourceStructureId = relation.getSourceId().getStringValue();
                        String sourcePath = relation.getSourcePath();
                        String sourceId = identifyResource(sourceStructureId, sourcePath);

                        String targetStructureId = relation.getTargetId().getStringValue();
                        String targetPath = relation.getTargetPath();
                        String targetId = identifyResource(targetStructureId, targetPath);

                        relations.put(sourceId, targetId);
                    }
                } catch (Exception e){}

                Map<String, Map<String, String>> valueMap = new LinkedHashMap<String, Map<String, String>>();
                valueMap.put("content", content);
                valueMap.put("properties", props);
                valueMap.put("propertiesHeritance", propsHeritance);
                valueMap.put("file", file);
                valueMap.put("relations", relations);

                exp.put(identifyResource(resource), valueMap);
                //out.print(exp);
            }
        }
        try {

            // Por alguna razon hay que definir para el JSONObject un Map<String, Object>
            // o de lo contrario no realiza correctamente la conversion
            Map<String, Object> exportacion = new LinkedHashMap<String, Object>();
            for (String s : exp.keySet()) {
                exportacion.put(s, exp.get(s));
            }
            JSONObject jo = new JSONObject(exportacion);
            out.print(jo.toString());
        } catch (Exception e) {
            out.print("Excepcion");
            out.print("{}");
        }
    } catch (Exception e) {
        out.print("ERROR " + e.getMessage() + "<br/>\n" + CmsException.getStackTraceAsString(e));
    }
%>