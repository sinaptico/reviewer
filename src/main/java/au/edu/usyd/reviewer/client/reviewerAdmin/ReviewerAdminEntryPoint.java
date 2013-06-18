package au.edu.usyd.reviewer.client.reviewerAdmin;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Starting point of the Reviewer Admin module
 * @author mdagraca
 */
public class ReviewerAdminEntryPoint implements EntryPoint {

	/** Asynchronous Reviewer admin service for model management. */
	private final static ReviewerAdminServiceAsync reviewerAdminService = (ReviewerAdminServiceAsync) GWT.create(ReviewerAdminService.class);
	
	/** Tree with the  menu  */
	private Tree menuTree = new Tree();
	
	/** Tabs for the forms **/
	private TabLayoutPanel tabs = new TabLayoutPanel(25, Unit.PX);
	
	/** Constants **/
	private String MENU_ITEM_ORGANIZATIONS = "Organizations";
	private String MENU_ITEM_CREATE_ORGANIZATION = "Create Organization";
	private String MENU_ITEM_EDIT_ORGANIZATIONS = "Edit Organizations";
	private String MENU_ITEM_USERS = "Users";
	private String MENU_ITEM_CREATE_USER="Create User";
	private String MENU_ITEM_EDIT_USERS="Edit Users";
	
	private String TAB_TITLE_ORGANIZATION ="Organization";
	private String TAB_TITLE_ORGANIZATIONS="Organizations";
	private String TAB_TITLE_USER="User";
	private String TAB_TITLE_USERS="Users";
	private String STYLE_CONTENT="contentDeco";
	private String STYLE_MENU="menuDeco";
	
	/** The css h1 style. */
	private String cssH1Style = "STYLE='text-align: left; color: #CE1126; clear: both; margin: 0 0 0 0; font-weight: normal; clear: left; font-size: 1.3em;'";
	
	/** The css div style. */
	private String cssDivStyle = "align='left' STYLE='margin: 0 0 0 0;'";
	
	/** logged user **/
	private User loggedUser = null;
	/** panel for the logged user information **/
	private VerticalPanel headerPanel = new VerticalPanel();
	
	private Command logoutCommand;
	
	@Override
	public void onModuleLoad() {
		
		GWT.setUncaughtExceptionHandler( new CustomUncaughtExceptionHandler() );

		// LoggedUser
		// get logged user to show his/her name and organization in the page
		if (loggedUser == null){
			// get teh logged user to obtain his/her organization
			reviewerAdminService.getLoggedUser(new AsyncCallback<User>(){
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed get the logged user" + caught.getMessage());
				}
	
				@Override
				public void onSuccess(User user) {
					if (user != null) {
						setLoggedUser(user);
					} 
				}
			});
		} 	
		// Menu tree
		TreeItem organizationsItem = new TreeItem(MENU_ITEM_ORGANIZATIONS);
		TreeItem createOrganizationItem = new TreeItem(MENU_ITEM_CREATE_ORGANIZATION);
		organizationsItem.addItem(createOrganizationItem);
		TreeItem editOrganizationsItem = new TreeItem("Edit Organizations");
		organizationsItem.addItem(editOrganizationsItem);
		
//		TreeItem usersItem = new TreeItem("Users");
//		TreeItem createUserItem = new TreeItem("Create User");
//		usersItem.addItem(createUserItem);
//		TreeItem editUsersItem = new TreeItem("Edit Users");
//		usersItem.addItem(editUsersItem);
//		organizationsItem.setState(true);
//		usersItem.setState(true);
		menuTree.addItem(organizationsItem);
//		menuTree.addItem(usersItem);

		// Tree item selection handler
		menuTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
					@Override
					public void onSelection(SelectionEvent<TreeItem> event) {
						final TreeItem treeItem = event.getSelectedItem();
						tabs.clear();
						if (treeItem.getText().equals(MENU_ITEM_CREATE_ORGANIZATION)){
							final OrganizationForm organizationForm = new OrganizationForm(reviewerAdminService);
							VerticalPanel organizationContentPanel = new VerticalPanel();
							organizationContentPanel.add(organizationForm);
							tabs.add(new ScrollPanel(organizationContentPanel), TAB_TITLE_ORGANIZATION);
						} else if (treeItem.getText().equals(MENU_ITEM_EDIT_ORGANIZATIONS)){
							VerticalPanel organizationsContentPanel = new VerticalPanel();
							final OrganizationsForm organizationsForm = new OrganizationsForm(reviewerAdminService, loggedUser);
							organizationsContentPanel.add(organizationsForm);
							tabs.add(new ScrollPanel(organizationsContentPanel), TAB_TITLE_ORGANIZATIONS);
						} else if (treeItem.getText().equals(MENU_ITEM_EDIT_USERS)){
							VerticalPanel editUsersContentPanel = new VerticalPanel();
							// The user clicked on the button so show edit users form
				    		final EditUsersForm editUsersForm = new EditUsersForm(reviewerAdminService,null, loggedUser);
							editUsersContentPanel.add(editUsersForm);
							tabs.add(new ScrollPanel(editUsersContentPanel), TAB_TITLE_USERS);
						} else if (treeItem.getText().equals(MENU_ITEM_CREATE_USER)){
							Window.alert("Not implemented yet");
						}
					}
				}
		);
		
		// Add Logout command
		logoutCommand = new Command(){
			public void execute() {
				reviewerAdminService.logout(new AsyncCallback<Void>(){
					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof MessageException){
							processMessageException((MessageException) caught);
						} else {
							Window.alert("Logout failed" + caught.getMessage());
						}
					}

					@Override
					public void onSuccess(Void result) {
						Window.Location.replace(GWT.getHostPageBaseURL()+"ReviewerAdmin.html");
					}
				});
			}
		};
		
		MenuBar logoutMenu = new MenuBar(true);
		logoutMenu.addItem("Logout",logoutCommand);
		
		// By default show create organization form
		menuTree.setSelectedItem(createOrganizationItem);
		tabs.setPixelSize(850, 600);
		tabs.selectTab(0);

		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.add(tabs);
		
		DecoratorPanel contentDeco = new DecoratorPanel();
		contentDeco.setStyleName(STYLE_CONTENT);
		contentDeco.setWidget(contentPanel);

		DecoratorPanel menuDeco = new DecoratorPanel();
		menuDeco.setStyleName(STYLE_MENU);
		menuDeco.setWidth("100%");
		menuDeco.setWidget(menuTree);

		VerticalPanel menuPanel = new VerticalPanel();
		menuPanel.add(menuDeco);
		menuPanel.add(new HTML("<hr/>"));
		menuPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		HorizontalPanel mainPanel = new HorizontalPanel();
		mainPanel.add(menuPanel);
		mainPanel.add(contentDeco);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		FlexTable headerTable = new FlexTable();
		headerTable.setSize("100%", "5%");
		headerTable.setWidget(0, 0, new HTML ("<div "+cssDivStyle +"><h1 "+cssH1Style +">REVIEWER ADMIN PAGE </h1>"));
		headerTable.setWidget(0, 1, logoutMenu);
		headerTable.getCellFormatter().setAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		headerTable.getCellFormatter().setAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		headerTable.setWidget(0, 1, logoutMenu);
	
		
		VerticalPanel reviewerAdminPanel = new VerticalPanel();
		reviewerAdminPanel.setSize("100%", "70%");
		reviewerAdminPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		reviewerAdminPanel.add(headerTable);
		reviewerAdminPanel.add(headerPanel);
		reviewerAdminPanel.add(mainPanel);
		
		RootPanel.get("reviewerAdminPanel").add(reviewerAdminPanel);
		
	}
	
	
	private void setLoggedUser(User user){
		loggedUser = user;
		Organization organization = user.getOrganization();
		VerticalPanel userPanel = new VerticalPanel();
		userPanel.add(new HTML(user.getFirstname() +"&nbsp;&nbsp;" + user.getLastname() + "&nbsp;-&nbsp;" + user.getEmail() + "&nbsp;-&nbsp;" +organization.getName()));
		userPanel.setStyleName("contentDeco");
		userPanel.setSize("100%", "10%");
		headerPanel.add(userPanel);
	}

	private void processMessageException(MessageException me){
		Window.alert(me.getMessage());
		if (me.getStatusCode() == Constants.HTTP_CODE_LOGOUT){
			logoutCommand.execute();
		}
	}
}
