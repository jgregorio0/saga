package com.saga.opencms.fileupload.replication;

import com.saga.opencms.fileupload.Messages;
import org.apache.commons.logging.Log;
import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.db.CmsPublishList;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsEvent;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.module.I_CmsModuleAction;
import org.opencms.report.I_CmsReport;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by jgregorio on 07/04/2015.
 */
public class ModuleAction implements I_CmsModuleAction {

    private static final Log LOG = CmsLog.getLog(ModuleAction.class);

    @Override
    public void initialize(CmsObject adminCms,
                           CmsConfigurationManager configurationManager,
                           CmsModule module) {
        try {
            createUploadFileTable();
        } catch (Exception e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_MODULE_ACTION), e);
        }
    }

    @Override
    public void moduleUninstall(CmsModule module) {

    }

    @Override
    public void moduleUpdate(CmsModule module) {

    }

    @Override
    public void publishProject(CmsObject cms, CmsPublishList publishList, int publishTag, I_CmsReport report) {

    }

    @Override
    public void shutDown(CmsModule module) {

    }

    /**
     * Creamos la tabla de subida de ficheros
     */
    private void createUploadFileTable() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = OpenCms.getSqlManager().getConnection("default");
            stmt = conn.createStatement();
            stmt.executeUpdate(Constants.CREATE_TABLE);
        } catch (Exception e) {
            LOG.error(Messages.get().container(
                    Messages.ERROR_CREATE_TABLE), e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                LOG.error(Messages.get().container(
                        Messages.ERROR_CLOSE_STMT), e);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                LOG.error(Messages.get().container(
                        Messages.ERROR_CLOSE_CONN), e);
            }
        }
    }


    @Override
    public void cmsEvent(CmsEvent event) {

    }
}
