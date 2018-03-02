package com.saga.opencms.synchronization;

import com.saga.opencms.util.SgCms;
import com.saga.opencms.util.SgEmail;
import com.saga.opencms.util.SgPublish;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.types.I_CmsResourceType;
import org.opencms.json.JSONException;
import org.opencms.json.JSONObject;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.scheduler.I_CmsScheduledJob;

import java.util.*;

/**
 * Created by sraposo on 10/11/2015.
 */
public class SgSynchronizationJob implements I_CmsScheduledJob {

    private static final Log LOG = CmsLog.getLog(SgSynchronizationJob.class);

    /**
     * Parametros de la tarea.
     */
    public static final String PARAM_URL_WS = "urlws";
    public static final String PARAM_PUBLISH = "publish";
    public static final String PARAM_EXPIRE = "expire";
    public static final String PARAM_EXP_FOLDER = "folder";
    public static final String PARAM_EXP_TYPE = "type";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_EMAIL_FROM = "from";
    public static final String PARAM_EMAIL_TO = "to";
    public static final String PARAM_EMAIL_SUBJECT = "subject";
    public static final String PARAM_EMAIL_TEMPLATE = "template";

    Map<String, Object> notifications;

    public String launch(CmsObject cms, Map parameters) throws Exception {
        LOG.info("*** UpdateResourcesJob ***");
        LOG.info("PARAMETERS: " + parameters.toString());

        String url = (String) parameters.get(PARAM_URL_WS);
        String publishStr = (String) parameters.get(PARAM_PUBLISH);
        String expireStr = (String) parameters.get(PARAM_EXPIRE);
        String emailStr = (String) parameters.get(PARAM_EMAIL);

        notifications = new HashMap<String, Object>();
        notifications.put("errors", new LinkedHashMap<String, String>());
        notifications.put("infos", new LinkedHashMap<String, String>());

        String msg = "La tarea no se ha ejecutado correctamente: " + url;
        try {
            if (url == null) {
                throw new Exception("La tarea no se ha ejecutado correctamente: " + url);
            }

            // Obtenemos la lista de recursos a actualizar
            SgRestService restService = new SgRestService();
            List<SgResource> updatingDatas = restService.executeWSResource(url);
            if (restService.getException() != null) {
                throw restService.getException();
            }
            addNotification("infos", "for updating", String.valueOf(updatingDatas.size()));

            // Obtenemos las rutas
            Set<String> updatingPaths = getPaths(updatingDatas);
            LOG.debug("resources for updating: " + updatingPaths.size());

            // Expiramos los recursos disponibles que no estan en la lista de los que vamos a actualizar
            List<String> expiredPaths = expire(cms, expireStr, updatingPaths);
            LOG.info("expired " + expiredPaths.size() + " resources");
            addNotification("infos", "expired", String.valueOf(expiredPaths.size()));

            // Habilitamos los recursos expirados que estan en la lista de los que vamos a actualizar
            List<String> enabledPaths = enable(cms, expireStr, updatingPaths);
            LOG.info("enabled " + enabledPaths.size() + " resources");
            addNotification("infos", "enabled", String.valueOf(enabledPaths.size()));

            // Actualizamos los recursos en OpenCms
            SgResourceService resourceService = new SgResourceService(cms, CmsResourceFilter.ALL);
            List<CmsResource> updatedResources = resourceService.updateResources(updatingDatas, false);
            notifyModifiedResources(updatedResources);
            LOG.info("updated " + updatedResources.size() + " resources");
            addNotification("infos", "updated", String.valueOf(updatedResources.size()));

            // Publish modified
            Set<String> modifiedPaths = new HashSet<String>();
            modifiedPaths.addAll(getCmsResourcePaths(updatedResources));
            modifiedPaths.addAll(expiredPaths);
            modifiedPaths.addAll(enabledPaths);
            publish(cms, publishStr, modifiedPaths);

            msg = "Ejecutada tarea de actualizacion de contenido desde la url: " + url + ". Se han actualizado " + updatedResources.size() + " recursos, expirado " + expiredPaths.size() + " recursos y habilitado " + enabledPaths.size() + " recursos";
        } catch (Exception e) {
            LOG.error("UpdateResourcesJob", e);
            addNotification("errors", "UpdateResourcesJob", CmsException.getStackTraceAsString(e));
        } finally {
            // Notifica los cambios
            notifyByEmail(cms, emailStr);
        }

        return msg;
    }

    /**
     * Add notifications message
     *
     * @param updatedResources
     */
    private void notifyModifiedResources(List<CmsResource> updatedResources) {
        for (int i = 0; updatedResources != null && i < updatedResources.size(); i++) {
            CmsResource resource = updatedResources.get(i);
            addNotification("infos", resource.getRootPath(), " >> MODIFIED");
        }
    }

    /**
     * Return root path list containing each CmsResource
     *
     * @param resources
     * @return
     */
    private List<String> getCmsResourcePaths(List<CmsResource> resources) {
        List<String> paths = new ArrayList<String>();
        for (CmsResource resource : resources) {
            paths.add(resource.getRootPath());
        }
        return paths;
    }

    private void addNotification(String type, String key, String value) {
        ((Map<String, String>) notifications.get(type)).put(key, value);
    }

    /**
     * Notify by email
     *
     * @param cms
     * @param emailStr
     * @throws org.opencms.json.JSONException
     */
    private void notifyByEmail(CmsObject cms, String emailStr) throws JSONException {
        if (StringUtils.isNotBlank(emailStr)) {
            try {
                JSONObject jEmail = new JSONObject(emailStr);
                String from = jEmail.getString(PARAM_EMAIL_FROM);
                String to = jEmail.getString(PARAM_EMAIL_TO);
                String subject = jEmail.getString(PARAM_EMAIL_SUBJECT);
                String template = jEmail.getString(PARAM_EMAIL_TEMPLATE);

                notifications.put("title", subject);
                SgEmail email = new SgEmail(false, "UTF-8", cms.getRequestContext().getSiteRoot());
                email.sendPebbleTemplateMail(from, Arrays.asList(to.split("\\|")), subject, template, notifications);
            } catch (Exception e) {
                LOG.error("notifying by email", e);
            }
        }
    }

    /**
     * Get paths from each Resource for updating
     *
     * @param resourcesForUpdating
     * @return
     */
    private Set<String> getPaths(List<SgResource> resourcesForUpdating) {
        HashSet<String> updatingPaths = new HashSet<String>();
        for (SgResource resource : resourcesForUpdating) {
            updatingPaths.add(resource.getPath());
        }
        return updatingPaths;
    }

    /**
     * Publish all modified resources
     *
     * @param cms
     * @param publishStr
     * @param modified
     */
    private void publish(CmsObject cms, String publishStr, Set<String> modified) {
        if (Boolean.valueOf(publishStr)) {
            LOG.info("publishing " + modified.size() + " resources");
            try {
                new SgPublish(cms).publishPaths(modified);
            } catch (CmsException e) {
                LOG.error("ERROR publishing resources", e);
                addNotification("errors", "publish", CmsException.getStackTraceAsString(e));
            }
        }
    }

    /**
     * Expire resources available that web service didn't send for updating
     *
     * @param cms
     * @param expireStr
     * @param updatingPaths
     * @return
     */
    private List<String> expire(CmsObject cms, String expireStr, Set<String> updatingPaths) {
        List<String> expiredPaths = new ArrayList<String>();

        // If expiration is configured
        if (StringUtils.isNotBlank(expireStr)) {
            try {
                // Get folder and type for filter
                JSONObject jExpire = new JSONObject(expireStr);
                String folder = jExpire.getString(PARAM_EXP_FOLDER);
                String type = jExpire.getString(PARAM_EXP_TYPE);

                // Create filter DEFAULT and search for folder and type
                I_CmsResourceType resourceType = OpenCms.getResourceManager().getResourceType(type);
                List<CmsResource> availables =
                        cms.readResources(folder,
                                CmsResourceFilter.DEFAULT.addRequireType(resourceType));

                // For each available resource check it is contained in updating resources
                SgCms sgCms = new SgCms(cms);
                for (CmsResource available : availables) {
                    try {
                        String sitePath = cms.getSitePath(available);

                        // If resources is available and it is not contained into updating paths it must be expired
                        if (!updatingPaths.contains(sitePath)) {
                            sgCms.setDateExpire(sitePath, new Date(System.currentTimeMillis()));
                            expiredPaths.add(available.getRootPath());
                            addNotification("infos", available.getRootPath(), " >> EXPIRED");
                        }
                    } catch (Exception e) {
                        LOG.error("expiring resource " + available.getRootPath(), e);
                        addNotification("errors", available.getRootPath(), CmsException.getStackTraceAsString(e));
                    }
                }
            } catch (Exception e) {
                LOG.error("expiring resources", e);
                addNotification("errors", "expire", CmsException.getStackTraceAsString(e));
            }

        }
        return expiredPaths;
    }

    /**
     * Validate resource types are identical
     *
     * @param resourceType
     * @param available
     * @return
     */
    private boolean validateResourceType(I_CmsResourceType resourceType, CmsResource available) {
        return OpenCms.getResourceManager().getResourceType(available).isIdentical(resourceType);
    }

    /**
     * Enable resources updated in case they were expired
     *
     * @param cms
     * @param updatingPaths
     * @return
     */
    private List<String> enable(CmsObject cms, String expireStr, Set<String> updatingPaths) {
        List<String> enabledPaths = new ArrayList<String>();

        // If expiration is configured
        if (StringUtils.isNotBlank(expireStr)) {
            try {
                // Get folder and type for filter
                JSONObject jExpire = new JSONObject(expireStr);
                String folder = jExpire.getString(PARAM_EXP_FOLDER);
                String type = jExpire.getString(PARAM_EXP_TYPE);

                // Create filter IGNORE_EXPIRATION, require expire after 0 and require type resource. Search for folder.
                I_CmsResourceType resourceType = OpenCms.getResourceManager().getResourceType(type);
                List<CmsResource> expires =
                        cms.readResources(folder,
                                CmsResourceFilter.IGNORE_EXPIRATION
                                        .addRequireExpireBefore(System.currentTimeMillis())
                                        .addRequireType(resourceType));

                // For each expired resource check if it is contained in updating resources
                SgCms sgCms = new SgCms(cms);
                for (CmsResource expired : expires) {
                    try {
                        String sitePath = cms.getSitePath(expired);

                        // If resources is expired and it is contained into updating paths it must be enabled
                        if (updatingPaths.contains(sitePath)) {
                            sgCms.setDateExpire(sitePath, new Date(CmsResource.DATE_EXPIRED_DEFAULT));
                            enabledPaths.add(expired.getRootPath());
                            addNotification("infos", expired.getRootPath(), " >> ENABLED");
                        }
                    } catch (Exception e) {
                        LOG.error("enabling resource " + expired.getRootPath(), e);
                        addNotification("errors", expired.getRootPath(), CmsException.getStackTraceAsString(e));
                    }
                }
            } catch (Exception e) {
                LOG.error("enabling resources", e);
                addNotification("errors", "enable", CmsException.getStackTraceAsString(e));
            }
        }

        return enabledPaths;
    }
}