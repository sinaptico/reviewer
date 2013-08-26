package au.edu.usyd.reviewer.client.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import au.edu.usyd.reviewer.client.admin.glosser.SiteForm;
import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;

import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.gwt.DocEntryWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

//TODO Documentation - link to Template form
//TODO Documentation - link to Question generation module
//TODO Documentation - link to "SpeedBack" options description 

/**
 * <p>Main form for "Writing Activities" creation. The information collected in this form is:<p>
 * 
 * <ul>
 *	<li><p><b>Course details and notifications:</b></p>
 *		<ul>
 *			<li><b>Course:</b> Course name for the current activity. (Drop-menu with the courses in the system).</li>
 *			<li><b>Tutorial:</b> Tutorial name for the current activity. (Drop-menu with the tutorial names for the selected course).</li>
 *			<li><b>Notifications:</b> Check box to indicate if the system will send email notifications about this writing activity to students.</li>
 *			<li><b>Early submit option:</b> Check box to indicate if students can submit their documents manually.</li>
 *		</ul>
 *	</li>
 *
 *	<li><p><b>Feedback:</b></p>
 *		<ul>
 *			<li><b>Track reviews:</b> Check box to indicate if the dates where students read feedback is stored.</li>
 *			<li><b>Glosser:</b> Drop-menu with the Glosser sites recorded in the system. Once selected, students will see the Glosser link in their assignments.</li>
 *			<li><b>MyStats:</b> Check box to indicate if the writing statistics are shown to students.</li>
 *		</ul>
 *	</li>
 *
 *	<li><p><b>Writing Task details:</b></p>
 *		<ul>
 *			<li><b>Name:</b> Name of the writing activity. (Used for the link that students see).</li>
 *			<li><b>Document type-genre:</b> 2 Drop-menus, one with the document types (document, presentation, spreadsheet, logbook and file upload) and the other one with the genre types (proposal, lab report, field trip, thesis and lab book).</li>
 *			<li><b>Document template:</b> Drop-menu with the list of saved templates for the course in the Google apps account. (If selected, students receive a copy of this document as their "empty" document for the writing activity).</li>
 *			<li><b>Groups:</b> Check box to indicate if the document are created for groups. (Group details are extracted from the student list saved for the course in the Google apps account.</li>
 *			<li><b>Start date:</b> Date where the documents are created and the activity starts.</li>
 *		</ul>
 *	</li>
 *	
 *	<li><p><b>Deadlines</b></p>
  *		<ul>
 *			<li><b>Name:</b> Name of the deadline. (Used for the deadline notifications that students see).</li>
 *			<li><b>Max Grade:</b> Maximun grade number that can be given by student/tutor/lecturer that reviews this deadline.</li>
 *			<li><b>Finish date:</b> Date where the documents are locked and the activity deadline finishes.</li>
 *		</ul>
 *	</li>
 *	
 *	<li><p><b>Reviewing Tasks: </b> The information related to the reviewing task is collected by the form {@link ActivityReviewForm Activity Review Form}.</p></li>
 * </ul> 
 * 
 */

public class ActivityForm extends Composite {

	/** The main panel of the form where all the fields and widgets are placed. */
	private VerticalPanel mainPanel = new VerticalPanel();	
	
	/** The writing activity that is managed by the form. */
	private WritingActivity writingActivity;	
	
	/** The course of the writing activity that is managed by the form. */
	private Course course = null;	
	
	/** Collection of courses list recorded in the system that are available in the form for the writing activity that is managed by the form. */
	private Collection<Course> courses;	
	
	/** Check box "Send email notifications to students" to indicate if the system will send email notifications about this writing activity to students. */
	private final CheckBox emailStudents = new CheckBox();	
	
	/** Check box to indicate if the writing statistics are shown to students. */
	private final CheckBox showStats = new CheckBox();	
	
	/** ListBox with the courses list recorded in the system with their details of the year and semester. */
	private final ListBox courseList = new ListBox();	
	
	/** ListBox with the status of the writing activity */
	private final ListBox status = new ListBox();	
	
	/** ListBox with the Glosser sites recorded in the system. */
	private final ListBox glosserList = new ListBox();	
	
	/** ListBox with the tutorial names for the selected course. */
	private final ListBox tutorialList = new ListBox();	
	
	/** TextBox with the name of the writing activity managed in the form. */
	
	private final TextBox name = new TextBox();	
	/** CheckBox that indicates if the documents are created for groups. */
	
	private final CheckBox groups = new CheckBox();	
	
	/** ListBox with document types: document, presentation, spreadsheet, logbook and file upload. */
	private final ListBox documentType = new ListBox();	
	
	/** ListBox with the list of saved templates for the course in the Google apps account. */
	
	private final ListBox documentTemplate = new ListBox();	
	
	/** DateBox with date where the documents are created and the activity starts. */
	private final DateBox startDate = new DateBox();
	
	/** FlexTable where dead lines details are placed in the form. */
	private final FlexTable deadlineTable = new FlexTable();	
	
	/** FlexTable where reviews details are placed in the form. */
	private final FlexTable reviewTable = new FlexTable();	
	
	/** CheckBox that indicates if early submits are allowed for the writing activity. */
	private final CheckBox allowSubmit = new CheckBox();	
	
	/** CheckBox that indicates if the dates when students read reviews are recorded. */
	private final CheckBox trackReviews = new CheckBox();	
	
	/** ListBox with the genre types: proposal, lab report, field trip, thesis and lab book. */
	private final ListBox genre = new ListBox();	
	
	/** TextBox List with the deadlines defined for the writing activity. */
	private List<TextBox> deadLineTextBoxList = new ArrayList<TextBox>();

	private Long organizationId = null;
	
	private Button addDeadline = new Button("Add Deadline");
	
	private User loggedUser =null;
	
	private HorizontalPanel googleTemplates = new HorizontalPanel();
	
	
	/**
	 * Instantiates a new activity form and populates the "Static" Drop-menus with the "Document Types", "Document genres" and "Activity statuses".  
	 */
	public ActivityForm() {

		initWidget(mainPanel);
		
		// assessment
		documentType.addItem(WritingActivity.DOCUMENT_TYPE_DOCUMENT);
		documentType.addItem(WritingActivity.DOCUMENT_TYPE_PRESENTATION);
		documentType.addItem(WritingActivity.DOCUMENT_TYPE_SPREADSHEET);
		documentType.addItem(WritingActivity.DOCUMENT_TYPE_LOGBOOK);
		documentType.addItem(WritingActivity.DOCUMENT_TYPE_FILE_UPLOAD);
		
		genre.addItem(WritingActivity.DOCUMENT_GENRE_PROPOSAL);
		genre.addItem(WritingActivity.DOCUMENT_GENRE_LAB_REPORT);
		genre.addItem(WritingActivity.DOCUMENT_GENRE_FIELD_TRIP);
		genre.addItem(WritingActivity.DOCUMENT_GENRE_THESIS);
		genre.addItem(WritingActivity.DOCUMENT_GENRE_LAB_BOOK);

		// status
		status.setEnabled(false);
		status.addItem("NONE");
		status.addItem("START");
		status.addItem("FINISH");
	}

	/**
	 * Adds a new deadline to the form. The deadline details added to the form include: deadline status, name, 
	 * max grade, dead line date and remove button (To delete an existing deadline). It also populates the "deadLineTextBoxList" 
	 * to be used in the creation of reviewing activities. 
	 *
	 * @param deadline - The deadline to be added.
	 */
	private void addDeadline(Deadline deadline) {
		ListBox status = new ListBox();
		status.setEnabled(false);
		status.addItem("NONE");
		status.addItem("START");
		status.addItem("FINISH");
		status.setSelectedIndex(deadline.getStatus());
		
		TextBox name = new TextBox();
		name.setValue(deadline.getName());
		
		TextBox maxGrade = new TextBox();
		maxGrade.setWidth("60px");
		maxGrade.setValue(String.valueOf(deadline.getMaxGrade()));
		
		DateBox deadlineDate = new DateBox();
		deadlineDate.setValue(deadline.getFinishDate());
		final Button remove = new Button("X");
		remove.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent arg0) {
				if (deadlineTable.getRowCount() == 1 ){
					Window.alert("This is the only deadline of the activity. You can not remove it.");
				} else {
					for(int i=1; i<deadlineTable.getRowCount(); i++) {
						if(remove.equals(deadlineTable.getWidget(i, 4))) {
							Deadline deadlineToRemove = writingActivity.getDeadlines().get(i-1);
							boolean removeOK = true;
							for(ReviewingActivity reviewing :writingActivity.getReviewingActivities()){
								if (reviewing.getStartDate()!= null && reviewing.getStartDate().equals(deadlineToRemove)){
									removeOK = false;
									Window.alert("The deadline can not be deleted because it's been used by a reviewing task.\nPlease, change the reviewing task and then try to remove it again.\nReviewing Task: " + reviewing.getName());
								}
							}
							if (removeOK){
								writingActivity.getDeadlines().remove(i-1);
								deadlineTable.removeRow(i);
								deadLineTextBoxList.remove(i-1);
							}
							
						}
					}
				}
			}});
		int row = deadlineTable.getRowCount();
		if (deadline.getStatus() == Deadline.STATUS_DEADLINE_FINISH){
			status.setEnabled(false);
			name.setEnabled(false);
			maxGrade.setEnabled(false);
			deadlineDate.setEnabled(false);
			remove.setEnabled(false);
		}
		deadlineTable.setWidget(row, 0, status);
		deadlineTable.setWidget(row, 1, name);
		deadlineTable.setWidget(row, 2, maxGrade);
		deadlineTable.setWidget(row, 3, deadlineDate);
		deadlineTable.setWidget(row, 4, remove);
		
		deadLineTextBoxList.add(name);
	}

	/**
	 * Adds a reviewing activity. The details added to the form include Name of the reviewing activity, Deadline when it starts, Finish Date, 
	 * Form type ("Comment", "Questions" or "Template"), Template, Allocation strategy, Ratings, Early submit option and Feedback Template Type.  
	 *
	 * @param reviewingActivity the reviewing activity
	 */
	protected void addReviewingActivity(ReviewingActivity reviewingActivity) {
		ActivityReviewForm reviewForm = new ActivityReviewForm();
		reviewForm.setOrganizationId(organizationId);
		List<String> deadLineNameList = new ArrayList<String>();
		
		for (int i = 0; i < deadLineTextBoxList.size(); i++) {
			String deadlineName = deadLineTextBoxList.get(i).getValue();
			if (!deadLineNameList.contains(deadlineName)){
				deadLineNameList.add(deadlineName);
			}
		}	
		
		reviewForm.setActivityReview(writingActivity, reviewingActivity,deadLineNameList);
		final Button remove = new Button("X");
		remove.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent arg0) {
				for(int i=0; i< reviewTable.getRowCount(); i++) {
					if(remove.equals(reviewTable.getWidget(i, 1))) {
						writingActivity.getReviewingActivities().remove(i);
						reviewTable.removeRow(i);
					}
				}
			}});	
		int row = reviewTable.getRowCount();
		reviewTable.setWidget(row, 0, reviewForm);
		reviewTable.setWidget(row, 1, remove);
		if (reviewingActivity.getStatus() >= Activity.STATUS_FINISH){
			remove.setEnabled(false);
		} else{
			remove.setEnabled(true);
		}
			
	}

	/**
	 * Gets the course of the writing Activity that is managed in the form.
	 *
	 * @return the course
	 */
	public Course getCourse() {
		return course;
	}

	/**
	 * Gets the writing activity. It takes the writing activity field and updates it with the value of all the components of the form.
	 *
	 * @return the writing activity
	 */
	public WritingActivity getWritingActivity() {
		writingActivity.setGlosserSite(Long.valueOf(glosserList.getValue(glosserList.getSelectedIndex())));
		writingActivity.setTutorial(tutorialList.getValue(tutorialList.getSelectedIndex()));
		writingActivity.setName(name.getValue());
		writingActivity.setDocumentType(documentType.getValue(documentType.getSelectedIndex()));
		writingActivity.setDocumentTemplate(documentTemplate.getValue(documentTemplate.getSelectedIndex()));
		writingActivity.setStartDate(startDate.getValue());
		writingActivity.setGroups(groups.getValue());
		writingActivity.setEmailStudents(emailStudents.getValue());
		writingActivity.setShowStats(showStats.getValue());
		writingActivity.setEarlySubmit(allowSubmit.getValue());
		writingActivity.setTrackReviews(trackReviews.getValue());
		writingActivity.setGenre(genre.getValue(genre.getSelectedIndex()));
		
		for(int i=1; i<deadlineTable.getRowCount(); i++) {
			writingActivity.getDeadlines().get(i-1).setName(((TextBox)deadlineTable.getWidget(i, 1)).getValue());
			writingActivity.getDeadlines().get(i-1).setMaxGrade(Double.valueOf(((TextBox)deadlineTable.getWidget(i, 2)).getValue()));
			writingActivity.getDeadlines().get(i-1).setFinishDate(((DateBox)deadlineTable.getWidget(i, 3)).getValue());
		}

		for(int i=0; i<reviewTable.getRowCount(); i++) {
			writingActivity.getReviewingActivities().set(i, ((ActivityReviewForm)reviewTable.getWidget(i, 0)).getActivityReview());
		}

		return writingActivity;
	}
	
	/**
	 * It loads all the defined components (Horizontal and Vertical panels, CheckBoxes, 
	 * TextBoxes, ListBoxes ...) into the form.	 
	 * 
	 */
	@Override
	public void onLoad() {
		// course
		courseList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Long courseId = Long.valueOf(courseList.getValue(courseList.getSelectedIndex()));
				for (Course c : courses) {
					if (c.getId().equals(courseId)) {
						setCourse(c);
						break;
					}
				}
			}
		});

		HorizontalPanel emailStudentsPanel = new HorizontalPanel();
		emailStudentsPanel.add(emailStudents);
		emailStudentsPanel.add(new Label(" send email notifications to students, staff and admins."));
//		emailStudentsPanel.add(new Label(" send email notifications to students."));
		
		Grid activityGrid = new Grid(6, 2);
		activityGrid.setWidget(0, 0, new Label("Course:"));
		activityGrid.setWidget(0, 1, courseList);
		activityGrid.setWidget(1, 0, new Label("Tutorial:"));
		activityGrid.setWidget(1, 1, tutorialList);
		activityGrid.setWidget(2, 0, new Label("Status:"));
		activityGrid.setWidget(2, 1, status);
		activityGrid.setWidget(3, 0, new Label("Notifications:"));
		activityGrid.setWidget(3, 1, emailStudentsPanel);
		activityGrid.setWidget(4, 0, new Label("Early submit option:"));
		activityGrid.setWidget(4, 1, allowSubmit);
		activityGrid.setWidget(5, 0, new Label("Track reviews:"));
		activityGrid.setWidget(5, 1, trackReviews);
	
		Grid feedbackGrid = new Grid(2, 2);
		feedbackGrid.setWidget(0, 0, new Label("Glosser:"));
		feedbackGrid.setWidget(0, 1, glosserList);

		HorizontalPanel groupsPanel = new HorizontalPanel();
		groupsPanel.add(groups);
		groupsPanel.add(new Label(" create group documents."));
		
		addDeadline.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {	
				Deadline newDeadline = new Deadline("Draft");
				writingActivity.getDeadlines().add(newDeadline);
				addDeadline(newDeadline);
			}});
		
		deadlineTable.setCellPadding(2);
		deadlineTable.setCellSpacing(0);
		deadlineTable.setBorderWidth(1);
		VerticalPanel deadlinePanel = new VerticalPanel();
		deadlinePanel.add(deadlineTable);
		deadlinePanel.add(addDeadline);
		Grid writeGrid = new Grid(5, 3);
		writeGrid.setWidget(0, 0, new Label("Name:"));
		writeGrid.setWidget(0, 1, name);
		writeGrid.setWidget(1, 0, new Label("Document type-genre:"));
		writeGrid.setWidget(1, 1, documentType);
		writeGrid.setWidget(1, 2, genre);
		writeGrid.setWidget(2, 0, new Label("Document template:"));
		writeGrid.setWidget(2, 1, documentTemplate);
		googleTemplates.clear();
		if (course != null && loggedUser != null){
			DocEntryWidget widget = new DocEntryWidget(course.getTemplatesFolderId(), "Templates", course.getDomainName(), false, loggedUser);
			googleTemplates.add(widget);
			writeGrid.setWidget(2, 2,googleTemplates );
		}
		writeGrid.setWidget(3, 0, new Label("Groups:"));
		writeGrid.setWidget(3, 1, groupsPanel);
		writeGrid.setWidget(4, 0, new Label("Start date:"));
		writeGrid.setWidget(4, 1, startDate);

		Button addReview = new Button("Add Review");
		addReview.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				ReviewingActivity newReview = new ReviewingActivity();
				addReviewingActivity(newReview);
				writingActivity.getReviewingActivities().add(newReview);
			}});

		reviewTable.setCellPadding(2);
		reviewTable.setCellSpacing(0);
		reviewTable.setBorderWidth(1);
		VerticalPanel reviewPanel = new VerticalPanel();
		reviewPanel.add(reviewTable);
		reviewPanel.add(addReview);
		mainPanel.clear();
		mainPanel.add(activityGrid);
		mainPanel.add(new HTML("<br/><b>Automatic Feedback</b>"));
		mainPanel.add(feedbackGrid);
		mainPanel.add(new HTML("<br/><b>Writing Task</b>"));
		mainPanel.add(writeGrid);
		mainPanel.add(new HTML("Deadlines:"));
		mainPanel.add(deadlinePanel);
		mainPanel.add(new HTML("<br/><b>Reviewing Tasks</b>"));
		mainPanel.add(reviewPanel);
		mainPanel.add(new HTML("<br/>"));
		
		/*
		 * If status == START then set all the fields to disabled except notifications, early submit option, track reviews and automatic feedback.
		 */
		disabledFields();
	}

	/**
	 * Sets the course.
	 *
	 * @param course the new course
	 */
	private void setCourse(Course course) {
		this.course = course;
		
		// set tutorial options
		tutorialList.clear();
		tutorialList.addItem(WritingActivity.TUTORIAL_ALL);
		for (String tutorial : course.getTutorials()) {
			tutorialList.addItem(tutorial);
		}
		// set templates options
		documentTemplate.clear();
		documentTemplate.addItem(WritingActivity.DOCUMENT_TEMPLATE_NONE, WritingActivity.DOCUMENT_TEMPLATE_NONE);
		for (DocEntry template : course.getTemplates()) {
			documentTemplate.addItem(template.getTitle(), template.getDocumentId());
		}
		googleTemplates.clear();
		if (course != null && loggedUser != null){
			DocEntryWidget widget = new DocEntryWidget(course.getTemplatesFolderId(), "Templates", course.getDomainName(), false, loggedUser);
			googleTemplates.add(widget);
		}
	}
	
	/**
	 * Sets the courses.
	 *
	 * @param courses the new courses
	 */
	public void setCourses(Collection<Course> courses) {
		this.courses = courses;
		courseList.clear();
		courseList.setEnabled(true);
		if (courses.size() > 0) {
			this.course = courses.iterator().next();
			for (Course course : courses) {
				courseList.addItem(course.getName()+"-"+course.getYear()+"S"+course.getSemester(), String.valueOf(course.getId()));
			}
		}

		Long courseId = Long.valueOf(courseList.getValue(courseList.getSelectedIndex()));
		for (Course c : courses) {
			if (c.getId().equals(courseId)) {
				course = c;
				tutorialList.clear();
				tutorialList.addItem(WritingActivity.TUTORIAL_ALL);
				for (String tutorial : course.getTutorials()) {
					tutorialList.addItem(tutorial);
				}

				documentTemplate.clear();
				documentTemplate.addItem(WritingActivity.DOCUMENT_TEMPLATE_NONE, WritingActivity.DOCUMENT_TEMPLATE_NONE);
				for (DocEntry template : course.getTemplates()) {
					documentTemplate.addItem(template.getTitle(), template.getDocumentId());
				}
				googleTemplates.clear();
				if (course != null && loggedUser != null){
					DocEntryWidget widget = new DocEntryWidget(course.getTemplatesFolderId(), "Templates", course.getDomainName(), false, loggedUser);
					googleTemplates.add(widget);
				}
				
				break;
			}
		}
	}
	
	public void setLoggedUser(User user){
		loggedUser = user;
	}

	/**
	 * Sets the glosser sites.
	 *
	 * @param glosserSites the new glosser sites
	 */
	public void setGlosserSites(List<SiteForm> glosserSites) {
		glosserList.clear();
		glosserList.addItem("none", String.valueOf(WritingActivity.GLOSSER_SITE_NONE));
		if (glosserSites.size() > 0) {
			for (SiteForm glosserSite : glosserSites) {
				glosserList.addItem(glosserSite.getName(), String.valueOf(glosserSite.getId()));
			}
		}
	}

	/**
	 * Sets the writing activity values extracted from the writing activity object into the form.
	 *
	 * @param writingActivity the new writing activity
	 */
	public void setWritingActivity(WritingActivity writingActivity) {
		this.writingActivity = writingActivity;
		status.setSelectedIndex(writingActivity.getStatus());
		name.setValue(writingActivity.getName());
		startDate.setValue(writingActivity.getStartDate());
		groups.setValue(writingActivity.getGroups());
		emailStudents.setValue(writingActivity.getEmailStudents());
		showStats.setValue(writingActivity.getShowStats());
		allowSubmit.setValue(writingActivity.getEarlySubmit());
		trackReviews.setValue(writingActivity.getTrackReviews());

		// set glosser site
		for (int i = 0; i < glosserList.getItemCount(); i++) {
			if (Long.valueOf(glosserList.getValue(i)).equals(writingActivity.getGlosserSite())) {
				glosserList.setSelectedIndex(i);
				break;
			}
		}

		// set tutorial
		for (int i = 0; i < tutorialList.getItemCount(); i++) {
			if (tutorialList.getValue(i).equals(writingActivity.getTutorial())) {
				tutorialList.setSelectedIndex(i);
				break;
			}
		}

		// set assessment types
		for (int i = 0; i < documentType.getItemCount(); i++) {
			if (documentType.getValue(i).equals(writingActivity.getDocumentType())) {
				documentType.setSelectedIndex(i);
				break;
			}
		}
		
		// set genre
		for (int i = 0; i < genre.getItemCount(); i++) {
			if (genre.getValue(i).equals(writingActivity.getGenre())) {
				genre.setSelectedIndex(i);
				break;
			}
		}		

		// set template
		for (int i = 0; i < documentTemplate.getItemCount(); i++) {
			if (documentTemplate.getValue(i).equals(writingActivity.getDocumentTemplate())) {
				documentTemplate.setSelectedIndex(i);
				break;
			}
		}
		
		deadlineTable.removeAllRows();
		deadlineTable.setHTML(0, 0, "Status");
		deadlineTable.setHTML(0, 1, "Name");
		deadlineTable.setHTML(0, 2, "Max grade");
		deadlineTable.setHTML(0, 3, "Finish date");
		for(Deadline deadline : writingActivity.getDeadlines()) {
			this.addDeadline(deadline);
		}
		
		reviewTable.removeAllRows();
		for(ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
			this.addReviewingActivity(reviewingActivity);
		}
	}

	/**
	 * Sets the writing activity and course.
	 *
	 * @param writingActivity the writing activity
	 * @param course the course
	 */
	public void setWritingActivityAndCourse(WritingActivity writingActivity, Course course) {
		this.setCourse(course);
		courseList.clear();
		courseList.addItem(course.getName(), String.valueOf(course.getId()));
		courseList.setEnabled(false);
		tutorialList.setEnabled(false);
		this.setWritingActivity(writingActivity);
	}
	
	
	public void setOrganizationId(Long organization){
		this.organizationId = organizationId;
	}
	
	private void disabledFields(){
		if (writingActivity != null && writingActivity.getId() != null) {
			switch (writingActivity.getStatus()) {
				case WritingActivity.STATUS_START:
					disabledStatusStart();
					break;
				case WritingActivity.STATUS_FINISH:
					disabledStatusFinish();
					break;
				default:
					disabledStatusDefault();
					break;
			}
		}
	}
	
	private void disabledStatusStart(){
		name.setEnabled(false);
		documentType.setEnabled(false);
		genre.setEnabled(false);
		documentTemplate.setEnabled(false);
		groups.setEnabled(true);
		startDate.setEnabled(false);
		
		// Get minimum deadline date
		Date date = null;
		int iRow = 0;
		for(int row=1; row<deadlineTable.getRowCount(); row++) {
			for(int col=0;col<5;col++){
				Widget widget = deadlineTable.getWidget(row, col);
				if  ( widget instanceof DateBox){
					DateBox dateBox = (DateBox)widget;
					Date deadlineDate = dateBox.getValue();
					if (date == null){
						date = deadlineDate;
						iRow = row;
					} else if (deadlineDate != null && deadlineDate.before(date)){
						date = deadlineDate;
						iRow = row;
					}
				}
			}
		}
		// disable the minimum deadlines row  but allow add new ones or modify the others
		if (date != null){
			for(int col=0;col<5;col++){
				Widget widget = deadlineTable.getWidget(iRow, col);
				if (widget instanceof FocusWidget){
					FocusWidget focusWidget = (FocusWidget) widget;
					focusWidget.setEnabled(false);
				} else if ( widget instanceof DateBox){
					DateBox dateBox = (DateBox)widget;
					dateBox.setEnabled(true);
				}
			}
		}
	}
	
	private void disabledStatusFinish(){
		name.setEnabled(false);
		documentType.setEnabled(false);
		genre.setEnabled(false);
		documentTemplate.setEnabled(false);
		groups.setEnabled(false);
		startDate.setEnabled(false);
		tutorialList.setEnabled(false);
		emailStudents.setEnabled(false);
		showStats.setEnabled(false);
		allowSubmit.setEnabled(false);
		trackReviews.setEnabled(false);
		addDeadline.setEnabled(false);
		for(int row=1; row<deadlineTable.getRowCount(); row++) {
			for(int col=0;col<5;col++){
				Widget widget = deadlineTable.getWidget(row, col);
				if (widget instanceof FocusWidget){
					FocusWidget focusWidget = (FocusWidget) widget;
					focusWidget.setEnabled(false);
				} else if ( widget instanceof DateBox){
					DateBox dateBox = (DateBox)widget;
					dateBox.setEnabled(false);
				} else if (widget instanceof Button){
					Button button = (Button) widget;
					button.setEnabled(false);
				}
			}
		}
	}
	
	private void disabledStatusDefault(){
		name.setEnabled(true);
		documentType.setEnabled(true);
		genre.setEnabled(true);
		documentTemplate.setEnabled(true);
		groups.setEnabled(true);
		startDate.setEnabled(true);
		tutorialList.setEnabled(true);
		emailStudents.setEnabled(true);
		showStats.setEnabled(true);
		allowSubmit.setEnabled(true);
		trackReviews.setEnabled(true);
	}
}
