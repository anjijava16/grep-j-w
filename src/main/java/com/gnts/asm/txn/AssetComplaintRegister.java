package com.gnts.asm.txn;

import org.vaadin.dialogs.ConfirmDialog;
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.domain.txn.AssetMaintDetailDM;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.asm.service.txn.AssetMaintDetailService;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class AssetComplaintRegister extends Window {
	private static final long serialVersionUID = 1L;
	private AssetDetailsService serviceAssetDetail = (com.gnts.asm.service.txn.AssetDetailsService) SpringContextHelper
			.getBean("assetDetails");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AssetMaintDetailService serviceAssetMaintDetails = (AssetMaintDetailService) SpringContextHelper
			.getBean("assetMaintDetails");
	private GERPComboBox cbEmployee = new GERPComboBox("Department");
	private GERPComboBox cbAssetName = new GERPComboBox("Asset Name");
	private GERPTextArea taComplaint = new GERPTextArea("Complaint");
	private GERPComboBox cbMaintType = new GERPComboBox("Type", BASEConstants.T_AMS_ASST_MAINT_DTLS,
			BASEConstants.MAINT_TYPE);
	private Button btnSave = new GERPButton("Save", "savebt");
	private Button btnCancel = new GERPButton("Cancel", "cancelbt");
	private Long companyid, employeeId;
	private String username;
	
	public AssetComplaintRegister() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = (Long) UI.getCurrent().getSession().getAttribute("employeeId");
		setCaption("New Ticket");
		center();
		setModal(true);
		setHeight("400px");
		setWidth("400px");
		cbEmployee.setWidth("250");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setRequired(true);
		loadEmployee();
		cbEmployee.setValue(employeeId);
		cbAssetName.setWidth("250");
		cbAssetName.setItemCaptionPropertyId("assetName");
		cbAssetName.setRequired(true);
		loadAssetList();
		taComplaint.setRequired(true);
		taComplaint.setWidth("250");
		taComplaint.setHeight("200");
		cbMaintType.setWidth("250");
		cbMaintType.setRequired(true);
		setContent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setMargin(true);
				addComponent(cbEmployee);
				addComponent(cbAssetName);
				addComponent(cbMaintType);
				addComponent(taComplaint);
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(btnSave);
						addComponent(btnCancel);
					}
				});
			}
		});
		btnSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Are you really sure?", "Raise Ticket", "Not quite",
						new ConfirmDialog.Listener() {
							private static final long serialVersionUID = 1L;
							
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									if (validateDetails()) {
										saveDetails();
									}
								}
							}
						});
			}
		});
		btnCancel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				close();
			}
		});
	}
	
	private Boolean validateDetails() {
		cbAssetName.setComponentError(null);
		cbMaintType.setComponentError(null);
		cbEmployee.setComponentError(null);
		taComplaint.setComponentError(null);
		Boolean isvalid = true;
		if (cbAssetName.getValue() == null) {
			cbAssetName.setComponentError(new UserError("Select Asset Name"));
			isvalid = false;
		}
		if (cbMaintType.getValue() == null) {
			cbMaintType.setComponentError(new UserError("Select Type"));
			isvalid = false;
		}
		if (cbEmployee.getValue() == null) {
			cbEmployee.setComponentError(new UserError("Select Employee"));
			isvalid = false;
		}
		if (taComplaint.getValue() == null || taComplaint.getValue().trim().length() == 0) {
			taComplaint.setComponentError(new UserError("Enter Complaint"));
			isvalid = false;
		}
		return isvalid;
	}
	
	private void saveDetails() {
		AssetMaintDetailDM assetMaintDetailDM = new AssetMaintDetailDM();
		assetMaintDetailDM.setAssetId((Long) cbAssetName.getValue());
		assetMaintDetailDM.setMaintenanceType((String) cbMaintType.getValue());
		assetMaintDetailDM.setProblemDescription(taComplaint.getValue());
		assetMaintDetailDM.setMaintStatus("Pending");
		assetMaintDetailDM.setPreparedBy((Long) cbEmployee.getValue());
		assetMaintDetailDM.setLastUpdatedBy(username);
		assetMaintDetailDM.setLastUpdatedDt(DateUtils.getcurrentdate());
		serviceAssetMaintDetails.saveOrUpdateAssetMaintDetail(assetMaintDetailDM);
		close();
	}
	
	// Load Enquiry List
	private void loadAssetList() {
		BeanContainer<Long, AssetDetailsDM> beanAssetDetails = new BeanContainer<Long, AssetDetailsDM>(
				AssetDetailsDM.class);
		beanAssetDetails.setBeanIdProperty("assetId");
		beanAssetDetails.addAll(serviceAssetDetail.getAssetDetailList(companyid, null, null, null, null, null,null));
		cbAssetName.setContainerDataSource(beanAssetDetails);
	}
	
	// Load Department list for pnladdedit's combo Box
	private void loadEmployee() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
					null, null, "P"));
			cbEmployee.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
		}
	}
}
