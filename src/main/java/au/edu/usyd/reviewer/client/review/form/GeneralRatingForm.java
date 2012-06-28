package au.edu.usyd.reviewer.client.review.form;

import au.edu.usyd.reviewer.client.core.GeneralRating;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <p>Class that includes grading items for reviews, it is used by students to rate the quality of the received feedback.</p>
 */
public class GeneralRatingForm extends RatingForm<GeneralRating> {

	/** The main panel of the form where all the fields and widgets are placed. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/** TextArea where the comments are written. */
	private TextArea comment = new TextArea();
	
	/** The overall score. */
	private ListBox overallScore = new ListBox();
	
	/** The usefulness score. */
	private ListBox usefulnessScore = new ListBox();
	
	/** The evidence score. */
	private ListBox evidenceScore = new ListBox();
	
	/** The content score. */
	private ListBox contentScore = new ListBox();

	/**
	 * Instantiates a new general rating form.
	 */
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

	/* (non-Javadoc)
	 * @see au.edu.usyd.reviewer.client.review.form.RatingForm#getRating()
	 */
	@Override
	public GeneralRating getRating() {
		rating.setComment(comment.getValue());
		rating.setOverallScore(overallScore.getSelectedIndex());
		rating.setUsefulnessScore(usefulnessScore.getSelectedIndex());
		rating.setEvidenceScore(evidenceScore.getSelectedIndex());
		rating.setContentScore(contentScore.getSelectedIndex());
		return rating;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onLoad()
	 */
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

	/* (non-Javadoc)
	 * @see au.edu.usyd.reviewer.client.review.form.RatingForm#setRating(au.edu.usyd.reviewer.client.core.Rating)
	 */
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
