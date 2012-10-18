//package au.edu.usyd.reviewer.server.util;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import jxl.read.biff.BiffException;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import au.edu.usyd.reviewer.server.Reviewer;
//
//public class ExcelEditorUnitTest {
//
//    private String dirPath;
//    private File aqgExperimentFolder;
//
//    @Before
//    public void setUp() {
//        dirPath = Reviewer.getDocumentsHome();
//        aqgExperimentFolder = new File(dirPath);
//        aqgExperimentFolder.mkdir();
//    }
//
//    @Test
//    public void testInsertToExcel() {
//        String path = dirPath + "/" + System.getProperty("aqg.insertToExcelPath");
//        ExcelEditor eu = new ExcelEditor(path);
//        ArrayList<String> docids = new ArrayList<String>();
//        ArrayList<String> scores = new ArrayList<String>();
//        ArrayList<String> QM = new ArrayList<String>();
//        ArrayList<String> questions = new ArrayList<String>();
//        ArrayList<String> predictedQG = new ArrayList<String>();
//        ArrayList<String> RealQG = new ArrayList<String>();
//        docids.add("Docid");
//        docids.add("docid1");
//        docids.add("docid2");
//        questions.add("Question");
//        questions.add("question1");
//        questions.add("question2");
//        QM.add("QualityMeasure");
//        QM.add("1");
//        QM.add("2");
//        scores.add("Score");
//        scores.add("3");
//        scores.add("4");
//        predictedQG.add("Prediction");
//        predictedQG.add("system");
//        predictedQG.add("system");
//        RealQG.add("Real");
//        RealQG.add("lecturer");
//        RealQG.add("lecturer");
//
//        eu.addelementinColumn0(questions);
//        eu.addelementinColumn1(QM);
//        eu.addelementinColumn2(scores);
//        eu.addelementinColumn3(predictedQG);
//        eu.addelementinColumn4(RealQG);
//        eu.addelementinColumn5(docids);
//        eu.commit();
//    }
//
//    @Test
//    public void readFromExcel() throws BiffException, IOException {
//        String path = dirPath + "/" + System.getProperty("aqg.loadExcelPath");
//        ExcelEditor eu = new ExcelEditor();
//        eu.read(path);
//        ArrayList<String> question = eu.getColumn1data();
//        ArrayList<String> generator = eu.getColumn2data();
//        ArrayList<String> documentid = eu.getColumn3data();
//        
//        assertThat(question.size(), is(3));
//        assertThat(generator.size(), is(3));
//        assertThat(documentid.size(), is(3));
//    }
//
//}
