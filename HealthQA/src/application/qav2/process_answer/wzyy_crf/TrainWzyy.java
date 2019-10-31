package application.qav2.process_answer.wzyy_crf;

import java.io.IOException;

import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.util.log.Trace;

public class TrainWzyy {
	static final Trace t = new Trace();
	
	public String work_dir = "res/data/complete-semantic-split-crf/train/";
	public String workDir(String filename) {
		return work_dir + filename;
	}
	
	public TrainWzyy(String _work_dir) {
		work_dir = _work_dir;
	}
	
	void prepareFile() throws IOException {
		DataWzyy.generateFrontLabel(workDir("train.orig"), workDir("1.front.af"));
		DataWzyy.manualFileToAF(workDir("train.orig"), workDir("9.man.af"));
		
	}
	
	void train(String model_name) throws Exception {
		// transfer to crf++ format
		CRFPPDataHandler td = new CRFPPDataHandler(work_dir);
		td.transferAllAggFilesToSingleSegFiles();
		td.combineAllSingleSegFilesToMultiSegFile("train.msf");
		
		CRF crf =  new CRF(workDir("../model/"+model_name));
		crf.learn(workDir("train.msf"), workDir("template.1"));
	}
	
	public static void main(String[] args) throws Exception {
		TrainWzyy wt = new TrainWzyy("res/data/complete-semantic-split-crf/train-2500/");
		wt.prepareFile();
		wt.train("crf.model");
//		
//		wt = new TrainWzyy("res/data/complete-semantic-split-crf/train-2000/");
//		wt.prepareFile();
//		wt.train("crf.2000.model");
//		
//		wt = new TrainWzyy("res/data/complete-semantic-split-crf/train-1500/");
//		wt.prepareFile();
//		wt.train("crf.1500.model");
		
		wt = new TrainWzyy("res/data/complete-semantic-split-crf/train-1000/");
		wt.prepareFile();
		wt.train("crf.1000.model");
//		
//		wt = new TrainWzyy("res/data/complete-semantic-split-crf/train-500/");
//		wt.prepareFile();
//		wt.train("crf.500.model");
	}
	
}
