<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.file.types.I_CmsResourceType" %>
<%@ page import="org.opencms.i18n.CmsEncoder" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.lock.CmsLock" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%!
    private void lock(CmsObject cmso, String sitePath) throws CmsException {
        CmsLock lock = cmso.getLock(sitePath);
        if (lock.isUnlocked()) {

            // if resource is unlock then lock it
            cmso.lockResource(sitePath);
        } else if (!lock.isOwnedInProjectBy(
                cmso.getRequestContext().getCurrentUser(),
                cmso.getRequestContext().getCurrentProject())) {

            // if resource is locked by others steal lock
            cmso.changeLock(sitePath);
        }
    }

    private void unlock(CmsObject cmso, String sitePath) throws CmsException {
        CmsLock lock = cmso.getLock(sitePath);
        if (lock.isInherited()) {

            // unlock parent if it is inherited lock
            unlock(cmso, CmsResource.getParentFolder(sitePath));
        } else if (!lock.isUnlocked()) {

            // change lock to current if it is locked by others
            if (!lock.isOwnedInProjectBy(
                    cmso.getRequestContext().getCurrentUser(),
                    cmso.getRequestContext().getCurrentProject())) {
                cmso.changeLock(sitePath);
            }
            cmso.unlockResource(sitePath);
        }
    }
%>
<%
    String rootPath = "/";
    String type = "sglayoutrow";
    String origin = "/system/modules/com.saga.sagasuite.core/schemas/sglayoutrow.xsd";
    String target = "/system/modules/com.saga.sagasuite.layout/schemas/sglayoutrow.xsd";

    CmsObject baseCmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
    CmsObject cmso = OpenCms.initCmsObject(baseCmso);
    cmso.getRequestContext().setSiteRoot("/");
    cmso.getRequestContext().setUri("/");

    HashMap<String, Object> modifies = new HashMap<String, Object>();
    List<String> errors = new ArrayList<String>();

    I_CmsResourceType resourceType = OpenCms.getResourceManager().getResourceType(type);
    List<CmsResource> resources = cmso.readResources(rootPath, CmsResourceFilter.ALL.addRequireType(resourceType));
    for (int iRes = 0; iRes < resources.size(); iRes++) {
        CmsResource resource = resources.get(iRes);
        try {
            CmsFile file = cmso.readFile(resource);
            String strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);

            if (strContent.contains(origin)) {
                // replace and save
                String repContent = strContent.replaceAll(origin, target);
                lock(cmso, resource.getRootPath());
                file.setContents(repContent.getBytes(CmsEncoder.ENCODING_UTF_8));
                cmso.writeFile(file);
                unlock(cmso, resource.getRootPath());
                // add to report
                HashMap<String, String> replaces = new HashMap<String, String>();
                replaces.put(origin, target);
                modifies.put(resource.getRootPath(), replaces);
            }
        } catch (Exception e) {
            errors.add(resource.getRootPath() + " :: " + CmsException.getStackTraceAsString(e));
        }
    }

    pageContext.setAttribute("modifies", modifies);
    pageContext.setAttribute("errors", errors);
%>
<c:if test="${not empty errors}">
    <ol>
        <c:forEach items="${errors}" var="error">
            <li>${error}</li>
        </c:forEach>
    </ol>
</c:if>
<c:if test="${not empty modifies}">
    <ol>
        <c:forEach items="${modifies.entrySet()}" var="modified">
            <li>
                <h2>${modified.key}</h2>
                <ol>
                    <c:if test="${not empty modified.value}">
                        <c:forEach items="${modified.value.entrySet()}" var="replace">
                            <li>${replace.key} >> ${replace.value}</li>
                        </c:forEach>
                    </c:if>
                </ol>
            </li>
        </c:forEach>
    </ol>
</c:if>