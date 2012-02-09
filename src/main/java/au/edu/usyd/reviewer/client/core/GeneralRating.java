package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class GeneralRating extends Rating implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer overallScore;
	private Integer evidenceScore;
	private Integer usefulnessScore;
	private Integer contentScore;
	@Column(length = 65535)
	private String comment;

	public String getComment() {
		return comment;
	}

	public Integer getContentScore() {
		return contentScore;
	}

	public Integer getEvidenceScore() {
		return evidenceScore;
	}

	public Integer getOverallScore() {
		return overallScore;
	}

	public Integer getUsefulnessScore() {
		return usefulnessScore;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setContentScore(Integer contentScore) {
		this.contentScore = contentScore;
	}

	public void setEvidenceScore(Integer evidenceScore) {
		this.evidenceScore = evidenceScore;
	}

	public void setOverallScore(Integer overallScore) {
		this.overallScore = overallScore;
	}

	public void setUsefulnessScore(Integer score) {
		this.usefulnessScore = score;
	}
}
