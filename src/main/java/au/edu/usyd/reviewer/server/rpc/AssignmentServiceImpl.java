package au.edu.usyd.reviewer.server.rpc;

import java.security.Principal;


import java.util.ArrayList;
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
import au.edu.usyd.reviewer.server.CourseDao;
import au.edu.usyd.reviewer.server.OrganizationDao;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AssignmentServiceImpl extends RemoteServiceServlet implements AssignmentService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	private AssignmentDao assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
	private UserDao userDao = UserDao.getInstance();
	private CourseDao courseDao = CourseDao.getInstance();
	private OrganizationDao organizationDao = OrganizationDao.getInstance();

	// logged user
	private User user = null;
		
	@Override
	public Collection<Course> getUserActivities(int semester, int year,  Long organizationId) throws Exception {
		initialize();
		User mockedUser = getMockedUser();
		if (isAdminOrSuperAdmin()){
			return assignmentDao.loadUserActivities(semester, year,mockedUser);
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public Collection<Course> getUserReviewingTasks(int semester, int year, Boolean includeFinishedReviews,  Long organizationId) throws Exception {
		initialize();
		User mockedUser = getMockedUser();
		if (isGuestOrAdminOrSuperAdmin()){
			return assignmentDao.loadUserReviewingTasks(semester, year, includeFinishedReviews, mockedUser);
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public Collection<Course> getUserWritingTasks(int semester, int year, Long organizationId ) throws Exception {
		initialize();
		User mockedUser = getMockedUser();
		if (isGuestOrAdminOrSuperAdmin()){
				return assignmentDao.loadUserWritingTasks(semester, year, mockedUser);
		} else {
			throw new Exception("Permission denied");
		}
			
	}

	private boolean isCourseInstructor(Course course) {
		User mockedUser = null;
		try {
			mockedUser = getMockedUser();
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return mockedUser == null ? false : course.getLecturers().contains(mockedUser) || course.getTutors().contains(mockedUser);
	}

	@Override
	public DocEntry submitDocEntry(DocEntry docEntry) throws Exception {
		initialize();
		DocEntry currentDocEntry =assignmentDao.loadDocEntry(docEntry.getDocumentId());
		if (!currentDocEntry.getLocked()) {
			User mockedUser = getMockedUser();
			if(currentDocEntry.getOwner() != null && currentDocEntry.getOwner().equals(this.getMockedUser()) || 
				currentDocEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(mockedUser)) {
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
		User mockedUser = getMockedUser();
		if ( mockedUser != null){
			logger.info("Getting user details, email=" + mockedUser.getEmail());
		} else {
			mockedUser = user;
		}
		return mockedUser;
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
		user = getUser();
		Organization organization = user.getOrganization();	
		Reviewer.initializeAssignmentManager(organization);
	}
	
	public User getUser() {
		
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			Object obj = request.getSession().getAttribute("user");
			
			if (obj != null)
			{
				user = (User) obj;
			}
			Principal principal = request.getUserPrincipal();
			UserDao userDao = UserDao.getInstance();
			if  (user == null){
				user = userDao.getUserByEmail(principal.getName());
				request.getSession().setAttribute("user", user);
				logger.info("Logged User: " + user.getEmail());
			} else if (principal.getName() != null && !principal.getName().equals(user.getEmail())){
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
			 
			if ( mockedUser != null) {
				if (mockedUser.getOrganization() == null){
					mockedUser = userDao.getUserByEmail(mockedUser.getEmail());
					request.getSession().setAttribute("mockedUser", mockedUser);
				}
			}
			else {
				mockedUser = user;
				request.getSession().setAttribute("mockedUser", mockedUser);
			} 
		} catch (MessageException e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_USER_NOT_MOCKED);
		}
		return mockedUser;
	}
	
	private boolean isAdmin(){
		return user == null? false : user.isAdmin();
	}
	
	private boolean isSuperAdmin(){
		return user == null? false : user.isSuperAdmin();
	}
	
	private boolean isAdminOrSuperAdmin(){
		return this.isAdmin() || this.isSuperAdmin();
	}
	
	private boolean isGuestOrAdminOrSuperAdmin(){
		return this.isAdmin() || this.isSuperAdmin() || isGuest();
	}
	
	private boolean isGuest(){
		return user == null? false : user.isGuest();
	}
	
	
	public Collection<Organization> getOrganizations() throws Exception{
		initialize();
		Collection organizations = new ArrayList<Organization>();
		if (isAdminOrSuperAdmin()){
			OrganizationManager organizationManager = OrganizationManager.getInstance();
			organizations = organizationManager.getOrganizations();
		} 
		return organizations;
	}
}
	