<%@ page import="org.opencms.json.JSONObject" %>
<%@page buffer="none" session="true" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%!
    private String getJsonField(JSONObject json, String field) {
        String s = null;
        try {
            s = json.getString(field);
        } catch (Exception e) {}
        return s;
    }
%>

<%
//    String url = "/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-ajax.jsp";
    String url = "/system/modules/com.saga.opencms.util/elements/solr-list-controller-ajax.jsp";

    //q=((title_es:"crema")^2 OR (description_es:"crema"))&fq=type:("RecipeChef" OR "MixRecipeChef")&fq=parent-folders:"/shared/.content/"&fq=con_locales:es&rows=6&start=0
//    String query = "q=((title_es:\\\"crema\\\")^2 OR (description_es:\\\"crema\\\"))" +
//            "&fq=type:(\\\"RecipeChef\\\" OR \\\"MixRecipeChef\\\")" +
//            "&fq=parent-folders:\\\"/shared/.content/\\\"" +
//            "&fq=con_locales:es" +
//            "&rows=6" +
//            "&start=0";

    //TODO tener en cuenta el locale
//    String fields = "[\"id\", \"title_es\", \"Title_prop\", \"path\", \"image_es\", \"xmlimage1_es\", \"type\"]";
//    String jsonStr = "{" +
//            "\"query\": \"" + query + "\"" +
//            ", \"fields\": " + fields +
//            "}";

//    String j = "{" +
//            "\"query\": \"" + "q=((title_es:\\\"crema\\\")^2 OR (description_es:\\\"crema\\\"))" +
//            "&fq=type:(\\\"RecipeChef\\\" OR \\\"MixRecipeChef\\\")" +
//            "&fq=parent-folders:\\\"/shared/.content/\\\"" +
//            "&fq=con_locales:es" +
//            "&rows=6" +
//            "&start=0" + "\"" +
//            ", \"fields\": " + "[\"id\", \"title_es\", \"Title_prop\", \"path\", \"image_es\", \"xmlimage1_es\", \"type\"]" +
//            "}";
%>
<html>
<head>
    <%--<script type="text/javascript" src="/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/resources/js/jquery-1.12.3.min.js"></script>--%>
        <script
                src="https://code.jquery.com/jquery-2.2.4.min.js"
                integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44="
                crossorigin="anonymous"></script>
</head>
<body>
<div>
    <button id="loadMore">MORE</button>
    <div id="results"></div>
    <%--<p>jQuery <%=jQuery%></p>--%>
    <%--<p>jFields <%=jFields%></p>--%>
    <%--<p>jTemplate <%=jTemplate%></p>--%>
    <%--<cms:include file="%(link.strong:/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-resultados-ajax.jsp)">--%>
    <%--<cms:param name="jsonStr"><%=jsonStr%></cms:param>--%>
    <%--</cms:include>--%>
    <%--<%--%>
    <%--String jsonResults = "" + request.getAttribute("jsonResults");--%>
    <%--JSONArray jsonArray = new JSONArray(jsonResults);--%>
    <%--for (int i = 0; i < jsonArray.length(); i++) {--%>
    <%--JSONObject json = jsonArray.getJSONObject(i);--%>
    <%--String id = getJsonField(json, "id");--%>
    <%--String title = getJsonField(json, "title_es");--%>
    <%--String titleProp = getJsonField(json, "Title_prop");--%>
    <%--String path = getJsonField(json, "path");--%>
    <%--String image = getJsonField(json, "image_es");--%>
    <%--String xmlimage1 = getJsonField(json, "xmlimage1_es");--%>
    <%--String type = getJsonField(json, "type");--%>

    <%--%>--%>
    <%--<h1><%=i%> - <h6><%=title%> (<%=id%>)</h6></h1>--%>

    <%--<dl>--%>
    <%--<dt>id</dt>--%>
    <%--<dd><%=id%></dd>--%>
    <%--<dt>title</dt>--%>
    <%--<dd><%=title%></dd>--%>
    <%--<dt>titleProp</dt>--%>
    <%--<dd><%=titleProp%></dd>--%>
    <%--<dt>path</dt>--%>
    <%--<dd><%=path%></dd>--%>
    <%--<dt>image</dt>--%>
    <%--<dd><%=image%></dd>--%>
    <%--<dt>xmlimage1</dt>--%>
    <%--<dd><%=xmlimage1%></dd>--%>
    <%--<dt>type</dt>--%>
    <%--<dd><%=type%></dd>--%>
    <%--</dl>--%>
    <%--<%--%>
    <%--//        }--%>

    <%--%>--%>

    <%
//        String query = "q=((title_es:\\\"crema\\\")^2 OR (description_es:\\\"crema\\\"))" +
//            "&fq=type:(\\\"RecipeChef\\\" OR \\\"MixRecipeChef\\\")" +
//            "&fq=parent-folders:\\\"/shared/.content/\\\"" +
//            "&fq=con_locales:es" +
//            "&rows=6" +
//            "&start=0";
        String query = "q=(title_es:'crema')";
        String fields = "['id', 'title_es', 'Title_prop', 'path', 'image_es', 'xmlimage1_es', 'type']";
//        String jsonStr =  "{jsonStr: " +
//                "{query: \"" + query + "\"" +
//                ", fields: " + fields +
//                "}" +
//        "}";


    %>

    <script>
        function loadResults(){
            var url = '<cms:link><%=url%></cms:link>';

//            var data = {jsonStr: "{" +
//                "\"query\": \"" + "q=((title_es:\\\"crema\\\")^2 OR (description_es:\\\"crema\\\"))" +
//            "&fq=type:(\\\"RecipeChef\\\" OR \\\"MixRecipeChef\\\")" +
//            "&fq=parent-folders:\\\"/shared/.content/\\\"" +
//            "&fq=con_locales:es" +
//            "&rows=6" +
//            "&start=0" + "\"" +
//                ", \"fields\": " + "[\"id\", \"title_es\", \"Title_prop\", \"path\", \"image_es\", \"xmlimage1_es\", \"type\"]" +
//                "}"};

            console.log("query1", "<%=query%>");
            console.log("fields", "<%=fields%>");

            var data = {
                query: "<%=query%>"
                , fields: "<%=fields%>"
            };
            console.log("data", data);

            $.ajax({
                type: "POST",
                url: url,
                data: data
            }).done(function(data){
                console.log("done", data);
            }).fail(function(err){
                console.log("fail", err);
            });
        }

        $(function(){
            $("#loadMore").click(function(){
                loadResults();
            });
        });
    </script>
</div>
</body>
</html>