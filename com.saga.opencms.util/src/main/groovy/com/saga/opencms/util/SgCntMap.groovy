package com.saga.opencms.util

import groovy.json.JsonBuilder
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException

public class SgCntMap {

    public static final String LINK = "link"
    public static final String TARGET = "target"

    CmsObject cmso;

    SgCntMap(CmsObject cmso) {
        this.cmso = cmso;
    }

    /**
     * Parse control code xml to json
     * @param strContent
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     */

    public static String toJson(String content)
            throws IOException, SAXException, ParserConfigurationException {
        def map = toMap(content);
        return new JsonBuilder(map).toString();
    }

    /**
     * Parse control code xml to map
     * @param strContent
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Map<String, Object> toMap(String content)
            throws IOException, SAXException, ParserConfigurationException {
        // Parse and remove <?xml ... ?>
        GPathResult xml = new SgSlurper(content).cleanControlCode().slurpXml();
        // Convert to Map
        return toMapParent(xml);
    }

    /**
     * Parse each parent node recursively
     * @param xml
     * @return
     */
    public static def toMapParent(GPathResult parent) {
//		println("parent node: " + parent.name())
        parent.children().inject([:]) { map, child ->
//			println("child node: " + child.name())
//			println("map: " + map)
            def lang = child.@language.text();
            if (lang) {
                map << [ "$lang" : toMapChild(map, child) ]
            } else {
                map << toMapChild(map, child)
            }
            map
        }
    }

    /**
     * Chech if it has brother and parse child node
     * @param map
     * @param child
     * @return
     */
    public static def toMapChild(def map, def child){
        def name = child.name();
        def brother = map.get(name);
        if (brother) {
            // Has brother
            if (brother instanceof List){
                // Add to list brother
                [ (child.name()) : (brother) << toMapChild(child) ]
            } else {
                // Create list with brother
                [ (child.name()) : [brother, toMapChild(child)]]
            }
        } else {
            // No brother
            [ (child.name()) : toMapChild(child)]
        }

    }

    /**
     * Parse child node
     * @param child
     * @return
     */
    public static def toMapChild(def child){
        (child.children().size() > 0 ? toMapParent(child) : child.text())
    }

    /**
     * Map content (Stringify JSON) and properties (Map) to resource
     *
     * {
     *  locale -> "en": {
     *   rootnode -> "Profesor":{
     *    string -> "Nombre":"PEREZ GARCÍA, LUIS",
     *    map -> "Departamento":{"link":{"target":"/sites/facultad/dpu.xml","uuid":"2b378fe2-20e5-11e6-aef9-7fb253176922"}},
     *    list -> "LinkInteres":[{"Href":{"link":{"target":"https://www.google.es"}},"Title":"Google"},{"Href":{"link":{"target":"https://www.wikipedia.es"}},"Title":"Wikipedia"},{"Href":{"link":{"target":"https://www.saga.es"}},"Title":"Saga"}]
     *   }
     *  }
     * }
     *
     * @param cmso
     * @param path
     * @param type
     * @param jsonStr JSON String content
     * @param props Map properties [propName : propValue]
     * @return
     */
    public void mapResource(String path, String type, String jsonStr){
        mapResource(path, type, jsonStr, [:])
    }

    /**
     * Map content (Stringify JSON) and properties (Map) to resource
     *
     * {
     *  locale -> "en": {
     *   rootnode -> "Profesor":{
     *    string -> "Nombre":"PEREZ GARCÍA, LUIS",
     *    map -> "Departamento":{"link":{"target":"/sites/facultad/dpu.xml","uuid":"2b378fe2-20e5-11e6-aef9-7fb253176922"}},
     *    list -> "LinkInteres":[{"Href":{"link":{"target":"https://www.google.es"}},"Title":"Google"},{"Href":{"link":{"target":"https://www.wikipedia.es"}},"Title":"Wikipedia"},{"Href":{"link":{"target":"https://www.saga.es"}},"Title":"Saga"}]
     *   }
     *  }
     * }
     *
     * @param cmso
     * @param path
     * @param type
     * @param jsonStrCnt JSON String content
     * @param props Map properties [propName : propValue]
     * @return
     */
    public void mapResource(
            String path, String type,
            String jsonStrCnt, Map<String, String> props){
        // create resource
        SgCms sgCms = new SgCms(cmso);
        sgCms.createResource(path, type);

        // create properties
        SgProperties sgProps = new SgProperties(cmso);
        sgProps.addProperties(props).save(path);

        // create content
        SgSlurper sgSlurper = new SgSlurper(jsonStrCnt);
        Map json = sgSlurper.cleanControlCode().slurpJson();
        addContent(path, json);
    }

    /**
     * Add content to resource given by map
     * {
     *   locale -> "en": {
     *    rootnode -> "Profesor":{
     *     string -> "Nombre":"PÉREZ GARCÍA, LUIS",
     *     map -> "Departamento":{"link":{"target":"/sites/facultad/.content/departamento/dpu.xml","uuid":"2b378fe2-20e5-11e6-aef9-7fb253176922"}},
     *     list -> "LinkInteres":[{"Href":{"link":{"target":"https://www.google.es"}},"Title":""},{"Href":{"link":{"target":"https://www.wikipedia.es"}},"Title":""},{"Href":{"link":{"target":"https://www.saga.es"}},"Title":""},"\n        Hola\n    "]
     *    }
     *   }
     *  }
     * @param cmso
     * @param path
     * @param map {"en":{"Profesor":{"Nombre":"PÉREZ GARCÍA, LUIS", ...}}, "es":{...}}
     */
    public void addContent(String path, Map map){
        // content
        SgCnt sgCnt = new SgCnt(cmso, path);

        // each locale
        map.each {k,v ->

            // init locale
            sgCnt.initLocale(k);

            // add content map next to root node
            def firstEntry = v.iterator().next();
            Map firstMapCnt = firstEntry.value;
            addContentMap(sgCnt, "", firstMapCnt);
        }

        //save content
        sgCnt.saveXml();

    }

    /**
     * Add content map
     * @param sgCnt
     * @param xmlPath
     * @param map
     */
    public static void addContentMap(SgCnt sgCnt, String xmlPath, Map map){
        // For each entry add content
        map.each { k,v ->
            addJsonContent(sgCnt, xmlPath, k, v, 1);
        }
    }

    /**
     * Add content depending on field type
     * @param sgCnt
     * @param xmlPath
     * @param k
     * @param v
     * @param pos
     */
    public static void addJsonContent(
            SgCnt sgCnt, String xmlPath,
            String k, def v, int pos){
        // next xmlPath
        xmlPath = nextXmlPath(xmlPath, k, pos);
        // Link
        if (k.equals(LINK)) {
            addContentLink(sgCnt, xmlPath, v)
        }
        // String
        else if (v instanceof String) {
            addContentString(sgCnt, xmlPath, v);
        }
        // Map
        else if (v instanceof Map) {
            addContentMap(sgCnt, xmlPath, v);
        }
        // List
        else if (v instanceof List) {
            addContentList(sgCnt, xmlPath, v)
        }
    }

    /**
     * Return next rootPath adding element elemPath
     * @param rootPath
     * @param elemPath
     * @param pos
     * @return
     */
    public static String nextXmlPath(String rootPath, String elemPath, int pos) {
        if (StringUtils.isBlank(rootPath)) {
            return "$elemPath[$pos]";
        } else {
            return "$rootPath/$elemPath[$pos]";
        }
    }

    /**
     * Add content link
     * @param sgCnt
     * @param xmlPath
     * @param map
     */
    public static void addContentLink(SgCnt sgCnt, String xmlPath, Map map){
        String parentXmlPath = getParentXmlPath(xmlPath);
        addContentString(sgCnt, parentXmlPath, map.get(TARGET));
    }

    /**
     * Add content string
     * @param sgCnt
     * @param xmlPath
     * @param value
     */
    public static void addContentString(SgCnt sgCnt, String xmlPath, String value){
        sgCnt.setStringValue(xmlPath, value);
    }

    /**
     * Add content list
     * @param sgCnt
     * @param xmlPath
     * @param list
     */
    public static void addContentList(SgCnt sgCnt, String xmlPath, List list){
        String k = getLastElemPath(xmlPath);
        list.eachWithIndex { v, i ->
            addJsonContent(sgCnt, xmlPath, k, v, i+1);
        }
    }

    /**
     * Return last element of xml path
     * @param xmlPath
     * @return
     */
    public static String getLastElemPath(String xmlPath){
        return xmlPath.substring(xmlPath.lastIndexOf("/") + 1);
    }

    /**
     * Return parent xml path
     * @param xmlPath
     * @return
     */
    public static String getParentXmlPath(String xmlPath){
        return xmlPath.substring(0, xmlPath.lastIndexOf("/"));
    }
}