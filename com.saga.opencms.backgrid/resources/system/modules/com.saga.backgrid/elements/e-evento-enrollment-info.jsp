<%@ page import="java.util.Map" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.entity.InscripcionEntity" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.entity.UsuarioEntity" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.model.EstadoInscripcion" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%-- CSS para el modal --%>
<cms:headincludes type="css" defaults="%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/css/magnific-popup.css:7c7e194e-1e7e-11e6-84fb-0050569f78c7)"/>

<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.ausape.zonaprivada.messages">
<div>

	<%-- Header --%>
	<div class="row">
		<h4 class="col-sm-6 pull-left"><fmt:message key="form.inscripcion.title"/></h4>
		<p class="col-sm-6 pull-rigth">
			<a href="<cms:link>/zona-privada/inscripciones/</cms:link>">
				<fmt:message key="form.inscripcion.delete.mis.inscriciones"/>
				&nbsp;<i class="fa fa-arrow-circle-o-up" aria-hidden="true"></i>
			</a>
		</p>
	</div>


		<%-- Cuerpo de la inscripcion --%>
	<p>
		<fmt:message key="form.inscripcion.delete.body"/>
	</p>
	<dl>
		<%
			Map<String, Object> pMap = (Map<String, Object>) request.getAttribute("pMap");
			InscripcionEntity enroll = (InscripcionEntity) pMap.get("enroll");
			UsuarioEntity auUser = enroll.getUsuario();
			String estado = EstadoInscripcion.getValor(enroll.getEstado());

			pageContext.setAttribute("empresa", enroll.getNameEmpresa());
			SimpleDateFormat dmyhs = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
			pageContext.setAttribute("fechaEnroll", dmyhs.format(enroll.getFechaInscripcion()));
		%>
		<dt>Nombre</dt>
		<dd><%=auUser.getFirstName()%></dd>
		<dt>Apellidos</dt>
		<dd><%=auUser.getLastName()%></dd>
		<dt>Email</dt>
		<dd><%=enroll.getEmailUsuario()%></dd>
		<c:if test="${not empty empresa}">
			<dt>Empresa</dt>
			<dd>${empresa}</dd>
		</c:if>
		<dt>Fecha inscripcion</dt>
		<dd>${fechaEnroll}</dd>
		<dt>Estado inscripción</dt>
		<dd><%=estado%></dd>
	</dl>

	<%-- form cancelar inscripcion --%>
	<p>
		<fmt:message key="form.inscripcion.delete.body.btn"/>
	</p>
	<a href="#delete-insc-form" id="delete-insc-btn" class="btn btn-danger">
		<fmt:message key="form.inscripcion.delete.modal.btn"/>
	</a>

	<form class="white-popup-block mfp-hide" id="delete-insc-form" method="post">
		<h2><fmt:message key="form.inscripcion.delete.modal.title"/></h2>
		<p><fmt:message key="form.inscripcion.delete.modal.body"/></p>
		<input type="hidden" value="cancelInsc" name="cancelInsc"/>
		<button type="submit" class="btn btn-danger"><fmt:message key="form.inscripcion.delete.modal.btn"/></button>
		<button type="button" class="btn btn-success" onclick="$.magnificPopup.close();"><fmt:message key="form.inscripcion.delete.modal.btn.cancel"/></button>
	</form>


	<script type="text/javascript">
		$(document).ready(function() {
			$('#delete-insc-btn').magnificPopup({
				type: 'inline',
				preloader: false
			});
		});
	</script>
</div>
</cms:bundle>