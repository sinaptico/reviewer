package au.edu.usyd.reviewer.client.core.gwt;

import com.google.gwt.user.client.ui.Button;

public class SubmitButton extends Button {

	private String submitHTML;
	private String submittingHTML;
	private String submittedHTML;

	public SubmitButton() {		
		this("Submit", "Submitting...", "Submitted");
	}

	public SubmitButton(String submitHTML, String submittingHTML, String submittedHTML) {
		this.submitHTML = submitHTML;
		this.submittingHTML = submittingHTML;
		this.submittedHTML = submittedHTML;
		this.updateStateSubmit();
	}

	public String getSubmitHTML() {
		return submitHTML;
	}

	public String getSubmittedHTML() {
		return submittedHTML;
	}

	public String getSubmittingHTML() {
		return submittingHTML;
	}

	public void setSubmitHTML(String submitHTML) {
		this.submitHTML = submitHTML;
	}

	public void setSubmittedHTML(String submittedHTML) {
		this.submittedHTML = submittedHTML;
	}

	public void setSubmittingHTML(String submittingHTML) {
		this.submittingHTML = submittingHTML;
	}

	public void updateStateSubmit() {
		this.setHTML(submitHTML);
		this.setEnabled(true);
	}

	public void updateStateSubmitted() {
		this.setEnabled(false);
		this.setHTML(submittedHTML);
	}

	public void updateStateSubmitting() {
		this.setEnabled(false);
		this.setHTML(submittingHTML);
	}
}
