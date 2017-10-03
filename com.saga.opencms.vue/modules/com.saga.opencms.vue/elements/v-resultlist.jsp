<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<fmt:setLocale value="${cms.locale}"/>
<cms:bundle basename="com.saga.sagasuite.search.messages">

    <%--SEARCH CONTAINER--%>
    <div id="${value.Id}" class="col-lg-12"
         v-cloak>
            <%--FILTERS--%>
        <div id="filters" class="col-lg-12 nopadding"
             v-if="filters.length > 0"
             v-show="showForm">

            <div class="box-search well ">
                <div class="filterbox">
                    <div class="filterbox-content  row" id="filters-buscador">
                        <div v-for="(filter, index) in filters">

                                <%--FILTER TEXT PRINCIPAL--%>
                            <cms:include
                                    file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/v-filters/v-filter-text-principal.jsp:7ee9fb2a-a768-11e7-90b6-7fb253176922)"/>

                                <%--FILTER TEXT--%>
                            <cms:include
                                    file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/v-filters/v-filter-text.jsp:7ecbebd7-a768-11e7-90b6-7fb253176922)"/>

                                <%--FILTER DATE 1 FIELD--%>
                            <cms:include
                                    file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/v-filters/v-filter-date1.jsp:7e7f7981-a768-11e7-90b6-7fb253176922)"/>

                                <%--FILTER DATE FROM AND TO FIELDS--%>
                            <cms:include
                                    file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/v-filters/v-filter-date2.jsp:7eab6b84-a768-11e7-90b6-7fb253176922)"/>

                                <%--FILTER CATEGORIES--%>
                            <cms:include
                                    file="%(link.strong:/system/modules/com.saga.sagasuite.search/elements/v-filters/v-filter-category.jsp:eb533c8d-a78c-11e7-90b6-7fb253176922)"/>



                        </div>
                            <%--<input type="hidden" name="numfield" :value="filters.length">--%>
                            <%--<input type="hidden" name="searchaction" value="search">--%>
                            <%--<input type="hidden" id="buscador-searchPage" name="searchPage" value="1">--%>
                        <div class="botones col-sm-12">
                            <button type="button" name="search" :id="id + '-submit'"
                                    value='<fmt:message key="search.filter.submit.btn"/>'
                                    class="ts-botontxt_buscar btn btn-default"
                                    @click="initResults()">
                                <fmt:message key="search.filter.submit.btn"/></button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

            <%--LIST--%>
        <div id="list" class="col-lg-12 nopadding">
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
                    v-if="rows + start < total"
                    v-on:click="loadMoreResults()">
                    <span class="inline-b v-align-m">
                        <fmt:message key="search.more.results.btn"/>
                    </span>
                <span class="fa fa-chevron-down inline-b v-align-m ml-5" aria-hidden="true"></span>
            </button>

                <%--NONE RESULTS--%>
            <div class="btn btn-brand"
                 v-else-if="total == 0">
                <span class="inline-b v-align-m">
                    <fmt:message key="search.more.results.none"/>
                </span>
            </div>
        </div>

            <%--LOADING--%>
        <div id="vue-list-loading" class="col-lg-1  2 nopadding text-center"
             v-else-if="loading">
                <span>
                    <span class="fa fa-spinner fa-spin fa-5x"></span>
                        <%--<fmt:message key="search.loading"/>--%>
                    <span class="sr-only"><fmt:message key="search.loading"/></span>
                </span>
        </div>
    </div>
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</cms:bundle>