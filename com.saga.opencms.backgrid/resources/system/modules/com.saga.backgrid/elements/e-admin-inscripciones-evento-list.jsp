<%@page buffer="none" session="false" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--<cms:headincludes type="javascript" defaults="--%>
<%--%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/underscore-1.8.3.js:e27d415f-1c11-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backbone-1.3.3.js:e202e13b-1c11-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-0.3.7.js:c148a221-1c21-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/moment-2.13.0.js:d0ae568f-1c26-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-moment-cell.js:42e02aa1-1c2a-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-paginator-0.3.9.js:fb1b1095-1c38-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backbone-pageable-1.4.8.js:a20aa928-1c39-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-filter-0.3.6.js:c55e6fcf-1c2f-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-select-all.js:1780e762-23e7-11e6-98f1-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-ausape.js:e3aba635-1cee-11e6-b069-7fb253176922)--%>
<%--"/>--%>

<cms:headincludes type="javascript" defaults="
%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/underscore-min-1.8.3.js:e290f071-1c11-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backbone-min-1.3.3.js:e2639edd-1c11-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-min-0.3.7.js:c1bb1303-1c21-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/moment-min-2.13.0.js:d0ce1391-1c26-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-moment-cell.min.js:42f49d03-1c2a-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-paginator-min-0.3.9.js:fb6fc047-1c38-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backbone-pageable-min-1.4.8.js:a25c72aa-1c39-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-filter-min-0.3.6.js:c5702311-1c2f-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-select-all.min.js:1795a7e4-23e7-11e6-98f1-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-ausape.js:e3aba635-1cee-11e6-b069-7fb253176922)
"/>

<%-- CSS para el modal --%>
<cms:headincludes type="css" defaults="%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/css/magnific-popup.css:7c7e194e-1e7e-11e6-84fb-0050569f78c7)"/>

<%--<cms:headincludes type="javascript" defaults="%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/underscore-min-1.8.3.js:e290f071-1c11-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backbone-min-1.3.3.js:e2639edd-1c11-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-min-0.3.7.js:c1bb1303-1c21-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/moment-min-2.13.0.js:d0ce1391-1c26-11e6-b983-7fb253176922)"/>--%>


<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.ausape.zonaprivada.messages">
<div>
	<div id="msgs"></div>
	<div id="enrolls"></div>
	<div id="abridge"></div>
	<div id="ch-state-mul">
		<p><fmt:message key="admin.eventos.change.state.mul.txt"/></p>
		<form>
			<input id="insIds" type="hidden" name="is">
			<input id="inE" type="hidden" name="e" value="${param.e}"/>
			<select id="chSt" name="cs">
				<option value="accept"><fmt:message key="admin.eventos.change.state.mul.accept"/></option>
				<option value="refuse"><fmt:message key="admin.eventos.change.state.mul.refuse"/></option>
				<option value="hold"><fmt:message key="admin.eventos.change.state.mul.hold"/></option>
			</select>
			<input type="submit" class="btn btn-success" name="csmb" value="<fmt:message key='admin.eventos.change.state.mul.btn'/>"/>
		</form>
	</div>


	<%-- Incluimos el modal para las inscripciones --%>
	<div id="insc-modal" class="white-popup-block mfp-hide">
		<div id="insc-detail" class="dialogMiddleCenter">
		</div>
	</div>
</div>
</cms:bundle>

<script>
	$(function(){
		// El div donde vamos a mostar la tabla
		var $gridDiv = $("#enrolls");

		// TABLA
		//	Definimos el modelo de datos
		var Enroll = Backbone.Model.extend({});

		// Declaramos la coleccion paginable con la uri que devuelve los datos
		var Enrolls = AuPageableCollection.extend({
			model: Enroll,
			url: "<cms:link>/system/modules/com.saga.ausape.zonaprivada/elements/e-admin-inscripciones-evento-controller.jsp</cms:link>",
			mode: "server",
			state: {
				pageSize: 10,
				sortKey: 'fechaInscripcion',
				order: 1
			},
			queryParams: {
				e:'${param.e}',
				is:'${param.is}',
				cs:'${param.cs}',
				csmb:'${param.csmb}',
				u:'#insc-modal',
				g:'/ausapeweb/Users',
				currentPage:'page',
				pageSize: 'size',
				sortKey: 'sort',
				totalRecords: 'totalRecords',
				totalPages: 'totalPages',
				directions: {
					"-1": "ASC",
					"1": "DESC"
				}
			}
		});

		// Declaramos la coleccion de datos
		var enrolls = new Enrolls();

		//	Declaramos los campos de la tabla
		var columns = [{
			name: "",
			cell: Backgrid.Extension.SelectRowCell.extend({
				className: "select-row-cell col-xs-1"
			}),
			headerCell: Backgrid.Extension.SelectAllHeaderCell.extend({
				className: "select-all-header-cell col-xs-1"
			})
		}, {
			name: "id",
			label: "#",
			editable: false,
			class: 'col-xs-1',
			cell: AuIntegerCell
		}, {
			name: "usuario",
			label: "Usuario",
			editable: false,
			class: 'col-xs-3',
			title: true,
			cell: AuStringCell
		},{
			name: "emailUsuario",
			label: "Email",
			editable: false,
			class: 'col-xs-2',
			title: true,
			cell: AuStringCell
		}, {
			name: "fechaInscripcion",
			label: "Fecha de inscripción",
			editable: false,
			class: 'col-xs-3',
			title: true,
			cell: AuMomentCell.extend({
				displayFormat: "DD/MM/YYYY - hh:mm A"
			})
		}, {
			name: "estado",
			label: "Estado",
			editable: false,
			class: 'col-xs-1',
			cell: AuStringCell
		}, {
			name: "link",
			label: "Detalle",
			title: "Ir al detalle de la inscripción",
			type: "icon",
			icon: "fa fa-list-alt",
			editable: false,
			sortable: false,
			class: 'col-xs-1',
			cell: AuUriCell.extend({
				events:{
					"click": function(e){
						e.stopImmediatePropagation();
						e.preventDefault();

						// Inicializamos el popup
						var $popup = $("#insc-detail");
						$popup.empty().append('<i class="fa fa-spinner fa-pulse fa-3x fa-fw"></i>');

						var a = this.$el.find("a");
						a.magnificPopup({
							type: 'inline',
							preloader: false,
							callbacks: {
								close: function(){
									enrolls.fetch({
										reset: true
									});
								}
							}
						});
						a.trigger("click")

						// Cargamos el detalle de la inscripcion
						setTimeout(function(){
							$.post( "<cms:link>/system/modules/com.saga.ausape.zonaprivada/elements/e-admin-inscripcion-detalle.jsp</cms:link>",
									{ i: a.attr("id"), e:'${param.e}' })
									.done(function( data ) {
										$popup.empty().append(data);
									});
						}, 500)
					}
				}
			})
		}];

		// Add selection event handler
		enrolls.on("backgrid:selected", function (model, selected) {

			// Cargamos el valor de los ids
			var $inscs = $("#insIds");
			if ($inscs.val())
				var inscArray = $inscs.val().split(",");
			else
				var inscArray = new Array();

			// Si se ha seleccionado agregamos, en caso de que no exista
			var idx = inscArray.indexOf(model.id);
			if(selected){
				if (idx == -1)
					inscArray.push(model.id)
			}

			// Si se ha deseleccionado eliminamos, en caso de que si exista
			else{
				if (idx > -1)
					inscArray.splice(idx, 1);
			}

			$inscs.val(inscArray)
		});

		// Initialize a new Grid instance
		var grid = new Backgrid.Grid({
			model: AuColumn,
			className: "inscripciones",
			columns: columns,
			collection: enrolls
		});

		// Renderizamos la tabla
		$gridDiv.append(grid.render().el);

		// PAGINACION
		// Creamos la paginacion
		var paginator = new AuPaginator({
			renderMultiplePagesOnly: true,
			collection: enrolls
		});

		//Renderizamos la paginacion
		$gridDiv.append(paginator.render().el);

		// Fetch some enrolls
		enrolls.fetch({
			reset: true
		});
	})
</script>