package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import au.edu.usyd.reviewer.client.core.util.StringUtil;

/**
 * <p>Class used to save the reviews that are given by peers/tutors/lecturers, it includes the content
 * and date when it's saved as well as the feedback templates that were used to write it when tutor/lectures use the Speed Back option.</p>
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(ReviewingActivity.REVIEW_TYPE_COMMENTS)
public class Review implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The content. */
	@Column(length = 65535)
	protected String content;
	
	/** The saved Date. */
	protected Date saved;
	
	/** Flag to show if it was submitted before the deadline. */
	protected Boolean earlySubmitted = false;
	
	/** The feedback templates used on the 'Speed Back' option. */
	@ManyToMany
	@JoinTable(name = "Review_FeedbackTemplates")
	@LazyCollection(LazyCollectionOption.FALSE)
	@javax.persistence.OrderBy("number")
	private Set<FeedbackTemplate> feedback_templates = new HashSet<FeedbackTemplate>();
		
	/** The feedback template type. */
	private String feedbackTemplateType = FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_DEFAULT;

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Review))
			return false;
		Review other = (Review) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Gets the saved.
	 *
	 * @return the saved
	 */
	public Date getSaved() {
		return saved;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Checks if is blank.
	 *
	 * @return true, if is blank
	 */
	public boolean isBlank() {
		return StringUtil.isBlank(content);
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the saved.
	 *
	 * @param saved the new saved
	 */
	public void setSaved(Date saved) {
		this.saved = saved;
	}

	/**
	 * Gets the early submitted.
	 *
	 * @return the early submitted
	 */
	public Boolean getEarlySubmitted() {
		return earlySubmitted;
	}

	/**
	 * Sets the early submitted.
	 *
	 * @param earlySubmitted the new early submitted
	 */
	public void setEarlySubmitted(Boolean earlySubmitted) {
		this.earlySubmitted = earlySubmitted;
	}

	/**
	 * Sets the feedback_templates.
	 *
	 * @param feedback_templates the new feedback_templates
	 */
	public void setFeedback_templates(Set<FeedbackTemplate> feedback_templates) {
		this.feedback_templates = feedback_templates;
	}

	/**
	 * Gets the feedback_templates.
	 *
	 * @return the feedback_templates
	 */
	public Set<FeedbackTemplate> getFeedback_templates() {
		return feedback_templates;
	}

	/**
	 * Sets the feedback template type.
	 *
	 * @param feedbackTemplateType the new feedback template type
	 */
	public void setFeedbackTemplateType(String feedbackTemplateType) {
		this.feedbackTemplateType = feedbackTemplateType;
	}

	/**
	 * Gets the feedback template type.
	 *
	 * @return the feedback template type
	 */
	public String getFeedbackTemplateType() {
		return feedbackTemplateType;
	}
	
	public Review clone(){
		Review review = new Review();
		review.setContent(this.getContent());
		review.setEarlySubmitted(this.getEarlySubmitted());
		
		Set<FeedbackTemplate> templates = new HashSet<FeedbackTemplate>();
		for( FeedbackTemplate template:this.getFeedback_templates()){
			if ( template != null){
				templates.add(template.clone());
			}
		}
		review.setFeedback_templates(templates);
		
		review.setFeedbackTemplateType(this.getFeedbackTemplateType());
		
		review.setId(this.getId());	
		review.setSaved(this.getSaved());
		return review;
	}
}
