<%@page buffer="none" session="false" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cms:headincludes type="javascript" defaults="
%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/underscore-1.8.3.js:e27d415f-1c11-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backbone-1.3.3.js:e202e13b-1c11-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-0.3.7.js:c148a221-1c21-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/moment-2.13.0.js:d0ae568f-1c26-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-moment-cell.js:42e02aa1-1c2a-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-paginator-0.3.9.js:fb1b1095-1c38-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backbone-pageable-1.4.8.js:a20aa928-1c39-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-filter-0.3.6.js:c55e6fcf-1c2f-11e6-b983-7fb253176922)
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-ausape.js:e3aba635-1cee-11e6-b069-7fb253176922)
"/>


<%--<cms:headincludes type="javascript" defaults="%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/underscore-min-1.8.3.js:e290f071-1c11-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backbone-min-1.3.3.js:e2639edd-1c11-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-min-0.3.7.js:c1bb1303-1c21-11e6-b983-7fb253176922)--%>
<%--|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/moment-min-2.13.0.js:d0ce1391-1c26-11e6-b983-7fb253176922)"/>--%>

<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.ausape.zonaprivada.messages">
<div>
	<h2><fmt:message key="admin.eventos.title"/></h2>
	<p><fmt:message key="admin.eventos.body"/></p>
	<div id="msgs"></div>
	<div id="events"></div>
</div>
</cms:bundle>

<script>
	$(function(){
		// El div donde vamos a mostar la tabla
		var $gridDiv = $("#events");

		// TABLA
		//	Definimos el modelo de datos
		var Event = Backbone.Model.extend({});

		// Declaramos la coleccion paginable con la uri que devuelve los datos
		var Events = AuPageableCollection.extend({
			model: Event,
			url: "<cms:link>/system/modules/com.saga.ausape.zonaprivada/elements/e-controller.jsp</cms:link>",
			mode: "server",
			state: {
				pageSize: 10,
				order: 1
			},
			queryParams: {
				u:'/administracion/evento/',
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
		var events = new Events();

		//	Declaramos los campos de la tabla
		var columns = [{
			name: "title",
			label: "Evento",
			type: "json",
			editable: false,
			class: 'col-xs-5',
			cell: AuUriCell
		}, {
			name: "esAbierto",
			label: "Estado",
			editable: false,
			class: 'col-xs-1',
			cell: AuStringCell
		}, {
			name: "fechaInicio",
			label: "Fecha de inicio",
			editable: false,
			class: 'col-xs-3',
			cell: AuMomentCell.extend({
				displayFormat: "DD/MM/YYYY - hh:mm A"
			})
		}, {
			name: "nInscripciones",
			label: "Número de inscripciones",
			editable: false,
			class: 'col-xs-2',
			cell: AuIntegerCell
		}, {
			name: "link",
			label: "Detalle",
			title: "Ir al detalle del evento",
			type: "icon",
			icon: "fa fa-list-alt",
			editable: false,
			sortable: false,
			class: 'col-xs-1',
			cell: AuUriCell
		}];

		// Initialize a new Grid instance
		var grid = new Backgrid.Grid({
			model: AuColumn,
			className: "eventos",
			columns: columns,
			collection: events
		});

		// Renderizamos la tabla
		$gridDiv.append(grid.render().el);

		// PAGINACION
		// Creamos la paginacion
		var paginator = new AuPaginator({
			renderMultiplePagesOnly: true,
			collection: events
		});

		//Renderizamos la paginacion
		$gridDiv.append(paginator.render().el);

		// Fetch some enrolls
		events.fetch({reset: true});
	})
</script>