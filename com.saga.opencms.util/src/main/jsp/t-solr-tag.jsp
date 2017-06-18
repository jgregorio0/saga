<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="sg" tagdir="/WEB-INF/tags/saga/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
	<c:set var="solrquery">fq=type:("function")&fq=parent-folders:"/"&fq=con_locales:en&rows=10&start=1</c:set>
	<h2>solrquery: ${solrquery}</h2>
	<c:set var="fields">id,path,link,title_en,contentdate[d]</c:set>
	<h2>fields: ${fields}</h2>
	<c:set var="results"><sg:solr solrquery='${solrquery}' fields='${fields}'/></c:set>
	<p>results: ${results}</p>
</div>