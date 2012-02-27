package au.edu.usyd.reviewer.client.assignment;

import java.util.Collection;
import java.util.Date;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.gwt.ReviewWidget;
import au.edu.usyd.reviewer.client.core.util.StyleLib;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReviewingTasks extends Composite {

	private FlexTable reviewFlexTable = new FlexTable();
	private VerticalPanel mainPanel = new VerticalPanel();

	public ReviewingTasks() {
		initWidget(mainPanel);
	}

	private void addReviewsToTable(final Course course) {
		int row = reviewFlexTable.getRowCount();
		reviewFlexTable.setHTML(row, 0, "<b>"+course.getName()+"</b>");
		reviewFlexTable.setHTML(row, 4, "");
		//reviewFlexTable.getRowFormatter().setStyleName(row, "documentsTableRow");
		reviewFlexTable.getRowFormatter().setStyleName(row, "documentsTableRowHeader");
		row = reviewFlexTable.getRowCount();

		for (final WritingActivity writingActivity : course.getWritingActivities()) {
			for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
				if (reviewingActivity !=null) {
					Date dateTime=new Date();
					//String style = StyleLib.dueDateStyle(reviewingActivity.getStatus(), Activity.STATUS_FINISH);
					String style = StyleLib.dueDateStyle(dateTime,reviewingActivity.getFinishDate());
					for (final ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
						// due date
						HTML dueDate = new HTML("<div style='" + style + "'>" + StyleLib.dueDateFormat(reviewingActivity.getFinishDate()) + "</div>");
		
						// review link
						ReviewWidget reviewLink = new ReviewWidget(reviewEntry.getReview(), reviewEntry.getDocEntry().getTitle(), true);
						
						reviewFlexTable.getRowFormatter().setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
						reviewFlexTable.getRowFormatter().setStyleName(row, "documentsTableRow");
						reviewFlexTable.setWidget(row, 1, reviewLink);
						reviewFlexTable.setWidget(row, 2, dueDate);
						row = reviewFlexTable.getRowCount();
					}
				}
			}
		}
	}

	@Override
	public void onLoad() {
		reviewFlexTable.setWidth("100%");
		reviewFlexTable.setStyleName("documentsTable");
		mainPanel.add(reviewFlexTable);
	}

	public void setTableEntries(Collection<Course> courses) {
		if (courses.size() > 0){
			reviewFlexTable.clear();
			reviewFlexTable.removeAllRows();
			reviewFlexTable.setWidth("100%");
			reviewFlexTable.setHTML(0, 0, "<b>Course</b>");
			reviewFlexTable.setHTML(0, 1, "<b>Document to review</b>");
			reviewFlexTable.setHTML(0, 2, "<b>Due date</b>");
			reviewFlexTable.getRowFormatter().setStyleName(0, "documentsTableHeader");

			for (Course course : courses) {
				if (course.getWritingActivities().size() > 0) {
					addReviewsToTable(course);
				}
			}
		}else{
			reviewFlexTable.clear();
			reviewFlexTable.removeAllRows();
			reviewFlexTable.setWidth("100%");
			reviewFlexTable.setHTML(0, 0, "<b>There are no Reviewing Tasks for the selected semester-year</b>");		
		}				
		

	}

	public void setLoadingMessage() {
		reviewFlexTable.clear();
		reviewFlexTable.removeAllRows();
		reviewFlexTable.setWidth("100%");
		reviewFlexTable.setHTML(0, 0, "<b>Loading...</b>");
	}
}
