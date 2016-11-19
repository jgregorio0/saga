<%@ page import="com.saga.ausape.zonaprivada.inscripciones.service.InscripcionServiceImpl" %>
<%@ page import="com.saga.ausape.zonaprivada.inscripciones.util.SgSolr" %>
<%@ page import="com.saga.sagasuite.core.CoreModuleAction" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.jsp.CmsJspTagLink" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.search.CmsSearchException" %>
<%@ page import="org.opencms.search.CmsSearchResource" %>
<%@ page import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" %>
<%!
	private final Log LOG = CmsLog.getLog(this.getClass());

	private static final String P_URL = "u";
	private static final String P_PAGE = "page";
	private static final String P_SIZE = "size";
	private static final String P_SORT = "sort";
	private static final String P_ORDER = "order";
	private static final String P_ROWS = "rows";
	private static final String P_S_TYPE = "t";

	private CmsSolrResultList solrResults(CmsObject cmso, String type, String start, String rows){
		SgSolr solrUtil = new SgSolr(cmso);

		solrUtil.addQParam(SgSolr.Q_TYPE, type);
		solrUtil.subQParam(SgSolr.Q_START, start, false);
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

	private Long obtainNumInsc(CmsObject cmso, InscripcionServiceImpl insService, CmsSearchResource searchRes) {
		CmsResource resource = null;
		try {
			resource = cmso.readResource(searchRes.getStructureId());
		} catch (CmsException e) {
			LOG.error("ERROR obteniendo recurso para contar", e);
		}
		LOG.debug("El recurso " + resource.toString());
		LOG.debug("Inscripcion service " + insService);
		return insService.countInscEvento(resource);
	}

	private String obtainStart(String pPage, String pSize) {
		String start = null;
		try {
			Integer pageInt = Integer.valueOf(pPage);
			Integer sizeInt = Integer.valueOf(pSize);
			Integer startInt = pageInt * sizeInt;
			start = startInt.toString();
		} catch (NumberFormatException e) {
			LOG.error("ERROR calculando parametro start para solr", e);
		}
		return start;
	}
%>

<%
	// Inicializamos
	JSONObject jObj = new JSONObject();
	InscripcionServiceImpl insService = CoreModuleAction.getSpringContext("AusapeConfigZP")
					.getBean(InscripcionServiceImpl.class);
	String uri = request.getParameter(P_URL);
	String baseLink = CmsJspTagLink.linkTagAction(uri, request);
	String pPage = request.getParameter(P_PAGE);
	String pSize = request.getParameter(P_SIZE);
	String pSort = request.getParameter(P_SORT);
	String pOrder = request.getParameter(P_ORDER);
	String pRows = request.getParameter(P_ROWS);
	String pSType = request.getParameter(P_S_TYPE);

	String start = obtainStart(pPage, pSize);
	if (pRows == null) {
		pRows = pSize;
	}

	Integer sizeInt = null;
	try {
		sizeInt = Integer.valueOf(pSize);
	} catch (NumberFormatException e) {
		LOG.error("Size and page parameter must be integer", e);
	}

	CmsObject cmso = CmsFlexController.getCmsObject(request);

	LOG.debug("pSType: " + pSType + " / start: " + start + " / pRows: " + pRows);
	CmsSolrResultList results = solrResults(cmso, pSType, start, pRows);

	// Obtenemos los mensajes
	List<String> errors = new ArrayList<String>();
	List<String> infos = new ArrayList<String>();//TODO recoger errores
	Map<String, Object> msgs = new HashMap<String, Object>();
	msgs.put("errors", errors);
	msgs.put("infos", infos);
	jObj.put("msgs", msgs);


	// Obtenemos el estado de la paginacion
	Map<String, Object> state = new HashMap<String, Object>(); //TODO recoger el estado
	long totalRecords = results.getNumFound();
	state.put("totalRecords", totalRecords);
	state.put("totalPages", totalRecords/sizeInt);
	jObj.put("state", state);

	// Obtenemos los datos
	List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
	for (int i = 0; i < results.size(); i++) {
		CmsSearchResource searchRes = results.get(i);

		try {
			Map<String, Object> map = new HashMap<String, Object>();

			String absPath = searchRes.getRootPath();

			String linkEv = CmsJspTagLink.linkTagAction(absPath, request);

			HashMap<String, String> titleMap = new HashMap<String, String>();
			String title = searchRes.getField("xmltitle_es_s");
			titleMap.put("text", title);
			titleMap.put("title", title);
			titleMap.put("href", linkEv);
			map.put("title", titleMap);

			String esAbierto = valEsAbierto(searchRes);
			map.put("esAbierto", esAbierto);

			Date dateInicio = searchRes.getDateField("xmldate_es_dt");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			map.put("fechaInicio", sdf.format(dateInicio));

			Long nInscriptiones = obtainNumInsc(cmso, insService, searchRes);
			map.put("nInscripciones", nInscriptiones);

			String link = baseLink + "?e=" + searchRes.getStructureId().toString();
			map.put("link", link);

			records.add(map);
		} catch (Exception e) {
			LOG.error("ERROR tratando inscripcion a eventos " + searchRes, e);
		}
	}
	jObj.put("records", records);
	response.getWriter().print(jObj);
%>