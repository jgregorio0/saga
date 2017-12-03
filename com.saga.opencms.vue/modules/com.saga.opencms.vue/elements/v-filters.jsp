<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%-- Carga los filtros --%>
<c:set var="filters" scope="request">[
    {
    "id": "v-searcher-text-filter-1"
    ,"count": 1
    ,"type": "TextFilter"
    ,"label": "Texto"
    ,"placeholder": "Introduzca texto"
    ,"solr": "content_es"
    ,"class": "col-sm-12"
    ,"isExactSearch": false
    ,"isContainsSearch": false
    ,"value": "<c:if test='${not empty param["v-searcher-text-filter-1"]}'>${param["v-searcher-text-filter-1"]}</c:if>"
    ,"showExactSearch": true
    ,"labelExactResult": "Búsqueda exacta"
    ,"buttonText": "Buscar"
    ,"buttonClass": "btn-default"
    }
    ,{
    "id": "v-searcher-text-filter-2"
    ,"count": 2
    ,"type": "TextFilter"
    ,"label": "Texto 2"
    ,"class": "col-sm-6"
    ,"value": "<c:if test='${not empty param["v-searcher-text-filter-2"]}'>${param["v-searcher-text-filter-2"]}</c:if>"
    ,"placeholder": "Buscar texto"
    ,"solr": "content_es"
    ,"isPrincipal": false
    ,"isExactSearch": false
    ,"isContainsSearch": false
    }
    ,{
    "id": "v-searcher-date-filter-3"
    ,"count": 3
    ,"type": "Date1Filter"
    ,"label": "Fecha 1"
    ,"class": "col-sm-6"
    ,"value": "<c:if test='${not empty param["v-searcher-date-filter-3"]}'>${param["v-searcher-date-filter-3"]}</c:if>"
    ,"placeholder": ""
    ,"solr": "contentdate"
    }
    ,{
    "id": "v-searcher-date-filter-4"
    ,"count": 4
    ,"type": "Date2Filter"
    ,"class": "col-sm-6"
    ,"labelDate1": "From"
    ,"valueDate1": "<c:if test='${not empty param["v-searcher-date-filter-4-from"]}'>${param["v-searcher-date-filter-4-from"]}</c:if>"
    ,"labelDate2": "To"
    ,"valueDate2": "<c:if test='${not empty param["v-searcher-date-filter-4-to"]}'>${param["v-searcher-date-filter-4-to"]}</c:if>"
    ,"solr": "lastmodified"
    }
    ,{
    "id": "v-searcher-category-filter-5"
    ,"count": 5
    ,"type": "CategoryFilter"
    ,"label": "Category"
    ,"class": "col-sm-12"
    ,"value": "<c:if test='${not empty param["v-searcher-category-filter-5"]}'>${param["v-searcher-category-filter-5"]}</c:if>"
    ,"placeholder": "Selecciona categoria"
    ,"solr": "category"
    ,"categoryRoot": "/.categories/location"
    ,"showParent": false
    ,"showEmptyOption": true
    ,"categories": [
    {"value":"location/africa", "text":"Africa"},
    {"value":"location/antarctica", "text":"Antartica"},
    {"value":"location/asia", "text":"Asia"},
    {"value":"location/australia", "text":"Australia"},
    {"value":"location/europe", "text":"Europe"},
    {"value":"location/america", "text":"America"},
    ]
    }
    ]
</c:set>