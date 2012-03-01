package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FeedbackTemplate implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String FEEDBACK_TYPE_DESCRIPTION_DEFAULT = "Default";
	public static final String FEEDBACK_TYPE_DESCRIPTION_A = "A";
	public static final String FEEDBACK_TYPE_DESCRIPTION_B = "B";
	
	@Id
	@GeneratedValue
	protected Long id;
	private String number;
	private String text;
	private String grade;	
	private int gradeNum;
	//private String link;
	@Column(length = 65535)
	private String descriptionA;
	@Column(length = 65535)
	private String descriptionB;

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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}   

	public Long getId() {
		return id;
	}

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

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGrade() {
		return grade;
	}

	public void setGradeNum(int gradeNum) {
		this.gradeNum = gradeNum;
	}

	public int getGradeNum() {
		return gradeNum;
	}

	public void setDescriptionA(String descriptionA) {
		this.descriptionA = descriptionA;
	}

	public String getDescriptionA() {
		return descriptionA;
	}

	public void setDescriptionB(String descriptionB) {
		this.descriptionB = descriptionB;
	}

	public String getDescriptionB() {
		return descriptionB;
	}

}
