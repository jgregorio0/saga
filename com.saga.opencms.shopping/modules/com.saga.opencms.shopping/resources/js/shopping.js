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
            _cSecure = "",//" secure;";
            _results = _jsId + '-results',
            _resController  = 'controller',
            _resTempHeader = 'templateHeader',
            _resTempHeaderId = 'templateHeaderId',
            _resTempItem = 'templateItem',
            _resTempItemId = 'templateItemId',
            _resTempPagination = 'templatePagination',
            _resTempPaginationId = 'templatePaginationId';

        SGShoppingCart.init = function(){
            try {
                loadQuantity();
                $('.' + _btn).click(modValue);
                loadResults();
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
                    //console.log("resource to modify", jResource);

                    jCookie[_jResources][strId] = jResource;
                    //console.log("modified cookie", jCookie);
                }

                saveCookie(jCookie);
            }
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
         * List results from cookie
         */
        function loadResults(){
            //console.log("loadQuantity");
            var $results = $('.' + _results);
            for(var i = 0; i < $results.length; i++){
                var $result = $($results[i]);
                var datas = $result.data();
                if (!datas[_resController]
                    || !datas[_resTempHeader]
                    || !datas[_resTempHeaderId]
                    || !datas[_resTempItem]
                    || !datas[_resTempItemId]
                    || !datas[_resTempPagination]
                    || !datas[_resTempPaginationId]) {
                    throw Error("Resuts " + _results + " do not define datas correctly");
                }

                var ctxt = {
                    "_resTempHeaderId": _resTempHeaderId,
                    "_resTempItemId": _resTempItemId,
                    "_resTempPaginationId": _resTempPaginationId
                };
                var jCookie = getJCookie(_cookieName);
                var jResources = jCookie[_jResources];
                if (jResources) {
                    var ids = Object.keys(jResources).join("|");
                    ctxt["ids"] = ids;
                    $.get(datas[_resController], ctxt)
                        .done(function(data){
                            $('#' + ctxt[_resTempHeaderId]).empty().append(data);
                            $('#' + ctxt[_resTempItemId]).append(data);
                            $('#' + ctxt[_resTempPaginationId]).empty().append(data);
                        })
                        .fail(function(err){
                            console.error("loading resources", ctxt, err);
                        })
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