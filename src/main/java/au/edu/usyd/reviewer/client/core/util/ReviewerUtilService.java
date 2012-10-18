package au.edu.usyd.reviewer.client.core.util;

import java.util.Collection;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.ui.Anchor;

@RemoteServiceRelativePath("reviewUtilService")

public interface ReviewerUtilService extends RemoteService {

	/**
	 * Returns the glosser url
	 * @param siteId site used in glosser
	 * @param docId doc id to access in glosser
	 * @return glosser url to access to document recieved as parameter
	 */
	public String getGlosserUrl(Long siteId, String docId);
	
	/**
	 * Return a collection of years. Current year and 5 years ago
	 * @return Collection of integers (years)
	 */
	public Collection<Integer> getYears();

}
