package com.saga.opencms.excel;

import com.saga.opencms.excel.model.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jesus on 18/10/2015.
 */
public class Workbook {

    private final org.apache.poi.ss.usermodel.Workbook POIWorkbook;

    private List<Sheet> sheets;

    /**
     * Read workbook from InputStream
     * @param stream
     * @throws java.io.IOException
     */
    public Workbook(InputStream stream) throws IOException {
        POIWorkbook = new WorkbookReader().read(stream);
        initWorkbook();
    }

    /**
     * Read workbook from InputStream
     * @param stream
     * @throws java.io.IOException
     */
    public Workbook(InputStream stream, String sheetName) throws IOException {
        POIWorkbook = new WorkbookReader().read(stream);
        initWorkbook(sheetName);
    }

    /**
     * Initialize Workbook model
     */
    private void initWorkbook() {
        sheets = new ArrayList<Sheet>();
        int numberOfSheets = POIWorkbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            org.apache.poi.ss.usermodel.Sheet POISheet = POIWorkbook.getSheetAt(i);
            Sheet sheet = new Sheet(POISheet);
            sheets.add(sheet);
        }
    }

    /**
     * Initialize Workbook model
     */
    private void initWorkbook(String sheetName) {
        sheets = new ArrayList<Sheet>();
        int numberOfSheets = POIWorkbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            org.apache.poi.ss.usermodel.Sheet POISheet = POIWorkbook.getSheetAt(i);
            if (POISheet.getSheetName().equals(sheetName)) {
                Sheet sheet = new Sheet(POISheet);
                sheets.add(sheet);
            }
        }
    }

    public org.apache.poi.ss.usermodel.Workbook getPOIWorkbook() {
        return POIWorkbook;
    }

    public List<Sheet> getSheets() {
        return sheets;
    }

    public void setSheets(List<Sheet> sheets) {
        this.sheets = sheets;
    }
}
