package au.edu.usyd.reviewer.client.admin;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.gwt.DocEntryWidget;
import au.edu.usyd.reviewer.client.core.gwt.WidgetFactory;
import au.edu.usyd.reviewer.client.core.util.StringUtil;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.client.ValueSpinner;

public class CourseForm extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private final TextArea courseLecturers = WidgetFactory.createNewTextAreaWithId("courseFormLecturers");
	private final TextArea courseTutors = WidgetFactory.createNewTextAreaWithId("courseFormTutors");
	private final TextArea supervisors = new TextArea();
	private final TextArea automaticReviewers = new TextArea();
	private final TextBox courseName = WidgetFactory.createNewTextBoxWithId("courseFormName");
	private final ValueSpinner courseYear = WidgetFactory.createNewValueSpinnerWithId(2010, 2009, 3000, "courseFormYear");
	private final ListBox courseSemester = WidgetFactory.createNewListBoxWithId("courseFormSemester");
	private final TextArea courseTutorials = WidgetFactory.createNewTextAreaWithId("courseTutorials");
	private final SimplePanel courseSpreadsheet = new SimplePanel();
	private Course course = new Course();

	public CourseForm() {
		initWidget(mainPanel);
		courseSemester.addItem("1", "1");
		courseSemester.addItem("2", "2");
	}

	public Course getCourse() {
		course.setName(courseName.getText());
		course.setSemester(Integer.valueOf(courseSemester.getItemText(courseSemester.getSelectedIndex())));
		course.setYear((int) courseYear.getSpinner().getValue());
		course.setTutorials(StringUtil.csvToStrings(courseTutorials.getText()));
		course.setLecturers(StringUtil.csvToUsers(courseLecturers.getText()));
		course.setTutors(StringUtil.csvToUsers(courseTutors.getText()));
		course.setSupervisors(StringUtil.csvToUsers(supervisors.getText()));
		course.setAutomaticReviewers(StringUtil.csvToUsers(automaticReviewers.getText()));
		return course;
	}

	@Override
	public void onLoad() {
		Grid grid = new Grid(4, 2);
		grid.setWidget(0, 0, new Label("Name:"));
		grid.setWidget(0, 1, courseName);
		grid.setWidget(1, 0, new Label("Semester:"));
		grid.setWidget(1, 1, courseSemester);
		grid.setWidget(2, 0, new Label("Year:"));
		grid.setWidget(2, 1, courseYear);
		grid.setWidget(3, 0, new Label("Tutorials: (e.g. mon, wed)"));
		grid.setWidget(3, 1, courseTutorials);

		Grid usersGrid = new Grid(5, 2);
		usersGrid.setWidget(0, 0, new HTML("Lecturers:<br/>(id, fname, lname, email)"));
		usersGrid.setWidget(0, 1, courseLecturers);
		usersGrid.setWidget(1, 0, new HTML("Tutors:<br/>(id, fname, lname, email)"));
		usersGrid.setWidget(1, 1, courseTutors);
		usersGrid.setWidget(2, 0, new HTML("Supervisors:<br/>(id, fname, lname, email)"));
		usersGrid.setWidget(2, 1, supervisors);
		usersGrid.setWidget(3, 0, new HTML("Automatic Reviewers:<br/>(id, fname, lname, email)"));
		usersGrid.setWidget(3, 1, automaticReviewers);		
		usersGrid.setWidget(4, 0, new Label("Students:"));
		usersGrid.setWidget(4, 1, courseSpreadsheet);

		mainPanel.add(grid);
		mainPanel.add(new HTML("<br/><b>Users</b>"));
		mainPanel.add(usersGrid);
		mainPanel.add(new HTML("<br/>"));
	}

	public void setCourse(Course course) {
		this.course = course;
		courseName.setText(course.getName());
		courseName.setEnabled(false);
		courseSemester.setSelectedIndex(course.getSemester() - 1);
		courseYear.getSpinner().setValue(course.getYear(), true);
		courseTutorials.setText(StringUtil.stringsToCsv(course.getTutorials()));
		courseLecturers.setText(StringUtil.usersToCsv(course.getLecturers()));
		courseTutors.setText(StringUtil.usersToCsv(course.getTutors()));
		supervisors.setText(StringUtil.usersToCsv(course.getSupervisors()));
		automaticReviewers.setText(StringUtil.usersToCsv(course.getAutomaticReviewers()));

		// students panel
		HorizontalPanel studentsPanel = new HorizontalPanel();
		studentsPanel.add(new DocEntryWidget(course.getSpreadsheetId(), "Students",course.getDomainName(), false));
		int students = 0;
		for (UserGroup studentGroup : course.getStudentGroups()) {
			students += studentGroup.getUsers().size();
		}
		studentsPanel.add(new HTML("(" + students + ")"));
		courseSpreadsheet.setWidget(studentsPanel);
	}
}
