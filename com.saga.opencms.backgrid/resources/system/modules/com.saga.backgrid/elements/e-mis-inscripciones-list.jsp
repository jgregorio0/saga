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

<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.saga.ausape.zonaprivada.messages">
<div>
	<h2><fmt:message key="mis.inscripciones.title"/></h2>
	<p><fmt:message key="mis.inscripciones.body"/></p>
	<div id="msgs"></div>
	<div id="enrolls"></div>
</div>
</cms:bundle>

<script>
	$(function(){
		// El div donde vamos a mostar la tabla
		var $grill = $("#enrolls");

		// TABLA
		//	Definimos el modelo de datos y la tabla
		var Enroll = Backbone.Model.extend({});

		// Hacemos la tabla paginable y declaramos la uri que devuelve los datos
		var Enrolls = AuPageableCollection.extend({
			model: Enroll,
			url: "<cms:link>/system/modules/com.saga.ausape.zonaprivada/elements/e-mis-inscripciones-controller.jsp</cms:link>",
			state: {
				pageSize: 10,
				order: 1
			},
			mode: "client"
		});

		// Declaramos la tabla
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
			type: "json",
			editable: false,
			class: 'col-xs-6',
			cell: AuUriCell
		}, {
			name: "fechaInscripcion",
			label: "Fecha de inscripción",
			editable: false,
			class: 'col-xs-3',
			title: true,
			cell: AuMomentCell.extend({
				displayFormat: "DD-MM-YYYY - hh:mm A"
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
		$grill.append(grid.render().el);

		// PAGINACION
		// Creamos la paginacion
		var paginator = new AuPaginator({
			renderMultiplePagesOnly: true,
			collection: enrolls
		});

		//Renderizamos la paginacion
		$grill.append(paginator.render().el);

		// FILTRO
//		var clientSideFilter = new Backgrid.Extension.ClientSideFilter({
//			collection: enrolls,
//			placeholder: "Busca evento",
//			// The model fields to search for matches
//			fields: ['title'],
//			// How long to wait after typing has stopped before searching can start
//			wait: 150
//		});
//
//		//Renderizamos el filtro
//		$grill.prepend(clientSideFilter.render().el);

		// Fetch some enrolls
		enrolls.fetch({reset: true});
	})
</script>