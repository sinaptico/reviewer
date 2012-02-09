package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Deadline implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int STATUS_DEADLINE_NONE = 0;
	public static final int STATUS_DEADLINE_START = 1;
	public static final int STATUS_DEADLINE_FINISH = 2;

	@Id
	@GeneratedValue
	private Long id;
	private Date finishDate = null;
	private String name;
	private Double maxGrade = 100.0;
	private int status = STATUS_DEADLINE_NONE;

	public Deadline() {

	}

	public Deadline(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Deadline))
			return false;
		Deadline other = (Deadline) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public Long getId() {
		return id;
	}

	public Double getMaxGrade() {
		return maxGrade;
	}

	public String getName() {
		return name;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMaxGrade(Double maxGrade) {
		this.maxGrade = maxGrade;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
