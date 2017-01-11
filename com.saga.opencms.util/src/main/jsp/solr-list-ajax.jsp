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
    public static final String url = "/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-ajax.jsp";
%>

<%
    // Obtenemos locale
    CmsObject cmso = CmsFlexController.getCmsObject(request);
    String lang = cmso.getRequestContext().getLocale().getLanguage();

    // Obtenemos parametros
    String resultsForPage = request.getParameter("resultsForPage");
    if (StringUtils.isEmpty(resultsForPage)) {
        resultsForPage = "6";
    }

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
<html>
<head>
    <script type="text/javascript" src="/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/resources/js/jquery-1.12.3.min.js"></script>
</head>
<body>
<div>
    <button id="loadMore">MORE</button>
    <div id="results"></div>

    <script>
        var start = 1;
        var rows = <%=resultsForPage%>;
        var url = '<cms:link><%=url%></cms:link>';
        function loadResults(){
            var data = {
                query: "<%=query%>" + "&start=" + start
                , fields: "<%=fields%>"
                , idx: start
            };
//            console.log("data", data);

            $.ajax({
                type: "POST",
                url: url,
                data: data
            }).done(function(data){
//                console.log("done", data);
                try {
                    var json = JSON.parse(data);
                    if (json.st === "error") {
                        console.error("searching for results");
                    } else {
                        var results = json.results;
//                        console.log(results);

                        var $resultsDiv = $("#results");
                        for(var iRes = 0; iRes < results.length; iRes++){
                            var jResult = results[iRes];
//                            console.log("jResult", jResult);

                            $resultsDiv.append("<h6>" + (json.idx + iRes) + "</h6>");

                            var $dl = $("<dl>");
                            for (var key in jResult) {
                                if (jResult.hasOwnProperty(key)) {
                                    var val = jResult[key];
//                                    console.log(key, val);

                                    var $dt = $("<dt>");
                                    $dt.text(key);
                                    var $dd = $("<dd>");
                                    $dd.text(val);

                                    $dl.append($dt);
                                    $dl.append($dd);
                                }
                            }

                            $resultsDiv.append($dl);
                        }
                    }
                } catch (err) {
                    console.error("parsing data to json", data);
                }

            }).fail(function(err){
                console.error("fail", err);
            });

            // Actualizamos start
            start = start + rows;
        }

        $(function(){
            $("#loadMore").click(function(){
                loadResults();
            });
            loadResults();
        });
    </script>
</div>
</body>
</html>