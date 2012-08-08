package au.edu.usyd.reviewer.client.core.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ReviewerPropertiesServiceAsync {

	void getGlosserHost(AsyncCallback callback);
	void getGlosserPort(AsyncCallback callback);
	

}
