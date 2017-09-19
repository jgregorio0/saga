<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<div>

    <%--AXIOS--%>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>


    <%--HTML--%>
    <div id="app">
        <ul v-if="posts && posts.length">
            <li v-for="post of posts">
                <p><strong>{{post.title}}</strong></p>
                <p>{{post.body}}</p>
            </li>
        </ul>

        <ul v-if="errors && errors.length">
            <li v-for="error of errors">
                {{error.message}}
            </li>
        </ul>

        <button id="loadData" onclick="loadData()">Load data</button>
    </div>

    <%--JS--%>
    <script>
        var object = {
            posts: [],
            errors: []
        };

        var vue = new Vue({
            el: '#app',
            data: object
        });

        var loadBtn = document.getElementById("loadData");
        loadBtn.addEventListener("click", loadData);

        function loadData(){
            axios.get('<cms:link>/system/modules/com.saga.opencms.vue/resources/json/axios.json</cms:link>'/*, {
                     params: {
                     ID: 12345
                     }
                     }*/)
                    .then(function (res) {
    //                    console.log(response);
                        object.posts = res.data;
                    })
                    .catch(function (error) {
                        console.error(error);
                    });
        }
    </script>
</div>