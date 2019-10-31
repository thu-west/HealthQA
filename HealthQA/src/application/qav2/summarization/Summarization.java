package application.qav2.summarization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import platform.util.log.Trace;
import application.qav2.ao.Answer;
import application.qav2.ao.Question;
import application.qav2.ao.ScoredAnswer;
import application.qav2.ao.ScoredQuestion;
import application.qav2.ao.ScoredPiece;
import application.qav2.dao.HealthqaDB;
import application.qav2.retrieval.text_retrieval.util.Similarity;
import application.qav2.summarization.util.RankAnswer;
import application.qav2.summarization.util.RankPiece;

public class Summarization {
	
	public static Trace t = new Trace().setValid(true, true);
	
	public static List<ScoredAnswer> findTopAnswers (Question input_question, List<ScoredQuestion> questions) throws Exception {
		
		List<Question> ql = new ArrayList<Question>();
		for ( ScoredQuestion qs : questions ) 
			ql.add(qs.question);
		List<Answer> all_answers = HealthqaDB.getAnswerList(ql);
		if ( all_answers == null )
			return null;
		for(Answer a : all_answers ){
			a.findKeywords();    // 计算每个answer的关键词
			a.findEntities();		//计算每个answer的实体
		}
		List<ScoredAnswer> temp = RankAnswer.findTopAnswers(input_question, questions, all_answers);
		
		Collections.sort(temp);
		
		return temp;
	}
	
	// 获取最优句子列表
	public static List<ScoredPiece> findTopPieces(Question input_question, List<ScoredAnswer> all_answers ) throws Exception {	
		// liu 2015.12.16
		if (all_answers.isEmpty()) {
			return null;
		} else {
		List<ScoredPiece> temp = RankPiece.findTopPieces(input_question, all_answers);
		Collections.sort(temp);
//		return temp;
		return removeSimilarityPiece(temp);
		}
		
	}
	//去除相似度大于0.5的片段
	public static List<ScoredPiece> removeSimilarityPiece(List<ScoredPiece> temp) throws Exception {
		List<ScoredPiece> finalPieces = new ArrayList<ScoredPiece>();
		//calculate similarity between pieces
		int allCount = temp.size();
		for (int i = allCount-1; i > 0; i--)  {
			double value = Similarity.computeQuesSimilarity(temp.get(0)
					.toString(), temp.get(i).toString());
			if (value > 0.5) {
				temp.remove(i);
			}
		}
		finalPieces.addAll(temp);
		return finalPieces;
	}

}
