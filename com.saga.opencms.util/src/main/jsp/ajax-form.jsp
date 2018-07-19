<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%--Formulario principal--%>
<form id="header-login-form" method="post" action="#"
      data-controller="<cms:link>/system/modules/com.saga.principal.frontend/handler/encrypt.jsp</cms:link>"
      data-login-config-path="/.loginconfig"
      data-site="${cms.requestContext.siteRoot}"
      data-locale="${cms.requestContext.locale}"
      data-test="true">
    <div class="form-group">
        <label for="headerLoginAccountType" class="sr-only">Selecciona tu tipo de cuenta</label>
        <select class="form-control" id="headerLoginAccountType" name="headerLoginAccountType">
            <option value="">Selecciona tu tipo de cuenta</option>
            <optgroup label="Individual">
                <option value="http://www.individual.fondoahorro.com/login/"
                        data-url-submit="http://www.individual.fondoahorro.com/login/"
                        data-url-recover-user="http://www.individual.fondoahorro.com/recover-user/"
                        data-url-recover-password="http://www.individual.fondoahorro.com/recover-password/"
                        data-url-active-account="none"
                        data-encryption="MD5">
                    Fondo de ahorro individual
                </option>
                <option value="http://www.individual.planretiro.com/login/"
                        data-url-submit="http://www.individual.planretiro.com/login/"
                        data-url-recover-user="none"
                        data-url-recover-password="http://www.individual.planretiro.com/recover-password/"
                        data-url-active-account="none" data-encryption="AES">
                    Plan personal de retiro
                </option>
            </optgroup>
            <optgroup label="Empresarial">
                <option value="http://www.empresarial.fondoahorro.com/login/"
                        data-url-submit="http://www.empresarial.fondoahorro.com/login/"
                        data-url-recover-user="none" data-url-recover-password="none"
                        data-url-active-account="http://www.empresarial.fondoahorro.com/active-account/"
                        data-encryption="AES">
                    Fondo de ahorro empresas
                </option>
            </optgroup>
        </select>
    </div>
    <div class="form-group form-group-depends-select form-group-depends-select-submit form-group-user disabled">
        <span class="form-group-icon fa fa-user" aria-hidden="true"></span>
        <label for="headerLoginUser" class="sr-only">Usuario</label>
        <input class="form-control" id="headerLoginUser" name="headerLoginUser" placeholder="Usuario"
               type="text">
    </div>
    <div class="form-group form-group-depends-select form-group-depends-select-submit form-group-password disabled">
        <span class="form-group-icon fa fa-key" aria-hidden="true"></span>
        <label for="headerLoginPassword" class="sr-only">Contraseña</label>
        <input class="form-control" id="headerLoginPassword" name="headerLoginPassword"
               placeholder="Contraseña" type="password">
    </div>
    <div class="actions form-group form-group-depends-select form-group-depends-select-submit form-group-submit disabled">
        <button type="submit" class="btn btn-specific-main btn-block rounded-4x" id="headerLoginSubmit">
            Entrar
        </button>
    </div>
</form>

<%--FORMULARIO GENERADO--%>
<div id="header-login-form-js" style="display: none;"></div>

<%--JS carga por AJAX --%>
<script>
    $(document).ready(function () {

        $("#header-login-form").submit(encryptAndLogin);

        // controlador carga de mas elementos
        function encryptAndLogin(e) {
            //console.log('encryptAndLogin', e);
            e.preventDefault();

            try {
                var ctxt = {};
                load(ctxt);
                if (validate(ctxt)) {
                    execute(ctxt);
                }
            } catch (err) {
                console.error(err);
            }
        }

        function load(ctxt) {
            //console.log('load', ctxt);

            var data = $("#header-login-form").data();
            ctxt.controller = data.controller;
            ctxt.loginConfigPath = data.loginConfigPath;
            ctxt.site = data.site;
            ctxt.locale = data.locale;
            ctxt.test = data.test || false;
        }

        function validate(ctxt) {
            if (ctxt.controller === undefined || ctxt.controller.length <= 0) {
                throw new Error('controller must not be empty')
            }
            if (ctxt.site === undefined || ctxt.site.length <= 0) {
                throw new Error('site must not be empty')
            }
            if (ctxt.locale === undefined || ctxt.locale.length <= 0) {
                throw new Error('locale must not be empty')
            }
            return true;
        }

        function execute(ctxt) {
            //console.log('execute', ctxt);

            showLoading(ctxt);
            var params = loadParams(ctxt);
            $.post(ctxt.controller, params)
                .done(function (res) {
//                    console.log('done execute - append data to container', ctxt, data);
                    getEncDataAndPostToLogin(res)
                })
                .fail(function (err) {
                    console.error("ERROR execute", ctxt, err);
                })
                .always(function () {
//                    console.log('always update')
                    hideLoading(ctxt);
                })
        }

        function ensureController (ctxt) {
            if (!ctxt.controller.startsWith("https")) {
                var host = window.location.host;
                var protocol = "https";
                ctxt.controller = protocol + "://" + host + ctxt.controller;
            }
        };

        function showLoading(ctxt) {
            //console.log('showLoading', ctxt);
            //todo disable and show loading
            // $('#' + ctxt.id).hide();
            // $('#' + ctxt.loading).show();
        }

        function loadParams(ctxt) {
            //console.log('params', ctxt);
            return {
                user: ctxt.user
                , pass: ctxt.pass
                , encryption: ctxt.encryption
                , urlSubmit: ctxt.urlSubmit
                , loginConfigPath: ctxt.loginConfigPath
                , site: ctxt.site
                , locale: ctxt.locale
            }
        }

        function hideLoading(ctxt) {
            //console.log('update', ctxt);
            //todo hide loading
        }

        function getEncDataAndPostToLogin(res) {
            //console.log("getEncDataAndPostToLogin", res);
            try {
                // response to JSON
                var jRes = JSON.parse(res.trim());
                if (jRes.error) {
                    throw new Error('response error: ' + jRes.errorMsg);
                }

                // load form and inputs
                var form = jRes.form;
                if (form === undefined || form.length <= 0) {
                    throw new Error('response form must not be empty');
                }
                var inputs = jRes.inputs;
                if (inputs === undefined || inputs.length <= 0) {
                    throw new Error('response inputs must not be empty');
                }

                // create form
                var $formContainer = $('#header-login-form-js');
                $formContainer.empty();

                // form = {"method":"POST", "target":"_blank", "action":"{{action}}"}
                var $form = $('<form>', form);
                for (var i = 0; i < inputs.length; i++) {
                    var input = inputs[i];
                    // input = {"name": "algo", "value": "value"}
                    var $input = $('<input>', input);
                    $form.append($input)
                }
                $formContainer.append($form);

                // submit
                $form.submit();
            } catch (e) {
                console.error(e)
            }
        }
    });
</script>