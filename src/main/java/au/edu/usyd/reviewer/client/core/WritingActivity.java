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

@Entity
public class WritingActivity extends Activity<DocEntry> {

	private static final long serialVersionUID = 1L;

	public static final String TUTORIAL_ALL = "all";

	public static final String DOCUMENT_TEMPLATE_NONE = "none";

	public static final String DOCUMENT_TYPE_DOCUMENT = "document";
	public static final String DOCUMENT_TYPE_PRESENTATION = "presentation";
	public static final String DOCUMENT_TYPE_SPREADSHEET = "spreadsheet";
	public static final String DOCUMENT_TYPE_LOGBOOK = "logbook";
	public static final String DOCUMENT_TYPE_FILE_UPLOAD = "file upload";
	
	public static final String DOCUMENT_GENRE_PROPOSAL = "proposal";
	public static final String DOCUMENT_GENRE_LAB_REPORT = "lab report";
	public static final String DOCUMENT_GENRE_FIELD_TRIP = "field trip";
	public static final String DOCUMENT_GENRE_THESIS = "thesis";
	public static final String DOCUMENT_GENRE_LAB_BOOK = "lab book";
	
	public static final long GLOSSER_SITE_NONE = 0;

	private String documentType = DOCUMENT_TYPE_DOCUMENT;
	private String documentTemplate = DOCUMENT_TEMPLATE_NONE;
	private Date startDate = null;
	private Boolean groups = false;
	private Long glosserSite = GLOSSER_SITE_NONE;
	private String tutorial = TUTORIAL_ALL;
	private Boolean emailStudents = false;
	private Boolean showStats = false;
	private String folderId;
	private Boolean earlySubmit = false;
	private Boolean trackReviews = false;
	private String genre = DOCUMENT_GENRE_PROPOSAL; 
	@OneToMany
	@Cascade(CascadeType.ALL)
	@IndexColumn(name = "deadlineIndex")
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "WritingActivity_Deadlines_Deadline")
	private List<Deadline> deadlines = new ArrayList<Deadline>();
	@OneToMany
	@Cascade(CascadeType.ALL)
	@IndexColumn(name = "reviewIndex")
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "WritingActivity_ReviewingActivities_ReviewingActivity")
	private List<ReviewingActivity> reviewingActivities = new ArrayList<ReviewingActivity>();
	@OneToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "WritingActivity_Grades_Grade")
	private Set<Grade> grades = new HashSet<Grade>();
	
	public List<Deadline> getDeadlines() {
		return deadlines;
	}

	public String getDocumentTemplate() {
		return documentTemplate;
	}

	public String getDocumentType() {
		return documentType;
	}

	public Boolean getEmailStudents() {
		return emailStudents;
	}

	public String getFolderId() {
		return folderId;
	}

	public Long getGlosserSite() {
		return glosserSite;
	}

	public Set<Grade> getGrades() {
		return grades;
	}

	public Boolean getGroups() {
		return groups;
	}

	public List<ReviewingActivity> getReviewingActivities() {
		return reviewingActivities;
	}

	public Boolean getShowStats() {
		return showStats;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTutorial() {
		return tutorial;
	}

	public void setDeadlines(List<Deadline> deadlines) {
		this.deadlines = deadlines;
	}

	public void setDocumentTemplate(String documentTemplate) {
		this.documentTemplate = documentTemplate;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setEmailStudents(Boolean emailStudents) {
		this.emailStudents = emailStudents;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public void setGlosserSite(Long glosserSite) {
		this.glosserSite = glosserSite;
	}

	public void setGrades(Set<Grade> grades) {
		this.grades = grades;
	}

	public void setGroups(Boolean groups) {
		this.groups = groups;
	}

	public void setReviewingActivities(List<ReviewingActivity> reviewingActivities) {
		this.reviewingActivities = reviewingActivities;
	}

	public void setShowStats(Boolean showStats) {
		this.showStats = showStats;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTutorial(String tutorial) {
		this.tutorial = tutorial;
	}

	public Boolean getEarlySubmit() {
		return earlySubmit;
	}

	public void setEarlySubmit(Boolean earlySubmit) {
		this.earlySubmit = earlySubmit;
	}

	public Boolean getTrackReviews() {
		return trackReviews;
	}

	public void setTrackReviews(Boolean trackReviews) {
		this.trackReviews = trackReviews;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}	
	
}
