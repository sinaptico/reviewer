package au.edu.usyd.reviewer.client.core.util;

public class UrlLib {
	 
	// Glosser host and port
	private static String glosserHost = null;
	private static String glosserPort = null;
	private static ReviewerPropertiesServiceAsync reviewerPropertiesService = (ReviewerPropertiesServiceAsync) GWT.create(ReviewerPropertiesService.class);
	
	//protected static String domain = "iwrite.eng.usyd.edu.au";
	
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

//	public static String entryUrl(String id) {		
//		if (id.startsWith("document:")) {
//			return UrlLib.documentUrl(id);
//		} else if (id.startsWith("presentation:")) {
//			return UrlLib.presentationUrl(id);
//		} else if (id.startsWith("spreadsheet:")) {
//			return UrlLib.spreadsheetUrl(id);
//		} else if (id.startsWith("folder:")) {
//			return UrlLib.folderUrl(id);
//		} else {
//			return null;
//		}
//	}

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
	public static String glosserUrl(Long siteId, String docId) {		
//		return "http://129.78.13.24:8080/glosser/siteauth.htm?siteId=" + siteId + "&docId=" + docId;
		return "http://"+getGlosserHost()+":"+getGlosserPort()+"/glosser/siteauth.htm?siteId=" + siteId + "&docId=" + docId;
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
    
    private static String getGlosserHost(){
		if (glosserHost == null){
			reviewerPropertiesService.getGlosserHost(new AsyncCallback() {
			    public void onFailure(Throwable caught) {
			    	Window.alert("Failed to get Glosser's Host: " + caught.getMessage());
			    }
			    public void onSuccess(Object result) {
			    	Window.alert("onSuccess Glosser's Host: " + result + " result " + (String) result);
			    	glosserHost = (String) result;
			    }
			  });
		}
		return glosserHost;
	}
		
	
	private static String getGlosserPort(){
		if (glosserPort == null){
			reviewerPropertiesService.getGlosserPort(new AsyncCallback() {
			    public void onFailure(Throwable caught) {
			    	Window.alert("Failed to get Glosser's Port: " + caught.getMessage());
			    }
			    public void onSuccess(Object result) {
			    	Window.alert("onSuccess Glosser's Port: " + result + " result " + (String) result);
			    	glosserPort = (String) result;
			    	
			    }
			  });
		}
		return glosserPort;
	}

}
