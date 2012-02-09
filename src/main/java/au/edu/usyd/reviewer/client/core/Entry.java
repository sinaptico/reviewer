package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Entry implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	protected Long id;
	protected String title;
	protected boolean locked = false;
	protected boolean downloaded = false;
	protected boolean localFile = false;
	protected boolean uploaded = false;
	protected String fileName;

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

	public boolean getDownloaded() {
		return downloaded;
	}	
	
	public Long getId() {
		return id;
	}

	public boolean getLocked() {
		return locked;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isLocalFile() {
		return localFile;
	}

	public void setLocalFile(boolean localFile) {
		this.localFile = localFile;
	}

	public boolean isUploaded() {
		return uploaded;
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
