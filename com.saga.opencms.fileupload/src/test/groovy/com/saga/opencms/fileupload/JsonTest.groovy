import org.json.JSONObject
import org.opencms.json.JSONArray

/**
 * Created by Jesus on 17/01/2016.
 */

infos = ["Se ha subido correctamente", "chupame el pie"]

//JSONObject json = new JSONObject(infos)
//print json
JSONArray jsonArray = new JSONArray(infos)
println jsonArray
JSONObject json = new JSONObject();
json.put("infos", infos)
println json