package au.edu.usyd.reviewer.client.admin.glosser;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GlosserServiceAsync {
	public void deleteSites(List<SiteForm> siteForms, AsyncCallback<Void> callback);

	public void getAllSites(AsyncCallback<List<SiteForm>> callback);

	public void getToolList(AsyncCallback<List<String>> callback);

	public void saveOrUpdateSite(SiteForm siteForm, AsyncCallback<Void> callback);

	public void saveSite(AsyncCallback<SiteForm> callback);
}
