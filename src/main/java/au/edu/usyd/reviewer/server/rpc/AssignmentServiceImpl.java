package au.edu.usyd.reviewer.server.rpc;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.apache.catalina.realm.RealmBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.assignment.AssignmentService;
import au.edu.usyd.reviewer.client.assignment.ReviewTask;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.util.ConnectionUtil;

public class AssignmentServiceImpl extends ReviewerServiceImpl implements AssignmentService {
	private static final long serialVersionUID = 1L;


	private final Logger logger = LoggerFactory.getLogger(getClass());
	

	// logged user
	private User user = null;
		
	@Override
	public Collection<Course> getUserActivities(int semester, int year,Long organizationId) throws Exception {
		initialize();
		User mockedUser = getMockedUser();
		if (isGuestOrAdminOrSuperAdminOrStaff()){
			return assignmentDao.loadUserActivities(semester, year,mockedUser);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	public List<ReviewTask> getUserReviewingTasks(int semester, int year, Boolean includeFinishedReviews, int start,int length) throws Exception {
		initialize();
		User mockedUser = getMockedUser();
		if (isGuestOrAdminOrSuperAdminOrStaff()){
			return assignmentDao.loadUserReviewingTasks(semester, year, includeFinishedReviews, mockedUser, start,length);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	public Integer getUserReviewingTasksTotalAccount(int semester, int year, Boolean includeFinishedReviews,Long organizationId) throws Exception {
		initialize();
		User mockedUser = getMockedUser();
		if (isGuestOrAdminOrSuperAdminOrStaff()){
			int reviewTasksSize = assignmentDao.loadUserReviewingTasksSize(semester, year, includeFinishedReviews, mockedUser);
			
			return reviewTasksSize;
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	
	@Override
	public Collection<Course> getUserWritingTasks(int semester, int year,Long organizationId) throws Exception {
		initialize();
		User mockedUser = getMockedUser();
		if (isGuestOrAdminOrSuperAdminOrStaff()){
				return assignmentDao.loadUserWritingTasks(semester, year, mockedUser);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
			
	}

	

	@Override
	public DocEntry submitDocEntry(DocEntry docEntry) throws Exception {
		initialize();
		if (isGuestOrAdminOrSuperAdminOrStaff()){
			DocEntry currentDocEntry =assignmentDao.loadDocEntry(docEntry.getDocumentId());
			if (!currentDocEntry.getLocked()) {
				User mockedUser = getMockedUser();
				if(currentDocEntry.getOwner() != null && currentDocEntry.getOwner().equals(mockedUser) || 
					currentDocEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(mockedUser)) {
					docEntry = assignmentManager.submitDocument(currentDocEntry);
				} else {
					throw new MessageException(Constants.EXCEPTION_SESSION_EXPIRED_SUBMIT_DOCUMENT);
				}
			} else {
				throw new MessageException(Constants.EXCEPTION_DEADLINE_ALREADY_PASSED);
			}
			if (docEntry != null){
				docEntry = docEntry.clone();
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
		return docEntry;
	}

	@Override
	public DocEntry updateDocEntry(DocEntry updatedEntry) throws Exception {
		initialize();
		WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(updatedEntry);
		Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
		if (isCourseInstructor(course) || isStaff() || isAdminOrSuperAdmin()) {
			// update document permissions
			DocEntry docEntry = assignmentDao.loadDocEntry(updatedEntry.getDocumentId());
			docEntry.setLocked(updatedEntry.getLocked());
			assignmentManager.updateDocument(docEntry);
			return docEntry;
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	public User getUserDetails() throws Exception {
		initialize();
		User mockedUser = null;
		if (isGuestOrAdminOrSuperAdminOrStaff()){
			mockedUser = getMockedUser();
			if ( mockedUser == null){
				mockedUser = user;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}

		return mockedUser;
	}

	@Override
	public User updateUserPassword(User user, String newPassword) throws Exception {
		initialize();
		User storedUser = null;
		if (isGuestOrAdminOrSuperAdminOrStaff()){
			storedUser = userDao.load(user.getId());
			
			String typedPasswordDigested = RealmBase.Digest(user.getPassword(), "MD5",null);
			
			if (storedUser.getPassword().equalsIgnoreCase(typedPasswordDigested)){
				storedUser.setPassword(RealmBase.Digest(newPassword, "MD5",null));
				storedUser = userDao.save(storedUser);
			}else{
				throw new MessageException(Constants.EXCEPTION_WRONG_PASSWORD);
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
		return storedUser;
	}
	
	@Override
	public Collection<Organization> getOrganizations() throws Exception{
		initialize();
		Collection organizations = new ArrayList<Organization>();
		if (isSuperAdmin()){
			OrganizationManager organizationManager = OrganizationManager.getInstance();
			organizations = organizationManager.getOrganizations();
		} 
		return organizations;
	}
}
	