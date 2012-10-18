package au.edu.usyd.reviewer.server;

import java.util.List;




import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.ReviewerProperty;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;



public class OrganizationPropertyDao extends ObjectDao {

	// Singleton
	private static OrganizationPropertyDao instance =null;
	
	/**
	 * Constructor
	 */
	private OrganizationPropertyDao(){
		super();
	}
	
	public static OrganizationPropertyDao getInstance(){
		if (instance == null){
			instance = new OrganizationPropertyDao();
		}
		return instance;
	}
	
	@Override
	protected Object getObject(Long objectId) throws MessageException{
		Session session = getSession();
		session.beginTransaction();
		OrganizationProperty property = (OrganizationProperty) session.createCriteria(OrganizationProperty.class).add(Property.forName("organizationId").eq(objectId)).uniqueResult();
		session.getTransaction().commit();
		if (property != null){
			property = property.clone();
		}
		return property;
	}

	/** 
	 * The OrganizationProperty class doesn't have an instance variable called name so 
	 * these method can not be implemented. 
	 */
	@Override
	protected List<Object> getObjects(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** 
	 * The OrganizationProperty class doesn't have an instance variable called name so 
	 * these method can not be implemented. 
	 */
	protected Object getObject( String name) {
		return null;
	}

	/**
	 * Save the organization its users and its courses
	 * @param organization organization to save
	 */
	public OrganizationProperty save(OrganizationProperty organizationProperty) throws MessageException{
		organizationProperty = (OrganizationProperty) super.save(organizationProperty);
		if (organizationProperty != null){
			organizationProperty = organizationProperty.clone(); 
		}
		return organizationProperty;
	}
	
}
