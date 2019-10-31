package evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;

import application.qav2.Ask;
import application.qav2.ao.Answer;

public class ManualEvaluate {
	
	public static void generate_answer_for_manual_evaluate() throws Exception {
		String[] fs = new String[] { "./res/standard_answer/1/", "./res/standard_answer/2/","./res/standard_answer/zdc/"};
		RougeInput ri = new RougeInput(fs, 300);	
		int num=ri.summary.length;	
		Answer[][] refer=new Answer[num][];
		Answer[] sum=new Answer[num];
		Answer[] ba=new Answer[num];
		String[] q=new String[num];
		sum=ri.summary; 
		ba=ri.best_answer_by_se;
		q=ri.question;
		refer=ri.standard_answer;
		for(int i=0;i<num;i++){
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"./res/manual_evaluate/" + (i+1) + ".txt"));
			bw.write("QUESTION: " + q[i]);
			bw.newLine();
			bw.newLine();
			bw.write("----------------------------------------------");
			bw.newLine();
			bw.newLine();
			bw.write("ANSWER 1");   //summary
			bw.newLine();
			bw.write(sum[i].raw_content);   //summary
			bw.newLine();
			bw.newLine();
			bw.write("----------------------------------------------");
			bw.newLine();
			bw.newLine();
			bw.write("ANSWER 2");   //best answer
			bw.newLine();
			bw.write(ba[i].raw_content);   //best answer			
			bw.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Ask.t.setValid(false, false);
		generate_answer_for_manual_evaluate();

	}

}
