package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * <p>Class to handle responses to {@link ReviewTemplate Review Templates}, it includes the section, 
 * choice(if it's a multiple selection question), the written text and the mark given by the tutor/lecturer.</p>
 */
@Entity
public class TemplateReply implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;	
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The review template. */
	@ManyToOne
	protected ReviewTemplate reviewTemplate;
	
	/** The section. */
	@ManyToOne
	protected Section section;
	
	/** The choice(if it's a multiple selection question). */
	protected String choice; 
	
	/** The text. */
	@Column(length = 65535)
	protected String text;
	
	/** The mark. */
	protected Integer mark;

	

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
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
	 * Gets the review template.
	 *
	 * @return the review template
	 */
	public ReviewTemplate getReviewTemplate() {
		return reviewTemplate;
	}

	/**
	 * Sets the review template.
	 *
	 * @param reviewTemplate the new review template
	 */
	public void setReviewTemplate(ReviewTemplate reviewTemplate) {
		this.reviewTemplate = reviewTemplate;
	}

	/**
	 * Gets the section.
	 *
	 * @return the section
	 */
	public Section getSection() {
		return section;
	}

	/**
	 * Sets the section.
	 *
	 * @param section the new section
	 */
	public void setSection(Section section) {
		this.section = section;
	}

	/**
	 * Gets the choice.
	 *
	 * @return the choice
	 */
	public String getChoice() {
		return choice;
	}

	/**
	 * Sets the choice.
	 *
	 * @param choice the new choice
	 */
	public void setChoice(String choice) {
		this.choice = choice;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets the mark.
	 *
	 * @return the mark
	 */
	public Integer getMark() {
		return mark;
	}

	/**
	 * Sets the mark.
	 *
	 * @param mark the new mark
	 */
	public void setMark(Integer mark) {
		this.mark = mark;
	}
	
}
