package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;

@Entity
public class TemplateReply implements Serializable {

	private static final long serialVersionUID = 1L;	
	@Id
	@GeneratedValue
	protected Long id;
	@ManyToOne
	protected ReviewTemplate reviewTemplate;
	@ManyToOne
	protected Section section;
	protected String choice; 
	@Column(length = 65535)
	protected String text;
	protected Integer mark;

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public ReviewTemplate getReviewTemplate() {
		return reviewTemplate;
	}

	public void setReviewTemplate(ReviewTemplate reviewTemplate) {
		this.reviewTemplate = reviewTemplate;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}
	
}
