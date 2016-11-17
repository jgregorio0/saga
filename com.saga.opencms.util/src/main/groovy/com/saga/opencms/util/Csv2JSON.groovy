package com.jesus.opencms.util
import org.apache.commons.io.IOUtils
import org.opencms.json.JSONArray
import org.opencms.json.JSONObject

String basePath = "C:\\Users\\jgregorio\\Desktop\\Talemnology\\anonimize";
// CSV file generated with Excel
String csvPath = basePath + "\\anonimize.csv"
String csvEncode = "windows-1252"

// CSV file created in UTF-8 encoding
String csvUtf8Path = basePath + "\\anonimize-utf8.csv"
String csvUtf8Encode = "UTF-8"

// JSON file created in UTF-8 encoding
String jsonPath = basePath + "\\anonimize.json"

// Encoding to UTF-8
//Encode.encodeAnsi2Utf8(csvPath, csvEncode, csvUtf8Path, csvUtf8Encode)

// CSV to JSON
//Csv2Json.transform(csvUtf8Path, pathJson)

try {
    // Codificamos en utf-8
    encode(csvPath, csvEncode, csvUtf8Path, csvUtf8Encode)

    // Transform csv to json
    transform(csvUtf8Path, jsonPath)
} catch (Exception e) {
    e.printStackTrace()
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

def transform(String csvPath, String jsonPath) {
    FileReader reader = new FileReader(csvPath)
    List<String> lines = IOUtils.readLines(reader)
    JSONArray jsonArray = new JSONArray();
    def header = [:]
//    def rows = [:]

// Creating json
    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
//        rows.clear()
        JSONObject jRow = new JSONObject();
        def words = line.tokenize(";")
        words.eachWithIndex{ word, j ->
            if (i == 0) {
                header.put(j, word.toString())
            } else {
                if (word != null ||
                        (word != null && word.toString() != null)) {
                    jRow.put(header.get(j), word.toString())
                }
            }
        }

        if (jRow.length() > 0){
            jsonArray.put(jRow)
        }
    }
// Replace characters to make it readable and ready
    String jsonStr = jsonArray.toString(1)
//    jsonStr = jsonStr.replaceAll("\\{", "\n{").replaceAll(",", ",\n").replaceAll("[^\"]#", "#")
    jsonStr = jsonStr.replaceAll("﻿", "")

// Writing json to file
    BufferedWriter bw = new BufferedWriter(new FileWriter(jsonPath))
    bw.write(jsonStr)
    bw.close()

    println "PROCESO FINALIZADO: \nGenerado json de ${jsonArray.length()} elementos en $jsonPath"
}