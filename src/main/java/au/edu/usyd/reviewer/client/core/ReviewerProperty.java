package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;


import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import au.edu.usyd.reviewer.client.core.util.Constants;

/**
 * This class represent an organization property. This property is set with a value in OrganizationProperty   
 * @author mdgraca
 */
@Entity
public class ReviewerProperty implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//The organization id
	@Id
	@GeneratedValue
	private Long id;
	
	//The organization name
	@Column(unique = true, nullable = false)
	private String name;
		
	/** The organizations */
	@OneToMany(mappedBy="property")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<OrganizationProperty> organizationProperties= new HashSet<OrganizationProperty>();

	public ReviewerProperty(){
		
	}
	
	/** Getters and Setters **/
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	
	public Set<OrganizationProperty> getOrganizationsProperties() {
		return organizationProperties;
	}


	public void setOrganizationProperties(Set<OrganizationProperty> organizationProperties) {
		this.organizationProperties = organizationProperties;
	}
	
	public void addOrganizationProperties(OrganizationProperty organizationProperty){
		getOrganizationsProperties().add(organizationProperty);
	}
	
	public void deleteOrganization(Organization organization){
		getOrganizationsProperties().remove(organization);
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
		if (!(obj instanceof ReviewerProperty))
			return false;
		ReviewerProperty other = (ReviewerProperty) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
	
	public ReviewerProperty clone(){
		ReviewerProperty property = new ReviewerProperty();
		property.setId(this.getId());
		property.setName(this.getName());
		return property;
	}
	
	/**
	 * Return a boolean to indicate if the property is a password or not
	 * @return true if property is a password otherwise false;
	 */
	public boolean isPassword(){
		boolean result = Constants.REVIEWER_EMAIL_PASSWORD.equals(this.getName());
		result |= Constants.REVIEWER_GOOGLE_PASSWORD.equals(this.getName());
		return result;
	}
}
