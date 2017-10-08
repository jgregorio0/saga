<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%--
FILTER TEXT PRINCIPAL
---------------------

"id": "${value.Id}-filter-${status.count}"
"count": ${status.count}
"type": "${elem.name}"
"label": "${elem.value.Label}"
"placeholder": "${elem.value.Placeholder}"
"solr": "${elem.value.FieldSolr}"
"class": "${filtercssclass}"
"isContainsSearch": ${elem.value.ContainsSearch}
"isPrincipal": true
"value": ""
}--%>
<div :class="'form-group ' + filter.class + ' filter filter-date'" :id="'filter-' + filter.count"
     v-if="filter.type == 'Date1Filter'">
    <label :for="filter.id"
           v-if="filter.label">{{filter.label}}</label>
    <label :for="filter.id" class="sr-only"
           v-else>{{filter.id}}</label>
    <input type="text" :name="filter.id" :id="filter.id" class="form-control datepicker"
           v-model="filter.value">
</div>