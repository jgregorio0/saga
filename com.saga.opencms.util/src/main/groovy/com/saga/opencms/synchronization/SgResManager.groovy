package com.saga.opencms.synchronization

import org.apache.commons.fileupload.FileItem
import org.apache.commons.logging.Log
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.file.CmsProject
import org.opencms.file.CmsProperty
import org.opencms.file.CmsResource
import org.opencms.file.CmsResourceFilter
import org.opencms.file.types.I_CmsResourceType
import org.opencms.jsp.CmsJspActionElement
import org.opencms.loader.CmsLoaderException
import org.opencms.main.CmsException
import org.opencms.main.CmsIllegalArgumentException
import org.opencms.main.CmsLog
import org.opencms.main.CmsRuntimeException
import org.opencms.main.OpenCms
import org.opencms.util.CmsRequestUtil
import org.opencms.xml.CmsXmlContentDefinition
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.CmsXmlException
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.opencms.xml.types.I_CmsXmlContentValue

import javax.servlet.http.HttpServletRequest

/**
 * Created by jgregorio on 02/03/2018.
 */
class SgResManager {
    private CmsJspActionElement cms;
    private CmsObject cmsObject;
    private static final Log LOG = CmsLog.getLog(SgResManager.class);

    public ResourceManager(CmsJspActionElement cms) {
        this.cms = cms;
        this.cmsObject = cms.getCmsObject();
    }

    public ResourceManager(CmsObject cmsObjectAdmin) {
        this.cms = null;
        this.cmsObject = cmsObjectAdmin;
    }

    public ResourceManager(CmsJspActionElement cms, CmsObject cmsObjectAdmin) {
        this.cms = cms;
        this.cmsObject = cmsObjectAdmin;
    }

    public boolean uploadImage(byte[] image, String title, String vfsPath, boolean publish) {
        boolean res = false;

        try {
            if(!this.cmsObject.existsResource(vfsPath, CmsResourceFilter.ALL)) {
                int e = OpenCms.getResourceManager().getResourceType("image").getTypeId();
                ArrayList propiedades = new ArrayList();
                propiedades.add(new CmsProperty("Title", title, title));
                this.cmsObject.createResource(vfsPath, e, image, propiedades);
            } else {
                CmsFile e1 = this.cmsObject.readFile(vfsPath);
                e1.setContents(image);
                this.cmsObject.writeFile(e1);
            }

            if(publish) {
                this.cmsObject.unlockResource(vfsPath);
                OpenCms.getPublishManager().publishResource(this.cmsObject, vfsPath);
            }

            res = true;
        } catch (CmsException var8) {
            var8.printStackTrace();
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return res;
    }

    public boolean uploadBinary(byte[] image, String title, String vfsPath, boolean publish) {
        boolean res = false;

        try {
            if(!this.cmsObject.existsResource(vfsPath, CmsResourceFilter.ALL)) {
                int e = OpenCms.getResourceManager().getResourceType("binary").getTypeId();
                ArrayList propiedades = new ArrayList();
                propiedades.add(new CmsProperty("Title", title, title));
                this.cmsObject.createResource(vfsPath, e, image, propiedades);
            } else {
                CmsFile e1 = this.cmsObject.readFile(vfsPath);
                e1.setContents(image);
                this.cmsObject.writeFile(e1);
            }

            if(publish) {
                this.cmsObject.unlockResource(vfsPath);
                OpenCms.getPublishManager().publishResource(this.cmsObject, vfsPath);
            }

            res = true;
        } catch (CmsException var8) {
            var8.printStackTrace();
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return res;
    }

    public HashMap uploadFile(String path) {
        HttpServletRequest request = this.cms.getRequest();
        List files = CmsRequestUtil.readMultipartFileItems(request);
        HashMap attachmentMap = null;
        boolean change = false;
        CmsProject project = this.cms.getRequestContext().currentProject();

        try {
            if(files != null) {
                if(project.getName().equals("Online")) {
                    this.cmsObject.getRequestContext().setCurrentProject(this.cmsObject.readProject("Offline"));
                    change = true;
                }

                attachmentMap = new HashMap();
                Iterator e = files.iterator();
                ArrayList pathsAttachment = new ArrayList();
                ArrayList pathsImage = new ArrayList();

                while(e.hasNext()) {
                    FileItem fi = (FileItem)e.next();
                    String fieldType = fi.getFieldName();
                    String type = "";
                    if(fieldType.indexOf("Attachment") != -1) {
                        pathsAttachment.add(path + fi.getName().replaceAll(" ", "_"));
                        type = "plain";
                    } else {
                        pathsImage.add(path + fi.getName().replaceAll(" ", ""));
                        type = "image";
                    }

                    int t = OpenCms.getResourceManager().getResourceType(type).getTypeId();
                    this.cmsObject.createResource(path, t, fi.get(), (List)null);
                    this.cmsObject.unlockResource(path);
                    OpenCms.getPublishManager().publishResource(this.cmsObject, path);
                }

                if(change) {
                    this.cmsObject.getRequestContext().setCurrentProject(project);
                }

                if(pathsAttachment.size() > 0) {
                    attachmentMap.put("Attachment", pathsAttachment);
                }

                if(pathsImage.size() > 0) {
                    attachmentMap.put("Image", pathsImage);
                }
            }
        } catch (CmsLoaderException var14) {
            LOG.error(var14.toString());
            var14.printStackTrace();
            attachmentMap = null;
        } catch (CmsIllegalArgumentException var15) {
            LOG.error(var15.toString());
            var15.printStackTrace();
            attachmentMap = null;
        } catch (CmsException var16) {
            LOG.error(var16.toString());
            var16.printStackTrace();
            attachmentMap = null;
        } catch (Exception var17) {
            LOG.error(var17.toString());
            var17.printStackTrace();
        }

        return attachmentMap;
    }

    public boolean copyResource(String fuente, String destino) {
        boolean existsDestino = this.cmsObject.existsResource(destino, CmsResourceFilter.ALL);
        boolean existsFuente = this.cmsObject.existsResource(fuente, CmsResourceFilter.ALL);
        boolean change = false;
        boolean resultado = false;

        try {
            CmsProject e = this.cmsObject.getRequestContext().currentProject();
            if(e.getName().equals("Online")) {
                this.cmsObject.getRequestContext().setCurrentProject(this.cmsObject.readProject("Offline"));
                change = true;
            }

            if(existsFuente && !existsDestino) {
                this.cmsObject.lockResource(fuente);
                this.cmsObject.copyResource(fuente, destino);
                this.cmsObject.unlockResource(fuente);
                this.cmsObject.unlockResource(destino);
                OpenCms.getPublishManager().publishResource(this.cmsObject, destino);
                resultado = true;
            } else {
                LOG.error("Error al copiar un recurso, o no existe la fuente o ya existe el recurso de destino.");
            }

            if(change) {
                this.cmsObject.getRequestContext().setCurrentProject(e);
            }
        } catch (Exception var8) {
            var8.printStackTrace();
            LOG.error("Error al copiar un recurso" + var8.toString());
        }

        return resultado;
    }

    public boolean createSibling(String fuente, String destino) {
        boolean existsDestino = this.cmsObject.existsResource(destino, CmsResourceFilter.ALL);
        boolean existsFuente = this.cmsObject.existsResource(fuente, CmsResourceFilter.ALL);
        boolean success = true;
        boolean change = false;
        boolean resultado = false;

        try {
            CmsProject e = this.cmsObject.getRequestContext().currentProject();
            if(e.getName().equals("Online")) {
                this.cmsObject.getRequestContext().setCurrentProject(this.cmsObject.readProject("Offline"));
                change = true;
            }

            if(existsFuente && !existsDestino) {
                this.cmsObject.lockResource(fuente);
                this.cmsObject.createSibling(fuente, destino, (List)null);
                this.cmsObject.unlockResource(fuente);
                OpenCms.getPublishManager().publishResource(this.cmsObject, destino);
                resultado = true;
            }

            if(change) {
                this.cmsObject.getRequestContext().setCurrentProject(e);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
            LOG.error(var9.toString());
        }

        return resultado;
    }

    /** @deprecated */
    @Deprecated
    public boolean saveResource(HashMap data, String resource, int type, boolean publish) {
        return this.saveCmsResource(data, resource, type, publish, (String)null) != null;
    }

    /** @deprecated */
    @Deprecated
    public boolean saveResource(HashMap data, String resource, int type, boolean publish, String customLocale) {
        return this.saveCmsResource(data, resource, type, publish, customLocale) != null;
    }

    public CmsResource saveCmsResource(HashMap data, String resource, String resourceTypeName, boolean publish) {
        return this.saveCmsResource(data, resource, resourceTypeName, publish, (String)null);
    }

    public CmsResource saveCmsResource(HashMap data, String resource, String resourceTypeName) {
        return this.saveCmsResource(data, resource, resourceTypeName, false, (String)null);
    }

    public CmsResource saveCmsResource(HashMap data, String resource, int type, boolean publish, String customLocale) {
        try {
            I_CmsResourceType e = OpenCms.getResourceManager().getResourceType(type);
            return this.saveCmsResource(data, resource, e.getTypeName(), publish, customLocale);
        } catch (CmsLoaderException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public CmsResource saveCmsResource(HashMap data, String resource, String type, boolean publish, String customLocale) {
        boolean success = true;
        Object cmsResource = null;

        try {
            boolean exc = this.cmsObject.existsResource(resource, CmsResourceFilter.ALL);
            CmsXmlContent content = null;
            Locale localizacion = this.cmsObject.getRequestContext().getLocale();
            if(customLocale != null) {
                localizacion = new Locale(customLocale);
            }

            if(exc) {
                CmsFile keys = this.cmsObject.readFile(resource);
                cmsResource = keys;
                content = CmsXmlContentFactory.unmarshal(this.cmsObject, keys);
                List itKeys = content.getLocales();
                if(!itKeys.contains(localizacion)) {
                    content.copyLocale((Locale)itKeys.get(0), localizacion);
                }
            } else {
                String keys1 = Schemas.getSchemaByType(type);
                CmsXmlContentDefinition itKeys1 = CmsXmlContentDefinition.unmarshal(keys1, new CmsXmlEntityResolver(this.cmsObject));
                content = CmsXmlContentFactory.createDocument(this.cmsObject, localizacion, "UTF-8", itKeys1);
            }

            Set keys2 = data.keySet();
            Iterator itKeys2 = keys2.iterator();
            boolean modified = false;

            while(itKeys2.hasNext()) {
                String key = (String)itKeys2.next();
                Object value = data.get(key);
                if(value instanceof ArrayList) {
                    modified = this.manageMultipleContent((ArrayList)value, key, localizacion, content) || modified;
                } else if(value instanceof HashMap) {
                    modified = this.manageNestedContent((HashMap)value, key, localizacion, content) || modified;
                } else if(value instanceof Choice) {
                    modified = this.manageChoiceContent(((Choice)value).getSubfields(), key, localizacion, content) || modified;
                } else {
                    modified = this.manageSimpleContent(key, (String)value, localizacion, content) || modified;
                }
            }

            if(modified) {
                cmsResource = this.createOrEditResource(resource, type, content, publish);
                if(cmsResource == null) {
                    success = false;
                } else {
                    success = true;
                }
            } else {
                success = true;
            }
        } catch (Exception var16) {
            var16.printStackTrace();
            LOG.error(var16.toString());
            success = false;
        }

        return (CmsResource)cmsResource;
    }

    private CmsXmlContent generaXmlContent(HashMap data, CmsXmlContent content, Locale localizacion) {
        try {
            content.getLocaleNode(localizacion);
        } catch (CmsRuntimeException var9) {
            try {
                content.addLocale(this.cmsObject, localizacion);
            } catch (CmsXmlException var8) {
                var8.printStackTrace();
                return null;
            }
        }

        try {
            Set exc = data.keySet();
            Iterator itKeys = exc.iterator();

            while(itKeys.hasNext()) {
                String key = (String)itKeys.next();
                Object value = data.get(key);
                if(value instanceof ArrayList) {
                    this.manageMultipleContent((ArrayList)value, key, localizacion, content);
                } else if(value instanceof HashMap) {
                    this.manageNestedContent((HashMap)value, key, localizacion, content);
                } else {
                    this.manageSimpleContent(key, (String)value, localizacion, content);
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
            LOG.error(var10.toString());
        }

        return content;
    }

    /** @deprecated */
    @Deprecated
    public boolean editResource(HashMap data, String resource, int type, boolean publish) {
        CmsResource cmsResource = null;
        boolean success = true;

        try {
            CmsFile exc = this.cmsObject.readFile(resource);
            CmsXmlContent content = CmsXmlContentFactory.unmarshal(this.cmsObject, exc);
            Locale localizacion = this.cmsObject.getRequestContext().getLocale();
            Set keys = data.keySet();
            Iterator itKeys = keys.iterator();

            while(itKeys.hasNext()) {
                String e = (String)itKeys.next();
                Object value = data.get(e);
                if(value instanceof ArrayList) {
                    this.manageMultipleContent((ArrayList)value, e, localizacion, content);
                } else if(value instanceof HashMap) {
                    this.manageNestedContent((HashMap)value, e, localizacion, content);
                } else {
                    this.manageSimpleContent(e, (String)value, localizacion, content);
                }
            }

            try {
                I_CmsResourceType e1 = OpenCms.getResourceManager().getResourceType(type);
                cmsResource = this.createOrEditResource(resource, e1.getTypeName(), content, publish);
            } catch (CmsLoaderException var14) {
                var14.printStackTrace();
            }

            if(cmsResource == null) {
                success = false;
            } else {
                success = true;
            }
        } catch (Exception var15) {
            var15.printStackTrace();
            LOG.error(var15.toString());
            success = false;
        }

        return success;
    }

    /** @deprecated */
    @Deprecated
    public boolean editResource(HashMap data, String resource) {
        return this.editResource((HashMap)data, (String)resource, true);
    }

    /** @deprecated */
    @Deprecated
    public boolean editResource(HashMap data, String resource, boolean publish) {
        CmsResource cmsResource = null;
        boolean success = true;

        try {
            CmsFile exc = this.cmsObject.readFile(resource);
            CmsXmlContent content = CmsXmlContentFactory.unmarshal(this.cmsObject, exc);
            Locale localizacion = this.cmsObject.getRequestContext().getLocale();
            Set keys = data.keySet();
            Iterator itKeys = keys.iterator();

            while(itKeys.hasNext()) {
                String key = (String)itKeys.next();
                Object value = data.get(key);
                if(value instanceof ArrayList) {
                    this.manageMultipleContent((ArrayList)value, key, localizacion, content);
                } else if(value instanceof HashMap) {
                    this.manageNestedContent((HashMap)value, key, localizacion, content);
                } else {
                    this.manageSimpleContent(key, (String)value, localizacion, content);
                }
            }

            cmsResource = this.editResource((String)resource, (CmsXmlContent)content, publish);
            if(cmsResource == null) {
                success = false;
            } else {
                success = true;
            }
        } catch (Exception var13) {
            var13.printStackTrace();
            LOG.error(var13.toString());
            success = false;
        }

        return success;
    }

    public boolean addCategory(String resource, String category, String fieldCategory, String localeStr, Boolean publish) throws Exception {
        Object cmsResource = null;
        boolean success = true;
        boolean change = false;
        if(fieldCategory != null && category != null) {
            CmsProject project = this.cmsObject.getRequestContext().getCurrentProject();
            if(project.getName().equals("Online")) {
                this.cmsObject.getRequestContext().setCurrentProject(this.cmsObject.readProject("Offline"));
                change = true;
            }

            CmsFile cmsFile = this.cmsObject.readFile(resource);
            CmsXmlContent content = CmsXmlContentFactory.unmarshal(this.cmsObject, cmsFile);
            Locale localizacion = this.cmsObject.getRequestContext().getLocale();
            if(localeStr != null) {
                localizacion = new Locale(localeStr);
            }

            I_CmsXmlContentValue contentValue = null;
            if(content.hasValue(fieldCategory, localizacion, 0) && category != null) {
                contentValue = content.getValue(fieldCategory, localizacion, 0);
                String byteContent = contentValue.getStringValue(this.cmsObject);
                if(byteContent != null && byteContent.length() > 0) {
                    byteContent = byteContent + "," + category;
                } else {
                    byteContent = category;
                }

                contentValue.setStringValue(this.cmsObject, byteContent);
            } else {
                contentValue = content.addValue(this.cmsObject, fieldCategory, localizacion, 0);
                contentValue.setStringValue(this.cmsObject, category);
            }

            this.cmsObject.lockResource(resource);
            byte[] byteContent1 = content.marshal();
            cmsFile.setContents(byteContent1);
            this.cmsObject.writeFile(cmsFile);
            cmsFile = this.cmsObject.readFile(resource);
            content = CmsXmlContentFactory.unmarshal(this.cmsObject, cmsFile);
            cmsFile = content.getHandler().prepareForWrite(this.cmsObject, content, cmsFile);
            this.cmsObject.unlockResource(resource);
            if(publish.booleanValue()) {
                OpenCms.getPublishManager().publishResource(this.cmsObject, resource);
            }

            if(cmsFile == null) {
                success = false;
            } else {
                success = true;
            }

            if(change) {
                this.cmsObject.getRequestContext().setCurrentProject(project);
            }

            return success;
        } else {
            return false;
        }
    }

    protected CmsResource createOrEditResource(String resource, String typeName, CmsXmlContent content, boolean publish) {
        boolean exists = this.cmsObject.existsResource(resource, CmsResourceFilter.ALL);
        boolean change = false;
        Object cmsResource = null;

        try {
            CmsProject e = this.cmsObject.getRequestContext().getCurrentProject();
            if(e.getName().equals("Online")) {
                this.cmsObject.getRequestContext().setCurrentProject(this.cmsObject.readProject("Offline"));
                change = true;
            }

            byte[] byteContent = content.marshal();
            CmsFile cmsFile;
            if(exists) {
                this.cmsObject.lockResource(resource);
                cmsFile = this.cmsObject.readFile(resource);
                cmsFile.setContents(byteContent);
                this.cmsObject.writeFile(cmsFile);
                cmsFile = this.cmsObject.readFile(resource);
                content = CmsXmlContentFactory.unmarshal(this.cmsObject, cmsFile);
                cmsFile = content.getHandler().prepareForWrite(this.cmsObject, content, cmsFile);
                this.cmsObject.unlockResource(resource);
                if(publish) {
                    OpenCms.getPublishManager().publishResource(this.cmsObject, resource);
                }

                cmsResource = cmsFile;
            } else {
                cmsResource = this.cmsObject.createResource(resource, OpenCms.getResourceManager().getResourceType(typeName), byteContent, new ArrayList());
                cmsFile = this.cmsObject.readFile(resource);
                content = CmsXmlContentFactory.unmarshal(this.cmsObject, cmsFile);
                content.getHandler().prepareForWrite(this.cmsObject, content, cmsFile);
                this.cmsObject.unlockResource(resource);
                if(publish) {
                    OpenCms.getPublishManager().publishResource(this.cmsObject, resource);
                }
            }

            if(change) {
                this.cmsObject.getRequestContext().setCurrentProject(e);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
            LOG.error(var11.toString());
        }

        return (CmsResource)cmsResource;
    }

    protected CmsResource editResource(String resource, CmsXmlContent content) {
        return this.editResource((String)resource, (CmsXmlContent)content, true);
    }

    protected CmsResource editResource(String resource, CmsXmlContent content, boolean publish) {
        boolean exists = this.cmsObject.existsResource(resource, CmsResourceFilter.ALL);
        boolean change = false;
        CmsFile cmsResource = null;

        try {
            CmsProject e = this.cmsObject.getRequestContext().currentProject();
            if(e.getName().equals("Online")) {
                this.cmsObject.getRequestContext().setCurrentProject(this.cmsObject.readProject("Offline"));
                change = true;
            }

            byte[] byteContent = content.marshal();
            if(exists) {
                CmsFile cmsFile = this.cmsObject.readFile(resource);
                this.cmsObject.lockResource(resource);
                cmsFile.setContents(byteContent);
                this.cmsObject.writeFile(cmsFile);
                this.cmsObject.unlockResource(resource);
                if(publish) {
                    OpenCms.getPublishManager().publishResource(this.cmsObject, resource);
                }

                cmsResource = cmsFile;
            }

            if(change) {
                this.cmsObject.getRequestContext().setCurrentProject(e);
            }
        } catch (Exception var10) {
            var10.printStackTrace();
            LOG.error(var10.toString());
        }

        return cmsResource;
    }

    protected boolean manageMultipleContent(List listaValores, String key, Locale localizacion, CmsXmlContent content) {
        boolean modified = false;
        if(listaValores != null && listaValores.size() > 0) {
            int i = 0;
            I_CmsXmlContentValue contentValue = null;
            Object contentValueInterno = null;
            if(content.hasValue(key, localizacion)) {
                contentValue = content.getValue(key, localizacion);
                int itList = contentValue.getMaxIndex();
                Iterator map2;
                HashMap map21;
                if(listaValores.size() == itList) {
                    if(listaValores.get(0) instanceof HashMap) {
                        for(map2 = listaValores.iterator(); map2.hasNext(); ++i) {
                            map21 = (HashMap)map2.next();
                            modified = this.manageNestedContent(map21, key, localizacion, content, i) || modified;
                        }
                    } else {
                        while(i < listaValores.size()) {
                            modified = this.manageSimpleContent(key, (String)listaValores.get(i), localizacion, content, i) || modified;
                            ++i;
                        }
                    }
                } else {
                    for(int var13 = itList - 1; var13 > 0; --var13) {
                        content.removeValue(key, localizacion, var13);
                    }

                    if(listaValores.get(0) instanceof HashMap) {
                        for(map2 = listaValores.iterator(); map2.hasNext(); ++i) {
                            map21 = (HashMap)map2.next();
                            this.manageNestedContent(map21, key, localizacion, content, i);
                        }
                    } else {
                        while(i < listaValores.size()) {
                            this.manageSimpleContent(key, (String)listaValores.get(i), localizacion, content, i);
                            ++i;
                        }
                    }

                    modified = true;
                }
            } else {
                if(listaValores.get(0) instanceof HashMap) {
                    for(Iterator var12 = listaValores.iterator(); var12.hasNext(); ++i) {
                        HashMap var14 = (HashMap)var12.next();
                        this.manageNestedContent(var14, key, localizacion, content, i);
                    }
                } else {
                    while(i < listaValores.size()) {
                        this.manageSimpleContent(key, (String)listaValores.get(i), localizacion, content, i);
                        ++i;
                    }
                }

                modified = true;
            }
        }

        return modified;
    }

    protected boolean manageChoiceContent(List<HashMap> listaValores, String key, Locale localizacion, CmsXmlContent content) {
        boolean modified = false;
        if(listaValores != null && listaValores.size() > 0) {
            boolean var19 = false;
            I_CmsXmlContentValue var20 = null;
            Object var21 = null;
            boolean borrarYCrear = false;
            Iterator contadorMap1;
            HashMap c;
            Iterator it;
            HashMap var23;
            if(content.hasValue(key, localizacion)) {
                var20 = content.getValue(key, localizacion);
                int xPath = var20.getMaxIndex();
                int contadorMap;
                String currentCont;
                if(xPath == listaValores.size()) {
                    contadorMap = 1;

                    for(contadorMap1 = listaValores.iterator(); contadorMap1.hasNext(); ++contadorMap) {
                        c = (HashMap)contadorMap1.next();
                        it = c.keySet().iterator();

                        while(it.hasNext()) {
                            Object key2 = it.next();
                            currentCont = key + "[" + contadorMap + "]";
                            List valor2 = content.getSubValues(currentCont, localizacion);
                            if(valor2.size() > 0 && !("" + key2).equals(((I_CmsXmlContentValue)valor2.get(0)).getName())) {
                                borrarYCrear = true;
                            }
                        }
                    }
                } else {
                    borrarYCrear = true;
                }

                Object valor21;
                Iterator var27;
                HashMap var28;
                Iterator var29;
                Integer var32;
                if(borrarYCrear) {
                    for(contadorMap = xPath - 1; contadorMap >= 0; --contadorMap) {
                        content.removeValue(key, localizacion, contadorMap);
                    }

                    var23 = new HashMap();
                    var20 = content.addValue(this.cmsObject, key, localizacion, 0);
                    String var24 = var20.getPath() + "/";
                    var27 = listaValores.iterator();

                    while(var27.hasNext()) {
                        var28 = (HashMap)var27.next();
                        var29 = var28.keySet().iterator();

                        while(var29.hasNext()) {
                            currentCont = (String)var29.next();
                            var32 = Integer.valueOf(1);
                            if(var23.containsKey(currentCont)) {
                                var32 = (Integer)var23.get(currentCont);
                                var32 = Integer.valueOf(var32.intValue() + 1);
                                var23.put(currentCont, var32);
                            } else {
                                var23.put(currentCont, Integer.valueOf(1));
                            }

                            valor21 = var28.get(currentCont);
                            if(valor21 instanceof ArrayList) {
                                this.manageMultipleContent((ArrayList)valor21, var24 + currentCont + "[" + var32 + "]", localizacion, content);
                            } else if(valor21 instanceof HashMap) {
                                this.manageNestedContent((HashMap)valor21, var24 + currentCont, localizacion, content, var32.intValue() - 1);
                            } else if(valor21 instanceof Choice) {
                                this.manageChoiceContent(((Choice)valor21).getSubfields(), var24 + currentCont + "[" + var32 + "]", localizacion, content);
                            } else if(valor21 instanceof String) {
                                this.manageSimpleContent(var24 + currentCont, (String)valor21, localizacion, content, var32.intValue() - 1);
                            }
                        }
                    }

                    modified = true;
                } else {
                    String var25 = key + "[1]" + "/";
                    HashMap var26 = new HashMap();
                    var27 = listaValores.iterator();

                    while(var27.hasNext()) {
                        var28 = (HashMap)var27.next();
                        var29 = var28.keySet().iterator();

                        while(var29.hasNext()) {
                            currentCont = (String)var29.next();
                            var32 = Integer.valueOf(1);
                            if(var26.containsKey(currentCont)) {
                                var32 = (Integer)var26.get(currentCont);
                                var32 = Integer.valueOf(var32.intValue() + 1);
                                var26.put(currentCont, var32);
                            } else {
                                var26.put(currentCont, Integer.valueOf(1));
                            }

                            valor21 = var28.get(currentCont);
                            if(valor21 instanceof ArrayList) {
                                this.manageMultipleContent((ArrayList)valor21, var25 + currentCont + "[" + var32 + "]", localizacion, content);
                            } else if(valor21 instanceof HashMap) {
                                this.manageNestedContent((HashMap)valor21, var25 + currentCont, localizacion, content, var32.intValue() - 1);
                            } else if(valor21 instanceof Choice) {
                                this.manageChoiceContent(((Choice)valor21).getSubfields(), var25 + currentCont + "[" + var32 + "]", localizacion, content);
                            } else if(valor21 instanceof String) {
                                this.manageSimpleContent(var25 + currentCont, (String)valor21, localizacion, content, var32.intValue() - 1);
                            }
                        }
                    }
                }
            } else {
                var20 = content.addValue(this.cmsObject, key, localizacion, 0);
                String var22 = var20.getPath() + "/";
                var23 = new HashMap();
                contadorMap1 = listaValores.iterator();

                while(contadorMap1.hasNext()) {
                    c = (HashMap)contadorMap1.next();
                    it = c.keySet().iterator();

                    while(it.hasNext()) {
                        String var30 = (String)it.next();
                        Integer var31 = Integer.valueOf(1);
                        if(var23.containsKey(var30)) {
                            var31 = (Integer)var23.get(var30);
                            var31 = Integer.valueOf(var31.intValue() + 1);
                            var23.put(var30, var31);
                        } else {
                            var23.put(var30, Integer.valueOf(1));
                        }

                        Object var33 = c.get(var30);
                        if(var33 instanceof ArrayList) {
                            this.manageMultipleContent((ArrayList)var33, var22 + var30 + "[" + var31 + "]", localizacion, content);
                        } else if(var33 instanceof HashMap) {
                            this.manageNestedContent((HashMap)var33, var22 + var30, localizacion, content, var31.intValue() - 1);
                        } else if(var33 instanceof Choice) {
                            this.manageChoiceContent(((Choice)var33).getSubfields(), var22 + var30 + "[" + var31 + "]", localizacion, content);
                        } else if(var33 instanceof String) {
                            this.manageSimpleContent(var22 + var30, (String)var33, localizacion, content, var31.intValue() - 1);
                        }
                    }
                }

                modified = true;
            }
        } else {
            if(content.hasValue(key, localizacion, 0)) {
                I_CmsXmlContentValue contentValue = content.getValue(key, localizacion);
                int numElementos = contentValue.getMaxIndex();

                for(int j = numElementos - 1; j >= contentValue.getMinOccurs(); --j) {
                    content.removeValue(key, localizacion, j);
                }
            }

            modified = true;
        }

        return modified;
    }

    private boolean manageNestedContent(HashMap map2, String key, Locale localizacion, CmsXmlContent content) {
        return this.manageNestedContent(map2, key, localizacion, content, 0);
    }

    private boolean manageNestedContent(HashMap map2, String key, Locale localizacion, CmsXmlContent content, int i) {
        boolean modified = false;
        Object contentValueInterno = null;
        I_CmsXmlContentValue contentValue = null;
        if(!content.hasValue(key, localizacion, i)) {
            contentValue = content.addValue(this.cmsObject, key, localizacion, i);
            modified = true;
        } else {
            contentValue = content.getValue(key, localizacion, i);
        }

        String xPath = contentValue.getPath() + "/";
        Set keys2 = map2.keySet();
        Iterator itKeys2 = keys2.iterator();

        while(itKeys2.hasNext()) {
            String key2 = (String)itKeys2.next();
            Object valor2 = map2.get(key2);
            if(valor2 instanceof ArrayList) {
                modified = this.manageMultipleContent((ArrayList)valor2, xPath + key2, localizacion, content) || modified;
            } else if(valor2 instanceof HashMap) {
                modified = this.manageNestedContent((HashMap)valor2, xPath + key2, localizacion, content) || modified;
            } else if(valor2 instanceof Choice) {
                modified = this.manageChoiceContent(((Choice)valor2).getSubfields(), xPath + key, localizacion, content) || modified;
            } else {
                modified = this.manageSimpleContent(xPath + key2, (String)valor2, localizacion, content) || modified;
            }
        }

        return modified;
    }

    private boolean manageSimpleContent(String key, String valor, Locale localizacion, CmsXmlContent content) {
        return this.manageSimpleContent(key, valor, localizacion, content, 0);
    }

    private boolean manageSimpleContent(String key, String valor, Locale localizacion, CmsXmlContent content, int i) {
        I_CmsXmlContentValue contentValue = null;
        boolean modified = false;
        if(content.hasValue(key, localizacion, i) && valor != null) {
            contentValue = content.getValue(key, localizacion, i);
            if(!valor.equals(contentValue.getStringValue(this.cmsObject))) {
                contentValue.setStringValue(this.cmsObject, valor);
                modified = true;
            }
        } else if(content.hasValue(key, localizacion, i) && valor == null) {
            content.removeValue(key, localizacion, i);
            modified = true;
        } else if(valor != null) {
            contentValue = content.addValue(this.cmsObject, key, localizacion, i);
            contentValue.setStringValue(this.cmsObject, valor);
            modified = true;
        }

        return modified;
    }

    public boolean copyToLocale(String resource, Locale fromLocale, Locale toLocales) {
        return this.copyToLocale(resource, fromLocale, (Locale)toLocales, true);
    }

    public boolean copyToLocale(String resource, Locale fromLocale, Locale toLocales, boolean publicar) {
        ArrayList l = new ArrayList();
        l.add(toLocales);
        return this.copyToLocale(resource, fromLocale, (List)l, publicar);
    }

    public boolean copyToLocale(String resource, Locale fromLocale, List<Locale> toLocales) {
        return this.copyToLocale(resource, fromLocale, (List)toLocales, true);
    }

    public boolean copyToLocale(String ruta, Locale fromLocale, List<Locale> toLocales, boolean publicar) {
        boolean b = true;

        try {
            CmsResource e = this.cmsObject.readResource(ruta);
            this.cmsObject.lockResource(ruta);
            CmsFile file = this.cmsObject.readFile(e);
            CmsXmlContent content = CmsXmlContentFactory.unmarshal(this.cmsObject, file);

            Locale to;
            for(Iterator decodedContent = toLocales.iterator(); decodedContent.hasNext(); content.copyLocale(fromLocale, to)) {
                to = (Locale)decodedContent.next();
                if(content.hasLocale(to)) {
                    content.removeLocale(to);
                }
            }

            String decodedContent1 = content.toString();
            file.setContents(decodedContent1.getBytes(content.getEncoding()));
            this.cmsObject.writeFile(file);
            this.cmsObject.unlockResource(ruta);
            if(publicar) {
                OpenCms.getPublishManager().publishResource(this.cmsObject, ruta);
            }
        } catch (UnsupportedEncodingException var11) {
            b = false;
            var11.printStackTrace();
            LOG.error("Error copiando de un idioma a otro");
        } catch (CmsException var12) {
            b = false;
            var12.printStackTrace();
            LOG.error("Error copiando de un idioma a otro");
        } catch (Exception var13) {
            b = false;
            LOG.error("Error copiando de un idioma a otro");
            var13.printStackTrace();
        }

        return b;
    }

    public class Choice {
        private String fieldName;
        private List subfields;

        public Choice(String fieldName, List subfields) {
            this.fieldName = fieldName;
            this.subfields = subfields;
        }

        public Choice(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public List getSubfields() {
            return this.subfields;
        }

        public void setSubfields(List subfields) {
            this.subfields = subfields;
        }
    }

    public class Schemas {
        private static final String SCHEMA_PREFIX = "opencms:/";

        public Schemas() {
        }

        public static String getSchemaByType(int type)
                throws Exception {
            try {
                I_CmsResourceType e = OpenCms.getResourceManager().getResourceType(type);
                String schema = e.getConfiguration().get("schema");
                return "opencms:/" + schema;
            } catch (CmsLoaderException var3) {
                throw new Exception("No existe el recurso con id " + type);
            }
        }

        public static String getSchemaByType(String typeName)
                throws Exception {
            try {
                I_CmsResourceType e = OpenCms.getResourceManager().getResourceType(typeName);
                String schema = e.getConfiguration().get("schema");
                return "opencms:/" + schema;
            } catch (CmsLoaderException var3) {
                throw new Exception("No existe el recurso con id " + typeName);
            }
        }
    }
}