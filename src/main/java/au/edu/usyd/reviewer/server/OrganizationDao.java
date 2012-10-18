package au.edu.usyd.reviewer.server;


import java.util.ArrayList;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * This class is used to interact with the database.
 * It has methods to create, retrieve, update an delete organizations 
 * @author mdagraca
 */
public class OrganizationDao extends ObjectDao{


	// Singleton
	public static OrganizationDao instance = null;
	
	/**
	 * Constructor
	 */
	private OrganizationDao() {
		super();
	}	

	/**
	 * Return the only instance of the OrganizationDao class. It's a singleton
	 * @param sessionFactory dabase session factory
	 * @return  OrganizationDao the only instance of this class
	 */
	public static OrganizationDao getInstance(){
		if (instance == null){
			instance = new OrganizationDao();
		}
		return instance;
	}
	
	@Override
	/**
	 * Return the organization with id equals to objectId
	 * @param id of the organization to look for
	 * @return Object organization whose id is equals to the objectId receive as parameter
	 */
	protected Object getObject(Long objectId) throws MessageException{
		Session session = getSession();
		session.beginTransaction();
		Organization organization = (Organization) session.createCriteria(Organization.class).add(Property.forName("id").eq(objectId)).uniqueResult();
		return organization;
	}
	
	/**
	 * Save the organization its users and its courses
	 * @param organization organization to save
	 */
	public Organization save(Organization organization) throws MessageException{
		organization = (Organization) super.save(organization);
		if (organization != null){
			organization = organization.clone();
		}
		return organization;
	}
	
	/**
	 * Return the organization with id equals to objectId
	 * @param id of the organization to look for
	 * @return Organization organization whose id is equals to the objectId receive as parameter
	 */
	public Organization load(Long organizationId) throws MessageException{
		Organization organization = (Organization) super.load(organizationId);
		if (organization != null){
			organization = organization.clone();
		}
		return organization;
	}
	
	/**
	 * Return a list of organizations
	 * @return list of organizations
	 */
	public List<Organization> getOrganizations(String organizationName) throws MessageException{
		List<Organization> organizations = new ArrayList<Organization>();
		List<Object> objects = super.loadObjects(organizationName);
	
		for(Object obj: objects){
			Organization organization = (Organization) obj;
			if (organization != null){
				organizations.add(organization.clone());
			}
		}
		return organizations;
	}
	
	/**
	 * List of objects (organizations)
	 * @return list of objects (organizations)
	 */
	protected List<Object> getObjects(String organizationName) throws MessageException{
		Session session = getSession();
		Criteria criteria = session.createCriteria(Organization.class);
		if (!StringUtil.isBlank(organizationName)){
			criteria.add(Restrictions.like("name", organizationName +"%"));
		}
		criteria.addOrder( Order.asc("name") );
		List<Organization> organizations = criteria.list();
		List<Object> objects = new ArrayList<Object>();
		objects.addAll(organizations);		
		return objects;
	}

	
	/**
	 * Return the organization whose name is equals to the name received as parameter
	 * @return object whose name is equals to the name received as parameter
	 */
	protected Object getObject(String name) throws MessageException{
		Session session = getSession();
		Organization organization = (Organization) session.createCriteria(Organization.class).add(Property.forName("name").eq(name)).uniqueResult();
		if (organization != null){
			organization = organization.clone();
		}
		return organization;
	}
	
	
	/**
	 * Return the organization with name equals to name received as parameter
	 * @param id of the organization to look for
	 * @return Organization organization whose id is equals to the objectId received as parameter
	 */
	public Organization load(String name) throws MessageException{
		Organization organization = (Organization) super.load(name);
		if (organization != null){
			organization = organization.clone();
		}
		return organization;
	}
	
	
	/** 
	 * Return all the organizations order by name
	 * @return list of organizations
	 */
	public List<Organization> getOrganizations(){
		List<Organization> organizations = new ArrayList<Organization>();
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(Organization.class);
			criteria.addOrder( Order.asc("name") );
			List<Organization> objects = criteria.list();
			session.getTransaction().commit();
			for(Object obj: objects){
				Organization organization = (Organization) obj;
				if (organization != null){
					organizations.add(organization.clone());
				}
			}
		} catch (Exception e) {
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
		}
		
		return organizations;
	}
}
