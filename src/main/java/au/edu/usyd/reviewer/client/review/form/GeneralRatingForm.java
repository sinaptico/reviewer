package au.edu.usyd.reviewer.client.review.form;

import au.edu.usyd.reviewer.client.core.GeneralRating;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GeneralRatingForm extends RatingForm<GeneralRating> {

	private VerticalPanel mainPanel = new VerticalPanel();
	private TextArea comment = new TextArea();
	private ListBox overallScore = new ListBox();
	private ListBox usefulnessScore = new ListBox();
	private ListBox evidenceScore = new ListBox();
	private ListBox contentScore = new ListBox();

	public GeneralRatingForm() {
		overallScore.addItem("");
		overallScore.addItem("Excelent");
		overallScore.addItem("Very Good");
		overallScore.addItem("Average");
		overallScore.addItem("Poor");
		overallScore.addItem("Terrible");
		usefulnessScore.addItem("");
		usefulnessScore.addItem("Agree");
		usefulnessScore.addItem("Neither Agree nor Disagree");
		usefulnessScore.addItem("Disagree");
		evidenceScore.addItem("");
		evidenceScore.addItem("Agree");
		evidenceScore.addItem("Neither Agree nor Disagree");
		evidenceScore.addItem("Disagree");
		contentScore.addItem("");
		contentScore.addItem("Language");
		contentScore.addItem("Content");
		contentScore.addItem("Both Language and Content");
		initWidget(mainPanel);
	}

	@Override
	public GeneralRating getRating() {
		rating.setComment(comment.getValue());
		rating.setOverallScore(overallScore.getSelectedIndex());
		rating.setUsefulnessScore(usefulnessScore.getSelectedIndex());
		rating.setEvidenceScore(evidenceScore.getSelectedIndex());
		rating.setContentScore(contentScore.getSelectedIndex());
		return rating;
	}

	@Override
	public void onLoad() {
		comment.setSize("300px", "100px");
		Grid ratingGrid = new Grid(5, 2);
		ratingGrid.setWidget(0, 0, new HTML("Overall, this review is."));
		ratingGrid.setWidget(0, 1, overallScore);
		ratingGrid.setWidget(1, 0, new HTML("This review is useful."));
		ratingGrid.setWidget(1, 1, usefulnessScore);
		ratingGrid.setWidget(2, 0, new HTML("This review is clearly written."));
		ratingGrid.setWidget(2, 1, evidenceScore);
		ratingGrid.setWidget(3, 0, new HTML("This review focused on."));
		ratingGrid.setWidget(3, 1, contentScore);
		ratingGrid.setWidget(4, 0, new HTML("Brief reason for your rating:"));
		ratingGrid.setWidget(4, 1, comment);
		mainPanel.add(ratingGrid);
	}

	@Override
	public void setRating(GeneralRating rating) {
		this.rating = rating;
		comment.setValue(rating.getComment());
		if (rating.getOverallScore() != null) {
			overallScore.setSelectedIndex(rating.getOverallScore());
		} else {
			overallScore.setSelectedIndex(-1);
		}
		if (rating.getUsefulnessScore() != null) {
			usefulnessScore.setSelectedIndex(rating.getUsefulnessScore());
		} else {
			usefulnessScore.setSelectedIndex(-1);
		}
		if (rating.getEvidenceScore() != null) {
			evidenceScore.setSelectedIndex(rating.getEvidenceScore());
		} else {
			evidenceScore.setSelectedIndex(-1);
		}
		if (rating.getContentScore() != null) {
			contentScore.setSelectedIndex(rating.getContentScore());
		} else {
			contentScore.setSelectedIndex(-1);
		}
	}
}
