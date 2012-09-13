package au.edu.usyd.reviewer.client.core.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ReviewerPropertiesServiceAsync {

	void getGlosserUrl(Long siteId, String docId, AsyncCallback<String> callback);
}