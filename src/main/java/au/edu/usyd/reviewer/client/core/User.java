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

@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	protected String id;
	protected String email = null;
	protected String firstname = null;
	protected String lastname = null;
	protected String nativeSpeaker = null;
	protected Boolean wasmuser = true; 
	protected String password = null;
	@ElementCollection	
	@JoinTable(name="User_roles", joinColumns = @JoinColumn(name="id", referencedColumnName="id") )
	@LazyCollection(LazyCollectionOption.FALSE)	
	private Set<String> role_name = new HashSet<String>();	

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

	public String getEmail() {
		return email;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getId() {
		return id;
	}

	public String getLastname() {
		return lastname;
	}

	public String getNativeSpeaker() {
		return nativeSpeaker;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setNativeSpeaker(String nativeSpeaker) {
		this.nativeSpeaker = nativeSpeaker;
	}

	public Boolean getWasmuser() {
		return wasmuser;
	}

	public void setWasmuser(Boolean wasmuser) {
		this.wasmuser = wasmuser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRole_name() {
		return role_name;
	}

	public void setRole_name(Set<String> role_name) {
		this.role_name = role_name;
	}	
}
