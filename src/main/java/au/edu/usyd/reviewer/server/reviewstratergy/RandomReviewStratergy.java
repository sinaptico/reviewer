package au.edu.usyd.reviewer.server.reviewstratergy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;

public class RandomReviewStratergy implements ReviewStratergy {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Collection<User> students;
	private Collection<DocEntry> docEntries;
	private ReviewingActivity reviewingActivity;

	public RandomReviewStratergy(ReviewingActivity reviewingActivity, Collection<DocEntry> docEntries, Collection<User> students) {
		this.reviewingActivity = reviewingActivity;
		this.docEntries = docEntries;
		this.students = students;
	}

	@Override
	public Map<DocEntry, Set<User>> allocateReviews() {
		// initialise data structure for review setup
		Map<DocEntry, Set<User>> reviewSetup = new HashMap<DocEntry, Set<User>>();
		for (DocEntry docEntry : docEntries) {
			reviewSetup.put(docEntry, new HashSet<User>());
		}

		// remove students already assigned to reviews
		for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
			students.remove(reviewEntry.getOwner());
		}

		// check if there are enough documents to review
		if (docEntries.size() < 2 || docEntries.size() < reviewingActivity.getNumStudentReviewers() || docEntries.size() < reviewingActivity.getNumTutorReviewers() || docEntries.size() < reviewingActivity.getNumLecturerReviewers()) {
			logger.error("Failed to start review: too few documents.");
			return reviewSetup;
		}

		// assign students to review random documents
		List<DocEntry> shuffledDocEntries = new ArrayList<DocEntry>(docEntries);
		Collections.shuffle(shuffledDocEntries,new Random());
		Iterator<DocEntry> docEntriesIterator = shuffledDocEntries.iterator();
		for (User student : students) {
			for (int i = 0; i < reviewingActivity.getNumStudentReviewers(); i++) {
				if (!docEntriesIterator.hasNext()) {
					docEntriesIterator = shuffledDocEntries.iterator();
				}
				reviewSetup.get(docEntriesIterator.next()).add(student);
			}
		}

		// check that students aren't assigned to review their own documents
		for (DocEntry docEntry : reviewSetup.keySet()) {
			for (User student : new HashSet<User>(reviewSetup.get(docEntry))) {
				if (docEntry.getOwner() != null && docEntry.getOwner().equals(student) || docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(student)) {
					Collections.shuffle(shuffledDocEntries,new Random());
					SWAP: for (int iteration = 0; iteration < 2; iteration++) {
						for (DocEntry swapDocEntry : shuffledDocEntries) {
							if ((swapDocEntry.getOwner() == null || !swapDocEntry.getOwner().equals(student)) && (swapDocEntry.getOwnerGroup() == null || !swapDocEntry.getOwnerGroup().getUsers().contains(student))) {
								switch (iteration) {
								case 0:
									// check if the student reviewer can be
									// swapped with the reviewer from the
									// another document
									for (User swapStudent : new HashSet<User>(reviewSetup.get(swapDocEntry))) {
										if ((docEntry.getOwner() == null || !docEntry.getOwner().equals(swapStudent)) && (docEntry.getOwnerGroup() == null || !docEntry.getOwnerGroup().getUsers().contains(swapStudent))) {
											reviewSetup.get(docEntry).remove(student);
											reviewSetup.get(docEntry).add(swapStudent);
											reviewSetup.get(swapDocEntry).remove(swapStudent);
											reviewSetup.get(swapDocEntry).add(student);
											break SWAP;
										}
									}
									break;
								case 1:
									// move student reviewer
									reviewSetup.get(docEntry).remove(student);
									reviewSetup.get(swapDocEntry).add(student);
									break SWAP;
								}
							}
						}
					}
					if (reviewSetup.get(docEntry).contains(student)) {
						logger.error("Failed to start review: unable to assign review to student.");
						return new HashMap<DocEntry, Set<User>>();
					}
				}
			}
		}
		return reviewSetup;
	}
}
