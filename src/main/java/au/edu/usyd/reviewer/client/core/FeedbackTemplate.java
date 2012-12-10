package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * <p>Class used for the semi-automated feedback given on the SpeedBack option, it includes 
 * details of the order(number) they appear in the SpeedBack panel as well as 2 possible descriptions 
 * that can be configured in the Review activity (Feedback Template Type).</p>
 */
@Entity
public class FeedbackTemplate implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Constant FEEDBACK_TYPE_DESCRIPTION_DEFAULT. */
	public static final String FEEDBACK_TYPE_DESCRIPTION_DEFAULT = "Default";
	
	/** The Constant FEEDBACK_TYPE_DESCRIPTION_A. */
	public static final String FEEDBACK_TYPE_DESCRIPTION_A = "A";
	
	/** The Constant FEEDBACK_TYPE_DESCRIPTION_B. */
	public static final String FEEDBACK_TYPE_DESCRIPTION_B = "B";
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The number used for ordering the choices in the SpeedBack option. */
	private String number;
	
	/** The text. */
	private String text;
	
	/** The grade. */
	private String grade;	
	
	/** The grade num. */
	private int gradeNum = 0;

	/** The description a. */
	@Column(length = 65535)
	private String descriptionA;
	
	/** The description b. */
	@Column(length = 65535)
	private String descriptionB;

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FeedbackTemplate))
			return false;
		FeedbackTemplate other = (FeedbackTemplate) obj;
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

//	public void setLink(String link) {
//		this.link = link;
//	}
//
//	public String getLink() {
//		return link;
//	}

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
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
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
	 * Sets the grade.
	 *
	 * @param grade the new grade
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}

	/**
	 * Gets the grade.
	 *
	 * @return the grade
	 */
	public String getGrade() {
		return grade;
	}

	/**
	 * Sets the grade num.
	 *
	 * @param gradeNum the new grade num
	 */
	public void setGradeNum(int gradeNum) {
		this.gradeNum = gradeNum;
	}

	/**
	 * Gets the grade num.
	 *
	 * @return the grade num
	 */
	public int getGradeNum() {
		return gradeNum;
	}

	/**
	 * Sets the description a.
	 *
	 * @param descriptionA the new description a
	 */
	public void setDescriptionA(String descriptionA) {
		this.descriptionA = descriptionA;
	}

	/**
	 * Gets the description a.
	 *
	 * @return the description a
	 */
	public String getDescriptionA() {
		return descriptionA;
	}

	/**
	 * Sets the description b.
	 *
	 * @param descriptionB the new description b
	 */
	public void setDescriptionB(String descriptionB) {
		this.descriptionB = descriptionB;
	}

	/**
	 * Gets the description b.
	 *
	 * @return the description b
	 */
	public String getDescriptionB() {
		return descriptionB;
	}

	public FeedbackTemplate clone(){
		FeedbackTemplate template = new FeedbackTemplate();
		template.setDescriptionA(this.getDescriptionA());
		template.setDescriptionB(this.getDescriptionB());
		template.setGrade(this.getGrade());
		template.setGradeNum(this.getGradeNum());
		template.setId(this.getId());
		template.setNumber(this.getNumber());
		template.setText(this.getText());
		return template;
	}
}
