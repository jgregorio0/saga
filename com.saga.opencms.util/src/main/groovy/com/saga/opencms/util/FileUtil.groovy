package com.saga.opencms.util

import org.apache.commons.io.IOUtils
import org.json.JSONArray

class CmsFileUtil {

    // CSV file generated with Excel
    String csvEncode = "windows-1252"

    // CSV file created in UTF-8 encoding
    String csvUtf8Encode = "UTF-8"

    String idProceso;
    String csvFilePath;
    String jsonPath = "/.migration/json/navegacion.json";

    public void init(def idProceso, def csvFilePath){
        this.idProceso = idProceso
        this.csvFilePath = csvFilePath
    }

    def transform2Json(String csvPath, String jsonPath) {
        FileReader reader = new FileReader(csvPath)
        List<String> lines = IOUtils.readLines(reader)
        JSONArray jsonArray = new JSONArray();
        def header = [:]
        def rows = [:]

        //Cargamos el porcentaje
        def total = lines.size()
        Double porcentaje = new Double(0d)

        // Creating json
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            rows.clear()
            def words = line.tokenize(";")
            words.eachWithIndex{ word, j ->
                if (i == 0) {
                    header.put(j, word.toString())
                } else {
                    if (word != null ||
                            (word != null && word.toString() != null)) {
                        rows.put(header.get(j), word.toString())
                    }
                }
            }

            if (rows.size() > 0){
                jsonArray.put(rows)
            }

            porcentaje = (new Double(i) * 100d) / total;
        }

        // Replace characters to make it readable and ready
        String jsonStr = jsonArray.myArrayList.toString()
        jsonStr = jsonStr.replaceAll("\\{", "\n{").replaceAll(",", ",\n").replaceAll("[^\"]#", "#")

        // Writing json to file
        BufferedWriter bw = new BufferedWriter(new FileWriter(jsonPath))
        bw.write(jsonStr)
        bw.close()

        println "PROCESO FINALIZADO: \nGenerado json de ${jsonArray.myArrayList.size()} elementos en $jsonPath"
    }

    def encode(String inputPath, String inputEncode, String outputPath, String outputEncode) {
        char BYTE_ORDER_MARK = '\uFEFF';

        FileInputStream fis = new FileInputStream(inputPath)
        InputStreamReader inputStreamReader = new InputStreamReader(fis, inputEncode);
        char[] data = new char[1024];
        int i = inputStreamReader.read(data);

        FileOutputStream fos = new FileOutputStream(outputPath)
        Writer writer = new OutputStreamWriter(fos, outputEncode);

        String text = "";
        writer.write(BYTE_ORDER_MARK);
        while(i !=-1){
            String str = new String(data,0,i);
            text = text+str;
            i = inputStreamReader.read(data);
        }

        writer.append(text);

        writer.close();
        fis.close();
        fos.close();
    }

    def encode2Utf8(String path) {
        String pathUtf8 = path;
        int end = path.lastIndexOf(".")
        if (end <= 0) {
            end = path.length();
        }

        pathUtf8 = path.substring(0, end) + "-uft8";
        if (end > 0) {
            pathUtf8 = pathUtf8 + path.substring(end)
        }
        return pathUtf8
    }
}