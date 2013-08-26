package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;



import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


/**
 * <p>Class for writing and reviewing activities management includes 
 * the fields Status, activity name and Entries.</p>
 *
 * @param <E> the element type
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Activity<E extends Entry> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant STATUS_NONE. */
	public static final int STATUS_NONE = 0;
	
	/** The Constant STATUS_START. */
	public static final int STATUS_START = 1;
	
	/** The Constant STATUS_FINISH. */
	public static final int STATUS_FINISH = 2;

	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The name. */
	protected String name;
	
	/** The status. */
	protected int status = STATUS_NONE;
	
	/** The entries. */
	@OneToMany(targetEntity = Entry.class)
	@Cascade(CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "Activity_Entries_Entry")
	protected Set<E> entries = new HashSet<E>();

	protected boolean deleted = false;
	
	/** this property indicates if the activity is still being saving or not **/
	protected boolean saving = false;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Activity))
			return false;
		Activity<?> other = (Activity<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Gets the entries.
	 *
	 * @return the entries
	 */
	public Set<E> getEntries() {
		return entries;
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
	 * Sets the entries.
	 *
	 * @param entries the new entries
	 */
	public void setEntries(Set<E> entries) {
		this.entries = entries;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isSaving() {
		return saving;
	}

	public void setSaving(boolean saving) {
		this.saving = saving;
	}
	
	
}
