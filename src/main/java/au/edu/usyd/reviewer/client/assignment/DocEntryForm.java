package au.edu.usyd.reviewer.client.assignment;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.gwt.DocEntryWidget;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <p>The Class DocEntryForm used from the instructor panel to lock/un-lock specific documents.</p>
 */
public class DocEntryForm extends Composite {

	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/** CheckBox to be use for lock/un-lock documents. */
	private final CheckBox locked = new CheckBox();
	
	/** The authors of the document. */
	private final HTML authors = new HTML();
	
	/** SimplePanel where the link to the document is added. */
	private final SimplePanel document = new SimplePanel();
	
	/** The document entry. */
	private DocEntry docEntry;

	
	/**
	 * Instantiates a new doc entry form.
	 */
	public DocEntryForm() {
		initWidget(mainPanel);
	}

	/**
	 * Gets the doc entry.
	 *
	 * @return the doc entry
	 */
	public DocEntry getDocEntry() {
		docEntry.setLocked(locked.getValue());
		return docEntry;
	}

	/** 
	 * <p>Main method of the form that loads the document link, authors and check box to update the document.</p>
	 */
	@Override
	public void onLoad() {
		Grid grid = new Grid(3, 2);
		grid.setWidget(0, 0, new Label("Document:"));
		grid.setWidget(0, 1, document);
		grid.setWidget(1, 0, new Label("Authors:"));
		grid.setWidget(1, 1, authors);
		grid.setWidget(2, 0, new Label("Locked:"));
		grid.setWidget(2, 1, locked);
		grid.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		mainPanel.add(grid);
		mainPanel.add(new HTML("<br/>"));
	}

	/**
	 * Sets the doc entry.
	 *
	 * @param docEntry the new doc entry
	 */
	public void setDocEntry(final DocEntry docEntry, final User loggedUser) {
		this.docEntry = docEntry;
		document.setWidget(new DocEntryWidget(docEntry.getDocumentId(), docEntry.getTitle(), docEntry.getDomainName(), docEntry.getLocked(),loggedUser));
		locked.setValue(docEntry.getLocked());
		locked.addValueChangeHandler(new ValueChangeHandler<Boolean>(){
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				document.setWidget(new DocEntryWidget(docEntry.getDocumentId(), docEntry.getTitle(), docEntry.getDomainName(), locked.getValue(),loggedUser));
			}});
		
		String users = new String();
		if (docEntry.getOwner() != null) {
			User user = docEntry.getOwner();
			users += user.getLastname() + ", " + user.getFirstname() + "<br/>";
		} else if (docEntry.getOwnerGroup() != null) {
			for (User user : docEntry.getOwnerGroup().getUsers()) {
				users += user.getUsername() + ": " + user.getLastname() + ", " + user.getFirstname() + "<br/>";
			}
		}
		authors.setHTML(users);
	}
}
