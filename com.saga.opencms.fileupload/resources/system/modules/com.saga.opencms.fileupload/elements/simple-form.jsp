<%@ page import="com.saga.opencms.fileupload.FileUpload" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%
	FileUpload view = new FileUpload(pageContext, request, response);
	view.handleRequest();
%>
<fmt:setLocale value="${cms.locale}"/>
<cms:bundle basename="com.saga.opencms.fileupload">
	<c:set var="formButton"><fmt:message key="upload.form.button"/></c:set>
	<c:set var="formLabel"><fmt:message key="upload.form.label"/></c:set>
	<c:set var="formTitle"><fmt:message key="upload.form.header"/></c:set>
	<c:set var="formBottomText"><fmt:message key="upload.form.bottomtext"/></c:set>

<div>



		<%-- Formulario de subida de fichero --%>
	<div class="webform">
		<form method="POST" enctype="multipart/form-data" >
			<div style="display: none;"><input type="hidden" value="submit" id="formaction" name="formaction"></div>
			<div class="webform_wrapper">
				<div class="webform_toptext">
					<header>
						<hgroup>
							<h2 class="title">${formTitle}</h2>
						</hgroup>
					</header>
				</div>
				<div class="webform_row">
					<div class="webform_label">
						<label for="file">${formLabel}</label>
					</div>
					<div class="webform_field">
						<input type="file" name="file" id="file" value="<c:out value='${param.file}'/>" placeholder="Seleccione un fichero"/> <br/>
					</div>
				</div>
				<div class="webform_button">
					<input type="submit" class="formbutton btn submitbutton" value="${formButton}" name="upload" id="upload" />
				</div>

				<div class="webform_bottomtext">
					<h6><strong><small>${formBottomText}</small></strong></h6>
				</div>
			</div>
		</form>
	</div>

		<%-- Mensajes de errores --%>
	<c:forEach items="${errors}" var="error" varStatus="status">
		<c:if test="${status.first}">
			<ul class="alert alert-danger">
		</c:if>
		<li>${error}</li>
		<c:if test="${status.last}">
			</ul>
		</c:if>
	</c:forEach>

		<%-- Mensajes de informacion --%>

		<c:forEach items="${infos}" var="info" varStatus="status">
			<c:if test="${status.first}">
				<ul class="alert alert-info">
			</c:if>
			<li>${info}</li>
			<c:if test="${status.last}">
				</ul>
			</c:if>
		</c:forEach>

</div>
</cms:bundle>