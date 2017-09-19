<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<div>
    <h1>PRODUCTS</h1>
    <%--HTML--%>
    <div id="v-products" class="center">

        <%--PRODUCTS--%>
        <div class="alert alert-warning" v-if="products.length == 0">
            No hay ningún producto disponible
        </div>
        <div class="input-group" v-for="product in products">

            <%--PRODUCT--%>
            <h2>{{product.id}}: {{product.quantity}}</h2>

            <%--MINUS--%>
            <span class="input-group-btn">
                <button type="button" class="btn btn-default shopping-btn-quantity"
                        v-on:click="sum(product, -1)">
                    <span class="glyphicon glyphicon-minus"></span>
                </button>
            </span>

            <%--COUNT--%>
            <input type="text" id="shopping-quantity-1" name="quantity" v-model="product.q"
                   class="form-control shopping-input-quantity"
                   value="product.q">

            <%--PLUS--%>
            <span class="input-group-btn">
                <button type="button" class="btn btn-default shopping-btn-quantity"
                        v-on:click="sum(product, +1)">
                    <span class="glyphicon glyphicon-plus"></span>
                </button>
            </span>
        </div>
    </div>

    <h1>BASKET</h1>
    <%--HTML--%>
    <div id="v-basket" class="center">

        <%--PRODUCTS--%>
        <div class="alert alert-warning" v-if="products.length == 0">
            No hay ningún producto en la lista
        </div>

        <div class="input-group" v-for="product in products">

            <%--MINUS--%>
            <span class="input-group-btn">
                <button type="button" class="btn btn-default shopping-btn-quantity"
                        v-on:click="sum(product, -1)">
                    <span class="glyphicon glyphicon-minus"></span>
                </button>
            </span>

            <%--COUNT--%>
            <input type="text" id="shopping-quantity-1" name="quantity" v-model="product.q"
                   class="form-control shopping-input-quantity"
                   value="product.q">

            <%--PLUS--%>
            <span class="input-group-btn">
                <button type="button" class="btn btn-default shopping-btn-quantity"
                        v-on:click="sum(product, +1)">
                    <span class="glyphicon glyphicon-plus"></span>
                </button>
            </span>

            <%--COUNT--%>
            <button type="button" id="shopping-rm" v-on:click="rm(product)"
                    class="form-control shopping-btn-rm"></button>
        </div>
    </div>

    <%--JS--%>
    <script src="<cms:link>../resources/js/shopping.js</cms:link>"></script>

    <%--<script>
        // Save to cookie
        var _jResources  = 'resources',
                _jQuantity  = 'quantity',
                _jTotal  = 'total',
                _cookieName = 'shoppingCart',
                _cPath = "path=/;",
                _cSecure = "";//" secure;";

        /**
         *
         * @param name
         * @returns {*}
         */
        function getCookieByName(name) {
            //console.log('getTheCookieName', name);
            var cookie;
            var cookies = document.cookie;
            var cookiesArr = cookies.split(';');
            for (var i = 0; i < cookiesArr.length; i++) {
                var cParts = cookiesArr[i].split('=');
                var cName =  cParts[0];
                var cValue =  cParts[1];
                if (name.trim() == cName.trim())
                    cookie = cValue;
            }
            return cookie;
        }

        /**
         *
         * @param name
         * @returns {*}
         */
        function getJCookie(name) {
            //console.log('getTheCookieName', name);
            var cookie = getCookieByName(name);
            if (!cookie) {
                cookie = '{"' + _jResources + '":{}, "' + _jTotal + '":0}';
            }
            return JSON.parse(cookie);
        }

        /**
         *
         * @param json
         */
        function saveCookie(json){
            console.log('saveCookie', json);

            // update cookie
            document.cookie = _cookieName + "=" + JSON.stringify(json) + ";" + _cSecure + _cPath;
        }

        // VUE
        var products = [
            {
                "id": "83e0a814-8040-11e7-a65a-f53d2dda7236",
                "q": 0
            }
        ];

        new Vue({
            el: '#v-shopping',
            data: {
                "products": products
            },
            methods: {
                sum: function(product, sumQuantity){
                    product.q = product.q + Number(sumQuantity);
                    saveProducts(this.products);
                }
            }
        });

        /**
         * Save all products into cookie
         * @param products
         */
        function saveProducts(products){
            var json = {};
            json.total = products.length;
            var datas = [];
            for(var i = 0; i < products.length; i++){

                var product = products[i];
                var data = {};
                data.id = product.id;
                data.q = product.q;
                datas.push(data);
            }
            json.datas = datas;
            saveCookie(json);
        }

    </script>--%>
</div>