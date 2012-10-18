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
 * This class represents the organizations which use reviewer
 * @author mdagraca
 */
@Entity
public class Organization implements Serializable {

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
		
	/// collection for reviewer properties 
	@OneToMany(mappedBy="organization")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<OrganizationProperty> organizationProperties= new HashSet<OrganizationProperty>();
	
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
		return organization;
	}
	
	/**
	 * Return the value of a specific organization property
	 * @param propertyName property name to look for
	 * @return String value of the organization property
	 */
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
	
	
	public  String getDocumentsHome() {
		String value = getPropertyValue(Constants.REVIEWER_DOCUMENTS_HOME);
		return value;
	}
	
	public  String getUploadsHome() {
		String value = getPropertyValue(Constants.REVIEWER_UPLOADS_HOME);
		return value;
	}	

	public  String getGoogleDomain() {
		String value = getPropertyValue(Constants.REVIEWER_GOOGLE_DOMAIN);
		return value;
	}

	public  String getGooglePassword() {
		String value = getPropertyValue(Constants.REVIEWER_GOOGLE_PASSWORD);
		return value;
	}
	
	public  String getGoogleUsername() {
		String value = getPropertyValue(Constants.REVIEWER_GOOGLE_USERNAME);
		return value;
	}
	
	public  String getEmailPassword() {
		String value = getPropertyValue(Constants.REVIEWER_EMAIL_PASSWORD);
		return value;
	}
	
	public  String getEmailUsername() {
		String value = getPropertyValue(Constants.REVIEWER_EMAIL_USERNAME);
		return value;
	}

	public String getPrivateKeyPath() {
		String value = getPropertyValue(Constants.REVIEWER_PRIVATE_KEY);
		return value;
	}

	public String getProperty(String property) {
		String value = getPropertyValue(property);
		return value;
	}

	public String getPublicKeyPath() {
		String value = getPropertyValue(Constants.REVIEWER_PUBLIC_KEY);
		return value;
	}

	public String getEmptyFile() {
		String value = getPropertyValue(Constants.REVIEWER_EMPTY_FILE);
		return value;
	}
	
	public String getEmptyDocument() {
		String value = getPropertyValue(Constants.REVIEWER_EMPTY_DOCUMENT);
		return value;
	}

	public String getSMTPHost(){
		String value = getPropertyValue(Constants.REVIEWER_SMTP_HOST);
		return value;
	}
	
	public String getSMTPPort(){
		String value = getPropertyValue(Constants.REVIEWER_SMTP_PORT);
		return value;
	}
	
	public String getGlosserHost(){
		String value = getPropertyValue(Constants.REVIEWER_GLOSSER_HOST);
		return value;
	}
	
	public String getGlosserPort(){
		String value = getPropertyValue(Constants.REVIEWER_GLOSSER_PORT);
		return value;
	}
	
	public String getOrganizationLogoFile(){
		String value = getPropertyValue(Constants.ORGANIZATION_LOGO_FILE);
		return value;
	}
	
	public String getOrganizationLogoHome(){
		String value = getPropertyValue(Constants.ORGANIZATION_LOGO_HOME);
		return value;
	}

	public String getImageLogo(){
		String image = "";
		String imagePath = getPropertyValue(Constants.ORGANIZATION_LOGO_HOME);
		String imageFile = getPropertyValue(Constants.ORGANIZATION_LOGO_FILE);
		if (imagePath != null && imageFile != null){
			image = imagePath + imageFile;
		}
		return image;
	}
	
	public String getDecryptedGooglePassword() throws Exception {
		String encryptedValue = this.getGooglePassword();
		return encryptedValue;
	}
	
	public String getDecryptedEmailPassword() throws Exception {
		String encryptedValue = this.getEmailPassword();
		return encryptedValue;
	}
}
