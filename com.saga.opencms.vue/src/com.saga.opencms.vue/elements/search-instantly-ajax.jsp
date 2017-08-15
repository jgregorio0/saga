<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form id="main" v-cloak>

    <div class="bar">
        <!-- Create a binding between the searchString model and the text field -->

        <input type="text" v-model="searchString" placeholder="Enter your search terms" />
    </div>

    <ul>
        <!-- Render a li element for every entry in the items array. Notice
             the custom search filter "searchFor". It takes the value of the
             searchString model as an argument. -->

        <li v-for="i in articles | searchFor searchString">
            <a v-bind:href="i.url"><p>{{i.title}}</p></a>
        </li>
    </ul>

</form>

<cms:headincludes type="javascript" defaults="https://code.jquery.com/jquery-2.2.2.min.js"/>
<cms:headincludes type="javascript" defaults="%(link.weak:/system/modules/com.saga.opencms.vue/resources/js/vue.js)"/>
<c:set var="module"><cms:link>/system/modules/com.saga.opencms.vue</cms:link></c:set>

<div>module = ${module}</div>

<%--JS for list search results--%>
<script>
    // Define a custom filter called "searchFor".

    Vue.filter('searchFor', function (value, searchString) {

        // The first parameter to this function is the data that is to be filtered.
        // The second is the string we will be searching for.

        var result = [];

        if(!searchString){
            return value;
        }

        searchString = searchString.trim().toLowerCase();

        result = value.filter(function(item){
            if(item.title.toLowerCase().indexOf(searchString) !== -1){
                return item;
            }
        })

        // Return an array with the filtered data.

        return result;
    })

    var articles = [];

    var vm = new Vue({
        el: '#main',
        data: {
            searchString: "",

            // The data model. These items would normally be requested via AJAX,
            // but are hardcoded here for simplicity.

            articles: articles
        }
    });

    // Load articles by AJAX
    var url = "${module}/elements/search-instantly-load.jsp";
    var json = {
                    "query" : "fq=parent-folders:\"/sites/default/\"",
                    "fields" : {
                        "url" : "rootPath",
                        "title" : "Title_prop"
                    }
                };
    var jqxhr = $.post(url, {json: JSON.stringify(json)})
            .done(function(data){
                var json = $.parseJSON(data);
                $.each(json, function(i, item){
                    articles.$set(i, item);
                });
            })
            .fail(function() {
                alert( "error" );
            });

</script>