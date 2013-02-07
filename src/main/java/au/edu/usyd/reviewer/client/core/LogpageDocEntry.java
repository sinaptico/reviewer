package au.edu.usyd.reviewer.client.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import javax.persistence.Entity;

/**
 * Class used to track the pages submitted to a Log Book.
 */
@Entity
public class LogpageDocEntry extends DocEntry {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The submitted. */
	private Date submitted = null;

	/**
	 * Gets the submitted.
	 *
	 * @return the submitted
	 */
	public Date getSubmitted() {
		return submitted;
	}

	/**
	 * Sets the submitted.
	 *
	 * @param submitted the new submitted
	 */
	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}
	
	public LogpageDocEntry clone(){
		LogpageDocEntry log = new LogpageDocEntry();
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
		
		if (this.getOwnerGroup()!= null){
			log.setOwnerGroup(this.getOwnerGroup().clone());
		}
		
		Set<Review> reviewSet = new HashSet<Review>();
		for(Review review:this.getReviews()){
			if(review!=null){
				reviewSet.add(review.clone());
			}
		}
		log.setReviews(reviewSet);
		
		log.setSubmitted(this.getSubmitted());
		log.setTitle(this.getTitle());
		log.setUploaded(this.isUploaded());
		
		return log;
	}
}
