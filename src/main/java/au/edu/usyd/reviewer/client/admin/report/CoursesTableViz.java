package au.edu.usyd.reviewer.client.admin.report;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

public class CoursesTableViz extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private DataTable data;
	private Collection<Course> courses;

	public CoursesTableViz(Collection<Course> courses) {
		this.courses = courses;
		initWidget(mainPanel);
	}

	private Options createOptions() {
		Options options = Options.create();
		return options;
	}
	
	private AbstractDataTable createTable(Collection<Course> courses) {
		data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Course");
		data.addColumn(ColumnType.NUMBER, "Year");
		data.addColumn(ColumnType.NUMBER, "Semester");
		data.addColumn(ColumnType.STRING, "Activity");
		data.addColumn(ColumnType.NUMBER, "Documents");
		for (Course course : courses) {
			for (WritingActivity writingActivity : course.getWritingActivities()) {
				data.addRows(1);
				data.setValue(data.getNumberOfRows() - 1, 0, course.getName());
				data.setValue(data.getNumberOfRows() - 1, 1, course.getYear());
				data.setValue(data.getNumberOfRows() - 1, 2, course.getSemester());
				data.setValue(data.getNumberOfRows() - 1, 3, writingActivity.getName() + " (" + writingActivity.getTutorial() + ")");
				data.setValue(data.getNumberOfRows() - 1, 4, writingActivity.getEntries().size());
			}
		}
		return data;
	}

	@Override
	public void onLoad() {
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			@Override
			public void run() {
				Table chart = new Table(createTable(courses), createOptions());
				mainPanel.add(chart);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);

	}
}