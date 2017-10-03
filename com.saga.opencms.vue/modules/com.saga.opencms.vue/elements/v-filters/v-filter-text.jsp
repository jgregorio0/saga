<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%--
FILTER TEXT
-----------

"id": "${value.Id}-filter-${status.count}"
,"count": ${status.count}
,"type": "TextFilter"
<c:if test="${elem.value.Label.isSet}">,"label": "${elem.value.Label}"</c:if>
<c:if test="${elem.value.Placeholder.isSet}">,"placeholder": "${elem.value.Placeholder}"</c:if>
,"solr": "${elem.value.FieldSolr}"
,"isPrincipal": false
,"class": "${filtercssclass}"
,"value": ""
--%>

<div :class="'form-group filter filter-text ' + filter.class" :id="'filter-' + filter.count"
     v-if="filter.type == 'TextFilter' && !filter.isPrincipal">
    <label :for="filter.id"
            v-if="filter.label">{{filter.label}}</label>
    <label :for="filter.id" class="sr-only"
            v-else>{{filter.placeholder}}</label>
    <input type="text" class="form-control" :placeholder="filter.placeholder"
           :name="filter.id" :id="filter.id"
           v-model:value="filter.value">
</div>