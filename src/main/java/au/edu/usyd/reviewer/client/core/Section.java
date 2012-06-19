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
 * Class to define sections/question in a {@link ReviewTemplate Review Template}, it includes number (for sorting), 
 * type (open question, multiple choice), text (content) and tool (When using automatic responses from Glosser).
 */
@Entity
public class Section implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant OPEN_QUESTION. */
	public static final Integer OPEN_QUESTION = 0;
	
	/** The Constant MULTIPLE_CHOICE. */
	public static final Integer MULTIPLE_CHOICE = 1;
	
	/** The Constant SCALE. */
	public static final Integer SCALE = 2;
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The number. */
	private Integer number;
	
	/** The type. */
	private Integer type;
	
	/** The text. */
	private String text;
	
	/** The Glosser tool used to retrieve and automatic response. */
	private String tool; 
	
	/** The choices. */
	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "Sections_Choices")
	@javax.persistence.OrderBy("number")
	private List<Choice> choices = new ArrayList<Choice>();		

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Section))
			return false;
		Section other = (Section) obj;
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
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
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
	 * Gets the choices.
	 *
	 * @return the choices
	 */
	public List<Choice> getChoices() {
		return choices;
	}

	/**
	 * Sets the choices.
	 *
	 * @param choices the new choices
	 */
	public void setChoices(List<Choice> choices) {
		this.choices = choices;
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
	 * Sets the number.
	 *
	 * @param number the new number
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * Gets the tool.
	 *
	 * @return the tool
	 */
	public String getTool() {
		return tool;
	}

	/**
	 * Sets the tool.
	 *
	 * @param tool the new tool
	 */
	public void setTool(String tool) {
		this.tool = tool;
	}	
	
}
