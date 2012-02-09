package au.edu.usyd.reviewer.client.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class DocEntry extends Entry {

	private static final long serialVersionUID = 1L;
	@Column
	protected String domainName;
	@Column(unique = true)
	protected String documentId;
	@OneToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "DocEntry_Reviews_Review")
	protected Set<Review> reviews = new HashSet<Review>();
	@ManyToOne
	protected User owner;
	@ManyToOne
	protected UserGroup ownerGroup;	
	protected Date earlySubmitDate = null;	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DocEntry))
			return false;
		DocEntry other = (DocEntry) obj;
		if (documentId == null) {
			if (other.documentId != null)
				return false;
		} else if (!documentId.equals(other.documentId))
			return false;
		return true;
	}

	public String getDocumentId() {
		return documentId;
	}

	public User getOwner() {
		return owner;
	}

	public UserGroup getOwnerGroup() {
		return ownerGroup;
	}

	public Set<Review> getReviews() {
		return reviews;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentId == null) ? 0 : documentId.hashCode());
		return result;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setOwnerGroup(UserGroup ownerGroup) {
		this.ownerGroup = ownerGroup;
	}

	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Date getEarlySubmitDate() {
		return earlySubmitDate;
	}

	public void setEarlySubmitDate(Date earlySubmitDate) {
		this.earlySubmitDate = earlySubmitDate;
	}	
}
