package platform.mltools.crfpp;

import java.io.*;

import platform.GlobalSettings;
import platform.util.MyFile;
import platform.util.log.Trace;

public class CRF {
	static Trace t = new Trace().setValid(false, true);
	
	static String crf_test_exe = GlobalSettings.contextDir("lib/CRF/crf_test.exe");
	static String crf_learn_exe = GlobalSettings.contextDir("lib/CRF/crf_learn.exe");
	static String console_encode = GlobalSettings.OS.contains("win")?"GBK":"utf8";
	
	String crf_model = null;
	
	public CRF(String model_path ) throws IOException {
		MyFile.exist(model_path);
		crf_model = model_path;
	}
	
	public String test ( String input_file ) throws Exception {
		MyFile.exist(input_file);
		Process p = null;
		if(GlobalSettings.OS.contains("win")) {
			p = Runtime.getRuntime().exec(crf_test_exe + " -m "+crf_model+" "+input_file);
		} else {
			// FIXME
			p = Runtime.getRuntime().exec(GlobalSettings.contextDir("lib/CRF++-0.58/crf_test")+" -m "+crf_model+" "+input_file);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader( p.getInputStream(), "utf8"));
		StringBuffer sb = new StringBuffer();
		String line = null;
		String pre_label = "";
		String curr_label = "";
		boolean in_mode = false;
		while( (line=br.readLine())!= null ) {
			if(line.isEmpty()) {
				if(in_mode) {
					in_mode=false;
					sb.append("\\"+pre_label+" ");
				}
				continue;
			}
			String[] ss = line.split("\t");
			curr_label = ss[ss.length-1];
			if(curr_label.equals("O")) {
				if(in_mode) {
					in_mode=false;
					sb.append("\\"+pre_label+" ");
				}
				sb.append(ss[0]);
			} else if(curr_label.startsWith("B")) {
				if(in_mode) {
					sb.append("\\"+pre_label);
				}
				sb.append(" "+ss[0]);
				pre_label = curr_label.substring(1);
				in_mode = true;
			} else if(curr_label.startsWith("I")) {
				sb.append(ss[0]);
			}
		}
		br.close();
		return sb.toString().replaceAll("^[ ]+", "").replaceAll("[ ]+$", "");
	}
	
	public void test ( String input_file, String output_file ) throws Exception {
		MyFile.exist(input_file);
		MyFile.exist(output_file);
		Process p = null;
		if(GlobalSettings.OS.contains("win")) {
			p = Runtime.getRuntime().exec(crf_test_exe + " -m "+crf_model+" "+input_file);
		} else {
			p = Runtime.getRuntime().exec("lib/CRF++-0.58/crf_test"+ " -m "+crf_model+" "+input_file);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader( p.getInputStream(), console_encode));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(output_file+".tmp"), "utf8"));
		StringBuffer sb = new StringBuffer();
		String line = null;
		String pre_label = "";
		String curr_label = "";
		boolean in_mode = false;
		while( (line=br.readLine())!= null ) {
			bw.write(line);
			bw.newLine();
		}
		br.close();
		bw.close();
		restoreFileReadable(output_file+".tmp", output_file);
	}
	
	public static void restoreFileReadable(String input, String output) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(input), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(output), "utf8"));
		String line = null;
		String pre_label = "";
		String curr_label = "";
		boolean in_mode = false;
		StringBuffer sb = new StringBuffer();
		while( (line=br.readLine())!= null ) {
			if(line.isEmpty()) {
				if(in_mode) {
					in_mode=false;
					sb.append("\\"+pre_label+" ");
				}
				t.debug(sb.toString().replaceAll("^[ ]+", "").replaceAll("[ ]+$", ""));
				bw.write(sb.toString().replaceAll("^[ ]+", "").replaceAll("[ ]+$", ""));
				bw.newLine();
				t.debug(pre_label);
				t.debug(curr_label);
				sb = new StringBuffer();
			}
//			t.debug(line);
			String[] ss = line.split("\t");
			curr_label = ss[ss.length-1];
			if(curr_label.equals("O")) {
				if(in_mode) {
					in_mode=false;
					sb.append("\\"+pre_label+" ");
				}
				sb.append(ss[0]);
			} else if(curr_label.startsWith("B")) {
				if(in_mode) {
					sb.append("\\"+pre_label);
				}
				sb.append(" "+ss[0]);
				pre_label = curr_label.substring(1);
				in_mode = true;
			} else if(curr_label.startsWith("I")) {
				sb.append(ss[0]);
			}
		}
		bw.close();
		br.close();
	}

	public void learn ( String train_data, String template ) throws Exception {
		Process p = null;
		if(GlobalSettings.OS.contains("win")) {
			p = Runtime.getRuntime().exec(crf_learn_exe + " -c 10.0 "+template+" "+train_data+" "+crf_model);
		} else {
			p = Runtime.getRuntime().exec("crf_learn -c 10.0 "+template+" "+train_data+" "+crf_model);
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader( p.getInputStream(), console_encode));
		String line = null;
		boolean in_mode = false;
		while( (line=br.readLine())!= null ) {
			t.remind(line);
		}
		br.close();
	}
	
	/**
	 * 答案的实体抽取和标点
	 */
	static void test() throws Exception {
//		String model = "D:/Project/healthqa/res/data/answer/EntityWithPunc/train/model.1";
//		String train_data = "D:/Project/healthqa/res/data/answer/EntityWithPunc/train/train.o";
//		String template = "D:/Project/healthqa/res/data/answer/EntityWithPunc/train/template.1";
//		String test_data = "D:/Project/healthqa/res/data/answer/EntityWithPunc/test/test.o";
//		String result = "D:/Project/healthqa/res/data/answer/EntityWithPunc/test/result.1";
//		
		String model = "D:/Project/healthqa/res/data/answer/EntityWithPunc/train/model.1";
		String train_data = "D:/Project/healthqa/res/data/answer/EntityWithPunc/train/train.o";
		String template = "D:/Project/healthqa/res/data/answer/EntityWithPunc/train/template.1";
		String test_data = "D:/Project/healthqa/res/data/answer/EntityWithPunc/test/test.o";
		String result = "D:/Project/healthqa/res/data/answer/EntityWithPunc/test/result.1";
		
		t.remind("Learning");
		new CRF( model ).learn(train_data, template);
		
//		t.remind("Testing");
//		new CRF(model).test(test_data, result);
		
		BufferedReader br = new BufferedReader (new FileReader( result ));
		BufferedWriter bw = new BufferedWriter(new FileWriter(result+".final"));
		String line = null;
		while( (line=br.readLine()) !=null ) {
			bw.write(line.replaceAll(" #\\\\ju[ ]*", "。").replace(" #\\dou ", "，").replace(" #\\fen ", "；").replace(" #\\mao ", "：").replaceAll(" #\\\\dn[a-z]* ", "、"));
			bw.newLine();
		}
		bw.close();
		br.close();
	}
	
	public static void main(String[] args) throws Exception {
//		test();
		
//		FileUtility.compare("D:/Project/healthqa/res/data/answer/EntityWithPunc/test/result.1 - 副本.final", "D:/Project/healthqa/res/data/answer/EntityWithPunc/test/result.1.final", "D:/Project/healthqa/res/data/answer/EntityWithPunc/test/compare.o");
//		restoreFileReadable("D:/1.tmp", "D:/1.o");
	}

}
