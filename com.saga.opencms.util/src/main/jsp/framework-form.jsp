<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%--
<%@page import="org.opencms.main.OpenCms"%>
<%@page import="org.opencms.util.CmsStringUtil"%>
<%@page import="groovy.lang.Binding"%>
<%@page import="groovy.util.GroovyScriptEngine"%>
<%@page import="org.apache.commons.logging.Log"%>
<%@page import="org.opencms.main.CmsLog"%>
<%
	Log LOG = CmsLog.getLog(this.getClass());

	String classesPath = "/classes";
	String modulePath = "/com/saga/cedinox/suscriptores";
	String scriptName = "controller/FormSuscripcionNewsletter.groovy";
	String dirClasses = OpenCms.getSystemInfo().getWebInfRfsPath() + classesPath;
	String rfsClassesModule = CmsStringUtil.joinPaths(dirClasses, modulePath);

	GroovyScriptEngine engine = new GroovyScriptEngine(rfsClassesModule);
	Binding binding = new Binding();
	binding.setProperty("b_pageContext", pageContext);
	binding.setProperty("b_request", request);
	binding.setProperty("b_response", response);
	binding.setProperty("b_engine", engine);
	try {
		engine.run(scriptName, binding);
	} catch (Exception e) {
		LOG.error(e, e);
	}
%>
--%>

<%@ page import="com.saga.cedinox.suscriptores.controller.FormSuscripcionNewsletter" %>
<%
    new FormSuscripcionNewsletter(pageContext, request, response).handler();
%>

<fmt:setLocale value="${cms.locale}"/>
<cms:bundle basename="com.saga.cedinox.suscriptores.messages">
    <div class="webform">
        <c:if test='${not empty errors.get("error.general")}'>
            <div class="alert alert-danger">${errors.get("error.general")}</div>
        </c:if>
        <c:if test="${showForm}">
            <form id="suscriptores-form" method="post">
                <div class="webform_wrapper">
                    <div class="webform_toptext">
                        <header>
                            <hgroup>
                                <h2 class="title">Modificar Suscripcion a la Newsletter</h2>
                            </hgroup>
                        </header>
                    </div>
                    <c:if test="${usuario.newsletter}">
                        <div class="alert alert-info">Usted ya esta inscrito en la newsletter</div>
                    </c:if>
                    <div class="webform_box">

                         <%--NEWSLETTER--%>
                         <div class="webform_row">
                             <div class="webform_label">
                                <c:choose>
                                    <c:when test="${usuario.newsletter}">
                                        ¿Quiere permanecer suscrito a la newsletter?
                                    </c:when>
                                    <c:otherwise>
                                        ¿Quiere suscribirse a la newsletter?
                                    </c:otherwise>
                                </c:choose>
                             </div>
                             <div class="webform_field">
                                 <div class="webform_field_checkbox" style="margin-right: 10px">
                                     <div class="webform_field_checkbox">
                                        <input type="hidden" id="newsletter" name="newsletter"
                                                <c:if test="${usuario.newsletter}">value="true"</c:if>>
                                         <input type="checkbox" class="check"
                                                data-input="newsletter" data-disable="bajaDefinitiva"
                                                <c:if test="${usuario.newsletter}">value="true" checked="true"</c:if>>
                                     </div>
                                 </div>
                             </div>
                         </div>
                    </div>
                        <%--    PIE--%>
                    <div class="webform_button">
                        <input type="hidden" name="submit" value="U" class="formbutton btn submitbutton">
                        <button type="submit">Guardar</button>
                    </div>
                </div>

                <div class="webform_bottomtext">
                    <h6><strong>
                        <small>Asegúrese de que ha rellenado todos los campos obligatorios y pulse
                            "Enviar".&nbsp;</small>
                    </strong></h6>
                    <p style="text-align: justify;"><small>En cumplimiento de los establecido por la Ley
                        Orgánica 15/1999 de 13 de Diciembre, de Protección de Datos de Carácter Personal, le
                        informamos que, los datos que cumplimente en este formulario quedarán incorporados y
                        serán tratados en los ficheros de CEDINOX. Asimismo, le informamos que dispone de la
                        posibilidad de ejercer los derechos de acceso, rectificación, cancelación y oposición de
                        sus datos de carácter personal dirigiéndose por escrito a la dirección&nbsp;<a
                                href="mailto:cedinox@acerinox">cedinox@acerinox.com</a>&nbsp;o en la calle
                        Santiago de Compostela 100 4ª planta 28035 Madrid.</small></p>
                </div>
            </form>
            <script>
                var _inputsCheck = 'input.check'

                $(function(){
                    $(_inputsCheck).on('change', updateCheckbox)
                })

                function updateCheckbox(e){
                    setCheckboxValue(e.target)
                }

                function setCheckboxValue(checkbox) {
                    var inputId = checkbox.getAttribute("data-input")
                    var input = document.getElementById(inputId)
                    if (input) {
                        var value = false
                        var isChecked = checkbox.checked
                        if (isChecked) {
                            value = true
                        }
                        input.setAttribute("value", value)
                    }
                }
            </script>
        </c:if>
    </div>
</cms:bundle>
