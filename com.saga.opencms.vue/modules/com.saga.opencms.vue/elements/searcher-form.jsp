<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="${cms.locale}"/>
<cms:bundle basename="com.saga.opencms.vue.searcher.messages">
    <form id="v-searcher-form" action="#" method="post">

            <%--HOME DELIVERY--%>
        <div class="ofertas-btn-section">
            <div class="row">
                <div class="col-lg-offset-1 col-lg-10">
                    <div id="v-home-delivery" class="row">
                        <div class="col-lg-5 col-xs-12 mb-15">
                            <button type="button" id="search-ofertas-by-store"
                                    class="btn btn-lg btn-block btn-brand pull-left"
                                    v-on:click="updateFilter('homeDelivery', false)">
                                <span class="bg-icon bg-tienda bg-icon-30 mr-20" aria-hidden="true"></span>
                                <fmt:message key="ofertas.search.by.store"/>
                            </button>
                        </div>
                        <div class="col-lg-5 col-lg-offset-2 col-xs-12">
                            <button type="button" id="search-ofertas-by-capraboacasa"
                                    class="btn btn-lg btn-block btn-white pull-right"
                                    v-on:click="updateFilter('homeDelivery', false)">
                                <span class="bg-icon bg-icon-30 mr-20 bg-shop-phone" aria-hidden="true"></span>
                                <fmt:message key="ofertas.search.by.capraboacasa"/>
                            </button>
                        </div>
                        <input type="text" id="homeDelivery" name="homeDelivery"
                               v-bind:value="filters.homeDelivery.val">
                    </div>
                </div>
            </div>
        </div>

            <%-- bloque de formulario con fondo azul claro --%>
        <div class="bg-brand-second-light fill-to-border mt-40 pt-40 pb-40 clearfix"
             v-if="showStoreFilters">
            <div class="row">
                <div class="col-lg-offset-1 col-lg-10">

                        <%--STORE--%>
                    <div class="form-group row mb-30">
                        <div class="col-xs-12">
                            <label><fmt:message key="ofertas.search.filter.where"/></label>
                        </div>

                        <div class="col-lg-10">
                            <div class="row">

                                    <%--REGION--%>
                                <div class="col-lg-4 col-xs-12 mb-15">
                                    <span <%--v-if="isRegionLoading"--%> class="form-control" id="region-spinner">
                                        <i class="fa fa-spinner fa-spin" aria-hidden="true"></i>
                                    </span>
                                    <select <%--v-else --%> class="search-ofertas form-control unselected" name="region"
                                                            id="region"
                                                            aria-label="<fmt:message key="ofertas.search.filter.region"/>"
                                                            data-ctxt='{"id":"region",
                                                "form":"v-searcher-form"
                                                ,"locale":"${cms.locale}"
                                                ,"url":"<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/api/v1/regions/</cms:link>"
                                                <%--,"default":"--- <fmt:message key="ofertas.search.filter.region"/> ---"--%>
                                                ,"prevs":["homeDelivery"],
                                                ,"nexts":["city"]}'
                                                            v-on:change="loadOrResetNext($event)">
                                        <option value="" selected="selected">--- <fmt:message
                                                key="ofertas.search.filter.region"/> ---
                                        </option>
                                        <option v-for="region in regions" v-bind:value="region.value">{{region.text}}
                                        </option>

                                            <%--<option value="" selected="selected">--- <fmt:message
                                                    key="ofertas.search.filter.region"/> ---
                                            </option>--%>
                                    </select>
                                </div>

                                    <%--CITY--%>
                                    <%--<div class="col-lg-4 col-xs-12 mb-15">
                                    <span class="form-control" id="city-spinner"><i class="fa fa-spinner fa-spin"
                                                                                    aria-hidden="true"></i></span>
                                        <select class="search-ofertas form-control unselected" name="city" id="city"
                                                aria-label="<fmt:message key="ofertas.search.filter.city"/>"
                                                data-ctxt='{"id":"city"
                                                    ,"locale":"${cms.locale}"
                                                    ,"url":"<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/api/v1/cities/</cms:link>"
                                                    ,"default":"--- <fmt:message key="ofertas.search.filter.city"/> ---"
                                                    ,"prevs":["region"]
                                                    ,"nexts":["location", "category", "subcategory"]}'>
                                            <option value="" selected="selected">--- <fmt:message
                                                    key="ofertas.search.filter.city"/> ---
                                            </option>
                                        </select>
                                    </div>--%>

                                    <%--LOCATION--%>
                                    <%--<div class="col-lg-4 col-xs-12 mb-15">
                                    <span class="form-control" id="location-spinner"><i class="fa fa-spinner fa-spin"
                                                                                        aria-hidden="true"></i></span>
                                        <select class="search-ofertas form-control unselected" name="location" id="location"
                                                aria-label="<fmt:message key="ofertas.search.filter.location"/>"
                                                data-ctxt='{"id":"location"
                                                    ,"locale":"${cms.locale}"
                                                    ,"url":"<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/api/v1/locations/</cms:link>"
                                                    ,"default":"--- <fmt:message key="ofertas.search.filter.location"/> ---"
                                                    ,"prevs":["region", "city"]
                                                    ,"nexts":["category","subcategory"]}'>
                                            <option value="" selected="selected">--- <fmt:message
                                                    key="ofertas.search.filter.location"/> ---
                                            </option>
                                        </select>
                                    </div>--%>
                            </div>
                        </div>
                            <%--<div class="col-lg-2 col-xs-12">
                                <input type="hidden" id="locale" name="locale" value="${cms.locale}">
                                <input type="hidden" id="uri" name="uri" value="${cms.vfs.requestContext.uri}">
                                <input type="hidden" id="site" name="site" value="${cms.vfs.requestContext.siteRoot}">
                                <input type="hidden" id="rows" name="rows" value="5">
                                <input type="hidden" id="submit" name="submit" value="submit">
                                <input type="hidden" id="search-ofertas-delivery" name="homeDelivery" value="">

                                <button id="search-ofertas-submit-btn" class="btn btn-brand btn-block" type="submit">
                                    <fmt:message key="ofertas.search.submit"/>
                                </button>

                                <button id="change-shop-btn" class="btn btn-brand no-margin btn-block" data-toggle="modal"
                                        data-target="#popup-change-shop" type="button">
                                    <fmt:message key="ofertas.search.change"/>
                                </button>

                            </div>--%>
                    </div>

                        <%--BUSQUEDA AVANZADA--%>
                        <%--<div id="search-ofertas-advancedsearch"
                             v-if="showAdvancedSearch">
                            <div class="pt-25">
                                <div class="form-group row mb-30">
                                    <div class="col-lg-4 col-xs-12 mb-15">
                                        <label for="category"><fmt:message key="ofertas.search.filter.category"/></label>
                                        <select class="search-ofertas form-control" name="category" id="category"
                                                aria-label="<fmt:message key="ofertas.search.filter.category"/>"
                                                data-ctxt='{"id":"category"
                                                    ,"locale":"${cms.locale}"
                                                    ,"url":"<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/api/v1/categories/</cms:link>"
                                                    ,"default":"--- <fmt:message key="ofertas.search.filter.category"/> ---"
                                                    ,"prevs":["region","city","location"]
                                                    ,"nexts":["subcategory"]
                                                    ,"selected":"${param.category}"}'>
                                            <option value="" selected="selected">--- <fmt:message
                                                    key="ofertas.search.filter.category"/> ---
                                            </option>
                                        </select>
                                    </div>
                                    <div class="col-lg-4 col-xs-12 mb-15">
                                        <label for="subcategory"><fmt:message
                                                key="ofertas.search.filter.subcategory"/></label>
                                        <select class="search-ofertas form-control" name="subcategory" id="subcategory"
                                                aria-label="<fmt:message key="ofertas.search.filter.subcategory"/>"
                                                data-ctxt='{"id":"subcategory"
                                                    ,"locale":"${cms.locale}"
                                                    ,"url":"<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/api/v1/subcategories/</cms:link>"
                                                    ,"default":"--- <fmt:message key="ofertas.search.filter.subcategory"/> ---"
                                                    ,"prevs":["region","city","location","category"]
                                                    ,"selected":"${param.subcategory}"}'>
                                            <option value="" selected="selected">--- <fmt:message
                                                    key="ofertas.search.filter.subcategory"/> ---
                                            </option>
                                        </select>
                                    </div>
                                    <div class="col-lg-4 col-xs-12 mb-15">
                                        <label for="offertype"><fmt:message
                                                key="ofertas.search.filter.offer.type"/></label>
                                        <select class="search-ofertas form-control" name="offertype" id="offertype"
                                                aria-label="<fmt:message key="ofertas.search.filter.offer.type"/>"
                                                data-ctxt='{"id":"offertype"
                                                    ,"locale":"${cms.locale}"
                                                    ,"url":"<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/api/v1/types/</cms:link>"
                                                    ,"default":"--- <fmt:message key="ofertas.search.filter.offer.type"/> ---"
                                                    ,"prevs":["region","city","location"]
                                                    ,"nexts":[]
                                                    ,"selected":"${param.offertype}"}'>
                                            <option value="" selected="selected">--- <fmt:message
                                                    key="ofertas.search.filter.offer.type"/> ---
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group row mb-30">
                                    <div class="col-xs-12">
                                        <label for="content" class="sr-only"><fmt:message
                                                key="ofertas.search.filter.text.placeholder"/></label>

                                        <div class="input-group">
                                            <input type="text" id="content" name="content" class="form-control"
                                                   placeholder="<fmt:message key="ofertas.search.filter.text.placeholder"/>">
                                        <span class="input-group-btn">
                                        <button class="btn btn-default" type="submit" id="text-btn"><span
                                                class="fa fa-search"
                                                aria-hidden="true"></span>
                                        </button>
                                        </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row text-center">
                                        &lt;%&ndash;<div class="col-lg-3 col-xs-6">
                                            <div class="checkbox to-right">
                                                <label>
                                                    <img class="inline-b v-align-m mr-5"
                                                         src="<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/resources/images/lupa-rosa.png</cms:link>"
                                                         aria-hidden="true"/>
                                                    <span class="inline-b v-align-m mr-5" id="labelcomparafield"><fmt:message
                                                            key="ofertas.search.filter.comparador"/></span>
                                                    <input type="checkbox" value="comparador" name="comparador"
                                                           aria-labelledby="labelcomparafield"
                                                           <c:if test="${not empty param.comparador}">checked="checked"</c:if>>
                                                </label>
                                            </div>
                                        </div>&ndash;%&gt;
                                    <div class="col-lg-3 col-xs-6">
                                        <div class="checkbox to-right">
                                            <label>
                                                <img class="inline-b v-align-m mr-5"
                                                     src="<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/resources/images/sin-gluten.png</cms:link>"
                                                     aria-hidden="true"/>
                                        <span class="inline-b v-align-m mr-5" id="labelglutenfield"><fmt:message
                                                key="ofertas.search.filter.singluten"/></span>
                                                <input type="checkbox" name="singluten"
                                                       aria-labelledby="labelglutenfield"
                                                       <c:if test="${not empty param.singluten}">checked="checked"</c:if>>
                                            </label>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-6">
                                        <div class="checkbox to-right">
                                            <label>
                                                <img class="inline-b v-align-m mr-5"
                                                     src="<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/resources/images/ecologico.png</cms:link>"
                                                     aria-hidden="true"/>
                                            <span class="inline-b v-align-m mr-5" id="labelecofield"><fmt:message
                                                    key="ofertas.search.filter.ecologico"/></span>
                                                <input type="checkbox" name="ecologico"
                                                       aria-labelledby="labelecofield"
                                                       <c:if test="${not empty param.ecologico}">checked="checked"</c:if>>
                                            </label>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-6">
                                        <div class="checkbox to-right">
                                            <label>
                                                <img class="inline-b v-align-m mr-5"
                                                     src="<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/resources/images/local-cat.png</cms:link>"
                                                     aria-hidden="true"/>
                                                <img class="inline-b v-align-m mr-5"
                                                     src="<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/resources/images/local-nav.png</cms:link>"
                                                     aria-hidden="true"/>
                                            <span class="inline-b v-align-m mr-5" id="labellocalfield"><fmt:message
                                                    key="ofertas.search.filter.local"/></span>
                                                <input type="checkbox" name="local"
                                                       aria-labelledby="labellocalfield"
                                                       <c:if test="${not empty param.local}">checked="checked"</c:if>>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>--%>
                </div>
            </div>
        </div>
    </form>

    <script>
        var VForm = new Vue({
                    el: "#v-searcher-form",
                    data: {
                        showStoreFilters: false,
                        showAdvancedSearch: false,
                        isRegionLoading: false,
                        filters: {
                            "homeDelivery": {"id": "homeDelivery", "val": null, "nexts": ["region"]},
                            "region": {
                                "id": "region",
                                "val": null,
                                "options": [],
                                "default": "--- <fmt:message key="ofertas.search.filter.region"/> ---",
                                "prevs": ["homeDelivery"],
                                "nexts": ["city"],
                                "url": "<cms:link>/system/modules/com.caprabo.mrmmccann.caprabocom.ofertas/api/v1/regions/</cms:link>",
                                "locale": "${cms.locale}"
                            }
                        }
                    },
                    methods: {
                        isHomeDelivery: function (val) {
                            console.log("isHomeDelivery", val);
                            this.filters.homeDelivery.val = Boolean(val);
                            this.showStoreFilters = true;
                        },
                        updateFilter: function (id, val) {
                            console.log("updateFilter", id, val);
                            if (!id || this.filters[id]) {
                                throw Error("id must not be empty and filter must be defined");
                            }

                            var filter = this.filters[id];
                            if (!validateFilters(filter.prevs)) {
                                resetFilter(id);
                            } else {
                                filter.val = val;
                                loadFilters(filter.nexts);
                            }
                        }
                    }
                });

        /**
         * Check if prevs filters have value
         */
        function validateFilters(prevs){
           //TODO
        }

        /**
         * Load next filters
         */
        function loadFilters(nexts) {
            //TODO
        }

        /**
         * Load filter options using data
         * @param ctxt
         */
        function loadOrResetNext(value, ctxt) {
            console.log('loadOrResetNext', value, ctxt);

            if (value) {
                loadFilters(ctxt.nexts);
            } else {
                resetFilter(ctxt.nexts);
            }
            // clear select
//            resetSelect(ctxt.id);

            // check if depending inputs are selected
//            if (prevsHaveBeenSelected(ctxt.prevs)) {

            // Show loading and hide select
//                showLoading(ctxt.id);

            // getPrevValues
//                addPreviousToCtxt(ctxt);
            //console.log("addPreviousToCtxt", ctxt);


            // AJAX request
            /*axios.get(ctxt.url, ctxt)
             .then(function (res) {
             console.log("for url: " + ctxt.url, res);

             /!*try {
             var json = JSON.parse(data);

             // add data options
             appendOptions(ctxt, json);
             // Hide loading and show select
             showResults(ctxt.id);
             } catch (err) {
             console.error('loading data', ctxt, data, err);
             }*!/
             })
             .catch(function (err) {
             console.log("for url: " + ctxt.url, err);
             });*/
        }

        /**
         * Add to context all form input values
         * @param ctxt
         */
        function loadInputs(ctxt) {
            $('#' + ctxt.form + ' :input').each(function (i, e) {
                var $input = $(e);
                var inputName = $input.attr('name');
                if (inputName) {
                    if ($input.attr('type') === 'checkbox') {
                        ctxt[inputName] = $input.is(":checked");
                    } else {
                        ctxt[inputName] = $input.val();
                    }
                }
            });
        }

        /**
         * Reset select options
         * @param nexts
         */
        function resetFilters(nexts) {
            console.log('resetFilters', nexts);
            if (!nexts || nexts.length == 0) {
                throw Error('resetFilters: nexts must not be empty', id);
            }

            for (idNext in nexts) {
                resetFilter(idNext)
            }
        }

        /**
         * resetFilter
         * @param idNext
         */
        function resetFilter(idNext) {
            VForm.filters[idNext].val = null;
            VForm.filters[idNext].options = [];
            resetFilters()
        }


    </script>
</cms:bundle>