package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>Class used for courses management, include the details of name, year, semester and the if 
 * of the Google documents folder for templates and writing activities.</P
 */
@Entity
public class Course implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	private Long id;
	
	/** The name. */
	private String name;
	
	/** The year. */
	private int year;
	
	/** The semester. */
	private int semester;
	
	/** The folder id. */
	private String folderId;
	
	/** The templates folder id. */
	private String templatesFolderId;
	
	/** The spreadsheet id. */
	private String spreadsheetId;
	
	/** The domain name. */
	private String domainName;
	
	/** The tutorials. */
	@ElementCollection
	@JoinTable(name = "Course_Tutorials")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<String> tutorials = new HashSet<String>();
	
	/** The lecturers. */
	@ManyToMany
	@JoinTable(name = "Course_Lecturers_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> lecturers = new HashSet<User>();
	
	/** The tutors. */
	@ManyToMany
	@JoinTable(name = "Course_Tutors_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> tutors = new HashSet<User>();
	
	/** The supervisors. */
	@ManyToMany
	@JoinTable(name = "Course_Supervisors_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> supervisors = new HashSet<User>();
	
	/** The student groups. */
	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "Course_StudentGroups_UserGroup")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<UserGroup> studentGroups = new HashSet<UserGroup>();
	
	/** The writing activities. */
	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "Course_Activities_Activity")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<WritingActivity> writingActivities = new HashSet<WritingActivity>();
	
	/** The templates. */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "Course_Templates_DocEntry")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<DocEntry> templates = new HashSet<DocEntry>();
	
	/** The automatic reviewers. */
	@ManyToMany
	@JoinTable(name = "Course_Automatic_Reviewers_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> automaticReviewers = new HashSet<User>();

	/** The organization */
	@ManyToOne
	@JoinColumn(name="organizationId")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Organization organization;
	
	public Course(){}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Course))
			return false;
		Course other = (Course) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Gets the lecturers.
	 *
	 * @return the lecturers
	 */
	public Set<User> getLecturers() {
		return lecturers;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the semester.
	 *
	 * @return the semester
	 */
	public int getSemester() {
		return semester;
	}

	/**
	 * Gets the spreadsheet id.
	 *
	 * @return the spreadsheet id
	 */
	public String getSpreadsheetId() {
		return spreadsheetId;
	}

	/**
	 * Gets the student groups.
	 *
	 * @return the student groups
	 */
	public Set<UserGroup> getStudentGroups() {
		return studentGroups;
	}

	/**
	 * Gets the supervisors.
	 *
	 * @return the supervisors
	 */
	public Set<User> getSupervisors() {
		return supervisors;
	}

	/**
	 * Gets the templates.
	 *
	 * @return the templates
	 */
	public Set<DocEntry> getTemplates() {
		return templates;
	}

	/**
	 * Gets the templates folder id.
	 *
	 * @return the templates folder id
	 */
	public String getTemplatesFolderId() {
		return templatesFolderId;
	}

	/**
	 * Gets the tutorials.
	 *
	 * @return the tutorials
	 */
	public Set<String> getTutorials() {
		return tutorials;
	}

	/**
	 * Gets the tutors.
	 *
	 * @return the tutors
	 */
	public Set<User> getTutors() {
		return tutors;
	}

	/**
	 * Gets the writing activities.
	 *
	 * @return the writing activities
	 */
	public Set<WritingActivity> getWritingActivities() {
		return writingActivities;
	}

	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
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
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the lecturers.
	 *
	 * @param lecturers the new lecturers
	 */
	public void setLecturers(Set<User> lecturers) {
		this.lecturers = lecturers;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the semester.
	 *
	 * @param semester the new semester
	 */
	public void setSemester(int semester) {
		this.semester = semester;
	}

	/**
	 * Sets the spreadsheet id.
	 *
	 * @param spreadsheetId the new spreadsheet id
	 */
	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
	}

	/**
	 * Sets the student groups.
	 *
	 * @param studentGroups the new student groups
	 */
	public void setStudentGroups(Set<UserGroup> studentGroups) {
		this.studentGroups = studentGroups;
	}

	/**
	 * Sets the supervisors.
	 *
	 * @param supervisors the new supervisors
	 */
	public void setSupervisors(Set<User> supervisors) {
		this.supervisors = supervisors;
	}

	/**
	 * Sets the templates.
	 *
	 * @param templates the new templates
	 */
	public void setTemplates(Set<DocEntry> templates) {
		this.templates = templates;
	}

	/**
	 * Sets the templates folder id.
	 *
	 * @param templatesFolderId the new templates folder id
	 */
	public void setTemplatesFolderId(String templatesFolderId) {
		this.templatesFolderId = templatesFolderId;
	}

	/**
	 * Sets the tutorials.
	 *
	 * @param tutorials the new tutorials
	 */
	public void setTutorials(Set<String> tutorials) {
		this.tutorials = tutorials;
	}

	/**
	 * Sets the tutors.
	 *
	 * @param tutors the new tutors
	 */
	public void setTutors(Set<User> tutors) {
		this.tutors = tutors;
	}

	/**
	 * Sets the writing activities.
	 *
	 * @param writingActivities the new writing activities
	 */
	public void setWritingActivities(Set<WritingActivity> writingActivities) {
		this.writingActivities = writingActivities;
	}

	/**
	 * Sets the year.
	 *
	 * @param year the new year
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Gets the domain name.
	 *
	 * @return the domain name
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * Sets the domain name.
	 *
	 * @param domainName the new domain name
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * Gets the automatic reviewers.
	 *
	 * @return the automatic reviewers
	 */
	public Set<User> getAutomaticReviewers() {
		return automaticReviewers;
	}

	/**
	 * Sets the automatic reviewers.
	 *
	 * @param automaticReviewers the new automatic reviewers
	 */
	public void setAutomaticReviewers(Set<User> automaticReviewers) {
		this.automaticReviewers = automaticReviewers;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Course clone(){
		Course course = new Course();
		
		Set<User> reviewers = new HashSet<User>();
		for(User user: this.getAutomaticReviewers()){
			if (user != null){
				reviewers.add(user.clone());
			}
		}
		
		course.setAutomaticReviewers(reviewers);
		course.setDomainName(this.getDomainName());
		course.setFolderId(this.getFolderId());
		if ( this.getId() != null && this.getId().longValue() > 0){
			course.setId(this.getId());
		}
		
		Set<User> lecturers = new HashSet<User>();
		for(User user: this.getLecturers()){
			if (user != null){
				lecturers.add(user.clone());
			}
		}
		course.setLecturers(lecturers);
		
		course.setName(this.getName());
		if (this.getOrganization() != null){
			course.setOrganization(this.getOrganization().clone());
		}
		course.setSemester(this.getSemester());
		course.setSpreadsheetId(this.getSpreadsheetId());
		
		Set<UserGroup> studentGroups = new HashSet<UserGroup>();
		for(UserGroup group: this.getStudentGroups()){
			if (group != null){
				studentGroups.add(group.clone());
			}
		}
		course.setStudentGroups(studentGroups);
		
		Set<User> supervisors = new HashSet<User>();
		for(User user: this.getSupervisors()){
			if (user != null){
				supervisors.add(user.clone());
			}
		}
		course.setSupervisors(supervisors);
		
		Set<DocEntry> docEntries = new HashSet<DocEntry>();
		for(DocEntry doc: this.getTemplates()){
			if (doc != null){
				docEntries.add(doc.clone());
			}
		}
		course.setTemplates(docEntries);
		course.setTemplatesFolderId(this.getTemplatesFolderId());
		
		Set<User> tutors = new HashSet<User>();
		for(User user: this.getTutors()){
			if (user != null){
				tutors.add(user.clone());
			}
		}
		course.setTutors(tutors);
		
		Set<String> tutorials = new HashSet<String>();
		for(String tutorial: this.getTutorials()){
			tutorials.add(tutorial);
		}
		course.setTutorials(tutorials);
		
		Set<WritingActivity> activities = new HashSet<WritingActivity>();
		for(WritingActivity activity: this.getWritingActivities()){
			if (activity != null){
				activities.add(activity.clone());
			}
		}
		course.setWritingActivities(activities);
		
		course.setYear(this.getYear());

		return course;
	}
}
