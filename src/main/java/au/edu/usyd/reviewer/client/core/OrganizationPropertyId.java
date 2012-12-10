package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;


/**
 * This class represents the organization property id used by hibernate
 * @author mdagraca
 *
 */
public class OrganizationPropertyId implements Serializable {

	// organization id
	private Long organizationId;
	 
	// property id
	private Long propertyId;
		 	 
	public OrganizationPropertyId(){
		
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
		if (!(obj instanceof Grade))
			return false;
		OrganizationPropertyId other = (OrganizationPropertyId) obj;
		if (organizationId == null) {
			if (other.organizationId != null)
				return false;
		} else if (!organizationId.equals(other.organizationId))
			return false;
		
		if (propertyId == null) {
			if (other.propertyId != null)
				return false;
		} else if (!propertyId.equals(other.propertyId))
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
		result = prime * result + ((propertyId == null) || (organizationId == null) ? 0 : propertyId.hashCode() + organizationId.hashCode());
		return result;
	}
}
		