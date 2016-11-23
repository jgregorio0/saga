<%@ page import="com.saga.ausape.zonaprivada.inscripciones.entity.InscripcionEntity" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.entity.UsuarioEntity" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.model.EstadoInscripcion" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.util.SgSolr" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.view.AdminInscripcionesController" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.view.AdminInscripcionesEventoController" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.search.CmsSearchException" %>
<%@ page import="org.opencms.search.CmsSearchResource" %>
<%@ page import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@page buffer="none" session="false" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%!
	private final Log LOG = CmsLog.getLog(AdminInscripcionesController.class);

	private CmsSolrResultList solrResults(CmsObject cmso, String id, String rows){
		SgSolr solrUtil = new SgSolr(cmso);

		solrUtil.addQParam(SgSolr.Q_ID, id);
		solrUtil.subQParam(SgSolr.Q_ROWS, rows, false);
		String query = solrUtil.createQuery();
		CmsSolrResultList results = null;
		try {
			results = solrUtil.search(query);
		} catch (CmsSearchException e) {
			e.printStackTrace();
		}
		return results;
	}

	private String getField(CmsSearchResource solrRes, String fieldName) {
		String fieldValue = null;
		try {
			fieldValue = solrRes.getField(fieldName);
		} catch (Exception e) {
			LOG.error("ERROR obteniendo de solr el campo " + fieldName, e);
		}
		return fieldValue;
	}

	private String getProperty(CmsObject cmso, String rootPath, String propName) {
		String propValue = null;
		try {
			String relPath = cmso.getRequestContext().removeSiteRoot(rootPath);
			propValue = cmso.readPropertyObject(relPath, propName, false).getValue();
		} catch (CmsException e) {
			LOG.error("ERROR leyendo propiedad " + propName
					+ " del recurso " + rootPath, e);
		}
		return propValue;
	}
%>

<%
	// Ejecutamos controlador
	AdminInscripcionesEventoController controller =
			new AdminInscripcionesEventoController(pageContext, request, response);
	controller.handleRequest();
	Map<String, Object> pMap = (Map<String, Object>) pageContext.getAttribute("pMap");
	JSONObject jObj = new JSONObject();
	String uri = request.getParameter("u");
	String baseLink = uri;

	// Obtenemos los mensajes
	List<String> errors = (List<String>) pMap.get("errors");
	List<String> infos = (List<String>) pMap.get("infos");
	Map<String, Object> msgs = new HashMap<String, Object>();
	msgs.put("errors", errors);
	msgs.put("infos", infos);
	jObj.put("msgs", msgs);

	// Obtenemos el estado de la paginacion
	Map<String, Object> state = null;
	Boolean isPagination = (Boolean) pMap.get("isPagination");
	if (isPagination) {
		state = new HashMap<String, Object>();
		state.put("totalRecords", pMap.get("totalRecords"));
		state.put("totalPages", pMap.get("totalPages"));
	}
	jObj.put("state", state);

	// Obtenemos los datos
	List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
	List<InscripcionEntity> enrolls = (List<InscripcionEntity>) pMap.get("enrolls");
	for (int i = 0; i < enrolls.size(); i++) {
		InscripcionEntity enroll = enrolls.get(i);
		Map<String, Object> map = new HashMap<String, Object>();

		String id = enroll.getId().toString();
		map.put("id", id);

		UsuarioEntity usuario = enroll.getUsuario();
		String nombre = usuario.getFirstName() + " " + usuario.getLastName();
		map.put("usuario", nombre);

		String email = usuario.getEmailUsuario();
		map.put("emailUsuario", email);

		Date fechaInscripcion = enroll.getFechaInscripcion();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		map.put("fechaInscripcion", sdf.format(fechaInscripcion));

		String estado = EstadoInscripcion.getValor(enroll.getEstado());
		map.put("estado", estado);

		String link = baseLink;
		map.put("link", link);

		records.add(map);
	}
	jObj.put("records", records);

	// Obtenemos los contadores
	List<Map<String, Object>> abridge = new ArrayList<Map<String, Object>>();

	Map<String, Object> inscritas = new HashMap<String, Object>();
	inscritas.put("text", "Nuevas");
	Long cInscritas = (Long) pMap.get("cInscritas");
	inscritas.put("badge", cInscritas);
	abridge.add(inscritas);

	Map<String, Object> canceladas = new HashMap<String, Object>();
	canceladas.put("text", "Cancelados");
	Long cCanceladas = (Long) pMap.get("cCanceladas");
	canceladas.put("badge", cCanceladas);
	abridge.add(canceladas);

	Map<String, Object> aceptadas = new HashMap<String, Object>();
	aceptadas.put("text", "Aceptados");
	Long cAceptadas = (Long) pMap.get("cAceptadas");
	aceptadas.put("badge", cAceptadas);
	abridge.add(aceptadas);

	Map<String, Object> rechazadas = new HashMap<String, Object>();
	rechazadas.put("text", "Rechazados");
	Long cRechazadas = (Long) pMap.get("cRechazadas");
	rechazadas.put("badge", cRechazadas);
	abridge.add(rechazadas);

	Map<String, Object> enReserva = new HashMap<String, Object>();
	enReserva.put("text", "En reserva");
	Long cEnReserva = (Long) pMap.get("cEnReserva");
	enReserva.put("badge", cEnReserva);
	abridge.add(enReserva);

	Map<String, Object> total = new HashMap<String, Object>();
	total.put("text", "Total");
	Long cTotal = (Long) pMap.get("cTotal");
	total.put("badge", cTotal);
	abridge.add(total);

	jObj.put("abridge", abridge);

	response.getWriter().print(jObj);
%>
