package application.qav2.summarization.util;

import java.sql.SQLException;
import java.util.*;
import application.qav2.ao.*;
import application.qav2.dao.Entity;
import application.qav2.dao.HealthqaDB;
import platform.nlp.ao.Word;
import platform.util.log.Trace;

public class RankPiece {

	static Trace t = new Trace().setValid(false, false);

	static int resetAnswerContent(ScoredAnswer a) {
		int init_len = a.answer.seg_content.length;
		ArrayList<Word[]> ss = new ArrayList<Word[]>();
		for (Word[] s : a.answer.seg_content) {

			if (s.length > 30) { // 长度大于30，尝试进行分割
				Set<Integer> split_point = new TreeSet<Integer>();
				for (int i = 2; i < s.length; i++) {
					if ((s[i].cont.equals("但") || s[i].cont.equals("但是")
							|| s[i].cont.equals("而且") || s[i].cont.equals("建议")
							|| s[i].cont.equals("祝") || s[i].cont.equals("而"))) { // 指定词前面的逗号作为分割点
						if (s[i - 1].pos.equals("wd")) {
							split_point.add(i - 1);
						}
					} else if (s[i].pos.matches("n.*")) {
						if (s[i - 1].pos.equals("wd")) { // 名词前面的逗号作为分割点
							split_point.add(i - 1);
						} else if (s[i - 2].pos.equals("wd") && // 名词前面是代词，形容词，方位词等，再前面的逗号作为分割点。
								(s[i - 1].pos.matches("r.*")
										|| s[i - 1].pos.matches("a.*")
										|| s[i - 1].pos.matches("s.*") || s[i - 1].pos
										.matches("f.*"))) {
							split_point.add(i - 2);
						}
					}
				}

				if (split_point.isEmpty()) { // 没有找到分割点，直接加入最终的句子列表
					ss.add(s);
				} else {
					Integer[] sp = new Integer[split_point.size()];
					split_point.toArray(sp); // 分割点List转换成int数组

					int begin = 0; // 标注当前的分割开始点（包含开始点）
					int end = 0; // 标注当前的分割终止点（不包含终止点）

					for (int j = 0; j <= sp.length; j++) {
						if (j == sp.length) // 如果是最后一个分割点，那么这次分割是从当前分割点+1到s的结尾
							end = s.length;
						else
							end = sp[j]; // 如果不是最后一个分割点，那么这次分割是从上次分割点+1到当前分割点
						Word[] s1 = new Word[end - begin];
						for (int k = 0; k < end - begin; k++) {
							s1[k] = s[begin + k];
						}
						begin = end + 1; // 设置下次的起始点。
						ss.add(s1);
					}
				}
			} else {
				ss.add(s);
			}
		}
		Word[][] sents = new Word[ss.size()][];
		ss.toArray(sents);
		a.answer.seg_content = sents;
		return a.answer.seg_content.length - init_len;
	}

	static ScoredPiece[] getPieces(ScoredAnswer[] answers) throws SQLException {
		List<Answer> alist = new ArrayList<Answer>();
		int total = 0;
		for(ScoredAnswer a : answers) {
			alist.add(a.answer);
		}
		List<Piece> plist = HealthqaDB.getPieceList(alist);
		for (ScoredAnswer a : answers) {
			int len_grow = resetAnswerContent(a);
			t.debug("" + len_grow);
			total += a.answer.seg_content.length;
		}
		ScoredPiece[] sents = new ScoredPiece[total];
		int i = -1;
		for (ScoredAnswer a : answers) {
			for (Word[] sent : a.answer.seg_content) {
				Piece s = new Piece(a.answer, sent);
				ScoredPiece ss = new ScoredPiece(s, a);
				sents[++i] = ss;
			}
		}
		return sents;
	}

	public static List<ScoredPiece> findTopPieces(Question input_question,
			List<ScoredAnswer> answers) throws Exception {
			  
		
		// get all pieces
//		ScoredPiece[] sents = getPieces(answers);
		List<ScoredPiece> sents = HealthqaDB.getScoredPieceList(answers);
//		List<ScoredPiece> sPieces = new ArrayList<ScoredPiece>();
		for( ScoredPiece ss : sents ){
			ss.piece.findKeywords();     // 计算每个piece的关键词
			ss.piece.findEntities();  		//计算每个piece的实体
		}
		
//		for(ScoredPiece s : sents)
//		{
//			for(Entity e1: s.piece.entities)
////				for(Entity e2:input_question.entities)
//				for(Entity e2:s.answer.answer.question.entities)
//					if(e1.name.contains(e2.name)){
//							if(sPieces.contains(s))
//								continue;
//						sPieces.add(s);
//					}
//		}
//			
//		return sPieces;
		
		
		// get tfidf
		HashMap<String, Double>[] tfidf = PieceTFIDF.tfidf(sents);

		for (int i = 0; i < tfidf.length; i++) {

			Iterator<String> itr = tfidf[i].keySet().iterator();

			// get TFIDF
			while (itr.hasNext())
				sents.get(i).TFIDF += tfidf[i].get(itr.next());

			// get POSITION
			Answer a = sents.get(i).answer.answer;
			String curr_sent = Word.toSimpleString(sents.get(i).piece.seg_content);
			int j = 0;
			for (; j < a.seg_content.length; j++) {       
				Word[] w_s = a.seg_content[j];
				if (Word.toSimpleString(w_s).equals(curr_sent))
					break;
			}
			sents.get(i).POS = 1 - (double) j / sents.get(i).answer.SN;

			// get WORD NUMBER
			sents.get(i).WN = sents.get(i).piece.seg_content.length;

			// get CHARACTER NUMBER
			sents.get(i).CN = sents.get(i).piece.raw_content.length();

			// get ANSWER SCORE
			sents.get(i).ANSWER_SCORE = sents.get(i).answer.score;

			// get QKW
			sents.get(i).QKW = 0;
			for (Keyword k1 : input_question.kws) {
				for (Keyword k2 : sents.get(i).piece.kws) {
					if (k1.word.equals(k2.word))
						sents.get(i).QKW += k1.weight;
				}
			}

			// get AKW
			sents.get(i).AKW = 0;
			for (Keyword k1 : sents.get(i).answer.kws_scored) {
				for (Keyword k2 : sents.get(i).piece.kws) {
					if (k1.word.equals(k2.word))
						sents.get(i).AKW += k1.weight;
				}
			}
			
			//get QUES_SIM
			sents.get(i).QUES_SIM = 0;
			for( Entity e1 : input_question.entities ){
				for( Entity e2 : sents.get(i).piece.entities) {
					if( e1.name.equals(e2.name) ) 
						sents.get(i).QUES_SIM = 5;   //计算piece与question的相似度
				}
			}
		}
		//liu 2016.1.7
				int count = sents.size();       
				 for (int i = count-1; i >= 0; i--) {
						if (sents.get(i).QKW == 0.00||sents.get(i).AKW ==0.00) 
							//System.out.println(scored_answers.get(i).QKW);
							sents.remove(i);
						
				 	}
				
		return ScoredPiece.calculateFinalScores(sents);

		}
	
	

}
