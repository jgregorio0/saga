package com.saga.opencms.excel.model;

import java.util.Comparator;

/**
 * Created by Jesus on 18/10/2015.
 */
public class Cell implements Comparator<Cell> {

    private org.apache.poi.ss.usermodel.Cell POICell;
    private String stringValue;
    private Integer indexRow;
    private Integer indexColumn;

    public Cell() {
    }

//    public Cell(String value, Integer indexRow, Integer indexColumn) {
//        this.value = value;
//        this.indexRow = indexRow;
//        this.indexColumn = indexColumn;
//    }

    public Cell(org.apache.poi.ss.usermodel.Cell POICell) {
        this.POICell = POICell;
        stringValue = initStringValue();
        indexRow = POICell.getRowIndex();
        indexColumn = POICell.getColumnIndex();
    }

    public Integer getIndexRow() {
        return indexRow;
    }

    public void setIndexRow(Integer indexRow) {
        this.indexRow = indexRow;
    }

    public Integer getIndexColumn() {
        return indexColumn;
    }

    public void setIndexColumn(Integer indexColumn) {
        this.indexColumn = indexColumn;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public org.apache.poi.ss.usermodel.Cell getPOICell() {
        return POICell;
    }

    public void setPOICell(org.apache.poi.ss.usermodel.Cell POICell) {
        this.POICell = POICell;
    }

    @Override
    public int compare(Cell c1, Cell c2) {
        int p = c1.getIndexRow() - c2.getIndexRow();
        if (p == 0) {
            p = c1.getIndexColumn() - c2.getIndexColumn();
        }
        return p;
    }

    public String initStringValue() {
        int cellType = POICell.getCellType();
        if (cellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING) {
            setStringValue(POICell.getStringCellValue());
        } else if (cellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
            setStringValue(String.valueOf(POICell.getNumericCellValue()));
        } else if (cellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN) {
            setStringValue(String.valueOf(POICell.getBooleanCellValue()));
        } else if (cellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA) {
            setStringValue(POICell.getCellFormula());
        } else {
            setStringValue(null);
        }
        return stringValue;
    }
}