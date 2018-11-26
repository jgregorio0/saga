package com.saga.opencms.util

import groovy.util.slurpersupport.GPathResult

class SgXml {

    Map parseXML(String resText) {

        def xml = new XmlSlurper().parseText(resText);
        return convertResource2Map(xml)
    }

    def convertResource2Map(xml) {
        def map = toMapNode(xml)
        structureContent(map)
        return map.resources;
    }

    /**
     * Parse each parent node recursively
     * @param xml
     * @return
     */
    def toMapNode(GPathResult node) {
        if (node.children().size() == 0) {

            // nodo final
            return [(toMapName(node)): toMapValue(node)]
        } else {

            // nodo padre
            def children = node.children().inject([:]) { map, child ->
                map << toMapChild(map, child)
                return map
            }
            return [(toMapName(node)): children]
        }
    }

    /**
     * Chech if it has brother and parse child node
     * @param map
     * @param child
     * @return
     */
    def toMapChild(def map, def child) {
        def name = toMapName(child);
        def brother = map.get(name);
        if (!brother) {
            // No brother
            return toMapNode(child)
        } else {
            if (!(brother instanceof List)) {
                // Create list with brother
                def list = [brother]
                list.addAll(toMapNode(child).values())
                return [(toMapName(child)): list]
            } else {
                // Add brother to list
                brother.addAll(toMapNode(child).values())
                return [(toMapName(child)): brother]
            }
        }
    }

    /**
     * Devuelve el valor de un nodo final
     * @param child
     * @return
     */
    def toMapValue(def child) {
        def tag = child.name();
        switch (tag) {
            case "documento":
                String url = child.@url.text()
                String text = child.text()
                return [url: "$url", text: "$text"]
            case "enlace":
                String url = child.@url.text()
                String target = child.@target.text()
                String text = child.text()

                return [url: "$url", target: "$target", text: "$text"]
            case "imagen":
                String url = child.@url.text()
                String text = child.text()
                return [url: "$url", text: "$text"]
            default:
                return child.text()
        }
    }

    /**
     * Devuelve la clave de un nodo final
     * @param child
     * @return
     */
    def toMapName(def child) {
        def tag = child.name();
        switch (tag) {
            case "resource":
                return child.@resourceId.text() + "@" + child.@rootPath.text() + "@" + child.@count.text()
            case "propertiesIndividual":
                return "properties"
            case "propertiesInherited":
                return "propertiesHeritance"
            default:
                return tag
        }
    }

    /**
     * Estructura contenido en un mapa de la siguiente forma:
     * [resources:[resourceId@resourcePath:[file:[...], content:[Title[1]:Titulo, ...]]]]
     * @param map
     * @return
     */
    def structureContent(Map map) {
        map.resources.each { key, val ->
            if (val.content) {
                def structuredContent = [:]
                structureContent(structuredContent, "", 1, val.content.noticia)
                val.content = structuredContent
            }
        }
    }

    /**
     * Estructura valores en un mapa de la siguiente forma:
     * [a: [b: [c, d, e]]] -> [a[1]/b[1]: c, a[1]/b[2]: d, a[1]/b[3]: e]
     * @param res
     * @param xRootPath
     * @param pos
     * @param elem
     * @return
     */
    def structureContent(Map res, def xRootPath, int pos, def elem) {
        if (elem instanceof Map) {
            elem.each { key, val ->
                String xPath = xRootPath.length() > 0 ? "$xRootPath[$pos]/$key" : "$key"
                structureContent(res, xPath, 1, val)
            }
        } else if (elem instanceof List) {
            elem.eachWithIndex { def item, int i ->
                structureContent(res, xRootPath, i + 1, item)
            }
        } else {
            res.put("$xRootPath[$pos]", elem)
        }

    }
}

