package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


/**
 * Class to define the document types that will include {@link Rubric Rubrics} into the system for the SpeedBack options.
 */
@Entity
public class DocumentType implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The number. */
	private Integer number;
	
	/** The name. */
	private String name;
	
	/** The genre. */
	private String genre;
	
	/** The rubrics. */
	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "DocumentType_Rubrics")
	@javax.persistence.OrderBy("number")
	private List<Rubric> rubrics = new ArrayList<Rubric>();	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DocumentType))
			return false;
		DocumentType other = (DocumentType) obj;
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
	 * Gets the rubrics.
	 *
	 * @return the rubrics
	 */
	public List<Rubric> getRubrics() {
		return rubrics;
	}

	/**
	 * Sets the rubrics.
	 *
	 * @param rubrics the new rubrics
	 */
	public void setRubrics(List<Rubric> rubrics) {
		this.rubrics = rubrics;
	}

	/**
	 * Sets the number.
	 *
	 * @param number the new number
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * Sets the genre.
	 *
	 * @param genre the new genre
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * Gets the genre.
	 *
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	public DocumentType clone(){
		DocumentType doc = new DocumentType();
		doc.setGenre(this.getGenre());
		if ( this.getId() != null && this.getId().longValue() > 0){
			doc.setId(this.getId());
		}
		doc.setName(this.getName());
		doc.setNumber(this.getNumber());
		
		List<Rubric> rubrics = new ArrayList<Rubric>();
		for(Rubric rubric : rubrics){
			if (rubric != null){
				rubrics.add(rubric.clone());
			}
		}
		doc.setRubrics(rubrics);
		
		return doc;
	}

}
