package au.edu.usyd.reviewer.client.core.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;

public class UrlLib {
	 
	// Service to get Glosser url
	private static ReviewerUtilServiceAsync reviewerUtilService = (ReviewerUtilServiceAsync) GWT.create(ReviewerUtilService.class);
	
	/**
	 * Document url.
	 *
	 * @param id the id
	 * @param domain the domain
	 * @return the string
	 */
	public static String documentUrl(String id, String domain) {
		return "http://docs.google.com/a/" + domain + "/document/edit?id=" + id.replace("document:", "");
	}

/**
 * Folder url.
 *
 * @param id the id
 * @param domain the domain
 * @return the string
 */
public static String folderUrl(String id, String domain) {
		return "http://docs.google.com/a/" + domain + "/#folders/" + id.replace(":", ".0.");
	}

	/**
	 * Glosser url.
	 *
	 * @param siteId the site id
	 * @param docId the doc id
	 * @return the string
	 */
	//public static String glosserUrl(Long siteId, String docId) {
    public static void glosserUrl(Anchor glosserLink, Long siteId, String docId){
//		return "http://129.78.13.24:8080/glosser/siteauth.htm?siteId=" + siteId + "&docId=" + docId;
    	//return "http://"+getGlosserHost()+":"+getGlosserHost()+"/glosser/siteauth.htm?siteId=" + siteId + "&docId=" + docId;
    	getGlosserUrl(glosserLink, siteId, docId);
	}

	/**
	 * Pdf download url.
	 *
	 * @param filename the filename
	 * @param docId the doc id
	 * @param deadlineId the deadline id
	 * @return the string
	 */
	public static String pdfDownloadUrl(String filename, String docId, Long deadlineId) {		
		return "file/" + filename + "?docId=" + docId + "&docVersion=" + deadlineId;
	}
	
	/**
	 * Pdf download url.
	 *
	 * @param filename the filename
	 * @param docId the doc id
	 * @return the string
	 */
	public static String pdfDownloadUrl(String filename, String docId) {		
		return "file/" + filename + "?docId=" + docId +"&fileType=uploaded";
	}	

	/**
	 * Presentation url.
	 *
	 * @param id the id
	 * @param domain the domain
	 * @return the string
	 */
	public static String presentationUrl(String id, String domain) {
		return "http://docs.google.com/a/" + domain + "/presentation/edit?id=" + id.replace("presentation:", "");
	}

	/**
	 * Spreadsheet url.
	 *
	 * @param id the id
	 * @param domain the domain
	 * @return the string
	 */
	public static String spreadsheetUrl(String id, String domain) {			
		return "http://spreadsheets.google.com/a/" + domain + "/ccc?key=" + id.replace("spreadsheet:", "");
	}

	/**
	 * Zip download url.
	 *
	 * @param filename the filename
	 * @param deadlineId the deadline id
	 * @param tutorial the tutorial
	 * @return the string
	 */
	public static String zipDownloadUrl(String filename, Long deadlineId, String tutorial) {		
		return "file/" + filename + "?docVersion=" + deadlineId + "&tutorial=" + tutorial;
	}

	/**
	 * Zip download url.
	 *
	 * @param filename the filename
	 * @param deadlineId the deadline id
	 * @param tutorial the tutorial
	 * @param reviewingActivity the reviewing activity
	 * @return the string
	 */
	public static String zipDownloadUrl(String filename, Long deadlineId, String tutorial, String reviewingActivity) {
		return "file/" + filename + "?docVersion=" + deadlineId + "&tutorial=" + tutorial +"&review="+reviewingActivity;
	}
    
    private static void getGlosserUrl(final Anchor glosserLink, Long siteId, String docId){
    	reviewerUtilService.getGlosserUrl(siteId, docId, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
			    Window.alert("Failed to get Glosser's Host: " + caught.getMessage());
			}
			public void onSuccess(String result) {
				glosserLink.setHref(result);
			}
		});
	}		
}
