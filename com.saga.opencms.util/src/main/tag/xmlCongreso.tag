<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Genera un xml con el contenido del congreso" %>


<%@ page import="com.saga.fecacongreso.Constantes" %>
<%@ page import="com.saga.fecacongreso.dao.GenericDao" %>
<%@ page import="com.saga.fecacongreso.model.Congreso" %>
<%@ page import="com.saga.fecacongreso.services.CongresoService" %>
<%@ page import="com.saga.opencms.hibernate.DaoException" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.i18n.CmsEncoder" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsMultiException" %>
<%@ page import="org.opencms.util.CmsUUID" %>
<%@ page import="org.opencms.xml.CmsXmlException" %>
<%@ page import="org.opencms.xml.content.CmsXmlContent" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>

<%@attribute name="idCongreso" type="java.lang.String" required="true" rtexprvalue="true"
             description="Query para ejecutar búsqueda en solr" %>

<%--
- idCongreso (Obligatorio): '38'
--%>

<%!


    String path;
    Locale locale;
    CmsFile file;
    String strContent;
    CmsObject cmso;
    CmsXmlContent xmlContent;

    List<String> errors;
    GenericDao dao;
    Congreso congreso;
    CongresoService congresoService;
    String uuidCongreso;

    public void init(HttpServletRequest request) throws DaoException {
        strContent = "";
        errors = new ArrayList<String>();
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

    /**
     * Initialize content.
     * The resource must be locked by user before change content.
     * Plain objects throw exception but strContent is readable.
     * @param cmso
     * @param path
     * @param locale
     */
    private void initContent(CmsObject cmso, String path, Locale locale)
            throws CmsException, UnsupportedEncodingException {
        this.cmso = cmso;
        this.path = path;
        this.locale = locale;

        //Leemos el recurso
        file = cmso.readFile(path, CmsResourceFilter.ALL);
        strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
        try {
            unmarshall(file);
        } catch (Exception e) {
            xmlContent = null;
        }
    }

    /**
     * Initialize content.
     * The resource must be locked by user before change content.
     * Plain objects have no xmlContent but strContent is still readable.
     * @param cmso
     * @param resource
     */
    public void initContent(CmsObject cmso, CmsResource resource)
            throws CmsException, UnsupportedEncodingException {
        initContent(cmso, cmso.getSitePath(resource), cmso.getRequestContext().getLocale());
    }


    /**
     * Unmarshall xml content from file
     * @param file
     * @return
     * @throws org.opencms.xml.CmsXmlException
     */
    public void unmarshall(CmsFile file) throws CmsXmlException {
        xmlContent = CmsXmlContentFactory.unmarshal(cmso, file);
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
%>
<%
    strContent = "";
    try {
        init(request);

        String idCongreso = request.getParameter(Constantes.PARAM_ID_CONGRESO);
        if (validateIdCongreso(idCongreso)) {
            String uuidCongreso = loadCongresoUUID(idCongreso);
            if (validateUUIDCongreso(uuidCongreso)) {
                CmsResource r = cmso.readResource(
                        new CmsUUID(uuidCongreso));
                initContent(cmso, r);
            }
        }
    } catch (Exception e) {
        errors.add(getStackTraceAsString(e));
    } finally {
        dao.cierraTransaccion();
    }
%>
<c:out value="<%=strContent%>" escapeXml="true" />