package com.saga.opencms.fileupload.replication;

import com.saga.opencms.fileupload.model.FileUpload;
import com.saga.opencms.fileupload.util.EmailUtil;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.types.CmsResourceTypeFolder;
import org.opencms.file.types.CmsResourceTypePlain;
import org.opencms.file.types.I_CmsResourceType;
import org.opencms.lock.CmsLockException;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.scheduler.I_CmsScheduledJob;
import org.opencms.security.CmsAccessControlEntry;
import org.opencms.security.CmsPermissionSet;
import org.opencms.security.I_CmsPrincipal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jgregorio on 05/11/2015.
 *
 * This Scheduled Job synchronize uploaded files
 * from the upload-P_FOLDER giving by parameter
 */
public class SyncFileUploadJob implements I_CmsScheduledJob {

    /** LOG **/
    private static final Log LOG = CmsLog.getLog(SyncFileUploadJob.class);

    private static final String P_FOLDER = "upload-folder";
    private static final String P_POOL_TO = "pool-to";
    private static final String P_POOL_FROM = "pool-from";
    private static final String P_EMAIL_A = "email-a";
    private static final String P_EMAIL_FROM = "email-from";
    private static final String P_EMAIL_TO = "email-to";

    /** Parametros */
    private EmailUtil email;
    private String poolTo;
    private String poolFrom;
    private String uploadFolder;
    private List<FileUpload> filesMod;
    private List<FileUpload> filesErr;
    private List<FileUpload> filesDel;
    private CmsObject cmso;

    @Override
    public String launch(CmsObject cms, Map<String, String> parameters) throws Exception {
        LOG.debug("\n\n **** SINCRONIZACION DE SUBIDA DE FICHEROS **** \n");

        String res = "No se ha sincronizado nada";

        load(cms, parameters);
        if (validate()) {

            LOG.debug("Fase 1: Obtenemos las conexiones a base de datos");
            Connection conTo = null;
            Connection conFrom = null;
            try {
                conFrom = OpenCms.getSqlManager().getConnection(poolFrom);
                conTo = OpenCms.getSqlManager().getConnection(poolTo);

                // LEEMOS LOS FICHEROS DEL ESCLAVO
                LOG.debug("Fase 2: LEEMOS LOS FICHEROS DEL ESCLAVO");
                List<FileUpload> filesSlave = readFiles(conFrom);

                // CREAMOS LOS FICHEROS EN EL MASTER
                LOG.debug("Fase 3: CREAMOS LOS FICHEROS EN EL MASTER");
                if (filesSlave != null && filesSlave.size() > 0) {
                    createFiles(filesSlave);
                }

                // ELIMINAMOS LOS FICHEROS EN EL ESCLAVO
                LOG.debug("Fase 4: ELIMINAMOS LOS FICHEROS EN EL ESCLAVO");
                deleteFilesSlave(conFrom, filesMod);

                // LEEMOS LOS FICHEROS DEL MAESTRO
                LOG.debug("Fase 5: LEEMOS LOS FICHEROS DEL ESCLAVO");
                List<FileUpload> filesMaster = readFiles(conFrom);

                // CREAMOS LOS FICHEROS EN EL MASTER
                LOG.debug("Fase 6: CREAMOS LOS FICHEROS EN EL MASTER");
                if (filesMaster != null && filesMaster.size() > 0) {
                    createFiles(filesMaster);
                }

                // ELIMINAMOS LOS FICHEROS EN EL MAESTRO
                LOG.debug("Fase 7: ELIMINAMOS LOS FICHEROS EN EL ESCLAVO");
                deleteFilesSlave(conFrom, filesMod);

                // IMPRIMIMOS LOS RESULTADOS
                LOG.debug("Fase 8: IMPRIMIMOS LOS RESULTADOS");
                res = "Tarea finalizada con los siguientes resultados:" +
                        "\nErrores: " + filesErr.size() +
                        "\nModificaciones: " + filesMod.size() +
                        "\nBorrados: " + filesDel.size();

                email.infoErrors("Ficheros subidos:", filesErr);
                email.infoModifies("Ficheros subidos:", filesMod);
                email.infoDeletes("Ficheros eliminados:", filesDel);
            } catch (SQLException e) {
                LOG.error("ERROR replicar inscripciones", e);
                email.infoError("ERROR replicar inscripciones", e);
            } finally {
                close(null, null, conTo);
                close(null, null, conFrom);
            }
        } else {
            res = "Parametros erroneos";
        }
        email.sendMail();
        return res;
    }

    /**
     * Delete uploaded files register from slave database
     * @param conn
     * @param filesMod
     */
    private void deleteFilesSlave(Connection conn, List<FileUpload> filesMod) {
        for (FileUpload file : filesMod) {
            deleteFile(conn, file);
        }
    }

    /**
     * Eliminamos fichero que hemos creado correctamente
     * @param conn
     * @param file
     * @return
     */
    private int deleteFile(Connection conn, FileUpload file) {
        PreparedStatement pstmt = null;
        int res = -1;

        try {
            pstmt = conn.prepareStatement(Constants.DELETE_FILE);
            pstmt.setInt(1, file.getId());
            res = pstmt.executeUpdate();
        } catch (SQLException e) {
            LOG.error("ERROR borrando fichero " + file.getId());
            email.infoError("ERROR borrando fichero " + file.getId(), e);
        } finally {
            close(null, pstmt, null);
        }
        return res;
    }

    /**
     * Validamos los parametros
     * @return
     */
    private boolean validate() {
        return uploadFolder != null
                && poolFrom != null
                && poolTo != null;
    }

    /**
     * Cargamos los parametros
     * @param cms
     * @param parameters
     */
    private void load(CmsObject cms, Map<String, String> parameters) {
        cmso = cms;
        filesMod = new ArrayList<FileUpload>();
        filesErr = new ArrayList<FileUpload>();
        filesDel = new ArrayList<FileUpload>();
        uploadFolder = cleanFolderPath(parameters.get(P_FOLDER));
        poolFrom = parameters.get(P_POOL_FROM);
        poolTo = parameters.get(P_POOL_TO);

        String asunto = parameters.get(P_EMAIL_A);
        String from = parameters.get(P_EMAIL_FROM);
        String to = parameters.get(P_EMAIL_TO);
        email = new EmailUtil(asunto, from, to,
                "ERRORES", "MODIFICADOS", "ELIMINADOS");
    }

    /**
     * Cerramos las conexiones que se hayan abierto
     * @param rs
     * @param stmt
     * @param conn
     */
    void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                LOG.error("ERROR cerrando resultados", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                LOG.error("ERROR cerrando estado", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                LOG.error("ERROR cerrando conexion", e);
            }
        }
    }

    /**
     * Obtiene ficheros a subir del pool esclavo
     * @param conFrom
     * @return
     */
    private List<FileUpload> readFiles(Connection conFrom) {
        List<FileUpload> files = new ArrayList<FileUpload>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conFrom.prepareStatement(Constants.SELECT_FILES);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                FileUpload file = new FileUpload();
                file.setId(rs.getInt(1));
                file.setSite(rs.getString(2));
                file.setUserName(rs.getString(3));
                file.setFileName(rs.getString(4));
                file.setContent(rs.getBytes(5));
                files.add(file);
            }
        } catch (SQLException e) {
            LOG.error("ERROR leyendo ficheros", e);
            email.infoError("ERROR leyendo ficheros", e);
        } finally {
            close(rs, pstmt, null);
        }
        return files;
    }

    /**
     * Synchronize Files
     * @param files
     */
    private void createFiles(List<FileUpload> files) {
        for (FileUpload file : files) {
            createCmsFile(file);
        }
    }

    /**
     * Creamos el fichero
     * @param file
     * @return
     */
    private void createCmsFile(FileUpload file) {
        LOG.debug("Creamos el fichero procedente del nodo >>" + poolFrom + " al nodo >>" + poolTo + " con id: " + file.getId());

        try {
            String resName = cmso.getRequestContext().getFileTranslator().translateResource(file.getFileName());
            String resfolderPath = cleanFolderPath(file.getSite()) +
                    cleanFolderPath(uploadFolder) +
                    cleanFolderPath(file.getUserName());
            String resPath = cleanFolderPath(resfolderPath)+ "/" + resName;

            // determine property for file
            List<CmsProperty> titleProp = createTitleProp(file.getFileName());

            // determine type
            I_CmsResourceType type = createType(file.getFileName());

            // ensure folders (user and upload folder only)
            ensureFolders(resPath);

            // create resource (ensure name not exists)
            String newFilePath = ensureName(resPath);
            cmso.createResource(newFilePath, type.getTypeId(), file.getContent(), titleProp);

            // assign permissions to user and deny to all
            cmso.chacc(newFilePath, I_CmsPrincipal.PRINCIPAL_USER, file.getUserName(),
                    CmsPermissionSet.PERMISSION_READ +
                            CmsPermissionSet.PERMISSION_VIEW +
                            CmsPermissionSet.PERMISSION_DIRECT_PUBLISH, 0,
                    CmsAccessControlEntry.ACCESS_FLAGS_OVERWRITE);

            cmso.chacc(newFilePath, I_CmsPrincipal.PRINCIPAL_GROUP,
                    CmsAccessControlEntry.PRINCIPAL_ALL_OTHERS_NAME,
                    0, CmsPermissionSet.PERMISSION_READ +
                            CmsPermissionSet.PERMISSION_VIEW +
                            CmsPermissionSet.PERMISSION_DIRECT_PUBLISH +
                            CmsPermissionSet.PERMISSION_CONTROL +
                            CmsPermissionSet.PERMISSION_WRITE,
                    CmsAccessControlEntry.ACCESS_FLAGS_OVERWRITE);

            // unlock and publish
            cmso.unlockResource(newFilePath);
            OpenCms.getPublishManager().publishResource(cmso,
                    cleanFolderPath(file.getSite()) +
                    cleanFolderPath(uploadFolder));

            filesMod.add(file);
        } catch (CmsLockException e) {
            LOG.error("ERROR No tiene permisos para subir el fichero"
                    + " " + file.getFileName()
                    + " en la carpeta " + uploadFolder
                    + " para el usuario " + file.getUserName()
                    , e);
            email.infoError("ERROR No tiene permisos para subir el fichero", e);
            filesErr.add(file);
        } catch (CmsException e) {
            LOG.error("ERROR creando el recurso"
                    + " " + file.getFileName()
                    + " en la carpeta " + uploadFolder
                    + " para el usuario " + file.getUserName()
                    , e);
            email.infoError("ERROR subiendo el fichero", e);
            filesErr.add(file);
        } catch (Exception e) {
            LOG.error("ERROR publicando el fichero"
                    + " " + file.getFileName()
                    + " en la carpeta " + uploadFolder
                    + " para el usuario " + file.getUserName()
        ,e);
        email.infoError("ERROR publicando el fichero", e);
            filesErr.add(file);
        }
    }

    /**
     * Ensure file name doesn't exist
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
            newFileName = OpenCms.getResourceManager()
                    .getNameGenerator().getNewFileName(cmso,
                            filename + "-%(number)" + suffix, 1);
        } else {
            String filename = resPath;
            newFileName = OpenCms.getResourceManager()
                    .getNameGenerator().getNewFileName(cmso,
                            filename + "-%(number)", 1);
        }
        return newFileName;
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
                    folder = cmso.readFolder(parentFolder);
                } catch (CmsException e) {
                    folder = cmso.createResource(parentFolder, CmsResourceTypeFolder.getStaticTypeId());
                }
                try {
                    cmso.unlockResource(folder);
                } catch (CmsException e){}
            }
        }

//        String userFolder = resPath.substring(0, resPath.lastIndexOf("/"));
//        ensureUploadFolder(userFolder);
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
            LOG.info("INFO no existe type para el fichero " + fileName, e);
        }
        return type;
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
     * Return path that starts with slash and ends without slash
     * @param path
     * @return
     */
    private String cleanFolderPath(String path) {
        String cleanPath = "/";
        if (path != null && path.length() > 0) {
            cleanPath = path;
            if (!path.startsWith("/")) {
                cleanPath = "/" + cleanPath;
            }
            if (cleanPath.endsWith("/")) {
                cleanPath = cleanPath.substring(0, cleanPath.length() - 1);
            }
        }
        return cleanPath;
    }
}