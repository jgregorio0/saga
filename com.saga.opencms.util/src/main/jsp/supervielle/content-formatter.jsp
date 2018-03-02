<%@ page import="com.saga.opencms.util.SgCntMap" %>
<%@ page import="org.opencms.i18n.CmsEncoder" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<cms:formatter var="content">
    <c:choose>
        <c:when test="${!cms.isOnlineProject}">
            <div class="margin-bottom-30">
                <div class="headline">
                    <h3 ${content.rdfa.Title}>${content.value.Title}</h3>
                </div>
                <div class="row">
                    <c:if test="${content.value.Image.exists}">
                        <div class="col-md-4 col-sm-2 hidden-xs">
                            <div class="thumbnail-kenburn">
                                <div class="overflow-hidden">
                                    <cms:img src="${content.value.Image}" scaleColor="transparent"
                                             width="400" scaleType="0" noDim="true" cssclass="img-responsive"/>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <div class="${content.value.Image.exists ? 'col-md-8 col-sm-10 col-xs-12' : 'col-xs-12' }">
                        <div ${content.rdfa.Text}>${content.value.Text}</div>
                        <c:if test="${content.value.Link.exists}">
                            <p>
                                <a class="btn-u btn-u-small"
                                   href="<cms:link>${content.value.Link}</cms:link>">${paragraph.value.Link}</a>
                            </p>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:set var="file" value="${content.file}"/>
            <%
                CmsFile file = (CmsFile) pageContext.getAttribute("file");
                String strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
                String json = SgCntMap.toJson(strContent);
            %>
            <%=json%>
        </c:otherwise>
    </c:choose>

</cms:formatter>