package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

	

import java.util.HashSet;
import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import au.edu.usyd.reviewer.client.core.util.Constants;

/**
 * <p>Class used to manage all users in the application, includes email, name, surname, if it's a native speaker, 
 * if it's a wasm user and if not, the MD5 digest of their password. It also includes the user role for Tomcat authentication purposes.</p>.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	/** The email. */
	@Column(unique = true, nullable = false)
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
	@JoinTable(name="User_roles", joinColumns = @JoinColumn(name="email", referencedColumnName="email") )
	@LazyCollection(LazyCollectionOption.FALSE)	
	private Set<String> role_name = new HashSet<String>();	

	/** The organization */
	@ManyToOne
	@JoinColumn(name="organizationId")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Organization organization;

	private String username;
	
	public User(){
		
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
	 * Gets the username from the email
	 *
	 * @return the username
	 */
	public String getUsername() {
		String usernameResult = null;
		if ( username == null && this.email != null){
//			String  expression="^[_a-z0-9-]+(\\.[_a-z0-9-]+)*"; 
//			Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE); 
//			Matcher matcher = pattern.matcher(email);
//			username = matcher.group(0);
			String email = getEmail();
			int i = email.indexOf("@");
			if ( i > 0){
				usernameResult = email.substring(0,i);
			}
			username = usernameResult;
		}
		return username;
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
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public User clone(){
		User user = new User();
		
		user.setId(this.getId());
		user.setEmail(this.getEmail());
		user.setFirstname(this.getFirstname());
		user.setLastname(this.getLastname());
		user.setNativeSpeaker(this.getNativeSpeaker());
		
		if (this.getOrganization() != null){
			user.setOrganization(this.getOrganization().clone());
		}
		user.setPassword(this.getPassword());
		
		Set<String> roles = new HashSet<String>();
		for(String role : this.getRole_name()){
			roles.add(role);
		}
		user.setRole_name(roles);
		user.setWasmuser(this.getWasmuser());
		return user;
	}
		
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void addRole(String role){
		this.getRole_name().add(role);
	}
	
	public boolean isSuperAdmin(){
		return this.getRole_name().contains(Constants.ROLE_SUPER_ADMIN);
	}
	
	public boolean isAdmin(){
		return this.getRole_name().contains(Constants.ROLE_ADMIN);
	}
	
	public boolean isGuest(){
		return this.getRole_name().contains(Constants.ROLE_GUEST);
	}
	
	public String getDomain(){
		String email = getEmail();
		int i = email.indexOf("@");
		String domain = email.substring(i+1,email.length());
		return domain;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
}
