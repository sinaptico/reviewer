package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * <p>Class used to manage feedback ratings, it is given by students, it includes overall, usefulness 
 * and content scores. </p>
 */
@Entity
public class GeneralRating extends Rating implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The overall score. */
	private Integer overallScore;
	
	/** The evidence score. */
	private Integer evidenceScore;
	
	/** The usefulness score. */
	private Integer usefulnessScore;
	
	/** The content score. */
	private Integer contentScore;
	
	/** The comment. */
	@Column(length = 65535)
	private String comment;

	/**
	 * Gets the comment.
	 *
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Gets the content score.
	 *
	 * @return the content score
	 */
	public Integer getContentScore() {
		return contentScore;
	}

	/**
	 * Gets the evidence score.
	 *
	 * @return the evidence score
	 */
	public Integer getEvidenceScore() {
		return evidenceScore;
	}

	/**
	 * Gets the overall score.
	 *
	 * @return the overall score
	 */
	public Integer getOverallScore() {
		return overallScore;
	}

	/**
	 * Gets the usefulness score.
	 *
	 * @return the usefulness score
	 */
	public Integer getUsefulnessScore() {
		return usefulnessScore;
	}

	/**
	 * Sets the comment.
	 *
	 * @param comment the new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Sets the content score.
	 *
	 * @param contentScore the new content score
	 */
	public void setContentScore(Integer contentScore) {
		this.contentScore = contentScore;
	}

	/**
	 * Sets the evidence score.
	 *
	 * @param evidenceScore the new evidence score
	 */
	public void setEvidenceScore(Integer evidenceScore) {
		this.evidenceScore = evidenceScore;
	}

	/**
	 * Sets the overall score.
	 *
	 * @param overallScore the new overall score
	 */
	public void setOverallScore(Integer overallScore) {
		this.overallScore = overallScore;
	}

	/**
	 * Sets the usefulness score.
	 *
	 * @param score the new usefulness score
	 */
	public void setUsefulnessScore(Integer score) {
		this.usefulnessScore = score;
	}
}
