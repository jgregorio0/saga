package com.saga.opencms.fileupload.model;

/**
 * Created by Jesus on 21/11/2015.
 */
public class FileUpload {

    private int id;
    private String site;
    private String userName;
    private String fileName;
    private byte[] Content;

    public FileUpload() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getContent() {
        return Content;
    }

    public void setContent(byte[] content) {
        Content = content;
    }
}
