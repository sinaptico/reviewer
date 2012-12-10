package au.edu.usyd.reviewer.server;

import java.util.ArrayList;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import au.edu.usyd.reviewer.client.core.ReviewerProperty;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * This class implements method to interact with the ReviewerProperty table in the database.
 * See the comments of the methods in ObjectDato class
 * @author mdagraca
 *
 */
public class ReviewerPropertyDao extends ObjectDao {

	// Singleton
	private static ReviewerPropertyDao instance = null;
	
	/** Constructor 
	 */
	private ReviewerPropertyDao(){
		super();
	}
	
	
	public static ReviewerPropertyDao getInstance(){
		if (instance == null){
			instance = new ReviewerPropertyDao();
		}
		return instance;
	}
	
	@Override
	protected Object getObject(Long objectId) throws MessageException{
		Session session = null;
		ReviewerProperty property = null;
		try{
			session = getSession();
			session.beginTransaction();
			property = (ReviewerProperty) session.createCriteria(ReviewerProperty.class).add(Property.forName("id").eq(objectId)).uniqueResult();
			if (property != null){
				property = property.clone();
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEWER_PROPERTY);
		}
		return property;

	}

	@Override
	protected List<Object> getObjects(String name) throws MessageException {
		Session session = null;
		List<Object> objects = new ArrayList<Object>();
		try{
			session = getSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(ReviewerProperty.class);
			criteria.add(Restrictions.like("name", name +"%"));
			criteria.addOrder( Order.asc("name") );
			List<ReviewerProperty> properties = criteria.list();
			objects.addAll(properties);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEWER_PROPERTIES);
		}
		return objects;
	}

	@Override
	protected ReviewerProperty getObject(String name) throws MessageException{
		Session session = null;
		ReviewerProperty property = null;
		try{
			session = getSession();
			session.beginTransaction();
			property = (ReviewerProperty) session.createCriteria(ReviewerProperty.class).add(Property.forName("name").eq(name)).uniqueResult();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEWER_PROPERTY);
		}
		return property;
	}
	
	public ReviewerProperty load(String name) throws MessageException{
		Object obj = super.load(name);
		ReviewerProperty property = (ReviewerProperty) obj;
		if (property != null){
			property = property.clone();
		}
		return property;
	}
	
}
