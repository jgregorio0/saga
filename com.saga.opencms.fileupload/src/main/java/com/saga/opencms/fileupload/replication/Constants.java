package com.saga.opencms.fileupload.replication;

/**
 * Created by Jesus on 21/11/2015.
 */
public class Constants {
    public static final String CREATE_TABLE=
            "CREATE TABLE IF NOT EXISTS `UPLOAD_FILES`" +
                    " (`FILE_ID` int(11) NOT NULL," +
                    " `SITE_PATH` varchar(128) NOT NULL," +
                    " `USER_NAME` varchar(128) NOT NULL," +
                    " `FILE_NAME` varchar(128) NOT NULL," +
                    " `FILE_CONTENT` mediumblob NOT NULL," +
                    " PRIMARY KEY (`FILE_ID`)" +
                    " ) ENGINE=InnoDB DEFAULT CHARSET=utf8";

    public static final String READ_MAX_ID=
            "SELECT MAX(UPLOAD_FILES.FILE_ID) MAXID" +
                    " FROM UPLOAD_FILES";

    public static final String DELETE_FILE=
            "DELETE FROM UPLOAD_FILES" +
                    " WHERE FILE_ID=?";

    public static final String INSERT_FILE=
            "INSERT INTO UPLOAD_FILES" +
                    " (FILE_ID," +
                    " SITE_PATH," +
                    " USER_NAME," +
                    " FILE_NAME," +
                    " FILE_CONTENT)" +
                    " VALUES (?, ?, ?, ?, ?)";

    public static final String SELECT_FILES=
            "SELECT * FROM UPLOAD_FILES";
}
