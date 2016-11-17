<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<c:set var="module" scope="request">/system/modules/com.saga.opencms.fileupload</c:set>
<c:set var="moduleUri" scope="request"><cms:link>${module}</cms:link></c:set>

<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <title>Mini Ajax File Upload Form</title>

    <!-- Google web fonts -->
    <link href="http://fonts.googleapis.com/css?family=PT+Sans+Narrow:400,700" rel='stylesheet' />

    <!-- The main CSS file -->
    <link href="${moduleUri}/resources/css/style.css" rel="stylesheet" />


    <!-- JavaScript Includes -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
    <script src="${moduleUri}/resources/js/jquery.knob.js"></script>

    <!-- jQuery File Upload Dependencies -->
    <script src="${moduleUri}/resources/js/jquery.ui.widget.js"></script>
    <script src="${moduleUri}/resources/js/jquery.iframe-transport.js"></script>
    <script src="${moduleUri}/resources/js/jquery.fileupload.js"></script>
</head>

<body>

<cms:include file="${module}/elements/advanced-form.jsp"/>

</body>
</html>