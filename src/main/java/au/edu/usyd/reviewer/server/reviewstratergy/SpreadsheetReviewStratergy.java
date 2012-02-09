package au.edu.usyd.reviewer.server.reviewstratergy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class SpreadsheetReviewStratergy implements ReviewStratergy {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Course course;
	private WritingActivity writingActivity;
	private AssignmentDao assignmentDao;
	private AssignmentRepository assignmentRepository;

	public SpreadsheetReviewStratergy(Course course, WritingActivity writingActivity, AssignmentDao assignmentDao, AssignmentRepository assignmentRepository) {
		this.course = course;
		this.writingActivity = writingActivity;
		this.assignmentDao = assignmentDao;
		this.assignmentRepository = assignmentRepository;
	}

	@Override
	public Map<DocEntry, Set<User>> allocateReviews() {
		Map<DocEntry, Set<User>> reviewSetup = new HashMap<DocEntry, Set<User>>();		
		Set<UserGroup> studentGroup = course.getStudentGroups();
		
		LOOP_ENTRIES: for (ListEntry listEntry : getListEntries()) {
			// get document to review			
			User reviewee = assignmentDao.loadUser(listEntry.getCustomElements().getValue("revieweeId").trim());
			DocEntry docEntry;
			UserGroup revieweeUserGroup = null;
			
			if (writingActivity.getGroups()){				
		        Iterator<UserGroup> it = studentGroup.iterator();
		        while(it.hasNext())
		        {
		        	UserGroup userGroup=(UserGroup)it.next();
		        	if (userGroup.getUsers().contains(reviewee)){
		        		revieweeUserGroup = userGroup;
		        		break;
		        	}
		        }
		        docEntry = assignmentDao.loadDocEntryWhereOwnerGroup(writingActivity, revieweeUserGroup);
			}else{
				docEntry = assignmentDao.loadDocEntryWhereUser(writingActivity, reviewee);
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
		return reviewSetup;
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
