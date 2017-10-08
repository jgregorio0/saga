<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.relations.CmsCategory" %>
<%@ page import="org.opencms.relations.CmsCategoryService" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
<c:if test="${value.ShowForm!=null && value.ShowForm == 'true' }">
 --%>

<%!
    private final Log LOG = CmsLog.getLog(this.getClass());

    private String readCategoryTitle(
            CmsCategoryService categoryService, CmsObject cmso,
            String catRelPath, String catRepo)
            throws CmsException {
        CmsCategory c = categoryService.readCategory(cmso, catRelPath, catRepo);
//        categoryService.readCategory(cmso, cat.getPath(), categoryRoot);
        String titulo = c.getTitle();
        //Primero intentamos leer la categoria Title_LOCALE, si no nos quedamos con el Title
        try {
            String site = OpenCms.getSiteManager().getCurrentSite(cmso).getSiteRoot();
            String pathResourceCat = c.getRootPath();
            pathResourceCat = pathResourceCat.replace(site, "");
            titulo = cmso.readPropertyObject(pathResourceCat, "Title_" + cmso.getRequestContext().getLocale(), false).getValue(c.getTitle());
        } catch (CmsException ex) {
            LOG.debug("error getting title from categoty: " + c);
        }
        return titulo;
    }
%>
<fmt:setLocale value="${cms.locale}"/>
<c:set var="messageproperty" value="${value.MessagesProperties }"/>
<c:if test="${!value.MessagesProperties.isSet }">
    <c:set var="messageproperty" value="com.saga.sagasuite.search.workplace"/>
</c:if>
<cms:bundle basename="${messageproperty }">
    <%
        CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
        List<String> filters = new ArrayList<String>();
    %>

    <c:if test="${param.loadform == 'true'}">
        <c:set var="contField">0</c:set>
        <c:set var="xmlFilters" value="${content.subValueList['Filter']}"/>
        <c:if test="${not empty xmlFilters}">
            <c:set var="contField">${xmlFilters.size()}</c:set>
        </c:if>

        <%--TODO send general--%>
        <c:set var="btnblockcssclass">col-sm-12</c:set>
        <c:if test="${value.ButtonBlockCssClass.isSet}">
            <c:set var="btnblockcssclass">${value.ButtonBlockCssClass}</c:set>
        </c:if>

        <%--TODO send general ,searchBtnText: '${value.ButtonText }'
        ,searchBtnClass: '${value.ButtonClass }'--%>

        <c:forEach var="elem" items="${content.subValueList['Filter']}" varStatus="status">

            <%--FILTER NAME--%>
            <c:set var="filterName">${value.Id }-field-${status.count}</c:set>

            <%--FILTER DEFAULT VALUE--%>
            <c:set var="filterValue"></c:set>
            <%
                String filterName = (String) pageContext.getAttribute("filterName");
                String filterValue = request.getParameter(filterName);
                if (StringUtils.isNotBlank(filterValue)) {
                    pageContext.setAttribute("filterValue", filterValue);
                }
            %>

            <%--FILTER CLASS--%>
            <c:set var="filterClass">col-sm-12</c:set>
            <c:if test="${elem.value.CssClass.isSet}">
                <c:set var="filterClass">${elem.value.CssClass}</c:set>
            </c:if>

            <%--TEXT FILTER PRINCIPAL--%>
            <c:choose>
                <c:when test="${elem.name == 'TextFilter' and elem.value.Principal == 'true'}">
                    <c:set var="filter">
                        {
                        "id": "${filterName}"
                        ,"count": ${status.count}
                        ,"type": "TextFilter"
                        <c:if test="${elem.value.Label.isSet}">,"label": "${elem.value.Label}"</c:if>
                        <c:if test="${elem.value.Placeholder.isSet}">,"placeholder": "${elem.value.Placeholder}"</c:if>
                        ,"solr": "${elem.value.FieldSolr}"
                        ,"class": "${filterClass}"
                        ,"isExactSearch": false
                        ,"isContainsSearch": ${elem.value.ContainsSearch}
                        ,"isPrincipal": true
                        ,"value": "${filterValue}"
                        ,"showExactSearch": ${value.ShowExactSearch.isSet && value.ShowExactSearch == 'true'}
                        ,"labelExactResult": "${value.LabelExactResult }"
                        ,"buttonText": "${value.ButtonText}"
                        ,"buttonClass": "${value.ButtonClass}"
                        }
                    </c:set>
                </c:when>
                <c:when test="${elem.name == 'TextFilter' and elem.value.Principal != 'true'}">
                    <c:set var="filter">
                        {
                        "id": "${filterName}"
                        ,"count": ${status.count}
                        ,"type": "TextFilter"
                        <c:if test="${elem.value.Label.isSet}">,"label": "${elem.value.Label}"</c:if>
                        <c:if test="${elem.value.Placeholder.isSet}">,"placeholder": "${elem.value.Placeholder}"</c:if>
                        ,"solr": "${elem.value.FieldSolr}"
                        ,"isPrincipal": false
                        ,"class": "${filterClass}"
                        ,"value": "${filterValue}"
                        }
                    </c:set>
                </c:when>
                <c:when test="${elem.name == 'Date1Filter'}">
                    <c:set var="filter">
                        {
                        "id": "${filterName}"
                        ,"count": ${status.count}
                        ,"type": "Date1Filter"
                        <c:if test="${elem.value.Label.isSet}">,"label": "${elem.value.Label}"</c:if>
                        <c:if test="${elem.value.Placeholder.isSet}">,"placeholder": "${elem.value.Placeholder}"</c:if>
                        ,"solr": "${elem.value.FieldSolr}"
                        ,"class": "${filterClass}"
                        ,"value": "${filterValue}"
                        }
                    </c:set>
                </c:when>
                <c:when test="${elem.name == 'Date2Filter'}">

                    <%--FILTER DEFAULT VALUE DATE--%>
                    <c:set var="filterValue1"></c:set>
                    <c:set var="filterValue2"></c:set>
                    <%
                        String filterValue1 = request.getParameter(filterName + "_d1");
                        if (StringUtils.isNotBlank(filterValue1)) {
                            pageContext.setAttribute("filterValue1", filterValue1);
                        }
                        String filterValue2 = request.getParameter(filterName + "_d2");
                        if (StringUtils.isNotBlank(filterValue2)) {
                            pageContext.setAttribute("filterValue2", filterValue2);
                        }
                    %>

                    <c:set var="filter">
                        {
                        "id": "${filterName}"
                        ,"count": ${status.count}
                        ,"type": "Date2Filter"
                        <c:if test="${elem.value.Label.isSet}">,"label": "${elem.value.Label}"</c:if>
                        <c:if test="${elem.value.Placeholder.isSet}">,"placeholder": "${elem.value.Placeholder}"</c:if>
                        ,"solr": "${elem.value.FieldSolr}"
                        ,"class": "${filterClass}"
                        ,"labelDate1": "${elem.value.LabelDate1 }"
                        ,"valueDate1": "${filterValue1}"
                        ,"labelDate2": "${elem.value.LabelDate2 }"
                        ,"valueDate2": "${filterValue2}"
                        }
                    </c:set>
                </c:when>
                <c:when test="${elem.name == 'CategoryFilter'}">
                    <c:set var="CategoryRoot" value="${elem.value.CategoryRoot}"/>
                    <c:set var="TreeFolder" value="${elem.value.TreeFolder}"/>
                    <c:set var="ShowParent" value="${elem.value.ShowParent}"/>
                    <c:set var="LabelCategoryFilterAll" value="${value.LabelCategoryFilterAll}"/>

                    <%
                        // Default value.
                        // 1- Parametro en la URI
                        // 2- Property category
                        // 3- Cadena vacia
                        if (StringUtils.isBlank(filterValue)) {
                            filterValue = (String) request.getAttribute("filtercategoryProperty");
                        } else {
                            filterValue = "";
                        }

                        // Almacenamos array de categorias
                        List<String> categoriesFilter = new ArrayList<String>();

                        // Obtenemos repositorio raiz y parametros para la carga de categorias
                        String categoryRoot = "" + pageContext.getAttribute("CategoryRoot");
                        String completeCategoryPath = categoryRoot;
                        String treeFolder = "" + pageContext.getAttribute("TreeFolder");
                        String showParent = "" + pageContext.getAttribute("ShowParent");
                        String labelCategoryFilterAll = "" + pageContext.getAttribute("LabelCategoryFilterAll");
                        if (categoryRoot.startsWith("/system/categories/")) {
                            categoryRoot = categoryRoot.replace("/system/categories/", "");
                        } else if (categoryRoot.indexOf("/.categories/") > -1) {
                            categoryRoot = categoryRoot.substring(categoryRoot.indexOf(".categories/") + ".categories/".length(), categoryRoot.length());
                        }

                        // Si incluye subcategorias
                        boolean includeSubCats = true;
                        if (treeFolder.equals("false"))
                            includeSubCats = false;

                        // Leemos las categorias
                        CmsCategoryService categoryService = new CmsCategoryService();
                        List<CmsCategory> categorias = categoryService.readCategories(cmso, categoryRoot, includeSubCats, completeCategoryPath);

                        // Contamos el nivel actual para luego pintar la tabulacion en caso de ser necesario
                        Integer nivelOrigen = categoryRoot.split("/").length;

                        // Obtenemos categoria padre
                        String title = "";
                        String catClass = "nivel-0";
                        String catValue = categoryRoot;

                        //Sacamos el titulo de la opcion de categoria padre
                        if (showParent != null && showParent.equals("blank")) {
                            title = "";
                        } else if (showParent != null && showParent.equals("all")) {
                            title = labelCategoryFilterAll;
                        } else if (showParent != null && showParent.equals("title")) {
                            title = readCategoryTitle(categoryService, cmso, categoryRoot, categoryRoot);
                        }

                        // Add categoria
                        JSONObject catFilter = new JSONObject();
                        catFilter.put("title", title);
                        catFilter.put("class", catClass);
                        catFilter.put("value", catValue);
                        categoriesFilter.add(catFilter.toString());

                        for (CmsCategory cat : categorias) {
                            title = "";
                            catClass = "";
                            catValue = cat.getPath();

                            //Primero intentamos leer la categoria Title_LOCALE, si no nos quedamos con el Title
                            title = readCategoryTitle(categoryService, cmso, cat.getPath(), categoryRoot);

                            //Si hay subniveles, tenemos que pintarlo en forma de arbol calculando las tabulaciones
                            if (includeSubCats) {
                                // nivel
                                Integer nivelAbsoluto = cat.getPath().split("/").length;
                                Integer nivelRelativo = nivelAbsoluto - nivelOrigen;

                                // tabulacion
                                String tab = "";
                                for (int i = 0; i < nivelRelativo; i++) {
                                    tab += "&nbsp;&nbsp;&nbsp;";
                                }
                                title = tab + title;

                                // clase
                                catClass = "nivel-" + nivelRelativo;
                            }

                            // Add categoria
                            catFilter = new JSONObject();
                            catFilter.put("title", title);
                            catFilter.put("class", catClass);
                            catFilter.put("value", catValue);
                            categoriesFilter.add(catFilter.toString());
                        }

                        StringBuffer sb = new StringBuffer();
                        for (String catFilterStr : categoriesFilter) {
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            sb.append(catFilterStr);
                        }
                        pageContext.setAttribute("categoriesFilter", "[" + sb.toString() + "]");
                    %>
                    <c:set var="filter">
                        {
                        "id": "${filterName}"
                        ,"count": ${status.count}
                        ,"type": "CategoryFilter"
                        <c:if test="${elem.value.Label.isSet}">,"label": "${elem.value.Label}"</c:if>
                        <c:if test="${elem.value.Placeholder.isSet}">,"placeholder": "${elem.value.Placeholder}"</c:if>
                        ,"solr": "${elem.value.FieldSolr}"
                        ,"class": "${filterClass}"
                        ,"categoryRoot": "${CategoryRoot}"
                        ,"treeFolder": "${TreeFolder}"
                        ,"showParent": "${ShowParent}"
                        ,"showEmptyOption": "${!elem.value.ShowEmptyOption.exists or elem.value.ShowEmptyOption=='true'}"
                        ,"labelCategoryFilterAll": "${LabelCategoryFilterAll}"
                        ,"fieldType": "${elem.value.FieldType}"
                        ,"categories": ${categoriesFilter}
                        ,"value": "${filterValue}"
                        }
                    </c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="filter"></c:set>
                </c:otherwise>
            </c:choose>
            <%
                String filter = "" + pageContext.getAttribute("filter");
                if (StringUtils.isNotBlank(filter)) {
                    filters.add(filter);
                }
            %>
        </c:forEach>


        <%--<c:if test="${value.DropDownForm!=null && value.DropDownForm == 'true' }">
            <div class="filterheader clearfix">
					 <span class="title-element">
						<c:if test="${value.TitleForm.isSet }">${value.TitleForm }</c:if>
						<c:if test="${!value.TitleForm.isSet }"><fmt:message key="formatter.search.form.title"/></c:if>
					 </span>
                <a class="pull-right" role="button" data-toggle="collapse" href="#filters-${value.Id }"
                   aria-expanded="false" aria-controls="filters-${value.Id }"><span
                        class="filter-link-text"><fmt:message key="formatter.filter.expand"/></span>&nbsp;<span
                        class="fa fa-angle-down" aria-hidden="true"></span></a>
            </div>
        </c:if>
        <c:if test="${value.DropDownForm==null || value.DropDownForm == 'false' }">
            <c:if test="${value.TitleForm.isSet }">
                <div class="filterheader clearfix">
                    <h3 class="title-element">${value.TitleForm }</h3>
                </div>
            </c:if>
        </c:if>--%>
        <%--<div class="filterbox-content <c:if test="${value.DropDownForm!=null && value.DropDownForm == 'true' }">collapse</c:if> row"
             id="filters-${value.Id }">
            <c:set var="contField" value="0"/>
            <c:set var="filtercssclass">col-sm-12</c:set>
            <c:set var="btnblockcssclass">col-sm-12</c:set>
            <c:if test="${value.ButtonBlockCssClass.isSet}">
                <c:set var="btnblockcssclass">${value.ButtonBlockCssClass}</c:set>
            </c:if>
            <c:forEach var="elem" items="${content.subValueList['Filter']}" varStatus="status">
                <c:if test="${elem.value.CssClass.isSet}">
                    <c:set var="filtercssclass">${elem.value.CssClass}</c:set>
                </c:if>
                <c:set var="fieldName">${value.Id }field-${status.count}</c:set>
                <%
                    String fieldName = "" + pageContext.getAttribute("fieldName");
                %>

                <c:choose>
                    <c:when test="${elem.name == 'TextFilter' and elem.value.Principal != 'true'}">
                        <%
                            String fieldValue = request.getParameter(fieldName);
                            if (fieldValue == null || fieldValue.length() == 0) {
                                String paramQueryGenerico = request.getParameter("query");
                                if (paramQueryGenerico == null || paramQueryGenerico.length() == 0)
                                    fieldValue = "";
                                else {
                                    fieldValue = paramQueryGenerico;
                                }
                            }
                            //										fieldValue = CmsStringUtil.escapeHtml(fieldValue);//.replaceAll("&quot;", "\"");
                            pageContext.setAttribute("fieldValue", fieldValue);
                        %>
                        <div class="form-group filter filter-text ${filtercssclass}"
                             id="filter-${status.count}">
                            <c:if test="${elem.value.Label.isSet}">
                                <label for="${fieldName}">${elem.value.Label }</label>
                            </c:if>
                            <c:if test="${!elem.value.Label.isSet}">
                                <label for="${fieldName}" class="sr-only">${elem.value.Placeholder }</label>
                            </c:if>
                            <input type="text" class="form-control" placeholder="${elem.value.Placeholder }"
                                   name="${fieldName}" id="${fieldName}"
                                   value="<c:out value='${fieldValue}'/>"/>
                        </div>
                        <c:set var="filtercssclass">col-sm-12</c:set>
                    </c:when>
                    <c:when test="${elem.name == 'Date1Filter'}">
                        <%
                            String fieldValue = request.getParameter(fieldName);
                            if (fieldValue == null)
                                fieldValue = "";
                            //										fieldValue = CmsStringUtil.escapeHtml(fieldValue);
                            pageContext.setAttribute("fieldValue", fieldValue);
                        %>
                        <div class="form-group ${filtercssclass} filter filter-date"
                             id="filter-${status.count}">
                            <c:if test="${elem.value.Label.isSet}">
                                <label for="${fieldName}">${elem.value.Label }</label>
                            </c:if>
                            <c:if test="${!elem.value.Label.isSet}">
                                <label for="${fieldName}" class="sr-only">${fieldName}</label>
                            </c:if>
                            <input type="text" name="${fieldName}" value="<c:out value='${fieldValue}'/>"
                                   id="${fieldName}" class="form-control datepicker"/>
                        </div>
                        <c:set var="filtercssclass">col-sm-12</c:set>
                    </c:when>
                    <c:when test="${elem.name == 'Date2Filter'}">
                        <%
                            String fieldValue1 = request.getParameter(fieldName + "_d1");
                            String fieldValue2 = request.getParameter(fieldName + "_d2");
                            if (fieldValue1 == null)
                                fieldValue1 = "";
                            if (fieldValue2 == null)
                                fieldValue2 = "";
                            if (fieldValue1.indexOf("NOW") > -1) {
                                //Si viene el parametro de control NOW lo cambiamos por la fecha de hoy en formato para el usuario
                                Date dateAux = new Date();
                                if (!fieldValue1.equals("NOW")) {
                                    String dateStrAux = fieldValue1.replace("NOW", "");
                                    dateStrAux = dateStrAux.replace("now", "");
                                    DateMathParser parser = new DateMathParser();
                                    dateAux = parser.parseMath(dateStrAux);
                                }
                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                fieldValue1 = df.format(dateAux);
                            }
                            if (fieldValue2.indexOf("NOW") > -1) {
                                //Si viene el parametro de control NOW lo cambiamos por la fecha de hoy en formato para el usuario
                                Date dateAux = new Date();
                                if (!fieldValue1.equals("NOW")) {
                                    String dateStrAux = fieldValue2.replace("NOW", "");
                                    dateStrAux = dateStrAux.replace("now", "");
                                    DateMathParser parser = new DateMathParser();
                                    dateAux = parser.parseMath(dateStrAux);
                                }
                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                fieldValue2 = df.format(dateAux);
                            }
                            //										fieldValue1 = CmsStringUtil.escapeHtml(fieldValue1);
                            //										fieldValue2 = CmsStringUtil.escapeHtml(fieldValue2);
                            pageContext.setAttribute("fieldValue1", fieldValue1);
                            pageContext.setAttribute("fieldValue2", fieldValue2);
                        %>
                        <div class="form-group filter filter-date filter-date-multiple form-inline ${filtercssclass }">
                            <div class="row">
                                <div class="form-group col-xs-6" id="filter-${status.count}">
                                    <c:if test="${elem.value.LabelDate1.isSet}">
                                        <label for="${fieldName}_d1">${elem.value.LabelDate1 }</label>
                                    </c:if>
                                    <c:if test="${!elem.value.LabelDate1.isSet}">
                                        <label for="${fieldName}_d1" class="sr-only">${fieldName}_d1</label>
                                    </c:if>
                                    <input type="text" name="${fieldName}_d1"
                                           value="<c:out value='${fieldValue1}'/>" id="${fieldName}_d1"
                                           class="form-control datepicker datepicker-1"/>
                                </div>
                                <div class="form-group col-xs-6" id="filter-${status.count}">
                                    <c:if test="${elem.value.LabelDate2.isSet}">
                                        <label for="${fieldName}_d2">${elem.value.LabelDate2 }</label>
                                    </c:if>
                                    <c:if test="${!elem.value.LabelDate2.isSet}">
                                        <label for="${fieldName}_d2" class="sr-only">${fieldName}_d2</label>
                                    </c:if>
                                    <input type="text" name="${fieldName}_d2"
                                           value="<c:out value='${fieldValue2}'/>" id="${fieldName}_d2"
                                           class="form-control datepicker datepicker-2"/>
                                </div>
                            </div>
                        </div>
                        <c:set var="filtercssclass">col-sm-12</c:set>
                    </c:when>
                    <c:when test="${elem.name == 'CategoryFilter'}">
                        <c:set var="CategoryRoot" value="${elem.value.CategoryRoot}"/>
                        <c:set var="TreeFolder" value="${elem.value.TreeFolder}"/>
                        <c:set var="ShowParent" value="${elem.value.ShowParent}"/>
                        <c:set var="LabelCategoryFilterAll" value="${value.LabelCategoryFilterAll}"/>

                        <%
                            //Cargamos del contexto
                            String categoryRoot = "" + pageContext.getAttribute("CategoryRoot");
                            String completeCategoryPath = categoryRoot;
                            String treeFolder = "" + pageContext.getAttribute("TreeFolder");
                            String showParent = "" + pageContext.getAttribute("ShowParent");
                            String labelCategoryFilterAll = "" + pageContext.getAttribute("LabelCategoryFilterAll");
                            if (categoryRoot.startsWith("/system/categories/")) {
                                categoryRoot = categoryRoot.replace("/system/categories/", "");
                            } else if (categoryRoot.indexOf("/.categories/") > -1) {
                                categoryRoot = categoryRoot.substring(categoryRoot.indexOf(".categories/") + ".categories/".length(), categoryRoot.length());
                            }

                            boolean includeSubCats = true;
                            if (treeFolder.equals("false"))
                                includeSubCats = false;

                            //Leemos las categorias
                            CmsCategoryService categoryService = new CmsCategoryService();
                            List<CmsCategory> categorias = categoryService.readCategories(cmso, categoryRoot, includeSubCats, completeCategoryPath);

                            //Contamos el nivel actual para luego pintar la tabulacion en caso de ser necesario
                            Integer nivelOrigen = categoryRoot.split("/").length;

                            //Sacamos el titulo de la opcion de categoria padre
                            String tituloCatPadre = null;
                            if (showParent != null && showParent.equals("blank")) {
                                tituloCatPadre = "";
                            } else if (showParent != null && showParent.equals("all")) {
                                tituloCatPadre = labelCategoryFilterAll;
                            } else if (showParent != null && showParent.equals("title")) {
                                CmsCategory c = categoryService.readCategory(cmso, categoryRoot, completeCategoryPath);

                                //Primero intentamos leer la categoria Title_LOCALE, si no nos quedamos con el Title
                                try {
                                    String site = OpenCms.getSiteManager().getCurrentSite(cmso).getSiteRoot();
                                    String pathResourceCat = c.getRootPath();
                                    pathResourceCat = pathResourceCat.replace(site, "");
                                    tituloCatPadre = cmso.readPropertyObject(pathResourceCat, "Title_" + cmso.getRequestContext().getLocale(), false).getValue(c.getTitle());
                                } catch (CmsException ex) {
                                    tituloCatPadre = c.getTitle();
                                }
                            }
                        %>
                        <div class="form-group filter filter-cat ${filtercssclass }"
                             id="filter-${status.count}">
                            <c:if test="${elem.value.Label.isSet}">
                                <label for="${fieldName}">${elem.value.Label }</label>
                            </c:if>
                            <c:if test="${!elem.value.Label.isSet}">
                                <label for="${fieldName}" class="sr-only">${fieldName}</label>
                            </c:if>
                            <c:choose>
                                <c:when test="${elem.value.FieldType == 'select' }">
                                    <div class="block-drop">
                                        <select class="form-control" name="${fieldName}" id="${fieldName}">
                                            <c:if test="${!elem.value.ShowEmptyOption.exists or elem.value.ShowEmptyOption=='true'}">
                                                <option value="" class="nivel-0"></option>
                                            </c:if>
                                            <%
                                                String fieldValue = request.getParameter(fieldName);
                                                // si nos llega el parametro vacio guardamos la propiedad de categoria definida (si es que existe) como fieldvalue para que se muestre seleccionada
                                                if (StringUtils.isBlank(fieldValue)) {
                                                    fieldValue = (String) request.getAttribute("filtercategoryProperty");
                                                }
                                                if (fieldValue == null)
                                                    fieldValue = "";
                                                if (tituloCatPadre != null) {
                                                    if (fieldValue.equals(categoryRoot))
                                                        out.println("<option class=\"nivel-0\" value=\"" + categoryRoot + "\" selected=\"selected\">" + tituloCatPadre + "</option>");
                                                    else
                                                        out.println("<option class=\"nivel-0\" value=\"" + categoryRoot + "\">" + tituloCatPadre + "</option>");
                                                }
                                                for (CmsCategory cat : categorias) {
                                                    String titleCat = "";
                                                    //Primero intentamos leer la categoria Title_LOCALE, si no nos quedamos con el Title
                                                    try {
                                                        String site = OpenCms.getSiteManager().getCurrentSite(cmso).getSiteRoot();
                                                        String pathResourceCat = cat.getRootPath();
                                                        pathResourceCat = pathResourceCat.replace(site, "");
                                                        titleCat = cmso.readPropertyObject(pathResourceCat, "Title_" + cmso.getRequestContext().getLocale(), false).getValue(cat.getTitle());
                                                    } catch (CmsException ex) {
                                                        titleCat = cat.getTitle();
                                                    }
                                                    //Si hay subniveles, tenemos que pintarlo en forma de arbol calculando las tabulaciones
                                                    if (includeSubCats) {
                                                        Integer nivelActual = cat.getPath().split("/").length;
                                                        Integer numTabulacion = nivelActual - nivelOrigen;
                                                        String tab = "";
                                                        for (int i = 0; i < numTabulacion; i++)
                                                            tab += "&nbsp;&nbsp;&nbsp;";
                                                        if (fieldValue.equals(cat.getPath()))
                                                            out.println("<option class=\"nivel-" + numTabulacion + "\" value=\"" + cat.getPath() + "\" selected=\"selected\">" + tab + titleCat + "</option>");
                                                        else
                                                            out.println("<option class=\"nivel-" + numTabulacion + "\" value=\"" + cat.getPath() + "\">" + tab + titleCat + "</option>");
                                                    } else {
                                                        if (fieldValue.equals(cat.getPath()))
                                                            out.println("<option value=\"" + cat.getPath() + "\" selected=\"selected\">" + titleCat + "</option>");
                                                        else
                                                            out.println("<option value=\"" + cat.getPath() + "\">" + titleCat + "</option>");
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>
                                </c:when>
                                <c:when test="${elem.value.FieldType == 'selectmultiple' }">
                                    <div class="block-drop">
                                        <select class="form-control" name="${fieldName}" id="${fieldName}"
                                                multiple>
                                            <%
                                                String[] fieldValuesArray = request.getParameterValues(fieldName);
                                                List<String> fieldValues = null;
                                                String categoryProperty = (String) request.getAttribute("filtercategoryProperty");

                                                if (fieldValuesArray != null)
                                                    fieldValues = Arrays.asList(fieldValuesArray);
                                                    // si nos llega el parametro vacio guardamos la propiedad de categoria definida (si es que existe) como fieldvalue para que se muestre seleccionada
                                                else if (!StringUtils.isBlank(categoryProperty)) {
                                                    fieldValues = new ArrayList<String>();
                                                    fieldValues.add(categoryProperty);
                                                } else
                                                    fieldValues = new ArrayList<String>();
                                                if (tituloCatPadre != null) {
                                                    if (fieldValues.contains(categoryRoot))
                                                        out.println("<option value=\"" + categoryRoot + "\" selected=\"selected\">" + tituloCatPadre + "</option>");
                                                    else
                                                        out.println("<option value=\"" + categoryRoot + "\">" + tituloCatPadre + "</option>");
                                                }
                                                for (CmsCategory cat : categorias) {
                                                    String titleCat = "";
                                                    //Primero intentamos leer la categoria Title_LOCALE, si no nos quedamos con el Title
                                                    try {
                                                        String site = OpenCms.getSiteManager().getCurrentSite(cmso).getSiteRoot();
                                                        String pathResourceCat = cat.getRootPath();
                                                        pathResourceCat = pathResourceCat.replace(site, "");
                                                        titleCat = cmso.readPropertyObject(pathResourceCat, "Title_" + cmso.getRequestContext().getLocale(), false).getValue(cat.getTitle());
                                                    } catch (CmsException ex) {
                                                        titleCat = cat.getTitle();
                                                    }
                                                    if (fieldValues.contains(cat.getPath()))
                                                        out.println("<option value=\"" + cat.getPath() + "\" selected=\"selected\">" + titleCat + "</option>");
                                                    else
                                                        out.println("<option value=\"" + cat.getPath() + "\">" + titleCat + "</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                </c:when>
                                <c:when test="${elem.value.FieldType == 'radio' }">
                                    <div class="form-group" id="filter-${status.count}">
                                        <div class="radio"><label for="${fieldName}"><input type="radio"
                                                                                            name="${fieldName}"
                                                                                            id="${fieldName}"
                                                                                            value=""
                                                                                            checked="checked"><fmt:message
                                                key="formatter.filter.nocategory"/></label></div>
                                        <%
                                            String fieldValue = request.getParameter(fieldName);
                                            // si nos llega el parametro vacio guardamos la propiedad de categoria definida (si es que existe) como fieldvalue para que se muestre seleccionada
                                            if (StringUtils.isBlank(fieldValue)) {
                                                fieldValue = (String) request.getAttribute("filtercategoryProperty");
                                            }
                                            if (fieldValue == null)
                                                fieldValue = "";
                                            if (tituloCatPadre != null) {
                                                if (fieldValue.equals(categoryRoot))
                                                    out.println("<div class=\"radio\"><label for=\"" + fieldName + "\"><input type=\"radio\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + categoryRoot + "\" checked=\"checked\">" + tituloCatPadre + "</label></div>");
                                                else
                                                    out.println("<div class=\"radio\"><label for=\"" + fieldName + "\"><input type=\"radio\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + categoryRoot + "\">" + tituloCatPadre + "</label></div>");
                                            }
                                            for (CmsCategory cat : categorias) {
                                                String titleCat = "";
                                                //Primero intentamos leer la categoria Title_LOCALE, si no nos quedamos con el Title
                                                try {
                                                    String site = OpenCms.getSiteManager().getCurrentSite(cmso).getSiteRoot();
                                                    String pathResourceCat = cat.getRootPath();
                                                    pathResourceCat = pathResourceCat.replace(site, "");
                                                    titleCat = cmso.readPropertyObject(pathResourceCat, "Title_" + cmso.getRequestContext().getLocale(), false).getValue(cat.getTitle());
                                                } catch (CmsException ex) {
                                                    titleCat = cat.getTitle();
                                                }
                                                if (fieldValue.equals(cat.getPath()))
                                                    out.println("<div class=\"radio\"><label for=\"" + fieldName + "\"><input type=\"radio\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + cat.getPath() + "\" checked=\"checked\">" + titleCat + "</label></div>");
                                                else
                                                    out.println("<div class=\"radio\"><label for=\"" + fieldName + "\"><input type=\"radio\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + cat.getPath() + "\">" + titleCat + "</label></div>");
                                            }
                                        %>
                                    </div>
                                </c:when>
                                <c:when test="${elem.value.FieldType == 'checkbox' }">
                                    <div class="form-group" id="filter-${status.count}">
                                        <c:set var="fieldName" value="${value.Id }field-${status.count}"/>
                                        <%
                                            String[] fieldValuesArray = request.getParameterValues(fieldName);
                                            List<String> fieldValues = null;
                                            // si nos llega el parametro vacio guardamos la propiedad de categoria definida (si es que existe) como fieldvalue para que se muestre seleccionada
                                            String categoryProperty = (String) request.getAttribute("filtercategoryProperty");

                                            if (fieldValuesArray != null)
                                                fieldValues = Arrays.asList(fieldValuesArray);
                                            else if (!StringUtils.isBlank(categoryProperty)) {
                                                fieldValues = new ArrayList<String>();
                                                fieldValues.add(categoryProperty);
                                            } else
                                                fieldValues = new ArrayList<String>();
                                            if (tituloCatPadre != null) {
                                                if (fieldValues.contains(categoryRoot))
                                                    out.println("<div class=\"checkbox\"><label for=\"" + fieldName + "\"><input type=\"checkbox\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + categoryRoot + "\" checked=\"checked\">" + tituloCatPadre + "</label></div>");
                                                else
                                                    out.println("<div class=\"checkbox\"><label for=\"" + fieldName + "\"><input type=\"checkbox\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + categoryRoot + "\">" + tituloCatPadre + "</label></div>");
                                            }
                                            for (CmsCategory cat : categorias) {
                                                String titleCat = "";
                                                //Primero intentamos leer la categoria Title_LOCALE, si no nos quedamos con el Title
                                                try {
                                                    String site = OpenCms.getSiteManager().getCurrentSite(cmso).getSiteRoot();
                                                    String pathResourceCat = cat.getRootPath();
                                                    pathResourceCat = pathResourceCat.replace(site, "");
                                                    titleCat = cmso.readPropertyObject(pathResourceCat, "Title_" + cmso.getRequestContext().getLocale(), false).getValue(cat.getTitle());
                                                } catch (CmsException ex) {
                                                    titleCat = cat.getTitle();
                                                }
                                                if (fieldValues.contains(cat.getPath()))
                                                    out.println("<div class=\"checkbox\"><label for=\"" + fieldName + "\"><input type=\"checkbox\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + cat.getPath() + "\" checked=\"checked\">" + titleCat + "</label></div>");
                                                else
                                                    out.println("<div class=\"checkbox\"><label for=\"" + fieldName + "\"><input type=\"checkbox\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + cat.getPath() + "\">" + titleCat + "</label></div>");
                                            }
                                        %>
                                    </div>
                                </c:when>
                            </c:choose>
                        </div>
                        <c:set var="filtercssclass">col-sm-12</c:set>
                    </c:when>
                    <c:when test="${elem.name == 'ResourceTypeFilter'}">
                        <div class="form-group filter filter-resource ${filtercssclass }"
                             id="filter-${status.count}">
                            <c:if test="${elem.value.Label.isSet}">
                                <label for="${fieldName}">${elem.value.Label }</label>
                            </c:if>
                            <c:if test="${!elem.value.Label.isSet}">
                                <label for="${fieldName}" class="sr-only">${fieldName}</label>
                            </c:if>
                            <c:choose>
                                <c:when test="${elem.value.FieldType == 'select' }">
                                    <%
                                        String fieldValue = request.getParameter(fieldName);
                                        // si nos llega el parametro vacio guardamos la propiedad de categoria definida (si es que existe) como fieldvalue para que se muestre seleccionada
                                        if (StringUtils.isBlank(fieldValue)) {
                                            fieldValue = (String) request.getAttribute("settingresourcetype");
                                        }
                                        if (fieldValue == null)
                                            fieldValue = "";
                                        pageContext.setAttribute("fieldValue", fieldValue);
                                    %>
                                    <div class="block-drop">
                                        <select name="${fieldName}" id="${fieldName}" class="form-control">
                                            <c:forEach items="${elem.valueList.Option }" var="r">
                                                <option value="${r.value.Value }"
                                                        <c:if test="${r.value.Value==fieldValue }">selected="selected"</c:if>>${r.value.Label }</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </c:when>
                                <c:when test="${elem.value.FieldType == 'selectmultiple' }">
                                    <%
                                        String[] fieldValuesArray = request.getParameterValues(fieldName);
                                        List<String> fieldValues = null;
                                        // si nos llega el parametro vacio guardamos el setting se tipo de recurso (si es que existe) como fieldvalue para que se muestre seleccionado
                                        String filtersettingresourcetype = (String) request.getAttribute("settingresourcetype");

                                        if (fieldValuesArray != null)
                                            fieldValues = Arrays.asList(fieldValuesArray);

                                        else if (!StringUtils.isBlank(filtersettingresourcetype)) {
                                            fieldValues = new ArrayList<String>();
                                            fieldValues.add(filtersettingresourcetype);
                                        } else
                                            fieldValues = new ArrayList<String>();
                                        pageContext.setAttribute("fieldValues", fieldValues);
                                    %>
                                    <div class="block-drop">
                                        <select class="form-control" name="${fieldName}" id="${fieldName}"
                                                multiple>
                                            <c:forEach items="${elem.valueList.Option }" var="r">
                                                <option value="${r.value.Value }"
                                                        <c:if test="${fn:contains( fieldValues, r.value.Value ) }">selected="selected"</c:if>>${r.value.Label }</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </c:when>
                                <c:when test="${elem.value.FieldType == 'radio' }">
                                    <%
                                        String fieldValue = request.getParameter(fieldName);
                                        // si nos llega el parametro vacio guardamos el setting se tipo de recurso (si es que existe) como fieldvalue para que se muestre seleccionado
                                        if (StringUtils.isBlank(fieldValue)) {
                                            fieldValue = (String) request.getAttribute("settingresourcetype");
                                        }
                                        if (fieldValue == null)
                                            fieldValue = "";
                                        pageContext.setAttribute("fieldValue", fieldValue);
                                    %>
                                    <c:forEach items="${elem.valueList.Option }" var="r">
                                        <div class="radio"><label for="${fieldName }"><input type="radio"
                                                                                             id="${fieldName}"
                                                                                             name="${fieldName}"
                                                                                             value="${r.value.Value }"
                                                                                             <c:if test="${r.value.Value==fieldValue }">checked="checked"</c:if>>${r.value.Label }
                                        </label></div>
                                    </c:forEach>
                                </c:when>
                                <c:when test="${elem.value.FieldType == 'checkbox' }">
                                    <%
                                        String[] fieldValuesArray = request.getParameterValues(fieldName);
                                        List<String> fieldValues = null;
                                        // si nos llega el parametro vacio guardamos el setting se tipo de recurso (si es que existe) como fieldvalue para que se muestre seleccionado
                                        String filtersettingresourcetype = (String) request.getAttribute("settingresourcetype");
                                        if (fieldValuesArray != null)
                                            fieldValues = Arrays.asList(fieldValuesArray);
                                        else if (!StringUtils.isBlank(filtersettingresourcetype)) {
                                            fieldValues = new ArrayList<String>();
                                            fieldValues.add(filtersettingresourcetype);
                                        } else
                                            fieldValues = new ArrayList<String>();
                                        pageContext.setAttribute("fieldValues", fieldValues);
                                    %>
                                    <div class="form-group">
                                        <c:forEach items="${elem.valueList.Option }" var="r">
                                            <div class="checkbox"><label for="${fieldName }"><input
                                                    type="checkbox" id="${fieldName}" name="${fieldName}"
                                                    value="${r.value.Value }"
                                                    <c:if test="${fn:contains( fieldValues, r.value.Value ) }">checked="checked"</c:if>>${r.value.Label }
                                            </label></div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                            </c:choose>
                        </div>
                        <c:set var="filtercssclass">col-sm-12</c:set>
                    </c:when>
                    <c:when test="${elem.name == 'SiteFilter'}">
                        <div class="form-group filter filter-resource ${filtercssclass }"
                             id="filter-${status.count}">
                            <c:if test="${elem.value.Label.isSet}">
                                <label for="${fieldName}">${elem.value.Label }</label>
                            </c:if>
                            <c:if test="${!elem.value.Label.isSet}">
                                <label for="${fieldName}" class="sr-only">${fieldName}</label>
                            </c:if>
                            <%
                                String[] fieldValuesArray = request.getParameterValues(fieldName);
                                List<String> fieldValues = null;
                                if (fieldValuesArray != null) {
                                    fieldValues = Arrays.asList(fieldValuesArray);
                                } else {
                                    String currentSite = (String) request.getAttribute("currentSite");
                                    fieldValues = new ArrayList<String>();
                                    fieldValues.add(currentSite);
                                }
                                pageContext.setAttribute("fieldValues", fieldValues);
                            %>
                            <div class="form-group">

                                <c:if test="${elem.value.ShowAll.isSet &&  elem.value.ShowAll=='true'}">

                                    &lt;%&ndash; Mostrar todos los sites menos el raiz y el shared configurado en OpenCms &ndash;%&gt;
                                    <%
                                        List<CmsSite> sites = OpenCms.getSiteManager().getAvailableSites(cmso, true);
                                        if (sites != null) {
                                            for (CmsSite s : sites) {
                                                //Si es el raiz o la compartida no lo aniadimos a la lista
                                                if (!s.getSiteRoot().equals("") && !s.getSiteRoot().equals("/") && !s.getSiteRoot().equals("/shared")) {
                                                    pageContext.setAttribute("osite", s);
                                    %>
                                    <div class="checkbox"><label for="${fieldName}"><input type="checkbox"
                                                                                           id="${fieldName}"
                                                                                           name="${fieldName}"
                                                                                           value="${osite.siteRoot}"
                                                                                           <c:if test="${fn:contains( fieldValues, osite.siteRoot ) }">checked="checked"</c:if>>${osite.title }
                                    </label></div>
                                    <%
                                                }
                                            }
                                        }
                                    %>
                                </c:if>

                                <c:if test="${!elem.value.ShowAll.isSet ||  elem.value.ShowAll=='false'}">
                                    <%
                                        List<CmsSite> sites = OpenCms.getSiteManager().getAvailableSites(cmso, true);
                                        Map<String, String> mapSites = new HashMap<String, String>();
                                        if (sites != null) {
                                            for (CmsSite s : sites) {
                                                mapSites.put(s.getSiteRoot(), s.getTitle());
                                            }
                                        }
                                    %>
                                    <c:forEach items="${elem.valueList.Site }" var="rootSite">
                                        <%
                                            CmsJspContentAccessValueWrapper s = (CmsJspContentAccessValueWrapper) pageContext.getAttribute("rootSite");
                                            if (s != null) {
                                                String title = mapSites.get(s.getStringValue());
                                                pageContext.setAttribute("titleSite", title);
                                        %>
                                        <div class="checkbox"><label for="${fieldName}"><input type="checkbox"
                                                                                               id="${fieldName}"
                                                                                               name="${fieldName}"
                                                                                               value="${rootSite}"
                                                                                               <c:if test="${fn:contains( fieldValues, rootSite ) }">checked="checked"</c:if>>${titleSite }
                                        </label></div>
                                        <%
                                            }
                                        %>
                                    </c:forEach>

                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${elem.name == 'SelectFilter'}">
                        <%
                            String value = request.getParameter(fieldName);
                            if (value == null)
                                value = "";
                            pageContext.setAttribute("fieldValue", value);
                        %>
                        <div class="form-group filter filter-resource ${filtercssclass }"
                             id="filter-${status.count}">
                            <div class="form-group">
                                <label for="${fieldName}">${elem.value.Label }</label>

                                <div class="block-drop">
                                    <select class="form-control" name="${fieldName}" id="${fieldName}">
                                        <c:if test="${!elem.value.ShowEmptyOption.exists or elem.value.ShowEmptyOption=='true'}">
                                            <option value="" class="nivel-0"></option>
                                        </c:if>
                                        <c:set var="options" value="${fn:split(elem.value.Options,'|')}"/>
                                        <c:if test="${not empty options}">
                                            <c:forEach var="optionOrigin" items="${options}">
                                                <c:set var="optionLabelValue"
                                                       value="${fn:split(optionOrigin,':' )}"/>
                                                <c:set var="valueOption"></c:set>
                                                <c:set var="labelOption"></c:set>
                                                <c:if test="${not empty optionLabelValue and fn:length(optionLabelValue)>1}">
                                                    <c:set var="valueOption">${fn:trim(optionLabelValue[0])}</c:set>
                                                    <c:set var="labelOption">${fn:trim(optionLabelValue[1])}</c:set>
                                                </c:if>
                                                <c:if test="${not empty optionLabelValue and fn:length(optionLabelValue)==1}">
                                                    <c:set var="valueOption">${fn:trim(optionLabelValue[0])}</c:set>
                                                    <c:set var="labelOption">${fn:trim(optionLabelValue[0])}</c:set>
                                                </c:if>
                                                <c:set var="checkedOption"></c:set>
                                                <c:if test="${fieldValue==valueOption}">
                                                    <c:set var="checkedOption">selected="selected"</c:set>
                                                </c:if>
                                                <option value="${valueOption}" ${checkedOption}>${labelOption}</option>
                                            </c:forEach>
                                        </c:if>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <c:set var="filtercssclass">col-sm-12</c:set>
                    </c:when>
                    <c:when test="${elem.name == 'CustomFilter'}">
                        <c:set var="fieldName" scope="request">${fieldName}</c:set>
                        <c:set var="filtercssclass" scope="request">col-sm-12</c:set>
                        <c:if test="${elem.value.CssClass.isSet}">
                            <c:set var="filtercssclass" scope="request">${elem.value.CssClass}</c:set>
                        </c:if>
                        <cms:include file="${elem}"/>
                    </c:when>
                </c:choose>
                <c:set var="contField" value="${contField+1 }"/>
            </c:forEach>
            <input type="hidden" name="numfield" value="${contField }"/>
            <input type="hidden" name="searchaction" value="search"/>
            <input type="hidden" id="${value.Id }-searchPage" name="searchPage" value="1"/>
            <c:if test="${elem.value.Principal == 'false'}">
                <div class="botones ${btnblockcssclass}">
                    <button type="submit" name="submit" id="${value.Id }-submit" value="${value.ButtonText }"
                            class="btn ${value.ButtonClass }">
                            ${value.ButtonText }
                    </button>
                </div>
            </c:if>
            <c:if test="${searchButtonPrint==null || not searchButtonPrint}">
                <div class="botones ${btnblockcssclass}">
                    <button type="submit" name="submit" id="${value.Id }-submit" value="${value.ButtonText }"
                            class="ts-botontxt_buscar btn ${value.ButtonClass }">
                            ${value.ButtonText }
                    </button>
                </div>
            </c:if>
        </div>
        </div>
        </form>

        <script>
            <c:set var="dplocale">${cms.locale}</c:set>
            <c:if test="${dplocale == 'en'}">
            <c:set var="dplocale">en - GB
            </c:set>
            </c:if>
            // accede al archivo con los locales: /system/modules/com.saga.sagasuite.core.script/resources/jquery-ui/1.10.3/js/jquery.ui.datepicker.locales.js
            $(document).ready(function () {
                $(".datepicker").datepicker($.datepicker.regional["${dplocale}"]);
            });
        </script>
        </div>
        <c:set var="filtercssclass"></c:set>--%>
    </c:if>

    <%
        request.setAttribute("filters", filters);
    %>
</cms:bundle>