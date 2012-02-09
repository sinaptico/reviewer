package au.edu.usyd.reviewer.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.LogbookDocEntry;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.server.rpc.AssignmentServiceImpl;
import au.edu.usyd.reviewer.server.rpc.ReviewServiceImpl;

public class AssignmentManagerUnitTest {

    private static User student1;
    private static User student2;
    private static User tutor1;
    private static Course course;
    private static WritingActivity writingActivity;
    private static WritingActivity groupActivity;
    private static WritingActivity logbookActivity;
    private static AssignmentManager assignmentManager;
    private String domain = Reviewer.getGoogleDomain();

    @BeforeClass
    public static void setUp() {
    	assignmentManager = Reviewer.getAssignmentManager();
    	
    }

    @Test
    public void shouldCreateCourse() throws Exception {
        // create course
        User lecturer1 = new User();
        lecturer1.setId("test.lecturer01");
        lecturer1.setFirstname("Test");
        lecturer1.setLastname("Lecturer01");
        lecturer1.setEmail("test.lecturer01@"+domain);
        
        tutor1 = new User();
        tutor1.setId("test.tutor1");
        tutor1.setFirstname("Test");
        tutor1.setLastname("Tutor01");
        tutor1.setEmail("test.tutor1@"+domain);
        
        course = new Course();
        course.setName("course1");
        course.setYear(2009);
        course.setSemester(1);
        course.getLecturers().add(lecturer1);
        course.getTutors().add(tutor1);
        course.getTutorials().add("monday");
        assignmentManager.saveCourse(course);

        // create student users
        student1 = new User();
        student1.setId("test.student01");
        student1.setFirstname("Test");
        student1.setLastname("Student01");
        student1.setEmail("test.student01@"+domain);

        student2 = new User();
        student2.setId("test.student02");
        student2.setFirstname("Test");
        student2.setLastname("Student02");
        student2.setEmail("test.student02@"+domain);

        User student3 = new User();
        student3.setId("test.student03");
        student3.setFirstname("Test");
        student3.setLastname("Student03");
        student3.setEmail("test.student03@"+domain);

        User student4 = new User();
        student4.setId("test.student04");
        student4.setFirstname("Test");
        student4.setLastname("Student04");
        student4.setEmail("test.student04@"+domain);

        UserGroup userGroup1 = new UserGroup();
        userGroup1.setName("1 + \\ / : * ? \" < > |");
        userGroup1.setTutorial("monday");
        userGroup1.getUsers().add(student1);
        userGroup1.getUsers().add(student2);

        UserGroup userGroup2 = new UserGroup();
        userGroup2.setName("2");
        userGroup2.setTutorial("monday");
        userGroup2.getUsers().add(student3);
        userGroup2.getUsers().add(student4);

        List<UserGroup> userGroups = new ArrayList<UserGroup>();
        userGroups.add(userGroup1);
        userGroups.add(userGroup2);

        assignmentManager.getAssignmentRepository().addStudentsToSpreadsheet(course, userGroups);
        assignmentManager.saveCourse(course);
        assertThat(course.getId(), notNullValue());
        assertThat(course.getStudentGroups().size(), is(2));
    }

    @Test
    public void shouldCreateActivity() throws Exception {    	
    	Deadline draftDeadline = new Deadline("Draft");
    	draftDeadline.setFinishDate(null);
        ReviewingActivity reviewingActivity = new ReviewingActivity();
        reviewingActivity.setNumStudentReviewers(1);
        reviewingActivity.setNumLecturerReviewers(1);
        reviewingActivity.setNumTutorReviewers(1);
        reviewingActivity.setAllocationStrategy(ReviewingActivity.REVIEW_STRATEGY_RANDOM);
        reviewingActivity.setFormType(ReviewingActivity.REVIEW_TYPE_COMMENTS);
        reviewingActivity.setStartDate(draftDeadline);
    
        writingActivity = new WritingActivity();
        writingActivity.setName("writingActivity");
        writingActivity.setDocumentType(WritingActivity.DOCUMENT_TYPE_PRESENTATION);
        writingActivity.setGroups(false);
        writingActivity.setTutorial(WritingActivity.TUTORIAL_ALL);
        writingActivity.getDeadlines().add(reviewingActivity.getStartDate());
        writingActivity.getDeadlines().add(new Deadline("Final"));
        writingActivity.getReviewingActivities().add(reviewingActivity);
        writingActivity.setEmailStudents(false);
        assignmentManager.saveActivity(course, writingActivity);
        assertThat(writingActivity.getId(), notNullValue());
        assertThat(reviewingActivity.getStartDate(), equalTo(draftDeadline));
        
    	Deadline groupActivityDeadline = new Deadline("Final");
    	groupActivityDeadline.setFinishDate(null);
        ReviewingActivity groupActivityReview = new ReviewingActivity();
        groupActivityReview.setNumStudentReviewers(1);
        groupActivityReview.setAllocationStrategy(ReviewingActivity.REVIEW_STRATEGY_RANDOM);
        groupActivityReview.setFormType(ReviewingActivity.REVIEW_TYPE_QUESTION);
        groupActivityReview.setStartDate(groupActivityDeadline);
        
        groupActivity = new WritingActivity();
        groupActivity.setName("groupActivity");
        groupActivity.setDocumentType(WritingActivity.DOCUMENT_TYPE_DOCUMENT);
        groupActivity.setGroups(true);
        groupActivity.getDeadlines().add(groupActivityDeadline);
        groupActivity.getReviewingActivities().add(groupActivityReview);
        groupActivity.setTutorial("monday");

        assignmentManager.saveActivity(course, groupActivity);
        assertThat(groupActivity.getId(), notNullValue());
        assertThat(groupActivityReview.getStartDate(), equalTo(groupActivity.getDeadlines().get(0)));
        
        logbookActivity = new WritingActivity();
        logbookActivity.setName("logbookActivity");
        logbookActivity.setDocumentType(WritingActivity.DOCUMENT_TYPE_LOGBOOK);
        logbookActivity.getDeadlines().add(new Deadline("Final"));
        logbookActivity.setGroups(false);
        assignmentManager.saveActivity(course, logbookActivity);
        assertThat(logbookActivity.getId(), notNullValue());
    }

    @Test
    public void shouldStartActivity() {
        assignmentManager.startActivity(course, writingActivity);
        assertThat(writingActivity.getStatus(), is(Activity.STATUS_START));
        assertThat(writingActivity.getEntries().size(), is(4));

        assignmentManager.startActivity(course, groupActivity);
        assertThat(groupActivity.getStatus(), is(Activity.STATUS_START));
        assertThat(groupActivity.getEntries().size(), is(2));
        
        assignmentManager.startActivity(course, logbookActivity);
        assertThat(logbookActivity.getStatus(), is(Activity.STATUS_START));
        assertThat(logbookActivity.getEntries().size(), is(4));
        for(DocEntry logbookDocEntry : logbookActivity.getEntries()) {
        	assertThat(((LogbookDocEntry)logbookDocEntry).getPages().size(), is(1));
        }
    }

    @Test
    public void testRemoveUserAfterActivityStart() throws Exception {
        // remove student from course spreadsheet
        List<User> students = new ArrayList<User>();
        students.add(student1);
        assignmentManager.getAssignmentRepository().removeStudentsFromSpreadsheet(course, students);
        assignmentManager.saveCourse(course);

        // check that documents have been removed for student
        assertThat(writingActivity.getEntries().size(), is(3));
        assertThat(groupActivity.getEntries().size(), is(2));
        assertThat(logbookActivity.getEntries().size(), is(3));
    }

    @Test
    public void testFinishActivityDraftDeadline() {
        assignmentManager.finishActivityDeadline(course, writingActivity, writingActivity.getDeadlines().get(0));
        assertThat(writingActivity.getDeadlines().get(0).getStatus(), is(Deadline.STATUS_DEADLINE_FINISH));
        assertThat(writingActivity.getEntries().size(), is(3));
        assertThat(writingActivity.getReviewingActivities().get(0).getStatus(), is(Activity.STATUS_START)); 
    }

    @Test
    public void shouldSaveReviewBeforeReviewFinish() throws Exception {
    	AssignmentServiceImpl assignmentServiceImpl = spy(new AssignmentServiceImpl()); 
    	ReviewServiceImpl reviewServiceImpl = spy(new ReviewServiceImpl()); 
    	
    	// student review
    	doReturn(student2).when(assignmentServiceImpl).getUser();
    	doReturn(student2).when(reviewServiceImpl).getUser();
        Collection<Course> student2Reviews = assignmentServiceImpl.getUserReviewingTasks(1,2009);
        Review student2Review = student2Reviews.iterator().next().getWritingActivities().iterator().next().getReviewingActivities().iterator().next().getEntries().iterator().next().getReview();
        student2Review = reviewServiceImpl.saveReview(student2Review);      
           
        // tutor review
        doReturn(tutor1).when(assignmentServiceImpl).getUser();
        doReturn(tutor1).when(reviewServiceImpl).getUser();
        Collection<Course> tutor1Reviews = assignmentServiceImpl.getUserReviewingTasks(1,2009);
        Review tutor1Review = tutor1Reviews.iterator().next().getWritingActivities().iterator().next().getReviewingActivities().iterator().next().getEntries().iterator().next().getReview();
        tutor1Review = reviewServiceImpl.saveReview(tutor1Review);       
    }

    @Test
    public void testAddUserAfterReviewStart() throws Exception {
    	// add new student to course spreadsheet
        User student5 = new User();
        student5.setId("test.student05");
        student5.setFirstname("Test");
        student5.setLastname("Student05");
        student5.setEmail("test.student05@"+domain);
        UserGroup userGroup3 = new UserGroup();
        userGroup3.setName("3");
        userGroup3.setTutorial("monday");
        userGroup3.getUsers().add(student5);
        List<UserGroup> userGroups = new ArrayList<UserGroup>();
        userGroups.add(userGroup3);
        assignmentManager.getAssignmentRepository().addStudentsToSpreadsheet(course, userGroups);
        assignmentManager.saveCourse(course);

        // check that review has been created for new student
        int totalReviews = 0;
        for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
        	assertThat(reviewingActivity.getEntries().size(), not(0));
        	totalReviews += reviewingActivity.getEntries().size();
        }
        assertThat(totalReviews, is(10));
        
        // check that documents have been created for new student
        assertThat(writingActivity.getEntries().size(), is(4));
        assertThat(groupActivity.getEntries().size(), is(3));
        assertThat(logbookActivity.getEntries().size(), is(4));
    }

    @Test
    public void testFinishReviewingActivity() {    	
        assignmentManager.finishReviewingActivity(course, writingActivity.getReviewingActivities().get(0),writingActivity.getDeadlines().get(0));
        assertThat(writingActivity.getReviewingActivities().get(0).getStatus(), is(Activity.STATUS_FINISH));

        // check that all reviews have been released
        int totalReviews = 0;
        int documentsWithoutReviews = 0;
        for (DocEntry docEntry : writingActivity.getEntries()) {
        	totalReviews += docEntry.getReviews().size();
        	if(docEntry.getReviews().size() == 0) {
        		documentsWithoutReviews ++;
        	}
        }
        assertThat(totalReviews, is(10));
        assertThat(documentsWithoutReviews, is(1));
    }

    @Test
    public void shouldNotSaveStudentReviewAfterReviewFinish() throws Exception {
    	AssignmentServiceImpl assignmentServiceImpl = spy(new AssignmentServiceImpl());
    	ReviewServiceImpl reviewServiceImpl = spy(new ReviewServiceImpl()); 
    	
        // student review
    	doReturn(student2).when(assignmentServiceImpl).getUser();
    	doReturn(student2).when(reviewServiceImpl).getUser();
        Collection<Course> student2Reviews = assignmentServiceImpl.getUserReviewingTasks(1,2009);
        ReviewEntry student2ReviewEntry = student2Reviews.iterator().next().getWritingActivities().iterator().next().getReviewingActivities().get(0).getEntries().iterator().next();
        try {
        	reviewServiceImpl.saveReview(student2ReviewEntry.getReview());
        	fail();
        } catch (Exception e) {
        	assertThat(e.getMessage(), is("The deadline has already passed."));
        }
    }
    
    @Test
    public void shouldSubmitTutorReviewAfterReviewFinish() throws Exception {
    	AssignmentServiceImpl assignmentServiceImpl = spy(new AssignmentServiceImpl());
    	ReviewServiceImpl reviewServiceImpl = spy(new ReviewServiceImpl()); 

        // tutor review
        doReturn(tutor1).when(assignmentServiceImpl).getUser();
        doReturn(tutor1).when(reviewServiceImpl).getUser();
        Collection<Course> tutor1Reviews = assignmentServiceImpl.getUserReviewingTasks(1,2009);
        ReviewEntry tutor1ReviewEntry = tutor1Reviews.iterator().next().getWritingActivities().iterator().next().getReviewingActivities().get(0).getEntries().iterator().next();
        reviewServiceImpl.saveReview(tutor1ReviewEntry.getReview());
    }
    
    @Test
    public void shouldSubmitDocumentBeforeActivityFinish() throws Exception {
    	AssignmentServiceImpl assignmentServiceImpl = spy(new AssignmentServiceImpl());
    	doReturn(student2).when(assignmentServiceImpl).getUser();
        LogbookDocEntry student2LogbookDocEntry = (LogbookDocEntry) assignmentManager.getAssignmentDao().loadDocEntryWhereUser(logbookActivity, student2);
        assignmentServiceImpl.submitDocEntry(student2LogbookDocEntry);
    }

    @Test
    public void testFinishActivity() {
        assignmentManager.finishActivityDeadline(course, writingActivity, writingActivity.getDeadlines().get(1));
        assertThat(writingActivity.getStatus(), is(Activity.STATUS_FINISH));

        assignmentManager.finishActivityDeadline(course, groupActivity, groupActivity.getDeadlines().get(0));
        assertThat(groupActivity.getStatus(), is(Activity.STATUS_FINISH));
        int totalReviews = 0;
        for (ReviewingActivity reviewingActivity : groupActivity.getReviewingActivities()) {
            assertThat(reviewingActivity.getEntries().size(), not(0));
            totalReviews += reviewingActivity.getEntries().size();
        }
        assertThat(totalReviews, is(4));
        
        assignmentManager.finishActivityDeadline(course, logbookActivity, logbookActivity.getDeadlines().get(0));
        assertThat(logbookActivity.getStatus(), is(Activity.STATUS_FINISH));
    }
    
    @Test
    public void shouldNotSubmitDocumentAfterActivityFinish() throws Exception {
    	AssignmentServiceImpl assignmentServiceImpl = spy(new AssignmentServiceImpl());
    	doReturn(student2).when(assignmentServiceImpl).getUser();
        LogbookDocEntry student2LogbookDocEntry = (LogbookDocEntry) assignmentManager.getAssignmentDao().loadDocEntryWhereUser(logbookActivity, student2);
        try {
	        assignmentServiceImpl.submitDocEntry(student2LogbookDocEntry);
	    	fail();
	    } catch (Exception e) {
	    	assertThat(e.getMessage(), is("The deadline has already passed."));
	    }
    }
    
    @Test
    public void testFinishActivityReview() {
        assignmentManager.finishReviewingActivity(course, groupActivity.getReviewingActivities().get(0),groupActivity.getDeadlines().get(0));
        assertThat(groupActivity.getReviewingActivities().get(0).getStatus(), is(Activity.STATUS_FINISH));
    }
    
    @Test
    public void testRemoveUserAfterActivityFinish() throws Exception {
        // remove student from course spreadsheet
        List<User> students = new ArrayList<User>();
        students.add(student2);
        assignmentManager.getAssignmentRepository().removeStudentsFromSpreadsheet(course, students);
        assignmentManager.saveCourse(course);
        assertThat(writingActivity.getEntries().size(), is(4));
        assertThat(groupActivity.getEntries().size(), is(3));
        assertThat(logbookActivity.getEntries().size(), is(4));
    }

    @Test 
    public void shouldUnlockDocument() throws Exception {
    	DocEntry docEntry = writingActivity.getEntries().iterator().next();
    	docEntry.setLocked(false);
        assignmentManager.updateDocument(docEntry);
             
        // check that document has been unlocked
        DocEntry updateDocEntry = assignmentManager.getAssignmentDao().loadDocEntry(docEntry.getDocumentId());
        assertThat(updateDocEntry.getLocked(), is(false));
    }
    
    @Test 
    public void shouldLockDocument() throws Exception {
    	DocEntry docEntry = writingActivity.getEntries().iterator().next();
    	docEntry.setLocked(true);
        assignmentManager.updateDocument(docEntry);
             
        // check that document has been locked
        DocEntry updateDocEntry = assignmentManager.getAssignmentDao().loadDocEntry(docEntry.getDocumentId());
        assertThat(updateDocEntry.getLocked(), is(true));
    }

    @Test
    public void testDeleteActivity() throws Exception {
    	assignmentManager.deleteActivity(writingActivity);
    	AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
    	course = assignmentDao.loadCourse(course.getId());
        assertThat(course.getWritingActivities().contains(writingActivity), is(false));
    }
    
    @Test
    public void shouldDeleteCourse() throws Exception {
        assignmentManager.deleteCourse(course);
        AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
        course = assignmentDao.loadCourse(course.getId());
        assertThat(course, nullValue());
    }

    @AfterClass
    public static void cleanUp() {

    }
}
