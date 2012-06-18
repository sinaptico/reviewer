package au.edu.usyd.reviewer.client.core;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * <p>Class that extends {@link Activity Activity} class and it is used for Reviewing Activities management, it contains
 * the start and finish dates, allocation strategy (random or spread sheet), type of form (Questions, comments, Template), 
 * as well as the reviewers number/type (lecturer/tutor/student) of the reviews.</p>
 */
@Entity
public class ReviewingActivity extends Activity<ReviewEntry> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant REVIEW_STRATEGY_RANDOM. */
	public static final String REVIEW_STRATEGY_RANDOM = "random";
	
	/** The Constant REVIEW_STRATEGY_SPREADSHEET. */
	public static final String REVIEW_STRATEGY_SPREADSHEET = "spreadsheet";
	
	/** The Constant REVIEW_TYPE_COMMENTS. */
	public static final String REVIEW_TYPE_COMMENTS = "comments";
	
	/** The Constant REVIEW_TYPE_QUESTION. */
	public static final String REVIEW_TYPE_QUESTION = "question";
	
	/** The Constant REVIEW_TYPE_TEMPLATE. */
	public static final String REVIEW_TYPE_TEMPLATE = "Template";
	
	/** The Constant TEMPLATE_NONE. */
	public static final long TEMPLATE_NONE = 0;

	/** The finish date. */
	private Date finishDate = null;
	
	/** The start date. */
	@ManyToOne
	private Deadline startDate = new Deadline();
	
	/** The allocation strategy. */
	private String allocationStrategy = REVIEW_STRATEGY_RANDOM;
	
	/** The form type. */
	private String formType = REVIEW_TYPE_COMMENTS;
	
	/** Boolean to manage if the Review accepts ratings from students. */
	private Boolean ratings = false;
	
	/** The number of lecturer reviewers. */
	private int numLecturerReviewers = 0;
	
	/** The number of tutor reviewers. */
	private int numTutorReviewers = 0;
	
	/** The number of student reviewers. */
	private int numStudentReviewers = 1;
	
	/** The number of automatic reviewers. */
	private int numAutomaticReviewers = 0;
	
	/** Max grade. */
	private int maxGrade = 10;	
	
	/** The review template id. */
	private long reviewTemplateId;
	
	/** Boolean to manage if the Review accepts early submission. */
	private Boolean earlySubmit = false; //display early submit button

	//private Boolean studentMarks = false; //allow students to mark a peer
	
	/** The feedback template type (For SpeedBack options). */
	private String feedbackTemplateType = FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_DEFAULT;

	/**
	 * Gets the allocation strategy.
	 *
	 * @return the allocation strategy
	 */
	public String getAllocationStrategy() {
		return allocationStrategy;
	}

	/**
	 * Gets the finish date.
	 *
	 * @return the finish date
	 */
	public Date getFinishDate() {
		return finishDate;
	}

	/**
	 * Gets the form type.
	 *
	 * @return the form type
	 */
	public String getFormType() {
		return formType;
	}

	/**
	 * Gets the max grade.
	 *
	 * @return the max grade
	 */
	public int getMaxGrade() {
		return maxGrade;
	}

	/**
	 * Gets the num lecturer reviewers.
	 *
	 * @return the num lecturer reviewers
	 */
	public int getNumLecturerReviewers() {
		return numLecturerReviewers;
	}

	/**
	 * Gets the num student reviewers.
	 *
	 * @return the num student reviewers
	 */
	public int getNumStudentReviewers() {
		return numStudentReviewers;
	}

	/**
	 * Gets the num tutor reviewers.
	 *
	 * @return the num tutor reviewers
	 */
	public int getNumTutorReviewers() {
		return numTutorReviewers;
	}

	/**
	 * Gets the ratings.
	 *
	 * @return the ratings
	 */
	public Boolean getRatings() {
		return ratings;
	}

	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Deadline getStartDate() {
		return startDate;
	}

	/**
	 * Sets the allocation strategy.
	 *
	 * @param allocationStrategy the new allocation strategy
	 */
	public void setAllocationStrategy(String allocationStrategy) {
		this.allocationStrategy = allocationStrategy;
	}

	/**
	 * Sets the finish date.
	 *
	 * @param finishDate the new finish date
	 */
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	/**
	 * Sets the form type.
	 *
	 * @param formType the new form type
	 */
	public void setFormType(String formType) {
		this.formType = formType;
	}

	/**
	 * Sets the max grade.
	 *
	 * @param maxGrade the new max grade
	 */
	public void setMaxGrade(int maxGrade) {
		this.maxGrade = maxGrade;
	}

	/**
	 * Sets the num lecturer reviewers.
	 *
	 * @param numLecturerReviewers the new num lecturer reviewers
	 */
	public void setNumLecturerReviewers(int numLecturerReviewers) {
		this.numLecturerReviewers = numLecturerReviewers;
	}

	/**
	 * Sets the num student reviewers.
	 *
	 * @param numStudentReviewers the new num student reviewers
	 */
	public void setNumStudentReviewers(int numStudentReviewers) {
		this.numStudentReviewers = numStudentReviewers;
	}

	/**
	 * Sets the num tutor reviewers.
	 *
	 * @param numTutorReviewers the new num tutor reviewers
	 */
	public void setNumTutorReviewers(int numTutorReviewers) {
		this.numTutorReviewers = numTutorReviewers;
	}

	/**
	 * Sets the ratings.
	 *
	 * @param ratings the new ratings
	 */
	public void setRatings(Boolean ratings) {
		this.ratings = ratings;
	}

	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(Deadline startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the review template id.
	 *
	 * @return the review template id
	 */
	public long getReviewTemplateId() {
		return reviewTemplateId;
	}

	/**
	 * Sets the review template id.
	 *
	 * @param reviewTemplateId the new review template id
	 */
	public void setReviewTemplateId(long reviewTemplateId) {
		this.reviewTemplateId = reviewTemplateId;
	}

	/**
	 * Gets the num automatic reviewers.
	 *
	 * @return the num automatic reviewers
	 */
	public int getNumAutomaticReviewers() {
		return numAutomaticReviewers;
	}

	/**
	 * Sets the num automatic reviewers.
	 *
	 * @param numAutomaticReviewers the new num automatic reviewers
	 */
	public void setNumAutomaticReviewers(int numAutomaticReviewers) {
		this.numAutomaticReviewers = numAutomaticReviewers;
	}

	/**
	 * Gets the early submit.
	 *
	 * @return the early submit
	 */
	public Boolean getEarlySubmit() {
		return earlySubmit;
	}

	/**
	 * Sets the early submit.
	 *
	 * @param earlySubmit the new early submit
	 */
	public void setEarlySubmit(Boolean earlySubmit) {
		this.earlySubmit = earlySubmit;
	}

	/**
	 * Sets the feedback template type.
	 *
	 * @param feedbackTemplateType the new feedback template type
	 */
	public void setFeedbackTemplateType(String feedbackTemplateType) {
		this.feedbackTemplateType = feedbackTemplateType;
	}

	/**
	 * Gets the feedback template type.
	 *
	 * @return the feedback template type
	 */
	public String getFeedbackTemplateType() {
		return feedbackTemplateType;
	}

//	public Boolean getStudentMarks() {
//		return studentMarks;
//	}
//
//	public void setStudentMarks(Boolean studentMarks) {
//		this.studentMarks = studentMarks;
//	}	
}
