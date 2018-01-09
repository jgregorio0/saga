<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.xml.content.CmsXmlContent" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@page buffer="none" session="false"%>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Cargamos las variables que luego vamos a ir necesitando a lo largo --%>

<%!
    private boolean validate(CmsResource res) {
//        return isGeneral(site, res) || isTransparencia(site, res);
        return true;
    }

    private String readValue(CmsObject cmso, CmsResource res, Locale locale, String xmlPath, int xmlIndex)
            throws Exception {

        //Leemos el recurso
        CmsFile file = cmso.readFile(cmso.getSitePath(res), CmsResourceFilter.ALL);
        CmsXmlContent xmlContent = CmsXmlContentFactory.unmarshal(cmso, file);
        return xmlContent.getStringValue(cmso, xmlPath, locale, xmlIndex);
    }

    /*private void addDefaultFilterValue(CmsObject cmso, CmsResource res)
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
    }*/

    /*public void setStringValue(String path, String value)
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
    }*/
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Script de consulta y modificación del valor de los campos de recursos</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="SAGA SOLUCIONES - OpenCms Partners">

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>

</head>

<body>
<div class="container-fluid">
    <div class="row clearfix">
        <div class="col-md-12 column">

            <h1>Ejecuci&oacute;n script</h1>

            <form method="post">
                <div class="form-group">
                    <label for="type">Tipo de recurso</label>
                    <input type="text" class="form-control" id="type" name="type" value="<c:out value="${param.type }"/>" placeholder="sgthemeconfig">
                </div>

                <div class="form-group">
                    <label for="site">Carpeta padre</label>
                    <input type="text" class="form-control" id="site" name="site" value="<c:out value="${param.site }" default="/"/>">
                </div>

                <div class="form-group">
                    <label for="locale">Locale</label>
                    <input type="text" class="form-control" id="locale" name="locale" value="<c:out value="${param.locale }" default="es"/>">
                </div>

                <div class="form-group">
                    <label for="xmlPath">Campo</label>
                    <input type="text" class="form-control" id="xmlPath" name="xmlPath" value="<c:out value="${param.xmlPath }"/>" placeholder="Theme">
                </div>

                <div class="form-group">
                    <label for="xmlIndex">Índice del campo</label>
                    <input type="text" class="form-control" id="xmlIndex" name="xmlIndex" value="<c:out value="${param.xmlIndex }" default="0"/>">
                </div>

                <div class="form-group">
                    <label>Indique el modo de ejecución:</label></br>
                    <input type="radio" name="mode" value="show" <c:if test='${param.mode eq "show"}'>checked</c:if>>Mostrar valor</br>
                    <input type="radio" name="mode" value="set" <c:if test='${param.mode eq "set"}'>checked</c:if>>Modificar</br>
                </div>

                <input type="hidden" value="true" name="exec"/>
                <input class="btn btn-success" type="submit" value="Ejecutar!"/>
            </form>


            <c:if test="${param.exec != null and param.type!=null and param.site!=null and param.mode!=null and param.xmlPath!=null and param.xmlIndex!=null}">
                <%
                    CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();

                    String type = "sgthemeconfig";
                    String site = "/";
                    String mode = "show";
                    String xmlPath = "Theme";
                    int xmlIndex = 0;
                    String localeParam = request.getParameter("locale");
                    Locale locale = localeParam == null ? cmso.getRequestContext().getLocale() : new Locale(localeParam);

                    Map<String, String> messages = new HashMap<String, String>();
                    CmsResourceFilter filter = CmsResourceFilter.ALL.addRequireFile();
                    if (!StringUtils.isEmpty(type)) {
                        filter = filter.addRequireType(
                                OpenCms.getResourceManager().getResourceType(type));
                        List<CmsResource> resources = cmso.readResources(site, filter, true);
                        for (int i = 0; i < resources.size(); i++) {
                            CmsResource res = resources.get(i);
                            String path = res.getRootPath();
                            String msg = "";
                            if (validate(res)) {
                                try {
                                    if (mode.equals("show")) {
                                        msg = readValue(cmso, res, locale, xmlPath, xmlIndex);
                                    }
                                } catch (Exception e) {
                                    msg = "ERROR: " + e.getCause().toString() + " || \n" + CmsException.getStackTraceAsString(e);
                                }
                            }
                            messages.put(path, msg);
                        }
                    }
                %>

                <div>
                    <h1>Results (<%=messages.size()%>)</h1>
                    <c:if test="<%=messages.size() > 0%>">
                        <c:forEach items="<%=messages.entrySet()%>" var="entry" varStatus="status">
                            ${status.count};${entry.key};${entry.value}<br/>
                        </c:forEach>
                    </c:if>
                </div>
            </c:if>

        </div>
    </div>
</div>
</body>
</html>