package au.edu.usyd.reviewer.client.core;

import java.util.Date
;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>Class that extends {@link Entry Entry} class and it is used for document management, it contains
 * the document id (Google Doc), owner(s) and the date it was submitted (if early submission is allowed)</>.
 * 
 */
@Entity
public class DocEntry extends Entry {

	
	/*
	 *  * @opt attributes
	 * @opt operations
	 */
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The domain name. */
	@Column
	protected String domainName;
	
	/** The document id. */
	@Column(unique = true)
	protected String documentId;
	
	/** The reviews. */
	@OneToMany(cascade = CascadeType.REMOVE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "DocEntry_Reviews_Review")
	protected Set<Review> reviews = new HashSet<Review>();
	
	/** The owner. */
	@ManyToOne
	protected User owner;
	
	/** The owner group. */
	@ManyToOne
	protected UserGroup ownerGroup;	
	
	/** The early submit date. */
	protected Date earlySubmitDate = null;	

	/* (non-Javadoc)
	 * @see au.edu.usyd.reviewer.client.core.Entry#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DocEntry))
			return false;
		DocEntry other = (DocEntry) obj;
		if (documentId == null) {
			if (other.documentId != null)
				return false;
		} else if (!documentId.equals(other.documentId))
			return false;
		return true;
	}

	/**
	 * Gets the document id.
	 *
	 * @return the document id
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Gets the owner group.
	 *
	 * @return the owner group
	 */
	public UserGroup getOwnerGroup() {
		return ownerGroup;
	}

	/**
	 * Gets the reviews.
	 *
	 * @return the reviews
	 */
	public Set<Review> getReviews() {
		return reviews;
	}

	/* (non-Javadoc)
	 * @see au.edu.usyd.reviewer.client.core.Entry#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentId == null) ? 0 : documentId.hashCode());
		return result;
	}

	/**
	 * Sets the document id.
	 *
	 * @param documentId the new document id
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * Sets the owner group.
	 *
	 * @param ownerGroup the new owner group
	 */
	public void setOwnerGroup(UserGroup ownerGroup) {
		this.ownerGroup = ownerGroup;
	}

	/**
	 * Sets the reviews.
	 *
	 * @param reviews the new reviews
	 */
	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	/**
	 * Gets the domain name.
	 *
	 * @return the domain name
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * Sets the domain name.
	 *
	 * @param domainName the new domain name
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * Gets the early submit date.
	 *
	 * @return the early submit date
	 */
	public Date getEarlySubmitDate() {
		return earlySubmitDate;
	}

	/**
	 * Sets the early submit date.
	 *
	 * @param earlySubmitDate the new early submit date
	 */
	public void setEarlySubmitDate(Date earlySubmitDate) {
		this.earlySubmitDate = earlySubmitDate;
	}
	
	
	public DocEntry clone(){
		DocEntry doc = new DocEntry();
		
		doc.setDocumentId(this.getDocumentId());
		doc.setDomainName(this.getDomainName());
		doc.setDownloaded(this.getDownloaded());
		doc.setEarlySubmitDate(this.getEarlySubmitDate());
		doc.setFileName(this.getFileName());
		doc.setId(this.getId());
		doc.setLocalFile(this.isLocalFile());
		doc.setLocked(this.getLocked());
		
		if ( this.getOwner() != null){
			doc.setOwner(this.getOwner().clone());
		}
		
		if (this.getOwnerGroup() != null){
			doc.setOwnerGroup(this.getOwnerGroup().clone());
		}
		
		Set<Review> reviews = new HashSet<Review>();
		for(Review review:this.getReviews()){
			if (review != null){
				reviews.add(review.clone());
			}
		}
		doc.setReviews(reviews);
		
		doc.setTitle(this.getTitle());
		doc.setUploaded(this.isUploaded());
		doc.setDeleted(this.isDeleted());
		
		return doc;
	}
}
