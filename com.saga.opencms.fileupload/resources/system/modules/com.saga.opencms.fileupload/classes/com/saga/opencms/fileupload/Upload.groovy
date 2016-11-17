package com.saga.opencms.fileupload

import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.FileUploadException
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.logging.Log
import org.opencms.file.CmsObject
import org.opencms.file.CmsProject
import org.opencms.file.CmsProperty
import org.opencms.file.CmsPropertyDefinition
import org.opencms.file.CmsRequestContext
import org.opencms.file.CmsResource
import org.opencms.file.types.CmsResourceTypeFolder
import org.opencms.file.types.CmsResourceTypePlain
import org.opencms.file.types.I_CmsResourceType
import org.opencms.flex.CmsFlexController
import org.opencms.i18n.CmsResourceBundleLoader
import org.opencms.lock.CmsLockException
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.security.CmsAccessControlEntry
import org.opencms.security.CmsPermissionSet
import org.opencms.security.I_CmsPrincipal
import org.opencms.util.CmsCollectionsGenericWrapper
import org.opencms.util.CmsRequestUtil

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.jsp.PageContext

/**
 * Created by Jesus on 26/12/2015.
 */
class Upload {

    private static final Log LOG = CmsLog.getLog(Upload.class);

    /** Bundle keys */
    private static final String BUNDLE = "com.saga.opencms.fileupload";
    private static final String UPLOAD_FOLDER = "upload.folder";
    private static final String UPLOAD_FORMATS = "upload.formats";
    private static final String MAX_SIZE = "upload.max.size.kbytes";
    private static final String UPLOAD_NONE = "upload.none";
    private static final String UPLOAD_SUCCESS_1 = "upload.success.1";
    private static final String UPLOAD_SUCCESS_2 = "upload.success.2";
    private static final String ERROR_NO_FILES = "error.upload.none";
    private static final String ERROR_UPLOAD_HANDLE = "error.upload.handler";
    private static final String ERROR_UPLOAD_OVERSIZE = "error.upload.oversize";
    private static final String ERROR_UPLOAD_FILE = "error.upload.file";
    private static final String ERROR_PUBLISH_FILE = "error.publish.file";
    private static final String ERROR_FILE_TYPE_NOT_FOUND = "error.filetype.notfound";
    private static final String ERROR_VALIDATION = "error.validation";
    private static final String ERROR_NO_PERMISSION = "error.permission";
    private static final String ERROR_UPLOAD_GENERIC = "error.upload.generic";
    private static final String ERROR_VAL_FORMAT = "error.validation.format";

    /** Parametros y atributos del request y contexto */
    private static final String UPLOAD_FILES = "upFiles";
    private static final String UPLOAD_MSG = "upMsg";
    private static final String P_FORMACTION = "formaction";
    private static final String P_SUBMIT = "upload";
    private static final String SUBMIT_VALUE = "Upload";
    private static final String P_FILE = "file";
    private static final String ERRORS = "errors";
    private static final String INFOS = "infos";

    private List<String> errors;
    private List<String> infos;
    private ResourceBundle bundle;
    private String uploadFolder;
    private long maxSizeBytes;
    private String userName;
    private List<String> formats;
    private List<String> upFiles;
    private CmsProject project;
    private boolean projectChanged;
    List<FileItem> fileItems;

    PageContext pageContext
    HttpServletRequest request
    HttpServletResponse response
    CmsObject cmso

    public void init(def pageContext, def request, def response){
        try {

            this.pageContext = pageContext
            this.request = request
            this.response = response
            this.cmso = CmsFlexController.getCmsObject(request)

            errors = new ArrayList<>();
            infos = new ArrayList<>();


            // Cargamos e inicializamos los parametros
            load();

            // Subimos los ficheros a la carpeta temporal (WEB-INF/packages/)
            fileItems = CmsRequestUtil.readMultipartFileItems(getRequest());

            // Validamos los parametros
            if (validate()) {

                for (int i = 0; i < fileItems.size(); i++) {
                    FileItem file = fileItems.get(i);

                    // Validamos el tamano
                    if (validateSize(file.getName(), file.getSize())) {

                        // Validamos el formato
                        if (validateFormat(file.getName())) {

                            //Si el proyecto actual es online lo modificamos a offline
                            if (project.getName().equals(CmsProject.ONLINE_PROJECT_NAME)) {
                                getCmsObject().getRequestContext().setCurrentProject(getCmsObject().readProject("Offline"));
                                projectChanged = true;
                            }

                            // Subimos el fichero a opencms
                            uploadFile(file);
                        }
                    }
                }
            }

            // Cargamos msg de ficheros que se han subido
            if (upFiles.size() == 1) {
                infos.add(bundle.getString(UPLOAD_SUCCESS_1));
            } else if (upFiles.size() > 1) {
                infos.add(bundle.getString(UPLOAD_SUCCESS_2));
            }
        } catch (Exception e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_UPLOAD_FILE), e);
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        } finally {
            if(projectChanged) {
                getCmsObject().getRequestContext().setCurrentProject(project);
            }

            saveAttributes();
        }
    }

    /**
     * Save attributes into request
     */
    private void saveAttributes() {
        getRequest().setAttribute(UPLOAD_FILES, upFiles);
        getRequest().setAttribute(ERRORS, errors);
        getRequest().setAttribute(INFOS, infos);
    }

    /**
     * Loading parameters
     */
    private void load() {
        bundle = CmsResourceBundleLoader.getBundle(BUNDLE, getCmsObject().getRequestContext().getLocale());
        uploadFolder = bundle.getString(UPLOAD_FOLDER);

        String maxSizeKBytesStr = bundle.getString(MAX_SIZE);
        try {
            maxSizeBytes = Long.valueOf(maxSizeKBytesStr) * 1024;
        } catch (NumberFormatException e){
            maxSizeBytes = 0;
        }

        String formatsStr = bundle.getString(UPLOAD_FORMATS);
        formats = new ArrayList<String>();
        try {
            formats = Arrays.asList(formatsStr.split("\\|"));
        } catch (Exception e){

            // Si no se especifica configuracion todos los formatos son aceptados
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_FORMAT_NO_CONFIG));
        }

        userName = getCmsObject().getRequestContext().getCurrentUser().getName();
        project = getRequestContext().getCurrentProject();
        projectChanged = false;
        upFiles = new ArrayList<String>();
    }

    /**
     * Validate parameters
     * @return
     */
    private boolean validate() {
        boolean validateBundle = bundle != null;
        if(!validateBundle) {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_BUNDLE_NOT_FOUND,
                    BUNDLE));
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        }

        boolean validateUploadFolder = (uploadFolder != null && uploadFolder.length() > 0);
        if(!validateUploadFolder) {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_UPLOAD_FOLDER));
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        }

        boolean validateUserName = userName != null && userName.length() > 0;
        if(!validateUserName) {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_USER));
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        }

        boolean validateFiles = fileItems != null && fileItems.size() > 0;
        if(!validateFiles) {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_FILES));
            errors.add(bundle.getString(ERROR_NO_FILES));
        }

        return (validateBundle
        && validateUploadFolder
        && validateUserName
        && validateFiles);
    }

    /**
     * Validate file size
     * @return
     */
    private boolean validateSize(String filename, Long size) {
        boolean validate = false;
        if ((maxSizeBytes > 0) && (size < maxSizeBytes)){
            validate = true;
        } else {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_SIZE,
//                    new Object[]{filename, userName, maxSizeBytes / 1024}));
                    [filename, userName, maxSizeBytes / 1024]));
            errors.add(bundle.getString(
                    ERROR_UPLOAD_OVERSIZE)
                    + " " + filename
                    + " (" + (size / 1024)
                    + " > " + (maxSizeBytes / 1024) + " kb)");
        }
        return validate;
    }

    /**
     * Validate format file checking text after "."
     * @param filename
     * @return
     */
    private boolean validateFormat(String filename) {
        boolean validateFormat = false;
        if (formats.size() > 0) {
            int point = filename.lastIndexOf('.');
            if (point != -1) {
                String format = filename.substring(point + 1, filename.length());

                // Basta con que coincida con 1 de los formatos
                for (String s : formats) {
                    if (format.toLowerCase().equals(s.toLowerCase())) {
                        validateFormat = true;
                        break;
                    }
                }
            }
        } else {
            // Todos los formatos se aceptan si no hay configuracion especifica
            validateFormat = true;
        }
        if (!validateFormat){
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_FORMAT,
                    filename));
            errors.add(bundle.getString(ERROR_VAL_FORMAT));
        }

        return validateFormat;
    }

    /**
     * Upload file
     * @param file
     * @return
     */
    public void uploadFile(FileItem file) {
        try {
            String resfolderPath = getUploadFolderPath();
            String resName = getCmsObject().getRequestContext()
                    .getFileTranslator().translateResource(file.getName());
            String resPath = resfolderPath + resName;

            // determine property for file
            List<CmsProperty> titleProp = createTitleProp(file.getName());

            // determine type
            I_CmsResourceType type = createType(file.getName());

            // ensure folders (user and upload folder only)
            ensureFolders(resPath);

            // create resource (ensure name not exists)
            String newFilePath = ensureName(resPath);
            getCmsObject().createResource(
                    newFilePath, type.getTypeId(), file.get(), titleProp);

            // assign permissions to user and deny to all
            getCmsObject().chacc(newFilePath, I_CmsPrincipal.PRINCIPAL_USER, userName,
                    CmsPermissionSet.PERMISSION_READ +
                            CmsPermissionSet.PERMISSION_VIEW +
                            CmsPermissionSet.PERMISSION_DIRECT_PUBLISH, 0,
                    CmsAccessControlEntry.ACCESS_FLAGS_OVERWRITE);

            getCmsObject().chacc(newFilePath, I_CmsPrincipal.PRINCIPAL_GROUP,
                    CmsAccessControlEntry.PRINCIPAL_ALL_OTHERS_NAME,
                    0, CmsPermissionSet.PERMISSION_READ +
                    CmsPermissionSet.PERMISSION_VIEW +
                    CmsPermissionSet.PERMISSION_DIRECT_PUBLISH +
                    CmsPermissionSet.PERMISSION_CONTROL +
                    CmsPermissionSet.PERMISSION_WRITE,
                    CmsAccessControlEntry.ACCESS_FLAGS_OVERWRITE);

            // unlock and publish
            getCmsObject().unlockResource(newFilePath);
            OpenCms.getPublishManager().publishResource(
                    getCmsObject(), uploadFolder);
            upFiles.add(file.getName());
        } catch (CmsLockException e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_PERMISSION,
//                    new Object[]{userName, uploadFolder})
                    [userName, uploadFolder])
                    , e);
            errors.add(bundle.getString(ERROR_NO_PERMISSION));
        } catch (CmsException e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_UPLOAD_FILE)
                    , e);
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        } catch (Exception e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_PUBLISHING,
//                    new Object[] {uploadFolder})
                    uploadFolder)
                    , e);
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        }
    }

    /**
     * Generify upload folder path
     * @return
     */
    private String getUploadFolderPath() {
        return uploadFolder + "/" + userName + "/";
    }

    /**
     * Generate title property for file
     * @param fileName
     */
    private List<CmsProperty> createTitleProp(String fileName) {
        // determine Title property value to set on new resource
        String title = fileName;
        if (title.lastIndexOf('.') != -1) {
            title = title.substring(0, title.lastIndexOf('.'));
        }
        List<CmsProperty> properties = new ArrayList<CmsProperty>(1);
        CmsProperty titleProp = new CmsProperty();
        titleProp.setName(CmsPropertyDefinition.PROPERTY_TITLE);
        if (OpenCms.getWorkplaceManager().isDefaultPropertiesOnStructure()) {
            titleProp.setStructureValue(title);
        } else {
            titleProp.setResourceValue(title);
        }
        properties.add(titleProp);
        return properties;
    }

    /**
     * Generate type for file
     * @param fileName
     * @return
     * @throws CmsException
     */
    private I_CmsResourceType createType(String fileName) throws CmsException{
        I_CmsResourceType type = OpenCms.getResourceManager().getResourceType(
                CmsResourceTypePlain.getStaticTypeName());
        try {
            type = OpenCms.getResourceManager().getDefaultTypeForName(fileName);
        } catch (CmsException e) {
            LOG.info(bundle.getString(ERROR_FILE_TYPE_NOT_FOUND)
                    + " " + fileName
                    , e);
        }
        return type;
    }

    /**
     * Search or create user and upload folder
     * @param resPath
     * @throws CmsException
     */
    private void ensureFolders(String resPath) throws CmsException {
        int lastSlash = resPath.lastIndexOf("/");
        if (lastSlash > 0) {
            String parentFolder = resPath.substring(0, lastSlash);
            if (parentFolder.contains(uploadFolder)) {
                ensureFolders(parentFolder);

                // Buscamos o creamos la carpeta
                CmsResource folder;
                try {
                    folder = getCmsObject().readFolder(parentFolder);
                } catch (CmsException e) {
                    folder = getCmsObject().createResource(parentFolder, CmsResourceTypeFolder.getStaticTypeId());
                }
                try {
                    getCmsObject().unlockResource(folder);
                } catch (CmsException e){}
            }
        }
    }

    /**
     * Generify new file name
     * @param resPath
     * @return
     * @throws CmsException
     */
    private String ensureName(String resPath) throws CmsException {
        String newFileName;
        int pos = resPath.lastIndexOf('.');
        if (pos >= 0) {
            String suffix = resPath.substring(pos);
            String filename = resPath.substring(0, pos);
            newFileName = OpenCms.getResourceManager().getNameGenerator()
                    .getNewFileName(getCmsObject(), filename + "-%(number)" + suffix, 1);
        } else {
            String filename = resPath;
            newFileName = OpenCms.getResourceManager().getNameGenerator()
                    .getNewFileName(getCmsObject(), filename + "-%(number)", 1);
        }
        return newFileName;
    }

    private CmsObject getCmsObject() {
        return cmso
    }

    private CmsRequestContext getRequestContext() {
        return cmso.getRequestContext()
    }
}
