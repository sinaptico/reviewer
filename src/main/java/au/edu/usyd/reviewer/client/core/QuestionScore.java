package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class QuestionScore implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	private int QualityMeasure;
	@ManyToOne
	@JoinTable(name = "Question_Score")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Question question;
	@Column(name = "comment", length = 500, nullable = true)
	private String comment;
	private String produced;
	private int grade;

	public String getComment() {
		return comment;
	}

	public int getGrade() {
		return grade;
	}

	public Long getId() {
		return id;
	}

	public String getProduced() {
		return produced;
	}

	public int getQualityMeasure() {
		return QualityMeasure;
	}

	public Question getQuestion() {
		return question;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProduced(String produced) {
		this.produced = produced;
	}

	public void setQualityMeasure(int qualityMeasure) {
		QualityMeasure = qualityMeasure;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public QuestionScore clone(){
		QuestionScore qs = new QuestionScore();
		qs.setComment(this.getComment());
		qs.setGrade(this.getGrade());
		qs.setId(this.getId());
		qs.setProduced(this.getProduced());
		qs.setQualityMeasure(this.getQualityMeasure());
		
		if(this.getQuestion()!=null){
			qs.setQuestion(this.getQuestion().clone());
		}
		
		return qs;
		
	}
}
