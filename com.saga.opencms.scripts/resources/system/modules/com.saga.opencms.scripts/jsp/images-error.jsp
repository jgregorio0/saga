<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" import="com.saga.sagasuite.scripts.SgReportManager,org.opencms.file.CmsFile,org.opencms.file.CmsObject, org.opencms.file.CmsResource, org.opencms.file.collectors.I_CmsResourceCollector"%>
<%@ page import="org.opencms.jsp.CmsJspActionElement" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="javax.imageio.ImageIO" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%!
	public boolean validateImage(CmsObject cmso, String r) throws IOException, CmsException {
		CmsFile imageFile = cmso.readFile(r);
		byte[] bytes = imageFile.getContents(); // Your image bytes
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		if(ImageIO.read(bis)==null)
			return false;
		else
			return true;
	}

%>

<%
	/******* CONFIGURACION DEL SCRIPT ********/
	String tituloScript = "Valida imagenes erroneas";
	String idProceso = "images-error"; //No utilizar ni espacios en blanco ni caracteres como tildes o similares
	String descripcion = "Script que valida imagenes erroneas.";

	pageContext.setAttribute("tituloScript",tituloScript);
	pageContext.setAttribute("idProceso",idProceso);
	pageContext.setAttribute("descripcion",descripcion);
/******* FIN *******/

/******* INICIALIZACION DEL SCRIPT ********/
	CmsJspActionElement cms= new CmsJspActionElement(pageContext,request,response);
	CmsObject cmso = cms.getCmsObject();
	SgReportManager reportManager = SgReportManager.getInstance(cmso);
	String user = cms.getRequestContext().getCurrentUser().getFullName();
	String currentUri = cms.getRequestContext().getUri();
/******* FIN *******/

/******* RECIBIMOS LOS PARAMETROS ********/
	String ajax = request.getParameter("ajax");
/******* FIN *******/

	if(ajax!=null){
		long init = System.currentTimeMillis();

		//Limpiamos el proceso por si hubiera informacion no borrada
		reportManager.finishReport(idProceso);

		/******* A partir de aqui, implementa el codigo del script ********/

		// Obtenemos los parametros
		String rootPath = request.getParameter("rootPath");
		if (rootPath == null) {
			rootPath = "/";
		}

//		CmsResource image = cmso.readResource("/ca/.galleries/images/organs/vicerectors/vpie/idea/fotos/01_Acte_presentacio_concurs.jpg");
//		out.println(image);
		String correcta = "/.galleries/imagenes-carrusel-home/recercaaliments03.jpg";
		String incorrecta = "/ca/.galleries/images/organs/vicerectors/vpie/idea/fotos/01_Acte_presentacio_concurs.jpg";

		I_CmsResourceCollector collector =
				OpenCms.getResourceManager().getContentCollector("allInSubTree");
		List<CmsResource> results = collector.getResults(cmso, "allInSubTree", rootPath+"|image");

		double porcentajeAnalizado = 0d;
		int contador = 0;
		int i = 0;
		int totalResultados = results.size();
		DecimalFormat df = new DecimalFormat("0.00");
		out.println("<h2>Listado de imagenes con errores:</h2>");
		for (CmsResource result : results) {
			contador++;
			porcentajeAnalizado = (new Double(contador)/new Double(totalResultados))*100d;
			String path = cmso.getSitePath(result);
			reportManager.addMessage(idProceso, user, "Leyendo " + contador + " de " + totalResultados + " -> " + path, porcentajeAnalizado);
			try {
				if (!validateImage(cmso, path)) {
					out.println(path + " - " + i + "</br>");
					String msg = "<span style='color:olive'><strong>WARN: la imagen " + path + " no es valida</strong></span>";
					reportManager.addMessage(idProceso, user, msg, porcentajeAnalizado);
				} else {
					String msg = "<span style='color:green'><strong>correcto " + path + "</strong></span>";
					reportManager.addMessage(idProceso, user, msg, porcentajeAnalizado);
				}
			} catch (Exception e) {
				out.println(path + " - " + i + "</br>");
				String msg = "<span style='color:red'><strong>ERROR: " + e.getMessage() + " - " + e.getCause() + "</strong></span>";
				reportManager.addMessage(idProceso, user, msg, porcentajeAnalizado);
			}

		}


		/******* Final codigo script propio ********/
		long end = System.currentTimeMillis();
		long duracion = end - init;
		out.println("<h3 class='alert alert-info'>El proceso ha durado: "+duracion+" mseg</h3>");

		//Paramos el proceso 1 segundo y medio para dar tiempo al refresco del estado actual
		Thread.sleep(1500);

		//Finalizamos el proceso
		reportManager.finishReport(idProceso);

	}else{
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>${tituloScript}</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="SAGA SOLUCIONES - OpenCms Partners">

	<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
	<!--[if lt IE 9]>
	<script src="js/html5shiv.js"></script>
	<![endif]-->

	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">


	<!-- Latest compiled and minified JavaScript -->
	<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
	<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>

	<style>
		.logocabecera{
			margin-top: 30px;
		}

		.fixed-block{
			position: fixed;
			width: 100%;
			top: 0;
			left: 0;
			right: 0;
		}

	</style>


</head>

<body>

<div id="content" tabindex="-1">
	<div class="container-fluid bg-primary">
		<div class="container">
			<div class="row">
				<div class="col-sm-9">
					<h1>${tituloScript}</h1>
					<p>${descripcion}</p>
					<p>Proceso = <strong>${idProceso}</strong></p>
					<br/>
				</div>
				<div class="col-sm-3 logocabecera">
					<img src="http://www.sagasoluciones.com/pagina-inicio-corporativa/images/logo-saga-white-240x73.png"/>
				</div>
			</div>

		</div>
	</div>
	<div class="well state-proccess">
		<div class="container">
			<div class="row">
				<div class="col-sm-3">
					<form method="post" id="scriptExecForm">
						<input type="hidden" name="ajax" value="true"/>
						<div>
							<label for="rootPath">Ruta del nuevo subsitemap:</label>
							<input type="text" id="rootPath" name="rootPath" value="<c:out value='${param.rootPath}'/>" placeholder="/ca/perfils/"/>
						</div>
						<button type="submit" id="submit" class="btn btn-success btn-lg">Ejecutar Script!!</button>
						<div class="checkbox">
							<label>
								<input type="checkbox" id="autoscroll" > Scroll automático
							</label>
						</div>
					</form>
				</div>
				<div class="col-sm-9">
					<div class="progress hide">
						<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
							0%
						</div>
					</div>
				</div>
			</div>

		</div>

	</div>

	<div class="row">
		<div class="container">
			<h3>Proceso de ejecuci&oacute;n:</h3>

			<div id="estadoProceso">
				<p>
					Ejecuta el script para ver el estado en esta zona.
				</p>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="container">
			<h3>Resultado del proceso:</h3>
			<div id="resultadoProceso">
				<p>
					Aqu&iacute; veremos el resultado de la ejecucion final del script.
				</p>
			</div>
		</div>
	</div>
</div>

<script>
	var line = 1;

	$( document ).ready(function() {
		console.log( "Pagina cargada correctamente" );


		$("#scriptExecForm").submit(function(e) {
			console.log( "Se ha realizado el submit del formulario de ejecucion" );
			var url = "<cms:link>${cms.requestContext.uri}</cms:link>"; // the script where you handle the form input.

			//Limpiamos el bloque del estado:		
			$("#estadoProceso").html("<p>Iniciando proceso ...</p>");
			$("#resultadoProceso").html("<p>Esperando finalización del proceso ...</p>");
			var porcentaje = "0";
			$('.progress').removeClass("hide");
			$('.progress-bar').css('width', porcentaje+'%').attr('aria-valuenow', porcentaje);
			$('.progress-bar').html(porcentaje+"%");

			//Creamos una llamada periodica para refrescar el estado del proceso
			var refreshState = setInterval(refrescarEstado, 1000);
			$.ajaxSetup({ cache: false });

			$.ajax({
				type: "POST",
				url: url,
				data: $("#scriptExecForm").serialize(), // serializes the form's elements.
				success: function(data)
				{
					$("#resultadoProceso").html(data);
					refrescarEstado();
					clearInterval(refreshState);
					//Forzamos el estado a 100%
					var porcentaje = "100";
					$('.progress-bar').css('width', porcentaje+'%').attr('aria-valuenow', porcentaje);
					$('.progress-bar').html(porcentaje+"%");
					//Ponemos la linea a 1 de nuevo
					line = 1;
				}
			});

			e.preventDefault(); // avoid to execute the actual submit of the form.
		});

		$(window).scroll(function() {
			if ($(window).scrollTop()>170){
				$(".state-proccess").addClass("fixed-block");
			}
			else {
				$(".state-proccess").removeClass("fixed-block");
			}
		});

	});

	function refrescarEstado() {
		$.ajax({
			type: 'POST',
			url: '<cms:link>/system/modules/com.saga.sagasuite.scriptjsp/ajaxRefresh.jsp?idProcess=${idProceso}&line=</cms:link>'+line,
			dataType: 'json',
			success: function(data)
			{
				console.log("Tratando estado del proceso");
				$.each(data, function(i,l) {
					console.log("recorriendo la linea -> "+l);
					$("#estadoProceso").append("<p id='line-"+l.idLine+"'>"+l.idLine+" ("+l.date+")->"+l.message+"   ---->   "+l.percentage+"%</p>");

					var porcentaje = l.percentage;
					$('.progress-bar').css('width', porcentaje+'%').attr('aria-valuenow', porcentaje);
					$('.progress-bar').html(porcentaje+"%");
					line = line+1;

					if( $('#autoscroll').prop('checked') ) {
						$('html, body').animate({scrollTop: $('#resultadoProceso').offset().top - 200}, 10);
					}
				});

			}
		});
	}

</script>

</body>
</html>
<%}%>