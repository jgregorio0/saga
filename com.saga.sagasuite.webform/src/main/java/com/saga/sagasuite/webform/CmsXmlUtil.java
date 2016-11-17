package com.saga.sagasuite.webform;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.xml.CmsXmlEntityResolver;
import org.opencms.xml.CmsXmlException;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class CmsXmlUtil {

    private static final Log LOG = CmsLog.getLog(CmsXmlUtil.class);

    static int LITERAL = 0;
    static int REGEX = 1;

    CmsObject cmso;
    String path;
    Locale locale;
    CmsFile file;
    String strContent;
    CmsXmlContent xmlContent;
    CmsXmlEntityResolver resolver;

    /**
     * Initialize content.
     * The resource must be locked by user before change content.
     * Plain objects have no xmlContent but strContent is still readable.
     * @param cmso
     * @param resourcePath
     */
    public CmsXmlUtil(CmsObject cmso, String resourcePath){
        Locale defLocale = OpenCms.getLocaleManager().getDefaultLocale();
        try {
            init(cmso, resourcePath, defLocale);
        } catch (Exception e) {
            LOG.error("ERROR init", e);
        }
    }

    /**
     * Initialize content.
     * The resource must be locked by user before change content.
     * Plain objects have no xmlContent but strContent is still readable.
     * @param cmso
     * @param resourcePath
     * @param locale
     */
    public CmsXmlUtil(CmsObject cmso, String resourcePath, Locale locale){
        try {
            init(cmso, resourcePath, locale);
        } catch (Exception e) {
            LOG.error("ERROR init", e);
        }
    }

    /**
     * Initialize content.
     * The resource must be locked by user before change content.
     * Plain objects have no xmlContent but strContent is still readable.
     * @param cmso
     * @param resourcePath
     * @param localeStr
     */
    public CmsXmlUtil(CmsObject cmso, String resourcePath, String localeStr){
        Locale locale = new Locale(localeStr);
        try {
            init(cmso, resourcePath, locale);
        } catch (Exception e) {
            LOG.error("ERROR init", e);
        }
    }

    /**
     * Initialize content.
     * The resource must be locked by user before change content.
     * Plain objects have no xmlContent but strContent is still readable.
     * @param cmso
     * @param resource
     * @param localeStr
     */
    public CmsXmlUtil(CmsObject cmso, CmsResource resource, String localeStr){
        String resourcePath = cmso.getSitePath(resource);
        Locale locale = new Locale(localeStr);
        try {
            init(cmso, resourcePath, locale);
        } catch (Exception e) {
            LOG.error("ERROR init", e);
        }
    }

    /**
     * Initialize content.
     * The resource must be locked by user before change content.
     * Plain objects have no xmlContent but strContent is still readable.
     * @param cmso
     * @param resource
     * @param locale
     */
    public CmsXmlUtil(CmsObject cmso, CmsResource resource, Locale locale) {
        String resourcePath = cmso.getSitePath(resource);
        try {
            init(cmso, resourcePath, locale);
        } catch (Exception e) {
            LOG.error("ERROR init", e);
        }
    }

    /**
     * Initialize content.
     * The resource must be locked by user before change content.
     * Plain objects throw exception but strContent is readable.
     * @param cmso
     * @param path
     * @param locale
     */
    private void init(CmsObject cmso, String path, Locale locale) throws CmsException, UnsupportedEncodingException {
        this.cmso = cmso;
        this.path = path;
        this.locale = locale;

        //Leemos el recurso
        file = cmso.readFile(path, CmsResourceFilter.ALL);
        strContent = new String(file.getContents(), CmsEncoder.ENCODING_UTF_8);
        resolver = new CmsXmlEntityResolver(cmso);
        try {
            unmarshall();
        } catch (Exception e) {
//			throw new Exception("unmarshalled failed for resource $path".toString())
            xmlContent = null;
        }
    }

    /**
     * Unmarshall string to xml content
     * @return
     * @throws CmsXmlException
     */
    public CmsXmlUtil unmarshall() throws CmsXmlException {
        xmlContent = CmsXmlContentFactory
                .unmarshal(cmso, strContent, CmsEncoder.ENCODING_UTF_8, resolver);
        return this;
    }

    /**
     * Marshall xml to string content
     * @return
     * @throws CmsXmlException
     * @throws java.io.UnsupportedEncodingException
     */
    public CmsXmlUtil marshall() throws CmsXmlException, UnsupportedEncodingException {
        strContent = new String (xmlContent.marshal(), CmsEncoder.ENCODING_UTF_8);
        return this;
    }
    /**
     * Asegura un valor en el path indicado, devolviendo uno existente o creandolo
     * @param path
     * @param pos
     * @return
     */
    public I_CmsXmlContentValue procureValue(String path, int pos) throws Exception {
        I_CmsXmlContentValue res = null;
        if (xmlContent == null) {
            throw new Exception("xml content not exists for resource $path".toString());
        }
        if (xmlContent.hasValue(path, locale, pos)) {
            res = xmlContent.getValue(path, locale, pos);
        } else {

            // Si no existe aseguramos que el padre exista
            if (CmsXmlUtils.isDeepXpath(path)) {
                String parentPath = CmsXmlUtils.createXpath(CmsXmlUtils.removeLastXpathElement(path), 1);
                procureValue(parentPath, obtainNodeIndex(parentPath));
            }

            // Comprobamos que al crear el padre no se haya creado automaticamente el hijo
            if (xmlContent.hasValue(path, locale, pos)) {
                res = xmlContent.getValue(path, locale, pos);
            } else {
                res = xmlContent.addValue(cmso, path, locale, pos);
            }
        }
        return res;
    }

    private int obtainNodeIndex(String path) {
        int index = CmsXmlUtils.getXpathIndexInt(path);
        if (index > 0) {
            index = index - 1;
        }
        return index;
    }
    /**
     * Asegura un valor en el path indicado, devolviendo uno existente o creandolo
     * @param path
     */
    public I_CmsXmlContentValue procureValue(String path) throws Exception {
        return procureValue(path, 0);
    }

    public CmsXmlUtil setStringValue(String path, String value){
        setStringValue(path, value, 0);
        return this;
    }

    /**
     * Modifica el valor del recurso. Si no existe
     * @param path
     * @param value
     * @param pos
     * @return
     */
    public CmsXmlUtil setStringValue(String path, String value, int pos){
        I_CmsXmlContentValue content = null;
        try {
            content = procureValue(path, pos);
        } catch (Exception e) {
            LOG.error("ERROR setting value " + value + " for path " + path, e);
        }
        content.setStringValue(cmso, value);
        return this;
    }

    /**
     * Filling the content with empty tags
     * @param tag
     * @param limit
     */
    public void fillWithEmptyValues(String tag, int limit) throws Exception {
        if (xmlContent == null) {
            throw new Exception("xml content not exists for resource $path".toString());
        }
        for (int i = 0; i < limit; i++) {
            if (!xmlContent.hasValue(tag, locale, i)){
                xmlContent.addValue(cmso, tag, locale, i);
            }
        }
    }

    /**
     * Update xml content and save file changes from string content.
     * If we modified xmlContent then we must marshall first.
     */
    public CmsXmlUtil save() throws CmsException, UnsupportedEncodingException {
        try {
            cmso.lockResource(path);
        } catch (CmsException e) {
            LOG.error("ERROR locking resource " + path, e);
        }
        if (xmlContent != null) {
            unmarshall();
        }
        file.setContents(strContent.getBytes(CmsEncoder.ENCODING_UTF_8));
        cmso.writeFile(file);
        try {
            cmso.unlockResource(path);
        } catch (CmsException e) {
            LOG.error("ERROR unlocking resource " + path, e);
        }
        return this;
    }

    /**
     * Saving file changes from String content.
     * For updating xml content execute unmarshall.
     */
    public CmsXmlUtil saveStr() throws UnsupportedEncodingException, CmsException {
        try {
            cmso.lockResource(path);
        } catch (CmsException e) {
            LOG.error("ERROR locking resource " + path, e);
        }
        file.setContents(strContent.getBytes(CmsEncoder.ENCODING_UTF_8));
        cmso.writeFile(file);
        try {
            cmso.unlockResource(path);
        } catch (CmsException e) {
            LOG.error("ERROR unlocking resource " + path, e);
        }
        return this;
    }

    /**
     * Saving file from Xml content changes.
     * For updating string content execute marshall.
     */
    public CmsXmlUtil saveXml() throws CmsException {
        try {
            cmso.lockResource(path);
        } catch (CmsException e) {
            LOG.error("ERROR locking resource " + path, e);
        }
        file.setContents(xmlContent.marshal());
        cmso.writeFile(file);
        try {
            cmso.unlockResource(path);
        } catch (CmsException e) {
            LOG.error("ERROR unlocking resource " + path, e);
        }
        return this;
    }

    /**
     * Repair content
     * @return
     */
    public CmsXmlUtil repair() throws CmsException {
        xmlContent.setAutoCorrectionEnabled(true);

        // now correct the XML
        xmlContent.correctXmlStructure(cmso);

        // Prepare for write
        xmlContent.getHandler().prepareForWrite(cmso, xmlContent, file);

        return this;
    }

    /**
     * Returns String value from OpenCmsString element
     * @param tag
     * @return
     */
    public String getStringValue(String tag) throws Exception {
        if (xmlContent == null) {
            throw new Exception("xml content not exists for resource $path".toString());
        }
        return xmlContent.getStringValue(cmso, tag, locale);
    }

    /**
     * Returns String value from OpenCmsHtml element
     * @param element
     * @return
     */
    public String getHtmlStringValue(String element) throws Exception {
        if (xmlContent == null) {
            throw new Exception("xml content not exists for resource $path".toString());
        }
        String value = null;
        if (xmlContent.hasValue(element, locale)){
            value = xmlContent.getValue(element, locale).getStringValue(cmso);
        }
        return value;
    }

    /**
     * Check if the content has value for element
     * @param element
     * @return
     */
    public boolean hasValue(String element) throws Exception {
        if (xmlContent == null) {
            throw new Exception("xml content not exists for resource $path".toString());
        }
        return xmlContent.hasValue(element, locale);
    }

    /**
     * Check if exists xmlPath content
     * @param xmlPath
     * @param pos
     * @return
     */
    public boolean contains(String xmlPath, int pos) {
        return xmlContent.getValue(xmlPath, locale, pos) != null;
    }

    /**
     * Check if exists xmlPath content
     * @param xmlPath
     * @return
     */
    public boolean contains(String xmlPath) {
        return contains(xmlPath, 0);
    }

    /**
     * Check if exists xmlPath content
     * @param xmlPath
     * @return
     */
    public int count(String xmlPath) {
        return xmlContent.getIndexCount(xmlPath, locale);
    }
}