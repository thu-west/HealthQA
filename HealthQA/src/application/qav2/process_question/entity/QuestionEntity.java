package application.qav2.process_question.entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;
import platform.GlobalSettings;
import platform.annotate.AnnotateByDict;
import platform.annotate.AnnotateByPostag;
import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.util.MyFile;
import platform.util.log.Trace;

public class QuestionEntity {
	
	static final Trace t = new Trace().setValid(false, true);
	static String work_dir = null;
	public static String workDir(String filename) {
		return work_dir + filename;
	}
	
	static String model_path = GlobalSettings.contextDir("res/data/question-entity/model/crf.model");
	static AnnotateByDict ad;
	static {
		try {
			ad = new AnnotateByDict(0);
		} catch (Exception e) {
			System.exit(0);
			e.printStackTrace();
		}
	}
	
	public static String annotate( String input_string ) throws Exception {
		UUID uuid = UUID.randomUUID();
		work_dir = "tmp/"+ uuid.toString()+"/";
		File f = new File(work_dir);
		f.mkdirs();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(workDir("input.orig")), "utf8"));
		bw.write(input_string);
		bw.close();
		// anno with dict
		ad.annotateFile(workDir("input.orig"), workDir("1.dict.af"), "utf8");
		System.out.println(workDir("input.orig"));
		// anno with postag
		AnnotateByPostag.label(workDir("input.orig"), workDir("2.postag.af"));
		// transfer to crf++ format
		CRFPPDataHandler td = new CRFPPDataHandler(work_dir);
		td.transferAllAggFilesToSingleSegFiles();
		td.combineAllSingleSegFilesToMultiSegFile("input.msf");
		// annotate
		CRF crf =  new CRF(model_path);
          String r = crf.test(workDir("input.msf"));
//		t.debug("deleting "+work_dir+", "+MyFile.delete(work_dir));
          
		
		return r;
	}
	
	public static void main(String[] args) throws Exception {
		String r = annotate("头皮麻，发紧，疑脑血栓近一周晨及上午头晕，头皮麻，发紧像有一道箍，并还好发 。本入2004年糖尿病至今，疑脑血拴。请问是否是脑血栓症状，应如何预防。");
		String string = annotate("空腹血糖7.72，现在吃二甲双胍，一天三次一次两片，可以吗。");
		
		System.out.println(string);
	}
}
