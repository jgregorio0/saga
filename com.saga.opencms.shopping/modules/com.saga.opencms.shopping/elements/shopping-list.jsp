<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<div class="shopping-results"
    data-controller="<cms:link>/system/modules/com.jgregorio.opencms.fileupload/elements/shopping-list-controller.jsp</cms:link>"
    data-template-header="<cms:link>/system/modules/com.jgregorio.opencms.fileupload/elements/shopping-list-header-ajax.jsp</cms:link>"
    data-template-header-id="shopping-header"
    data-template-item="<cms:link>/system/modules/com.jgregorio.opencms.fileupload/elements/shopping-list-item-ajax.jsp</cms:link>"
    data-template-item-id="shopping-items"
    data-template-pagination="<cms:link>/system/modules/com.jgregorio.opencms.fileupload/elements/shopping-list-pagination-ajax.jsp</cms:link>"
    data-template-pagination-id="shopping-pagination">
    <h1>Shopping list!!</h1>
    <div id="shopping-header"></div>
    <div>
        <ul id="shopping-items"></ul>
    </div>
    <div id="shopping-pagination"></div>
</div>