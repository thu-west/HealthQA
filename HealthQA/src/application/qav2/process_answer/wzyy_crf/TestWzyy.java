package application.qav2.process_answer.wzyy_crf;

import java.io.IOException;

import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.util.log.Trace;

public class TestWzyy {
	static final Trace t = new Trace();
	
	public String work_dir = "res/data/complete-semantic-split-crf/test/";
	public String workDir(String filename) {
		return work_dir + filename;
	}
	
	public TestWzyy(String work_dir) {
		this.work_dir = work_dir;
	}
	
	void prepareFile() throws IOException {
		DataWzyy.generateFrontLabel(workDir("test.orig"), workDir("1.front.af"));
		DataWzyy.manualFileToAF(workDir("test.orig"), workDir("9.man.af"));
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
		TestWzyy tw = new TestWzyy("res/data/complete-semantic-split-crf/test/");
		tw.prepareFile();
		tw.test("crf.model", "test.2500.result");
		tw.test("crf.500.model", "test.500.result");
		tw.test("crf.1000.model", "test.1000.result");
		tw.test("crf.1500.model", "test.1500.result");
		tw.test("crf.2000.model", "test.2000.result");
	}
}
