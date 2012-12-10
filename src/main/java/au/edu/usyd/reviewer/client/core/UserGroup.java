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

/**
 * <p>Class to manage the student groups, includes the name of the group (usually a number), 
 * the tutorial they belong to and the list of {@link User users}.</p>
 */
@Entity
public class UserGroup implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	private Long id;
	
	/** The name. */
	private String name;
	
	/** The tutorial. */
	private String tutorial;
	
	/** The users. */
	@ManyToMany
	@JoinTable(name = "UserGroup_Users_User")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<User> users = new HashSet<User>();

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
	 * Gets the tutorial.
	 *
	 * @return the tutorial
	 */
	public String getTutorial() {
		return tutorial;
	}

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tutorial == null) ? 0 : tutorial.hashCode());
		return result;
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
	 * Sets the tutorial.
	 *
	 * @param tutorial the new tutorial
	 */
	public void setTutorial(String tutorial) {
		this.tutorial = tutorial;
	}

	/**
	 * Sets the users.
	 *
	 * @param users the new users
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public UserGroup clone(){
		UserGroup group = new UserGroup();
		group.setId(this.getId());
		group.setName(this.getName());
		group.setTutorial(this.getTutorial());
		
		Set<User> users = new HashSet<User>();
		for(User user: this.getUsers()){
			if (user != null){
				users.add(user.clone());
			}
		}
		group.setUsers(users);
		return group;
	}
}
