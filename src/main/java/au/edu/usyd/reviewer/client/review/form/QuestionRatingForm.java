package au.edu.usyd.reviewer.client.review.form;

import java.util.ArrayList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Question;
import au.edu.usyd.reviewer.client.core.QuestionRating;
import au.edu.usyd.reviewer.client.core.QuestionScore;
import au.edu.usyd.reviewer.client.review.ReviewService;
import au.edu.usyd.reviewer.client.review.ReviewServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QuestionRatingForm extends RatingForm<QuestionRating> {
	private ReviewServiceAsync reviewService = (ReviewServiceAsync) GWT.create(ReviewService.class);
	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel feedbackPanel = new HorizontalPanel();
	private HorizontalPanel btnsPane = new HorizontalPanel();
	private FlexTable questionFlexTable = new FlexTable();
	private FlexTable mainTable = new FlexTable();
	private ArrayList<String> qualitymeasurelist = new ArrayList<String>();
	private ArrayList<QuestionScore> scorelist = new ArrayList<QuestionScore>();
	private int[] producerlist = new int[40];
	private int[] gradelist = new int[150];
	private List<Question> questionlist;
	private ListBox speakerbglistbox;
	Button bntSubmit = new Button("Submit");
	Button bntClose = new Button("Close");

	public QuestionRatingForm() {
		initWidget(mainPanel);
	}

	public native void closeWindow()
	/*-{
		$wnd.close();
	}-*/;

	@Override
	public QuestionRating getRating() {
		return rating;
	}

	@Override
	public void onLoad() {
		HTML instructions = new HTML("The purpose of the following questions, as a form of feedback, is not only to trigger your reflection on but also improve your academic writing. \n We want to get your feedback on the quality of these questions. So please read, and identify who you believe who wrote the question and score each question as described below.\n The questions might have been written by one of the following:<ul><li> <b>Supervisor</b>: your research supervisor.</li> <li><b> Student/Peer</b>: research students enrolled in this course. </li> <li><b>Computer Generated</b>: Intelligent Automatic Question Generation tool we developed.</li> <li><b>Standard Question List</b>: This list contains generic trigger questions which we obtained from learning material, e.g. What is the specific thesis, problem, or research question that my literature review helps to define? Is it clearly defined? Is its significance (scope, severity, relevance) clearly established?</li></ul>");
		// mainPanel.add(instructions);
		mainTable.setWidget(0, 0, instructions);
		mainTable.setWidth("600px");
		mainTable.setCellSpacing(20);

		// mainTable.setBorderWidth(10);
		questionFlexTable.getRowFormatter().addStyleName(0, "questionHeader");
		questionFlexTable.addStyleName("questionList");
		questionFlexTable.setText(0, 0, "ID");
		questionFlexTable.setText(0, 1, "Question");

		// setup the feedback
		feedbackPanel.add(new Label("Please input all the required field!"));
		feedbackPanel.setStyleName("feedbackstyle");
		feedbackPanel.setVisible(false);

		bntClose.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeWindow();
			}
		});
		bntSubmit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				// Element hi=DOM.getElementById("listbox1");

				final NodeList<Element> formFields = Document.get().getElementsByTagName("INPUT");
				for (int i = 0; i < formFields.getLength(); i++) {
					InputElement inputelement = (InputElement) formFields.getItem(i);
					String name = inputelement.getName();
					//String value = inputelement.getValue();
					inputelement.setId("radio" + i);
					Boolean isChecked = inputelement.isChecked();
					System.out.println("name:" + name + "IsChecked" + isChecked);

				}
				/*
				 * for(int i = 0; i < formFields.getLength(); i++) { Element
				 * radioelement=(Element) DOM.getElementById("radio"+i);
				 * InputElement radioeleinput=(InputElement) radioelement;
				 * System.out.println("name:"+radioeleinput.getId()+"IsChecked"+
				 * radioeleinput.isChecked()); }
				 */
				final NodeList<Element> selectFields = Document.get().getElementsByTagName("Select");
				for (int i = 0; i < selectFields.getLength() - 1; i++) {
					SelectElement selectlement = (SelectElement) selectFields.getItem(i);
					int index = selectlement.getSelectedIndex();
					if (index == 0) {
						selectlement.focus();
						feedbackPanel.setVisible(true);
						return;
					} else {
						// TODO store one question generator value
						producerlist[i] = index;
						System.out.println("selected index:" + index);

					}

					// flag 1 shows a radiobutton have selected

					InputElement focusElement = null;
					for (int j = 0; j < 5; j++) {
						int flag = 0;
						for (int f = 0; f < 5; f++) {
							int indexall = 25 * i + 5 * j + f;
							int indexf = i * 5 + j;

							Element radioelement = DOM.getElementById("radio" + indexall);
							InputElement radioeleinput = (InputElement) radioelement;
							// if a radio button is checked, get the value
							if (radioeleinput.isChecked()) {
								int score = f + 1;
								int questionnum = i + 1;
								//int measure = j + 1;
								// ToDo store one quality measure value
								System.out.println("question:" + questionnum + " qualitymeasure:" + questionnum + " score:" + score);
								gradelist[indexf] = score;
								flag = 1;
							}
							focusElement = radioeleinput;
						}
						if (flag == 0) {
							focusElement.focus();
							feedbackPanel.setVisible(true);
							return;
						}
					}
				}
				feedbackPanel.setVisible(false);
				// create the score object and list
				for (int i = 0; i < questionlist.size(); i++) {
					// each question has serveral quality measures
					for (int j = 0; j < qualitymeasurelist.size(); j++) {
						QuestionScore questionScore = new QuestionScore();
						questionScore.setQuestion(questionlist.get(i));
						questionScore.setQualityMeasure(j + 1);
						questionScore.setProduced(String.valueOf(producerlist[i]));
						int indexgrade = (i * qualitymeasurelist.size() + j);
						questionScore.setGrade(gradelist[indexgrade]);
						questionScore.setComment("its hard to tell who the question producer is.");
						scorelist.add(questionScore);
					}
				}
				// set the evaluator's background info.
				if (speakerbglistbox.getSelectedIndex() == 1) {
					rating.setEvaluatorBackground("Yes");
				} else if (speakerbglistbox.getSelectedIndex() == 2) {
					rating.setEvaluatorBackground("No");
				} else {
					speakerbglistbox.setFocus(true);
					feedbackPanel.setVisible(true);
					return;
				}

				// insert operation
				rating.setScores(scorelist);
				reviewService.submitRating(rating, null, new AsyncCallback<QuestionRating>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to submit!");
					}

					@Override
					public void onSuccess(QuestionRating questionRating) {
						Window.alert("Thanks for your scoring!");
						closeWindow();
					}
				});
			}
		});
		btnsPane.add(bntSubmit);
		btnsPane.add(bntClose);
		HorizontalPanel additionalCommentPanel = new HorizontalPanel();

		additionalCommentPanel.add(new Label("Are you a native English speaker?"));
		speakerbglistbox = new ListBox();
		speakerbglistbox.addItem("");
		speakerbglistbox.addItem("Yes");
		speakerbglistbox.addItem("No");
		additionalCommentPanel.add(speakerbglistbox);
		mainTable.setWidget(1, 0, feedbackPanel);
		mainTable.setWidget(2, 0, questionFlexTable);
		mainTable.setWidget(3, 0, additionalCommentPanel);
		mainTable.setWidget(4, 0, btnsPane);
		mainPanel.add(mainTable);
	}

	@Override
	public void setRating(QuestionRating rating) {
		this.rating = rating;

		List<Question> questions = rating.getTriggerQuestions();
		if (questions == null) {
			Window.alert("Sorry, we don't have the document in our database!");
			closeWindow();
		}
		qualitymeasurelist.add("QM1: This question is correctly written.");
		qualitymeasurelist.add("QM2: This question is clear.");
		qualitymeasurelist.add("QM3: This question is appropriate to the context.");
		qualitymeasurelist.add("QM4: This question makes me reflect about what I have written");
		qualitymeasurelist.add("QM5: This is a useful question");
		// assign the list to quesionlist
		questionlist = questions;
		// j shows the seq number
		int j = 1;
		for (int i = 1; i < questions.size() + 1; i++) {
			Label text = new Label();
			Question question = questions.get(i - 1);
			text.setText(question.getQuestion());

			// text.setText(questions.get(i));
			questionFlexTable.setText(j, 0, "Q" + Integer.toString(i) + ":");
			HorizontalPanel questionPanel = new HorizontalPanel();
			questionPanel.add(text);
			questionFlexTable.setWidget(j, 1, questionPanel);
			FlexTable qualitymeasureTab = new FlexTable();
			qualitymeasureTab.addStyleName("questionList");
			for (int k = 0; k < qualitymeasurelist.size(); k++) {
				Label lblqm = new Label();

				lblqm.setText(qualitymeasurelist.get(k));
				HorizontalPanel radioPanel = new HorizontalPanel();
				RadioButton radio0 = new RadioButton(Integer.toString(i) + Integer.toString(k), "1 (worst)");
				RadioButton radio1 = new RadioButton(Integer.toString(i) + Integer.toString(k), "2");
				RadioButton radio2 = new RadioButton(Integer.toString(i) + Integer.toString(k), "3");
				RadioButton radio3 = new RadioButton(Integer.toString(i) + Integer.toString(k), "4");
				RadioButton radio4 = new RadioButton(Integer.toString(i) + Integer.toString(k), "5 (best)");
				radioPanel.add(radio0);
				radioPanel.add(radio1);
				radioPanel.add(radio2);
				radioPanel.add(radio3);
				radioPanel.add(radio4);
				VerticalPanel qmPanel = new VerticalPanel();
				qmPanel.add(lblqm);
				qmPanel.add(radioPanel);
				qualitymeasureTab.setWidget(k, 0, qmPanel);
				// questionFlexTable.setWidget(j+1, 1, qmPanel);
				// questionFlexTable.getCellFormatter().addStyleName(j+1,
				// 1, "qualityMeasureList");
				// j=j+2;
			}
			questionFlexTable.setWidget(j + 1, 1, qualitymeasureTab);
			questionFlexTable.getCellFormatter().addStyleName(j + 1, 1, "qualityMeasureList");
			j = j + 2;
			ListBox listbox = new ListBox();
			listbox.setName("listbox" + Integer.toString(i));
			listbox.addItem("");
			listbox.addItem("Supervisor");
			listbox.addItem("Student/Peer");
			listbox.addItem("Computer Generated");
			Label lbl = new Label();
			lbl.setText("Who do you think wrote this question?");
			HorizontalPanel listpanel = new HorizontalPanel();
			listpanel.add(lbl);
			listpanel.add(listbox);
			questionFlexTable.setWidget(j, 1, listpanel);
			questionFlexTable.getCellFormatter().addStyleName(j, 1, "qualityMeasureList");
			j = j + 1;
		}
	}
}
