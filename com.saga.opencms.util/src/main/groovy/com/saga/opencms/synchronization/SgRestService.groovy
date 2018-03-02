package com.saga.opencms.synchronization

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.IOUtils
import org.apache.commons.logging.Log
import org.opencms.main.CmsLog

/**
 * Created by jgregorio on 02/03/2018.
 */
class SgRestService {
    private static final Log LOG = CmsLog.getLog(SgRestService.class);

    private Exception exception;

    public ArrayList<LinkedHashMap<?, ?>> executeWSList(String wsUrl){
        try {
            //Lanzamos petición http al WS con los parametros indicados
            URL url = new URL(wsUrl);
            URLConnection uc = url.openConnection();
            uc.connect();

            //Obtenemos la respuesta y la transformamos a UTF8
            String respuesta = IOUtils.toString(uc.getInputStream()).trim();
            byte[] bytes = respuesta.getBytes();
            respuesta = new String(bytes, "UTF-8");

            //Mapeamos el json obtenido a un Map
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<LinkedHashMap<?, ?>> jsonData = mapper.readValue(bytes, ArrayList.class);

            //Devolvemos el json en formato Lista de Map
            return jsonData;
        } catch(Exception e) {
            LOG.error("executing wsUrl: " + wsUrl);
            this.exception = e;
            return new ArrayList<LinkedHashMap<?, ?>>();
        }
    }

    public LinkedHashMap<?, ?> executeWSObject(String wsUrl){
        try {
            //Lanzamos petición http al WS con los parametros indicados
            URL url = new URL(wsUrl);
            URLConnection uc = url.openConnection();
            uc.connect();

            //Obtenemos la respuesta y la transformamos a UTF8
            String respuesta = IOUtils.toString(uc.getInputStream()).trim();
            byte[] bytes = respuesta.getBytes();
            respuesta = new String(bytes, "UTF-8");

            //Mapeamos el json obtenido a un Map
            ObjectMapper mapper = new ObjectMapper();
            LinkedHashMap<?, ?> jsonData = mapper.readValue(bytes, LinkedHashMap.class);

            //Devolvemos el json en formato Map
            return jsonData;

        } catch(Exception e) {
            LOG.error("executing wsUrl: " + wsUrl);
            this.exception = e;
            return new LinkedHashMap();
        }
    }

    public String executeWSString(String wsUrl){
        try {
            //Lanzamos petición http al WS con los parametros indicados
            URL url = new URL(wsUrl);
            URLConnection uc = url.openConnection();
            uc.connect();

            //Obtenemos la respuesta y la transformamos a UTF8
            String respuesta = IOUtils.toString(uc.getInputStream()).trim();
            byte[] bytes = respuesta.getBytes();
            return new String(bytes, "UTF-8");
        } catch(Exception e) {
            LOG.error("executing wsUrl: " + wsUrl);
            this.exception = e;
            return null;
        }
    }

    public ArrayList<SgResource> executeWSResource(String wsUrl){
        try {
            //Lanzamos petición http al WS con los parametros indicados
            URL url = new URL(wsUrl);
            URLConnection uc = url.openConnection();
            uc.connect();

            //Obtenemos la respuesta y la transformamos a UTF8
            String respuesta = IOUtils.toString(uc.getInputStream()).trim();
            byte[] bytes = respuesta.getBytes();
            respuesta = new String(bytes, "UTF-8");

            //Mapeamos el json obtenido a un arraylist de Resource
            ObjectMapper mapper = new ObjectMapper();
            ArrayList jsonData = (ArrayList)mapper.readValue(bytes, ArrayList.class);
            ArrayList resources = new ArrayList();
            Iterator var9 = jsonData.iterator();

            while(var9.hasNext()) {
                Map data = (Map)var9.next();
                resources.add(new SgResource(data));
            }

            return resources;
        } catch (Exception e) {
            LOG.error("executing wsUrl: " + wsUrl);
            this.exception = e;
            return new ArrayList();
        }
    }

    public Exception getException() {
        return exception;
    }
}
