package au.edu.usyd.reviewer.client.core.util;

/**
 * <p>Class with URL format methods; Includes:</p>
 * <ul>
 * 	<li>Document, Presentation, Spreadsheets URLs.</li>  
 *  <li>Folder URL.</li>
 *  <li>Download PDF and ZIP files URL.</li>
 *  <li>Linkt to Glosser.</li>
 * </ul>
 */
public class UrlLib {
	
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
		return "http://129.78.13.24:8080/glosser/siteauth.htm?siteId=" + siteId + "&docId=" + docId;
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



}
