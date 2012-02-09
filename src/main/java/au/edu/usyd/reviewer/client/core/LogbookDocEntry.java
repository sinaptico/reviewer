package au.edu.usyd.reviewer.client.core;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class LogbookDocEntry extends DocEntry {

	private static final long serialVersionUID = 1L;
	@OneToMany(cascade = CascadeType.ALL)
	@IndexColumn(name = "page")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<LogpageDocEntry> pages = new ArrayList<LogpageDocEntry>();

	public List<LogpageDocEntry> getPages() {
		return pages;
	}

	public void setPages(List<LogpageDocEntry> pages) {
		this.pages = pages;
	}
}
