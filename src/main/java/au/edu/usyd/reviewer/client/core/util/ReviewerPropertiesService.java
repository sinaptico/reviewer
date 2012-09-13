package au.edu.usyd.reviewer.client.core.util;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.ui.Anchor;

@RemoteServiceRelativePath("reviewPropertiesService")

public interface ReviewerPropertiesService extends RemoteService {
	
	public String getGlosserUrl(Long siteId, String docId);
}
