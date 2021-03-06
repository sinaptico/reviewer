package au.edu.usyd.reviewer.client.admin;


import java.util.Collection;


import java.util.Date;
import java.util.List;

import au.edu.usyd.reviewer.client.admin.glosser.GlosserService;
import au.edu.usyd.reviewer.client.admin.glosser.GlosserServiceAsync;
import au.edu.usyd.reviewer.client.admin.glosser.ShowSitesComposite;
import au.edu.usyd.reviewer.client.admin.glosser.SiteForm;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.gwt.WidgetFactory;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.CustomUncaughtExceptionHandler;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;

//TODO Documentation - include description of GlosserSite
//TODO Move CSS style to external files
//TODO Move out Glosser admin tab to GLOSSER project

/**
 * <p>Starting point of the "Admin" module. The options available in this form include:<p>
 * 
 * <ul>
 *	<li><p><b>Menus for Course, Activity and Review Template:</b></p>
 *		<ul>
 *			<li><b>Course: </b>This option of the menu allows users to create new courses in the system. 
 *				The details for the course creation are gathered by the form: 
 *				{@link CourseForm Course form}.
 *			</li>
 *			<li><b>Activity: </b>This option of the menu allows users to create new writing and reviewing activities.
 *								 The details for this activities are collected by the form:
 *								 {@link ActivityForm Activity Form}
 *			</li>
 *			<li><b>Review template: </b>This options allows users to create review templates.
 *										The details for this templates are collected by the form:
 *										{@link ReviewTemplateForm Review Template Form}
 *			</li>
 *		</ul>
 *	</li>
 *
 *	<li><p><b>Impersonate user: </b>This menu calls the form {@link UserForm User Form} and uses it to collect an ID that is then used to mock an User.</p></li>
 *	
 *  </li>
 * </ul> 
 */
public class AdminEntryPoint implements EntryPoint {

	
	/** Asynchronous admin service for model management. */
	private final static AdminServiceAsync adminService = (AdminServiceAsync) GWT.create(AdminService.class);
	
	/** Asynchronous Glosser service for Glosser model management. */
	private final GlosserServiceAsync glosserService = GWT.create(GlosserService.class);
	
	/** VerticalPanel used in the assignments tab option. */
	private VerticalPanel assignmentsPanel = new VerticalPanel();
	
	/** Tree with the courses and their writing activities. */
	private Tree coursesTree = new Tree();
	
	/** Collection with the courses recored in the system. */
	private Collection<Course> courses;
	
	/** List of glosser sites recorded in the system. */
	private List<SiteForm> glosserSites;
	
	/** Simple panel used to load the information related to the selected node on the tree. */
	private SimplePanel nodePanel = new SimplePanel();
	
	/** The course stack panel. */
	private VerticalPanel courseStackPanel = new VerticalPanel();
	
	/** The grade book panel. */
	private SimplePanel gradeBookPanel = new SimplePanel();
	
	/** The reports panel. */
	private SimplePanel reportsPanel = new SimplePanel();
	
	/** Label used to show information about the selected course - activity from the courses tree. */
	private HTML activityLabel = new HTML("<b>&nbsp;</b>");
	
	/** The css h1 style. */
	private String cssH1Style = "STYLE='text-align: left; color: #CE1126; clear: both; margin: 0 0 0 0; font-weight: normal; clear: left; font-size: 1.3em;'";
	
	/** The css div style. */
	private String cssDivStyle = "align='left' STYLE='margin: 0 0 0 0;'";
	
	/** The review template tree. */
	private Tree reviewTemplateTree = new Tree();
	
	/** The review templates content panel. */
	private VerticalPanel reviewTemplatesContentPanel = new VerticalPanel();
	
	/** Panel for the main filter. */
	private HorizontalPanel yearSemesterPanel = new HorizontalPanel();
	
	/** The course's semester included in the filter. */
	private ListBox courseSemester = WidgetFactory.createNewListBoxWithId("courseSemester");	
	
	/** The course's year included in the filter. */
	private ListBox courseYear = WidgetFactory.createNewListBoxWithId("courseYear");
	
	/** The refresh filter button. */
	private SubmitButton refreshCourseTreeButton = new SubmitButton("Load", "Loading courses, please wait...", "Load");
	
	final ActivityForm activityForm = new ActivityForm();
	
	
	/** The course's year included in the filter. */
	private ListBox organizationsList = WidgetFactory.createNewListBoxWithId("organizationsList");

	/** panel for the organizations drop dwon list **/
	private VerticalPanel organizationsPanel = new VerticalPanel();
	
	/** logged user **/ 
	private User loggedUser = null;
	
	/** panel for the logged user information **/
	private VerticalPanel headerPanel = new VerticalPanel();
		
	private boolean toolsLoaded = false;
	
	private Command logoutCommand;
	
	private MenuItem logoutItem;
	
	private FlexTable headerTable = new FlexTable();
	
	
	/** 
	 * <p>Main method of the entry point that loads the "Glosser sites" and menus for user impersonation, courses, activities and review 
	 * templates creation. It also loads the panels and trees with the course and review templates lists according to the defined filter (Year - Semester).</p>
	 * 
	 */
	@Override
	public void onModuleLoad() {
		// uncaught exception handler
		GWT.setUncaughtExceptionHandler( new CustomUncaughtExceptionHandler() );
		
					
		// get logged user to show his/her name and organization in the page
		if (loggedUser == null){
			// get the logged user to obtain his/her organization
			adminService.getLoggedUser(new AsyncCallback<User>(){
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();					
					if (Window.Location.getHostName().contains("usyd") || 
						Window.Location.getHostName().contains("unsw") ||
						Window.Location.getHostName().contains("uws")){
							Window.Location.reload();
					} else {
						Window.alert("Failed to get the logged user.\nPlease close the browser and try again");
					}
				}
	
				@Override
				public void onSuccess(User user) {
					if (user != null) {
						
						// Glosser sites panel
						final ShowSitesComposite glosserPanel = new ShowSitesComposite();
						glosserService.getAllSites(new AsyncCallback<List<SiteForm>>() {
							@Override
							public void onFailure(Throwable caught) {
								if (loggedUser != null){
									if (caught instanceof MessageException){
										processMessageException((MessageException) caught);
									} else {
										Window.alert("Failed to get Glosser sites: " + caught.getMessage());
									}
								}
							}

							@Override
							public void onSuccess(final List<SiteForm> sites) {
								glosserSites = sites;
								glosserService.getToolList(new AsyncCallback<List<String>>() {
									@Override
									public void onFailure(Throwable caught) {
										if (caught instanceof MessageException){
											processMessageException((MessageException) caught);
										} else {
											Window.alert("Failed to get Glosser tools list: " + caught.getMessage());
										}
									}

									@Override
									public void onSuccess(final List<String> tools) {
										glosserPanel.setSitesAndTools(sites, tools);
									}
								});
							}
						});

						final TabLayoutPanel tabs = new TabLayoutPanel(25, Unit.PX);
						tabs.add(new ScrollPanel(assignmentsPanel), "Assignments");
						tabs.add(new ScrollPanel(gradeBookPanel), "GradeBook");
						tabs.add(new ScrollPanel(reportsPanel), "Reports");
						tabs.add(new ScrollPanel(glosserPanel), "Glosser");
						tabs.add(new ScrollPanel(reviewTemplatesContentPanel), "Review Templates");
						
						// semesters 
						courseSemester.addItem("1", "1");
						courseSemester.addItem("2", "2");
						Date today = new Date();
						int month = today.getMonth();
						if (month < 6) {
							courseSemester.setSelectedIndex(0);
						} else {
							courseSemester.setSelectedIndex(1);
						}	
		
						setLoggedUser(user);
						if (user.isSuperAdmin()){
							// Get Organizations to populate  a drop down list
							getOrganizations();
						} else {
							// set organization corresponding to the loggedUser				
							yearSemesterPanel.add(new HTML("<span>Semester-Year:</span>"));
						    yearSemesterPanel.add(new HTML("<span style='margin-left:20px;'></span>"));
						    yearSemesterPanel.add(courseSemester);
						    yearSemesterPanel.add(courseYear);
						    yearSemesterPanel.add(organizationsPanel);
						    yearSemesterPanel.add(refreshCourseTreeButton);
						}
						refreshCoursesTree(tabs);
						
						// Save Course
						Command newCourseCmd = new Command() {
							@Override
							public void execute() {
								final DialogBox dialogBox = new DialogBox();
								final CourseForm courseForm = new CourseForm();
								final SubmitButton createButton = new SubmitButton("Create", "Creating...", "Created");
								createButton.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										try{
											Course course = courseForm.getCourse();
											if (isValidCourse(course)){
												createButton.updateStateSubmitting();
												adminService.saveCourse(course, new AsyncCallback<Course>() {
													@Override
													public void onFailure(Throwable caught) {
														if (caught instanceof MessageException){
															processMessageException((MessageException) caught);
														} else {
															Window.alert("Failed to create course: " + caught.getMessage());
														}
														createButton.updateStateSubmit();
													}

													@Override
													public void onSuccess(Course course) {
														dialogBox.hide();
														refreshCoursesTree(tabs);
														createButton.updateStateSubmit();
													}
												});
											} else {
												createButton.updateStateSubmit();
											}
										} catch(Exception e){
											createButton.updateStateSubmit();
										}
									}
								});

								HorizontalPanel buttonsPanel = new HorizontalPanel();
								buttonsPanel.setWidth("100%");
								buttonsPanel.add(createButton);
								buttonsPanel.add(new Button("Close", new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										dialogBox.hide();
									}
								}));

								VerticalPanel panel = new VerticalPanel();
								panel.add(courseForm);
								panel.add(buttonsPanel);
								dialogBox.setHTML("<b>Course</b>");
								dialogBox.setWidget(panel);
								//dialogBox.center();
								dialogBox.show();
							}
						};
						
						// Save Writing activity
						Command newActivityCmd = new Command() {
							@Override
							public void execute() {
								WritingActivity writingActivity = new WritingActivity();
								writingActivity.getDeadlines().add(new Deadline("Final"));
								if (courses.isEmpty()){
									Window.alert(Constants.EXCEPTION_NOT_COURSES_FOR_ACTIVITY);
								} else {
									
									activityForm.setCourses(courses);
									activityForm.setLoggedUser(loggedUser);
									activityForm.setGlosserSites(glosserSites);
									activityForm.setWritingActivity(writingActivity);
									final DialogBox dialogBox = new DialogBox();
									final SubmitButton createButton = new SubmitButton("Create", "Creating...", "Created");
									createButton.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											createButton.updateStateSubmitting();
											if (isValidActivity(activityForm.getWritingActivity())){
												adminService.saveWritingActivity(activityForm.getCourse().getId(), activityForm.getWritingActivity(), new AsyncCallback<WritingActivity>() {
													@Override
													public void onFailure(Throwable caught) {
														if (caught instanceof MessageException){
															processMessageException((MessageException) caught);
														} else {
															Window.alert("Failed to create activity: " + caught.getMessage());
														}
														createButton.updateStateSubmit();
													}
						
													@Override
													public void onSuccess(WritingActivity writingActivity) {
														dialogBox.hide();
														refreshCoursesTree(tabs);
														createButton.updateStateSubmit();
													}
												});
											} else {
												createButton.updateStateSubmit();
											}
										}
									});
									HorizontalPanel buttonsPanel = new HorizontalPanel();
									buttonsPanel.setWidth("100%");
									buttonsPanel.add(createButton);
									buttonsPanel.add(new Button("Close", new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											dialogBox.hide();
										}
									}));
									
									VerticalPanel panel = new VerticalPanel();
									panel.add(activityForm);
									panel.add(buttonsPanel);
									dialogBox.setHTML("<b>Activity</b>");
									dialogBox.setWidget(panel);
									dialogBox.center();
									dialogBox.show();

								}
							}
						};
						// Save Review template
						Command newReviewTemplateCmd = new Command() {
							@Override
							public void execute() {
								final DialogBox dialogBox = new DialogBox();
								final ReviewTemplateForm reviewTemplateForm = new ReviewTemplateForm();
								final SubmitButton createButton = new SubmitButton("Create", "Creating...", "Created");
								createButton.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										createButton.updateStateSubmitting();
										adminService.saveReviewTemplate(reviewTemplateForm.getReviewTemplate(), new AsyncCallback<ReviewTemplate>() {
											@Override
											public void onFailure(Throwable caught) {
												if (caught instanceof MessageException){
													processMessageException((MessageException) caught);
												} else {
													Window.alert("Failed to create Review Template: " + caught.getMessage());
												}
												createButton.updateStateSubmit();
											}

											@Override
											public void onSuccess(ReviewTemplate reviewTemplate) {
												Window.alert("Review Template saved.");		
												refreshTemplateTree();
												dialogBox.hide();								
											}
										});
									}
								});

								HorizontalPanel buttonsPanel = new HorizontalPanel();
								buttonsPanel.setWidth("100%");
								buttonsPanel.add(createButton);
								buttonsPanel.add(new Button("Close", new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										dialogBox.hide();
									}
								}));

								VerticalPanel panel = new VerticalPanel();
								panel.add(reviewTemplateForm);
								panel.add(buttonsPanel);
								dialogBox.setHTML("<b>Review Template</b>");
								dialogBox.setWidget(panel);
								dialogBox.center();
								dialogBox.show();
							}
						};		
						
						// Create new menu
						MenuBar newMenu = new MenuBar(true);
						MenuItem newCourseMenuItem = WidgetFactory.createNewMenuItem("Course", newCourseCmd, "newCourseMenuItem");
						newMenu.addItem(newCourseMenuItem);
						MenuItem newActivityMenuItem = WidgetFactory.createNewMenuItem("Activity", newActivityCmd, "newActivityMenuItem");
						newMenu.addItem(newActivityMenuItem);
						MenuItem newReviewTemplateMenuItem = WidgetFactory.createNewMenuItem("Review Template", newReviewTemplateCmd, "newReviewTemplateMenuItem");
						newMenu.addItem(newReviewTemplateMenuItem);
						MenuItem createNewMenuItem = WidgetFactory.createNewMenuItem("Create new >", newMenu, "createNewMenuItem");

						// Impersonate user
						Command mockUserCmd = new Command() {
							@Override
							public void execute() {
								final DialogBox dialogBox = new DialogBox();
								final UserForm userForm = new UserForm();
								userForm.setMockUser(true);
								final Button mockUserButton = new Button("Impersonate");
								mockUserButton.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										mockUserButton.setEnabled(false);
										User user = userForm.getUser();
										if (validateUser(user)){
											adminService.mockUser(user, new AsyncCallback<User>() {
												@Override
												public void onFailure(Throwable caught) {
													String message =  "Failed to mock user: ";
													if (caught instanceof MessageException){
														processMessageException((MessageException) caught);
													} else {
														message = message + caught.getMessage();
														Window.alert(message);
													}
													mockUserButton.setEnabled(true);
												}
					
												@Override
												public void onSuccess(User user) {
													Window.alert("You are now logged in as '" + user.getUsername() + "'");
													dialogBox.hide();
												}
											});
										} else {
											Window.alert("Please enter a username or an email");
										}
									}
								});

								HorizontalPanel buttonsPanel = new HorizontalPanel();
								buttonsPanel.setWidth("100%");
								buttonsPanel.add(mockUserButton);
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
						};

						// Testing menu
						MenuBar testingMenu = new MenuBar(true);
						MenuItem mockUserMenuItem = WidgetFactory.createNewMenuItem("Impersonate user", mockUserCmd, "mockUserMenuItem");
						testingMenu.addItem(mockUserMenuItem);
						MenuItem testingMenuItem = WidgetFactory.createNewMenuItem("Test >", testingMenu, "testingMenuItem");

						// Menu bar
						MenuBar menu = new MenuBar();
						menu.setAutoOpen(false);
						menu.setWidth("100%");
						menu.setAnimationEnabled(true);
						menu.addItem(createNewMenuItem);
						menu.addSeparator();
						menu.addItem(testingMenuItem);

						// Create courses tree		
						coursesTree.setAnimationEnabled(true);
						coursesTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
							@Override
							public void onSelection(SelectionEvent<TreeItem> event) {
								final TreeItem treeItem = event.getSelectedItem();
								tabs.selectTab(0);
								if (treeItem.getUserObject() instanceof Course) {
									Course course = (Course) treeItem.getUserObject();
									activityLabel.setHTML("<b>" + course.getName() + "</b>");
									final VerticalPanel panel = new VerticalPanel();
									final CourseForm courseForm = new CourseForm();
									courseForm.setCourse(course, loggedUser);
									final SubmitButton saveButton = new SubmitButton("Save", "Saving...", "Saved");
									final User user = loggedUser;
									saveButton.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											try{
												Course course = courseForm.getCourse();
												saveButton.updateStateSubmitting();
												adminService.saveCourse(course, new AsyncCallback<Course>() {
													@Override
													public void onFailure(Throwable caught) {
														if (caught instanceof MessageException){
															processMessageException((MessageException) caught);
														} else {
															Window.alert("Failed to save course: " + caught.getMessage());
														}
														saveButton.updateStateSubmit();
													}
					
													@Override
													public void onSuccess(Course course) {
														Window.alert("Course saved.");
														courseForm.setCourse(course, user);
														saveButton.updateStateSubmit();
													}
												});
											} catch(Exception e){
												saveButton.updateStateSubmit();
											}
										}
									});

									final Button deleteButton = new Button("Delete");
									deleteButton.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											deleteButton.setEnabled(false);
											if (Window.confirm("Are you sure you want to delete this course?")) {
												try{
													Course course = courseForm.getCourse();
													adminService.deleteCourse(course, new AsyncCallback<Course>() {
														@Override
														public void onFailure(Throwable caught) {
															if (caught instanceof MessageException){
																processMessageException((MessageException) caught);
															} else {
																Window.alert("Failed to delete course: " + caught.getMessage());
															}
															deleteButton.setEnabled(true);
															refreshCoursesTree(tabs);
														}
					
														@Override
														public void onSuccess(Course course) {
															Window.alert("Course deleted.");
															refreshCoursesTree(tabs);
															panel.removeFromParent();
														}
													});
												} catch(Exception e){
													deleteButton.setEnabled(true);
												}
											} else {
												deleteButton.setEnabled(true);
											}
										}
									});
									
									HorizontalPanel buttonsPanel = new HorizontalPanel();
									buttonsPanel.add(saveButton);
									buttonsPanel.add(new HTML("&nbsp;&nbsp;"));
									buttonsPanel.add(deleteButton);
									panel.add(buttonsPanel);
									panel.add(new HTML("<hr/>"));
									panel.add(courseForm);
									nodePanel.setWidget(panel);
								} else if (treeItem.getUserObject() instanceof WritingActivity) {
									WritingActivity writingActivity = (WritingActivity) treeItem.getUserObject();
									Course course = (Course) treeItem.getParentItem().getUserObject();
									activityLabel.setHTML("<b>" + course.getName() + " > " + writingActivity.getName() + "</b>");
									final ActivityForm activityForm = new ActivityForm();
									activityForm.setGlosserSites(glosserSites);
									activityForm.setLoggedUser(loggedUser);
									activityForm.setWritingActivityAndCourse(writingActivity, course);
									final VerticalPanel panel = new VerticalPanel();
									final SubmitButton saveButton = new SubmitButton("Save", "Saving...", "Saved");
									saveButton.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											saveButton.setEnabled(false);
											saveButton.updateStateSubmitting();
											adminService.saveWritingActivity(activityForm.getCourse().getId(), activityForm.getWritingActivity(), new AsyncCallback<WritingActivity>() {
												@Override
												public void onFailure(Throwable caught) {
													if (caught instanceof MessageException){
														processMessageException((MessageException) caught);
													} else {
														Window.alert("Failed to save activity: " + caught.getMessage());
													}
													saveButton.updateStateSubmit();
												}

												@Override
												public void onSuccess(WritingActivity writingActivity) {
													Window.alert("Activity saved.");
													activityForm.setWritingActivity(writingActivity);
													saveButton.updateStateSubmit();
												}
											});
										}
									});
									final Button deleteButton = new Button("Delete");
									deleteButton.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											deleteButton.setEnabled(false);
											if (Window.confirm("Are you sure you want to delete this activity?")) {
												adminService.deleteWritingActivity(activityForm.getWritingActivity(), new AsyncCallback<WritingActivity>() {
													@Override
													public void onFailure(Throwable caught) {
														if (caught instanceof MessageException){
															processMessageException((MessageException) caught);
														} else {
															Window.alert("Failed to delete activity: " + caught.getMessage());
														}
														deleteButton.setEnabled(true);
													}

													@Override
													public void onSuccess(WritingActivity writingActivity) {
														Window.alert("Activity deleted.");
														refreshCoursesTree(tabs);
														panel.removeFromParent();
													}
												});
											} else {
												deleteButton.setEnabled(true);
											}
										}
									});

									GradeBook gradeBook = new GradeBook(adminService);
									gradeBook.setWritingActivity(writingActivity);
									gradeBookPanel.setWidget(gradeBook);
									
									ReportsTable reportsTable = new ReportsTable(adminService);
									reportsTable.setWritingActivity(writingActivity);
									reportsPanel.setWidget(reportsTable);

									HorizontalPanel buttonsPanel = new HorizontalPanel();
									buttonsPanel.add(saveButton);
									buttonsPanel.add(new HTML("&nbsp;&nbsp;"));
									buttonsPanel.add(deleteButton);
									panel.add(buttonsPanel);
									panel.add(new HTML("<hr/>"));
									panel.add(activityForm);
									nodePanel.setWidget(panel);
								}
							}
						});
						
						HTML info = new HTML("<img src='images/icon-info.gif'/> Click on the tree to administer the writing activities for your course.");
						nodePanel.setWidget(info);
						assignmentsPanel.add(nodePanel);
						
						// Add handler for templates tree
				    	reviewTemplateTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
							@Override
							public void onSelection(SelectionEvent<TreeItem> event) {
									final TreeItem treeItem = event.getSelectedItem();
									tabs.selectTab(4);
									ReviewTemplate reviewTemplate = (ReviewTemplate) treeItem.getUserObject();
									activityLabel.setHTML("<b>" + reviewTemplate.getName() + "</b>");
									final ReviewTemplateForm reviewTemplateForm = new ReviewTemplateForm();
									reviewTemplateForm.setReviewTemplate(reviewTemplate);
									final SubmitButton saveButton = new SubmitButton("Save", "Saving...", "Saved");
									saveButton.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											saveButton.updateStateSubmitting();
											adminService.saveReviewTemplate(reviewTemplateForm.getReviewTemplate(), new AsyncCallback<ReviewTemplate>() {
												@Override
												public void onFailure(Throwable caught) {
													if (caught instanceof MessageException){
														processMessageException((MessageException) caught);
													} else {
														Window.alert("Failed to save Review Template: " + caught.getMessage());
													}
													saveButton.updateStateSubmit();
												}

												@Override
												public void onSuccess(ReviewTemplate reviewTemplate) {
													Window.alert("Review Template saved.");		
													refreshTemplateTree();
													reviewTemplateForm.setReviewTemplate(reviewTemplate);
													saveButton.updateStateSubmit();
													reviewTemplatesContentPanel.add(reviewTemplateForm);
												}
											});
										}
									});
									
									final Button deleteButton = new Button("Delete");
									deleteButton.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											deleteButton.setEnabled(false);
											if (Window.confirm("Are you sure you want to delete this template?")) {
												adminService.deleteReviewTemplate(reviewTemplateForm.getReviewTemplate(), new AsyncCallback<ReviewTemplate>() {
													@Override
													public void onFailure(Throwable caught) {
														if (caught instanceof MessageException){
															processMessageException((MessageException) caught);
														} else {
															Window.alert("Failed to delete Review Template: " + caught.getMessage());
														}
														deleteButton.setEnabled(true);
													}

													@Override
													public void onSuccess(ReviewTemplate reviewTemplate) {
														Window.alert("Review Template deleted.");
														refreshTemplateTree();
														reviewTemplatesContentPanel.clear();
													}
												});
											} else {
												deleteButton.setEnabled(true);
											}
										}
									});	
									
									// Button to see the emails of the users that share the review template.
									// This form allows add new emails
									final Button shareReviewTemplateButton = new Button("Share");
									shareReviewTemplateButton.addClickHandler(new ClickHandler(){		
										@Override
										public void onClick(ClickEvent event) {
											final ShareReviewTemplateWithForm shareReviewTemplateForm = new ShareReviewTemplateWithForm(adminService, reviewTemplateForm.getReviewTemplate());
										    final DialogBox dialogBox = new DialogBox();
										    dialogBox.setWidth("100px");
										    HorizontalPanel buttonsPanel = new HorizontalPanel();
											buttonsPanel.setWidth("100%");
											
											buttonsPanel.add(new Button("Close", new ClickHandler() {
												@Override
												public void onClick(ClickEvent event) {
													dialogBox.hide();
													// reload all the reviews of the organization
													ReviewTemplate reviewTemplate = shareReviewTemplateForm.getReviewTemplate();
													reviewTemplateForm.setReviewTemplate(reviewTemplate);
													treeItem.setUserObject(reviewTemplate);
													refreshTemplateTree();
												}
											}));
											
										    VerticalPanel panel = new VerticalPanel();
											panel.add(shareReviewTemplateForm);
											panel.add(buttonsPanel);
											dialogBox.setHTML("Review Template shared with");
											dialogBox.setWidget(panel);
											dialogBox.center();
											dialogBox.show();
								    	}				
									});

									
									reviewTemplatesContentPanel.clear();
									HorizontalPanel reviewTemplateButtonsPanel = new HorizontalPanel();
									reviewTemplateButtonsPanel.add(saveButton);
									reviewTemplateButtonsPanel.add(deleteButton);
									reviewTemplateButtonsPanel.add(shareReviewTemplateButton);
									reviewTemplatesContentPanel.add(reviewTemplateButtonsPanel);
									reviewTemplatesContentPanel.add(new HTML("<hr/>"));
									reviewTemplatesContentPanel.add(reviewTemplateForm);
							}
						});
				 	
				    	// Add change event handler to the organizations list
				    	organizationsList.addChangeHandler(new ChangeHandler(){
							@Override
							public void onChange(ChangeEvent event) {
								Long organizationId = null;
								
								if (loggedUser != null && loggedUser.isSuperAdmin()){
									if (organizationsList.getItemCount() > 0){
										organizationId = Long.valueOf(organizationsList.getValue(organizationsList.getSelectedIndex()));
									} else { // organization list size = 0 then get organization from logged user 
										organizationId = loggedUser.getOrganization().getId();
									}
									activityForm.setOrganizationId(organizationId);
								} else { // logged user is not manager and 
									if (loggedUser != null && loggedUser.getOrganization()!= null){
										organizationId = loggedUser.getOrganization().getId();
										activityForm.setOrganizationId(organizationId);
									}
								}
							} 
				    	});
				    	
				    	
						tabs.setPixelSize(690, 650);
						tabs.selectTab(0);

						VerticalPanel contentPanel = new VerticalPanel();
						contentPanel.add(activityLabel);
						contentPanel.add(new HTML("<hr/>"));
						contentPanel.add(tabs);
						
						DecoratorPanel contentDeco = new DecoratorPanel();
						contentDeco.setStyleName("contentDeco");
						contentDeco.setWidget(contentPanel);

						DecoratorPanel menuDeco = new DecoratorPanel();
						menuDeco.setStyleName("menuDeco");
						menuDeco.setWidth("100%");
						menuDeco.setWidget(menu);

						VerticalPanel menuPanel = new VerticalPanel();
						menuPanel.add(menuDeco);
						menuPanel.add(courseStackPanel);
						menuPanel.add(new HTML("<hr/>"));
						
						
						menuPanel.add(new HTML("<b/>Review Templates:<b/>"));
						menuPanel.add(reviewTemplateTree);
						
						HorizontalPanel mainPanel = new HorizontalPanel();
						mainPanel.add(menuPanel);
						mainPanel.add(contentDeco);
						
						VerticalPanel adminPanel = new VerticalPanel();
						adminPanel.setSize("75%", "75%");
						adminPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
						adminPanel.add(mainPanel);	
							
						refreshCourseTreeButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {				
								refreshCoursesTree(tabs);				
							}
					    	
					    });

								
						// get Current year and 5 years ago
						adminService.getYears(new AsyncCallback<Collection<Integer>>(){
							@Override
							public void onFailure(Throwable caught) {
								if (loggedUser != null){
									if (caught instanceof MessageException){
										processMessageException((MessageException) caught);
									} else {
										Window.alert("Failed get the years " + caught.getMessage());
									}
								}
							}

							@Override
							public void onSuccess(Collection<Integer> years) {
								setYearsPanel(years); 
							}
						});
								
						// Add Logout command
						logoutCommand = new Command(){
							public void execute() {
								logout();
							}
						};

						// logout header menu
						MenuBar logoutMenu = new MenuBar(true);
						logoutItem = new MenuItem("Logout",logoutCommand);
						logoutItem.setEnabled(true);	
						logoutMenu.addItem(logoutItem);
						
						
						headerTable.setSize("76%", "5%");
						headerTable.setWidget(0, 0, new HTML ("<div "+cssDivStyle +" align='center'><h1 "+cssH1Style +">ADMIN PAGE </h1>"));
						headerTable.setWidget(0, 2, logoutMenu);
						headerTable.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
						headerTable.getCellFormatter().setAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);;
						headerTable.getCellFormatter().setAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
						
						RootPanel.get("adminPanel").add(headerTable);
						RootPanel.get("adminPanel").add(headerPanel);
						RootPanel.get("adminPanel").add(new HTML("</br>"));
					    RootPanel.get("adminPanel").add(yearSemesterPanel);
					    RootPanel.get("adminPanel").add(new HTML("</br>"));
						RootPanel.get("adminPanel").add(adminPanel);
						
						refreshCoursesTree(tabs);

					} 
				}
			});
		} 			
	}

	
	/**
	 * Gets the review templates recorded in the system and populates the Review Template Tree.
	 */
	private void refreshTemplateTree() {
		Long organizationId = null;
		// If an organization was selected from the drop down list then get the review template belong to this organization
		if (organizationsList.getItemCount() > 0){
			organizationId = Long.valueOf(organizationsList.getValue(organizationsList.getSelectedIndex()));
		} 
		
		// set the current organization in activity form
		activityForm.setOrganizationId(organizationId);
		
		// call to review templates remote method
		adminService.getReviewTemplates (organizationId, new AsyncCallback<Collection<ReviewTemplate>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof MessageException){
					processMessageException((MessageException) caught);
				} else {
					Window.alert("Failed get courses: " + caught.getMessage());
				}
			}

			@Override
			public void onSuccess(Collection<ReviewTemplate> templateList) {
				reviewTemplateTree.clear();
				// review templates tree		
				for (ReviewTemplate reviewTemplate : templateList) {
					Label name = new Label();
					name.setText(reviewTemplate.getName());
					TreeItem reviewTemplateItem = new TreeItem(new HTML("<img src='images/text.png'></img> <span><b>" + reviewTemplate.getName() + "</b></span>"));
					reviewTemplateItem.setUserObject(reviewTemplate);
					reviewTemplateTree.addItem(reviewTemplateItem);
				}
				
			}
		});
	}
	
	/**
	 * Gets the courses recorded in the system according to the defined filter year - semester and populates the Courses Tree.
	 */
	private void refreshCoursesTree(TabLayoutPanel tabs) {
		refreshCourseTreeButton.updateStateSubmitting();
		
		Long organizationId = null;
		// if there are organizations in the drop down list get the organization selected by the user
		
		if (organizationsList != null  && organizationsList.getItemCount() > 0){
			organizationId = Long.valueOf(organizationsList.getValue(organizationsList.getSelectedIndex()));
		}
		

		courseStackPanel.clear();
		courseStackPanel.setWidth("200px");
		courseStackPanel.add(coursesTree);
		coursesTree.clear();
		activityLabel.setHTML("<b>&nbsp;</b>");
		activityLabel.setStyleName("activityLabel");
		
		Integer semester = Integer.valueOf(courseSemester.getItemText(courseSemester.getSelectedIndex()));
		
		Integer year = null;
		if ( courseYear.getItemCount() > 0){
			year = Integer.valueOf(courseYear.getItemText(courseYear.getSelectedIndex()));
		}
		
			
		adminService.getCourses(semester, year,organizationId, new AsyncCallback<Collection<Course>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (loggedUser != null){
					
					if (caught instanceof MessageException){
						processMessageException((MessageException) caught);
					} else {
						Window.alert("Failed get courses. " + caught.getMessage());
					}
				}
				refreshCourseTreeButton.updateStateSubmit();
			}
	
			@Override
			public void onSuccess(Collection<Course> courseList) {
				refreshCourseTreeButton.updateStateSubmit();
				courses = courseList;
				// courses tree
				coursesTree.clear();
				for (Course course : courses) {
					TreeItem courseItem = new TreeItem(new HTML("<img src='images/google/icon_6_folder.gif'></img> <span><b>" + course.getName()+"-"+course.getYear()+"S"+course.getSemester() + "</b></span>"));
					courseItem.setUserObject(course);
					for (WritingActivity writingActivity : course.getWritingActivities()) {
						final TreeItem activityItem = new TreeItem(writingActivity.getName() + " (" + writingActivity.getTutorial() + ")");
						activityItem.setUserObject(writingActivity);
						courseItem.addItem(activityItem);
					}
					coursesTree.addItem(courseItem);
					courseItem.setState(false);
				}
				// refresh templates	
				refreshTemplateTree();
				refreshCourseTreeButton.updateStateSubmit();
			}
		});
		tabs.selectTab(0);
		nodePanel.clear();
	}
	
	// verify if the logged user enters an email or a username
	private boolean validateUser(User user){
		boolean result = false;
		result = user != null;
		result &= (!StringUtil.isBlank(user.getEmail()) || !StringUtil.isBlank(user.getUsername()));
		return result;
	}
	
	// Populate drop down list with organizations
	private void getOrganizations(){
		adminService.getOrganizations(new AsyncCallback<Collection<Organization>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof MessageException){
					processMessageException((MessageException) caught);
				} else {
					Window.alert("Failed get organizations: " + caught.getMessage());
				}
			}

			@Override
			public void onSuccess(Collection<Organization> organizations) {
				organizationsList.clear();
				for(Organization organization : organizations){
					if (organization != null){
						organizationsList.addItem(organization.getName(), organization.getId().toString());
					}
				}
				// set organization corresponding to the loggedUser				
				Organization organization = loggedUser.getOrganization();
				int index = getListBoxValuesIndex(organizationsList, organization.getId().toString());
				organizationsList.setSelectedIndex(index);
				organizationsList.fireEvent(new ListChangeEvent());
				yearSemesterPanel.add(new HTML("<span>Semester-Year-Organization:</span>"));
				organizationsPanel.add(organizationsList);
				
			    yearSemesterPanel.add(new HTML("<span style='margin-left:20px;'></span>"));
			    yearSemesterPanel.add(courseSemester);
			    yearSemesterPanel.add(courseYear);
			    yearSemesterPanel.add(organizationsPanel);
			    yearSemesterPanel.add(refreshCourseTreeButton);
			}
			
		});
	}
	
	private void setLoggedUser(User user){
		loggedUser = user;
		Organization organization = user.getOrganization();
		HTML htmlUser = new HTML(user.getFirstname() +"&nbsp;&nbsp;" + user.getLastname() + "&nbsp;-&nbsp;" + user.getEmail() + "&nbsp;-&nbsp;" +organization.getName());
		htmlUser.setStyleName("userText");
		headerTable.setWidget(0, 1, htmlUser);
		headerTable.getCellFormatter().setAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);;
		
		HTML htmlAssigments = new HTML ("<a href='Assignments.html'><< Go to the Assignments List</a></br></br><img src='images/icon-info.gif'/> If you have selected the option 'Impersonate User' then by clicking the link above you will see the assignments list of that user. </br>In order to go back to your normal 'Assignments list' you have to click the 'Assignments' link at the top of the page again.</div></br>");
		headerPanel.add(htmlAssigments);

		// Add a link to Google Doc to get user authorization access code
//		if (StringUtil.isBlank(loggedUser.getGoogleToken())){
//			// this method gets the code url parameter and call Google oAuth2 to obtain the user token
//			String code = Window.Location.getParameter("code");
//			if (StringUtil.isBlank(code) ){
//				getGoogleAuthorizationUrl();
//			} else {
//				getUserTokens(code);
//			}
//		}
		
	}
	
	private int getListBoxValuesIndex(ListBox lb, String value) {
		  if (value == null) {
		    return 0;
		  }
		  for (int i = 0; i < lb.getItemCount(); i++) {
		    String CompareValue = lb.getValue(i);
		    if (value.equals(CompareValue)) {
		      return i;
		    }
		  }
		  return 0;
	}
	
	private void setYearsPanel(Collection<Integer> years){
		for (Integer year: years){
			if (year != null){
				courseYear.addItem(year.toString(),year.toString());
			}
		}
	}

	private boolean isValidCourse(Course course){
		boolean valid = true;
		if (StringUtil.isBlank(course.getName())){
			valid = false;
			Window.alert(Constants.EXCEPTION_EMPTY_COURSE_NAME);
		} else if (course.getTutorials().isEmpty()){
			valid = false;
			Window.alert(Constants.EXCEPTION_EMPTY_COURSE_TUTORIALS);
		} else {
			Date today = new Date();
			int month = today.getMonth();
			int year = today.getYear();
			if ((course.getSemester() == 1 && month > 7 && course.getYear() <= year) || 
						(course.getYear() < year )){
				valid = false;		
				Window.alert(Constants.EXCEPTION_WRONG_SEMESTER);
			}
		}
		return valid;
		
		
	}

	private boolean isValidActivity(WritingActivity activity){
		boolean valid = true;
		if (courses.isEmpty()){
			valid = false;
			Window.alert(Constants.EXCEPTION_NOT_COURSES_FOR_ACTIVITY);
		} else if (StringUtil.isBlank(activity.getName())){
			Window.alert("Please, enter the name of the activity, this field is mandatory.");
			valid = false;
		}
		return valid;
	}
	
	private void processMessageException(MessageException me){
		Window.alert(me.getMessage());
		if (me.getStatusCode() == Constants.HTTP_CODE_LOGOUT){
			logoutCommand.execute();
		}
	}
	
	class ListChangeEvent extends ChangeEvent {}
	
	private void logout() {
		logoutItem.setEnabled(false);
		adminService.logout(new AsyncCallback<Void>(){
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				if (loggedUser !=  null && loggedUser.getOrganization() != null && loggedUser.getOrganization().isShibbolethEnabled()){
					Window.Location.replace("https://" + loggedUser.getOrganization().getReviewerDomain() + "/Shibboleth.sso/Logout");
				} else {
					Window.Location.replace(GWT.getHostPageBaseURL()+"Admin.html");
				}
			}
		});
	}
	
	private void getGoogleAuthorizationUrl() {
		
		adminService.getGoogleAuthorizationUrl(GWT.getHostPageBaseURL()+"Admin.html", new AsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof MessageException){
					Window.alert(((MessageException) caught).getMessage());
				} 
			}

			@Override
			public void onSuccess(String sUrl) {
				String htmlForm = "<br>You have currently not given permissions to access your data. Please authenticate this app with the Google OAuth provider." + 
						   "<form action="+  sUrl + " method='POST'><input type='submit' value='Ok, authorize this app with my id'/></form>";
				HTML htmlGoogleAutorization = new HTML (htmlForm);
				headerPanel.add(htmlGoogleAutorization);
			}
		});		
	}
	
	private void getUserTokens(String code) {
		String state = Window.Location.getParameter("state");
		adminService.getUserTokens(code, state, GWT.getHostPageBaseURL()+"Admin.html", new AsyncCallback<User>(){
			@Override
			public void onFailure(Throwable caught) {	
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(User user) {
				loggedUser = user;
			}
		});
	}
	
}
 
 