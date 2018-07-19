<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.json.JSONArray" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsLog" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%!
    private static final String CONTENT_TYPE = "text/html";
    private static final String ENCODING = "UTF-8";

    private static final String JRES_ERROR_MSG = "errorMsg";
    private static final String JRES_ERROR = "error";
    private static final String JRES_INPUTS = "inputs";
    private static final String JRES_FORM = "form";

    private static final String PARAM_URL_SUBMIT = "urlSubmit";
    private static final String PARAM_ENCRYPTION = "encryption";
    private static final String PARAM_PASS = "pass";
    private static final String PARAM_USER = "user";

    private static final String FORM_TARGET_BLANK = "_blank";
    private static final String FORM_METHOD_POST = "POST";
    private static final String FORM_ACTION = "action";
    private static final String FORM_TARGET = "target";
    private static final String FORM_METHOD = "method";

    private static final String INPUT_TEXT_VALUE = "text";
    private static final String INPUT_VALUE = "value";
    private static final String INPUT_NAME = "name";
    private static final String INPUT_TYPE = "type";

    private static final String MD5_USUARIO = "Usuario";
    private static final String MD5_USER_ENC = "HUsuario";
    private static final String MD5_PASS_ENC = "HPassword";

    final Log LOG = CmsLog.getLog(this.getClass());

    private boolean validate(String user, String pass, String encryption, String urlSubmit)
            throws Exception {
        if (StringUtils.isBlank(user)) {
            throw new Exception("user must not be empty");
        }
        if (StringUtils.isBlank(pass)) {
            throw new Exception("pass must not be empty");
        }
        if (StringUtils.isBlank(encryption)) {
            throw new Exception("encryption must not be empty");
        }
        if (StringUtils.isBlank(urlSubmit)) {
            throw new Exception("urlSubmit must not be empty");
        }
        return true;
    }


%>
<%
    String user = request.getParameter(PARAM_USER);
    String pass = request.getParameter(PARAM_PASS);
    String encryption = request.getParameter(PARAM_ENCRYPTION);
    String urlSubmit = request.getParameter(PARAM_URL_SUBMIT);

    JSONObject jRes = new JSONObject();
    try {
        if (validate(user, pass, encryption, urlSubmit)) {
            JSONObject form = new JSONObject();
            JSONArray inputs = new JSONArray();

            // form
            form.put(FORM_METHOD, FORM_METHOD_POST);
            form.put(FORM_TARGET, FORM_TARGET_BLANK);
            form.put(FORM_ACTION, urlSubmit);

            // inputs
            JSONObject jUserText = new JSONObject();
            jUserText.put(INPUT_TYPE, INPUT_TEXT_VALUE);
            jUserText.put(INPUT_NAME, MD5_USUARIO);
            jUserText.put(INPUT_VALUE, user);
            inputs.put(jUserText);

            JSONObject jUserEnc = new JSONObject();
            jUserEnc.put(INPUT_TYPE, INPUT_TEXT_VALUE);
            jUserEnc.put(INPUT_NAME, MD5_USER_ENC);
            jUserEnc.put(INPUT_VALUE, user);
            inputs.put(jUserEnc);

            JSONObject jPassEnc = new JSONObject();
            jPassEnc.put(INPUT_TYPE, INPUT_TEXT_VALUE);
            jPassEnc.put(INPUT_NAME, MD5_PASS_ENC);
            jPassEnc.put(INPUT_VALUE, pass);
            inputs.put(jPassEnc);

            jRes.put(JRES_FORM, form);
            jRes.put(JRES_INPUTS, inputs);
            jRes.put(JRES_ERROR, false);
        }


    } catch (Exception e) {
        LOG.error("error creando json", e);
        jRes.put(JRES_ERROR, true);
        jRes.put(JRES_ERROR_MSG, CmsException.getStackTraceAsString(e));
    } finally {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(ENCODING);
        response.getWriter().write(jRes.toString());
    }
%>

