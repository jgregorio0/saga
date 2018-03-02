<%@ page import="com.saga.opencms.util.SgCntMap" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.i18n.CmsEncoder" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="apollo" tagdir="/WEB-INF/tags/apollo" %>

<fmt:setLocale value="${cms.locale}"/>
<cms:bundle basename="org.opencms.apollo.template.flexible.messages">
    <cms:formatter var="content" val="value">
        <c:choose>
            <c:when test="${cms.isOnlineProject}">
                <c:set var="file" value="${content.file}"/>
                <%
                    CmsFile file = (CmsFile) pageContext.getAttribute("file");
                    String strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
                    String json = SgCntMap.toJson(strContent);
                %>
                <%=json%>
            </c:when>
            <c:otherwise>
                <c:set var="hasScript" value="${fn:contains(fn:toLowerCase(value.Code), 'script')}"/>
                <apollo:init-messages reload="${value.RequireReload.toBoolean or hasScript}">


                    <div<c:if
                            test="${not empty cms.element.settings.cssClass}"> class="${cms.element.settings.cssClass}"</c:if>>

                        <c:if test="${not cms.element.settings.hideTitle}">
                            <div class="headline"><h3 ${rdfa.Title}>${value.Title}</h3></div>
                        </c:if>
                            ${value.Code}

                    </div>

                </apollo:init-messages>
            </c:otherwise>
        </c:choose>
    </cms:formatter>
</cms:bundle>
