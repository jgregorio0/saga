(function(window){
    'use strict';

    function SgCookie(){
        var SgCookie = {

            // PARAMETERS
            _mainName: 'sg',
            _path: '/',
            _isSecure: false,

            initilize: function (opts) {
                console.log('initilize', opts);
                this._path = opts.path || this._path;
                this._isSecure = opts.isSecure || this._isSecure;
            },

            /**
             * delete cookie
             * @param name
             */
            delete: function (name) {
                var jCookie = this.getJCookie(name);
                delete jCookie[name];
                this.save(this._mainName, jCookie);
            },

            /**
             * save cookie
             * @param json
             */
            save: function (name, json) {
                console.log('save', name, json);
                if (!json || json.constructor !== {}.constructor) {
                    throw Error("json can be saved only", json);
                }
                this.saveEncoded(name, json, this._isSecure, this._path);
            },

            /**
             * Create or overwrite cookie
             * @param json
             */
            saveEncoded: function (name, json, isSecure, path) {
                console.log('saveEncoded', name, json, isSecure, path);

                if (!name) {
                    throw Error("name must be provided", name);
                }

                if (!json || json.constructor !== {}.constructor) {
                    throw Error("json can be saved only", json);
                }

                //SECURE
                var cSecure = "";
                if (isSecure) {
                    cSecure = " secure;"
                }

                //PATH
                var cPath = "";
                if (path) {
                    path = " path=" + path + ";";
                }

                var mainJCookie = this.getJCookie(this._name);
                mainJCookie[name] = json;

                var value = encodeURIComponent(JSON.stringify(json));

                // update cookie
                document.cookie = name + "=" + value + ";" + isSecure + path;
            },

            /**
             * Get cookie by name
             * @param name
             * @returns {*}
             */
            getCookieByName: function (name) {
                //console.log('getTheCookieName', name);
                var cookie;
                var cookies = document.cookie;
                var cookiesArr = cookies.split(';');
                for (var i = 0; i < cookiesArr.length; i++) {
                    var cParts = cookiesArr[i].split('=');
                    var cName = cParts[0];
                    var cValue = cParts[1];
                    if (name.trim() == cName.trim())
                        cookie = cValue;
                }
                return cookie;
            },

            /**
             * Get cookie value by name and decode value
             * @param name
             * @returns {string}
             */
            getDecodedCookieByName: function (name) {
                console.log('getDecodedCookieByName', name);
                return decodeURIComponent(this.getCookieByName(name))
            },

            /**
             * Obtain json products from cookie or create empty
             * @param name
             * @returns {*}
             */
            getJCookie: function (name) {
                console.log('getJCookie', name);
                //console.log('getTheCookieName', name);
                var cookie = this.getDecodedCookieByName(name);
                if (!cookie) {
                    cookie = '{}';
                }
                return JSON.parse(cookie);
            }
        };

        return SgCookie;
    }

    //define globally if it doesn't already exist
    if(typeof(window.SgCookie) === 'undefined'){
        window.SgCookie = SgCookie();
    }

    // Auto init
    //window.SGBasket.init();
})(window, Vue);