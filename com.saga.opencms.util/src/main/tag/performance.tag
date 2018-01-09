<%--
<%@ taglib prefix="sg" tagdir="/WEB-INF/tags/core/templates" %>
<sg:performance begin="true"/>
[...]
<sg:performance begin="false" text="T-DEFAULT"/>

Se puede calcular tiempo de ejecución entre dos partes del código en cualquier JSP añadiendo con las tres líneas anteriores.
Funciona como una pila:
1- en begin=true añade el tiempo de ejecución actual
2- y en begin="false" obtiene el último tiempo de ejecución almacenado y calcula el tiempo total de ejecución restandolo al actual.
--%>

<%@ tag import="java.util.Stack" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Guarda el timemillis actual por id. En caso de existir el id se almacena como fin e imprime la diferencia del tiempo fin e inicial." %>

<%@attribute name="begin" type="java.lang.Boolean" required="true" rtexprvalue="true"
             description="Indica si se debe esperar a la siguiente llamada para mostrar el tiempo de performance" %>
<%@attribute name="text" type="java.lang.String" required="false" rtexprvalue="true"
             description="Texto adjunto al calculo del tiempo de duración" %>

<%!
    public static Stack<Long> performances;
//    final static String PROP_KEY = "sagasuite.performance";

    public static Stack<Long> instance() {
        if (performances == null) {
            performances = new Stack<Long>();
        }
        return performances;
    }
%>
<c:set var="showPerformance">${cms.vfs.propertySearch[cms.requestContext.uri]['sagasuite.performance']}</c:set>
<c:if test="${not empty showPerformance and showPerformance}">
    <%
        // init instance
        Stack<Long> performances = instance();

        // Check performance
        Double performance = null;
        long now = System.currentTimeMillis();
        if (begin) {
            performances.add(now);
        } else {
            performance = new Double(now - performances.pop()) / 1000;
        }
        jspContext.setAttribute("performance", performance);
        jspContext.setAttribute("text", text != null ? text + ": " : "");
    %>
    <%--If exists performance and property sagasuite.performance is true print performance--%>
    <c:if test="${not empty performance}">
        <p style="color:red;background:white">${text}${performance} s</p>
    </c:if>
</c:if>