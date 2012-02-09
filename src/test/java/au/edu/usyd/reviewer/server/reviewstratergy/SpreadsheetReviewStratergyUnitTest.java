package au.edu.usyd.reviewer.server.reviewstratergy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.reviewstratergy.SpreadsheetReviewStratergy;

import com.google.gdata.data.spreadsheet.ListEntry;

public class SpreadsheetReviewStratergyUnitTest {

    private List<ListEntry> listEntries;
    private AssignmentDao assignmentDao;
    private WritingActivity writingActivity;
    private Course course;

    @Before
    public void setUp() {
    	assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
    	
    	User user1 = new User();
        user1.setId("user1");
        User user2 = new User();
        user2.setId("user2");
        User user3 = new User();
        user3.setId("user3");       
        assignmentDao.save(user1);
        assignmentDao.save(user2);
        assignmentDao.save(user3);
        
        UserGroup userGroup1 = new UserGroup();
        userGroup1.setName("1");
        Set<User> users1 = new HashSet<User>();
        users1.add(user1);
        users1.add(user2);
        userGroup1.setUsers(users1);
        UserGroup userGroup2 = new UserGroup();
        userGroup2.setName("2");
        Set<User> users2 = new HashSet<User>();
        users2.add(user3);        
        userGroup2.setUsers(users2);
        assignmentDao.save(userGroup1);
        assignmentDao.save(userGroup2);

        DocEntry docEntry1 = new DocEntry();
        docEntry1.setDocumentId("document:1");
        docEntry1.setOwner(user1);
        docEntry1.setOwnerGroup(userGroup1);
        DocEntry docEntry2 = new DocEntry();
        docEntry2.setDocumentId("document:2");        
        docEntry2.setOwner(user2); 
        docEntry2.setOwnerGroup(userGroup1);
        DocEntry docEntry3 = new DocEntry();
        docEntry3.setDocumentId("document:3");
        docEntry3.setOwner(user3);
        docEntry3.setOwnerGroup(userGroup2);
        assignmentDao.save(docEntry1);
        assignmentDao.save(docEntry2);
        assignmentDao.save(docEntry3);
        
        writingActivity = new WritingActivity();
        writingActivity.getEntries().add(docEntry1);
        writingActivity.getEntries().add(docEntry2);
        writingActivity.getEntries().add(docEntry3);        
        assignmentDao.save(writingActivity);
        
        
        course = new Course();
        course.setName("test course");
        Set<WritingActivity> writingActivities = new HashSet<WritingActivity>();
        writingActivities.add(writingActivity);
        course.setWritingActivities(writingActivities);
        Set<UserGroup> userGroups = new HashSet<UserGroup>();
        userGroups.add(userGroup1);        
        userGroups.add(userGroup2);
        course.setStudentGroups(userGroups);

        ListEntry listEntry1 = new ListEntry();
        listEntry1.getCustomElements().setValueLocal("revieweeId", user1.getId());
        listEntry1.getCustomElements().setValueLocal("reviewerId", user3.getId());
        ListEntry listEntry2 = new ListEntry();
        listEntry2.getCustomElements().setValueLocal("revieweeId", user2.getId());
        listEntry2.getCustomElements().setValueLocal("reviewerId", user3.getId());
        ListEntry listEntry3 = new ListEntry();
        listEntry3.getCustomElements().setValueLocal("revieweeId", user3.getId());
        listEntry3.getCustomElements().setValueLocal("reviewerId", user2.getId());
        
        listEntries = new ArrayList<ListEntry>();
        listEntries.add(listEntry1);
        listEntries.add(listEntry2);
        listEntries.add(listEntry3);
    }

    @Test
    public void testIndividualReviewAllocation() {
    	SpreadsheetReviewStratergy reviewallocation = spy(new SpreadsheetReviewStratergy(course, writingActivity, assignmentDao, null)); 
    	doReturn(listEntries).when(reviewallocation).getListEntries();
    	Map<DocEntry, Set<User>> reviewSetup = reviewallocation.allocateReviews();
        assertThat(reviewSetup.size(), is(3));
        for (DocEntry docEntry : reviewSetup.keySet()) {
            assertThat(reviewSetup.get(docEntry).size(), is(1));
            //check that reviewer is not the owner of the document
            for(User reviewer : reviewSetup.get(docEntry)) {
            	assertThat(docEntry.getOwner(), not(reviewer));
            }
        }
        
        writingActivity.setGroups(true);
        
    	reviewallocation = spy(new SpreadsheetReviewStratergy(course, writingActivity, assignmentDao, null)); 
    	doReturn(listEntries).when(reviewallocation).getListEntries();
    	reviewSetup = reviewallocation.allocateReviews();
        assertThat(reviewSetup.size(), is(2));
        for (DocEntry docEntry : reviewSetup.keySet()) {
            assertThat(reviewSetup.get(docEntry).size(), is(1));
            //check that reviewer is not the owner of the document
            for(User reviewer : reviewSetup.get(docEntry)) {
            	assertThat(docEntry.getOwner(), not(reviewer));
            }
        }        
    }
    
    @After
    public void cleanUp() {
    	assignmentDao.delete(writingActivity);
    }
}
