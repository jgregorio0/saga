import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jesus on 01/04/2016.
 */
public class Test {

    JSONObject json;

    @org.junit.Test
    public void testCreateJSON() throws Exception {
        String jsonStr = "{ \"fq\" : " +
                "{\"parent-folders\":\"/sites/default/\", \"con_locales\" : [\"es\", \"en\"]}, \"type\":[\"OR\", \"alkacon-v8-webformreport\",\"sigmagridtable\"]}";
        json = new JSONObject(jsonStr);
        System.out.println(json);
    }

//    @org.junit.Test
//    public void testIterateJSON() throws Exception {
//        testCreateJSON();
//        Iterator<String> itIds = json.keys();
//        while (itIds.hasNext()) {
//            String id = itIds.next();
//            System.out.println("Id: " + id);
//            Object filters = json.get(id);
////            JSONObject filters = json.getJSONObject(id);
//            if (filters instanceof JSONObject) {
//
//                Iterator<String> itNames = (JSONObject)filters.keys();
//                while (itNames.hasNext()) {
//                    String name = itNames.next();
//                    System.out.println("Name: " + name);
//
//                    Object valueObj = filters.get(name);
//                    CmsSolrUtil.Param param = null;
//                    if (valueObj instanceof String) {
//                        String valueStr = (String)valueObj;
//                        param = new CmsSolrUtil.Param(id, name, valueStr);
//                    } else if (valueObj instanceof JSONArray) {
//                        JSONArray valueJArr = (JSONArray)valueObj;
//                        param = new CmsSolrUtil.Param(id, name, null, null);
//                        for (int i = 0; i < valueJArr.length(); i++) {
//                            String valueStr = valueJArr.getString(i);
//                            if (i == 0) {
//                                param.setDel(valueStr);
//                            } else {
//                                param.addValues(valueStr);
//                            }
//                        }
//                    }
//                    if (param != null) {
//                        System.out.println(param.toString());
//                    } else {
//                        System.out.println("Param Mierda null");
//                    }
//            } else if (filters instanceof JSONArray) {
//                            JSONArray valueJArr = (JSONArray)filters;
//                            param = new CmsSolrUtil.Param(id, name, null, null);
//                            for (int i = 0; i < valueJArr.length(); i++) {
//                                String valueStr = valueJArr.getString(i);
//                                if (i == 0) {
//                                    param.setDel(valueStr);
//                                } else {
//                                    param.addValues(valueStr);
//                                }
//                            }
//                        }
//                        if (param != null) {
//                            System.out.println(param.toString());
//                        } else {
//                            System.out.println("Param Mierda null");
//                        }
//            }
//
//
//            }
//        }
//    }

    @org.junit.Test
    public void testToJSONArray() throws Exception {
        String jsonStr = "{\"url\" : \"rootPath\", \"title\" : \"Title_prop\"}";
        JSONObject jsonFields = new JSONObject(jsonStr);
        System.out.println(jsonFields);
        Map<String, String> results = new HashMap<String, String>();
//        results.put()

    }

}
