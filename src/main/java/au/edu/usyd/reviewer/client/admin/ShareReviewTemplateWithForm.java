package au.edu.usyd.reviewer.client.admin;



import java.util.ArrayList;

import java.util.List;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

public class ShareReviewTemplateWithForm extends Composite {
	
	//Service to manage review template
	private AdminServiceAsync adminService;

	// Review template to share
	private ReviewTemplate reviewTemplate;

	// Create a CellTable.
    private CellTable<User> usersTable = new CellTable<User>();
    
	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();

	// email of the user that who will share the review template
	final TextBox emailText = new TextBox();
	
	private SubmitButton shareButton;
	
	/** Constants **/
	private String FORM_TITLE="Review template share with";
	
	
	public ShareReviewTemplateWithForm(AdminServiceAsync adminService, ReviewTemplate reviewTemplate) {
		this.reviewTemplate = reviewTemplate;
		this.adminService = adminService;
		initWidget(mainPanel);
	}

	/** 
	 * Method that places the components in the form.
	 */
	@Override
	public void onLoad() {
		
		// Display 15 rows in one page
		usersTable.setPageSize(15);
	        
	    // Add a text column to show the firstname. 
	    TextColumn<User> firstNameColumn = new TextColumn<User>() {
	      @Override
	      public String getValue(User user) {
		        return user.getFirstname();
		   }
	    };
	    usersTable.addColumn(firstNameColumn, "Firstname");
	    
	    
	    // Add a text column to show the firstname. 
	    TextColumn<User> lastNameColumn = new TextColumn<User>() {
	      @Override
	      public String getValue(User user) {
		        return user.getLastname();
		   }
	    };
	    usersTable.addColumn(lastNameColumn, "LastName");
	    
	    
	    // Add a text column to show the email. 
	    TextColumn<User> emailColumn = new TextColumn<User>() {
	      @Override
	      public String getValue(User user) {
		        return user.getEmail();
		   }
	    };
	    usersTable.addColumn(emailColumn, "email");
	 
	    // Add column with delete button to delete the user
	    Column<User,String> deleteButtonColumn = createDeleteButtonColumn();
	    usersTable.addColumn(deleteButtonColumn);
	    
	    
	    
	    
	    // Get list of users that share the review template
	    final List<User> users = new ArrayList<User>(reviewTemplate.getSharedWith());
	    loadUsersInGrid(users);
	    	    	 
	    // Set the width of the table and put the table in fixed width mode.
	    usersTable.setWidth("100%");
	    
	    SimplePager pager = new SimplePager();
	    pager.setDisplay(usersTable);
	    	 
	    VerticalPanel vp = new VerticalPanel();
	    vp.add(usersTable);
	    vp.add(pager);
	    
	    
	    Label emailLabel = new Label("Email:");
	    
	    emailText.setWidth("300px");
		Grid emailGrid = new Grid(2, 2);
		emailGrid.setWidget(0, 0, emailLabel);
		emailGrid.setWidget(0,1, emailText);
		
		// Load Button
		shareButton = shareShareButton();
		emailGrid.setWidget(1,0,shareButton);
				
		Button cleanButton = new Button("Clean");
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				emailText.setText("");
				VerticalPanel emptyPanel = new VerticalPanel();
			}
		});
		
		emailGrid.setWidget(1,1,cleanButton);
		
	    VerticalPanel emailPanel = new VerticalPanel();
	    emailPanel.add(emailGrid);
	    
	    mainPanel.add(vp);
	    mainPanel.add(emailPanel);
	}

	
	public SubmitButton shareShareButton(){ 
		final SubmitButton shareButton = new SubmitButton("Share", "Sharing...", "Shared");
		// add click handler to load button
		shareButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				shareButton.updateStateSubmitting();
				String email = emailText.getText();
				if (!StringUtil.isBlank(email)){
					adminService.shareReviewTemplateWith(reviewTemplate, email, new AsyncCallback<ReviewTemplate>() {
						@Override
						public void onFailure(Throwable caught) {
							if (caught instanceof MessageException){
								Window.alert(caught.getMessage());
							} else {
								Window.alert("Failed to share the Review Template: " + caught.getMessage());
							}
							shareButton.updateStateSubmit();
						}
	
						@Override
						public void onSuccess(ReviewTemplate newReviewTemplate) {		
							loadUsersInGrid(newReviewTemplate.getSharedWith());	
							shareButton.updateStateSubmit();
							emailText.setText("");
							reviewTemplate = newReviewTemplate;
						}
					});
				} else {
					Window.alert("Please, enter a value for the email");
				}
			}
		});	
		return shareButton;
	}
		
	private void loadUsersInGrid(final List<User> users){
		AsyncDataProvider<User> provider = new AsyncDataProvider<User>() {
		    @Override
		    protected void onRangeChanged(HasData<User> display) {
		    	int start = display.getVisibleRange().getStart();
		    	int end = start + display.getVisibleRange().getLength();
		    	end = end >= users.size() ? users.size() : end;
		    	List<User> sub = users.subList(start, end);
		    	updateRowData(start, sub);
		    }
		};
		provider.addDataDisplay(usersTable);
		provider.updateRowCount(users.size(), true);
	}	
	
	public ReviewTemplate getReviewTemplate(){
		return reviewTemplate;
	}
	
	
	private Column<User,String> createDeleteButtonColumn(){
		// Create button an column
		ButtonCell deleteButton = new ButtonCell();
	    Column<User,String> deleteButtonColumn = new Column<User,String>(deleteButton) {
	    	  public String getValue(User user) {
	    	    return "X"; //button name
	    	  }
	    };
	
	    // add field update to click handler
	    deleteButtonColumn.setFieldUpdater(new FieldUpdater<User, String>() {
	    	@Override
	    	public void update(int index, final User user, String value) {
	    		if (Window.confirm("Are you sure you don't want to share the review template with the selected user?")) {
		    		String email = user.getEmail();
		    		adminService.noShareReviewTemplateWith(reviewTemplate, email, new AsyncCallback<ReviewTemplate>() {
						@Override
						public void onFailure(Throwable caught) {
							if (caught instanceof MessageException){
								Window.alert(caught.getMessage());
							} else {
								Window.alert("Failed to delete the user of the list of users who share the Review Template: " + caught.getMessage());
							}
						}
	
						@Override
						public void onSuccess(ReviewTemplate newReviewTemplate) {		
							loadUsersInGrid(newReviewTemplate.getSharedWith());	
						}
					});
	    		}
	    	}
	    });
	    return deleteButtonColumn;
	}
}
