<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<c:choose>
    <c:when test="${!cms.isOnlineProject}">
        <div id="toolbar-placeholder" style="height: 52px;background: #eee"></div>
        <cms:enable-ade/>
        <cms:container name="container" type="row" width="1200" maxElements="5" editableby="ROLE.DEVELOPER"/>
    </c:when>
    <c:otherwise>
        {
        "route": "/noticia/",
        "head": {
        "title": "<cms:info property="opencms.title"/>",
        "image": "<cms:link>/favicon.ico</cms:link>"
        "description": "<cms:info property="opencms.description"/>"
        },
        "components":[
        <cms:container name="container" type="row" width="1200" maxElements="5" editableby="ROLE.DEVELOPER"/>
        ]
        }
    </c:otherwise>
</c:choose>