package au.edu.usyd.reviewer.client.mystats;

import au.edu.usyd.reviewer.client.core.DocEntry;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MyStatsServiceAsync {

	public void getDocEntry(String docId, AsyncCallback<DocEntry> asyncCallback);

}
