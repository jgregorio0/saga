<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="${cms.locale}" />
<!DOCTYPE html>

<html lang="${cms.locale}" ${googleShareHead}>
<head>
    <title><cms:info property="opencms.title" /></title>
    <meta charset="${cms.requestContext.encoding}">


    <%--JQUERY--%>
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha256-k2WSCIexGzOj3Euiig+TlR8gA0EmPjuc79OEeY5L45g=" crossorigin="anonymous"></script>

    <%--BOOTSTRAP--%>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>



    <%--VUE JS--%>
    <c:if test="${not cms.isOnlineProject}">
        <cms:headincludes type="javascript" defaults="%(link.weak:/system/modules/com.saga.opencms.vue/resources/js/vue-2.4.0.js:ab44a97c-86f6-11e7-8cd2-f53d2dda7236)"/>
    </c:if>
    <c:if test="${cms.isOnlineProject}">
        <script src="https://unpkg.com/vue"></script>
    </c:if>

    <%--AXIOS--%>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>

    <cms:enable-ade />
</head>

<body>
<cms:container name="container" type="center"
               width="1200" maxElements="1"/>
</body>
</html>