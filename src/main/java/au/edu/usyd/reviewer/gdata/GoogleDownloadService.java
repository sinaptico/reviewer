package au.edu.usyd.reviewer.gdata;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gdata.client.GoogleAuthTokenFactory.UserToken;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.ServiceException;


import com.google.gdata.data.MediaContent;
/**
 * This class is used to download files from Google
 *
 */
public class GoogleDownloadService {

	// Google Doc Service						
    private RetryDocsService docsService;
    // Google Spreadsheet Service
    private SpreadsheetService spreadsheetsService;
    
    private String GOOGLE_SPREADSHEET_EXPORT_URL="https://spreadsheets.google.com/feeds/download/spreadsheets/Export?key=";
    

    /**
     * Constructor
     * @param docsService the service used to download documents
     * @param spreadsheetServie the service used to download spreadsheets
     */
    public GoogleDownloadService(RetryDocsService docsService, SpreadsheetService spreadsheetsService){
    	this.docsService = docsService;
    	this.spreadsheetsService = spreadsheetsService;
    }
    
    /**
     * It downloads a document on google to the file system.
     * 
     * @param entry
     *            The document entry who needs to be downloaded.
     * @param destination
     *            The location of the directory on the file sytem where the file needs to be downloaded to.
     * @throws MalformedURLException
     *             When no valid url could be created using the file entry.
     * @throws IOException
     *             When the file could not be written to the file system.
     * @throws ServiceException
     *             When an error occurred in the google service.
     */
    public void download(DocumentListEntry entry, String destination) throws MessageException {
    	try {
    		String fileExtension = destination.substring(destination.lastIndexOf(".") + 1);
    		destination = destination.substring(0,destination.lastIndexOf("."));
    		
    	    if (Constants.GOOGLE_EXPORT_TYPE_DOC.equals(fileExtension)) {
	            destination = destination + "." +  Constants.GOOGLE_EXPORT_TYPE_DOC;
	        	downloadDocument(entry, destination, Constants.GOOGLE_EXPORT_TYPE_DOC);
	        } else if (Constants.GOOGLE_EXPORT_TYPE_XLS.equals(fileExtension)) {
	        	destination = destination + "." + Constants.GOOGLE_EXPORT_TYPE_XLS;
	            downloadSpreadSheet(entry, destination, Constants.GOOGLE_EXPORT_TYPE_XLS);
	        } else if (Constants.GOOGLE_EXPORT_TYPE_PPT.equals(fileExtension)) {
	        	destination = destination + "." + Constants.GOOGLE_EXPORT_TYPE_PPT;
	            downloadDocument(entry, destination, Constants.GOOGLE_EXPORT_TYPE_PPT);
	        } else if (Constants.GOOGLE_EXPORT_TYPE_PNG.equals(fileExtension)) {
	        	destination = destination + "." + Constants.GOOGLE_EXPORT_TYPE_PNG;
	            downloadDocument(entry, destination, Constants.GOOGLE_EXPORT_TYPE_PNG);
	        } else if (Constants.GOOGLE_EXPORT_TYPE_PDF.equals(fileExtension)) { 
	        	destination = destination + "." + Constants.GOOGLE_EXPORT_TYPE_PDF;
	        	downloadDocument(entry, destination, Constants.GOOGLE_EXPORT_TYPE_PDF);
	        } else {
	        	downloadNativeFile(entry, destination + "." + fileExtension);
    		}
        } catch (Exception e) {
        	e.printStackTrace();
        	String error = String.format(Constants.EXCEPTION_GOOGLE_DOWNLOAD_FILE, destination, entry.getType());
        	throw new MessageException(error);
        }
    }

    /**
     * Download a document
     * 
     * @param entry The document entry who needs to be downloaded
     * @param filepath The destination of the file who needs to be downloaded, including the file name and extension.
     * @param exportType The export type (could be doc, rtf, ..)
     * @throws IOException When the file could not be written to the file system.
     * @throws MalformedURLException When the used url was incorrect.
     * @throws ServiceException When an excpetion occured inside the google service.
     */
    private void downloadDocument(DocumentListEntry entry, String filepath, String exportType) throws Exception {
//        String exportUrl = ((MediaContent) entry.getContent()).getUri() + "&exportFormat=" + exportType.toString();
        String exportUrl = ((MediaContent) entry.getContent()).getUri() + "&format=" + exportType.toString();
        downloadFile(new URL(exportUrl), filepath);
    }

    /**
    * Downloads a spreadSheet file using the spreadsheet service token and the spreadsheet export url.
    * 
    * @param entry The spreadsheets entry who needs to be downloaded.
    * @param filepath The destination of the file who needs to be downloaded, including the file name and extension.
    * @param exportType The export type (could be xsl, but also csv etc)
    * @throws IOException When the file could not be written to the file system.
    * @throws MalformedURLException When the used url was incorrect.
    * @throws ServiceException When a exception occured inside the google service.
    */
   private void downloadSpreadSheet(DocumentListEntry entry, String filepath, String exportType)  throws Exception {

       // For spreadsheets we need a different authorization token
       // We need to set the token from the spreadSheet service to the doc service
       UserToken docsToken = (UserToken) docsService.getAuthTokenFactory().getAuthToken();
       UserToken spreadsheetsToken = (UserToken) spreadsheetsService.getAuthTokenFactory().getAuthToken();
       docsService.setUserToken(spreadsheetsToken.getValue());

       String exportUrl = this.GOOGLE_SPREADSHEET_EXPORT_URL + entry.getDocId() + "&exportFormat=" + exportType.toString();
       downloadFile(new URL(exportUrl), filepath);

       // Restore docs token
       docsService.setUserToken(docsToken.getValue());
   }

   /**
    * Downloads a native file like a pdf (gdcos non-editable files like documents, spreadsheets, ...)
    * 
    * @param entry
    *            The entry who needs to be downloaded.
    * @param filepath
    *            The destination of the file who needs to be downloaded, including the file name and extension.
    * @throws IOException
    *             When the file could not be written to the file system.
    * @throws ServiceException
    *             When an error occured with the Google service.
    */
   private void downloadNativeFile(DocumentListEntry entry, String filepath) throws Exception {
       MediaContent mc = (MediaContent) entry.getContent();
       URL exportURL = new URL(mc.getUri());
       downloadFile(exportURL, filepath);
   }

   /**
    * Downloads the file to the file system.
    * 
    * @param exportUrl
    *            The URL of the Google document who needs to be downloaded.
    * @param filepath
    *            The destination of the file who needs to be saved, including the file name and extension.
    * @throws IOException
    *             When the file could not be written to the file system.
    * @throws MalformedURLException
    *             When the given url was incorrect.
    * @throws ServiceException
    *             When an error occurred with the Google service.
    */
   private void downloadFile(URL exportUrl, String filepath) throws Exception {
	   InputStream inStream = null;
       FileOutputStream outStream = null;
       try{
		   File file = new File(filepath);
		   file.createNewFile();
	
	       MediaContent mc = new MediaContent();
	       mc.setUri(exportUrl.toString());
	       MediaSource ms = docsService.getMedia(mc);
	   
           inStream = ms.getInputStream();
           outStream = new FileOutputStream(file);

           int c;
           while ((c = inStream.read()) != -1) {
               outStream.write(c);
           }
       } catch(Exception e){
    	   throw e;
       
       } finally {
           if (inStream != null) {
               inStream.close();
           }
           if (outStream != null) {
               outStream.flush();
               outStream.close();
           }
       }
   }
}
