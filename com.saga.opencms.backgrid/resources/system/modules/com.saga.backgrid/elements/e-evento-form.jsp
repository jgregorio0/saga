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

	<%-- form --%>
	<p>
		<fmt:message key="form.inscripcion.body"/>
	</p>
	<a href="#inscribirse-form" id="inscribirse-btn" class="btn btn-success">
		<fmt:message key="form.inscripcion.modal.btn"/>
	</a>
	<form class="white-popup-block mfp-hide" id="inscribirse-form" method="post">
		<h2><fmt:message key="form.inscripcion.modal.title"/></h2>
		<p><fmt:message key="form.inscripcion.modal.body"/></p>
		<input type="hidden" value="inscribir" name="inscribir"/>
		<button type="submit" class="btn btn-success"><fmt:message key="form.inscripcion.modal.btn"/></button>
		<button type="button" class="btn btn-danger" onclick="$.magnificPopup.close();"><fmt:message key="form.inscripcion.modal.btn.cancel"/></button>
	</form>

	<script type="text/javascript">
		$(document).ready(function() {
			$('#inscribirse-btn').magnificPopup({
				type: 'inline',
				preloader: false
			});
		});
	</script>
</div>
</cms:bundle>