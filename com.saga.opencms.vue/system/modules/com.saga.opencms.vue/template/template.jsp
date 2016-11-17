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

    <cms:headincludes type="javascript" defaults="%(link.weak:/system/modules/com.saga.opencms.vue/resources/js/vue.js)"/>

    <cms:enable-ade />
</head>

<body>

<cms:container name="container" type="center"
               width="1200" maxElements="1"/>

</body>
</html>