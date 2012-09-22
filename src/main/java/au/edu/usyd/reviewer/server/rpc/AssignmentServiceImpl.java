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
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AssignmentServiceImpl extends RemoteServiceServlet implements AssignmentService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	private AssignmentDao assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());;
	private UserDao userDao = UserDao.getInstance();

	// logged user
	private User user = null;
		
	@Override
	public Collection<Course> getUserActivities(int semester, int year) throws Exception {
		initialize();
		return assignmentDao.loadUserActivities(semester, year,this.user);
	}

	@Override
	public Collection<Course> getUserReviewingTasks(int semester, int year, Boolean includeFinishedReviews) throws Exception {
		initialize();
		return assignmentDao.loadUserReviewingTasks(semester, year, includeFinishedReviews, this.user);
	}

	@Override
	public Collection<Course> getUserWritingTasks(int semester, int year) throws Exception {
		initialize();
		User mockedUser = getMockedUser();
		if (mockedUser != null){
			return assignmentDao.loadUserWritingTasks(semester, year, mockedUser);
		} else{
			throw new MessageException("The users could not be mocked");
		}
			
	}

	private boolean isCourseInstructor(Course course) {
		return user == null ? false : course.getLecturers().contains(user) || course.getTutors().contains(user);
	}

	@Override
	public DocEntry submitDocEntry(DocEntry docEntry) throws Exception {
		initialize();
		DocEntry currentDocEntry =assignmentDao.loadDocEntry(docEntry.getDocumentId());
		if (!currentDocEntry.getLocked()) {
			if(currentDocEntry.getOwner() != null && currentDocEntry.getOwner().equals(this.getMockedUser()) || currentDocEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(this.user)) {
				docEntry = assignmentManager.submitDocument(currentDocEntry);
			} else {
				throw new Exception("Your session has expired. Please login again to submit your document.");
			}
		} else {
			throw new Exception("The deadline has already passed.");
		}
		if (docEntry != null){
			docEntry = docEntry.clone();
		}
		return docEntry;
	}

	@Override
	public DocEntry updateDocEntry(DocEntry updatedEntry) throws Exception {
		initialize();
		WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(updatedEntry);
		Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
		if (isCourseInstructor(course)) {
			// update document permissions
			DocEntry docEntry = assignmentDao.loadDocEntry(updatedEntry.getDocumentId());
			docEntry.setLocked(updatedEntry.getLocked());
			assignmentManager.updateDocument(docEntry);
			return docEntry;
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public User getUserDetails() throws Exception {
		initialize();
		logger.info("Getting user details, email=" + user.getEmail());
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
		
		return storedUser;
	}
	
	/**
	 * Get logger user, its organization an initialize Reviewer with it
	 */
	private void initialize() throws Exception{
		if (user == null){
			user = getUser();
		}
		
		if (assignmentManager.getOrganization() == null){
			Organization organization = user.getOrganization();	
			Reviewer.initializeAssignmentManager(organization);
		}
	}
	
	private User getUser() {
		
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			Object obj = request.getSession().getAttribute("user");
			
			if (obj != null)
			{
				user = (User) obj;
			}
			
			if  (user == null){
				UserDao userDao = UserDao.getInstance();
				Principal principal = request.getUserPrincipal();
				user = userDao.getUserByEmail(principal.getName());
				request.getSession().setAttribute("user", user);
			} 
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	private User getMockedUser() throws MessageException{
		User mockedUser = null;
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			mockedUser = (User) request.getSession().getAttribute("mockedUser");
			 
			if ( mockedUser != null && mockedUser.getOrganization() == null){
				mockedUser = userDao.getUserByEmail(mockedUser.getEmail());
				request.getSession().setAttribute("mockedUser", mockedUser);
			}
			else if ( mockedUser == null && !user.isManager()){
					mockedUser = getUser();
					request.getSession().setAttribute("mockedUser", mockedUser);
			} else if (mockedUser == null && user.isManager()){
				throw new MessageException(Constants.EXCEPTION_USER_NOT_MOCKED);
			}
		} catch (MessageException e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_USER_NOT_MOCKED);
		}
		return mockedUser;
	}
}
	