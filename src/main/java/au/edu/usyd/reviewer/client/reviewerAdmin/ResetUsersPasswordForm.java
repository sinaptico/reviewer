package au.edu.usyd.reviewer.client.reviewerAdmin;

import java.util.ArrayList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResetUsersPasswordForm extends Composite {
	
	//Service to manage organizations and their users
	private ReviewerAdminServiceAsync reviewerAdminService;

	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	// Organization selected in the edit organizations form
	private Organization organization;
	
	private User loggedUser = null;
	
	private CheckBox superAdminRoleCheckBox = new CheckBox();
	private CheckBox adminRoleCheckBox = new CheckBox();
	private CheckBox staffRoleCheckBox = new CheckBox();
	private CheckBox guestRoleCheckBox  = new CheckBox();
	
	public ResetUsersPasswordForm(ReviewerAdminServiceAsync reviewerAdminService, Organization organization) {
		this.reviewerAdminService = reviewerAdminService;
		this.organization = organization;
		initWidget(mainPanel);
	}
	
	/** 
	 * Method that places the components in the form.
	 */
	@Override
	public void onLoad() {
		
		superAdminRoleCheckBox.setValue(false);
		adminRoleCheckBox.setValue(false);
		staffRoleCheckBox.setValue(false);
		guestRoleCheckBox.setValue(false);
		
		// Add checkboxes for each role in reviewer and a button to force the users with these roles to change their passwords in Google		
		HorizontalPanel superAdminPanel = new HorizontalPanel();
		superAdminPanel.add(superAdminRoleCheckBox);
		superAdminPanel.add(new Label(" Super Admin"));
		
		HorizontalPanel adminPanel = new HorizontalPanel();
		adminPanel.add(adminRoleCheckBox);
		adminPanel.add(new Label(" Admin"));
		
		// for lecturers and tutors
		HorizontalPanel staffPanel = new HorizontalPanel();
		staffPanel.add(staffRoleCheckBox);
		staffPanel.add(new Label(" Staff (lecturers and tutors)"));
		
		// for students
		HorizontalPanel guestPanel = new HorizontalPanel();
		guestPanel.add(guestRoleCheckBox);
		guestPanel.add(new Label(" Guest (Students)"));
		
		Grid grid = new Grid(4, 2);
		grid.setWidget(0, 0, new Label("Roles:"));
		grid.setWidget(1, 0, superAdminPanel);
		grid.setWidget(1, 1, adminPanel);
		grid.setWidget(2, 0, staffPanel);
		grid.setWidget(2, 1, guestPanel);
		grid.setWidget(3 ,0, createResetUsersPasswordButton());
		
		mainPanel.clear();
		mainPanel.add(grid);

	}

	
	public SubmitButton createResetUsersPasswordButton(){
		final SubmitButton createButton = new SubmitButton("Force", "Forcing...", "Forced");
		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try{
					if (roleSected()){
						createButton.updateStateSubmitting();
						List<String> roles = getRoles();
						reviewerAdminService.forceUsersChangePassword(organization, roles,new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								if (caught instanceof MessageException){
									Window.alert(caught.getMessage());
								} else {
									Window.alert("Failed to force users to change password.\n" + caught.getMessage());
								}
								createButton.updateStateSubmit();
							}

							@Override
							public void onSuccess(Void result) {
								Window.alert("The next time that the users login in Google they should change their password.");
								createButton.updateStateSubmit();
							}
						});
					} else {
						createButton.updateStateSubmit();
						Window.alert("Please chose one of the role.");
					}
				} catch(Exception e){
					createButton.updateStateSubmit();
				}
			}
		});
		return createButton;
	}
	
	private boolean roleSected(){
		return superAdminRoleCheckBox.getValue() || adminRoleCheckBox.getValue() ||
		staffRoleCheckBox.getValue() || guestRoleCheckBox.getValue(); 
	}
	
	private List<String> getRoles() {
		List<String> roles = new ArrayList<String>();
		if (superAdminRoleCheckBox.getValue()){
			roles.add(Constants.ROLE_SUPER_ADMIN);
		}
		if (adminRoleCheckBox.getValue()){
			roles.add(Constants.ROLE_ADMIN);
		}
		if(staffRoleCheckBox.getValue()){
			roles.add(Constants.ROLE_STAFF);
		}
		if(guestRoleCheckBox.getValue()){
			roles.add(Constants.ROLE_GUEST);
		}
		
		return roles;
	}
}
