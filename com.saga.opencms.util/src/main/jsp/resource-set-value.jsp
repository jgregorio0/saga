<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="java.util.List" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.xml.CmsXmlEntityResolver" %>
<%@ page import="org.opencms.i18n.CmsEncoder" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.xml.content.CmsXmlContent" %>
<%@ page import="org.opencms.xml.types.I_CmsXmlContentValue" %>
<%@ page import="org.opencms.xml.CmsXmlUtils" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.opencms.lock.CmsLock" %>
<%@ page import="org.opencms.db.CmsPublishList" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<%!


    String type;
    String site;
    CmsObject cmso;

    Locale locale;
    CmsFile file;
    String strContent;
    CmsXmlContent xmlContent;
    List<CmsResource> pubList;

    private boolean validate(CmsResource res) {
        return isGeneral(site, res) || isTransparencia(site, res);
    }

    private void addDefaultFilterValue(CmsObject cmso, CmsResource res)
            throws Exception {
        locale = cmso.getRequestContext().getLocale();

        //Leemos el recurso
        file = cmso.readFile(cmso.getSitePath(res), CmsResourceFilter.ALL);
        strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
        CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(cmso);
        xmlContent = CmsXmlContentFactory.unmarshal(cmso, file);

        // Modificamos el campo
        setStringValue("/SolrFieldQuery",
                "fq=((type:sgindicadortransparencia AND xmlactive_es:true) OR (type:(* AND NOT sgindicadortransparencia)))");
        save(res);
    }

    public void setStringValue(String path, String value)
            throws Exception {
        setStringValue(path, value, 0);
    }

    public void setStringValue(String path, String value, int pos)
            throws Exception {
        I_CmsXmlContentValue content = procureValue(path, pos);
        content.setStringValue(cmso, value);
    }

    public I_CmsXmlContentValue procureValue(String path, int pos)
            throws Exception {
        if (xmlContent == null) {
            throw new Exception("xml content not exists for resource $path".toString());
        }
        if (xmlContent.hasValue(path, locale, pos)) {
            return xmlContent.getValue(path, locale, pos);
        } else {

            // Si no existe aseguramos que el padre exista
            if (CmsXmlUtils.isDeepXpath(path)) {
                String parentPath = CmsXmlUtils.createXpath(CmsXmlUtils.removeLastXpathElement(path), 1);
                return procureValue(parentPath, obtainNodeIndex(parentPath));
            }

            // Comprobamos que al crear el padre no se haya creado automaticamente el hijo
            if (xmlContent.hasValue(path, locale, pos)) {
                return xmlContent.getValue(path, locale, pos);
            } else {
                return xmlContent.addValue(cmso, path, locale, pos);
            }
        }
    }

    private int obtainNodeIndex(String path) {
        int index = CmsXmlUtils.getXpathIndexInt(path);
        if (index > 0) {
            index = index - 1;
        }
        return index;
    }

    public void save(CmsResource res)
            throws CmsException {
        String path = cmso.getSitePath(res);
        lock(cmso, path);
        file.setContents(xmlContent.marshal());
        cmso.writeFile(file);
        unlock(cmso, path);
    }

    public void lock(CmsObject cmso, String path)
            throws CmsException {
        CmsLock lock = cmso.getLock(path);
        if (!lock.isUnlocked() &&
                !lock.isOwnedBy(cmso.getRequestContext().getCurrentUser())) {
            cmso.changeLock(path);
        }
        cmso.lockResource(path);
    }


    public void unlock(CmsObject cmso, String path)
            throws CmsException {
        CmsLock lock = cmso.getLock(path);
        if (lock.isInherited()) {
            unlock(cmso, CmsResource.getParentFolder(path));
        } else if (!lock.isUnlocked()) {
            if (!lock.isOwnedBy(cmso.getRequestContext().getCurrentUser())) {
                cmso.changeLock(path);
            }
            cmso.unlockResource(path);
        }
    }

    private void publishAll() throws CmsException {
        publish(pubList);
    }

    private void addPublish(CmsResource res) {
        if (pubList == null) {
            pubList = new ArrayList<CmsResource>();
        }
        pubList.add(res);
    }


    private void publishOne(CmsResource res) throws Exception {
        OpenCms.getPublishManager().publishResource(cmso, cmso.getSitePath(res));
    }

    public CmsPublishList publish(List<CmsResource> pubList) throws CmsException {
        CmsPublishList toPublish = OpenCms.getPublishManager().getPublishList(cmso, pubList, true, true);
        OpenCms.getPublishManager().publishProject(cmso, null, toPublish);
        return toPublish;
    }


    private boolean isTransparencia(String site, CmsResource res) {
        return res.getRootPath().contains(CmsStringUtil.joinPaths(site, "/es/.content/sgsearchadvanced/"));
    }

    private boolean isGeneral(String site, CmsResource res) {
        return res.getRootPath().contains(CmsStringUtil.joinPaths(site, "/es/transparencia/.content/sgsearchadvanced/"));
    }
%>


<%
    type = "sgsearchadvanced";
    site = "/";
    cmso = CmsFlexController.getCmsObject(request);
    CmsResourceFilter filter = CmsResourceFilter.ALL.addRequireFile();
    out.print("<div>");
    if (!StringUtils.isEmpty(type)) {
        filter = filter.addRequireType(
                OpenCms.getResourceManager().getResourceType(type));
        List<CmsResource> resources = cmso.readResources(site, filter, true);
        out.print("<ul>");
        for (int i = 0; i < resources.size(); i++) {
            CmsResource res = resources.get(i);
            out.print("<li>" + i + " - " + res.getRootPath() + "</li>");
            if (validate(res)) {
                try {
                    addDefaultFilterValue(cmso, res);
                    addPublish(res);
                    publishOne(res);
                    out.print("<p>MODIFICADO</p>");
                } catch (Exception e) {
                    out.print("<p>ERROR editando recurso </p>" + res.getRootPath());
                    out.print("<p>" + e.getCause().toString() + "</p>");
                }

            }
        }
    /*try {
      publishAll();
      out.print("<p>PUBLICADOS LOS RECURSOS</p>");
    } catch (Exception e) {
      out.print("<p>ERROR publicando recursos</p>");
      out.print("<p>" + e.getCause().toString() + "</p>");
    }*/
        out.print("</ul>");
    } else {
        out.print("<h2>" + "Debe incluir el parametro 'type'" + "</h2>");
    }
    out.print("</div>");
%>

