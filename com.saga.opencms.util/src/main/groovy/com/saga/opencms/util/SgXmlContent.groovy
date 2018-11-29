package com.saga.opencms.util

import org.apache.commons.logging.Log
import org.opencms.file.CmsObject
import org.opencms.main.CmsLog
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.types.I_CmsXmlContentValue

import java.util.regex.Pattern

public class SgXmlContent {

    final static Log LOG = CmsLog.getLog(SgXmlContent.class)

    /*void example(){
        // LinkGallery (attachments & links)
        String xPathFrom = "Content[1]";
        String xPathTo = "Content[" + 2 + "]";
        String xPathNamesParent = "Content/TextBlock/Gallery"
        boolean isMoved = moveMultipleNode(cmso, locale, content, xPathNamesParent, xPathFrom, xPathTo)
    }*/

    /**
     * Move all parent nodes given by xPath
     * @param cmso
     * @param locale
     * @param content
     * @param xPathFrom
     * @param xPathTo
     * @param xPathNamesParent
     */
    static boolean moveMultipleNode(CmsObject cmso, Locale locale, CmsXmlContent content, String xPathNamesParent,
                                    String xPathFrom, String xPathTo) {
        // multiples padres
        List<I_CmsXmlContentValue> parents = content.getValues(xPathNamesParent, locale)
        Collections.sort(parents, new Comparator<I_CmsXmlContentValue>() {
            @Override
            int compare(I_CmsXmlContentValue o1, I_CmsXmlContentValue o2) {
                return o1.getPath().compareTo(o2.getPath())
            }
        })

        for (int iParent = 0; iParent < parents.size(); iParent++) {
            // por cada link (attachment o enlace) ordenados por indice menor a mayor
            // creamos uno igual en el contenido resultante de reemplazar xPathFrom por xPathTo
            I_CmsXmlContentValue parent = parents[iParent]
            boolean isMoveSimpleNode = moveSimpleNode(cmso, locale, content, parent, xPathFrom, xPathTo)
            if (!isMoveSimpleNode) {
                throw new Exception("No se ha podido mover " + parent.getPath())
            }
        }

        return true;
    }

    /**
     * Move parent by replacing xPathFrom to xPathTo in parent xPath
     * @param cmso
     * @param locale
     * @param content
     * @param parent Content[1]/TextBlock[1]/Gallery[1]/LinksGallery[1]/Option[1]/Attachment[1]
     * @param xPathFrom Content[1]
     * @param xPathTo Content[2]
     */
    static boolean moveSimpleNode(CmsObject cmso, Locale locale, CmsXmlContent content, I_CmsXmlContentValue parent,
                                  String xPathFrom, String xPathTo) {

        String xPathParentFrom = parent.getPath()
        String xPathParentTo = xPathParentFrom.replaceFirst(Pattern.quote(xPathFrom), xPathTo)

        // para evitar sobreescribir valores lo movemos despues del ultimo
        def countXPathTo = getIndexCount(content, xPathParentTo, locale)
        if (countXPathTo > 0) {
            xPathParentTo = SgXPath.setLastPos(xPathParentTo, countXPathTo + 1)
        }
        boolean isMoved = moveSimpleNodeSubValues(cmso, locale, content, parent, xPathParentFrom, xPathParentTo)

        return isMoved
    }

    /**
     * Move content xPathParentFrom >> xPathParentTo by moving all subvalues
     * @param cmso
     * @param locale
     * @param content
     * @param parent
     * @param xPathParentFrom
     * @param xPathParentTo
     * @return
     */
    static boolean moveSimpleNodeSubValues(CmsObject cmso, Locale locale, CmsXmlContent content, I_CmsXmlContentValue parent, String xPathParentFrom, String xPathParentTo) {
        boolean isMoved = false;
        try {
            List<I_CmsXmlContentValue> simpleSubValuesOld = content.getAllSimpleSubValues(parent)
            for (int iSubValue = 0; iSubValue < simpleSubValuesOld.size(); iSubValue++) {
                // por cada nodo simple del nodo padre
                // creamos nodo en destino y copiamos su valor al ultimo Content
                I_CmsXmlContentValue subValueOld = simpleSubValuesOld.get(iSubValue)

                String xPathOld = subValueOld.getPath()
                String xPathNew = xPathOld.replaceFirst(Pattern.quote(xPathParentFrom), xPathParentTo)

                String nodeValue = subValueOld.getStringValue(cmso)
                I_CmsXmlContentValue subValueNew = setNodeValue(cmso, locale, content, xPathNew, nodeValue)
//                I_CmsXmlContentValue subValueNew = addNodeValue(cmso, locale, content, xPathNew, nodeValue)

                if (!content.hasValue(subValueNew.getPath(), locale)) {
                    throw new Exception("No se ha podido copiar el subnodo simple " + subValueOld.getPath() + " >> " + subValueNew.getPath())
                }
            }

            // si se ha creado correctamente el nuevo nodo en el ultimo content borramos el anterior
            removeNode(xPathParentFrom, content, locale)
            isMoved = true;
        } catch (Exception e) {
            LOG.error("Moviendo valores del nodo " + xPathParentFrom + " a " + xPathParentTo, e)

            // en caso de error eliminamos el nodo de destino
            if (content.hasValue(xPathParentTo, locale)) {
                removeNode(xPathParentTo, content, locale)
            }
        }
        return isMoved
    }

    /**
     * Permite generar un nuevo nodo. Esta función es genérica para crear un nodo dada su ruta y no tiene en cuenta
     * configuraciones de mapeo. Es una función recursiva
     */
    static I_CmsXmlContentValue createNode(CmsObject cmso, Locale locale, CmsXmlContent content, String xPathsWithIdx) throws Exception {
        String[] paths = xPathsWithIdx.split("/")
        String path = null
        for (int i = 0; i < paths.length; i++) {
            path = path ? path + "/" + paths[i] : paths[i]
            if (!content.hasValue(path, locale)) {
                addNode(path, content, cmso, locale)
            }
        }
        return content.getValue(xPathsWithIdx, locale)
    }

    static void addNode(String xPathFull, CmsXmlContent content, CmsObject cmso, Locale locale) {
        int pos = SgXPath.getXPathPos(xPathFull)
        content.addValue(cmso, xPathFull, locale, pos - 1)
    }

    static void removeNode(String xPathFull, CmsXmlContent content, Locale locale) {
        int pos = SgXPath.getXPathPos(xPathFull)
        content.removeValue(xPathFull, locale, pos - 1)
    }

    static I_CmsXmlContentValue setNodeValue(
            CmsObject cmso, Locale locale, CmsXmlContent content, String xPathNew, String nodeValue) {
        I_CmsXmlContentValue subValueNew = createNode(cmso, locale, content, xPathNew)
        subValueNew.setStringValue(cmso, nodeValue)
        subValueNew
    }

    static int getIndexCount(CmsXmlContent content, String path, Locale locale) {
        List<I_CmsXmlContentValue> elements = content.getValues(path, locale);
        if (elements == null) {
            return 0;
        } else {
            int count = 0;
            for (int i = 0; i < elements.size(); i++) {
                I_CmsXmlContentValue element = elements.get(i)
                String nodePathName = SgXPath.getParentXPath(path) + SgXPath.getLastSimpleName(path)
                if (element.getPath().startsWith(nodePathName)) {
                    count++;
                }
            }
            return count;
        }
    }
}