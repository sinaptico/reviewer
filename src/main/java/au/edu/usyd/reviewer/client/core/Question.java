package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

@Entity
public class Question implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	@JoinTable(name = "Question_Owner")
	private User owner;
	private String docId;
	@Column(name = "Question", length = 500, nullable = true)
	private String Question;
	@Column(name = "sourceSentence", length = 500, nullable = true)
	private String sourceSentence;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Question))
			return false;
		Question other = (Question) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getDocId() {
		return docId;
	}

	public Long getId() {
		return id;
	}

	public User getOwner() {
		return owner;
	}

	public String getQuestion() {
		return Question;
	}

	public String getSourceSentence() {
		return sourceSentence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setQuestion(String question) {
		Question = question;
	}

	public void setSourceSentence(String sourceSentence) {
		this.sourceSentence = sourceSentence;
	}

	public Question clone(){
		Question question = new Question();
		question.setDocId(this.getDocId());
		question.setId(this.getId());
		
		if ( this.getOwner() != null){
			question.setOwner(this.getOwner().clone());
		}
		
		question.setQuestion(this.getQuestion());
		question.setSourceSentence(this.getSourceSentence());
		return question;
	}
}
