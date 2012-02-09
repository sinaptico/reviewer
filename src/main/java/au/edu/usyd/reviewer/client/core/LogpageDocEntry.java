package au.edu.usyd.reviewer.client.core;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class LogpageDocEntry extends DocEntry {

	private static final long serialVersionUID = 1L;
	private Date submitted = null;

	public Date getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}
}
