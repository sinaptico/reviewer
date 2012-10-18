package au.edu.usyd.reviewer.client.reviewerAdmin;

import java.util.ArrayList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * <p>For edit the properties for the selected organization 
 * @author mdagraca
 */
public class EditPropertiesForm extends Composite {
	
	//Service to manage organizations
	private ReviewerAdminServiceAsync reviewerAdminService;

	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	// Organization selected in the edit organizations form
	private Organization organization;
	
	// Create a CellTable.
    private CellTable<OrganizationProperty> propertiesTable = new CellTable<OrganizationProperty>();
    // new property value
    private String newPropertyValue = null;
    
	
	/** Constants **/
	private String TAB_TITLE_PROPERTIES="Properties";
	private String EXCEPTION_ERROR_MESSSAGE="Failed to load organization properties: ";
	private String MESSAGE_NAME_EMPTY="Please, enter a property value. This field is mandatory";
	private String TAB_TITLE_PROPERTY ="Property";
	private String MESSAGE_PROPERTIES_NOT_EXIST="There is not properties for this organization";
	private String MESSAGE_SAVED = "Property saved.";
	private String MESSAGE_DELETED = "Property deleted";
	
	/**
	 * Constructor
	 * @param reviewerAdminService reviewer admin service with the methods to manage users, organizations and reviewer properties
	 * @param organization organization selected in the edit organizations form
	 */
	public EditPropertiesForm(ReviewerAdminServiceAsync reviewerAdminService, Organization organization) {
		this.reviewerAdminService = reviewerAdminService;
		this.organization = organization;
		initWidget(mainPanel);
	}
	
	/** 
	 * Method that places the components in the form.
	 */
	@Override
	public void onLoad() {
  
	    // Display 10 rows in one page
	    propertiesTable.setPageSize(10);
	    
	    
	    // Add a text column to show the name. 
	    TextColumn<OrganizationProperty> nameColumn = new TextColumn<OrganizationProperty>() {
	      @Override
	      public String getValue(OrganizationProperty property) {
		        return property.getProperty().getName();
		   }
	    };
	    
	    propertiesTable.addColumn(nameColumn, "Name");
	    
	    // Add column with an input cell to edit the value of the property
	    TextInputCell valueCell = new TextInputCell();
	    Column<OrganizationProperty, String> valueColumn = new Column<OrganizationProperty, String>(valueCell) {
	      @Override
	      public String getValue(OrganizationProperty property) {
	        return property.getValue();
	      }
	    };
	 	    
	    valueColumn.setFieldUpdater(new FieldUpdater() {
			@Override
			public void update(int index, Object object, Object value) {
				newPropertyValue = (String)value;
			}
		});
	    
	    propertiesTable.addColumn(valueColumn, "Value");
	    propertiesTable.addColumnStyleName(2, "gridOrganizationPropertyValueColumn");
	   
	    // Add column with save button to save the value of the property
	    Column<OrganizationProperty,String> saveButtonColumn = createSaveButtonColumn();
	    propertiesTable.addColumn(saveButtonColumn);
	    
	    // Add column with delete button to save the value of the property
	    Column<OrganizationProperty,String> deleteButtonColumn = createDeleteButtonColumn();
	    propertiesTable.addColumn(deleteButtonColumn);
	    	
	    // Get properties
	    final List<OrganizationProperty> properties = new ArrayList<OrganizationProperty>(organization.getOrganizationProperties());
	    AsyncDataProvider<OrganizationProperty> provider = new AsyncDataProvider<OrganizationProperty>() {
	    	@Override
	    	protected void onRangeChanged(HasData<OrganizationProperty> display) {
	    		int start = display.getVisibleRange().getStart();
	    	    int end = start + display.getVisibleRange().getLength();
	    	    end = end >= properties.size() ? properties.size() : end;
	    	    List<OrganizationProperty> sub = properties.subList(start, end);
	    	    updateRowData(start, sub);
	    	}
	    };
	    
	    provider.addDataDisplay(propertiesTable);
	    provider.updateRowCount(properties.size(), true);
	    	 
	    SimplePager pager = new SimplePager();
	    pager.setDisplay(propertiesTable);
	    	 
	    VerticalPanel vp = new VerticalPanel();
	    vp.add(propertiesTable);
	    vp.add(pager);
	    mainPanel.add(vp);
	    
	}
	
	/**
	 * Create a column with the save button and its click handler
	 * @return column save button
	 */
	private Column<OrganizationProperty,String> createSaveButtonColumn(){
		ButtonCell saveButton = new ButtonCell();
	    Column<OrganizationProperty,String> saveButtonColumn = new Column<OrganizationProperty,String>(saveButton) {
	    	  public String getValue(OrganizationProperty propertyt) {
	    	    return "Save"; //button name
	    	  }
	    };
	    	
	    saveButtonColumn.setFieldUpdater(new FieldUpdater<OrganizationProperty, String>() {
	    	@Override
	    	public void update(int index, final OrganizationProperty property, String value) {
	    		// The user clicked on the button 
	    		// there is not an organization with the same name
	    		final String oldValue = property.getValue();
	    		property.setValue(newPropertyValue);
	    		if (!StringUtil.isBlank(newPropertyValue) ){
	    			reviewerAdminService.saveOrganizationProperty(property, new AsyncCallback<OrganizationProperty>(){
						@Override
						public void onFailure(Throwable caught) {
							String message = EXCEPTION_ERROR_MESSSAGE;
							if (caught instanceof MessageException){
								message = caught.getMessage();
							} else {
								message += caught.getMessage();
							}
							Window.setTitle(TAB_TITLE_PROPERTY);
							Window.alert(message);
							property.setValue(oldValue);
						}
		
						@Override
						public void onSuccess(OrganizationProperty property) {
							Window.setTitle(TAB_TITLE_PROPERTY);
							Window.alert(MESSAGE_SAVED);
						}
					});
	    		} else {
	    			Window.setTitle(TAB_TITLE_PROPERTY);
	    			Window.alert(MESSAGE_NAME_EMPTY);
	    		}
	    	}
	    });
	    return saveButtonColumn;
	}

	
	
	/**
	 * Create a column with the delete button and its click handler
	 * @return column delete button
	 */
	private Column<OrganizationProperty,String> createDeleteButtonColumn(){
		ButtonCell deleteButton = new ButtonCell();
	    Column<OrganizationProperty,String> saveButtonColumn = new Column<OrganizationProperty,String>(deleteButton) {
	    	  public String getValue(OrganizationProperty propertyt) {
	    	    return "Delete"; //button name
	    	  }
	    };
	    	
	    saveButtonColumn.setFieldUpdater(new FieldUpdater<OrganizationProperty, String>() {
	    	@Override
	    	public void update(int index, final OrganizationProperty property, String value) {
	    		reviewerAdminService.deleteOrganizationProperty(property, new AsyncCallback<OrganizationProperty>(){
					@Override
					public void onFailure(Throwable caught) {
						String message = EXCEPTION_ERROR_MESSSAGE;
						if (caught instanceof MessageException){
							message = caught.getMessage();
						} else {
							message += caught.getMessage();
						}
						Window.setTitle(TAB_TITLE_PROPERTY);
						Window.alert(message);
					}
		
					@Override
					public void onSuccess(OrganizationProperty property) {
						Window.setTitle(TAB_TITLE_PROPERTY);
						Window.alert(MESSAGE_DELETED);
					}
				});
	    	}
	    });
	    return saveButtonColumn;
	}
}
