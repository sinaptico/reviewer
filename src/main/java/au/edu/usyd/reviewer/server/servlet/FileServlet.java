package au.edu.usyd.reviewer.server.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;
import au.edu.usyd.reviewer.server.util.FileUtil;

public class FileServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = null;
	private AssignmentDao assignmentDao = null;
	private static String UPLOAD_DIRECTORY = null;	
	private static String EMPTY_FILE = null;
	private User user;
	private Organization organization;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = null;
		DataInputStream in = null;
		try {
			initialize(request,response);
		
			String docId = request.getParameter("docId");
			String docVersion = request.getParameter("docVersion");
			String tutorial = request.getParameter("tutorial");
			String fileType = request.getParameter("fileType");
			String reviewingActivity = request.getParameter("review");
			
			DocEntry docEntry = null;
	
			File file = null;
			String filename = null;
			if (docVersion != null) {
				Deadline deadline = assignmentDao.loadDeadline(Long.valueOf(docVersion));
				if (docId != null) {
					// get document course and activity
					docEntry = assignmentDao.loadDocEntry(docId);
					WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(docEntry);
					Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
	
					// check if user owns the document or is a lecturer or tutor
					if ((docEntry.getOwner() != null && docEntry.getOwner().equals(user)) || 
						(docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(user)) || 
						(course.getLecturers().contains(user) || course.getTutors().contains(user))) {
						file = new File(assignmentManager.getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), WritingActivity.TUTORIAL_ALL, organization) + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf");
						filename = docEntry.getTitle() + " - " + deadline.getName() + ".pdf";
					} else {
						// check if user is a reviewer of a document
						ReviewEntry reviewEntry = assignmentDao.loadReviewEntryWhereDocEntryAndOwner(docEntry, user);
						if (reviewEntry != null && reviewEntry.getOwner().equals(user)) {
							file = new File(assignmentManager.getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), WritingActivity.TUTORIAL_ALL, organization) + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf");
							filename = docEntry.getTitle() + " - " + deadline.getName() + ".pdf";
						}
					}
				} else if (tutorial != null) {
					// get course and activity
					WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDeadline(deadline);
					Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
	
					// check if user is a lecturer or tutor of the course
					if (course.getLecturers().contains(user)|| course.getTutors().contains(user)) {
						// check tutorial value
						if (writingActivity.getTutorial().equals(tutorial) || course.getTutorials().contains(tutorial) && writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL)) {
							
							if (reviewingActivity!=null){
								file = new File(assignmentManager.getDocumentsFolder(course.getId(), Long.valueOf(reviewingActivity), deadline.getId(), tutorial, organization) + ".zip");
							}else{
								file = new File(assignmentManager.getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), tutorial, organization) + ".zip");
							}
							
							filename = writingActivity.getName() + " (" + tutorial + ") - " + deadline.getName() + ".zip";
						}
					}
				}
			}
	
			if (StringUtils.equals(fileType, "uploaded")){
				// check if user is a lecturer or tutor of the course
				if (docId != null) {				
					docEntry = assignmentDao.loadDocEntry(docId);
					WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(docEntry);
					Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
					   // check if user is a lecturer or tutor of the course
					if (docEntry.getOwner() != null && docEntry.getOwner().equals(user) || docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(user) || course.getLecturers().contains(user) || course.getTutors().contains(user)) {
							filename = docEntry.getFileName();
							file = new File(UPLOAD_DIRECTORY +"/"+filename);
						}		
				}
			}
			
			// serve file
			if ((file != null)) {
				out = response.getOutputStream();
				if (file.exists() && (filename != null)) {
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + FileUtil.escapeFilename(filename) + "\"");			
					response.setContentLength((int) file.length());
					logger.info("Serving file: " + file.getAbsolutePath());
					int length = 0;
					byte[] bbuf = new byte[1024];
					in = new DataInputStream(new FileInputStream(file));
					while ((in != null) && ((length = in.read(bbuf)) != -1)) {
						out.write(bbuf, 0, length);
					}
					in.close();
				}else{ //empty file				
					file = new File(EMPTY_FILE);				
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + FileUtil.escapeFilename("Empty.pdf") + "\"");			
					response.setContentLength((int) file.length());
//					logger.info("Serving empty file: " + file.getAbsolutePath());
					int length = 0;
					byte[] bbuf = new byte[1024];
					in = new DataInputStream(new FileInputStream(file));
					while ((in != null) && ((length = in.read(bbuf)) != -1)) {
						out.write(bbuf, 0, length);
					}
					in.close();				
				}
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			try{
				if (out != null){
					out.close();
				}
				if (in != null){
					in.close();
				}
			} catch(Exception closeException){
				e.printStackTrace();
			}
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		try {
			initialize(req, resp);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException(e1.getMessage());
		}
		String docId = null;
		String csv = null;
		
//		logger.info("User uploading file: " + user.getEmail());      			
        // process only multipart requests
        if (ServletFileUpload.isMultipartContent(req)) {
//        	logger.info("Multipart Content ");

            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            // Parse the request
            try {            	
//            	logger.info("Starting to parse request. " + req.toString());
                @SuppressWarnings("rawtypes")
				Iterator items = upload.parseRequest(req).iterator();
//              logger.info("Parsing request... "+items.toString());
                while (items.hasNext()) {            	
                	  FileItem item = (FileItem) items.next();
                	  if (item.isFormField()) {
//                		  logger.info("item.getFieldName: " +item.getFieldName());
                          if (item.getFieldName().equals("docId")) {
                              docId = item.getString();
                          }
                          if (item.getFieldName().equals("csv")) {
                              csv = item.getString();
                          }                          
                          continue;
                      }            
                	
//                		logger.info("Attempting to upload file into docId: " + docId);
                	
	          			if (docId != null) {
		      				// get document course and activity
		      				DocEntry docEntry = assignmentDao.loadDocEntry(docId);
		      				WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(docEntry);
		      				Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
	      
	//      				    logger.info("Owner: " + docEntry.getOwner());      				    	  
		      				// check if user owns the document or is a lecturer or tutor
		      				if (docEntry.getOwner() != null && docEntry.getOwner().equals(user) || docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(user) || course.getLecturers().contains(user) || course.getTutors().contains(user)) {
	
		                        // get only the file name not whole path
		                        String fileName = item.getName();
		                        String extension = "";
	
		                        if (fileName != null) {
		                        	extension = FilenameUtils.getExtension(fileName);
		                        }
		                        fileName = course.getName()+" - Sem- "+Integer.toString(course.getSemester())+" - "+Integer.toString(course.getYear())+" - "+docEntry.getTitle();
		                        File uploadedFile = new File(UPLOAD_DIRECTORY, fileName+"."+extension);
		                        File filePath = new File(UPLOAD_DIRECTORY);
		                        filePath.mkdirs();
		                        
		                        item.write(uploadedFile);	                            
		                        resp.setStatus(HttpServletResponse.SC_CREATED);
		                        resp.getWriter().print("The file was created successfully.");                        
		                        resp.flushBuffer();
		                            
		                        docEntry.setUploaded(true);
		                        docEntry.setFileName(fileName+"."+extension);
		                    
		                        docEntry = assignmentDao.save(docEntry);
		      				}       
	      			    }     
                }
                  
                  if (csv != null){
                	  resp.setContentType("application/octet-stream");
                	  resp.setHeader("Content-Disposition", "attachment; filename=\"" + FileUtil.escapeFilename("results.csv") + "\"");			
                	  resp.getOutputStream().write(csv.getBytes());
                  }else{
                	  logger.info("Failed to Serve csv file, (NULL content) ");
                  }
                  
                  
            } catch (Exception e) {
            	e.printStackTrace();
            	logger.info("An error occurred while creating the file : " + e.getMessage());
            	throw new ServletException("An error occurred while creating the file : " + e.getMessage());
            }
        } else {
        	ServletException me = new ServletException("Request contents type is not supported by the servlet.");
            throw me;
        }
    }
	

	/**
	 * Get logger user, its organization an initialize Reviewer with it
	 */
	private void initialize(HttpServletRequest request, HttpServletResponse response) throws Exception{
		user = getUser(request, response);
		organization = user.getOrganization();
		UPLOAD_DIRECTORY = Reviewer.getOrganizationsHome() + organization.getName()+ Reviewer.getUploadsHome();	
		EMPTY_FILE = Reviewer.getOrganizationsHome() + organization.getName() + Reviewer.getUploadsHome() + Reviewer.getEmptyDocument();
		 
		if (assignmentManager == null){
			assignmentManager = Reviewer.getAssignmentManager();
			assignmentDao = assignmentManager.getAssignmentDao();
		}		
		Reviewer.initializeAssignmentManager(organization);
	}
	

	private User getUser(HttpServletRequest request, HttpServletResponse response) {
		
		User user = null;
		try {
			user = getLoggedUser(request, response);
			if (user != null && user.isSuperAdmin() || user.isAdmin()) {
				User mockedUser = (User) request.getSession().getAttribute("mockedUser");
				if (mockedUser != null) {
					user = mockedUser;
					if ( mockedUser.getOrganization() == null){
						UserDao userDao = UserDao.getInstance();
						user = userDao.getUserByEmail(mockedUser.getEmail());
					}
				} 
			}
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public User getLoggedUser(HttpServletRequest request,HttpServletResponse response) throws MessageException{
		try {			
			
			// Get user from session
			Object obj = request.getSession().getAttribute("user");
			if (obj != null){
				user = (User) obj;
			}
			
			// getEmail
			String email = getEmail(request,response);
			
			if (email == null && user == null){
				// ERROR we need the email o the user to continue. 
				MessageException me = new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);;
				me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
				throw me;
			} else if (email != null && user != null && (user != null && user.getEmail() != null && user.getEmail().equals(email))){
				//user is logged ==> continue	
			} else {
				// user is null or user's email is different to the email obtained from request ==> get user from Database
				if (email != null && ((user == null) || (user != null && user.getEmail() != null && !user.getEmail().equals(email)))){
					UserDao userDao = UserDao.getInstance();
					user = userDao.getUserByEmail(email);
				} 
				
				// Get organization
				organization = getOrganization(email, user);
												
				if (organization == null){
					// ERROR we need the organization to know if shibboleth property is enabled or not
					logger.info("Organization is null so we can not verify the shibboleth property");
					MessageException me = new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);;
					me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
					throw me;
				} else {
					// Verify if the organization is activated and deleted
					if (!organization.isActivated() ){
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_UNACTIVATED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					} else if (organization.isDeleted()){
						organization = null;
						user = null;
						request.getSession().setAttribute("user", null);
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_DELETED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					}
					
					// Check if shibboleth is enabled or not in the organization	
					if (organization.isShibbolethEnabled()){
						if (user != null){
							// set user in session
							user.setOrganization(organization);
							if (StringUtil.isBlank(user.getFirstname()) || StringUtil.isBlank(user.getLastname())){
								String firstname = (String) request.getAttribute("givenName");
								String lastname = (String) request.getAttribute("surname");
								user.setFirstname(firstname);
								user.setLastname(lastname);
								UserDao userDao = UserDao.getInstance();
								user = userDao.save(user);
							}
							request.getSession().setAttribute("user", user);
						} else {	
							// create user
							user = createUser(request, email, organization);
										
							// set user in session
							request.getSession().setAttribute("user", user);
						}
					} else{
						// User comes from reviewer login page
						if (user != null){
							user.setOrganization(organization);
							
							request.getSession().setAttribute("user", user);
						} else {
							MessageException me = new MessageException(Constants.EXCEPTION_INVALID_LOGIN);;
							me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
							throw me;
						}
					}	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException)e;
			} else {
				throw new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);
			}
		}
		return user;
	}
	
	private String getEmail(HttpServletRequest request, HttpServletResponse response){
		// Get email from request
		String email = null;
		if (request.getUserPrincipal() != null) {
			// Get email from reviewer login page
			email = request.getUserPrincipal().getName();
		} else if (request.getAttribute("email") != null){
			// Get email from AAF IdP property
			email = (String) request.getAttribute("email");
		}
		return email;
	}
	
	/**
	 * Get organization from logged user or from the database using the domain of the email
	 * @param email email to get the domain
	 * @return Organization
	 * @throws MessageException
	 */
	private Organization getOrganization(String email, User user) throws MessageException{
		Organization organization = null;
		if (user != null){
			// Get organization from user
			organization = user.getOrganization();
		}  else {
			// Get organization using the email domain
			int i = email.indexOf("@");
			String domain = email.substring(i+1,email.length());
			OrganizationManager organizationManager = OrganizationManager.getInstance();
			organization = organizationManager.getOrganizationByDomain(domain);
		}
		return organization;
	}
	
	/**
	 * Create a user in the database. This method should be called only the first time that a new user loggin in reviewer and organization use shibboleht (AAF login)
	 * @param request Request to obtain the givenName and the surname of the user
	 * @param email email of the user
	 * @return User
	 * @throws MessageException
	 */
	private User createUser(HttpServletRequest request, String email, Organization anOrganization) throws MessageException{
		
		// add user into the database as a guest 
		String firstname = (String) request.getAttribute("givenName");
		String lastname = (String) request.getAttribute("surname");
		UserDao userDao = UserDao.getInstance();
		User newUser = userDao.getUserByEmail(email);
		if (newUser == null){
			newUser = new User();
			newUser.setFirstname(firstname);
			newUser.setLastname(lastname);
			newUser.setEmail(email);
			newUser.setOrganization(anOrganization);
			newUser.addRole(Constants.ROLE_GUEST);
			newUser = userDao.save(newUser);
		}
		return newUser;
	}
}
