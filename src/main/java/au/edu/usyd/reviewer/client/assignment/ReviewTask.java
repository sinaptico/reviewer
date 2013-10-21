package au.edu.usyd.reviewer.client.assignment;

import java.io.Serializable;
import java.util.Date;
/**
 * This class is used to represent the reviewing tasks in the assignments page.
 * Each ReviewTask is a row in the table of reviewing tasks 
 *
 */


public class ReviewTask implements Serializable {
	
	private String courseName;
	private Long  reviewId;
	private String docEntryTitle;
	private Date savedDate;
	private Date finishDate;
	
	public ReviewTask(){}
	
	
	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDocEntryTitle() {
		return docEntryTitle;
	}

	public void setDocEntryTitle(String docEntryTitle) {
		this.docEntryTitle = docEntryTitle;
	}

	public Date getSavedDate() {
		return savedDate;
	}

	public void setSavedDate(Date savedDate) {
		this.savedDate = savedDate;
	}

		
	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public Long getReviewId() {
		return reviewId;
	}

	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
	}
	
	public ReviewTask clone(){
		ReviewTask reviewTask = new ReviewTask();
		reviewTask.setCourseName(this.getCourseName());
		reviewTask.setReviewId(this.getReviewId());
		reviewTask.setDocEntryTitle(this.getDocEntryTitle());
		reviewTask.setFinishDate(this.getFinishDate());
		reviewTask.setSavedDate(this.getSavedDate());
		return reviewTask;
	}

}
