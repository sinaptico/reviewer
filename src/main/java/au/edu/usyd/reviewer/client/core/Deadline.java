package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * <p>Class used for the deadlines definitions of a writing activity, includes name, 
 * finish date, max grade and status.</p>
 */
@Entity
public class Deadline implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant STATUS_DEADLINE_NONE. */
	public static final int STATUS_DEADLINE_NONE = 0;
	
	/** The Constant STATUS_DEADLINE_START. */
	public static final int STATUS_DEADLINE_START = 1;
	
	/** The Constant STATUS_DEADLINE_FINISH. */
	public static final int STATUS_DEADLINE_FINISH = 2;

	/** The id. */
	@Id
	@GeneratedValue
	private Long id;
	
	/** The finish date. */
	private Date finishDate = null;
	
	/** The name. */
	private String name;
	
	/** The max grade. */
	private Double maxGrade = 100.0;
	
	/** The status. */
	private int status = STATUS_DEADLINE_NONE;

	/**
	 * Instantiates a new deadline.
	 */
	public Deadline() {

	}

	/**
	 * Instantiates a new deadline.
	 *
	 * @param name the name
	 */
	public Deadline(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Deadline))
			return false;
		Deadline other = (Deadline) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Gets the max grade.
	 *
	 * @return the max grade
	 */
	public Double getMaxGrade() {
		return maxGrade;
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
	 * Gets the status.
	 *
	 * @return the status
	 */
	public int getStatus() {
		return status;
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
	 * Sets the finish date.
	 *
	 * @param finishDate the new finish date
	 */
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
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
	 * Sets the max grade.
	 *
	 * @param maxGrade the new max grade
	 */
	public void setMaxGrade(Double maxGrade) {
		this.maxGrade = maxGrade;
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
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}
