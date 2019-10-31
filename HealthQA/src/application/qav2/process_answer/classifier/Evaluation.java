package application.qav2.process_answer.classifier;

import java.io.*;
import java.util.*;
import application.qav2.process_answer.entity.AnswerEntity;
import application.qav2.process_answer.wzyy_crf.Wzyy;

public class Evaluation {
	
	static Map<String, Integer> map = new HashMap<String, Integer>();
	static Map<Integer, String> label = new HashMap<Integer, String>();
	static {
		map.put("d",0); label.put(0,"d");
		map.put("i",1); label.put(1,"i");
		map.put("k",2); label.put(2,"k");
		map.put("m",3); label.put(3,"m");
		map.put("y",4); label.put(4,"y");
		map.put("t",5); label.put(5,"t");
		map.put("fw",6); label.put(6,"fw");
		map.put("ab",7); label.put(7,"ab");
//		map.put("cq",6); label.put(6,"cq");
	}
	
	static void generateTest() throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("res/data/minimal-piece-classification/test/test.raw"), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("res/data/minimal-piece-classification/test/test.orig"), "utf8"));
		String line = null;
		while( (line=br.readLine()) != null ) {
			String entity_line = AnswerEntity.annotate(line);
			String wzyy_line = Wzyy.annotate(entity_line);
			String[] piece = wzyy_line.split("。");
			for(String p : piece ) {
				bw.write(p+"。");
				bw.newLine();
			}
		}
		br.close();
		bw.close();
	}
	
	static void test() throws Exception {
		Classifier t = new Classifier(LabelSet.TOP, LabelSet.TOP_LABEL_SET);
		t.test("res/data/minimal-piece-classification/test/test.orig.man", "res/data/minimal-piece-classification/test/test.result");
	}
	
	public static void evaluation (String filename ) throws IOException {
		int[][] confusion_matrix = new int[label.size()][label.size()];
		int[] predict_stat = new int[label.size()];
		int[] gold_stat = new int[label.size()];
		float[][] eval = new float[label.size()][3];  // precision, recall, f-score
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename), "utf8"));
		String line = null;
		while( (line=br.readLine()) != null ) {
			if(line.isEmpty()) continue;
			String[] ss = line.split("\t");
			String gold = ss[0];
			String predict = ss[1];
			if(map.containsKey(predict)&& map.containsKey(gold)) {
				confusion_matrix[map.get(predict)][map.get(gold)]++;
				predict_stat[map.get(predict)]++;
				gold_stat[map.get(gold)]++;
			}
		}
		for( int i=0; i<eval.length; i++) {
			eval[i][0] = confusion_matrix[i][i]/(float)predict_stat[i]; // precision
			eval[i][1] = confusion_matrix[i][i]/(float)gold_stat[i]; // recall
			eval[i][2] = 2*(eval[i][0]*eval[i][1])/(eval[i][0]+eval[i][1]); // f1-score
		}
		
		float p = 0, r=0, f=0;
		for(int i=0; i<eval.length; i++) {
			float[] aeval = eval[i];
			String temp = label.get(i);
			System.out.println(temp+"\t"+aeval[0]+"\t"+aeval[1]+"\t"+aeval[2]);
			p+=aeval[0];
			r+=aeval[1];
			f+=aeval[2];
		}
		System.out.println("aver\t"+(p/eval.length)+"\t"+(r/eval.length)+"\t"+(f/eval.length));
		br.close();
	}
	
	public static void main(String[] args) throws Exception {
//		generateTest();
//		test();
		evaluation("res/data/minimal-piece-classification/test/test.result");
	}

}
