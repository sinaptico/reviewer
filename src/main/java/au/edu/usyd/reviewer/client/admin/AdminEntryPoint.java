package au.edu.usyd.reviewer.client.admin;

import java.util.Collection;
import java.util.List;

import au.edu.usyd.reviewer.client.admin.glosser.GlosserService;
import au.edu.usyd.reviewer.client.admin.glosser.GlosserServiceAsync;
import au.edu.usyd.reviewer.client.admin.glosser.ShowSitesComposite;
import au.edu.usyd.reviewer.client.admin.glosser.SiteForm;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.gwt.WidgetFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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

	/** 
	 * <p>Main method of the entry point that loads the "Glosser sites" and menus for user impersonation, courses, activities and review 
	 * templates creation. It also loads the panels and trees with the course and review templates lists according to the defined filter (Year - Semester).</p>
	 * 
	 * 
	 */
	@Override
	public void onModuleLoad() {
		// Glosser sites panel
		final ShowSitesComposite glosserPanel = new ShowSitesComposite();
		glosserService.getAllSites(new AsyncCallback<List<SiteForm>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get Glosser sites: " + caught.getMessage());
			}

			@Override
			public void onSuccess(final List<SiteForm> sites) {
				glosserSites = sites;
				glosserService.getToolList(new AsyncCallback<List<String>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to get Glosser tools list: " + caught.getMessage());
					}

					@Override
					public void onSuccess(final List<String> tools) {
						glosserPanel.setSitesAndTools(sites, tools);
					}
				});
			}
		});
		
		

		Command newCourseCmd = new Command() {
			@Override
			public void execute() {
				final DialogBox dialogBox = new DialogBox();
				final CourseForm courseForm = new CourseForm();
				final SubmitButton createButton = new SubmitButton("Create", "Creating...", "Created");
				createButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						createButton.updateStateSubmitting();
						adminService.saveCourse(courseForm.getCourse(), new AsyncCallback<Course>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to create course: " + caught.getMessage());
								createButton.updateStateSubmit();
							}

							@Override
							public void onSuccess(Course course) {
								dialogBox.hide();
								refreshCoursesTree();
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
				panel.add(courseForm);
				panel.add(buttonsPanel);
				dialogBox.setHTML("<b>Course</b>");
				dialogBox.setWidget(panel);
				//dialogBox.center();
				dialogBox.show();
			}
		};

		Command newActivityCmd = new Command() {
			@Override
			public void execute() {
				WritingActivity writingActivity = new WritingActivity();
				writingActivity.getDeadlines().add(new Deadline("Final"));
				final ActivityForm activityForm = new ActivityForm();
				activityForm.setCourses(courses);
				activityForm.setGlosserSites(glosserSites);
				activityForm.setWritingActivity(writingActivity);
				final DialogBox dialogBox = new DialogBox();
				final SubmitButton createButton = new SubmitButton("Create", "Creating...", "Created");
				createButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						createButton.updateStateSubmitting();
						adminService.saveWritingActivity(activityForm.getCourse().getId(), activityForm.getWritingActivity(), new AsyncCallback<WritingActivity>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to create activity: " + caught.getMessage());
								createButton.updateStateSubmit();
							}

							@Override
							public void onSuccess(WritingActivity writingActivity) {
								dialogBox.hide();
								refreshCoursesTree();
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
				panel.add(activityForm);
				panel.add(buttonsPanel);
				dialogBox.setHTML("<b>Activity</b>");
				dialogBox.setWidget(panel);
				dialogBox.center();
				dialogBox.show();
			}
		};
		
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
								Window.alert("Failed to create Review Template: " + caught.getMessage());
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

		Command mockUserCmd = new Command() {
			@Override
			public void execute() {
				final DialogBox dialogBox = new DialogBox();
				final UserForm userForm = new UserForm();
				final Button mockUserButton = new Button("Impersonate");
				mockUserButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						mockUserButton.setEnabled(false);
						adminService.mockUser(userForm.getUser(), new AsyncCallback<User>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to mock user: " + caught.getMessage());
								mockUserButton.setEnabled(true);
							}

							@Override
							public void onSuccess(User user) {
								Window.alert("You are now logged in as '" + user.getId() + "'");
								dialogBox.hide();
							}
						});
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

				if (treeItem.getUserObject() instanceof Course) {
					Course course = (Course) treeItem.getUserObject();
					activityLabel.setHTML("<b>" + course.getName() + "</b>");
					final VerticalPanel panel = new VerticalPanel();
					final CourseForm courseForm = new CourseForm();
					courseForm.setCourse(course);
					final SubmitButton saveButton = new SubmitButton("Save", "Saving...", "Saved");
					saveButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							saveButton.updateStateSubmitting();
							adminService.saveCourse(courseForm.getCourse(), new AsyncCallback<Course>() {
								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Failed to save course: " + caught.getMessage());
									saveButton.updateStateSubmit();
								}

								@Override
								public void onSuccess(Course course) {
									Window.alert("Course saved.");
									courseForm.setCourse(course);
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
							if (Window.confirm("Are you sure you want to delete this course?")) {
								adminService.deleteCourse(courseForm.getCourse(), new AsyncCallback<Course>() {
									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Failed to delete course: " + caught.getMessage());
										deleteButton.setEnabled(true);
									}

									@Override
									public void onSuccess(Course course) {
										Window.alert("Course deleted.");
										refreshCoursesTree();
										panel.removeFromParent();
									}
								});
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
									Window.alert("Failed to save activity: " + caught.getMessage());
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
										Window.alert("Failed to delete activity: " + caught.getMessage());
										deleteButton.setEnabled(true);
									}

									@Override
									public void onSuccess(WritingActivity writingActivity) {
										Window.alert("Activity deleted.");
										refreshCoursesTree();
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
		
		refreshTemplateTree();

		// Add handler for templates tree
    	reviewTemplateTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				final TreeItem treeItem = event.getSelectedItem();

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
									Window.alert("Failed to save Review Template: " + caught.getMessage());
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
										Window.alert("Failed to delete Review Template: " + caught.getMessage());
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
					
					reviewTemplatesContentPanel.clear();
					HorizontalPanel reviewTemplateButtonsPanel = new HorizontalPanel();
					reviewTemplateButtonsPanel.add(saveButton);
					reviewTemplateButtonsPanel.add(deleteButton);
					reviewTemplatesContentPanel.add(reviewTemplateButtonsPanel);
					reviewTemplatesContentPanel.add(new HTML("<hr/>"));
					reviewTemplatesContentPanel.add(reviewTemplateForm);
			}
		});
 	

		TabLayoutPanel tabs = new TabLayoutPanel(25, Unit.PX);
		tabs.add(new ScrollPanel(assignmentsPanel), "Assignments");
		tabs.add(new ScrollPanel(gradeBookPanel), "GradeBook");
		tabs.add(new ScrollPanel(reportsPanel), "Reports");
		tabs.add(new ScrollPanel(glosserPanel), "Glosser");
		tabs.add(new ScrollPanel(reviewTemplatesContentPanel), "Review Templates");
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
		
		RootPanel.get("adminPanel").add(new HTML ("<div "+cssDivStyle +"><h1 "+cssH1Style +">IWRITE ADMIN PAGE</h1><a href='Assignments.html'><< Go to the Assignments List</a></br></br><img src='images/icon-info.gif'/> If you have selected the option 'Impersonate User' then by clicking the link above you will see the assignments list of that user. </br>In order to go back to your normal 'Assignments list' you have to click the 'Assginments' link at the top of the page again.</div></br>"));
		
		refreshCourseTreeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {				
				refreshCoursesTree();				
			}
	    	
	    });

		courseSemester.addItem("1", "1");
		courseSemester.addItem("2", "2");
		
		courseYear.addItem("2012", "2012");
		courseYear.addItem("2011", "2011");
		courseYear.addItem("2010", "2010");
		courseYear.addItem("2009", "2009");
		
	    yearSemesterPanel.add(new HTML("<span>Semester-Year:</span>"));
	    yearSemesterPanel.add(new HTML("<span style='margin-left:20px;'></span>"));
	    yearSemesterPanel.add(courseSemester);
	    yearSemesterPanel.add(courseYear);
	    yearSemesterPanel.add(refreshCourseTreeButton);
	    
	    RootPanel.get("adminPanel").add(yearSemesterPanel);
	    RootPanel.get("adminPanel").add(new HTML("</br>"));
				
		RootPanel.get("adminPanel").add(adminPanel);
		this.refreshCoursesTree();
	}

	
	/**
	 * Gets the review templates recorded in the system and populates the Review Template Tree.
	 */
	private void refreshTemplateTree() {
    	adminService.getReviewTemplates(new AsyncCallback<Collection<ReviewTemplate>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed get courses: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Collection<ReviewTemplate> templateList) {
				// review templates tree
				reviewTemplateTree.clear();
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
	private void refreshCoursesTree() {
		refreshCourseTreeButton.updateStateSubmitting();
		
		courseStackPanel.clear();
		courseStackPanel.setWidth("200px");
		courseStackPanel.add(coursesTree);
		coursesTree.clear();
		activityLabel.setHTML("<b>&nbsp;</b>");
		activityLabel.setStyleName("activityLabel");		
		
		Integer semester = Integer.valueOf(courseSemester.getItemText(courseSemester.getSelectedIndex()));
		Integer year = Integer.valueOf(courseYear.getItemText(courseYear.getSelectedIndex()));

		
		adminService.getCourses(semester, year, new AsyncCallback<Collection<Course>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed get courses: " + caught.getMessage());
				refreshCourseTreeButton.updateStateSubmit();
			}

			@Override
			public void onSuccess(Collection<Course> courseList) {
				refreshCourseTreeButton.updateStateSubmit();
				courses = courseList;
				// courses tree
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
			}
		});
	}
}
