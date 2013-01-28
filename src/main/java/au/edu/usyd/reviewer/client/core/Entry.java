package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


/**
 * <p>Class used to define the basic fields of the Document entries such as title, if it's been locked, downloaded, 
 * if it's a local file and if so, the file name.</p>
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Entry implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	protected Long id;
	
	/** The title. */
	protected String title;
	
	/** The locked. */
	protected boolean locked = false;
	
	/** The downloaded. */
	protected boolean downloaded = false;
	
	/** The local file. */
	protected boolean localFile = false;
	
	/** The uploaded. */
	protected boolean uploaded = false;
	
	/** The file name. */
	protected String fileName;
	
	protected boolean deleted;
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Entry))
			return false;
		Entry other = (Entry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Gets the downloaded.
	 *
	 * @return the downloaded
	 */
	public boolean getDownloaded() {
		return downloaded;
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
	 * Gets the locked.
	 *
	 * @return the locked
	 */
	public boolean getLocked() {
		return locked;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
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
	 * Sets the downloaded.
	 *
	 * @param downloaded the new downloaded
	 */
	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
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
	 * Sets the locked.
	 *
	 * @param locked the new locked
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Checks if is local file.
	 *
	 * @return true, if is local file
	 */
	public boolean isLocalFile() {
		return localFile;
	}

	/**
	 * Sets the local file.
	 *
	 * @param localFile the new local file
	 */
	public void setLocalFile(boolean localFile) {
		this.localFile = localFile;
	}

	/**
	 * Checks if is uploaded.
	 *
	 * @return true, if is uploaded
	 */
	public boolean isUploaded() {
		return uploaded;
	}

	/**
	 * Sets the uploaded.
	 *
	 * @param uploaded the new uploaded
	 */
	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
