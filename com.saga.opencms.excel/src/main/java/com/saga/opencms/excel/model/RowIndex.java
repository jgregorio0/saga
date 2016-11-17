package com.saga.opencms.excel.model;

public class RowIndex {

    public static final String ERROR_ROW_INDEX = "ERROR row must be bigger than 1";

    public static Integer rowIndex(Integer value) {
        if (value <= 0)
            throw new IllegalArgumentException(ERROR_ROW_INDEX);
        return new Integer(value - 1);
    }
}
