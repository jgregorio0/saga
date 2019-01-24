<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Tag que fuerza la URL absoluta añadiendo http" %>

<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="org.apache.commons.lang.StringUtils" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@attribute name="url" required="true" rtexprvalue="true" type="java.lang.String"
             description="URL externa" %>
<%@attribute name="isSecure" required="false" rtexprvalue="true" type="java.lang.Boolean"
             description="Forzar SSL" %>

<%!
    private final Log LOG = CmsLog.getLog(this.getClass());

    private final String HTTP = "http://";
    private final String HTTPS = "https://";
    private final String SEP = "//";

    private String absolutize(String url, Boolean isSecure){
        String absUrl = url;
        if (StringUtils.isNotBlank(absUrl)) {
            int iSep = absUrl.indexOf(SEP);
            if (iSep > 0) {
                absUrl = absUrl.substring(iSep + SEP.length());
            }

            if (isSecure == null) {
                absUrl = SEP + absUrl;
            } else if (isSecure) {
                absUrl = HTTPS + absUrl;
            } else {
                absUrl = HTTP + absUrl;
            }
        }
        return absUrl;
    }

%>
<c:out value="<%=absolutize(url, isSecure)%>"/>