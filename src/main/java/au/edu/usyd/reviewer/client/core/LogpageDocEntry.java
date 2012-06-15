package au.edu.usyd.reviewer.client.core;

import java.util.Date;

import javax.persistence.Entity;

/**
 * Class used to track the pages submitted to a Log Book.
 */
@Entity
public class LogpageDocEntry extends DocEntry {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The submitted. */
	private Date submitted = null;

	/**
	 * Gets the submitted.
	 *
	 * @return the submitted
	 */
	public Date getSubmitted() {
		return submitted;
	}

	/**
	 * Sets the submitted.
	 *
	 * @param submitted the new submitted
	 */
	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}
}
