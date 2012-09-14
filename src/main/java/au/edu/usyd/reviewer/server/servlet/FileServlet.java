package au.edu.usyd.reviewer.server.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.util.FileUtil;

public class FileServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager(getUser().getOrganization());
	private AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
	private static final String UPLOAD_DIRECTORY = Reviewer.getUploadsHome();	
	private static final String EMPTY_FILE = Reviewer.getEmptyFile();
	private User user;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		setUser(user);
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
				if (docEntry.getOwner() != null && docEntry.getOwner().equals(user) || docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getUsers().contains(user) || course.getLecturers().contains(user) || course.getTutors().contains(user)) {
					file = new File(assignmentManager.getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), WritingActivity.TUTORIAL_ALL) + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf");
					filename = docEntry.getTitle() + " - " + deadline.getName() + ".pdf";
				} else {
					// check if user is a reviewer of a document
					ReviewEntry reviewEntry = assignmentDao.loadReviewEntryWhereDocEntryAndOwner(docEntry, user);
					if (reviewEntry != null && reviewEntry.getOwner().equals(user)) {
						file = new File(assignmentManager.getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), WritingActivity.TUTORIAL_ALL) + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf");
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
							file = new File(assignmentManager.getDocumentsFolder(course.getId(), Long.valueOf(reviewingActivity), deadline.getId(), tutorial) + ".zip");
						}else{
							file = new File(assignmentManager.getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), tutorial) + ".zip");
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
			ServletOutputStream out = response.getOutputStream();
			if (file.exists() && (filename != null)) {
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + FileUtil.escapeFilename(filename) + "\"");			
				response.setContentLength((int) file.length());
				logger.info("Serving file: " + file.getAbsolutePath());
				int length = 0;
				byte[] bbuf = new byte[1024];
				DataInputStream in = new DataInputStream(new FileInputStream(file));
				while ((in != null) && ((length = in.read(bbuf)) != -1)) {
					out.write(bbuf, 0, length);
				}
				in.close();
			}else{ //empty file				
				file = new File(EMPTY_FILE);				
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + FileUtil.escapeFilename("Empty.pdf") + "\"");			
				response.setContentLength((int) file.length());
				logger.info("Serving empty file: " + file.getAbsolutePath());
				int length = 0;
				byte[] bbuf = new byte[1024];
				DataInputStream in = new DataInputStream(new FileInputStream(file));
				while ((in != null) && ((length = in.read(bbuf)) != -1)) {
					out.write(bbuf, 0, length);
				}
				in.close();				
			}
			out.flush();
			out.close();
		}
	}

	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		User user = (User) req.getSession().getAttribute("user");
		setUser(user);
		String docId = null;
		String csv = null;
		
		logger.info("User uploading file: " + user.getEmail());      			
        // process only multipart requests
        if (ServletFileUpload.isMultipartContent(req)) {
        	logger.info("Multipart Content ");

            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            // Parse the request
            try {            	
            	logger.info("Starting to parse request. " + req.toString());
                @SuppressWarnings("rawtypes")
				Iterator items = upload.parseRequest(req).iterator();
                logger.info("Parsing request... "+items.toString());
                  while (items.hasNext()) {            	
                	  FileItem item = (FileItem) items.next();
                	  if (item.isFormField()) {
                		  logger.info("item.getFieldName: " +item.getFieldName());
                          if (item.getFieldName().equals("docId")) {
                              docId = item.getString();
                          }
                          if (item.getFieldName().equals("csv")) {
                              csv = item.getString();
                          }                          
                          continue;
                      }            
                	
                	logger.info("Attempting to upload file into docId: " + docId);
                	
          			if (docId != null) {
      				// get document course and activity
      				DocEntry docEntry = assignmentDao.loadDocEntry(docId);
      				WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(docEntry);
      				Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
      
      				    logger.info("Owner: " + docEntry.getOwner());      				    	  
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
	                        
	                        //if (uploadedFile.createNewFile()) {
	                            item.write(uploadedFile);	                            

	                            resp.setStatus(HttpServletResponse.SC_CREATED);
	                            resp.getWriter().print("The file was created successfully.");                        
	                            resp.flushBuffer();
	                        //} else
	                          //  throw new IOException("The file already exists in repository.");
	                            
	                            docEntry.setUploaded(true);
	                            docEntry.setFileName(fileName+"."+extension);
	                            logger.info("fileName+Extension " + fileName+"."+extension);
	                            assignmentDao.save(docEntry);
	      				}       
      			    }     
                }
                  
                  if (csv != null){
                	  logger.info("Serving csv file");
                	  resp.setContentType("application/octet-stream");
                	  resp.setHeader("Content-Disposition", "attachment; filename=\"" + FileUtil.escapeFilename("results.csv") + "\"");			
                	  resp.getOutputStream().write(csv.getBytes());
                  }else{
                	  logger.info("Failed to Serve csv file, (NULL content) ");
                  }
                  
                  
            } catch (Exception e) {
            	logger.info("An error occurred while creating the file : " + e.getMessage());
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occurred while creating the file : " + e.getMessage());
            }
        } else {
        	logger.info("Not a multipart request, User uploading file: " + user);      
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                            "Request contents type is not supported by the servlet.");
        }
    }
	
	private void setUser(User anUser) {
		user = anUser;
	}
	private User getUser() {
		return user;
	}

}
