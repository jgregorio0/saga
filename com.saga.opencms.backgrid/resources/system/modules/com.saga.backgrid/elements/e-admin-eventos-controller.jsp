<%@ page import="com.alkacon.simapi.CmykJpegReader.StringUtil" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.util.SgSolr" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.view.AdminEventosController" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.jsp.CmsJspTagLink" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.search.CmsSearchResource" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.ParseException" %>
<%@page buffer="none" session="false" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%!
	private final Log LOG = CmsLog.getLog(AdminEventosController.class);

	private static final String P_PAGE = "page";
	private static final String P_SIZE = "size";
	private static final String P_SORT = "sort";
	private static final String P_ORDER = "order";
	private static final String P_ROWS = "rows";
	private static final String P_S_TYPE = "t";
	private static final String P_FILTER_TXT = "ftxt";
	private static final String P_FILTER_DATE_START = "fdatestart";
	private static final String P_FILTER_DATE_END = "fdateend";

	private String solrQuery(HttpServletRequest request) {
		String type = request.getParameter(P_S_TYPE);

		// Parse state
		String pageStr = request.getParameter(P_PAGE);
		Integer page = null;
		if (!StringUtil.isEmpty(pageStr)) {
			page = Integer.valueOf(pageStr) - 1;
		}

		String pageSizeStr = request.getParameter(P_SIZE);
		Integer pageSize = null;
		if (!StringUtil.isEmpty(pageSizeStr)) {
			pageSize = Integer.valueOf(pageSizeStr);
		}
		boolean isPagination = page != null && pageSize != null;

		String sort = request.getParameter(P_SORT);
		String order = request.getParameter(P_ORDER);
		boolean isSorting = !StringUtil.isEmpty(sort) && !StringUtil.isEmpty(order);

		// Parse solr state
		String start = "0";
		String rows = request.getParameter(P_ROWS);
		if (page != null && pageSize != null) {
			Integer startInt = page*pageSize;
			start = startInt.toString();
			rows = pageSize.toString();
		}

		if (rows == null) {
			rows = "100";
		}

		// Parse filters
		String txtFilter = request.getParameter(P_FILTER_TXT);
		String dateFilterStart = request.getParameter(P_FILTER_DATE_START);
		Date dateFilterFrom = null;
		if (!StringUtil.isEmpty(dateFilterStart)) {
			try {
				SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
				dateFilterFrom = dmy.parse(dateFilterStart);
			} catch (ParseException e) {
				LOG.error("ERROR parsing data filter " + dateFilterStart);
			}
		}

		String dateFilterEnd = request.getParameter(P_FILTER_DATE_END);
		Date dateFilterTo = null;
		if (!StringUtil.isEmpty(dateFilterEnd)) {
			try {
				SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
				dateFilterTo = dmy.parse(dateFilterEnd);
			} catch (ParseException e) {
				LOG.error("ERROR parsing data filter " + dateFilterEnd);
			}
		}

		// Create query
		CmsObject cmso = CmsFlexController.getCmsObject(request);
		SgSolr solr = new SgSolr(cmso);

		// Tipo de recurso
		if (!StringUtil.isEmpty(type)) {
			solr.addQParam(SgSolr.Q_TYPE, type);
		}

		// Paginacion
		solr.subQParam(SgSolr.Q_START, start, false);
		solr.subQParam(SgSolr.Q_ROWS, rows, false);

		// Orden
		if (!StringUtil.isEmpty(sort) && !StringUtil.isEmpty(order)) {
			solr.subQParam(SgSolr.Q_SORT, sort + " " + order, false);
		}

		// Filtros
		if (txtFilter != null) {
			solr.subQParam(SgSolr.Q_Q + "Title_prop:", txtFilter + "*", false);
		}

		if (dateFilterFrom != null && dateFilterTo != null) {
			solr.addQDateParam("fq=xmldate_es_dt:", dateFilterFrom, dateFilterTo);
		} else if (dateFilterFrom != null) {
			solr.addQDateParamFrom("fq=xmldate_es_dt:", dateFilterFrom);
		} else if (dateFilterTo != null) {
			solr.addQDateParamTo("fq=xmldate_es_dt:", dateFilterTo);
		}

		// Obligamos a que permita inscripciones
		solr.addQParam("fq=xmlreqregistro_es_b:", "(\"true\" OR \"false\")", false);

		return solr.createQuery();
	}

	private String valEsAbierto(CmsSearchResource searchRes) {
		Date currentDate = new Date();
		Date startDate = startDate(searchRes);

		// Si no existe fecha de inicio de inscripcion
		if (startDate == null) {
			return "N/A";
		} else if (currentDate.after(startDate)){
			Date endDate = endDate(searchRes);

			// Si no existe fecha de fin
			if (endDate == null) {
				return "Abierto";
			}

			// Si la fecha actual es menor que la de fin
			else if (currentDate.before(endDate)) {
				return "Abierto";
			}

			// Si la fecha actual es mayor que la de fin
			else {
				return "Cerrado";
			}
		} else {

			// Si la fecha actual es menor que la de inicio
			return "Cerrado";
		}
	}

	private Date endDate(CmsSearchResource searchRes) {
		Date date = null;
		try {
			date = searchRes.getDateField("xmlfinfechains_es_dt");
		} catch (Exception e) {
			LOG.error("ERROR obteniendo fecha del evento", e);
		}
		return date;
	}

	private Date startDate(CmsSearchResource searchRes) {
		Date date = null;
		try {
			date = searchRes.getDateField("xmliniciofechains_es_dt");
		} catch (Exception e) {
			LOG.error("ERROR obteniendo xmliniciofechains_es_dt del evento", e);
		}
		return date;
	}
%>

<%
	// Creamos la query
	String q = solrQuery(request);
	request.setAttribute("solrQuery", q);

	// Ejecutamos controlador obteniendo eventos de solr
	AdminEventosController controller = new AdminEventosController(pageContext, request, response);
	controller.handleRequest();
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
	List events = (List) pMap.get("events");
	Map<String, Long> enrollsByEvent = (Map<String, Long>) pMap.get("enrollsByEvent");
	for (int i = 0; i < events.size(); i++) {
		CmsSearchResource event = (CmsSearchResource) events.get(i);
		try {
			Map<String, Object> map = new HashMap<String, Object>();

			String absPath = event.getRootPath();

			String linkEv = CmsJspTagLink.linkTagAction(absPath, request);

			HashMap<String, String> titleMap = new HashMap<String, String>();
			String title = event.getField("xmltitle_es_s");
			titleMap.put("text", title);
			titleMap.put("title", title);
			titleMap.put("href", linkEv);
			map.put("xmltitle_es_s", titleMap);

			String esAbierto = valEsAbierto(event);
			map.put("esAbierto", esAbierto);

			Date dateInicio = event.getDateField("xmldate_es_dt");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			map.put("xmldate_es_dt", sdf.format(dateInicio));

			String structureId = event.getStructureId().toString();
			Long nInscriptiones = enrollsByEvent.get(structureId);
			if (nInscriptiones == null) {

			}
			map.put("nInscripciones", nInscriptiones);

			String link = baseLink + "?e=" + structureId;
			map.put("link", link);

			records.add(map);
		} catch (Exception e) {
			LOG.error("ERROR tratando inscripcion a evento " + event, e);
		}
	}
	jObj.put("records", records);
	response.getWriter().print(jObj);
%>
