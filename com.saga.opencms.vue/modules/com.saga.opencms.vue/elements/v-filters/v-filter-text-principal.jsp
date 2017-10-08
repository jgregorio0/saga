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
"isPrincipal": true/false
"value": ""
"showExactSearch": true/false
"labelExactResult": "${value.LabelExactResult}"
"buttonText": "${value.LabelExactResult}"
"buttonClass": "${value.LabelExactResult}"
}--%>

<div class="row"
     v-if="filter.type == 'TextFilter' && filter.isPrincipal">
    <div class="col-sm-6 col-sm-offset-3">
        <label :for="filter.id"
               v-if="filter.label">{{filter.label}}</label>

        <div :class="'input-group filter filter-text filter-main ' + filter.class"
             :id="'filter-' + filter.count">
            <%--<c:set var="searchButtonPrint" value="true"/>--%>
            <input type="text" class="form-control block-level"
                   :placeholder="filter.placeholder" id="filter.id" :name="filter.id"
                   v-model="filter.value">
            <%--<input type="hidden" name="numfield" value="${contField }"/>
            <input type="hidden" name="searchaction" value="search"/>
            <input type="hidden" id="${value.Id }-searchPage" name="searchPage" value="1"/>--%>
                <span class="input-group-btn">
                    <button type="button" name="submit"
                            :value="filter.buttonText" :class="'btn ' + filter.buttonClass">
                        {{filter.buttonText}}
                    </button>
                </span>
        </div>

        <div class="checkbox"
             v-if="filter.showExactSearch">
            <label for="exactSearch" class="exact-search">
                <input type="checkbox" name="exactSearch" id="exactSearch">{{filterlabelExactResult}}
            </label>
        </div>
    </div>
</div>