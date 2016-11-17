package com.saga.opencms.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jesus on 18/10/2015.
 */
public class WorkbookReader {

    /** The log object for this class. */
    private static final String ERROR_NULL_STREAM = "ERROR InputStream is null";

    /**
     * Read Workbook from file
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public Workbook read(File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        return read(stream);
    }

    /**
     * Read Workbook from InputStream
     * @param stream
     * @return
     * @throws java.io.IOException
     */
    public Workbook read(InputStream stream) throws IOException {
        if (stream == null)
            throw new IllegalArgumentException(ERROR_NULL_STREAM);
        try {
            return WorkbookFactory.create(stream);
        } catch (InvalidFormatException e) {
            throw new IOException(e);
        } finally {
            stream.close();
        }
    }
}
