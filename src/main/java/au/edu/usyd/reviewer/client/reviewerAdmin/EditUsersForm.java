package au.edu.usyd.reviewer.client.reviewerAdmin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.usyd.reviewer.client.admin.UserForm;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.gwt.WidgetFactory;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

public class EditUsersForm extends Composite {
	
	
	//Service to manage organizations
	private ReviewerAdminServiceAsync reviewerAdminService;

	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	// Create a CellTable.
    private CellTable<User> usersTable = new CellTable<User>();
    
	private Organization organization;
	private Collection<User> users = new ArrayList<User>();
	private TextBox firstnameSearchText = new TextBox();
	private TextBox lastnameSearchText = new TextBox();
	private SubmitButton loadButton;
	
	
	/** Constants **/
	private String TAB_TITLE_USERS="Users";
	private String EXCEPTION_ERROR_MESSSAGE="Failed to load the organization users: ";
	private String MESSAGE_USERS_NOT_EXIST="There are not users for this organization";
	private String MESSAGE_FIELDS_EMPTY="The firstname or lastname can not be emtpy.";
	private String STYLE_TEXT="RichTextToolbar";
	
	private User loggedUser = null;
	
	/** The course's year included in the filter. */
	private ListBox organizationsList = WidgetFactory.createNewListBoxWithId("organizationsList");
	private Map<Long,Organization> organizationsMap = new HashMap<Long,Organization>();

	/** panel for the organizations drop dwon list **/
	private VerticalPanel organizationsPanel = new VerticalPanel();
	private VerticalPanel usersPanel = new VerticalPanel();;

	/**
	 * Constructor
	 * @param reviewerAdminService reviewer admin service with the methods to manage users, organizations and reviewer properties
	 * @param organization organization selected in the edit organizations form
	 */
	public EditUsersForm(ReviewerAdminServiceAsync reviewerAdminService, Organization organization, User loggedUser) {
		this.reviewerAdminService = reviewerAdminService;
		this.organization = organization;
		initWidget(mainPanel);
		this.loggedUser = loggedUser;
	}
	
	/** 
	 * Method that places the components in the form.
	 */
	@Override
	public void onLoad() {
  
		// Add change event handler to the organizations list
		organizationsList.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				if (organization == null){
					Long organizationId = Long.valueOf(organizationsList.getValue(organizationsList.getSelectedIndex()));
					organization = organizationsMap.get(organizationId);
				} else {
					int index = getListBoxValuesIndex(organizationsList, organization.getId().toString());
					organizationsList.setSelectedIndex(index);
					organizationsList.fireEvent(new ListChangeEvent());
				}
			}
    	});
	
		Grid searchGrid = new Grid(4,2);
		// Firstname
		Label firstNameLabel = new Label("Firstname:");
		searchGrid.setWidget(0, 0, firstNameLabel);
		searchGrid.setWidget(0, 1, firstnameSearchText);
		
		// Lastname
		Label lastnameLabel = new Label("Lastname:");
		searchGrid.setWidget(1, 0, lastnameLabel);
		searchGrid.setWidget(1, 1, lastnameSearchText);
		
		if (loggedUser !=null && (loggedUser.isSuperAdmin() || loggedUser.isAdmin())){
			// get Organizations
			getOrganizations();
			
			Label organizationLabelLabel = new Label("Organization:");
			searchGrid.setWidget(2, 0, organizationLabelLabel);
			searchGrid.setWidget(2, 1, organizationsList);
			
			// Load Button
			loadButton = createLoadButton();
			searchGrid.setWidget(3,0,loadButton);
					
			Button cleanButton = new Button("Clean");
			cleanButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					firstnameSearchText.setText("");
					lastnameSearchText.setText("");
				}
			});
			searchGrid.setWidget(3,1,cleanButton);
		} else {
			// Load Button
			loadButton = createLoadButton();
			searchGrid.setWidget(2,0,loadButton);
					
			Button cleanButton = new Button("Clean");
			cleanButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					firstnameSearchText.setText("");
					lastnameSearchText.setText("");
				}
			});
			searchGrid.setWidget(2,1,cleanButton);
		}
		
		
		// Conf main panel
		mainPanel.add(new HTML("</br>"));
		mainPanel.add(searchGrid);
		mainPanel.add(new HTML("</br>"));
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
				// verify if user firstname, lastname and email entered by the user is empty
				if (!StringUtil.isBlank(firstnameSearchText.getText()) || 
				   !StringUtil.isBlank(lastnameSearchText.getText())){
				   User user = new User();
				   user.setFirstname(firstnameSearchText.getText());
				   user.setLastname(lastnameSearchText.getText());
				   user.setOrganization(organization);
				   reviewerAdminService.getUsers(user, new AsyncCallback<Collection<User>>() {
					   @Override
					   public void onFailure(Throwable caught) {
						   String message = EXCEPTION_ERROR_MESSSAGE;
						   if (caught instanceof MessageException){
							   // the exception is a message for the user, it is not an error
							   message =  caught.getMessage();
						   } else {
							   message += caught.getMessage();
						   }
						   Window.setTitle(TAB_TITLE_USERS);
						   Window.alert(message);
						   loadButton.updateStateSubmit();
					   }
	
					   @Override
						public void onSuccess(Collection<User> usersCollection) {
							// verify if there are users
							if (usersCollection.size() > 0){
								// add the organization to the table
								users = usersCollection;
								addUsers();
							} else {
								Label message = new Label(MESSAGE_USERS_NOT_EXIST);
								DecoratorPanel messageDeco = new DecoratorPanel();
								messageDeco.add(message);
								messageDeco.setStyleName(STYLE_TEXT);
								messageDeco.setWidth("100%");
								mainPanel.add(messageDeco);
							}
							loadButton.updateStateSubmit();
						}
				   });
				} else {
					Window.setTitle(TAB_TITLE_USERS);
	    			Window.alert(MESSAGE_FIELDS_EMPTY);
				}
			}
		});
		return loadButton;
	}
	
	/**
	 * Generate a table with one row per organization in the collection of organizations 
	 * @param organizations organizations to set on the table
	 */
	public void addUsers(){
		
		usersPanel.clear();
		usersTable = new CellTable<User>();
		
	    // Display 10 rows per page
	    usersTable.setPageSize(1);
	    
	    // Add column with an input cell to edit the firstname of the user
	    TextCell firstNameCell = new TextCell();
	    Column<User, String> firstNameColumn = new Column<User, String>(firstNameCell) {
	      @Override
	      public String getValue(User user) {
	        return user.getFirstname();
	      }
	    };
	 	    
	    usersTable.addColumn(firstNameColumn, "Firstname");
	    
	    // Add column with an input cell to edit the lastname of the user
	    TextCell lastNameCell = new TextCell();
	    Column<User, String> lastNameColumn = new Column<User, String>(lastNameCell) {
	      @Override
	      public String getValue(User user) {
	        return user.getLastname();
	      }
	    };	 	    
	    usersTable.addColumn(lastNameColumn, "Lastname");
	   
	    
	    // Add column with an input cell to edit the email of the user
	    TextCell emailCell = new TextCell();
	    Column<User, String> emailColumn = new Column<User, String>(emailCell) {
	      @Override
	      public String getValue(User user) {
	        return user.getEmail();
	      }
	    };
	    usersTable.addColumn(lastNameColumn, "Email");
	   
	    // Add column with edit button to save the user's firstname, lastname and email
	    Column<User,String> editButtonColumn = createEditButtonColumn();
	    usersTable.addColumn(editButtonColumn);
	        
	    // Get users
	    final List<User> usersList = new ArrayList<User>(users);
	    AsyncDataProvider<User> provider = new AsyncDataProvider<User>() {
	    	@Override
	    	protected void onRangeChanged(HasData<User> display) {
	    		int start = display.getVisibleRange().getStart();
	    	    int end = start + display.getVisibleRange().getLength();
	    	    end = end >= usersList.size() ? usersList.size() : end;
	    	    List<User> sub = usersList.subList(start, end);
	    	    updateRowData(start, sub);
	    	}
	    };
	    
	    provider.addDataDisplay(usersTable);
	    provider.updateRowCount(usersList.size(), true);
	    	 
	    SimplePager pager = new SimplePager();
	    pager.setDisplay(usersTable);
	    
	    
	    usersPanel.add(usersTable);
	    usersPanel.add(pager);
	    mainPanel.add(usersPanel);
	    
	    

	}
	/**
	 * Create a column with the edit button and its click handler
	 * @return column save button
	 */
	private Column<User,String> createEditButtonColumn(){
		
			
		ButtonCell editButton = new ButtonCell();
	    Column<User,String> saveButtonColumn = new Column<User,String>(editButton) {
	    	  public String getValue(User usert) {
	    	    return "Edit"; //button name
	    	  }
	    };
	    	
	    saveButtonColumn.setFieldUpdater(new FieldUpdater<User, String>() {
	    	@Override
	    	public void update(int index, final User user, String value) {
	    		// The user clicked on the button
	    		final UserForm userForm = new UserForm();
				userForm.setUser(user);
				userForm.setLoggedUser(loggedUser);
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
				panel.add(userForm);
				panel.add(buttonsPanel);
				dialogBox.setHTML("<b>Users</b>");
				dialogBox.setWidget(panel);
				dialogBox.center();
				dialogBox.show();
	    	
	    	}
	    });
	    return saveButtonColumn;
	}

	// Populate drop down list with organizations
	private void getOrganizations(){
		
		reviewerAdminService.getOrganizations(null,new AsyncCallback<Collection<Organization>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed get organizations: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Collection<Organization> organizations) {
				organizationsList.clear();
				for(Organization organization : organizations){
					if (organization != null){
						organizationsList.addItem(organization.getName(), organization.getId().toString());
						organizationsMap.put(organization.getId(), organization);
					}
				}
				// set organization corresponding to the loggedUser				
				Organization organization = loggedUser.getOrganization();
				int index = getListBoxValuesIndex(organizationsList, organization.getId().toString());
				organizationsList.setSelectedIndex(index);
				organizationsList.fireEvent(new ListChangeEvent());
				organizationsPanel.add(new Label("Organization:"));
				organizationsPanel.add(organizationsList);
			}
			
		});
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
	
	class ListChangeEvent extends ChangeEvent {}

}
	
	
	
	

	

