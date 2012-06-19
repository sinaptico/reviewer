package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>Class used to manage all users in the application, includes email, name, surname, if it's a native speaker, 
 * if it's a wasm user and if not, the MD5 digest of their password. It also includes the user role for Tomcat authentication purposes.</p>.
 */
@Entity
public class User implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	protected String id;
	
	/** The email. */
	protected String email = null;
	
	/** The first name. */
	protected String firstname = null;
	
	/** The last name. */
	protected String lastname = null;
	
	/** The native speaker. */
	protected String nativeSpeaker = null;
	
	/** Boolean that show if it's a wasm user. */
	protected Boolean wasmuser = true; 
	
	/** MD5 digest of the password. */
	protected String password = null;
	
	/** The role_name. */
	@ElementCollection	
	@JoinTable(name="User_roles", joinColumns = @JoinColumn(name="id", referencedColumnName="id") )
	@LazyCollection(LazyCollectionOption.FALSE)	
	private Set<String> role_name = new HashSet<String>();	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Gets the firstname.
	 *
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the lastname.
	 *
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * Gets the native speaker.
	 *
	 * @return the native speaker
	 */
	public String getNativeSpeaker() {
		return nativeSpeaker;
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
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the firstname.
	 *
	 * @param firstname the new firstname
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the lastname.
	 *
	 * @param lastname the new lastname
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * Sets the native speaker.
	 *
	 * @param nativeSpeaker the new native speaker
	 */
	public void setNativeSpeaker(String nativeSpeaker) {
		this.nativeSpeaker = nativeSpeaker;
	}

	/**
	 * Gets the wasmuser.
	 *
	 * @return the wasmuser
	 */
	public Boolean getWasmuser() {
		return wasmuser;
	}

	/**
	 * Sets the wasmuser.
	 *
	 * @param wasmuser the new wasmuser
	 */
	public void setWasmuser(Boolean wasmuser) {
		this.wasmuser = wasmuser;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the role_name.
	 *
	 * @return the role_name
	 */
	public Set<String> getRole_name() {
		return role_name;
	}

	/**
	 * Sets the role_name.
	 *
	 * @param role_name the new role_name
	 */
	public void setRole_name(Set<String> role_name) {
		this.role_name = role_name;
	}	
}
