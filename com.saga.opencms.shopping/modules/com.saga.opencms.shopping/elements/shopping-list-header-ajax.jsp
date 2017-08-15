<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<c:set var="dataSize">${param.dataSize}</c:set>
<c:set var="total">${param.total}</c:set>
<cms:include file="%(link.strong:/system/modules/com.jgregorio.opencms.fileupload/elements/shopping-list-header.jsp)">
    <cms:param name="dataSize">${dataSize}</cms:param>
    <cms:param name="dataSize">${total}</cms:param>
</cms:include>