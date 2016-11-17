<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.types.I_CmsResourceType" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.file.CmsResource" %>
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
					<label for="folders">Indique el nombre de las carpetas separadas por punto y coma (;):</label>
					<input type="text" class="form-control" id="folders" name="folders" value="<c:out value="${param.folders }"/>" placeholder="/organs/vicerectors/;/serveis/">
				</div>

				<div class="form-group">
					<label>Indique el tipo de carpeta:</label></br>
					<input type="radio" name="type" value="folder" checked>Carpeta</br>
					<input type="radio" name="type" value="imagegallery" checked>Galeria de imagenes</br>
					<input type="radio" name="type" value="downloadgallery" checked>Galeria de descargas</br>
				</div>

				<input type="hidden" value="true" name="exec"/>
				<input class="btn btn-success" type="submit" value="Ejecutar!"/>
			</form>


			<c:if test="${param.exec != null and param.folders!=null and param.type!=null}">
				<c:set var="cmsObject" value="${cms.vfs.cmsObject }" scope="request"/>
				<%
					CmsObject cmso = (CmsObject)request.getAttribute("cmsObject");

					String foldersStr = request.getParameter("folders");
					String typeStr = request.getParameter("type");
					I_CmsResourceType type = OpenCms.getResourceManager().getResourceType(typeStr);
					String[] folders = foldersStr.split(";");
					if (folders != null && folders.length > 0) {
						for (int i = 0; i < folders.length; i++) {

							// Por cada entrada obtenemos su ultima carpeta
							String folder = folders[i];
							folder = CmsResource.getFolderPath(folder);
							if (!cmso.existsResource(folder)){

								// Comprobamos que existe las secciones del path
								String[] paths = folder.split("/");
								if (paths != null && paths.length > 0) {
									String folderPath = "";
									for (int j = 0; j < paths.length; j++) {
										String pathPart = paths[j];
										if (pathPart != null && pathPart.length() > 0) {
											folderPath = folderPath + "/" + pathPart;

											// Si no existe esta carpeta la creamos
											if (!cmso.existsResource(folderPath)){
												cmso.createResource(folderPath, type);
												out.print("Creamos -> " + folderPath + "<br/>");
											}
										}
									}
								}
							}
						}
					}
				%>
			</c:if>

		</div>
	</div>
</div>
</body>
</html>
