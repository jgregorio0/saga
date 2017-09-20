<div>
<script src="https://npmcdn.com/vue/dist/vue.js"></script>

<div id="exercise">
    <!-- 1) Show an alert when the Button gets clicked -->
    <div>
        <button v-on:click="alert">Show Alert</button>
    </div>
    <!-- 2) Listen to the "keydown" event and store the value in a data property (hint: event.target.value gives you the value) -->
    <div>
        <input type="text" v-on:keydown="storeKeyDown($event)">
        <p>{{ keydown }}</p>
    </div>
    <!-- 3) Adjust the example from 2) to only fire if the "key down" is the ENTER key -->
    <div>
        <input type="text" v-on:keydown.enter="storeKeyDown($event)">
        <p>{{ keydown }}</p>
    </div>
</div>

    <script>
        var ex2 = new Vue({
            el:'#exercise',
            data:{
                keydown: ""
            },
            methods: {
                alert: function(){
                    alert('botton clicked!!');
                },
                storeKeyDown: function (event){
                    this.keydown = event.target.value;
                }
            }
        });
    </script>
</div>