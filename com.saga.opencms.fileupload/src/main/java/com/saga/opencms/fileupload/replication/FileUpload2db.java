package com.saga.opencms.fileupload.replication;

import com.saga.opencms.fileupload.Messages;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.opencms.i18n.CmsResourceBundleLoader;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsRequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.sql.*;
import java.util.*;

/**
 * Created by jgregorio on 06/07/2015.
 */
public class FileUpload2db extends CmsJspActionElement {

    private static final Log LOG = CmsLog.getLog(FileUpload2db.class);

    /** Mensajes */
    private static final String BUNDLE = "com.saga.opencms.fileupload";
    private static final String MAX_SIZE = "upload.max.size.kbytes";
    private static final String UPLOAD_FORMATS = "upload.formats";
    private static final String UPLOAD_SUCCESS_1 = "upload.success.1";
    private static final String UPLOAD_SUCCESS_2 = "upload.success.2";
    private static final String ERROR_NO_FILES = "error.upload.none";
    private static final String ERROR_UPLOAD_GENERIC = "error.upload.generic";
    private static final String ERROR_UPLOAD_OVERSIZE = "error.upload.oversize";
    private static final String ERROR_VAL_FORMAT = "error.validation.format";

    /** parametros de la base de datos */
    private static final String POOL = "default";


    /** Parametros y atributos que recupera del request y el contexto */
    private static final String UPLOAD_FILES = "upFiles";
    private static final String P_FORMACTION = "formaction";
    private static final String P_SUBMIT = "upload";
    private static final String P_FILE = "file";
    private static final String ERRORS = "errors";
    private static final String INFOS = "infos";

    private List<String> errors;
    private List<String> infos;
    private ResourceBundle bundle;
    private String site;
    private long maxSizeBytes;
    private String userName;
    private List<String> upFiles;
    private List<String> formats;
    FileItem submit;
    FileItem file;
    FileItem formaction;

    public FileUpload2db(PageContext context, HttpServletRequest req, HttpServletResponse res) {
        super(context, req, res);
        errors = new ArrayList<String>();
        infos = new ArrayList<String>();
    }

    public void handleRequest() {
        try {

            // Cargamos e inicializamos los parametros
            load();

            // Validamos si se ha hecho submit
            if (formaction != null && submit != null) {

                // Validamos los parametros
                if (validate()) {

                    // Validamos el tamano
                    if (validateSize(file.getSize())) {

                        // Validamos el formato
                        if (validateFormat(file.getName())) {

                            // Guardamos en la bbdd
                            saveFile(site, userName, file);
                        }
                    }
                }

                // Cargamos msg de ficheros que se han subido
                if (upFiles.size() == 1) {
                    infos.add(bundle.getString(UPLOAD_SUCCESS_1));
                } else if (upFiles.size() > 1) {
                    infos.add(bundle.getString(UPLOAD_SUCCESS_2));
                }
            }
        } catch (Exception e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_UPLOAD_FILE), e);
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        } finally {
            saveAttributes();
        }
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
            // Informamos en load()
//            LOG.info(Messages.get().container(
//                    Messages.INFO_VALIDATION_FORMAT_NO_CONFIG);
            validateFormat = true;
        }
        if (!validateFormat){
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_FORMAT,
                    file.getName()));
            errors.add(bundle.getString(ERROR_VAL_FORMAT));
        }

        return validateFormat;
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
     * Validate file size
     * @return
     */
    private boolean validateSize(long size) {
//        long maxSystemFileSize = OpenCms.getWorkplaceManager().getFileBytesMaxUploadSize(getCmsObject());
        boolean validate = false;
        if ((maxSizeBytes > 0) && (size < maxSizeBytes)){
                validate = true;
        } else {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_SIZE,
                    new Object[]{file.getName(), userName, maxSizeBytes / 1024}));
            errors.add(bundle.getString(
                    ERROR_UPLOAD_OVERSIZE)
                    + " " + file.getName()
                    + " (" + (file.getSize() / 1024)
                    + " > " + (maxSizeBytes / 1024) + " kb)");
        }
        return validate;
    }

    /**
     * Validamos las variables
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
        boolean validateUserName = userName != null && userName.length() > 0;
        if(!validateUserName) {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_USER));
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        }
        boolean validateFile = file != null && file.getName() != null && file.getName().length() > 0;
        if(!validateFile) {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_FILES));
            errors.add(bundle.getString(ERROR_NO_FILES));
        }
        boolean validateSite = site != null && site.length() > 0;
        if(!validateSite) {
            LOG.info(Messages.get().container(
                    Messages.INFO_VALIDATION_SITE));
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        }

        return validateBundle
                && validateUserName
                && validateFile;
    }

    /**
     * Cargamos las variables
     */
    private void load() {
        bundle = CmsResourceBundleLoader.getBundle(BUNDLE, getCmsObject().getRequestContext().getLocale());

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
        site = getCmsObject().getRequestContext().getSiteRoot();
        upFiles = new ArrayList<String>();
        loadMultipartParameters();
    }

    /**
     * Load multipart form parameters
     */
    private void loadMultipartParameters() {
        Map<String, FileItem> itemsMap = new HashMap<String, FileItem>();
        List<FileItem> fileItems = CmsRequestUtil.readMultipartFileItems(getRequest());
        if (fileItems != null) {
            Iterator<FileItem> iterator = fileItems.iterator();

            while (iterator.hasNext()){
                FileItem fileItem = iterator.next();
                itemsMap.put(fileItem.getFieldName(), fileItem);
            }

            formaction = itemsMap.get(P_FORMACTION);
            submit = itemsMap.get(P_SUBMIT);
            file = itemsMap.get(P_FILE);
        }
    }

    /**
     * Generate task id
     * @param conn
     * @return
     */
    private int generateId(Connection conn) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = 0;

        try {
            pstmt = conn.prepareStatement(Constants.READ_MAX_ID);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                id = rs.getInt("MAXID");
            }
            id++;
        } catch (SQLException e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_GENERATE_ID), e);
        } finally {
            closeAll(null, pstmt, rs);
        }
        return id;
    }

    /**
     * Save file into database
     *
     * @param site
     * @param fi
     * @return
     */
    public void saveFile(String site, String userName, FileItem fi) {
        try {
            Connection conn = OpenCms.getSqlManager().getConnection(POOL);
            int id = generateId(conn);

            String filename = fi.getName();
            byte[] content = fi.get();

            int res = save(conn, id, site, userName, filename, content);
            if (res == 1) {
                upFiles.add(fi.getName());
            }
        } catch (SQLException e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_SAVE_FILE_CONNECTION,
                            new Object[]{fi.getName(), POOL}),
                    e);
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        }
    }

    /**
     * Guardamos en la base de datos
     * @param conn
     * @param id
     * @param site
     * @param userName
     * @param filename
     * @param content    @return
     */
    private int save(Connection conn, int id,
                     String site, String userName,
                     String filename, byte[] content) {
        PreparedStatement pstmt = null;
        int res = -1;

        try {
            pstmt = conn.prepareStatement(Constants.INSERT_FILE);
            pstmt.setInt(1, id);
            pstmt.setString(2, site);
            pstmt.setString(3, userName);
            pstmt.setString(4, filename);
            pstmt.setBytes(5, content);

            res = pstmt.executeUpdate();
        } catch (SQLException e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_SAVE_FILE,
                    new Object[]{filename, id}),
                    e);
            errors.add(bundle.getString(ERROR_UPLOAD_GENERIC));
        } finally {
            closeAll(null, pstmt, null);
        }
        return res;
    }

    /**
     * Ensure file name doesn't exist
     * @param resPath
     * @return
     * @throws org.opencms.main.CmsException
     */
    private String ensureName(String resPath) throws CmsException {
        String newFileName;
        int pos = resPath.lastIndexOf('.');
        if (pos >= 0) {
            String suffix = resPath.substring(pos);
            String filename = resPath.substring(0, pos);
            newFileName = OpenCms.getResourceManager().getNameGenerator().getNewFileName(getCmsObject(), filename + "-%(number)" + suffix, 1);
        } else {
            String filename = resPath;
            newFileName = OpenCms.getResourceManager().getNameGenerator().getNewFileName(getCmsObject(), filename + "-%(number)", 1);
        }
        return newFileName;
    }

    /**
     * This method closes the result sets and statement and connections.<p>
     *
     * @param con The connection.
     * @param statement The statement.
     * @param res The result set.
     */
    private void closeAll(Connection con, Statement statement, ResultSet res) {

        // result set
        if (res != null) {
            try {
                res.close();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
        // statement
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
        // connection
        if (con != null) {
            try {
                if (!con.isClosed()) {
                    con.close();
                }
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }
}
