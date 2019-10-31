package application.qav2.process_answer.entity;

import java.io.*;
import java.util.UUID;

import platform.GlobalSettings;
import platform.annotate.AnnotateByDict;
import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.mltools.crfpp.ReadableFileHandler;
import platform.util.MyFile;
import platform.util.log.Trace;

public class AnswerEntity {
	
	static final Trace t = new Trace().setValid(false, true);
	static String work_dir = null;
	static String workDir(String filename) {
		return work_dir + filename;
	}
	
	static String model_path = GlobalSettings.contextDir("res/data/answer-entity/model/crf.model");
	static AnnotateByDict ad = null;
	
	static {
		try {
			ad = new AnnotateByDict(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	static String restorePuncs ( String anno_answer ) throws Exception  {
		anno_answer = anno_answer.replaceAll(" #\\\\dn[a-z]* ", "、");
		anno_answer = anno_answer.replaceAll(" #\\\\mao ", "：");
		anno_answer = anno_answer.replaceAll(" #\\\\fen ", "；");
		anno_answer = anno_answer.replaceAll(" #\\\\dou ", "，");
		anno_answer = anno_answer.replaceAll(" #\\\\ju[ ]*", "。");
		t.debug(anno_answer);
		anno_answer = ReadableFileHandler.refineLine(anno_answer);
		t.debug(anno_answer);
		return anno_answer;
	}
	
	public static String annotate(String input_string) throws Exception {
		UUID uuid = UUID.randomUUID();
		work_dir = "tmp/"+ uuid.toString()+"/";
		File f = new File(work_dir);
		f.mkdirs();
		
		input_string = input_string.replaceAll("[，。、：；]", "#");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(workDir("input.orig")), "utf8"));
		bw.write(input_string);
		bw.newLine();
		bw.close();
		
		ad.annotateFile(workDir("input.orig"), workDir("1.dict.af"), "utf8");
		CRFPPDataHandler cdh = new CRFPPDataHandler(work_dir);
		cdh.transferAllAggFilesToSingleSegFiles();
		cdh.combineAllSingleSegFilesToMultiSegFile("input.msf");
		
		CRF crf = new CRF(model_path);
		String r = crf.test(workDir("input.msf"));
		
		t.debug("deleting "+work_dir+", "+MyFile.delete(work_dir));
		t.debug(r);
		return restorePuncs(r);
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(
				annotate("头皮麻，发紧，疑脑血栓近一周晨及上午头晕，头皮麻，发紧像有一道箍，并还好发 。\\0本入2004年糖尿病至今，疑脑血拴。请问是否是脑血栓症状，应如何预防。")
				);
		
//		char a = 0;
//		StringBuffer sb = new StringBuffer();
//		sb.append("发撒的发").append(a).append("范德萨发神");
//		
//		System.out.println(sb.toString().replaceAll("\\s", ""));
	}

}
