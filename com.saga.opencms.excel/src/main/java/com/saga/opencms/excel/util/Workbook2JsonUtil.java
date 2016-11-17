package com.saga.opencms.excel.util;

import org.json.JSONObject;

/**
 * Created by Jesus on 18/10/2015.
 */
public class Workbook2JsonUtil {

    private static final String WORKBOOK = "workbook";

    private JSONObject json;

//    public Workbook2JsonUtil(Workbook workbook) {
//        json = new JSONObject();
//        Iterator<org.apache.poi.ss.usermodel.Sheet> itWorkbook = workbook.getPOIWorkbook().sheetIterator();
//        while (itWorkbook.hasNext()){
//            org.apache.poi.ss.usermodel.Sheet POISheet = itWorkbook.next();
//            Sheet sheet = new Sheet(POISheet);
            // TODO crear Json
//            json.put(WORKBOOK, )
//            String JSONSheet = "";
//            Iterator<Row> itSheet = POISheet.iterator();
//            while (itSheet.hasNext()){
//                Row POIRow = itSheet.next();
//                String JSONRow = "";
//                Iterator<Cell> itRow = POIRow.iterator();
//                while (itRow.hasNext()) {
//                    Cell POICell = itRow.next();
//                    String JSONCell = "";
//                    if (POICell.getCellType() == Cell.CELL_TYPE_STRING) {
//                        String stringCellValue = POICell.getStringCellValue();
//
//                    }
//
//                }
//            }
//        }
//    }
}
