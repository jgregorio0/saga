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
    <%--<script src="https://cdnjs.cloudflare.com/ajax/libs/vuex/3.0.1/vuex.min.js"></script>--%>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/vue/2.5.16/vue.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/vue-resource/1.5.0/vue-resource.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/vuex/3.0.1/vuex.js"></script>


    <div id="v-list"
         <%--plocale="en"
         prows="10"
         pstart="0"
         pcontroller="/vue-controller.jsp"
         ptemplate="/vue-template-list.jsp"
         pquery='&fq=type:("image")&fq=con_locales:en&fq=parent-folders:("/sites/default/")'--%>></div>
    <script>

        // STORE
        /*var store = new Vuex.Store({
            state: {
                vFilters: {}
            },
            getters: {
                vFilters: function (state) {
                    return state.vFilters
                }
            },
            mutations: {
                initFilters: function (state, payload) {
                    var json = {};
                    payload.forEach(function (vFilter, index) {
                        vFilter.index = index;
                        json[vFilter.id] = vFilter;
                    });
                    state.vFilters = json;
                },
                updateFilter: function (state, payload) {
                    state.vFilters[payload.id].value = payload.value;
                }
            }
        });*/

        var vm = new Vue({
            el: '#v-list',
            // store: store,
            props: {
                id: {
                    required: true
                }/*,
                plocale: {
                    required: true
                },
                prows: {
                    required: true,
                    type: Number
                },
                pstart: {
                    required: true,
                    type: Number
                },
                pcontroller: {
                    required: true
                },
                ptemplate: {
                    required: true
                },
                pquery: {
                    required: true
                }*/
            },
            /*data: function () {
                return {
                    vFilters: filters._vnode.data.props.pFilters,
                    vLoading: false,
                    vStart: this.pstart,
                    vTotal: 0,
                    vDatasSize: 0,
                    vDatas: [],
                    vFields: ""
                }
            },
            /!*created () {
                events.$on('filtersChanged', function () {
//                console.log('$on filtersChanged', filters._vnode.data.props.pFilters);
//                this.vFilters = filters._vnode.data.props.pFilters;
                    this.initResults();
                });
                events.$on('loadMoreResults', function () {
//                console.log('on filters loadMoreResults');
                    this.loadMoreResults();
                })
            },*!/
            mounted () {
                this.loadResults();
            },
            methods: {

                /!**
                 * Load data from main container, post controller to get results and post template for print them.
                 * start === 0 means first loading.
                 *!/
                loadResults: function () {
//                console.log("loadResults", this.vStart);

                    if (this.validateContext()) {

                        // load list datas
                        this.getResults();
                    }
                },

                /!**
                 * Validate controller, locale and rows
                 *
                 *!/
                validateContext: function () {
//                console.debug("validateContext", this.pcontroller, this.plocale, this.prows);

                    // controller
                    if (!this.pcontroller) {
                        throw Error("controller undefined");
                    }

                    // locale
                    if (!this.plocale) {
                        throw Error("locale undefined");
                    }

                    // rows
                    if (this.prows === undefined) {
                        throw Error("rows undefined");
                    }

                    return true;
                },

                /!**
                 * Post controller to get results
                 * GET {"controller.jsp", {"locale": "es", "rows": 10, "start": 0, "query": "fq=...", "filters": "date..."}
             * RESPONSE {"error": true/false, "errorMsg": "...", "errorTrace": "....", "dataSize": 10, "data": []}
             *!/
                getResults: function () {
//                console.debug("getResults");

                    var params = this.getResultsParams();

                    if (this.validateGetResultsParams(params)) {

                        // Show loading
                        this.vLoading = true;

                        // Load data from controller
                        ((this.$http.post(this.pcontroller, {params: params}).then(function (response) {
                            // Response to JSON
                            try {
                                if (response.status !== 200) {
                                    throw Error("Response failed");
                                }

                                var jRes = response.body;

                                // OCMS handleSolrSelect
//                            this.update(jRes);

                                // Custom controller
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
                                this.vLoading = false;
                            }
                        }, function (err) {
                            console.error("POST controller", params, err);

                            // Hide loading and show results
                            this.vLoading = false;
                        })));
                    }
                },

                /!**
                 * Load params for update list
                 *!/
                getResultsParams: function () {
//                console.debug("getResultsParams", this.plocale, this.prows, this.vStart, this.pquery, this.vFilters);
                    var params = {};

                    // params
                    params.locale = this.plocale;
                    params.rows = this.prows;
                    params.start = this.vStart;
                    params.query = encodeURI(this.pquery);
                    params.filters = encodeURI(JSON.stringify(this.vFilters));

                    return params;
                },

                /!**
                 * Validate attributes and params
                 *
                 * @param params
                 *!/
                validateGetResultsParams: function (params) {
//                console.debug("validateGetResultsParams", this.pcontroller, params.locale, params.start);
                    // controller
                    if (!this.pcontroller) {
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

                /!**
                 * Update attributes after receiving json response
                 *!/
                update: function (jRes) {
//                console.debug("update", jRes);
//                OCMS handleSolrSelect
                    /!*this.vTotal = Number(jRes.response.numFound);
                    this.vDatasSize = Number(jRes.response.docs.length);
                    if (this.vStart == 0) {
                        this.vDatas = jRes.response.docs;
                    } else {
                        this.vDatas = this.vDatas.concat(jRes.response.docs);
                    }*!/

//                Custom controller
                    this.vTotal = Number(jRes.total);
                    this.vDatasSize = Number(jRes.dataSize);
                    if (this.vStart == 0) {
                        this.vDatas = jRes.data;
                    } else {
                        this.vDatas = this.vDatas.concat(jRes.data);
                    }
                    this.vFields = jRes.vFields;
                },

                /!**
                 * Load data from main container, post controller to get results and post template for print them.
                 * start === 0 means first loading.
                 *!/
                loadMoreResults: function () {
                    this.vStart = (this.vStart + this.prows);
                    this.loadResults();
                },

                /!**
                 * Load data from main container, post controller to get results and post template for print them.
                 * start === 0 means first loading.
                 *!/
                initResults: function () {
                    this.vStart = 0;
                    this.loadResults();
                }
            }*/
        })

    </script>


</div>