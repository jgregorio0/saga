<%@ page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.file.types.I_CmsResourceType" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
<%@ page import="org.opencms.xml.content.CmsXmlContent" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ page import="org.opencms.xml.types.I_CmsXmlContentValue" %>
<%@ page import="java.util.*" %>
<%@ page import="org.opencms.file.CmsProject" %>

<%--
  Realiza la exportación a JSON. Los parámetros que recibe son:
  - type: Tipo del recurso que exportar
  - max: Número máximo de recursos que retornar
  - folder: Carpeta desde la que obtener los recursos
  - since: Fecha de última modificación expresada en milisegundos a partir de la que obtener los recurso
--%>

<%!
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
    Map<String, Map<String, String>> exp = new LinkedHashMap<String, Map<String, String>>();

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
            exp.put(resource.getStructureId().getStringValue() + "@" + resource.getRootPath(), content);
        }
    }
    try {
        JSONObject jo = new JSONObject(exp);
        out.print(jo.toString());
    } catch (Exception e) {
        out.print("{}");
    }


%>