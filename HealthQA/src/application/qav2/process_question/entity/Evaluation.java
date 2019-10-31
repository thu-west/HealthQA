package application.qav2.process_question.entity;

import java.io.*;
import java.util.*;

public class Evaluation {
	
	static Map<String, Integer> map = new HashMap<String, Integer>();
	static Map<Integer, String> label = new HashMap<Integer, String>();
	static {
		map.put("O", 0); label.put(0, "O");
		map.put("d", 1); label.put(1, "d");
		map.put("m", 2); label.put(2, "m");
		map.put("c", 3); label.put(3, "c");
		map.put("t", 4); label.put(4, "t");
		map.put("o", 5); label.put(5, "o");
		map.put("os", 6); label.put(6, "os");
		map.put("i", 7); label.put(7, "i");
		map.put("is", 8); label.put(8, "is");
//		map.put("gm", 9); label.put(9, "gm");
//		map.put("gmt", 10); label.put(10, "gmt");
//		map.put("g", 11); label.put(11, "g");
//		map.put("tt", 12); label.put(12, "tt");
//		map.put("l", 13); label.put(13, "l");
//		map.put("a", 14); label.put(14, "a");
//		map.put("x", 15); label.put(15, "x");
		map.put("s", 5);
		map.put("ss", 6);
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
			String gold = ss[ss.length-2].replaceAll("[IB]", "");
			String predict = ss[ss.length-1].replaceAll("[IB]", "");
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
	
	public static void main(String[] args) throws IOException {
		
//		System.out.println("----dict");
//		evaluation("res/data/question-entity/test/test.result.dict.tmp");
		System.out.println("----word");
		evaluation("res/data/question-entity/test/test.result.word.tmp");
		System.out.println("----word+postag");
		evaluation("res/data/question-entity/test/test.result.word.postag.tmp");
		System.out.println("----word+dict");
		evaluation("res/data/question-entity/test/test.result.word.dict.tmp");
		System.out.println("----all");
		evaluation("res/data/question-entity/test/test.result.tmp");
	}

}
