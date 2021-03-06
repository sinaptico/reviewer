package au.edu.usyd.reviewer.server;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import au.edu.usyd.reviewer.client.core.WritingActivity;
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
	private static String COURSE_DELETED = "deleted";
	
	
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
			course = (Course) session.createCriteria(Course.class)
									 .add(Property.forName(COURSE_ID).eq(objectId))
									 .add(Property.forName(COURSE_DELETED).eq(false))
									 .uniqueResult();
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
			if (course != null && !course.isDeleted()){
				Set<WritingActivity> activities = new HashSet<WritingActivity>();
				for(WritingActivity activity : course.getWritingActivities()){
					if (!activity.isDeleted()){
						activities.add(activity);	
					}
				}
				course.setWritingActivities(activities);
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
				if (course != null && !course.isDeleted()){
					Set<WritingActivity> activities = new HashSet<WritingActivity>();
					for(WritingActivity activity : course.getWritingActivities()){
						if (!activity.isDeleted()){
							activities.add(activity);	
						}
					}
					course.setWritingActivities(activities);
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
			criteria.add(Restrictions.eq(COURSE_DELETED, false));
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
			if (course != null && !course.isDeleted()){
				Set<WritingActivity> activities = new HashSet<WritingActivity>();
				for(WritingActivity activity : course.getWritingActivities()){
					if (!activity.isDeleted()){
						activities.add(activity);	
					}
				}
				course.setWritingActivities(activities);
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
			String query = "from Course course where organizationId=:organizationId and course.deleted=false";
			session.beginTransaction();
			List<Course> courses = session.createQuery(query)
										  .setParameter("organizationId", organization.getId()).list();
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
			course = (Course) session.createCriteria(Course.class)
									 .add(Property.forName(COURSE_ID).eq(courseId))
									 .add(Property.forName(COURSE_DELETED).eq(false))
									 .uniqueResult();
			session.getTransaction().commit();
			if (course != null){
				Set<WritingActivity> activities = new HashSet<WritingActivity>();
				for(WritingActivity activity : course.getWritingActivities()){
					if (!activity.isDeleted()){
						activities.add(activity);	
					}
				}
				course.setWritingActivities(activities);
				return course.clone();
			}
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
	 * Return a list of courses not deleted belong to the organization received as parameter
	 * @param organization owner of the courses
	 * @return list of courses of the organization
	 * @throws MessageException message to the logged user
	 */
	public List<Course> loadCourses(Organization organization) throws MessageException{
		List<Course> result = new ArrayList<Course>();
		Session session = null;
		try{
			String query = "from Course course " + "where course.organization=:organization and course.deleted=false";
	        session = getSession();
	        session.beginTransaction();
	        List<Course> courses = session.createQuery(query).setParameter("organization", organization).list();
	        session.getTransaction().commit();
	        
	        for(Course course: courses){
	        	if (course != null){
	        		Set<WritingActivity> activities = new HashSet<WritingActivity>();
					for(WritingActivity activity : course.getWritingActivities()){
						if (!activity.isDeleted()){
							activities.add(activity);	
						}
					}
					course.setWritingActivities(activities);
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
			String query = "from Course course " + 
						   "where course.semester=:semester AND course.year=:year and course.organization=:organization " + 
						   " and course.deleted = false";
			
			List<Course> courses = session.createQuery(query).setParameter("semester", semester)
															 .setParameter("year", year)
															 .setParameter("organization", organization)
															 .list();				
			session.getTransaction().commit();
				
			for(Course course: courses){
				if (course != null){
					Set<WritingActivity> activities = new HashSet<WritingActivity>();
					for(WritingActivity activity : course.getWritingActivities()){
						if (!activity.isDeleted()){
							activities.add(activity);	
						}
					}
					course.setWritingActivities(activities);
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
	public List<Course> loadCourses(Integer semester, Integer year, Organization organization, User user, 
			                        Integer limit, Integer page, String tasks, boolean finished) throws MessageException{
		Session session = null;
		try{
			 
			String sQuery = "SELECT DISTINCT course FROM Course course ";
			String where = "";
			if ( user != null){
				where = "(lecturer=:user OR tutor=:user OR student=:user)";
			}
		
			if ( tasks != null && tasks.contains(Constants.WRITING)){
					sQuery +=  "LEFT JOIN FETCH course.lecturers lecturer " +
				   	   "LEFT JOIN FETCH course.tutors tutor " +
				   	   "LEFT JOIN FETCH course.studentGroups studentGroup " + 
				   	   "LEFT JOIN FETCH studentGroup.users student " +
				   	   "JOIN FETCH course.writingActivities writingActivity " +
				   	   "JOIN FETCH writingActivity.entries docEntry " + 
				   	   "LEFT JOIN FETCH docEntry.ownerGroup ownerGroup " +
				   	   "LEFT JOIN FETCH ownerGroup.users owner ";
					if (user != null){
						where += " AND (docEntry.owner=:user OR owner=:user)";
					}
			
				
			} else if ( tasks != null && tasks.contains(Constants.REVIEWING)){
				sQuery +=  "LEFT JOIN FETCH course.lecturers lecturer " +
					   	   "LEFT JOIN FETCH course.tutors tutor " +
					   	   "LEFT JOIN FETCH course.studentGroups studentGroup " + 
					   	   "LEFT JOIN FETCH studentGroup.users student " +
					   	   "JOIN FETCH course.writingActivities writingActivity " +
					   	   "JOIN FETCH writingActivity.reviewingActivities reviewingAcitvity " +
					   	   "JOIN FETCH reviewingAcitvity.entries reviewEntry ";
				if (user != null){
					where += " AND (reviewEntry.owner=:user)";
				}
				if (!finished){
					where = where + "AND (reviewingAcitvity.status = 1)";	
				}
			} else {
				sQuery +=  "LEFT JOIN course.lecturers lecturer " +
				   		   "LEFT JOIN course.tutors tutor " +
				   		   "LEFT JOIN course.studentGroups studentGroup " + 
				   		   "LEFT JOIN studentGroup.users student " ;
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
			if (!StringUtil.isBlank(where)){
				where += " course.deleted = false";
			} else {
				where += " and course.deleted = false";
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
					Set<WritingActivity> activities = new HashSet<WritingActivity>();
					for(WritingActivity activity : course.getWritingActivities()){
						if (!activity.isDeleted()){
							activities.add(activity);	
						}
					}
					course.setWritingActivities(activities);
					resultList.add(course.clone());
				}
			}
			return resultList;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
	}
	
	@Override
	protected Object getObject(String name) throws MessageException{
		Session session = getSession();
		Course course = (Course) session.createCriteria(Course.class)
										.add(Property.forName(COURSE_NAME).eq(name))
										.add(Property.forName(COURSE_DELETED).eq(false)).uniqueResult();
		if (course != null){
			Set<WritingActivity> activities = new HashSet<WritingActivity>();
			for(WritingActivity activity : course.getWritingActivities()){
				if (!activity.isDeleted()){
					activities.add(activity);	
				}
			}
			course.setWritingActivities(activities);
			course = course.clone();
		}
		return course;
	}
	
	public Course loadCourseWhereWritingActivity(WritingActivity writingActivity) throws MessageException{
		Session session = null;
		try{
			String query = "from Course course " + 
			"join fetch course.writingActivities writingActivity " + 
			"where writingActivity=:writingActivity "
			+ " and course.deleted = false  and writingActivity.deleted = false";
			session = this.getSession();
			session.beginTransaction();
			Course course = (Course) session.createQuery(query).setParameter("writingActivity", writingActivity).uniqueResult();
			session.getTransaction().commit();
			if (course != null){
				course = course.clone();
			}
			return course;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_COURSE);
		}
	}

	/**
	 * Return all the deleted courses of the organization corresponding to the semester and year received as parameters.
	 * @param semester semester of the courses
	 * @param year year of the courses
	 * @param organization organization owner of the courses
	 * @param limit quantity of courses per page
	 * @param page page to show
	 * @return List of courses
	 * @throws MessageException message to the logged user
	 */
	public List<Course> loadDeletedCourses(Integer semester, Integer year, Organization organization, 
            Integer limit, Integer page) throws MessageException{
		Session session = null;
		try{
			 
			String sQuery = "SELECT DISTINCT course FROM Course course " + 
							"LEFT JOIN course.lecturers lecturer " +
							"LEFT JOIN course.tutors tutor " +
							"LEFT JOIN course.studentGroups studentGroup " + 
							"LEFT JOIN studentGroup.users student " ;;
			String where = "";
				
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
			if (!StringUtil.isBlank(where)){
				where += " course.deleted = true";
			} else {
				where += " and course.deleted = true";
			}
			session = this.getSession();
			session.beginTransaction();
			
			sQuery += " WHERE " + where;
			sQuery += " ORDER BY course.name";
			
			Query query= session.createQuery(sQuery);
			if (semester!=null){
				query.setParameter("semester", semester);
			}
			if (year != null){		 
				query.setParameter("year", year);
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
			throw new MessageException(Constants.EXCEPTION_GET_DELETED_COURSES);
		}		
	}
	/**
	 * Return the courses with semester and year equals to the ones received as parameters and where the user staff is lecturer or tutor
	 * @param semester
	 * @param year
	 * @param organization
	 * @param staff
	 * @return
	 * @throws MessageException
	 */
	public List<Course> loadStaffCourses(Integer semester, Integer year, Organization organization, User staff) throws MessageException{
		Session session = null;
		List<Course> resultList = new ArrayList<Course>();
		try{
			List<Course> courses = loadCourses(semester, year, organization);
			for(Course course : courses){
				if (course != null && (course.getLecturers().contains(staff) || course.getTutors().contains(staff))){
					resultList.add(course.clone());
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_COURSE);
		}
		return resultList;
	}

}
