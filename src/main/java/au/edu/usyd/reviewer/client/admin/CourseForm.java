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

/** 
 * <p>Main form for the creation and editing of "Courses". The information collected in this form is:<p>
 * 
 * <ul>
 *	<li><p><b>Course details:</b> This details include Name, Semester, Year and Tutorials: (e.g. mon, wed).</p> </li>
 *
 *	<li><p><b>Users:</b> Lecturers, Tutors, Supervisors and Automatic Reviewers. 
 *						 The information needed is: Id, First name, Last name and Email. It has to be entered comma separated.</p>
 *	</li>
 * </ul>
 * 
 * 
 */
public class CourseForm extends Composite {

	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/** TextArea with the course lecturers. */
	private final TextArea courseLecturers = WidgetFactory.createNewTextAreaWithId("courseFormLecturers");
	
	/** TextArea with the course tutors. */
	private final TextArea courseTutors = WidgetFactory.createNewTextAreaWithId("courseFormTutors");
	
//	/** TextArea with the course supervisors. */
//	private final TextArea courseSupervisors = new TextArea();
	
//	/** TextArea with the course automatic reviewers. */
//	private final TextArea courseAutomaticReviewers = new TextArea();
	
	/** TextArea with the course name. */
	private final TextBox courseName = WidgetFactory.createNewTextBoxWithId("courseFormName");
	
	/** ValueSpinner with the course year. */
	private final ValueSpinner courseYear = WidgetFactory.createNewValueSpinnerWithId(2013, 2009, 3000, "courseFormYear");
	
	/** ListBox with the  course semester. */
	private final ListBox courseSemester = WidgetFactory.createNewListBoxWithId("courseFormSemester");
	
	/** TextArea with the  course tutorials. */
	private final TextArea courseTutorials = WidgetFactory.createNewTextAreaWithId("courseTutorials");
	
	/** SimplePanel to include a link to the course spreadsheet and label with number of students. */
	private final SimplePanel courseSpreadsheet = new SimplePanel();
	
	/** The course. */
	private Course course = new Course();

	/**
	 * Instantiates a new course form.
	 */
	public CourseForm() {
		initWidget(mainPanel);
		courseSemester.addItem("1", "1");
		courseSemester.addItem("2", "2");
	}

	/**
	 * Gets the course with the values extracted from the form components.
	 *
	 * @return the course
	 */
	public Course getCourse() {
		course.setName(courseName.getText());
		course.setSemester(Integer.valueOf(courseSemester.getItemText(courseSemester.getSelectedIndex())));
		course.setYear((int) courseYear.getSpinner().getValue());
		course.setTutorials(StringUtil.csvToStrings(courseTutorials.getText()));
		course.setLecturers(StringUtil.csvToUsers(courseLecturers.getText()));
		course.setTutors(StringUtil.csvToUsers(courseTutors.getText()));
//		course.setSupervisors(StringUtil.csvToUsers(courseSupervisors.getText()));
//		course.setAutomaticReviewers(StringUtil.csvToUsers(courseAutomaticReviewers.getText()));
		return course;
	}

	/** 
	 * Method that places the components in the form.
	 */
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

		Grid usersGrid = new Grid(3, 2);
		usersGrid.setWidget(0, 0, new HTML("Lecturers:<br/>(id, fname, lname, email)"));
		usersGrid.setWidget(0, 1, courseLecturers);
		usersGrid.setWidget(1, 0, new HTML("Tutors:<br/>(id, fname, lname, email)"));
		usersGrid.setWidget(1, 1, courseTutors);
//		usersGrid.setWidget(2, 0, new HTML("Supervisors:<br/>(id, fname, lname, email)"));
//		usersGrid.setWidget(2, 1, courseSupervisors);
//		usersGrid.setWidget(2, 0, new HTML("Automatic Reviewers:<br/>(id, fname, lname, email)"));
//		usersGrid.setWidget(2, 1, courseAutomaticReviewers);		
//		usersGrid.setWidget(2, 0, new Label("Students:"));
		usersGrid.setWidget(2, 1, courseSpreadsheet);

		mainPanel.add(grid);
		mainPanel.add(new HTML("<br/><b>Users</b>"));
		mainPanel.add(usersGrid);
		mainPanel.add(new HTML("<br/>"));
	}

	
	/**
	 * Sets the course values in the form according to the received course object.
	 *
	 * @param course the new course
	 */
	public void setCourse(Course course) {
		this.course = course;
		courseName.setText(course.getName());
		courseName.setEnabled(false);
		courseSemester.setSelectedIndex(course.getSemester() - 1);
		courseYear.getSpinner().setValue(course.getYear(), true);
		courseTutorials.setText(StringUtil.stringsToCsv(course.getTutorials()));
		courseLecturers.setText(StringUtil.usersToCsv(course.getLecturers()));
		courseTutors.setText(StringUtil.usersToCsv(course.getTutors()));
//		courseSupervisors.setText(StringUtil.usersToCsv(course.getSupervisors()));
//		courseAutomaticReviewers.setText(StringUtil.usersToCsv(course.getAutomaticReviewers()));

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
