package au.edu.usyd.reviewer.client.admin;

import java.util.ArrayList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Choice;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.Section;
import au.edu.usyd.reviewer.client.core.gwt.WidgetFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main form for the creation and editing of "Review templates".
 */
@SuppressWarnings("deprecation")
public class ReviewTemplateForm extends Composite {

	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/** The review template name. */
	private final TextBox reviewTemplateName = WidgetFactory.createNewTextBoxWithId("reviewTemplateName");
	
	/** The review template description. */
	private final TextArea reviewTemplateDescription = WidgetFactory.createNewTextAreaWithId("reviewTemplateDescription");
	
	/** The section table. */
	public FlexTable sectionTable = new FlexTable();
	
	/** The review template that is managed by the form. */
	private ReviewTemplate reviewTemplate = new ReviewTemplate();

	/**
	 * Instantiates a new review template form.
	 */
	public ReviewTemplateForm() {
		initWidget(mainPanel);		
	}
	
	/**
	 * Adds a choice record to the selected section.
	 *
	 * @param sectionRow the section row
	 * @param newChoice the new choice
	 */
	public void addChoice(int sectionRow, Choice newChoice) {
		final FlexTable currentChoiceTable = (FlexTable) ((VerticalPanel) sectionTable.getWidget(sectionRow, 1)).getWidget(1);	
		
		int row = currentChoiceTable.getRowCount();
		newChoice.setNumber(row+1);
		TextBox text = WidgetFactory.createNewTextBoxWithId("choiceText_"+row); 
		text.setValue(newChoice.getText());		
		text.setWidth("342px");
		 
		TextBox number = WidgetFactory.createNewTextBoxWithId("choiceNumber_"+row); 
		number.setWidth("35px");
		number.setEnabled(false);
		number.setValue(newChoice.getNumber().toString());
		
		final Button remove = new Button("X");
		remove.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent arg0) {
				for(int i=0; i<currentChoiceTable.getRowCount(); i++) {
					if(remove.equals(currentChoiceTable.getWidget(i, 2))) {
						currentChoiceTable.removeRow(i);
					}	
				}
			}});		
		
		currentChoiceTable.setWidget(row, 0, number);
		currentChoiceTable.setWidget(row, 1, text);
		currentChoiceTable.setWidget(row, 2, remove);
	}	

	/**
	 * Adds a section to the review template.
	 *
	 * @param section the section
	 */
	private void addSection(final Section section) {
		final ListBox type = new ListBox();
		type.addItem("Open Text");
		type.addItem("Multiple Choice");
		type.addItem("Scale");
		type.setSelectedIndex(section.getType());
		
		type.addChangeListener(new ChangeListener(){ 
			@Override
			public void onChange(Widget sender) {
				int itemSelected = type.getSelectedIndex();
				int sectionRow = 0;
				
				for(int i=0; i<sectionTable.getRowCount(); i++) {
					if(type.equals(((VerticalPanel) sectionTable.getWidget(i, 0)).getWidget(0))){ 
						sectionRow = i;
					}
				}
				
				if (itemSelected == Section.MULTIPLE_CHOICE){
					removeWidgetFromSectionTable(sectionRow, 1);
					addChoicesTable(sectionRow);
				}else{
					removeWidgetFromSectionTable(sectionRow, 0); //addChoiceButton
					removeWidgetFromSectionTable(sectionRow, 1); //Table with Choices
					
					if (itemSelected == Section.SCALE){
						addScaleTable(sectionRow,0,0);
					}
				}
			}
		  }); 
		
		TextArea text = new TextArea();
		text.setWidth("412px");
		text.setHeight("53px");
		text.setValue(section.getText());
		
		final Button remove = new Button("X");
		remove.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent arg0) {
				for(int i=0; i<sectionTable.getRowCount(); i++) {
					if(remove.equals(sectionTable.getWidget(i, 3))) {
						sectionTable.removeRow(i);
					}	
				}
			}});
		
		ListBox toolList = new ListBox();
		
		toolList.addItem("none");
		toolList.addItem("structure");
		toolList.addItem("flow");
		toolList.addItem("flowmap");
		toolList.addItem("topics");
		toolList.addItem("topicsmap");
		toolList.addItem("conceptmap");
		toolList.addItem("question");
		toolList.addItem("language");
		toolList.addItem("participation");
		
		if (section.getTool()!=null){
			for (int i=0; i<toolList.getItemCount();i++){
			  if (section.getTool().equalsIgnoreCase(toolList.getItemText(i))){
				  toolList.setSelectedIndex(i);			  
			  }
			}
		}
		

		
		int row = sectionTable.getRowCount();
		VerticalPanel sectionTypePanel = new VerticalPanel();
		sectionTypePanel.add(type);
		VerticalPanel sectionTextPanel = new VerticalPanel();
		sectionTextPanel.add(text);
		sectionTable.setWidget(row, 0, sectionTypePanel);
		sectionTable.setWidget(row, 1, sectionTextPanel);
		sectionTable.setWidget(row, 2, toolList);
		sectionTable.setWidget(row, 3, remove);		
		
	}	
	
	/**
	 * Removes the widget from section table.
	 *
	 * @param sectionRow the section row
	 * @param column the column
	 */
	public void removeWidgetFromSectionTable(int sectionRow, int column){
		try {((VerticalPanel) sectionTable.getWidget(sectionRow, column)).remove(1); 
		} catch (Exception e) {	/*The widget to delete doesn't exist.  Just continue */ }
	}
	
	/**
	 * Adds a widget to the specified section table.
	 *
	 * @param sectionRow the section row
	 * @param column the column
	 * @param widget the widget
	 */
	public void addWidgetToSectionTable(int sectionRow, int column, Widget widget){		
		((VerticalPanel) sectionTable.getWidget(sectionRow, column)).add(widget);
	}
	
	/**
	 * Adds a choices table to the specified section row.
	 *
	 * @param sectionRow the section row
	 */
	public void addChoicesTable(final int sectionRow) {
		FlexTable choiceTable = new FlexTable();
		
		final Button addChoice = new Button("Add Option");
		addChoice.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {	
				Choice newChoice = new Choice();	
				int selectedSectionRow = sectionRow;
				for(int i=0; i<sectionTable.getRowCount(); i++) {
					try { 
						if(addChoice.equals(((VerticalPanel) sectionTable.getWidget(i, 0)).getWidget(1))) {
							selectedSectionRow = i;
					    }						
					} catch (Exception e) {	
						/*The addChoice Button doesn't exist in this row.  Just continue */ 
					}
				}					
				addChoice(selectedSectionRow, newChoice);							
			}
		});		
		
		addWidgetToSectionTable(sectionRow,0,addChoice);		
		addWidgetToSectionTable(sectionRow,1,choiceTable);
	} 	
	
	/**
	 * Adds a scale table to the specified section row.
	 *
	 * @param sectionRow the section row
	 * @param selectedFrom the selected from
	 * @param selectedTo the selected to
	 */
	public void addScaleTable(final int sectionRow, int selectedFrom, int selectedTo) {
		FlexTable scaleTable = new FlexTable();		
		final ListBox to = new ListBox();
		final ListBox from = new ListBox();
		
		from.addItem("1");
		for (int i=3; i<=10;i++){
			to.addItem(String.valueOf(i));
		}
		
		from.setSelectedIndex(selectedFrom);
		to.setSelectedIndex(selectedTo);
		
		scaleTable.setWidget(0, 0, from);
		scaleTable.setWidget(0, 1, to);
		addWidgetToSectionTable(sectionRow,1,scaleTable);
	} 		
	
	/**
	 * Gets the review template with all the values from the form.
	 *
	 * @return the review template
	 */
	public ReviewTemplate getReviewTemplate() {
		reviewTemplate.setName(reviewTemplateName.getText());
		reviewTemplate.setDescription(reviewTemplateDescription.getText());		
	
		Section section;
		List<Section> sections = new ArrayList<Section>();
		for(int i=0; i<sectionTable.getRowCount(); i++) {
			section = new Section();		
			section.setType(Integer.valueOf(((ListBox)((VerticalPanel) sectionTable.getWidget(i, 0)).getWidget(0)).getSelectedIndex()));
			int selectedTool = ((ListBox)(sectionTable.getWidget(i, 2))).getSelectedIndex();
			section.setTool(((ListBox)(sectionTable.getWidget(i, 2))).getItemText(selectedTool));
			section.setText(((TextArea)((VerticalPanel) sectionTable.getWidget(i, 1)).getWidget(0)).getValue());
			section.setNumber(i+1);
			
			Choice choice;
			List<Choice> choices= new ArrayList<Choice>();
			if (section.getType() == Section.MULTIPLE_CHOICE){
				FlexTable choicesTable = (FlexTable) ((VerticalPanel) sectionTable.getWidget(i, 1)).getWidget(1);
				for(int j=0; j<choicesTable.getRowCount(); j++) {
					choice = new Choice();			
					choice.setNumber(Integer.valueOf(((TextBox)choicesTable.getWidget(j, 0)).getValue()));
					choice.setText(((TextBox)choicesTable.getWidget(j, 1)).getValue());
					choices.add(choice);					
				}
			  section.setChoices(choices);	
			}
			
			if (section.getType() == Section.SCALE){
				FlexTable scaleTable = (FlexTable) ((VerticalPanel) sectionTable.getWidget(i, 1)).getWidget(1);
				int fromIndex = ((ListBox)scaleTable.getWidget(0, 0)).getSelectedIndex();
				int toIndex = ((ListBox)scaleTable.getWidget(0, 1)).getSelectedIndex();
				int fromValue = Integer.valueOf(((ListBox)scaleTable.getWidget(0, 0)).getValue(fromIndex));
				int toValue = Integer.valueOf(((ListBox)scaleTable.getWidget(0, 1)).getValue(toIndex));	
				
				for(int j=fromValue; j<=toValue;j++){
					choice = new Choice();			
					choice.setNumber(j);
					choice.setText("Scale Choice #"+j);
					choices.add(choice);					
				}
			  section.setChoices(choices);
			}
			
			sections.add(section);			
		}
	
	  reviewTemplate.setSections(sections);
	  return reviewTemplate;
   }   
	
	/**
	 * Sets the review template values in the form from the reviewtemplate object received.
	 *
	 * @param reviewTemplate the new review template
	 */
	public void setReviewTemplate(ReviewTemplate reviewTemplate) {
		sectionTable = new FlexTable();
		
		this.reviewTemplate = null;
		this.reviewTemplate= reviewTemplate;
		reviewTemplateName.setText(reviewTemplate.getName());		
		reviewTemplateDescription.setText(reviewTemplate.getDescription());
		
		// set Sections and choices
		int sectionRow = 0;
		for(Section section : reviewTemplate.getSections()) {
			addSection(section);
			if (section.getType()==Section.MULTIPLE_CHOICE){
				addChoicesTable(sectionRow);
				for (Choice choice : section.getChoices()) {
					addChoice(sectionRow, choice);
				}
			}
			if (section.getType()==Section.SCALE){
				addScaleTable(sectionRow, 0, section.getChoices().size()-3);
			}
			sectionRow++;
		}
	}	

	/**
	 * It loads all the defined components (Horizontal and Vertical panels, CheckBoxes, 
	 * TextBoxes, ListBoxes ...) into the form.	 
	 * 
	 */
	@Override
	public void onLoad() {
		Grid grid = new Grid(2, 2);
		grid.setWidget(0, 0, new Label("Name:"));
		grid.setWidget(0, 1, reviewTemplateName);
		grid.setWidget(1, 0, new Label("Description:"));
		reviewTemplateDescription.setWidth("365px");
		grid.setWidget(1, 1, reviewTemplateDescription);
		
		Button addSection = new Button("Add Section");
		addSection.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {	
				Section newSection = new Section();				
				newSection.setType(0);
				newSection.setNumber(reviewTemplate.getSections().size()+1);
				reviewTemplate.getSections().add(newSection);
				addSection(newSection);
			}});
		
		sectionTable.setCellPadding(2);
		sectionTable.setCellSpacing(0);
		sectionTable.setBorderWidth(1);	
		VerticalPanel sectionPanel = new VerticalPanel();
		sectionPanel.add(sectionTable);
		sectionPanel.add(addSection);
		
		mainPanel.clear();
		mainPanel.add(grid);
		mainPanel.add(new HTML("Sections:"));
		mainPanel.add(sectionPanel);		
	}

}
