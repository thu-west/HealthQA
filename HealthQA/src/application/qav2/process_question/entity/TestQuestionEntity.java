package application.qav2.process_question.entity;

import platform.annotate.AnnotateByDict;
import platform.annotate.AnnotateByPostag;
import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.mltools.crfpp.ReadableFileHandler;
import platform.util.log.Trace;

public class TestQuestionEntity {
	static final Trace t = new Trace();
	String work_dir = "res/data/question-entity/test/";
	public String workDir(String filename) {
		return work_dir + filename;
	}
	
	public TestQuestionEntity(String work_dir) {
		this.work_dir = work_dir;
	}
	void prepareFile () throws Exception {
		// anno with dict
		AnnotateByDict ad = new AnnotateByDict(0);
		ad.annotateFile(workDir("test.orig"), workDir("1.dict.af"), "utf8");
		// anno with postag
		AnnotateByPostag.label(workDir("test.orig"), workDir("2.postag.af"));
		// refine manual aggregate file
		ReadableFileHandler.refine(workDir("9.man.af"));
	}
	
	void test(String model_name, String result_name) throws Exception {
		// transfer to crf++ format
		CRFPPDataHandler td = new CRFPPDataHandler(work_dir);
		td.transferAllAggFilesToSingleSegFiles();
		td.combineAllSingleSegFilesToMultiSegFile("test.msf");
		CRF crf =  new CRF(workDir("../model/"+model_name));
		crf.test(workDir("test.msf"), workDir(result_name));
	}
	
	public static void main(String[] args) throws Exception {
		TestQuestionEntity tqe = new TestQuestionEntity("res/data/question-entity/test/");
//		tqe.prepareFile();
		System.out.println("all");
		tqe.test("crf.model", "test.result");
		System.out.println("dict");
		tqe.test("crf.model.dict", "test.result.dict");
		System.out.println("word+dict");
		tqe.test("crf.model.word.dict", "test.result.word.dict");
		System.out.println("word");
		tqe.test("crf.model.word", "test.result.word");
		System.out.println("word+postag");
		tqe.test("crf.model.word.postag", "test.result.word.postag");
	}
}
