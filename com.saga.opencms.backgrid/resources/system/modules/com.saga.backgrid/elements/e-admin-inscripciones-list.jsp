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
|%(link.weak:/system/modules/com.saga.ausape.zonaprivada/resources/js/backgrid-ausape.js:e3aba635-1cee-11e6-b069-7fb253176922)
"/>

<div>
	<div id="msgs"></div>
	<div id="enrolls"></div>
</div>

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
			url: "<cms:link>/system/modules/com.saga.ausape.zonaprivada/elements/e-admin-inscripciones-controller.jsp</cms:link>",
			mode: "server",
			state: {
				pageSize: 3,
				order: 1
			},
			queryParams: {
				currentPage: 'page',
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
			name: "id",
			label: "#",
			editable: false,
			class: 'col-xs-1',
			cell: AuIntegerCell
		}, {
			name: "title",
			label: "Evento",
			editable: false,
			class: 'col-xs-5',
			cell: AuStringCell
		}, {
			name: "fechaInscripcion",
			label: "Fecha de inscripción",
			editable: false,
			class: 'col-xs-4',
			title: true,
			cell: AuMomentCell.extend({
				displayFormat: "D-M-YYYY - h:m A"
			})
		}, {
			name: "estado",
			label: "Estado",
			editable: false,
			class: 'col-xs-2',
			cell: AuStringCell
		}];

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
			renderMultiplePagesOnly: false,
			collection: enrolls
		});

		//Renderizamos la paginacion
		$gridDiv.append(paginator.render().el);

		// TODO el filtro debe ser ejecutado en el servidor y cuidado con la paginacion
//		// FILTRO
//		var clientSideFilter = new Backgrid.Extension.ClientSideFilter({
//			collection: Enrolls.fullCollection,
//			placeholder: "Busca evento",
//			fields: ['title']
//		});
//
//		//Renderizamos el filtro
//		$gridDiv.prepend(clientSideFilter.render().el);

		// Fetch some enrolls
		enrolls.fetch({reset: true});
	})
</script>