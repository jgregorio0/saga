<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.i18n.CmsEncoder" %>
<%@ page import="org.opencms.xml.CmsXmlEntityResolver" %>
<%@ page import="org.opencms.xml.content.CmsXmlContent" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ page import="java.util.Locale" %>
<%@ taglib prefix="c" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="es">
<head>
	<title>Ejecucion Script Groovy</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="SAGA SOLUCIONES - OpenCms Partners">

	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">

	<!-- Latest compiled and minified JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>

</head>

<body>
<div class="container-fluid">
	<div class="row clearfix">
		<div class="col-md-12 column">
			<%
				CmsObject cmso = CmsFlexController.getCmsObject(request);
				CmsResource resource = cmso.readResource("/ca/serveis/oficina/.content/udlnoticia/udlnoticia-00001.xml");
				cmso.lockResource(resource);
				CmsFile file = cmso.readFile(resource);
				String strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
				CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(cmso);
				CmsXmlContent xmlContent = CmsXmlContentFactory
						.unmarshal(cmso, strContent, CmsEncoder.ENCODING_UTF_8, resolver);
				xmlContent.getValue("DataNoticia", new Locale("ca")).setStringValue(cmso, new Long(0L).toString());
				file.setContents(xmlContent.marshal());
				cmso.writeFile(file);
				cmso.unlockResource(resource);
			%>
		</div>
	</div>
</div>
</body>
</html>