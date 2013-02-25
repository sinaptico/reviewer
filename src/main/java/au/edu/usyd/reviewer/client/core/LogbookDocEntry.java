package au.edu.usyd.reviewer.client.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
	
	public LogbookDocEntry clone(){
		LogbookDocEntry log = new LogbookDocEntry();
		log.setDeleted(this.isDeleted());
		log.setDocumentId(this.getDocumentId());
		log.setDomainName(this.getDomainName());
		log.setDownloaded(this.getDownloaded());
		log.setEarlySubmitDate(this.getEarlySubmitDate());
		log.setFileName(this.getFileName());
		log.setId(this.getId());
		log.setLocalFile(this.isLocalFile());
		log.setLocked(this.getLocked());
		
		if (this.getOwner() != null){
			log.setOwner(this.getOwner().clone());
		}
		
		if (this.getOwnerGroup()!=null){
			log.setOwnerGroup(this.getOwnerGroup().clone());
		}
		
		List<LogpageDocEntry> pagesList = new ArrayList<LogpageDocEntry>();
		for(LogpageDocEntry page:this.getPages()){
			if (page != null){
				pagesList.add(page.clone());
			}
		}
		log.setPages(pagesList);
		
		Set<Review> reviewSet = new HashSet<Review>();
		for(Review review: this.getReviews()){
			if(review!= null){
				reviewSet.add(review.clone());
			}
		}
		log.setReviews(reviewSet);
		
		log.setTitle(this.getTitle());
		log.setUploaded(this.isUploaded());
		
		return log;
	}
}
