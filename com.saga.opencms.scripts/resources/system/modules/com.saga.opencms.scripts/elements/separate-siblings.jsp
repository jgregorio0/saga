<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.util.List" %>
<%@ page import="static java.lang.Thread.sleep" %>
<%@page buffer="none" session="false"%>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Cargamos las variables que luego vamos a ir necesitando a lo largo --%>

<!DOCTYPE html>
<html lang="es">
<head>
	<title>Script de creación de carpetas</title>
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

			<h1>Ejecuci&oacute;n script Groovy</h1>

			<form method="post">

				<div class="form-group">
					<label for="resource">Indique el recurso a separar:</label>
					<input type="text" class="form-control" id="resource" name="resource" value="<c:out value="${param.resource }"/>" placeholder="/organs/vicerectors/">
				</div>

				<input type="hidden" value="true" name="exec"/>
				<input class="btn btn-success" type="submit" value="Ejecutar!"/>
			</form>


			<c:if test="${param.exec != null and param.resource!=null}">
				<c:set var="cmsObject" value="${cms.vfs.cmsObject }" scope="request"/>
				<%!
					public void separateSiblings(CmsObject cmso, String oldpath) throws Exception {
						List<CmsResource> siblings = null;
						try {
							siblings = cmso.readSiblings(oldpath, CmsResourceFilter.ALL);
						} catch (Exception e) {}

						if (siblings != null && siblings.size() > 0){
							for (CmsResource sibling : siblings){
								String sibPath = cmso.getSitePath(sibling);
								String sibPathNew = sibPath + "-new";
								cmso.copyResource(sibPath, sibPathNew,
										CmsResource.CmsResourceCopyMode.valueOf(1));
								cmso.deleteResource(sibPath,
										CmsResource.CmsResourceDeleteMode.valueOf(1));
								if (cmso.existsResource(sibPath)){
									OpenCms.getPublishManager().publishResource(cmso, sibPath);
									sleep(3000L);
								}
								if (cmso.existsResource(sibPath)){
									throw new Exception("No se ha podido romper" +
											" la relacion de hermano para el recurso $sibPath");
								}
								cmso.moveResource(sibPathNew, sibPath);
							}
						}
					}
				%>
				<%
					CmsObject cmso = (CmsObject)request.getAttribute("cmsObject");
					String path = request.getParameter("resource");
					separateSiblings(cmso, path);
				%>
			</c:if>

		</div>
	</div>
</div>
</body>
</html>
