package au.edu.usyd.reviewer.client.core.util;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("reviewPropertiesService")

public interface ReviewerPropertiesService extends RemoteService {
	
	public String getGlosserHost();
	
	public String getGlosserPort();

}
