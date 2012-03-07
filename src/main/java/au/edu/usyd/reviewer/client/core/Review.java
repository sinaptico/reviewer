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

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(ReviewingActivity.REVIEW_TYPE_COMMENTS)
public class Review implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	protected Long id;
	@Column(length = 65535)
	protected String content;
	protected Date saved;
	protected Boolean earlySubmitted = false;
	@ManyToMany
	@JoinTable(name = "Review_FeedbackTemplates")
	@LazyCollection(LazyCollectionOption.FALSE)
	@javax.persistence.OrderBy("number")
	private Set<FeedbackTemplate> feedback_templates = new HashSet<FeedbackTemplate>();
	private String feedbackTemplateType = FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_DEFAULT;

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

	public String getContent() {
		return content;
	}

	public Long getId() {
		return id;
	}

	public Date getSaved() {
		return saved;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public boolean isBlank() {
		return StringUtil.isBlank(content);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSaved(Date saved) {
		this.saved = saved;
	}

	public Boolean getEarlySubmitted() {
		return earlySubmitted;
	}

	public void setEarlySubmitted(Boolean earlySubmitted) {
		this.earlySubmitted = earlySubmitted;
	}

	public void setFeedback_templates(Set<FeedbackTemplate> feedback_templates) {
		this.feedback_templates = feedback_templates;
	}

	public Set<FeedbackTemplate> getFeedback_templates() {
		return feedback_templates;
	}

	public void setFeedbackTemplateType(String feedbackTemplateType) {
		this.feedbackTemplateType = feedbackTemplateType;
	}

	public String getFeedbackTemplateType() {
		return feedbackTemplateType;
	}
}
