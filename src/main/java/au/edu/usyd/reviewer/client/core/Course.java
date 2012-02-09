package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class Course implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private int year;
	private int semester;
	private String folderId;
	private String templatesFolderId;
	private String spreadsheetId;
	private String domainName;
	@ElementCollection
	@JoinTable(name = "Course_Tutorials")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<String> tutorials = new HashSet<String>();
	@ManyToMany
	@JoinTable(name = "Course_Lecturers_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> lecturers = new HashSet<User>();
	@ManyToMany
	@JoinTable(name = "Course_Tutors_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> tutors = new HashSet<User>();
	@ManyToMany
	@JoinTable(name = "Course_Supervisors_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> supervisors = new HashSet<User>();
	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "Course_StudentGroups_UserGroup")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<UserGroup> studentGroups = new HashSet<UserGroup>();
	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "Course_Activities_Activity")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<WritingActivity> writingActivities = new HashSet<WritingActivity>();
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "Course_Templates_DocEntry")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<DocEntry> templates = new HashSet<DocEntry>();
	@ManyToMany
	@JoinTable(name = "Course_Automatic_Reviewers_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> automaticReviewers = new HashSet<User>();

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

	public String getFolderId() {
		return folderId;
	}

	public Long getId() {
		return id;
	}

	public Set<User> getLecturers() {
		return lecturers;
	}

	public String getName() {
		return name;
	}

	public int getSemester() {
		return semester;
	}

	public String getSpreadsheetId() {
		return spreadsheetId;
	}

	public Set<UserGroup> getStudentGroups() {
		return studentGroups;
	}

	public Set<User> getSupervisors() {
		return supervisors;
	}

	public Set<DocEntry> getTemplates() {
		return templates;
	}

	public String getTemplatesFolderId() {
		return templatesFolderId;
	}

	public Set<String> getTutorials() {
		return tutorials;
	}

	public Set<User> getTutors() {
		return tutors;
	}

	public Set<WritingActivity> getWritingActivities() {
		return writingActivities;
	}

	public int getYear() {
		return year;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLecturers(Set<User> lecturers) {
		this.lecturers = lecturers;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
	}

	public void setStudentGroups(Set<UserGroup> studentGroups) {
		this.studentGroups = studentGroups;
	}

	public void setSupervisors(Set<User> supervisors) {
		this.supervisors = supervisors;
	}

	public void setTemplates(Set<DocEntry> templates) {
		this.templates = templates;
	}

	public void setTemplatesFolderId(String templatesFolderId) {
		this.templatesFolderId = templatesFolderId;
	}

	public void setTutorials(Set<String> tutorials) {
		this.tutorials = tutorials;
	}

	public void setTutors(Set<User> tutors) {
		this.tutors = tutors;
	}

	public void setWritingActivities(Set<WritingActivity> writingActivities) {
		this.writingActivities = writingActivities;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Set<User> getAutomaticReviewers() {
		return automaticReviewers;
	}

	public void setAutomaticReviewers(Set<User> automaticReviewers) {
		this.automaticReviewers = automaticReviewers;
	}
	
}
