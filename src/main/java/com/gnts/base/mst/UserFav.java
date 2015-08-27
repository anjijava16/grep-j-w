/**
 * File Name 		: UserFav.java 
 * Description 		: this class is used for add/edit User Favourites details. 
 * Author 			: SOUNDAR C 
 * Date 			: Feb 25, 2014
 * Modified By 		: SOUNDAR C 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd
 * Version       Date           	Modified By               Remarks
 * 0.1          Mar 03 2014        	SOUNDAR C		        Initial Version
 * 0.2		    14-Jun-2014			SK						Code re-factoring
 * 0.3          Jun 23, 2014        D Joel Glindan          Code Re-factoring 
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.UserFavDM;
import com.gnts.base.service.mst.UserFavService;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPSaveNotification;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;

public class UserFav extends BaseUI {
	private UserFavService servUserfavBean = (UserFavService) SpringContextHelper
			.getBean("userfavourites");
	private BeanItemContainer<UserFavDM> beanUserfav = null;
	// local variables declaration
	private Long companyid;
	private Long userId;
	private int recordCnt = 0;
	private String username;
	private List<UserFavDM> listUserFav = new ArrayList<UserFavDM>();
	// Initialize logger
	private Logger logger = Logger.getLogger(UserFav.class);
	private static final long serialVersionUID = 1L;
	private Button btnDelete;
	private CheckBox chCheckall;

	// Constructor
	public UserFav() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName")
				.toString();
		companyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		userId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("userId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username
				+ " > " + "Inside UserFav() constructor");
		buildview();
	}

	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username
				+ " > " + "Painting UserFav UI");
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		btnSearch.setVisible(false);
		btnReset.setVisible(false);
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnDownload.setVisible(false);
		btnDelete = new Button("Delete");
		btnDelete.addStyleName("delete");
		btnDelete.setEnabled(true);
		btnDelete.setVisible(true);
		btnDelete.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			/*
			 * 
			 * this function handles button click event
			 * 
			 * @param ClickEvent event
			 */
			@Override
			public void buttonClick(ClickEvent event) {
				if (listUserFav != null) {
					for (UserFavDM obj : listUserFav) {
						if (obj.isSelected()) {
							servUserfavBean.deleteUserfavourites(obj
									.getUserfavId());
							new GERPSaveNotification();
						}
					}
				}
				loadSrchRslt();
				btnDelete.setEnabled(false);
				chCheckall.setValue(false);
			}
		});
		chCheckall = new CheckBox("Select All");
		chCheckall.addStyleName("delete");
		chCheckall.setEnabled(true);
		chCheckall.setVisible(true);
		setCheckBoxTable();
		chCheckall.setImmediate(true);
		chCheckall.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().equals(true)) {
					loadSrchRslt();
					btnDelete.setEnabled(true);
				} else {
					loadSrchRslt();
					btnDelete.setEnabled(false);
				}
			}
		});
		hlCmdBtnLayout.addComponent(chCheckall);
	
		hlCmdBtnLayout.setSpacing(true);
		hlCmdBtnLayout.setComponentAlignment(chCheckall, Alignment.MIDDLE_RIGHT);
		hlCmdBtnLayout.addComponent(btnDelete);
		hlCmdBtnLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);
		hlCmdBtnLayout.setExpandRatio(btnDelete, 1);
		// build search layout
		resetFields();
		loadSrchRslt();
	}

	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username
				+ " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		listUserFav = servUserfavBean.getUserFavouritesList(userId, null, "F");
		recordCnt = listUserFav.size();
		if ( chCheckall.getValue().equals(true)) {
			List<UserFavDM> mylist = new ArrayList<UserFavDM>();
			for (UserFavDM obj : listUserFav) {
				obj.setSelected(true);
				mylist.add(obj);
			}
			beanUserfav = new BeanItemContainer<UserFavDM>(UserFavDM.class);
			beanUserfav.addAll(mylist);
		} else {
			List<UserFavDM> mylist = new ArrayList<UserFavDM>();
			for (UserFavDM obj : listUserFav) {
				obj.setSelected(false);
				mylist.add(obj);
			}
			beanUserfav = new BeanItemContainer<UserFavDM>(UserFavDM.class);
			beanUserfav.addAll(mylist);
		}
		tblMstScrSrchRslt.setContainerDataSource(beanUserfav);
		tblMstScrSrchRslt.setSelectable(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username
				+ " > " + "Got the UserFav result set");
		tblMstScrSrchRslt.setContainerDataSource(beanUserfav);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "selected",
				"userfavId", "userName", "screenName", "lastUpdatedDt",
				"lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "", "Ref.Id",
				"User Name", "Screen Name", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("userfavId", Align.LEFT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : "
				+ recordCnt);
	}

	private void setCheckBoxTable() {
		tblMstScrSrchRslt.addGeneratedColumn("selected", new ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component generateCell(final Table source,
					final Object itemId, final Object columnId) {
				final UserFavDM bean = (UserFavDM) itemId;
				final CheckBox chbox = new CheckBox();
				chbox.setImmediate(true);
				chbox.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(final ValueChangeEvent event) {
						bean.setSelected((Boolean) event.getProperty()
								.getValue());
						if (event.getProperty().getValue().equals(true)) {
							btnDelete.setEnabled(true);
						} else {
							btnDelete.setEnabled(false);
						}
					}
				});
				if (bean.isSelected()) {
					chbox.setValue(true);
				} else {
					chbox.setValue(false);
				}
				return chbox;
			}
		});
	}

	
	@Override
	protected void resetFields() {
	}

	@Override
	protected void searchDetails() throws NoDataFoundException {
	}

	@Override
	protected void resetSearchDetails() {
	}

	@Override
	protected void addDetails() {
	}

	@Override
	protected void showAuditDetails() {
	}

	@Override
	protected void cancelDetails() {
	}

	@Override
	protected void editDetails() {
	}

	@Override
	protected void validateDetails() throws ValidationException {
	}

	@Override
	protected void saveDetails() throws ERPException.SaveException {
	}
}
