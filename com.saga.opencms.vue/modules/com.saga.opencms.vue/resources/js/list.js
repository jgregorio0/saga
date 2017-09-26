/*!
 * VUE List v1.0
 * ===================================
 * VUE Loader for list
 */

(function (Vue, axios) {

    if (window.Vue === undefined) {
        throw Error("Vue must be defined");
    }
    if (window.axios === undefined) {
        throw Error("axios must be defined");
    }

    function SgVueList() {
        var SgVueList = {
            _el: 'vue-list',
            /*_container: '#vue-list-container',
             _list: '#vue-list',
             _more : '#vue-list-more',
             _moreBtnClass : '.vue-list-more-btn',
             _loading: '#vue-list-loading',*/

            /**
             * Initialize AJAX list attributes
             * @param options
             */
            initialize: function (options) {
                console.log("SgVueList", options);
                if (options) {
                    this._el = options.el || this._el;
                    /*
                     this._container = options.container || this._container;
                     this._list = options.list || this._list;
                     this._more = options.more || this._more;
                     this._moreBtnClass = options.moreBtnClass || this._moreBtnClass;
                     this._loading = options.loading || this._loading;*/
                }

                var el = document.getElementById(this._el);
                if (!el) {
                    console.debug("elemento" + this._el + " not exists");
                } else {

                    // Create context starting on 0 and execute submit
                    var ctxt = {};
                    ctxt.sg = window.SgVueList;
                    ctxt.start = 0;

                    // create VUE Object
                    var vList = new Vue({
                        el: '#' + this._el,
                        data: {
                            total: 0,
                            datasSize: 0,
                            datas: [],
                            query: "",
                            start: 0,
                            rows: 0,
                            fields: [],
                            loading: false
                        },
                        methods: {
                            setTotal: function (total) {
                                this.total = total;
                            },
                            setDatasSize: function (datasSize) {
                                this.datasSize = datasSize;
                            },
                            setDatas: function (datas) {
                                this.datas = datas;
                            },
                            setQuery: function (query) {
                                console.log("setQuery", query);
                                this.query = query;

                                // start
                                var queryParams = new URLSearchParams(encodeURI(query));
                                this.setStart(queryParams.get("start"));

                                // rows
                                this.setRows(queryParams.get("rows"));
                            },
                            setFields: function (fields) {
                                this.fields = fields;
                            },
                            setStart: function (start) {
                                this.start = start;
                            },
                            setRows: function (rows) {
                                this.rows = rows;
                            },
                            getLast: function () {
                                this.start + this.rows
                            },
                            setLoading: function (loading) {
                                this.loading = loading;
                            },
                            update: function (jRes) {
                                console.log("update", jRes);
                                this.setTotal(jRes.total);
                                this.setDatasSize(jRes.dataSize);
                                this.setDatas(jRes.data);
                                this.setQuery(jRes.query);
                                this.setFields(jRes.fields);
                            }
                        }
                    });
                    ctxt.sg._vList = vList;

                    ctxt.sg.loadResults(ctxt);
                }
            },

            /**
             * Load data from main container, post controller to get results and post template for print them.
             * ctxt.start === 0 means first loading.
             * @param ctxt
             * @param start
             */
            loadResults: function (ctxt) {
                console.log("loadResults", ctxt);

                // Load form input and data
                ctxt.sg.loadData(ctxt);

                // Update list content and more results btn
                ctxt.sg.listController(ctxt);
            },

            /**
             * Load data-cxtx from main container
             * @param ctxt
             */
            loadData: function (ctxt) {
                console.log("loadData", ctxt);

                // read data and add to context
                var el = document.getElementById(ctxt.sg._el);
                ctxt.datas = JSON.parse(el.dataset.ctxt);
            },


            /**
             * Load params for update list
             * @param ctxt
             * @param start
             */
            loadListControllerParams: function (ctxt) {
                var params = {};

                // params
                params.locale = ctxt.datas.locale;
                params.rows = ctxt.datas.rows;
                params.start = ctxt.start;

                return params;
            },

            /**
             * Validata datas (ctxt.datas) and inputs (ctxt.params)
             *
             * @param ctxt
             */
            validateListController: function (ctxt, params) {
                // controller
                if (!ctxt.datas.controller) {
                    throw Error("elemento data-ctxt controller must be defined")
                }

                // locale
                if (!params.locale) {
                    throw Error("parameter locale must be defined")
                }

                // start
                if (params.start === undefined) {
                    throw Error("parameter start must be defined")
                }

                return true;
            },

            /**
             * Post controller to get results and post template to print them
             * @param ctxt
             */
            listController: function (ctxt) {
                console.log("listController", ctxt);

                var params = ctxt.sg.loadListControllerParams(ctxt);

                if (ctxt.sg.validateListController(ctxt, params)) {

                    // Show loading
                    ctxt.sg._vList.setLoading(true);

                    // AXIOS request
                    axios.post(ctxt.datas.controller, params)
                        .then(function (data) {
                            console.log('POST listController', ctxt, data);

                            // Response to JSON
                            try {
                                var jRes = JSON.parse(data);

                                // Check if error
                                if (!jRes.error) {

                                    // If validate then update
                                    ctxt.sg._vList.update(jRes);
                                } else {
                                    console.error("JSON Response", jRes.errorMsg, jRes.errorTrace);
                                }
                            } catch (err) {
                                console.error('loading data', ctxt, data, err);
                            } finally {
                                ctxt.sg._vList.setLoading(false);
                            }
                        })
                        .catch(function (err) {
                            console.error("POST controller", ctxt, err);

                            // Hide loading and show results
                            ctxt.sg._vList.setLoading(false);
                        });
                }
            },

            /*/!**
             * Load params and POST list template
             * @param ctxt
             * @returns {{}}
             *!/
            paramsListTemplate: function (ctxt) {
                var params = {};

                params.jRes = encodeURI(JSON.stringify(ctxt.jRes));
                params.locale = ctxt.datas.locale;
                params.start = ctxt.start;

                return params;
            },

            /!**
             * Validate before print results using template
             * @param ctxt
             * @returns {boolean}
             *!/
            validateListTemplate: function (ctxt, params) {
                // list template
                if (!ctxt.datas.list) {
                    throw Error("form data-ctxt list must be defined");
                }

                // list container
                if ($(ctxt.sg._list).length === 0) {
                    throw Error("container " + ctxt.sg._list + " not found");
                }

                // jRes.error
                if (ctxt.jRes.error) {
                    throw Error(ctxt.jRes.errorMsg);
                }

                // start
                if (params.start === undefined) {
                    throw Error("parameter start must be defined");
                }

                // locale
                if (!params.locale) {
                    throw Error("parameter locale must be defined");
                }

                return true;
            },

            /!**
             * update result destacado
             * @param ctxt
             *!/
            listTemplate: function (ctxt) {
                console.log("listTemplate", ctxt);

                // Load params for template
                var params = ctxt.sg.paramsListTemplate(ctxt);

                if (ctxt.sg.validateListTemplate(ctxt, params)) {

                    // POST to template
                    $.post(ctxt.datas.list, params)
                        .done(function (data) {
                            console.log("POST listTemplate", data);

                            var $list = $(ctxt.sg._list);

                            // If fist search empty list
                            if (ctxt.start == 0) {
                                $list.empty();
                            }
                            $list.append(data);
                            ctxt.sg.updateList(ctxt);
                            ctxt.sg.showList(ctxt);
                        })
                        .fail(function (err) {
                            console.error("Error loading offers from template", ctxt, err);
                            ctxt.sg.showList(ctxt);
                        })
                }
            },

            /!**
             * Update new items events
             * @param ctxt
             *!/
            updateList: function (ctxt) {
                console.log("updateList", ctxt);
            },

            /!**
             * Show list and hide loading
             * @param ctxt
             *!/
            showList: function (ctxt) {
                console.log("showList", ctxt);

                // hide list and show loading
                $(ctxt.sg._loading).hide();
                $(ctxt.sg._list).show();
            },

            /!**
             * Loading list data
             * @param ctxt
             *!/
            loadingList: function (ctxt) {
                console.log("loadingList", ctxt);

                // Show loading
                $(ctxt.sg._loading).show();

                // Hide list if start==0
                if (ctxt.start == 0) {
                    $(ctxt.sg._list).hide();
                }
            },

            /!**
             * Load params for POST list template
             * @param ctxt
             * @returns {}
             *!/
            paramsMoreTemplate: function (ctxt) {
                var params = {};

                params.jRes = encodeURI(JSON.stringify(ctxt.jRes));
                params.locale = ctxt.datas.locale;
                params.start = ctxt.start;

                return params;
            },

            /!**
             * Validate before print results using template
             * @param ctxt
             * @returns {boolean}
             *!/
            validateMoreTemplate: function (ctxt, params) {
                // more template
                if (!ctxt.datas.more) {
                    throw Error("container data-ctxt more must be defined");
                }

                // more container
                if ($(ctxt.sg._more).length === 0) {
                    throw Error("container " + ctxt.sg._more + " not found");
                }

                // jRes.error
                if (ctxt.jRes.error) {
                    throw Error(ctxt.jRes.errorMsg);
                }

                // start
                if (params.start === undefined) {
                    throw Error("start must be defined");
                }

                // locale
                if (!params.locale) {
                    throw Error("locale must be defined");
                }

                return true;
            },

            /!**
             * POST more template to print more results button
             * @param ctxt
             *!/
            moreTemplate: function (ctxt) {
                console.log("moreTemplate", ctxt);

                // Load params for template
                var params = ctxt.sg.paramsMoreTemplate(ctxt);

                if (ctxt.sg.validateMoreTemplate(ctxt, params)) {

                    // POST to template
                    $.post(ctxt.datas.more, params)
                        .done(function (data) {
                            console.log("POST moreTemplate", data);

                            var $more = $(ctxt.sg._more);

                            $more.html(data);
                            ctxt.sg.updateMore(ctxt);
                            ctxt.sg.showFooter(ctxt);
                        })
                        .fail(function (err) {
                            console.error("Error loading offers from template", ctxt, err);

                            ctxt.sg.showFooter(ctxt);
                        })
                }
            },

            /!**
             * update more for reloading list on click more results button
             * @param ctxt
             *!/
            updateMore: function (ctxt) {
                console.log("updateFooter", ctxt);
                // Paginate on click btn more
                var $btnMore = $(ctxt.sg._moreBtnClass);
                $btnMore.on('click', ctxt.sg.moreResults);
            },

            showFooter: function (ctxt) {
                $(ctxt.sg._loading).hide();
                $(ctxt.sg._more).show();
            },

            moreResults: function (e) {
                // Hide more results btn
                var $moreBtn = $(this);
                $moreBtn.hide();

                // Create context
                var ctxt = {};
                ctxt.sg = window.SgVueList;

                // Load data starting on btn attribute data-start
                var datas = $moreBtn.data();
                if (datas && datas.start) {
                    // Start on more results button data-start
                    ctxt.start = Number(datas.start);
                    ctxt.sg.loadResults(ctxt);
                }
            }*/
        };

        return SgVueList;
    }

    //define globally if it doesn't already exist
    if (typeof(window.SgVueList) === 'undefined') {
        window.SgVueList = SgVueList();
    }

    // Auto init
    window.SgVueList.initialize();


})(window.jQuery);