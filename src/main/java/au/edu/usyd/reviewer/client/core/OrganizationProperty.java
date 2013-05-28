package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * This class represents the relationship between Organization and ReviewerProperty classes
 * @author mdgraca
 */
@Entity
@Table(name="Organization_Properties_ReviewerProperty")
@IdClass(OrganizationPropertyId.class)
public class OrganizationProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Id of the property
	@Id
	private Long propertyId;
	
	//Id of the organization
	@Id
	private Long organizationId;
	
	// The value of the property for the organization
	private String value;

	//Relationshiop with ReviewerProperty class
	@ManyToOne
	@JoinColumn(name = "propertyId", updatable = false, insertable = false, referencedColumnName = "id")
	//@PrimaryKeyJoinColumn(name="property", referencedColumnName="id")
	private ReviewerProperty property;
	
	//Relationship with Organization class
	@ManyToOne
	@JoinColumn(name = "organizationId", updatable = false, insertable = false, referencedColumnName = "id")
	//@PrimaryKeyJoinColumn(name="organizationId", referencedColumnName="id")
	@JsonIgnore
	private Organization organization;

	public OrganizationProperty(){
		
	}
	
	/** Begin Getters and Setters **/
	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ReviewerProperty getProperty() {
		return property;
	}

	public void setProperty(ReviewerProperty property) {
		this.property = property;
	}

	@JsonIgnore
	public Organization getOrganization() {
		return organization;
	}

	@JsonIgnore
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}	
	
	/** End Getters and Setters **/
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Grade))
			return false;
		OrganizationProperty other = (OrganizationProperty) obj;
		if (organizationId == null) {
			if (other.organizationId != null)
				return false;
		} else if (!organizationId.equals(other.organizationId))
			return false;
		
		if (propertyId == null) {
			if (other.propertyId != null)
				return false;
		} else if (!propertyId.equals(other.propertyId))
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
		result = prime * result + ((propertyId == null) || (organizationId == null) ? 0 : propertyId.hashCode() + organizationId.hashCode());
		return result;
	}
	
	public OrganizationProperty clone(){
		OrganizationProperty property = new OrganizationProperty();
		property.setOrganization(this.getOrganization());
		if (this.getProperty() != null){
			property.setProperty(this.getProperty().clone());
		}
		property.setOrganizationId(this.getOrganizationId());
		property.setPropertyId(this.getPropertyId());
		property.setValue(this.getValue());
		return property;
	}
}
