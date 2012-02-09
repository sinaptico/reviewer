package au.edu.usyd.reviewer.client.admin.glosser;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("glosserService")
public interface GlosserService extends RemoteService {
	public void deleteSites(List<SiteForm> siteForms);

	public List<SiteForm> getAllSites();

	public List<String> getToolList();

	public void saveOrUpdateSite(SiteForm siteForm);

	public SiteForm saveSite() throws SiteException;
}
