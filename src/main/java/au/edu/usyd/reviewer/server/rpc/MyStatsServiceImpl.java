package au.edu.usyd.reviewer.server.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.mystats.MyStatsService;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.util.CloneUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MyStatsServiceImpl extends RemoteServiceServlet implements MyStatsService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	private AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
	
	@Override
	public DocEntry getDocEntry(String docId) throws Exception {
		DocEntry docEntry = assignmentDao.loadDocEntry(docId);
		if (docEntry == null) {
			throw new Exception("Document not found");
		}
		
		if(docEntry.getOwner() != null && docEntry.getOwner().equals(getUser()) || docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(getUser())) {
			return CloneUtil.clone(docEntry);
		} else {
			throw new Exception("Permission denied");
		}
	}

	public User getUser() throws Exception {
		User user = (User) this.getThreadLocalRequest().getSession().getAttribute("user");
		if (user == null) {
			throw new Exception("Your session has expired. Please login again.");
		} else {
			return user;
		}
	}

}
