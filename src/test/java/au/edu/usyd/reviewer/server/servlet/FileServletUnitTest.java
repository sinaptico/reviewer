//package au.edu.usyd.reviewer.server.servlet;
//
//import static org.hamcrest.CoreMatchers.*;
//import static org.junit.Assert.*;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//
//import au.edu.usyd.reviewer.client.core.Activity;
//import au.edu.usyd.reviewer.client.core.Course;
//import au.edu.usyd.reviewer.client.core.Deadline;
//import au.edu.usyd.reviewer.client.core.DocEntry;
//import au.edu.usyd.reviewer.client.core.Organization;
//import au.edu.usyd.reviewer.client.core.Review;
//import au.edu.usyd.reviewer.client.core.ReviewEntry;
//import au.edu.usyd.reviewer.client.core.ReviewingActivity;
//import au.edu.usyd.reviewer.client.core.User;
//import au.edu.usyd.reviewer.client.core.UserGroup;
//import au.edu.usyd.reviewer.client.core.WritingActivity;
//import au.edu.usyd.reviewer.server.AssignmentDao;
//import au.edu.usyd.reviewer.server.Reviewer;
//import au.edu.usyd.reviewer.server.util.FileUtil;
//
//public class FileServletUnitTest {
//	private AssignmentDao assignmentDao;
//	private Deadline deadline1;
//	private Deadline groupDeadline1;
//	private Course course1;
//	private DocEntry docEntry1;
//	private DocEntry docEntry2;
//	private DocEntry groupDocEntry1;
//	private User lecturer1;
//	private User tutor1;
//	private User user1;
//	private User user2;
//	private File pdfFile1;
//	private File pdfFile2;
//	private File groupPdfFile1;
//	private File zipFile1;
//
//	@After
//	public void cleanUp() {
//		assignmentDao.delete(course1);
//
//		pdfFile1.delete();
//		pdfFile2.delete();
//		groupPdfFile1.delete();
//		zipFile1.delete();
//	}
//
//	@Before
//	public void setUp() throws IOException {
//		assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
//
//		Organization organization = new Organization();
//		organization.setName("FileServletUnitTest");
//		
//		lecturer1 = new User();
//		lecturer1.setUsername("lecturer1");
//		lecturer1.setOrganization(organization);
//		assignmentDao.save(lecturer1);
//
//		tutor1 = new User();
//		tutor1.setUsername("tutor1");
//		tutor1.setOrganization(organization);
//		assignmentDao.save(tutor1);
//
//		user1 = new User();
//		user1.setUsername("user1");
//		user1.setOrganization(organization);
//		user1.setEmail("user1@example.com");
//		user1.setOrganization(organization);
//		assignmentDao.save(user1);
//
//		user2 = new User();
//		user2.setUsername("user2");
//		user2.setOrganization(organization);
//		user2.setEmail("user2@example.com");
//		user2.setOrganization(organization);
//		assignmentDao.save(user2);
//
//		UserGroup userGroup1 = new UserGroup();
//		userGroup1.setName("userGroup1");
//		userGroup1.setTutorial("tutorial1");
//		userGroup1.getUsers().add(user1);
//		assignmentDao.save(userGroup1);
//		
//		UserGroup userGroup2 = new UserGroup();
//		userGroup2.setName("userGroup2");
//		userGroup2.setTutorial("tutorial1");
//		userGroup2.getUsers().add(user2);
//		assignmentDao.save(userGroup2);
//
//		docEntry1 = new DocEntry();
//		docEntry1.setDocumentId("document:01");
//		docEntry1.setTitle("Document 01");
//		docEntry1.setOwner(user1);
//		assignmentDao.save(docEntry1);
//
//		docEntry2 = new DocEntry();
//		docEntry2.setDocumentId("document:02");
//		docEntry2.setTitle("Document 02");
//		docEntry2.setOwner(user2);
//		assignmentDao.save(docEntry2);
//
//		groupDocEntry1 = new DocEntry();
//		groupDocEntry1.setDocumentId("document:04");
//		groupDocEntry1.setOwnerGroup(userGroup1);
//		assignmentDao.save(groupDocEntry1);
//
//		Review review1 = new Review();
//		review1.setContent("content");
//		assignmentDao.save(review1);
//		ReviewEntry reviewEntry1 = new ReviewEntry();
//		reviewEntry1.setOwner(user1);
//		reviewEntry1.setDocEntry(docEntry2);
//		reviewEntry1.setReview(review1);
//		assignmentDao.save(reviewEntry1);
//
//		deadline1 = new Deadline("Final");
//		ReviewingActivity reviewingActivity1 = new ReviewingActivity();
//		reviewingActivity1.setStartDate(deadline1);
//		reviewingActivity1.setStatus(Activity.STATUS_START);
//		reviewingActivity1.getEntries().add(reviewEntry1);
//
//		WritingActivity activity1 = new WritingActivity();
//		activity1.setName("activity1");
//		activity1.getDeadlines().add(deadline1);
//		activity1.getEntries().add(docEntry1);
//		activity1.getEntries().add(docEntry2);
//		activity1.getReviewingActivities().add(reviewingActivity1);
//		activity1.getReviewingActivities().get(0).setStartDate(activity1.getDeadlines().get(0));
//		activity1.setStatus(Activity.STATUS_START);
//		assignmentDao.save(activity1);
//
//		groupDeadline1 = new Deadline("Final");
//		
//		WritingActivity groupActivity1 = new WritingActivity();
//		groupActivity1.setName("groupActivity1");
//		groupActivity1.getDeadlines().add(groupDeadline1);
//		groupActivity1.getEntries().add(groupDocEntry1);
//		groupActivity1.setGroups(true);
//		groupActivity1.setName("groupActivity1");
//		assignmentDao.save(groupActivity1);
//
//		course1 = new Course();
//		course1.setName("course1");
//		course1.getLecturers().add(lecturer1);
//		course1.getTutors().add(tutor1);
//		course1.getTutorials().add("tutorial1");
//		course1.getStudentGroups().add(userGroup1);
//		course1.getStudentGroups().add(userGroup2);
//		course1.getWritingActivities().add(activity1);
//		course1.getWritingActivities().add(groupActivity1);
//		course1.setOrganization(organization);
//		assignmentDao.save(course1);
//		
//		pdfFile1 = new File(Reviewer.getAssignmentManager(organization).getDocumentsFolder(course1.getId(), activity1.getId(), deadline1.getId(), WritingActivity.TUTORIAL_ALL) + "/" + FileUtil.escapeFilename(docEntry1.getDocumentId()) + ".pdf");
//		pdfFile1.getParentFile().mkdirs();
//		pdfFile1.createNewFile();
//		FileOutputStream  out = new FileOutputStream(pdfFile1);
//		out.write(1);
//		out.close();
//		
//		pdfFile2 = new File(Reviewer.getAssignmentManager(organization).getDocumentsFolder(course1.getId(), activity1.getId(), deadline1.getId(), WritingActivity.TUTORIAL_ALL) + "/" + FileUtil.escapeFilename(docEntry2.getDocumentId()) + ".pdf");
//		pdfFile2.getParentFile().mkdirs();
//		pdfFile2.createNewFile();
//		out = new FileOutputStream(pdfFile2);
//		out.write(1);
//		out.close();
//		
//		groupPdfFile1 = new File(Reviewer.getAssignmentManager(organization).getDocumentsFolder(course1.getId(), groupActivity1.getId(), groupDeadline1.getId(), WritingActivity.TUTORIAL_ALL) + "/" + FileUtil.escapeFilename(groupDocEntry1.getDocumentId()) + ".pdf");
//		groupPdfFile1.getParentFile().mkdirs();
//		groupPdfFile1.createNewFile();
//		out = new FileOutputStream(groupPdfFile1);
//		out.write(1);
//		out.close();
//		
//		zipFile1 = new File(Reviewer.getAssignmentManager(organization).getDocumentsFolder(course1.getId(), activity1.getId(), deadline1.getId(), "tutorial1") + ".zip");
//		zipFile1.getParentFile().mkdirs();
//		zipFile1.createNewFile();
//		out = new FileOutputStream(zipFile1);
//		out.write(1);
//		out.close();
//	}
//
//	@Test
//	public void shouldNotServePdfFileToUser() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", user2);
//		request.setParameter("docId", docEntry1.getDocumentId());
//		request.setParameter("docVersion", String.valueOf(deadline1.getId()));
//		
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), nullValue());
//		assertThat(response.getContentLength(), is(0));
//	}
//	
//	@Test
//	public void shouldNotServeZipFileToUser() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", user2);
//		request.setParameter("tutorial", "tutorial1");
//		request.setParameter("docVersion", String.valueOf(deadline1.getId()));
//
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), nullValue());
//		assertThat(response.getContentLength(), is(0));
//	}
//
//	@Test
//	public void shouldServePdfFileToLecturer() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", lecturer1);
//		request.setParameter("docId", docEntry1.getDocumentId());
//		request.setParameter("docVersion", String.valueOf(deadline1.getId()));
//
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), is("application/octet-stream"));
//		assertThat(response.getContentLength(), not(0));
//	}
//	
//	@Test
//	public void shouldServePdfFileToOwner() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", user1);
//		request.setParameter("docId", docEntry1.getDocumentId());
//		request.setParameter("docVersion", String.valueOf(deadline1.getId()));
//
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), is("application/octet-stream"));
//		assertThat(response.getContentLength(), not(0));
//	}
//
//	@Test
//	public void shouldServePdfFileToOwnerGroup() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", user1);
//		request.setParameter("docId", groupDocEntry1.getDocumentId());
//		request.setParameter("docVersion", String.valueOf(groupDeadline1.getId()));
//
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), is("application/octet-stream"));
//		assertThat(response.getContentLength(), not(0));
//	}
//
//	@Test
//	public void shouldServePdfFileToReviewer() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", user1);
//		request.setParameter("docId", docEntry2.getDocumentId());
//		request.setParameter("docVersion", String.valueOf(deadline1.getId()));
//
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), is("application/octet-stream"));
//		assertThat(response.getContentLength(), not(0));
//	}
//
//	@Test
//	public void shouldServePdfFileToTutor() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", tutor1);
//		request.setParameter("docId", docEntry1.getDocumentId());
//		request.setParameter("docVersion", String.valueOf(deadline1.getId()));
//
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), is("application/octet-stream"));
//		assertThat(response.getContentLength(), not(0));
//	}
//
//	@Test
//	public void shouldServeZipFileToLecturer() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", lecturer1);
//		request.setParameter("tutorial", "tutorial1");
//		request.setParameter("docVersion", String.valueOf(deadline1.getId()));
//
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), is("application/octet-stream"));
//		assertThat(response.getContentLength(), not(0));
//	}
//	
//	@Test
//	public void shouldServeZipFileToTutor() throws Exception {
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.getSession().setAttribute("user", tutor1);
//		request.setParameter("tutorial", "tutorial1");
//		request.setParameter("docVersion", String.valueOf(deadline1.getId()));
//
//		FileServlet fileServlet = new FileServlet();
//		fileServlet.doGet(request, response);
//		assertThat(response.getContentType(), is("application/octet-stream"));
//		assertThat(response.getContentLength(), not(0));
//	}
//}
