package au.edu.usyd.reviewer.server.util;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.glosser.gdata.GoogleDocsServiceImpl;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.Question;
import au.edu.usyd.reviewer.client.core.QuestionScore;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.QuestionDao;
import au.edu.usyd.reviewer.server.Reviewer;

import com.google.gdata.data.docs.DocumentListEntry;

public class QuestionUtil {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private GoogleDocsServiceImpl googleDocsServiceImpl = null;

	// index the document to lucence
	public void downloadDoc(String dirpath, WritingActivity writingActivity) throws Exception, MalformedURLException {
		logger.info("Download document!");
		for (DocEntry docEntry : writingActivity.getEntries()) {
			try {
				DocumentListEntry documentListEntry = googleDocsServiceImpl.getDocument(docEntry.getDocumentId());
				String filePath = dirpath + "/" + FileUtil.escapeFilename(docEntry.getDocumentId() + ".html");
				googleDocsServiceImpl.downloadDocumentFile(documentListEntry, filePath);
			} catch (Exception e) {
				logger.info("document " + docEntry.getId() + " is not found");
			}
		}
	}

	public void insertScoretoExcel(String filepath, WritingActivity writingActivity) {
		logger.info("Retrieve score from DB and then insert into Excel!");
		ArrayList<String> docidlist = new ArrayList<String>();
		ArrayList<String> scorelist = new ArrayList<String>();
		ArrayList<String> QMlist = new ArrayList<String>();
		ArrayList<String> questionlist = new ArrayList<String>();
		ArrayList<String> predictedQGlist = new ArrayList<String>();
		ArrayList<String> RealQGlist = new ArrayList<String>();
		ArrayList<String> sourcesentenclist = new ArrayList<String>();
		ArrayList<String> nativespeakerlist = new ArrayList<String>();
		docidlist.add("Docid");
		scorelist.add("Score");
		QMlist.add("Quality Measure");
		questionlist.add("Question");
		predictedQGlist.add("Prediction");
		RealQGlist.add("Real");
		sourcesentenclist.add("Source Sentence");
		nativespeakerlist.add("Native Speaker");
		
		QuestionDao questionDao = new QuestionDao(Reviewer.getHibernateSessionFactory());
		
		for (DocEntry doc : writingActivity.getEntries()) {
			List<Question> questions = questionDao.getQuestion(doc.getDocumentId());
			for (Question question : questions) {
				List<QuestionScore> questionScores = questionDao.getScore(question);
				for (QuestionScore questionScore : questionScores) {
					scorelist.add(String.valueOf(questionScore.getGrade()));
					QMlist.add(String.valueOf(questionScore.getQualityMeasure()));
					questionlist.add(question.getQuestion());
					predictedQGlist.add(questionScore.getProduced());
					//RealQGlist.add(question.getOwner().getUsername());
					RealQGlist.add(question.getOwner().getEmail());
					docidlist.add(doc.getDocumentId());
					sourcesentenclist.add(question.getSourceSentence());
					nativespeakerlist.add(doc.getOwner().getNativeSpeaker());
				}
			}
		}
		
		ExcelEditor excelEditor = new ExcelEditor(filepath);
		excelEditor.addelementinColumn0(questionlist);
		excelEditor.addelementinColumn1(QMlist);
		excelEditor.addelementinColumn2(scorelist);
		excelEditor.addelementinColumn3(predictedQGlist);
		excelEditor.addelementinColumn4(RealQGlist);
		excelEditor.addelementinColumn5(docidlist);
		excelEditor.addelementinColumn6(sourcesentenclist);
		excelEditor.addelementinColumn7(nativespeakerlist);
		excelEditor.commit();
	}

	public void readExcelInsertDB(String filepath, Organization organization) throws MessageException {
		logger.info("Read Excel and then insert into DB!");
		ExcelEditor excelEditor = new ExcelEditor();
		try {
			excelEditor.read(filepath);
		} catch (Exception e) {
			logger.info("Excel file not found:" + filepath);
		}
		ArrayList<String> questionlist = excelEditor.getColumn1data();
		ArrayList<String> generatorlist = excelEditor.getColumn2data();
		ArrayList<String> documentidlist = excelEditor.getColumn3data();
		ArrayList<String> sourceSentencelist = excelEditor.getColumn4data();
		AssignmentDao assignment = new AssignmentDao(Reviewer.getHibernateSessionFactory());
		QuestionDao questionDao = new QuestionDao(Reviewer.getHibernateSessionFactory()); 
		for (int i = 1; i < questionlist.size(); i++) {
			User user = new User();
			user.setOrganization(organization);
//			user.setUsername(generatorlist.get(i));
			user.setEmail(generatorlist.get(i));
			assignment.save(user);
			Question questionObj = new Question();
			questionObj.setOwner(user);
			questionObj.setQuestion(questionlist.get(i));
			questionObj.setDocId(documentidlist.get(i));
			if (user.getUsername().equals("aqg")) {
				questionObj.setSourceSentence(sourceSentencelist.get(i));
			}
			questionDao.saveQuestion(questionObj);
		}
	}

	public void setGoogleDocsServiceImpl(GoogleDocsServiceImpl googleDocsServiceImpl) {
		this.googleDocsServiceImpl = googleDocsServiceImpl;
	}

}
