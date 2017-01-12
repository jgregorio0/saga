<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@page buffer="none" session="true" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%!
    // URL del controlador
    public static final String urlController = "/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-ajax.jsp";
    //    public static final String urlTemplate = "/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-template.jsp";
    public static final String urlTemplate = "/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-template-multi.jsp";
%>

<%
    // Obtenemos locale
    CmsObject cmso = CmsFlexController.getCmsObject(request);
    String lang = cmso.getRequestContext().getLocale().getLanguage();
    String uri = cmso.getRequestContext().getUri();

    // Obtenemos parametros
    String idSearcher = request.getParameter("id");
    if (StringUtils.isEmpty(idSearcher)) {
        idSearcher = "searcher";
    }
    pageContext.setAttribute("idSearcher", idSearcher);

    String resultsForPage = request.getParameter("resultsForPage");
    if (StringUtils.isEmpty(resultsForPage)) {
        resultsForPage = "6";
    }
    pageContext.setAttribute("resultsForPage", resultsForPage);

    String resourcetypes = request.getParameter("resourcetypes");
    if (StringUtils.isEmpty(resourcetypes)) {
        resourcetypes = "(\"RecipeChef\" OR \"MixRecipeChef\" OR \"PieceOfNews\" OR \"Workshop\")";
    }
    String resTypRep = resourcetypes.replace("\"", "'");

    String q = request.getParameter("q");
    String qQuery = "";
    if (!StringUtils.isEmpty(q)) {
        // Usamos single quoted
        String qRep = q.replace("\"", "'");
        if (!qRep.startsWith("'")) {
            qRep = "'" + qRep;
        }
        if (!qRep.endsWith("'")) {
            qRep = qRep + "'";
        }

        qQuery = "q=((title_" + lang + ":" + qRep + ")^2 OR (description_" + lang + ":" + qRep + "))";
    }

    // Generamos query
//    String query = "q=((title_es:'crema')^2 OR (description_es:'crema'))&fq=type:('RecipeChef' OR 'MixRecipeChef')&fq=parent-folders:'/shared/.content/'&fq=con_locales:es&rows=6&start=0";
    String query = qQuery +
            "&fq=type:" + resTypRep +
            "&fq=parent-folders:'/shared/.content/'" +
            "&fq=con_locales:" + lang +
            "&rows=" + resultsForPage;
    //"&start=0";

    // Definimos los campos que queremos
    String fields = "id,title_" + lang + ",Title_prop,path,image_" + lang + ",xmlimage1_" + lang + ",type";
%>

<fmt:setLocale value="${cms.locale}"/>
<cms:bundle basename="com.caprabo.mrmmccann.caprabochef.formatters.template">
    <cms:formatter var="content">

        <div class="container" style="margin-bottom:50px;">


                <%--<p>id: <%=idSearcher%></p>--%>
            <p>query: <%=query%></p>
                <%--<p>fields: <%=fields%></p>--%>


            <div class="row">
                <div class="col-xs-12">
                    <p class="resultados-busqueda-titulo">
                            <%--TODO Maquetar titulo--%>
                        <fmt:message key="BUSCADOR_TAGS_TITLE_${idSearcher}"/>
                    </p>
                </div>
            </div>

            <div class="resultados-busqueda-contenido">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="row" id="recetas-wrapper">
                            <div class="col-xs-12 col-md-12 col-lg-12 col-lg-offset-0">

                                    <%--LISTADO DE RESULTADOS--%>
                                <div class="row" id="results-${idSearcher}"></div>


                                    <%--<cms:include file="%(link.strong:/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-template.jsp)">--%>
                                    <%--<cms:param name="id"><%=id%></cms:param>--%>
                                    <%--<cms:param name="title"><%=title%></cms:param>--%>
                                    <%--<cms:param name="path"><%=rootPath%></cms:param>--%>
                                    <%--<cms:param name="image"><%=image%></cms:param>--%>
                                    <%--<cms:param name="type"><%=type%></cms:param>--%>
                                    <%--</cms:include>--%>

                                    <%--BOTON MAS RESULTADOS--%>
                                <div class="col-xs-12">
                                    <hr/>
                                        <%--TODO activar ga--%>
                                        <%--<a href="javascript:void(0)" onclick="ga('send', 'event', '${cms.vfs.propertySearch[cms.requestContext.uri]['google.analytics.code']}', 'ver mas', 'ver mas')">--%>
                                    <h6 class="pull-right" id="more-${idSearcher}" style="cursor:pointer;">
                                            <span style="font-family:WorkSans-Regular;font-size:13px;color:#999;">
                                                <fmt:message key="BUSCADOR_TAGS_MORE" />
                                            </span>
                                    </h6>
                                        <%--</a>--%>
                                </div>


                            </div>
                        </div>

                    </div>
                </div>
            </div>


        </div>

        <%--SCRIPT BUSCA SOLR Y CARGA RECURSOS POR AJAX--%>
        <script>
            <%--var urlController = '<cms:link><%=urlController%></cms:link>';--%>
            <%--var urlTemplate = '<cms:link><%=urlTemplate%></cms:link>';--%>
            <%--var $resultsDiv = $("#results-<%=idSearcher%>");--%>
            <%--var rows = ${resultsForPage};--%>
            <%--var start = 1;--%>
            <%--var query = "<%=query%>";--%>
            <%--var fields = "<%=fields%>";--%>
            <%--var uri = '<%=uri%>';--%>

            var ctx = {
                urlController: '<cms:link><%=urlController%></cms:link>',
                urlTemplate: '<cms:link><%=urlTemplate%></cms:link>',
                idSearcher: '<%=idSearcher%>',
                resultsId: 'results-<%=idSearcher%>',
                rows: ${resultsForPage},
                start: 1,
                query: "<%=query%>",
                fields: "<%=fields%>",
                uri: '<%=uri%>'
            };

            // Click event for load results
            loadOnClick(ctx);
            function loadOnClick(ctx){
                var $btnMore = $("#more-" + ctx.idSearcher);
                var start = 1;
                $btnMore.click(function(){
//                    console.log("idSearcher", ctx.idSearcher);
                    // Actualizamos start y cargamos resultados
                    start = start + ctx.rows;
                    loadResults(ctx, start);
                });
                loadResults(ctx, start);
            }

            // Load and print results every click
            function loadResults(ctx, start){
                var data = {
                    query: ctx.query + "&start=" + start
                    , fields: ctx.fields
                    , idx: start
                    , uri: ctx.uri
                };
//            console.log("data", data);

                $.ajax({
                    type: "POST",
                    url: ctx.urlController,
                    data: data
                }).done(function(data){
//                console.log("done", data);

                    try {
                        var json = JSON.parse(data);
                        if (json.st === "error") {
                            console.error("loading ajax results for tag searcher");
                        } else {
                            var results = json.results;
                            var idx = json.idx;
//                        console.log(results);
                            resourcesTemplate(results, idx, ctx.urlTemplate, ctx.resultsId)
                        }
                    } catch (err) {
                        console.error("parsing data to json and formating with template", data);
                    }

                }).fail(function(err){
                    console.error("fail loadResults", err);
                });
            }

            // Load html for single resource
            function jsonTemplate(json, urlTemplate, resultsId){
                // Ejecutamos la llamada para formatear cada resultado
                $.ajax({
                    type: "POST",
                    url: urlTemplate,
                    data: json
                }).done(function(data){
//                    var $resultsDiv = $("#" + resultsId);
//                    $resultsDiv.append(data);
                }).fail(function(err){
                    console.error("fail jsonTemplate", err);
                })
            }

            // Load html for multi resources
            function resourcesTemplate(results, idx, urlTemplate, resultsId){
//                console.log("result", results);
//                console.log("urlTemplate", urlTemplate);
//                console.log("resultsId", resultsId);

                for(var iRes = 0; iRes < results.length; iRes++){
                    var result = results[iRes];
                    var idxRes = idx + iRes;
                    resourceTemplate(result, idxRes, urlTemplate, resultsId);
                }
            }

            function resourceTemplate(result, idxRes, urlTemplate, resultsId){
//                console.log("result", result);
//                console.log("idx", idxRes);

                // Obtenemos los parametros
                var id = result.id;
                var title = result.title_<%=lang%>;
                if (!title) {
                    title = result.titleProp;
                }
                var path = result.path;
                var link = result.link;
                var image = result.image_<%=lang%>;
                if(!image){
                    image = result.xmlimage1_<%=lang%>;
                }
                var type = result.type;


                // Generamos el json para el template
                var data = {
                    id: id,
                    path: path,
                    link: link,
                    title: title,
                    image: image,
                    type: type,
                    idx: idxRes,
                    idGroup: resultsId
                }

                // Ejecutamos la llamada para formatear cada resultado
                $.ajax({
                    type: "POST",
                    url: urlTemplate,
                    data: data
                }).done(idxRes, function(data){

                    // Agregamos el resultado al buscador
                    var $resultsDiv = $("#" + resultsId);
                    $resultsDiv.append(data);
//                    sort("#" + resultsId);
                }).fail(function(err){
                    console.error("fail resourceTemplate", err);
                })
            }

            // Sort div
            function sort(containerId){
                $(containerId + " div").sort(function(a,b){
                    return parseInt(a.id) > parseInt(b.id);
                }).each(function(){
                    var elem = $(this);
                    elem.remove();
                    $(elem).appendTo(containerId);
                });
            }
        </script>
    </cms:formatter>
</cms:bundle>