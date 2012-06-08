package au.edu.usyd.reviewer.client.assignment;

import java.util.ArrayList;
import java.util.Collection;


import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.gwt.WidgetFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TabPanel;

import au.edu.usyd.reviewer.client.admin.UserForm;
import com.google.gwt.user.client.ui.DialogBox;

public class AssignmentEntryPoint implements EntryPoint {

	private AssignmentServiceAsync assignmentService = (AssignmentServiceAsync) GWT.create(AssignmentService.class);
	private VerticalPanel mainPanel = new VerticalPanel();
	private String panelWidth = "900px";
	private String cssH1Style = "STYLE='color: #CE1126; clear: both; margin: 0 0 0 0; font-weight: normal; clear: left; font-size: 1.3em;'";
	private String cssTextStyle = "STYLE='font: normal 13px Arial,Helvetica,sans-serif; color: #333;'";
	private HorizontalPanel yearSemesterPanel = new HorizontalPanel();
	private ListBox courseSemester = WidgetFactory.createNewListBoxWithId("courseSemester");	
	private ListBox courseYear = WidgetFactory.createNewListBoxWithId("courseYear");
	private CheckBox includeFinishedReviews = new CheckBox();

	@Override
	public void onModuleLoad() {
		
		//Assignment pages header
		mainPanel.add(new HTML("<h1 "+cssH1Style +" >ASSIGNMENTS LIST</h1></br>"));
		mainPanel.add(new HTML("<p "+cssTextStyle +" >This section of the website provides an environment for students and academics to manage their written assignments, and reviews. The assignment</br> submission system is based on Google Docs. How does the assignment submission system work? Visit our Help page to learn more. If you have trouble, </br>please see the Troubleshooting Guide on the Help page for solutions to common problems or contact <a href='mailto:i.write@sydney.edu.au'>i.write@sydney.edu.au</a> for futher support.</p></br>"));
		
		//Tomcat login, check if current user is not a WASM user
		final FlexTable userDetailsFlexTable = new FlexTable(); 
		
		final Button userDetailsButton = new Button("Change password");
		
		assignmentService.getUserDetails( new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
				userDetailsButton.setEnabled(false);
			}

			@Override
			public void onSuccess(final User user) {
				//Window.alert("You are now logged in as '" + user.getId() + "'");
				if (!user.getWasmuser()){
					
					userDetailsFlexTable.clear();
					userDetailsFlexTable.setWidth("60%");
					mainPanel.add(userDetailsFlexTable);
					mainPanel.add(new HTML("</br>"));					
					
					final TabPanel userDetailsPanel = new TabPanel();
					userDetailsFlexTable.setHTML(0, 0, "<p "+cssTextStyle +" >If you need to change your password, please click here: </p>");							
					userDetailsFlexTable.setWidget(0, 1, userDetailsButton);
					mainPanel.add(new HTML("</br>"));
					
					userDetailsButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							final DialogBox dialogBox = new DialogBox();
							final UserForm userForm = new UserForm();
							userForm.setUser(user);
							userForm.disableNotUpdatableFileds();
							HorizontalPanel buttonsPanel = new HorizontalPanel();
							buttonsPanel.setWidth("100%");							
							final Button updateDetailsButton = new Button("Update");
							
							updateDetailsButton.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									//updateDetailsButton.setEnabled(false);
									if (userForm.validateCurrentPassword() && userForm.validateNewPassword()){
										assignmentService.updateUserPassword(userForm.getUser(), userForm.getNewPassword(), new AsyncCallback<User>() {
											@Override
											public void onFailure(Throwable caught) {
												Window.alert("Failed to update password, please verify your current password.");
												//updateDetailsButton.setEnabled(true);
											}
	
											@Override
											public void onSuccess(User user) {
												Window.alert("Your password has been changed.");
												dialogBox.hide();
											}
										});
									}
								}
							});							

							buttonsPanel.add(updateDetailsButton);
							buttonsPanel.add(new Button("Close", new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									dialogBox.hide();
								}
							}));
					
							VerticalPanel panel = new VerticalPanel();
							panel.add(userForm);
							panel.add(buttonsPanel);
							dialogBox.setHTML("<b>User</b>");
							dialogBox.setWidget(panel);
							dialogBox.center();
							dialogBox.show();
						}
					});					
					
				}				
			}
		});		
		
		courseSemester.addItem("1", "1");
		courseSemester.addItem("2", "2");
		
		courseYear.addItem("2012", "2012");
		courseYear.addItem("2011", "2011");
		courseYear.addItem("2010", "2010");
		courseYear.addItem("2009", "2009");
		
		//Checkbox to include reviewing tasks
		includeFinishedReviews.setText("Show finished reviewing activities");
		
		
		// assignments panel
		final WritingTasks writingTasks = new WritingTasks(assignmentService);
		final TabPanel documentsPanel = new TabPanel();
		documentsPanel.add(writingTasks, "Writing Tasks");
		documentsPanel.setWidth(panelWidth);
		documentsPanel.selectTab(0);	
		
		// reviews panel
		final TabPanel reviewsPanel = new TabPanel();
		final ReviewingTasks reviewingTasks = new ReviewingTasks();
		reviewsPanel.add(reviewingTasks, "Reviewing Tasks");
		reviewsPanel.setWidth(panelWidth);
		reviewsPanel.selectTab(0);
		
		// activities panel
		final TabPanel activitiesPanel = new TabPanel();
		final InstructorPanel instructorPanel = new InstructorPanel(assignmentService);
		activitiesPanel.add(instructorPanel, "Instructor Panel");
		activitiesPanel.setWidth(panelWidth);
		activitiesPanel.selectTab(0);
		final HTML htmlAdminLink = new HTML("<br/><p "+cssTextStyle +" >As an Administrator user of the iWrite application, you can go to the Admin page and set up Writing Activities and Reviews. <a href='Admin.html'>Admin Page</a> </p></br>");
		
		final SubmitButton refreshPanelButton = new SubmitButton("Load activities", "Loading activities, please wait...", "Load");
        
	    refreshPanelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Integer semester = Integer.valueOf(courseSemester.getItemText(courseSemester.getSelectedIndex()));
				Integer year = Integer.valueOf(courseYear.getItemText(courseYear.getSelectedIndex()));
				
				/****************************************************************************/
				// assignments panel				
				refreshPanelButton.updateStateSubmitting();
				writingTasks.setLoadingMessage();
				assignmentService.getUserWritingTasks(semester, year, new AsyncCallback<Collection<Course>>() {
					@Override
					public void onFailure(Throwable caught) {
						// Window.alert("Failed to get documents. ");
						writingTasks.setTableEntries(new ArrayList<Course>());
						refreshPanelButton.updateStateSubmit();
					}

					@Override
					public void onSuccess(Collection<Course> courses) {
							writingTasks.setTableEntries(courses);
							refreshPanelButton.updateStateSubmit();
					}
				});
				/****************************************************************************/
				
				/****************************************************************************/
				// reviews panel
				//mainPanel.remove(reviewsPanel);
				refreshPanelButton.updateStateSubmitting();
				reviewingTasks.setLoadingMessage();
				assignmentService.getUserReviewingTasks(semester, year, includeFinishedReviews.getValue(), new AsyncCallback<Collection<Course>>() {
					@Override
					public void onFailure(Throwable caught) {
						 //Window.alert("Failed to get reviews. ");
						reviewingTasks.setTableEntries(new ArrayList<Course>());
						refreshPanelButton.updateStateSubmit();
					}

					@Override
					public void onSuccess(Collection<Course> courses) {
							reviewingTasks.setTableEntries(courses);
							refreshPanelButton.updateStateSubmit();
					}
				});						
				/****************************************************************************/
				
				/****************************************************************************/
				mainPanel.remove(htmlAdminLink);
				mainPanel.remove(activitiesPanel);
				assignmentService.getUserActivities(semester, year,new AsyncCallback<Collection<Course>>() {
					@Override
					public void onFailure(Throwable caught) {
						// Window.alert("Failed to get courses. ");
					}

					@Override
					public void onSuccess(Collection<Course> courses) {
						if (!courses.isEmpty()){
							instructorPanel.setTableEntries(courses);
							mainPanel.add(htmlAdminLink);
							mainPanel.add(activitiesPanel);	
						}						
					}
				});				
				
				/****************************************************************************/
			}
		});				
	    
	    FlexTable filterActivitiesGrid = new FlexTable();
	    filterActivitiesGrid.setWidget(0, 0, new Label("Semester-Year:"));
		filterActivitiesGrid.setWidget(0, 1, courseSemester);
		filterActivitiesGrid.setWidget(0, 2, courseYear);
		filterActivitiesGrid.setWidget(0, 3, refreshPanelButton);
		filterActivitiesGrid.setWidget(1, 0, includeFinishedReviews);
		filterActivitiesGrid.getFlexCellFormatter().setColSpan(1, 0, 3);
		filterActivitiesGrid.getFlexCellFormatter().setRowSpan(0, 3, 2);
		
		filterActivitiesGrid.getRowFormatter().setStyleName(0, "centerFilterTable");
		filterActivitiesGrid.getRowFormatter().setStyleName(1, "centerFilterTable");
		filterActivitiesGrid.getCellFormatter().setWidth(0, 3, "150px");

	    
	    //mainPanel.add(yearSemesterPanel);
		mainPanel.add(filterActivitiesGrid);
	    mainPanel.add(new HTML("</br>"));
		
	    // assignments panel
	    mainPanel.add(documentsPanel);

		// reviews panel
		mainPanel.add(new HTML("<br/>"));
		mainPanel.add(reviewsPanel);		

		// activities panel	
		RootPanel.get("mainPanel").add(mainPanel);
		refreshPanelButton.click();
	}
}
