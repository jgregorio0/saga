<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.sigmagrid.table.workplace">
<cms:formatter var="content" val="value" rdfa="rdfa">
<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
				<h1 class="title">
					<fmt:message key="text.default.Title"/>
				</h1>
	</c:when>
<c:otherwise>

<c:if test="${cms.element.setting.marginbottom.value != '0'}">
    <c:set var="marginClass">margin-bottom-${cms.element.setting.marginbottom.value}</c:set>
</c:if>

<c:if test="${not empty cms.element.settings.classmainbox }">
    <c:set var="classmainbox">${cms.element.settings.classmainbox}</c:set>
</c:if>

<article class="articulo element parent <c:out value=' ${marginClass} ${classmainbox} ' />">

	<div class="wrapper <c:out value='${value.CssClass} ' />">

		<c:if test="${not cms.element.settings.hidetitle}">
			<header class="headline">
				<h2 class="title" ${rdfa.Title}>${value.Title}</h2>
			</header>
		</c:if>

		<c:if test="${value.JSPList.exists and value.JSPController.exists and value.JSPResources.exists}">

			<%-- Carga de Recursos --%>
			<%-- TODO include in format config --%>
			<cms:include file="${value.JSPResources}">
				<cms:param name="contextpath"><cms:link>/system/modules/com.saga.sigmagrid.table</cms:link></cms:param>
			</cms:include>

			<%-- Carga la lista --%>
			<cms:include file="${value.JSPList}">
				<cms:param name="controller"><cms:link>${value.JSPController}</cms:link></cms:param>
				<cms:param name="dateformat">${value.DateFormat}</cms:param>
				<cms:param name="pool">${value.OpenCmsPool}</cms:param>
			</cms:include>
		</c:if>

	</div>
</article>

</c:otherwise>
</c:choose>
</cms:formatter>
</cms:bundle>