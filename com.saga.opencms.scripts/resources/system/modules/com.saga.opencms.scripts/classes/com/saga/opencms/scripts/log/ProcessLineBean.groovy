package com.saga.opencms.scripts

import java.text.DecimalFormat
import java.text.SimpleDateFormat

public class ProcessLineBean {

	private String idProcess;
	private String userName;
	private int idLine;
	private Date date;
	private String message;
	private Double percentage;
	private Map<String,String> data;

	public ProcessLineBean(String idProcess, String userName, int idLine,
						   Date date, String message) {
		super();
		this.idProcess = idProcess;
		this.userName = userName;
		this.idLine = idLine;
		this.date = date;
		this.message = message;
	}
	public ProcessLineBean(String idProcess, String userName, int idLine,
						   Date date, String message, Double percentage) {
		super();
		this.idProcess = idProcess;
		this.userName = userName;
		this.idLine = idLine;
		this.date = date;
		this.message = message;
		this.percentage = percentage;
	}
	public ProcessLineBean(String idProcess, String userName, int idLine,
						   Date date, String message, Double percentage, Map<String,String> data) {
		super();
		this.idProcess = idProcess;
		this.userName = userName;
		this.idLine = idLine;
		this.date = date;
		this.message = message;
		this.percentage = percentage;
		this.data = data;
	}


	public String getIdProcess() {
		return idProcess;
	}
	public void setIdProcess(String idProcess) {
		this.idProcess = idProcess;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getIdLine() {
		return idLine;
	}
	public void setIdLine(int idLine) {
		this.idLine = idLine;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Double getPercentage() {
		return percentage;
	}
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public Map<String,String> addData(String key, String value){
		if(data==null)
			data = new HashMap<String,String>();
		data.put(key,value);
		return data;
	}
	public Map<String,String> getJsonData(){
		Map<String,String> dataJson = new HashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		DecimalFormat decf = new DecimalFormat("0.00");

		dataJson.put("idProcess", idProcess);
		dataJson.put("userName", userName);
		dataJson.put("idLine", idLine+"");
		dataJson.put("date", df.format(date));
		dataJson.put("message", message);
		if(percentage==null){
			percentage = new Double("0.0");
		}
		dataJson.put("percentage", decf.format(percentage));
		//Incluimos todo los valores del data
		if(data!=null)
		{
			for(String k:data.keySet()){
				dataJson.put(k, data.get(k));
			}
		}


		return dataJson;
	}

	@Override
	public String toString() {
		return "ProcessLineBean [idProcess=" + idProcess + ", userName="
		+ userName + ", idLine=" + idLine + ", date=" + date
		+ ", message=" + message + ", percentage=" + percentage + "]";
	}
}