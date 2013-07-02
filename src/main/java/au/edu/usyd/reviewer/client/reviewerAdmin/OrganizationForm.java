package au.edu.usyd.reviewer.client.reviewerAdmin;


import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;


import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.gwt.WidgetFactory;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <p>Main form for the creation and editing of "Organizations". 
 * @author mdagraca
 *
 */
public class OrganizationForm extends Composite {

	//Service to manage organizations
	private ReviewerAdminServiceAsync reviewerAdminService;
	
	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/** TextArea with the organization name. */
	private final TextBox organizationName = WidgetFactory.createNewTextBoxWithId("organizationFormName");
	
	/** Constants **/
	private String EXCEPTION_ERROR_MESSSAGE="Failed to create organization: ";
	private String TAB_TITLE_ORGANIZATION ="Organization";
	private String MESSAGE_SAVED="Organization Saved";
	private String MESSAGE_NAME_EMPTY="Please, enter an organization name. This field is mandatory";
	/**
	 * Constructor
	 */
	public OrganizationForm(ReviewerAdminServiceAsync reviewerAdminService) {
		this.reviewerAdminService = reviewerAdminService;
		initWidget(mainPanel);
	}
	
	/** 
	 * Method that places the components in the form.
	 */
	@Override
	public void onLoad() {
		Grid grid = new Grid(2, 2);
		grid.setWidget(0, 0, new Label("Name:"));
		grid.setWidget(0,1, organizationName);
		organizationName.setWidth("300px");
		grid.getColumnFormatter().setWidth(0, "20%");
	    grid.getColumnFormatter().setWidth(1, "100%");
	    
		// Set save button click handler
		final SubmitButton saveButton = new SubmitButton("Save", "Saving...", "Saved");
		setSaveButtonClickHandler(saveButton);
		
		// Buttons 
		grid.setWidget(1,0,saveButton);
		Button cleanButton = new Button("Clean", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				organizationName.setText("");
				organizationName.setEnabled(true);
			}
		});
		grid.setWidget(1,1, cleanButton);
		
		mainPanel.add(new HTML("</br>"));
		mainPanel.add(grid);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	}

	/**
	 * It creates the click hander for the save button received as parameter
	 * @param saveButton save button to set the click handler
	 */
	private void setSaveButtonClickHandler(final SubmitButton saveButton){
		
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveButton.updateStateSubmitting();
				String name = organizationName.getText();
				if (!StringUtil.isBlank(name)){
					Organization organization = new Organization();
					organization.setName(name);
					saveOrganization(organization, saveButton);
				} else {
					Window.alert(MESSAGE_NAME_EMPTY);
				}
							
			}
		});
	}
	
	
	private void saveOrganization(final Organization organization, final SubmitButton saveButton){
		
		// call save organization service
		reviewerAdminService.saveOrganization(organization, new AsyncCallback<Organization>() {
			@Override
			public void onFailure(Throwable caught) {
				String message = EXCEPTION_ERROR_MESSSAGE;
				if (caught instanceof MessageException){
					// this is not an error, it's a message for the user
					message =  caught.getMessage();
				} else {
					message += caught.getMessage();
				}
				Window.alert(message);
				saveButton.updateStateSubmit();
			}

			@Override
			public void onSuccess(Organization organization) {
				Window.setTitle(TAB_TITLE_ORGANIZATION);
				Window.alert(MESSAGE_SAVED);
				organizationName.setText("");
				saveButton.updateStateSubmit();
			}
		});	
	}

}
