<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<c:choose>
    <c:when test="${!cms.isOnlineProject}">
        <div id="toolbar-placeholder" style="height: 52px;background: #eee"></div>
        <cms:enable-ade/>
        <cms:container name="header" type="element" width="1200" maxElements="1" editableby="ROLE.DEVELOPER"/>
        <cms:container name="container" type="element" width="1200" maxElements="1" editableby="ROLE.DEVELOPER"/>
        <cms:container name="aside" type="element" width="1200" maxElements="1" editableby="ROLE.DEVELOPER"/>
    </c:when>
    <c:otherwise>
        {
            "route": "${cms.template.uri}",
            "head": {
                "title": "<cms:info property="opencms.title"/>",
                "image": "<cms:link>/favicon.ico</cms:link>"
                "description": "<cms:info property="opencms.description"/>"
            },
            "components":[
                <cms:container name="header" type="element" width="1200" maxElements="1" editableby="ROLE.DEVELOPER"/>,
                <cms:container name="container" type="element" width="1200" maxElements="1" editableby="ROLE.DEVELOPER"/>,
                <cms:container name="aside" type="element" width="1200" maxElements="1" editableby="ROLE.DEVELOPER"/>
            ]
        }
    </c:otherwise>
</c:choose>