<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cms:secureparams/>
<fmt:setLocale value="${cms.locale}"/>

<cms:formatter var="content" val="value" rdfa="rdfa">
    <c:choose>
        <c:when test="${cms.element.inMemoryOnly}">
            <h1 class="title">
                Edite este recurso!!
            </h1>
        </c:when>
        <c:otherwise>

            <c:set var="messageproperty" value="${value.MessagesProperties }" scope="request"/>
            <c:if test="${!value.MessagesProperties.isSet }">
                <c:set var="messageproperty" value="com.saga.sagasuite.search.workplace" scope="request"/>
            </c:if>

            <cms:bundle basename="${messageproperty }">

                <c:if test="${cms.element.setting.marginbottom.value != '0'}">
                    <c:set var="marginClass">margin-bottom-${cms.element.setting.marginbottom.value}</c:set>
                </c:if>
                <c:if test="${not empty cms.element.settings.classmainbox }">
                    <c:set var="classmainbox">${cms.element.settings.classmainbox}</c:set>
                </c:if>
                <c:if test="${cms.element.settings.featuredtitle =='true' }">
                    <c:set var="featuredtitle">featured-title</c:set>
                </c:if>

                <%-- gestionamos setting de tipo de recurso ===================================================================--%>

                <c:if test="${not empty cms.element.settings.resourcetype }">
                    <c:set var="settingresourcetype" scope="request">${cms.element.settings.resourcetype}</c:set>
                </c:if>

                <%-- Definimos el tipo de etiqueta html para nuestro encabezado y encabezado de elementos que se listan --%>

                <c:set var="titletag">h1</c:set>
                <c:set var="titletagelement" scope="request">h2</c:set>

                <c:if test="${not empty cms.element.settings.titletag }">
                    <c:set var="titletag">${cms.element.settings.titletag }</c:set>
                    <c:if test="${titletag == 'h1'}">
                        <c:set var="titletagelement" scope="request">h2</c:set>
                    </c:if>
                    <c:if test="${titletag == 'h2'}">
                        <c:set var="titletagelement" scope="request">h3</c:set>
                    </c:if>
                    <c:if test="${titletag == 'h3'}">
                        <c:set var="titletagelement" scope="request">h4</c:set>
                    </c:if>
                    <c:if test="${titletag == 'h4'}">
                        <c:set var="titletagelement" scope="request">h5</c:set>
                    </c:if>
                    <c:if test="${titletag == 'h5'}">
                        <c:set var="titletagelement" scope="request">h6</c:set>
                    </c:if>
                    <c:if test="${titletag == 'div'}">
                        <c:set var="titletagelement" scope="request">div</c:set>
                    </c:if>
                </c:if>

                <c:if test="${not empty cms.element.settings.titletagelement }">
                    <c:set var="titletagelement" scope="request">${cms.element.settings.titletagelement }</c:set>
                </c:if>

                <c:if test="${not empty cms.element.settings.titletagsize }">
                    <c:set var="titletagsize">${cms.element.settings.titletagsize }</c:set>
                </c:if>

                <c:if test="${not empty cms.element.settings.titletagelementsize }">
                    <c:set var="titletagelementsize"
                           scope="request">${cms.element.settings.titletagelementsize }</c:set>
                </c:if>

                <div class="element parent sg-search advanced <c:out value=' ${marginClass} ${classmainbox} ' />">

                    <div class="wrapper <c:if test='${value.ShowResults == "true" }'>hasresults </c:if> <c:out value='${value.CssClass}' />">


                            <%-- Cargamos las variables que luego vamos a ir necesitando a lo largo --%>
                        <c:set var="cmsObject" value="${cms.vfs.cmsObject }" scope="request"/>
                        <c:set var="value" value="${value }" scope="request"/>
                        <c:set var="content" value="${content }" scope="request"/>
                        <c:set var="currentSite" scope="request">${cms.requestContext.siteRoot }</c:set>
                        <c:set var="currentProject" scope="request">${cms.requestContext.currentProject }</c:set>
                        <c:set var="isEdited" scope="request">${cms.edited}</c:set>

                            <%-- Si se ha marcado el readPropertyCategory tenemos que buscar la propiedad category --%>
                        <c:if test="${content.value.readPropertyCategory.exists && content.value.readPropertyCategory=='true'}">
                            <c:set var="categoryProperty" scope="request"><cms:property name="category" file="search"/></c:set>
                            <%-- lo guardamos tambien con el formato que lee el filtro de categorias y lo enviamos en el request para la jsp de filter.jsp --%>
                            <c:set var="filtercategoryProperty"
                                   scope="request">${fn:substringAfter(categoryProperty, "/.categories/")}</c:set>
                        </c:if>

                        <c:if test="${not cms.element.settings.hidetitle && value.HideTitle != 'true'}">
                            <header class="headline <c:out value='${featuredtitle}' />">
                                <${titletag} class="title ${titletagsize}">${value.Title }</
                            ${titletag}>
                            </header>
                        </c:if>

                        <c:if test="${value.TextForm.isSet }">
                            <div class="tstextform">${value.TextForm }</div>
                        </c:if>

                            <%-- gestionamos si se muestra y/o se carga el formulario de búsqueda ===============================================================--%>

                        <c:set var="loadform" value="true"/>
                        <c:set var="showform"></c:set>

                        <c:if test="${value.NotLoadForm.isSet and value.NotLoadForm == 'true'}">
                            <c:set var="loadform" value="false"/>
                        </c:if>

                        <c:if test="${value.ShowForm.exists and value.ShowForm == 'false'}">
                            <c:set var="showform">false</c:set>
                        </c:if>

                            <%-- si hay setting manda para cargar el formulario o no --%>
                        <c:if test="${not empty cms.element.settings.loadform }">
                            <c:set var="loadform">${cms.element.settings.loadform}</c:set>
                        </c:if>

                            <%-- si hay setting manda para mostrar el formulario o no (se manda el valor de la clase css para ocultar o no) --%>
                        <c:if test="${not empty cms.element.settings.showform }">
                            <c:set var="showform">${cms.element.settings.showform }</c:set>
                        </c:if>


                            <%-- JSP que muestra los filtros configurados en el recurso --%>
                        <cms:include
                                file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/v-filters.jsp:d0c3eb43-a767-11e7-90b6-7fb253176922)">
                            <cms:param name="loadform">${loadform}</cms:param>
                        </cms:include>
                        <c:if test="${not cms.isOnlineProject}">
                            <div class="alert alert-info">filters: ${filters}</div>
                        </c:if>


                        <c:if test="${value.TextHeader.isSet }">
                            <div class="tstextheader">${value.TextHeader }</div>
                        </c:if>

                            <%-- gestionamos mostrar resultados --%>

                        <c:set var="showresults"></c:set>

                        <c:if test="${value.ShowResults != null and value.ShowResults == 'true' }">
                            <c:set var="showresults">${value.ShowResults}</c:set>
                        </c:if>

                            <%-- se comprueba el setting que es el que manda --%>
                        <c:if test="${not empty cms.element.settings.showresults }">
                            <c:if test="${cms.element.settings.showresults == 'false'}">
                                <c:set var="showresults">false</c:set>
                            </c:if>
                            <c:if test="${cms.element.settings.showresults == 'true'}">
                                <c:set var="showresults">true</c:set>
                            </c:if>
                        </c:if>

                            <%--<c:if test="${not empty showresults and showresults == 'true'}">--%>

                            <%-- Hacemos el include de la jsp que se encarga de realizar la busqueda sobre solr en base a los parametros recibidos --%>
                        <cms:include
                                file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/v-solrquery.jsp:579fff56-a768-11e7-90b6-7fb253176922)"/>
                        <c:if test="${not cms.isOnlineProject}">
                            <div class="alert alert-info">query: ${query}</div>
                        </c:if>
                            <%-- JSP que muestra los resultados obtenidos en la busqueda realizada --%>
                            <%--<c:if test="${value.ViewType != 'file' }">
                            <cms:include file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/c-resultlist.jsp:2c26bd1b-c2c8-11e2-9372-69f9ccfec931)"/>
                            </c:if>
                            <c:if test="${value.ViewType == 'file' }">
                            <cms:include file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/c-resultlist-table-files.jsp:3881daf7-adca-11e3-a428-f18cf451b707)"/>
                            </c:if>	--%>
                        <cms:include
                                file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/v-resultlist.jsp:5f5b0ff9-a768-11e7-90b6-7fb253176922)"/>

                            <%-- JSP que muestra la paginacion en base a la configuracion realizada y al numero de resultados encontrados --%>
                            <%--<cms:include file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/c-pagination.jsp:630ed814-c449-11e2-9372-69f9ccfec931)"/>--%>

                            <%--</c:if>--%>

                        <c:if test="${value.TextFooter.isSet }">
                            <div class="tstextfooter">${value.TextFooter }</div>
                        </c:if>

                            <%-- SELECT2 --%>
                        <script type="text/javascript">
                            $(document).ready(function () {

                                <c:if test="${value.DropDownForm!=null && value.DropDownForm == 'true' }">
                                // PARA CAMBIAR LA ORIENTACION DE LA FLECHA DE DESPLIEGUE EN EL LOS FILTROS

                                $('#filters-${value.Id }').on('shown.bs.collapse', function () {
                                    console.log('filter shown');
                                    $('[aria-controls="filters-${value.Id }"][aria-expanded="true"] .fa').removeClass('fa-angle-down').addClass('fa-angle-up');
                                    $('[aria-controls="filters-${value.Id }"][aria-expanded="true"] .filter-link-text').html('<fmt:message key="formatter.filter.contract"/>');
                                });

                                $('#filters-${value.Id }').on('hidden.bs.collapse', function () {
                                    $('[aria-controls="filters-${value.Id }"][aria-expanded="false"] .fa').removeClass('fa-angle-up').addClass('fa-angle-down');
                                    $('[aria-controls="filters-${value.Id }"][aria-expanded="false"] .filter-link-text').html('<fmt:message key="formatter.filter.expand"/>');
                                });
                                </c:if>
                                $('input').iCheck({
                                    labelHover: false,
                                    cursor: true
                                });
                                if ($('.sg-search.advanced select').length > 0) {
                                    $('.sg-search.advanced select').select2({
                                        theme: "bootstrap",
                                        width: "100%",
                                        language: {
                                            noResults: function () {
                                                <c:if test="${cms.locale == 'es'}">
                                                return "No se encontraron resultados";
                                                </c:if>
                                                <c:if test="${cms.locale == 'en'}">
                                                return "No results found";
                                                </c:if>
                                                <c:if test="${cms.locale == 'de'}">
                                                return "Keine Übereinstimmungen gefunden";
                                                </c:if>
                                                <c:if test="${cms.locale == 'fr'}">
                                                return "Aucun résultat trouvé";
                                                </c:if>
                                                <c:if test="${cms.locale == 'it'}">
                                                return "Nessun risultato trovato";
                                                </c:if>
                                                <c:if test="${cms.locale == 'pt'}">
                                                return "Sem resultados";
                                                </c:if>
                                                <c:if test="${cms.locale == 'ca'}">
                                                return "No es van trobar resultats";
                                                </c:if>
                                            }
                                        }
                                    });
                                } else {
                                }
                            });
                        </script>

                            <%--TODO: add into template?? VUE JS--%>
                        <script src="https://unpkg.com/vue"></script>

                            <%--TODO: add into template?? VUE RESOURCE--%>
                        <script src="https://cdn.jsdelivr.net/npm/vue-resource@1.3.4"></script>

                            <%--TODO: add into template?? SgVueList--%>
                        <script src="<cms:link>../resources/js/search-vue.js</cms:link>"></script>
                        <script>
                            window.Saga.vueSearch.initialize({
                                id: '${value.Id}'
                                ,controller: '<cms:link>../elements/v-controller.jsp</cms:link>'
                                ,locale: '${cms.locale}'
                                ,rows: '${value.NumResultPerPage}'
                                ,query: '${query}'
                                ,filters: ${filters}
                                /*filters: [
                                 {type: 'between2dates', val: ''},
                                 {type: 'text', val: ''}
                                 ],*/
                                ,showForm: '${showform}'
                                ,isDropDown: ${value.DropDownForm}
                            });
                        </script>
                    </div>
                </div>
            </cms:bundle>

        </c:otherwise>
    </c:choose>
</cms:formatter>
