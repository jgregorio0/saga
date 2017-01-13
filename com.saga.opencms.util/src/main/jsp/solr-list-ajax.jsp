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
    public static final String urlController = "/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-ajax-controller.jsp";
    public static final String urlTemplate = "/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-ajax-template.jsp";
%>

<%
    // Obtenemos locale
    CmsObject cmso = CmsFlexController.getCmsObject(request);
    String lang = cmso.getRequestContext().getLocale().getLanguage();
    String uri = cmso.getRequestContext().getUri();

    String gaCategory = "";
    try {
        gaCategory = cmso.readPropertyObject(uri, "google.analytics.category", true).getValue();
    } catch (Exception e){}

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

    // Generamos la query con tags y texto
    String tag = request.getParameter("tag");
    String qTag = "";
    if (!StringUtils.isEmpty(tag)) {
        // Usamos single quoted
        String tagRep = tag.replace("\"", "'");
        if (!tagRep.startsWith("'")) {
            tagRep = "'" + tagRep;
        }
        if (!tagRep.endsWith("'")) {
            tagRep = tagRep + "'";
        }
        qTag = "(tag_" + lang + ":" + tagRep + ")^3";
    }

    String q = request.getParameter("q");
    String qText = "";
    if (!StringUtils.isEmpty(q)) {
        // Usamos single quoted
        String qRep = q.replace("\"", "'");
        if (!qRep.startsWith("'")) {
            qRep = "'" + qRep;
        }
        if (!qRep.endsWith("'")) {
            qRep = qRep + "'";
        }

        qText = "(title_" + lang + ":" + qRep + ")^2 OR (description_" + lang + ":" + qRep + ")";
    }

    String qQuery = "";
    if (!StringUtils.isEmpty(qTag)) {
        qQuery = qTag;
        if (!StringUtils.isEmpty(qText)) {
            qQuery = qTag + " OR " + qText;
        }
        qQuery = "q=(" + qQuery + ")";
    } else if (!StringUtils.isEmpty(qText)) {
        qQuery = "q=(" + qText + ")";
    }

    // Generamos query
//    String query = "q=((title_es:'crema')^2 OR (description_es:'crema'))&fq=type:('RecipeChef' OR 'MixRecipeChef')&fq=parent-folders:'/shared/.content/'&fq=con_locales:es&rows=6&start=0";
    String query = qQuery +
            "&fq=type:" + resTypRep +
//            qTag +
            "&fq=parent-folders:'/shared/.content/'" +
            "&fq=con_locales:" + lang +
            "&rows=" + resultsForPage +
            "&sort=released desc";
    //"&start=0";

    // Definimos los campos que queremos
    String fields = "id,title_" + lang + ",Title_prop,path,image_" + lang + ",xmlimage_" + lang + ",type";
%>

<fmt:setLocale value="${cms.locale}"/>
<cms:bundle basename="com.caprabo.mrmmccann.caprabochef.formatters.template">
    <cms:formatter var="content">


        <div class="container" style="margin-bottom:50px;">


                <%--<p>id: <%=idSearcher%></p>--%>
                <%--<p>query: <%=query%></p>--%>
                <%--<p>fields: <%=fields%></p>--%>

                <%--TITULO--%>
            <div class="row">
                <div class="col-xs-12" id="buscador-tag-resultados-title">
                    <h1 class="WS-Thin"><b class="WS-Medium">
                        <fmt:message key="BUSCADOR_TAGS_TITLE_${idSearcher}"/>
                    </b><span id="total-${idSearcher}" class="badge"></span></h1>
                </div>
            </div>
            <style>
                #buscador-tag-resultados-title{
                    color: #999999;
                    border-bottom: 1px solid #dadada;
                    margin-bottom: 30px;
                }
                #total-${idSearcher}{
                    font-size: 25px;
                    margin-left: 10px;
                }
            </style>

                <%--CONTENIDO--%>
            <div class="resultados-busqueda-contenido">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="row" id="recetas-wrapper">
                            <div class="col-xs-12 col-md-12 col-lg-12 col-lg-offset-0">

                                    <%--LISTADO DE RESULTADOS--%>
                                <div class="row" id="results-${idSearcher}"></div>


                                    <%--<cms:include file="%(link.strong:/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-ajax-template.jsp)">--%>
                                    <%--<cms:param name="id"><%=id%></cms:param>--%>
                                    <%--<cms:param name="title"><%=title%></cms:param>--%>
                                    <%--<cms:param name="path"><%=rootPath%></cms:param>--%>
                                    <%--<cms:param name="image"><%=image%></cms:param>--%>
                                    <%--<cms:param name="type"><%=type%></cms:param>--%>
                                    <%--</cms:include>--%>

                                    <%--BOTON MAS RESULTADOS--%>
                                <div class="col-xs-12" id="buscador-tag-resultados-more">
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
                                <style>
                                    #buscador-tag-resultados-more {
                                        color: #999999;
                                        margin-bottom: -40px;
                                    }
                                </style>

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
                emptyMsg: '<fmt:message key="BUSCADOR_TAGS_EMPTY"/>',
                gaCategory: '<%=gaCategory%>',
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
                            var total = json.total;

                            // Si hay algun resultado
                            if (total) {
                                // Mostramos total
                                showTotal(ctx.idSearcher, total);
                                var results = json.results;
                                var idx = json.idx;
//                        console.log(results);
                                resourcesTemplate(results, idx, ctx.urlTemplate, ctx.resultsId, ctx.gaCategory)
                            } else if (start === 1) {
                                // Si no hay ningun resultado
                                showEmpty(ctx.resultsId, ctx.emptyMsg);
                            }
                        }
                    } catch (err) {
                        console.error("parsing data to json and formating with template", data);
                    }

                }).fail(function(err){
                    console.error("fail loadResults", err);
                });
            }

            function showEmpty(resultsId, msg){
                var $resultsDiv = $("#" + resultsId);
                var msg = '<div class="alert alert-warning fade in alert-dismissable">' + msg + '</div>';
                $resultsDiv.append(msg);
            }

            function showTotal(idSearcher, total){
                var $total = $("#total-" + idSearcher);
                $total.text(total);
            }

            // Load html for multi resources
            function resourcesTemplate(results, idx, urlTemplate, resultsId, gaCategory){
//                console.log("result", results);
//                console.log("urlTemplate", urlTemplate);
//                console.log("resultsId", resultsId);
                for(var iRes = 0; iRes < results.length; iRes++){
                    var result = results[iRes];
                    var idxRes = idx + iRes;
                    resourceTemplate(result, idxRes, urlTemplate, resultsId, gaCategory);
                }
            }

            function resourceTemplate(result, idxRes, urlTemplate, resultsId, gaCategory){
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
                    idGroup: resultsId,
                    gaCategory: gaCategory
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
                }).fail(function(err){
                    console.error("fail resourceTemplate", err);
                })
            }
        </script>
    </cms:formatter>
</cms:bundle>