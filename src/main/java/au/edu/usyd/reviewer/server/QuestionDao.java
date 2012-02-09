package au.edu.usyd.reviewer.server;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Question;
import au.edu.usyd.reviewer.client.core.QuestionScore;
import au.edu.usyd.reviewer.client.core.User;

public class QuestionDao {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private SessionFactory sessionFactory;

	public QuestionDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public List<Question> getQuestion(String docId) {
		logger.debug("Retrieving question: id=" + docId);
		String query = "select distinct question from Question question where docId=:docId";
		Session session = this.getSession();
		session.beginTransaction();
		List<Question> triggerQuestionList = session.createQuery(query).setParameter("docId", docId).list();
		session.getTransaction().commit();
		return triggerQuestionList;
	}

	public List<QuestionScore> getScore(Question question) {
		logger.debug("Retrieving score!");
		String query = "select distinct score from QuestionScore score" + " join fetch score.question question " + "where question=:question";
		Session session = this.getSession();
		session.beginTransaction();
		List<QuestionScore> questionScores = session.createQuery(query).setParameter("question", question).list();
		session.getTransaction().commit();
		return questionScores;
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public void saveQuestion(Question question) {

		logger.debug("Saving question");
		Session session = this.getSession();
		session.beginTransaction();
		session.saveOrUpdate(question);
		session.getTransaction().commit();
	}

	public void saveScore(QuestionScore questionScore) {
		logger.debug("Saving score!");
		Session session = this.getSession();
		session.beginTransaction();
		session.saveOrUpdate(questionScore);
		session.getTransaction().commit();
	}

	public void saveScoreAndDocOwner(QuestionScore questionScore, User user) {
		logger.debug("Saving score!");
		Session session = this.getSession();
		session.beginTransaction();
		session.update(user);
		session.saveOrUpdate(questionScore);
		session.getTransaction().commit();
	}

	public List<Question> searchQuestions(User user, String docId) {
		logger.debug("Geting Question List from user:" + user.getId() + "Doc:" + docId);
		Session session = this.getSession();
		session.beginTransaction();
		String query = "select distinct question from Question question join fetch question.owner owner " + "where docId=:docId AND owner=:user";
		Query queryObj = session.createQuery(query);
		queryObj.setParameter("docId", docId);
		queryObj.setParameter("user", user);
		List<Question> triggerQuestionList = queryObj.list();
		session.getTransaction().commit();
		return triggerQuestionList;
	}

}
