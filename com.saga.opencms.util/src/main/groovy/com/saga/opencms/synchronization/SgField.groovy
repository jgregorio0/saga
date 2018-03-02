package com.saga.opencms.synchronization

/**
 * Created by jgregorio on 02/03/2018.
 */
class SgField {

    private String name;
    private String value;
    private String type;
    private List<SgField> fields;

    public static final String FIELD_TYPE_SIMPLE = "simple";
    public static final String FIELD_TYPE_NESTED = "nested";
    public static final String FIELD_TYPE_MULTIPLE_SIMPLE = "multiplesimple";
    public static final String FIELD_TYPE_MULTIPLE_NESTED = "multiplenested";
    public static final String FIELD_TYPE_MULTIPLE_CHOICE = "choice";

    public Field(String name) {
        this.name = name;
    }

    public Field(String name, String value, String type, List<SgField> fields) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SgField> getFields() {
        return fields;
    }

    public void setFields(List<SgField> nested) {
        this.fields = fields;
    }
}
