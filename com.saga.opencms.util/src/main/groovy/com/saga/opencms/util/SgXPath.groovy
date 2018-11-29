package com.saga.opencms.util

import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.Log
import org.opencms.main.CmsLog
import org.opencms.xml.CmsXmlUtils

class SgXPath {

    private static Log LOG = CmsLog.getLog(this.getClass());

    static String removeIndexes(String xPathWithIdxs){
        return CmsXmlUtils.removeXpath(xPathWithIdxs);
    }

    static int getXPathPos(String xPathWithIdx){
        return CmsXmlUtils.getXpathIndexInt(xPathWithIdx);
    }

    static String getXPath(List<String> xPathsWithIdx){
        return xPathsWithIdx.join("/")
    }

    static List<String> slicePath (String path) {
        return path.split("/").toList()
    }

    static List<Integer> xPathIndexes (String xPathWithIdxs) {
        return xPathWithIdxs.split('/').collect { CmsXmlUtils.getXpathIndexInt(it) };
    }

    static String xPathStr(List<String> xPathNames, List<Integer> xPathIdxs) {
        List targetXPaths = []
        xPathNames.eachWithIndex { elem, i ->
            targetXPaths.add("$elem[${xPathIdxs[i] + 1}]")
        }
        targetXPaths.join("/")
    }

    static String parentXPath(String xPath) {
        if (StringUtils.isBlank(xPath) || !xPath.contains("/")) {
            return null
        }

        return xPath.substring(0, xPath.lastIndexOf('/'))
    }

    /**
     * Dados los índices en la ruta destino donde se deben repetir valor,
     * los índices del valor original actual
     * y el índice del nodo origen actual a crear obtenemos el índice del nodo destino que estamos creando
     * @param targetRepetitionIndexes
     * @param currentValueIndexes
     * @param xPathIdxs
     * @param i
     * @return
     */
    static int getCurrentNodeIndex(List sourceXPathIdxs, List sourceRepetitionIndexes, List targetRepetitionIndexes, int i) {
        if (targetRepetitionIndexes.contains(i)) {
            // existe configuracion de nodo multiple (*) para este nodo
            // sourceRepetitionIndexes(nivel del nodo multiple en origen 0, 1, 2...): [1]
            // targetRepetitionIndexes(nivel del nodo multiple en destino 0, 1, 2...): [4]
            // xPathIdxs(indices de las rutas del nodo)[1,1,1,4,1]
            def repetitionIdx = targetRepetitionIndexes.indexOf(i)
            def sourceLvlIdx = sourceRepetitionIndexes.get(repetitionIdx);
            sourceXPathIdxs.get(sourceLvlIdx) - 1
        } else {
            0;
        }
    }

    /**
     * Obtien la lista con los índices de iteración según la configuración del mapeo. Ejemplo:
     *
     * Si en la configuración hemos definido una ruta de mapeo tal que así: Content* /ImageMain /Image*
     * Este método devolverá la lista [0, 2], indicando que en esta ruta se repiten valores en la posición 0
     * y 2 de la ruta. Cada posición (fragmento) de la ruta viene demarcada por el carácter '/'
     *
     * @param path
     * @return
     */
    static List listMultipleLevels(String path) {
        def splitter = path.split('/') as ArrayList;
        splitter.collect({
            if (it.endsWith('*')) splitter.indexOf(it)
        }).inject([], { r, v -> if (v != null) r << v; r });
    }

    static List calculateTargetXPathIdxs(String[] targetPaths, List sourceXPathIdxs, List sourceRepetitionIndexes,
                                         List targetRepetitionIndexes, int index) throws Exception {
        def res = []
        int currentNodeIndex = getCurrentNodeIndex(
                sourceXPathIdxs, sourceRepetitionIndexes, targetRepetitionIndexes, index);
        res.add(currentNodeIndex)
        if (index != targetPaths.length - 1) {
            res.addAll(
                    calculateTargetXPathIdxs(
                            targetPaths, sourceXPathIdxs, sourceRepetitionIndexes,
                            targetRepetitionIndexes, index + 1))
        }
        return res
    }

    static String getParentXPath(String xPath) {
        if (!StringUtils.isBlank(xPath) && xPath.contains("/")) {
            String parent = xPath.substring(0, xPath.length() - 1);
            return parent.substring(0, parent.lastIndexOf(47) + 1);
        } else {
            return null;
        }
    }

    static String getLastFullName(String xPath) {
        if (!StringUtils.isBlank(xPath) && xPath.contains("/")) {
            String parent = xPath.substring(0, xPath.length() - 1);
            return xPath.substring(parent.lastIndexOf(47) + 1);
        }
        return xPath;
    }

    static String setLastPos(String xPathFull, int pos) {
        String xPathParent = SgXPath.getParentXPath(xPathFull)
        String xPathName = getLastSimpleName(xPathFull)
        return xPathParent +
                xPathName +
                "[" + pos + "]"
    }

    static String getLastSimpleName(String xPathFull) {
        String xPathName = SgXPath.getLastFullName(xPathFull)
        xPathName = SgXPath.removeIndexes(xPathName);
        xPathName
    }
}