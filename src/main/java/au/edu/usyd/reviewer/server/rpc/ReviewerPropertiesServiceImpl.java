package au.edu.usyd.reviewer.server.rpc;


import au.edu.usyd.reviewer.client.core.util.ReviewerPropertiesService;
import au.edu.usyd.reviewer.server.Reviewer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ReviewerPropertiesServiceImpl extends RemoteServiceServlet implements ReviewerPropertiesService {

	@Override
	public String getGlosserUrl(Long siteId, String docId) {
		return Reviewer.getGlosserUrl(siteId, docId);
	}

}
