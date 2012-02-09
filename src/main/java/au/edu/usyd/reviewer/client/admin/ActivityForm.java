package au.edu.usyd.reviewer.client.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.edu.usyd.reviewer.client.admin.glosser.SiteForm;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class ActivityForm extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private WritingActivity writingActivity;
	private Course course = null;
	private Collection<Course> courses;
	private final CheckBox emailStudents = new CheckBox();
	private final CheckBox showStats = new CheckBox();
	private final ListBox courseList = new ListBox();
	private final ListBox status = new ListBox();
	private final ListBox glosserList = new ListBox();
	private final ListBox tutorialList = new ListBox();
	private final TextBox name = new TextBox();
	private final CheckBox groups = new CheckBox();
	private final ListBox documentType = new ListBox();
	private final ListBox documentTemplate = new ListBox();
	private final DateBox startDate = new DateBox();
	private final FlexTable deadlineTable = new FlexTable();
	private final FlexTable reviewTable = new FlexTable();
	private final CheckBox allowSubmit = new CheckBox();
	private final CheckBox trackReviews = new CheckBox();
	private final ListBox genre = new ListBox();
	private List<TextBox> deadLineTextBoxList = new ArrayList<TextBox>();

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
				for(int i=1; i<deadlineTable.getRowCount(); i++) {
					if(remove.equals(deadlineTable.getWidget(i, 4))) {
						writingActivity.getDeadlines().remove(i-1);
						deadlineTable.removeRow(i);
						deadLineTextBoxList.remove(i-1);
					}	
				}
			}});
		int row = deadlineTable.getRowCount();
		deadlineTable.setWidget(row, 0, status);
		deadlineTable.setWidget(row, 1, name);
		deadlineTable.setWidget(row, 2, maxGrade);
		deadlineTable.setWidget(row, 3, deadlineDate);
		deadlineTable.setWidget(row, 4, remove);
		
		deadLineTextBoxList.add(name);
	}

	protected void addReviewingActivity(ReviewingActivity reviewingActivity) {
		ActivityReviewForm reviewForm = new ActivityReviewForm();	
		List<String> deadLineNameList = new ArrayList<String>();
		
		for (int i = 0; i < deadLineTextBoxList.size(); i++) {
			deadLineNameList.add(deadLineTextBoxList.get(i).getValue());	
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
	}

	public Course getCourse() {
		return course;
	}

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
		emailStudentsPanel.add(new Label(" send email notifications to students."));

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
	

		HorizontalPanel showStatsPanel = new HorizontalPanel();
		showStatsPanel.add(showStats);
		showStatsPanel.add(new Label(" show writing statistics to students."));

		Grid feedbackGrid = new Grid(2, 2);
		feedbackGrid.setWidget(0, 0, new Label("Glosser:"));
		feedbackGrid.setWidget(0, 1, glosserList);
		feedbackGrid.setWidget(1, 0, new Label("MyStats:"));
		feedbackGrid.setWidget(1, 1, showStatsPanel);

		HorizontalPanel groupsPanel = new HorizontalPanel();
		groupsPanel.add(groups);
		groupsPanel.add(new Label(" create group documents."));
		
		Button addDeadline = new Button("Add Deadline");
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
	}

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
	}
	
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
				break;
			}
		}
	}

	public void setGlosserSites(List<SiteForm> glosserSites) {
		glosserList.clear();
		glosserList.addItem("none", String.valueOf(WritingActivity.GLOSSER_SITE_NONE));
		if (glosserSites.size() > 0) {
			for (SiteForm glosserSite : glosserSites) {
				glosserList.addItem(glosserSite.getName(), String.valueOf(glosserSite.getId()));
			}
		}
	}

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

	public void setWritingActivityAndCourse(WritingActivity writingActivity, Course course) {
		this.setCourse(course);
		courseList.clear();
		courseList.addItem(course.getName(), String.valueOf(course.getId()));
		courseList.setEnabled(false);
		tutorialList.setEnabled(false);
		this.setWritingActivity(writingActivity);
	}
}
