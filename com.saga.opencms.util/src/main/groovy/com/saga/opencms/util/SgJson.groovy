package com.saga.opencms.util

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.opencms.json.I_JSONString
import org.opencms.json.JSONArray
import org.opencms.json.JSONException
import org.opencms.json.JSONObject
import org.opencms.main.CmsLog

/**
 * Created by jgregorio on 09/09/2016.
 */
public class SgJson {
	
	private static final Log LOG = CmsLog.getLog(SgJson.class);

	private StringBuffer bf;
	private String sep;

	public SgJSON(){
		bf = new StringBuffer();
		sep = ", ";
	}

	/**
	 * Add value par using JSON format
	 * @param key
	 * @param value
	 */
	public void addJSONValuePar(String key, Object value){
		if (bf.length() > 0) {
			bf.append(sep);
		}
		bf.append(toJSONValuePar(key, value));
	}

	/**
	 * Print JSON format including parameters loaded previously
	 * @return
	 */
	public String printJSON(){
		return "{" + bf.toString() + "}";
	}

	/**
	 * Validate param is not empty
	 * @param param
	 * @return
	 */
	public static boolean validate(String param){
		String clParam = cleanParam(param);
		return !StringUtils.isEmpty(clParam);
	}

	/**
	 * Clean whitespace
	 * @param param
	 * @return
	 */
	public static String cleanParam(String param) {
		return param != null ? param.trim() : param;
	}

	/**
	 * Get param and clean whitespace
	 * @param json
	 * @param paramName
	 * @return
	 */
	public static String getString(JSONObject json, String paramName) throws JSONException {
		return cleanParam(json.getString(paramName));
	}

	/**
	 * Return string value for JSON response.
	 * For example, it depends on class it shows {string: "test"} or {int: 1}.
	 * @param value
	 * @return
	 */
	public static String toJSONValue(Object value) {
		String res = "null";
		if(value != null && !value.equals((Object)null)) {
			if(value instanceof I_JSONString) {
				String o = null;
				try {
					o = ((I_JSONString)value).toJSONString();
				} catch (Exception var3) {
					LOG.error("ERROR parsing to JSON value " + value, var3);
				}

				if(o instanceof String) {
					res = (String)o;
				} else {
					LOG.error("ERROR parsing to JSON. Bad value from " + value);
				}
			} else {
				try {
					res = value instanceof Number?JSONObject.numberToString((Number) value):(!(value instanceof Boolean) && !(value instanceof JSONObject) && !(value instanceof JSONArray)?(value instanceof Map?(new JSONObject((Map)value)).toString():(value instanceof Collection ?(new JSONArray((Collection)value)).toString():(value.getClass().isArray()?(new JSONArray(value)).toString():JSONObject.quote(value.toString())))):value.toString());
				} catch (JSONException e) {
					LOG.error("ERROR parsing to JSON value " + value, e);
				}
			}
		}
		return res;
	}

	/**
	 * Format par of values to JSON
	 * @param key
	 * @param value
	 * @return
	 */
	public static String toJSONValuePar(String key, Object value) {
		return toJSONValue(key) + ": " + toJSONValue(value);
	}
}