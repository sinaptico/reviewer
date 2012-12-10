package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


//TODO: Improve the model so more than one mark can be given for a deadline.

/**
 * <p>Class used to save the marks given to students, the model handles 1 mark 
 * per student per deadline.</p>
 */
@Entity
public class Grade implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The deadline. */
	@ManyToOne
	private Deadline deadline;
	
	/** The user. */
	@ManyToOne
	private User user;
	
	/** The value. */
	private Double value;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Grade))
			return false;
		Grade other = (Grade) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	/**
	 * Gets the deadline.
	 *
	 * @return the deadline
	 */
	public Deadline getDeadline() {
		return deadline;
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
	 * Gets the user.
	 *
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Double getValue() {
		return value;
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
	 * Sets the deadline.
	 *
	 * @param deadline the new deadline
	 */
	public void setDeadline(Deadline deadline) {
		this.deadline = deadline;
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
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(Double value) {
		this.value = value;
	}
	
	public Grade clone(){
		Grade grade = new Grade();
		
		if (this.getDeadline() != null){
			grade.setDeadline(this.getDeadline().clone());
		}
		
		grade.setId(this.getId());
		if ( this.getUser() != null){
			grade.setUser(this.getUser().clone());
		}
		grade.setValue(this.getValue());
		return grade;
	}
}
