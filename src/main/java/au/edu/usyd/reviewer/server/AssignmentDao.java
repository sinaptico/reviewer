package au.edu.usyd.reviewer.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import au.edu.usyd.reviewer.client.core.Rating;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.TemplateReply;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;

public class AssignmentDao {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private SessionFactory sessionFactory;

	public AssignmentDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void delete(Object object) {
		Session session = this.getSession();
		session.beginTransaction();
		session.delete(object);
		session.getTransaction().commit();
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}



	public Course loadCourseWhereDeadline(Deadline deadline) {
		String query = "select distinct course from Course course " 
			+ "join fetch course.writingActivities writingActivity " 
			+ "join fetch writingActivity.deadlines deadline " 
			+ "where deadline=:deadline";
		Session session = this.getSession();
		session.beginTransaction();
		Course course = (Course) session.createQuery(query).setParameter("deadline", deadline).uniqueResult();
		session.getTransaction().commit();
		if (course != null){
			course = course.clone();
		}
		return course;
	}
	
	public Course loadCourseWhereReviewingActivity(ReviewingActivity reviewingActivity) {
		String query = "from Course course " + 
		"join fetch course.writingActivities writingActivity " + 
		"join fetch writingActivity.reviewingActivities reviewingActivity " +
		"where reviewingActivity=:reviewingActivity";
		Session session = this.getSession();
		session.beginTransaction();
		Course course = (Course) session.createQuery(query).setParameter("reviewingActivity", reviewingActivity).uniqueResult();
		session.getTransaction().commit();
		if (course != null){
			course = course.clone();
		}
		return course;
	}

	public Course loadCourseWhereWritingActivity(WritingActivity writingActivity) {
		String query = "from Course course " + 
		"join fetch course.writingActivities writingActivity " + 
		"where writingActivity=:writingActivity";
		Session session = this.getSession();
		session.beginTransaction();
		Course course = (Course) session.createQuery(query).setParameter("writingActivity", writingActivity).uniqueResult();
		session.getTransaction().commit();
		if (course != null){
			course = course.clone();
		}
		return course;
	}

	public Deadline loadDeadline(Long deadlineId) {
		Session session = this.getSession();
		session.beginTransaction();
		Deadline deadline = (Deadline) session.createCriteria(Deadline.class).add(Property.forName("id").eq(deadlineId)).uniqueResult();
		session.getTransaction().commit();
		if (deadline != null){
			deadline = deadline.clone();
		}
		return deadline;
	}

	public DocEntry loadDocEntry(String documentId) {
		Session session = this.getSession();
		session.beginTransaction();
		DocEntry docEntry = (DocEntry) session.createCriteria(DocEntry.class).add(Property.forName("documentId").eq(documentId)).uniqueResult();
		session.getTransaction().commit();
		if (docEntry != null){
			docEntry = docEntry.clone();
		}
		return docEntry;
	}
	
	public DocEntry loadDocEntryWhereId(Long id) {
		Session session = this.getSession();
		session.beginTransaction();
		DocEntry docEntry = (DocEntry) session.createCriteria(DocEntry.class).add(Property.forName("id").eq(id)).uniqueResult();
		session.getTransaction().commit();
		if (docEntry != null){
			docEntry = docEntry.clone();
		}
		return docEntry;
	}	
	
	public ReviewEntry loadReviewEntry(Long reviewEntryId) {
		Session session = this.getSession();
		session.beginTransaction();
		ReviewEntry reviewEntry = (ReviewEntry) session.createCriteria(ReviewEntry.class).add(Property.forName("id").eq(reviewEntryId)).uniqueResult();
		session.getTransaction().commit();
		if (reviewEntry != null){
			reviewEntry = reviewEntry.clone();
		}
		return reviewEntry;
	}	

	
	public DocEntry loadDocEntryWhereOwnerGroup(WritingActivity writingActivity, UserGroup ownerGroup) {
		String query = "from Activity activity " + 
		"join fetch activity.entries docEntry " + 
		"where activity=:activity AND docEntry.ownerGroup=:ownerGroup";
		Session session = this.getSession();
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
	}

	public DocEntry loadDocEntryWhereUser(WritingActivity writingActivity, User user) {
		String query = "from Activity activity " + 
		"join fetch activity.entries docEntry " + 
		"where activity=:activity AND docEntry.owner=:user";
		Session session = this.getSession();
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
	}

	public Grade loadGrade(Deadline deadline, User user) {
		Session session = this.getSession();
		session.beginTransaction();
		Grade grade = (Grade) session.createCriteria(Grade.class).add(Property.forName("deadline").eq(deadline)).add(Property.forName("user").eq(user)).uniqueResult();
		session.getTransaction().commit();
		if (grade != null){
			grade = grade.clone();
		}
		return grade;
	}

	public List<Course> loadLecturerCourses(Integer semester, Integer year, User lecturer) {
		String query = "from Course course " + "join fetch course.lecturers lecturer " + "where lecturer=:lecturer and course.semester=:semester AND course.year=:year";
		Session session = this.getSession();
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
	}

	public Rating loadRating(Long ratingId) {
		Session session = this.getSession();
		session.beginTransaction();
		Rating rating = (Rating) session.createCriteria(Rating.class).add(Property.forName("id").eq(ratingId)).uniqueResult();
		session.getTransaction().commit();
		if (rating != null){
			rating = rating.clone();
		}
		return rating;
	}

	public Review loadReview(Long reviewId) {
		Session session = this.getSession();
		session.beginTransaction();
		Review review = (Review) session.createCriteria(Review.class).add(Property.forName("id").eq(reviewId)).uniqueResult();
		session.getTransaction().commit();
		if (review != null){
			review = review.clone();
		}
		return review;
	}

	public ReviewEntry loadReviewEntryWhereDocEntryAndOwner(DocEntry docEntry, User owner) {
		String ownerQuery = "select distinct reviewEntry from ReviewEntry reviewEntry " + 
		"join fetch reviewEntry.docEntry docEntry " + 
		"join fetch reviewEntry.owner owner " + 
		"where owner=:owner AND docEntry=:docEntry";

		Session session = this.getSession();
		session.beginTransaction();
		ReviewEntry reviewEntry = (ReviewEntry) session.createQuery(ownerQuery).setParameter("owner", owner).setParameter("docEntry", docEntry).uniqueResult();
		session.getTransaction().commit();
		if (reviewEntry != null){
			reviewEntry = reviewEntry.clone();
		}
		return reviewEntry;
	}

	public ReviewEntry loadReviewEntryWhereReview(Review review) {
		String ownerQuery = "select reviewEntry from ReviewEntry reviewEntry " + 
		"join fetch reviewEntry.review review " + 
		"where review=:review";

		Session session = this.getSession();
		session.beginTransaction();
		ReviewEntry reviewEntry = (ReviewEntry) session.createQuery(ownerQuery).setParameter("review", review).uniqueResult();
		session.getTransaction().commit();
		if (reviewEntry != null){
			reviewEntry = reviewEntry.clone();
		}
		return reviewEntry;
	}


	public Course loadReviewForViewing(User user, long reviewId) {
		logger.debug("Loading user review: user.username=" + user.getUsername() + ", review.id=" + reviewId);
		String ownerQuery = "select distinct course from Course course " + 
		"left join fetch course.lecturers lecturer " + 
		"left join fetch course.tutors tutor " + 
		"left join fetch course.supervisors supervisor " + 
		"join fetch course.studentGroups studentGroup " + 
		"join fetch studentGroup.users student " + 
		"join fetch course.writingActivities writingActivity " + 
		"join fetch writingActivity.entries docEntry " + 
		"join fetch docEntry.reviews review " + 
		"where review.id=:reviewId " +
		"AND (supervisor=:user OR tutor=:user OR lecturer=:user OR (student=:user AND (docEntry.owner=:user OR docEntry.ownerGroup=studentGroup)))";

		Session session = this.getSession();
		session.beginTransaction();
		Course course = (Course) session.createQuery(ownerQuery).setParameter("user", user).setParameter("reviewId", reviewId).uniqueResult();
		session.getTransaction().commit();
		if (course != null){
			course = course.clone();
		}
		return course;
	}

	public ReviewingActivity loadReviewingActivity(Long reviewingActivityId) {
		Session session = this.getSession();
		session.beginTransaction();
		ReviewingActivity reviewingActivity = (ReviewingActivity) session.createCriteria(ReviewingActivity.class).add(Property.forName("id").eq(reviewingActivityId)).uniqueResult();
		session.getTransaction().commit();
		if (reviewingActivity != null){
			reviewingActivity = reviewingActivity.clone();
		}
		return reviewingActivity;
	}

	public ReviewingActivity loadReviewingActivityWhereReview(Review review) {
		String query = "from ReviewingActivity reviewingActivity " + 
		"join fetch reviewingActivity.entries reviewEntry " + 
		"where reviewEntry.review=:review";
		Session session = this.getSession();
		session.beginTransaction();
		ReviewingActivity reviewingActivity = (ReviewingActivity) session.createQuery(query).setParameter("review", review).uniqueResult();
		session.getTransaction().commit();
		if (reviewingActivity != null){
			reviewingActivity = reviewingActivity.clone();
		}
		return reviewingActivity;
	}

	public List<Course> loadUserActivities(int semester, int year, User user) {
		logger.debug("Loading user activities: user.username=" + user.getUsername());
		String query = "select distinct course from Course course " 
			+ "left join fetch course.lecturers lecturer " 
			+ "left join fetch course.tutors tutor " 
			+ "where (lecturer=:user OR tutor=:user)"
			+ "AND (course.semester=:semester AND course.year=:year)"; 

		Session session = this.getSession();
		session.beginTransaction();
		List<Course> courses = session.createQuery(query).setParameter("user", user).setParameter("semester", semester).setParameter("year", year).list();
		session.getTransaction().commit();
		List<Course> resultList = new ArrayList<Course>();
		for(Course course : courses){
			if (course != null){
				resultList.add(course.clone());
			}
		}
		return resultList;
	}

	public UserGroup loadUserGroupWhereUser(Course course, User user) {
		String query = "from Course course " + "join fetch course.studentGroups studentGroup " + 
					   "join fetch studentGroup.users student " + 
					   "where course=:course AND student=:user";
		Session session = this.getSession();
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
	}

	public Rating loadUserRatingForEditing(User owner, Review review) {
		logger.debug("Loading rating: owner.username=" + owner.getUsername() + ", review.id=" + review.getId());
		Session session = this.getSession();
		session.beginTransaction();
		Rating rating = (Rating) session.createCriteria(Rating.class).add(Property.forName("owner").eq(owner)).add(Property.forName("review").eq(review)).uniqueResult();
		session.getTransaction().commit();
		if (rating != null){
			rating = rating.clone();
		}
		return rating;
	}

	public Course loadUserReviewForEditing(User user, long reviewId) {
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
		Session session = this.getSession();
		session.beginTransaction();
		Course course = (Course) session.createQuery(query).setParameter("user", user).setParameter("reviewId", reviewId).uniqueResult();
		session.getTransaction().commit();
		if (course != null){
			course = course.clone();
		}
		return course;
	}

	public List<Course> loadUserReviewingTasks(int semester, int year, Boolean includeFinishedReviews, User user) {
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
			
			
		Session session = this.getSession();
		session.beginTransaction();
		List<Course> courses = session.createQuery(query).setParameter("user", user).setParameter("semester", semester).setParameter("year", year).list();
		session.getTransaction().commit();
		List<Course> resultList = new ArrayList<Course>();
		for(Course course : courses){
			if (course != null){
				resultList.add(course.clone());
			}
		}
		return resultList;
	}

	public List<Course> loadUserWritingTasks(int semester, int year, User user) {
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
		Session session = this.getSession();
		session.beginTransaction();
		List<Course> courses = session.createQuery(query).setParameter("user", user).setParameter("semester", semester).setParameter("year", year).list();
		session.getTransaction().commit();
		List<Course> resultList = new ArrayList<Course>();
		for(Course course : courses){
			if (course != null){
				resultList.add(course.clone());
			}
		}
		return resultList;
	}

	public WritingActivity loadWritingActivity(long writingActivityId) {
		Session session = this.getSession();
		session.beginTransaction();
		WritingActivity writingActivity = (WritingActivity) session.createCriteria(WritingActivity.class).add(Property.forName("id").eq(writingActivityId)).uniqueResult();
		session.getTransaction().commit();
		if (writingActivity != null){
			writingActivity = writingActivity.clone();
		}
		return writingActivity;
	}
	
	public WritingActivity loadWritingActivityWhereDeadline(Deadline deadline) {
		String query = "select distinct writingActivity from WritingActivity writingActivity " 
			+ "join fetch writingActivity.deadlines deadline " 
			+ "where deadline=:deadline ";
		Session session = this.getSession();
		session.beginTransaction();
		WritingActivity writingActivity = (WritingActivity) session.createQuery(query).setParameter("deadline", deadline).uniqueResult();
		session.getTransaction().commit();
		if (writingActivity != null){
			writingActivity = writingActivity.clone();
		}
		return writingActivity;
	}
	
	public WritingActivity loadWritingActivityWhereDocEntry(DocEntry docEntry) {
		String query = "from WritingActivity writingActivity " + 
		"join fetch writingActivity.entries docEntry " + 
		"where docEntry=:docEntry";
		Session session = this.getSession();
		session.beginTransaction();
		WritingActivity writingActivity = (WritingActivity) session.createQuery(query).setParameter("docEntry", docEntry).uniqueResult();
		session.getTransaction().commit();
		if (writingActivity != null){
			writingActivity = writingActivity.clone();
		}
		return writingActivity;
	}

	public void save(Object object) {
		Session session = this.getSession();
		session.beginTransaction();
		session.saveOrUpdate(object);
		session.getTransaction().commit();
	}

	public Collection<ReviewTemplate> loadReviewTemplates(Organization organization) {
		List<ReviewTemplate> result = new ArrayList<ReviewTemplate>();
		if ( organization != null){
			String query = "from ReviewTemplate review " + "where review.organization=:organization";
	        Session session = this.getSession();
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
	}
	
	public ReviewTemplate loadReviewTemplate(Long reviewTemplateId) {
		Session session = this.getSession();
		session.beginTransaction();
		ReviewTemplate reviewTemplate = (ReviewTemplate) session.createCriteria(ReviewTemplate.class).add(Property.forName("id").eq(reviewTemplateId)).uniqueResult();
		session.getTransaction().commit();
		if (reviewTemplate != null){
			reviewTemplate = reviewTemplate.clone();
		}
		return reviewTemplate;
	}

	public boolean isReviewTemplateInUse(ReviewTemplate reviewTemplate) {
		String query = "from TemplateReply templateReply " + 
		"where reviewTemplate=:template";
		Session session = this.getSession();
		session.beginTransaction();		 
		List<TemplateReply> templateReplies = session.createQuery(query).setParameter("template", reviewTemplate).list();
		session.getTransaction().commit();
		return templateReplies.size() > 0;
	}

	public Collection<DocumentType> loadDocumentTypes(String genre) {
		String query = "from DocumentType documentType " + 
		"where genre=:genre";
		Session session = this.getSession();
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
	}
	
}
