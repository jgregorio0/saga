package com.saga.sagasuite.webform;

import com.alkacon.opencms.v8.formgenerator.CmsFormHandler;
import com.alkacon.opencms.v8.formgenerator.CmsWebformDefaultActionHandler;
import com.alkacon.opencms.v8.formgenerator.I_CmsField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.json.JSONObject;
import org.opencms.file.CmsObject;
import org.opencms.flex.CmsFlexController;
import org.opencms.main.CmsLog;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by jgregorio on 07/04/2016.
 */
public class RecaptchaValidationActionHandler extends CmsWebformDefaultActionHandler {

    private static final Log LOG = CmsLog.getLog(RecaptchaValidationActionHandler.class);

    private final String THEME_CONFIG_FILENAME = ".themeconfig";
    private final String THEME_CONFIG_RECAPTCHA_KEY = "ReCaptchaSecretKey";
    private final String PROP_SECRET_KEY = "recaptcha.secretkey";
    private final String FORM_FIELD_RECAPTCHA_PARAM = "recaptcha";
    private final String RECAPTCHA_RESPONSE = "g-recaptcha-response";
    private final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";
    private final String RECAPTCHA_SECRET_P = "secret";
    private final String RECAPTCHA_RESPONSE_P = "response";
    private final String CONN_PARAM_STRING = "=%s&";
    private final String CONN_METHOD = "POST";
    private final String CONN_ACC_CH = "Accept-Charset";
    private final String CONN_CON_TYPE = "Content-Type";
    private final String CONN_CHARSET = "UTF-8";
    private final String CONN_CHARSET_P = "charset";
    private final String CONN_APP_MIME = "application/x-www-form-urlencoded";
    private final String ERROR_P = "fe";

    private String fieldName;

    @Override
    public String beforeWebformAction(CmsObject cmsObject, CmsFormHandler formHandler) {
        boolean success = false;
        try {
            // Obtenemos la respuesta codificada
            String recRes = formHandler.getRequest().getParameter(RECAPTCHA_RESPONSE);

            // Validamos el campo captcha
            if (recRes != null && !recRes.isEmpty()) {

                // Obtenemos un json como respuesta de validacion
                String secValue = loadSecretKey(formHandler);

                // Llamamos a la API de ReCaptcha para validar la respuesta del usuario
                String resultRecaptcha = callReCaptcha(recRes, secValue);
                JSONObject jsonRecaptcha = new JSONObject(resultRecaptcha);

                if (jsonRecaptcha != null) {
                    success = jsonRecaptcha.getBoolean("success");
                }
            }
        } catch (Exception e) {
            LOG.error("ERROR beforeWebformAction", e);
        }

        // Verificamos si la respuesta ha sido correcta
        if (success) {
            return null;
        } else {

            // Creamos un link al formulario actual y cargamos los parametros
            String target = formHandler.getRequestContext().getUri();

            // Invalidamos el campo captcha y cargamos los parametros en la url
            prepareParamMap(formHandler);
            invalidate(formHandler);

            return target;
        }
    }

    /**
     * Invalida el campo captcha
     * @param formHandler
     */
    private void invalidate(CmsFormHandler formHandler) {
        String recaptchaFieldName = getFieldName(formHandler);
        formHandler.getErrors().put(recaptchaFieldName, formHandler.ERROR_VALIDATION);
    }

    /**
     * Copy all parameters from request but formaction
     * @param formHandler
     * @return
     */
    private void prepareParamMap(CmsFormHandler formHandler) {
        formHandler.getParameterMap().remove(formHandler.PARAM_FORMACTION);
        formHandler.getParameterMap().remove(RECAPTCHA_RESPONSE);
    }

    /**
     * Devuelve el nombre del campo recaptcha
     * @param formHandler
     */
    private String getFieldName(CmsFormHandler formHandler) {
        for (I_CmsField field : formHandler.getFormConfiguration().getFields()) {
            String recParam = field.getParameters().get(FORM_FIELD_RECAPTCHA_PARAM);
            if (recParam != null && !recParam.isEmpty()) {
                return field.getName();
            }
        }
        return null;
    }

    /**
     * Llamada a la API de ReCaptcha para validar el codigo
     *
     * @param recRes
     * @param secValue
     * @return
     */
    private String callReCaptcha(String recRes, String secValue) {
        String resultRecaptcha = null;
        InputStream responseApi = null;
        try {
            String params = RECAPTCHA_SECRET_P + CONN_PARAM_STRING
                    + RECAPTCHA_RESPONSE_P + CONN_PARAM_STRING;
            String queryRecaptcha = String.format(params,
                    URLEncoder.encode(secValue, CONN_CHARSET),
                    URLEncoder.encode(recRes, CONN_CHARSET));

            HttpURLConnection httpConnection =
                    (HttpURLConnection) new URL(RECAPTCHA_URL).openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod(CONN_METHOD);
            httpConnection.setRequestProperty(CONN_ACC_CH, CONN_CHARSET);
            httpConnection.setRequestProperty(
                    CONN_CON_TYPE, CONN_APP_MIME + ";" + CONN_CHARSET_P + "=" + CONN_CHARSET);

            OutputStream output = httpConnection.getOutputStream();
            output.write(queryRecaptcha.getBytes(CONN_CHARSET));

            responseApi = httpConnection.getInputStream();

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(responseApi, CONN_CHARSET));
            String line;
            resultRecaptcha = "";
            while((line = bufferedReader.readLine()) != null) {
                resultRecaptcha += line;
            }
        } catch (Exception e) {
            LOG.error("ERROR validando ReCaptcha", e);
        }
        return resultRecaptcha;
    }

    /**
     * Carga la clave secreta configurada en el loadconfig o en las propiedades del site
     * @return
     * @param formHandler
     */
    private String loadSecretKey(CmsFormHandler formHandler) {
        String secKey = null;
        HttpServletRequest request = formHandler.getRequest();
        CmsObject cmso = CmsFlexController.getCmsObject(request);

        // 1- Load secret key from .themeconfig
        secKey = loadByThemeconfig(request, cmso);

        // 2- If it does not exists load afrom property
        if (StringUtils.isEmpty(secKey)) {
            secKey = loadByProperty(cmso);
        }

        return secKey;
    }

    private String loadByProperty(CmsObject cmso) {
        String secKey = null;

        // Read property recaptcha.secretkey
        try {
            secKey = cmso.readPropertyObject(
                    cmso.getRequestContext().getUri(),
                    PROP_SECRET_KEY, true)
                    .getValue();
        } catch (Exception e) {
            LOG.error("ERROR readPropertyObject secret key", e);
        }
        return secKey;
    }

    private String loadByThemeconfig(HttpServletRequest request, CmsObject cmso) {
        String secKey = null;
        String path = SgTag.resourcePathLookBackTag(request, THEME_CONFIG_FILENAME);
        if (!StringUtils.isEmpty(path)) {
            CmsXmlUtil xml = new CmsXmlUtil(cmso, path, "es");
            try {
                secKey = xml.getStringValue(THEME_CONFIG_RECAPTCHA_KEY);
            } catch (Exception e) {
                LOG.error("ERROR xml.getStringValue secret key", e);
            }
        }
        return secKey;
    }
}