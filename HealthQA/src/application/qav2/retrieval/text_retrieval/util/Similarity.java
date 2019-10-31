package application.qav2.retrieval.text_retrieval.util;

/** 问题：
 *	1. 标点符号也计入分词，需去除
 *	//2. 相似度的提升。
 *	3. 问题的比较，既考虑title的相似度，也考虑content？如何分配权重？
 */

import java.io.*;
import java.util.*;

import knowledge.DictGallary;
import platform.Platform;
import platform.nlp.ao.Word;
import platform.util.log.Trace;
import application.qav2.retrieval.text_retrieval.util.DynamicTrie;

public class Similarity {
	
	static final double sim_threshold = 0.9;

	static Trace t = new Trace().setValid(false, false);
//	static Trace t = new Trace();//.setValid(Config.debug, Config.remind);
	static long dict_time = 0;
	static long sim_time_1 = 0;
	static long sim_time_2 = 0;
	static long sim_time_2_1 = 0;
	static long sim_time_2_2 = 0;
	static long sim_time_2_3 = 0;
	
	static void resetTime() {
		dict_time = 0;
		sim_time_1 = 0;
		sim_time_2 = 0;
		sim_time_2_1 = 0;
		sim_time_2_2 = 0;
		sim_time_2_3 = 0;
	}
	
	// Define Stuff
	private static DynamicTrie cilinTrie = new DynamicTrie();
	private static HashMap<String, ArrayList<int[]>> cilin = new HashMap<String, ArrayList<int[]>>();
	// 同义词词林, 每一条: 词语, 对应的全部分类编号
	static{
		try {
			long stime0 = System.currentTimeMillis();
			//DynamicTrie.loadCilin(cilinTrie);
			loadCilin();
			t.debug("time for load cilin: "+(System.currentTimeMillis() - stime0));
			t.remind("load cilin");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	final static double alpha = 0.2;
	final static double beta = 0.45;
	final static double threshold = 0.2;
	final static double delta = 0.85;
	final static double singleChar = 0;
	final static double raise = 2;

	// 读入同义词词林

	private static void loadCilin() throws IOException {
		String line;
		BufferedReader fi = new BufferedReader(new FileReader(DictGallary.cilin));
		line = fi.readLine();
		while (line != null) {
			String[] strs = line.split(" ");
			for (int i = 2; i < strs.length; i++) {
				String type = strs[0].substring(0, 7);
				if (cilin.get(strs[i]) == null) {
					ArrayList<int[]> list = new ArrayList<int[]>();
					list.add(stringToInt(type));
					cilin.put(strs[i], list);
				} else
					cilin.get(strs[i]).add(stringToInt(type));
			}
			line = fi.readLine();
		}
		fi.close();
		//System.out.println("Cilin has been loaded successfully.");
	}
	
	private static int[] stringToInt(String str) {
		//1 -- 31; A -- 41; a -- 61;
		int[] code = new int[str.length()];
		char letter;
		for(int i = 0; i < str.length(); i++) {
			letter = str.charAt(i);
			if(letter >= 'A' && letter <= 'Z') code[i] = letter - 10; 
			if(letter >= 'a') code[i] = letter - 30;
			if(letter <= '9') code[i] = letter;
		}
		return code;
	}

	//读入Word类型的词语
	private static double wordSimilarity(Word Word1, Word Word2) {
		String word1 = Word1.cont, word2 = Word2.cont;
		
		if (word1.equals(word2)) 
			return 1;
		long t1 = System.currentTimeMillis();
		ArrayList<int[]> types1 = new ArrayList<int[]>();
		ArrayList<int[]> types2 = new ArrayList<int[]>();
		/**
			types1 = cilinTrie.getTypes(word1);
			types2 = cilinTrie.getTypes(word2);
		*/
			types1 = cilin.get(word1);
			types2 = cilin.get(word2);
			long t2 = System.currentTimeMillis();
			dict_time += (t2-t1);

		// 如果至少一个词语不存在
		if (types1 == null || types2 == null) {
			
			int count = 0;
			for(int i = 0; i < word1.length(); i++){
				for(int j = 0; j < word2.length(); j++){
					if(word1.charAt(i) == word2.charAt(j))
						count ++;
				}
			}

			sim_time_1 += (System.currentTimeMillis()-t2);

//			String.format("%s-%x，%s-%x", word1, )
//			System.out.println(word1 + ":" + word2);
			return count * count / word1.length() / word2.length();
			
		} else {
			double score = 0, tempScore;
			for (int[] iter1 : types1) 
				for (int[] iter2 : types2) {
				int depth = 0, length;
				while(iter1[depth] == iter2[depth] & depth < 4) 
					depth++;
				if(iter1[depth] == iter2[depth]) 
					length = 2;
				else length = 12 - 2 * depth;
				tempScore = Math.exp(-alpha * length) * Math.tanh(beta * depth);
				score = tempScore > score? tempScore : score;
			}
			sim_time_2 += (System.currentTimeMillis()-t2);
			return score;
		}
	}

	private static double raiseNounVerb(Word Word1) {
		//给名词和动词加权重score * raise (raise > 1)
		char speech = Word1.pos.charAt(0);
		switch(speech) {
		case 'v': case 'n': return raise;
		default: return 1;
		}
	}


	//读入Word[][]类型的“句子”。
	public static double pieceSimilarity(Word[][] para1, Word[][] para2, boolean printFlag) {
		resetTime();
		long tm1 = System.currentTimeMillis();
		//printFlag控制是否打印信息
		//先把段落（二维）变成句子（一维）
		//去掉重复词（仅仅判断字符串而忽略Word类型的其他信息） 
		//System.out.println("^^^^" + Word.toSimpleString(para2));
		
		long stime = System.currentTimeMillis();
		Hashtable<String, Word> union = new Hashtable<String, Word>();

		for(Word[] sent:para1) 
			for(Word w:sent) 
				if(union.get(w.cont) == null) 
					union.put(w.cont, w);
		ArrayList<Word> sent1 = new ArrayList<Word>(union.values());
		Word[] piece1 = new Word[sent1.size()];
		sent1.toArray(piece1);
		union.clear();

		for(Word[] sent:para2) 
			for(Word w:sent) 
				if(union.get(w.cont) == null) 
					union.put(w.cont, w);
		ArrayList<Word> sent2 = new ArrayList<Word>(union.values());
		Word[] piece2 = new Word[sent2.size()];
		sent2.toArray(piece2);

		//合并词语集
		for(Word[] sent:para1) for(Word w:sent) if(union.get(w.cont) == null) union.put(w.cont, w);

		
		ArrayList<Word> uw = new ArrayList<Word>(union.values());
		int size = uw.size();
		Word[] unionWords = new Word[size];
		uw.toArray(unionWords);

		//相似度向量     
		double[] score1 = new double[size];
		double[] score2 = new double[size];

		//顺序向量
		int[] order1 = new int[size];
		int[] order2 = new int[size];

		for (int i = 0; i < size; i++) {
			double score = 0, tempScore;
			int index = 0, j = 0;
//			System.out.println(Word.toSimpleString(unionWords));
//		System.out.println(Word.toSimpleString(piece1));
			for (; j < piece1.length; j++) {
				tempScore = wordSimilarity(unionWords[i], piece1[j]) * raiseNounVerb(unionWords[i]);
				
				if (tempScore > score) {
					score = tempScore;   
					index = j;
				}
			}
			score1[i] = score > threshold ? score : 0;
			order1[i] = index;
		}

		for (int i = 0; i < size; i++) {
			double score = 0, tempScore;
			int index = 0, j = 0;
			for (; j < piece2.length; j++) {
				tempScore = wordSimilarity(unionWords[i], piece2[j]) * raiseNounVerb(unionWords[i]);
				if (tempScore > score) {
					score = tempScore;
					index = j;
				}
			}
			score2[i] = score > threshold ? score : 0;
			order2[i] = index;
		}		

		/**if(printFlag) {
			System.out.println("----------------------------------------");
			for(Word[] a:para1) System.out.print(Word.toSimpleString(a));
			System.out.print("\t\t");
			for(Word[] a:para2) System.out.print(Word.toSimpleString(a));

			System.out.println("\nUnion of Words:");
			System.out.print(Word.toSimpleString(unionWords));


			System.out.print("\nscore1: ");
			for(double a:score1) System.out.print(a + " ");
			System.out.print("\t\tscore2: ");
			for(double a:score2) System.out.print(a + " ");
			System.out.print("\norder1: ");
			for(int a:order1) System.out.print(a + " ");
			System.out.print("\t\torder2:");
			for(int a:order2) System.out.print(a + " ");
			System.out.println("\n--------------------");
		}*/

		double aSum = 0, bSum = 0, cSum = 0, mSum = 0, pSum = 0;
		for (int i = 0; i < size; i++) {
			aSum += score1[i] * score1[i];
			bSum += score2[i] * score2[i];
			cSum += score1[i] * score2[i];
			mSum += Math.pow(order1[i] - order2[i], 2);
			pSum += Math.pow(order1[i] + order2[i], 2);
		}
		
		t.debug("query dict time: "+dict_time);
		t.debug("word sim time when not in cilin: "+sim_time_1);
		t.debug("word sim time when in cilin: "+sim_time_2);
		t.debug("word sim time 1 when in cilin: "+sim_time_2_1);
		t.debug("word sim time 2 when in cilin: "+sim_time_2_2);
		t.debug("total_time:"+(System.currentTimeMillis()-tm1));
		return delta * cSum / Math.sqrt(aSum * bSum) + (1 - delta) * (1 - Math.sqrt(mSum / pSum));
	}
	
	public static double computeQuesSimilarity(String str1, String str2) throws Exception {
//		loadCilin();
		long stime = System.currentTimeMillis();
		
		Word[][] words1 = Platform.segment(str1);
		Word[][] words2 = Platform.segment(str2);
		
		long t2 = System.currentTimeMillis();
		t.debug("segment time: "+(t2-stime));
		
		double temp =  pieceSimilarity(words1, words2, true);
		t.debug("piece compare total:" + (System.currentTimeMillis()-t2));
		return temp;
	}
	
	public static void main(String[] args) throws Exception {
//		String str1 = "感冒了咋办?是看医生还是?我们知道,宝。性别。女。年龄。20。病情描述。感冒了咋办。是看医生还是。我们知道,宝宝、孕妇、老人，你身边的这些人很容易感冒，有没有。鼻塞流鼻涕、喉咙痛、咳嗽、发热、全身痛，感冒了这些症状有没有。风寒/风热感冒了吃什么好的快。感冒引起的鼻塞和哮喘怎么办。感冒了去哪里治疗好。亲，39健康问答之感冒问答知识库给你最全面的实用指南。下期预告――感冒用药问答知识库，欢迎围观。http。//ask.39.net/zs/ganmao。我要进入专题提问。>>。发病时间。不清楚。";
//		String str2 = "性别。男。年龄。67。病情描述。注射胰岛素血糖降到正常是不是该减计量呀。补充问题1。(2013-04-0217。08。30)。血糖升高体温随着升高是怎么回事。发病时间。不清楚。";
		
//		String str1 = "糖尿病典型的症状是多饮（口干）、多食（易饥饿）、消瘦及尿多等，但目前好多成年性糖尿病患者症状是不典型的。";
//		String str2 = "糖尿病患者比较典型的症状是三多一少，即多饮、多食、多尿和体重减轻。";
		
		String str1 = "人怕蛇";
		String str2 = "蛇怕人";
		System.out.println(computeQuesSimilarity(str1, str2));
		System.out.println("------");
		//System.out.println(computeQuesSimilarity(str1, str2, true));
	}
}
