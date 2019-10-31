package application.qav2.process_answer.entity;

import platform.annotate.AnnotateByDict;
import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.mltools.crfpp.ReadableFileHandler;
import platform.util.log.Trace;

public class TrainAnswerEnitity {

	static final Trace t = new Trace();
	String work_dir = "res/data/answer-entity/train/";
	public String workDir(String filename) {
		return work_dir + filename;
	}
	
	public TrainAnswerEnitity(String work_dir) {
		this.work_dir = work_dir;
	}
	
	void prepareFile () throws Exception {
		// prepare dict label
		new AnnotateByDict(1).annotateFile(workDir("train.orig"), workDir("1.dict.af"), "utf8");
		// refine
		ReadableFileHandler.refine(workDir("9.man.af"));
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
//		TrainAnswerEnitity tae = new TrainAnswerEnitity("res/data/answer-entity/train-without-punc/");
//		tae.prepareFile();
//		tae.train("crf.nopunc.model");
//		tae = new TrainAnswerEnitity("res/data/answer-entity/train/");
//		tae.prepareFile();
//		tae.train("crf.model");
		
		//2016.1.5 test
		TrainAnswerEnitity tae = new TrainAnswerEnitity("res/data/answer-entity/train-lhx/");
		//tae.prepareFile();
		tae.train("crf.model6");
		
	
	}

}
