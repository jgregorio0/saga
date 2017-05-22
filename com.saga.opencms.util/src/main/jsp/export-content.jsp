<%@ page buffer="none" session="false" %>
<%@ page import="org.opencms.file.types.I_CmsResourceType" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
<%@ page import="org.opencms.xml.content.CmsXmlContent" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ page import="org.opencms.xml.types.I_CmsXmlContentValue" %>
<%@ page import="java.util.*" %>
<%@ page import="org.opencms.file.*" %>
<%@ page import="org.opencms.relations.CmsRelationFilter" %>
<%@ page import="org.opencms.relations.CmsRelation" %>

<%--
  Realiza la exportación a JSON. Los parámetros que recibe son:
  - type: Tipo del recurso que exportar
  - max: Número máximo de recursos que retornar
  - folder: Carpeta desde la que obtener los recursos
  - since: Fecha de última modificación expresada en milisegundos a partir de la que obtener los recurso
--%>

<%!
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
%>
<%
  final CmsObject cmsObject = CmsFlexController.getCmsObject(request);
  cmsObject.getRequestContext().setSiteRoot("");
  final CmsProject offline = cmsObject.readProject("Offline");
  cmsObject.getRequestContext().setCurrentProject(offline);
  final Locale locale = cmsObject.getRequestContext().getLocale();
  Map<String, Map<String, Map<String, String>>> exp = new LinkedHashMap<String, Map<String, Map<String, String>>>();

  final String type = request.getParameter("type");
  final String max = request.getParameter("max");
  final String since = request.getParameter("since");
  String folder = request.getParameter("folder");

  if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(type)) {
    Long startDate = null;
    Integer maxResults = null;
    try {
      startDate = Long.parseLong(since);
    } catch(Exception e) {

    }

    try {
      maxResults = Integer.parseInt(max);
    } catch(Exception e) {

    }


    final I_CmsResourceType resourceType = OpenCms.getResourceManager().getResourceType(type);
    folder = CmsStringUtil.isNotEmptyOrWhitespaceOnly(folder) ? folder : "/";
    CmsResourceFilter filter = CmsResourceFilter.DEFAULT_FILES.addRequireType(resourceType.getTypeId()).addExcludeFlags(CmsResource.FLAG_TEMPFILE).addExcludeTimerange();
    if (startDate != null) {
      filter = filter.addRequireLastModifiedAfter(startDate);
    }
    List<CmsResource> find = cmsObject.readResources(folder, filter, true);
    List<CmsResource> resources = new ArrayList<CmsResource>();
    if (maxResults != null) {
      resources = maxResults < find.size() ? find.subList(0, maxResults) : find;
    } else {
      resources = find;
    }

    for (CmsResource resource : resources) {

      // Map content
      Map<String, String> content = new LinkedHashMap<String, String>();

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

      // Map properties
      HashMap<String, String> props = new HashMap<String, String>();
      List<CmsProperty> propList = cmsObject.readPropertyObjects(resource, true);
      for (CmsProperty prop : propList){
        if (!prop.isNullProperty()) {
          props.put(prop.getName(), prop.getValue());
        }
      }

      // Map file info
      HashMap<String, String> file = new HashMap<String, String>();
      try {
        file.put("dateContent", Long.toString(resource.getDateContent()));
        file.put("dateCreated", Long.toString(resource.getDateCreated()));
        file.put("dateExpired", Long.toString(resource.getDateExpired()));
        file.put("dateLastModified", Long.toString(resource.getDateLastModified()));
        file.put("dateReleased", Long.toString(resource.getDateReleased()));
        file.put("state", Integer.toString(resource.getState().getState()));
      } catch (Exception e){}

      // Map relations
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
    out.print("Excepcion generando JSON");
    out.print("{}");
  }
%>