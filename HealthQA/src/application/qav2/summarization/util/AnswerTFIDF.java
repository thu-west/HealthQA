package application.qav2.summarization.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import application.qav2.ao.Keyword;
import application.qav2.ao.ScoredAnswer;
import platform.mltools.TFIDFProcessor;
import platform.nlp.ao.Word;

public class AnswerTFIDF {

	public static HashMap<String, Double>[] tfidf ( List<ScoredAnswer> answers  ) {
		
		// get documents
		String[][] documents = new String[answers.size()][];
		for( int i=0; i<answers.size(); i++ ) {
			int len = 0;
			for ( Word[] sent : answers.get(i).answer.seg_content ){
				for( Word w : sent ){
					if( w.pos.matches("n.*")
							|| w.pos.matches("v.*")
							|| w.pos.matches("a.*")
							|| w.pos.matches("b.*")
							|| w.pos.matches("z.*")
							|| w.pos.matches("d.*")){
						len++;
					}
				}
			}
			documents[i] = new String[len];
			len = -1;
			for ( Word[] sent : answers.get(i).answer.seg_content ){
				for ( Word w : sent ){
					if( w.pos.matches("n.*")
							|| w.pos.matches("v.*")
							|| w.pos.matches("a.*")
							|| w.pos.matches("b.*")
							|| w.pos.matches("z.*")
							|| w.pos.matches("d.*")){
						documents[i][++len] = w.cont;
					}
				}
			}
		}
		
		TFIDFProcessor a_ti = null;
		try {
			a_ti = new TFIDFProcessor(documents, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HashMap<String, Double>[] tfidf = a_ti.documents_tfidf;
		// add to keywords
		for( int i=0; i<tfidf.length; i++) {
			int term_num = tfidf[i].size();
			int kw_num = term_num/10 < 10 ? (term_num<10?term_num:10) : term_num/10;
			answers.get(i).kws_scored = new Keyword[kw_num];
			ArrayList<Entry<String, Double>> temp = new ArrayList<Entry<String, Double>>(tfidf[i].entrySet());
			Collections.sort(temp, new Comparator<Entry<String, Double>>() {
				@Override
				public int compare(Entry<String, Double> arg0,
						Entry<String, Double> arg1) {
					return arg1.getValue().compareTo(arg0.getValue());
				}
			});
			for( int j=0; j<kw_num; j++) {
				answers.get(i).kws_scored[j] = new Keyword(temp.get(j).getKey(), (float)(double)(temp.get(j).getValue()));
			}
			float weight_sum = 0;
			for( Keyword k : answers.get(i).kws_scored ) {
				weight_sum += k.weight;
			}
			for( Keyword k : answers.get(i).kws_scored ) {
				k.weight /= weight_sum;
			}
		}
		
		return a_ti.documents_tfidf;
	}
}

//	private static HashMap<String, List> specialWordOfQ = new HashMap<String, List>(); // 提取所查询问题中词性特殊的词作为关键字，以<词性符号，词语集>形式存储
//	private static HashMap<String, HashMap<String, Float>> NormalTfOfAnswerI = new HashMap<String, HashMap<String, Float>>(); // 记录每一个Answer归一化后的TF，包括Answer_ID,word,normaltf(归一化)
////	private static HashMap<String, Float> dfOfAnswerList = new HashMap<String, Float>(); // 记录所有Answer的DF，记录形式《word,df》
//	private static HashMap<String, Float> idfOfAnswerList = new HashMap<String, Float>(); // 记录所有Answer的idf,记录形式《word,idf》
//	private static HashMap<String, HashMap<String, Float>> TfIdfOfAnswerI = new HashMap<String, HashMap<String, Float>>(); // 记录每一个Answer的tf-idf，记录形式《Answer_ID,<word,tf_idf>>
//	public static HashMap<String, HashMap<String, Float>> TfOfAnswerI = new HashMap<String, HashMap<String, Float>>(); // 记录每一个Answer的tf-idf，记录形式《Answer_ID,<word,tf_idf>>
//	public static HashMap<String, HashMap<String, Float>> IdfOfAnswerI = new HashMap<String, HashMap<String, Float>>(); // 记录每一个Answer的tf-idf，记录形式《Answer_ID,<word,tf_idf>>
//
//	public static Map<String, List> SpecialWordsFind(String q) // 输入所查询问题，选取特定词性词语构成specialWordOfQ
//			throws IOException {
//		Word[][] Seg_Q = Platform.segment(q);
//		List<String> noun = new ArrayList<String>(); // 存储名词相关
//		List<String> verb = new ArrayList<String>(); // 存储动词相关
//		List<String> number = new ArrayList<String>(); // 存储数词相关
//		List<String> r = new ArrayList<String>();// 存储疑问词等代词
//		for (int i = 0; i < Seg_Q.length; i++) {
//			if (Seg_Q[i] != null) {
//				for (int j = 0; j < Seg_Q[i].length; j++) {
//					if (Seg_Q[i][j].pos.equals("n")
//							|| Seg_Q[i][j].pos.equals("b")
//							|| Seg_Q[i][j].pos.equals("nt")
//							|| Seg_Q[i][j].ne.equals("S")) { // 名词: n(noun)
//						// b(other-noun-modifier)，nt(temporal-noun) ne=s(命名实体)
//						if (!noun.contains(Seg_Q[i][j].cont)) {
//							noun.add(Seg_Q[i][j].cont);
//						}
//					} else if (Seg_Q[i][j].pos.equals("v")) { // 动词v(verb)
//						if (!verb.contains(Seg_Q[i][j].cont)) {
//							verb.add(Seg_Q[i][j].cont);
//						}
//					} else if (Seg_Q[i][j].pos.equals("m")) { // 数词m(number)
//						if (!number.contains(Seg_Q[i][j].cont)) {
//							number.add(Seg_Q[i][j].cont);
//						}
//					} else if (Seg_Q[i][j].pos.equals("r")) { // 代词r，主要为寻找疑问代词(pronoun)
//						if (!r.contains(Seg_Q[i][j].cont)) {
//							r.add(Seg_Q[i][j].cont);
//						}
//					}
//				}
//			}
//			specialWordOfQ.put("noun", noun);
//			specialWordOfQ.put("verb", verb);
//			specialWordOfQ.put("number", number);
//			specialWordOfQ.put("r", r);
//		}
//		return specialWordOfQ;
//	}
//
//	public static HashMap<String, HashMap<String, Float>> normalTF(
//			List<Answer> alist) { // 对每个答案求归一化的TF
//		HashMap<String, Integer> tempTf = new HashMap<String, Integer>();
//		Iterator<Answer> ita = alist.iterator();
//		int wordNum = 0;
//		int wordtf = 0;
//		while (ita.hasNext()) { // 对所有Answer循环
//			Answer tempAi = ita.next();
//			wordNum = 0;
//			wordtf = 0;
//			tempTf.clear();
//			for (int i = 0; i < tempAi.content.length; i++) { // 对Answer中的句子循环
//				for (int j = 0; j < tempAi.content[i].length; j++) { // 对句子中的词语循环
//					tempAi.content[i][j].cont = tempAi.content[i][j].cont
//							.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", "");
//					if (!tempAi.content[i][j].cont.equals("")) {
//						wordtf = 0;
//						for (int p = 0; p < tempAi.content.length; p++) { // 此二层循环为遍历特定Answer的所有词统计频率
//							for (int q = 0; q < tempAi.content[p].length; q++) {
//								if (i != p || j != q) {
//									if (tempAi.content[i][j].cont
//											.equals(tempAi.content[p][q].cont)) {
//										tempAi.content[p][q].cont = "";
//										wordtf++;
//										// System.out.println("i:"+i+" j:"+j+" w"+tempAi.content[i][j].cont+" wt: "+wordtf+" wordlen:"+tempAi.content[i][j].cont.length());
//									}
//								}
//							}
//						}
//						tempTf.put(tempAi.content[i][j].cont, ++wordtf); // 没有归一化
//						wordNum += wordtf;
//						// System.out.println("Word" + tempAi.content[i][j].cont
//						// + " tf: "+ tempTf.get(tempAi.content[i][j].cont));
//						tempAi.content[i][j].cont = "";
//					}
//
//				}
//			}
//			HashMap<String, Float> temp = new HashMap<String, Float>();
//			for (String word : tempTf.keySet()) {
//
//				Float normal = new Float(tempTf.get(word)) / wordNum;
//				temp.put(word, normal);
//				// TfOfAnswerI.put(tempAi.ID, tempTf);
//				NormalTfOfAnswerI.put(tempAi.ID, temp); // 归一化
//
//			}
//		}
//
//		return NormalTfOfAnswerI;
//	}
//
//	public static HashMap<String, Float> IDF(List<Answer> alist) { // 求答案集每个词的idf
//		int D = alist.size();
//		int Dt = 0;
//		List<String> located = new ArrayList<String>();
//		if (NormalTfOfAnswerI.size() == 0) {
//			normalTF(alist);
//		}
//		for (String ID : NormalTfOfAnswerI.keySet()) { // 对AnswerList循环
//			for (String word : NormalTfOfAnswerI.get(ID).keySet()) { // 对每一个Answer的word循环
//				if (!located.contains(word)) { // 统计word出现的次数
//					Dt = 1;
//					located.add(word);
//					for (String ID2 : NormalTfOfAnswerI.keySet()) {
//						if (!ID.equals(ID2)) {
//							HashMap<String, Float> temp2 = NormalTfOfAnswerI
//									.get(ID2);
//							if (temp2.keySet().contains(word)) {
//								Dt = Dt + 1;
//								continue;
//							}
//						}
//					}
//				//	dfOfAnswerList.put(word, new Float(Dt / D));
//					idfOfAnswerList.put(word,
//							(float)MyMath.log((double)D / (1 + Dt), 10));
//					// idfOfAnswerList.put(word, Log.log(D / (1 + Dt), 10)); //
//					// 公式IDF＝log(|D|/(1+|Dt|))，其中|D|表示文档总数，|Dt|表示包含关键词t的文档数量。
//					// idf.put(word, Log.log( D / (1+Dt), 10));
//				}
//			}
//
//		}
//		return idfOfAnswerList;
//	}
//
//	public static HashMap<String, HashMap<String, Float>> tfIdf(
//			List<Answer> alist) { // 对每个答案求tf-idf
//		if (NormalTfOfAnswerI.size() == 0) {
//			normalTF(alist);
//		}
//		if (idfOfAnswerList.size() == 0) {
//			IDF(alist);
//		}
//		Float tfidf;
//		HashMap<String, Float> temp = new HashMap<String, Float>();
//		HashMap<String, Float> temp_tf = new HashMap<String, Float>();
//		HashMap<String, Float> temp_idf = new HashMap<String, Float>();
//		for (String ID : NormalTfOfAnswerI.keySet()) { // 对所有Answer的词循环
//			temp.clear();
//			for (String word : NormalTfOfAnswerI.get(ID).keySet()) { // 对Answer中的每个词循环
//				HashMap<String, Float> temp2 = new HashMap<String, Float>();
//				temp2 = NormalTfOfAnswerI.get(ID);
//				float tf = temp2.get(word);
//				float idf = idfOfAnswerList.get(word);
//				tfidf = tf*idf;
//				temp_tf.put(word, tf);
//				temp_idf.put(word, idf);
//				temp.put(word, tfidf);
//			}
//			HashMap<String, Float> tempForInput = new HashMap<String, Float>();
//			HashMap<String, Float> tempTfForInput = new HashMap<String, Float>();
//			HashMap<String, Float> tempIdfForInput = new HashMap<String, Float>();
//			for (String wt : temp.keySet()) {
//				Float ti = new Float(temp.get(wt));
//				float tf = temp_tf.get(wt);
//				float idf = temp_idf.get(wt);
//				tempForInput.put(wt, ti);
//				tempTfForInput.put(wt, tf);
//				tempIdfForInput.put(wt, idf);
//			}
//			TfIdfOfAnswerI.put(ID, tempForInput);
//			TfOfAnswerI.put(ID, tempTfForInput);
//			IdfOfAnswerI.put(ID, tempIdfForInput);
//		}
//		return TfIdfOfAnswerI;
//	}
//	
//	
//	
//	public static void main(String[] args) {
//		try {
//			//	String Q = "老年糖尿病肾病的症状有哪些？";
//				String Q = "1型糖尿病和2型糖尿病有什么区别？";
//				List<Question> qlist = Platform.findBasicReleventQuestions(Q,Config.maxSearchResultCount);
//				List<Answer> alist = Platform.findAllAnswersForQuestions(qlist);
//				
//				alist=DumpRemove.AnsdupRemove(alist); //去除原问题答案完全一致的Answer
//				
////				Iterator<Answer> ita = alist.iterator();
////				while(ita.hasNext()){
////					Answer a  = ita.next();
////					System.out.println(a.toString());
////				}
////				
////				System.out.println("=========================================================================");
////				//根据词性获取所查询问题的关键字
////				Map<String, List> specialWord = KeyWordExtract.SpecialWordsFind(Q);
////				for (String word : specialWord.keySet()) {             //提取查询问题的关键字
////					System.out.println(word +": "+ specialWord.get(word)); 
////				}
////				
////				//基于DF提取所有答案集的关键字
////				//基于TF-TDF提取每个答案的TF-IDF
////				
//////				System.out.println("=========================================================================");
//////				//查看TF
//////				HashMap<String, HashMap<String, Float>> Tf = KeyWordExtract.normalTF(alist);
//////				for(String ID: Tf.keySet()){
//////					System.out.println("ID:" + ID +"  word+tf:"+Tf.get(ID));
//////				}
////				
////				System.out.println("=========================================================================");
////				//查看IDF
////				HashMap<String, Float> IDF = KeyWordExtract.IDF(alist);
////				for(String word: IDF.keySet()){
////					System.out.println("word:" + word +"  idf:"+IDF.get(word));
////				}
////				
//				System.out.println("=========================================================================");
//				//查看tfidf
//				HashMap<String, HashMap<String, Float>> tfidf = AnswerTFIDF.tfIdf(alist);
//				int i=0;
//				for(String ID: tfidf.keySet()){
//					BufferedWriter bw = new BufferedWriter( new FileWriter( "D:\\wokao\\"+(++i)+".xls"));
//					bw.write( findAnswerByID(ID, alist));
//					bw.newLine();
//					List<Map.Entry<String,Float>> sort=new ArrayList();
//					sort.addAll(tfidf.get(ID).entrySet());  
//			        ValueComparator vc=new ValueComparator();  
//			        Collections.sort(sort,vc);  
//					for( Map.Entry<String,Float> entry: sort ){
//						String word = entry.getKey();
//						String temp = word + "\t\t\t" + tfidf.get(ID).get(word) + "\t" + AnswerTFIDF.TfOfAnswerI.get(ID).get(word)
//								+ "\t" + AnswerTFIDF.IdfOfAnswerI.get(ID).get(word);
//						bw.write(temp);
//						bw.newLine();
//					}
//					bw.close();
//				}
//				
//				System.out.println("done");
//				
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	}
//	
//	public static String findAnswerByID( String ID, List<Answer> as ) {
//		for( Answer a : as ){
//			if( a.ID.equals(ID))
//				return a.raw_content;
//		}
//		return null;
//	}
//	
//	private static class ValueComparator implements Comparator<Map.Entry<String, Float>>    
//    {    
//        public int compare(Map.Entry<String, Float> mp1, Map.Entry<String, Float> mp2)     
//        {
//        	return (mp2.getValue() - mp1.getValue())>=0?1:-1;
//        }    
//    }

