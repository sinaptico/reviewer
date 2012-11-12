package au.edu.usyd.reviewer.client.admin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.google.gwt.user.client.ui.Button;  


/**
 * The Class GradeBook manages the grades given to the selected writing activity. 
 * The details include the grades for each defined deadline of the writing activity.
 */
public class GradeBook extends Composite {

	/** Asynchronous admin service for model management. */
	private final AdminServiceAsync adminService;
	
	/** ListGrid with the grades. */
	private final ListGrid gradesGrid = new ListGrid();
	
	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();

	/**
	 * Instantiates a new grade book.
	 *
	 * @param adminService the admin service
	 */
	public GradeBook(AdminServiceAsync adminService) {
		this.adminService = adminService;
		initWidget(mainPanel);
	}
	
	/**
	 * Creates the user record that can be added to the grid.
	 *
	 * @param user the user
	 * @param grades the grades
	 * @return the record
	 */
	private Record createUserRecord(User user, Collection<Grade> grades) {
		Record record = new ListGridRecord();
		record.setAttribute("Unikey", user.getUsername());
		record.setAttribute("Firstname", user.getFirstname());
		record.setAttribute("Lastname", user.getLastname());
		for(Grade grade : grades) {			
			record.setAttribute(getColName(grade.getDeadline()), grade.getValue());
		}
		return record;
	}

	/**
	 * Gets the column name from the deadline received.
	 *
	 * @param deadline the deadline
	 * @return the column name
	 */
	private String getColName(Deadline deadline) {
		return deadline.getName() + " (" + deadline.getMaxGrade() + ")";
	}

	/**
	 * Gets the grades from the writing activity and user received.
	 *
	 * @param writingActivity the writing activity
	 * @param user the user
	 * @return the grades
	 */
	private Collection<Grade> getGrades(WritingActivity writingActivity, User user)  {
		List<Grade> grades = new LinkedList<Grade>();
		for(Grade grade : writingActivity.getGrades()) {
			if(grade != null && grade.getUser() != null && grade.getUser().equals(user)) {
				grades.add(grade);
			}
		}
		return grades;
	}

	/**
	 * Method that configures the grades grid and adds the "Download CSV" button. 
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onLoad() {
		gradesGrid.setWidth(730);
		gradesGrid.setHeight(610);
		gradesGrid.setAlternateRecordStyles(true);
		gradesGrid.setShowAllRecords(true);
		gradesGrid.setCellHeight(22);
		gradesGrid.setCanEdit(true);
		gradesGrid.setEditEvent(ListGridEditEvent.CLICK);

		//mainPanel.add(new HTML("<a href='data:text/csv,"+exportCSV(gradesGrid)+"'>CSV</a>"));
		String UPLOAD_ACTION_URL = "file";
		final FormPanel form = new FormPanel();
		form.setAction(UPLOAD_ACTION_URL);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addStyleName("table-center");
		form.addStyleName("demo-panel-padded");
		form.setWidth("275px");					

		final VerticalPanel holder = new VerticalPanel();
		
		Hidden param = new Hidden();
		param.setName("csv");					               
		param.setValue(exportCSV(gradesGrid));					
		holder.add(param);

		holder.add(new Button("Download CSV file", new ClickListener() {
			public void onClick(Widget sender) { form.submit();}
		}));
		holder.add(new HTML("<hr />"));
		form.add(holder);

		mainPanel.add(form);		
		mainPanel.add(gradesGrid);		
	}
	
	/**
	 * Returns a string in CSV format from the ListGrid received.
	 *
	 * @param listGrid the list grid
	 * @return the string
	 */
	private String exportCSV(ListGrid listGrid) {
			StringBuilder stringBuilder = new StringBuilder(); 
			
			// column names
			ListGridField[] fields = listGrid.getFields();
			for (int i = 0; i < fields.length; i++) {
				ListGridField listGridField = fields[i];
				stringBuilder.append("\"");
				stringBuilder.append(listGridField.getName());
				stringBuilder.append("\",");
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1); // remove last ","
			stringBuilder.append("\n");
			
			// column data
			ListGridRecord[] records = listGrid.getRecords();
			for (int i = 0; i < records.length; i++) {
				ListGridRecord listGridRecord = records[i];
				ListGridField[] listGridFields = listGrid.getFields();
				for (int j = 0; j < listGridFields.length; j++) {
					ListGridField listGridField = listGridFields[j];
					stringBuilder.append("\"");
					stringBuilder.append(listGridRecord.getAttribute(listGridField.getName()));
					stringBuilder.append("\",");
				}
				stringBuilder.deleteCharAt(stringBuilder.length() - 1); // remove last ","
				stringBuilder.append("\n");
			}
			
			return stringBuilder.toString();		
	}

    
	/**
	 * Sets the writing activity grades into the main grid.
	 *
	 * @param writingActivity the new writing activity
	 */
	public void setWritingActivity(WritingActivity writingActivity) {
		this.updateGradesTable(writingActivity.getDeadlines());
		for (DocEntry docEntry : writingActivity.getEntries()) {
			List<User> users = new LinkedList<User>();
			if (docEntry.getOwner() != null) {
				users.add(docEntry.getOwner());
			} else {
				users.addAll(docEntry.getOwnerGroup().getUsers());
			}
			for(User user : users) {
				Record record = createUserRecord(user, getGrades(writingActivity, user));
				gradesGrid.addData(record);
			}
		}
	}
	
	/**
	 * Update grades from the values in the main grid.
	 *
	 * @param deadlines the deadlines
	 */
	private void updateGradesTable(Collection<Deadline> deadlines) {
		gradesGrid.clear();
		List<ListGridField> fields = new LinkedList<ListGridField>();
		ListGridField unikeyField = new ListGridField("Unikey");
		unikeyField.setCanEdit(false);
		fields.add(unikeyField);
		ListGridField firstnameField = new ListGridField("Firstname");
		firstnameField.setCanEdit(false);
		fields.add(firstnameField);
		ListGridField lastnameField = new ListGridField("Lastname");
		lastnameField.setCanEdit(false);
		fields.add(lastnameField);
		for(final Deadline deadline : deadlines) {
			final String colName = getColName(deadline);
			ListGridField gradeField = new ListGridField(colName);
			gradeField.setType(ListGridFieldType.FLOAT);
			gradeField.addCellSavedHandler(new CellSavedHandler(){
				@Override
				public void onCellSaved(CellSavedEvent event) {					
					String userId = event.getRecord().getAttributeAsString("Unikey");
					Double gradeValue = event.getRecord().getAttributeAsDouble(colName);
					if(gradeValue < deadline.getMaxGrade()) {
						adminService.updateGrade(deadline, userId, gradeValue, new AsyncCallback<Grade>(){
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to update grade: " + caught.getMessage());
							}
							@Override
							public void onSuccess(Grade grade) {
								// TODO Auto-generated method stub
							}});
					} else {
						Window.alert("Grade must be less than " + deadline.getMaxGrade());
						event.getRecord().setAttribute(colName, event.getOldValue());
					}
				}});
			fields.add(gradeField);
		}
		gradesGrid.setFields(fields.toArray(new ListGridField[0]));
		
	}
}
