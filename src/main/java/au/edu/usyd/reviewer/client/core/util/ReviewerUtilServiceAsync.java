package au.edu.usyd.reviewer.client.core.util;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ReviewerUtilServiceAsync {

	/**
	 * Returns the glosser url
	 * @param siteId site used in glosser
	 * @param docId doc id to access in glosser
	 * @return callback is the glosser url
	 */
	void getGlosserUrl(Long siteId, String docId, AsyncCallback<String> callback);
	
	/**
	 * Returns a collection of integers with the current year and 5 years ago.
	 * @param callback is the collection of integer
	 */
	public void getYears(AsyncCallback<Collection<Integer>> callback);

}