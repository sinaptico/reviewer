package au.edu.usyd.reviewer.client.admin.report;

import java.io.Serializable;
import java.util.Date;

public class UserStats implements Serializable {

	private static final long serialVersionUID = 1L;

	private int daysWriting = 0;
	private Date firstRevision = null;
	private int groupContributors = 0;
	private int groupRevisions = 0;
	private Date lastRevision = null;
	private int revisions = 0;
	private int sessionsWriting = 0;
	private String userId;

	public int getDaysWriting() {
		return daysWriting;
	}

	public double getDuration() {
		if(lastRevision == null || firstRevision == null)
			return 0;
		return ((double) (lastRevision.getTime() - firstRevision.getTime()) / (1000 * 60 * 60 * 24));
	}

	public Date getFirstRevision() {
		return firstRevision;
	}

	public int getGroupContributors() {
		return groupContributors;
	}

	public int getGroupRevisions() {
		return groupRevisions;
	}

	public Date getLastRevision() {
		return lastRevision;
	}

	public int getRevisions() {
		return revisions;
	}

	public double getRevisionsPerDay() {
		if(daysWriting == 0)
			return 0;
		return revisions / daysWriting;
	}

	public double getRevisionsPerSession() {
		if(sessionsWriting == 0)
			return 0;
		return revisions / sessionsWriting;
	}

	public double getSessionsPerDay() {
		if(daysWriting == 0)
			return 0;
		return sessionsWriting / daysWriting;
	}

	public int getSessionsWriting() {
		if(daysWriting == 0)
			return 0;
		return sessionsWriting;
	}

	public String getUserId() {
		return userId;
	}

	public void setDaysWriting(int daysWriting) {
		this.daysWriting = daysWriting;
	}

	public void setFirstRevision(Date firstRevision) {
		this.firstRevision = firstRevision;
	}

	public void setGroupContributors(int groupContributors) {
		this.groupContributors = groupContributors;
	}

	public void setGroupRevisions(int groupRevisions) {
		this.groupRevisions = groupRevisions;
	}

	public void setLastRevision(Date lastRevision) {
		this.lastRevision = lastRevision;
	}

	public void setRevisions(int revisions) {
		this.revisions = revisions;
	}

	public void setSessionsWriting(int sessionsWriting) {
		this.sessionsWriting = sessionsWriting;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
