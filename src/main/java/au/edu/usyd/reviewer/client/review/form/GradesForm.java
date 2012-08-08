package au.edu.usyd.reviewer.client.review.form;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Grade;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

//TODO: get the css styles out to an external file.

/**
 * <p>Class used to record the grades of a student after their document is reviewed</p>.
 */
public class GradesForm extends Composite implements HasChangeHandlers {

	/** The grade or grades if it's more than one student. */
	private List<Grade> grades;
	
	/** The grade boxes. */
	private List<TextBox> gradeBoxes;
	
	/** The grades grid. */
	private FlexTable gradesGrid = new FlexTable();

	/**
	 * Instantiates a new grades form.
	 *
	 * @param grades the grades
	 */
	public GradesForm(List<Grade> grades) {
		this.grades = new ArrayList<Grade>(grades);
		this.gradeBoxes = new ArrayList<TextBox>(grades.size());
		for(Grade grade : grades) {
			TextBox gradeBox = new TextBox();
			gradeBox.setValue(String.valueOf(grade.getValue()));
			gradeBoxes.add(gradeBox);
		}
		initWidget(gradesGrid);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasChangeHandlers#addChangeHandler(com.google.gwt.event.dom.client.ChangeHandler)
	 */
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		final List<HandlerRegistration> handlerRegistrations = new LinkedList<HandlerRegistration>();
		for(TextBox gradeBox : gradeBoxes) {
			HandlerRegistration handlerRegistration = gradeBox.addChangeHandler(handler);
			handlerRegistrations.add(handlerRegistration);
		}
		HandlerRegistration handlerRegistration = new HandlerRegistration() {
			@Override
			public void removeHandler() {
				for(HandlerRegistration handlerRegistration : handlerRegistrations) {
					handlerRegistration.removeHandler();
				}
			}
		};
		return handlerRegistration;
	}

	/**
	 * Gets the grades.
	 *
	 * @return the grades
	 * @throws Exception the exception
	 */
	public List<Grade> getGrades() throws Exception{
		for(int i=0; i<grades.size(); i++) {
			if (isNumber(gradeBoxes.get(i).getValue()) ){
				Double gradeValueFromTextBox = Double.valueOf(gradeBoxes.get(i).getValue());
				if (gradeValueFromTextBox <= grades.get(i).getDeadline().getMaxGrade()){
					if (gradeValueFromTextBox >= 0 ){
						grades.get(i).setValue(gradeValueFromTextBox);
					}else{
						throw new Exception("Marks must be numeric values greater than zero (0).");
					}
				}else{
					throw new Exception("The maximum mark for this review is: " + grades.get(i).getDeadline().getMaxGrade());
				}
			}else{
				throw new Exception("Marks must be numeric values greater than zero (0).");
			}
		}
		return grades;
	}
	
   /**
    * Checks if is number.
    *
    * @param number the number to test
    * @return true, if is number
    */
   public boolean isNumber(String number) {        
        try {
            Double.parseDouble(number);        
        } catch (NumberFormatException ex) {
            return false;
        }        
        return true;
    }	

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onLoad()
	 */
	@Override
	public void onLoad() {
		for(int i=0; i<grades.size(); i++) {
			gradeBoxes.get(i).setWidth("50px");
			int row = gradesGrid.getRowCount();			
			gradesGrid.setHTML(row, 0, "<div STYLE='margin: 0 0 0 3em;'>"+grades.get(i).getUser().getLastname()+", "+grades.get(i).getUser().getFirstname()+"</>");
			gradesGrid.setWidget(row, 1, gradeBoxes.get(i));
		}
	}
}
