<%@ page buffer="none" session="true"%>
<%@ taglib prefix="sg" tagdir="/WEB-INF/tags/core/templates" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--OBTENEMOS XML--%>
<%--<c:set var="uuid"><sg:congresoUUID idCongreso="38"/></c:set>--%>
<%--<sg:resourceXmlCnt id="${uuid}"/>--%>
<sg:resourceXmlCnt id="/system/modules/com.saga.sagasuite.core/.config"/>

<h1>LEER VALOR DE UN CAMPO</h1>
<sg:resourceXmlField var="typeName" xmlPath="ResourceType[1]/TypeName" lang="en"/>
<p>ResourceType[1]/TypeName: ${typeName}</p>

<h1>LEER VARIOS CAMPOS</h1>
<sg:resourceXmlField var="TypeName" xmlPath="ResourceType[2]/TypeName" lang="en"/>
<sg:resourceXmlField var="DetailPagesDisabled" xmlPath="ResourceType[2]/DetailPagesDisabled" lang="en"/>
<sg:resourceXmlField var="ElementView" xmlPath="ResourceType[2]/ElementView" lang="en"/>
<p>ResourceType[2]/TypeName: ${TypeName}</p>
<p>ResourceType[2]/DetailPagesDisabled: ${DetailPagesDisabled}</p>
<p>ResourceType[2]/ElementView: ${ElementView}</p>


<h1>CAMPOS MULTIPLES</h1>
<c:set var="ResourceType">ResourceType</c:set>
<sg:resourceXmlCount var="end" xmlPath="${ResourceType}" lang="en"/>
<c:forEach var="i" begin="1" end="${end}">
    <c:set var="TypeName">${ResourceType}[${i}]/TypeName</c:set>
    <sg:resourceXmlField var="TypeNameValue" xmlPath="${TypeName}" lang="en"/>
    <p>${TypeName}: ${TypeNameValue}</p>
</c:forEach>

<%-- XML de ejemplo: /system/modules/com.saga.sagasuite.core/.config
<?xml version="1.0" encoding="UTF-8"?>

<ModuleConfigurations xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="opencms://system/modules/org.opencms.ade.config/schemas/module_config.xsd">
    <ModuleConfiguration language="en">
        <ResourceType>
            <TypeName><![CDATA[sgexternaldocument]]></TypeName>
            <DetailPagesDisabled>true</DetailPagesDisabled>
        </ResourceType>
        <ResourceType>
            <TypeName><![CDATA[sgfullpage]]></TypeName>
            <DetailPagesDisabled>true</DetailPagesDisabled>
            <ElementView>
                <link type="WEAK">
                <target><![CDATA[/system/modules/com.alkacon.bootstrap.schemas/elementviews/template.xml]]></target>
                <uuid>bac8ebc0-448b-11e4-8287-005056b61161</uuid>
                </link>
            </ElementView>
        </ResourceType>
        <ResourceType>
            <TypeName><![CDATA[sgbasenewresource]]></TypeName>
            <Disabled><![CDATA[true]]></Disabled>
            <DetailPagesDisabled>true</DetailPagesDisabled>
        </ResourceType>
        <ResourceType>
            <TypeName><![CDATA[sgtabs]]></TypeName>
            <DetailPagesDisabled>true</DetailPagesDisabled>
        </ResourceType>
        <ResourceType>
            <TypeName><![CDATA[sgconfig]]></TypeName>
            <Disabled><![CDATA[true]]></Disabled>
            <DetailPagesDisabled>true</DetailPagesDisabled>
        </ResourceType>
        <ResourceType>
            <TypeName><![CDATA[sglayoutrow]]></TypeName>
            <DetailPagesDisabled>true</DetailPagesDisabled>
            <ElementView>
                <link type="WEAK">
                <target><![CDATA[/system/modules/com.alkacon.bootstrap.schemas/elementviews/layout.xml]]></target>
                <uuid>cfaece13-448b-11e4-8287-005056b61161</uuid>
                </link>
            </ElementView>
        </ResourceType>
        <ResourceType>
            <TypeName><![CDATA[sgtemplaterow]]></TypeName>
            <DetailPagesDisabled>true</DetailPagesDisabled>
            <ElementView>
                <link type="WEAK">
                <target><![CDATA[/system/modules/com.alkacon.bootstrap.schemas/elementviews/template.xml]]></target>
                <uuid>bac8ebc0-448b-11e4-8287-005056b61161</uuid>
                </link>
            </ElementView>
        </ResourceType>
        <ResourceType>
            <TypeName><![CDATA[sgthemeconfig]]></TypeName>
            <Disabled><![CDATA[true]]></Disabled>
            <DetailPagesDisabled>true</DetailPagesDisabled>
        </ResourceType>
        <Property>
            <PropertyName><![CDATA[Title]]></PropertyName>
            <DisplayName><![CDATA[Título]]></DisplayName>
            <Description><![CDATA[Indique un título para la sección (será el que se mapee al 'title' de la página de cara al SEO)]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <PreferFolder><![CDATA[true]]></PreferFolder>
            <Order><![CDATA[10]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[Description]]></PropertyName>
            <DisplayName><![CDATA[Descripción]]></DisplayName>
            <Description><![CDATA[Indique una descripción para la sección (será la que se mapee al meta 'description' de la página de cara al SEO)]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <PreferFolder><![CDATA[true]]></PreferFolder>
            <Order><![CDATA[20]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[template]]></PropertyName>
            <DisplayName><![CDATA[Template]]></DisplayName>
            <Description><![CDATA[Cargador del template por defecto]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Default><![CDATA[provider=org.opencms.loader.CmsDefaultTemplateContextProvider,template=/system/modules/com.saga.sagasuite.core/templates/load-themeconfig.jsp]]></Default>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.pagecssclass]]></PropertyName>
            <DisplayName><![CDATA[Clase Css para la página]]></DisplayName>
            <Description><![CDATA[Puede asignar una o varias clases css con el nombre que desee que se incluirán en el <body> de la página o rama de navegación]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[30]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.skin]]></PropertyName>
            <DisplayName><![CDATA[Skin]]></DisplayName>
            <Description><![CDATA[Conjunto de estilos que cambian el aspecto y comportamiento de la página o rama de navegación a la que se aplique. Debe introducirlo con formato 'skin-1', 'skin-2'... 'skin-n'. ]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[40]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[NavImage]]></PropertyName>
            <DisplayName><![CDATA[Imagen para menú vertical]]></DisplayName>
            <Description><![CDATA[Indique la ruta completa de la imagen que quiera que acompañe a la entrada de la sección en los menús verticales]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[45]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[category]]></PropertyName>
            <DisplayName><![CDATA[Categoría para listados y búsquedas]]></DisplayName>
            <Description><![CDATA[Introduzca la ruta completa de la categoría que desee usar como filtro para los listados y búsquedas para esta página o sección.]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <PreferFolder><![CDATA[true]]></PreferFolder>
            <Order><![CDATA[50]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.css]]></PropertyName>
            <DisplayName><![CDATA[Css custom]]></DisplayName>
            <Description><![CDATA[Especifique la ruta del archivo css que desee cargar para esta página o carpeta de OpenCms]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[60]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.js]]></PropertyName>
            <DisplayName><![CDATA[Js custom]]></DisplayName>
            <Description><![CDATA[Especifique la ruta del archivo js que desee cargar para esta página o carpeta de OpenCms]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[70]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[search.exclude]]></PropertyName>
            <DisplayName><![CDATA[Excluir de Búsquedas]]></DisplayName>
            <Description><![CDATA[Seleccione "Sí" si desea excluir el/los contenido/os de las búsquedas realizadas en la web, evitando así que se muestren accesos a estos recursos entre los resultados]]></Description>
            <Widget><![CDATA[select]]></Widget>
            <Default><![CDATA[false]]></Default>
            <WidgetConfig><![CDATA[true:Si|false:No]]></WidgetConfig>
            <Order><![CDATA[80]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.editable]]></PropertyName>
            <DisplayName><![CDATA[Editable]]></DisplayName>
            <Description><![CDATA[Marca la casilla para que el recurso sea editable]]></Description>
            <Widget><![CDATA[select]]></Widget>
            <Default><![CDATA[true]]></Default>
            <WidgetConfig><![CDATA[true:Editable|false:No editable]]></WidgetConfig>
            <Order><![CDATA[90]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.analytics]]></PropertyName>
            <DisplayName><![CDATA[Script de Analytics]]></DisplayName>
            <Description><![CDATA[Especifique la ruta del archivo "analytics.js" que desee cargar para esta página o carpeta de OpenCms. Por defecto se usa el archivo con este nombre que se incluye en el raíz del Site, pero puede especificarse la ruta de otro para una página o carpeta de OpenCms que se desee.]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[100]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.template]]></PropertyName>
            <DisplayName><![CDATA[Template específico]]></DisplayName>
            <Description><![CDATA[Indique el nombre del template que desea utilizar. No hace falta indicar la ruta completa, solo el nombre del recurso jsp. Ej: home.jsp]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[110]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.scripttag.afterMeta]]></PropertyName>
            <DisplayName><![CDATA[Javascript después de metas]]></DisplayName>
            <Description><![CDATA[Permite definir la ruta de un fichero de texto que incluye un script que insertar en el <head> después de los css]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[115]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.scripttag.endHead]]></PropertyName>
            <DisplayName><![CDATA[Javascript final HEAD]]></DisplayName>
            <Description><![CDATA[Permite definir la ruta de un fichero de texto que incluye un script que insertar al final de la etiqueta <head>]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[120]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.scripttag.startBody]]></PropertyName>
            <DisplayName><![CDATA[Javascript comienzo BODY]]></DisplayName>
            <Description><![CDATA[Permite definir la ruta de un fichero de texto que incluye un script que insertar al principio de la etiqueta <body>]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[125]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.scripttag.endBody]]></PropertyName>
            <DisplayName><![CDATA[Javascript final BODY]]></DisplayName>
            <Description><![CDATA[Permite definir la ruta de un fichero de texto que incluye un script que insertar al final de la etiqueta <body>]]></Description>
            <Widget><![CDATA[string]]></Widget>
            <Order><![CDATA[130]]></Order>
        </Property>
        <Property>
            <PropertyName><![CDATA[sagasuite.target]]></PropertyName>
            <DisplayName><![CDATA[Abrir en ventana nueva]]></DisplayName>
            <Description><![CDATA[Selecciona si deseas que esta página se abra o no en una ventana nueva cuando se enlace desde cualquier menú]]></Description>
            <Widget><![CDATA[select]]></Widget>
            <WidgetConfig><![CDATA[_blank:Abrir en ventana nueva|_self:Abrir en misma ventana]]></WidgetConfig>
        </Property>
    </ModuleConfiguration>
</ModuleConfigurations>
--%>
