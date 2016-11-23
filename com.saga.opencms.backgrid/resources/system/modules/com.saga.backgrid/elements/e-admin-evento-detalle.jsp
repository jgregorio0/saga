<%@ page import="com.saga.ausape.zonaprivada.inscripciones.view.AdminEventoDetalleController" %>
<%@page buffer="none" session="false" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
	AdminEventoDetalleController controller = new AdminEventoDetalleController(pageContext, request, response);
	controller.handleRequest();
%>
<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.ausape.zonaprivada.messages">
	<%--<cms:formatter var="content" val="value" rdfa="rdfa">--%>
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
	<c:if test="${not empty content}">

		 <%-- Cargamos la variable con el id especifico del recurso para su uso posterior--%>
		<c:set var="idresource" value="${content.id}" scope="request"></c:set>

		 <article class="articulo parent element evento org-opencms-ade-containerpage-client-ui-css-I_CmsLayoutBundle-I_CmsDragDropCss-dragElement">
			 <div class="wrapper">

				 <!-- Cabecera del articulo -->
				 <header class="headline">
					 <c:if test="${value.SubTitle.isSet }">
					 <hgroup>
						 </c:if>
						<span class="time">
						<cms:include file="%(link.strong:/system/modules/com.saga.ausape.frontend/elements/e-time-period.jsp:4e8623bb-0c8b-11e6-8adc-7fb253176922)">
							<cms:param name="inidate">${value.FichaEvento.value.FechaInicio }</cms:param>
							<c:if test="${value.FichaEvento.value.FechaFin.isSet }">
								<cms:param name="findate">${value.FichaEvento.value.FechaFin }</cms:param>
							</c:if>
							<cms:param name="showtime">false</cms:param>
						</cms:include>
						</span>
						 <h1 class="title" ${rdfa.Title}>${value.Title}</h1>
						 <c:if test="${value.SubTitle.isSet }">
						 <h2 class="subtitle" ${rdfa.SubTitle}>${value.SubTitle}</h2>
					 </hgroup>
					 </c:if>
				 </header>


				 <%-- Listado de inscripciones asociadas al evento --%>
				 <h4><fmt:message key="admin.evento.detail.inscripciones.title"/></h4>
				 <p><fmt:message key="admin.evento.detail.inscripciones.body"/></p>

				 <cms:include file="%(link.strong:/system/modules/com.saga.ausape.zonaprivada/elements/e-admin-inscripciones-evento-list.jsp:0aca5a9a-20e2-11e6-a4cf-0050569f78c7)">
					 <cms:param name="eventId" value="${idresource}"/>
				 </cms:include>

				 <hr/>

				 <%-- Info del evento --%>
				 <p>
					 <button class="btn btn-primary" type="button" data-toggle="collapse" data-target="#masInfo" aria-expanded="false" aria-controls="masInfo">
						 <fmt:message key="admin.evento.detail.mas.info.btn"/>
					 </button>
				 </p>
				 <div class="collapse" id="masInfo">
					 <c:if test="${value.FichaEvento.exists}">
						 <c:set var="fichaevento" value="${value.FichaEvento}" scope="request"></c:set>
					 </c:if>

						 <%-- Pintamos la ficha y el media principal --%>
					 <c:set var="contentPrincipal" value="${content}" scope="request"></c:set>
					 <cms:include file="%(link.strong:/system/modules/com.saga.ausape.frontend/elements/e-evento-block.jsp:4e764538-0c8b-11e6-8adc-7fb253176922)">
						 <cms:param name="count">${status.count}</cms:param>
					 </cms:include>

						 <%-- Pintamos los bloques de contenido --%>
					 <c:if test="${value.Content.exists}">

						 <c:forEach var="elem" items="${content.valueList.Content}" varStatus="status">
							 <c:set var="contentblock" value="${elem}" scope="request"></c:set>
							 <cms:include file="%(link.strong:/system/modules/com.saga.sagasuite.core/elements/e-default-contentblock.jsp:e9a36d37-df65-11e4-bcf9-01e4df46f753)">
								 <cms:param name="contentBlockCount">${status.count }</cms:param>
								 <cms:param name="tabs">false</cms:param>
							 </cms:include>
						 </c:forEach>

					 </c:if> <%-- Fin cierre comprobacion de seccion --%>

					 <!-- Pie del articulo -->
					 <footer <c:if test="${value.ShowFooter != 'true' }">class="hide"</c:if>>
						 <div class="posted">
							 <span class="fa fa-calendar" aria-hidden="true"></span>&nbsp;&nbsp;
							 <cms:include file="%(link.strong:/system/modules/com.saga.sagasuite.core/elements/e-time.jsp:ea2c737f-df65-11e4-bcf9-01e4df46f753)">
								 <cms:param name="date">${value.Date }</cms:param>
								 <cms:param name="showtime">false</cms:param>
							 </cms:include>
						 </div>
						 <c:if test="${value.Author.exists }">
							 <div class="autor"><i class="fa fa-user"></i>&nbsp;&nbsp;${value.Author.value.Author }</div>
						 </c:if>
						 <c:if test="${value.Source.isSet }">
							 <div class="fuente"><i class="fa fa-book"></i>&nbsp;&nbsp;${value.Source}</div>
						 </c:if>
					 </footer>
				 </div>
			 </div> <!-- Fin de wrapper -->
		 </article>
	</c:if>
</div>
</cms:bundle>