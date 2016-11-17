package com.saga.opencms.excel.model;

import java.util.List;

/**
 * Created by Jesus on 18/10/2015.
 */
public class Row {

    org.apache.poi.ss.usermodel.Row POIRow;
    private Integer index;
    List<Cell> cells;

    public Row (Integer index, List<Cell> cells){
        this.index = index;
        this.cells = cells;
    }

    public Row(org.apache.poi.ss.usermodel.Row POIRow) {
        this.POIRow = POIRow;
        initRow();
    }

    private void initRow() {

    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }
}
