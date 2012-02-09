package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Grade implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	protected Long id;
	@ManyToOne
	private Deadline deadline;
	@ManyToOne
	private User user;
	private Double value;
	
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
	
	public Deadline getDeadline() {
		return deadline;
	}
	
	public Long getId() {
		return id;
	}
	
	public User getUser() {
		return user;
	}
	
	public Double getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setDeadline(Deadline deadline) {
		this.deadline = deadline;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
