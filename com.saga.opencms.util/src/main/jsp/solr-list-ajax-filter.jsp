<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@page taglibs="c,cms,fn,fmt" %>

<fmt:setLocale value="${cms.locale}" />

<cms:bundle basename="com.caprabo.mrmmccann.caprabochef.formatters.template">


    <%!
        public String fCheckFilterDietType(javax.servlet.http.HttpServletRequest request, String sParamValues, int val)
        {
            String res = "";
            String sVal = Integer.toString(val);
            if( request.getParameterValues(sParamValues) != null ){
                String[] pa=request.getParameterValues(sParamValues);
                for(int i=0; i<pa.length; i++){
                    if ( pa[i]!=null && !pa[i].trim().equals("") && pa[i].equals(sVal) ){
                        res = "checked";
                    }
                }
            }
            return res;
        }
    %>
    <div>

        <form class="form-group" action="<cms:link>/${cms.locale}/home/buscador-tags</cms:link>">
            <div class="row" id="search-area">
                <div class="col-xs-12 col-lg-4" id="search-title">
                    <h3 class="WS-Light" style="color: #005fab;"><b class="WS-SemiBold"><fmt:message key="BUSCADOR_BUSCA" /> </b> <%--<fmt:message key="BUSCADOR_TURECETA" /> --%> </h3>
                </div>
                <div class="col-xs-12 col-sm-8 col-md-8 col-lg-4">
                    <div class="form-inline" id="main-search">
                        <input name="q" id='search-page' placeholder="Buscar" class="buscador" style="outline-color: transparent !important;" value='<c:out value="${(empty param.q) ? '' : param.q}"/>'/>
                            <%--<input name="tag" id='search-page' placeholder="Buscar" class="buscador"--%>
                            <%--value='<c:out value="${(empty param.tag) ? '' : param.tag}"/>'--%>
                            <%--style="outline-color: transparent !important;"/>--%>
                        <input type="hidden" name="a" value="search"/>
                        <input type="hidden" id="searchPage" name="p" value="1";/>
                    </div>
                </div>

                    <%--7624: Cambios del Buscador de recetas: La busqueda debe ejecutarse en el filtro para obtener el numero de resultados --%>
                <cms:include file="%(link.strong:/system/modules/com.caprabo.mrmmccann.caprabochef.formatters/functions/buscador-tags-num-resultados.jsp)"/>
                <c:set var="varNumRecipes">${resultSize}</c:set>

                <div class="hidden-xs col-sm-4 col-md-4 col-lg-4" id='mix-sor-wrapper'>
                    <h6 class="pull-left hidden-sm hidden-md hidden-lg">${varNumRecipes}&nbsp;
                        <fmt:message key="BUSCADOR_RESULTADOS" />
                    </h6>
                </div>
            </div>
            <div class="row" id="mix-recetas">
                <div class="col-sm-8 col-md-8 col-lg-4 col-lg-offset-4">
                    <h6 class="pull-left hidden-xs">${varNumRecipes}&nbsp;
                        <fmt:message key="BUSCADOR_RESULTADOS" />
                    </h6>
                </div>
            </div>
            <div class="row" id="filters">
                <div class="col-xs-12 col-md-10 col-md-offset-1">
                    <button class="btn btn-blue" id='advanced-search-button' onclick="ga('send', 'event', '${cms.vfs.propertySearch[cms.requestContext.uri]['google.analytics.category']}', 'busqueda', 'buscar')">
                        <h6 class="WS-Regular" style="color: #fff;"><fmt:message key="BUSCADOR_APLICARFILTROS" /></h6>
                    </button>
                </div>
            </div>
        </form>
        <div class="divider"></div>
    </div>

</cms:bundle>