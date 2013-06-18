/*******************************************************************************
 * Copyright 2010, 2011. Stephen O'Rouke. The University of Sydney
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * - Contributors
 * 	Stephen O'Rourke
 * 	Jorge Villalon (CMM)
 * 	Ming Liu (AQG)
 * 	Rafael A. Calvo
 * 	Marco Garcia
 ******************************************************************************/
package au.edu.usyd.reviewer.gdata;

import com.google.gdata.data.BaseEntry;

import com.google.gdata.data.MediaContent;
import com.google.gdata.data.OutOfLineContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.*;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Implements interaction with Google services using gdata API. {@link http://code.google.com/apis/documents/}
 * <p>
 * Documents are accessed using the Access Control List (ACL). From java.security.acl:
 * An ACL can be thought of as a data structure with multiple ACL entries. 
 * Each ACL entry, of interface type AclEntry, contains a set of permissions associated with a particular principal. 
 * (A principal represents an entity such as an individual user or a group). 
 * Additionally, each ACL entry is specified as being either positive or negative. If positive, the permissions are to be granted to the associated principal. 
 * If negative, the permissions are to be denied.
 * @author rafa
 *
 */
public class GoogleDocsServiceImpl {

    private static final String DOCUMENT_FEEDS_URL = "https://docs.google.com/feeds/default/private/full/";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private RetryDocsService docsService;
	private int maxRetryAttempts = 5;

	public GoogleDocsServiceImpl(String sessionToken) throws AuthenticationException, MalformedURLException {
		setAuthSubToken(sessionToken);
	}

	public GoogleDocsServiceImpl(String username, String password) throws AuthenticationException, MalformedURLException {
		setUserCredentials(username, password);
	}

	public AclEntry addDocumentPermission(DocumentListEntry entry, AclRole aclRole, String user) throws IOException, ServiceException {
		logger.info("MARIELA - Adding document permission: " + entry.getResourceId() + ", role=" + aclRole.getValue() + ", user=" + user);
		AclEntry aclEntry = new AclEntry();
		AclScope aclScope = new AclScope(AclScope.Type.USER, user);
		aclEntry.setRole(aclRole);
		aclEntry.setScope(aclScope);
		return docsService.insert(new URL(entry.getAclFeedLink().getHref()), aclEntry);
	}

	public DocumentEntry copyDocument(String newTitle, DocumentEntry documentEntry) throws MalformedURLException, IOException, ServiceException {
		return copyDocument(newTitle, documentEntry.getResourceId());
	}

	public DocumentEntry copyDocument(String newTitle, String documentResourceId) throws MalformedURLException, IOException, ServiceException {
		DocumentEntry newDocumentEntry = new DocumentEntry();
		newDocumentEntry.setId(DOCUMENT_FEEDS_URL + documentResourceId);
		newDocumentEntry.setTitle(new PlainTextConstruct(newTitle));
		newDocumentEntry.setWritersCanInvite(false);
		newDocumentEntry.setHidden(false);
		return createDocument(newDocumentEntry, new URL(DOCUMENT_FEEDS_URL));
	}

	public DocumentEntry createDocument(DocumentEntry documentEntry, URL url) throws IOException, ServiceException {
//		logger.info("Creating document titled: " + documentEntry.getTitle().getPlainText());
		return docsService.insert(url, documentEntry);
	}
	
	public DocumentListEntry createDocument(File file, String title) throws IOException, ServiceException {
		  String mimeType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();

		  DocumentListEntry newDocument = new DocumentListEntry();
		  newDocument.setFile(file, mimeType);
		  newDocument.setTitle(new PlainTextConstruct(title));

		  // Prevent collaborators from sharing the document with others?
		  // newDocument.setWritersCanInvite(false);
		  
		  //return docsService.insert(new URL("https://docs.google.com/feeds/default/private/full/?convert=false"), newDocument);
		  return docsService.insert(new URL(DOCUMENT_FEEDS_URL), newDocument);
	}	

	public DocumentEntry createDocument(String title) throws IOException, ServiceException {
		return createDocument(title, new URL(DOCUMENT_FEEDS_URL));
	}

	public DocumentEntry createDocument(String title, FolderEntry folderEntry) throws IOException, ServiceException {
		return createDocument(title, new URL(((MediaContent) folderEntry.getContent()).getUri()));
	}

	public DocumentEntry createDocument(String title, String folderRecourseId) throws IOException, ServiceException {
		return createDocument(title, new URL(DOCUMENT_FEEDS_URL + folderRecourseId + "/contents"));
	}

	public DocumentEntry createDocument(String title, URL url) throws IOException, ServiceException {
		DocumentEntry documentEntry = new DocumentEntry();
		documentEntry.setTitle(new PlainTextConstruct(title));
		documentEntry.setWritersCanInvite(false);
		documentEntry.setHidden(false);
		return createDocument(documentEntry, url);
	}

	public FolderEntry createFolder(String title) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Creating folder titled: " + title);
		FolderEntry folderEntry = new FolderEntry();
		folderEntry.setTitle(new PlainTextConstruct(title));
		return docsService.insert(new URL(DOCUMENT_FEEDS_URL), folderEntry);
	}

	public FolderEntry createFolder(String title, String folderRecourseId) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Creating folder titled: " + title);
		FolderEntry folderEntry = new FolderEntry();
		folderEntry.setTitle(new PlainTextConstruct(title));
		return docsService.insert(new URL(DOCUMENT_FEEDS_URL + folderRecourseId + "/contents"), folderEntry);
	}

	public PresentationEntry createPresentation(String title) throws MalformedURLException, IOException, ServiceException {
		return createPresentation(title, new URL(DOCUMENT_FEEDS_URL));
	}

	public PresentationEntry createPresentation(String title, DocumentListEntry folderEntry) throws MalformedURLException, IOException, ServiceException {
		return createPresentation(title, new URL(((MediaContent) folderEntry.getContent()).getUri()));
	}

	public PresentationEntry createPresentation(String title, String folderRecourseId) throws MalformedURLException, IOException, ServiceException {
		return createPresentation(title, new URL(DOCUMENT_FEEDS_URL + folderRecourseId + "/contents"));
	}

	public PresentationEntry createPresentation(String title, URL url) throws IOException, ServiceException {
//		logger.info("Creating presentation titled: " + title);
		PresentationEntry presentationEntry = new PresentationEntry();
		presentationEntry.setTitle(new PlainTextConstruct(title));
		presentationEntry.setWritersCanInvite(false);
		presentationEntry.setHidden(false);
		return docsService.insert(url, presentationEntry);
	}

	public SpreadsheetEntry createSpreadsheet(String title) throws MalformedURLException, IOException, ServiceException {
		return createSpreadsheet(title, new URL(DOCUMENT_FEEDS_URL));
	}

	public SpreadsheetEntry createSpreadsheet(String title, FolderEntry folderEntry) throws MalformedURLException, IOException, ServiceException {
		return createSpreadsheet(title, new URL(((MediaContent) folderEntry.getContent()).getUri()));
	}

	public SpreadsheetEntry createSpreadsheet(String title, String folderRecourseId) throws MalformedURLException, IOException, ServiceException {
		return createSpreadsheet(title, new URL(DOCUMENT_FEEDS_URL + folderRecourseId + "/contents"));
	}

	public SpreadsheetEntry createSpreadsheet(String title, URL url) throws IOException, ServiceException {
//		logger.info("Creating spreadsheet titled: " + title);
		SpreadsheetEntry spreadsheetEntry = new SpreadsheetEntry();
		spreadsheetEntry.setTitle(new PlainTextConstruct(title));
		spreadsheetEntry.setWritersCanInvite(false);
		spreadsheetEntry.setHidden(false);
		return docsService.insert(url, spreadsheetEntry);
	}

//	public void delete(DocumentListEntry documentListEntry) throws IOException, ServiceException {
//		logger.info("Deleting document: " + documentListEntry.getResourceId());
//		docsService.delete(new URL(DOCUMENT_FEEDS_URL + documentListEntry.getResourceId()), "*");
//	}

	public void deleteDocumentPermission(DocumentListEntry entry, String user) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Deleting document permission: " + entry.getResourceId() + ", user=" + user);
		List<AclEntry> aclEntries = getDocumentPermissions(entry);
		for (AclEntry aclEntry : aclEntries) {
			if (aclEntry.getScope().getValue().equals(user)) {
				aclEntry.setEtag("*");
				aclEntry.delete();
				return;
			}
		}
	}


	public String downloadDocumentHtml(BaseEntry<?> entry) throws IOException, ServiceException {
		MediaContent mc = new MediaContent();
		mc.setUri(((OutOfLineContent) entry.getContent()).getUri() + "&format=html");
		MediaSource ms = docsService.getMedia(mc);
		BufferedReader reader = new BufferedReader(new InputStreamReader(ms.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} finally {
			reader.close();
		}
		return sb.toString();
	}

	public DocumentEntry getDocument(String id) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Getting document: " + id);
		DocumentEntry entry = docsService.getEntry(new URL(DOCUMENT_FEEDS_URL + id), DocumentEntry.class);
		return entry;
	}

	public DocumentListEntry getDocumentListEntry(String id) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Getting document: " + id);
		DocumentListEntry entry = docsService.getEntry(new URL(DOCUMENT_FEEDS_URL + id), DocumentListEntry.class);
		return entry;
	}

	public List<AclEntry> getDocumentPermissions(DocumentListEntry entry) throws MalformedURLException, IOException, ServiceException {
		AclFeed aclFeed = docsService.getFeed(new URL(entry.getAclFeedLink().getHref()), AclFeed.class);
		return aclFeed.getEntries();
	}

	public List<RevisionEntry> getDocumentRevisions(DocumentListEntry documentListEntry) throws MalformedURLException, IOException, ServiceException {
		return getDocumentRevisions(documentListEntry.getResourceId());
	}

	public List<RevisionEntry> getDocumentRevisions(String id) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Getting document revisions: " + id);
		RevisionFeed feed = docsService.getFeed(new URL(DOCUMENT_FEEDS_URL + id + "/revisions"), RevisionFeed.class);
		return feed.getEntries();
	}

	public List<DocumentListEntry> getDocuments() throws MalformedURLException, IOException, ServiceException {
		return getDocuments(new URL(DOCUMENT_FEEDS_URL + "-/document"));
	}

	public List<DocumentListEntry> getDocuments(URL url) throws MalformedURLException, IOException, ServiceException {
		DocumentListFeed feed = docsService.getFeed(url, DocumentListFeed.class);
		List<DocumentListEntry> entries = feed.getEntries();
		while (feed.getNextLink() != null) {
			feed = docsService.getFeed(new URL(feed.getNextLink().getHref()), DocumentListFeed.class);
			entries.addAll(feed.getEntries());
		}
		return entries;
	}

	public List<DocumentListEntry> getDocumentsWhereUserPermission(String userId, AclRole aclRole) throws IOException, ServiceException {
		return getDocuments(new URL(DOCUMENT_FEEDS_URL + "-/document?" + aclRole.getValue() + "=" + URLEncoder.encode(userId, "UTF-8")));
	}

	public FolderEntry getFolder(String id) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Getting folder: " + id);
		FolderEntry entry = docsService.getEntry(new URL(DOCUMENT_FEEDS_URL + id), FolderEntry.class);
		return entry;
	}

	public List<DocumentListEntry> getFolderDocuments(DocumentListEntry folderEntry) throws IOException, ServiceException {
		return getDocuments(new URL(((MediaContent) folderEntry.getContent()).getUri()));
	}

	public List<DocumentListEntry> getFolderDocuments(String folderRecourseId) throws IOException, ServiceException {
		return getDocuments(new URL(DOCUMENT_FEEDS_URL + folderRecourseId + "/contents"));
	}

	public int getMaxRetryAttempts() {
		return maxRetryAttempts;
	}

	public PresentationEntry getPresentation(String id) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Getting presentation: " + id);
		PresentationEntry entry = docsService.getEntry(new URL(DOCUMENT_FEEDS_URL + id), PresentationEntry.class);
		return entry;
	}

	public SpreadsheetEntry getSpreadsheet(String id) throws MalformedURLException, IOException, ServiceException {
//		logger.info("Getting spreadsheet: " + id);
		SpreadsheetEntry entry = docsService.getEntry(new URL(DOCUMENT_FEEDS_URL + id), SpreadsheetEntry.class);
		return entry;
	}

	public List<DocumentListEntry> getUserDocuments(String user) throws MalformedURLException, IOException, ServiceException {
		return getDocuments(new URL(DOCUMENT_FEEDS_URL + "-/document/" + user));
	}

	public boolean hasDocumentPermission(String docId, String userId, AclRole aclRole) throws IOException, ServiceException {
		List<DocumentListEntry> entries = getDocuments(new URL(DOCUMENT_FEEDS_URL + "-/" + docId + "?" + aclRole.getValue() + "=" + URLEncoder.encode(userId, "UTF-8")));
		for (DocumentListEntry entry : entries) {
			if (entry.getResourceId().equals(docId)) {
				return true;
			}
		}
		return false;
	}

	public <D extends DocumentListEntry> D moveEntry(D documentListEntry, String folderRecourseId) throws MalformedURLException, IOException, ServiceException {
		return docsService.insert(new URL(DOCUMENT_FEEDS_URL + folderRecourseId + "/contents"), documentListEntry);
	}

	public void setAuthSubToken(String sessionToken) throws AuthenticationException, MalformedURLException {
		docsService = new RetryDocsService("AuthSub - Docs Service");
        docsService.setMaxRetryAttempts(maxRetryAttempts);
		docsService.setAuthSubToken(sessionToken);
	}

	public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
		docsService.setMaxRetryAttempts(maxRetryAttempts);
	}

	public void setUserCredentials(String username, String password) throws AuthenticationException, MalformedURLException {
		docsService = new RetryDocsService("Client - Docs Service");
		docsService.setAuthSubToken(null);
        docsService.setMaxRetryAttempts(maxRetryAttempts);
		docsService.setUserCredentials(username, password);
	}

	public void updateAclEntry(AclEntry aclEntry) throws MalformedURLException, IOException, ServiceException {
		aclEntry.setEtag("*");
		docsService.update(new URL(aclEntry.getEditLink().getHref()), aclEntry);
	}
	
	public boolean updateDocumentPermission(DocumentListEntry entry, AclRole aclRole, String user) throws MalformedURLException, IOException, ServiceException {
		logger.info("MARIELA - Updating document permission: " + entry.getResourceId() + ", user=" + user + " role " + aclRole.getValue());
		List<AclEntry> aclEntries = getDocumentPermissions(entry);
		for (AclEntry aclEntry : aclEntries) {
			if (aclEntry.getScope().getValue().equals(user)) {
				aclEntry.setRole(aclRole);
				updateAclEntry(aclEntry);
				return true;
			}
		}
		return false;
	}

	public <E extends DocumentListEntry> E uploadDocumentContent(E entry, File file) throws MalformedURLException, IOException, ServiceException {
		String mimeType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();
		MediaFileSource media = new MediaFileSource(file, mimeType);
		media.setEtag("*");
		entry = (E) docsService.updateMedia(new URL(entry.getMediaEditLink().getHref()), entry.getClass(), media);
		return entry;
	}

	public <E extends DocumentListEntry> E uploadDocumentContent(E entry, String content, String mimeType) throws MalformedURLException, IOException, ServiceException {
		MediaByteArraySource media = new MediaByteArraySource(content.getBytes(), mimeType);
		media.setEtag("*");
		entry = (E) docsService.updateMedia(new URL(entry.getMediaEditLink().getHref()), entry.getClass(), media);
		return entry;
	}

	public DocumentEntry uploadDocumentHtml(DocumentEntry documentEntry, String html) throws MalformedURLException, IOException, ServiceException {
		return uploadDocumentContent(documentEntry, html, "text/html");
	}

	public SpreadsheetEntry uploadSpreadsheetCsv(SpreadsheetEntry spreadsheetEntry, String csv) throws MalformedURLException, IOException, ServiceException {
		return uploadDocumentContent(spreadsheetEntry, csv, "text/csv");
	}

	public RetryDocsService getDocsService(){ 
		return docsService;
	}
	
	public void updateCourseFolderName(FolderEntry folderEntry, String newTitle) throws MalformedURLException, IOException, ServiceException {
		  DocumentListEntry entry =  this.getDocumentListEntry(folderEntry.getDocId());
		  entry.setTitle(new PlainTextConstruct(newTitle));
		  DocumentListEntry updatedEntry =  docsService.update(new URL(entry.getEditLink().getHref()), entry);
	}
}
