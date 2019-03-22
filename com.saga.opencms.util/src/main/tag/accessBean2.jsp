<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="udl" tagdir="/WEB-INF/tags/udl" %>

<udl:test sitePath="/demo/.content/udlcf-profesor/udlcf-profesor-00001.xml">
    <a href="${content.filename}" class="course__teacher__image avatar">
        <img src="${value.Image}" alt="${value.Title}">
    </a>
    <h5 class="course__teacher__name"><a href="${content.filename}">${value.Title}</a></h5>
    <p class="course__teacher__position">${value.Position}</p>
</udl:test>