package au.edu.usyd.reviewer.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

public class AssignmentDaoUnitTest {

	private AssignmentDao assignmentDao;
	private UserDao userDao;
	private User lecturer1;
	private User tutor1;
	private User user1;
	private User user2;
	private User user3;
	private DocEntry docEntry1;
	private DocEntry docEntry2;
	private DocEntry docEntry3;
	private DocEntry groupDocEntry1;
	private DocEntry groupDocEntry2;
	private DocEntry groupDocEntry3;
	private ReviewingActivity reviewingActivity1;
	private WritingActivity activity1;
	private WritingActivity activity2;
	private WritingActivity groupActivity1;
	private Course course1;
	private Course course2;
	private Course course3;
	private Review review1;
	private Review review2;
	private Review review3;
	private Review review4;
	private Review review5;
	private Review review6;
	private ReviewEntry reviewEntry1;

	@After
	public void cleanUp() {
		assignmentDao.delete(course1);
		assignmentDao.delete(course2);
		assignmentDao.delete(course3);
	}

	@Before
	public void setUp() {
		assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
		Organization organization = new Organization();
		organization.setName("ASSIGNMENTDAO_TEST");
		userDao = UserDao.getInstance();
		lecturer1 = new User();
		lecturer1.setEmail("lecturer1@test.com");
		lecturer1.setOrganization(organization);
		assignmentDao.save(lecturer1);

		tutor1 = new User();
		tutor1.setEmail("tutor1@test.com");
		tutor1.setOrganization(organization);
		assignmentDao.save(tutor1);

		user1 = new User();
		user1.setEmail("user1@test.com");
		user1.setOrganization(organization);
		assignmentDao.save(user1);

		user2 = new User();
		user2.setEmail("user2@example.com");
		user2.setOrganization(organization);
		assignmentDao.save(user2);

		user3 = new User();
		user3.setEmail("user3@example.com");
		user3.setOrganization(organization);
		assignmentDao.save(user3);

		UserGroup userGroup1 = new UserGroup();
		userGroup1.setName("userGroup1");
		userGroup1.setTutorial("tutorial1");
		userGroup1.getUsers().add(user1);
		assignmentDao.save(userGroup1);

		UserGroup userGroup2 = new UserGroup();
		userGroup2.setName("userGroup2");
		userGroup1.setTutorial("tutorial2");
		userGroup2.getUsers().add(user2);
		assignmentDao.save(userGroup2);

		UserGroup userGroup3 = new UserGroup();
		userGroup3.setName("userGroup3");
		userGroup1.setTutorial("tutorial3");
		userGroup3.getUsers().add(user3);
		assignmentDao.save(userGroup3);

		docEntry1 = new DocEntry();
		docEntry1.setDocumentId("document:01");
		docEntry1.setTitle("Document 01");
		docEntry1.setOwner(user1);
		assignmentDao.save(docEntry1);

		docEntry2 = new DocEntry();
		docEntry2.setDocumentId("document:02");
		docEntry2.setOwner(user2);
		assignmentDao.save(docEntry2);

		docEntry3 = new DocEntry();
		docEntry3.setDocumentId("document:03");
		docEntry3.setOwner(user3);
		assignmentDao.save(docEntry3);

		groupDocEntry1 = new DocEntry();
		groupDocEntry1.setDocumentId("document:04");
		groupDocEntry1.setOwnerGroup(userGroup1);
		assignmentDao.save(groupDocEntry1);

		groupDocEntry2 = new DocEntry();
		groupDocEntry2.setDocumentId("document:05");
		groupDocEntry2.setOwnerGroup(userGroup2);
		assignmentDao.save(groupDocEntry2);

		groupDocEntry3 = new DocEntry();
		groupDocEntry3.setDocumentId("document:06");
		groupDocEntry3.setOwnerGroup(userGroup3);
		assignmentDao.save(groupDocEntry3);

		review1 = new Review();
		review1.setContent("content");
		assignmentDao.save(review1);
		reviewEntry1 = new ReviewEntry();
		reviewEntry1.setOwner(user1);
		reviewEntry1.setDocEntry(docEntry3);
		reviewEntry1.setReview(review1);
		assignmentDao.save(reviewEntry1);

		review2 = new Review();
		review2.setContent("content");
		assignmentDao.save(review2);
		ReviewEntry reviewEntry2 = new ReviewEntry();
		reviewEntry2.setOwner(user2);
		reviewEntry2.setDocEntry(docEntry1);
		reviewEntry2.setReview(review2);
		assignmentDao.save(reviewEntry2);

		review3 = new Review();
		review3.setContent("content");
		assignmentDao.save(review3);
		ReviewEntry reviewEntry3 = new ReviewEntry();
		reviewEntry3.setOwner(user3);
		reviewEntry3.setDocEntry(docEntry2);
		reviewEntry3.setReview(review3);
		assignmentDao.save(reviewEntry3);

		review4 = new Review();
		review4.setContent(" ");
		assignmentDao.save(review4);
		ReviewEntry reviewEntry4 = new ReviewEntry();
		reviewEntry4.setOwner(tutor1);
		reviewEntry4.setDocEntry(docEntry1);
		reviewEntry4.setReview(review4);
		assignmentDao.save(reviewEntry4);

		review5 = new Review();
		review5.setContent("content");
		assignmentDao.save(review5);
		ReviewEntry reviewEntry5 = new ReviewEntry();
		reviewEntry5.setOwner(tutor1);
		reviewEntry5.setDocEntry(docEntry2);
		reviewEntry5.setReview(review5);
		assignmentDao.save(reviewEntry5);

		review6 = new Review();
		review6.setContent("content");
		assignmentDao.save(review6);
		ReviewEntry reviewEntry6 = new ReviewEntry();
		reviewEntry6.setOwner(tutor1);
		reviewEntry6.setDocEntry(groupDocEntry1);
		reviewEntry6.setReview(review6);
		assignmentDao.save(reviewEntry6);

		Deadline activity1Deadline = new Deadline("Final");
		reviewingActivity1 = new ReviewingActivity();
		reviewingActivity1.setStartDate(activity1Deadline);
		reviewingActivity1.setStatus(Activity.STATUS_START);
		reviewingActivity1.getEntries().add(reviewEntry1);
		reviewingActivity1.getEntries().add(reviewEntry2);
		reviewingActivity1.getEntries().add(reviewEntry3);
		reviewingActivity1.getEntries().add(reviewEntry4);
		reviewingActivity1.getEntries().add(reviewEntry5);

		activity1 = new WritingActivity();
		activity1.setName("activity1");
		activity1.getDeadlines().add(activity1Deadline);
		activity1.getEntries().add(docEntry1);
		activity1.getEntries().add(docEntry2);
		activity1.getEntries().add(docEntry3);
		activity1.getReviewingActivities().add(reviewingActivity1);
		activity1.setStatus(Activity.STATUS_START);
		assignmentDao.save(activity1);

		activity2 = new WritingActivity();
		activity2.setName("activity2");
		assignmentDao.save(activity2);

		Deadline groupDeadline1 = new Deadline("Final");
		ReviewingActivity groupReviewingActivity1 = new ReviewingActivity();
		groupReviewingActivity1.setStartDate(groupDeadline1);
		groupReviewingActivity1.getEntries().add(reviewEntry6);

		groupActivity1 = new WritingActivity();
		groupActivity1.setName("groupActivity1");
		groupActivity1.getDeadlines().add(groupDeadline1);
		groupActivity1.getEntries().add(groupDocEntry1);
		groupActivity1.getEntries().add(groupDocEntry2);
		groupActivity1.getEntries().add(groupDocEntry3);
		groupActivity1.setGroups(true);
		groupActivity1.setName("groupActivity1");
		groupActivity1.getReviewingActivities().add(groupReviewingActivity1);
		assignmentDao.save(groupActivity1);

		course1 = new Course();
		course1.setName("course1");
		course1.setYear(2011);
		course1.setSemester(2);
		course1.getLecturers().add(lecturer1);
		course1.getTutors().add(tutor1);
		course1.getStudentGroups().add(userGroup1);
		course1.getStudentGroups().add(userGroup2);
		course1.getStudentGroups().add(userGroup3);
		course1.getWritingActivities().add(activity1);
		course1.getWritingActivities().add(activity2);
		course1.getWritingActivities().add(groupActivity1);
		course1.setOrganization(organization);
		assignmentDao.save(course1);

		course2 = new Course();
		course2.setOrganization(organization);
		course2.setName("course2");
		course2.setYear(2011);
		course2.setSemester(2);		
		assignmentDao.save(course2);

		course3 = new Course();
		course3.setName("course3");
		course3.setYear(2011);
		course3.setSemester(2);		
		course3.getTutors().add(tutor1);
		course3.setOrganization(organization);
		assignmentDao.save(course3);
	}

	@Test
	public void shouldDeleteActivity() {
		course1.getWritingActivities().remove(activity1);
		assignmentDao.save(course1);
		assignmentDao.delete(activity1);
		assertThat(assignmentDao.loadWritingActivity(activity1.getId()), nullValue());

		course1.getWritingActivities().remove(activity2);
		assignmentDao.save(course1);
		assignmentDao.delete(activity2);
		assertThat(assignmentDao.loadWritingActivity(activity2.getId()), nullValue());

		course1.getWritingActivities().remove(groupActivity1);
		assignmentDao.save(course1);
		assignmentDao.delete(groupActivity1);
		assertThat(assignmentDao.loadWritingActivity(groupActivity1.getId()), nullValue());
	}

	@Test
	public void shouldLoadCourse() {
		CourseDao courseDao = CourseDao.getInstance();
		Course course1;
		int lecturesSize = 0;
		int groupsSize = 0;
		int tutorsSize = 0;
		int activities = 0;
		try {
			course1 = courseDao.loadCourse(this.course1.getId());
			lecturesSize = course1.getLecturers().size();
			
			tutorsSize = course1.getTutors().size();
			groupsSize = course1.getStudentGroups().size();
			activities = course1.getWritingActivities().size();
			
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertThat(lecturesSize, is(1));
		assertThat(tutorsSize, is(1));
		assertThat(groupsSize, is(3));
		assertThat(activities, is(3));

		
		assertThat(course2.getLecturers().size(), is(0));
		assertThat(course2.getTutors().size(), is(0));
		assertThat(course2.getStudentGroups().size(), is(0));
		assertThat(course2.getWritingActivities().size(), is(0));
		Course course2 = null;
		lecturesSize = 0;
		groupsSize = 0;
		tutorsSize = 0;
		activities = 0;
		try {
			course2 = courseDao.loadCourse(this.course2.getId());
			lecturesSize = course2.getLecturers().size();
			
			tutorsSize = course2.getTutors().size();
			groupsSize = course2.getStudentGroups().size();
			activities = course2.getWritingActivities().size();
			
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertThat(lecturesSize, is(0));
		assertThat(tutorsSize, is(1));
		assertThat(groupsSize, is(3));
		assertThat(activities, is(3));
//		
//		Course course3 = courseDao.loadCourse(this.course3.getId());
//		assertThat(course3.getLecturers().size(), is(0));
//		assertThat(course3.getTutors().size(), is(1));
//		assertThat(course3.getStudentGroups().size(), is(0));
//		assertThat(course3.getWritingActivities().size(), is(0));
	}

	@Test
	public void shouldLoadCourseWhereWritingActivity() {
		Course course1 = assignmentDao.loadCourseWhereWritingActivity(activity1);
		assertThat(course1.getId(), equalTo(this.course1.getId()));
		assertThat(course1.getName(), equalTo(this.course1.getName()));
	}
	
	@Test
	public void shouldLoadDocEntry() {
		DocEntry docEntry1 = assignmentDao.loadDocEntry(this.docEntry1.getDocumentId());
		assertThat(docEntry1.getId(), equalTo(this.docEntry1.getId()));
		assertThat(docEntry1.getTitle(), equalTo(this.docEntry1.getTitle()));
	}

	@Test
	public void shouldLoadReviewingActivity() {
		ReviewingActivity reviewingActivity1 = assignmentDao.loadReviewingActivityWhereReview(review1);
		assertThat(reviewingActivity1.getId(), equalTo(this.reviewingActivity1.getId()));
		assertThat(reviewingActivity1.getName(), equalTo(this.reviewingActivity1.getName()));
		assertThat(reviewingActivity1.getStatus(), equalTo(this.reviewingActivity1.getStatus()));
	}

	@Test
	public void shouldLoadUserReviewForEditing() {
		Course course1 = assignmentDao.loadUserReviewForEditing(user1, reviewEntry1.getReview().getId());
		assertThat(course1.getId(), equalTo(this.course1.getId()));
	}
	
	@Test
	public void shouldLoadUserReviewForViewing() {
		// finish reviews
		docEntry3.getReviews().add(review1);
		assignmentDao.save(docEntry3);
		docEntry1.getReviews().add(review2);
		assignmentDao.save(docEntry1);
		docEntry2.getReviews().add(review3);
		assignmentDao.save(docEntry2);
		docEntry1.getReviews().add(review4);
		assignmentDao.save(docEntry1);
		docEntry2.getReviews().add(review5);
		assignmentDao.save(docEntry2);
		groupDocEntry1.getReviews().add(review6);
		assignmentDao.save(groupDocEntry1);

		// student 1
		Course user1Course = assignmentDao.loadReviewForViewing(user1, review4.getId());
		assertThat(user1Course.getStudentGroups().size(), is(1));
		assertThat(user1Course.getWritingActivities().size(), is(1));
		Iterator<WritingActivity> user1Activities = user1Course.getWritingActivities().iterator();
		WritingActivity user1Activity = user1Activities.next();
		assertThat(user1Activity.getEntries().size(), is(1));
		DocEntry user1DocEntry = user1Activity.getEntries().iterator().next();
		assertThat(user1DocEntry.getReviews().size(), is(1));
		Review user1Review = user1DocEntry.getReviews().iterator().next();
		assertThat(review4, equalTo(user1Review));

		// student 2
		Course user2Course = assignmentDao.loadReviewForViewing(user2, review4.getId());
		assertThat(user2Course, nullValue());
	}

	@Test
	public void shouldLoadWritingActivity() {
		WritingActivity activity1 = assignmentDao.loadWritingActivity(this.activity1.getId());
		assertThat(activity1.getId(), equalTo(this.activity1.getId()));
		assertThat(activity1.getName(), equalTo(this.activity1.getName()));
		assertThat(activity1.getStatus(), equalTo(this.activity1.getStatus()));
	}

	@Test
	public void shouldLoadWritingActivityWhereDocEntry(){
		WritingActivity activity1 = assignmentDao.loadWritingActivityWhereDocEntry(docEntry1);
		assertThat(activity1.getId(), equalTo(this.activity1.getId()));
	}

	@Test
	public void testContainsUserQuery() {
		Boolean result = false;
		try {
			result = userDao.containsUser(user1);
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertThat(result, is(true));
		
		try {
			result = userDao.containsUser(user2);
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertThat(result, is(true));
		
		try {
			result = userDao.containsUser(user3);
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertThat(result, is(true));
		
		try {
			result = userDao.containsUser(new User());
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertThat(result, is(false));
	}

	@Test
	public void testUserActivitiesQuery() {
		// students
		List<Course> user1Activities = assignmentDao.loadUserActivities(2,2011,user1);
		assertThat(user1Activities.size(), is(0));
		List<Course> user2Activities = assignmentDao.loadUserActivities(2,2011,user2);
		assertThat(user2Activities.size(), is(0));
		List<Course> user3Activities = assignmentDao.loadUserActivities(2,2011,user3);
		assertThat(user3Activities.size(), is(0));

		// tutor
		List<Course> tutor1Activities = assignmentDao.loadUserActivities(2,2011,tutor1);
		assertThat(tutor1Activities.size(), is(2));

		// lecturer
		List<Course> lecturer1Activities = assignmentDao.loadUserActivities(2,2011,lecturer1);
		assertThat(lecturer1Activities.size(), is(1));
	}

	@Test
	public void testUserReviewingTasksQuery() {
		// student
		List<Course> user1Courses = assignmentDao.loadUserReviewingTasks(2,2011,true,user1);
		assertThat(user1Courses.size(), is(1));
		assertThat(user1Courses.get(0).getStudentGroups().size(), is(1));
		assertThat(user1Courses.get(0).getWritingActivities().size(), is(1));
		Iterator<WritingActivity> user1Activities = user1Courses.get(0).getWritingActivities().iterator();
		ReviewingActivity user1ActivityReview = user1Activities.next().getReviewingActivities().iterator().next();
		assertThat(user1ActivityReview.getEntries().size(), is(1));
		assertThat(user1ActivityReview.getEntries().iterator().next().getReview(), equalTo(review1));

		// tutor
		List<Course> tutor1Courses = assignmentDao.loadUserReviewingTasks(2,2011,true,tutor1);
		assertThat(tutor1Courses.size(), is(1));
		assertThat(tutor1Courses.get(0).getWritingActivities().size(), is(2));
		Set<WritingActivity> tutor1Activities = tutor1Courses.get(0).getWritingActivities();
		for (WritingActivity tutor1Activity : tutor1Activities) {
			for (ReviewingActivity reviewingActivity : tutor1Activity.getReviewingActivities()) {
				assertThat(reviewingActivity.getEntries().size(), not(0));
			}
		}
	}

	@Test
	public void testUserWritingTasksQueryAfterReviewFinish() {
		// finish reviews
		docEntry3.getReviews().add(review1);
		assignmentDao.save(docEntry3);
		docEntry1.getReviews().add(review2);
		assignmentDao.save(docEntry1);
		docEntry2.getReviews().add(review3);
		assignmentDao.save(docEntry2);
		docEntry1.getReviews().add(review4);
		assignmentDao.save(docEntry1);
		docEntry2.getReviews().add(review5);
		assignmentDao.save(docEntry2);
		groupDocEntry1.getReviews().add(review6);
		assignmentDao.save(groupDocEntry1);

		groupActivity1.getReviewingActivities().get(0).setStatus(Activity.STATUS_FINISH);
		assignmentDao.save(groupActivity1);
		activity1.getReviewingActivities().get(0).setStatus(Activity.STATUS_FINISH);
		assignmentDao.save(activity1);

		// student
		List<Course> user1Courses = assignmentDao.loadUserWritingTasks(2,2011,user1);
		assertThat(user1Courses.size(), is(1));
		assertThat(user1Courses.get(0).getStudentGroups().size(), is(1));
		assertThat(user1Courses.get(0).getWritingActivities().size(), is(2));
		Set<WritingActivity> user1Activities = user1Courses.get(0).getWritingActivities();
		assertThat(user1Activities.size(), is(2));

		for (WritingActivity user1WritingActivity : user1Activities) {
			if (user1WritingActivity.equals(activity1)) {
				assertThat(user1WritingActivity, equalTo(activity1));
				assertThat(user1WritingActivity.getEntries().size(), is(1));
				DocEntry docEntry1 = user1WritingActivity.getEntries().iterator().next();
				assertThat(docEntry1, equalTo(this.docEntry1));
				assertThat(docEntry1.getReviews().size(), is(2));
			} else if (user1WritingActivity.equals(groupActivity1)) {
				assertThat(user1WritingActivity, equalTo(groupActivity1));
				assertThat(user1WritingActivity.getEntries().size(), is(1));
				DocEntry groupDocEntry1 = user1WritingActivity.getEntries().iterator().next();
				assertThat(groupDocEntry1, equalTo(this.groupDocEntry1));
				assertThat(groupDocEntry1.getReviews().size(), is(1));
			} else {
				fail();
			}
		}
	}

	@Test
	public void testUserWritingTasksQueryBeforeReviewFinish() {
		// student
		List<Course> user1Courses = assignmentDao.loadUserWritingTasks(2,2011,user1);
		assertThat(user1Courses.size(), is(1));
		assertThat(user1Courses.get(0).getStudentGroups().size(), is(1));
		assertThat(user1Courses.get(0).getWritingActivities().size(), is(2));
		Iterator<WritingActivity> user1Activities = user1Courses.get(0).getWritingActivities().iterator();

		WritingActivity activity1 = user1Activities.next();
		assertThat(activity1, equalTo(activity1));
		assertThat(activity1.getEntries().size(), is(1));
		DocEntry docEntry1 = activity1.getEntries().iterator().next();
		assertThat(docEntry1, equalTo(docEntry1));
		assertThat(docEntry1.getReviews().size(), is(0));

		WritingActivity groupActivity1 = user1Activities.next();
		assertThat(groupActivity1, equalTo(groupActivity1));
		assertThat(groupActivity1.getEntries().size(), is(1));
		DocEntry groupDocEntry1 = groupActivity1.getEntries().iterator().next();
		assertThat(groupDocEntry1, equalTo(groupDocEntry1));
		assertThat(groupDocEntry1.getReviews().size(), is(0));
	}

}
