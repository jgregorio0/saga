package com.saga.opencms.scripts

import org.apache.log4j.Logger
import org.json.simple.JSONValue
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.json.JSONArray
import org.opencms.loader.CmsLoaderException
import org.opencms.main.CmsException
import org.opencms.main.CmsIllegalArgumentException
import org.opencms.main.OpenCms

import java.text.DecimalFormat
import java.text.SimpleDateFormat

public class SgReportManager {

	private static final String FOLDER_REPORTS = "/system/scripts-logs/";
	private static final String EXTENSION_REPORTS = ".html";

	private static SgReportManager instance = null;

	private CmsObject cmsObject;

	private Map<String,List<ProcessLineBean>> processReport;

	protected static final Logger log = Logger.getLogger(SgReportManager.class);

	protected SgReportManager(CmsObject cmsObject) {
		processReport = new HashMap<String,List<ProcessLineBean>>();
		this.cmsObject = cmsObject;
	}

	protected SgReportManager() {
		processReport = new HashMap<String,List<ProcessLineBean>>();
	}

	public static SgReportManager getInstance(CmsObject cmsObject) {
		if(instance == null) {
			instance = new SgReportManager(cmsObject);
		}else{
			instance.cmsObject = cmsObject;
		}
		return instance;
	}

	public static SgReportManager getInstance() {
		if(instance == null) {
			instance = new SgReportManager();
		}
		return instance;
	}

	@Deprecated
	public void addMessage(String idProcess, String user, String message){

		//Vemos primero si existe ya el proceso para ver la linea actual
		int numLine = 1;
		List<ProcessLineBean> currentList;
		if(processReport.containsKey(idProcess)){
			currentList = processReport.get(idProcess);
			numLine = currentList.size()+1;
		}else{
			currentList = new ArrayList<ProcessLineBean>();
		}

		//Creamos la linea del report
		ProcessLineBean line = new ProcessLineBean(idProcess, user, numLine, new Date(), message);

		//Añadimos la linea
		currentList.add(line);

		//Actualizamos el proceso
		processReport.put(idProcess,currentList);
	}

	public void addMessage(String idProcess, String user, String message, Double percentage){

		//Vemos primero si existe ya el proceso para ver la linea actual
		int numLine = 1;
		List<ProcessLineBean> currentList;
		if(processReport.containsKey(idProcess)){
			currentList = processReport.get(idProcess);
			numLine = currentList.size()+1;
		}else{
			currentList = new ArrayList<ProcessLineBean>();
		}

		//Creamos la linea del report
		ProcessLineBean line = new ProcessLineBean(idProcess, user, numLine, new Date(), message, percentage);

		//Añadimos la linea
		currentList.add(line);

		//Actualizamos el proceso
		processReport.put(idProcess,currentList);
	}

	public List<ProcessLineBean> getReport(String idProcess){
		return processReport.get(idProcess);
	}

	public JSONArray getJsonReport(String idProcess){
		if(processReport.containsKey(idProcess)){
			//Devolvemos la lista de procesos 
			List<ProcessLineBean> currentList = processReport.get(idProcess);
			JSONArray jsonArray = new JSONArray(getDataList(currentList));
			return jsonArray;
		}else{
			//Devolvemos una lista vacia
			List<ProcessLineBean> currentList = new ArrayList<ProcessLineBean>();
			JSONArray jsonArray = new JSONArray(getDataList(currentList));
			return jsonArray;
		}
	}

	public String getJsonReportString(String idProcess){
		if(processReport.containsKey(idProcess)){
			//Devolvemos la lista de procesos 
			List<ProcessLineBean> currentList = processReport.get(idProcess);
			JSONArray jsonArray = new JSONArray(getDataList(currentList));
			return jsonArray.toString();
		}else{
			//Devolvemos una lista vacia
			List<ProcessLineBean> currentList = new ArrayList<ProcessLineBean>();
			JSONArray jsonArray = new JSONArray(getDataList(currentList));
			return jsonArray.toString();
		}
	}

	public JSONArray getJsonReport(String idProcess, Integer lastLine){
		if(lastLine==null){
			lastLine = 0;
		}
		if(processReport.containsKey(idProcess)){
			//Devolvemos la lista de procesos 
			List<ProcessLineBean> currentList = processReport.get(idProcess);

			//Recorremos la lista solo a partir de la linea indicada
			List<ProcessLineBean> auxList = new ArrayList<ProcessLineBean>();
			if(lastLine<=currentList.size()){
				for(int i=lastLine-1;i<currentList.size();i++){
					auxList.add(currentList.get(i));
				}
			}

			JSONArray jsonArray = new JSONArray(getDataList(auxList));
			return jsonArray;
		}else{
			//Devolvemos una lista vacia
			List<ProcessLineBean> currentList = new ArrayList<ProcessLineBean>();
			JSONArray jsonArray = new JSONArray(getDataList(currentList));
			return jsonArray;
		}
	}

	public String getJsonReportString(String idProcess, Integer lastLine){
		if(processReport.containsKey(idProcess)){
			//Devolvemos la lista de procesos 
			List<ProcessLineBean> currentList = processReport.get(idProcess);

			//Recorremos la lista solo a partir de la linea indicada
			List<ProcessLineBean> auxList = new ArrayList<ProcessLineBean>();
			if(lastLine<=currentList.size()){
				for(int i=lastLine-1;i<currentList.size();i++){
					auxList.add(currentList.get(i));
				}
			}

			List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();

			JSONArray jsonArray = new JSONArray();
			for(ProcessLineBean l: currentList){
				dataList.add(l.getJsonData());
				jsonArray.put(l.getJsonData());
			}

			String jsonText = JSONValue.toJSONString(getDataList(auxList));
			//return jsonText;
			return jsonArray.toString();
		}else{
			//Devolvemos una lista vacia
			List<ProcessLineBean> currentList = new ArrayList<ProcessLineBean>();
			String jsonText = JSONValue.toJSONString(getDataList(currentList));
			return jsonText;
		}
	}

	/**
	 * Metodo que se ejecutará cuando finalice el proceso. Se encargará de borrar el resultado del objeto en memoria y escribir todo el resultado en un recurso de tipo plano en OpenCms
	 * @param idProcess
	 */
	public void finishReport(String idProcess){
		if(processReport.containsKey(idProcess)){
			//Obtenemos la lista completa
			List<ProcessLineBean> currentList = processReport.get(idProcess);

			//Obtenemos el informe en String
			String stringReport = getStringReport(currentList);

			//Creamos el recurso en OpenCms
			if(cmsObject!=null){
				createResourceReport(idProcess, stringReport);
			}

			//Borramos la lista del proceso
			processReport.remove(idProcess);
		}
	}

	public String getStringReport(List<ProcessLineBean> lines){
		StringBuffer sb = new StringBuffer();

		if(lines!=null && lines.size()>0){
			//Cabecera del informe
			sb.append("Proceso: "+lines.get(0).getIdProcess()+"\n");
			sb.append("Usuario: "+lines.get(0).getUserName()+"\n");

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			DecimalFormat decf = new DecimalFormat("0.00");

			//Recorremos todas las lineas y las pintamos
			for(ProcessLineBean l:lines){
				if(l.getPercentage()!=null)
					sb.append(l.getIdLine()+" ("+df.format(l.getDate())+"): "+l.getMessage()+" ---------> "+decf.format(l.getPercentage())+"% \n");
				else
					sb.append(l.getIdLine()+" ("+df.format(l.getDate())+"): "+l.getMessage()+"\n");
			}

		}

		return sb.toString();
	}

	private void createResourceReport (String idProceso, String report){

		//Nombre del recurso: AÑO-MES-DIA-HORA-MINUTO-SEGUNDO-IDPROCESO.log
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		String resourcename = FOLDER_REPORTS+df.format(now)+"-"+idProceso+EXTENSION_REPORTS;

		int cont = 1;
		while(cmsObject.existsResource(resourcename)){
			resourcename = resourcename.replace(EXTENSION_REPORTS, "-"+cont+EXTENSION_REPORTS);
			cont++;
		}

		//Comprobamos si existe la carpeta para los logs
		if(!cmsObject.existsResource(FOLDER_REPORTS)){
			//Creamos la carpeta y la publicamos
			try{
				cmsObject.createResource(FOLDER_REPORTS, OpenCms.getResourceManager().getResourceType("folder"));
				OpenCms.getPublishManager().publishResource(cmsObject, FOLDER_REPORTS);
				OpenCms.getPublishManager().waitWhileRunning(10000);
			}catch (CmsException ex){
				ex.printStackTrace();
				log.error("No se ha podido crear la carpeta de logs: "+FOLDER_REPORTS+". Causa: "+ex.getCause());
			} catch (Exception e) {
				log.error("No se ha podido publicar la carpeta de logs: "+FOLDER_REPORTS+". Causa: "+e.getCause());
				e.printStackTrace();
			}
		}

		//Creamos el recurso en OpenCms
		try {
			CmsResource cmsResource = cmsObject.createResource(resourcename, OpenCms.getResourceManager().getResourceType("plain"));
			CmsFile cmsFile = new CmsFile(cmsResource);
			cmsFile.setContents(report.getBytes());
			cmsObject.writeFile(cmsFile);
			cmsObject.unlockResource(cmsFile);
		} catch (CmsIllegalArgumentException e) {
			e.printStackTrace();
			log.error("Error al crear el recurso con el resultado del proceso "+idProceso+". Causa: "+e.getCause());
		} catch (CmsLoaderException e) {
			e.printStackTrace();
			log.error("Error al crear el recurso con el resultado del proceso "+idProceso+". Causa: "+e.getCause());
		} catch (CmsException e) {
			e.printStackTrace();
			log.error("Error al crear el recurso con el resultado del proceso "+idProceso+". Causa: "+e.getCause());
		}

	}

	public List<Map<String,String>> getDataList(List<ProcessLineBean> lines){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();

		for(ProcessLineBean l: lines){
			dataList.add(l.getJsonData());
		}

		return dataList;
	}

}
