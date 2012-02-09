package au.edu.usyd.reviewer.client.admin;

import java.util.Collection;
import java.util.List;

import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.StyleLib;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.widgetideas.client.ValueSpinner;

public class ActivityReviewForm extends Composite {
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private final ListBox status = new ListBox();
	private final TextBox name = new TextBox();
	private final ListBox reviewType = new ListBox();
	public final ListBox reviewTemplateLst = new ListBox();
	private final ListBox allocationStrategy = new ListBox();
	private final ListBox startDate = new ListBox();
	private final DateBox finishDate = new DateBox();
	private final CheckBox ratings = new CheckBox();
	private final ValueSpinner numLecturerReviewers = new ValueSpinner(0, 0, 15);
	private final ValueSpinner numTutorReviewers = new ValueSpinner(0, 0, 15);
	private final ValueSpinner numStudentReviewers = new ValueSpinner(0, 0, 15);
	private final ValueSpinner numAutomaticReviewers = new ValueSpinner(0, 0, 15);
	private ReviewingActivity reviewingActivity;
	private WritingActivity writingActivity;
	private final CheckBox earlySubmit = new CheckBox();
	//private final CheckBox marking = new CheckBox();
	private final static AdminServiceAsync adminService = (AdminServiceAsync) GWT.create(AdminService.class);
	
	
	public ActivityReviewForm() {
		//populate list box with review templates
		reviewTemplateLst.addItem("None", String.valueOf(0));
    	adminService.getReviewTemplates(new AsyncCallback<Collection<ReviewTemplate>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed get courses: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Collection<ReviewTemplate> reviewTemplateList) {
				for (ReviewTemplate reviewTemplate : reviewTemplateList) {
					reviewTemplateLst.addItem(reviewTemplate.getName(), String.valueOf(reviewTemplate.getId()));
				}
				for (int i = 0; i < reviewTemplateLst.getItemCount(); i++) {
					if (reviewTemplateLst.getValue(i).equals(String.valueOf(reviewingActivity.getReviewTemplateId()))) {
						reviewTemplateLst.setSelectedIndex(i);
						break;
					}
				}				
			}
		});			
	
		initWidget(mainPanel);

		// status
		status.setEnabled(false);
		status.addItem("NONE");
		status.addItem("START");
		status.addItem("FINISH");

		// review
		reviewType.addItem(ReviewingActivity.REVIEW_TYPE_COMMENTS);
		reviewType.addItem(ReviewingActivity.REVIEW_TYPE_QUESTION);
		allocationStrategy.addItem(ReviewingActivity.REVIEW_STRATEGY_RANDOM);
		allocationStrategy.addItem(ReviewingActivity.REVIEW_STRATEGY_SPREADSHEET);
		
		//Automatic Feed Back - Review TEmplates
		reviewType.addItem(ReviewingActivity.REVIEW_TYPE_TEMPLATE);
	}	
	
	public ReviewingActivity getActivityReview() {
		reviewingActivity.setName(name.getValue());
		reviewingActivity.setStartDate(writingActivity.getDeadlines().get(startDate.getSelectedIndex()));
		reviewingActivity.setFinishDate(finishDate.getValue());
		reviewingActivity.setAllocationStrategy(allocationStrategy.getValue(allocationStrategy.getSelectedIndex()));
		reviewingActivity.setFormType(reviewType.getValue(reviewType.getSelectedIndex()));
		reviewingActivity.setReviewTemplateId(Long.valueOf(reviewTemplateLst.getValue(reviewTemplateLst.getSelectedIndex())));
		reviewingActivity.setRatings(ratings.getValue());
		reviewingActivity.setNumLecturerReviewers((int) numLecturerReviewers.getSpinner().getValue());
		reviewingActivity.setNumTutorReviewers((int) numTutorReviewers.getSpinner().getValue());
		reviewingActivity.setNumStudentReviewers((int) numStudentReviewers.getSpinner().getValue());
		reviewingActivity.setNumAutomaticReviewers((int) numAutomaticReviewers.getSpinner().getValue());
		reviewingActivity.setEarlySubmit(earlySubmit.getValue());
		//reviewingActivity.setStudentMarks(marking.getValue());
		return reviewingActivity;
	}
	
	@Override
	public void onLoad() {
		numLecturerReviewers.getTextBox().setWidth("20px");
		numTutorReviewers.getTextBox().setWidth("20px");
		numStudentReviewers.getTextBox().setWidth("20px");
		numAutomaticReviewers.getTextBox().setWidth("20px");
		HorizontalPanel reviewersPanel = new HorizontalPanel();
		reviewersPanel.add(new Label("lecturers"));
		reviewersPanel.add(numLecturerReviewers);
		reviewersPanel.add(new Label("tutors"));
		reviewersPanel.add(numTutorReviewers);
		reviewersPanel.add(new Label("students"));
		reviewersPanel.add(numStudentReviewers);
		reviewersPanel.add(new Label("Automatic"));
		reviewersPanel.add(numAutomaticReviewers);		
		
		HorizontalPanel ratingsPanel = new HorizontalPanel();
		ratingsPanel.add(ratings);
		ratingsPanel.add(new Label(" allow peers to rate their reviews."));
		
		Button reviewersListButton = new Button("List");
		reviewersListButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {	
				final DialogBox dialogBox = new DialogBox();
				final ReviewersTable reviewersTable = new ReviewersTable(adminService, writingActivity, reviewingActivity);
				
				VerticalPanel panel = new VerticalPanel();
				//panel.add(buttonsPanel);
				panel.add(reviewersTable);				
				dialogBox.setHTML("<b>Reviewers List. "+ reviewingActivity.getName()+" With deadline: "+ StyleLib.dueDateFormat(reviewingActivity.getFinishDate()) +"</b>");
				dialogBox.setWidget(panel);
				dialogBox.center();
				dialogBox.show();
				//reviewersTable.setReviewingActivity(reviewingActivity);
			}});		

		Grid reviewGrid = new Grid(11, 2);
		reviewGrid.setWidget(0, 0, new Label("Name:"));
		reviewGrid.setWidget(0, 1, name);
		reviewGrid.setWidget(1, 0, new Label("Status:"));
		reviewGrid.setWidget(1, 1, status);
		reviewGrid.setWidget(2, 0, new Label("Review start:"));
		reviewGrid.setWidget(2, 1, startDate);
		reviewGrid.setWidget(3, 0, new Label("Review finish:"));
		reviewGrid.setWidget(3, 1, finishDate);
		reviewGrid.setWidget(4, 0, new Label("Form type:"));
		reviewGrid.setWidget(4, 1, reviewType);
		reviewGrid.setWidget(5, 0, new Label("Template:"));
		reviewGrid.setWidget(5, 1, reviewTemplateLst);		
		reviewGrid.setWidget(6, 0, new Label("Allocation strategy:"));
		reviewGrid.setWidget(6, 1, allocationStrategy);
		reviewGrid.setWidget(7, 0, new Label("No. reviewers:"));
		reviewGrid.setWidget(7, 1, reviewersPanel);
		reviewGrid.setWidget(8, 0, new Label("Ratings:"));
		reviewGrid.setWidget(8, 1, ratingsPanel);
		reviewGrid.setWidget(9, 0, new Label("Early submit option:"));
		reviewGrid.setWidget(9, 1, earlySubmit);			
		reviewGrid.setWidget(10, 0, new Label("Reviewers list:"));
		reviewGrid.setWidget(10, 1, reviewersListButton);		
		//reviewGrid.setWidget(11, 0, new Label("Allow peer marking:"));
		//reviewGrid.setWidget(11, 1, marking);		
		
		mainPanel.add(reviewGrid);
	}
	
	public void setActivityReview(WritingActivity writingActivity, ReviewingActivity reviewingActivity, List<String> deadLineNameList) {
		this.writingActivity = writingActivity;
		this.reviewingActivity = reviewingActivity;
		
		startDate.clear();
		for (int i = 0; i < deadLineNameList.size(); i++) {
			startDate.addItem(deadLineNameList.get(i));
		}
		
		status.setSelectedIndex(reviewingActivity.getStatus());
		name.setValue(reviewingActivity.getName());
		finishDate.setValue(reviewingActivity.getFinishDate());
		ratings.setValue(reviewingActivity.getRatings());
		numLecturerReviewers.getSpinner().setValue(reviewingActivity.getNumLecturerReviewers(), true);
		numTutorReviewers.getSpinner().setValue(reviewingActivity.getNumTutorReviewers(), true);
		numStudentReviewers.getSpinner().setValue(reviewingActivity.getNumStudentReviewers(), true);
		numAutomaticReviewers.getSpinner().setValue(reviewingActivity.getNumAutomaticReviewers(), true);
		earlySubmit.setValue(reviewingActivity.getEarlySubmit());
		//marking.setValue(reviewingActivity.getStudentMarks());

		// set review types
		for (int i = 0; i < allocationStrategy.getItemCount(); i++) {
			if (allocationStrategy.getValue(i).equals(reviewingActivity.getAllocationStrategy())) {
				allocationStrategy.setSelectedIndex(i);
				break;
			}
		}
		for (int i = 0; i < reviewType.getItemCount(); i++) {
			if (reviewType.getValue(i).equals(reviewingActivity.getFormType())) {
				reviewType.setSelectedIndex(i);
				break;
			}
		}
		for (int i = 0; i < startDate.getItemCount(); i++) {
			if (startDate.getValue(i).equals(reviewingActivity.getStartDate().getName())) {
				startDate.setSelectedIndex(i);
				break;
			}
		}
	}
}
