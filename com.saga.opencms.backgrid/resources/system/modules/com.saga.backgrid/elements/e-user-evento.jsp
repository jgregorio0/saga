<%@page buffer="none" session="false" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.ausape.zonaprivada.messages">

	<div>
		<c:forEach items="${pMap.errors}" var="error">
			<div class="alert alert-danger" role="alert">
					${error}
			</div>
		</c:forEach>
		<c:forEach items="${pMap.infos}" var="info">
			<div class="alert alert-warning" role="warning">
					${info}
			</div>
		</c:forEach>
	</div>

	<%-- Formulario de inscripcion --%>
	<c:if test="${pMap.showForm}">
		<cms:include file="%(link.strong:/system/modules/com.saga.ausape.zonaprivada/elements/e-evento-form.jsp:f23d31c2-1e5c-11e6-bd55-0050569f78c7)"/>
	</c:if>

	<%-- Mostrar informacion de inscripcion --%>
	<c:if test="${pMap.showEnrollment}">
		<cms:include file="%(link.strong:/system/modules/com.saga.ausape.zonaprivada/elements/e-evento-enrollment-info.jsp:eb127a3f-1e5c-11e6-bd55-0050569f78c7)"/>
	</c:if>
</cms:bundle>