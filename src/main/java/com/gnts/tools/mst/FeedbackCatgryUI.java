package com.gnts.tools.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.tools.domain.mst.FeedbackCatgryDM;
import com.gnts.tools.service.mst.FeedbackCatgryService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class FeedbackCatgryUI extends BaseUI {
	private static final long serialVersionUID = 1L;
	private FeedbackCatgryService sevicebeanfbCategory = (FeedbackCatgryService) SpringContextHelper
			.getBean("feedbackcatgry");
	// form layout for input controls
	private FormLayout formLayout1, formLayout2, formLayout3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlCommetTblLayout = new VerticalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add Input fields
	private TextField tfcatename, tfweightage;
	private ComboBox cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private String loginUserName;
	private Long companyId;
	private String feedcatgry;
	private int recordCnt = 0;
	private BeanItemContainer<FeedbackCatgryDM> beanPFeedbackCatgryDM = null;
	private Logger logger = Logger.getLogger(FeedbackCatgryUI.class);
	public FeedbackQestion question;
	
	public FeedbackCatgryUI() {
		// Get the logged in user name and company id from the session
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		tfcatename = new GERPTextField("Feedback Category");
		tfweightage = new GERPTextField("weightage (%)");
		// create form layouts to hold the input items
		formLayout1 = new FormLayout();
		formLayout2 = new FormLayout();
		formLayout3 = new FormLayout();
		// Add the user input items into appropriate form layout
		formLayout1.addComponent(tfcatename);
		formLayout2.addComponent(tfweightage);
		formLayout3.addComponent(cbstatus);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		hlSearchLayout.addComponent(formLayout1);
		hlSearchLayout.addComponent(formLayout3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling User Input layout");
		// Add components for User Input Layout
		hlUserInputLayout.removeAllComponents();
		formLayout1 = new FormLayout();
		formLayout2 = new FormLayout();
		formLayout3 = new FormLayout();
		// Add the user input items into appropriate form layout
		formLayout1.addComponent(tfcatename);
		formLayout2.addComponent(tfweightage);
		formLayout3.addComponent(cbstatus);
		HorizontalLayout hh = new HorizontalLayout();
		hh.addComponent(formLayout1);
		hh.addComponent(formLayout2);
		hh.addComponent(formLayout3);
		
		TabSheet	tabFeedback=new TabSheet();
		tabFeedback.addTab(hh, "Feedbackcategory");
		tabFeedback.addTab(vlCommetTblLayout, "FeedQuestion");
		tabFeedback.setWidth("100%");
		tabFeedback.setSizeFull();
		hlUserInputLayout.addComponent(tabFeedback);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setHeight("495%");
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeFull();
		hlUserInputLayout.setMargin(false);
		
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
			List<FeedbackCatgryDM> feedbackList = new ArrayList<FeedbackCatgryDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Search Parameters are " + tfcatename.getValue() + "," + companyId + " , "
					+ (String) cbstatus.getValue());
			feedbackList = sevicebeanfbCategory.getfbCatgryList(companyId, tfcatename.getValue(), "Active");
			recordCnt = feedbackList.size();
			beanPFeedbackCatgryDM = new BeanItemContainer<FeedbackCatgryDM>(FeedbackCatgryDM.class);
			beanPFeedbackCatgryDM.addAll(feedbackList);
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Got the PNCCenter. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanPFeedbackCatgryDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "fbcatgryId", "catgryName", "weightage", "catgryStatus",
					"lastupdateddt", "lastupdatedby", });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Feedback Category", "Department Name",
					"Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("pncmapid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ " Invoking Reset search Detail");
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		assembleUserInputLayout();
		// Add input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// new feedback
		// reset the input controls to default value
		question = new FeedbackQestion(vlCommetTblLayout, null);
		resetFields();
	}
	
	private void editfeedbackcat() {
		if (tblMstScrSrchRslt.getValue() != null) {
			FeedbackCatgryDM feedbackdm = beanPFeedbackCatgryDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbstatus.setValue(feedbackdm.getCatgryStatus());
			tfcatename.setValue(feedbackdm.getCatgryName());
			tfweightage.setValue(Long.valueOf(feedbackdm.getWeightage()).toString());
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editfeedbackcat();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if ((tfcatename.getValue() == null)) {
			tfcatename.setComponentError(new UserError(GERPErrorCodes.NULL_FEEDBACK_CAT));
			errorFlag = true;
		} else {
			tfcatename.setComponentError(null);
			errorFlag = false;
		}
		if ((tfweightage.getValue() == null)) {
			tfweightage.setComponentError(new UserError(GERPErrorCodes.NULL_WEIGHTAGE));
			errorFlag = true;
		} else {
			tfweightage.setComponentError(null);
			errorFlag = false;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
		FeedbackCatgryDM feedbackctgDM = new FeedbackCatgryDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			feedbackctgDM = beanPFeedbackCatgryDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		feedbackctgDM.setCompanyId(companyId);
		feedbackctgDM.setCatgryName(tfcatename.getValue());
		feedbackctgDM.setWeightage(Long.valueOf(tfweightage.getValue().toString()));
		if (cbstatus.getValue() != null) {
			feedbackctgDM.setCatgryStatus((String) cbstatus.getValue());
		}
		feedbackctgDM.setLastupdateddt(DateUtils.getcurrentdate());
		feedbackctgDM.setLastupdatedby(loginUserName);
		sevicebeanfbCategory.saveorupdatefbCatgryDetails(feedbackctgDM);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for PNC Dept. ID " + feedcatgry);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_TOOL_FEEDBACK_CATGRY);
		UI.getCurrent().getSession().setAttribute("audittablepk", feedcatgry);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Canceling action ");
		assembleSearchLayout();
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		tfcatename.setValue(null);
		tfweightage.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
	}
}
