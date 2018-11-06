<%--Fuerza acceso al servidor de balanceo modificando la cookie--%>

<c:set var="forceServer"><cms:property name="sagasuite.force.server" file="search" default="false"/></c:set>
<c:if test='${not forceServer eq "false"}'>
    <%

        String forceServer = (String) pageContext.getAttribute("forceServer");

        // Creating the cookies object
        Cookie sRoute = new Cookie("ROUTEID", forceServer);


        // Setting expiry date of cookies = 24 Hr
        sRoute.setPath("/");


        // Adding cookies to the response header.
        response.addCookie(sRoute);

    %>
</c:if>