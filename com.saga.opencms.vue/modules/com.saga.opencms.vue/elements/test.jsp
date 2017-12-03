<div>
    <script src="https://npmcdn.com/vue/dist/vue.js"></script>

    <div id="app">
        <h1>{{ title }}</h1>
        <button @click="title = 'Changed'"></button>
        <button @click="destroy"></button>
    </div>

    <script>
        var vm = new Vue({
            el: "#app",
            data: {
               title: "Hola"
            },
            beforeCreate: function(){
                console.log("beforeCreate");
            },
            created: function(){
                console.log("created");
            },
            beforeMount: function(){
                console.log("beforeMount");
            },
            mounted: function(){
                console.log("mounted");
            },
            beforeUpdate: function(){
                console.log("beforeUpdate");
            },
            updated: function(){
                console.log("updated");
            },
            beforeDestroy: function(){
                console.log("beforeDestroy");
            },
            destroyed: function(){
                console.log("destroyed");
            },
            methods: {
                destroy: function(){
                    this.$destroy();
                }
            }
        });
    </script>
</div>