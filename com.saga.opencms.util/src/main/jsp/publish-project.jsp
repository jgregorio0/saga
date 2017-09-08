<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsProject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.scheduler.jobs.CmsPublishJob" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%!

    private final Log LOG = CmsLog.getLog(this.getClass());

    public static final String OFERTA_PARENT_FOLDER = "/shared/.content/Oferta/";
    public static final String PROJECT_DESCRIPTION = "Proyecto para la publicación de Ofertas";
    public static final String PROJECT_NAME = "Ofertas";
    public static final String ROOT = "/";

    CmsObject cmso;

    private void load(HttpServletRequest request) throws CmsException {
        // Obtenemos CmsObject al que asignaremos nuevo proyecto
        CmsObject cmsOrigin = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
        cmso = OpenCms.initCmsObject(cmsOrigin);
        cmso.getRequestContext().setSiteRoot(ROOT);
    }


    private CmsProject createProject() throws CmsException {
        // Creamos proyecto del grupo Users
        String name = PROJECT_NAME;
        String desc = PROJECT_DESCRIPTION;
        String group = OpenCms.getDefaultUsers().getGroupUsers();

        // Tipo de proyecto temporal == DELETE AFTER PUBLISH
        CmsProject.CmsProjectType type = CmsProject.CmsProjectType.valueOf(1);
        CmsProject project = cmso.createProject(name, desc, group, group, type);
        return project;
    }


    private void addOfertasFolderToProject() throws CmsException {
        //"enable" folder in the current users project
        cmso.copyResourceToProject(OFERTA_PARENT_FOLDER);
        touch(OFERTA_PARENT_FOLDER, false);
    }

    public void touch(
            String resourceName,
            boolean recursive) throws CmsException {
        cmso.lockResource(resourceName);
        cmso.setDateLastModified(resourceName, System.currentTimeMillis(), recursive);
        cmso.unlockResource(resourceName);
    }
%>
<%
    try {
        load(request);

        // Create and set to current project
        CmsProject project = createProject();
        cmso.getRequestContext().setCurrentProject(project);

        // Add ofertas parent folder to project
        addOfertasFolderToProject();

        // Create and execute publish job
        CmsPublishJob pubJob = new CmsPublishJob();
        Map<String, String> params = new HashMap<String, String>();
        pubJob.launch(cmso, params);
    } catch (Exception e) {
        LOG.error("Publishing project", e);
    }
%>