<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.opencms.vue.searcher.messages">
    <%--SEARCHER--%>
    <div class="v-searcher"
         data-ctxt='{"id":"v-searcher",
                    "controller":"<cms:link>/system/modules/com.saga.opencms.vue/elements/searcher-controller.jsp</cms:link>"
                    "form": "v-searcher-form",
                    "results": "v-searcher-results",
                    "pagination": "v-searcher-pagination"}'>

        <h1>SEARCHER VUE JS</h1>

        <%--FILTERS--%>
        <cms:include file="%(link.strong:/system/modules/com.saga.opencms.vue/elements/searcher-form.jsp)"/>
        <%--RESULTS--%>
        <div class="v-searcher-results"></div>
        <%--PAGINATION--%>
        <div class="v-searcher-pagination"></div>
    </div>
</cms:bundle>