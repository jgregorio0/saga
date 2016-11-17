/**
 * Messages for fileupload project
 */

package com.saga.opencms.fileupload;

import org.opencms.i18n.A_CmsMessageBundle;
import org.opencms.i18n.I_CmsMessageBundle;

/**
 * Convenience class to access the localized messages of this OpenCms package.<p>
 *
 * @since 6.0.0
 */
public final class Messages extends A_CmsMessageBundle {

    /** Message constant for key in the resource bundle. */
    public static final String INFO_RESOURCE_TYPE_NOT_FOUND = "INFO_DEFAULT_TYPE_NOT_FOUND";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_PLAIN_TYPE_NOT_FOUND = "INFO_PLAIN_TYPE_NOT_FOUND";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_NO_FORMAT_CONFIG = "INFO_NO_FORMAT_CONFIG";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_UPLOAD_FILE_SIZE_TOO_HIGH_1 = "ERR_UPLOAD_FILE_SIZE_TOO_HIGH_1";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_TEMP_FILE = "ERR_TEMP_FILE";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_UPLOAD_FILE = "ERR_UPLOAD_FILE";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_UPLOAD_FILE_NOT_FOUND_0 = "ERR_UPLOAD_FILE_NOT_FOUND_0";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_HANDLE_FILE_UPLOAD = "ERR_HANDLE_FILE_UPLOAD";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_CONECTION_POOL = "ERR_CONECTION_POOL";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_DB_CONECTION = "ERR_DB_CONECTION";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_GENERATE_ID = "ERR_GENERATE_ID";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_OVERSIZE = "ERR_OVERSIZE";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_GENERATE_ID = "ERROR_GENERATE_ID";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_SAVE_FILE = "ERROR_SAVE_FILE";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_SAVE_FILE_CONNECTION = "ERROR_SAVE_FILE_CONNECTION";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_UPLOAD_FILE = "ERROR_UPLOAD_FILE";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_PERMISSION = "ERROR_PERMISSION";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_PUBLISHING = "ERROR_PUBLISHING";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_MODULE_ACTION = "ERROR_MODULE_ACTION";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_CREATE_TABLE = "ERROR_CREATE_TABLE";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_CLOSE_STMT = "ERROR_CLOSE_STMT";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_CLOSE_CONN = "ERROR_CLOSE_CONN";

    /** Message constant for key in the resource bundle. */
    public static final String ERROR_JSON_RESPONSE = "ERROR_JSON_RESPONSE";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_VALIDATION_FORMAT_NO_CONFIG = "INFO_VALIDATION_FORMAT_NO_CONFIG";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_VALIDATION_BUNDLE_NOT_FOUND = "INFO_VALIDATION_BUNDLE_NOT_FOUND";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_VALIDATION_USER = "INFO_VALIDATION_USER";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_VALIDATION_FILES = "INFO_VALIDATION_FILES";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_VALIDATION_SITE = "INFO_VALIDATION_SITE";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_VALIDATION_SIZE = "INFO_VALIDATION_SIZE";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_VALIDATION_FORMAT = "INFO_VALIDATION_FORMAT";

    /** Message constant for key in the resource bundle. */
    public static final String INFO_VALIDATION_UPLOAD_FOLDER = "INFO_VALIDATION_UPLOAD_FOLDER";




    /** Static instance member. */
    private static final I_CmsMessageBundle INSTANCE = new Messages();

    /** Name of the resource bundle. */
    private static final String BUNDLE_NAME = "com.saga.opencms.fileupload.messages";

    /**
     * Hides the public constructor for this utility class.<p>
     */
    private Messages() {

        // hide the constructor
    }

    /**
     * Returns an instance of this localized message accessor.<p>
     *
     * @return an instance of this localized message accessor
     */
    public static I_CmsMessageBundle get() {

        return INSTANCE;
    }

    /**
     * Returns the bundle name for this OpenCms package.<p>
     *
     * @return the bundle name for this OpenCms package
     */
    public String getBundleName() {

        return BUNDLE_NAME;
    }
}