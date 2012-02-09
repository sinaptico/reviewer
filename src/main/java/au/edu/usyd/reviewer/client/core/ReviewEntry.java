package au.edu.usyd.reviewer.client.core;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class ReviewEntry extends Entry {

	private static final long serialVersionUID = 1L;

	@OneToOne
	protected Review review;
	@ManyToOne
	protected DocEntry docEntry;
	@ManyToOne
	protected User owner;

	public DocEntry getDocEntry() {
		return docEntry;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public Review getReview() {
		return review;
	}
	
	public void setDocEntry(DocEntry docEntry) {
		this.docEntry = docEntry;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setReview(Review review) {
		this.review = review;
	}
}
