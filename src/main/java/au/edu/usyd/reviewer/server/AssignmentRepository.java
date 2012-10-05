package au.edu.usyd.reviewer.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import glosser.gdata.GoogleDocsServiceImpl;
import glosser.gdata.GoogleSpreadsheetServiceImpl;
import glosser.gdata.GoogleUserServiceImpl;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.LogbookDocEntry;
import au.edu.usyd.reviewer.client.core.LogpageDocEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainErrorCode;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ResourceNotFoundException;
import com.google.gdata.util.ServiceException;

public class AssignmentRepository {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String SPREADSHEET_HEADER = "firstname,lastname,email,group,tutorial";
	private GoogleDocsServiceImpl googleDocsServiceImpl;
	private GoogleSpreadsheetServiceImpl googleSpreadsheetServiceImpl;
	private GoogleUserServiceImpl googleUserServiceImpl;	
	
	public AssignmentRepository(String username, String password, String domain) throws  MessageException{
		try {
			this.googleDocsServiceImpl = new GoogleDocsServiceImpl(username, password);
			this.googleSpreadsheetServiceImpl = new GoogleSpreadsheetServiceImpl(username, password);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GOOGLE_AUTHENTICATION);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GOOGLE_URL_MALFORMED);
		}
		try {
			this.googleUserServiceImpl = new GoogleUserServiceImpl(username, password, domain);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_GOOGLE_AUTHENTICATION);
		} 
	}

	public void addStudentsToSpreadsheet(Course course, Collection<UserGroup> studentGroups) throws Exception {
		SpreadsheetEntry spreadsheetEntry = googleDocsServiceImpl.getSpreadsheet(course.getSpreadsheetId());
		WorksheetEntry worksheetEntry = googleSpreadsheetServiceImpl.getSpreadsheetWorksheets(spreadsheetEntry).get(0);
		for (UserGroup studentGroup : studentGroups) {
			for (User student : studentGroup.getUsers()) {
				ListEntry listEntry = new ListEntry();
				for (String property : Arrays.copyOf(SPREADSHEET_HEADER.split(","), 3)) {
					listEntry.getCustomElements().setValueLocal(property, BeanUtils.getProperty(student, property));
				}
				listEntry.getCustomElements().setValueLocal("group", studentGroup.getName());
				listEntry.getCustomElements().setValueLocal("tutorial", studentGroup.getTutorial());
				googleSpreadsheetServiceImpl.addWorksheetRow(worksheetEntry, listEntry);
			}
		}
	}
	
	public void createActivity(Course course, WritingActivity writingActivity) throws MalformedURLException, IOException, ServiceException,MessageException {
		try{
			String folderName = writingActivity.getName() + (!writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) ? " (" + writingActivity.getTutorial() + ")" : "");
			FolderEntry folderEntry = googleDocsServiceImpl.createFolder(folderName, course.getFolderId());
			writingActivity.setFolderId(folderEntry.getResourceId());
		} catch(ResourceNotFoundException e){
			throw new MessageException(Constants.EXCEPTION_ACTIVITY_NOT_SAVED_GOOGLE_COURSE_NOT_EXIST);
		}
	}
	
	public <D extends DocEntry> D createDocument(WritingActivity writingActivity, D docEntry, Course course) throws Exception {
		// get or create document
		DocumentListEntry documentListEntry = null;
		if (docEntry.getId() == null) {
			if (writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_PRESENTATION)) {
				documentListEntry = googleDocsServiceImpl.createPresentation(docEntry.getTitle(), writingActivity.getFolderId());
			} else if ((writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_SPREADSHEET))) {
				documentListEntry = googleDocsServiceImpl.createSpreadsheet(docEntry.getTitle(), writingActivity.getFolderId());
			} else if ((writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_LOGBOOK))) {
				documentListEntry = googleDocsServiceImpl.createFolder(docEntry.getTitle(), writingActivity.getFolderId());
			} else  if ((writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_DOCUMENT))) { //WritingActivity.DOCUMENT_TYPE_DOCUMENT
				if (!writingActivity.getDocumentTemplate().equals(WritingActivity.DOCUMENT_TEMPLATE_NONE)) {
					documentListEntry = googleDocsServiceImpl.copyDocument(docEntry.getTitle(), writingActivity.getDocumentTemplate());
					documentListEntry = googleDocsServiceImpl.moveEntry(documentListEntry, writingActivity.getFolderId());
				} else {
					documentListEntry = googleDocsServiceImpl.createDocument(docEntry.getTitle(), writingActivity.getFolderId());
				}
			} 
			if ((writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_FILE_UPLOAD))) {
				docEntry.setLocalFile(true);
				docEntry.setDocumentId("file:"+course.getName()+" - Sem- "+Integer.toString(course.getSemester())+" - "+Integer.toString(course.getYear())+" - "+docEntry.getTitle());
			}else{
				docEntry.setDocumentId(documentListEntry.getResourceId());	
			}
		} else {
			documentListEntry = googleDocsServiceImpl.getDocumentListEntry(docEntry.getDocumentId());
		}
		
		// create logbook page entries
		if(writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_LOGBOOK)) {
			for (LogpageDocEntry logpageDocEntry : ((LogbookDocEntry) docEntry).getPages()) {
				if (logpageDocEntry.getId() == null) {
					DocumentListEntry newDocumentListEntry = googleDocsServiceImpl.createDocument(logpageDocEntry.getTitle(), documentListEntry.getResourceId());
					logpageDocEntry.setDocumentId(newDocumentListEntry.getResourceId());
				}
			}
		}

		// get document owners
		Collection<User> owners;
		if (docEntry.getOwner() != null) {
			owners = new LinkedList<User>();
			owners.add(docEntry.getOwner());
		} else {
			owners = docEntry.getOwnerGroup().getUsers();
		}

		// add writer permissions
		if (!(writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_FILE_UPLOAD))) {
		  List<AclEntry> aclEntries = googleDocsServiceImpl.getDocumentPermissions(documentListEntry);
		  USER_LOOP: for (User owner : owners) {
			for (AclEntry aclEntry : aclEntries) {
				if (aclEntry.getScope().getValue().equals(owner.getUsername() + "@" + googleUserServiceImpl.getDomain())) {
					googleDocsServiceImpl.updateDocumentPermission(documentListEntry, AclRole.WRITER, owner.getUsername() + "@" + googleUserServiceImpl.getDomain());
					continue USER_LOOP;
				}
			}
			googleDocsServiceImpl.addDocumentPermission(documentListEntry, AclRole.WRITER, owner.getUsername() + "@" + googleUserServiceImpl.getDomain());
		  }
		}
		return docEntry;
	}

	public User createUser(User user) throws ServiceException, IOException {
		try {
			googleUserServiceImpl.createUser(user.getUsername(), user.getFirstname(), user.getLastname(), "Changeme" + user.getUsername() + "!");
		} catch (AppsForYourDomainException e) {
			if (e.getErrorCode().equals(AppsForYourDomainErrorCode.EntityExists)) {
				// continue
			} else {
				logger.error("Failed to save user", e);
				throw e;
			}
		}
		return user;
	}
	
	public void deleteActivity(WritingActivity writingActivity) throws MalformedURLException, IOException, ServiceException {
		FolderEntry folderEntry = googleDocsServiceImpl.getFolder(writingActivity.getFolderId());
		folderEntry.delete();
	}

	public void deleteCourse(Course course) throws MalformedURLException, IOException, ServiceException {
		FolderEntry folderEntry = googleDocsServiceImpl.getFolder(course.getFolderId());
		folderEntry.delete();
	}

	public void downloadDocumentFile(DocEntry docEntry, String filePath) throws IOException, ServiceException, MessageException {
		try{
			DocumentListEntry documentListEntry = googleDocsServiceImpl.getDocument(docEntry.getDocumentId());
			googleDocsServiceImpl.downloadDocumentFile(documentListEntry, filePath);
		} catch(AuthenticationException ae){
			throw new MessageException(Constants.EXCEPTION_GOOGLE_AUTHENTICATION_);
		}
	}

	public GoogleDocsServiceImpl getGoogleDocsServiceImpl() {
		return googleDocsServiceImpl;
	}

	public GoogleSpreadsheetServiceImpl getGoogleSpreadsheetServiceImpl() {
		return googleSpreadsheetServiceImpl;
	}

	public void lockActivityDocuments(WritingActivity writingActivity) {
		try {
			List<DocumentListEntry> documentListEntries = googleDocsServiceImpl.getFolderDocuments(writingActivity.getFolderId());
			for (DocumentListEntry documentListEntry : documentListEntries) {
				try {
					List<AclEntry> aclEntries = googleDocsServiceImpl.getDocumentPermissions(documentListEntry);
					for (AclEntry aclEntry : aclEntries) {
						if (aclEntry.getRole().equals(AclRole.WRITER)) {
							aclEntry.setRole(AclRole.READER);
							aclEntry.update();
						}
					}
				} catch (Exception e) {
					logger.error("Error updating document permission.", e);
				}
			}
		} catch (Exception e) {
			logger.error("Error getting folder documents.", e);
		}
	}

	public void removeStudentsFromSpreadsheet(Course course, List<User> students) throws Exception {
		SpreadsheetEntry spreadsheetEntry = googleDocsServiceImpl.getSpreadsheet(course.getSpreadsheetId());
		WorksheetEntry worksheetEntry = googleSpreadsheetServiceImpl.getSpreadsheetWorksheets(spreadsheetEntry).get(0);
		List<ListEntry> listEntries = googleSpreadsheetServiceImpl.getWorksheetRows(worksheetEntry);
		for (User student : students) {
			for (ListEntry listEntry : listEntries) {
				if (listEntry.getCustomElements().getValue("id").equals(BeanUtils.getProperty(student, "id"))) {
					listEntry.delete();
				}
			}
		}
	}
	
	public List<DocumentListEntry> setUpFolders(Course course) throws Exception{
		// create course folder
		FolderEntry folderEntry;
		if (course.getFolderId() == null) {
			String name = course.getName() + "-sem" + course.getSemester() + "-" + course.getYear();
			folderEntry = googleDocsServiceImpl.createFolder(name);
			course.setFolderId(folderEntry.getResourceId());
		} else {
			folderEntry = googleDocsServiceImpl.getFolder(course.getFolderId());
		}		
		
		// create templates folder
		FolderEntry templatesFolderEntry;
		if (course.getTemplatesFolderId() == null) {
			String name = "templates";
			templatesFolderEntry = googleDocsServiceImpl.createFolder(name, course.getFolderId());
			course.setTemplatesFolderId(templatesFolderEntry.getResourceId());
		} else {
			templatesFolderEntry = googleDocsServiceImpl.getFolder(course.getTemplatesFolderId());
		}

		// download templates		
		List<DocumentListEntry> templates = googleDocsServiceImpl.getFolderDocuments(templatesFolderEntry);
		return templates;
	}

	public void updateCourse(Course course) throws Exception {

		// create course spreadsheet
		SpreadsheetEntry spreadsheetEntry;
		if (course.getSpreadsheetId() == null) {
			String name = "students";
			spreadsheetEntry = googleDocsServiceImpl.createSpreadsheet(name, course.getFolderId());
			googleDocsServiceImpl.uploadSpreadsheetCsv(spreadsheetEntry, SPREADSHEET_HEADER);
			course.setSpreadsheetId(spreadsheetEntry.getResourceId());
		} else {
			spreadsheetEntry = googleDocsServiceImpl.getSpreadsheet(course.getSpreadsheetId());
		}
		
		//check if lecturers are wasm users, (create passwords for non wasm users)
		for (User lecturer : course.getLecturers()){
			if (lecturer.getEmail().contains("sydney.edu.au") || lecturer.getEmail().contains("usyd.edu.au") ){
				lecturer.setWasmuser(true);
			}else{
				lecturer.setWasmuser(false);
				lecturer.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
			}
			lecturer.addRole(Constants.ROLE_ADMIN);
			lecturer.addRole(Constants.ROLE_GUEST);				
		}
		
		//check if tutors are wasm users, (create passwords for non wasm users)
		for (User tutor : course.getTutors()){
			if (tutor.getEmail().contains("sydney.edu.au") || tutor.getEmail().contains("usyd.edu.au") ){
				tutor.setWasmuser(true);
			}else{
				tutor.setWasmuser(false);
				tutor.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
			}
			tutor.addRole(Constants.ROLE_ADMIN);					
			tutor.addRole(Constants.ROLE_GUEST);
		}		

		// clear student groups
		List<UserGroup> studentGroups = new ArrayList<UserGroup>(course.getStudentGroups());
		for (UserGroup studentGroup : studentGroups) {
			studentGroup.getUsers().clear();
		}

		// get student groups from spreadsheet Reviewer.getGoogleDomain();
		WorksheetEntry worksheetEntry = googleSpreadsheetServiceImpl.getSpreadsheetWorksheets(spreadsheetEntry).get(0);

		// add students to groups
		for (ListEntry listEntry : googleSpreadsheetServiceImpl.getWorksheetRows(worksheetEntry)) {
			User student = new User();
			for (String property : Arrays.copyOf(SPREADSHEET_HEADER.split(","), 3)) {
				BeanUtils.setProperty(student, property, StringUtils.trim(listEntry.getCustomElements().getValue(property)));
			}
			UserDao userDao = UserDao.getInstance();
			// Update user information with database information.
			// I Added it because the id is null
			if (userDao.containsUser(student)){
				student = userDao.getUserByEmail(student.getEmail());
			}
			UserGroup studentGroup = new UserGroup();
			studentGroup.setName(StringUtils.trim(listEntry.getCustomElements().getValue("group")));
			studentGroup.setTutorial(StringUtils.trim(listEntry.getCustomElements().getValue("tutorial")));

			// check if tutorial is valid
			if (!course.getTutorials().contains(studentGroup.getTutorial())) {
				throw new Exception(Constants.EXCEPTION_INVALID_TUTORIAL);
			}
			
			//check if student is a wasm user, (create passwords for non wasm users)
			if (student.getEmail().contains("sydney.edu.au") || student.getEmail().contains("usyd.edu.au") ){
				student.setWasmuser(true);
			}else{
				student.setWasmuser(false);
				student.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
				student.getRole_name().add("guest");
			}
			
			// check if student group already exists
			if (studentGroups.contains(studentGroup)) {
				studentGroup = studentGroups.get(studentGroups.indexOf(studentGroup));
			} else {
				studentGroups.add(studentGroup);
				course.getStudentGroups().add(studentGroup);
			}
			studentGroup.getUsers().add(student);
		}
	}
	
	public void updateCourseDocumentPermissions(Course course) throws Exception{
		// update lecturer and tutor permissions	
		UserGroup instructors = new UserGroup();
		instructors.getUsers().addAll(course.getLecturers());
		instructors.getUsers().addAll(course.getTutors());

		DocEntry courseFolder = new DocEntry();
		courseFolder.setDocumentId(course.getFolderId());
		courseFolder.setLocked(true);
		courseFolder.setOwnerGroup(instructors);
		this.updateDocument(courseFolder);
			
		DocEntry courseSpreadsheet = new DocEntry();
		courseSpreadsheet.setDocumentId(course.getSpreadsheetId());
		courseSpreadsheet.setLocked(false);
		courseSpreadsheet.setOwnerGroup(instructors);
		this.updateDocument(courseSpreadsheet);
			
		DocEntry templatesFolder = new DocEntry();
		templatesFolder.setDocumentId(course.getTemplatesFolderId());
		templatesFolder.setLocked(false);
		templatesFolder.setOwnerGroup(instructors);
		this.updateDocument(templatesFolder);
	}
	
	public void updateDocument(DocEntry docEntry) throws MalformedURLException, IOException, ServiceException {
		// get document owners
		Collection<User> owners = new LinkedList<User>();
		if (docEntry.getOwner() != null) {
			owners.add(docEntry.getOwner());
		} else if (docEntry.getOwnerGroup() != null) {
			owners = docEntry.getOwnerGroup().getUsers();
		}
		
		// update or add permissions
		DocumentListEntry documentListEntry = googleDocsServiceImpl.getDocumentListEntry(docEntry.getDocumentId());
		AclRole newAclRole = docEntry.getLocked() ? AclRole.READER : AclRole.WRITER;
		List<AclEntry> aclEntries = googleDocsServiceImpl.getDocumentPermissions(documentListEntry);
		USER_LOOP: for (User owner : owners) {
			for (AclEntry aclEntry : aclEntries) {
				if (aclEntry.getScope().getValue().equals(owner.getUsername() + "@" + googleUserServiceImpl.getDomain())) {
					googleDocsServiceImpl.updateDocumentPermission(documentListEntry, newAclRole, owner.getUsername() + "@" + googleUserServiceImpl.getDomain());
					continue USER_LOOP;
				}
			}
			googleDocsServiceImpl.addDocumentPermission(documentListEntry, newAclRole, owner.getUsername() + "@" + googleUserServiceImpl.getDomain());
		}
		
		// delete permissions
		for (AclEntry aclEntry : aclEntries) {
			User user = new User();
			user.setEmail(aclEntry.getScope().getValue());
			//user.setUsername(StringUtils.substringBefore(aclEntry.getScope().getValue(), "@" + googleUserServiceImpl.getDomain()));
			if (!owners.contains(user) && aclEntry.getRole().equals(AclRole.WRITER)) {
				if (docEntry instanceof LogpageDocEntry) {
					googleDocsServiceImpl.updateDocumentPermission(documentListEntry, AclRole.READER, user.getId() + "@" + googleUserServiceImpl.getDomain());
				}else{
					aclEntry.delete();
				}
			}
		}	
	}	
}
