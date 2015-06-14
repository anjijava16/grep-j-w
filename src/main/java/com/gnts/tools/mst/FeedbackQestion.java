package com.gnts.tools.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.tools.domain.mst.FeedBackQuestionDM;
import com.gnts.tools.service.mst.FeedbackQuestionService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class FeedbackQestion implements ClickListener {
	private static final long serialVersionUID = 1L;
	private FeedbackQuestionService sevicebeanfbQuestion = (FeedbackQuestionService) SpringContextHelper
			.getBean("feedbackquestion");
	private Table tblFeedback = null;
	private Long companyId;
	private Button btnSave = new Button("Add");
	private Button btnFeedback = new Button("Add");
	private Button btnEdit = new Button("Edit");
	private VerticalLayout vlTableForm, vlMainLayout, vlTableLayout;
	private VerticalLayout vlDocuemntPanel;
	private VerticalLayout vlDocument;
	private HorizontalLayout hlButtonLayout1;
	private String basepath1, basepath;
	private FormLayout flMainform1, flMainform2, flMainform3, flMainform4;
	private String userName, strWidth = "150px";
	private int total = 0;
	private BeanItemContainer<FeedBackQuestionDM> beanFeedBackQuestion = null;
	List<FeedBackQuestionDM> feedbackQuesList = new ArrayList<FeedBackQuestionDM>();
	private ComboBox cbAnsType = new GERPComboBox("Answer Type", BASEConstants.M_TOOL_FEEDBACK_QUESTION,
			BASEConstants.ANS_TYPES);
	private ComboBox cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private TextArea taQuestion;
	private FormLayout flcolumn1, flcolumn2;
	private Logger logger = Logger.getLogger(FeedbackQestion.class);
	
	public FeedbackQestion(VerticalLayout vlCommetTblLayout, Long fbcatgryId) {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		fbcatgryId = fbcatgryId;
		buildview(vlCommetTblLayout);
	}
	
	private void buildview(VerticalLayout vlCommetTblLayout) {
		// TODO Auto-generated method stub
		vlCommetTblLayout.removeAllComponents();
		btnSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnSave == event.getButton()) {
					saveFeedbackQuestions();
				}
			}
		});
		btnSave.setStyleName("add");
		btnEdit = new Button("Edit", this);
		btnEdit.setEnabled(false);
		btnEdit.addStyleName("editbt");
		btnFeedback = new Button(" Add", this);
		btnFeedback.addStyleName("add");
		taQuestion = new TextArea("Question");
		taQuestion.setRequired(true);
		taQuestion.setWidth(strWidth);
		taQuestion.setHeight("50px");
		/**
		 * add fields to header layout
		 */
		/**
		 * add fields to form Layout
		 */
		flMainform1 = new FormLayout();
		flMainform2 = new FormLayout();
		flMainform3 = new FormLayout();
		flMainform4 = new FormLayout();
		flMainform1.setSpacing(true);
		flMainform1.addComponent(taQuestion);
		flMainform2.addComponent(cbAnsType);
		flMainform3.addComponent(cbstatus);
		flMainform4.addComponent(btnSave);
		/**
		 * declare the table and add in panel
		 */
		tblFeedback = new Table();
		// tblFeedback.setStyleName(Runo.TABLE_SMALL);
		tblFeedback.setWidth("1000");
		tblFeedback.setPageLength(5);
		tblFeedback.setSizeUndefined();
		tblFeedback.setSizeFull();
		tblFeedback.setInvalidAllowed(true);
		tblFeedback.setFooterVisible(true);
		tblFeedback.setColumnCollapsingAllowed(true);
		tblFeedback.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblFeedback.isSelected(event.getItemId())) {
					tblFeedback.setImmediate(true);
					btnSave.setCaption("Add");
					btnSave.setStyleName("savebt");
					taQuestion.setValue("");
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnSave.setCaption("Update");
					btnSave.setStyleName("savebt");
					editFQuestionDetails();
				}
			}
		});
		HorizontalLayout hluserInput = new HorizontalLayout();
		hluserInput.addComponent(flMainform1);
		hluserInput.addComponent(flMainform2);
		hluserInput.addComponent(flMainform3);
		hluserInput.addComponent(flMainform4);
		hluserInput.setMargin(true);
		hluserInput.setSpacing(true);
		hluserInput.setMargin(true);
		hluserInput.setComponentAlignment(flMainform4, Alignment.MIDDLE_RIGHT);
		vlTableForm = new VerticalLayout();
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.setSpacing(true);
		vlTableForm.addComponent(hluserInput);
		/**
		 * add search , table and add/edit panel in main layout
		 */
		VerticalLayout hlButton = new VerticalLayout();
		hlButton.addComponent(vlTableForm);
		hlButton.addComponent(tblFeedback);
		hlButton.setSpacing(true);
		vlCommetTblLayout.addComponent(hlButton);
		setTableProperties();
		loadsrch(false, null);
	}
	
	public void editFQuestionDetails() {
		try {
			if (tblFeedback.getValue() != null) {
				FeedBackQuestionDM editFeedquestion = beanFeedBackQuestion.getItem(tblFeedback.getValue()).getBean();
				if (editFeedquestion.getQuestionName() != null) {
					taQuestion.setValue(editFeedquestion.getQuestionName());
				}
				if (editFeedquestion.getAnswerType() != null) {
					cbAnsType.setValue(editFeedquestion.getAnswerType());
				}
				if (editFeedquestion.getStatus() != null) {
					cbstatus.setValue(editFeedquestion.getStatus());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Edit is nnot workinghbhhhhhhhhhhhhh");
		}
	}
	
	private void setTableProperties() {
		tblFeedback.setColumnAlignment("questionId", Align.RIGHT);
		tblFeedback.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblFeedback.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
				} else {
					btnEdit.setEnabled(true);
				}
				if (tblFeedback.isSelected(event.getItemId())) {
					btnFeedback.setEnabled(true);
				} else {
					btnFeedback.setEnabled(false);
				}
			}
		});
	}
	
	public void saveFeedbackQuestions() {
		validateAll();
		FeedBackQuestionDM saveFeedbackQuest = new FeedBackQuestionDM();
		if (tblFeedback.getValue() != null) {
			saveFeedbackQuest = beanFeedBackQuestion.getItem(tblFeedback.getValue()).getBean();
			feedbackQuesList.remove(saveFeedbackQuest);
		}
		if (taQuestion != null) {
			saveFeedbackQuest.setQuestionName(taQuestion.getValue());
		}
		if (cbAnsType != null) {
			saveFeedbackQuest.setAnswerType((String) cbAnsType.getValue());
		}
		if (cbstatus != null) {
			saveFeedbackQuest.setStatus((String) cbstatus.getValue());
		}
		saveFeedbackQuest.setCompanyId(companyId);
		saveFeedbackQuest.setLastupdatedby(userName);
		saveFeedbackQuest.setLastupdateddt(DateUtils.getcurrentdate());
		if (taQuestion.isValid()) {
			feedbackQuesList.add(saveFeedbackQuest);
			resetfields();
			loadsrch(false, null);
		}
		btnSave.setCaption("Add");
	}
	
	public void loadsrch(boolean fromdb, Long fbcatgryId) {
		if (fromdb) {
			feedbackQuesList = sevicebeanfbQuestion.getFeedbackQuestionList(null, null, fbcatgryId, "Active");
		}
		try {
			tblFeedback.removeAllItems();
			tblFeedback.setWidth("1250");
			total = feedbackQuesList.size();
			System.out.println("LISTSIZE---1->" + feedbackQuesList.size());
			beanFeedBackQuestion = new BeanItemContainer<FeedBackQuestionDM>(FeedBackQuestionDM.class);
			beanFeedBackQuestion.addAll(feedbackQuesList);
			tblFeedback.setSelectable(true);
			tblFeedback.setContainerDataSource(beanFeedBackQuestion);
			tblFeedback.setColumnAlignment("questionId", Align.RIGHT);
			tblFeedback.addItem(taQuestion);
			tblFeedback.setVisibleColumns(new Object[] { "questionId", "questionName", "answerType", "lastupdateddt",
					"lastupdatedby" });
			tblFeedback.setColumnHeaders(new String[] { "Ref.Id", "Question", "Answer Type", "Last Updated Date",
					"Last Updated By" });
			tblFeedback.setColumnFooter("lastUpdatedBy", "No.of Records : " + total);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	public void resetfields() {
		taQuestion.setValue("");
		cbAnsType.setValue(null);
		cbstatus.setValue(null);
		btnSave.setComponentError(null);
		tblFeedback.removeAllItems();
		// /commentList=new ArrayList<CommentsDM>();
		total = 0;
	}
	
	private boolean validateAll() {
		boolean valid = true;
		try {
			taQuestion.validate();
			taQuestion.setComponentError(null);
		}
		catch (Exception e) {
			logger.info("validaAll :comments is empty--->" + e);
			taQuestion.setComponentError(new UserError("Enter Question"));
			valid = false;
		}
		return valid;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
	}
}
