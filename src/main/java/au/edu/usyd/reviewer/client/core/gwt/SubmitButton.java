package au.edu.usyd.reviewer.client.core.gwt;

import com.google.gwt.user.client.ui.Button;

/**
 * <p>Class that extends the GWT Button class to include the messages: "Submit", "Submitting..." and "Submitted".</p>
 */
public class SubmitButton extends Button {

	/** The submit html optional message. */
	private String submitHTML;
	
	/** The submitting html optional message. */
	private String submittingHTML;
	
	/** The submitted html optional message. */
	private String submittedHTML;

	/**
	 * Instantiates a new submit button.
	 */
	public SubmitButton() {		
		this("Submit", "Submitting...", "Submitted");
	}

	/**
	 * Instantiates a new submit button.
	 *
	 * @param submitHTML the submit html
	 * @param submittingHTML the submitting html
	 * @param submittedHTML the submitted html
	 */
	public SubmitButton(String submitHTML, String submittingHTML, String submittedHTML) {
		this.submitHTML = submitHTML;
		this.submittingHTML = submittingHTML;
		this.submittedHTML = submittedHTML;
		this.updateStateSubmit();
	}

	/**
	 * Gets the submit html.
	 *
	 * @return the submit html
	 */
	public String getSubmitHTML() {
		return submitHTML;
	}

	/**
	 * Gets the submitted html.
	 *
	 * @return the submitted html
	 */
	public String getSubmittedHTML() {
		return submittedHTML;
	}

	/**
	 * Gets the submitting html.
	 *
	 * @return the submitting html
	 */
	public String getSubmittingHTML() {
		return submittingHTML;
	}

	/**
	 * Sets the submit html.
	 *
	 * @param submitHTML the new submit html
	 */
	public void setSubmitHTML(String submitHTML) {
		this.submitHTML = submitHTML;
	}

	/**
	 * Sets the submitted html.
	 *
	 * @param submittedHTML the new submitted html
	 */
	public void setSubmittedHTML(String submittedHTML) {
		this.submittedHTML = submittedHTML;
	}

	/**
	 * Sets the submitting html.
	 *
	 * @param submittingHTML the new submitting html
	 */
	public void setSubmittingHTML(String submittingHTML) {
		this.submittingHTML = submittingHTML;
	}

	/**
	 * Update state submit.
	 */
	public void updateStateSubmit() {
		this.setHTML(submitHTML);
		this.setEnabled(true);
	}

	/**
	 * Update state submitted.
	 */
	public void updateStateSubmitted() {
		this.setEnabled(false);
		this.setHTML(submittedHTML);
	}

	/**
	 * Update state submitting.
	 */
	public void updateStateSubmitting() {
		this.setEnabled(false);
		this.setHTML(submittingHTML);
	}
}
