<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="${cms.locale}" />

<cms:bundle basename="com.saga.sagasuite.theme.diputacion.sevilla.workplace">

    <%-- cargamos las properties de formato de pagina: con imagen de fondo y/o en caja --%>
    <c:set var="pagebg">
        <cms:property name="sagasuite.pagebg" file="search"/>
    </c:set>

    <!DOCTYPE html>
    <%--
    El atributo 'googleShareHead' incluye el espacio de nombres de shcema.org para declarar el tipo de pagina.
    Lo usa Google+ para determinar el tipo de publicacion. Este atributos se carga en
    /system/modules/com.saga.sagasuite.core/templates/load-themeconfig.jsp (plantilla por defecto) con el tipo 'Article'
    en caso de vistas de detalle y 'Organization' en cualquier otra pagina
    --%>
    <html lang="${cms.locale}" ${googleShareHead}>
    <head>

        <title><cms:info property="opencms.title" /></title>
        <meta charset="${cms.requestContext.encoding}">
        <meta http-equiv="X-UA-Compatible" content="IE=edge"><%-- Activamos para que Internet explorer muestra la página sin modo de compatibilidad en la versión exacta del navegador --%>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"> <%-- Mantener proporciones partiendo del ancho del dispositivo donde se reproduce --%>
        <meta name="description" content="${descriptionpage }">
        <meta name="keywords" content="${keywordspage }">
        <meta name="robots" content="index, follow">
        <meta name="revisit-after" content="7 days">

            <%-- Incluimos el script de los Metas desde el loadconfig del sagasuite core o mediante property configurable en la pagina --%>
            ${metas}
            <%-- fin de metas --%>
            <%-- Incluimos metas de todas las redes sociales (OpenGraph, Facebook, Twitter, Google+)--%>
            ${socialMetas}
            <%-- fin de metas redes sociales --%>
        <!-- BLOQUE: Stylesheets --------------------------------------------------------------------------->

        <cms:headincludes type="css" closetags="false" />

        <c:if test="${empty skin}">
            <link rel="stylesheet" type="text/css" href="<cms:link>/system/modules/${theme}/skins/skin-default/css/screen.css</cms:link>" id="style-skin"/>
        </c:if>
        <c:if test="${not empty skin}">
            <link rel="stylesheet" type="text/css" href="<cms:link>/system/modules/${theme}/skins/${skin}/css/screen.css</cms:link>" id="style-skin"/>
        </c:if>

            <%-- Cargamos la css custom definida por properties --%>

        <c:if test="${not empty csscustom}">
            <link rel="stylesheet" type="text/css" href="<cms:link>${csscustom}</cms:link>" />
        </c:if>


            <%-- css de impresion --%>

        <c:if test="${empty skin}">
            <link rel="stylesheet" media="print" type="text/css" href="<cms:link>/system/modules/${theme}/skins/skin-default/css/print.css</cms:link>" />
        </c:if>
        <c:if test="${not empty skin }">
            <link rel="stylesheet" media="print" type="text/css" href="<cms:link>/system/modules/${theme}/skins/${skin}/css/print.css</cms:link>" />
        </c:if>

            <%-- Especifico para versiones antiguas de IE --%>
        <!--[if lt IE 9]>
        <c:if test="${empty skin }">
            <link rel="stylesheet" media="print" type="text/css" href="<cms:link>/system/modules/${theme}/skins/skin-default/css/ie.css</cms:link>" />
        </c:if>
        <c:if test="${not empty skin }">
            <link rel="stylesheet" media="print" type="text/css" href="<cms:link>/system/modules/${theme}/skins/${skin}/css/ie.css</cms:link>" />
        </c:if>
        <![endif]-->

        <!-- FIN BLOQUE: Stylesheets -->


        <!-- BLOQUE: JavaScript --------------------------------------------------------------------------------------------------->

            <%-- Incluimos scripts definidos en la configurión de formatters --%>
        <cms:headincludes type="javascript" defaults="%(link.weak:/system/modules/com.saga.sagasuite.core.script/resources/jquery/1.10.2/jquery-1.10.2.min.js:927c45ea-7f61-11e3-9005-f18cf451b707)
	|%(link.weak:/system/modules/com.saga.sagasuite.theme.diputacion.sevilla/resources/js/bootstrap.min.js:ea3121cc-1a8c-11e5-9096-01e4df46f753)
	|%(link.weak:/system/modules/com.saga.sagasuite.core.script/resources/prettyphoto/3.1.5/js/jquery.prettyPhoto.closebtnup.js:b3864a15-5ddf-11e4-841d-f18cf451b707)
	|%(link.weak:/system/modules/com.saga.sagasuite.core.script/resources/holder/2.3.1/holder.min.js:13e5e548-fc55-11e3-a209-f18cf451b707)
	|%(link.weak:/system/modules/com.saga.sagasuite.share/resources/js/sgshare-facebook.js:91e154a6-5ac8-11e4-8e60-f18cf451b707)
	|%(link.weak:/system/modules/com.saga.sagasuite.core.script/resources/jquery.validate/1.13.1/jquery.validate-1.13.1.min.js:97ebf687-c8ca-11e4-b15d-01e4df46f753)
	|%(link.weak:/system/modules/com.saga.sagasuite.login/resources/js/sglogin.min.js)
	|%(link.weak:/system/modules/com.saga.opencms.habla/resources/js/habla-app.min.js)
	|%(link.weak:/system/modules/com.saga.opencms.habla/resources/js/sgfollow.min.js)
	|%(link.weak:/system/modules/com.saga.opencms.habla/resources/js/sgscrollresult.min.js)
	|%(link.weak:/system/modules/com.saga.opencms.habla/resources/js/sgscrollresult-defaults.min.js)
	|%(link.weak:/system/modules/com.saga.sagasuite.core.script/resources/bootstrap/hover-dropdown.min.js:8a879926-8d96-11e3-ae25-f18cf451b707)
	|%(link.weak:/system/modules/com.saga.sagasuite.core.script/resources/sagasuite/sg-menu-responsive.js:55d1575f-8fd7-11e3-a7b5-f18cf451b707)
	|%(link.weak:/system/modules/com.saga.sagasuite.core.script/resources/sagasuite/sg-ajaxbutton.min.js:ee9aeec7-255f-11e5-9150-01e4df46f753)
	|%(link.weak:/system/modules/com.saga.sagasuite.core.script/resources/waypoints/jquery.waypoints.min.js:29f11cde-1fbf-11e5-9091-01e4df46f753)
	|%(link.weak:/system/modules/com.saga.sagasuite.theme.diputacion.sevilla/resources/js/theme.js:ea58f528-1a8c-11e5-9096-01e4df46f753)" />

        <c:if test="${cms.requestContext.currentProject.onlineProject}">
            <script src="<cms:link>/system/modules/com.saga.sagasuite.core.script/resources/tinymce/4.1.7/tinymce.min.js</cms:link>" type="text/javascript"></script>
        </c:if>

            <%-- Cargamos el js custom definida por properties --%>
        <c:if test="${not empty jscustom}">
            <script src="<cms:link>${jscustom}</cms:link>" type="text/javascript" ></script>
        </c:if>

            <%-- Cargamos el js especifico para IE --%>
        <!--[if lt IE 9]>
        <script src="<cms:link>/system/modules/com.saga.sagasuite.theme.diputacion.sevilla/resources/js/diputacion.ie.min.js</cms:link>" type="text/javascript" ></script>
        <![endif]-->

            <%-- Incluimos el script de analytics desde el loadconfig del sagasuite core o mediante property configurable en la pagina --%>

        <c:if test="${not empty analytics}">
            ${analytics}
        </c:if>

            <%-- fin de analytics --%>

        <!-- FIN BLOQUE: JavaScript -->

        <!-- Locale y editable ---------------------------------------------------------------------------------------------->

        <c:if test="${multilocale }">
            <%-- Si el multilocale esta activado, tenemos que indicar la referencia de este mismo contenido en otros idiomas  --%>
            <%-- TODO

            <link rel=”alternate” hreflang=”en” href=”http://www.dominiointernacional.com/en/widgets.html” />
            --%>
        </c:if>

        <!-- Controlamos que la pagina sea editable o no a traves de la property de editable -->
        <c:if test="${empty edit || edit=='true'}">
            <cms:enable-ade/>
        </c:if>

        <!-- fin -->

        <!-- Fav and touch icons ----------------------------------------------------------------------------------------------------->

        <link rel="apple-touch-icon-precomposed" href="<cms:link>/apple-touch-icon-precomposed.png</cms:link>">
        <link rel="shortcut icon" href="<cms:link>/favicon.ico</cms:link>">

        <!-- fin -->


    </head>

    <!-- FIN HEAD -->

    <body class="${pagecssclass }<c:if test="${pagebg == 'pagebgtrue' }"> bg-image-body</c:if><c:if test="${pagebox == 'pageboxtrue' }"> body-boxed</c:if><cms:device type="desktop"> desktop-device</cms:device><cms:device type="tablet"> responsive-device tablet-device</cms:device><cms:device type="mobile"> responsive-device mobile-device</cms:device>">
    <cms:include file="/system/modules/com.saga.sagasuite.share/elements/sgfacebook-init-script.jsp"/>
    <!-- Accesibilidad -->
    <h1 class="sr-only"><cms:info property="opencms.title" /></h1>
    <a href="#content-interior" class="sr-only" accesskey="2"><fmt:message key="label.access.jump.content"/></a>

    <div id="page" class="page-interior <c:if test="${pagebox == 'pageboxtrue' }"> page-container container</c:if>">

        <!-- Header  -->
        <div id="header">
            <div class="wrapper">
                <cms:container name="header-container" type="page" width="1200"  maxElements="2" editableby="ROLE.DEVELOPER"/>
            </div>
        </div>
        <!-- End Main Header -->

        <!-- Main Page Content and Sidebar -->
        <div id="content-interior" class="content content-interior">
            <div class="main-content">
                <div class="wrapper">
                    <div class="detalle">
                        <cms:container name="wrapper-container" type="page" width="1200"  maxElements="10" editableby="ROLE.DEVELOPER"/>
                    </div>
                </div>
            </div>
        </div>

        <div id="footer">
            <cms:container name="footer-container" type="page" width="1200"  maxElements="10" editableby="ROLE.DEVELOPER"/>
        </div>

    </div><!-- /.page -->

    <!-- End Content -->
    <!-- End Main Content -->
        <%--	<a data-toggle="collapse" href="#skin-switcher" aria-expanded="false" class="btn btn-info btn-lg btn-skin-switcher" aria-controls="skin-switcher"><span class="fa fa-reorder"></span></a>
            <div id="skin-switcher" class="collapse">
                <ul class="list-unstyled">
                    <li class="text-center margin-bottom-10">
                        Selecciona un skin
                    </li>
                    <li>
                        <button class="btn btn-lg btn-skin btn-skin-default btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el Skin por defecto (verde)" data-skin-header="skin-switcher" data-skin-type="skin-default">&nbsp;</button>
                    </li>
                    <li>
                        <button class="btn btn-lg btn-skin btn-skin-1 btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el Skin Azul" data-skin-header="skin-switcher" data-skin-type="skin-1">&nbsp;</button>
                    </li>
                    <li>
                        <button class="btn btn-lg btn-skin btn-skin-2 btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el Skin Granate" data-skin-header="skin-switcher" data-skin-type="skin-2">&nbsp;</button>
                    </li>
                    <li>
                        <button class="btn btn-lg btn-skin btn-skin-3 btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el Skin Naranja" data-skin-header="skin-switcher" data-skin-type="skin-3">&nbsp;</button>
                    </li>
                    <li>
                        <button class="btn btn-lg btn-skin btn-skin-4 btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el Skin Morado" data-skin-header="skin-switcher" data-skin-type="skin-4">&nbsp;</button>
                    </li>
                    <li class="text-center margin-bottom-10 margin-top-10">
                        Selecciona un modelo:
                    </li>
                    <li>
                        <button class="btn btn-default btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el formato de p&aacute;gina con imagen de fondo" data-class-body="bg-image-body" data-switcher="page">Imagen de fondo</button>
                    </li>
                    <li>
                        <button class="btn btn-default btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el formato de p&aacute;gina sin imagen de fondo" data-class-body="no-bg-image-body" data-switcher="page">SIN Imagen de fondo</button>
                    </li>
                    <li>
                        <button class="btn btn-default btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el formato de p&aacute;gina encajada en contenedor" data-class-body="body-boxed" data-switcher="page">Encajada</button>
                    </li>
                    <li>
                        <button class="btn btn-default btn-block margin-bottom-5 hastooltip"  data-placement="left" title="Previsualiza el formato de p&aacute;gina sin encajar en contenedor" data-class-body="no-body-boxed" data-switcher="page">NO Encajada</button>
                    </li>
                </ul>
            </div>
          <script type="text/javascript">
            $(function() {
              $('[data-skin-header="skin-switcher"]').click( function () {
                $('#overlay-m').show();
                setTimeout(function(){
                  $('#overlay-m').hide();
                },1400);
                var skin = $(this).data('skin-type');
                $('#style-skin').attr('href','<cms:link>/system/modules/com.saga.sagasuite.theme.diputacion.sevilla/skins/</cms:link>' + skin + '/css/screen.css');
              });
            });
          </script>
          --%>
    </body>

    </html>

</cms:bundle>