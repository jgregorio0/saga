<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsProperty" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.jsp.CmsJspActionElement" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>

<%@taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
  final Log LOG = CmsLog.getLog(this.getClass());

  /******* CONFIGURACION DEL SCRIPT ********/
  String tituloScript = "Leer propiedad de recursos";
  String idProceso = "read-property-resources"; //No utilizar ni espacios en blanco ni caracteres como tildes o similares
  String descripcion = "Script que le propiedad el valor de una propiedad de varios recursos.";

  pageContext.setAttribute("tituloScript", tituloScript);
  pageContext.setAttribute("idProceso", idProceso);
  pageContext.setAttribute("descripcion", descripcion);

  /******* FIN *******/

  /******* INICIALIZACION DEL SCRIPT ********/
  CmsJspActionElement cms= new CmsJspActionElement(pageContext,request,response);
  CmsObject cmso = cms.getCmsObject();

  ArrayList<String> errors = new ArrayList<String>();
  HashMap<String, String> results = new HashMap<String, String>();

  /******* RECIBIMOS LOS PARAMETROS ********/
  String ajax = request.getParameter("ajax");

  /******* FIN *******/
  long init = System.currentTimeMillis();
  if(ajax!=null){
//        LOG.error("Ejecutamos script ajax=true");
    // Obtenemos los parametros
    String rootPath = request.getParameter("rootPath");
    String propName = request.getParameter("propName");
//        LOG.error("parametros rootPath: " + rootPath + " propName: " + propName);
    try {

      //List<CmsResource> resources = cmso.readResources(rootPath, CmsResourceFilter.ALL);
      List<CmsResource> resources = cmso.readResourcesWithProperty(rootPath, propName);
      LOG.error("resources size: " + resources.size());
      for (int i = 0; i < resources.size(); i++) {
        CmsResource resource = resources.get(i);
//                LOG.error("resource: " + resource);
        try {
          CmsProperty cmsProperty = cmso.readPropertyObject(resource, propName, false);
          if (!cmsProperty.isNullProperty() && !StringUtils.isBlank(cmsProperty.getValue())) {
            results.put(resource.getRootPath(), cmsProperty.getValue());
//                        LOG.error("Añadimos propiedad: " + resource.getRootPath() + " >> " + propValue);
          }


        } catch (Exception e) {
          LOG.error("error recurso " + resource, e);
          errors.add("El recurso " + resource + " lanza error " + e.getMessage());
        }
      }
%>
<p class="alert alert-success">El script se ha ejecutado correctamente</p>

<div>
  <h2>ERRORES (<%=errors.size()%>)</h2>
  <c:forEach items="<%=errors%>" var="error">
    <div class="alert alert-danger">${error}</div>
  </c:forEach>
  <h2>RESULTADOS (<%=results.size()%>)</h2>
  <c:forEach items="<%=results.entrySet()%>" var="result" varStatus="status">
    <p>${result.key}; ${result.value}</p>
  </c:forEach>
</div>

<%
}catch(Exception ex){
  ex.printStackTrace();
  LOG.error("ERROR Ejecutando " + idProceso, ex);
%>
<p class="alert alert-danger">Se ha producido un error: <br/> <%=ex %></p>
<%
  }
  /******* Final codigo script propio ********/
  long end = System.currentTimeMillis();
  long duracion = end - init;
  out.println("<h3 class='alert alert-info'>El proceso ha durado: "+duracion+" mseg</h3>");
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
        <div class="col-sm-4">
          <form method="post" id="scriptExecForm">
            <input type="hidden" name="ajax" value="true"/>

            <div>
              <label for="rootPath">Ruta de los ficheros:</label>
              <input type="text" class="form-control" id="rootPath" name="rootPath" value="<c:out value='${param.jsonFile }'/>" placeholder="/sites/congresos/">
            </div>
            <div>
              <label for="propName">Nombre de la propiedad:</label>
              <input type="text" class="form-control" id="propName" name="propName" value="<c:out value='${param.propName }'/>" placeholder="template-elements">
            </div>
            <button type="submit" id="submit" class="btn btn-success btn-lg">Ejecutar Script!!</button>
          </form>
        </div>
        <div class="col-sm-8">
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

//        $(window).scroll(function() {
//            if ($(window).scrollTop()>170){
//                $(".state-proccess").addClass("fixed-block");
//            }
//            else {
//                $(".state-proccess").removeClass("fixed-block");
//            }
//        });

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
//                    if( $('#autoscroll').prop('checked') ) {
//                        $('html, body').animate({scrollTop: $('#resultadoProceso').offset().top - 200}, 10);
//                    }
        });
      }
    });
  }

</script>





</body>
</html>
<%}%>