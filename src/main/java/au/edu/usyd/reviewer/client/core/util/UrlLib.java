package au.edu.usyd.reviewer.client.core.util;

public class UrlLib {
	
	//protected static String domain = "iwrite.eng.usyd.edu.au";
	
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

	public static String folderUrl(String id, String domain) {
		return "http://docs.google.com/a/" + domain + "/#folders/" + id.replace(":", ".0.");
	}

	public static String glosserUrl(Long siteId, String docId) {		
		return "http://129.78.13.24:8080/glosser/siteauth.htm?siteId=" + siteId + "&docId=" + docId;
	}

	public static String pdfDownloadUrl(String filename, String docId, Long deadlineId) {		
		return "file/" + filename + "?docId=" + docId + "&docVersion=" + deadlineId;
	}
	
	public static String pdfDownloadUrl(String filename, String docId) {		
		return "file/" + filename + "?docId=" + docId +"&fileType=uploaded";
	}	

	public static String presentationUrl(String id, String domain) {
		return "http://docs.google.com/a/" + domain + "/present/edit?id=" + id.replace("presentation:", "");
	}

	public static String spreadsheetUrl(String id, String domain) {			
		return "http://spreadsheets.google.com/a/" + domain + "/ccc?key=" + id.replace("spreadsheet:", "");
	}

	public static String zipDownloadUrl(String filename, Long deadlineId, String tutorial) {		
		return "file/" + filename + "?docVersion=" + deadlineId + "&tutorial=" + tutorial;
	}

	public static String zipDownloadUrl(String filename, Long deadlineId, String tutorial, String reviewingActivity) {
		return "file/" + filename + "?docVersion=" + deadlineId + "&tutorial=" + tutorial +"&review="+reviewingActivity;
	}



}
