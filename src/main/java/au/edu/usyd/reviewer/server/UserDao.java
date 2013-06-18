package au.edu.usyd.reviewer.server;

import java.util.ArrayList;


import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
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
	private String ORGANIZATION_ID = "organizationId";
	private String USER_USERNAME = "username";
	
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
			he.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
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
		Session session = null;
		try{
			session = getSession();
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
		}catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USERS);
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
		boolean hasUsers = false;
		Session session = null;
		try{
			String query = "from User user where organizationId=:organizationId";
			session = getSession();
			session.beginTransaction();
			List<User> users = session.createQuery(query).setParameter(ORGANIZATION_ID, organization.getId()).list();
			session.getTransaction().commit();
			hasUsers = (users.size() > 0);
		}catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USERS);
		}
		return hasUsers;
	}

	/**
	 * Return a collection of user whose first name starts with firsName parameter, last name starts with lastName parameters and belong to the organization
	 * received as parameter. Use pagination with start row and returns maxRows users
	 * @param organization  user organization
	 * @param firstName user first name
	 * @param lastName user last name
	 * @param page page to show
	 * @param limit quantity of users per page
	 * @return collection of users
	 * @throws MessageException message to the logged user
	 */
	public Collection<User> geUsers(Organization organization, String firstName,  String lastName, int page,int  limit) throws MessageException {
		Session session = null;
		List<User> users = new ArrayList<User>();
		List<User> usersResult = new ArrayList<User>();
		try{
			session = getSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(User.class);
			
			if (!StringUtil.isBlank(firstName)){
				criteria.add(Restrictions.like(USER_FIRSTNAME, firstName +"%"));
			}
			
			if (!StringUtil.isBlank(lastName)){
				criteria.add(Restrictions.like(USER_LASTNAME, lastName +"%"));
			}
			
			criteria.add(Restrictions.eq(ORGANIZATION, organization));
			
			criteria.addOrder(Order.asc(USER_LASTNAME));
			criteria.setMaxResults(limit);
			criteria.setFirstResult(limit * (page - 1));
			session.getTransaction().commit();
			
			
			for(User user: users){
				if(user!=null){
					usersResult.add(user.clone());
				}
			}
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USERS);
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
		Session session = null;
		User user = null;
		try{
			session = getSession();
			session.beginTransaction();
			user = (User) session.createCriteria(User.class).add(Property.forName(field).eq(value)).uniqueResult();
			session.getTransaction().commit();
			if (user != null){
				user = user.clone();
			}
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
	 * Return a user whose username is equals to the username received as parameter and belongs to the organization 
	 * @param username user name
	 * @param organization  user organization
	 * @return
	 * @throws MessageException
	 */
	public User getUserByUsername(String username, Organization organization) throws MessageException{
		Session session = null;
		User user = null;
		try{
			session = getSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("username", username));
			criteria.add(Restrictions.eq(ORGANIZATION, organization));
			criteria.addOrder( Order.asc(USER_LASTNAME) );
			user = (User) criteria.uniqueResult();
			session.getTransaction().commit();
			if (user != null){
				user = user.clone();
			}
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USERS);
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
		if (user != null){
			return user.clone();
		}
		return user;
	}
	
	/**
	 * Return a collection of user whose firstname starts with firsName of the user or  lastname starts with lastname  of hte user and belong to the organization
	 * of the user
	 * @param user  user with fisrtname or lastname to look for
	 * @return collection of users
	 * @throws MessageException message to the logged user
	 */
	public Collection<User> geUsers(User user) throws MessageException {
		Session session = null;
		List<User> usersResult = new ArrayList<User>();
		try{
			session = getSession();
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
			
			
			for(User aUser: users){
				if(aUser!=null){
					usersResult.add(aUser.clone());
				}
			}
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USERS);
		}
		return usersResult;
	}

	public List<User> getUsers(Organization organization,Integer page, Integer limit, String roles, boolean assigned) throws MessageException{
		Session session = null;
		List<User> users = new ArrayList<User>();
		List<User> usersResult = new ArrayList<User>();
		try{
			session = getSession();
			session.beginTransaction();
			String queryString = "select distinct user from User user ";
			String where =		 "where user.organization=:organization ";
								 
			String conditions = "";
			
			boolean lecturers = roles != null && roles.contains(Constants.LECTURERS); 
			boolean tutors = roles != null && roles.contains(Constants.TUTORS);
			boolean students = roles!= null && roles.contains(Constants.STUDENTS);
			boolean all = roles != null && roles.contains(Constants.ALL); 
			
			if ( assigned && ( all || lecturers || tutors || students )){
				queryString +=", Course course ";
				where += "and user.organization=course.organization ";
			}
						
			if (all){
				lecturers = false;
				tutors = false;
				students = false;
			} 
			
			if (lecturers && assigned){
				queryString += "left join course.lecturers lecturer "; 
				conditions = "(lecturer=user ";
			} 

			if (tutors && assigned) {
				queryString += "left join course.tutors tutor ";
				if (conditions.equals("")){
					conditions = "(tutor=user ";
				} else {
					conditions += " OR tutor=user ";
				}
			} 
			
			if ( (lecturers || tutors) && !assigned && !all){
				queryString += " join user.role_name role " ;
				conditions += " ('" + Constants.ROLE_ADMIN + "' = role "; ;
			}
			
			if (students && assigned) {
				queryString += "join course.studentGroups studentGroup " + 
				 				"join studentGroup.users student ";
				if (conditions.equals("")){
					conditions = "(student=user ";
				} else {
					conditions += " OR student=user ";
				}
			} else if (students && !assigned && !all) {
				if ( conditions.equals("")){
					queryString += " join user.role_name role " ;
					conditions += " ('" + Constants.ROLE_GUEST + "' = role ";
				} else {
					conditions += " or '" + Constants.ROLE_GUEST + "' = role ";;
				}
			}
			
			if (!conditions.equals("")){
				conditions +=")";
				queryString += where + " and " + conditions;
			} else {
				queryString += where;
			}
			String order = " order by user.name";
			Query query = session.createQuery(queryString);
			query.setParameter(ORGANIZATION, organization);
			
			if (limit == null || (limit != null && limit < 1)){
				limit = 10;
			}
			query.setMaxResults(limit);
			
			if (page == null || (page!=null && page < 1)){
				page = 1;
			}
			query.setFirstResult(limit * (page - 1));
			
			users = query.list();
			
			session.getTransaction().commit();
			
			for(User user: users){
				if(user!=null){
					usersResult.add(user.clone());
				}
			}
			return usersResult;
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USERS);
		}
	}
	
	
	/**
	 * Return user whose email is equals to the email received as parameter
	 * @param email user email
	 * @return user
	 * @throws MessageException message to the logged user
	 */
	public List<User> getUserByUsername(String username) throws MessageException{
		Session session = null;
		List<User> users = new ArrayList<User>();;
		try{
			session = getSession();
			session.beginTransaction();
			List<User> usersList = new ArrayList<User>();
			usersList = session.createCriteria(User.class).add(Property.forName(USER_USERNAME).eq(username)).list();
			session.getTransaction().commit();
			for(User user: usersList){
				if(user!=null){
					users.add(user.clone());
				}
			}
			
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_USER);
		}
		return users;
	}
}
