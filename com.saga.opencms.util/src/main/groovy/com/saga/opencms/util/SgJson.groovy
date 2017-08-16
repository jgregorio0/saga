package com.saga.opencms.util

import org.apache.commons.logging.Log
import org.opencms.json.JSONArray
import org.opencms.json.JSONObject
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
/**
 * Created by jgregorio on 09/09/2016.
 */
public class SgJson {

	private static final Log LOG = CmsLog.getLog(SgJson.class);
	public static final String ERROR = "error"
	public static final String ERROR_MSG = "errorMsg"
	public static final String ERROR_TRACE = "errorTrace"
	public static final String TOTAL = "total"
	public static final String DATA_SIZE = "dataSize"
	public static final String DATA = "data"
	public static final String FIELDS = "fields"
	public static final String QUERY = "query"

	public static JSONObject errorJResponse(Exception e){
		JSONObject jRes = new JSONObject();
		jRes.put(ERROR, true);
		jRes.put(ERROR_MSG, e.getMessage());
		jRes.put(ERROR_TRACE, CmsException.getStackTraceAsString(e));
		return jRes;
	}

	public static JSONObject successJResponse(def total, JSONArray datas, String query, String fields){
		JSONObject jRes = successJResponse(total, datas, query);
		jRes.put(FIELDS, fields);
		return jRes;
	}

	public static JSONObject successJResponse(def total, JSONArray datas, String query){
		JSONObject jRes = successJResponse(total, datas);
		jRes.put(QUERY, query);
		return jRes;
	}

	public static JSONObject successJResponse(def total, JSONArray datas){
		return successJResponse(Long.valueOf(total), datas);
	}

	public static JSONObject successJResponse(long total, JSONArray datas){
		JSONObject jRes = new JSONObject();
		jRes.put(ERROR, false);
		jRes.put(TOTAL, total);
		jRes.put(DATA_SIZE, datas.length());
		jRes.put(DATA, datas);
		return jRes;
	}

	public static JSONObject jResutls(String jsonStr){
		JSONObject jResult = new JSONObject();
		try {
			jResult = new JSONObject(jsonStr);
		} catch (Exception e) {
			LOG.debug("parsing json resutls", e);
		}
		return jResult;
	}

	public static Object getJField(JSONObject json, String field){
		Object o = null;
		try {
			o = json.get(field);
		} catch (Exception e) {
			LOG.debug("getting field $field from json $json", e);
		}
		return o;
	}
}