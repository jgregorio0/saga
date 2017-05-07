<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%@ page import="com.saga.sagasuite.scripts.SgReportManager" %>
<%@ page import="groovy.lang.GroovyClassLoader" %>
<%@ page import="groovy.lang.GroovyObject" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.jsp.CmsJspActionElement" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.io.File" %>

<%@taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
    final Log LOG = CmsLog.getLog(this.getClass());

    /******* CONFIGURACION DEL SCRIPT ********/
    String tituloScript = "Mapea recursos";
    String idProceso = "mapping-resources"; //No utilizar ni espacios en blanco ni caracteres como tildes o similares
    String descripcion = "Script que mapea un JSON a Recurso.";

    pageContext.setAttribute("tituloScript", tituloScript);
    pageContext.setAttribute("idProceso", idProceso);
    pageContext.setAttribute("descripcion", descripcion);

    /******* FIN *******/

    /******* INICIALIZACION DEL SCRIPT ********/
    CmsJspActionElement cms= new CmsJspActionElement(pageContext,request,response);
    CmsObject cmso = cms.getCmsObject();
    SgReportManager reportManager = SgReportManager.getInstance(cmso);
    String user = cms.getRequestContext().getCurrentUser().getFullName();
    /******* FIN *******/

    /******* RECIBIMOS LOS PARAMETROS ********/
    String ajax = request.getParameter("ajax");

    /******* FIN *******/
    long init = System.currentTimeMillis();
    if(ajax!=null){

        //Limpiamos el proceso por si hubiera informacion no borrada
        reportManager.finishReport(idProceso);

        /*******		  INICIO 		*********/
        /******* A partir de aqui, implementa el codigo del script ********/
        Double porcentaje = new Double("0");
        reportManager.addMessage(idProceso, user, "Iniciado proceso de migracion: ", new Double("0"));

        // Obtenemos los parametros
        String jsonFile = request.getParameter("jsonFile");
        String onlyCheck = request.getParameter("onlyCheck");

        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        //Obtenemos la ruta del webinf:
        String webInfPath = OpenCms.getSystemInfo().getWebInfRfsPath();
        String scriptName = "classes/com/saga/sagasuite/scriptgroovy/migration/MappingResources.groovy";

        //Obtiene la clase groovy
        Class groovyClass = loader.parseClass(new File(webInfPath+scriptName));
        reportManager.addMessage(idProceso, user, "Cargado script groovy: "+webInfPath+scriptName, new Double("0"));

        //Llama al metodo init del script groovy indicado
        try{
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
            groovyObject.invokeMethod("init", new Object[] { cmso, idProceso, jsonFile, onlyCheck});
%>
<p class="alert alert-success">El script se ha ejecutado correctamente</p>
<%
}catch(Exception ex){
    ex.printStackTrace();
    LOG.error("ERROR Ejecutando " + scriptName, ex);
%>
<p class="alert alert-danger">Se ha producido un error: <br/> <%=ex %></p>
<%
    }
    /******* Final codigo script propio ********/
    long end = System.currentTimeMillis();
    long duracion = end - init;
    out.println("<h3 class='alert alert-info'>El proceso ha durado: "+duracion+" mseg</h3>");

    //Paramos el proceso 1 segundo y medio para dar tiempo al refresco del estado actual
    Thread.sleep(3000);

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
                <div class="col-sm-4">
                    <form method="post" id="scriptExecForm">
                        <input type="hidden" name="ajax" value="true"/>

                        <div>
                            <label for="jsonFile">Fichero Json con la configuracion:</label>
                            <input type="text" class="form-control" id="jsonFile" name="jsonFile" value="<c:out value='${param.jsonFile }'/>" placeholder="/.content/mapping.json">
                        </div>
                        <div class="checkbox">
                            <label>
                                <input type="checkbox" id="onlyCheck" > Ejecutar sin modificar recursos
                            </label>
                        </div>
                        <button type="submit" id="submit" class="btn btn-success btn-lg">Ejecutar Script!!</button>
                        <%--<div class="checkbox">
                            <label>
                                <input type="checkbox" id="autoscroll" > Scroll automático
                            </label>
                        </div>--%>
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