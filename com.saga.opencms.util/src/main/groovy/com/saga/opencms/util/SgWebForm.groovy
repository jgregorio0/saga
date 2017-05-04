package com.saga.opencms.util

import com.alkacon.opencms.v8.formgenerator.*
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataAccess
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.opencms.db.CmsDbPool
import org.opencms.i18n.CmsEncoder
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.util.CmsStringUtil
import org.opencms.util.CmsUUID

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.jsp.PageContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

public class SgWebForm {

	private final Log LOG = CmsLog.getLog(SgWebForm.class);

    /** Static Params */
    public static final String F_DATE_CREATED = "datecreated";

    /** JDBC MySQL Queries */
    private final String READ_NEXT_ENTRY_ID =
            "SELECT MAX(CMS_WEBFORM_ENTRIES.ENTRY_ID) MAXID" +
                    " FROM CMS_WEBFORM_ENTRIES";

    private final String WRITE_FORM_ENTRY=
            "INSERT INTO CMS_WEBFORM_ENTRIES" +
                    " (ENTRY_ID, FORM_ID, DATE_CREATED, RESOURCE_ID, STATE)" +
                    " VALUES (?, ?, ?, ?, ?)";

    private final String WRITE_FORM_DATA=
            "INSERT INTO CMS_WEBFORM_DATA (REF_ID, FIELDNAME, FIELDVALUE)" +
                    " VALUES (?, ?, ?)";

	private String formUri;
	private String formId;
	private CmsFormHandler formHandler;
	private CmsForm formConfiguration;
    private CmsFormDataAccess dataAccess;

    /**
     * Constructor only for data access
     */
	public SgWebForm(){
        this.dataAccess = CmsFormDataAccess.getInstance();
    }

    /**
     * Constructor for webform resource and data access
     * @param context
     * @param req
     * @param res
     * @param formUri
     */
	public SgWebForm(PageContext context, HttpServletRequest req, HttpServletResponse res, String formUri){
        // DB Access
        this();

		// Formulario
        this.formUri = formUri;
		this.formHandler = null;
        this.formConfiguration = null;
		try {
            this.formHandler = CmsFormHandlerFactory.create(context, req, res, formUri);
            this.formConfiguration = formHandler.getFormConfiguration();
            this.formId = formHandler.getFormConfiguration().getFormId();
		} catch (Exception e) {
			LOG.error("Cargando formulario " + formUri, e);
		}
	}

	public String getFormUri() {
		return formUri
	}

	public void setFormUri(String formUri) {
		this.formUri = formUri
	}

	public String getFormId() {
		return formId
	}

	public void setFormId(String formId) {
		this.formId = formId
	}

	public CmsFormHandler getFormHandler() {
		return formHandler
	}

	public void setFormHandler(CmsFormHandler formHandler) {
		this.formHandler = formHandler
	}

	public CmsForm getFormConfiguration() {
		return formConfiguration
	}

	public void setFormConfiguration(CmsForm formConfiguration) {
		this.formConfiguration = formConfiguration
	}

    /**
     * Get columns from fields
     * @param showAllFields
     * @param fieldsToShow
     * @return
     */
    public List<CmsFormReportColumn> getColumns(boolean showAllFields, List<String> fieldsToShow) {
        List<CmsFormReportColumn> columns = new ArrayList<CmsFormReportColumn>();
        try {
            Iterator it = formConfiguration.getFields().iterator();

            // Add column fields
            while(it.hasNext()) {
                I_CmsField field = (I_CmsField)it.next();

                // If it is field to show
                if(!StringUtils.isBlank(field.getDbLabel()) &&
                        isFieldForShow(field) &&
                        (showAllFields ||
                                (fieldsToShow).contains(field.getDbLabel()))) {

                    // Add report column
                    columns.add(new CmsFormReportColumn(field));
                    if(field.isHasSubFields()) {
                        Iterator k = field.getSubFields().entrySet().iterator();

                        while(k.hasNext()) {
                            Map.Entry entry = (Map.Entry)k.next();
                            columns.addAll(getColumnsFromFields((List)entry.getValue()));
                        }
                    }
                }
            }
        } catch (Exception e){
            LOG.error("error cargando campos para el formulario " + formUri, e);
        }
        return columns;
    }

    /**
     * It is a field to show if it is not CmsPagingField or EmptyField
     * @param field
     * @return
     */
    private boolean isFieldForShow(I_CmsField field){
        return !field.getType().equals(CmsPagingField.getStaticType()) &&
                !field.getType().equals(CmsEmptyField.getStaticType())
    }

    /**
     * Get columns from field list
     * @param fields
     * @return
     */
    public List<CmsFormReportColumn> getColumnsFromFields(List<I_CmsField> fields) {
        List<CmsFormReportColumn> result = new ArrayList<CmsFormReportColumn>(fields.size());
        Iterator i = fields.iterator();

        while(i.hasNext()) {
            result.add(new CmsFormReportColumn((I_CmsField)i.next()));
        }

        return result;
    }

    /**
     * Get array with column labels
     * @param columns
     * @return
     */
    public String columnsLabel(List<CmsFormReportColumn> columns){
        if (columns == null || columns.size() == 0) {
            return "[]";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < columns.size(); i++) {
            CmsFormReportColumn column = columns.get(i);
            String dbLabel = column.getColumnLabel();
            if (i != 0) {
                sb.append(", ");
            }

            sb.append(dbLabel);
        }
        return "[" + sb.toString() + "]";
    }

    /**
     * Get list of data for each row
     * @param forms
     * @return
     */
    private List getDataRows(List<CmsFormDataBean> forms) {
        List<Map> data = new ArrayList<Map>();

        // For each form data bean
        for (int i = 0; i < forms.size(); i++) {
            CmsFormDataBean dataBean = forms.get(i);
            Map<String, Object> row = new HashMap<String, Object>();

            // Add date created
            row.put(F_DATE_CREATED, dataBean.getDateCreated());

            // For each column
            List<CmsFormReportColumn> columns = getColumns(true, null);
            for (int j = 0; j < columns.size(); j++) {
                CmsFormReportColumn column = columns.get(j);
                String val;

                // TODO accion Si es una accion
//				if (column.getColumnLabel().equals(ActionFormReportColumn.ACTION_FIELD_LABEL)) {
//					ActionFormReportColumn actionColumn = (ActionFormReportColumn) next;
//					val = actionColumn.getActionContent(dataBean.getEntryId());
//					row.add(val);
//				} else {

                // Get label and value
                String dbLabel = column.getColumnDbLabel();
                val = dataBean.getFieldValue(dbLabel);
                if(CmsStringUtil.isEmpty(val)) {
//                    row.put(dbLabel, "-"); TODO add format
                    row.put(dbLabel, "-");
                } else {
                    String escVal = CmsEncoder.escapeXml(val);
                    // TODO add format
//                    row.put(dbLabel, "<div title='" + escVal + "'>" + escVal + "</div>");
                    row.put(dbLabel, escVal);
                }
//				}
            }
            data.add(row);
        }
        return data;
    }

    /**
     * Delete form entry and associated data
     * @param entryId
     * @return
     */
    public boolean delete(int entryId){
        dataAccess.deleteForm(entryId);
    }

    /**
     * Update entry field value
     * @param entryId
     * @param fieldName
     * @param fieldValue
     */
    public void update(int entryId, String fieldName, String fieldValue){
        try {
            dataAccess.updateFieldValue(entryId, fieldName, fieldValue);
        } catch (Exception e) {
            LOG.error("deleting users failed", e);
        }
    }

    /**
     * Persists field into database
     * @param entryId
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public boolean insert(String fieldName, String fieldValue) {
        return insert(fieldName, fieldValue, CmsDbPool.KEY_POOL_DEFAULT);
    }

    /**
     * Persists field into database
     * @param entryId
     * @param fieldName
     * @param fieldValue
     * @param poolName
     * @return
     */
    public boolean insert(String fieldName, String fieldValue, String poolName) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        boolean inserted = false;
        int newId = 0;
        try {
            con = OpenCms.getSqlManager().getConnection(poolName);
            // 1) Compute next id
            stmt = con.prepareStatement(READ_NEXT_ENTRY_ID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                newId = rs.getInt("MAXID");
            }
            newId++;

            // 2) Write a new entry
            stmt = con.prepareStatement(WRITE_FORM_ENTRY);

            CmsForm form = formHandler.getFormConfiguration();
            String formId = form.getFormId();
            long dateCreated = System.currentTimeMillis();
            CmsUUID resourceId;
            try {
                resourceId = formHandler.getCmsObject().readResource(
                        formHandler.getRequestContext().getUri()).getStructureId();
            } catch (CmsException e) {
                resourceId = CmsUUID.getNullUUID();
            }
            stmt.setInt(1, newId);
            stmt.setString(2, formId);
            stmt.setLong(3, dateCreated);
            stmt.setString(4, resourceId.toString());
            stmt.setInt(5, 0); // initial state
            int rc = stmt.executeUpdate();
            if (rc != 1) {
                LOG.error("Could not insert new form submission" +
                        " into CMS_WEBFORM_ENTRIES." +
                        " The following submission is not stored: " + newId);
                newId = -1;
                return false;
            }
            inserted = true;

            // connection is still needed, so only close statement
            SgSql.closeAll(null, stmt, null);

            // 3) Now insert the data values for this submission with that ref_id:
            stmt = con.prepareStatement(WRITE_FORM_DATA);
            // loop over all form fields:


            // a field can contain more than one value (e.g. table field), so for all values one entry is created
            stmt.setInt(1, newId);
            stmt.setString(2, fieldName);
            stmt.setString(3, fieldValue);

            /*
             * at this level we can allow to loose a field value and try
             * to save the others instead of failing everything.
             */
            try {
                rc = stmt.executeUpdate();
            } catch (SQLException sqlex) {
                LOG.error("Could not store field " + fieldName +
                        " with value " + fieldValue +
                        " into database for submitted form: " + newId, sqlex);
            }
            if (rc != 1) {
                LOG.error("Could not store field " + fieldName +
                        " with value " + fieldValue +
                        " into database for submitted form: " + newId);
            }
        } catch (Exception e) {
            LOG.error("Inserting field " + fieldName + " : " + fieldValue);
        } finally {
            SgSql.closeAll(con, stmt, rs);
        }
        return inserted;
    }
}