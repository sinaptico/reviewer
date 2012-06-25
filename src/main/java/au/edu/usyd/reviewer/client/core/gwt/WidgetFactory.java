package au.edu.usyd.reviewer.client.core.gwt;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.widgetideas.client.ValueSpinner;

/**
 * <p>A factory for creating Widget objects.</p>
 */
public final class WidgetFactory {

	/**
	 * Creates a new Widget object.
	 *
	 * @param id the id
	 * @return the list box
	 */
	public static ListBox createNewListBoxWithId(String id) {
		ListBox listBox = new ListBox();
		listBox.getElement().setId(id);
		return listBox;
	}

	/**
	 * Creates a new Widget object.
	 *
	 * @param text the text
	 * @param command the command
	 * @param id the id
	 * @return the menu item
	 */
	public static MenuItem createNewMenuItem(String text, Command command, String id) {
		MenuItem menuItem = new MenuItem(text, command);
		menuItem.getElement().setId(id);
		return menuItem;
	}

	/**
	 * Creates a new Widget object.
	 *
	 * @param text the text
	 * @param menuBar the menu bar
	 * @param id the id
	 * @return the menu item
	 */
	public static MenuItem createNewMenuItem(String text, MenuBar menuBar, String id) {
		MenuItem menuItem = new MenuItem(text, menuBar);
		menuItem.getElement().setId(id);
		return menuItem;
	}

	/**
	 * Creates a new Widget object.
	 *
	 * @param id the id
	 * @return the text area
	 */
	public static TextArea createNewTextAreaWithId(String id) {
		TextArea textArea = new TextArea();
		textArea.getElement().setId(id);
		return textArea;
	}

	/**
	 * Creates a new Widget object.
	 *
	 * @param id the id
	 * @return the text box
	 */
	public static TextBox createNewTextBoxWithId(String id) {
		TextBox textBox = new TextBox();
		textBox.getElement().setId(id);
		return textBox;
	}

	/**
	 * Creates a new Widget object.
	 *
	 * @param value the value
	 * @param min the min
	 * @param max the max
	 * @param id the id
	 * @return the value spinner
	 */
	public static ValueSpinner createNewValueSpinnerWithId(long value, int min, int max, String id) {
		ValueSpinner valueSpinner = new ValueSpinner(value, min, max);
		valueSpinner.getElement().setId(id);
		return valueSpinner;
	}
	
	/**
	 * Creates a new Widget object.
	 *
	 * @param id the id
	 * @param name the name
	 * @param text the text
	 * @return the check box
	 */
	public static CheckBox createNewCheckBoxWithId(String id, String name, String text) {
		CheckBox checkBox = new CheckBox();
		checkBox.getElement().setId(id);
		checkBox.setName(name);
		checkBox.setText(text);		
		return checkBox;
	}	
}
