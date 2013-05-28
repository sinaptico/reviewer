package au.edu.usyd.reviewer.server;


import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.util.Constants;
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
		Organization organization = null;
		Session session = null;
		try{
			session = getSession();
			session.beginTransaction();
			organization = (Organization) session.createCriteria(Organization.class).add(Property.forName("id").eq(objectId)).uniqueResult();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATION);
		}
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
		Session session = null;
		List<Object> objects = new ArrayList<Object>();
		try{
			session = getSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(Organization.class);
			if (!StringUtil.isBlank(organizationName)){
				criteria.add(Restrictions.like("name", organizationName +"%"));
			}
			criteria.addOrder( Order.asc("name") );
			List<Organization> organizations = criteria.list();
			objects.addAll(organizations);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATIONS);
		}
		return objects;
	}

	
	/**
	 * Return the organization whose name is equals to the name received as parameter
	 * @return object whose name is equals to the name received as parameter
	 */
	protected Object getObject(String name) throws MessageException{
		Session session = null;
		Organization organization = null;
		try{
			session = getSession();
			session.beginTransaction();
			organization = (Organization) session.createCriteria(Organization.class).add(Property.forName("name").eq(name)).uniqueResult();
			if (organization != null){
				organization = organization.clone();
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATION);
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
	public List<Organization> getOrganizations() throws MessageException{
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
	
	
	public Set<OrganizationProperty> getOrganizationProperties(Long organizationId) throws MessageException{
		Set<OrganizationProperty> properties = new HashSet<OrganizationProperty>();
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			List<OrganizationProperty> organizationProperties =  session.createCriteria(OrganizationProperty.class).add(Property.forName("organizationId").eq(organizationId)).list();
			session.getTransaction().commit();
			for(OrganizationProperty organizationProperty : organizationProperties){
				if (organizationProperty != null){
					properties.add(organizationProperty.clone());
				}
			}
			return properties;
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATIONS);
		}
	}

	/**
	 * Returns all the organization with pagination. If the name is not empty it uses it as filter
	 * @param page page to look for
	 * @param limit quantity of organizations per page
	 * @param name name to filter
	 * @return List of Organizations
	 * @throws MessageException
	 */
	public List<Organization> getOrganizations(Integer page, Integer limit, String name) throws MessageException{
		List<Organization> organizations = new ArrayList<Organization>();
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(Organization.class);
			if (!StringUtil.isBlank(name)){
				criteria.add(Restrictions.like("name", name +"%"));
			}
			criteria.addOrder( Order.asc("name") );
			if (limit == null || (limit != null && limit < 1)){
				limit = 10;
			}
			criteria.setMaxResults(limit);
			if (page == null || (page!=null && page < 1)){
				page = 1;
			}
			criteria.setFirstResult(limit * (page - 1));
			
		    organizations = criteria.list();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			} 
			throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATIONS);
		}
		return organizations;
	}
	
	
	public Organization getOrganizationByDomain(String domain) throws MessageException{
		Organization organization = null;
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			String sQuery = "SELECT DISTINCT organization FROM Organization organization "+
							"LEFT JOIN FETCH organization.emailDomain emailDomain " +
							"WHERE emailDomain=:domain";
			Query query= session.createQuery(sQuery);
			if (domain!=null){
				query.setParameter("domain", domain.toLowerCase());
			}
			organization = (Organization) query.uniqueResult();
			if (organization != null){
				organization = organization.clone();
			}
			return organization;
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATION);
		}
	}
}
