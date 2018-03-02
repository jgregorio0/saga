package com.saga.opencms.synchronization

/**
 * Created by jgregorio on 02/03/2018.
 */
class SgResource {

    private String title;
    private String path;
    private String resourceType;
    private List<SgField> fields;

    public Resource() {
    }

    public Resource(String title, String path, String resourceType) {
        this.title = title;
        this.path = path;
        this.resourceType = resourceType;
    }

    public Resource(String title, String path, String resourceType, List<SgField> fields) {
        this.title = title;
        this.path = path;
        this.resourceType = resourceType;
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<SgField> getFields() {
        return fields;
    }

    public void setFields(List<SgField> fields) {
        this.fields = fields;
    }
}
