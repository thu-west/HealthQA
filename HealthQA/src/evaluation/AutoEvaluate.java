package evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;

import application.qav2.Ask;
import application.qav2.ao.Answer;
import platform.nlp.ao.Word;



public class AutoEvaluate { //output Rouge of summary and best_answer_by_search_engine 
	
	
	public static void evaluate() throws Exception {
		// 设置存放相对应答案的三个目录
//		String[] fs = new String[] { "./res/standard_answer/151/", "./res/standard_answer/251/","./res/standard_answer/351/"};
		String[] fs = new String[] { "./res/standard_answer/1/", "./res/standard_answer/2/","./res/standard_answer/3/"};
//		String[] fs = new String[] { "./res/standard_answer/150/", "./res/standard_answer/250/","./res/standard_answer/350/"};
		RougeInput ri = new RougeInput(fs, 300);	
		int num=ri.summary.length;	
		Answer[][] refer=new Answer[num][];
		Answer[] sum=new Answer[num];
		Answer[] ba=new Answer[num];
		sum=ri.summary;
		ba=ri.best_answer_by_se;
		refer=ri.standard_answer;
		double sr1,br1;
		double sr2,br2;
		double srsu4,brsu4;
		
		System.out.println("----------------------------");
		for (Answer[] aa : ri.standard_answer) {
			for (Answer aaa : aa) {
				System.out.println(Word.toPlainString(aaa.seg_content));
			}
		}

		for (Answer aa : ri.summary) {
			System.out.println(Word.toPlainString(aa.seg_content));
		}
		
		System.out.println("----------------------------");
		
		
		//Evaluate Rouge-1 Rouge-2 Rouge-4 of summary
		sr1=RougeForAll.EvaluateRouge1(sum, refer);
		sr2=RougeForAll.EvaluateRouge2(sum, refer);
		srsu4=RougeForAll.EvaluateRougeSU4(sum, refer);
		//Evaluate Rouge-1 Rouge-2 Rouge-4 of best answer returned by search engine
		br1=RougeForAll.EvaluateRouge1(ba, refer);                
		br2=RougeForAll.EvaluateRouge2(ba, refer);
		brsu4=RougeForAll.EvaluateRougeSU4(ba, refer);
		//output
		BufferedWriter bw1 = new BufferedWriter(new FileWriter("rouge-1.txt", true));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("rouge-2.txt", true));
		BufferedWriter bw3 = new BufferedWriter(new FileWriter("rouge-3.txt", true));
		bw1.write(sr1+"\t"+br1);
		bw1.newLine();
		bw2.write(sr2+"\t"+br2);
		bw2.newLine();
		bw3.write(srsu4+"\t"+brsu4);
		bw3.newLine();
		bw1.close();
		bw2.close();
		bw3.close();
//		System.out.println("Rouge for summary:");
//		System.out.println("Rouge-1\tRouge-2\tRouge-3");
//		System.out.print(sr1);
//		System.out.println("\n");
//		System.out.println("Rouge-2:");
//		System.out.print(sr2);
//		System.out.println("\n");
//		System.out.println("Rouge-SU4:");
//		System.out.print(srsu4);
//		System.out.println("\n");
//		System.out.println("Rouge for best answer from search engine:");
//		System.out.println("Rouge-1:");
//		System.out.print(br1);
//		System.out.println("\n");
//		System.out.println("Rouge-2:");
//		System.out.print(br2);
//		System.out.println("\n");
//		System.out.println("Rouge-SU4:");
//		System.out.print(brsu4);
//		System.out.println("\n");
	}
	public static void main(String[] args) throws Exception {
		Ask.t.setValid(false, false);
		BufferedWriter bw1 = new BufferedWriter(new FileWriter("rouge-1.txt", true));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("rouge-2.txt", true));
		BufferedWriter bw3 = new BufferedWriter(new FileWriter("rouge-3.txt", true));
		bw1.write("------------------------------------");
		bw1.newLine();
		bw2.write("------------------------------------");
		bw2.newLine();
		bw3.write("------------------------------------");
		bw3.newLine();
		bw1.close();
		bw2.close();
		bw3.close();
		for(int i=0; i<10; i++) {
			System.out.println("========> "+i);
			evaluate();
		}
	}
}

