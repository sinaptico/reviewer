package au.edu.usyd.reviewer.client.core;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class ReviewTemplateEntry extends Entry {

	private static final long serialVersionUID = 1L;

	@OneToOne
	protected TemplateReply reviewReply;
	@ManyToOne
	protected ReviewTemplate reviewTemplate;
	@ManyToOne
	protected User owner;
	
	
	public TemplateReply getReviewReply() {
		return reviewReply;
	}
	public void setReviewReply(TemplateReply reviewReply) {
		this.reviewReply = reviewReply;
	}
	public ReviewTemplate getReviewTemplate() {
		return reviewTemplate;
	}
	public void setReviewTemplate(ReviewTemplate reviewTemplate) {
		this.reviewTemplate = reviewTemplate;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}

	
	
	
}
