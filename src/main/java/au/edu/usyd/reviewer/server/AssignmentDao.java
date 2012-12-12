package au.edu.usyd.reviewer.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.DocumentType;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.QuestionReview;
import au.edu.usyd.reviewer.client.core.Rating;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewReply;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.Section;
import au.edu.usyd.reviewer.client.core.TemplateReply;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

public class AssignmentDao {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private SessionFactory sessionFactory;

	public AssignmentDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void delete(Object object) throws MessageException{
		Session session = this.getSession();
		try{
			session.beginTransaction();
			session.delete(object);
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_DELETE);
		}
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}



	public Course loadCourseWhereDeadline(Deadline deadline) throws MessageException{
		Session session = null;
		try{
			String query = "select distinct course from Course course " 
				+ "join fetch course.writingActivities writingActivity " 
				+ "join fetch writingActivity.deadlines deadline " 
				+ "where deadline=:deadline";
			session = this.getSession();
			session.beginTransaction();
			Course course = (Course) session.createQuery(query).setParameter("deadline", deadline).uniqueResult();
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
	
	public Course loadCourseWhereReviewingActivity(ReviewingActivity reviewingActivity) throws MessageException{
		Session session = null;
		try{
			String query = "from Course course " + 
			"join fetch course.writingActivities writingActivity " + 
			"join fetch writingActivity.reviewingActivities reviewingActivity " +
			"where reviewingActivity=:reviewingActivity";
			session = this.getSession();
			session.beginTransaction();
			Course course = (Course) session.createQuery(query).setParameter("reviewingActivity", reviewingActivity).uniqueResult();
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

	public Course loadCourseWhereWritingActivity(WritingActivity writingActivity) throws MessageException{
		Session session = null;
		try{
			String query = "from Course course " + 
			"join fetch course.writingActivities writingActivity " + 
			"where writingActivity=:writingActivity";
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

	public Deadline loadDeadline(Long deadlineId) throws MessageException {
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			Deadline deadline = (Deadline) session.createCriteria(Deadline.class).add(Property.forName("id").eq(deadlineId)).uniqueResult();
			session.getTransaction().commit();
			if (deadline != null){
				deadline = deadline.clone();
			}
			return deadline;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_DEADLINE);
		}
	}

	public DocEntry loadDocEntry(String documentId) throws MessageException{
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			DocEntry docEntry = (DocEntry) session.createCriteria(DocEntry.class).add(Property.forName("documentId").eq(documentId)).uniqueResult();
			session.getTransaction().commit();
			if (docEntry != null){
				docEntry = docEntry.clone();
			}
			return docEntry;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_DOCENTRY);
		}
	}
	
	public DocEntry loadDocEntryWhereId(Long id) throws MessageException{
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			DocEntry docEntry = (DocEntry) session.createCriteria(DocEntry.class).add(Property.forName("id").eq(id)).uniqueResult();
			session.getTransaction().commit();
			if (docEntry != null){
				docEntry = docEntry.clone();
			}
			return docEntry;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_DOCENTRY);
		}
	}	
	
	public ReviewEntry loadReviewEntry(Long reviewEntryId) throws MessageException{
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			ReviewEntry reviewEntry = (ReviewEntry) session.createCriteria(ReviewEntry.class).add(Property.forName("id").eq(reviewEntryId)).uniqueResult();
			session.getTransaction().commit();
			if (reviewEntry != null){
				reviewEntry = reviewEntry.clone();
			}
			return reviewEntry;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEWENTRY);
		}
	}	

	
	public DocEntry loadDocEntryWhereOwnerGroup(WritingActivity writingActivity, UserGroup ownerGroup) throws MessageException {
		Session session = null;
		try{
			String query = "from Activity activity " + 
			"join fetch activity.entries docEntry " + 
			"where activity=:activity AND docEntry.ownerGroup=:ownerGroup";
			session = this.getSession();
			session.beginTransaction();
			writingActivity = (WritingActivity) session.createQuery(query).setParameter("activity", writingActivity).setParameter("ownerGroup", ownerGroup).uniqueResult();
			session.getTransaction().commit();
			if (writingActivity == null) {
				return null;
			} else {
				DocEntry doc = writingActivity.getEntries().iterator().next();
				if (doc != null){
					doc = doc.clone();
				}
				return doc;
			}
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_DOCENTRY);
		}
	}

	public DocEntry loadDocEntryWhereUser(WritingActivity writingActivity, User user)throws MessageException {
		Session session = null;
		try{
			String query = "from Activity activity " + 
			"join fetch activity.entries docEntry " + 
			"where activity=:activity AND docEntry.owner=:user";
			session = this.getSession();
			session.beginTransaction();
			writingActivity = (WritingActivity) session.createQuery(query).setParameter("activity", writingActivity).setParameter("user", user).uniqueResult();
			session.getTransaction().commit();
			if (writingActivity == null) {
				return null;
			} else {
				DocEntry doc = writingActivity.getEntries().iterator().next();
				if (doc != null){
					doc = doc.clone();
				}
				return doc;
			}
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_DOCENTRY);
		}
	}

	public Grade loadGrade(Deadline deadline, User user) throws MessageException{
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			Grade grade = (Grade) session.createCriteria(Grade.class).add(Property.forName("deadline").eq(deadline)).add(Property.forName("user").eq(user)).uniqueResult();
			session.getTransaction().commit();
			if (grade != null){
				grade = grade.clone();
			}
			return grade;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_GRADE);
		}
	}

	public List<Course> loadLecturerCourses(Integer semester, Integer year, User lecturer) throws MessageException{
		Session session = null;
		try{
			String query = "from Course course " + "join fetch course.lecturers lecturer " + "where lecturer=:lecturer and course.semester=:semester AND course.year=:year";
			session = this.getSession();
			session.beginTransaction();
			List<Course> courses = session.createQuery(query).setParameter("semester", semester).setParameter("year", year).setParameter("lecturer", lecturer).list();
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

	public Rating loadRating(Long ratingId) throws MessageException{
		Session session =null;
		try{
			session = this.getSession();
			session.beginTransaction();
			Rating rating = (Rating) session.createCriteria(Rating.class).add(Property.forName("id").eq(ratingId)).uniqueResult();
			session.getTransaction().commit();
			if (rating != null){
				rating = rating.clone();
			}
			return rating;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_RATING);
		}
	}

	public Review loadReview(Long reviewId) throws MessageException {
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			Review review = (Review) session.createCriteria(Review.class).add(Property.forName("id").eq(reviewId)).uniqueResult();
			session.getTransaction().commit();
			if (review != null){
				review = review.clone();
			}
			return review;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEW);
		}
	}

	public ReviewEntry loadReviewEntryWhereDocEntryAndOwner(DocEntry docEntry, User owner) throws MessageException{
		Session session = null;
		try{
			String ownerQuery = "select distinct reviewEntry from ReviewEntry reviewEntry " + 
			"join fetch reviewEntry.docEntry docEntry " + 
			"join fetch reviewEntry.owner owner " + 
			"where owner=:owner AND docEntry=:docEntry";
	
			session = this.getSession();
			session.beginTransaction();
			ReviewEntry reviewEntry = (ReviewEntry) session.createQuery(ownerQuery).setParameter("owner", owner).setParameter("docEntry", docEntry).uniqueResult();
			session.getTransaction().commit();
			if (reviewEntry != null){
				reviewEntry = reviewEntry.clone();
			}
			return reviewEntry;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEWENTRY);
		}
	}

	public ReviewEntry loadReviewEntryWhereReview(Review review) throws MessageException {
		Session session = null;
		try{
			String ownerQuery = "select reviewEntry from ReviewEntry reviewEntry " + 
			"join fetch reviewEntry.review review " + 
			"where review=:review";
	
			session = this.getSession();
			session.beginTransaction();
			ReviewEntry reviewEntry = (ReviewEntry) session.createQuery(ownerQuery).setParameter("review", review).uniqueResult();
			session.getTransaction().commit();
			if (reviewEntry != null){
				reviewEntry = reviewEntry.clone();
			}
			return reviewEntry;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEWENTRY);
		}
	}


	public Course loadReviewForViewing(User user, long reviewId) throws MessageException{
		Session session = null;
		try{
			logger.debug("Loading user review: user.username=" + user.getUsername() + ", review.id=" + reviewId);
			String ownerQuery = "select distinct course from Course course " + 
			"left join fetch course.lecturers lecturer " + 
			"left join fetch course.tutors tutor " + 
			"join fetch course.studentGroups studentGroup " + 
			"join fetch studentGroup.users student " + 
			"join fetch course.writingActivities writingActivity " + 
			"join fetch writingActivity.entries docEntry " + 
			"join fetch docEntry.reviews review " + 
			"where review.id=:reviewId " +
			"AND (supervisor=:user OR tutor=:user OR lecturer=:user OR (student=:user AND (docEntry.owner=:user OR docEntry.ownerGroup=studentGroup)))";
	
			session = this.getSession();
			session.beginTransaction();
			Course course = (Course) session.createQuery(ownerQuery).setParameter("user", user).setParameter("reviewId", reviewId).uniqueResult();
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

	public ReviewingActivity loadReviewingActivity(Long reviewingActivityId) throws MessageException{
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			ReviewingActivity reviewingActivity = (ReviewingActivity) session.createCriteria(ReviewingActivity.class).add(Property.forName("id").eq(reviewingActivityId)).uniqueResult();
			session.getTransaction().commit();
			if (reviewingActivity != null){
				reviewingActivity = reviewingActivity.clone();
			}
			return reviewingActivity;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIWING_ACTIVITY);
		}
	}

	public ReviewingActivity loadReviewingActivityWhereReview(Review review)throws MessageException {
		Session session = null;
		try{
			String query = "from ReviewingActivity reviewingActivity " + 
			"join fetch reviewingActivity.entries reviewEntry " + 
			"where reviewEntry.review=:review";
			session = this.getSession();
			session.beginTransaction();
			ReviewingActivity reviewingActivity = (ReviewingActivity) session.createQuery(query).setParameter("review", review).uniqueResult();
			session.getTransaction().commit();
			if (reviewingActivity != null){
				reviewingActivity = reviewingActivity.clone();
			}
			return reviewingActivity;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIWING_ACTIVITY);
		}
	}

	public List<Course> loadUserActivities(int semester, int year, User user) throws MessageException{
		List<Course> resultList = new ArrayList<Course>();
		Session session = null;
		try{
			logger.debug("Loading user activities: user.username=" + user.getUsername());
			String query = "select distinct course from Course course " 
				+ "left join fetch course.lecturers lecturer " 
				+ "left join fetch course.tutors tutor " 
				+ "where (lecturer=:user OR tutor=:user)"
				+ "AND (course.semester=:semester AND course.year=:year)"; 
	
			session = this.getSession();
			session.beginTransaction();
			List<Course> courses = session.createQuery(query).setParameter("user", user).setParameter("semester", semester).setParameter("year", year).list();
			session.getTransaction().commit();
			
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
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
	}

	public UserGroup loadUserGroupWhereUser(Course course, User user) throws MessageException{
		Session session = null;
		try{
			String query = "from Course course " + "join fetch course.studentGroups studentGroup " + 
						   "join fetch studentGroup.users student " + 
						   "where course=:course AND student=:user";
			session = this.getSession();
			session.beginTransaction();
			course = (Course) session.createQuery(query).setParameter("course", course).setParameter("user", user).uniqueResult();
			session.getTransaction().commit();
			if (course == null) {
				return null;
			} else {
				 UserGroup group = course.getStudentGroups().iterator().next();
				 if (group != null){
					 group = group.clone();
				 }
				 return group;
			}
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_USERGROUP);
		}
	}

	public Rating loadUserRatingForEditing(User owner, Review review)throws MessageException {
		Session session = null;
		try{
			logger.debug("Loading rating: owner.username=" + owner.getUsername() + ", review.id=" + review.getId());
			session = this.getSession();
			session.beginTransaction();
			Rating rating = (Rating) session.createCriteria(Rating.class).add(Property.forName("owner").eq(owner)).add(Property.forName("review").eq(review)).uniqueResult();
			session.getTransaction().commit();
			if (rating != null){
				rating = rating.clone();
			}
			return rating;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_RATING);
		}
	}

	public Course loadUserReviewForEditing(User user, long reviewId) throws MessageException{
		Session session = null;
		try{
			logger.debug("Loading user review: user.username=" + user.getUsername() + ", review.id=" + reviewId);
			String query = "select distinct course from Course course " + 
			"left join fetch course.lecturers lecturer " + 
			"left join fetch course.tutors tutor " + 
			"left join fetch course.supervisors supervisor " + 
			"left join fetch course.automaticReviewers automaticReviewer " +
			"join fetch course.studentGroups studentGroup " + 
			"join fetch studentGroup.users student " + 
			"join fetch course.writingActivities writingActivity " + 
			"join fetch writingActivity.reviewingActivities reviewingActivity " + 
			"join fetch reviewingActivity.entries entry " + 
			"where (student=:user OR supervisor=:user OR tutor=:user OR lecturer=:user OR automaticReviewer=:user) " +
			"AND entry.owner=:user AND entry.review.id=:reviewId";
			session = this.getSession();
			session.beginTransaction();
			Course course = (Course) session.createQuery(query).setParameter("user", user).setParameter("reviewId", reviewId).uniqueResult();
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

	public List<Course> loadUserReviewingTasks(int semester, int year, Boolean includeFinishedReviews, User user) throws MessageException{
		Session session = null;
		List<Course> resultList = new ArrayList<Course>();
		try{
			logger.debug("Loading user reviews: user.username=" + user.getUsername());
			String query = "select distinct course from Course course " 
				+ "left join fetch course.lecturers lecturer " 
				+ "left join fetch course.tutors tutor " 
				+ "left join fetch course.supervisors supervisor " 
				+ "left join fetch course.automaticReviewers automaticReviewer "
				+ "join fetch course.studentGroups studentGroup " 
				+ "join fetch studentGroup.users student " 
				+ "join fetch course.writingActivities writingActivity " 
				+ "join fetch writingActivity.reviewingActivities reviewingAcitvity " 
				+ "join fetch reviewingAcitvity.entries reviewEntry "
				+ "where (student=:user OR supervisor=:user OR tutor=:user OR lecturer=:user OR automaticReviewer=:user) "
				+ "AND (reviewEntry.owner=:user)"
				+ "AND (course.semester=:semester AND course.year=:year)";
			
			if (!includeFinishedReviews){
				query = query + "AND (reviewingAcitvity.status = 1)";	
			}
				
				
			session = this.getSession();
			session.beginTransaction();
			List<Course> courses = session.createQuery(query).setParameter("user", user).setParameter("semester", semester).setParameter("year", year).list();
			session.getTransaction().commit();
			
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
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
	}

	public List<Course> loadUserWritingTasks(int semester, int year, User user) throws MessageException{
		Session session = null;
		List<Course> resultList = new ArrayList<Course>();
		try{
			logger.debug("Loading user reviews: user.username=" + user.getUsername());
			String query = "select distinct course from Course course " 
				+ "left join fetch course.lecturers lecturer " 
				+ "left join fetch course.tutors tutor " 
				+ "left join fetch course.supervisors supervisor " 
				+ "join fetch course.studentGroups studentGroup " 
				+ "join fetch studentGroup.users student " 
				+ "join fetch course.writingActivities writingActivity " 
				+ "join fetch writingActivity.entries docEntry " 
				+ "left join fetch docEntry.ownerGroup ownerGroup "
				+ "left join fetch ownerGroup.users owner "
				+ "where (student=:user OR supervisor=:user OR tutor=:user OR lecturer=:user) "
				+ "AND (docEntry.owner=:user OR owner=:user)"
				+ "AND (course.semester=:semester AND course.year=:year)";
			session = this.getSession();
			session.beginTransaction();
			List<Course> courses = session.createQuery(query).setParameter("user", user).setParameter("semester", semester).setParameter("year", year).list();
			session.getTransaction().commit();
			
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
			throw new MessageException(Constants.EXCEPTION_GET_COURSES);
		}
	}

	public WritingActivity loadWritingActivity(long writingActivityId)throws MessageException {
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			WritingActivity writingActivity = (WritingActivity) session.createCriteria(WritingActivity.class).add(Property.forName("id").eq(writingActivityId)).uniqueResult();
			session.getTransaction().commit();
			if (writingActivity != null){
				writingActivity = writingActivity.clone();
			}
			return writingActivity;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_WRITING_ACTIVITY);
		}
	}
	
	public WritingActivity loadWritingActivityWhereDeadline(Deadline deadline) throws MessageException{
		Session session = null;
		try{
			String query = "select distinct writingActivity from WritingActivity writingActivity " 
				+ "join fetch writingActivity.deadlines deadline " 
				+ "where deadline=:deadline ";
			session = this.getSession();
			session.beginTransaction();
			WritingActivity writingActivity = (WritingActivity) session.createQuery(query).setParameter("deadline", deadline).uniqueResult();
			session.getTransaction().commit();
			if (writingActivity != null){
				writingActivity = writingActivity.clone();
			}
			return writingActivity;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_WRITING_ACTIVITY);
		}

	}
	
	public WritingActivity loadWritingActivityWhereDocEntry(DocEntry docEntry) throws MessageException{
		Session session = null;
		try{
			String query = "from WritingActivity writingActivity " + 
			"join fetch writingActivity.entries docEntry " + 
			"where docEntry=:docEntry";
			session = this.getSession();
			session.beginTransaction();
			WritingActivity writingActivity = (WritingActivity) session.createQuery(query).setParameter("docEntry", docEntry).uniqueResult();
			session.getTransaction().commit();
			if (writingActivity != null){
				writingActivity = writingActivity.clone();
			}
			return writingActivity;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_WRITING_ACTIVITY);
		}

	}

	public void save(Object object) throws MessageException{
		Session session = this.getSession();
		try{
			session.beginTransaction();
			session.saveOrUpdate(object);
			session.getTransaction().commit();
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_SAVE);
		}
	}

	public List<ReviewTemplate> loadReviewTemplates(Organization organization) throws MessageException{
		List<ReviewTemplate> result = new ArrayList<ReviewTemplate>();
		Session session = null;
		try{
			if ( organization != null){
				String query = "from ReviewTemplate review " + "where review.organization=:organization";
		        session = this.getSession();
		        session.beginTransaction();
		        List<ReviewTemplate> reviewTemplates = session.createQuery(query).setParameter("organization", organization).list();
		        session.getTransaction().commit();
		        for (ReviewTemplate template:reviewTemplates){
		        	if (template != null){
		        		result.add(template.clone());
		        	}
		        }
			}
	 		return result;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEW_TEMPLATES);
		}

	}
	
	public ReviewTemplate loadReviewTemplate(Long reviewTemplateId) throws MessageException{
		Session session = null;
		ReviewTemplate reviewTemplate = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			reviewTemplate = (ReviewTemplate) session.createCriteria(ReviewTemplate.class).add(Property.forName("id").eq(reviewTemplateId)).uniqueResult();
			session.getTransaction().commit();
			if (reviewTemplate != null){
				reviewTemplate = reviewTemplate.clone();
			}
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEW_TEMPLATES);
		}
		return reviewTemplate;
	}

	public boolean isReviewTemplateInUse(ReviewTemplate reviewTemplate) {
		Session session = null;
		boolean result = false;
		try{
			String query = "from TemplateReply templateReply " + 
			"where reviewTemplate=:template";
			session = this.getSession();
			session.beginTransaction();		 
			List<TemplateReply> templateReplies = session.createQuery(query).setParameter("template", reviewTemplate).list();
			session.getTransaction().commit();
			return templateReplies.size() > 0;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			return false;
		}
	}

	public Collection<DocumentType> loadDocumentTypes(String genre) throws MessageException {
		Session session = null;
		try{
			String query = "from DocumentType documentType " + 
			"where genre=:genre";
			session = this.getSession();
			session.beginTransaction();
			List<DocumentType> documentTypes = session.createQuery(query).setParameter("genre",genre).list();
			session.getTransaction().commit();
			
			List<DocumentType> types = new ArrayList<DocumentType>();
			for(DocumentType docType : documentTypes){
				if (docType != null){
					types.add(docType.clone());
				}
			}
			return types;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_DOCUMENT_TYPES);
		} 
	}
	
	public QuestionReview loadQuestionReview(Review review) throws MessageException{
		QuestionReview questionReview = new QuestionReview();
		Session session = null;
		try{
			String query = "select distinct questionReview from QuestionReview questionReview " +  
			"where id=:id";

			session = this.getSession();
			session.beginTransaction();
			questionReview =  (QuestionReview) session.createQuery(query).setParameter("id", review.getId()).uniqueResult();
			session.getTransaction().commit();
			if (questionReview != null){
				questionReview = questionReview.clone();
			}
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_REVIEW);
		}
		return questionReview;
	}

	
	public ReviewReply loadReviewReply(Review review) throws MessageException{
		ReviewReply reviewReply = new ReviewReply();
		Session session = null;
		try{
			String query = "select distinct reviewReply from ReviewReply reviewReply " +  
			"where id=:id";

			session = this.getSession();
			session.beginTransaction();
			reviewReply =  (ReviewReply) session.createQuery(query).setParameter("id", review.getId()).uniqueResult();
			session.getTransaction().commit();
			if (reviewReply != null){
				reviewReply = reviewReply.clone();
			}
		} catch(HibernateException he){
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			he.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GET_REVIEW);
		}
		return reviewReply;
	}
	
	public Grade loadGrade(Long id) throws MessageException{
		Session session = null;
		try{
			session = this.getSession();
			session.beginTransaction();
			Grade grade = (Grade) session.createCriteria(Grade.class).add(Property.forName("id").eq(id)).uniqueResult();
			session.getTransaction().commit();
			if (grade != null){
				grade = grade.clone();
			}
			return grade;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_GRADE);
		}
	}
	
	public UserGroup loadUserGroup(Long id) throws MessageException {
		Session session = null;
		try{
			String query = "from UserGroup " +  
						   "where id=:id";
			session = this.getSession();
			session.beginTransaction();
			UserGroup group = (UserGroup) session.createQuery(query).setParameter("id", id).uniqueResult();
			session.getTransaction().commit();
			if (group != null){
			 group = group.clone();
			}
			return group;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_USERGROUP);
		}
	}
	
	public Review loadReview(long reviewId) throws MessageException{
		Session session = null;
		try{
			String ownerQuery = "from Review review " + 
								"where review.id=:reviewId ";
	
			session = this.getSession();
			session.beginTransaction();
			Review review = (Review) session.createQuery(ownerQuery).setParameter("reviewId", reviewId).uniqueResult();
			session.getTransaction().commit();
			if (review != null){
				review = review.clone();
			}
			return review;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIWING_ACTIVITY);
		}
	}

	public List<ReviewTemplate> loadReviewTemplates(Organization organization, Integer page, Integer limit) throws MessageException{
		List<ReviewTemplate> result = new ArrayList<ReviewTemplate>();
		Session session = null;
		try{
			if ( organization != null){
				String sQuery = "from ReviewTemplate review " + "where review.organization=:organization";
		        session = this.getSession();
		        session.beginTransaction();
		        Query query = session.createQuery(sQuery);
		        if (organization != null){
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
		        List<ReviewTemplate> reviewTemplates = query.list();
		        session.getTransaction().commit();
		        for (ReviewTemplate template:reviewTemplates){
		        	if (template != null){
		        		result.add(template.clone());
		        	}
		        }
			}
	 		return result;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_REVIEW_TEMPLATES);
		}

	}
	
	public Section loadSection(Long id) throws MessageException {
		Session session = null;
		try{
			String ownerQuery = "from Section section " + 
								"where section.id=:id ";
	
			session = this.getSession();
			session.beginTransaction();
			Section section = (Section) session.createQuery(ownerQuery).setParameter("id", id).uniqueResult();
			session.getTransaction().commit();
			if (section != null){
				section = section.clone();
			}
			return section;
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_GET_SECTION);
		}
	}
}
