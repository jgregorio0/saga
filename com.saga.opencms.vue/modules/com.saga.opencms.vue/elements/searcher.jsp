<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cms:secureparams/>
<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.opencms.vue.searcher.messages">
    <div>
		<h1>FILTERS</h1>
		<v-filters id="v-searcher-main"
				   pLocale="en"
				   pFilters='[{ "id": "buscador-field-1" ,"count": 1 ,"type": "TextFilter" ,"label": "Texto Principal","placeholder": "Enter a word or phrase to search" ,"solr": "content" ,"class": "col-sm-12" ,"isExactSearch": false ,"isContainsSearch": false ,"isPrincipal": true ,"value": "hola" ,"showExactSearch": true ,"labelExactResult": "Búsqueda exacta" ,"buttonText": "Buscar" ,"buttonClass": "btn-default" }, { "id": "buscador-field-2" ,"count": 2 ,"type": "TextFilter" ,"label": "Texto","placeholder": "Enter a word or phrase to search" ,"solr": "content" ,"isPrincipal": false ,"class": "col-sm-12" ,"value": "" }, { "id": "buscador-field-3" ,"count": 3 ,"type": "Date1Filter" ,"label": "Fecha simple" ,"solr": "contentdate" ,"class": "col-sm-12" ,"value": "" }, { "id": "buscador-field-4" ,"count": 4 ,"type": "Date2Filter" ,"solr": "contentdate" ,"class": "col-sm-12" ,"labelDate1": "Desde:" ,"valueDate1": "" ,"labelDate2": "Hasta:" ,"valueDate2": "" }, { "id": "buscador-field-5" ,"count": 5 ,"type": "CategoryFilter" ,"label": "Categoria" ,"solr": "" ,"class": "col-sm-12" ,"categoryRoot": "/.categories/topic/" ,"treeFolder": "true" ,"showParent": "title" ,"showEmptyOption": "false" ,"labelCategoryFilterAll": "Todas" ,"fieldType": "select" ,"categories": [{"title":"Topic","class":"nivel-0","value":"topic/"}, {"title":"   Buildings","class":"nivel-1","value":"topic/buildings/"}, {"title":"   Machines","class":"nivel-1","value":"topic/machines/"}, {"title":"   Nature","class":"nivel-1","value":"topic/nature/"}, {"title":"   People","class":"nivel-1","value":"topic/people/"}, {"title":"   Transportation","class":"nivel-1","value":"topic/transportation/"}] ,"value": "" }]'>
		</v-filters>

		<h1>RESULTS</h1>
		<v-results id="v-searcher-main"
				   pLocale="en"
				   pRows="10"
				   pStart="0"
				   pController="<cms:link>/system/modules/com.saga.opencms.vue/elements/v-controller.jsp</cms:link>"
				   pQuery='&fq=type:("image")&fq=con_locales:en&fq=parent-folders:("/sites/default/")'>
		</v-results>
		<script src="<cms:link>/system/modules/com.saga.opencms.vue/resources/js/v-search/build.js</cms:link>"></script>
	</div>
	
</cms:bundle>