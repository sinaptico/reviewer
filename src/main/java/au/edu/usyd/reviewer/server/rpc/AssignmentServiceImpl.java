package au.edu.usyd.reviewer.server.rpc;

import java.security.Principal;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.realm.RealmBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.assignment.AssignmentService;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;
import au.edu.usyd.reviewer.server.util.CloneUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AssignmentServiceImpl extends RemoteServiceServlet implements AssignmentService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	private AssignmentDao assignmentDao = null;
	private UserDao userDao = UserDao.getInstance();

	// logged user
	private User user = null;
		
	@Override
	public Collection<Course> getUserActivities(int semester, int year) throws Exception {
		initialize();
		return CloneUtil.clone(assignmentDao.loadUserActivities(semester, year,this.user));
	}

	@Override
	public Collection<Course> getUserReviewingTasks(int semester, int year, Boolean includeFinishedReviews) throws Exception {
		initialize();
		return CloneUtil.clone(assignmentDao.loadUserReviewingTasks(semester, year, includeFinishedReviews, this.user));
	}

	@Override
	public Collection<Course> getUserWritingTasks(int semester, int year) throws Exception {
		initialize();
		return CloneUtil.clone(assignmentDao.loadUserWritingTasks(semester, year, this.user));
	}

	private boolean isCourseInstructor(Course course) {
		return user == null ? false : course.getLecturers().contains(user) || course.getTutors().contains(user);
	}

	@Override
	public <D extends DocEntry> D submitDocEntry(D docEntry) throws Exception {
		initialize();
		D currentDocEntry = (D) assignmentDao.loadDocEntry(docEntry.getDocumentId());
		if (!currentDocEntry.getLocked()) {
			if(currentDocEntry.getOwner() != null && currentDocEntry.getOwner().equals(this.user) || currentDocEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(this.user)) {
				docEntry = assignmentManager.submitDocument(currentDocEntry);
			} else {
				throw new Exception("Your session has expired. Please login again to submit your document.");
			}
		} else {
			throw new Exception("The deadline has already passed.");
		}
		return CloneUtil.clone(docEntry);
	}

	@Override
	public <D extends DocEntry> D updateDocEntry(D updatedEntry) throws Exception {
		initialize();
		WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(updatedEntry);
		Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
		if (isCourseInstructor(course)) {
			// update document permissions
			D docEntry = (D) assignmentDao.loadDocEntry(updatedEntry.getDocumentId());
			docEntry.setLocked(updatedEntry.getLocked());
			assignmentManager.updateDocument(docEntry);
			return CloneUtil.clone(docEntry);
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public User getUserDetails() throws Exception {
		initialize();
		logger.info("Getting user details, email=" + user.getEmail() +  " email ");
		return user;
	}

	@Override
	public User updateUserPassword(User user, String newPassword) throws Exception {
		initialize();
		logger.info("Changing user password, email =" + user.getEmail());
		User storedUser = userDao.load(user.getId());
		
		String typedPasswordDigested = RealmBase.Digest(user.getPassword(), "MD5",null);
		
		if (storedUser.getPassword().equalsIgnoreCase(typedPasswordDigested)){
			storedUser.setPassword(RealmBase.Digest(newPassword, "MD5",null));
			assignmentDao.save(storedUser);
		}else{
			throw new Exception("Wrong password, please try again");
		}
		
		return CloneUtil.clone(storedUser);
	}
	
	/**
	 * Get logger user, its organization an initialize Reviewer with it
	 */
	private void initialize(){
		if (user == null){
			user = getUser();
			Organization organization = user.getOrganization();	
			Reviewer.initializeAssignmentManager(organization);
			assignmentDao = assignmentManager.getAssignmentDao();
		}
	}
	
	private User getUser() {
		UserDao userDao = UserDao.getInstance();
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			Principal principal = request.getUserPrincipal(); 
			user = userDao.getUserByEmail(principal.getName());
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}
}
	