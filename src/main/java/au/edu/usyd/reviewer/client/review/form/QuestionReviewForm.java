package au.edu.usyd.reviewer.client.review.form;

import java.util.List;

import au.edu.usyd.reviewer.client.core.Question;
import au.edu.usyd.reviewer.client.core.QuestionReview;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QuestionReviewForm extends ReviewForm<QuestionReview> {
	private VerticalPanel mainPanel = new VerticalPanel();
	private TextArea contentField = new TextArea();
	private FlexTable questionFlexTable = new FlexTable();
	private TextArea inputbox1 = new TextArea();
	private TextArea inputbox2 = new TextArea();
	private TextArea inputbox3 = new TextArea();
	private TextArea inputbox4 = new TextArea();
	private TextArea inputbox5 = new TextArea();
	private Question question1 = new Question();
	private Question question2 = new Question();
	private Question question3 = new Question();
	private Question question4 = new Question();
	private Question question5 = new Question();
	private Label infoLabel = new Label();

	public QuestionReviewForm() {
		initWidget(mainPanel);
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		final HandlerRegistration handlerRegistration1 = inputbox1.addChangeHandler(handler);
		final HandlerRegistration handlerRegistration2 = inputbox2.addChangeHandler(handler);
		final HandlerRegistration handlerRegistration3 = inputbox3.addChangeHandler(handler);
		final HandlerRegistration handlerRegistration4 = inputbox4.addChangeHandler(handler);
		final HandlerRegistration handlerRegistration5 = inputbox5.addChangeHandler(handler);
		final HandlerRegistration handlerRegistration6 = contentField.addChangeHandler(handler);
		HandlerRegistration handlerRegistration = new HandlerRegistration() {
			@Override
			public void removeHandler() {
				handlerRegistration1.removeHandler();
				handlerRegistration2.removeHandler();
				handlerRegistration3.removeHandler();
				handlerRegistration4.removeHandler();
				handlerRegistration5.removeHandler();
				handlerRegistration6.removeHandler();
			}
		};
		return handlerRegistration;
	}

	@Override
	public QuestionReview getReview() {
		question1.setQuestion(inputbox1.getText());
		question2.setQuestion(inputbox2.getText());
		question3.setQuestion(inputbox3.getText());
		question4.setQuestion(inputbox4.getText());
		question5.setQuestion(inputbox5.getText());
		review.setContent(contentField.getText());
		return review;
	}

	@Override
	public boolean isModified() {
		return !inputbox1.getValue().equals(question1.getQuestion()) || !inputbox2.getValue().equals(question2.getQuestion()) || !inputbox3.getValue().equals(question3.getQuestion()) || !inputbox4.getValue().equals(question4.getQuestion()) || !inputbox5.getValue().equals(question5.getQuestion()) || !contentField.getValue().equals(review.getContent());
	}

	@Override
	public void onLoad() {
		infoLabel.setText("Please input five specific trigger questions based on the assignment. These questions, as a form of feedback, are not only to trigger the student's reflection on, but also improve, their academic writing.");
		infoLabel.setWidth("500px");
		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.add(new HTML("Please input any additional comments about the assignment."));
		contentPanel.add(contentField);
		contentField.setSize("500px", "160px");
		inputbox1.setSize("500px", "60px");
		inputbox2.setSize("500px", "60px");
		inputbox3.setSize("500px", "60px");
		inputbox4.setSize("500px", "60px");
		inputbox5.setSize("500px", "60px");
		questionFlexTable.setCellPadding(4);
		questionFlexTable.setText(0, 0, "Q1.");
		questionFlexTable.setText(1, 0, "Q2.");
		questionFlexTable.setText(2, 0, "Q3.");
		questionFlexTable.setText(3, 0, "Q4.");
		questionFlexTable.setText(4, 0, "Q5.");
		questionFlexTable.setWidget(0, 1, inputbox1);
		questionFlexTable.setWidget(1, 1, inputbox2);
		questionFlexTable.setWidget(2, 1, inputbox3);
		questionFlexTable.setWidget(3, 1, inputbox4);
		questionFlexTable.setWidget(4, 1, inputbox5);
		questionFlexTable.setWidget(5, 1, contentPanel);
		mainPanel.setSpacing(15);
		mainPanel.add(infoLabel);
		mainPanel.add(questionFlexTable);
	}

	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
		contentField.setEnabled(!locked);
		inputbox1.setEnabled(!locked);
		inputbox2.setEnabled(!locked);
		inputbox3.setEnabled(!locked);
		inputbox4.setEnabled(!locked);
		inputbox5.setEnabled(!locked);
	}

	@Override
	public void setReview(QuestionReview review) {
		this.review = review;
		contentField.setText(this.review.getContent());

		List<Question> questions = review.getQuestions();
		question1 = questions.get(0);
		inputbox1.setText(question1.getQuestion());
		question2 = questions.get(1);
		inputbox2.setText(question2.getQuestion());
		question3 = questions.get(2);
		inputbox3.setText(question3.getQuestion());
		question4 = questions.get(3);
		inputbox4.setText(question4.getQuestion());
		question5 = questions.get(4);
		inputbox5.setText(question5.getQuestion());
	}
}