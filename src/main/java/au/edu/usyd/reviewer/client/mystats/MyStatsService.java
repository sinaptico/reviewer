package au.edu.usyd.reviewer.client.mystats;

import au.edu.usyd.reviewer.client.core.DocEntry;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("mystatsService")
public interface MyStatsService extends RemoteService {

	public DocEntry getDocEntry(String docId) throws Exception;	
}
