package au.edu.usyd.reviewer.server.util;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.edu.usyd.glosser.gdata.GoogleDocsServiceImpl;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;

public class QuestionUtilUnitTest {
    private WritingActivity writingActivity;
    private Course course;
    private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
    private String domain = Reviewer.getGoogleDomain();

    @Before
    public void setUp() {
    	AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();

        User student3 = new User();
        student3.setId("test.student03");
        student3.setFirstname("Test");
        student3.setLastname("Student03");
        student3.setEmail("test.student03@"+domain);
        assignmentDao.save(student3);
        
        User student4 = new User();
        student4.setId("test.student04");
        student4.setFirstname("Test");
        student4.setLastname("Student04");
        student4.setEmail("test.student04@"+domain);
        assignmentDao.save(student4);
        
        UserGroup userGroup1 = new UserGroup();
        userGroup1.setName("1");
        userGroup1.setTutorial("tutorial");
        userGroup1.getUsers().add(student3);
        userGroup1.getUsers().add(student4);
        assignmentDao.save(userGroup1);

        writingActivity = new WritingActivity();
        writingActivity.setName("AQGTestactivity");
        writingActivity.setDocumentType(WritingActivity.DOCUMENT_TYPE_DOCUMENT);
        writingActivity.setGroups(false);
        assignmentDao.save(writingActivity);
        
    	course = new Course();
        course.setName("course1");
        course.getStudentGroups().add(userGroup1);
        course.setYear(2009);
        course.setSemester(1);
        course.getWritingActivities().add(writingActivity);
        assignmentDao.save(course);
    }

    @Test
    public void testDownloadDoc() throws MalformedURLException, Exception {
        String path = assignmentManager.getDocumentsFolder(course.getId(),writingActivity.getId(),"aqg", "");
        File aqgExperimentFolder = new File(path);
        aqgExperimentFolder.mkdirs();
        QuestionUtil questionUtil = new QuestionUtil();
        GoogleDocsServiceImpl service = mock(GoogleDocsServiceImpl.class);
        questionUtil.setGoogleDocsServiceImpl(service);
        questionUtil.downloadDoc(path, writingActivity);
    }

    @Test
    public void testInsertQuestionsIntoDB() {
        String path = assignmentManager.getDocumentsFolder(course.getId(),writingActivity.getId(),"aqg", "");
            File aqgExperimentFolder = new File(path);
            aqgExperimentFolder.mkdirs();
            String filepath = path + Reviewer.getProperty("aqg.loadExcelPath");
            QuestionUtil questionUtil = new QuestionUtil();
            questionUtil.readExcelInsertDB(filepath);
    }

    @Test
    public void testRetireveFromDBInsertIntoExcel() {
         String dirpath = assignmentManager.getDocumentsFolder(course.getId(),writingActivity.getId(),"aqg", "");
            String filepath = dirpath + Reviewer.getProperty("aqg.insertToExcelPath");
            File aqgExperimentFolder = new File(dirpath);
            aqgExperimentFolder.mkdirs();
            QuestionUtil questionUtil = new QuestionUtil();
            questionUtil.insertScoretoExcel(filepath, writingActivity); 
    }
    
    @After
    public void cleanUp() {
    	AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
        assignmentDao.delete(course);
    }
}
