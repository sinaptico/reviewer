package au.edu.usyd.reviewer.server;



import java.util.ArrayList;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * This class is used to interact with the database.
 * It has methods to create, retrieve, update an delete courses 
 * @author mdagraca
 */
public class CourseDao extends ObjectDao{


	// Singleton
	public static CourseDao instance = null;
	
	private static String COURSE_ID ="id";
	private static String COURSE_NAME = "name";
	
	
	/**
	 * Constructor
	 */
	private CourseDao() {
		super();
	}	

	/**
	 * Return the only instance of the CourseDao class. It's a singleton
	 * @param sessionFactory database session factory
	 * @return  CourseDao the only instance of this class
	 */
	public static CourseDao getInstance(){
		if (instance == null){
			instance = new CourseDao();
		}
		return instance;
	}
	
	@Override
	/**
	 * Return the course with id equals to objectId
	 * @param id of the course to look for
	 * @return Object course whose id is equals to the objectId receive as parameter
	 */
	protected Object getObject(Long objectId) throws MessageException{
		Session session = getSession();
		Course course = null;
		try{
			session.beginTransaction();
			course = (Course) session.createCriteria(Course.class).add(Property.forName(COURSE_ID).eq(objectId)).uniqueResult();
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSE);
		} 
		if (course != null){
			course = course.clone();
		}
		return course;
	}
	
	/**
	 * Save the course its users and its courses
	 * @param course course to save
	 */
	public Course save(Course course) throws MessageException{
		try{
			course = (Course) super.save(course);
			if (course != null){
				course = course.clone();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_SAVE_COURSE);
		}
		return course;
	}
	
	/**
	 * Return the course with id equals to objectId
	 * @param id of the course to look for
	 * @return Course course whose id is equals to the objectId receive as parameter
	 */
	public Course load(Long courseId) throws MessageException{
		Course course = null;
		try{
			course = (Course) super.load(courseId);
			if (course != null){
				course = course.clone();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSE);
		}
		return course;
	}
	
	/**
	 * Return a list of courses
	 * @return list of courses
	 */
	public List<Course> getCourses(String courseName) throws MessageException{
		List<Course> courses = new ArrayList<Course>();
		try{
			List<Object> objects = super.loadObjects(courseName);
			for(Object obj: objects){
				Course course = (Course) obj;
				if (course != null){
					course = course.clone();
					courses.add(course);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
		return courses;
	}
	
	/**
	 * List of objects (courses)
	 * @return list of objects (courses)
	 */
	protected List<Object> getObjects(String courseName) throws MessageException{
		Session session = null;
		List<Course> courses = new ArrayList<Course>();
		try{
			session = getSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(Course.class);
			criteria.add(Restrictions.like(COURSE_NAME, courseName +"%"));
			criteria.addOrder( Order.asc(COURSE_NAME) );
			courses = criteria.list();
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
		List<Object> objects = new ArrayList<Object>();
		objects.addAll(courses);
		return objects;
	}

	
	
	/**
	 * Return the course with name equals to name received as parameter
	 * @param id of the course to look for
	 * @return Course course whose id is equals to the objectId received as parameter
	 */
	public Course load(String name) throws MessageException{
		Course course = null;
		try {
			course = (Course) super.load(name);
			if (course != null){
				course = course.clone();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSE);
		}
		return course;
	}
	

	/** 
	 * Returns a boolean to indicate if there are courses belong to the organization received as parameter 
	 * @param organization related to the courses 
	 * @return true if there are courses belong to the organization otherwise false
	 * @throws MessageException message to the user
	 */
	public boolean hasCourses(Organization organization) throws MessageException{
		Session session = null;
		boolean hasCourses = false;
		try{
			session = getSession();
			String query = "from Course course where organizationId=:organizationId";
			session.beginTransaction();
			List<Course> courses = session.createQuery(query).setParameter("organizationId", organization.getId()).list();
			session.getTransaction().commit();
			hasCourses =  courses.size() > 0;
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
		return hasCourses;
	}

	/**
	 * Return the course with id equals to the courseId received as parameter
	 * @param courseId course id to look for
	 * @return Course
	 * @throws MessageException message to the logged user
	 */
	public Course loadCourse(Long courseId) throws MessageException{
		Session session = null;
		Course course = null;
		try{
			session = getSession();
			session.beginTransaction();
			course = (Course) session.createCriteria(Course.class).add(Property.forName(COURSE_ID).eq(courseId)).uniqueResult();
			session.getTransaction().commit();
			course =  course.clone();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSE);
		}
		return course;
	}

	/**
	 * Return a list of courses belong to the organization received as parameter
	 * @param organization owner of the courses
	 * @return list of courses of the organization
	 * @throws MessageException message to the logged user
	 */
	public List<Course> loadCourses(Organization organization) throws MessageException{
		List<Course> result = new ArrayList<Course>();
		Session session = null;
		try{
			String query = "from Course course " + "where course.organization=:organization";
	        session = getSession();
	        session.beginTransaction();
	        List<Course> courses = session.createQuery(query).setParameter("organization", organization).list();
	        session.getTransaction().commit();
	        
	        for(Course course: courses){
	        	result.add(course.clone());
	        }
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
        return result;
	}	

//	/**
//	 * Return a list of courses belong to the organization and whose semester and year are equals to the parameters received
//	 * Use pagination with page and limit
//	 * @param semester course semester
//	 * @param year course year
//	 * @param organization organization owner of the course
//	 * @param int page used in pagination
//	 * @param int limit used in pagination
//	 * @return list of courses
//	 * @throws MessageException message to the logged user
//	 */
//	public List<Course> loadCourses(Integer semester, Integer year, Organization organization, int page, int limit) throws MessageException{
//		Session session = null;
//		List<Course> result = new ArrayList<Course>();
//		try{
//			session = getSession();
//			session.beginTransaction();
//			String query = "from Course course " + "where course.semester=:semester AND course.year=:year and course.organization=:organization";
//			List<Course> courses = session.createQuery(query).setParameter("semester", semester)
//															 .setParameter("year", year)
//															 .setParameter("organization", organization)
//															 .setMaxResults(limit).setFirstResult(limit * page).list();
//			session.getTransaction().commit();
//				
//			for(Course course: courses){
//				if (course != null){
//	 				result.add(course.clone());
//				}
//			}
//		} catch(HibernateException he){
//			if ( session != null && session.getTransaction() != null){
//				session.getTransaction().rollback();
//			}
//			he.printStackTrace();
//			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
//		}
//        return result;		
//	}
	
//	/**
//	 * Return all the courses of all the organization. Use pagination
//	 * @param semester
//	 * @param year
//	 * @param page
//	 * @param limit
//	 * @return
//	 * @throws MessageException
//	 */
//	public List<Course> loadCourses(Integer semester, Integer year, int page, int limit) throws MessageException{
//		Session session = null;
//		List<Course> result = new ArrayList<Course>();
//		try{
//			session = getSession();
//			session.beginTransaction();
//			String query = "from Course course " + "where course.semester=:semester AND course.year=:year";
//			List<Course> courses = session.createQuery(query).setParameter("semester", semester)
//															 .setParameter("year", year)
//															 .setMaxResults(limit)
//															 .setFirstResult(limit * page).list();
//			session.getTransaction().commit();
//				
//			for(Course course: courses){
//				if (course != null){
//					result.add(course.clone());
//				}
//			}
//		} catch(HibernateException he){
//			if ( session != null && session.getTransaction() != null){
//				session.getTransaction().rollback();
//			}
//			he.printStackTrace();
//			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
//		}
//        return result;		
//	}
	
	/**
	 * Return all the courses of the organization without pagination
	 * @param semester
	 * @param year
	 * @param organization
	 * @return
	 * @throws MessageException
	 */
	public List<Course> loadCourses(Integer semester, Integer year, Organization organization) throws MessageException{
		Session session = null;
		List<Course> result = new ArrayList<Course>();
		try{
			session = getSession();
			session.beginTransaction();
			String query = "from Course course " + "where course.semester=:semester AND course.year=:year and course.organization=:organization";
			
			List<Course> courses = session.createQuery(query).setParameter("semester", semester)
															 .setParameter("year", year)
															 .setParameter("organization", organization)
															 .list();				
			session.getTransaction().commit();
				
			for(Course course: courses){
				if (course != null){
					result.add(course.clone());
				}
			}
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
        return result;		
	}
	
	/**
	 * Return the courses belong to the organization where the user is student,tutor or lecturer. Use Pagination
	 * @param semester
	 * @param year
	 * @param page
	 * @param limit
	 * @param user
	 * @return
	 * @throws MessageException
	 */
	public List<Course> loadCourses(Integer semester, Integer year, Organization organization, User user, Integer limit, Integer page) throws MessageException{
		Session session = null;
		try{
			 
			String sQuery = "SELECT DISTINCT course FROM Course course ";
			String where = "";
			
			if ( user != null){
				sQuery +=  "LEFT JOIN course.lecturers lecturer " +
				   		 "LEFT JOIN course.tutors tutor " +
				   		 "LEFT JOIN course.studentGroups studentGroup " + 
				   		 "LEFT JOIN studentGroup.users student " ;
				where += "(lecturer=:user OR tutor=:user OR student=:user) ";
			}
				
			if (semester != null){
				if (!StringUtil.isBlank(where)){
					where += " AND course.semester=:semester";
				} else {
					where += "course.semester=:semester";
				}
			}
			
			if (year != null){
				if (!StringUtil.isBlank(where)){
					where += " AND course.year=:year";
				} else {
					where += "course.year=:year";
				}
			}
			
			if (organization != null){
				if (!StringUtil.isBlank(where)){
					where += " AND course.organization=:organization";
				} else {
					where += "course.organization=:organization";
				} 
			}
					
			session = this.getSession();
			session.beginTransaction();
			
			if (!StringUtil.isBlank(where)){
				sQuery += " WHERE " + where;
			}
			sQuery += " ORDER BY course.name";
			Query query= session.createQuery(sQuery);
			if (semester!=null){
				query.setParameter("semester", semester);
			}
			if (year != null){		 
				query.setParameter("year", year);
			}
			if (user!=null){
				query.setParameter("user", user);
			}
			if (organization!= null){
				query.setParameter("organization", organization);
			}
			if (limit == null || (limit != null && limit < 1)){
				limit = 10;
			}
			if (page == null || (page!=null && page < 1)){
				page = 1;
			}
			
			query.setMaxResults(limit);
			query.setFirstResult(limit * (page - 1));
			List<Course> courses = query.list();
			session.getTransaction().commit();
			List<Course> resultList = new ArrayList<Course>();
			for(Course course : courses){
				if (course != null){
					resultList.add(course.clone());
				}
			}
			return resultList;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_COURSE);
		}
	}
	
	@Override
	protected Object getObject(String name) throws MessageException{
		Session session = getSession();
		User user = (User) session.createCriteria(User.class).add(Property.forName(COURSE_NAME).eq(name)).uniqueResult();
		if (user != null){
			user = user.clone();
		}
		return user;
	}
}
