<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<div>
<%--HTML--%>
<div id="app">
    <button type="button" v-on:click="reverseMessage">Reverse</button>
    <p>{{message}}</p>
</div>

<%--JS--%>
<script>
    var object = {
        message: 'Hello Vue.js!'
    }

    new Vue({
        el: '#app',
        data: object,
        methods: {
            reverseMessage: function () {
                this.message = this.message.split('').reverse().join('')
            }
        }

    })
</script>
</div>