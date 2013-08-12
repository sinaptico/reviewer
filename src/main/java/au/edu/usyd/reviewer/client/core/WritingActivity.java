package au.edu.usyd.reviewer.client.core;

import java.util.ArrayList;



import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>Class that extends {@link Activity Activity} class and it is used for Writing Activities management. It contains:
 * <ul>
 * 	<li><p><b>Tutorial where the activity is going to be used.</b></p></li>
 * 	<li><p><b>Notifications options:</b></p>
 * 		<ul>
 * 			<li>Boolean to check if email notifications are sent to students.</li>
 * 			<li>Boolean to check if the early submit option is allowed.</li>
 * 			<li>Boolean to check if reviews are tracked: For studies purposes, track when students read their feedback.</li>
 * 		</ul>
 * 	</li>
 * 	<li><p><b>Automatic Feedback options:</b></p>
 * 		<ul>
 * 			<li>Glosser record (To enable automatic Glosser feedback).</li>
 * 			<li>Boolean to check if "MyStats" are shown.</li>
 * 		</ul>
 * 	</li>
 * 	<li><p><b>Writing Task options:</b></p>
 * 		<ul>
 * 			<li>Name.</li>
 * 			<li>Document type and genre.</li>
 * 			<li>Document template.</li>
 * 			<li>Boolean to check if the documents are group based.</li>
 *  		<li>Start date.</li>
 *  	</ul>
 *  </li>
 * </ul>
 */
@Entity
public class WritingActivity extends Activity<DocEntry> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant TUTORIAL_ALL. */
	public static final String TUTORIAL_ALL = "all";

	/** The Constant DOCUMENT_TEMPLATE_NONE. */
	public static final String DOCUMENT_TEMPLATE_NONE = "none";

	/** The Constant DOCUMENT_TYPE_DOCUMENT. */
	public static final String DOCUMENT_TYPE_DOCUMENT = "document";
	
	/** The Constant DOCUMENT_TYPE_PRESENTATION. */
	public static final String DOCUMENT_TYPE_PRESENTATION = "presentation";
	
	/** The Constant DOCUMENT_TYPE_SPREADSHEET. */
	public static final String DOCUMENT_TYPE_SPREADSHEET = "spreadsheet";
	
	/** The Constant DOCUMENT_TYPE_LOGBOOK. */
	public static final String DOCUMENT_TYPE_LOGBOOK = "logbook";
	
	/** The Constant DOCUMENT_TYPE_FILE_UPLOAD. */
	public static final String DOCUMENT_TYPE_FILE_UPLOAD = "file upload";
	
	/** The Constant DOCUMENT_GENRE_PROPOSAL. */
	public static final String DOCUMENT_GENRE_PROPOSAL = "proposal";
	
	/** The Constant DOCUMENT_GENRE_LAB_REPORT. */
	public static final String DOCUMENT_GENRE_LAB_REPORT = "lab report";
	
	/** The Constant DOCUMENT_GENRE_FIELD_TRIP. */
	public static final String DOCUMENT_GENRE_FIELD_TRIP = "field trip";
	
	/** The Constant DOCUMENT_GENRE_THESIS. */
	public static final String DOCUMENT_GENRE_THESIS = "thesis";
	
	/** The Constant DOCUMENT_GENRE_LAB_BOOK. */
	public static final String DOCUMENT_GENRE_LAB_BOOK = "lab book";
	
	/** The Constant GLOSSER_SITE_NONE. */
	public static final long GLOSSER_SITE_NONE = 0;

	/** The document type. */
	private String documentType = DOCUMENT_TYPE_DOCUMENT;
	
	/** The document template. */
	private String documentTemplate = DOCUMENT_TEMPLATE_NONE;
	
	/** The start date. */
	private Date startDate = null;
	
	/** Boolean to check if it's a group activity. */
	private Boolean groups = false;
	
	/** The glosser site. */
	private Long glosserSite = GLOSSER_SITE_NONE;
	
	/** The tutorial. */
	private String tutorial = TUTORIAL_ALL;
	
	/** Boolean to check if email to students are sent. */
	private Boolean emailStudents = false;
	
	/** Boolean to check if stats are shown to students. */
	private Boolean showStats = false;
	
	/** The folder id where the google documents are stored. */
	private String folderId;
	
	/** Boolean to check if the early submit option will be available. */
	private Boolean earlySubmit = false;
	
	/** Boolean to check if the reviews are tracked. */
	private Boolean trackReviews = false;
	
	/** The genre. */
	private String genre = DOCUMENT_GENRE_PROPOSAL; 
	
	/** The deadlines. */
	@OneToMany
	@Cascade(CascadeType.ALL)
	@IndexColumn(name = "deadlineIndex")
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "WritingActivity_Deadlines_Deadline")
	private List<Deadline> deadlines = new ArrayList<Deadline>();
	
	/** The reviewing activities. */
	@OneToMany
	@Cascade(CascadeType.ALL)
	@IndexColumn(name = "reviewIndex")
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "WritingActivity_ReviewingActivities_ReviewingActivity")
	private List<ReviewingActivity> reviewingActivities = new ArrayList<ReviewingActivity>();
	
	/** The grades. */
	@OneToMany
	@Cascade(CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "WritingActivity_Grades_Grade")
	private Set<Grade> grades = new HashSet<Grade>();
	
	/** The exclude empty docs in reviews. */
	private Boolean excludeEmptyDocsInReviews = true;
	
	/**
	 * Gets the deadlines.
	 *
	 * @return the deadlines
	 */
	public List<Deadline> getDeadlines() {
		return deadlines;
	}

	/**
	 * Gets the document template.
	 *
	 * @return the document template
	 */
	public String getDocumentTemplate() {
		return documentTemplate;
	}

	/**
	 * Gets the document type.
	 *
	 * @return the document type
	 */
	public String getDocumentType() {
		return documentType;
	}

	/**
	 * Gets the email students.
	 *
	 * @return the email students
	 */
	public Boolean getEmailStudents() {
		return emailStudents;
	}

	/**
	 * Gets the folder id.
	 *
	 * @return the folder id
	 */
	public String getFolderId() {
		return folderId;
	}

	/**
	 * Gets the glosser site.
	 *
	 * @return the glosser site
	 */
	public Long getGlosserSite() {
		return glosserSite;
	}

	/**
	 * Gets the grades.
	 *
	 * @return the grades
	 */
	public Set<Grade> getGrades() {
		return grades;
	}

	/**
	 * Gets the groups.
	 *
	 * @return the groups
	 */
	public Boolean getGroups() {
		return groups;
	}

	/**
	 * Gets the reviewing activities.
	 *
	 * @return the reviewing activities
	 */
	public List<ReviewingActivity> getReviewingActivities() {
		return reviewingActivities;
	}

	/**
	 * Gets the show stats.
	 *
	 * @return the show stats
	 */
	public Boolean getShowStats() {
		return showStats;
	}

	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Gets the tutorial.
	 *
	 * @return the tutorial
	 */
	public String getTutorial() {
		return tutorial;
	}

	/**
	 * Sets the deadlines.
	 *
	 * @param deadlines the new deadlines
	 */
	public void setDeadlines(List<Deadline> deadlines) {
		this.deadlines = deadlines;
	}

	/**
	 * Sets the document template.
	 *
	 * @param documentTemplate the new document template
	 */
	public void setDocumentTemplate(String documentTemplate) {
		this.documentTemplate = documentTemplate;
	}

	/**
	 * Sets the document type.
	 *
	 * @param documentType the new document type
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	/**
	 * Sets the email students.
	 *
	 * @param emailStudents the new email students
	 */
	public void setEmailStudents(Boolean emailStudents) {
		this.emailStudents = emailStudents;
	}

	/**
	 * Sets the folder id.
	 *
	 * @param folderId the new folder id
	 */
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	/**
	 * Sets the glosser site.
	 *
	 * @param glosserSite the new glosser site
	 */
	public void setGlosserSite(Long glosserSite) {
		this.glosserSite = glosserSite;
	}

	/**
	 * Sets the grades.
	 *
	 * @param grades the new grades
	 */
	public void setGrades(Set<Grade> grades) {
		this.grades = grades;
	}

	/**
	 * Sets the groups.
	 *
	 * @param groups the new groups
	 */
	public void setGroups(Boolean groups) {
		this.groups = groups;
	}

	/**
	 * Sets the reviewing activities.
	 *
	 * @param reviewingActivities the new reviewing activities
	 */
	public void setReviewingActivities(List<ReviewingActivity> reviewingActivities) {
		this.reviewingActivities = reviewingActivities;
	}

	/**
	 * Sets the show stats.
	 *
	 * @param showStats the new show stats
	 */
	public void setShowStats(Boolean showStats) {
		this.showStats = showStats;
	}

	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Sets the tutorial.
	 *
	 * @param tutorial the new tutorial
	 */
	public void setTutorial(String tutorial) {
		this.tutorial = tutorial;
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
	 * Gets the track reviews.
	 *
	 * @return the track reviews
	 */
	public Boolean getTrackReviews() {
		return trackReviews;
	}

	/**
	 * Sets the track reviews.
	 *
	 * @param trackReviews the new track reviews
	 */
	public void setTrackReviews(Boolean trackReviews) {
		this.trackReviews = trackReviews;
	}

	/**
	 * Gets the genre.
	 *
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * Sets the genre.
	 *
	 * @param genre the new genre
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}	
	
	/**
	 * Gets the current deadline.
	 *
	 * @return the current deadline
	 */
	public Deadline getCurrentDeadline(){
		Deadline currentDeadline = null;
		for(Deadline deadline : getDeadlines()){
			if (currentDeadline == null && deadline.getStatus() == Deadline.STATUS_DEADLINE_START){
				currentDeadline = deadline;
			} else if ( deadline.getStatus() == Deadline.STATUS_DEADLINE_START && 
					deadline.getFinishDate() != null && currentDeadline.getFinishDate() != null && 
				    deadline.getFinishDate().before(currentDeadline.getFinishDate())){
				currentDeadline = deadline;
			}
		}
		return currentDeadline;
	}

	public Deadline getFinalDeadline(){
		Deadline finalDeadline = null;
		
		for(Deadline deadline : getDeadlines()){
			if (finalDeadline == null){
				finalDeadline = deadline;
			} else if ( deadline.getFinishDate() != null && finalDeadline.getFinishDate() != null && 
					    deadline.getFinishDate().after(finalDeadline.getFinishDate())){
				finalDeadline = deadline;
			}
		}
		return finalDeadline;
	}
	
	/**
	 * Sets the exclude empty docs in reviews.
	 *
	 * @param excludeEmptyDocsInReviews the new exclude empty docs in reviews
	 */
	public void setExcludeEmptyDocsInReviews(Boolean excludeEmptyDocsInReviews) {
		this.excludeEmptyDocsInReviews = excludeEmptyDocsInReviews;
	}

	/**
	 * Gets the exclude empty docs in reviews.
	 *
	 * @return the exclude empty docs in reviews
	 */
	public Boolean getExcludeEmptyDocsInReviews() {
		return excludeEmptyDocsInReviews;
	}

	public WritingActivity clone(){
		WritingActivity activity = new WritingActivity();

		List<Deadline> deadlines = new ArrayList<Deadline>();
		for(Deadline deadline : this.getDeadlines()){
			if (deadline!= null){
				deadlines.add(deadline.clone());
			}
		}
		activity.setDeadlines(deadlines);
		
		activity.setDocumentTemplate(this.getDocumentTemplate());
		activity.setDocumentType(this.getDocumentType());
		activity.setEarlySubmit(this.getEarlySubmit());
		activity.setEmailStudents(this.getEmailStudents());
		
		Set<DocEntry> entries = new HashSet<DocEntry>();
		for(DocEntry entry: this.getEntries()){
			if (entry != null){
				entries.add(entry.clone());
			}
		}
		activity.setEntries(entries);
		
		activity.setExcludeEmptyDocsInReviews(this.getExcludeEmptyDocsInReviews());	
		activity.setFolderId(this.getFolderId());
		activity.setGenre(this.getGenre());
		activity.setGlosserSite(this.getGlosserSite());
		
		Set<Grade> grades = new HashSet<Grade>();
		for(Grade grade: this.grades){
			if (grade != null){
				grades.add(grade.clone());
			}
		}
		activity.setGrades(grades);
		
		activity.setGroups(this.getGroups());
		activity.setId(this.getId());
		activity.setName(this.getName());
		
		List<ReviewingActivity> reviewingActivities = new ArrayList<ReviewingActivity>();
		for(ReviewingActivity reviewingActivity: this.getReviewingActivities()){
			if (reviewingActivity != null){
				reviewingActivities.add(reviewingActivity.clone());
			}
		}
		activity.setReviewingActivities(reviewingActivities);
		
		activity.setShowStats(this.getShowStats());
		activity.setStartDate(this.getStartDate());
		activity.setStatus(this.getStatus());
		activity.setTrackReviews(this.getTrackReviews());
		activity.setTutorial(this.getTutorial());
		activity.setDeleted(this.isDeleted());
		return activity;
		
	}
}
