package au.edu.usyd.reviewer.server.rpc;

import java.util.Collection;

import org.apache.catalina.realm.RealmBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.assignment.AssignmentService;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;
import au.edu.usyd.reviewer.server.util.CloneUtil;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AssignmentServiceImpl extends RemoteServiceServlet implements AssignmentService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager(getUser().getOrganization());
	private AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
	private UserDao userDao = UserDao.getInstance();

	public User getUser() {
//		User user = (User) this.getThreadLocalRequest().getSession().getAttribute("user");
//		return user;
		UserDao userDao = UserDao.getInstance();
		User user = null;
		try {
			user = userDao.getUserByEmail(this.getThreadLocalRequest().getUserPrincipal().getName());
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public Collection<Course> getUserActivities(int semester, int year) throws Exception {
		return CloneUtil.clone(assignmentDao.loadUserActivities(semester, year,this.getUser()));
	}

	@Override
	public Collection<Course> getUserReviewingTasks(int semester, int year, Boolean includeFinishedReviews) throws Exception {
		return CloneUtil.clone(assignmentDao.loadUserReviewingTasks(semester, year, includeFinishedReviews, this.getUser()));
	}

	@Override
	public Collection<Course> getUserWritingTasks(int semester, int year) throws Exception {
		return CloneUtil.clone(assignmentDao.loadUserWritingTasks(semester, year, this.getUser()));
	}

	public boolean isCourseInstructor(Course course) {
		User user = getUser();
		return user == null ? false : course.getLecturers().contains(user) || course.getTutors().contains(user);
	}

	@Override
	public <D extends DocEntry> D submitDocEntry(D docEntry) throws Exception {
		D currentDocEntry = (D) assignmentDao.loadDocEntry(docEntry.getDocumentId());
		if (!currentDocEntry.getLocked()) {
			if(currentDocEntry.getOwner() != null && currentDocEntry.getOwner().equals(this.getUser()) || currentDocEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(this.getUser())) {
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
		logger.info("Getting user details, email=" + getUser().getEmail() +  " email ");
		User user = null;
		if ( getUser().getId() != null){		
			return CloneUtil.clone(userDao.load(getUser().getId()));
		} else {
			return CloneUtil.clone(userDao.getUserByEmail(getUser().getEmail()));
		}
	}

	@Override
	public User updateUserPassword(User user, String newPassword) throws Exception {
		logger.info("Changing user password, email =" + getUser().getEmail());
		User storedUser = userDao.load(getUser().getId());
		
		String typedPasswordDigested = RealmBase.Digest(user.getPassword(), "MD5",null);
		
		if (storedUser.getPassword().equalsIgnoreCase(typedPasswordDigested)){
			storedUser.setPassword(RealmBase.Digest(newPassword, "MD5",null));
			assignmentDao.save(storedUser);
		}else{
			throw new Exception("Wrong password, please try again");
		}
		
		return CloneUtil.clone(storedUser);
	}
}
