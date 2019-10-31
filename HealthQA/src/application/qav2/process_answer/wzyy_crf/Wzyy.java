package application.qav2.process_answer.wzyy_crf;

import java.io.*;
import java.util.*;

import application.qav2.process_answer.entity.AnswerEntity;
import platform.GlobalSettings;
import platform.mltools.crfpp.CRF;
import platform.mltools.crfpp.CRFPPDataHandler;
import platform.mltools.crfpp.ReadableFileHandler;
import platform.util.MyFile;
import platform.util.log.Trace;

public class Wzyy {
	static final Trace t = new Trace().setValid(false, true);
	
	public static String work_dir = null;
	static String workDir(String filename) {
		return work_dir + filename;
	}
	
	static String model_path = GlobalSettings.contextDir("res/data/complete-semantic-split-crf/model/crf.model");
	
	public static String annotate(String entity_string) throws Exception {
		t.debug("input: "+entity_string);
		entity_string = entity_string.replaceAll("[，。]", "#");
		String plain_string = entity_string.replaceAll("[ ]*\\\\[a-z]+[ ]*", "");
		plain_string = annotate_plain_string(plain_string);
		t.debug("output plain: "+plain_string);
		List<String> punc_list = new ArrayList<String>();
		for(int i=0; i<plain_string.length(); i++) {
			if(plain_string.charAt(i)=='，') 
				punc_list.add("，");
			if(plain_string.charAt(i)=='。') 
				punc_list.add("。");
		}
		Iterator<String> it = punc_list.iterator();
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<entity_string.length(); i++) {
			if(entity_string.charAt(i)=='#') {
				sb.append(it.next());
			} else {
				sb.append(entity_string.charAt(i));
			}
		}
		t.debug("final entity output: "+sb.toString());
		return ReadableFileHandler.refineLine(sb.toString());
	}
	
	static String annotate_plain_string(String plain_string) throws Exception {
		UUID uuid = UUID.randomUUID();
		work_dir = "tmp/"+ uuid.toString()+"/";
		File f = new File(work_dir);
		f.mkdirs();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(workDir("input.orig")), "utf8"));
		bw.write(plain_string);
		bw.close();
		
		DataWzyy.generateFrontLabel(workDir("input.orig"), workDir("1-wzyy-front.af"));
		DataWzyy.manualFileToAF(workDir("input.orig"), workDir("10-wzyy.af"));
		CRFPPDataHandler cdh = new CRFPPDataHandler(work_dir);
		cdh.transferAllAggFilesToSingleSegFiles();
		cdh.combineAllSingleSegFilesToMultiSegFile("input.msf");
		
		CRF crf = new CRF(model_path);
		String r = crf.test(workDir("input.msf"));
		
		t.debug("deleting "+work_dir+", "+MyFile.delete(work_dir));
		
		return r.replaceAll("[ ]*#\\\\dou[ ]*", "，").replaceAll("[ ]*#\\\\ju[ ]*", "。");
	}
	
	public static void main(String[] args) throws Exception {
//		String s = "有糖尿病的话去旅行一般是没有什么危险的。但是有一些注意事项。首先是要注意检测血糖的仪器做自我监测。其次是一定要在包里备好药物或者是胰岛素。并且要另外备一些糖块以防止低血糖。去旅游的时候还是得注意饮食的规律并且运动量如果大了要适当补充糖分。根据你的情况糖尿病人如果控制好血糖，按时用药控制饮食和正常人是一样的不限制出游。根据你的描述建议查血糖在正常范围内，可以出游但要注意不要太劳累注意饮食和按时吃药。";
	    String s= "感冒是很常见的，主要是上呼吸道防疫功能弱的原因，平时注意多锻炼，注意饮食不要偏食。就会减少感冒次数及减轻严重程度。";
		s = AnswerEntity.annotate(s);
		String s1 = Wzyy.annotate(s);
		String[] answer_pieces = s1.replace("。", "。###").split("###");
		System.out.println(answer_pieces.length);
		
//		System.out.println(s);
//		System.out.println("---------------------");
//		String r = annotate(s);
//		System.out.println(r);
	}

}
