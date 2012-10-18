package au.edu.usyd.reviewer.server;

import java.util.ArrayList;
import java.util.List;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * This class has methods interacts with the database
 * @author mdagraca
 *
 */
public abstract class ObjectDao {
	
	protected SessionFactory sessionFactory;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/**
	 * Getter for current database session
	 * @return hibernate session
	 */
	protected Session getSession() throws MessageException{
		SessionFactory sessionFactory = Reviewer.getHibernateSessionFactory();
		try{
			return sessionFactory.getCurrentSession();
		} catch(HibernateException he){
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_HIBERNATE_SESSION_MESSAGE);   
		}
	}
	
	/**
	 * Delete the object received as parameter
	 * @param object to delete
	 */
	protected void delete(Object object) throws MessageException {
		Session session = getSession();
		try{
			session.beginTransaction();
			session.delete(object);
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_DELETE_MESSAGE);
		}
	}
	

	/**
	 * Save or update the object received as parameter in the database
	 * @param object to save or update
	 */
	protected Object save(Object object) throws MessageException{
		Session session = getSession();
		try{
			session.beginTransaction();
			session.saveOrUpdate(object);
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_SAVE_MESSAGE);
		}
		return object;
	}
	
	/**
	 * Return the object with id equals to objectId
	 * The method getObject must be implemented by all the subclass of ObjectDao
	 * @param object id to look for
	 * @return object
	 */
	protected Object load(Long objectId) throws MessageException{
		Object object = null;
		Session session = getSession();
		try{
			session.beginTransaction();
			object = getObject (objectId);
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_LOAD_MESSAGE);
		} 
		return object;
	}
	
	/**
	 * This method returns an object
	 * @param session  hibernate session
	 * @param name the name of object  starts with the name received as parameter
	 * @return object whose name starts with the name received as parameter
	 */
	protected Object load(String name) throws MessageException {
		Object object = null;
		Session session = getSession();
		try{
			session.beginTransaction();
			object = getObject (name);
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_LOAD_MESSAGE);
		} 
		return object;
	}
	
	/**
	 * This method returns a list of object
	 * @param session  hibernate session
	 * @param name the name of the objects in the list starts with the name received as parameter
	 * @return list of objects whose name starts with the name received as parameter
	 */
	public List<Object> loadObjects(String name) throws MessageException{
		List<Object> objects = new ArrayList<Object>();
		Session session = this.getSession();
		try{
			session.beginTransaction();
			objects = getObjects(name);
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_LOAD_MESSAGE);
		} 
        return objects;
	}	
	
	/** Abstract methods that must be implemented by the subclasses of ObjectDao.
	 *  The objects returns by these methods will be instances of the subclasses. 
	 **/
	
	/**
	 * This method returns a specific object
	 * @param objectId id of the object to obtain
	 * @return object with id equals to the object id received as parameter
	 */
	protected abstract Object getObject(Long objectId)throws MessageException;
	
	/**
	 * This method returns a list of object
	 * @param name the name of the objects in the list starts with the name received as parameter
	 * @return list of objects whose name starts with the name received as parameter
	 */
	protected abstract List<Object> getObjects(String name)throws MessageException;
	/**
	 * This method returns an object
	 * @param name the name of object  starts with the name received as parameter
	 * @return object whose name starts with the name received as parameter
	 */
	protected abstract Object getObject(String name)throws MessageException;
	
	
}
