package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


/**
 * <p>Class to define Rubrics to be used in the SppedBack functionality, it contains number(for sorting), name and link to iWrite tutorials.</p>
 */
@Entity
public class Rubric implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The number. */
	private String number;
	
	/** The name. */
	private String name;
	
	/** The iWrite tutorial link. */
	private String link;	
	
	/** The feedback templates. */
	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "Rubric_FeedbackTemplates")
	@javax.persistence.OrderBy("number")
	private List<FeedbackTemplate> feedbackTemplates = new ArrayList<FeedbackTemplate>();	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Rubric))
			return false;
		Rubric other = (Rubric) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the feedback templates.
	 *
	 * @param feedbackTemplates the new feedback templates
	 */
	public void setFeedbackTemplates(List<FeedbackTemplate> feedbackTemplates) {
		this.feedbackTemplates = feedbackTemplates;
	}

	/**
	 * Gets the feedback templates.
	 *
	 * @return the feedback templates
	 */
	public List<FeedbackTemplate> getFeedbackTemplates() {
		return feedbackTemplates;
	}

	/**
	 * Sets the number.
	 *
	 * @param number the new number
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the link.
	 *
	 * @param link the new link
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Gets the link.
	 *
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

}
