package au.edu.usyd.reviewer.client.reviewerAdmin;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * This form show a grid with all the organizations 
 * @author mdagraca
 *
 */
public class OrganizationsForm extends Composite {

	private ReviewerAdminServiceAsync reviewerAdminService;
	
	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	// Create a CellTable.
    CellTable<Organization> organizationsTable = new CellTable<Organization>();
    private VerticalPanel organizationsPanel = new VerticalPanel();
 	
	private TextBox searchText = new TextBox();
	private String newOrganizationName = null;
	private Collection<Organization> organizations;

	private List<Organization> organizationsList;
	private SubmitButton loadButton;
	
	/** Constants **/
	private String TAB_TITLE_ORGANIZATIONS="Organizations";
	private String TITLE_USERS = "Edit Users";
	private String EXCEPTION_ERROR_MESSSAGE="Failed to load organizations: ";
	private String MESSAGE_NAME_EMPTY="Please, enter an organization name. This field is mandatory";
	private String TAB_TITLE_ORGANIZATION ="Organization";
	private String MESSAGE_SAVED="Organization Saved";
	public  String MESSAGE_ORGANIZATIONS_NOT_EXIST="There is not organizations with this name";
	public  String MESSAGE_ORGANIZATION_EXIST="There is an organization with this name";
	public  String TITLE_PROPERTIES = "Organization Properties";  
	public  String MESSAGE_EMPTY_SEARCH_RESULT = "No results found for your search";
	private String STYLE_TEXT="RichTextToolbar";
	private String MESSAGE_PROPERTIES_OK ="Proproperties OK, Organization activated.";
	private String MESSAGE_ALL_PROPERTIES_SAVED="All the properties were saved";
	private String TITLE_RESET_USERS_PASSWORD = "Force users to change his password in Google";
	private User loggedUser = null;
	/**
	 * Constructor
	 */
	public OrganizationsForm(ReviewerAdminServiceAsync reviewerAdminService, User loggedUser) {
		this.reviewerAdminService = reviewerAdminService;
		initWidget(mainPanel);
	}
	
	/** 
	 * Method that places the components in the form.
	 */
	@Override
	public void onLoad() {
		Label searchLabel = new Label("Organization Name:");
		
		Grid searchGrid = new Grid(2, 2);
		searchGrid.setWidget(0, 0, searchLabel);
		searchText.setWidth("300px");
		searchGrid.setWidget(0,1, searchText);
		searchGrid.getColumnFormatter().setWidth(0, "20%");
		searchGrid.getColumnFormatter().setWidth(1, "100%");
		
		// Load Button
		loadButton = createLoadButton();
		searchGrid.setWidget(1,0,loadButton);
				
		Button cleanButton = new Button("Clean");
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchText.setText("");
				VerticalPanel emptyPanel = new VerticalPanel();
				organizationsPanel.add(emptyPanel);
			}
		});
		searchGrid.setWidget(1,1,cleanButton);
		
		// Conf main panel
		mainPanel.add(new HTML("</br>"));
		mainPanel.add(searchGrid);
		mainPanel.add(new HTML("</br>"));
		mainPanel.add(organizationsPanel);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	}

	/**
	 * Create load button and configurate it to obtain all the organizations
	 * @return SubmitButton load button
	 */
	public SubmitButton createLoadButton(){ 
		final SubmitButton loadButton = new SubmitButton("Load", "Loading...", "Loaded");
		// add click handler to load button
		loadButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loadButton.updateStateSubmitting();
				// verify if organization name entered by the users is empty
				String organizationName = searchText.getText();
				// if organization name is empty then getOrganization returns all the organizations
				reviewerAdminService.getOrganizations(organizationName, new AsyncCallback<Collection<Organization>>() {
					@Override
					public void onFailure(Throwable caught) {
						String message = EXCEPTION_ERROR_MESSSAGE;
						if (caught instanceof MessageException){
							// the exception is a message for the user, it is not an error
							message =  caught.getMessage();
						} else {
							message += caught.getMessage();
						}
						Window.setTitle(TAB_TITLE_ORGANIZATIONS);
						Window.alert(message);
						loadButton.updateStateSubmit();
					}
	
					@Override
					public void onSuccess(Collection<Organization> organizationsCollection) {
						organizations = organizationsCollection;
						// verify if there are organizations
						if (organizations.size() > 0){
							// add the organization to the table
							addOrganizations();
						} else {
							Label message = new Label(MESSAGE_EMPTY_SEARCH_RESULT);
							DecoratorPanel messageDeco = new DecoratorPanel();
							messageDeco.add(message);
							messageDeco.setStyleName(STYLE_TEXT);
							messageDeco.setWidth("100%");
							mainPanel.add(messageDeco);
						}
						loadButton.updateStateSubmit();
					}
				});
			}
		});
		return loadButton;
	}
		
	/**
	 * Generate a table with one row per organization in the collection of organizations 
	 * @param organizations organizations to set on the table
	 */
	public void addOrganizations(){
			
			organizationsTable = new CellTable<Organization>();
		    // Display 10 rows in one page
			organizationsTable.setPageSize(10);
		    
			// Add a text column to show the organization id. 
		    TextColumn<Organization> idColumn = new TextColumn<Organization>() {
		      @Override
		      public String getValue(Organization organization) {
			        return organization.getId().toString();
			   }
		    };  
		    organizationsTable.addColumn(idColumn, "Id");
		    
		    // deleted field
		    Column<Organization, Boolean> deletedColumn = new Column<Organization, Boolean>(new CheckboxCell()) {
				 @Override
				 public Boolean getValue(Organization organization) {
					 return organization.isDeleted();
				 }
			 };  
			 
			 deletedColumn.setFieldUpdater(new FieldUpdater<Organization, Boolean>() {
				 @Override
				 public void update(int index, Organization organization, Boolean value) {
					 organization.setDeleted(value);
				 }
			 });
			 
			 organizationsTable.addColumn(deletedColumn, "Deleted");
			 
			 Column<Organization, Boolean> activatedColumn = new Column<Organization, Boolean>(new CheckboxCell()) {
				 @Override
				 public Boolean getValue(Organization organization) {
					 return organization.isActivated();
				 }
			 };
			 
			 activatedColumn.setFieldUpdater(new FieldUpdater<Organization, Boolean>() {
				 @Override
				 public void update(int index, Organization organization, Boolean value) {
					 organization.setActivated(value);
				 }
			 });
			 
			 organizationsTable.addColumn(activatedColumn, "Activated");	
		
			 // Add a text column to show the organization name. 
			 TextInputCell nameInputCell = new TextInputCell();
			 
			 Column<Organization, String> nameColumn = new Column<Organization, String>(nameInputCell) {
				 @Override
				 public String getValue(Organization organization) {
					 return organization.getName();
				 }
			 };
		    
		    nameColumn.setFieldUpdater(new FieldUpdater() {
				@Override
				public void update(int index, Object object, Object value) {
					newOrganizationName = ((String)value);
				}
			});
		    
		   
		    organizationsTable.addColumn(nameColumn, "Name");
		    organizationsTable.addColumnStyleName(2, "gridOrganizationNameColumn");
		    // Add column with save button to save the value of the property
		    Column<Organization,String> saveButtonColumn = createSaveButtonColumn(nameColumn);
		    organizationsTable.addColumn(saveButtonColumn);
		    		    
		    // Add column with edit properties button to save the value of the property
		    Column<Organization,String> editProperpetiesColumn = createEditPropertiesColumn();
		    organizationsTable.addColumn(editProperpetiesColumn);
		    organizationsTable.addColumnStyleName(3, "gridOrganizationPropertyLargButtonColumn");
		    
		    // Add column with check properties button
		    Column<Organization,String> checkPropertiesColumn = checkPropertiesColumn();
		    organizationsTable.addColumn(checkPropertiesColumn);
		    organizationsTable.addColumnStyleName(4, "gridOrganizationPropertyLargButtonColumn");
		    
		    // Add column with edit users button to edit the users belong to the organization selected by the user
		    Column<Organization, String> editUsersColumn = createEditUsersColumn();
		    organizationsTable.addColumn(editUsersColumn);
		    organizationsTable.addColumnStyleName(5, "gridOrganizationPropertyLargButtonColumn");
		    
		    // Add column with a button to force all the users to change his/her password the next time that they login in Google.
		    // Before force, the loggged user must choose the role of users
		    Column<Organization, String> resetUsersPasswordColumn = createResetUsersPasswordColumn();
		    organizationsTable.addColumn(resetUsersPasswordColumn);
		    organizationsTable.addColumnStyleName(6, "gridOrganizationPropertyLargButtonColumn");
		    
		    // Set organizations in table
	    	organizationsList = new ArrayList<Organization>(organizations);
	    	
	    	AsyncDataProvider<Organization> provider = new AsyncDataProvider<Organization>() {
	    	      @Override
	    	      protected void onRangeChanged(HasData<Organization> display) {
	    	        int start = display.getVisibleRange().getStart();
	    	        int end = start + display.getVisibleRange().getLength();
	    	        end = end >= organizationsList.size() ? organizationsList.size() : end;
	    	        List<Organization> sub = organizationsList.subList(start, end);
	    	        updateRowData(start, sub);
	    	      }
	    	};
	    	provider.addDataDisplay(organizationsTable);
	    	provider.updateRowCount(organizationsList.size(), true);
	    	 
	    	// Add pager for the table
	    	SimplePager pager = new SimplePager();
	    	pager.setDisplay(organizationsTable);
	    	organizationsPanel.clear(); 
	    	organizationsPanel.add(organizationsTable);
	    	organizationsPanel.add(pager);
	}

	/**
	 * Create a column save button 
	 * @param nameColumn column where the button will be 
	 * @return  Column<Organization,String> column save button
	 */
	private Column<Organization,String> createSaveButtonColumn(final Column<Organization, String> nameColumn){
		// Create button an column
		ButtonCell saveButton = new ButtonCell();
	    Column<Organization,String> saveButtonColumn = new Column<Organization,String>(saveButton) {
	    	  public String getValue(Organization organization) {
	    	    return "Save"; //button name
	    	  }
	    };
	
	    // add field update to click handler
	    saveButtonColumn.setFieldUpdater(new FieldUpdater<Organization, String>() {
	    	@Override
	    	public void update(int index, final Organization organization, String value) {
	    		final String name = organization.getName();
	    		// verify if the organization name entered by the is empty
	    		if (!StringUtil.isBlank(name)){
	    			organization.setName(name);
	    			saveOrganization(organization);
	    		} else {
	    			Window.alert(MESSAGE_NAME_EMPTY);
	    		}
	    	}
	    });
	    return saveButtonColumn;
	}
	
	/**
	 * Create edit properties button and its click handler. 
	 * When the user click on it the edit properties form is shown
	 * @return Column<Organization,String> edit properties button
	 */
	private Column<Organization,String> createEditPropertiesColumn(){
		ButtonCell editPropertiesButton = new ButtonCell();
	    Column<Organization,String> editProperpetiesColumn = new Column<Organization,String>(editPropertiesButton) {
	    	public String getValue(Organization propertyt) {
	    		return "Edit Properties"; //button name
	    	}
	    };
	    	
	    editProperpetiesColumn.setFieldUpdater(new FieldUpdater<Organization, String>() {
	    	@Override
	    	public void update(int index, final Organization organization, String value) {
	    	    // The user clicked on the button so show edit properties form
	    		final EditPropertiesForm editPropertiesForm = new EditPropertiesForm(reviewerAdminService,organization);
			    final DialogBox dialogBox = new DialogBox();
			    HorizontalPanel buttonsPanel = new HorizontalPanel();
				buttonsPanel.setWidth("100%");
				
				buttonsPanel.add(new Button("Close", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dialogBox.hide();
						loadButton.click();
					}
				}));
			    VerticalPanel panel = new VerticalPanel();
				panel.add(editPropertiesForm);
				panel.add(buttonsPanel);
				dialogBox.setHTML(TITLE_PROPERTIES);
				dialogBox.setWidget(panel);
				dialogBox.center();
				dialogBox.show();
	    	}
	    });
	    return editProperpetiesColumn;
	}
	
	/**
	 * Save the organization and its properties
	 * @param organization organization to save
	 * @param name name of the organization
	 */
	private void saveOrganization(final Organization organization){
		// call to saveOrganization to save the organization name
		reviewerAdminService.saveOrganization(organization, new AsyncCallback<Organization>() {
			@Override
			public void onFailure(final Throwable caught) {
				String message = EXCEPTION_ERROR_MESSSAGE;
				if (caught instanceof MessageException){
					message =  caught.getMessage();
				} else {
					message += caught.getMessage();
				}
				Window.setTitle(TAB_TITLE_ORGANIZATION);
				Window.alert(message);
			}

			@Override
			public void onSuccess(final Organization organization) {
				Window.setTitle(TAB_TITLE_ORGANIZATION);
				Window.alert(MESSAGE_SAVED);
				loadButton.click();
			}
		});
		
	}
	
	
	/**
	 * Create edit properties button and its click handler. 
	 * When the user click on it the edit properties form is shown
	 * @return Column<Organization,String> edit properties button
	 */
	private Column<Organization,String>createEditUsersColumn(){
		ButtonCell editUsersButton = new ButtonCell();
	    Column<Organization,String> editUsersButtonColumn = new Column<Organization,String>(editUsersButton) {
	    	public String getValue(Organization user) {
	    		return "Edit Users"; //button name
	    	}
	    };
	    	
	    editUsersButtonColumn.setFieldUpdater(new FieldUpdater<Organization, String>() {
	    	@Override
	    	public void update(int index, Organization organization, String value) {
	    	    // The user clicked on the button so show edit users form
	    		final EditUsersForm editUsersForm = new EditUsersForm(reviewerAdminService,organization, loggedUser);
			    final DialogBox dialogBox = new DialogBox();
			    HorizontalPanel buttonsPanel = new HorizontalPanel();
				buttonsPanel.setWidth("100%");
				buttonsPanel.add(new Button("Close", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dialogBox.hide();
					}
				}));
				
			    VerticalPanel panel = new VerticalPanel();
				panel.add(editUsersForm);
				panel.add(buttonsPanel);
				dialogBox.setHTML(TITLE_USERS);
				dialogBox.setWidget(panel);
				dialogBox.center();
				dialogBox.show();
	    	}
	    });
	    return editUsersButtonColumn;
	}
	
	private Column<Organization,String> createResetUsersPasswordColumn(){
		ButtonCell resetUsersPasswordButton = new ButtonCell();
	    Column<Organization,String> resetUsersPasswordButtonColumn = new Column<Organization,String>(resetUsersPasswordButton) {
	    	public String getValue(Organization user) {
	    		return "Reset Users Password"; //button name
	    	}
	    };
	    	
	    resetUsersPasswordButtonColumn.setFieldUpdater(new FieldUpdater<Organization, String>() {
	    	@Override
	    	public void update(int index, Organization organization, String value) {
	    	    // The user clicked on the button so show the reset password form
	    		final ResetUsersPasswordForm resetUsersPasswordForm = new ResetUsersPasswordForm(reviewerAdminService,organization);
			    final DialogBox dialogBox = new DialogBox();
			    HorizontalPanel buttonsPanel = new HorizontalPanel();
				buttonsPanel.setWidth("100%");
				buttonsPanel.add(new Button("Close", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dialogBox.hide();
					}
				}));
				
			    VerticalPanel panel = new VerticalPanel();
				panel.add(resetUsersPasswordForm);
				panel.add(buttonsPanel);
				dialogBox.setHTML(TITLE_RESET_USERS_PASSWORD);
				dialogBox.setWidget(panel);
				dialogBox.center();
				dialogBox.show();
	    	}
	    });
	    return resetUsersPasswordButtonColumn;
	}

	
//	private Column<Organization, String> deleteOrganizationColumn(){
//		ButtonCell deleteOrganizationButton = new ButtonCell();
//		Column<Organization, String> deleteOrganizationButtonColumn = new Column<Organization,String>(deleteOrganizationButton){
//			public String getValue(Organization organization){
//				return "Delete"; //button name
//			}
//		};
//		
//		deleteOrganizationButtonColumn.setFieldUpdater(new FieldUpdater<Organization, String>() {
//			@Override
//			public void update(int index, Organization organization, String value) {
//				// the user clicked on the button
//				reviewerAdminService.deleteOrganization(organization, new AsyncCallback<Organization>() {
//					@Override
//					public void onFailure(final Throwable caught) {
//						String message = EXCEPTION_ERROR_MESSSAGE;
//						if (caught instanceof MessageException){
//							message =  caught.getMessage();
//						} else {
//							message += caught.getMessage();
//						}
//						Window.setTitle(TAB_TITLE_ORGANIZATION);
//						Window.alert(message);
//					}
//		
//					@Override
//					public void onSuccess(Organization organization) {
//						organizations.remove(organization);
//						Window.setTitle(TAB_TITLE_ORGANIZATION);
//						Window.alert(MESSAGE_DELETED); 
//						loadButton.click();
//					}
//				});
//			}
//		});
//		return deleteOrganizationButtonColumn;
//	}
	
	private Column<Organization,String> checkPropertiesColumn() {
		
		ButtonCell checkPropertiesButton = new ButtonCell();
		Column<Organization, String> checkPropertiesButtonColumn = new Column<Organization,String>(checkPropertiesButton){
			public String getValue(Organization organization){
				return "Check Properties"; //button name
			}
		};
		
		checkPropertiesButtonColumn.setFieldUpdater(new FieldUpdater<Organization, String>() {
			@Override
			public void update(int index, Organization organization, String value) {
				// the user clicked on the button
				reviewerAdminService.checkOrganizationProperties(organization, new AsyncCallback<Organization>() {
					@Override
					public void onFailure(final Throwable caught) {
						String message = EXCEPTION_ERROR_MESSSAGE;
						if (caught instanceof MessageException){
							message =  caught.getMessage();
						} else {
							message += caught.getMessage();
						}
						Window.setTitle(TAB_TITLE_ORGANIZATION);
						Window.alert(message);
					}
		
					@Override
					public void onSuccess(Organization organization) {
						Window.setTitle(TAB_TITLE_ORGANIZATION);
						Window.alert(MESSAGE_PROPERTIES_OK); 
						loadButton.click();
					}
				});
			}
		});
		return checkPropertiesButtonColumn;
	}
}
