package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>Class used for email management of the course</p> 
 */
@Entity
public class EmailCourse extends Email implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	
	/** The course */
	@ManyToOne
//	@JoinColumn(name="courseId")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Course course;
	
	public EmailCourse(){
		super();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
	
	public EmailCourse clone(){
		EmailCourse email = new EmailCourse();

		email.setId(this.getId());
		email.setName(this.getName());
		email.setMessage(this.getMessage());
		
//		if (this.getCourse()!= null){
//			email.setCourse(this.getCourse().clone());
//		}
		
		return email;
	}
}
