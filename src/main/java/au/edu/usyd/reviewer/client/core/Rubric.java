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


@Entity
public class Rubric implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	protected Long id;
	private String number;
	private String name;
	private String link;	
	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "Rubric_FeedbackTemplates")
	@javax.persistence.OrderBy("number")
	private List<FeedbackTemplate> feedbackTemplates = new ArrayList<FeedbackTemplate>();	

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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}   

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFeedbackTemplates(List<FeedbackTemplate> feedbackTemplates) {
		this.feedbackTemplates = feedbackTemplates;
	}

	public List<FeedbackTemplate> getFeedbackTemplates() {
		return feedbackTemplates;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLink() {
		return link;
	}

}
