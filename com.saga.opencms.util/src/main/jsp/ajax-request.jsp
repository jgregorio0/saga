<div class="reinscripcion-container">
    <%--FORM--%>
    <form id="reinscripcion-form" class="form-inline" method="post"
          data-controller="<cms:link>/system/modules/com.saga.cedinox/functions/f-reinscripcion-revista-datos-usuario-by-codigo-unico.jsp</cms:link>"
          data-locale="${cms.locale}"
          data-uri="${cms.requestContext.uri}">
        <div class="form-group">
            <label for="codigo-unico"><fmt:message key="reinscripcion.revista.codigo.unico.label"/></label>
            <input type="text" class="form-control" id="codigo-unico"
                   placeholder="<fmt:message key="reinscripcion.revista.codigo.unico.placeholder"/>">
        </div>
        <button type="submit" class="btn btn-primary">
            <fmt:message key="reinscripcion.revista.codigo.unico.validate"/></button>
    </form>

    <%--LOADING OVERLAY--%>
    <div id="reinscripcion-loading" class="fade" style="
                                    background: rgba(255,255,255,0.4);
                                    position: absolute;
                                    left: 0;
                                    right: 0;
                                    top: 0;
                                    bottom: 0;
                                    width: 100%;
                                    height: 100%;
                                    -webkit-transition: all 0.3s ease;
                                    -moz-transition: all 0.3s ease;
                                    -ms-transition: all 0.3s ease;
                                    -o-transition: all 0.3s ease;
                                    transition: all 0.3s ease;
                                    z-index: -1;">
            <span class="fa fa-spinner fa-spin fa-3x fa-fw" style="
                position: absolute;
                left: 50%;
                top: 50%;
                margin: -39px 0 0 -39px;
                display: block;
                color: #222;
                /* z-index: 10000; */
                "></span>
        <span class="sr-only"><fmt:message key="reinscripcion.revista.loading"/></span>
    </div>
</div>

<script>

    var _id = '#reinscripcion';
    var _idContainer = _id + '-container';
    var _idForm = _id + '-form';
    var _idCodigoUnico = '#codigo-unico';
    var _idLoading = _id + '-loading';

    // Evento onclick para comprobar el codigo unico
    $(function () {
        $(_idForm).on('submit', loadDataByCodigoRevista)
    });

    // controlador carga de mas elementos
    function loadDataByCodigoRevista(e) {
        console.log('verMasProveedores', e);
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
        console.log('load');
        ctxt.inputs = $(_idForm).serializeArray();
        /*var $form = $(_idForm);
        var codigoUnico = $form.find(_idCodigoUnico).val();

        console.log('codigoUnico', codigoUnico);
        ctxt.codigoUnico = codigoUnico;*/

        var datas = $form.data();
        ctxt.controller = datas.controller;
        ctxt.locale = datas.locale;
        ctxt.uri = datas.uri;
    }

    function validate(ctxt) {
        console.log('validate', ctxt);
        if (ctxt.codigoUnico === undefined || ctxt.codigoUnico.length < 0) {
            throw new Error('codigoUnico must not be empty: ' + ctxt.codigoUnico)
        }
        if (ctxt.controller === undefined || ctxt.controller.length < 0) {
            throw new Error('controller must not be empty: ' + ctxt.controller)
        }
        if (ctxt.locale === undefined || ctxt.locale.length < 0) {
            throw new Error('locale must not be empty: ' + ctxt.locale)
        }
        if (ctxt.uri === undefined || ctxt.uri.length < 0) {
            throw new Error('uri must not be empty: ' + ctxt.uri)
        }
        return true;
    }

    function execute(ctxt) {
        console.log('execute', ctxt);

        showLoading(ctxt);
        var params = loadParams(ctxt);

        $.post(ctxt.controller, params)
            .done(function (data) {
                console.log('done execute', ctxt, data);
                // updateData(ctxt, data)
            })
            .fail(function (err) {
                console.error("ERROR execute", ctxt, err);
            })
            .always(function () {
//                    console.log('always update')
                update(ctxt);
            })
    }

    function showLoading() {
        var $loading = $(_idLoading);
        $loading.addClass('in');
        $loading.css('z-index', 0);
    }

    function hideLoading() {
        var $loading = $(_idLoading);
        $loading.removeClass('in');
        $loading.css('z-index', -1);
    }

    function loadParams(ctxt) {
        return {locale: ctxt.locale, uri: ctxt.uri, codigoUnico: ctxt.codigoUnico}
    }

    function update(ctxt) {
//        console.log('update', ctxt);
        hideLoading();
    }
</script>