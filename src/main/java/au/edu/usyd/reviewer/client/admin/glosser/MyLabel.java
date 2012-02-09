package au.edu.usyd.reviewer.client.admin.glosser;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Label;

public class MyLabel extends Label {

	private String width;

	private String fieldName;

	private int row;

	private int column;

	public MyLabel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyLabel(Element element) {
		super(element);
		// TODO Auto-generated constructor stub
	}

	public MyLabel(String text) {
		super(text);
		// TODO Auto-generated constructor stub
	}

	public MyLabel(String text, boolean wordWrap) {
		super(text, wordWrap);
		// TODO Auto-generated constructor stub
	}

	public int getColumn() {
		return column;
	}

	public String getFieldName() {
		return fieldName;
	}

	public int getRow() {
		return row;
	}

	public String getWidth() {
		return width;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setRow(int row) {
		this.row = row;
	}

	@Override
	public void setWidth(String width) {
		super.setWidth(width);
		this.width = width;
	}
}
