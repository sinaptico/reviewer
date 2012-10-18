package au.edu.usyd.reviewer.server.rpc;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import au.edu.usyd.reviewer.client.core.util.ReviewerUtilService;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.util.CalendarUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ReviewerUtilServiceImpl extends RemoteServiceServlet implements ReviewerUtilService {

	@Override
	public String getGlosserUrl(Long siteId, String docId) {
		return Reviewer.getGlosserUrl(siteId, docId);
	}
	
	public Collection<Integer> getYears(){
		return CalendarUtil.getYears();
	}

}
