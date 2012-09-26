package au.edu.usyd.reviewer.server.rpc;

import java.security.Principal;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.client.mystats.MyStatsService;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MyStatsServiceImpl extends RemoteServiceServlet implements MyStatsService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	private AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
	// logged user
	private User user = null;
	// logged user organization
	private Organization organization = null;

	
	@Override
	public DocEntry getDocEntry(String docId) throws Exception {
		initialize();
		DocEntry docEntry = assignmentDao.loadDocEntry(docId);
		if (docEntry == null) {
			throw new Exception("Document not found");
		}
		
		if(docEntry.getOwner() != null && docEntry.getOwner().equals(getUser()) || docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(getUser())) {
			return docEntry;
		} else {
			throw new Exception("Permission denied");
		}
	}

	/**
	 * Get logger user, its organization an initialize Reviewer with it
	 */
	private void initialize() throws Exception{
		user = getUser();
		if (assignmentManager.getOrganization() == null){
			Organization organization = user.getOrganization();	
			Reviewer.initializeAssignmentManager(organization);
		}
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
				
			} else if (principal.getName() != null && !principal.getName().equals(user.getEmail())){
				user = userDao.getUserByEmail(principal.getName());
				request.getSession().setAttribute("user", user);
				
			}
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}

}
