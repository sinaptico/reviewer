package au.edu.usyd.reviewer.server.reviewstratergy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.server.reviewstratergy.RandomReviewStratergy;
import au.edu.usyd.reviewer.server.reviewstratergy.ReviewStratergy;

@RunWith(Parameterized.class)
public class RandomReviewStratergyUnitTest {

	protected List<DocEntry> docEntries;
	protected List<User> students;

	@Parameters
	public static List<Object[]> setUp() {

		User user1 = new User();
		user1.setId("user1");
		User user2 = new User();
		user2.setId("user2");
		User user3 = new User();
		user3.setId("user3");
		User user4 = new User();
		user4.setId("user4");

		UserGroup userGroup1 = new UserGroup();
		userGroup1.setName("1");
		userGroup1.getUsers().add(user1);
		UserGroup userGroup2 = new UserGroup();
		userGroup2.setName("2");
		userGroup2.getUsers().add(user2);
		UserGroup userGroup3 = new UserGroup();
		userGroup3.setName("3");
		userGroup3.getUsers().add(user2);
		userGroup3.getUsers().add(user3);
		userGroup3.getUsers().add(user4);

		DocEntry docEntry1 = new DocEntry();
		docEntry1.setDocumentId("document:1");
		docEntry1.setOwnerGroup(userGroup1);
		DocEntry docEntry2 = new DocEntry();
		docEntry2.setDocumentId("document:2");
		docEntry2.setOwnerGroup(userGroup2);
		DocEntry docEntry3 = new DocEntry();
		docEntry3.setDocumentId("document:3");
		docEntry3.setOwnerGroup(userGroup3);

		List<User> students1 = new ArrayList<User>();
		students1.add(user1);
		students1.add(user2);
		List<DocEntry> docEntries1 = new ArrayList<DocEntry>();
		docEntries1.add(docEntry1);
		docEntries1.add(docEntry2);

		List<User> students2 = new ArrayList<User>();
		students2.add(user1);
		students2.add(user2);
		students2.add(user3);
		students2.add(user4);
		List<DocEntry> docEntries2 = new ArrayList<DocEntry>();
		docEntries2.add(docEntry1);
		docEntries2.add(docEntry3);

		return Arrays.asList(new Object[][] { { docEntries1, students1 }, { docEntries2, students2 } });
	}

	public RandomReviewStratergyUnitTest(List<DocEntry> docEntries, List<User> students) {
		this.docEntries = docEntries;
		this.students = students;
	}

	@Test
	public void testReviewAllocation() {
		ReviewStratergy reviewallocation = new RandomReviewStratergy(new ReviewingActivity(), docEntries, students);
		Map<DocEntry, Set<User>> reviewSetup = reviewallocation.allocateReviews();
		assertThat(reviewSetup.size(), is(docEntries.size()));
		for (DocEntry docEntry : reviewSetup.keySet()) {
			// check that document has at least one reviewer
			assertThat(reviewSetup.get(docEntry).size(), greaterThanOrEqualTo(1));
			// check that reviewer is not the owner of the document
			for (User reviewer : reviewSetup.get(docEntry)) {
				if (docEntry.getOwner() != null) {
					assertThat(docEntry.getOwner(), not(reviewer));
				} else if (docEntry.getOwnerGroup() != null) {
					for (User owner : docEntry.getOwnerGroup().getUsers()) {
						assertThat(owner, not(reviewer));
					}
				} else {
					fail("document has no owner");
				}
			}
		}

		// check that all students have a document to review
		STUDENTS_LOOP: for (User student : students) {
			for (Set<User> reviewers : reviewSetup.values()) {
				if (reviewers.contains(student))
					continue STUDENTS_LOOP;
			}
			fail("student has no document to review");
		}
	}
}
