package com.saga.opencms.excel.model;

import java.util.*;

/**
 * Created by Jesus on 18/10/2015.
 */
public class Sheet {

    private org.apache.poi.ss.usermodel.Sheet POISheet;
    private String name;
    private Map<Integer, List<Cell>> rows;
    private Map<Integer, List<Cell>> columns;
    private List<Cell> head;
    List<List<Cell>> cells;

//    public Sheet(String name, List<Row> rows) {
//        this.name = name;
//        this.rows = rows;
//        initCells();
//        initColumns();
//    }

//    public Sheet(String name, List<List<Cell>> cells) {
//        this.name = name;
//        this.cells = cells;
//        initRows();
//        initColumns();
//    }

    public Sheet(org.apache.poi.ss.usermodel.Sheet POISheet) {
        this.POISheet = POISheet;
//        cells = new ArrayList<List<Cell>>();
        head = new ArrayList<Cell>();
        rows = new HashMap<Integer, List<Cell>>();
        columns = new HashMap<Integer, List<Cell>>();
        initSheet();
    }

    private void initSheet() {
        name = POISheet.getSheetName();
        Iterator<org.apache.poi.ss.usermodel.Row> itRow = POISheet.iterator();
        while (itRow.hasNext()){
            org.apache.poi.ss.usermodel.Row POIRow = itRow.next();
            Iterator<org.apache.poi.ss.usermodel.Cell> itCell = POIRow.iterator();
            while (itCell.hasNext()){
                org.apache.poi.ss.usermodel.Cell POICell = itCell.next();
                Cell cell = new Cell(POICell);
                addCell(POICell.getRowIndex(), POICell.getColumnIndex(), cell);
            }
        }
    }

//    /**
//     * Initialize cells from rows
//     */
//    private void initCells() {
//        for (int i = 0; i < rows.size(); i++) {
//            Row row = rows.get(i);
//            List<Cell> cellsRow = row.getCells();
//            cells.add(i, cellsRow);
//        }
//    }
//
//    /**
//     * Initialize list of columns
//     */
//    private void initColumns() {
//        columns = new ArrayList<Column>();
//        for (int i = 0; i < cells.size(); i++) {
//            List<Cell> cellsRow = cells.get(i);
//            for (int j = 0; j < cellsRow.size(); j++) {
//                Cell cell = cellsRow.get(j);
//                Column column = columns.get(j);
//                if (column != null) {
//                    column.getCells().add(j, cell);
//                } else {
//                    column = new Column(j, new ArrayList<Cell>());
//                    column.getCells().add(cell);
//                }
//            }
//        }
//    }
//
//    /**
//     * Initialize list of rows
//     */
//    private void initRows() {
//        rows = new ArrayList<Row>();
//        for (int i = 0; i < cells.size(); i++) {
//            List<Cell> cellRow = cells.get(i);
//            Row row = new Row(i, cellRow);
//            rows.add(i, row);
//        }
//    }

    /**
     * Add one cell in the specified row and column position
     * @param columnIndex
     * @param cell
     */
    private void addCell(int rowIndex, int columnIndex, Cell cell) {
        if (rowIndex == 0) {
            head.add(cell);
            Collections.sort(head, new Cell());
        }

        List<Cell> rowCells = rows.get(rowIndex);
        if (rowCells != null) {
            rowCells.add(cell);
        } else {
            rowCells = new ArrayList<Cell>();
            rowCells.add(cell);
            rows.put(rowIndex, rowCells);
        }
        Collections.sort(rowCells, new Cell());

        List<Cell> columnCells = columns.get(columnIndex);
        if (columnCells != null) {
            columnCells.add(cell);
        } else {
            columnCells = new ArrayList<Cell>();
            columnCells.add(cell);
            columns.put(columnIndex, columnCells);
        }
        Collections.sort(columnCells, new Cell());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public org.apache.poi.ss.usermodel.Sheet getPOISheet() {
        return POISheet;
    }

    public void setPOISheet(org.apache.poi.ss.usermodel.Sheet POISheet) {
        this.POISheet = POISheet;
    }

    public Map<Integer, List<Cell>> getRows() {
        return rows;
    }

    public void setRows(Map<Integer, List<Cell>> rows) {
        this.rows = rows;
    }

    public Map<Integer, List<Cell>> getColumns() {
        return columns;
    }

    public void setColumns(Map<Integer, List<Cell>> columns) {
        this.columns = columns;
    }

    public List<List<Cell>> getCells() {
        return cells;
    }

    public void setCells(List<List<Cell>> cells) {
        this.cells = cells;
    }

    public List<Cell> getHead() {
        return head;
    }

    public void setHead(List<Cell> head) {
        this.head = head;
    }
}
