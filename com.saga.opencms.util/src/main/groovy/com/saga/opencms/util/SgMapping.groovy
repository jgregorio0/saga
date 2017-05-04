package com.saga.opencms.util

import groovy.json.JsonBuilder
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.CmsResourceTypeXmlContent
import org.opencms.file.types.I_CmsResourceType
import org.opencms.i18n.CmsEncoder
import org.opencms.loader.CmsLoaderException
import org.opencms.main.CmsException
import org.opencms.main.OpenCms
import org.opencms.xml.CmsXmlContentDefinition
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.CmsXmlException
import org.opencms.xml.CmsXmlUtils
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.opencms.xml.types.I_CmsXmlContentValue
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException

public class SgMapping {

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
     * @param jsonStr String that represents JSON
     * @param props Map properties [propName : propValue]
     * @return
     */
    public static CmsResource mapResource(
            CmsObject cmso, String path, String type,
            String jsonStr, Map<String, String> props){
        // create resource
        SgCms sgCms = new SgCms(cmso);
        sgCms.createResource(path, type);

        // create properties
        SgProperties sgProps = new SgProperties(cmso);
        sgProps.addProperties(props).save(path);

        // create content
        SgSlurper sgSlurper = new SgSlurper(jsonStr);
        def json = sgSlurper.slurpJson();
        addContentMap(cmso, path, json);
    }

    /**
     * Add content to resource given by map
     * @param cmso
     * @param path
     * @param map {"en":{"UPOProfesor":{"Nombre":"PEREZ-PRAT DURBAN, LUIS", ...}}, "es":{...}}
     */
    public static void addContentMap(CmsObject cmso, String path, Map map){
        map.each {k,v ->
            // content locale
            SgCnt sgCnt = new SgCnt(cmso, path, k);
            // content map
            addContentMap(sgCnt, v.iterator().next());
        }
        /*{
            locale -> "en": {
                rootnode -> "UPOProfesor":{
                    string -> "Nombre":"PEREZ-PRAT DURBAN, LUIS",
                    map -> "Departamento":{"link":{"target":"/sites/upo-demo-facultad/.content/upodepartamento/dpu.xml","uuid":"2b378fe2-20e5-11e6-aef9-7fb253176922"}},
                    list -> "LinkInteres":[{"Href":{"link":{"target":"https://www.google.es"}},"Title":""},{"Href":{"link":{"target":"https://www.wikipedia.es"}},"Title":""},{"Href":{"link":{"target":"https://www.saga.es"}},"Title":""},"\n        Hola\n    "]
                }
            }
        }*/
    }

//TODO no es posible hace como en ochrecursosutil
    public static void addContentMap(SgCnt sgCnt, String xmlPath, Map map){

        map.each { k,v ->
            xmlPath += "/" + v + "[1]";
            // String
            if (v instanceof String) {
                addContentString(sgCnt, xmlPath, v);
            }
            // Map
            if (v instanceof Map) {
                addContentMap(sgCnt, xmlPath, v);
            }
            // List
            if (v instanceof List) {

            }
            addContentMap(sgCnt, map)
        }
    }

    public static void addContentString(SgCnt sgCnt, String xmlPath, String value){
        sgCnt.setStringValue(xmlPath, value)
    }
}