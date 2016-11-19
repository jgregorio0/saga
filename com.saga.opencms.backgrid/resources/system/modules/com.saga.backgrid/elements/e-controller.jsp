<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.jsp.CmsJspTagLink" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.util.CmsUUID" %>
<%@ page import="java.util.*" %>
<%@page buffer="none" session="false" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%!
	private final Log LOG = CmsLog.getLog(this.getClass());

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

	private String getXmlField(CmsObject cmso, String rootPath, String propName) {
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

	private String getProperty(CmsObject cmso, CmsResource resource, String propName) {
		String propValue = null;
		try {
			propValue = cmso.readPropertyObject(resource, propName, false).getValue();
		} catch (CmsException e) {
			LOG.error("ERROR leyendo propiedad " + propName
					+ " del recurso " + resource, e);
		}
		return propValue;
	}

	private CmsResource readResource(CmsObject cmso, String uuid) {
		CmsUUID CmsUuid = CmsUUID.valueOf(uuid);
		CmsResource resource = null;
		try {
			resource = cmso.readResource(CmsUuid);
		} catch (CmsException e) {
			LOG.error("ERROR leyendo el recurso cond uuid: " + uuid, e);
		}
		return resource;
	}

%>

<%
	// Ejecutamos controlador
//	AdminEventosController controller = new AdminEventosController(pageContext, request, response);
//	controller.handleRequest();
	Map<String, Object> pMap = (Map<String, Object>) pageContext.getAttribute("pMap");
	JSONObject jObj = new JSONObject();
	String uri = request.getParameter("u");
	String baseLink = CmsJspTagLink.linkTagAction(uri, request);

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
	List<Object[]> events = (List<Object[]>) pMap.get("events");
	for (int i = 0; i < events.size(); i++) {
		Object[] event = events.get(i);
		try {
			Map<String, Object> map = new HashMap<String, Object>();

			String linkEv = CmsJspTagLink.linkTagAction((String) event[2], request);
			CmsResource resource = readResource(cmso, (String) event[1]);
			HashMap<String, String> titleMap = new HashMap<String, String>();
			String title = getProperty(cmso, resource, "Title");
			titleMap.put("text", title);
			titleMap.put("title", title);
			titleMap.put("href", linkEv);
			map.put("title", titleMap);

			String fInicio = getProperty(cmso, resource, "collector.date");
			Date dateInicio = new Date(Long.valueOf(fInicio));
			map.put("fechaInicio", dateInicio);

			map.put("nInscripciones", event[0]);

			String link = baseLink + "?e=" + event[1];
			map.put("link", link);

			records.add(map);
		} catch (Exception e) {
			LOG.error("ERROR tratando inscripcion a eventos {" +
					event[0] + ", " + event[1] + ", " + event[2] + "}");
		}
	}
	jObj.put("records", records);
	response.getWriter().print(jObj);
%>
