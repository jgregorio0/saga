package com.saga.opencms.excel;

import org.junit.Test;

import java.io.*;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Jesus on 18/10/2015.
 */
public class WorkbookTest {

    @Test
    public void initWorkbookTest(){
        String filename = "DHP.xlsx";
        InputStream stream = WorkbookTest.class.getResourceAsStream(filename);

        try {
            Workbook workbook = new Workbook(stream);
            assertTrue(workbook != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}