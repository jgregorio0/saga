<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"
        import="com.saga.upo.util.SgCnt,com.saga.upo.util.SgPublish" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsProject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.jsp.CmsJspActionElement" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.report.A_CmsReportThread" %>
<%@ page import="org.opencms.report.CmsHtmlReport" %>
<%@ page import="org.opencms.util.CmsUUID" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.opencms.db.CmsPublishList" %>
<%@ page import="org.opencms.file.types.I_CmsResourceType" %>
<%@taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%!
    private final Log LOG = CmsLog.getLog(this.getClass());

    public class SgRepairHTMLRedirectThread extends A_CmsReportThread {

        private CmsHtmlReport m_report;
        private CmsObject cmso;
        private String path;
        private boolean isTest;
        private String csvFilePath;


        private static final String SITES = "/sites";
        private static final String SITES_FACULTADES = "/sites/facultades";

        public SgRepairHTMLRedirectThread(CmsObject cms, String path, boolean isTest) {
            super(
                    cms,
                    "ResourceAnalytics");
            try {
                cmso = customCmsObject(cms, null, "/", "/", null);
                this.path = path;
                this.isTest = isTest;
                initHtmlReport(cms.getRequestContext().getLocale());
            } catch (Exception e) {
                LOG.error("Inicializando SgRepairHTMLRedirectThread", e);
            }
        }

        /**
         * Customize a new initialized copy of CmsObject
         * @param baseCms
         * @param uri
         * @param site
         * @param project
         * @return
         * @throws CmsException
         */
        public CmsObject customCmsObject(
                CmsObject baseCms, String locale, String uri, String site, CmsProject project)
                throws CmsException {
            CmsObject cmso = baseCms;
            boolean isLocale = StringUtils.isNotBlank(locale);
            boolean isCustomSite = StringUtils.isNotBlank(site);
            boolean isCustomUri = StringUtils.isNotBlank(uri);
            boolean isCustomProject = project != null;
            if (isLocale || isCustomSite || isCustomUri || isCustomProject) {
                cmso = OpenCms.initCmsObject(baseCms);
                if (isLocale) {
                    cmso.getRequestContext().setLocale(new Locale(locale));
                }
                if (isCustomSite) {
                    cmso.getRequestContext().setSiteRoot(site);
                }
                if (isCustomUri) {
                    cmso.getRequestContext().setUri(uri);
                }
                if (isCustomProject) {
                    cmso.getRequestContext().setCurrentProject(project);
                }
            }
            return cmso;
        }


        public CmsHtmlReport getReport() {

            return m_report;
        }

        protected void initHtmlReport(Locale locale) {

            m_report = new CmsHtmlReport(locale, cmso.getRequestContext().getSiteRoot(), true, false);
        }

        @Override
        public String getReportUpdate() {
            return getReport().getReportUpdate();
        }

        @Override
        public void run() {

            LOG.info("***Inicia reparacion HTMLRedirect***");
//            m_report.print("Inicia proceso", I_CmsReport.FORMAT_HEADLINE);
            try {
                LOG.debug("path: " + path);
                List<CmsResource> htmlRedirects = null;
                if (CmsResource.isFolder(path)) {
                    I_CmsResourceType redirectType = OpenCms.getResourceManager().getResourceType("htmlredirect");
                    htmlRedirects = cmso.readResources(path, CmsResourceFilter.IGNORE_EXPIRATION.addRequireType(redirectType), true);
                } else {
                    htmlRedirects = new ArrayList<CmsResource>();
                    htmlRedirects.add(
                            cmso.readResource(path, CmsResourceFilter.IGNORE_EXPIRATION));
                }

                LOG.debug(htmlRedirects.size() + " recursos");

                SgPublish sgPublish = new SgPublish(cmso);
                for (int iHtmlRedirect = 0; iHtmlRedirect < htmlRedirects.size(); iHtmlRedirect++) {
                    CmsResource htmlRedirect = htmlRedirects.get(iHtmlRedirect);
                    LOG.debug("el recurso: " + htmlRedirect.getRootPath());

                    try {
                        SgCnt cnt = new SgCnt(cmso, htmlRedirect);
                        List<Locale> locales = cnt.getXmlContent().getLocales();
                        LOG.debug("contiene los locales: " + Arrays.toString(locales.toArray()));

                        boolean modified = false;
                        for (int iLocales = 0; iLocales < locales.size(); iLocales++) {
                            Locale locale = locales.get(iLocales);

                            try {
                                if (validateLocale(cnt, locale)) {
                                    if (!isTest){
                                        cnt.getXmlContent().removeLocale(locale);
                                        modified = true;
                                    }
                                    LOG.debug("eliminamos locale " + locale);
                                }
                            } catch (Exception e) {
                                LOG.error("No se ha podido eliminar locale " + locale + " para el recurso " + htmlRedirect.getRootPath());
                            }
                        }

                        if (modified) {
                            cnt.saveXml();
                            LOG.debug("guardamos el contenido modificado");

                            if (SgPublish.isPublished(htmlRedirect)) {
                                sgPublish.add(htmlRedirect.getRootPath());
                                LOG.debug("incluimos en la lista de publicacion");
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("No se ha podido reparar el recurso " + htmlRedirect.getRootPath(), e);
                    }
                }

                if (sgPublish.getPubList().size() > 0) {
                    CmsPublishList publishList = sgPublish.publish(false, false);
                    LOG.debug("publicamos " + publishList.size() + " recursos");
                }
            } catch (Throwable e) {
                LOG.error("running search broken links", e);
//                m_report.println(e);
            }
            LOG.info("***FIN reparacion htmlredirect***");
//            m_report.print("Fin proceso", I_CmsReport.FORMAT_HEADLINE);
        }

        private boolean validateLocale(SgCnt cnt, Locale locale) {
            return cnt.getXmlContent().hasLocale(locale) && !locale.getLanguage().equals("en");
        }
    }
%>
<%
    /******* CONFIGURACION DEL SCRIPT ********/
    String tituloScript = "Reparación de HTMLRedirect";
    String idProceso = "repair-htmlredirect"; //No utilizar ni espacios en blanco ni caracteres como tildes o similares
    String descripcion = "Script que realiza una reparación de los HTMLRedirect de los que debe eliminar los locale distintos a 'en'.";

    pageContext.setAttribute("tituloScript", tituloScript);
    pageContext.setAttribute("idProceso", idProceso);
    pageContext.setAttribute("descripcion", descripcion);
/******* FIN *******/

/******* INICIALIZACION DEL SCRIPT ********/
    CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

/******* FIN *******/

/******* RECIBIMOS LOS PARAMETROS ********/
    String ajax = request.getParameter("ajax");
    String report = request.getParameter("report");
    String run = request.getParameter("run");
    String path = request.getParameter("path");
    boolean isTest = request.getParameter("isTest") != null;
/******* FIN *******/

    if (ajax != null) {
        //Creamos una instancia de la clase
        SgRepairHTMLRedirectThread thread = new SgRepairHTMLRedirectThread(cms.getCmsObject(), path, isTest);
        OpenCms.getThreadStore().addThread(thread);
        //thread.run();
        out.println(thread.getUUID());
    } else if (run != null) {
        SgRepairHTMLRedirectThread reportAux = (SgRepairHTMLRedirectThread) OpenCms.getThreadStore().retrieveThread(new CmsUUID(run));
        boolean isAlive = reportAux.isAlive();
        LOG.debug("run thread " + reportAux.getId());
        reportAux.run();
        out.println("Proceso terminado correctamente");
    } /*else if (report != null) {
        response.setHeader("Content-Disposition", "inline");
        SgRepairHTMLRedirectThread reportAux = (SgRepairHTMLRedirectThread) OpenCms.getThreadStore().retrieveThread(new CmsUUID(report));
        String respuesta = reportAux.getReportUpdate();
        out.println(respuesta);
    }*/ else {
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
    <%--<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>--%>

    <script type="text/javascript"
            src="<cms:link>/system/modules/com.saga.sagasuite.core.script/resources/jquery/1.10.2/jquery-1.10.2.min.js</cms:link>"></script>
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>

    <style>
        .logocabecera {
            margin-top: 30px;
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
                <%--<div class="col-sm-3 logocabecera">
                    <img src="http://www.sagasoluciones.com/pagina-inicio-corporativa/images/logo-saga-white-240x73.png"/>
                </div>--%>
            </div>

        </div>
    </div>
    <div class="well state-proccess">
        <div class="container">
            <div class="row">
                <div class="col-sm-9">
                    <form method="post" id="scriptExecForm">
                        <div class="form-group">
                            <label for="path">Indique la ruta a partir de la cual se reparan los htmlredirect:</label>
                            <input type="text" id="path" name="path" placeholder="/sites/facultades/">

                        </div>
                        <div class="form-group">
                            <input type="checkbox" id="isTest" name="isTest" checked>
                            <label class="form-check-label" for="isTest">
                                Si desea realizar una prueba antes de modificar recursos marque la casilla
                            </label>
                        </div>
                        <small>Active el nivel Debug para htmlredirect_jsp en Vista de Administración >  Herramientas workspace >  Log settings </small>
                        <hr>
                        <input type="hidden" name="ajax" value="true"/>
                        <button type="submit" id="submit" class="btn btn-success btn-lg">Ejecutar Script!!</button>
                    </form>
                </div>
                <div class="col-sm-3">
                    <div id="resultadoProceso"></div>
                </div>
            </div>

        </div>

    </div>
    <div class="row">
        <div class="container">
            <div id="resultadoProceso">
            </div>
        </div>
    </div>
    <div class="hide" id="stateprocess"></div>
</div>

<script>
    var uuidReport;
    var refreshState;
    var runState;
    var intentosFallidos = 0;
    var respuesta;

    $(document).ready(function () {
        console.log("Pagina cargada correctamente");

        $("#scriptExecForm").submit(function (e) {
            e.preventDefault(); // avoid to execute the actual submit of the form.

            console.log("Se ha realizado el submit del formulario de ejecucion");

            //Limpiamos el bloque del estado:
            //$("#estadoProceso").html("<p>Iniciando proceso ...</p>");
//            $("#resultadoProceso").html("<div class='text-center'><img src='http://www.sagasoluciones.com/pagina-inicio-corporativa/images/loading.gif'/><p>Esperando ID del proceso ...</p></div>");
            /*$("#resultadoProceso").html("<div class='text-center'><p>Esperando ID del proceso ...</p></div>");
             var porcentaje = "0";
             $('.progress').removeClass("hide");
             $('.progress-bar').css('width', porcentaje + '%').attr('aria-valuenow', porcentaje);
             $('.progress-bar').html(porcentaje + "%");*/

            //Creamos una llamada periodica para refrescar el estado del proceso
            refreshState = setInterval(refrescarEstado, 1000);
            $.ajaxSetup({cache: false});

            var url = "<cms:link>${cms.requestContext.uri}</cms:link>"; // enviamos a la misma jsp

            console.log("submit form", url, $("#scriptExecForm").serialize());
            $.ajax({
                type: "POST",
                url: url,
                data: $("#scriptExecForm").serialize(), // serializes the form's elements.
                success: function (data) {
                    uuidReport = data.trim();
                    $("#resultadoProceso").html("<div class='text-center'><p>Revise el estado del proceso en el LOG...</p></div>");
//                    runState = setInterval(runningState, 1000);
                    $.ajax({
                        type: 'GET',
                        url: '<cms:link>${cms.requestContext.uri}</cms:link>?run=' + uuidReport,
                        dataType: 'html',
                        success: function (data) {
                            console.log("runningState", uuidReport, data);
                            if (!isNullOrWhiteSpace(data)) {
                                respuesta = data;
                                console.log("Proceso ejecutado correctamente");
//                                clearInterval(runState);
                            }
                        }
                    });
                }
            });

        });

        /*function runningState() {
         $.ajax({
         type: 'GET',
         url: '


        <cms:link>${cms.requestContext.uri}</cms:link>?run=' + uuidReport,
         dataType: 'html',
         success: function (data) {
         console.log("runningState", uuidReport, data);
         if (!isNullOrWhiteSpace(data)) {
         respuesta = data;
         console.log("Proceso ejecutado correctamente");
         clearInterval(runState);
         }
         }
         });
         }*/

        function refrescarEstado() {
            if (!isNullOrWhiteSpace(uuidReport)) {
                $.ajax({
                    type: 'POST',
                    url: '<cms:link>${cms.requestContext.uri}</cms:link>?report=' + uuidReport,
                    dataType: 'html',
                    success: function (data) {
                        if (!isNullOrWhiteSpace(data)) {
//                            $("#stateprocess").append(data);
                            /*var porcentaje = $('#stateprocess span:last').html();
                             $('.progress-bar').css('width', porcentaje + '%').attr('aria-valuenow', porcentaje);
                             $('.progress-bar').html(porcentaje + "%");*/
                            /*if (porcentaje == '100.00') {
                             $("#resultadoProceso").html("<div class='text-center'><p>Generando informe ...</p></div>");
                             }*/
                        }
                        else {
                            console.log("Report vacio, sumamos un intento");
                            intentosFallidos++;
                            if (intentosFallidos > 5) {
//                                $("#resultadoProceso").html("<div class='text-center'><p class='alert alert-success'>" + respuesta + "</p></div>");
                                clearInterval(refreshState);
                                console.log("Se han sobrepasado los 5 intentos, paramos el proceso");
                            }
                        }
                    }
                });
            }
        }

        function isNullOrWhiteSpace(str) {
            return str == null || str.replace(/\s/g, '').length < 1;
        }
    });


</script>

</body>
</html>
<%}%>