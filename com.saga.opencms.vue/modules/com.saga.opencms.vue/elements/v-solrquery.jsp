<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.jsp.util.CmsJspContentAccessValueWrapper" %>
<%@ page import="org.opencms.jsp.CmsJspActionElement" %>
<%@ page import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ page import="org.opencms.file.CmsProject" %>
<%@ page import="org.opencms.util.CmsHtmlExtractor" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
<%@ page import="org.opencms.search.solr.CmsSolrQuery" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<cms:secureparams/>

<%!
    private static final Log LOG = CmsLog.getLog("__COM_SAGA_SAGASUITE_SEARCH--ELEMENTS--C_SOLRQUERY_JSP_");
%>
<%
    CmsObject cmsObject = (CmsObject) request.getAttribute("cmsObject");
%>

<c:set var="rpage" value="${value.NumResultPerPage }"/>
<c:if test="${value.NavPageSize.isSet }">
    <c:set var="navpage" value="${value.NavPageSize }"/>
</c:if>
<c:if test="${!value.NavPageSize.isSet }">
    <c:set var="navpage" value="10"/>
</c:if>
<c:set var="indexOffline" value="${value.SolrIndexOffline }"/>
<c:set var="indexOnline" value="${value.SolrIndex }"/>
<c:if test="${content.value.Popularity.isSet }">
    <c:set var="popularity" value="${content.value.Popularity }"/>
</c:if>
<c:set var="Autorun" value="${value.Autorun }"/>
<c:set var="ShowForm" value="${value.ShowForm }"/>
<c:if test="${value.InitParameter.isSet }">
    <c:set var="initParameter" value="${value.InitParameter }"/>
</c:if>
<c:set var="initSolrQuery" value=""/>
<c:if test="${value.InitSolrQuery.isSet }">
    <c:set var="initSolrQuery" value="${value.InitSolrQuery }"/>
</c:if>

<c:if test="${value.MultiSitesSearch.isSet }">
    <c:set var="multiSitesSearch" value="${value.MultiSitesSearch }"/>
</c:if>


<%
    String submit = request.getParameter("submit");
    String searchaction = request.getParameter("searchaction");
    boolean exactSearch = false;
    if (request.getParameter("exactSearch") != null) {
        exactSearch = true;
    }

    boolean autorun = false;
    CmsJspContentAccessValueWrapper _wrapper = (CmsJspContentAccessValueWrapper) pageContext.getAttribute("Autorun");
    autorun = new Boolean(_wrapper.toString());
    pageContext.setAttribute("__Autorun", autorun);

    boolean showForm = false;
    CmsJspContentAccessValueWrapper _wrapperShowForm = (CmsJspContentAccessValueWrapper) pageContext.getAttribute("ShowForm");
    showForm = new Boolean(_wrapperShowForm.toString());
    pageContext.setAttribute("__ShowForm", showForm);
    String initSolrQuery = null;
    if (autorun && submit == null && searchaction == null) {

        //Si se ha configurado una consulta por defecto, la leemos y la metemos en el request para tenerla presente a la hora de hacer la query:
        initSolrQuery = "" + pageContext.getAttribute("initSolrQuery");

        if (initSolrQuery == null || initSolrQuery.equals("")) {
            //Si hay parametros de inicio configurado hacemos un forward forzando los parametros configurados
            String initParameter = "" + pageContext.getAttribute("initParameter");
            //FIX: Solo para el caso que no vengamos de editar ya que el ADE no le sienta bien
            Boolean isEdited = false;
            if (request.getAttribute("isEdited") != null)
                isEdited = Boolean.valueOf("" + request.getAttribute("isEdited"));
            if (initParameter != null && initParameter.length() > 0 && !initParameter.equals("null") && !isEdited) {
                CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);
                String newUrl = cms.link(cms.getRequestContext().getUri() + initParameter);
                response.sendRedirect(newUrl);
            }
            initSolrQuery = null;
        }


    }

    if (submit != null) {
        pageContext.setAttribute("__Autorun", false);
    }

    CmsSolrResultList results = null;

//Si se ha hecho submit en el formulario, hacemos la consulta a solr
    boolean isOnline = CmsProject.isOnlineProject(cmsObject.getRequestContext().getCurrentProject().getUuid());

//Carga de campos del XML
    long rpage = Long.parseLong("" + pageContext.getAttribute("rpage"));
    String navPageStr = "" + pageContext.getAttribute("navpage");
    long navepage = 10;
    if (navPageStr != null && !navPageStr.equals("null"))
        navepage = Long.parseLong(navPageStr);
    String searchPage = request.getParameter("searchPage");

    String indexOffline = "" + pageContext.getAttribute("indexOffline");
    String indexOnline = "" + pageContext.getAttribute("indexOnline");

    /*long start = 1;
    long currentPage = 1;
    if (searchPage != null) {
        currentPage = Long.parseLong(searchPage);
        start = (currentPage - 1) * rpage;
        pageContext.setAttribute("currentPage", currentPage);
    } else {
        pageContext.setAttribute("currentPage", 1);
    }*/

    String query = "";

    List<String> queryParser = new ArrayList<String>();

//incluimos el orden y la paginacion
//query += "&sort=score asc";
    query += "&rows=" + rpage;

%>

<%-- gestion de categorias --%>
<%-- si se definen en el recurso --%>
<c:if test="${value.ResourceCategory.isSet and empty categoryProperty}">

    <c:forEach var="ResCatElem" items="${content.valueList.ResourceCategory }">
        <c:set var="ResourceCategory" value="${ResCatElem }"/>
        <%

            //System.out.println("Elemento individual "+pageContext.getAttribute("ResourceCategory"));
            CmsJspContentAccessValueWrapper _wrapperCat = (CmsJspContentAccessValueWrapper) pageContext.getAttribute("ResourceCategory");
            String catOk = _wrapperCat.toString();
            catOk = catOk.substring(catOk.indexOf("categories/") + "categories/".length(), catOk.length());
            query += "&fq=category:\"" + catOk + "\"";
        %>

    </c:forEach>
</c:if>

<%
    //Dependiendo si es offline u online cogemos un indice u otro
    String indice = indexOnline;
    if (!isOnline)
        indice = indexOffline;


//Si la variable initSolrQuery es null, entonces seguimos con el filtro, en caso contrario no hace falta pq esa será la query por defecto
    if (initSolrQuery == null) {
%>

<c:forEach var="elem" items="${content.subValueList['Filter']}" varStatus="status">
    <c:set var="fieldName">${value.Id }field-${status.count}</c:set>
    <%
        String fieldName = "" + pageContext.getAttribute("fieldName");
    %>
    <c:choose>

        <%-- FILTRO TEXTO =============================================================================--%>

        <c:when test="${elem.name == 'TextFilter'}">
            <c:set var="solrField" value="${elem.value.FieldSolr }"/>
            <%
                String popularity = "";
                if (pageContext.getAttribute("popularity") != null)
                    popularity = " AND " + pageContext.getAttribute("popularity");

                String solrField = "" + pageContext.getAttribute("solrField");
                String[] fields = solrField.split(",");
                String paramQuery = request.getParameter("query");


                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null) {
                    fieldValue = CmsHtmlExtractor.extractText(fieldValue, cmsObject.getRequestContext().getEncoding());
                    queryParser.addAll(CmsStringUtil.splitAsList(fieldValue, " "));

                    boolean containsSearch = Boolean.parseBoolean("" + pageContext.getAttribute("containsSearch"));
                    if (exactSearch) {
                        fieldValue = "\"" + fieldValue + "\"";
                    } else if (containsSearch) {
                        fieldValue = "*" + fieldValue + "*";
                    }
                }
                if (fieldValue != null && fieldValue.length() > 2 && fields.length == 1) {
                    String boost = "";
                    String field = "";
                    //Vemos si se ha configurado un peso, y partimos el string para quedarnos con la config
                    if (solrField.indexOf("^") > -1) {
                        boost = solrField.substring(solrField.indexOf("^") + 1);
                        field = solrField.substring(0, solrField.indexOf("^"));
                        query += "&q=" + field + ":" + fieldValue + "^" + boost + popularity;
                    } else {
                        field = solrField;
                        query += "&q=" + field + ":" + fieldValue + popularity;
                    }

                } else if (fieldValue != null && fieldValue.length() > 2 && fields.length > 1) {

                    for (int i = 0; i < fields.length; i++) {
                        String boost = "";
                        String field = "";
                        //Vemos si se ha configurado un peso, y partimos el string para quedarnos con la config
                        if (fields[i].indexOf("^") > -1) {
                            boost = fields[i].substring(fields[i].indexOf("^") + 1);
                            field = fields[i].substring(0, fields[i].indexOf("^"));
                            if (i == 0) //La primera iteracion
                                query += "&q=((" + field + ":" + fieldValue + "^" + boost;
                            else
                                query += " OR " + field + ":" + fieldValue + "^" + boost;
                        } else {
                            field = fields[i];
                            if (i == 0) //La primera iteracion
                                query += "&q=((" + field + ":" + fieldValue + "";
                            else
                                query += " OR " + field + ":" + fieldValue + "";
                        }
                    }
                    query += ")" + popularity + ")";
                } else {
                    //Si no ha llegado el parametro correspondiente, probamos si llega un parametro generico query
                    String paramQueryGenerico = request.getParameter("query");
                    if (paramQueryGenerico != null && paramQueryGenerico.length() > 2) {
                        if (fields.length == 1) {
                            query += "&q=" + solrField + ":" + paramQueryGenerico;
                        } else {
                            for (int i = 0; i < fields.length; i++) {
                                if (i == 0) //La primera iteracion
                                    query += "&q=((" + fields[i] + ":" + paramQueryGenerico;
                                else
                                    query += " OR " + fields[i] + ":" + paramQueryGenerico;
                            }
                            query += ")" + popularity + ")";
                        }
                    }
                }

            %>
        </c:when>

        <%-- FILTRO FECHA =============================================================================--%>

        <c:when test="${elem.name == 'Date1Filter'}">
            <c:set var="solrField" value="${elem.value.FieldSolr }"/>
            <%
                String solrField = "" + pageContext.getAttribute("solrField");
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    try {
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        DateFormat dfSolr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date d = df.parse(fieldValue);
                        Date d2 = new Date(d.getTime() + (24 * 60 * 60 * 1000)); //1 dia despues
                        query += "&fq=" + solrField + ":[" + dfSolr.format(d) + " TO " + dfSolr.format(d2) + "]";
                    } catch (Exception ex) {
                        //noop
                    }
                }
            %>

        </c:when>
        <c:when test="${elem.name == 'Date2Filter'}">
            <c:set var="solrField" value="${elem.value.FieldSolr }"/>
            <%
                String fieldValue1 = request.getParameter(fieldName + "_d1");
                String fieldValue2 = request.getParameter(fieldName + "_d2");
                String solrField = "" + pageContext.getAttribute("solrField");
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat dfSolr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date d1 = null;
                Date d2 = null;
                try {
                    if ((fieldValue1 != null && (fieldValue1.length() == 10 || fieldValue1.equals("NOW") || fieldValue1.equals("*"))) || (fieldValue2 != null && (fieldValue2.length() == 10) || fieldValue2.equals("NOW") || fieldValue2.equals("*"))) {
                        String fechaSolr1 = "";
                        String fechaSolr2 = "";
                        if (fieldValue1 != null && (fieldValue1.indexOf("NOW") > -1 || fieldValue1.equals("*"))) {
                            fechaSolr1 = fieldValue1;
                        } else if (fieldValue1 != null && fieldValue1.length() == 10) {
                            d1 = df.parse(fieldValue1);
                            fechaSolr1 = dfSolr.format(d1);
                        } else {
                            fechaSolr1 = "*";
                        }

                        if (fieldValue2 != null && (fieldValue2.indexOf("NOW") > -1 || fieldValue2.equals("*"))) {
                            fechaSolr2 = fieldValue2;
                        } else if (fieldValue2 != null && fieldValue2.length() == 10) {
                            d2 = df.parse(fieldValue2);
                            fechaSolr2 = dfSolr.format(d2);
                        } else {
                            fechaSolr2 = "*";
                        }
                        query += "&fq=" + solrField + ":[" + fechaSolr1 + " TO " + fechaSolr2 + "]";
                    }
                } catch (Exception ex) {
                    //noop
                }
            %>
        </c:when>

        <%-- FILTRO CATEGORIA =============================================================================--%>

        <c:when test="${elem.name == 'CategoryFilter' }">

            <c:set var="solrField" value="${elem.value.FieldSolr }"/>
            <c:choose>
                <c:when test="${elem.value.FieldType == 'select' || elem.value.FieldType == 'radio'}">
                    <%
                        String solrField = "" + pageContext.getAttribute("solrField");
                        String fieldValue = request.getParameter(fieldName);
                        if (fieldValue != null && fieldValue.length() > 0) {
                            //si establecemos valor en este campo se guarda como vacia la variable que nos viene si se ha marcado leer propiedad de categoria y existe esa propiedad
                            pageContext.setAttribute("categoryProperty", "");
                            // se suma el valor para el valor de category en la query
                            query += "&fq=category:\"" + fieldValue + "\"";
                        }
                    %>
                </c:when>
                <c:when test="${elem.value.FieldType == 'selectmultiple' || elem.value.FieldType == 'checkbox' }">
                    <%
                        String solrField = "" + pageContext.getAttribute("solrField");
                        String[] fieldValuesArray = request.getParameterValues(fieldName);
                        if (fieldValuesArray != null && fieldValuesArray.length > 0) {
                            //si establecemos valor en este campo se guarda como vacia la variable que nos viene si se ha marcado leer propiedad de categoria y existe esa propiedad
                            pageContext.setAttribute("categoryProperty", "");
                            // se suma el valor para el valor de category en la query
                            query += "&fq=category:(";
                            List<String> fieldValues = Arrays.asList(fieldValuesArray);
                            int cont = 1;
                            for (String v : fieldValues) {


                                if (cont == fieldValues.size())
                                    query += "\"" + v + "\")"; //Si es el ultimo no metemos el OR
                                else
                                    query += "\"" + v + "\"" + " OR ";
                                cont++;
                            }
                        }
                    %>
                </c:when>
            </c:choose>
        </c:when>

        <%-- FILTRO TIPO DE RECURSO =============================================================================--%>
        <c:when test="${elem.name == 'ResourceTypeFilter'}">
            <c:choose>
                <c:when test="${elem.value.FieldType == 'select' || elem.value.FieldType == 'radio'}">
                    <%
                        String solrField = "" + pageContext.getAttribute("solrField");
                        String fieldValue = request.getParameter(fieldName);
                        if (fieldValue != null && !fieldValue.equals("*")) {
                            query += "&fq=type:" + fieldValue;
                            //si establecemos valor en este campo se guarda como vacia la variable que nos viene del setting de tipo de recurso
                            pageContext.setAttribute("settingresourcetype", "");
                        }
                    %>
                </c:when>
                <c:when test="${elem.value.FieldType == 'selectmultiple' || elem.value.FieldType == 'checkbox' }">
                    <%
                        String solrField = "" + pageContext.getAttribute("solrField");
                        String[] fieldValuesArray = request.getParameterValues(fieldName);
                        if (fieldValuesArray != null && fieldValuesArray.length > 0) {
                            List<String> fieldValues = Arrays.asList(fieldValuesArray);
                            int cont = 1;
                            String typeQuery = "";
                            for (String v : fieldValues) {
                                if (cont == fieldValues.size())
                                    typeQuery += v; //Si es el ultimo no metemos el OR
                                else
                                    typeQuery += "(" + v + ")" + " OR ";
                                cont++;
                            }
                            query += "&fq=type:(" + typeQuery + ")";
                            //si establecemos valor en este campo se guarda como vacia la variable que nos viene del setting de tipo de recurso
                            pageContext.setAttribute("settingresourcetype", "");
                        }
                    %>
                </c:when>
            </c:choose>
        </c:when>

        <%-- FILTRO SITE =============================================================================--%>

        <c:when test="${elem.name == 'SiteFilter'}">
            <%
                String solrField = "" + pageContext.getAttribute("solrField");
                String[] fieldValuesArray = request.getParameterValues(fieldName);
                if (fieldValuesArray != null && fieldValuesArray.length > 0) {
                    query += "&fq=parent-folders:(";
                    List<String> fieldValues = Arrays.asList(fieldValuesArray);
                    int cont = 1;
                    for (String v : fieldValues) {
                        if (!v.endsWith("/"))
                            v = v + "/";
                        if (cont == fieldValues.size())
                            query += "\"" + v + "\")"; //Si es el ultimo no metemos el OR
                        else
                            query += "\"" + v + "\"" + " OR ";
                        cont++;
                    }
                    pageContext.setAttribute("parentFolderConfigurado", true);
                } else {
                    pageContext.setAttribute("parentFolderConfigurado", true);
            %>
            <c:set var="rootPathQuery">&fq=parent-folders:${currentSite}/</c:set>
            <%
                }
            %>
        </c:when>

        <%--FILTRO PERSONALIZADO =============================================================================--%>
        <c:when test="${elem.name == 'SelectFilter'}">
            <c:set var="solrField" value="${elem.value.FieldSolr }"/>
            <%
                String solrField = "" + pageContext.getAttribute("solrField");
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && !fieldValue.equals("")) {
                    query += "&fq=" + solrField + ":\"" + fieldValue + "\"";
                }
            %>
        </c:when>
        <c:when test="${elem.name == 'CustomFilter'}">
            <%
                String customQuery = "" + request.getAttribute("query_" + fieldName);
                if (customQuery != null && !customQuery.equals("") && !customQuery.equals("null")) {
                    query += customQuery;
                }
            %>
        </c:when>
    </c:choose>
</c:forEach>

<%-- FIN DE FILTROS ===============--%>
<%-- si el filtro de categoria no tiene valor se lee de la property de category en el caso que se haya marcado el campo en el recurso --%>
<c:if test="${not empty categoryProperty}">
    <c:set var="ResourceCategory" value="${categoryProperty }"/>
    <%
        String Cat = "" + pageContext.getAttribute("ResourceCategory");
        int iCategories = Cat.indexOf("categories/") + "categories/".length();
        Cat = Cat.substring(iCategories);
        query += "&fq=category:" + "\"" + Cat + "\"";
    %>
</c:if>

<c:forEach var="elem" items="${content.valueList['SolrFieldQuery']}">
    <c:set var="fieldquery" value="${elem}"/>
    <%
        String fieldquery = "" + pageContext.getAttribute("fieldquery");
        query += "&" + fieldquery;
    %>
</c:forEach>

<%-- TIPO DE RECURSO DEFINIDO =======================================================================--%>
<c:set var="resourcetype" value=""/>
<c:forEach var="elem" items="${content.valueList['ResourceType']}" varStatus="status">
    <c:if test="${status.first }">
        <c:set var="resourcetype">&fq=type:("${elem}"</c:set>
    </c:if>
    <c:if test="${!status.first }">
        <c:set var="resourcetype">${resourcetype} OR "${elem}"</c:set>
    </c:if>
    <c:if test="${status.last }">
        <c:set var="resourcetype">${resourcetype})</c:set>
    </c:if>
</c:forEach>

<%-- TIPO DE RECURSO DEFINIDO EN EL SETTING =======================================================================--%>
<%-- si el filtro de tipo de recurso no tiene valor se lee del setting por defecto y se machaca la configuracion de tipo de recurso definida en el buscador --%>
<c:if test="${not empty settingresourcetype }">
    <c:set var="resourcetype">
        &fq=type:${settingresourcetype}
    </c:set>
</c:if>

<%-- EXCLUIR TIPO DE RECURSO DEFINIDO =======================================================================--%>
<c:set var="excluderesourcetype" value=""/>
<c:forEach var="elem" items="${content.valueList['ExcludeResourceType']}" varStatus="status">
    <c:if test="${status.first }">
        <c:set var="excluderesourcetype">&fq=type:(!"${elem}"</c:set>
    </c:if>
    <c:if test="${!status.first }">
        <c:set var="excluderesourcetype">${excluderesourcetype} AND !"${elem}"</c:set>
    </c:if>
    <c:if test="${status.last }">
        <c:set var="excluderesourcetype">${excluderesourcetype})</c:set>
    </c:if>
</c:forEach>

<c:set var="resourcetype">${resourcetype}${excluderesourcetype }</c:set>
<%
    String resourcetype = "" + pageContext.getAttribute("resourcetype");
    query += resourcetype;

//Anadimos el filtro por locale actual
    query += "&fq=con_locales:" + cmsObject.getRequestContext().getLocale();


%>

<c:forEach var="elem" items="${content.valueList['Order']}" varStatus="status">
    <c:set var="order" value="${elem}"/>
    <c:if test="${status.first}">
        <%
            String order = "" + pageContext.getAttribute("order");
            if (order.indexOf("{rand}") > -1) {
                String rand = "" + java.lang.Math.round(java.lang.Math.random() * 10000);
                order = order.replace("{rand}", rand);
            }
            query += "&sort=" + order;
        %>
    </c:if>

    <c:if test="${!status.first}">
        <%
            String order = "" + pageContext.getAttribute("order");
            if (order.indexOf("{rand}") > -1) {
                String rand = "" + java.lang.Math.round(java.lang.Math.random() * 10000);
                order = order.replace("{rand}", rand);
            }
            query += ", " + order;
        %>
    </c:if>

</c:forEach>

<%-- Tenemos que validar que ya el filtro de site no haya metido el parent folder porque en ese caso no podemos meter nada mas --%>
<c:if test="${parentFolderConfigurado==null || parentFolderConfigurado == 'false'}">
    <c:choose>
        <c:when test="${multiSitesSearch!=null && multiSitesSearch == 'true' }">
            <c:set var="rootPathQuery">&fq=parent-folders:/</c:set>
        </c:when>
        <c:otherwise>
            <c:set var="rootPathQuery">&fq=parent-folders:("${currentSite}/")</c:set>
        </c:otherwise>
    </c:choose>
    <c:forEach var="path" items="${content.valueList['RootPath']}" varStatus="status">
        <c:if test="${not fn:startsWith(path, '/sites/')}">
            <c:set var="path">${currentSite}${path }</c:set>
        </c:if>
        <c:if test="${status.first }">
            <c:set
                    var="rootPathQuery">&fq=parent-folders:("${path}"</c:set> <%-- Abrimos el parentesis '(' que engloba la lista entera de path --%>
        </c:if>
        <c:if test="${!status.first }">
            <c:set
                    var="rootPathQuery">${rootPathQuery} OR "${path}"</c:set> <%-- Concatenamos en la consulta con operador OR --%>
        </c:if>
        <c:if test="${status.last }">
            <c:set
                    var="rootPathQuery">${rootPathQuery})</c:set> <%-- Cerramos el parentesis '(' que abrimos para englobar la lista --%>
        </c:if>
    </c:forEach>
    <%-- Si se ha configurado una lista de carpetas a excluir --%>
    <c:if test="${content.value.ExcludePath.isSet }">
        <c:set var="rootPathQuery" value="${fn:substring(rootPathQuery, 0, fn:length(rootPathQuery)-1)}"></c:set>
        <c:forEach var="path" items="${content.valueList['ExcludePath']}" varStatus="status">
            <c:if test="${not fn:startsWith(path, '/sites/')}">
                <c:set var="path">${currentSite}${path }</c:set>
            </c:if>
            <c:set var="rootPathQuery">${rootPathQuery} AND !"${path}"</c:set>
            <c:if test="${status.last }">
                <c:set var="rootPathQuery">${rootPathQuery})</c:set>
            </c:if>
        </c:forEach>
    </c:if>
</c:if>

<%
        //Configuracion del ParentFolder
        String rootPathQuery = (String) pageContext.getAttribute("rootPathQuery");
        if (rootPathQuery != null && rootPathQuery.length() > 0) {
            //incluimos los recursos configurados por el usuario
            query += rootPathQuery;
        }

    } //Fin if initSolrQuery==null
    else {
        query = initSolrQuery; //La query sera la escrita en el recurso
    }
    request.setAttribute("query", query);
%>
