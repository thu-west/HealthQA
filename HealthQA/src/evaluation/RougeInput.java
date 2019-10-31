package evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import application.qav2.Ask;
import application.qav2.ao.Answer;
import platform.nlp.ao.Word;
import platform.util.log.Trace;

public class RougeInput {

	public String[] question;
	public Answer[] summary;
	public Answer[] best_answer_by_se;
	public Answer[][] standard_answer;

	public String[] fs;
	
	public static final int SAMPLE_NUM = 70;

	public RougeInput(String[] directories, int max_summary_length) throws Exception {
		
		// 设置存放相对应答案的三个目录
		this.fs = directories;
		try {
			gather_standard_answer(max_summary_length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void gather_standard_answer(int max_length) throws Exception {

		FileFilter ff = new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				if (arg0.isDirectory())
					return false;
				return true;
			}
		};

		for( int k=0; k<fs.length-1; k++) {
			if ( new File(fs[k]).listFiles(ff).length != new File(fs[k+1]).listFiles(ff).length ){
				System.out.println("the "+k+" and "+(k+1)+" directory's document number is not the same");
				System.exit(0);
			}
		}

		File f = new File(fs[0]);
		System.out.println("folder: "+fs[0]);
		int count = f.listFiles(ff).length;
		System.out.println("file count: " +count);

		standard_answer = new Answer[SAMPLE_NUM][fs.length];
		summary = new Answer[SAMPLE_NUM];
		question=new String[SAMPLE_NUM];
		best_answer_by_se = new Answer[SAMPLE_NUM];
		
		Integer[] rand = new Integer[SAMPLE_NUM];
		Random RAND = new Random();
		Set<Integer> set = new TreeSet<Integer>();
		
		for(int k=0; k<SAMPLE_NUM; k++) {
			int r = RAND.nextInt(count)+1;
			while(set.contains(r)) {
				r = RAND.nextInt(count)+1;
			}
			set.add(r);
		}
		rand = set.toArray(rand);
		for(int temp : rand)
			System.out.print(temp+", ");
		System.out.println();
		
		boolean summary_initialize = false;
		boolean ba_initialize = false;
		Trace t = new Trace(fs.length*SAMPLE_NUM, fs.length*SAMPLE_NUM/1);
		for (int i = 0; i < fs.length; i++) {
			for (int j = 0; j < SAMPLE_NUM; j++) {
				t.debug("processing "+fs[i] + rand[j] + ".txt", true);
				BufferedReader br = new BufferedReader(new FileReader(fs[i]
						+ rand[j] + ".txt"));
				String line = null;
				boolean flag = false;
				while ((line = br.readLine()) != null) {
					// set answer[]
					if (line.contains("QUESTION: ") && !summary_initialize) {
						line = line.replaceAll("QUESTION: ", "");
						line=line.replaceAll("(?i)[^(a-zA-Z0-9,.+;-=!?，。；?!\"\'\\[\\]\\”\\“\\‘\\’\u4E00-\u9FA5)]", "");
						String s = new Ask().ask("temp", line, max_length,"p");		
						summary[j] = new Answer(null, null, s, null);
						summary[j].segContent();
						question[j]=line;
						continue;
					}
					else if(line.equals("ANSWER 1") && !ba_initialize){
						line=br.readLine();
						if(line.length()>=max_length){
							line=line.substring(0, max_length-1);
						}
						Answer temp = new Answer(null, null, line, null);
						temp.segContent();
						best_answer_by_se[j] = temp;
						continue;
					}
					// set standard_answer
					else if (line.contains("###")) {
						line = line.replaceAll("###", "");
						Answer a = new Answer(null, null, line, null);
						a.segContent();
						standard_answer[j][i] = a;
						flag = true;
						break;
					}
				}
				if (!flag) {
					System.out.println("no standard answer in " + fs[i]
							+ (j + 1) + ".txt");
					System.exit(0);
				}
				br.close();
			}
			summary_initialize = true;
			ba_initialize = true;
		}
	}

	public static void main(String[] args) throws Exception {
		
		String[] fs = new String[] { "./res/standard_answer_test/zdc/", "./res/standard_answer_test/zy/",
		"./res/standard_answer_test/fire/" };
		
		/*
		 * output ri.summary <Answer[]>; ri.standard_answer <Answer[][]>
		 */
		RougeInput ri = new RougeInput(fs, 300);
		
		System.out.println("question");
		//String q;
		for (String q:ri.question) {
			System.out.println(q);
		}
		
		System.out.println("standard_answer");
		for (Answer[] aa : ri.standard_answer) {
			for (Answer aaa : aa) {
				System.out.println(Word.toPlainString(aaa.seg_content));
			}
		}

		System.out.println("summary");
		for (Answer aa : ri.summary) {
			System.out.println(Word.toPlainString(aa.seg_content));
		}
		
		System.out.println("best_answer_by_se");
		for (Answer a : ri.best_answer_by_se) {
			System.out.println(Word.toPlainString(a.seg_content));
		}
	}
}
