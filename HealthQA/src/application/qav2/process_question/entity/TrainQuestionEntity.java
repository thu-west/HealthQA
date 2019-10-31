package application.qav2.process_question.entity;

import platform.annotate.AnnotateByDict;
import platform.annotate.AnnotateByPostag;
import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.mltools.crfpp.ReadableFileHandler;
import platform.util.log.Trace;

public class TrainQuestionEntity {
	static final Trace t = new Trace();
	String work_dir = "res/data/question-entity/train/";
	public String workDir(String filename) {
		return work_dir + filename;
	}
	
	public TrainQuestionEntity(String _work_dir) {
		work_dir = _work_dir;
	}
	
	void prepareFile () throws Exception {
		// anno with dict
		AnnotateByDict ad = new AnnotateByDict(0);
		ad.annotateFile(workDir("raw"), workDir("1.dict.af"), "utf8");
		// anno with postag
		AnnotateByPostag.label(workDir("raw"), workDir("2.postag.af"));
		// refine manual aggregate file
		ReadableFileHandler.refine(workDir("9.man.af"));
	}
	
	void train(String model_name) throws Exception {
		// transfer to crf++ format
		CRFPPDataHandler td = new CRFPPDataHandler(work_dir);
		td.transferAllAggFilesToSingleSegFiles();
		td.combineAllSingleSegFilesToMultiSegFile("train.msf");
		
		CRF crf =  new CRF(workDir("../model/"+model_name));
		crf.learn(workDir("train.msf"), workDir("template.4"));
	}
	
	public static void main(String[] args) throws Exception {
//		TrainQuestionEntity tqe = new TrainQuestionEntity("res/data/question-entity/train/");
//		tqe.prepareFile();
//		tqe.train("crf.model");
		
		TrainQuestionEntity tqe = new TrainQuestionEntity("res/data/question-entity/train-word/");
		tqe.train("crf.model.word");
		t.remind("finish word");
		TrainQuestionEntity tqe1 = new TrainQuestionEntity("res/data/question-entity/train-word-dict/");
		tqe1.train("crf.model.word.dict");
		t.remind("finish word-dict");
		TrainQuestionEntity tqe2 = new TrainQuestionEntity("res/data/question-entity/train-word-postag/");
		tqe2.train("crf.model.word.postag");
		t.remind("finish word-postag");
		TrainQuestionEntity tqe3 = new TrainQuestionEntity("res/data/question-entity/train-dict/");
		tqe3.train("crf.model.dict");
		t.remind("finish dict");
	}

}
