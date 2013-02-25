package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import java.util.ArrayList;
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
	
	public QuestionRating clone(){
		QuestionRating qr = new QuestionRating();

		Entry entry = qr.getEntry();
		if (entry != null){
			if (entry instanceof DocEntry){
				qr.setEntry( ((DocEntry) entry).clone());
			} else if(entry instanceof LogbookDocEntry){
				qr.setEntry( ((LogbookDocEntry) entry).clone());
			} else if(entry instanceof LogpageDocEntry){
				qr.setEntry( ((LogpageDocEntry) entry).clone());
			} else if(entry instanceof ReviewEntry){
				qr.setEntry( ((ReviewEntry) entry).clone());
			} else if(entry instanceof ReviewTemplateEntry){
				qr.setEntry( ((ReviewTemplateEntry) entry).clone());
			}
		}
		
		qr.setEvaluatorBackground(this.getEvaluatorBackground());
		qr.setId(this.getId());
		
		if(qr.getOwner() !=null){
			qr.setOwner(this.getOwner().clone());
		}
		
		List<QuestionScore> scoreList = new ArrayList<QuestionScore>();
		for(QuestionScore qs : this.getScores()){
			if(qs!=null){
				scoreList.add(qs.clone());
			}
		}
		qr.setScores(scoreList);
		
		List<Question> questionList = new ArrayList<Question>();
		for(Question q: this.getTriggerQuestions()){
			if(q!=null){
				questionList.add(q.clone());
			}
		}
		
		qr.setTriggerQuestions(questionList);
		
		return qr;
	}
}
