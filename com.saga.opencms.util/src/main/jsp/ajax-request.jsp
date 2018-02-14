<%--Contenedor de elementos--%>
<div id="proveedoresTabproveedores"></div>

<%--Botón ver mas elementos--%>
<a href="#" id="verMasProveedores"
   title="Ver más"
   class="verMasProveedores botones acciones destacados has-tip tip-top"
   data-start="0"
   data-rows="5"
   data-container="proveedoresTabproveedores"
   data-url="<cms:link>/system/modules/com.saga.bcieproveedores.frontend/formatters/site-saai/proceso-compra-proveedores-paginando-ajax.jsp</cms:link>"
   data-loading="verMasProveedoresLoading"
   data-params='{"uuid": "${param.uuid}"}'>
    <i class="foundicon-plus text-32"></i>
</a>

<%--JS carga por AJAX --%>
<script>

    // Evento onclick para el botón ver mas elementos
    $(document).ready(function () {
        $('#verMasProveedores').on('click', verMasProveedores)
    });

    // controlador carga de mas elementos
    function verMasProveedores(e) {
//        console.log('verMasProveedores', e);
        e.preventDefault();

        try {
            var ctxt = {id: 'verMasProveedores'};
            load(ctxt);
            if (validate(ctxt)) {
                execute(ctxt);
            }
        } catch (err) {
            console.error(err);
        }
    }

    function load(ctxt) {
//        console.log('load', ctxt);

        var btn = $('#' + ctxt.id);
        var datas = btn.data();
//        console.log('datas', datas);
        ctxt.start = datas.start + 1;
        ctxt.rows = datas.rows;
        ctxt.container = datas.container;
        ctxt.url = datas.url;
        ctxt.loading = datas.loading;
        ctxt.params = datas.params;
    }

    function validate(ctxt) {
//        console.log('validate', ctxt);
        if (ctxt.start === undefined || ctxt.start < 0) {
            throw new Error('validate start data must not be undefined or less than 0: ' + ctxt.start)
        }
        if (ctxt.rows === undefined || ctxt.rows < 1) {
            throw new Error('validate rows data must not be undefined or less than 1: ' + ctxt.rows)
        }
        if (ctxt.container === undefined) {
            throw new Error('validate container data must not be undefined: ' + ctxt.container)
        }
        if (ctxt.url === undefined) {
            throw new Error('validate url data must not be undefined: ' + ctxt.url)
        }
        if (ctxt.loading === undefined) {
            throw new Error('validate loading data must not be undefined: ' + ctxt.loading)
        }
        return true;
    }

    function execute(ctxt) {
//        console.log('execute', ctxt);

        loading(ctxt);

        var params = params(ctxt);

        $.post(ctxt.url, params)
                .done(function (data) {
//                    console.log('done execute - append data to container', ctxt, data);
                    updateData(ctxt, data)
                })
                .fail(function (err) {
                    console.error("ERROR execute", ctxt, err);
                })
                .always(function () {
//                    console.log('always update')
                    update(ctxt);
                })
    }

    function loading(ctxt) {
//        console.log('loading', ctxt);
        $('#' + ctxt.id).hide();
        $('#' + ctxt.loading).show();
    }

    function params(ctxt){
        return {start: ctxt.start, rows: ctxt.rows}
    }

    function updateData(ctxt, data){
        if (data.trim() === "") {
//                        console.log('no more results');
            $('#' + ctxt.id).html('No hay más resultados').unbind('click');
        } else {
            $('#' + ctxt.container).append(data);
        }
    }

    function update(ctxt) {
//        console.log('update', ctxt);
        var btn = $('#' + ctxt.id)
        btn.data('start', ctxt.start + 1);

        $('#' + ctxt.loading).hide();
        $('#' + ctxt.id).show();
    }
</script>