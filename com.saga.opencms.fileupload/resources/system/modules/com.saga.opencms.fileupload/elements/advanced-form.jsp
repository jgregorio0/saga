<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<cms:headincludes type="javascript" defaults="%(link.weak:/system/modules/com.saga.opencms.fileupload/resources/js/script.js)"/>

<%--Create Form--%>
<form id="upload" method="post" action="<cms:link>/system/modules/com.saga.opencms.fileupload/elements/upload.jsp</cms:link>" enctype="multipart/form-data">
	<div id="drop">
		Drop Here

		<a>Browse</a>
		<input type="file" name="upl" multiple />
	</div>

	<ul>
		<!-- The file uploads will be shown here -->
	</ul>
</form>

<!-- Our main JS file -->
<%--<script src="%(link.weak:/system/modules/com.saga.opencms.fileupload/resources/js/script.js)"></script>--%>