package com.saga.opencms.util


public class SgTransform {

    /**
     * Transforma a mapa un xml obtenido a partir de XmlSlurper
     * @param xml XmlSlurper.parse(xmlString)
     * @return
     */
    def xml2Map(xml) {
        xml.children().collectEntries {
            def tag = it.name()
            switch (tag) {
                case "resource":
                    String resource = it.@rootPath.text() + "@" + it.@resourceId.text()
                    ["$resource": convertResource2Map(it)]
                    break;
                case "documento":
                    String url = it.@url.text()
                    String text = it.text()
                    [documento: [url: "$url", text: "$text"]]
                    break;
                case "enlace":
                    String url = it.@url.text()
                    String text = it.text()
                    [enlace: [url: "$url", text: "$text"]]
                    break;
                case "imagen":
                    String url = it.@url.text()
                    String text = it.text()
                    [imagen: [url: "$url", text: "$text"]]
                    break;
                case "propertiesIndividual":
                    ["properties", it.childNodes() ? convertResource2Map(it) : it.text()]
                    break;
                case "propertiesInherited":
                    ["propertiesHeritance", it.childNodes() ? convertResource2Map(it) : it.text()]
                    break;
                default:
                    [it.name(), it.childNodes() ? convertResource2Map(it) : it.text()]
            }
        }
    }
}