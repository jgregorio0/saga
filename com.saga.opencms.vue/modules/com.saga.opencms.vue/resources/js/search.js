/**
 * VUE Searcher v1.0
 * ==============
 *
 * VUE Loader for searcher
 */

(function (root, Vue) {

    if (root.Vue === undefined) {
        throw Error("Vue must be defined");
    }

    //define globally if it doesn't already exist
    //root.Saga = root.Saga || {};
    /*if (typeof(root.Saga.SgVueList) === 'undefined') {
     root.Saga.SgVueList = SgVueList();
     }*/

    // Auto init
    //window.SgVueList.initialize();

    //function SgVueList() {
    var vueSearch = {
        _id: "vue-search",
        _locale: "",
        _rows: "",
        _controller: "",
        _query: "",
        _filters: [],
        _showForm: "true",

        initialize: function (opts) {
            if (opts) {
                this._id = opts.id || this._id;
                this._locale = opts.locale;
                this._rows = opts.rows;
                this._controller = opts.controller;
                this._query = opts.query;
                this._filters = opts.filters || this.sortFilters(this._filters);
                this._showForm = opts.showForm || this._showForm;
            }

            var el = document.getElementById(this._id);
            if (!el) {
                console.debug("element " + this._id + " not exists");
            } else {
                console.debug("init list", this._id, this._locale, this._rows, this._controller, this._query);

                // create VUE Object
                new Vue({
                    el: '#' + this._id,
                    data: {
                        id: this._id,
                        controller: this._controller,
                        locale: this._locale,
                        rows: Number(this._rows),
                        query: this._query,
                        filters: this._filters,
                        start: 0,
                        total: 0,
                        datasSize: 0,
                        datas: [],
                        fields: [],
                        loading: false,
                        showForm: this._showForm === "true"
                    },
                    mounted: function () {
                        this.initDatePicker();
                        this.initResults();
                    },
                    methods: {
                        /**
                         * Validate controller, locale and rows
                         *
                         */
                        validateContext: function () {
                            console.debug("validateContext", this.controller, this.locale, this.rows);

                            // controller
                            if (!this.controller) {
                                throw Error("controller undefined");
                            }

                            // locale
                            if (!this.locale) {
                                throw Error("locale undefined");
                            }

                            // rows
                            if (this.rows === undefined) {
                                throw Error("rows undefined");
                            }

                            return true;
                        },

                        /**
                         * Load data from main container, post controller to get results and post template for print them.
                         * start === 0 means first loading.
                         */
                        loadResults: function () {
                            console.log("loadResults", this.start);

                            if (this.validateContext()) {

                                // load list datas
                                this.listController();
                            }
                        },

                        /**
                         * Update attributes after receiving json response
                         */
                        update: function (jRes) {
                            console.debug("update", jRes);
                            this.total = Number(jRes.total);
                            this.datasSize = Number(jRes.dataSize);
                            if (this.start == 0) {
                                this.datas = jRes.data;
                            } else {
                                this.datas = this.datas.concat(jRes.data);
                            }

                            //this.query = jRes.query;
                            this.fields = jRes.fields;
                        },

                        /**
                         * Load data from main container, post controller to get results and post template for print them.
                         * start === 0 means first loading.
                         */
                        loadMoreResults: function () {
                            this.start = (this.start + this.rows);
                            this.loadResults();
                        },

                        /**
                         * Load data from main container, post controller to get results and post template for print them.
                         * start === 0 means first loading.
                         */
                        initResults: function () {
                            this.start = 0;
                            this.loadResults();
                        },

                        /**
                         * Load params for update list
                         */
                        loadListControllerParams: function () {
                            console.debug("loadListControllerParams", this.locale, this.rows, this.start);
                            var params = {};

                            // params
                            params.locale = this.locale;
                            params.rows = this.rows;
                            params.start = this.start;
                            params.query = encodeURI(this.query);
                            params.filters = encodeURI(JSON.stringify(this.filters));

                            return params;
                        },

                        /**
                         * Validate attributes and params
                         *
                         * @param params
                         */
                        validateListController: function (params) {
                            console.debug("validateListController", this.controller, params.locale, params.start);
                            // controller
                            if (!this.controller) {
                                throw Error("controller must be defined")
                            }

                            // locale
                            if (!params.locale) {
                                throw Error("locale must be defined")
                            }

                            // start
                            if (params.start === undefined) {
                                throw Error("start must be defined")
                            }

                            return true;
                        },

                        /**
                         * Post controller to get results and post template to print them
                         */
                        listController: function () {
                            console.debug("listController");

                            var params = this.loadListControllerParams();

                            if (this.validateListController(params)) {

                                // Show loading
                                this.loading = true;

                                // Load data from controller
                                ((this.$http.get(this.controller, {params: params}).then(function (response) {
                                    // Response to JSON
                                    try {
                                        if (response.status !== 200) {
                                            throw Error("Response failed");
                                        }

                                        var jRes = response.body;

                                        // Check if error
                                        if (!jRes.error) {

                                            // If validate then update
                                            this.update(jRes);
                                        } else {
                                            console.error("JSON Response", jRes.errorMsg, jRes.errorTrace);
                                        }
                                    } catch (err) {
                                        console.error('loading data', params, response, err);
                                    } finally {
                                        this.loading = false;
                                    }
                                }, function (err) {
                                    console.error("POST controller", params, err);

                                    // Hide loading and show results
                                    this.loading = false;
                                })));
                            }
                        },

                        /**
                         * Load JQuery datepicker filters
                         */
                        initDatePicker: function () {
                            // Datepicker
                            var dplocale = this.locale === 'en' ? "en-GB" : this.locale;
                            $.datepicker.setDefaults($.datepicker.regional[dplocale]);

                            var vm = this;
                            $('.datepicker').datepicker({
                                onSelect: function(selectedDate) {
                                    if (selectedDate) {
                                        var id = this.id;

                                        var iLastDash = id.lastIndexOf("-");
                                        var count = id.substring(iLastDash + 1);
                                        var filterField = "value";

                                        var iUndescore = id.indexOf("_d");
                                        if (iUndescore > 0) {
                                            filterField = id.includes("_d1") ? "valueDate1" : "valueDate2";
                                            count = id.substring(iLastDash + 1, iUndescore);
                                        }
                                        vm.setFilterValue(Number(count), filterField, selectedDate);
                                    }
                                }
                            });
                        },

                        /**
                         * Set filter value
                         * @param id
                         * @param value
                         */
                        setFilterValue: function(count, field, value){
                            this.filters[count - 1][field] = value;
                        }
                    }

                });
            }
        },

        /**
         * Sort filters by count
         * @param filters
         */
        sortFilters: function (filters) {
            return filters.sort(
                function(x, y)
                {
                    return x.count - y.count;
                }
            );
        }
    };

    //    return SgVueList;
    //}
    root.Saga = root.Saga || {};
    root.Saga.vueSearch = vueSearch;
})(window, Vue);