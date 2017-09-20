(function(window, Vue, SgCookie){
    //I recommend this
    'use strict';

    function SGBasket(){
        var SGBasket = {
                // PARAMETERS
            _basketId: '#v-basket',
            _productsId: '#v-products',

            _products:  null,
            _basket:  null,

            _jProducts: 'datas',
            _jId:  'id',
            _jQuantity: 'quantity',
            _jTotal: 'total',
            _cName:  'sgbasket',
            _cPath: '/',
            _cSecure:  false,
            _top:  100,
            _bottom:  0,
            _begin:  0,

            initialize: function(){
                this._products: new Vue({
                    el: window.SGBasket._productsId,
                    data: {
                        products: []
                    },
                    methods: {
                        prods: window.SGBasket.setProductsAvailable,
                        sum: window.SGBasket.sumProdQuant
                    }
                });

                this._basket: new Vue({
                    el: window.SGBasket._basketId,
                    data: {
                        products: []
                    },
                    methods: {
                        prods: window.SGBasket.setProductsSelected,
                        sum: window.SGBasket.sumProdQuant

                        //rm: removeProduct
                    }
                });
            },


        // INIT
        /*SGBasket.init = function(){
         try {
         products.load();
         basket.load();
         } catch (err) {
         console.error(err);
         }
         };*/

        /**
         * Set products available
         * @param products
         */
        setProductsAvailable: function(listOfProducts){
            this.products = listOfProducts;
        },

        /**
         * Set products seleted
         * @param products
         */
        setProductsSelected: function(listOfProducts){
            this.products = listOfProducts;
        },

        /**
         * Add product available
         * @param product
         */
        addProductAvailable: function(product) {
            productsAvailables.push(product);
        },

        /**
         * Add product selected
         * @param product
         */
        addProductSelected: function(product) {
            productsSelected.push(product);
        },

        /**
         * Upload basket list products from cookie
         */
        updateBasketList: function(){
            console.log('updateBasketList');
            var cookie = getJCookie(_cName);
            var jProducts = cookie[_jProducts];
            //TODO GET content of products from controller
            //TODO each jProducts upload quantity if exists, get from controller if not
            productsSelected = jProducts;
        },

        /**
         * Upload list of products with given ids
         */
        loadProductsFromIds: function (ids){
            console.log('loadProductsFromIds');
            //TODO GET content of products from controller, update productsAvailables and update quantities from cookie
            var testProd1 = {};
            testProd1[_jId] = "1";
            testProd1[_jQuantity] = "1";

            productsAvailables = [testProd1];
            updateAvailableListQuantities();
        },

        /**
         * Upload quantities from cookies
         */
        updateAvailableListQuantities: function (){
            console.log('loadFromCookie');
            var cookie = getJCookie(_cName);
            var jProducts = cookie[_jProducts];

            for (var i = 0; i < productsAvailables.length; i++) {
                var prodAv = productsAvailables[i];
                var id = prodAv[_jId];
                var jProd = jProducts[id];
                if (jProd){
                    prodAv[_jQuantity] = jProd[_jQuantity];
                } else {
                    prodAv[_jQuantity] = _begin;
                }
            }
        },

        /*/!**
         * Get cookie by name
         * @param name
         * @returns {*}
         *!/
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

        /!**
         * Obtain json products from cookie or create empty
         * @param name
         * @returns {*}
         *!/
        function getJCookie(name) {
            console.log('getJCookie', name);
            //console.log('getTheCookieName', name);
            var cookie = getCookieByName(name);
            if (!cookie) {
                cookie = '{"' + _jProducts + '":{}, "' + _jTotal + '":0}';
            }
            return JSON.parse(cookie);
        }*/

        /**
         * Sum product quantity
         * @param product
         * @param sumQuantity
         */
        sumProdQuant: function (product, sumQuantity, top, bottom){
            console.log('sumProdQuant', product, sumQuantity, top, bottom);
            var q = product[_jQuantity] + Number(sumQuantity);

            //top limit
            if (top !== undefined && q > top) {
                q = top;
            }

            //bottom limit
            if (bottom !== undefined && q < bottom) {
                q = bottom;
            }

            //change value and save
            product[_jQuantity] = q;
            updateProduct(product);
        },

        /**
         * Save all products into cookie
         * @param products
         */
        updateProduct: function (product){
            console.log('updateProduct', product);
            var jCookie = getJCookie(_cName);
            var jProducts = jCookie[_jProducts];

            // update cookie
            jProducts[product[_jId], product];

            var total = Object.keys(jProducts).length;
            jCookie[_jTotal] = total;

            saveCookie(jCookie);

            //update product lists
            updateAvailableList();
            updateBasketList();
        },

        /*/!**
         * Create or modify cookie
         * @param json
         *!/
        function saveCookie(json){
            console.log('saveCookie', json);

            saveCookie(_cName, json, _cSecure, _cPath);
        }

        /!**
         * Create or overwrite cookie
         * @param json
         *!/
        function saveCookie(name, json, isSecure, path){
            console.log('saveCookie', name, json, isSecure, path);

            //SECURE
            var cSecure = "";
            if (isSecure) {
                cSecure = " secure;"
            }

            //PATH
            var cPath = "";
            if (path) {
                cPath = " path=" + path + ";";
            }

            // update cookie
            document.cookie = name + "=" + JSON.stringify(json) + ";" + cSecure + cPath;
        }*/

        /**
         * Remove product from cookie
         * @param product
         */
        /*function removeProduct(product){
         var removed = null;
         var jProducts = cookie[_jProducts];
         for(var i = 0; i < jProducts.length; i++){
         if (jProducts[i][_jId] === product[_jId]){
         removed = jProducts[i];
         delete jProducts[i];
         }
         }
         console.log("deleted product", removed);

         saveProducts(jProducts);

         return removed;
         }*/
        };



        return SGBasket;
    }

    //define globally if it doesn't already exist
    if(typeof(window.SGBasket ) === 'undefined'){
        window.SGBasket  = SGBasket();
    }

    // Auto init
    //window.SGBasket.init();
})(window, Vue);