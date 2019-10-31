package application.qav2.process_answer.entity;

import java.io.IOException;

import platform.annotate.AnnotateByDict;
import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.util.log.Trace;

public class TestAnswerEntity {

	static final Trace t = new Trace();
	String work_dir = "res/data/answer-entity/train/";
	public String workDir(String filename) {
		return work_dir + "/"+ filename;
	}
	
	public TestAnswerEntity(String work_dir) {
		this.work_dir = work_dir;
	}
	
	void prepareFile() throws IOException, Exception {
		// anno by dict
		new AnnotateByDict(1).annotateFile(workDir("test.orig"), workDir("1.dict.af"), "utf8");
		// refine
//		ReadableFileHandler.refine(workDir("9.man.af"));
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
//		TestAnswerEntity tae = new TestAnswerEntity("res/data/answer-entity/test/");
////		tae.prepareFile();
//		tae.test("crf.model", "test.result");
////		tae = new TestAnswerEntity("res/data/answer-entity/test/");
////		tae.prepareFile();
////		tae.test("crf.nopunc.model", "test.nopunc.result");
		
		
		TestAnswerEntity tae = new TestAnswerEntity("res/data/answer-entity/test-lhx/");
		tae.prepareFile();
		tae.test("crf.model", "test.result");
		
	}
	
}
