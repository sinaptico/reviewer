package au.edu.usyd.reviewer.server;

import java.util.ArrayList;


import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * This class is used to interact with the database.
 * It has methods to create, retrieve, update an delete users 
 * @author mdagraca
 */
public class UserDao extends ObjectDao {

	// Database Fields
	private String USER_ID ="id";
	private String USER_LASTNAME ="lastname";
	private String USER_FIRSTNAME = "firstname";
	private String USER_EMAIL="email";
	private String ORGANIZATION = "organization";
	
	// Singleton
	public static UserDao instance = null;
	
	/**
	 * Constructor
	 */
	private UserDao() {
		super();
	}	
	
	/**
	 * Return the only instance of the UserDao class. It's a singleton
	 * @param sessionFactory dabase session factory
	 * @return  UserDao the only instance of this class
	 */
	public static UserDao getInstance(){
		if (instance == null){
			instance = new UserDao();
		}
		return instance;
	}
	
	@Override
	protected Object getObject(Long objectId) throws MessageException{
		Session session = null;
		User user = null;
		try{
			session = getSession();
			session.beginTransaction();
			user = (User) session.createCriteria(User.class).add(Property.forName(USER_ID).eq(objectId)).uniqueResult();
			if (user != null){
				user = user.clone();
			}
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USER);
		} 
		return user;
	}

	
	@Override
	protected List<Object> getObjects(String lastName) throws MessageException{
		Session session = null;
		List<Object> objects = new ArrayList<Object>();
		try{
			session = getSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.like(USER_LASTNAME, lastName +"%"));
			criteria.addOrder( Order.asc(USER_LASTNAME) );
			List<User>users = criteria.list();
			session.getTransaction().commit();
			for(User user: users){
				if (user!= null){
					objects.add(user.clone());
				}
			}
		}catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USERS);
		}	
		return objects;	
	}

	@Override
	protected Object getObject(String name) throws MessageException{
		Session session = null;
		User user = null;
		try{
			session = getSession();
			session.beginTransaction();
			user = (User) session.createCriteria(User.class).add(Property.forName(USER_LASTNAME).eq(name)).uniqueResult();
			if (user != null){
				user = user.clone();
			}
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USER);
		}
		return user;
	}
	

	/**
	 * Return a list of users with name equals to the name received as parameter and belong to the organization with the id received as parameter
	 * @param name of the users
	 * @para  userId id of the organization of the users
	 * @return list of users
	 * 
	 */
	public List<User> geUsers(String lastName, Organization organization) throws MessageException{
		List<User> usersResult = new ArrayList<User>();
		Session session = getSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.like(USER_LASTNAME, lastName +"%"));
		criteria.add(Restrictions.eq(ORGANIZATION, organization));
		criteria.addOrder( Order.asc(USER_LASTNAME) );
		List<User> users = criteria.list();
		session.getTransaction().commit();
		for(User user: users){
			if (user!=null){
				usersResult.add(user.clone());
			}
		}
		return usersResult;
	}
	
	
	/**
	 * Return a boolean to indicate if the user exists or not into the database
	 * @param user user to look ofr
	 * @return true if the user exists otherwise false
	 * @throws MessageException message to the user
	 */
	public boolean containsUser(User user) throws MessageException{
		user = getUniqueUserByField(USER_EMAIL, user.getEmail());
		return (user != null);
	}
	
	/**
	 * Return the user with id equals to the userId received as parameter
	 * @param String userId belong to the user
	 * @return User whose id is equals to the userId received as parameter
	 */
	public User load(Long userId) throws MessageException{
		User user = (User) super.load(userId);
		if (user != null){
			user = user.clone();
		}
		return user;
	}
	
	/** 
	 * Returns a boolean to indicate if there are users belong to the organization received as parameter 
	 * @param organization related to the users 
	 * @return true if there are users belong to the organization otherwise false
	 * @throws MessageException message to the user
	 */
	public boolean hasUsers(Organization organization) throws MessageException{
		String query = "from User user where organizationId=:organizationId";
		Session session = getSession();
		session.beginTransaction();
		List<User> users = session.createQuery(query).setParameter(ORGANIZATION, organization).list();
		session.getTransaction().commit();
		return (users.size() > 0);
	}

	/**
	 * Return a collection of user whose first name starts with firsName parameter, last name starts with lastName parameters and belong to the organization
	 * received as parameter. Use pagination with start row and returns maxRows users
	 * @param organization  user organization
	 * @param firstName user first name
	 * @param lastName user last name
	 * @param startRow start row
	 * @param maxRows max record returned
	 * @return collection of users
	 * @throws MessageException message to the logged user
	 */
	public Collection<User> geUsers(Organization organization, String firstName,  String lastName, int startRow,int  maxRows) throws MessageException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(User.class);
		if (!StringUtil.isBlank(firstName)){
			criteria.add(Restrictions.like(USER_FIRSTNAME, firstName +"%"));
		}
		
		if (!StringUtil.isBlank(lastName)){
			criteria.add(Restrictions.like(USER_LASTNAME, lastName +"%"));
		}
		
		criteria.add(Restrictions.eq(ORGANIZATION, organization));
		
		criteria.addOrder(Order.asc(USER_LASTNAME));
		criteria.setMaxResults(maxRows);
		criteria.setFirstResult(startRow);
		session.beginTransaction();
		List<User> users = criteria.list();
		session.getTransaction().commit();
		
		List<User> usersResult = new ArrayList<User>();
		for(User user: users){
			if(user!=null){
				usersResult.add(user.clone());
			}
		}
		return usersResult;
	}
	
	/**
	 * Returns a collection of users belong to all the organizations
	 * @param firstName
	 * @param lastName
	 * @param startRow
	 * @param maxRows
	 * @return
	 * @throws MessageException
	 */
	public Collection<User> geUsers(String firstName,  String lastName, int startRow,int  maxRows) throws MessageException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(User.class);
		if (!StringUtil.isBlank(firstName)){
			criteria.add(Restrictions.like(USER_FIRSTNAME, firstName +"%"));
		}
		
		if (!StringUtil.isBlank(lastName)){
			criteria.add(Restrictions.like(USER_LASTNAME, lastName +"%"));
		}
		
		criteria.addOrder(Order.asc(USER_LASTNAME));
		criteria.setMaxResults(maxRows);
		criteria.setFirstResult(startRow);
		session.beginTransaction();
		List<User> users = criteria.list();
		session.getTransaction().commit();
		
		List<User> usersResult = new ArrayList<User>();
		for(User user: users){
			if (user != null){
				usersResult.add(user.clone());
			}
		}
		return usersResult;
	}
	
	/**
	 * Return user whose email is equals to the email received as parameter
	 * @param email user email
	 * @return user
	 * @throws MessageException message to the logged user
	 */
	public User getUserByEmail(String email) throws MessageException{
		return getUniqueUserByField(USER_EMAIL, email);
	}
	
	/**
	 * Return the users whose field is equals to the field is equals to the field received as parameter and has the value "value"
	 * @param field user field
	 * @param value value of the field
	 * @return user
	 * @throws MessageException message to the logged user
	 */
	private User getUniqueUserByField(String field, String value) throws MessageException {
		Session session = getSession();
		session.beginTransaction();
		User user = (User) session.createCriteria(User.class).add(Property.forName(field).eq(value)).uniqueResult();
		session.getTransaction().commit();
		if (user != null){
			user = user.clone();
		}
		return user;
	}
	
	/**
	 * Return a user whose username is equals to the username received as parameter and belongs to the organization 
	 * @param username user name
	 * @param organization  user organization
	 * @return
	 * @throws MessageException
	 */
	public User getUserByUsername(String username, Organization organization) throws MessageException{
		Session session = getSession();
		session.beginTransaction();
		
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("username", username));
		criteria.add(Restrictions.eq(ORGANIZATION, organization));
		criteria.addOrder( Order.asc(USER_LASTNAME) );
		User user = (User) criteria.uniqueResult();
		session.getTransaction().commit();
		if (user != null){
			user = user.clone();
		}
		return user;
	}
	
	/**
	 * Save user
	 * @param user to save
	 * @return saved user
	 * @throws MessageException message to the logged user
	 */
	public User save(User user) throws MessageException{
		user = (User) super.save(user);
		return user.clone();
	}
	
	/**
	 * Return a collection of user whose firstname starts with firsName of the user or  lastname starts with lastname  of hte user and belong to the organization
	 * of the user
	 * @param user  user with fisrtname or lastname to look for
	 * @return collection of users
	 * @throws MessageException message to the logged user
	 */
	public Collection<User> geUsers(User user) throws MessageException {
		Session session = getSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(User.class);
		if (!StringUtil.isBlank(user.getFirstname())){
			criteria.add(Restrictions.like(USER_FIRSTNAME, user.getFirstname() +"%"));
		}
		
		if (!StringUtil.isBlank(user.getLastname())){
			criteria.add(Restrictions.like(USER_LASTNAME, user.getLastname() +"%"));
		}
		
		Organization organization = user.getOrganization();
		criteria.add(Restrictions.eq(ORGANIZATION, organization));
		
		criteria.addOrder(Order.asc(USER_LASTNAME));
		
		List<User> users = criteria.list();
		session.getTransaction().commit();
		
		List<User> usersResult = new ArrayList<User>();
		for(User aUser: users){
			if(aUser!=null){
				usersResult.add(aUser.clone());
			}
		}
		return usersResult;
	}
	
}
