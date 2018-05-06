<%--
  Created by IntelliJ IDEA.
  User: jesus
  Date: 06/05/2018
  Time: 23:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <%--<script src="https://cdnjs.cloudflare.com/ajax/libs/vue/0.12.7/vue.min.js"></script>--%>
    <%--<script src="https://cdnjs.cloudflare.com/ajax/libs/vue-resource/0.1.13/vue-resource.min.js"></script>--%>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/vue/2.5.16/vue.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/vue-resource/1.5.0/vue-resource.js"></script>

        <div id="app">
            <h1>{{ json }}</h1>
        </div>
        <script>
            var vm = new Vue ({
                el: '#app',
                data: {
                  json: {}
                },
                methods: {
                    execute () {
                        this.$http.get('http://httpbin.org/get').then(function (response) {
                            // Response to JSON
                            try {
                                console.log('response', response);
                                this.json = response;
                            } catch (err) {
                                console.error('loading data', response, err);
                            } finally {
                                // this.loading = false;
                            }
                        }, function (err) {
                            console.error("http get", err);

                            // Hide loading and show results
                            // this.loading = false;
                        });
                    }
                },
                created () {
                    this.execute();
                }
            })

        </script>


</div>