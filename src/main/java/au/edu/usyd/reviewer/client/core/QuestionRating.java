package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.List;

public class QuestionRating extends Rating implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Question> questions = null;
	private List<QuestionScore> questionScores = null;
	private String evaluatorBackground = null;

	public String getEvaluatorBackground() {
		return evaluatorBackground;
	}

	public List<QuestionScore> getScores() {
		return questionScores;
	}

	public List<Question> getTriggerQuestions() {
		return questions;
	}

	public void setEvaluatorBackground(String evaluatorBackground) {
		this.evaluatorBackground = evaluatorBackground;
	}

	public void setScores(List<QuestionScore> questionScores) {
		this.questionScores = questionScores;
	}

	public void setTriggerQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
