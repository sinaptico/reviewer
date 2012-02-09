package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class UserGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String tutorial;
	@ManyToMany
	@JoinTable(name = "UserGroup_Users_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> users = new HashSet<User>();

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserGroup))
			return false;
		UserGroup other = (UserGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tutorial == null) {
			if (other.tutorial != null)
				return false;
		} else if (!tutorial.equals(other.tutorial))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTutorial() {
		return tutorial;
	}

	public Set<User> getUsers() {
		return users;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tutorial == null) ? 0 : tutorial.hashCode());
		return result;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTutorial(String tutorial) {
		this.tutorial = tutorial;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
