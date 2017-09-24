<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

    <fmt:setLocale value="${cms.locale}"/>
    <cms:bundle basename="com.saga.opencms.vue.list.messages">


        <%--LIST CONTAINER--%>
        <div id="vue-list" class="col-lg-12 sg-ajax-list"
             data-ctxt='{"locale":"${cms.locale}"
                    ,"rows":"2"
                    ,"controller":"<cms:link>/system/modules/com.saga.opencms.vue/elements/list-controller.jsp</cms:link>"}'>

                <%--TITLE--%>
            <div class="col-lg-12"><h1><fmt:message key="list.title"/></h1></div>

                <%--LIST--%>
            <div id="vue-list" class="col-lg-12 nopadding">
                <ul>
                    <li v-for="data in datas">
                        <dl>
                            <dt>ID</dt>
                            <dd>{{ data.id}}</dd>
                        </dl>
                        <dl>
                            <dt>PATH</dt>
                            <dd>{{ data.path}}</dd>
                        </dl>
                        <dl>
                            <dt>TITLE</dt>
                            <dd>{{ data.Title_prop}}</dd>
                        </dl>
                    </li>
                </ul>
            </div>

                <%--MORE BTN--%>
            <div id="vue-list-more" class="col-lg-12 nopadding mt-20 mb-20 text-center"
                 v-if="!loading">
                <button type="button" class="btn btn-brand btn-lg vue-list-more-btn" <%--data-start="getLast()"--%>
                        <%--v-if="getLast() < total"--%>
                        v-on:click="loadMoreResults()">
                    <span class="inline-b v-align-m">
                        <fmt:message key="list.more.results.btn"/>
                    </span>
                    <span class="fa fa-chevron-down inline-b v-align-m ml-5" aria-hidden="true"></span>
                </button>

                    <%--NONE RESULTS--%>
                <div class="btn btn-brand"
                     <%--v-else--%>>
                <span class="inline-b v-align-m">
                    <fmt:message key="list.more.results.none"/>
                </span>
                </div>
            </div>

                <%--LOADING--%>
            <div id="vue-list-loading" class="col-lg-1  2 nopadding text-center"
                 v-else="loading">
                <span>
                    <%--<span class="fa fa-spinner fa-spin fa-5x"></span>--%>
                        <fmt:message key="list.loading"/>
                    <span class="sr-only"><fmt:message key="list.loading"/></span>
                </span>
            </div>
        </div>

        <%--<button type="button" class="btn btn-brand btn-lg" id="run-example">START</button>--%>
    </cms:bundle>

<%--<script src="<cms:link>/system/modules/com.saga.opencms.vue/resources/js/list.js</cms:link>"></script>--%>


<script src="https://cdn.jsdelivr.net/npm/vue-resource@1.3.4"></script>

<script>
    var _el = "vue-list";
    var el = document.getElementById(this._el);
    if (!el) {
        console.debug("elemento" + this._el + " not exists");
    } else {

        // Load context (controller, locale and rows)
        var ctxt = JSON.parse(el.dataset.ctxt);

        // create VUE Object
        new Vue({
            el: '#' + this._el,
            data: {
                controller: ctxt.controller,
                locale: ctxt.locale,
                rows: Number(ctxt.rows),
                start: 0,
                total: 0,
                datasSize: 0,
                datas: [],
                query: "",
                fields: [],
                loading: false
            },
            computed: {
                getLast: function () {
                    this.start + this.rows
                }
            },
            mounted: function(){
                this.loadResults();
            },
            methods: {
//                setQuery: function (query) {
//                    console.log("setQuery", query);
//                    this.query = query;

                // start
                /*var queryParams = new URLSearchParams(encodeURI(query));
                    this.setStart(queryParams.get("start"));*/

                    // rows
//                    this.setRows(queryParams.get("rows"));
//                },

                /**
                 * Validate controller, locale and rows
                 *
                 */
                validateContext: function () {
                    console.log("validateContext", this.controller, this.locale, this.rows);

                    // controller
                    if (!this.controller) {
                        throw Error("data-ctxt controller undefined");
                    }

                    // locale
                    if (!this.locale) {
                        throw Error("data-ctxt locale undefined");
                    }

                    // rows
                    if (this.rows === undefined) {
                        throw Error("data-ctxt rows undefined");
                    }

                    return true;
                },

                /**
                 * Load data from main container, post controller to get results and post template for print them.
                 * ctxt.start === 0 means first loading.
                 * @param ctxt
                 * @param start
                 */
                loadResults: function(){
                    console.log("loadResults", this.start);

                    if  (this.validateContext()) {

                        // load list datas
                        this.listController();
                    }
                },

                /**
                 * Update attributes after receiving json response
                 */
                update: function (jRes) {
                    console.log("update", jRes);
                    this.total = Number(jRes.total);
                    this.datasSize = Number(jRes.dataSize);
                    this.datas = jRes.data;
                    this.query = jRes.query;
                    this.fields = jRes.fields;
                },

                /**
                 * Load data from main container, post controller to get results and post template for print them.
                 * ctxt.start === 0 means first loading.
                 * @param ctxt
                 * @param start
                 */
                loadMoreResults: function(){
                    this.start = (this.start + this.rows);
                    this.loadResults();
                },

                /**
                 * Load params for update list
                 * @param ctxt
                 * @param start
                 */
                loadListControllerParams: function () {
                    console.log("loadListControllerParams", this.locale, this.rows, this.start);
                    var params = {};

                    // params
                    params.locale = this.locale;
                    params.rows = this.rows;
                    params.start = this.start;

                    return params;
                },

                /**
                 * Validata datas (ctxt.datas) and inputs (ctxt.params)
                 *
                 * @param ctxt
                 */
                validateListController: function (params) {
                    console.log("validateListController", this.controller, params.locale, params.start);
                    // controller
                    if (!this.controller) {
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
                listController: function () {
                    console.log("listController");

                    var params = this.loadListControllerParams();

                    if (this.validateListController(params)) {

                        // Show loading
                        this.loading = true;

                        // Load data from controller
                        this.$http.get(this.controller).then(function (response) {
                            // Response to JSON
                            try {
                                var jRes = JSON.parse(response);

                                // Check if error
                                if (!jRes.error) {

                                    // If validate then update
                                    this.update(jRes);
                                } else {
                                    console.error("JSON Response", jRes.errorMsg, jRes.errorTrace);
                                }
                            } catch (err) {
                                console.error('loading data', ctxt, response, err);
                            } finally {
                                this.loading = false;
                            }
                        }, function (err) {
                            console.error("POST controller", ctxt, err);

                            // Hide loading and show results
                            this.loading = false;
                        });
                    }
                }
            }
        });
    }
</script>

<%--
<script>
    window.vList = new Vue({
        el: '#vue-list',
        data: {
            total: 0,
            datasSize: 0,
            datas: [],
            query: 2,
            start: 0,
            rows: 10,
            fields: [],
            loading: false
        },
        methods: {
            setDatas: function (datas) {
                this.datasSize = datas.length;
                this.datas = datas;
            },
            setTotal: function (total) {
                this.total = total;
            },
            setQuery: function (query) {
                this.query = query;
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
            }
        }
    });

    document.getElementById("run-example").addEventListener("click", function () {
        console.log("run-example");
        var vList = window.vList;

        // loading true and wait 1 sec
        vList.setLoading(true);
        /*setTimeout(function(){
         window.vList.setLoading(false);
         },1000);

         // query fq=parent-folders:"/sites/default/"&fq=type:image&rows=2&start=0
         vList.setTotal(4);
         vList.setRows(2);
         vList.setStart(0);
         vList.setItems([
         {id:"1", path:"/1", Title_prop:"Uno"}
         ,{id:"2", path:"/2", Title_prop:"Dos"}
         ]);*/
    });
</script>--%>
