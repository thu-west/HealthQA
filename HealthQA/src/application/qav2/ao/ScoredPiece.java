package application.qav2.ao;

import java.util.ArrayList;
import java.util.List;

import application.qav2.dao.Entity;

public class ScoredPiece implements Comparable<ScoredPiece> {
	public Piece piece;
	public ScoredAnswer answer;
	public Double score = 0.0;

	public double TFIDF = 0;
	public double POS = 0;
	private double STAT = 0;
	public double ANSWER_SCORE = 0;
	public int WN = 0;
	public int CN = 0;
	public double QKW = 0;
	public double AKW = 0;
	public double QUES_SIM = 0;

//	private double total = 52;
//	private double alpha_QKW = 16 / total;
//	private double alpha_ANSWER_SCORE = 12 / total;
//	private double alpha_AKW = 10 / total;
//	private double alpha_TFIDF = 6 / total;
//	private double alpha_STAT = 4 / total;
//	private double alpha_POS = 4 / total;

	private double total = 48;
	private double alpha_QKW = 16 / total;
	private double alpha_ANSWER_SCORE = 12 / total;
	private double alpha_AKW = 10 / total;
	private double alpha_TFIDF = 10 / total;
	private double alpha_STAT = 4 / total;
	private double alpha_POS = 2 / total;
	private double alpha_QUES_SIM = 8/total;

	
	public ScoredPiece(Piece piece, ScoredAnswer as) {
		this.piece = piece;
		this.answer = as;
	}

	public static List<ScoredPiece> calculateFinalScores(List<ScoredPiece> sents) throws Exception {
		
		List<ScoredPiece> sPieces = new ArrayList<ScoredPiece>();

		double aver_CN = 0, aver_WN = 0;
		int total_sent = sents.size();
		for (ScoredPiece s : sents) {
			aver_CN += s.CN;
			aver_WN += s.WN;
		}
		aver_CN /= total_sent;
		aver_WN /= total_sent;
		for (ScoredPiece s : sents) {
			s.STAT = Math.pow(0.5, aver_WN / s.WN)
					+ Math.pow(0.5, aver_CN / s.CN);
		}

		// normalize
		double aver_TFIDF = 0, aver_POS = 0, aver_STAT = 0, aver_ANSWER_SCORE = 0, aver_QKW = 0, aver_AKW = 0,aver_QUES_SIM=0;
		for (ScoredPiece as : sents) {
			aver_TFIDF += as.TFIDF;
			aver_POS += as.POS;
			aver_STAT += as.STAT;
			aver_ANSWER_SCORE += as.ANSWER_SCORE;
			aver_QKW += as.QKW;
			aver_AKW += as.AKW;
			aver_QUES_SIM += as.QUES_SIM;
		}
		aver_TFIDF /= total_sent;
		aver_POS /= total_sent;
		aver_STAT /= total_sent;
		aver_ANSWER_SCORE /= total_sent;
		aver_QKW /= total_sent;
		aver_AKW /= total_sent;
		aver_QUES_SIM/=total_sent;
		
   		for (ScoredPiece as : sents) {

			as.TFIDF /= aver_TFIDF;
			as.POS /= aver_POS;
			as.STAT /= aver_STAT;
			as.ANSWER_SCORE /= aver_ANSWER_SCORE;
			as.QKW /= aver_QKW;
			as.AKW /= aver_AKW;
			as.QUES_SIM /= aver_QUES_SIM;
			as.score = as.TFIDF * as.alpha_TFIDF + as.POS * as.alpha_POS
					+ as.STAT * as.alpha_STAT + as.ANSWER_SCORE
					* as.alpha_ANSWER_SCORE + as.QKW * as.alpha_QKW + as.AKW
					* as.alpha_AKW+as.alpha_QUES_SIM * as.QUES_SIM;
			if(as.piece.raw_content.contains("要不要")
					|| as.piece.raw_content.contains("是不是")
					|| as.piece.raw_content.contains("吗")
					|| as.piece.raw_content.contains("呢")
					|| as.piece.raw_content.contains("是否")
					|| as.piece.raw_content.contains("注意什么")
					|| as.piece.raw_content.contains("问题分析")
					|| as.piece.raw_content.contains("病情分析")
					|| as.piece.raw_content.contains("指导意见")
					|| as.piece.raw_content.contains("祝您健康")	
					|| as.piece.raw_content.contains("意见建议")	
					)
				as.score = 0.00;
//			if(as.piece.raw_content.length()<=5)
//				as.score = as.score*0.1;
			if (as.piece.raw_content.length()<=5) {
				as.score = 0.00;
			}
		}
//   		}
	   for (int i = 0; i < sents.size(); i++) {
		if (sents.get(i).score == 0.00) {
			sents.remove(i);
		}
	}
//		return sents;
	    
	   //liu 2015.12.15
		for(ScoredPiece s : sents)
			{
				for(Entity e1: s.piece.entities)
//					for(Entity e2:input_question.entities)
					for(Entity e2:s.answer.answer.question.entities)
						if(e1.name.contains(e2.name)){
								if(sPieces.contains(s))
									continue;
							sPieces.add(s);
						}
			}			
			return sPieces;
	}

	
	@Override
	public String toString() {
		return String.format("piece: %s\n\tscore: %.2f <== QKW: %.2f, AKW: %.2f, TFIDF: %.2f, POS: %.2f, ANSWER_SCORE: %.2f, STAT: %.2f (WN-%d,CN-%d)",
						piece.raw_content, score, QKW, AKW, TFIDF, POS, ANSWER_SCORE,
						STAT, WN, CN);
	}

	@Override
	public int compareTo(ScoredPiece arg0) {
		return -score.compareTo(arg0.score);
	}
}
