<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="${cms.locale}"/>
<cms:bundle basename="com.saga.sagasuite.core.row">
    <cms:formatter var="content" val="value">

        <c:set var="detailOnly" value="false"/>
        <c:if test="${cms.element.setting.detail == 'only'}">
            <c:set var="detailOnly">true</c:set>
        </c:if>

        <c:set var="rowClass" value=""/>
        <c:if test="${cms.element.setting.createrow.value == 'true'}">
            <c:set var="rowClass" value="row"/>
        </c:if>
        <c:if test="${cms.element.setting.marginbottom.value != '0'}">
            <c:set var="rowClass">${rowClass} margin-bottom-${cms.element.setting.marginbottom.value}</c:set>
        </c:if>

        <c:set var="spacer" value=" "/>

        <c:choose>
            <c:when test="${!cms.isOnlineProject}">
                <div class="layout-container <c:if test='${cms.element.setting.createcontainer.value == "true"}'>container</c:if>"
                     <c:if test="${cms.element.setting.contextualId} !=''">id="${cms.element.setting.contextualId}"</c:if>>

                    <div class="${rowClass}<c:if test="${value.AdditionalClass.isSet}">${spacer}${value.AdditionalClass}${spacer }${cms.element.setting.contextualClass}</c:if>">

                        <c:set var="showDetailOnly"
                               value="${(cms.isEditMode) and (detailOnly == 'true') and (not cms.detailRequest)}"/>

                        <c:forEach var="column" items="${content.valueList.Column}" varStatus="loop">

                            <c:set var="detailView" value="false"/>
                            <c:if test="${(loop.count == 1) and (cms.element.setting.detail == 'view')}">
                                <c:set var="detailView">true</c:set>
                            </c:if>

                            <c:set var="bssClass" value=""/>
                            <c:set var="spacer" value=""/>

                            <c:if test="${cms.element.setting.createrow.value == 'true'}">
                                <c:if test="${column.value.XXS.stringValue != '-'}">
                                    <c:set var="bssClass">${column.value.XXS}</c:set>
                                    <c:set var="spacer" value=" "/>
                                </c:if>
                                <c:if test="${column.value.XS.stringValue != '-'}">
                                    <c:set var="bssClass">${column.value.XS}</c:set>
                                    <c:set var="spacer" value=" "/>
                                </c:if>
                                <c:if test="${column.value.SM.stringValue != '-'}">
                                    <c:set var="bssClass">${bssClass}${spacer}${column.value.SM}</c:set>
                                    <c:set var="spacer" value=" "/>
                                </c:if>
                                <c:if test="${column.value.MD.stringValue != '-'}">
                                    <c:set var="bssClass">${bssClass}${spacer}${column.value.MD}</c:set>
                                    <c:set var="spacer" value=" "/>
                                </c:if>
                                <c:if test="${column.value.LG.stringValue != '-'}">
                                    <c:set var="bssClass">${bssClass}${spacer}${column.value.LG}</c:set>
                                    <c:set var="spacer" value=" "/>
                                </c:if>
                            </c:if>
                            <c:if test="${column.value.Modifier.isSet and cms.element.setting.usemodifier.value == 'true'}">
                                <c:set var="bssClass">${column.value.Modifier}</c:set>
                            </c:if>

                            <c:if test="${column.value.Modifier.isSet and cms.element.setting.usemodifier.value == 'false'}">
                                <c:set var="wrapperClass">${column.value.Modifier}</c:set>
                            </c:if>

                            <c:set var="role" value="${column.value.Editors}"/>
                            <c:set var="parent_role" value="${cms.container.param}"/>

                            <c:choose>
                                <c:when test="${(role == 'ROLE.DEVELOPER') or (parent_role == 'ROLE.DEVELOPER')}">
                                    <c:set var="role" value="ROLE.DEVELOPER"/>
                                </c:when>
                                <c:when test="${((role != '') or (role == 'ROLE.EDITOR')) and (parent_role == 'ROLE.EDITOR')}">
                                    <c:set var="role" value="ROLE.EDITOR"/>
                                </c:when>
                            </c:choose>

                            <c:set var="alert" value=""/>
                            <c:if test="${role == 'ROLE.EDITOR'}">
                                <c:set var="alert"><span> </span><span
                                        class="label rounded label-warning">Restricted</span></c:set>
                            </c:if>
                            <c:if test="${role == 'ROLE.DEVELOPER'}">
                                <c:set var="alert"><span> </span><span
                                        class="label rounded label-danger">Restricted</span></c:set>
                            </c:if>
                            <c:if test="${detailView == 'true'}">
                                <c:set var="alert">${alert}<span> </span><span class="label rounded label-success">Detail view container</span></c:set>
                            </c:if>

                            <%-- Comprobamos el device en el que se mostrara cada container de layout--%>

                            <c:set var="device"></c:set>
                            <c:set var="isFirst">true</c:set>
                            <c:if test="${column.value.DesktopVisible == 'true'}">
                                <c:set var="device">desktop</c:set>
                                <c:set var="isFirst">false</c:set>
                            </c:if>
                            <c:if test="${column.value.TabletVisible == 'true'}">
                                <c:if test="${!isFirst}">
                                    <c:set var="device">${device},</c:set>
                                </c:if>
                                <c:set var="device">${device}tablet</c:set>
                                <c:set var="isFirst">false</c:set>
                            </c:if>
                            <c:if test="${column.value.MobileVisible == 'true'}">
                                <c:if test="${!isFirst}">
                                    <c:set var="device">${device},</c:set>
                                </c:if>
                                <c:set var="device">${device}mobile</c:set>
                                <c:set var="isFirst">false</c:set>
                            </c:if>


                            <c:if test="${showDetailOnly}">
                                <div class="${bssClass}">
                                    <div class="${wrapperClass}">
                                        <div class="servive-block servive-block-ade rounded-3x servive-block-light">
                                            <h2 class="heading-md">
                                                Detail container
                                                <span class="label rounded label-danger">Blocked</span>
                                                <span> </span>
                                                <span class="label rounded label-success">Only for detail pages</span>
                                            </h2>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                            <cms:device type="${device}">
                                <c:choose>
                                    <c:when test="${column.value.Count.stringValue == '0'}">
                                        <div class="${bssClass}"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${not empty bssClass}">
                                            <div class="${bssClass}">
                                        </c:if>
                                        <c:if test="${not empty wrapperClass}">
                                            <div class="${wrapperClass}">
                                        </c:if>
                                        <div class="wrapper">
                                            <cms:container
                                                    name="${column.value.Name}"
                                                    type="${column.value.Type}"
                                                    tagClass="layout-container-box"
                                                    maxElements="${column.value.Count}"
                                                    detailview="${detailView}"
                                                    detailonly="${detailOnly}"
                                                    editableby="${role}"
                                                    param="${role}">

                                                <div class="servive-block servive-block-ade">
                                                    <h2 class="heading-md"><fmt:message
                                                            key="bootstrap.row.headline.emptycontainer"/> ${alert}</h2>

                                                    <p>${column.value.EmptyText}</p>
                                                </div>
                                            </cms:container>
                                        </div>
                                        <c:if test="${not empty wrapperClass}">
                                            </div>
                                        </c:if>
                                        <c:if test="${not empty bssClass}">
                                            </div>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </cms:device>
                        </c:forEach>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:set var="showDetailOnly"
                       value="${(cms.isEditMode) and (detailOnly == 'true') and (not cms.detailRequest)}"/>

                <c:forEach var="column" items="${content.valueList.Column}" varStatus="loop">

                    <c:set var="detailView" value="false"/>
                    <c:if test="${(loop.count == 1) and (cms.element.setting.detail == 'view')}">
                        <c:set var="detailView">true</c:set>
                    </c:if>

                    <c:set var="role" value="${column.value.Editors}"/>
                    <c:set var="parent_role" value="${cms.container.param}"/>

                    <c:choose>
                        <c:when test="${(role == 'ROLE.DEVELOPER') or (parent_role == 'ROLE.DEVELOPER')}">
                            <c:set var="role" value="ROLE.DEVELOPER"/>
                        </c:when>
                        <c:when test="${((role != '') or (role == 'ROLE.EDITOR')) and (parent_role == 'ROLE.EDITOR')}">
                            <c:set var="role" value="ROLE.EDITOR"/>
                        </c:when>
                    </c:choose>

                    <cms:container
                            name="${column.value.Name}"
                            type="${column.value.Type}"
                            tagClass="layout-container-box"
                            maxElements="${column.value.Count}"
                            detailview="${detailView}"
                            detailonly="${detailOnly}"
                            editableby="${role}"
                            param="${role}">
                    </cms:container>
                </c:forEach>
            </c:otherwise>
        </c:choose>

    </cms:formatter>
</cms:bundle>