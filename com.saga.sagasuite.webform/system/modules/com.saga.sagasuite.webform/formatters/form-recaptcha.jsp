<%@page buffer="none" session="false" taglibs="c,cms,fmt,fn" import="com.alkacon.opencms.v8.formgenerator.*" trimDirectiveWhitespaces="true"%>
<c:set var="boxschema"><cms:elementsetting name="boxschema" default="box_schema1" /></c:set>
<c:set var="uri" value="${cms.element.sitePath}" />
<%
    SgFormHandler form = null;
%>
<c:set var="locale" value="${cms:vfs(pageContext).context.locale}" />

<c:choose>
    <c:when test="${cms.element.inMemoryOnly}">
        <%
            // initialize the form handler
            form = SgFormHandlerFactory.create(pageContext, request, response);
        %>
        <div>
            <h3><%= form.getMessages().key("webform.init.newAlkaconWebform") %></h3>
            <h4><%= form.getMessages().key("webform.init.pleaseEdit") %></h4>
        </div>
    </c:when>
    <c:otherwise>
        <%
            // initialize the form handler
            form = SgFormHandlerFactory.create(pageContext, request, response, (String)pageContext.getAttribute("uri"));
        %>
    </c:otherwise>
</c:choose>

<%
    boolean dd = form.downloadData();
    pageContext.setAttribute("dd", dd);
%>

<c:choose>
    <c:when test="${dd}">
        <%
            form.createForm();
        %>
    </c:when>
    <c:otherwise>
        <cms:formatter var="content" val="value">

            <c:if test="${not empty cms.element.settings.classmainbox }">
                <c:set var="classmainbox">${cms.element.settings.classmainbox}</c:set>
            </c:if>
            <c:if test="${cms.element.setting.marginbottom.value != '0'}">
                <c:set var="marginClass">margin-bottom-${cms.element.setting.marginbottom.value}</c:set>
            </c:if>
            <c:if test="${cms.element.settings.featuredtitle =='true' }">
                <c:set var="featuredtitle">featured-title</c:set>
            </c:if>

            <%-- Definimos el tipo de etiqueta html para nuestro encabezado --%>

            <c:set var="titletag">h1</c:set>

            <c:if test="${not empty cms.element.settings.titletag }">
                <c:set var="titletag">${cms.element.settings.titletag }</c:set>
            </c:if>
            <div class="element parent OpenCmsWebform <c:out value='${featuredtitle} ${marginClass} ${classmainbox}' />">

                <div class="wrapper">

                    <c:if test="${not cms.element.settings.hidetitle and value.Title.isSet }">
                        <c:if test="${value.Title.isSet}">
                            <header class="headline <c:out value='${featuredtitle}' />">
                                <${titletag} class="title">
                                ${value.Title}
                            </${titletag}>
                            </header>
                        </c:if>
                    </c:if>
                    <c:if test="${cms.element.settings.dividertop}">
                        <!-- Separador del recurso -->
                        <hr class="divider">
                    </c:if>

                    <%
                        form.createForm();
                    %>
                    <c:if test="${cms.element.settings.dividerbottom}">
                        <!-- Separador del recurso -->
                        <hr class="divider">
                    </c:if>
                </div>
            </div>
        </cms:formatter>
    </c:otherwise>
</c:choose>
<script type="text/javascript">

    $(document).ready(function() {
        $('input').iCheck({
            labelHover: false,
            cursor: true
        });
        //var objeto = $('.check-wrapper input').attr('id');
        //alert(objeto);
        //$('.check-wrapper input').attr('id').replace(/["~!@#$%^&*\(\)_+=`{}\[\]\|\\:;'<>,.\/?"\- \t\r\n]+/g, '-');

        $( ".check-wrapper input" ).each(function( index ) {
            var objeto = $(this).attr('id');
            var r = objeto.replace(/[\s"~!@#$%^&áéíóúÁÉÍÓÚñÑÇçüÜ*\(\)_+=`{}\[\]\|\\:;'<>,.\/?"\- \t\r\n]+/g, '-');
            $(this).attr("id", r);
        });
        $( ".check-wrapper label" ).each(function( index ) {
            var objetolabel = $(this).attr('for');
            var r = objetolabel.replace(/[\s"~!@#$áéíóúÁÉÍÓÚñÑÇçüÜ%^&*\(\)_+=`{}\[\]\|\\:;'<>,.\/?"\- \t\r\n]+/g, '-');
            $(this).attr("for", r);
        });

        $('.webform select').select2({
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
                }
            }
        });

    });
</script>