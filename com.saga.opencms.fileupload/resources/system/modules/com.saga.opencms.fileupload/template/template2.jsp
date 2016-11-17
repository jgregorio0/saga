<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="${cms.locale}" />
<c:set var="module" scope="request">/system/modules/com.saga.opencms.fileupload</c:set>

<!DOCTYPE html>
<html lang="${cms.locale}">
<head>

    <title><cms:info property="opencms.title" /></title>
    <meta charset="${cms.requestContext.encoding}">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"><%-- Activamos para que Internet explorer muestra la página sin modo de compatibilidad en la versión exacta del navegador --%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"> <%-- Mantener proporciones partiendo del ancho del dispositivo donde se reproduce --%>
    <meta name="description" content="${descriptionpage }">
    <meta name="keywords" content="${keywordspage }">
    <meta name="robots" content="index, follow">
    <meta name="revisit-after" content="7 days">

    <cms:headincludes type="css" defaults="%(link.weak:/system/modules/com.saga.opencms.fileupload/resources/css/style.css)"/>

    <!-- JavaScript Includes -->
    <%--<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>--%>

    <!-- The JS files -->
    <cms:headincludes type="javascript" defaults="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js
        |%(link.weak:/system/modules/com.saga.opencms.fileupload/resources/js/jquery.knob.js)
        |%(link.weak:/system/modules/com.saga.opencms.fileupload/resources/js/jquery.ui.widget.js)
        |%(link.weak:/system/modules/com.saga.opencms.fileupload/resources/js/jquery.fileupload.js)" />
</head>
<body>

<cms:include file="${module}/elements/advanced-form.jsp"/>
<%--<cms:enable-ade/>--%>
<%--<cms:container name="form" type="page" width="1200"  maxElements="1" editableby="ROLE.DEVELOPER"/>--%>

</body>
</html>