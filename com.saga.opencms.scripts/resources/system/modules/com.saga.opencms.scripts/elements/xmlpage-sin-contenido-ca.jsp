<%@ taglib prefix="c" uri="http://www.opencms.org/taglib/cms" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.collectors.I_CmsResourceCollector" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.i18n.CmsEncoder" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.xml.CmsXmlEntityResolver" %>
<%@ page import="org.opencms.xml.content.CmsXmlContent" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
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
			<%!
				public boolean contieneLocale(CmsObject cmso, CmsResource resource, String localeStr) throws UnsupportedEncodingException, CmsException {
					CmsFile file = cmso.readFile(resource);
					String strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
					CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(cmso);
					CmsXmlContent xmlContent = CmsXmlContentFactory
							.unmarshal(cmso, strContent, CmsEncoder.ENCODING_UTF_8, resolver);
					return xmlContent.hasLocale(new Locale(localeStr));
				}
			%>
			<%
				CmsObject cmso = CmsFlexController.getCmsObject(request);
				I_CmsResourceCollector collector =
						OpenCms.getResourceManager().getContentCollector("allInSubTree");
				List<CmsResource> results = collector.getResults(cmso, "allInSubTree", "/|xmlpage");

				out.print("<br/>RESULTADOS");
				int j = 1;
				for (int i = 0; i < results.size(); i++) {
					CmsResource resource = results.get(i);
					boolean contenidoValido = false;
					try {
						contenidoValido = contieneLocale(cmso, resource, "ca");
					} catch (Exception e) {}
					if (!contenidoValido) {
						out.print("<br/>" + j + "- " + resource.getRootPath());
						j++;
					}
				}
			%>
		</div>
	</div>
</div>
</body>
</html>