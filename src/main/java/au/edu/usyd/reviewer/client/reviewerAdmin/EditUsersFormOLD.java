package au.edu.usyd.reviewer.client.reviewerAdmin;

import java.util.ArrayList;

import java.util.Collection;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <p>For edit the users for the selected organization 
 * @author mdagraca
 */
public class EditUsersFormOLD extends Composite {
	
	//Service to manage organizations
	private ReviewerAdminServiceAsync reviewerAdminService;

	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	private TextBox firstNameText = new TextBox();
	private TextBox lastNameText = new TextBox();
	private TextBox emailText = new TextBox();
	private VerticalPanel usersPanel = new VerticalPanel();
	
	// Organization selected in the edit organizations form
	private Organization organization;
	
	private String MESSAGE_FIELD_EMPTIES="Please, enter some of the search fields";
	private String EXCEPTION_ERROR_MESSSAGE="Failed to load users: ";
	private String TAB_TITLE_USERS="Users";
	public  String MESSAGE_EMPTY_SEARCH_RESULT = "No results found for your search";
	private String STYLE_TEXT="RichTextToolbar";
	
	/**
	 * Constructor
	 * @param reviewerAdminService reviewer admin service with the methods to manage users, organizations and reviewer properties
	 * @param organization organization selected in the edit organizations form
	 */
	public EditUsersFormOLD(ReviewerAdminServiceAsync reviewerAdminService, Organization organization) {
		this.reviewerAdminService = reviewerAdminService;
		this.organization = organization;
		initWidget(mainPanel);
	}
	
	
	/** 
	 * Method that places the components in the form.
	 */
	@Override
	public void onLoad() {
		//show search fields
		Label firstNameLabel = new Label("First Name:");
		Label lastNameLabel = new Label("Last Name:");
		Label emailLabel = new Label("Email");
		
		Grid searchGrid = new Grid(4, 2);
		searchGrid.setWidget(0,0, firstNameLabel);
		searchGrid.setWidget(0,1, firstNameText);
		searchGrid.setWidget(1,0, lastNameLabel);
		searchGrid.setWidget(1,1, lastNameText);
		searchGrid.setWidget(2,0, emailLabel);
		searchGrid.setWidget(2,1, emailText);
	
		// Load Button
		SubmitButton loadButton = createLoadButton();
		searchGrid.setWidget(3,0,loadButton);
	
		Button cleanButton = new Button("Clean");
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				firstNameText.setText("");
				lastNameText.setText("");
				VerticalPanel emptyPanel = new VerticalPanel();
				usersPanel.add(emptyPanel);
			}
		});
		searchGrid.setWidget(3,1,cleanButton);
		mainPanel.add(new HTML("</br>"));
		mainPanel.add(searchGrid);
		mainPanel.add(new HTML("</br>"));		
		mainPanel.add(usersPanel);
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
				// verify if user first name entered  and the user last name by the users are empty
				if (!StringUtil.isBlank(lastNameText.getText()) && 
					!StringUtil.isBlank(firstNameText.getText()) && 
					!StringUtil.isBlank(emailText.getText())){
					// organization name entered by the users is not empty
					loadButton.updateStateSubmitting();
					String firstName = firstNameText.getText();
					String lastName = lastNameText.getText();
					String email = emailText.getText();
					
					// call rcp service getOrganizations
					if ( organization != null){
						getUsersWithOrganization(firstName, lastName, email, loadButton);
					} else { 
						getUsersWithoutOrganization(firstName, lastName, email, loadButton);
					}
				} else {
						Window.alert(MESSAGE_FIELD_EMPTIES);
					loadButton.updateStateSubmit();
				}
			}
		});
		return loadButton;
	}
	
	private void getUsersWithOrganization(String firstName, String lastName, String email, final SubmitButton loadButton){
//		reviewerAdminService.getUsers(organization.getId(), firstName, lastName, email, new AsyncCallback<Collection<User>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				String message = EXCEPTION_ERROR_MESSSAGE;
//				if (caught instanceof MessageException){
//					// the exception is a mmessage for the user, it is not an error
//					message =  caught.getMessage();
//				} else {
//					message += caught.getMessage();
//				}
//				Window.setTitle(TAB_TITLE_USERS);
//				Window.alert(message);
//				loadButton.updateStateSubmit();
//			}
//
//			@Override
//			public void onSuccess(Collection<User> users) {
//				// verify if there are organizations
//				if (users.size() > 0){
//					// add the organization to the table
//					addUsersToTable();
//				} else {
//					Label message = new Label(MESSAGE_EMPTY_SEARCH_RESULT);
//					DecoratorPanel messageDeco = new DecoratorPanel();
//					messageDeco.add(message);
//					messageDeco.setStyleName(STYLE_TEXT);
//					messageDeco.setWidth("100%");
//					mainPanel.add(messageDeco);
//				}
//				loadButton.updateStateSubmit();
//			}
//		});
	}
	
	private void getUsersWithoutOrganization(String firstName, String lastName, String email, final SubmitButton loadButton){
//		reviewerAdminService.getUsers(firstName, lastName, email, new AsyncCallback<Collection<User>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				String message = EXCEPTION_ERROR_MESSSAGE;
//				if (caught instanceof MessageException){
//					// the exception is a mmessage for the user, it is not an error
//					message =  caught.getMessage();
//				} else {
//					message += caught.getMessage();
//				}
//				Window.setTitle(TAB_TITLE_USERS);
//				Window.alert(message);
//				loadButton.updateStateSubmit();
//			}
//
//			@Override
//			public void onSuccess(Collection<User> users) {
//				// verify if there are organizations
//				if (users.size() > 0){
//					// add the organization to the table
//					addUsersToTable();
//				} else {
//					Label message = new Label(MESSAGE_EMPTY_SEARCH_RESULT);
//					DecoratorPanel messageDeco = new DecoratorPanel();
//					messageDeco.add(message);
//					messageDeco.setStyleName(STYLE_TEXT);
//					messageDeco.setWidth("100%");
//					mainPanel.add(messageDeco);
//				}
//				loadButton.updateStateSubmit();
//			}
//		});
	}
	
	private void addUsersToTable(){
		
		
	}
}
