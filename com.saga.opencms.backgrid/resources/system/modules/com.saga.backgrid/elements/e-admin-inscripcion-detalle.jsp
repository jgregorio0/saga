<%@ page import="com.saga.ausape.zonaprivada.inscripciones.entity.InscripcionEntity" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.entity.UsuarioEntity" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.view.AdminInscripcionDetalleController" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.util.EventosUtil" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.model.EstadoInscripcion" %>
<%@page buffer="none" session="false" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	AdminInscripcionDetalleController controller = new AdminInscripcionDetalleController(pageContext, request, response);
	controller.handleRequest();
%>

<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.ausape.zonaprivada.messages">
	<div>
		<h2><fmt:message key="admin.enroll.detail.title"/></h2>

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

		<c:set var="enroll" value="${pMap.enroll}"/>
		<c:if test="${not empty enroll}">
			<p><fmt:message key="admin.enroll.detail.body"/></p>
			<%
				InscripcionEntity enroll = (InscripcionEntity) pageContext.getAttribute("enroll");
				CmsObject cmso = CmsFlexController.getCmsObject(request);
				CmsResource event = EventosUtil.readResource(cmso, enroll.getUuidEvento());
				UsuarioEntity usuario = enroll.getUsuario();
			%>
			<dl>
				<dt>Id</dt>
				<dd>${enroll.id}</dd>

				<dt>Evento</dt>
				<dd>
					<a href="<cms:link><%=EventosUtil.getPath(cmso, event)%></cms:link>">
						<%=EventosUtil.getProperty(cmso, event, "Title")%>
					</a>
				</dd>

				<dt>Usuario</dt>
				<dd><%=usuario.getFirstName()%> <%=usuario.getLastName()%> (<%=usuario.getEmailUsuario()%>)</dd>

				<dt>Nombre Empresa</dt>
				<dd>${enroll.nameEmpresa}</dd>
				<dt>Fecha inscripción</dt>
				<dd>${enroll.fechaInscripcion}</dd>
				<dt>Última modificación</dt>
				<dd>${enroll.fechaModificacion}</dd>
				<dt>Estado</dt>
				<dd><%=EventosUtil.getState(enroll)%></dd>
			</dl>

			<%-- Modal inscripcion --%>
			<form id="insc-form">
				<p><fmt:message key="admin.enroll.detail.btn.txt"/></p>
				<input id="inI" type="hidden" name="i" value="${enroll.id}"/>
				<input id="inE" type="hidden" name="e" value="${param.e}"/>

				<%-- Mostramos las opciones del administrador --%>
				<%
//					boolean todos = enroll.getEstado() == EstadoInscripcion.INSCRITO.getValor();
					boolean esAceptado = enroll.getEstado() == EstadoInscripcion.ACEPTADO.getValor();
					boolean esEnReserva = enroll.getEstado() == EstadoInscripcion.EN_RESERVA.getValor();
					boolean esRechazado = enroll.getEstado() == EstadoInscripcion.RECHAZADO.getValor();
//					boolean esCancelado = enroll.getEstado() == EstadoInscripcion.CANCELADO.getValor();
//					pageContext.setAttribute("todos", todos);
					pageContext.setAttribute("esAceptado", esAceptado);
					pageContext.setAttribute("esEnReserva", esEnReserva);
					pageContext.setAttribute("esRechazado", esRechazado);
				%>
				<c:if test="${not esAceptado}">
					<input id="onAccept" type="submit" class="btn btn-success" name="accept" value="<fmt:message key='admin.enroll.detail.btn.accept'/>"/>
				</c:if>
				<c:if test="${not esRechazado}">
					<input id="onRefuse" type="submit" class="btn btn-danger" name="refuse" value="<fmt:message key='admin.enroll.detail.btn.refuse'/>"/>
				</c:if>
				<c:if test="${not esEnReserva}">
					<input id="onHold" type="submit" class="btn btn-warning" name="hold" value="<fmt:message key='admin.enroll.detail.btn.hold'/>"/>
				</c:if>
			</form>

			<%-- Añadimos el procedimiento para que se ejecute como modal --%>
			<script>
				$("#onAccept").on( "click", function(e){
					e.preventDefault();
					$.post( "<cms:link>/system/modules/com.saga.ausape.zonaprivada/elements/e-admin-inscripcion-detalle.jsp</cms:link>",
							{ i: $("#inI").val(), accept: $("#onAccept").val() })
							.done(function( data ) {
								$("#insc-detail").empty().append(data);
							});
				})
				$("#onRefuse").on( "click", function(e){
					e.preventDefault();
					$.post( "<cms:link>/system/modules/com.saga.ausape.zonaprivada/elements/e-admin-inscripcion-detalle.jsp</cms:link>",
							{ i: $("#inI").val(), refuse: $("#onRefuse").val() })
							.done(function( data ) {
								$("#insc-detail").empty().append(data);
							});
				})
				$("#onHold").on( "click", function(e){
					e.preventDefault();
					$.post( "<cms:link>/system/modules/com.saga.ausape.zonaprivada/elements/e-admin-inscripcion-detalle.jsp</cms:link>",
							{ i: $("#inI").val(), hold: $("#onHold").val() })
							.done(function( data ) {
								$("#insc-detail").empty().append(data);
							});
				})
			</script>
		</c:if>
	</div>
</cms:bundle>