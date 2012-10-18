package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 * <p>Main class used to manage feedback ratings, it includes the document entry and owner.  
 * This class is extended by {@link GeneralRating GeneralRating} and {@link QuestionRating QuestionRating}. </p>
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Rating implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The entry. */
	@ManyToOne
	protected Entry entry;
	
	/** The owner. */
	@ManyToOne
	protected User owner;

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rating other = (Rating) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Gets the entry.
	 *
	 * @return the entry
	 */
	public Entry getEntry() {
		return entry;
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
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public User getOwner() {
		return owner;
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
	 * Sets the entry.
	 *
	 * @param entry the new entry
	 */
	public void setEntry(Entry entry) {
		this.entry = entry;
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
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public Rating clone(){
		Rating rating = new Rating();
		if (this.getEntry() != null){
			rating.setEntry(this.getEntry());
		}
		
		rating.setId(this.getId());
		if (this.getOwner() != null){
			rating.setOwner(owner.clone());
		}
		return rating;
	}
}
