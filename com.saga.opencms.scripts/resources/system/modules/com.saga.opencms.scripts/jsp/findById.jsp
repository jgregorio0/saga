<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.util.CmsUUID" %>

<%@taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Ejecucion Script Groovy</title>
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
            <form>
                <input type="text" id="id" name="id" value="${param.id}">
                <button type="submit">Buscar</button>
            </form>
            <%
                CmsResource res = null;
                String error = null;
                String id = request.getParameter("id");
                if (id != null) {
                    CmsObject cmso = CmsFlexController.getCmsObject(request);
                    try {
                        res = cmso.readResource(CmsUUID.valueOf(id));
                    } catch (Exception e) {
                        error = CmsException.getStackTraceAsString(e);
                    }
                }
            %>
            <c:choose>
                <c:when test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:when>
                <c:otherwise>
                    <p>Recurso: <%=res != null ? res.getRootPath() : "BUSCA..."%>
                    </p>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>
</body>
</html>