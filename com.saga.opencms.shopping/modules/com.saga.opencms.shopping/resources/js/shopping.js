(function(window, $){
    //I recommend this
    'use strict';

    function initShoppingCart(){
        var SGShoppingCart = {};

        var _jsId = 'shopping',
            _input = _jsId + '-input-quantity',
            _inputStructureId = 'structureId',
            _inputMax = 'max',
            _inputMin = 'min',
            _inputRmValue = 'rmValue',
            _btn = _jsId + '-btn-quantity',
            _btnAmount = 'amount',
            _btnInputId = 'inputId',
            _jResources  = 'resources',
            _jQuantity  = 'quantity',
            _jTotal  = 'total',
            //_separator  = '-',
            _cookieName = 'shoppingCart',
            _cPath = "path=/;",
            _cSecure = "";//" secure;";

        SGShoppingCart.init = function(){
            try {
                loadQuantity();
                $('.' + _btn).click(modValue);
                /*$('.' + _btnMinus).click(minus);
                $('.' + _btnPlus).click(plus);*/
            } catch (err) {
                console.error(err);
            }
        };

        /**
         * Click button plus or minus trigger event
         * @param e
         */
        function modValue(e){
            e.preventDefault();

            // validate btn data
            var $btn = $(this);
            var btnDatas = $btn.data();
            if (!btnDatas[_btnInputId] || !btnDatas[_btnAmount]) {
                throw Error("Button must contain data " + _btnInputId + " and " + _btnAmount);
            }

            // validate input
            var $input = $('#' + btnDatas[_btnInputId]);
            var inputDatas = $input.data();
            if (!inputDatas[_inputStructureId]) {
                throw Error("Input must contain data " + _inputStructureId);
            }

            var value = Number($input.val() || 0);

            // btn amount to add/remove
            var amount = btnDatas[_btnAmount];

            // modify value
            var newValue = value + amount;

            // check max value
            var max = inputDatas[_inputMax];
            if ((max !== undefined) && newValue > max) {
                newValue = max;
            }

            // check min value
            var min = inputDatas[_inputMin];
            if ((min !== undefined) && newValue < min) {
                newValue = min;
            }

            // if value changes
            if (newValue !== value) {
                $input.val(newValue);
                newValue = Number($input.val());

                // mod cookie
                var jCookie = getJCookie(_cookieName);
                var strId = inputDatas[_inputStructureId];

                // remove data
                var rmVal = inputDatas[_inputRmValue];
                if (rmVal !== undefined && newValue === rmVal) {
                    delete jCookie[_jResources][strId];
                } else {
                    // add data
                    var jResource = jCookie[_jResources][strId];
                    if (!jResource) {
                        jResource = {};
                    }
                    jResource[_jQuantity] = newValue;
                    console.log("resource to modify", jResource);

                    jCookie[_jResources][strId] = jResource;
                    console.log("modified cookie", jCookie);
                }

                saveCookie(jCookie);
            }
        }

        /**
         * Get index from button id
         * @param id
         * @returns {string}
         */
        function getIndex(id){
            if (!(id.split("[").length === 1) || !(id.split("[").length === 1)) {
                throw Error('Buttons format id must end with index surrended by squared bracket "id[index]"');
            }
            var idx = id.substring(id.indexOf("[") + 1, id.indexOf("]"));
            return idx;
        }

        /**
         * Load quantity from cookie to input
         */
        function loadQuantity(){
            //console.log("loadQuantity");
            var $inputs = $('.' + _input);
            for(var i = 0; i < $inputs.length; i++){
                var $input = $($inputs[i]);
                var datas = $input.data();
                if (datas && datas[_inputStructureId]) {
                    var strId = datas[_inputStructureId];
                    var jCookie = getJCookie(_cookieName);
                    var jResource = jCookie[_jResources][strId];
                    if (jResource && jResource[_jQuantity]) {
                        $input.val(jResource[_jQuantity]);
                    }
                }
            }
        }

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

            // update total
            /*json.total = 0;
            var total = 0;
            for(var i in json[_jResources]){
                total++;
            }
            json.total = total;*/
            // Not available IE 8
            json.total = Object.keys(json[_jResources]).length;
            console.log('modified total', json.total);

            // update cookie
            document.cookie = _cookieName + "=" + JSON.stringify(json) + ";" + _cSecure + _cPath;
        }

        return SGShoppingCart;
    }

    //define globally if it doesn't already exist
    if(typeof(window.SGShoppingCart) === 'undefined'){
        window.SGShoppingCart = initShoppingCart();
    }
    else{
        console.log("SGShoppingCart already defined.");
    }

    // Auto init
    window.SGShoppingCart.init();
})(window, $);