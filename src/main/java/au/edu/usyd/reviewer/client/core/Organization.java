package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;



import java.util.HashSet;
import java.util.Set;


import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.OrganizationDao;
import au.edu.usyd.reviewer.server.Reviewer;


/**
 * This class represents the organizations which use reviewer
 * @author mdagraca
 */
@Entity
public class Organization implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	//The organization id
	@Id
	@GeneratedValue
	private Long id;
	
	//The organization name
	@Column(unique = true, nullable = false)
	private String name;
		
	/// collection for reviewer properties 
	@OneToMany(mappedBy="organization")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<OrganizationProperty> organizationProperties= new HashSet<OrganizationProperty>();
	

	/// collection emails of the organization 
	@OneToMany(mappedBy = "organization")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<EmailOrganization> emails= new HashSet<EmailOrganization>();

	private boolean deleted = false;
	private boolean activated = false;
	
	
	
	/** emailDomains are the domains of the emails that belong to the organization. 
	 * If this table has only one and this domain is equals to Google Domain then it means that the email for access to the Google
	 * will be username@googleDomain otherwise username.emailDomain@googleDomain 
	 */
	@ElementCollection
	@JoinTable(name = "Organization_Emails_Domains")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<String> emailDomains = new HashSet<String>();

	public Organization(){
	}
	
	/** Begin Getters and Setters **/
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
	
	public Set<OrganizationProperty> getOrganizationProperties() {
		return organizationProperties;
	}

	public void setOrganizationProperties(Set<OrganizationProperty> properties) {
		this.organizationProperties = properties;
	}
		
	
	public Set<EmailOrganization> getEmails() {
		return emails;
	}


	public void setEmails(Set<EmailOrganization> emails) {
		this.emails = emails;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deteled) {
		this.deleted = deteled;
	}
	
	public boolean isActivated() {
		return activated;
	}
	
	public void setActivated(boolean activated){
		this.activated = activated;
	}
	
	
	public Set<String> getEmailDomains() {
		return emailDomains;
	}

	public void setEmailDomains(Set<String> emailDomains) {
		this.emailDomains = emailDomains;
	}
	

	/** End Getters and Setters **/
	

	/**
	 * Add a property to the collection of properties
	 * @param property property to add
	 */
	public void addProperty(ReviewerProperty property, String value){
		OrganizationProperty orgProp = new OrganizationProperty();
		orgProp.setOrganization(this);
		orgProp.setOrganizationId(this.getId());
		orgProp.setProperty(property);
		orgProp.setPropertyId(property.getId());
		orgProp.setValue(value);
		getOrganizationProperties().add(orgProp);
	}

	/**
	 * Return the properties related with the organization.
	 * Take in consideration that this properties don't have the value for this organization.
	 * If you want to get the value use getOrganizationProperties()
	 * @return Set<ReviewerProperty> set of properties belong to the organization
	 */
	public Set<ReviewerProperty> getProperties(){
		Set<ReviewerProperty> properties = new HashSet<ReviewerProperty>();
		for(OrganizationProperty orgProp: getOrganizationProperties()){
			properties.add(orgProp.getProperty());
		}
		return properties;
	}
	/**
	 * Delete a property from the collection of properties
	 * @param property property to delete
	 */
	public void deleteProperty(ReviewerProperty property){
		getProperties().remove(property);
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
		if (!(obj instanceof Organization))
			return false;
		Organization other = (Organization) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	/**
	 * It returns a boolean to indicate if the organization has properties or not
	 * @return true if the organization has properties otherwise false
	 */
	public boolean hasProperties(){
		return (getOrganizationProperties().size() > 0);
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
	 * Clone the current organization
	 * @return organization cloned
	 */
	public Organization clone(){
		Organization organization = new Organization();
		organization.setId(this.getId());
		organization.setName(this.getName());
		
		for(OrganizationProperty property : this.getOrganizationProperties()){
			if (property != null && property.getProperty() != null){
				property.setOrganization(organization);
				organization.addProperty(property.getProperty().clone(), property.getValue());
			}
		}
		
		Set<EmailOrganization> emailsOrganization = new HashSet<EmailOrganization>();
		for(EmailOrganization email: this.getEmails()){
			if(email != null){
				emailsOrganization.add(email.clone());
			}
		}
		organization.setEmails(emailsOrganization);
		organization.setDeleted(this.isDeleted());
		organization.setActivated(this.isActivated());
		
		Set<String> emailDomains = new HashSet<String>();
		for(String domain: this.getEmailDomains()){
			emailDomains.add(domain);
		}
		organization.setEmailDomains(emailDomains);
		return organization;
	}
	
	/**
	 * Return the value of a specific organization property
	 * @param propertyName property name to look for
	 * @return String value of the organization property
	 */
	@JsonIgnore
	public String getPropertyValue(String propertyName){
		String value = null;
		for(OrganizationProperty organizationProperty :getOrganizationProperties()){
			ReviewerProperty property = organizationProperty.getProperty();
			if  (property.getName().equals(propertyName)){
				value = organizationProperty.getValue();
				break;
			}
		}
		return value;
	}
		
	@JsonIgnore
	public  String getGoogleDomain() {
		String value = getPropertyValue(Constants.REVIEWER_GOOGLE_DOMAIN);
		return value;
	}
	
	@JsonIgnore
	public  String getGooglePassword() {
		String value = getPropertyValue(Constants.REVIEWER_GOOGLE_PASSWORD);
		return value;
	}
	
	@JsonIgnore
	public  String getGoogleUsername() {
		String value = getPropertyValue(Constants.REVIEWER_GOOGLE_USERNAME);
		return value;
	}
	
	@JsonIgnore
	public  String getEmailPassword() {
		String value = getPropertyValue(Constants.REVIEWER_EMAIL_PASSWORD);
		return value;
	}
	
	@JsonIgnore
	public  String getEmailUsername() {
		String value = getPropertyValue(Constants.REVIEWER_EMAIL_USERNAME);
		return value;
	}

	@JsonIgnore
	public String getProperty(String property) {
		String value = getPropertyValue(property);
		return value;
	}

	
	@JsonIgnore
	public String getSMTPHost(){
		String value = getPropertyValue(Constants.REVIEWER_SMTP_HOST);
		return value;
	}
	
	@JsonIgnore
	public String getSMTPPort(){
		String value = getPropertyValue(Constants.REVIEWER_SMTP_PORT);
		return value;
	}
	
	@JsonIgnore
	public String getGlosserHost(){
		String value = getPropertyValue(Constants.REVIEWER_GLOSSER_HOST);
		return value;
	}
	
	@JsonIgnore
	public String getGlosserPort(){
		String value = getPropertyValue(Constants.REVIEWER_GLOSSER_PORT);
		return value;
	}
	
	@JsonIgnore
	public String getOrganizationLogoFile(){
		String value = getPropertyValue(Constants.ORGANIZATION_LOGO_FILE);
		return value;
	}
	
	public String getReviewerDomain(){
		String value = getPropertyValue(Constants.REVIEWER_DOMAIN);
		return value;
	}
	
	/**
	 * Return an email template 
	 * @param name name of the email template
	 * @return EmailOrganization Email of the organization
	 */
	public EmailOrganization getEmail(String name){
		EmailOrganization result = null;
		for(EmailOrganization email : getEmails()){
			if  (email != null && email.getName().equals(name)){
				result = email;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns a boolean to indicate if the organization uses AAF authentication (shibboleth) or not
	 * @return boolean
	 */
	public  boolean isShibbolethEnabled() {
		String value = getPropertyValue(Constants.ORGANIZATION_SHIBBOLETH_ENABLED);
		return StringUtil.stringToBool(value);
	}
	
	/**
	 * Return a string with the begining of the password of the new users in Google Apps
	 * @return
	 */
	public String getOrganizationPasswordNewUsers(){
		String value = getPropertyValue(Constants.ORGANIZATION_PASSWORD_NEW_USERS);
		return value;
	}
	
	
	public boolean hasEmails(){
		return getEmails().size() > 0;
	}
	
	public void addEmail(EmailOrganization email){
		EmailOrganization emailExist = null;
		
		if ( email != null && email.getName() != null){
			emailExist = getEmail(email.getName());
		}
		
		if (emailExist == null){
			getEmails().add(email);
		}
	}
	
	/**
	 * Return a boolean indicating if the domain received belongs to the organization emails domains or not 
	 * @param email
	 * @return
	 */
	public boolean domainBelongsToEmailsDomain(String domain){
		return  this.emailDomains.contains(domain);
	}
	
	/**
	 * Add a domain to emails domains
	 * @param domain
	 */
	public void addDomainToEmailsDomains(String domain){
		this.emailDomains.add(domain);
	}

	/**
	 * Return a boolean to say if the domains of the emails of organization contains the google apps domain (Google Domain) and it's the
	 * only email in this table for this organization
	 * @return true or false
	 */
	public boolean isGoogleDomianTheOnlyDomainInEmailDomains() {
		return  this.emailDomains.contains(this.getGoogleDomain()) && 
			   (this.emailDomains.size() == 1);
	}

}
