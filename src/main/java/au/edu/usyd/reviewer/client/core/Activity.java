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

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Activity<E extends Entry> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int STATUS_NONE = 0;
	public static final int STATUS_START = 1;
	public static final int STATUS_FINISH = 2;

	@Id
	@GeneratedValue
	protected Long id;
	protected String name;
	protected int status = STATUS_NONE;
	@OneToMany(targetEntity = Entry.class)
	@Cascade(CascadeType.REMOVE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "Activity_Entries_Entry")
	protected Set<E> entries = new HashSet<E>();

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

	public Set<E> getEntries() {
		return entries;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setEntries(Set<E> entries) {
		this.entries = entries;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
