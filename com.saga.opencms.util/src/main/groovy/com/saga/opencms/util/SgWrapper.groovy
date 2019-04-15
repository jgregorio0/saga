package com.saga.opencms.util

import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.jsp.util.CmsJspContentAccessBean
import org.opencms.jsp.util.CmsJspContentAccessValueWrapper

class SgWrapper {
    CmsObject cmso;
    String sitePath;
    CmsResource resource;
    CmsJspContentAccessBean content;
    Map<String, CmsJspContentAccessValueWrapper> value;
    Map<String, String> rdfa;


    private static final String SEP = ",";

    SgWrapper(CmsObject cmso, String sitePath) {
        this.cmso = cmso;
        this.sitePath = sitePath;
        init()
    }

    def init() {
        resource = cmso.readResource(sitePath);
        content = new CmsJspContentAccessBean(cmso, resource);
        value = content.getValue();
        rdfa = content.getRdfa();
    }

    String getValues(CmsJspContentAccessBean content, String xPath) {
        String values = "";
        List<CmsJspContentAccessValueWrapper> list = content.getValueList().get(xPath);
        for (int i = 0; i < list.size(); i++) {
            CmsJspContentAccessValueWrapper wrapper = list.get(i);
            String stringValue = wrapper.getStringValue();
            if (StringUtils.isNotBlank(values)) {
                values += SEP;
            }
            values += stringValue;
        }
        return values;
    }

    String getValue(CmsJspContentAccessBean content, String xPath) {
        String value = "";
        CmsJspContentAccessValueWrapper wrapper = content.getValue().get(xPath);
        if (wrapper.getExists() && wrapper.getIsSet()) {
            value = wrapper.getStringValue();
        }
        return value;
    }
}