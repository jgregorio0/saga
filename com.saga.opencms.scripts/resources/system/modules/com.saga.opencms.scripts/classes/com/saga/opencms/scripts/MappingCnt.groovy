package com.saga.opencms.scripts

import com.saga.sagasuite.scriptgroovy.util.*
import com.saga.sagasuite.scripts.SgReportManager
import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.json.JSONObject
import org.opencms.xml.CmsXmlUtils
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.types.I_CmsXmlContentValue

/**
 * Ejecuta la importación de recursos
 *
 * @param cmsObject CmsObject
 * @param configPath Ruta del fichero json con la configuración de la importación
 */

class MappingCnt {


    CmsObject cmso;
    String idProceso;
    String conFile;
    boolean onlyCheck;
    String site;
    Locale locale;
    SgLogUtil log;
    GroovyClassLoader groovyClassLoader;


    public void init(def cms, def idProcess, def config, def onlyCheck) {
        this.cmso = cms;
        this.idProceso = idProceso;
        this.conFile = config;
        this.onlyCheck = onlyCheck != null;
        this.site = cmso.getRequestContext().getSiteRoot();
        this.locale = cmso.getRequestContext().getLocale();
        this.log = new SgLogUtil(
                SgReportManager.getInstance(cmso),
                idProcess,
                cmso.getRequestContext().getCurrentUser().getName());
        execute()
    }

    def execute() {
        try {
            if (!cmso.getRequestContext().getSiteRoot().isEmpty()) {
                throw new Exception("Este script debe ejecutarse desde el raiz")
            }

            // Obtenemos el fichero de configuracion
            List cnf = (List) new SgSlurperUtil(cmso, conFile).slurpObject()
            log.add("Fichero de configuracion: " + cnf.toString()).print()

            // Obtenemos los recursos llamando a ExMappingCnt
            JSONObject jResources = obtainJsonExport();
            int total = jResources.length()
            log.add("Obtenemos $total recursos").print();

            SgCms sg = new SgCms(cmso)
            // Inicializamos las carpetas de destino
            initFolders(sg, cnf);

            // Tipo de recurso a crear
//            CmsXmlContentDefinition cntDef = sg.contentDefinition(cnf.targetType)

            jResources.keys().eachWithIndex { sourceRes, j ->
                log.init().percentage(j+1, total)
                        .add("${j+1}/$total - Importando $sourceRes").print()
                try {

                    // Creamos el nuevo recurso
                    log.add("Creando nuevo recurso en: '${cnf.targetFolder[0]}'" +
                            " del tipo: '${cnf.targetType[0]}'" +
                            " y siguiendo el patron: '${cnf.namePattern[0]}'");
                    String newPath = sg.nextResoucePath(
                            cnf.targetFolder[0], cnf.namePattern[0]);
                    if (!onlyCheck) {
                        sg.createResource(newPath, cnf.targetType[0]);
                    }
                    log.add("Nuevo recurso creado: $newPath");

                    // Agregamos las propiedades de la migracion al recurso
                    String uuid = sourceRes.substring(0, sourceRes.indexOf('@'));
                    String path = sourceRes.substring(
                            sourceRes.indexOf('@') + 1, sourceRes.length());

                    if (!onlyCheck) {
                        new SgProperties(cmso).addProperties(newPath,
                                ["migration.path": path,
                                 "migration.uuid": uuid])
                    }
                    log.add("Agregamos las propiedades: " +
                            "[\"migration.path\": path, \"migration.uuid\": uuid]")

                    // Para el contenido del recurso
                    SgCnt cnt = new SgCnt();
                    if (!onlyCheck) {
                        cnt = new SgCnt(cmso, newPath, locale);
                    }

                    // Iteramos sobre los campos del recurso que vamos a importar
                    jResources.get(sourceRes).each { sourcePath, sourceValues ->
                        String escapeValues = SgCnt.escapeHTML(sourceValues);
                        if (escapeValues.length() > 100) {
                            escapeValues = escapeValues.substring(0, 100);
                        }
                        log.add("Para el path $sourcePath cuyo contenido es: ${escapeValues}")

                        // Path del recurso origen sin los índices
                        String sourceSimplePath = CmsXmlUtils.removeXpath(sourcePath);

                        // Obtenemos de la configuración si se ha definido un script para mapear un valor (ruta) en concreto
                        String dynamicNodeMapping = cnf.dynamicNodeMappings[0].get(sourceSimplePath);
                        if (dynamicNodeMapping) {
                            log.add("Ejecutamos migracion dinamica mediante la clase $dynamicNodeMapping")
//                            CmsXmlContentFactory.createDocument(cmso, locale, 'UTF-8', cntDef)
                            invokeScript(cmso, dynamicNodeMapping, 'mapValue',
                                    [cmso, cnf, newPath, sourceSimplePath, sourceValues].toArray());
                        } else {

                            // Obtenemos la configuracion de la ruta origen
                            String sourceConfigPath = cnf.fieldMapping[0].keySet().find {
                                sourceSimplePath.equals(cleanAsterisk(it))
                            };

                            // obtenemos la configuración de la ruta destino definida para la ruta origen
                            String targetConfigPath = sourceConfigPath == null ?
                                    null
                                    : cnf.fieldMapping[0].get(sourceConfigPath);

                            if (targetConfigPath) {

                                // Obtenemos los índices de la ruta origen y destino
                                // donde se produce repetición y Validamos que el patrón de repetición
                                // no difiere del origen al destino
                                List sourcePatternIdxs = getXPathPattern(sourceConfigPath);
                                List targetPatternIdxs = getXPathPattern(targetConfigPath);
                                if (sourcePatternIdxs.size() != targetPatternIdxs.size()) {
                                    throw new Exception('NO COINCIDEN LOS PATRONES DE REPETICIÓN');
                                }

                                // Obtenemos los índices de la ruta origen.
                                // Si por ejemplo la ruta es Content[2]/Image[10]/Image[1]
                                // la lista obtenida sería [2, 10, 1]
                                List sourceIdxValues = sourcePath.split('/').collect {
                                    CmsXmlUtils.getXpathIndexInt(it) };

                                // Generamos la ruta con los indices en la posicion configurada
                                String sourcePathWithIndex = cnt.generatePathWithIndex(
                                        cleanAsterisk(sourcePath), sourceIdxValues
                                );
                                String targetPathWithIndex = cnt.generatePathWithIndex(
                                        cleanAsterisk(targetConfigPath), targetPatternIdxs,
                                        sourcePatternIdxs, sourceIdxValues);
                                log.add("Mapear con indices: $sourcePathWithIndex -> $targetPathWithIndex");

                                if (!onlyCheck) {
                                    //TODO: modificar edicion de recursos, limpiar tanta tralla
                                    // Creamos el nodo destino y le asignamos el valor correspondiente
                                    cnt.setStringValueIdx(targetPathWithIndex, sourceValues);
                                    log.add("Agregamos en la ruta $targetPathWithIndex: $escapeValues")

                                    //TODO Asignamos los valores por defecto a nodos
    //                                createDefaultValues(cmso, locale, content, targetNode, cfg);

                                    cnt.saveXml()
                                }
                            }
                        }
                    }
                } catch (e) {
                    log.error(e).print()
                }
            }
            log.print();
//
//                            // Mapeos estáticos
//                            log.add("Añadimos valores estaticos")
//                            createStaticValues(cmso, locale, content, cfg);
//
//                            // Aplicamos post procesamiento al contenido creado
//                            log.add("Ejecutamos postprocesamiento")
//                            applyResourceProcessor(groovyClassLoader, cmso, locale, content, cfg, sourceResource, resourceValues);
//
//                            // Almacenamos el recurso
//                            I_CmsResourceType resourceType = OpenCms.resourceManager.getResourceType(cfg.targetType)
//                            saveResource(cmso, content, resourceType, newPath, resourceProperties);
//                            log.add("Recurso almacenado")
//                                    .add("Procesamiento finalizado correctamente")
//                        } catch (e) {
//                            log.error(e).print()
//                        }
//                        log.print()
//                    }
        } catch (Exception e) {
            log.error(e).print()
        }
    }



    String cleanAsterisk(String simplePath) {
        return simplePath.replaceAll("\\*","")
    }

    def initFolders(SgCms sg, def cnf) {
        sg.createFolder(cnf.targetFolder)
                .createDownloadGallery(cnf.downloadGallery)
                .createImageGallery(cnf.imageGallery)
                .createLinkGallery(cnf.linkGallery)
    }

    /**
     * Obtiene los recursos exportados llamando al script ExMappingCnt
     * @return
     */
    JSONObject obtainJsonExport() {
        // La ruta del script:
        String scriptPath = "/system/modules/com.saga.sagasuite.scriptgroovy/" +
                "classes/com/saga/sagasuite/scriptgroovy/ExMappingCnt.groovy";

        //Llama al metodo init del script groovy indicado
        try{
            return (JSONObject) invokeScript(
                    cmso, scriptPath, "init",
                    [cmso, idProceso, conFile].toArray());
        } catch (Exception e) {
            log.error(e).print()
        }

    }

    /**
     * Invoca el método de un script groovy
     * by: rtinoco
     *
     * @param cmso CmsObject
     * @param path Ruta del VFS donde se encuentra el script
     * @param method Método que llamar
     * @param args Argumentos del método
     * @return
     */
    def invokeScript(
            CmsObject cmso, String path,
            String method, Object[] args) {
        def loader = instanceGroovyClassLoader()

        // Obtenmos el contenido del script
        byte[] scriptContent = cmso.readFile(
                cmso.readResource(path)).contents;
        Class aClass = loader.parseClass(
                new String(scriptContent, "UTF-8"));
        GroovyObject groovyObject =
                (GroovyObject) aClass.newInstance();
        groovyObject.invokeMethod(method, args);
    }

    def instanceGroovyClassLoader() {
        if (!groovyClassLoader) {
            groovyClassLoader = new GroovyClassLoader(
                    getClass().getClassLoader());
        }
        return groovyClassLoader
    }

    /**
     * Obtien la lista con los índices de iteración según la configuración del mapeo. Ejemplo:
     *
     * Si en la configuración hemos definido una ruta de mapeo tal que así: Content* /ImageMain /Image*
     * Este método devolverá la lista [0, 2], indicando que en esta ruta se repiten valores en la posición 0
     * y 2 de la ruta. Cada posición (fragmento) de la ruta viene demarcada por el carácter '/'
     *
     * by: rtinoco
     * @param path
     * @return
     */
    List getXPathPattern(String path) {
        def splitter = path.split('/') as ArrayList;
        splitter.collect(
                {if (it.endsWith('*')) splitter.indexOf(it)})
                .inject([], {r, v -> if (v != null) r << v; r});
    }

    /**
     *
     * Genera el nodo correspondiente a la configuración del mapeo en el recurso destino
     *
     * by: rtinoco
     * @param cmsObject CmsObject
     * @param locale Locale
     * @param targetPaths Ruta destino dividida en sus distintas partes como un array
     * @param targetRepetitionIndexes Posiciones en la ruta destino donde se repiten nodos
     * @param currentValueIndexes Índices del valor actual
     * @param content Contenido que creamos
     * @param parentNode Nodo padre del nodo actual que se procesa
     * @param index Índice del nodo que debemos crear
     * @return El nuevo nodo creado
     * @throws Exception
     */
    I_CmsXmlContentValue createTargetNode(
            CmsObject cmsObject, Locale locale, String[] targetPaths,
            List targetRepetitionIndexes, List currentValueIndexes,
            CmsXmlContent content, String parentNode, int index) throws Exception {
        I_CmsXmlContentValue contentValue;

        int currentNodeIndex = getCurrentNodeIndex(
                targetRepetitionIndexes, currentValueIndexes, index);
        final String currentNode = parentNode != null ?
                parentNode + "/" + targetPaths[index]
                : targetPaths[index];


        if (!content.hasValue(currentNode, locale, currentNodeIndex)) {
            /* Esto es necesario porque se pueden dar situaciones en las que se reciba un
            * path con un índice y no se hayan creado los índices previos (vamos a crear el path
            * Content[1]/Image[10]/Image pero no están creados Content[1]/Image[9]/Image, ....). De esta forma
            * aseguramos que todos los índices previos al que se reciben se crean antes */
            for (int i = 0; i <= currentNodeIndex ; i++) {
                if (!content.hasValue(currentNode, locale, i)) {
                    content.addValue(cmsObject, currentNode, locale, i);
                }
            }
            contentValue = content.getValue(currentNode, locale, currentNodeIndex);
        } else {
            contentValue = content.getValue(currentNode, locale, currentNodeIndex);
        }
        // si hemos llegado al nodo final asignamos el valor
        if (index != targetPaths.length - 1) {
            String newParentNode = currentNode + "[" + (currentNodeIndex + 1) + "]";
            contentValue = createTargetNode(cmsObject, locale, targetPaths,
                    targetRepetitionIndexes, currentValueIndexes, content, newParentNode, index + 1);
        }
        contentValue;
    }

    /**
     * Dados los índices en la ruta destino donde se deben repetir valor, los índices del valor original
     * actual y el índice del nodo origen actual a crear obtenemos el índice del nodo destino que estamos
     * creando
     *
     * by: rtinoco
     * @param targetRepetitionIndexes
     * @param currentValueIndexes
     * @param i
     * @return
     */
    int getCurrentNodeIndex(List targetRepetitionIndexes, List currentValueIndexes, int i) {
        if (targetRepetitionIndexes.contains(i)) {
            currentValueIndexes.get(targetRepetitionIndexes.indexOf(i)) - 1;
        } else {
            0;
        }
    }

    /**
     * Asigna el valor de un campo del contenido estructurado
     *
     *
     * @param cmsObject CmsObject
     * @param cfg Mapa con la configuración de la importación
     * @param node Nodo del contenido estructurado al que asignar el valor
     * @param sourcePath Ruta del nodo correspondiente en el recurso origen
     * @param value Valor que asignar
     * @return
     */
    def setNodeValue(GroovyClassLoader classLoader, CmsObject cmsObject, CmsXmlContent content, Map cfg, I_CmsXmlContentValue node, String sourcePath, String value) {

        // Obtenemos el script de transformación del valor si lo hubiera
        String transformerScript = cfg.valueTransformers.get(CmsXmlUtils.removeXpath(node.path));

        // Si existe el script lo ejecutamos
        if (transformerScript) {
            invokeScript(cmsObject, classLoader, transformerScript, 'transformValue', [cmsObject, cfg, content, node, sourcePath, value] as Object[]);
        } else {
            // si no existe el script ejecutamos la asignación de valor por defecto
            switch (node.typeName) {
            // Si asignamos el valor de tipo fichero descargamos el fichero del origen,
            // lo almacenamos en la galería configurada y asignamo el valor
                case "OpenCmsVfsFile":
                    CmsResource resource = createFile(cmsObject, cfg, value);
                    node.setStringValue(cmsObject, resource?.rootPath);
                    break;
            // Si se trata de una categoría, creamos las categoría correspondientes en el destino
            // y asignamos el valor
                case "OpenCmsCategory":
                    def categories = StringUtils.split(value, ';,') as ArrayList;
                    createFolder(cmsObject, cfg.categoryFolder, 'folder');
                    def paths = [];
                    categories.sort().each {
                        String path = (cfg.categoryFolder + '/' + it).replaceAll("/+", "/");
                        createFolder(cmsObject, path, 'folder');
                        paths << path;
                    }
                    node.setStringValue(cmsObject, paths.join(','));
                    break;
            // En cualquier otro caso asignamos el valor de forma directa
                default:
                    if (cfg.valueMappings.get(sourcePath)) {
                        String mappedValue = cfg.valueMappings.get(sourcePath).get(value);
                        node.setStringValue(cmsObject, StringUtils.defaultString(mappedValue));
                    } else {
                        node.setStringValue(cmsObject, value);
                    }

            }
        }

    }

    /**
     *  Asigna los valores por defecto configurados en el mapeo. El mapeo permite asignar valores por
     *  defecto a nodos compuestos en el recurso destino que no tienen una equivalencia en el origen.
     *  Un ejemplo de ello sería:
     *  - Queremos mapear el nodo origen Content/ImageMain/File al nodo destino Content/Gallery/Image/Image
     *  - El tipo Content/Gallery/Image es un tipo complejo que tiene los campos Image, Position, Width, Height
     *  - Pero en el origen no existen los campos Position, Width y Height, aún así queremos que se asignen valores
     *  a estos campos
     *  - En la configuración del mapeo podemos definir valores por defecto para estos campos en el recurso destino
     *  de la siguiente forma:
     *
     *  {
     *     ...
     *     defaultValues : {
     *         "Content/Gallery/Image/Position" : "top",
     *         "Content/Gallery/Image/Width" : "100%",
     *         "Content/Gallery/Image/Height" : "200",
     *     }
     *     ...
     *  }
     *
     *  De esta forma, cada vez que se cree un nodo Content/Gallery/Image/Image se va a tomar su nodo padre
     *  (Content/Gallery/Image) y se va a comprobar si esta ruta comienza por alguna de las definidads en 'defaultValues',
     *  si es así, se crea el nodo correspondiente y se asigna el valor
     *
     * @param cmsObject CmsObject
     * @param locale Locale
     * @param content CmsXmlContent del recurso que se está creando
     * @param targetNode Nodo al que se le está asignando el valor
     * @param cfg Configuración del mapeo
     * @return
     */
    def createDefaultValues(CmsObject cmsObject, Locale locale, CmsXmlContent content, I_CmsXmlContentValue targetNode, Map cfg) {
        if (CmsXmlUtils.isDeepXpath(targetNode.path)) {
            final String parentPath = CmsXmlUtils.removeLastXpathElement(CmsXmlUtils.removeXpath(targetNode.path));
            Set defVals = cfg.defaultValues.keySet().findAll {
                parentPath.startsWith(CmsXmlUtils.removeLastXpathElement(it));
            }

            defVals.each {
                try {
                    String defaultValue = cfg.defaultValues.get(it);
                    def pathLength = CmsXmlUtils.removeLastXpathElement(it).split("/").length;
                    def lastPath = CmsXmlUtils.getLastXpathElement(it);
                    def nodePathFragments = CmsXmlUtils.removeLastXpathElement(targetNode.path).split("/") as List;
                    def defValuePath = nodePathFragments.subList(0, pathLength).join("/") + "/" + lastPath;

                    if (content.hasValue(defValuePath, locale, 0)) {
                        if (StringUtils.isEmpty(content.getValue(defValuePath, locale, 0).getStringValue(cmsObject))) {
                            def value = content.getValue(defValuePath, locale, 0);
                            value.setStringValue(cmsObject, defaultValue);
                        }
                    } else {
                        def value = content.addValue(cmsObject, defValuePath, locale, 0);
                        value.setStringValue(cmsObject, defaultValue);
                    }

                } catch (e) {

                }
            }
        }
    }
}
