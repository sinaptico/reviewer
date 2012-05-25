package au.edu.usyd.reviewer.server.reviewstratergy;

import java.io.File;
import java.util.ArrayList;
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

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentRepository;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.util.FileUtil;

import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class SpreadsheetReviewStratergy implements ReviewStratergy {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Course course;
	private WritingActivity writingActivity;
	private AssignmentDao assignmentDao;
	private AssignmentRepository assignmentRepository;
	private String folder;

	public SpreadsheetReviewStratergy(Course course, WritingActivity writingActivity, AssignmentDao assignmentDao, AssignmentRepository assignmentRepository) {
		this.course = course;
		this.writingActivity = writingActivity;
		this.assignmentDao = assignmentDao;
		this.assignmentRepository = assignmentRepository;
		this.folder = getDocumentsFolder(course.getId(),writingActivity.getId(),writingActivity.getCurrentDeadline().getId(),WritingActivity.TUTORIAL_ALL);
	}
	
	public String getDocumentsFolder(long courseId, long activityId, long activityDeadlineId, String tutorial) {
		String documentsHome = Reviewer.getDocumentsHome();
		return String.format(documentsHome + "/%s/%s/%s/%s", courseId, activityId, activityDeadlineId, tutorial);
	}

	@Override
	public Map<DocEntry, Set<User>> allocateReviews() {
		Map<DocEntry, Set<User>> reviewSetup = new HashMap<DocEntry, Set<User>>();
		
		List<Integer> entriesNumberList = new ArrayList<Integer>();
		List<ListEntry> listEntries = getListEntries();
		for (int i=0; i<listEntries.size(); i++){entriesNumberList.add(i);}
		
		LOOP_ENTRIES: for (ListEntry listEntry : listEntries) {
			// get document to review			
			User reviewee = assignmentDao.loadUser(listEntry.getCustomElements().getValue("revieweeId").trim());
			DocEntry docEntry;
			
			docEntry = returnDocEntry(reviewee);
			
			//find an non-empty entry in case that the assigned one in the spreadsheet is. 
			if (docEntryIsEmpty(docEntry)){
				Collections.shuffle(entriesNumberList,new Random());
			    
			    for (Integer entryNumber : entriesNumberList) {
			    	reviewee = assignmentDao.loadUser(listEntries.get(entryNumber).getCustomElements().getValue("revieweeId").trim());
			    	
			    	User reviewer = assignmentDao.loadUser(listEntries.get(entryNumber).getCustomElements().getValue("reviewerId").trim());
			    	if (reviewer.getId().equalsIgnoreCase(reviewee.getId())){continue;}
			    	
			    	DocEntry tempDocEntry = returnDocEntry(reviewee);
			    	if (!docEntryIsEmpty(tempDocEntry)){
			    		docEntry = tempDocEntry;
			    		break;
			    	}
				}	
			}
			
	        if (docEntry !=null){	        	
				// check that reviewer has not already been assigned to review this document
				User reviewer = assignmentDao.loadUser(listEntry.getCustomElements().getValue("reviewerId").trim());
				ReviewEntry reviewEntry = assignmentDao.loadReviewEntryWhereDocEntryAndOwner(docEntry, reviewer);
				if (reviewEntry != null) {
					continue LOOP_ENTRIES;
				}

				// assign reviewer to document
				if (!reviewSetup.containsKey(docEntry)) {
					reviewSetup.put(docEntry, new HashSet<User>());
				}
				reviewSetup.get(docEntry).add(reviewer);
	        }				
		}
		
		
		// check that students aren't assigned to review their own documents
//		for (DocEntry docEntry : reviewSetup.keySet()) {
//			for (User student : new HashSet<User>(reviewSetup.get(docEntry))) {
//				if (docEntry.getOwner() != null && docEntry.getOwner().equals(student) || docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(student)) {
//					Collections.shuffle(shuffledDocEntries,new Random());
//					SWAP: for (int iteration = 0; iteration < 2; iteration++) {
//						for (DocEntry swapDocEntry : shuffledDocEntries) {
//							if ((swapDocEntry.getOwner() == null || !swapDocEntry.getOwner().equals(student)) && (swapDocEntry.getOwnerGroup() == null || !swapDocEntry.getOwnerGroup().getUsers().contains(student))) {
//								switch (iteration) {
//								case 0:
//									// check if the student reviewer can be
//									// swapped with the reviewer from the
//									// another document
//									for (User swapStudent : new HashSet<User>(reviewSetup.get(swapDocEntry))) {
//										if ((docEntry.getOwner() == null || !docEntry.getOwner().equals(swapStudent)) && (docEntry.getOwnerGroup() == null || !docEntry.getOwnerGroup().getUsers().contains(swapStudent))) {
//											reviewSetup.get(docEntry).remove(student);
//											reviewSetup.get(docEntry).add(swapStudent);
//											reviewSetup.get(swapDocEntry).remove(swapStudent);
//											reviewSetup.get(swapDocEntry).add(student);
//											break SWAP;
//										}
//									}
//									break;
//								case 1:
//									// move student reviewer
//									reviewSetup.get(docEntry).remove(student);
//									reviewSetup.get(swapDocEntry).add(student);
//									break SWAP;
//								}
//							}
//						}
//					}
//					if (reviewSetup.get(docEntry).contains(student)) {
//						logger.error("Failed to start review: unable to assign review to student.");
//						return new HashMap<DocEntry, Set<User>>();
//					}
//				}
//			}
//		}
		
		
		return reviewSetup;
	}
	
	private DocEntry returnDocEntry(User reviewee) {
		DocEntry docEntry;
		UserGroup revieweeUserGroup = null;
		
		if (writingActivity.getGroups()){					
			revieweeUserGroup = returnUserGroup(reviewee);
	        docEntry = assignmentDao.loadDocEntryWhereOwnerGroup(writingActivity, revieweeUserGroup);
		}else{
			docEntry = assignmentDao.loadDocEntryWhereUser(writingActivity, reviewee);
		}
		
		return docEntry;
	}

	private boolean docEntryIsEmpty(DocEntry docEntry) {
		File file = new File(folder + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf");
		
		try{
			File empty = new File(Reviewer.getEmptyDocument());
			if (empty.length() == file.length()){return true;}			
		} catch (Exception e) {
			logger.error("Error reading empty document.", e);					
		}	

		return false;
	}

	private UserGroup returnUserGroup(User reviewee) {
		Set<UserGroup> studentGroup = course.getStudentGroups();
        Iterator<UserGroup> it = studentGroup.iterator();
        UserGroup revieweeUserGroup = null;
        
		while(it.hasNext())
        {
        	UserGroup userGroup=(UserGroup)it.next();
        	if (userGroup.getUsers().contains(reviewee)){
        		revieweeUserGroup = userGroup;
        		break;
        	}
        }
		return revieweeUserGroup;
	}

	protected List<ListEntry> getListEntries() {
		List<ListEntry> listEntries = null;
		try {
			SpreadsheetEntry spreadsheetEntry = assignmentRepository.getGoogleDocsServiceImpl().getSpreadsheet(course.getSpreadsheetId());
			WorksheetEntry worksheetEntry = assignmentRepository.getGoogleSpreadsheetServiceImpl().getSpreadsheetWorksheets(spreadsheetEntry).get(1);
			listEntries = assignmentRepository.getGoogleSpreadsheetServiceImpl().getWorksheetRows(worksheetEntry);
		} catch (Exception e) {
			logger.error("Failed to alloation reviews", e);
		}
		return listEntries;
	}
}
