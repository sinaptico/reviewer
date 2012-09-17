package au.edu.usyd.reviewer.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.Question;
import au.edu.usyd.reviewer.client.core.QuestionScore;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

public class QuestionDaoUnitTest {
    private QuestionDao questionDao;
    private AssignmentDao assignmentDao;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Question LecturerQuestion1, LecturerQuestion2, LecturerQuestion3, LecturerQuestion4, LecturerQuestion5;
    private Question TutorQuestion1, TutorQuestion2, TutorQuestion3, TutorQuestion4, TutorQuestion5;
    private Question SystemQuestion1, SystemQuestion2, SystemQuestion3, SystemQuestion4, SystemQuestion5;
    private Question GenericQuestion1, GenericQuestion2, GenericQuestion3, GenericQuestion4, GenericQuestion5;
    private List<Question> questionlist;
    private User user1, user2, user3, aqg, generic, student1;
    private DocEntry docEntry1;
    private String domain = null;
    private OrganizationDao organizationDao = OrganizationDao.getInstance();
    @Before
    public void setUp() {
    	Organization organization = new Organization();
    	organization.setName("QUESTION DAO UNIT TEST");
    	try {
			organizationDao.save(organization);
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	domain = organization.getGoogleDomain();
        assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
        questionDao = new QuestionDao(Reviewer.getHibernateSessionFactory());
        LecturerQuestion1 = new Question();
        LecturerQuestion2 = new Question();
        LecturerQuestion3 = new Question();
        LecturerQuestion4 = new Question();
        LecturerQuestion5 = new Question();

        // LecturerQuestion.setId(1);
        LecturerQuestion1.setQuestion("What type of empirical data did Goldberg et al. (2003) collect?");
        LecturerQuestion2.setQuestion("Why do you think that work such as automatic scoring of essays (Shermis & Burstein, 2003) focuses on the final product?");
        LecturerQuestion3.setQuestion("Why has peer review been recognized as one effective way to learn writing? What evidence did (Carlson & Berry, 2008) provide?");
        LecturerQuestion4.setQuestion("Why is your framework is based on a taxonomy of collaborative writing proposed by Lowry et al. (2003) ?");
        LecturerQuestion5.setQuestion("Why do you think that work such as automatic scoring of essays(O’Rourke & Calvo, 2009) focuses on the final product?");
        TutorQuestion1 = new Question();
        TutorQuestion2 = new Question();
        TutorQuestion3 = new Question();
        TutorQuestion4 = new Question();
        TutorQuestion5 = new Question();

        // TutorQuestion.setId(2L);
        TutorQuestion1.setQuestion("Does any other scholar agree or disagree with Goldberg's opinon that when using computers, students prefer to make revisions while producing, rather than after producing... ? Do you think its useful for your research ?");
        TutorQuestion2.setQuestion("Is it true that the text mining projects, such as automatic scoring of essays (Shermis & Burstein, 2003), visualization (O’Rourke & Calvo, 2009), and document clustering (Andrews & Fox, 2007) only focus on the final product? Are they useful to support revision activities in writing? ");
        TutorQuestion3.setQuestion("Why does Carlson point out peer review which has been recognized as one effective way to learn writing? How is it important to your research?");
        TutorQuestion4.setQuestion("Is process mining successfully applied in the following areas, such as  performance characteristics, process discovery..? Does any other scholar apply processs mining in education? ");
        TutorQuestion5.setQuestion("Does anyone use the taxonomy of collaborative writing proposed by Lowry ? Do you need to do some modification on the taxonomy for your research?");
        SystemQuestion1 = new Question();
        SystemQuestion2 = new Question();
        SystemQuestion3 = new Question();
        SystemQuestion4 = new Question();
        SystemQuestion5 = new Question();
        // SystemQuestion.setId(3L);
        SystemQuestion1.setQuestion("In the study of Lowry, why did strategies , work modes and roles involve in CW? Does any other scholar agree or disagree with Lowry? How does it relate to your research question?");
        SystemQuestion2.setQuestion("Why does Burstein conduct this study to focus on the final product , not on the writing process itself? (What is the research question formulated by Burstein? What is Burstein’s contribution to our understanding of the problem under study? )");
        SystemQuestion3.setQuestion("In the study of Gabriel & van der Werf, why does the field of process mining cover many areas , like performance characteristics ( e.g. throughput times ) , process discovery ( discovery of...? Does any other scholar agree or disagree with Gabriel & van der Werf? How does it relate to your research question?");
        SystemQuestion4.setQuestion("Did Goldberg objectively find that when students write on computers , writing becomes a more social process in which students share...? ( Is the analysis of the data accurate and relevant to the research question?) How does it relate to your research question?");
        SystemQuestion5.setQuestion("Does Carlson objectively show that review feedback , especially peer review , has been recognized as one effective way to learn writing? (How accurate and valid are the measurements?) How does it relate to your research question?");
        GenericQuestion1 = new Question();
        GenericQuestion2 = new Question();
        GenericQuestion3 = new Question();
        GenericQuestion4 = new Question();
        GenericQuestion5 = new Question();
        // GenericQuestion.setId(4L);
        GenericQuestion1.setQuestion("Does your Literature Review cover the most important relevant work in your research field?");
        GenericQuestion2.setQuestion("Do you clearly identify the contributions of the literature reviewed?");
        GenericQuestion3.setQuestion("Why do you think that work such as automatic scoring of essays(O’Rourke & Calvo, 2009) focuses on the final product?");
        GenericQuestion4.setQuestion("Do you connect the literature to the research topic by identifying its relevance ?");
        GenericQuestion5.setQuestion("What are the author's credentials? Are the author's arguments supported by evidence (e.g. primary historical material, case studies, narratives, statistics, recent scientific findings)?");

        docEntry1 = new DocEntry();
        docEntry1.setDocumentId("document:123");
        assignmentDao.save(docEntry1);

        student1 = new User();
        student1.setUsername("student01");
        student1.setEmail("test.student01@"+domain);
        student1.setOrganization(organization);
        assignmentDao.save(student1);
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@"+domain);
        user1.setOrganization(organization);
        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@"+domain);
        user2.setOrganization(organization);
        user3 = new User();
        user3.setUsername("user3");
        user3.setEmail("user3@"+domain);
        user3.setOrganization(organization);

        assignmentDao.save(user1);
        assignmentDao.save(user2);
        assignmentDao.save(user3);

        aqg = new User();
        aqg.setUsername("AQG1");
        aqg.setEmail("AQG@"+domain);
        generic = new User();
        generic.setUsername("generic");
        generic.setEmail("generic@"+domain);

        assignmentDao.save(aqg);
        assignmentDao.save(generic);
        saveQuestion();
    }

    public void saveQuestion() {
        LecturerQuestion1.setOwner(user1);
        LecturerQuestion1.setDocId(docEntry1.getDocumentId());
        LecturerQuestion2.setOwner(user1);
        LecturerQuestion2.setDocId(docEntry1.getDocumentId());
        LecturerQuestion3.setOwner(user1);
        LecturerQuestion3.setDocId(docEntry1.getDocumentId());
        LecturerQuestion4.setOwner(user1);
        LecturerQuestion4.setDocId(docEntry1.getDocumentId());
        LecturerQuestion5.setOwner(user1);
        LecturerQuestion5.setDocId(docEntry1.getDocumentId());

        TutorQuestion1.setOwner(user2);
        TutorQuestion1.setDocId(docEntry1.getDocumentId());
        TutorQuestion2.setOwner(user2);
        TutorQuestion2.setDocId(docEntry1.getDocumentId());
        TutorQuestion3.setOwner(user2);
        TutorQuestion3.setDocId(docEntry1.getDocumentId());
        TutorQuestion4.setOwner(user2);
        TutorQuestion4.setDocId(docEntry1.getDocumentId());
        TutorQuestion5.setOwner(user2);
        TutorQuestion5.setDocId(docEntry1.getDocumentId());

        SystemQuestion1.setOwner(aqg);
        SystemQuestion1.setDocId(docEntry1.getDocumentId());
        SystemQuestion2.setOwner(aqg);
        SystemQuestion2.setDocId(docEntry1.getDocumentId());
        SystemQuestion3.setOwner(aqg);
        SystemQuestion3.setDocId(docEntry1.getDocumentId());
        SystemQuestion4.setOwner(aqg);
        SystemQuestion4.setDocId(docEntry1.getDocumentId());
        SystemQuestion5.setOwner(aqg);
        SystemQuestion5.setDocId(docEntry1.getDocumentId());

        GenericQuestion1.setOwner(generic);
        GenericQuestion1.setDocId(docEntry1.getDocumentId());
        GenericQuestion2.setOwner(generic);
        GenericQuestion2.setDocId(docEntry1.getDocumentId());
        GenericQuestion3.setOwner(generic);
        GenericQuestion3.setDocId(docEntry1.getDocumentId());
        GenericQuestion4.setOwner(generic);
        GenericQuestion4.setDocId(docEntry1.getDocumentId());
        GenericQuestion5.setOwner(generic);
        GenericQuestion5.setDocId(docEntry1.getDocumentId());

        questionDao.saveQuestion(LecturerQuestion1);
        questionDao.saveQuestion(LecturerQuestion2);
        questionDao.saveQuestion(LecturerQuestion3);
        questionDao.saveQuestion(LecturerQuestion4);
        questionDao.saveQuestion(LecturerQuestion5);

        questionDao.saveQuestion(TutorQuestion1);
        questionDao.saveQuestion(TutorQuestion2);
        questionDao.saveQuestion(TutorQuestion3);
        questionDao.saveQuestion(TutorQuestion4);
        questionDao.saveQuestion(TutorQuestion5);

        questionDao.saveQuestion(SystemQuestion1);
        questionDao.saveQuestion(SystemQuestion2);
        questionDao.saveQuestion(SystemQuestion3);
        questionDao.saveQuestion(SystemQuestion4);
        questionDao.saveQuestion(SystemQuestion5);

        questionDao.saveQuestion(GenericQuestion1);
        questionDao.saveQuestion(GenericQuestion2);
        questionDao.saveQuestion(GenericQuestion3);
        questionDao.saveQuestion(GenericQuestion4);
        questionDao.saveQuestion(GenericQuestion5);

        logger.info("saving triggerQuestions!");
    }

    @Test
    public void searchQuestion() {
        questionlist = questionDao.getQuestion(docEntry1.getDocumentId());
        List<String> sortedlist = new ArrayList<String>();
        for (Question question : questionlist) {
            if (!question.getQuestion().isEmpty()) {
                sortedlist.add(question.getQuestion());
            }
        }
        Random random = new Random();
        int size = sortedlist.size();
        List<String> randomlist = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            int j = random.nextInt(sortedlist.size());
            randomlist.add(sortedlist.get(j));
            sortedlist.remove(j);
        }

        logger.info("retrieving triggerQuestion:" + randomlist.size());
    }

    @Test
    public void getQuestionListByUserDoc() {

        List<Question> questions = questionDao.searchQuestions(student1, docEntry1.getDocumentId());
        logger.info("retrieving triggerQuestion:" + questions.size() + "from user:" + student1.getEmail() + "and doc:" + docEntry1.getId());

    }

    @Test
    public void savingScore() {
        Question LecturerQuestion1 = null;
        Question TutorQuestion1 = null;
        Question SystemQuestion1 = null;
        Question GenericQuestion1 = null;
        questionlist = questionDao.getQuestion(docEntry1.getDocumentId());
        for (Question question : questionlist) {
            if (question.getOwner().getUsername().equals("user1")) {
                LecturerQuestion1 = question;
            } else if (question.getOwner().equals(user2)) {
                TutorQuestion1 = question;
            } else if (question.getOwner().equals(aqg)) {
                SystemQuestion1 = question;
            } else if (question.getOwner().equals(generic)) {
                GenericQuestion1 = question;
            }

        }

        QuestionScore score1 = new QuestionScore();
        score1.setQuestion(LecturerQuestion1);
        score1.setQualityMeasure(1);
        score1.setProduced("lecturer");
        score1.setComment("its hard to tell who the question producer is.");
        questionDao.saveScore(score1);
        QuestionScore score2 = new QuestionScore();
        score2.setQuestion(TutorQuestion1);
        score2.setQualityMeasure(1);
        score2.setProduced("tutor");
        score2.setComment("its hard to tell who the question producer is.");
        questionDao.saveScore(score2);
        QuestionScore score3 = new QuestionScore();
        score3.setQuestion(SystemQuestion1);
        score3.setQualityMeasure(1);
        score3.setProduced("system");
        score3.setComment("its hard to tell who the question producer is.");
        questionDao.saveScore(score3);
        QuestionScore score4 = new QuestionScore();
        score4.setQuestion(GenericQuestion1);
        score4.setQualityMeasure(1);
        score4.setProduced("Generic");
        score4.setComment("its hard to tell who the question producer is.");
        questionDao.saveScore(score4);

        List<QuestionScore> questionScores = questionDao.getScore(LecturerQuestion1);
    }
    
    @After
    public void cleanUp() {
    	assignmentDao.delete(docEntry1);
    }
}
