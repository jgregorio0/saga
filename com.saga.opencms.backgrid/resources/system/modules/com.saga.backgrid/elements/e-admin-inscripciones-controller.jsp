<%@ page import="com.saga.ausape.zonaprivada.inscripciones.entity.InscripcionEntity" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.model.EstadoInscripcion" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.util.SgSolr" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.view.AdminInscripcionesController" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
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
	//	pageContext.setAttribute("adminGroup", "/ausapeweb/Users");
	AdminInscripcionesController controller = new AdminInscripcionesController(pageContext, request, response);
	controller.handleRequest();
	Map<String, Object> pMap = (Map<String, Object>) pageContext.getAttribute("pMap");
	JSONObject jObj = new JSONObject();

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
	CmsObject cmso = CmsFlexController.getCmsObject(request);
	List<InscripcionEntity> enrolls = (List<InscripcionEntity>) pMap.get("enrolls");
	for (int i = 0; i < enrolls.size(); i++) {
		InscripcionEntity enroll = enrolls.get(i);
		Map<String, Object> map = new HashMap<String, Object>();

//		CmsSolrResultList results = solrResults(CmsFlexController.getCmsObject(request), enroll.getUuidEvento(), "1");
//		CmsSearchResource solrRes = results.get(0);

		String id = enroll.getId().toString();
		map.put("id", id);

//		String title = getField(solrRes, "xmltitle_es_s");
		String title = getProperty(cmso, enroll.getPathEvento(), "Title");
		map.put("title", title);

		Date fechaInscripcion = enroll.getFechaInscripcion();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		map.put("fechaInscripcion", sdf.format(fechaInscripcion));

		String estado = EstadoInscripcion.getValor(enroll.getEstado());
		map.put("estado", estado);

		records.add(map);
	}
	jObj.put("records", records);
	response.getWriter().print(jObj);
%>
