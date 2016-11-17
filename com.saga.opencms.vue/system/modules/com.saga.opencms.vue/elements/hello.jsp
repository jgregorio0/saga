<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<div id="app">
    {{message}}
</div>
<script>
    var object = {
        message: 'Hello Vue.js!'
    }

    new Vue({
        el: '#app',
        data: object
    })
</script>
