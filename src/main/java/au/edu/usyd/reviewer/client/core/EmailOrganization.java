package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>Class used for email management of the organizations</p> 
 */
@Entity
public class EmailOrganization extends Email implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The organization */
	@ManyToOne
//	@JoinColumn(name="organizationId")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Organization organization;
	
	public EmailOrganization(){
		super();
	}
	
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	public EmailOrganization clone(){
		EmailOrganization email = new EmailOrganization();
		
		email.setId(this.getId());
		email.setName(this.getName());
		email.setMessage(this.getMessage());
		
//		if (this.getOrganization()!= null){
//			email.setOrganization(this.getOrganization().clone());
//		}
		
		return email;
	}
}
