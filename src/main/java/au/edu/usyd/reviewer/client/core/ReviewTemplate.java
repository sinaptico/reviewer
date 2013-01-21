package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>Class used to define Review Templates, it contains name, description and a list of {@link Section Sections} that can be used as questions.</p>
 */
@Entity
public class ReviewTemplate implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The name. */
	private String name;
	
	/** The description. */
	private String description;
	
	/** The sections. */
	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "ReviewTemplates_Sections")
    @javax.persistence.OrderBy("number")
	private List<Section> sections = new ArrayList<Section>();	
	
	/** The organization */
	@ManyToOne
	@JoinColumn(name="organizationId")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Organization organization;
	
	private boolean deleted = false;
	
	public ReviewTemplate(){
		
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
		if (!(obj instanceof ReviewTemplate))
			return false;
		ReviewTemplate other = (ReviewTemplate) obj;
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

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
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
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the sections.
	 *
	 * @return the sections
	 */
	public List<Section> getSections() {
		return sections;
	}

	/**
	 * Sets the sections.
	 *
	 * @param sections the new sections
	 */
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public ReviewTemplate clone(){
		ReviewTemplate template = new ReviewTemplate();
		template.setName(this.getName());
		template.setDescription(this.getDescription());	
		template.setId(this.getId());
		
		if (this.getOrganization() != null){
			template.setOrganization(this.getOrganization().clone());
		}

		List<Section> sections = new ArrayList<Section>();
		for(Section section : this.getSections()){
			if (section != null){
				sections.add(section.clone());
			}
		}
		template.setSections(sections);
		
		return template;
	}

}
