package com.saga.cedinox.suscriptores.util


import org.opencms.json.JSONArray
import org.opencms.json.JSONObject
import org.opencms.main.CmsException

class SgJsonRes {

    public static final String ERROR = "error"
    public static final String ERROR_MSG = "errorMsg"
    public static final String ERROR_TRACE = "errorTrace"
    public static final String TOTAL = "total"
    public static final String DATA_SIZE = "dataSize"
    public static final String DATA = "data"
    public static final String QUERY = "query"

    SgJsonRes() {}

    public static JSONObject errorJResponse(Exception e) {
        JSONObject jRes = new JSONObject();
        jRes.put(ERROR, true);
        jRes.put(ERROR_MSG, e.getMessage());
        jRes.put(ERROR_TRACE, CmsException.getStackTraceAsString(e));
        return jRes;
    }

    public static JSONObject successJResponse(long total, JSONArray datas, String query) {
        JSONObject jRes = successJResponse(total, datas);
        jRes.put(QUERY, query);
        return jRes;
    }

    public static JSONObject successJResponse(long total, JSONArray datas) {
        JSONObject jRes = new JSONObject();
        jRes.put(ERROR, false);
        jRes.put(TOTAL, total);
        jRes.put(DATA_SIZE, datas.length());
        jRes.put(DATA, datas);
        return jRes;
    }
}
