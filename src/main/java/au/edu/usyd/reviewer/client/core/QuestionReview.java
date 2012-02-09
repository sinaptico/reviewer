package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import au.edu.usyd.reviewer.client.core.util.StringUtil;

@Entity
@DiscriminatorValue(ReviewingActivity.REVIEW_TYPE_QUESTION)
public class QuestionReview extends Review implements Serializable {

	private static final long serialVersionUID = 1L;
	@OneToMany
	@IndexColumn(name = "questionIndex")
	@Cascade(CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Question> questions = new ArrayList<Question>();

	public List<Question> getQuestions() {
		return questions;
	}

	@Override
	public boolean isBlank() {
		for (Question question : questions) {
			if (StringUtil.isNotBlank(question.getQuestion())) {
				return false;
			}
		}
		return StringUtil.isBlank(content);
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
