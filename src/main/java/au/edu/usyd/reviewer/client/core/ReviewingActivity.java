package au.edu.usyd.reviewer.client.core;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ReviewingActivity extends Activity<ReviewEntry> {

	private static final long serialVersionUID = 1L;

	public static final String REVIEW_STRATEGY_RANDOM = "random";
	public static final String REVIEW_STRATEGY_SPREADSHEET = "spreadsheet";
	public static final String REVIEW_TYPE_COMMENTS = "comments";
	public static final String REVIEW_TYPE_QUESTION = "question";
	public static final String REVIEW_TYPE_TEMPLATE = "Template";
	public static final long TEMPLATE_NONE = 0;

	private Date finishDate = null;
	@ManyToOne
	private Deadline startDate = new Deadline();
	private String allocationStrategy = REVIEW_STRATEGY_RANDOM;
	private String formType = REVIEW_TYPE_COMMENTS;
	private Boolean ratings = false;
	private int numLecturerReviewers = 0;
	private int numTutorReviewers = 0;
	private int numStudentReviewers = 1;
	private int numAutomaticReviewers = 0;
	private int maxGrade = 10;	
	private long reviewTemplateId;
	private Boolean earlySubmit = false; //display early submit button
	//private Boolean studentMarks = false; //allow students to mark a peer

	public String getAllocationStrategy() {
		return allocationStrategy;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public String getFormType() {
		return formType;
	}

	public int getMaxGrade() {
		return maxGrade;
	}

	public int getNumLecturerReviewers() {
		return numLecturerReviewers;
	}

	public int getNumStudentReviewers() {
		return numStudentReviewers;
	}

	public int getNumTutorReviewers() {
		return numTutorReviewers;
	}

	public Boolean getRatings() {
		return ratings;
	}

	public Deadline getStartDate() {
		return startDate;
	}

	public void setAllocationStrategy(String allocationStrategy) {
		this.allocationStrategy = allocationStrategy;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public void setMaxGrade(int maxGrade) {
		this.maxGrade = maxGrade;
	}

	public void setNumLecturerReviewers(int numLecturerReviewers) {
		this.numLecturerReviewers = numLecturerReviewers;
	}

	public void setNumStudentReviewers(int numStudentReviewers) {
		this.numStudentReviewers = numStudentReviewers;
	}

	public void setNumTutorReviewers(int numTutorReviewers) {
		this.numTutorReviewers = numTutorReviewers;
	}

	public void setRatings(Boolean ratings) {
		this.ratings = ratings;
	}

	public void setStartDate(Deadline startDate) {
		this.startDate = startDate;
	}

	public long getReviewTemplateId() {
		return reviewTemplateId;
	}

	public void setReviewTemplateId(long reviewTemplateId) {
		this.reviewTemplateId = reviewTemplateId;
	}

	public int getNumAutomaticReviewers() {
		return numAutomaticReviewers;
	}

	public void setNumAutomaticReviewers(int numAutomaticReviewers) {
		this.numAutomaticReviewers = numAutomaticReviewers;
	}

	public Boolean getEarlySubmit() {
		return earlySubmit;
	}

	public void setEarlySubmit(Boolean earlySubmit) {
		this.earlySubmit = earlySubmit;
	}

//	public Boolean getStudentMarks() {
//		return studentMarks;
//	}
//
//	public void setStudentMarks(Boolean studentMarks) {
//		this.studentMarks = studentMarks;
//	}	
}
