<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<c:set var="total">${param.total}</c:set>
<c:set var="dataSize">${param.dataSize}</c:set>
<c:choose>
    <c:when test="${total > dataSize}">
        Ver mas
    </c:when>
    <c:when test="${total > 0}">
        No hay mas resultados
    </c:when>
    <c:otherwise>
        No hay resultados
    </c:otherwise>
</c:choose>