package au.edu.usyd.reviewer.client.admin;

import java.util.LinkedList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ClickEvent;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.Canvas;
  

public class ReviewersTable extends Composite {

	private final AdminServiceAsync adminService;
	private VerticalPanel mainPanel = new VerticalPanel();
	private ReviewingActivity reviewingActivity;
	private WritingActivity writingActivity;
	private com.google.gwt.user.client.ui.Button closeButton = new com.google.gwt.user.client.ui.Button("Close");
	private com.google.gwt.user.client.ui.Button newEntryButton = new com.google.gwt.user.client.ui.Button("Add Review");
	private HTML statusMessage = new HTML("Done.");
	private ListGrid reviewersGrid = new ListGrid() {
		
		@Override
        protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
			
            String fieldName = this.getFieldName(colNum);
            if (fieldName.equals("buttonField")) {
                IButton button = new IButton();
                button.setHeight(16);
                button.setWidth(16);
                button.setTitle("X");
                button.addClickHandler(new ClickHandler() {
                    public void onClick(final ClickEvent event) {
                    	if (Window.confirm("Selected review entry will be deleted, Are you sure?")){
                    		statusMessage.setHTML("<img src='images/google/apps_upload_icon.gif'></img> <span>Please Wait...</span>");
                    		adminService.deleteReviewEntry(record.getAttribute("ID"), new AsyncCallback<String>(){
        						@Override
        						public void onFailure(Throwable caught) {
        							Window.alert("Failed to delete review entry: " + caught.getMessage());
        							statusMessage.setText("Done.");
        						}
        						@Override
        						public void onSuccess(String result) {
        							removeData(record);
									refreshFields();
									sort();        							
        							statusMessage.setText("Done.");
        						}});
                    	}
                    }
                });
                return button;
            } else {
                return null;
            }

        }
    };
	
	public ReviewersTable(AdminServiceAsync adminService, WritingActivity writingActivity, ReviewingActivity reviewingActivity) {
		this.adminService = adminService;
		initWidget(mainPanel);		
		this.reviewingActivity = reviewingActivity;
		this.writingActivity = writingActivity;
		this.updateReviewersTable();
	}
	
	private ListGridRecord createUserRecord(User user, ReviewEntry reviewEntry) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("ID", reviewEntry.getId());
		record.setAttribute("Unikey", user.getId());
		record.setAttribute("Firstname", user.getFirstname());
		record.setAttribute("Lastname", user.getLastname());
		record.setAttribute("Document ID", reviewEntry.getDocEntry().getId());
		record.setAttribute("Document Title", reviewEntry.getDocEntry().getTitle());

		return record;
	}
	
	private void updateReviewersTable() {
		List<ListGridField> fields = new LinkedList<ListGridField>();
		ListGridField idField = new ListGridField("ID");
		idField.setCanSort(false);
		idField.setCanEdit(false);
		idField.setWidth(1);
		fields.add(idField);
		final ListGridField unikeyField = new ListGridField("Unikey");
		unikeyField.setCanSort(false);
		unikeyField.setCanEdit(false);
		unikeyField.setWidth(60);
		fields.add(unikeyField);
		ListGridField firstnameField = new ListGridField("Firstname");
		firstnameField.setCanSort(false);
		firstnameField.setCanEdit(false);
		firstnameField.setWidth(120);
		fields.add(firstnameField);
		ListGridField lastnameField = new ListGridField("Lastname");
		lastnameField.setCanSort(false);
		lastnameField.setCanEdit(false);
		lastnameField.setWidth(120);
		fields.add(lastnameField);
		
		ListGridField documentIdField = new ListGridField("Document ID");
		documentIdField.setCanSort(false);
		documentIdField.setCanEdit(true);
		documentIdField.setWidth(80);
		documentIdField.addCellSavedHandler(new CellSavedHandler(){
			@Override
			public void onCellSaved(final CellSavedEvent event) {
				final String idValue = event.getRecord().getAttributeAsString("ID");
				String newDocEntryValue = event.getRecord().getAttributeAsString("Document ID");
				if (idValue !=null ){
					if(validateNewId(newDocEntryValue)) {
						statusMessage.setHTML("<img src='images/google/apps_upload_icon.gif'></img> <span>Please Wait...</span>");
						adminService.updateReviewDocEntry(idValue, newDocEntryValue, new AsyncCallback<String>(){
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to update review entry: " + caught.getMessage());
								event.getRecord().setAttribute("Document ID", event.getOldValue());
								reviewersGrid.refreshCell(event.getRowNum(), event.getColNum());
								statusMessage.setText("Done.");
							}
							@Override
							public void onSuccess(String result) {
								event.getRecord().setAttribute("Document Title", result);
								reviewersGrid.refreshCell(event.getRowNum(), event.getColNum()+1);
								statusMessage.setText("Done.");
							}});
					} else {
						event.getRecord().setAttribute("Document ID", event.getOldValue());
						Window.alert("Please veify the new Document Id.");					
					}
				}
			}

		});
		fields.add(documentIdField);
		
		ListGridField documentField = new ListGridField("Document Title");
		documentField.setCanSort(false);
		documentField.setCanEdit(false);
		fields.add(documentField);	
		
		ListGridField buttonField = new ListGridField("buttonField", " ");
		buttonField.setWidth(18);	
		buttonField.setCanEdit(false);
		buttonField.setCanSort(false);
		buttonField.setShowDefaultContextMenu(false);
		fields.add(buttonField);		
		
		reviewersGrid.setFields(fields.toArray(new ListGridField[0]));
		
		reviewersGrid.addCellSavedHandler(new CellSavedHandler(){
			@Override
			public void onCellSaved(final CellSavedEvent event) {
				final String reviewingActivityId = reviewingActivity.getId().toString();
				final String unikey = event.getRecord().getAttributeAsString("Unikey");
				final String newDocEntryId = event.getRecord().getAttributeAsString("Document ID");
				if (event.getRecord().getAttributeAsString("ID") == null ){
						if (validateUnikey(unikey) && validateNewId(newDocEntryId)){
							statusMessage.setHTML("<img src='images/google/apps_upload_icon.gif'></img> <span>Please Wait...</span>");
							
							adminService.saveNewReviewEntry(reviewingActivityId, unikey, newDocEntryId, new AsyncCallback<ReviewEntry>(){
								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Failed to create review entry: " + caught.getMessage());
									reviewersGrid.removeData(event.getRecord());
									statusMessage.setText("Done.");
								}
								@Override
								public void onSuccess(ReviewEntry result) {
									event.getRecord().setAttribute("ID", result.getId());
									event.getRecord().setAttribute("Document Title", result.getDocEntry().getTitle());
									event.getRecord().setAttribute("Firstname", result.getOwner().getFirstname());
									event.getRecord().setAttribute("Lastname", result.getOwner().getLastname());
									event.getRecord().setAttribute("Document Title", result.getDocEntry().getTitle());
									
									reviewersGrid.refreshRow(event.getRowNum());
									unikeyField.setCanEdit(false);
									reviewersGrid.refreshFields();
									reviewersGrid.sort(7, SortDirection.DESCENDING);
									statusMessage.setText("Done.");
								}});							
						}else{
							event.getRecord().setAttribute("Unikey", event.getOldValue());
							Window.alert("Please verify the Document ID and check if the student with unikey: '"+ unikey + "' is enrolled in this course.");									
						}
				}else{
					Window.alert(event.getRecord().getAttributeAsString("ID"));
				}
			}

		});		
		
		setUpGrid();

		for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
			if (reviewEntry.getOwner() != null) {
				ListGridRecord record = createUserRecord(reviewEntry.getOwner(), reviewEntry);
				reviewersGrid.addData(record);	
				reviewersGrid.sort(7, SortDirection.DESCENDING);
			}
		}
		
		mainPanel.add(reviewersGrid);

		HorizontalPanel controlsPanel = new HorizontalPanel();
		//controlsPanel.setWidth("100%");							
		controlsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		VerticalPanel messagePanel = new VerticalPanel();
		statusMessage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		messagePanel.add(statusMessage);
		
		newEntryButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				unikeyField.setCanEdit(true);
				reviewersGrid.refreshFields();
				reviewersGrid.sort();
				reviewersGrid.startEditingNew();  
			}
		});	
		
		controlsPanel.add(newEntryButton);
		
		closeButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				((DialogBox) mainPanel.getParent().getParent().getParent().getParent()).hide();
				Window.Location.reload();		
			}
		});			
		
		controlsPanel.add(closeButton);		
		
		mainPanel.add(statusMessage);
		mainPanel.add(controlsPanel);
	}
	
	private boolean validateNewId(String newDocEntryValue) {		
		if (isNumber(newDocEntryValue)){
			for (DocEntry docEntry : writingActivity.getEntries()) {
				if (docEntry.getId().equals(Long.valueOf(newDocEntryValue)) ){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean validateUnikey(String unikeyValue) {		

		for (DocEntry docEntry : writingActivity.getEntries()) {
			if (docEntry.getOwner() != null){
				if(docEntry.getOwner().getId().equalsIgnoreCase(unikeyValue)){
					return true;
				}
			}else{
				for(User user: docEntry.getOwnerGroup().getUsers()){
					if(user.getId().equalsIgnoreCase(unikeyValue)){
						return true;
					}
				}
			}
		}
		return false;
	}	

	private boolean isNumber(String in) { try { Integer.parseInt(in); } catch (NumberFormatException ex) { return false; } return true; }
	
	private void setUpGrid() {
		reviewersGrid.setShowRecordComponents(true);        
		reviewersGrid.setShowRecordComponentsByCell(true);
		//reviewersGrid.setCanRemoveRecords(true);
		reviewersGrid.setWidth(730);
		reviewersGrid.setHeight(610);
	//	reviewersGrid.setAlternateRecordStyles(true);
		reviewersGrid.setShowAllRecords(true);
		reviewersGrid.setCellHeight(22);
		reviewersGrid.setCanEdit(true);
		reviewersGrid.setEditEvent(ListGridEditEvent.CLICK);
		reviewersGrid.setSortField("buttonField");	

	}	
}
