package au.edu.usyd.reviewer.client.core;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>Class used for Log Book management that includes a list of pages from the class {@link LogpageDocEntry LogpageDocEntry}.</p>
 */
@Entity
public class LogbookDocEntry extends DocEntry {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The pages. */
	@OneToMany(cascade = CascadeType.ALL)
	@IndexColumn(name = "page")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<LogpageDocEntry> pages = new ArrayList<LogpageDocEntry>();

	/**
	 * Gets the pages.
	 *
	 * @return the pages
	 */
	public List<LogpageDocEntry> getPages() {
		return pages;
	}

	/**
	 * Sets the pages.
	 *
	 * @param pages the new pages
	 */
	public void setPages(List<LogpageDocEntry> pages) {
		this.pages = pages;
	}
}
