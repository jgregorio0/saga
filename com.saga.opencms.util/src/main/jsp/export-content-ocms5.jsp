<%@ page buffer="none" session="false" %>
<%@ page import="com.isotrol.opencms.log.Logger" %>
<%@ page import="com.opencms.core.CmsException" %>
<%@ page import="com.opencms.file.CmsFile" %>
<%@ page import="com.opencms.file.CmsObject" %>
<%@ page import="com.opencms.file.CmsProject" %>
<%@ page import="com.opencms.file.CmsResource" %>
<%@ page import="com.opencms.flex.jsp.CmsJspActionElement" %>
<%@ page import="org.jdom.Document" %>
<%@ page import="org.jdom.Element" %>
<%@ page import="org.jdom.JDOMException" %>
<%@ page import="org.jdom.input.SAXBuilder" %>
<%@ page import="org.jdom.output.Format" %>
<%@ page import="org.jdom.output.XMLOutputter" %>
<%@ page import="org.xml.sax.InputSource" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.util.*" %>

<%--
  Realiza la exportación a JSON. Los parámetros que recibe son:
  - folder: Carpeta contenedora de los recursos
  - type: Tipo del recurso que exportar
  - since: Fecha de última modificación expresada en milisegundos a partir de la que obtener los recurso
  - max: Número máximo de recursos que retornar
  - start: Índice del recurso a partir del que empieza a exportar [1, total]

  final String folder = request.getParameter("folder");
        final String type = request.getParameter("type");
        final String since = request.getParameter("since");
        final String max = request.getParameter("max");

--%>

<%! public static final int MAX_LENGTH_XML = 1040000;

    public static final String ISO_8859_1 = "ISO-8859-1";

    CmsProject project;

    private class FolderException extends Exception {
        FolderException() {
            super();
        }
    }

    private class TypeException extends Exception {
        TypeException() {
            super();
        }
    }

    private class SinceException extends Exception {
        SinceException() {
            super();
        }
    }

    private class MaxException extends Exception {
        MaxException() {
            super();
        }
    }

    private class DeletedException extends Exception {
        DeletedException() {
            super();
        }
    }

    private class MaxLengthXMLException extends Exception {
        MaxLengthXMLException() {
            super();
        }
    }

    private class EmptyContentException extends Exception {
        EmptyContentException() {
            super();
        }
    }

    private String getStackTraceAsString(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);

        return stringWriter.toString();
    }


    /**
     * Personaliza el objecto CmsObject al principio de la ejecucion
     * @param cmso
     * @throws CmsException
     */
    private void customizeCmsObject(CmsObject cmso) throws CmsException {
        // ONLINE = 1
        // OFFLINE = 4
        this.project = cmso.getRequestContext().currentProject();
        cmso.getRequestContext().setCurrentProject(4);

    }

    /**
     * Restaura el objecto CmsObject al final de la ejecucion
     * @param cmso
     * @throws CmsException
     */
    private void restoreCmsObject(CmsObject cmso) throws CmsException {
        // ONLINE = 1
        // OFFLINE = 4
        cmso.getRequestContext().setCurrentProject(this.project.getId());
    }

    /**
     * Valida si recibe el parámetro folder obligatorio
     * @param folder
     * @return
     * @throws TypeException
     */
    private boolean validateFolder(String folder) throws FolderException {
        if (folder == null || folder.equals("")) {
            throw new FolderException();
        }
        return true;
    }

    /**
     * Valida si no se requiere tipo
     * o el recurso es del tipo requerido
     * @param type
     * @param resource
     * @return
     * @throws TypeException
     */
    private boolean validateType(String type, CmsResource resource) throws TypeException {
        if (type != null && !type.equals("")) {
            int typeInt = Integer.parseInt(type);
            if (resource.getType() != typeInt) {
                // el recurso no es del tipo requerido
                throw new TypeException();
            }
        }
        return true;
    }

    /**
     * Valida si no se requiere fecha de modificacion
     * o el recurso ha sido modificado posteriormente a la fecha de modificacion indicada
     * @param since
     * @param resource
     * @return
     * @throws TypeException
     */
    private boolean validateSince(String since, CmsResource resource) throws SinceException {
        if (since != null && !since.equals("")) {
            long sinceLong = Long.parseLong(since);
            if (resource.getDateLastModified() < sinceLong) {
                throw new SinceException();
            }
        }
        return true;
    }

    /**
     * Valida si no se requiere fecha de modificacion
     * o el recurso ha sido modificado posteriormente a la fecha de modificacion indicada
     * @param max
     * @param iExpResources
     * @return
     * @throws TypeException
     */
    private boolean validateMax(String max, int iExpResources) throws MaxException {
        if (max != null && !max.equals("")) {
            int maxInt = Integer.parseInt(max);
            if (iExpResources >= maxInt) {
                throw new MaxException();
            }
        }
        return true;
    }

    /**
     * Valida que el recurso no ha sido eliminado
     * C_STATE_UNCHANGED = 0
     * C_STATE_CHANGED = 1
     * C_STATE_NEW = 2
     * C_STATE_DELETED = 3
     * @param resource
     * @return
     */
    private boolean validateStatus(CmsResource resource) throws DeletedException {
        if (resource.getState() == 3) {
            throw new DeletedException();
        }

//        m_state == 2
        return true;
    }

    /**
     * Valida que el contenido del recurso no es vacio.
     * @param outputter
     * @param document
     * @return
     */
    private boolean validateMaxLengthXML(XMLOutputter outputter, Document document) throws MaxLengthXMLException {
        int limitLength = outputter.outputString(document).length();
        if (limitLength > MAX_LENGTH_XML) {
            throw new MaxLengthXMLException();
        }

        return true;
    }

    /**
     * Valida que el contenido del recurso no es vacio.
     * @param content
     * @return
     */
    private boolean validateNotEmptyContent(String content) throws EmptyContentException {
        if (content.trim().length() == 0) {
            throw new EmptyContentException();
        }

        return true;
    }

    /**
     * Obtiene contenido XML del recurso
     * @param cmso
     * @param resource
     * @param builder
     * @return
     * @throws CmsException
     * @throws JDOMException
     * @throws IOException
     */
    private Element xmlContent(CmsObject cmso, CmsResource resource, SAXBuilder builder)
            throws Exception {
        Element xmlContent = new Element("content");

        CmsFile file = cmso.readFile(resource.getAbsolutePath());

        // escape XML
        String fileContents = new String(file.getContents(), ISO_8859_1);
//        fileContents = escapeUnicode(fileContents);
        if (validateNotEmptyContent(fileContents)) {
            // create XML document
            InputSource inpS = new InputSource(new ByteArrayInputStream(fileContents.getBytes(ISO_8859_1)));
            inpS.setEncoding(ISO_8859_1);
            Document xmlDoc = builder.build(inpS);

/*        InputSource inpS = new InputSource(new ByteArrayInputStream(file.getContents()));
        inpS.setEncoding(ISO_8859_1);
        Document xmlDoc = builder.build(inpS);*/

//            Element xmlContent = new Element("content");
            xmlContent.addContent((xmlDoc.getRootElement().detach()));
        }


        return xmlContent;
    }

    /**
     * Obtiene las propiedades heredadas sin incluir las individuales
     * @param cmso
     * @param resource
     * @param propsIndividual
     * @return
     * @throws CmsException
     */
    private Map propertiesInherited(CmsObject cmso, CmsResource resource, Map propsIndividual) throws CmsException {
        Map propsInherited = cmso.readProperties(resource.getAbsolutePath(), true);

        Set entries = propsIndividual.entrySet();
        Iterator itProps = entries.iterator();
        while (itProps.hasNext()) {
            Map.Entry entry = (Map.Entry) itProps.next();
            String key = (String) entry.getKey();
            if (propsInherited.containsKey(key)) {
                propsInherited.remove(key);
            }
        }

        return propsInherited;
    }

    /**
     * XML de propiedades a partir de un Map<String,String>
     * @param propsIndividual
     * @param propertiesIndividual
     * @return
     */
    private Element map2XML(Map propsIndividual, String propertiesIndividual) {
        Set entries = propsIndividual.entrySet();
        Iterator itProps = entries.iterator();
        Element xmlProperites = new Element(propertiesIndividual);
        while (itProps.hasNext()) {
            Map.Entry entry = (Map.Entry) itProps.next();
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());

            Element xmlProp = new Element(key).addContent(value);
            xmlProperites.addContent(xmlProp);
        }
        return xmlProperites;
    }

    /**
     * XML con informacion basica del recurso
     * @param resource
     * @return
     */
    private Element xmlFile(CmsResource resource) {
        int resourceType = resource.getType();
        long dateCreated = resource.getDateCreated();
        long dateLastModified = resource.getDateLastModified();
        int state = resource.getState();

        Map file = new HashMap();
        file.put("type", String.valueOf(resourceType));
        file.put("dateCreated", String.valueOf(dateCreated));
        file.put("dateLastModified", String.valueOf(dateLastModified));
        file.put("state", String.valueOf(state));

        Element xmlFile = map2XML(file, "file");
        return xmlFile;
    }

    private String escapeUnicode(String source) {
        if (source == null) {
            return null;
        } else {
            StringBuffer result = new StringBuffer(source.length() * 2);

            for (int i = 0; i < source.length(); ++i) {
                char ch = source.charAt(i);
                switch (ch) {
                    /*case '"':
                        result.append("&quot;");
                        break;*/
                    case '&':
                        int terminatorIndex;
                        if ((terminatorIndex = source.indexOf(";", i)) > 0) {
                            if (source.substring(i + 1, terminatorIndex).matches("#[0-9]+")) {
                                result.append(ch);
                            } else if (source.substring(i + 1, terminatorIndex).matches("#[0-9]+")) {
                            } else {
                                result.append("&amp;");
                            }
                        } else {
                            result.append("&amp;");
                        }
                        break;
                    /*case '<':
                        result.append("&lt;");
                        break;
                    case '>':
                        result.append("&gt;");
                        break;*/
                    default:
                        result.append(ch);
                }
            }

            return new String(result);
        }
    }
%>
<%
    CmsJspActionElement jsp = new CmsJspActionElement(pageContext, request, response);
    CmsObject cmso = jsp.getCmsObject();

    // project online
    customizeCmsObject(cmso);
    try {

        // Map<String, Map<String, String>> exp = new LinkedHashMap<String, Map<String, String>>();
        Element xmlResources = new Element("resources");
        Document document = new Document(xmlResources);

        final String folder = request.getParameter("folder");
        final String type = request.getParameter("type");
        final String since = request.getParameter("since");
        final String max = request.getParameter("max");
        final String start = request.getParameter("start");

        // XML Builder
        SAXBuilder builder = new SAXBuilder();

        // XML Out
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
//        Format format = Format.getCompactFormat();
        format.setEncoding(ISO_8859_1);
        /*prettyFormat.setEscapeStrategy(new EscapeStrategy() {
            public boolean shouldEscape(char c) {
                switch (c) {
                    case '"':
                        return true;
                    case '&':
                        return true;
                    default:
                        return false;
                }
            }
        });*/
        outputter.setFormat(format);

        if (validateFolder(folder)) {
            // dada una carpeta obtenemos los recurso contenidos
            Vector vector = cmso.getResourcesInFolder(folder);
            int total = vector.size();
            xmlResources.setAttribute("total", String.valueOf(total));

            // inicializamos el bucle
            int iExpResources = 0;
            boolean isXMLOverloaded = false;
            int startInt = 1;
            if (start != null && !start.equals("")) {
                int startParam = Integer.valueOf(start).intValue();
                startInt = startParam > 0 ? startParam : 1;
            }

            xmlResources.setAttribute("begin", String.valueOf(start));
            for (int iResource = startInt - 1; iResource < vector.size() && !isXMLOverloaded; iResource++) {
                int countResource = iResource + 1;

                // para cada recurso obtenemos sus datos
                CmsResource resource = (CmsResource) vector.get(iResource);

                Logger.debug(this.getClass(), countResource + "/" + total + " -- " + resource.getAbsolutePath());
//                out.println(i + "/" + total + " -- " + resource.getAbsolutePath());
                try {

                    // XML element current resource
                    Element xmlResource = new Element("resource");
                    xmlResource.setAttribute("rootPath", resource.getAbsolutePath());
                    xmlResource.setAttribute("resourceId", String.valueOf(resource.getResourceId()));
                    xmlResource.setAttribute("count", String.valueOf(countResource));

                    // valida la longitud del XML para cortarlo antes de sobrepasarla
                    if (validateMaxLengthXML(outputter, document)) {

                        // incluye ultimo recurso
                        xmlResources.setAttribute("end", String.valueOf(countResource));

                        if (validateType(type, resource) &&
                                validateSince(since, resource) &&
                                validateMax(max, iExpResources) &&
                                validateStatus(resource)) {

                            // informacion basica del recurso
                            Element xmlFile = xmlFile(resource);
                            xmlResource.addContent(xmlFile);

                            // contenido del recurso
                            Element xmlContent = xmlContent(cmso, resource, builder);
                            xmlResource.addContent(xmlContent);

                            // propiedades individuales y heredadas del recurso
                            Map propsIndividual = cmso.readProperties(resource.getAbsolutePath(), false);
                            Element xmlProperitesInd = map2XML(propsIndividual, "propertiesIndividual");
                            xmlResource.addContent(xmlProperitesInd);

                            Map propsInherited = propertiesInherited(cmso, resource, propsIndividual);
                            Element xmlProperitesInherited = map2XML(propsInherited, "propertiesInherited");
                            xmlResource.addContent(xmlProperitesInherited);

                            // incluye resource en export xml
                            xmlResources.addContent(xmlResource);
                            iExpResources++;
                        }
                    }

                } catch (TypeException e) {
                    Logger.debug(this.getClass(), "El recurso " + resource + " no es del tipo indicado: " + type);
                } catch (SinceException e) {
                    Logger.debug(this.getClass(), "El recurso " + resource + " no tiene fecha de modificacion posterior a " + since + " > " + resource.getDateLastModified());
                } catch (MaxException e) {
                    Logger.debug(this.getClass(), "El recurso " + resource + " está por encima del limite máximo de recursos a exportar " + max);
                } catch (DeletedException e) {
                    Logger.debug(this.getClass(), "El recurso " + resource + " ha sido eliminado");
                } catch (EmptyContentException e) {
                    Logger.debug(this.getClass(), "El contenido del recurso " + resource + " es vacío");
                } catch (MaxLengthXMLException e) {
                    Logger.debug(this.getClass(), "Se ha sobrepasado el limite maximo de logitud para el XML fijado en " + MAX_LENGTH_XML);
                    isXMLOverloaded = true;
                } catch (Exception e) {
                    Logger.error(this.getClass(), "El recurso " + resource + " no ha podido ser exportado eliminado", e);

                    // TODO corta la ejecucion, eliminar tras desarrollo
                    throw e;
                }
            }
        }

        outputter.output(document, out);
    } catch (FolderException e) {
        Logger.debug(this.getClass(), "El parametro folder es obligatorio");
        // TODO out.print("<export></export>");
    } catch (Exception e) {
        out.print(getStackTraceAsString(e));
        // TODO out.print("<export></export>");
    } finally {
        restoreCmsObject(cmso);
    }
%>