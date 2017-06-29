<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Genera CmsXmlContent del congreso" %>

<%@ tag import="com.saga.fecacongreso.dao.GenericDao" %>
<%@ tag import="com.saga.fecacongreso.model.Congreso" %>
<%@ tag import="com.saga.fecacongreso.services.CongresoService" %>
<%@ tag import="com.saga.opencms.hibernate.DaoException" %>
<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.file.CmsFile" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.file.CmsResource" %>
<%@ tag import="org.opencms.flex.CmsFlexController" %>
<%@ tag import="org.opencms.main.CmsException" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="org.opencms.main.CmsMultiException" %>
<%@ tag import="org.opencms.util.CmsUUID" %>
<%@ tag import="org.opencms.xml.content.CmsXmlContent" %>
<%@ tag import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ tag import="java.io.PrintWriter" %>
<%@ tag import="java.io.StringWriter" %>
<%@ tag import="java.util.*" %>

<%@ attribute name="idCongreso" type="java.lang.String" required="true" rtexprvalue="true"
             description="Id de base de datos del congreso" %>

<%--
- idCongreso (Obligatorio): '38'
- fields (Obligatorio): 'TipoComunicacion/Campo1/Contar'
- locale (Opcional): 'es'
--%>

<%!
    Log LOG = CmsLog.getLog("TAG_CONGRESO");

    List<String> errors;
    GenericDao dao;
    Congreso congreso;
    CongresoService congresoService;
    CmsObject cmso;

    public void init(HttpServletRequest request) throws DaoException {
        loadDao();
        loadCongresoService();
        cmso = CmsFlexController.getCmsObject(request);
    }

    public void loadCongresoService(){
        congresoService = new CongresoService();
    }

    public void loadDao() throws DaoException {
        dao = new GenericDao();
        dao.iniciaTransaccion();
    }

    public String loadCongresoUUID(String idCongreso) throws Exception {
        String uuid = null;
        // Obtenemos el idcongreso
        Long idCongresoLong = Long.parseLong(idCongreso);
        // Leemos de la BBDD
        try {
            if (!idCongreso.equals("0")) {
                // Leemos el objeto congreso de la BBDD
                congreso = dao.findById(Congreso.class, idCongresoLong);
                uuid = congreso.getUuid();
            }
            // Metemos en el request el id para la siguiente navegacion de
            // pantalla
//                request.setAttribute("idCongreso", idCongresoLong);

        } catch (Exception e) {
            errors.add(getStackTraceAsString(e));
        }
        return uuid;
    }

    /**
     * Returns the stack trace (including the message) of an exception as a String.<p>
     *
     * If the exception is a CmsException,
     * also writes the root cause to the String.<p>
     *
     * @param e the exception to get the stack trace from
     * @return the stack trace of an exception as a String
     */
    public static String getStackTraceAsString(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        if (e instanceof CmsMultiException) {
            CmsMultiException me = (CmsMultiException)e;
            Iterator<CmsException> it = me.getExceptions().iterator();
            while (it.hasNext()) {
                Throwable t = it.next();
                t.printStackTrace(printWriter);
            }
        }
        return stringWriter.toString();
    }

    private boolean validateUUIDCongreso(String uuidCongreso) throws Exception {
        if (uuidCongreso == null || !CmsUUID.isValidUUID(uuidCongreso)) {
            throw new Exception("UUID congreso erroneo");
        }
        return true;
    }

    private boolean validateIdCongreso(String idCongreso) throws Exception {
        if (idCongreso == null || idCongreso.length() <= 0) {
            throw new Exception("Parametro id congreso erroneo");
        }
        return true;
    }

    private Locale initLocale(String locale) {
        Locale loc;
        if (locale == null){
            loc = cmso.getRequestContext().getLocale();
        } else {
            loc = new Locale(locale);
        }
        return loc;
    }
%>
<%
    Map<String, String> xmlValues = new HashMap<String, String>();
    try {
        init(request);

        // Validate db id
        if (validateIdCongreso(idCongreso)) {
            String uuidCongreso = loadCongresoUUID(idCongreso);
//            jspContext.setAttribute("uuidCongreso", uuidCongreso);
            // Validate uuid
            if (validateUUIDCongreso(uuidCongreso)) {
                CmsResource congreso = cmso.readResource(CmsUUID.valueOf(uuidCongreso));
                CmsFile file = cmso.readFile(congreso);
                CmsXmlContent xmlCongreso = CmsXmlContentFactory.unmarshal(cmso, file);
                request.setAttribute("xmlCongreso", xmlCongreso);
                /*Locale loc = initLocale(locale);
                for (int i = 0; i < fields.size(); i++) {
                    String field = (String)fields.get(i);
                    String stringValue = xmlCongreso.getStringValue(cmso, field, loc);
                    xmlValues.put(field, stringValue);
                }*/
            }
        }
    } catch (Exception e) {
        LOG.error(e);
    } finally {
        request.setAttribute("xmlValues", xmlValues);
        dao.cierraTransaccion();
    }
%>