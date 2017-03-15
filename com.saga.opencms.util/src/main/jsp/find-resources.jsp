<%@ page import="groovy.lang.GroovyObject" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="groovy.lang.GroovyClassLoader" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%!
    GroovyClassLoader groovyClassLoader;

    void invokeScript(
            CmsObject cmso, String path,
            String method, Object[] args) throws CmsException, UnsupportedEncodingException, IllegalAccessException, InstantiationException {
        GroovyClassLoader loader = instanceGroovyClassLoader();

        // Obtenmos el contenido del script
        byte[] scriptContent = cmso.readFile(
                cmso.readResource(path)).getContents();
        Class aClass = loader.parseClass(
                new String(scriptContent, "UTF-8"));
        GroovyObject groovyObject =
                (GroovyObject) aClass.newInstance();
        groovyObject.invokeMethod(method, args);
    }

    /**
     * Devuelve instancia de la clase GroovyClassLoader
     * @return
     */
    GroovyClassLoader instanceGroovyClassLoader() {
        if (groovyClassLoader == null) {
            groovyClassLoader = new GroovyClassLoader(
                    getClass().getClassLoader());
        }
        return groovyClassLoader;
    }
%>
<%
    CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
    String path = "/findResources";

%>